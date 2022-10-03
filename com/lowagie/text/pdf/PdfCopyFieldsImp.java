package com.lowagie.text.pdf;

import java.util.StringTokenizer;
import com.lowagie.text.ExceptionConverter;
import java.util.Collection;
import java.util.Map;
import java.util.Iterator;
import com.lowagie.text.exceptions.BadPasswordException;
import java.io.IOException;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.List;
import com.lowagie.text.DocListener;
import com.lowagie.text.DocumentException;
import java.io.OutputStream;
import com.lowagie.text.Document;
import java.util.HashMap;
import java.util.ArrayList;

class PdfCopyFieldsImp extends PdfWriter
{
    private static final PdfName iTextTag;
    private static final Integer zero;
    ArrayList readers;
    HashMap readers2intrefs;
    HashMap pages2intrefs;
    HashMap visited;
    ArrayList fields;
    RandomAccessFileOrArray file;
    HashMap fieldTree;
    ArrayList pageRefs;
    ArrayList pageDics;
    PdfDictionary resources;
    PdfDictionary form;
    boolean closing;
    Document nd;
    private HashMap tabOrder;
    private ArrayList calculationOrder;
    private ArrayList calculationOrderRefs;
    private boolean hasSignature;
    protected static final HashMap widgetKeys;
    protected static final HashMap fieldKeys;
    
    PdfCopyFieldsImp(final OutputStream os) throws DocumentException {
        this(os, '\0');
    }
    
    PdfCopyFieldsImp(final OutputStream os, final char pdfVersion) throws DocumentException {
        super(new PdfDocument(), os);
        this.readers = new ArrayList();
        this.readers2intrefs = new HashMap();
        this.pages2intrefs = new HashMap();
        this.visited = new HashMap();
        this.fields = new ArrayList();
        this.fieldTree = new HashMap();
        this.pageRefs = new ArrayList();
        this.pageDics = new ArrayList();
        this.resources = new PdfDictionary();
        this.closing = false;
        this.calculationOrder = new ArrayList();
        this.pdf.addWriter(this);
        if (pdfVersion != '\0') {
            super.setPdfVersion(pdfVersion);
        }
        (this.nd = new Document()).addDocListener(this.pdf);
    }
    
    void addDocument(PdfReader reader, final List pagesToKeep) throws DocumentException, IOException {
        if (!this.readers2intrefs.containsKey(reader) && reader.isTampered()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("the.document.was.reused"));
        }
        reader = new PdfReader(reader);
        reader.selectPages(pagesToKeep);
        if (reader.getNumberOfPages() == 0) {
            return;
        }
        reader.setTampered(false);
        this.addDocument(reader);
    }
    
    void addDocument(PdfReader reader) throws DocumentException, IOException {
        if (!reader.isOpenedWithFullPermissions()) {
            throw new BadPasswordException(MessageLocalization.getComposedMessage("pdfreader.not.opened.with.owner.password"));
        }
        this.openDoc();
        if (this.readers2intrefs.containsKey(reader)) {
            reader = new PdfReader(reader);
        }
        else {
            if (reader.isTampered()) {
                throw new DocumentException(MessageLocalization.getComposedMessage("the.document.was.reused"));
            }
            reader.consolidateNamedDestinations();
            reader.setTampered(true);
        }
        reader.shuffleSubsetNames();
        this.readers2intrefs.put(reader, new IntHashtable());
        this.readers.add(reader);
        final int len = reader.getNumberOfPages();
        final IntHashtable refs = new IntHashtable();
        for (int p = 1; p <= len; ++p) {
            refs.put(reader.getPageOrigRef(p).getNumber(), 1);
            reader.releasePage(p);
        }
        this.pages2intrefs.put(reader, refs);
        this.visited.put(reader, new IntHashtable());
        this.fields.add(reader.getAcroFields());
        this.updateCalculationOrder(reader);
    }
    
    private static String getCOName(final PdfReader reader, PRIndirectReference ref) {
        String name = "";
        while (ref != null) {
            final PdfObject obj = PdfReader.getPdfObject(ref);
            if (obj == null) {
                break;
            }
            if (obj.type() != 6) {
                break;
            }
            final PdfDictionary dic = (PdfDictionary)obj;
            final PdfString t = dic.getAsString(PdfName.T);
            if (t != null) {
                name = t.toUnicodeString() + "." + name;
            }
            ref = (PRIndirectReference)dic.get(PdfName.PARENT);
        }
        if (name.endsWith(".")) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }
    
    protected void updateCalculationOrder(final PdfReader reader) {
        final PdfDictionary catalog = reader.getCatalog();
        final PdfDictionary acro = catalog.getAsDict(PdfName.ACROFORM);
        if (acro == null) {
            return;
        }
        final PdfArray co = acro.getAsArray(PdfName.CO);
        if (co == null || co.size() == 0) {
            return;
        }
        final AcroFields af = reader.getAcroFields();
        for (int k = 0; k < co.size(); ++k) {
            final PdfObject obj = co.getPdfObject(k);
            if (obj != null) {
                if (obj.isIndirect()) {
                    String name = getCOName(reader, (PRIndirectReference)obj);
                    if (af.getFieldItem(name) != null) {
                        name = "." + name;
                        if (!this.calculationOrder.contains(name)) {
                            this.calculationOrder.add(name);
                        }
                    }
                }
            }
        }
    }
    
    void propagate(final PdfObject obj, final PdfIndirectReference refo, final boolean restricted) {
        if (obj == null) {
            return;
        }
        if (obj instanceof PdfIndirectReference) {
            return;
        }
        switch (obj.type()) {
            case 6:
            case 7: {
                final PdfDictionary dic = (PdfDictionary)obj;
                for (final PdfName key : dic.getKeys()) {
                    if (restricted) {
                        if (key.equals(PdfName.PARENT)) {
                            continue;
                        }
                        if (key.equals(PdfName.KIDS)) {
                            continue;
                        }
                    }
                    final PdfObject ob = dic.get(key);
                    if (ob != null && ob.isIndirect()) {
                        final PRIndirectReference ind = (PRIndirectReference)ob;
                        if (this.setVisited(ind) || this.isPage(ind)) {
                            continue;
                        }
                        final PdfIndirectReference ref = this.getNewReference(ind);
                        this.propagate(PdfReader.getPdfObjectRelease(ind), ref, restricted);
                    }
                    else {
                        this.propagate(ob, null, restricted);
                    }
                }
                break;
            }
            case 5: {
                final Iterator it2 = ((PdfArray)obj).listIterator();
                while (it2.hasNext()) {
                    final PdfObject ob2 = it2.next();
                    if (ob2 != null && ob2.isIndirect()) {
                        final PRIndirectReference ind2 = (PRIndirectReference)ob2;
                        if (this.isVisited(ind2) || this.isPage(ind2)) {
                            continue;
                        }
                        final PdfIndirectReference ref2 = this.getNewReference(ind2);
                        this.propagate(PdfReader.getPdfObjectRelease(ind2), ref2, restricted);
                    }
                    else {
                        this.propagate(ob2, null, restricted);
                    }
                }
                break;
            }
            case 10: {
                throw new RuntimeException(MessageLocalization.getComposedMessage("reference.pointing.to.reference"));
            }
        }
    }
    
    private void adjustTabOrder(final PdfArray annots, final PdfIndirectReference ind, final PdfNumber nn) {
        final int v = nn.intValue();
        ArrayList t = this.tabOrder.get(annots);
        if (t == null) {
            t = new ArrayList();
            for (int size = annots.size() - 1, k = 0; k < size; ++k) {
                t.add(PdfCopyFieldsImp.zero);
            }
            t.add(new Integer(v));
            this.tabOrder.put(annots, t);
            annots.add(ind);
        }
        else {
            int size;
            int k;
            for (size = (k = t.size() - 1); k >= 0; --k) {
                if (t.get(k) <= v) {
                    t.add(k + 1, new Integer(v));
                    annots.add(k + 1, ind);
                    size = -2;
                    break;
                }
            }
            if (size != -2) {
                t.add(0, new Integer(v));
                annots.add(0, ind);
            }
        }
    }
    
    protected PdfArray branchForm(final HashMap level, final PdfIndirectReference parent, final String fname) throws IOException {
        final PdfArray arr = new PdfArray();
        for (final Map.Entry entry : level.entrySet()) {
            final String name = entry.getKey();
            final Object obj = entry.getValue();
            final PdfIndirectReference ind = this.getPdfIndirectReference();
            final PdfDictionary dic = new PdfDictionary();
            if (parent != null) {
                dic.put(PdfName.PARENT, parent);
            }
            dic.put(PdfName.T, new PdfString(name, "UnicodeBig"));
            final String fname2 = fname + "." + name;
            final int coidx = this.calculationOrder.indexOf(fname2);
            if (coidx >= 0) {
                this.calculationOrderRefs.set(coidx, ind);
            }
            if (obj instanceof HashMap) {
                dic.put(PdfName.KIDS, this.branchForm((HashMap)obj, ind, fname2));
                arr.add(ind);
                this.addToBody(dic, ind);
            }
            else {
                final ArrayList list = (ArrayList)obj;
                dic.mergeDifferent(list.get(0));
                if (list.size() == 3) {
                    dic.mergeDifferent(list.get(2));
                    final int page = list.get(1);
                    final PdfDictionary pageDic = this.pageDics.get(page - 1);
                    PdfArray annots = pageDic.getAsArray(PdfName.ANNOTS);
                    if (annots == null) {
                        annots = new PdfArray();
                        pageDic.put(PdfName.ANNOTS, annots);
                    }
                    final PdfNumber nn = (PdfNumber)dic.get(PdfCopyFieldsImp.iTextTag);
                    dic.remove(PdfCopyFieldsImp.iTextTag);
                    this.adjustTabOrder(annots, ind, nn);
                }
                else {
                    final PdfArray kids = new PdfArray();
                    for (int k = 1; k < list.size(); k += 2) {
                        final int page2 = list.get(k);
                        final PdfDictionary pageDic2 = this.pageDics.get(page2 - 1);
                        PdfArray annots2 = pageDic2.getAsArray(PdfName.ANNOTS);
                        if (annots2 == null) {
                            annots2 = new PdfArray();
                            pageDic2.put(PdfName.ANNOTS, annots2);
                        }
                        final PdfDictionary widget = new PdfDictionary();
                        widget.merge(list.get(k + 1));
                        widget.put(PdfName.PARENT, ind);
                        final PdfNumber nn2 = (PdfNumber)widget.get(PdfCopyFieldsImp.iTextTag);
                        widget.remove(PdfCopyFieldsImp.iTextTag);
                        final PdfIndirectReference wref = this.addToBody(widget).getIndirectReference();
                        this.adjustTabOrder(annots2, wref, nn2);
                        kids.add(wref);
                        this.propagate(widget, null, false);
                    }
                    dic.put(PdfName.KIDS, kids);
                }
                arr.add(ind);
                this.addToBody(dic, ind);
                this.propagate(dic, null, false);
            }
        }
        return arr;
    }
    
    protected void createAcroForms() throws IOException {
        if (this.fieldTree.isEmpty()) {
            return;
        }
        (this.form = new PdfDictionary()).put(PdfName.DR, this.resources);
        this.propagate(this.resources, null, false);
        this.form.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
        this.tabOrder = new HashMap();
        this.calculationOrderRefs = new ArrayList(this.calculationOrder);
        this.form.put(PdfName.FIELDS, this.branchForm(this.fieldTree, null, ""));
        if (this.hasSignature) {
            this.form.put(PdfName.SIGFLAGS, new PdfNumber(3));
        }
        final PdfArray co = new PdfArray();
        for (int k = 0; k < this.calculationOrderRefs.size(); ++k) {
            final Object obj = this.calculationOrderRefs.get(k);
            if (obj instanceof PdfIndirectReference) {
                co.add((PdfObject)obj);
            }
        }
        if (co.size() > 0) {
            this.form.put(PdfName.CO, co);
        }
    }
    
    @Override
    public void close() {
        if (this.closing) {
            super.close();
            return;
        }
        this.closing = true;
        try {
            this.closeIt();
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    protected void closeIt() throws IOException {
        for (int k = 0; k < this.readers.size(); ++k) {
            this.readers.get(k).removeFields();
        }
        for (int r = 0; r < this.readers.size(); ++r) {
            final PdfReader reader = this.readers.get(r);
            for (int page = 1; page <= reader.getNumberOfPages(); ++page) {
                this.pageRefs.add(this.getNewReference(reader.getPageOrigRef(page)));
                this.pageDics.add(reader.getPageN(page));
            }
        }
        this.mergeFields();
        this.createAcroForms();
        for (int r = 0; r < this.readers.size(); ++r) {
            final PdfReader reader = this.readers.get(r);
            for (int page = 1; page <= reader.getNumberOfPages(); ++page) {
                final PdfDictionary dic = reader.getPageN(page);
                final PdfIndirectReference pageRef = this.getNewReference(reader.getPageOrigRef(page));
                final PdfIndirectReference parent = this.root.addPageRef(pageRef);
                dic.put(PdfName.PARENT, parent);
                this.propagate(dic, pageRef, false);
            }
        }
        for (final Map.Entry entry : this.readers2intrefs.entrySet()) {
            final PdfReader reader2 = entry.getKey();
            try {
                (this.file = reader2.getSafeFile()).reOpen();
                final IntHashtable t = entry.getValue();
                final int[] keys = t.toOrderedKeys();
                for (int i = 0; i < keys.length; ++i) {
                    final PRIndirectReference ref = new PRIndirectReference(reader2, keys[i]);
                    this.addToBody(PdfReader.getPdfObjectRelease(ref), t.get(keys[i]));
                }
            }
            finally {
                try {
                    this.file.close();
                    reader2.close();
                }
                catch (final Exception ex) {}
            }
        }
        this.pdf.close();
    }
    
    void addPageOffsetToField(final HashMap fd, final int pageOffset) {
        if (pageOffset == 0) {
            return;
        }
        for (final AcroFields.Item item : fd.values()) {
            for (int k = 0; k < item.size(); ++k) {
                final int p = item.getPage(k);
                item.forcePage(k, p + pageOffset);
            }
        }
    }
    
    void createWidgets(final ArrayList list, final AcroFields.Item item) {
        for (int k = 0; k < item.size(); ++k) {
            list.add(item.getPage(k));
            final PdfDictionary merged = item.getMerged(k);
            final PdfObject dr = merged.get(PdfName.DR);
            if (dr != null) {
                PdfFormField.mergeResources(this.resources, (PdfDictionary)PdfReader.getPdfObject(dr));
            }
            final PdfDictionary widget = new PdfDictionary();
            for (final PdfName key : merged.getKeys()) {
                if (PdfCopyFieldsImp.widgetKeys.containsKey(key)) {
                    widget.put(key, merged.get(key));
                }
            }
            widget.put(PdfCopyFieldsImp.iTextTag, new PdfNumber(item.getTabOrder(k) + 1));
            list.add(widget);
        }
    }
    
    void mergeField(final String name, final AcroFields.Item item) {
        HashMap map = this.fieldTree;
        final StringTokenizer tk = new StringTokenizer(name, ".");
        if (!tk.hasMoreTokens()) {
            return;
        }
        while (true) {
            final String s = tk.nextToken();
            Object obj = map.get(s);
            if (tk.hasMoreTokens()) {
                if (obj == null) {
                    obj = new HashMap();
                    map.put(s, obj);
                    map = (HashMap)obj;
                }
                else {
                    if (!(obj instanceof HashMap)) {
                        return;
                    }
                    map = (HashMap)obj;
                }
            }
            else {
                if (obj instanceof HashMap) {
                    return;
                }
                final PdfDictionary merged = item.getMerged(0);
                if (obj == null) {
                    final PdfDictionary field = new PdfDictionary();
                    if (PdfName.SIG.equals(merged.get(PdfName.FT))) {
                        this.hasSignature = true;
                    }
                    for (final PdfName key : merged.getKeys()) {
                        if (PdfCopyFieldsImp.fieldKeys.containsKey(key)) {
                            field.put(key, merged.get(key));
                        }
                    }
                    final ArrayList list = new ArrayList();
                    list.add(field);
                    this.createWidgets(list, item);
                    map.put(s, list);
                }
                else {
                    final ArrayList list2 = (ArrayList)obj;
                    final PdfDictionary field2 = list2.get(0);
                    final PdfName type1 = (PdfName)field2.get(PdfName.FT);
                    final PdfName type2 = (PdfName)merged.get(PdfName.FT);
                    if (type1 == null || !type1.equals(type2)) {
                        return;
                    }
                    int flag1 = 0;
                    final PdfObject f1 = field2.get(PdfName.FF);
                    if (f1 != null && f1.isNumber()) {
                        flag1 = ((PdfNumber)f1).intValue();
                    }
                    int flag2 = 0;
                    final PdfObject f2 = merged.get(PdfName.FF);
                    if (f2 != null && f2.isNumber()) {
                        flag2 = ((PdfNumber)f2).intValue();
                    }
                    if (type1.equals(PdfName.BTN)) {
                        if (((flag1 ^ flag2) & 0x10000) != 0x0) {
                            return;
                        }
                        if ((flag1 & 0x10000) == 0x0 && ((flag1 ^ flag2) & 0x8000) != 0x0) {
                            return;
                        }
                    }
                    else if (type1.equals(PdfName.CH) && ((flag1 ^ flag2) & 0x20000) != 0x0) {
                        return;
                    }
                    this.createWidgets(list2, item);
                }
            }
        }
    }
    
    void mergeWithMaster(final HashMap fd) {
        for (final Map.Entry entry : fd.entrySet()) {
            final String name = entry.getKey();
            this.mergeField(name, entry.getValue());
        }
    }
    
    void mergeFields() {
        int pageOffset = 0;
        for (int k = 0; k < this.fields.size(); ++k) {
            final HashMap fd = this.fields.get(k).getFields();
            this.addPageOffsetToField(fd, pageOffset);
            this.mergeWithMaster(fd);
            pageOffset += this.readers.get(k).getNumberOfPages();
        }
    }
    
    @Override
    public PdfIndirectReference getPageReference(final int page) {
        return this.pageRefs.get(page - 1);
    }
    
    @Override
    protected PdfDictionary getCatalog(final PdfIndirectReference rootObj) {
        try {
            final PdfDictionary cat = this.pdf.getCatalog(rootObj);
            if (this.form != null) {
                final PdfIndirectReference ref = this.addToBody(this.form).getIndirectReference();
                cat.put(PdfName.ACROFORM, ref);
            }
            return cat;
        }
        catch (final IOException e) {
            throw new ExceptionConverter(e);
        }
    }
    
    protected PdfIndirectReference getNewReference(final PRIndirectReference ref) {
        return new PdfIndirectReference(0, this.getNewObjectNumber(ref.getReader(), ref.getNumber(), 0));
    }
    
    @Override
    protected int getNewObjectNumber(final PdfReader reader, final int number, final int generation) {
        final IntHashtable refs = this.readers2intrefs.get(reader);
        int n = refs.get(number);
        if (n == 0) {
            n = this.getIndirectReferenceNumber();
            refs.put(number, n);
        }
        return n;
    }
    
    protected boolean setVisited(final PRIndirectReference ref) {
        final IntHashtable refs = this.visited.get(ref.getReader());
        return refs != null && refs.put(ref.getNumber(), 1) != 0;
    }
    
    protected boolean isVisited(final PRIndirectReference ref) {
        final IntHashtable refs = this.visited.get(ref.getReader());
        return refs != null && refs.containsKey(ref.getNumber());
    }
    
    protected boolean isVisited(final PdfReader reader, final int number, final int generation) {
        final IntHashtable refs = this.readers2intrefs.get(reader);
        return refs.containsKey(number);
    }
    
    protected boolean isPage(final PRIndirectReference ref) {
        final IntHashtable refs = this.pages2intrefs.get(ref.getReader());
        return refs != null && refs.containsKey(ref.getNumber());
    }
    
    @Override
    RandomAccessFileOrArray getReaderFile(final PdfReader reader) {
        return this.file;
    }
    
    public void openDoc() {
        if (!this.nd.isOpen()) {
            this.nd.open();
        }
    }
    
    static {
        iTextTag = new PdfName("_iTextTag_");
        zero = new Integer(0);
        widgetKeys = new HashMap();
        fieldKeys = new HashMap();
        final Integer one = new Integer(1);
        PdfCopyFieldsImp.widgetKeys.put(PdfName.SUBTYPE, one);
        PdfCopyFieldsImp.widgetKeys.put(PdfName.CONTENTS, one);
        PdfCopyFieldsImp.widgetKeys.put(PdfName.RECT, one);
        PdfCopyFieldsImp.widgetKeys.put(PdfName.NM, one);
        PdfCopyFieldsImp.widgetKeys.put(PdfName.M, one);
        PdfCopyFieldsImp.widgetKeys.put(PdfName.F, one);
        PdfCopyFieldsImp.widgetKeys.put(PdfName.BS, one);
        PdfCopyFieldsImp.widgetKeys.put(PdfName.BORDER, one);
        PdfCopyFieldsImp.widgetKeys.put(PdfName.AP, one);
        PdfCopyFieldsImp.widgetKeys.put(PdfName.AS, one);
        PdfCopyFieldsImp.widgetKeys.put(PdfName.C, one);
        PdfCopyFieldsImp.widgetKeys.put(PdfName.A, one);
        PdfCopyFieldsImp.widgetKeys.put(PdfName.STRUCTPARENT, one);
        PdfCopyFieldsImp.widgetKeys.put(PdfName.OC, one);
        PdfCopyFieldsImp.widgetKeys.put(PdfName.H, one);
        PdfCopyFieldsImp.widgetKeys.put(PdfName.MK, one);
        PdfCopyFieldsImp.widgetKeys.put(PdfName.DA, one);
        PdfCopyFieldsImp.widgetKeys.put(PdfName.Q, one);
        PdfCopyFieldsImp.fieldKeys.put(PdfName.AA, one);
        PdfCopyFieldsImp.fieldKeys.put(PdfName.FT, one);
        PdfCopyFieldsImp.fieldKeys.put(PdfName.TU, one);
        PdfCopyFieldsImp.fieldKeys.put(PdfName.TM, one);
        PdfCopyFieldsImp.fieldKeys.put(PdfName.FF, one);
        PdfCopyFieldsImp.fieldKeys.put(PdfName.V, one);
        PdfCopyFieldsImp.fieldKeys.put(PdfName.DV, one);
        PdfCopyFieldsImp.fieldKeys.put(PdfName.DS, one);
        PdfCopyFieldsImp.fieldKeys.put(PdfName.RV, one);
        PdfCopyFieldsImp.fieldKeys.put(PdfName.OPT, one);
        PdfCopyFieldsImp.fieldKeys.put(PdfName.MAXLEN, one);
        PdfCopyFieldsImp.fieldKeys.put(PdfName.TI, one);
        PdfCopyFieldsImp.fieldKeys.put(PdfName.I, one);
        PdfCopyFieldsImp.fieldKeys.put(PdfName.LOCK, one);
        PdfCopyFieldsImp.fieldKeys.put(PdfName.SV, one);
    }
}
