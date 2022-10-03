package org.apache.poi.xslf.usermodel;

import org.apache.poi.sl.usermodel.SimpleShape;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.xmlbeans.XmlObject;
import java.io.InputStream;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import org.openxmlformats.schemas.presentationml.x2006.main.NotesMasterDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesMaster;
import org.apache.poi.sl.usermodel.MasterSheet;

public class XSLFNotesMaster extends XSLFSheet implements MasterSheet<XSLFShape, XSLFTextParagraph>
{
    private CTNotesMaster _slide;
    
    XSLFNotesMaster() {
        this._slide = prototype();
    }
    
    protected XSLFNotesMaster(final PackagePart part) throws IOException, XmlException {
        super(part);
        final NotesMasterDocument doc = NotesMasterDocument.Factory.parse(this.getPackagePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        this._slide = doc.getNotesMaster();
    }
    
    private static CTNotesMaster prototype() {
        final InputStream is = XSLFNotesMaster.class.getResourceAsStream("notesMaster.xml");
        if (is == null) {
            throw new POIXMLException("Missing resource 'notesMaster.xml'");
        }
        try {
            try {
                final NotesMasterDocument doc = NotesMasterDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                return doc.getNotesMaster();
            }
            finally {
                is.close();
            }
        }
        catch (final Exception e) {
            throw new POIXMLException("Can't initialize NotesMaster", e);
        }
    }
    
    public CTNotesMaster getXmlObject() {
        return this._slide;
    }
    
    @Override
    protected String getRootElementName() {
        return "notesMaster";
    }
    
    public MasterSheet<XSLFShape, XSLFTextParagraph> getMasterSheet() {
        return null;
    }
    
    @Override
    boolean isSupportTheme() {
        return true;
    }
    
    @Override
    String mapSchemeColor(final String schemeColor) {
        final String notesMasterColor = this.mapSchemeColor(this._slide.getClrMap(), schemeColor);
        return (notesMasterColor == null) ? schemeColor : notesMasterColor;
    }
}
