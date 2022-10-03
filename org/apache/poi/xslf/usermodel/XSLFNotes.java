package org.apache.poi.xslf.usermodel;

import org.apache.poi.sl.usermodel.MasterSheet;
import org.apache.xmlbeans.XmlObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommonSlideData;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import org.openxmlformats.schemas.presentationml.x2006.main.NotesDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesSlide;
import org.apache.poi.sl.usermodel.Notes;

public final class XSLFNotes extends XSLFSheet implements Notes<XSLFShape, XSLFTextParagraph>
{
    private CTNotesSlide _notes;
    
    XSLFNotes() {
        this._notes = prototype();
    }
    
    XSLFNotes(final PackagePart part) throws IOException, XmlException {
        super(part);
        final NotesDocument doc = NotesDocument.Factory.parse(this.getPackagePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        this._notes = doc.getNotes();
    }
    
    private static CTNotesSlide prototype() {
        final CTNotesSlide ctNotes = CTNotesSlide.Factory.newInstance();
        final CTCommonSlideData cSld = ctNotes.addNewCSld();
        cSld.addNewSpTree();
        return ctNotes;
    }
    
    public CTNotesSlide getXmlObject() {
        return this._notes;
    }
    
    @Override
    protected String getRootElementName() {
        return "notes";
    }
    
    @Override
    public XSLFTheme getTheme() {
        final XSLFNotesMaster m = this.getMasterSheet();
        return (m != null) ? m.getTheme() : null;
    }
    
    public XSLFNotesMaster getMasterSheet() {
        for (final POIXMLDocumentPart p : this.getRelations()) {
            if (p instanceof XSLFNotesMaster) {
                return (XSLFNotesMaster)p;
            }
        }
        return null;
    }
    
    public List<List<XSLFTextParagraph>> getTextParagraphs() {
        final List<List<XSLFTextParagraph>> tp = new ArrayList<List<XSLFTextParagraph>>();
        for (final XSLFShape sh : super.getShapes()) {
            if (sh instanceof XSLFTextShape) {
                final XSLFTextShape txt = (XSLFTextShape)sh;
                tp.add(txt.getTextParagraphs());
            }
        }
        return tp;
    }
    
    @Override
    String mapSchemeColor(final String schemeColor) {
        return this.mapSchemeColor(this._notes.getClrMapOvr(), schemeColor);
    }
}
