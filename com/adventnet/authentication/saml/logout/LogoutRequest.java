package com.adventnet.authentication.saml.logout;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import com.adventnet.authentication.util.AuthUtil;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import com.adventnet.authentication.saml.SamlException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import org.apache.xml.security.exceptions.XMLSecurityException;
import javax.xml.xpath.XPathExpressionException;
import com.adventnet.authentication.saml.util.SamlUtil;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.authentication.saml.settings.SamlSettings;

public class LogoutRequest
{
    private String id;
    private final SamlSettings settings;
    private String nameId;
    private String currentUrl;
    private String logoutRequestString;
    private HttpServletRequest request;
    private static final String SAML_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final Logger LOGGER;
    private static String logoutRequestTemplate;
    
    public LogoutRequest(final SamlSettings settings, final HttpServletRequest request) {
        this.settings = settings;
        String samlLogoutRequest = null;
        if (request != null) {
            samlLogoutRequest = request.getParameter("SAMLRequest");
            this.currentUrl = request.getRequestURL().toString();
            this.request = request;
        }
        if (samlLogoutRequest == null) {
            this.logoutRequestString = LogoutRequest.logoutRequestTemplate;
            this.id = SamlUtil.generateUniqueID();
            final Map<String, String> valueMap = this.generateSubstitutor(settings);
            for (final String key : valueMap.keySet()) {
                this.logoutRequestString = this.logoutRequestString.replaceFirst(key, valueMap.get(key));
            }
            try {
                if (settings.getLogoutRequestSigned()) {
                    this.logoutRequestString = SamlUtil.addSign(SamlUtil.convertStringToDocument(this.logoutRequestString), settings.getSPkey(), settings.getSPcert(), settings.getSignatureAlgorithm());
                }
            }
            catch (final XPathExpressionException e) {
                e.printStackTrace();
            }
            catch (final XMLSecurityException e2) {
                e2.printStackTrace();
            }
            LogoutRequest.LOGGER.log(Level.FINE, "Logout request to be sent to IDP :: " + this.logoutRequestString);
        }
        else {
            this.logoutRequestString = SamlUtil.base64decodedInflated(samlLogoutRequest);
            this.id = getId(this.logoutRequestString);
        }
    }
    
    public Boolean isValid() throws Exception {
        final Long currentTimeInMillis = System.currentTimeMillis();
        try {
            if (this.logoutRequestString == null || this.logoutRequestString.isEmpty()) {
                throw new SamlException("SAML Logout Request is not loaded", 13);
            }
            if (this.request == null) {
                throw new Exception("The HttpRequest of the current host was not established");
            }
            if (this.currentUrl == null || this.currentUrl.isEmpty()) {
                throw new Exception("The URL of the current host was not established");
            }
            final String signature = this.request.getParameter("Signature");
            final Document logoutRequestDocument = SamlUtil.loadXML(this.logoutRequestString);
            final Element rootElement = logoutRequestDocument.getDocumentElement();
            rootElement.normalize();
            if (rootElement.hasAttribute("NotOnOrAfter")) {
                final String notOnOrAfter = rootElement.getAttribute("NotOnOrAfter");
                final Date notOnOrAfterDate = SamlUtil.parseDate(notOnOrAfter);
                final Long notOnOrAfterInMillis = notOnOrAfterDate.getTime();
                if (notOnOrAfterInMillis < currentTimeInMillis) {
                    throw new SamlException("Could not validate timestamp: expired. Check system clock.", 51);
                }
            }
            if (rootElement.hasAttribute("Destination")) {
                final String destinationUrl = rootElement.getAttribute("Destination");
                if (destinationUrl != null && !destinationUrl.isEmpty() && !destinationUrl.equals(this.currentUrl)) {
                    throw new SamlException("The LogoutRequest was received at " + this.currentUrl + " instead of " + destinationUrl, 42);
                }
            }
            this.getNameId(logoutRequestDocument);
            final String issuer = getIssuer(logoutRequestDocument);
            if (issuer == null || issuer.isEmpty()) {
                throw new SamlException(String.format("Invalid issuer in the Logout Request", new Object[0]), 44);
            }
            if (signature != null && !signature.isEmpty()) {
                final X509Certificate cert = this.settings.getIdpx509cert();
                if (cert == null) {
                    throw new SamlException("In order to validate the sign on the Logout Request, the x509cert of the IdP is required", 4);
                }
                String signAlg = this.request.getParameter("SigAlg");
                if (signAlg == null || signAlg.isEmpty()) {
                    signAlg = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
                }
                final String relayState = this.getEncodedParameter("RelayState");
                String signedQuery = "SAMLRequest=" + this.getEncodedParameter("SAMLRequest");
                if (relayState != null && !relayState.isEmpty()) {
                    signedQuery = signedQuery + "&RelayState=" + relayState;
                }
                String signAlgorithm = this.getEncodedParameter("SigAlg");
                signAlgorithm = ((signAlgorithm != null) ? signAlgorithm : SamlUtil.urlEncoder(signAlg));
                signedQuery = signedQuery + "&SigAlg=" + signAlgorithm;
                if (!SamlUtil.validateBinarySignature(signedQuery, SamlUtil.base64decoder(signature), cert, signAlg)) {
                    throw new SamlException("Signature validation failed. Logout Request rejected", 36);
                }
            }
            LogoutRequest.LOGGER.log(Level.FINE, "LogoutRequest validated --> " + this.logoutRequestString);
            return true;
        }
        catch (final Exception e) {
            LogoutRequest.LOGGER.log(Level.SEVERE, "LogoutRequest invalid --> " + this.logoutRequestString, e);
            return false;
        }
    }
    
    private String getEncodedParameter(final String name) {
        final Matcher matcher = Pattern.compile(Pattern.quote(name) + "=([^&#]+)").matcher(this.request.getQueryString());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return SamlUtil.urlEncoder(this.request.getParameter(name));
    }
    
    private static String getIssuer(final Document samlLogoutRequestDocument) throws XPathExpressionException {
        String issuer = null;
        final NodeList nodes = SamlUtil.query(samlLogoutRequestDocument, "/samlp:LogoutRequest/saml:Issuer", null);
        if (nodes.getLength() == 1) {
            issuer = nodes.item(0).getTextContent();
        }
        return issuer;
    }
    
    private String getNameId(final Document samlLogoutRequestDocument) throws Exception {
        final Map<String, String> nameIdData = getNameIdData(samlLogoutRequestDocument);
        LogoutRequest.LOGGER.log(Level.FINE, "LogoutRequest has NameID --> " + nameIdData.get("Value"));
        return nameIdData.get("Value");
    }
    
    private static Map<String, String> getNameIdData(final Document samlLogoutRequestDocument) throws Exception {
        final NodeList nameIdNodes = SamlUtil.query(samlLogoutRequestDocument, "/samlp:LogoutRequest/saml:NameID", null);
        if (nameIdNodes != null && nameIdNodes.getLength() == 1) {
            final Element nameIdElem = (Element)nameIdNodes.item(0);
            final Map<String, String> nameIdData = new HashMap<String, String>();
            if (nameIdElem != null) {
                nameIdData.put("Value", nameIdElem.getTextContent());
                if (nameIdElem.hasAttribute("Format")) {
                    nameIdData.put("Format", nameIdElem.getAttribute("Format"));
                }
                if (nameIdElem.hasAttribute("SPNameQualifier")) {
                    nameIdData.put("SPNameQualifier", nameIdElem.getAttribute("SPNameQualifier"));
                }
                if (nameIdElem.hasAttribute("NameQualifier")) {
                    nameIdData.put("NameQualifier", nameIdElem.getAttribute("NameQualifier"));
                }
            }
            return nameIdData;
        }
        throw new SamlException("No name id found in Logout Request.", 38);
    }
    
    private static String getId(final String samlLogoutRequestString) {
        final Document doc = SamlUtil.loadXML(samlLogoutRequestString);
        return getId(doc);
    }
    
    private static String getId(final Document samlLogoutRequestDocument) {
        String id = null;
        try {
            final Element rootElement = samlLogoutRequestDocument.getDocumentElement();
            rootElement.normalize();
            id = rootElement.getAttribute("ID");
        }
        catch (final Exception ex) {}
        return id;
    }
    
    private Map<String, String> generateSubstitutor(final SamlSettings settings) {
        final Map<String, String> valueMap = new HashMap<String, String>();
        valueMap.put("id", this.id);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String issueInstantString = sdf.format(new Date());
        valueMap.put("issueInstant", issueInstantString);
        String destinationStr = "";
        final URL slo = settings.getIdpSingleLogoutServiceUrl();
        if (slo != null) {
            destinationStr = " Destination=\"" + slo.toString() + "\"";
        }
        valueMap.put("destinationStr", destinationStr);
        valueMap.put("issuer", settings.getSpEntityId());
        String nameIdFormat = null;
        this.nameId = AuthUtil.getUserCredential().getLoginName();
        if (this.nameId != null) {
            final String domainName = AuthUtil.getUserCredential().getDomainName();
            if (domainName != null && !domainName.isEmpty() && !domainName.equals("-")) {
                this.nameId = domainName + "\\\\" + this.nameId;
            }
            nameIdFormat = settings.getSpNameIDFormat();
        }
        else {
            this.nameId = settings.getIdpEntityId();
            nameIdFormat = "urn:oasis:names:tc:SAML:2.0:nameid-format:entity";
        }
        final String nameIdStr = generateNameId(this.nameId, null, nameIdFormat);
        valueMap.put("nameIdStr", nameIdStr);
        return valueMap;
    }
    
    private static String generateNameId(final String value, final String spnq, final String format) {
        String res = null;
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            final Document doc = dbf.newDocumentBuilder().newDocument();
            final Element nameId = doc.createElement("saml:NameID");
            if (spnq != null && !spnq.isEmpty()) {
                nameId.setAttribute("SPNameQualifier", spnq);
            }
            if (format != null && !format.isEmpty()) {
                nameId.setAttribute("Format", format);
            }
            nameId.appendChild(doc.createTextNode(value));
            doc.appendChild(nameId);
            res = SamlUtil.convertDocumentToString(doc);
        }
        catch (final Exception e) {
            LogoutRequest.LOGGER.log(Level.SEVERE, "Error executing generateNameId: " + e.getMessage(), e);
        }
        return res;
    }
    
    public String getEncodedLogoutRequest() throws IOException {
        final String encodedLogoutRequest = SamlUtil.base64encoder(this.logoutRequestString);
        return encodedLogoutRequest;
    }
    
    public String getID() {
        return this.id;
    }
    
    static {
        LOGGER = Logger.getLogger(LogoutRequest.class.getName());
        final StringBuilder template = new StringBuilder();
        template.append("<samlp:LogoutRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ");
        template.append("ID=\"id\" ");
        template.append("Version=\"2.0\" ");
        template.append("IssueInstant=\"issueInstant\"destinationStr >");
        template.append("<saml:Issuer>issuer</saml:Issuer>");
        template.append("nameIdStr</samlp:LogoutRequest>");
        LogoutRequest.logoutRequestTemplate = template.toString();
    }
}
