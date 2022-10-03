package org.apache.xerces.jaxp.validation;

import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Comment;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Text;
import org.apache.xerces.xni.XNIException;
import org.w3c.dom.DocumentType;
import javax.xml.transform.dom.DOMResult;
import org.apache.xerces.xni.XMLDocumentHandler;

interface DOMDocumentHandler extends XMLDocumentHandler
{
    void setDOMResult(final DOMResult p0);
    
    void doctypeDecl(final DocumentType p0) throws XNIException;
    
    void characters(final Text p0) throws XNIException;
    
    void cdata(final CDATASection p0) throws XNIException;
    
    void comment(final Comment p0) throws XNIException;
    
    void processingInstruction(final ProcessingInstruction p0) throws XNIException;
    
    void setIgnoringCharacters(final boolean p0);
}
