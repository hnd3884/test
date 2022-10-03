package com.adventnet.authentication.saml.logout;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import org.apache.xml.security.exceptions.XMLSecurityException;
import java.util.Iterator;
import java.util.Map;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.cert.X509Certificate;
import org.w3c.dom.Element;
import java.util.logging.Level;
import com.adventnet.authentication.saml.SamlException;
import com.adventnet.authentication.saml.util.SamlUtil;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import com.adventnet.authentication.saml.settings.SamlSettings;
import javax.servlet.http.HttpServletRequest;

public class LogoutResponse
{
    private HttpServletRequest request;
    private SamlSettings settings;
    private String currentUrl;
    private String logoutResponseString;
    private Document logoutResponseDocument;
    private String id;
    private String inResponseTo;
    private static String logoutResponseTemplate;
    private static final Logger LOGGER;
    
    public LogoutResponse(final SamlSettings settings, final HttpServletRequest request) {
        this.settings = settings;
        String samlLogoutResponse = null;
        if (request != null) {
            this.currentUrl = request.getRequestURL().toString();
            samlLogoutResponse = request.getParameter("SAMLResponse");
            this.request = request;
        }
        if (samlLogoutResponse != null && !samlLogoutResponse.isEmpty()) {
            this.logoutResponseString = SamlUtil.base64decodedInflated(samlLogoutResponse);
            this.logoutResponseDocument = SamlUtil.loadXML(this.logoutResponseString);
        }
    }
    
    public boolean isValid() {
        final String requestId = (String)this.request.getSession().getAttribute("AuthID");
        try {
            if (this.logoutResponseDocument == null) {
                throw new SamlException("SAML Logout Response is not loaded", 13);
            }
            if (this.currentUrl == null || this.currentUrl.isEmpty()) {
                throw new Exception("The URL of the current host was not established");
            }
            final String signature = this.request.getParameter("Signature");
            final Element rootElement = this.logoutResponseDocument.getDocumentElement();
            rootElement.normalize();
            final String responseInResponseTo = rootElement.hasAttribute("InResponseTo") ? rootElement.getAttribute("InResponseTo") : null;
            if (requestId != null && responseInResponseTo != null && !responseInResponseTo.equals(requestId)) {
                throw new SamlException("The InResponseTo of the Logout Response: " + responseInResponseTo + ", does not match the ID of the Logout request sent by the SP: " + requestId, 50);
            }
            final String issuer = this.getIssuer();
            if (issuer == null || issuer.isEmpty()) {
                throw new SamlException("Invalid issuer in the Logout Response ", 44);
            }
            if (rootElement.hasAttribute("Destination")) {
                final String destinationUrl = rootElement.getAttribute("Destination");
                if (destinationUrl != null && !destinationUrl.isEmpty() && !destinationUrl.equals(this.currentUrl)) {
                    throw new SamlException("The LogoutResponse was received at " + this.currentUrl + " instead of " + destinationUrl, 42);
                }
            }
            if (signature != null && !signature.isEmpty()) {
                final X509Certificate cert = this.settings.getIdpx509cert();
                if (cert == null) {
                    throw new SamlException("In order to validate the sign on the Logout Response, the x509cert of the IdP is required", 4);
                }
                String signAlg = this.request.getParameter("SigAlg");
                if (signAlg == null || signAlg.isEmpty()) {
                    signAlg = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
                }
                String signedQuery = "SAMLResponse=" + this.getEncodedParameter(this.getEncodedParameter("SAMLResponse"));
                final String relayState = this.getEncodedParameter("RelayState");
                if (relayState != null && !relayState.isEmpty()) {
                    signedQuery = signedQuery + "&RelayState=" + relayState;
                }
                String signAlgorithm = this.getEncodedParameter("SigAlg");
                signAlgorithm = ((signAlgorithm != null) ? signAlgorithm : SamlUtil.urlEncoder(signAlg));
                signedQuery = signedQuery + "&SigAlg=" + signAlgorithm;
                if (!SamlUtil.validateBinarySignature(signedQuery, SamlUtil.base64decoder(signature), cert, signAlg)) {
                    throw new SamlException("Signature validation failed. Logout Response rejected", 36);
                }
            }
            LogoutResponse.LOGGER.log(Level.FINE, "LogoutResponse validated --> " + this.logoutResponseString);
            return true;
        }
        catch (final Exception e) {
            LogoutResponse.LOGGER.log(Level.SEVERE, "LogoutResponse invalid --> " + this.logoutResponseString, e);
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
    
    private String getIssuer() throws XPathExpressionException {
        String issuer = null;
        final NodeList issuers = SamlUtil.query(this.logoutResponseDocument, "/samlp:LogoutResponse/saml:Issuer", null);
        if (issuers.getLength() == 1) {
            issuer = issuers.item(0).getTextContent();
        }
        return issuer;
    }
    
    public String getStatus() throws XPathExpressionException {
        String statusCode = null;
        final NodeList entries = SamlUtil.query(this.logoutResponseDocument, "/samlp:LogoutResponse/samlp:Status/samlp:StatusCode", null);
        if (entries.getLength() == 1) {
            statusCode = entries.item(0).getAttributes().getNamedItem("Value").getNodeValue();
        }
        return statusCode;
    }
    
    public void build(final String inResponseTo) throws XPathExpressionException, XMLSecurityException {
        this.id = SamlUtil.generateUniqueID();
        this.inResponseTo = inResponseTo;
        final Map<String, String> valueMap = this.generateSubstitutor(this.settings);
        this.logoutResponseString = LogoutResponse.logoutResponseTemplate;
        for (final String key : valueMap.keySet()) {
            this.logoutResponseString = this.logoutResponseString.replace(key, valueMap.get(key));
        }
        if (this.settings.getLogoutRequestSigned()) {
            this.logoutResponseString = SamlUtil.addSign(SamlUtil.convertStringToDocument(this.logoutResponseString), this.settings.getSPkey(), this.settings.getSPcert(), this.settings.getSignatureAlgorithm());
        }
    }
    
    private Map<String, String> generateSubstitutor(final SamlSettings settings) {
        final Map<String, String> valueMap = new HashMap<String, String>();
        valueMap.put("id", this.id);
        final String issueInstantString = SamlUtil.formatDateTime(System.currentTimeMillis());
        valueMap.put("issueInstant", issueInstantString);
        String destinationStr = "";
        final URL slo = settings.getIdpSingleLogoutServiceResponseUrl();
        if (slo != null) {
            destinationStr = " Destination=\"" + slo.toString() + "\"";
        }
        valueMap.put("destinationStr", destinationStr);
        String inResponseStr = "";
        if (this.inResponseTo != null) {
            inResponseStr = " InResponseTo=\"" + this.inResponseTo + "\"";
        }
        valueMap.put("inResponseStr", inResponseStr);
        valueMap.put("issuer", settings.getSpEntityId());
        return valueMap;
    }
    
    public String getEncodedLogoutResponse() throws IOException {
        return SamlUtil.base64encoder(this.logoutResponseString);
    }
    
    static {
        LOGGER = Logger.getLogger(LogoutResponse.class.getName());
        final StringBuilder template = new StringBuilder();
        template.append("<samlp:LogoutResponse xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ");
        template.append("ID=\"id\" ");
        template.append("Version=\"2.0\" ");
        template.append("IssueInstant=\"issueInstant\"destinationStr inResponseStr >");
        template.append("<saml:Issuer>issuer</saml:Issuer>");
        template.append("<samlp:Status>");
        template.append("<samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\" />");
        template.append("</samlp:Status>");
        template.append("</samlp:LogoutResponse>");
        LogoutResponse.logoutResponseTemplate = template.toString();
    }
}
