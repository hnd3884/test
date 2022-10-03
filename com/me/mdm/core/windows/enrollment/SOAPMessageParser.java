package com.me.mdm.core.windows.enrollment;

import java.net.URLDecoder;
import java.net.URL;
import org.json.JSONArray;
import javax.xml.namespace.QName;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.om.OMElement;
import java.util.Iterator;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.json.JSONObject;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import org.apache.axiom.soap.SOAPMessage;
import java.util.logging.Logger;

public class SOAPMessageParser
{
    Logger parserLog;
    
    public SOAPMessageParser() {
        this.parserLog = Logger.getLogger("MDMEnrollment");
    }
    
    public SOAPMessage getSOAPMessage(final byte[] request) {
        SOAPMessage message = null;
        try {
            this.parserLog.log(Level.FINE, "SOAP Message to be parsed \n ", new String(request));
            final ByteArrayInputStream ins = new ByteArrayInputStream(request);
            final XMLInputFactory factory = XMLInputFactory.newInstance();
            factory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
            final XMLStreamReader reader = factory.createXMLStreamReader(ins, "utf-8");
            final StAXSOAPModelBuilder builder = new StAXSOAPModelBuilder(reader);
            message = builder.getSoapMessage();
        }
        catch (final Exception exp) {
            this.parserLog.log(Level.WARNING, "Exception occurred during parsing SOAP Message", exp);
        }
        return message;
    }
    
    public JSONObject parseSOAPHeaders(final SOAPMessage soapMessage) {
        JSONObject jsonObject = new JSONObject();
        try {
            final SOAPHeader header = soapMessage.getSOAPEnvelope().getHeader();
            SOAPHeaderBlock securityBlock = null;
            SOAPHeaderBlock actionBlock = null;
            SOAPHeaderBlock messageBlock = null;
            SOAPHeaderBlock toBlock = null;
            final Iterator extractHeaderBlocks = header.examineAllHeaderBlocks();
            while (extractHeaderBlocks.hasNext()) {
                final SOAPHeaderBlock headerBlock = extractHeaderBlocks.next();
                if (headerBlock.getLocalName().equalsIgnoreCase("Security")) {
                    securityBlock = headerBlock;
                }
                else if (headerBlock.getLocalName().equalsIgnoreCase("Action")) {
                    actionBlock = headerBlock;
                }
                else if (headerBlock.getLocalName().equalsIgnoreCase("MessageID")) {
                    messageBlock = headerBlock;
                }
                else {
                    if (!headerBlock.getLocalName().equalsIgnoreCase("To")) {
                        continue;
                    }
                    toBlock = headerBlock;
                }
            }
            if (messageBlock != null) {
                jsonObject.put(messageBlock.getLocalName(), (Object)messageBlock.getText());
            }
            if (actionBlock != null) {
                jsonObject.put(actionBlock.getLocalName(), (Object)actionBlock.getText());
            }
            if (toBlock != null) {
                jsonObject.put(toBlock.getLocalName(), (Object)toBlock.getText());
            }
            if (securityBlock != null) {
                jsonObject = this.parseSecurityBlock(securityBlock, jsonObject);
            }
        }
        catch (final Exception exp) {
            this.parserLog.log(Level.WARNING, "Exception occurred during parsing SOAP Headers", exp);
        }
        this.parserLog.log(Level.INFO, "parseSOAPHeaders return JSON : \n", jsonObject.toString());
        return jsonObject;
    }
    
    public JSONObject parseDiscoverRequest(final SOAPMessage soapMessage) {
        JSONObject jsonObject = null;
        try {
            jsonObject = this.parseSOAPHeaders(soapMessage);
            final SOAPBody body = soapMessage.getSOAPEnvelope().getBody();
            final OMElement discoverElement = body.getFirstElement();
            final OMElement requestElement = discoverElement.getFirstElement();
            final Iterator iterator = requestElement.getChildElements();
            while (iterator.hasNext()) {
                final OMElement childElement = iterator.next();
                jsonObject.put(childElement.getLocalName(), (Object)childElement.getText());
            }
        }
        catch (final Exception exp) {
            this.parserLog.log(Level.WARNING, "Exception occurred during parseDiscoverRequest SOAP Body", exp);
        }
        this.parserLog.log(Level.INFO, "parseDiscoverRequest return JSON : \n", jsonObject);
        return jsonObject;
    }
    
    public JSONObject parseCertificateEnrollmentRequest(final SOAPMessage soapMessage) {
        JSONObject jsonObject = null;
        try {
            jsonObject = this.parseSOAPHeaders(soapMessage);
            final String enrollmentServerURL = String.valueOf(jsonObject.get("To"));
            final SOAPBody body = soapMessage.getSOAPEnvelope().getBody();
            final OMElement requestSecurityTokenElement = body.getFirstElement();
            final OMElement tokenTypeElement = requestSecurityTokenElement.getFirstChildWithName(new QName("http://docs.oasis-open.org/ws-sx/ws-trust/200512", "TokenType", "wst"));
            jsonObject.put(tokenTypeElement.getLocalName(), (Object)tokenTypeElement.getText());
            final OMElement requestTypeElement = requestSecurityTokenElement.getFirstChildWithName(new QName("http://docs.oasis-open.org/ws-sx/ws-trust/200512", "RequestType", "wst"));
            jsonObject.put(requestTypeElement.getLocalName(), (Object)requestTypeElement.getText());
            final OMElement binarySecurityElement = requestSecurityTokenElement.getFirstChildWithName(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "BinarySecurityToken", "wsse"));
            jsonObject.put(binarySecurityElement.getLocalName(), (Object)binarySecurityElement.getText());
            final OMElement additionalContextElement = requestSecurityTokenElement.getFirstChildWithName(new QName("http://schemas.xmlsoap.org/ws/2006/12/authorization", "AdditionalContext", "ac"));
            final Iterator childElements = additionalContextElement.getChildElements();
            while (childElements.hasNext()) {
                final OMElement childElement = childElements.next();
                final String attributeName = childElement.getAttributeValue(new QName("Name"));
                final OMElement valueElement = childElement.getFirstElement();
                final String attributeValue = valueElement.getText();
                if (jsonObject.has(attributeName)) {
                    JSONArray jsonArray = null;
                    if (jsonObject.get(attributeName) instanceof JSONArray) {
                        jsonArray = jsonObject.getJSONArray(attributeName);
                    }
                    else {
                        jsonArray = new JSONArray();
                        jsonArray.put((Object)String.valueOf(jsonObject.get(attributeName)));
                    }
                    jsonArray.put((Object)attributeValue);
                    jsonObject.put(attributeName, (Object)jsonArray);
                }
                else {
                    jsonObject.put(attributeName, (Object)attributeValue);
                }
            }
            final URL url = new URL(enrollmentServerURL);
            final String queryParameters = URLDecoder.decode(url.getQuery().replace("+", "%2B"), "utf-8");
            final String[] split;
            final String[] pairs = split = queryParameters.split("&");
            for (final String pair : split) {
                final int idx = pair.indexOf("=");
                jsonObject.put(pair.substring(0, idx), (Object)pair.substring(idx + 1));
            }
        }
        catch (final Exception exp) {
            this.parserLog.log(Level.WARNING, "Exception occurred during parseCertificateEnrollmentRequest SOAP Body", exp);
        }
        this.parserLog.log(Level.INFO, "parseCertificateEnrollmentRequest return JSON : \n", jsonObject);
        return jsonObject;
    }
    
    private JSONObject parseSecurityBlock(final SOAPHeaderBlock securityBlock, final JSONObject jsonObject) throws Exception {
        final OMElement securityElement = securityBlock.getFirstElement();
        final JSONObject userAuthJSON = new JSONObject();
        final Iterator iterator = securityElement.getChildElements();
        if (securityElement.getLocalName().contains("UsernameToken")) {
            final JSONObject usernamePasswordJSON = new JSONObject();
            while (iterator.hasNext()) {
                final OMElement childElement = iterator.next();
                jsonObject.put(childElement.getLocalName(), (Object)childElement.getText());
                usernamePasswordJSON.put(childElement.getLocalName(), (Object)childElement.getText());
            }
            userAuthJSON.put("tokenType", (Object)securityElement.getLocalName());
            userAuthJSON.put("tokenValue", (Object)usernamePasswordJSON);
        }
        else if (securityElement.getLocalName().contains("BinarySecurityToken")) {
            userAuthJSON.put("tokenValue", (Object)securityElement.getText());
            userAuthJSON.put("tokenType", (Object)securityElement.getAttributeValue(new QName("ValueType")));
        }
        jsonObject.put("UserAuthJson", (Object)userAuthJSON);
        return jsonObject;
    }
}
