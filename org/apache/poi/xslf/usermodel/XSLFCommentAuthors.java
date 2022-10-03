package org.apache.poi.xslf.usermodel;

import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthor;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.presentationml.x2006.main.CmAuthorLstDocument;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthorList;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class XSLFCommentAuthors extends POIXMLDocumentPart
{
    private final CTCommentAuthorList _authors;
    
    XSLFCommentAuthors() {
        final CmAuthorLstDocument doc = CmAuthorLstDocument.Factory.newInstance();
        this._authors = doc.addNewCmAuthorLst();
    }
    
    XSLFCommentAuthors(final PackagePart part) throws IOException, XmlException {
        super(part);
        final CmAuthorLstDocument doc = CmAuthorLstDocument.Factory.parse(this.getPackagePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        this._authors = doc.getCmAuthorLst();
    }
    
    public CTCommentAuthorList getCTCommentAuthorsList() {
        return this._authors;
    }
    
    public CTCommentAuthor getAuthorById(final long id) {
        for (final CTCommentAuthor author : this._authors.getCmAuthorArray()) {
            if (author.getId() == id) {
                return author;
            }
        }
        return null;
    }
}
