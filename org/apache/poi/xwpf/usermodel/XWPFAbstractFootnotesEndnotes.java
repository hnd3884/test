package org.apache.poi.xwpf.usermodel;

import java.util.Iterator;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.ArrayList;
import org.apache.poi.openxml4j.opc.OPCPackage;
import java.util.List;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public abstract class XWPFAbstractFootnotesEndnotes extends POIXMLDocumentPart
{
    protected XWPFDocument document;
    protected List<XWPFAbstractFootnoteEndnote> listFootnote;
    private FootnoteEndnoteIdManager idManager;
    
    public XWPFAbstractFootnotesEndnotes(final OPCPackage pkg) {
        super(pkg);
        this.listFootnote = new ArrayList<XWPFAbstractFootnoteEndnote>();
    }
    
    public XWPFAbstractFootnotesEndnotes(final OPCPackage pkg, final String coreDocumentRel) {
        super(pkg, coreDocumentRel);
        this.listFootnote = new ArrayList<XWPFAbstractFootnoteEndnote>();
    }
    
    public XWPFAbstractFootnotesEndnotes() {
        this.listFootnote = new ArrayList<XWPFAbstractFootnoteEndnote>();
    }
    
    public XWPFAbstractFootnotesEndnotes(final PackagePart part) {
        super(part);
        this.listFootnote = new ArrayList<XWPFAbstractFootnoteEndnote>();
    }
    
    public XWPFAbstractFootnotesEndnotes(final POIXMLDocumentPart parent, final PackagePart part) {
        super(parent, part);
        this.listFootnote = new ArrayList<XWPFAbstractFootnoteEndnote>();
    }
    
    public XWPFAbstractFootnoteEndnote getFootnoteById(final int id) {
        for (final XWPFAbstractFootnoteEndnote note : this.listFootnote) {
            if (note.getCTFtnEdn().getId().intValue() == id) {
                return note;
            }
        }
        return null;
    }
    
    public XWPFDocument getXWPFDocument() {
        if (this.document != null) {
            return this.document;
        }
        return (XWPFDocument)this.getParent();
    }
    
    public void setXWPFDocument(final XWPFDocument doc) {
        this.document = doc;
    }
    
    public void setIdManager(final FootnoteEndnoteIdManager footnoteIdManager) {
        this.idManager = footnoteIdManager;
    }
    
    public FootnoteEndnoteIdManager getIdManager() {
        return this.idManager;
    }
}
