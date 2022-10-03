package com.adventnet.authentication.saml;

import java.io.PrintWriter;
import com.adventnet.authentication.saml.logout.LogoutResponse;
import com.adventnet.authentication.saml.logout.LogoutRequest;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.cert.X509Certificate;
import com.adventnet.authentication.saml.util.Constants;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.text.ParseException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.net.URL;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import org.apache.xml.security.exceptions.XMLSecurityException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.Calendar;
import com.adventnet.authentication.saml.util.SamlUtil;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.authentication.saml.settings.SamlSettings;
import java.util.logging.Logger;

public class Auth
{
    private static final Logger LOGGER;
    private static final String SAML_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private SamlSettings settings;
    private HttpServletResponse response;
    private HttpServletRequest request;
    private boolean authenticated;
    private String samlResponseString;
    private Document samlResponseDocument;
    private String nameid;
    private String nameidFormat;
    private Map<String, List<String>> attributes;
    private static String authRequestTemplate;
    
    public Auth(final SamlSettings settings, final HttpServletRequest request, final HttpServletResponse response) throws SamlException {
        this.authenticated = false;
        this.attributes = new HashMap<String, List<String>>();
        this.settings = settings;
        this.request = request;
        this.response = response;
        final List<String> settingsErrors = settings.checkSettings();
        if (!settingsErrors.isEmpty()) {
            final StringBuilder errorMsg = new StringBuilder("Invalid settings :: ");
            for (final String s : settingsErrors) {
                errorMsg.append(s);
                errorMsg.append(",");
            }
            Auth.LOGGER.log(Level.SEVERE, errorMsg.toString());
            throw new SamlException(errorMsg.toString(), 2);
        }
        Auth.LOGGER.log(Level.FINE, "Settings validated");
    }
    
    public void login() throws IOException, XPathExpressionException, XMLSecurityException {
        final Map<String, String> parameters = new HashMap<String, String>();
        final String id = SamlUtil.generateUniqueID();
        final Calendar cal = Calendar.getInstance();
        String authRequest = this.getAuthnRequest(this.generateSubstitutor(this.settings, id, cal));
        Auth.LOGGER.log(Level.FINE, "auth request :: " + authRequest);
        if (this.settings.getAuthnRequestsSigned()) {
            authRequest = SamlUtil.addSign(SamlUtil.convertStringToDocument(authRequest), this.settings.getSPkey(), this.settings.getSPcert(), this.settings.getSignatureAlgorithm());
        }
        authRequest = this.getEncodedAuthnRequest(authRequest);
        parameters.put("SAMLRequest", authRequest);
        final String ssoUrl = this.settings.getIdpSingleSignOnServiceUrl().toString();
        this.request.getSession().setAttribute("AuthID", (Object)id);
        SamlUtil.sendRedirect(this.response, ssoUrl, parameters);
    }
    
    private String getEncodedAuthnRequest(final String authRequest) throws IOException {
        return SamlUtil.deflatedBase64encoded(authRequest);
    }
    
    private String getAuthnRequest(final Map<String, String> valueMap) {
        String authReq = Auth.authRequestTemplate;
        for (final String key : valueMap.keySet()) {
            authReq = authReq.replace(key, valueMap.get(key));
        }
        return authReq;
    }
    
    private Map<String, String> generateSubstitutor(final SamlSettings settings, final String id, final Calendar cal) {
        final Map<String, String> valueMap = new HashMap<String, String>();
        String destinationStr = "";
        final URL sso = settings.getIdpSingleSignOnServiceUrl();
        if (sso != null) {
            destinationStr = " Destination=\"" + sso.toString() + "\"";
        }
        valueMap.put("{destinationStr}", destinationStr);
        final String nameIDPolicyFormat = settings.getSpNameIDFormat();
        final String nameIDPolicyStr = "<samlp:NameIDPolicy Format=\"" + nameIDPolicyFormat + "\" />";
        valueMap.put("{nameIDPolicyStr}", nameIDPolicyStr);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String issueInstantString = sdf.format(new Date());
        valueMap.put("{issueInstant}", issueInstantString);
        valueMap.put("{id}", id);
        valueMap.put("{assertionConsumerServiceURL}", String.valueOf(settings.getSpAssertionConsumerServiceUrl()));
        valueMap.put("{spEntityid}", settings.getSpEntityId());
        return valueMap;
    }
    
    public void processResponse() throws Exception {
        this.authenticated = false;
        final String samlResponseParameter = this.request.getParameter("SAMLResponse");
        if (samlResponseParameter != null) {
            this.loadXmlFromBase64(this.request.getParameter("SAMLResponse"));
            Auth.LOGGER.log(Level.FINE, "SAML RESPONSE :: " + this.samlResponseString);
            try {
                this.validateResponse();
            }
            catch (final Exception e) {
                e.printStackTrace();
                Auth.LOGGER.log(Level.SEVERE, "processResponse error. invalid_response --> " + samlResponseParameter);
                throw e;
            }
            this.nameid = this.getNameId();
            this.nameidFormat = this.getNameIdFormat();
            this.attributes = this.getAttributes();
            this.authenticated = true;
            Auth.LOGGER.log(Level.FINE, "processResponse success --> " + samlResponseParameter);
            return;
        }
        final String errorMsg = "SAML Response not found, Only supported HTTP_POST Binding";
        Auth.LOGGER.log(Level.SEVERE, "processResponse error." + errorMsg);
        throw new SamlException(errorMsg, 8);
    }
    
    public final boolean isAuthenticated() {
        return this.authenticated;
    }
    
    public Map<String, List<String>> getAttributes() throws XPathExpressionException, SamlException {
        if (this.authenticated) {
            return this.attributes;
        }
        if (!this.authenticated && this.samlResponseDocument == null) {
            return null;
        }
        final HashMap<String, List<String>> attributes = new HashMap<String, List<String>>();
        final NodeList nodes = this.queryAssertion("/saml:AttributeStatement/saml:Attribute");
        if (nodes.getLength() != 0) {
            for (int i = 0; i < nodes.getLength(); ++i) {
                final NamedNodeMap attrName = nodes.item(i).getAttributes();
                final String attName = attrName.getNamedItem("Name").getNodeValue();
                if (attributes.containsKey(attName)) {
                    throw new SamlException("Found an Attribute element with duplicated Name", 37);
                }
                final NodeList childrens = nodes.item(i).getChildNodes();
                final List<String> attrValues = new ArrayList<String>();
                for (int j = 0; j < childrens.getLength(); ++j) {
                    if ("AttributeValue".equals(childrens.item(j).getLocalName())) {
                        attrValues.add(childrens.item(j).getTextContent());
                    }
                }
                attributes.put(attName, attrValues);
            }
            Auth.LOGGER.log(Level.FINE, "SAMLResponse has attributes: " + attributes.toString());
        }
        else {
            Auth.LOGGER.log(Level.FINE, "SAMLResponse has no attributes");
        }
        return attributes;
    }
    
    private Date getSessionNotOnOrAfter() throws XPathExpressionException, ParseException {
        String notOnOrAfter = null;
        final NodeList entries = this.queryAssertion("/saml:AuthnStatement[@SessionNotOnOrAfter]");
        if (entries.getLength() > 0) {
            notOnOrAfter = entries.item(0).getAttributes().getNamedItem("SessionNotOnOrAfter").getNodeValue();
            return SamlUtil.parseDate(notOnOrAfter);
        }
        return null;
    }
    
    public String getNameIdFormat() throws Exception {
        if (this.authenticated) {
            return this.nameidFormat;
        }
        if (!this.authenticated && this.samlResponseDocument == null) {
            return null;
        }
        final HashMap<String, String> nameIdData = this.getNameIdData();
        String nameidFormat = null;
        if (!nameIdData.isEmpty() && nameIdData.containsKey("Format")) {
            Auth.LOGGER.log(Level.FINE, "SAMLResponse has NameID Format --> " + nameIdData.get("Format"));
            nameidFormat = nameIdData.get("Format");
        }
        return nameidFormat;
    }
    
    public String getNameId() {
        String nameID = null;
        if (this.authenticated) {
            return this.nameid;
        }
        if (!this.authenticated && this.samlResponseDocument == null) {
            return null;
        }
        try {
            final HashMap<String, String> nameIdData = this.getNameIdData();
            if (!nameIdData.isEmpty()) {
                Auth.LOGGER.log(Level.FINE, "SAMLResponse has NameID --> " + nameIdData.get("Value"));
                nameID = nameIdData.get("Value");
            }
        }
        catch (final Exception e) {
            return null;
        }
        return nameID;
    }
    
    private HashMap<String, String> getNameIdData() throws Exception {
        final HashMap<String, String> nameIdData = new HashMap<String, String>();
        final NodeList nameIdNodes = this.queryAssertion("/saml:Subject/saml:NameID");
        if (nameIdNodes != null && nameIdNodes.getLength() == 1) {
            final Element nameIdElem = (Element)nameIdNodes.item(0);
            if (nameIdElem != null) {
                final String value = nameIdElem.getTextContent();
                if (value.isEmpty()) {
                    throw new SamlException("An empty NameID value found", 39);
                }
                nameIdData.put("Value", value);
                if (nameIdElem.hasAttribute("Format")) {
                    nameIdData.put("Format", nameIdElem.getAttribute("Format"));
                }
                if (nameIdElem.hasAttribute("SPNameQualifier")) {
                    final String spNameQualifier = nameIdElem.getAttribute("SPNameQualifier");
                    if (!spNameQualifier.equals(this.settings.getSpEntityId())) {
                        throw new SamlException("The SPNameQualifier value mistmatch the SP entityID value.", 40);
                    }
                    nameIdData.put("SPNameQualifier", spNameQualifier);
                }
                if (nameIdElem.hasAttribute("NameQualifier")) {
                    nameIdData.put("NameQualifier", nameIdElem.getAttribute("NameQualifier"));
                }
            }
            return nameIdData;
        }
        throw new SamlException("No name id found in Document.", 38);
    }
    
    private NodeList queryAssertion(final String assertionXpath) throws XPathExpressionException {
        final String assertionExpr = "/saml:Assertion";
        final String signatureExpr = "ds:Signature/ds:SignedInfo/ds:Reference";
        final String signedAssertionQuery = "/samlp:Response/saml:Assertion/ds:Signature/ds:SignedInfo/ds:Reference";
        NodeList nodeList = this.query(signedAssertionQuery, null);
        String nameQuery;
        if (nodeList.getLength() == 0) {
            final String signedMessageQuery = "/samlp:Response/ds:Signature/ds:SignedInfo/ds:Reference";
            nodeList = this.query(signedMessageQuery, null);
            if (nodeList.getLength() == 1) {
                final Node responseReferenceNode = nodeList.item(0);
                String responseId = responseReferenceNode.getAttributes().getNamedItem("URI").getNodeValue();
                if (responseId != null && !responseId.isEmpty()) {
                    responseId = responseId.substring(1);
                }
                else {
                    responseId = responseReferenceNode.getParentNode().getParentNode().getParentNode().getAttributes().getNamedItem("ID").getNodeValue();
                }
                nameQuery = "/samlp:Response[@ID='" + responseId + "']";
            }
            else {
                nameQuery = "/samlp:Response";
            }
            nameQuery += "/saml:Assertion";
        }
        else {
            final Node assertionReferenceNode = nodeList.item(0);
            String assertionId = assertionReferenceNode.getAttributes().getNamedItem("URI").getNodeValue();
            if (assertionId != null && !assertionId.isEmpty()) {
                assertionId = assertionId.substring(1);
            }
            else {
                assertionId = assertionReferenceNode.getParentNode().getParentNode().getParentNode().getAttributes().getNamedItem("ID").getNodeValue();
            }
            nameQuery = "/samlp:Response//saml:Assertion[@ID='" + assertionId + "']";
        }
        nameQuery += assertionXpath;
        return this.query(nameQuery, null);
    }
    
    private void validateResponse() throws Exception {
        final String currentUrl = this.request.getRequestURL().toString();
        final Long currentTimeinMillis = System.currentTimeMillis();
        final String requestId = (String)this.request.getSession().getAttribute("AuthID");
        if (this.samlResponseDocument == null) {
            throw new Exception("SAML Response is not loaded");
        }
        if (currentUrl == null || currentUrl.isEmpty()) {
            throw new Exception("The URL of the current host was not established");
        }
        final Element rootElement = this.samlResponseDocument.getDocumentElement();
        rootElement.normalize();
        final String responseInResponseTo = rootElement.hasAttribute("InResponseTo") ? rootElement.getAttribute("InResponseTo") : null;
        if (responseInResponseTo != null && requestId != null && !requestId.equals(responseInResponseTo)) {
            throw new SamlException("The InResponseTo of the Response: " + responseInResponseTo + ", does not match the ID of the AuthNRequest sent by the SP: " + requestId, 50);
        }
        if (!rootElement.getAttribute("Version").equals("2.0")) {
            throw new SamlException("Unsupported SAML Version.", 16);
        }
        if (!rootElement.hasAttribute("ID")) {
            throw new SamlException("Missing ID attribute on SAML Response.", 17);
        }
        final NodeList statusElement = this.samlResponseDocument.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:protocol", "StatusCode");
        if (statusElement == null || statusElement.getLength() == 0) {
            throw new SamlException("Missing Status on response", 18);
        }
        final NamedNodeMap attributes = statusElement.item(0).getAttributes();
        if (attributes == null || attributes.getLength() == 0) {
            throw new SamlException("Missing Status Code on response", 19);
        }
        final Node namedItem = attributes.getNamedItem("Value");
        if (namedItem == null) {
            throw new SamlException("Exception while obtaining saml status", 20);
        }
        if (namedItem.getNodeValue().equals("urn:oasis:names:tc:SAML:2.0:status:Success")) {
            Auth.LOGGER.log(Level.FINE, "Got success status code in saml response");
            final Date sessionExpiration = this.getSessionNotOnOrAfter();
            if (sessionExpiration != null) {
                final Long sessionExpiryWithClockDrift = sessionExpiration.getTime() + Constants.ALLOWED_CLOCK_DRIFT;
                if (sessionExpiryWithClockDrift == currentTimeinMillis || sessionExpiryWithClockDrift < currentTimeinMillis) {
                    throw new SamlException("The attributes have expired, based on the SessionNotOnOrAfter of the AttributeStatement of this Response", 41);
                }
            }
            if (rootElement.hasAttribute("Destination")) {
                final String destinationUrl = rootElement.getAttribute("Destination");
                if (destinationUrl != null && isValidUrl(destinationUrl)) {
                    if (destinationUrl.isEmpty()) {
                        throw new SamlException("The response has an empty Destination value", 43);
                    }
                    if (!destinationUrl.equals(currentUrl)) {
                        throw new SamlException("The response was received at " + currentUrl + " instead of " + destinationUrl, 42);
                    }
                }
            }
            final List<String> issuers = this.getIssuers();
            for (int i = 0; i < issuers.size(); ++i) {
                final String issuer = issuers.get(i);
                if (issuer.isEmpty()) {
                    throw new SamlException(String.format("Invalid issuer in the Assertion/Response", new Object[0]), 44);
                }
            }
            if (!this.validateTimestamps(currentTimeinMillis)) {
                throw new Exception("Timing issues (please check your clock settings)");
            }
            final ArrayList<String> signedElements = this.processSignedElements();
            final String responseTag = "{urn:oasis:names:tc:SAML:2.0:protocol}Response";
            final String assertionTag = "{urn:oasis:names:tc:SAML:2.0:assertion}Assertion";
            final boolean hasSignedResponse = signedElements.contains(responseTag);
            final boolean hasSignedAssertion = signedElements.contains(assertionTag);
            if (signedElements.isEmpty() || (!hasSignedAssertion && !hasSignedResponse)) {
                throw new SamlException("No Signature found. SAML Response rejected", 35);
            }
            final X509Certificate cert = this.settings.getIdpx509cert();
            if (hasSignedResponse && !SamlUtil.validateSign(this.samlResponseDocument, cert, "/samlp:Response/ds:Signature")) {
                throw new SamlException("Signature validation failed. SAML Response rejected", 36);
            }
            final Document documentToCheckAssertion = this.samlResponseDocument;
            if (hasSignedAssertion && !SamlUtil.validateSign(documentToCheckAssertion, cert, "/samlp:Response/saml:Assertion/ds:Signature")) {
                throw new SamlException("Signature validation failed. SAML Response rejected", 36);
            }
            Auth.LOGGER.log(Level.FINE, "SAMLResponse validated --> " + this.samlResponseString);
        }
        else {
            if (namedItem.getNodeValue().equals("urn:oasis:names:tc:SAML:2.0:status:Responder")) {
                throw new SamlException("Responser failure", 21);
            }
            if (namedItem.getNodeValue().equals("urn:oasis:names:tc:SAML:2.0:status:Requester")) {
                throw new SamlException("Requester failure", 22);
            }
            throw new SamlException("Saml reponse has failed", 23);
        }
    }
    
    private static boolean isValidUrl(final String url) {
        try {
            new URL(url).toURI();
            return true;
        }
        catch (final URISyntaxException exception) {
            return false;
        }
        catch (final MalformedURLException exception2) {
            return false;
        }
    }
    
    private boolean validateTimestamps(final Long currenTimeInMillis) throws SamlException, DOMException, ParseException {
        final NodeList timestampNodes = this.samlResponseDocument.getElementsByTagNameNS("*", "Conditions");
        if (timestampNodes.getLength() != 0) {
            for (int i = 0; i < timestampNodes.getLength(); ++i) {
                final NamedNodeMap attrName = timestampNodes.item(i).getAttributes();
                final Node nbAttribute = attrName.getNamedItem("NotBefore");
                final Node naAttribute = attrName.getNamedItem("NotOnOrAfter");
                if (naAttribute != null) {
                    final Date notOnOrAfterDate = SamlUtil.parseDate(naAttribute.getNodeValue());
                    final Long notOnOrAfterinMillis = notOnOrAfterDate.getTime() + Constants.ALLOWED_CLOCK_DRIFT;
                    if (notOnOrAfterinMillis <= currenTimeInMillis) {
                        throw new SamlException("Could not validate timestamp: expired. Check system clock.", 47);
                    }
                }
                if (nbAttribute != null) {
                    final Date notBeforeDate = SamlUtil.parseDate(nbAttribute.getNodeValue());
                    final Long notBeforeinMillis = notBeforeDate.getTime() - Constants.ALLOWED_CLOCK_DRIFT;
                    if (notBeforeinMillis > currenTimeInMillis) {
                        throw new SamlException("Could not validate timestamp: not yet valid. Check system clock.", 46);
                    }
                }
            }
        }
        return true;
    }
    
    private List<String> getIssuers() throws XPathExpressionException, SamlException {
        final List<String> issuers = new ArrayList<String>();
        final NodeList responseIssuer = SamlUtil.query(this.samlResponseDocument, "/samlp:Response/saml:Issuer", null);
        if (responseIssuer.getLength() > 0) {
            if (responseIssuer.getLength() != 1) {
                throw new SamlException("Issuer of the Response is multiple.", 48);
            }
            final String value = responseIssuer.item(0).getTextContent();
            if (!issuers.contains(value)) {
                issuers.add(value);
            }
        }
        final NodeList assertionIssuer = this.queryAssertion("/saml:Issuer");
        if (assertionIssuer.getLength() == 1) {
            final String value = assertionIssuer.item(0).getTextContent();
            if (!issuers.contains(value)) {
                issuers.add(value);
            }
            return issuers;
        }
        throw new SamlException("Issuer of the Assertion not found or multiple.", 49);
    }
    
    private ArrayList<String> processSignedElements() throws XPathExpressionException, SamlException {
        final ArrayList<String> signedElements = new ArrayList<String>();
        final ArrayList<String> verifiedSeis = new ArrayList<String>();
        final ArrayList<String> verifiedIds = new ArrayList<String>();
        final NodeList signNodes = this.query("//ds:Signature", null);
        for (int i = 0; i < signNodes.getLength(); ++i) {
            final Node signNode = signNodes.item(i);
            final String signedElement = "{" + signNode.getParentNode().getNamespaceURI() + "}" + signNode.getParentNode().getLocalName();
            final String responseTag = "{urn:oasis:names:tc:SAML:2.0:protocol}Response";
            final String assertionTag = "{urn:oasis:names:tc:SAML:2.0:assertion}Assertion";
            if (!signedElement.equals(responseTag) && !signedElement.equals(assertionTag)) {
                throw new SamlException("Invalid Signature Element " + signedElement + " SAML Response rejected", 24);
            }
            final Node idNode = signNode.getParentNode().getAttributes().getNamedItem("ID");
            if (idNode == null || idNode.getNodeValue() == null || idNode.getNodeValue().isEmpty()) {
                throw new SamlException("Signed Element must contain an ID. SAML Response rejected", 25);
            }
            final String idValue = idNode.getNodeValue();
            if (verifiedIds.contains(idValue)) {
                throw new SamlException("Duplicated ID. SAML Response rejected", 26);
            }
            verifiedIds.add(idValue);
            final NodeList refNodes = SamlUtil.query(null, "ds:SignedInfo/ds:Reference", signNode);
            if (refNodes.getLength() != 1) {
                throw new SamlException("Unexpected number of Reference nodes found for signature. SAML Response rejected.", 30);
            }
            final Node refNode = refNodes.item(0);
            final Node seiNode = refNode.getAttributes().getNamedItem("URI");
            if (seiNode != null && seiNode.getNodeValue() != null && !seiNode.getNodeValue().isEmpty()) {
                final String sei = seiNode.getNodeValue().substring(1);
                if (!sei.equals(idValue)) {
                    throw new SamlException("Found an invalid Signed Element. SAML Response rejected", 27);
                }
                if (verifiedSeis.contains(sei)) {
                    throw new SamlException("Duplicated Reference URI. SAML Response rejected", 28);
                }
                verifiedSeis.add(sei);
            }
            signedElements.add(signedElement);
        }
        if (!signedElements.isEmpty() && !this.validateSignedElements(signedElements)) {
            throw new SamlException("Found an unexpected Signature Element. SAML Response rejected", 29);
        }
        return signedElements;
    }
    
    private boolean validateSignedElements(final ArrayList<String> signedElements) throws XPathExpressionException, SamlException {
        if (signedElements.size() > 2) {
            return false;
        }
        final Map<String, Integer> occurrences = new HashMap<String, Integer>();
        for (final String e : signedElements) {
            if (occurrences.containsKey(e)) {
                occurrences.put(e, occurrences.get(e) + 1);
            }
            else {
                occurrences.put(e, 1);
            }
        }
        final String responseTag = "{urn:oasis:names:tc:SAML:2.0:protocol}Response";
        final String assertionTag = "{urn:oasis:names:tc:SAML:2.0:assertion}Assertion";
        if ((occurrences.containsKey(responseTag) && occurrences.get(responseTag) > 1) || (occurrences.containsKey(assertionTag) && occurrences.get(assertionTag) > 1) || (!occurrences.containsKey(responseTag) && !occurrences.containsKey(assertionTag))) {
            return false;
        }
        if (occurrences.containsKey(responseTag)) {
            final NodeList expectedSignatureNode = this.query("/samlp:Response/ds:Signature", null);
            if (expectedSignatureNode.getLength() != 1) {
                throw new SamlException("Unexpected number of Response signatures found. SAML Response rejected.", 31);
            }
        }
        if (occurrences.containsKey(assertionTag)) {
            final NodeList expectedSignatureNode = this.query("/samlp:Response/saml:Assertion/ds:Signature", null);
            if (expectedSignatureNode.getLength() != 1) {
                throw new SamlException("Unexpected number of Assertion signatures found. SAML Response rejected.", 32);
            }
        }
        return true;
    }
    
    private NodeList query(final String nameQuery, final Node context) throws XPathExpressionException {
        return SamlUtil.query(this.samlResponseDocument, nameQuery, context);
    }
    
    private void loadXmlFromBase64(final String responseStr) throws SamlException, ParserConfigurationException, XPathExpressionException, SAXException, IOException {
        this.samlResponseString = new String(SamlUtil.base64decoder(responseStr), "UTF-8");
        this.samlResponseDocument = SamlUtil.loadXML(this.samlResponseString);
        if (this.samlResponseDocument == null) {
            throw new SamlException("SAML Response could not be processed", 13);
        }
    }
    
    public void logout() throws IOException {
        final LogoutRequest logoutRequest = new LogoutRequest(this.settings, null);
        this.request.getSession().setAttribute("AuthID", (Object)logoutRequest.getID());
        this.sendPostRequestToIDP(this.settings.getIdpSingleLogoutServiceUrl().toString(), logoutRequest.getEncodedLogoutRequest());
    }
    
    public void processSLO() throws Exception {
        final String samlRequestParameter = this.request.getParameter("SAMLRequest");
        final String samlResponseParameter = this.request.getParameter("SAMLResponse");
        if (samlResponseParameter != null) {
            final LogoutResponse logoutResponse = new LogoutResponse(this.settings, this.request);
            if (!logoutResponse.isValid()) {
                Auth.LOGGER.log(Level.SEVERE, "processSLO error. invalid_logout_response");
                Auth.LOGGER.log(Level.FINE, " --> " + samlResponseParameter);
                throw new SamlException("Exception occurred while validating IDP initiated logout request", 10);
            }
            final String status = logoutResponse.getStatus();
            if (status == null || !status.equals("urn:oasis:names:tc:SAML:2.0:status:Success")) {
                Auth.LOGGER.log(Level.SEVERE, "processSLO error. logout_not_success");
                Auth.LOGGER.log(Level.FINE, " --> " + samlResponseParameter);
            }
            else {
                Auth.LOGGER.log(Level.FINE, "processSLO success --> " + samlResponseParameter);
                this.request.getSession().invalidate();
            }
        }
        else {
            if (samlRequestParameter == null) {
                final String errorMsg = "SAML LogoutRequest/LogoutResponse not found.";
                Auth.LOGGER.log(Level.SEVERE, "processSLO error." + errorMsg);
                throw new SamlException(errorMsg, 9);
            }
            final LogoutRequest logoutRequest = new LogoutRequest(this.settings, this.request);
            if (!logoutRequest.isValid()) {
                Auth.LOGGER.log(Level.SEVERE, "processSLO error. invalid_logout_request");
                Auth.LOGGER.log(Level.FINE, " --> " + samlRequestParameter);
            }
            else {
                Auth.LOGGER.log(Level.FINE, "processSLO success --> " + samlRequestParameter);
                this.request.getSession().invalidate();
                final LogoutResponse logoutResponseBuilder = new LogoutResponse(this.settings, this.request);
                logoutResponseBuilder.build(logoutRequest.getID());
                this.sendPostRequestToIDP(this.settings.getIdpSingleLogoutServiceResponseUrl().toString(), logoutResponseBuilder.getEncodedLogoutResponse());
            }
        }
    }
    
    private void sendPostRequestToIDP(final String sloUrl, final String samlString) throws IOException {
        this.response.reset();
        final StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<body onload='document.forms[\"form\"].submit()'>");
        sb.append("<form name='form' action='" + sloUrl + "' method='post'>");
        sb.append("<input type='hidden' name='SAMLRequest' value='" + samlString + "'>");
        sb.append("</form>");
        sb.append("</body>");
        sb.append("</html>");
        Auth.LOGGER.log(Level.FINE, "sending post request to logout from IDP :: " + sb.toString());
        final PrintWriter pw = this.response.getWriter();
        pw.write(sb.toString());
        pw.flush();
    }
    
    public SamlSettings getSettings() {
        return this.settings;
    }
    
    static {
        LOGGER = Logger.getLogger(Auth.class.getName());
        final StringBuilder template = new StringBuilder();
        template.append("<samlp:AuthnRequest \n xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" \n xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" \n ID=\"{id}\" \n Version=\"2.0\" \n IssueInstant=\"{issueInstant}\" \n ProviderName=\"ME\" \n IsPassive=\"false\" \n {destinationStr} \n ProtocolBinding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" \n AssertionConsumerServiceURL=\"{assertionConsumerServiceURL}\">");
        template.append("\n \t<saml:Issuer>{spEntityid}</saml:Issuer>");
        template.append("\n \t{nameIDPolicyStr}\n \t</samlp:AuthnRequest>");
        Auth.authRequestTemplate = template.toString();
    }
}
