package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import javax.xml.stream.XMLStreamException;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import javax.xml.ws.WebServiceException;
import java.io.StringReader;
import com.sun.xml.internal.ws.api.policy.ModelUnmarshaller;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import javax.xml.stream.XMLStreamReader;
import java.util.HashSet;
import java.util.Set;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public class SafePolicyReader
{
    private static final PolicyLogger LOGGER;
    private final Set<String> urlsRead;
    private final Set<String> qualifiedPolicyUris;
    
    public SafePolicyReader() {
        this.urlsRead = new HashSet<String>();
        this.qualifiedPolicyUris = new HashSet<String>();
    }
    
    public PolicyRecord readPolicyElement(final XMLStreamReader reader, final String baseUrl) {
        if (null == reader || !reader.isStartElement()) {
            return null;
        }
        final StringBuffer elementCode = new StringBuffer();
        final PolicyRecord policyRec = new PolicyRecord();
        final QName elementName = reader.getName();
        int depth = 0;
        try {
            do {
                switch (reader.getEventType()) {
                    case 1: {
                        final QName curName = reader.getName();
                        final boolean insidePolicyReferenceAttr = NamespaceVersion.resolveAsToken(curName) == XmlToken.PolicyReference;
                        if (elementName.equals(curName)) {
                            ++depth;
                        }
                        final StringBuffer xmlnsCode = new StringBuffer();
                        final Set<String> tmpNsSet = new HashSet<String>();
                        if (null == curName.getPrefix() || "".equals(curName.getPrefix())) {
                            elementCode.append('<').append(curName.getLocalPart());
                            xmlnsCode.append(" xmlns=\"").append(curName.getNamespaceURI()).append('\"');
                        }
                        else {
                            elementCode.append('<').append(curName.getPrefix()).append(':').append(curName.getLocalPart());
                            xmlnsCode.append(" xmlns:").append(curName.getPrefix()).append("=\"").append(curName.getNamespaceURI()).append('\"');
                            tmpNsSet.add(curName.getPrefix());
                        }
                        final int attrCount = reader.getAttributeCount();
                        final StringBuffer attrCode = new StringBuffer();
                        for (int i = 0; i < attrCount; ++i) {
                            boolean uriAttrFlg = false;
                            if (insidePolicyReferenceAttr && "URI".equals(reader.getAttributeName(i).getLocalPart())) {
                                uriAttrFlg = true;
                                if (null == policyRec.unresolvedURIs) {
                                    policyRec.unresolvedURIs = new HashSet<String>();
                                }
                                policyRec.unresolvedURIs.add(relativeToAbsoluteUrl(reader.getAttributeValue(i), baseUrl));
                            }
                            if (!"xmlns".equals(reader.getAttributePrefix(i)) || !tmpNsSet.contains(reader.getAttributeLocalName(i))) {
                                if (null == reader.getAttributePrefix(i) || "".equals(reader.getAttributePrefix(i))) {
                                    attrCode.append(' ').append(reader.getAttributeLocalName(i)).append("=\"").append(uriAttrFlg ? relativeToAbsoluteUrl(reader.getAttributeValue(i), baseUrl) : reader.getAttributeValue(i)).append('\"');
                                }
                                else {
                                    attrCode.append(' ').append(reader.getAttributePrefix(i)).append(':').append(reader.getAttributeLocalName(i)).append("=\"").append(uriAttrFlg ? relativeToAbsoluteUrl(reader.getAttributeValue(i), baseUrl) : reader.getAttributeValue(i)).append('\"');
                                    if (!tmpNsSet.contains(reader.getAttributePrefix(i))) {
                                        xmlnsCode.append(" xmlns:").append(reader.getAttributePrefix(i)).append("=\"").append(reader.getAttributeNamespace(i)).append('\"');
                                        tmpNsSet.add(reader.getAttributePrefix(i));
                                    }
                                }
                            }
                        }
                        elementCode.append(xmlnsCode).append(attrCode).append('>');
                        break;
                    }
                    case 2: {
                        final QName curName = reader.getName();
                        if (elementName.equals(curName)) {
                            --depth;
                        }
                        elementCode.append("</").append("".equals(curName.getPrefix()) ? "" : (curName.getPrefix() + ':')).append(curName.getLocalPart()).append('>');
                        break;
                    }
                    case 4: {
                        elementCode.append(reader.getText());
                        break;
                    }
                    case 12: {
                        elementCode.append("<![CDATA[").append(reader.getText()).append("]]>");
                    }
                }
                if (reader.hasNext() && depth > 0) {
                    reader.next();
                }
            } while (8 != reader.getEventType() && depth > 0);
            policyRec.policyModel = ModelUnmarshaller.getUnmarshaller().unmarshalModel(new StringReader(elementCode.toString()));
            if (null != policyRec.policyModel.getPolicyId()) {
                policyRec.setUri(baseUrl + "#" + policyRec.policyModel.getPolicyId(), policyRec.policyModel.getPolicyId());
            }
            else if (policyRec.policyModel.getPolicyName() != null) {
                policyRec.setUri(policyRec.policyModel.getPolicyName(), policyRec.policyModel.getPolicyName());
            }
        }
        catch (final Exception e) {
            throw SafePolicyReader.LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1013_EXCEPTION_WHEN_READING_POLICY_ELEMENT(elementCode.toString()), e));
        }
        this.urlsRead.add(baseUrl);
        return policyRec;
    }
    
    public Set<String> getUrlsRead() {
        return this.urlsRead;
    }
    
    public String readPolicyReferenceElement(final XMLStreamReader reader) {
        try {
            if (NamespaceVersion.resolveAsToken(reader.getName()) == XmlToken.PolicyReference) {
                for (int i = 0; i < reader.getAttributeCount(); ++i) {
                    if (XmlToken.resolveToken(reader.getAttributeName(i).getLocalPart()) == XmlToken.Uri) {
                        final String uriValue = reader.getAttributeValue(i);
                        reader.next();
                        return uriValue;
                    }
                }
            }
            reader.next();
            return null;
        }
        catch (final XMLStreamException e) {
            throw SafePolicyReader.LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1001_XML_EXCEPTION_WHEN_PROCESSING_POLICY_REFERENCE(), e));
        }
    }
    
    public static String relativeToAbsoluteUrl(final String relativeUri, final String baseUri) {
        if ('#' != relativeUri.charAt(0)) {
            return relativeUri;
        }
        return (null == baseUri) ? relativeUri : (baseUri + relativeUri);
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(SafePolicyReader.class);
    }
    
    public final class PolicyRecord
    {
        PolicyRecord next;
        PolicySourceModel policyModel;
        Set<String> unresolvedURIs;
        private String uri;
        
        PolicyRecord() {
        }
        
        PolicyRecord insert(final PolicyRecord insertedRec) {
            if (null == insertedRec.unresolvedURIs || insertedRec.unresolvedURIs.isEmpty()) {
                insertedRec.next = this;
                return insertedRec;
            }
            final PolicyRecord head = this;
            PolicyRecord oneBeforeCurrent = null;
            PolicyRecord current = head;
            while (null != current.next) {
                if (null != current.unresolvedURIs && current.unresolvedURIs.contains(insertedRec.uri)) {
                    if (null == oneBeforeCurrent) {
                        insertedRec.next = current;
                        return insertedRec;
                    }
                    oneBeforeCurrent.next = insertedRec;
                    insertedRec.next = current;
                    return head;
                }
                else {
                    if (insertedRec.unresolvedURIs.remove(current.uri) && insertedRec.unresolvedURIs.isEmpty()) {
                        insertedRec.next = current.next;
                        current.next = insertedRec;
                        return head;
                    }
                    oneBeforeCurrent = current;
                    current = current.next;
                }
            }
            insertedRec.next = null;
            current.next = insertedRec;
            return head;
        }
        
        public void setUri(final String uri, final String id) throws PolicyException {
            if (SafePolicyReader.this.qualifiedPolicyUris.contains(uri)) {
                throw SafePolicyReader.LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1020_DUPLICATE_ID(id)));
            }
            this.uri = uri;
            SafePolicyReader.this.qualifiedPolicyUris.add(uri);
        }
        
        public String getUri() {
            return this.uri;
        }
        
        @Override
        public String toString() {
            String result = this.uri;
            if (null != this.next) {
                result = result + "->" + this.next.toString();
            }
            return result;
        }
    }
}
