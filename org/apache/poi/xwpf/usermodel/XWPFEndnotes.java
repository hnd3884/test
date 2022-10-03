package org.apache.poi.xwpf.usermodel;

import java.util.ArrayList;
import java.util.List;
import org.apache.xmlbeans.XmlObject;
import java.io.OutputStream;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlOptions;
import java.io.IOException;
import java.util.Iterator;
import java.io.InputStream;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.ooxml.POIXMLException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.EndnotesDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFtnEdn;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdn;
import org.apache.poi.util.Internal;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEndnotes;

public class XWPFEndnotes extends XWPFAbstractFootnotesEndnotes
{
    protected CTEndnotes ctEndnotes;
    
    public XWPFEndnotes() {
    }
    
    public XWPFEndnotes(final PackagePart part) {
        super(part);
    }
    
    @Internal
    public void setEndnotes(final CTEndnotes endnotes) {
        this.ctEndnotes = endnotes;
    }
    
    public XWPFEndnote createEndnote() {
        final CTFtnEdn newNote = CTFtnEdn.Factory.newInstance();
        newNote.setType(STFtnEdn.NORMAL);
        final XWPFEndnote footnote = this.addEndnote(newNote);
        footnote.getCTFtnEdn().setId(this.getIdManager().nextId());
        return footnote;
    }
    
    public boolean removeFootnote(final int pos) {
        if (this.ctEndnotes.sizeOfEndnoteArray() >= pos - 1) {
            this.ctEndnotes.removeEndnote(pos);
            this.listFootnote.remove(pos);
            return true;
        }
        return false;
    }
    
    @Override
    protected void onDocumentRead() throws IOException {
        try (final InputStream is = this.getPackagePart().getInputStream()) {
            final EndnotesDocument notesDoc = EndnotesDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.ctEndnotes = notesDoc.getEndnotes();
        }
        catch (final XmlException e) {
            throw new POIXMLException();
        }
        for (final CTFtnEdn note : this.ctEndnotes.getEndnoteList()) {
            this.listFootnote.add(new XWPFEndnote(note, this));
        }
    }
    
    @Override
    protected void commit() throws IOException {
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTEndnotes.type.getName().getNamespaceURI(), "endnotes"));
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.ctEndnotes.save(out, xmlOptions);
        out.close();
    }
    
    public void addEndnote(final XWPFEndnote endnote) {
        this.listFootnote.add(endnote);
        this.ctEndnotes.addNewEndnote().set((XmlObject)endnote.getCTFtnEdn());
    }
    
    @Internal
    public XWPFEndnote addEndnote(final CTFtnEdn note) {
        final CTFtnEdn newNote = this.ctEndnotes.addNewEndnote();
        newNote.set((XmlObject)note);
        final XWPFEndnote xNote = new XWPFEndnote(newNote, this);
        this.listFootnote.add(xNote);
        return xNote;
    }
    
    @Override
    public XWPFEndnote getFootnoteById(final int id) {
        return (XWPFEndnote)super.getFootnoteById(id);
    }
    
    public List<XWPFEndnote> getEndnotesList() {
        final List<XWPFEndnote> resultList = new ArrayList<XWPFEndnote>();
        for (final XWPFAbstractFootnoteEndnote note : this.listFootnote) {
            resultList.add((XWPFEndnote)note);
        }
        return resultList;
    }
    
    public boolean removeEndnote(final int pos) {
        if (this.ctEndnotes.sizeOfEndnoteArray() >= pos - 1) {
            this.ctEndnotes.removeEndnote(pos);
            this.listFootnote.remove(pos);
            return true;
        }
        return false;
    }
}
