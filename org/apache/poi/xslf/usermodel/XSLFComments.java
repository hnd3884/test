package org.apache.poi.xslf.usermodel;

import org.openxmlformats.schemas.presentationml.x2006.main.CTComment;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentList;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.presentationml.x2006.main.CmLstDocument;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class XSLFComments extends POIXMLDocumentPart
{
    private final CmLstDocument doc;
    
    XSLFComments() {
        this.doc = CmLstDocument.Factory.newInstance();
    }
    
    XSLFComments(final PackagePart part) throws IOException, XmlException {
        super(part);
        this.doc = CmLstDocument.Factory.parse(this.getPackagePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }
    
    public CTCommentList getCTCommentsList() {
        return this.doc.getCmLst();
    }
    
    public int getNumberOfComments() {
        return this.doc.getCmLst().sizeOfCmArray();
    }
    
    public CTComment getCommentAt(final int pos) {
        return this.doc.getCmLst().getCmArray(pos);
    }
}
