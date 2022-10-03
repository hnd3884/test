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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.FootnotesDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFtnEdn;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdn;
import org.apache.poi.util.Internal;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFootnotes;

public class XWPFFootnotes extends XWPFAbstractFootnotesEndnotes
{
    protected CTFootnotes ctFootnotes;
    
    public XWPFFootnotes(final PackagePart part) {
        super(part);
    }
    
    public XWPFFootnotes() {
    }
    
    @Internal
    public void setFootnotes(final CTFootnotes footnotes) {
        this.ctFootnotes = footnotes;
    }
    
    public XWPFFootnote createFootnote() {
        final CTFtnEdn newNote = CTFtnEdn.Factory.newInstance();
        newNote.setType(STFtnEdn.NORMAL);
        final XWPFFootnote footnote = this.addFootnote(newNote);
        footnote.getCTFtnEdn().setId(this.getIdManager().nextId());
        return footnote;
    }
    
    public boolean removeFootnote(final int pos) {
        if (this.ctFootnotes.sizeOfFootnoteArray() >= pos - 1) {
            this.ctFootnotes.removeFootnote(pos);
            this.listFootnote.remove(pos);
            return true;
        }
        return false;
    }
    
    @Override
    protected void onDocumentRead() throws IOException {
        try (final InputStream is = this.getPackagePart().getInputStream()) {
            final FootnotesDocument notesDoc = FootnotesDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.ctFootnotes = notesDoc.getFootnotes();
        }
        catch (final XmlException e) {
            throw new POIXMLException();
        }
        for (final CTFtnEdn note : this.ctFootnotes.getFootnoteList()) {
            this.listFootnote.add(new XWPFFootnote(note, this));
        }
    }
    
    @Override
    protected void commit() throws IOException {
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTFootnotes.type.getName().getNamespaceURI(), "footnotes"));
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.ctFootnotes.save(out, xmlOptions);
        out.close();
    }
    
    public void addFootnote(final XWPFFootnote footnote) {
        this.listFootnote.add(footnote);
        this.ctFootnotes.addNewFootnote().set((XmlObject)footnote.getCTFtnEdn());
    }
    
    @Internal
    public XWPFFootnote addFootnote(final CTFtnEdn note) {
        final CTFtnEdn newNote = this.ctFootnotes.addNewFootnote();
        newNote.set((XmlObject)note);
        final XWPFFootnote xNote = new XWPFFootnote(newNote, this);
        this.listFootnote.add(xNote);
        return xNote;
    }
    
    public List<XWPFFootnote> getFootnotesList() {
        final List<XWPFFootnote> resultList = new ArrayList<XWPFFootnote>();
        for (final XWPFAbstractFootnoteEndnote note : this.listFootnote) {
            resultList.add((XWPFFootnote)note);
        }
        return resultList;
    }
}
