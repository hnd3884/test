package org.apache.xmlbeans.impl.store;

import org.w3c.dom.Element;
import org.apache.xmlbeans.impl.soap.DetailEntry;
import org.apache.xmlbeans.impl.soap.Text;
import org.apache.xmlbeans.impl.soap.Detail;
import java.util.Locale;
import org.w3c.dom.Document;
import org.apache.xmlbeans.impl.soap.SOAPBodyElement;
import org.apache.xmlbeans.impl.soap.SOAPFault;
import javax.xml.transform.Source;
import org.apache.xmlbeans.impl.soap.SOAPPart;
import org.apache.xmlbeans.impl.soap.SOAPHeaderElement;
import org.apache.xmlbeans.impl.soap.SOAPHeader;
import org.apache.xmlbeans.impl.soap.SOAPBody;
import org.apache.xmlbeans.impl.soap.SOAPEnvelope;
import org.apache.xmlbeans.impl.soap.SOAPException;
import org.apache.xmlbeans.impl.soap.Name;
import java.util.Iterator;
import org.apache.xmlbeans.impl.soap.SOAPElement;
import org.apache.xmlbeans.impl.soap.Node;
import javax.xml.namespace.QName;

public interface Saaj
{
    public static final String SAAJ_IMPL = "SAAJ_IMPL";
    
    void setCallback(final SaajCallback p0);
    
    Class identifyElement(final QName p0, final QName p1);
    
    void soapNode_detachNode(final Node p0);
    
    void soapNode_recycleNode(final Node p0);
    
    String soapNode_getValue(final Node p0);
    
    void soapNode_setValue(final Node p0, final String p1);
    
    SOAPElement soapNode_getParentElement(final Node p0);
    
    void soapNode_setParentElement(final Node p0, final SOAPElement p1);
    
    void soapElement_removeContents(final SOAPElement p0);
    
    String soapElement_getEncodingStyle(final SOAPElement p0);
    
    void soapElement_setEncodingStyle(final SOAPElement p0, final String p1);
    
    boolean soapElement_removeNamespaceDeclaration(final SOAPElement p0, final String p1);
    
    Iterator soapElement_getAllAttributes(final SOAPElement p0);
    
    Iterator soapElement_getChildElements(final SOAPElement p0);
    
    Iterator soapElement_getNamespacePrefixes(final SOAPElement p0);
    
    SOAPElement soapElement_addAttribute(final SOAPElement p0, final Name p1, final String p2) throws SOAPException;
    
    SOAPElement soapElement_addChildElement(final SOAPElement p0, final SOAPElement p1) throws SOAPException;
    
    SOAPElement soapElement_addChildElement(final SOAPElement p0, final Name p1) throws SOAPException;
    
    SOAPElement soapElement_addChildElement(final SOAPElement p0, final String p1) throws SOAPException;
    
    SOAPElement soapElement_addChildElement(final SOAPElement p0, final String p1, final String p2) throws SOAPException;
    
    SOAPElement soapElement_addChildElement(final SOAPElement p0, final String p1, final String p2, final String p3) throws SOAPException;
    
    SOAPElement soapElement_addNamespaceDeclaration(final SOAPElement p0, final String p1, final String p2);
    
    SOAPElement soapElement_addTextNode(final SOAPElement p0, final String p1);
    
    String soapElement_getAttributeValue(final SOAPElement p0, final Name p1);
    
    Iterator soapElement_getChildElements(final SOAPElement p0, final Name p1);
    
    Name soapElement_getElementName(final SOAPElement p0);
    
    String soapElement_getNamespaceURI(final SOAPElement p0, final String p1);
    
    Iterator soapElement_getVisibleNamespacePrefixes(final SOAPElement p0);
    
    boolean soapElement_removeAttribute(final SOAPElement p0, final Name p1);
    
    SOAPBody soapEnvelope_addBody(final SOAPEnvelope p0) throws SOAPException;
    
    SOAPBody soapEnvelope_getBody(final SOAPEnvelope p0) throws SOAPException;
    
    SOAPHeader soapEnvelope_getHeader(final SOAPEnvelope p0) throws SOAPException;
    
    SOAPHeader soapEnvelope_addHeader(final SOAPEnvelope p0) throws SOAPException;
    
    Name soapEnvelope_createName(final SOAPEnvelope p0, final String p1);
    
    Name soapEnvelope_createName(final SOAPEnvelope p0, final String p1, final String p2, final String p3);
    
    Iterator soapHeader_examineAllHeaderElements(final SOAPHeader p0);
    
    Iterator soapHeader_extractAllHeaderElements(final SOAPHeader p0);
    
    Iterator soapHeader_examineHeaderElements(final SOAPHeader p0, final String p1);
    
    Iterator soapHeader_examineMustUnderstandHeaderElements(final SOAPHeader p0, final String p1);
    
    Iterator soapHeader_extractHeaderElements(final SOAPHeader p0, final String p1);
    
    SOAPHeaderElement soapHeader_addHeaderElement(final SOAPHeader p0, final Name p1);
    
    void soapPart_removeAllMimeHeaders(final SOAPPart p0);
    
    void soapPart_removeMimeHeader(final SOAPPart p0, final String p1);
    
    Iterator soapPart_getAllMimeHeaders(final SOAPPart p0);
    
    SOAPEnvelope soapPart_getEnvelope(final SOAPPart p0);
    
    Source soapPart_getContent(final SOAPPart p0);
    
    void soapPart_setContent(final SOAPPart p0, final Source p1);
    
    String[] soapPart_getMimeHeader(final SOAPPart p0, final String p1);
    
    void soapPart_addMimeHeader(final SOAPPart p0, final String p1, final String p2);
    
    void soapPart_setMimeHeader(final SOAPPart p0, final String p1, final String p2);
    
    Iterator soapPart_getMatchingMimeHeaders(final SOAPPart p0, final String[] p1);
    
    Iterator soapPart_getNonMatchingMimeHeaders(final SOAPPart p0, final String[] p1);
    
    boolean soapBody_hasFault(final SOAPBody p0);
    
    SOAPFault soapBody_addFault(final SOAPBody p0) throws SOAPException;
    
    SOAPFault soapBody_getFault(final SOAPBody p0);
    
    SOAPBodyElement soapBody_addBodyElement(final SOAPBody p0, final Name p1);
    
    SOAPBodyElement soapBody_addDocument(final SOAPBody p0, final Document p1);
    
    SOAPFault soapBody_addFault(final SOAPBody p0, final Name p1, final String p2) throws SOAPException;
    
    SOAPFault soapBody_addFault(final SOAPBody p0, final Name p1, final String p2, final Locale p3) throws SOAPException;
    
    Detail soapFault_addDetail(final SOAPFault p0) throws SOAPException;
    
    Detail soapFault_getDetail(final SOAPFault p0);
    
    String soapFault_getFaultActor(final SOAPFault p0);
    
    String soapFault_getFaultCode(final SOAPFault p0);
    
    Name soapFault_getFaultCodeAsName(final SOAPFault p0);
    
    String soapFault_getFaultString(final SOAPFault p0);
    
    Locale soapFault_getFaultStringLocale(final SOAPFault p0);
    
    void soapFault_setFaultActor(final SOAPFault p0, final String p1);
    
    void soapFault_setFaultCode(final SOAPFault p0, final Name p1) throws SOAPException;
    
    void soapFault_setFaultCode(final SOAPFault p0, final String p1) throws SOAPException;
    
    void soapFault_setFaultString(final SOAPFault p0, final String p1);
    
    void soapFault_setFaultString(final SOAPFault p0, final String p1, final Locale p2);
    
    void soapHeaderElement_setMustUnderstand(final SOAPHeaderElement p0, final boolean p1);
    
    boolean soapHeaderElement_getMustUnderstand(final SOAPHeaderElement p0);
    
    void soapHeaderElement_setActor(final SOAPHeaderElement p0, final String p1);
    
    String soapHeaderElement_getActor(final SOAPHeaderElement p0);
    
    boolean soapText_isComment(final Text p0);
    
    DetailEntry detail_addDetailEntry(final Detail p0, final Name p1);
    
    Iterator detail_getDetailEntries(final Detail p0);
    
    public interface SaajCallback
    {
        void setSaajData(final org.w3c.dom.Node p0, final Object p1);
        
        Object getSaajData(final org.w3c.dom.Node p0);
        
        Element createSoapElement(final QName p0, final QName p1);
        
        Element importSoapElement(final Document p0, final Element p1, final boolean p2, final QName p3);
    }
}
