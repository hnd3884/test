package org.apache.poi.xssf.model;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.Collections;
import org.apache.xmlbeans.XmlObject;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.util.Removal;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSst;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.HashMap;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.SstDocument;
import java.util.Map;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;
import java.util.List;
import java.io.Closeable;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class SharedStringsTable extends POIXMLDocumentPart implements SharedStrings, Closeable
{
    private final List<CTRst> strings;
    private final Map<String, Integer> stmap;
    protected int count;
    protected int uniqueCount;
    private SstDocument _sstDoc;
    private static final XmlOptions options;
    
    public SharedStringsTable() {
        this.strings = new ArrayList<CTRst>();
        this.stmap = new HashMap<String, Integer>();
        (this._sstDoc = SstDocument.Factory.newInstance()).addNewSst();
    }
    
    public SharedStringsTable(final PackagePart part) throws IOException {
        super(part);
        this.strings = new ArrayList<CTRst>();
        this.stmap = new HashMap<String, Integer>();
        this.readFrom(part.getInputStream());
    }
    
    public void readFrom(final InputStream is) throws IOException {
        try {
            int cnt = 0;
            this._sstDoc = SstDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            final CTSst sst = this._sstDoc.getSst();
            this.count = (int)sst.getCount();
            this.uniqueCount = (int)sst.getUniqueCount();
            for (final CTRst st : sst.getSiArray()) {
                this.stmap.put(this.xmlText(st), cnt);
                this.strings.add(st);
                ++cnt;
            }
        }
        catch (final XmlException e) {
            throw new IOException("unable to parse shared strings table", (Throwable)e);
        }
    }
    
    protected String xmlText(final CTRst st) {
        return st.xmlText(SharedStringsTable.options);
    }
    
    @Removal(version = "4.2")
    @Deprecated
    public CTRst getEntryAt(final int idx) {
        return this.strings.get(idx);
    }
    
    @Override
    public RichTextString getItemAt(final int idx) {
        return (RichTextString)new XSSFRichTextString(this.strings.get(idx));
    }
    
    @Override
    public int getCount() {
        return this.count;
    }
    
    @Override
    public int getUniqueCount() {
        return this.uniqueCount;
    }
    
    @Removal(version = "4.2")
    @Deprecated
    public int addEntry(final CTRst st) {
        final String s = this.xmlText(st);
        ++this.count;
        if (this.stmap.containsKey(s)) {
            return this.stmap.get(s);
        }
        ++this.uniqueCount;
        final CTRst newSt = this._sstDoc.getSst().addNewSi();
        newSt.set((XmlObject)st);
        final int idx = this.strings.size();
        this.stmap.put(s, idx);
        this.strings.add(newSt);
        return idx;
    }
    
    public int addSharedStringItem(final RichTextString string) {
        if (!(string instanceof XSSFRichTextString)) {
            throw new IllegalArgumentException("Only XSSFRichTextString argument is supported");
        }
        return this.addEntry(((XSSFRichTextString)string).getCTRst());
    }
    
    @Removal(version = "4.2")
    @Deprecated
    public List<CTRst> getItems() {
        return Collections.unmodifiableList((List<? extends CTRst>)this.strings);
    }
    
    public List<RichTextString> getSharedStringItems() {
        final ArrayList<RichTextString> items = new ArrayList<RichTextString>();
        for (final CTRst rst : this.strings) {
            items.add((RichTextString)new XSSFRichTextString(rst));
        }
        return Collections.unmodifiableList((List<? extends RichTextString>)items);
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveCDataLengthThreshold(1000000);
        xmlOptions.setSaveCDataEntityCountThreshold(-1);
        final CTSst sst = this._sstDoc.getSst();
        sst.setCount((long)this.count);
        sst.setUniqueCount((long)this.uniqueCount);
        this._sstDoc.save(out, xmlOptions);
    }
    
    @Override
    protected void commit() throws IOException {
        final PackagePart part = this.getPackagePart();
        try (final OutputStream out = part.getOutputStream()) {
            this.writeTo(out);
        }
    }
    
    @Override
    public void close() throws IOException {
    }
    
    static {
        (options = new XmlOptions()).put((Object)"SAVE_INNER");
        SharedStringsTable.options.put((Object)"SAVE_AGGRESSIVE_NAMESPACES");
        SharedStringsTable.options.put((Object)"SAVE_USE_DEFAULT_NAMESPACE");
        SharedStringsTable.options.setSaveImplicitNamespaces((Map)Collections.singletonMap("", "http://schemas.openxmlformats.org/spreadsheetml/2006/main"));
    }
}
