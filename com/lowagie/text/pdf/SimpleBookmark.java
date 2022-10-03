package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import java.io.Reader;
import com.lowagie.text.xml.simpleparser.SimpleXMLParser;
import java.io.InputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import com.lowagie.text.xml.simpleparser.IanaEncodings;
import java.io.OutputStream;
import org.apache.commons.text.StringEscapeUtils;
import java.util.Map;
import java.io.Writer;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.ArrayList;
import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler;

public final class SimpleBookmark implements SimpleXMLDocHandler
{
    private ArrayList topList;
    private Stack attr;
    
    private SimpleBookmark() {
        this.attr = new Stack();
    }
    
    private static List bookmarkDepth(final PdfReader reader, PdfDictionary outline, final IntHashtable pages) {
        final ArrayList list = new ArrayList();
        while (outline != null) {
            final HashMap map = new HashMap();
            final PdfString title = (PdfString)PdfReader.getPdfObjectRelease(outline.get(PdfName.TITLE));
            map.put("Title", title.toUnicodeString());
            final PdfArray color = (PdfArray)PdfReader.getPdfObjectRelease(outline.get(PdfName.C));
            if (color != null && color.size() == 3) {
                final ByteBuffer out = new ByteBuffer();
                out.append(color.getAsNumber(0).floatValue()).append(' ');
                out.append(color.getAsNumber(1).floatValue()).append(' ');
                out.append(color.getAsNumber(2).floatValue());
                map.put("Color", PdfEncodings.convertToString(out.toByteArray(), null));
            }
            final PdfNumber style = (PdfNumber)PdfReader.getPdfObjectRelease(outline.get(PdfName.F));
            if (style != null) {
                final int f = style.intValue();
                String s = "";
                if ((f & 0x1) != 0x0) {
                    s += "italic ";
                }
                if ((f & 0x2) != 0x0) {
                    s += "bold ";
                }
                s = s.trim();
                if (s.length() != 0) {
                    map.put("Style", s);
                }
            }
            final PdfNumber count = (PdfNumber)PdfReader.getPdfObjectRelease(outline.get(PdfName.COUNT));
            if (count != null && count.intValue() < 0) {
                map.put("Open", "false");
            }
            try {
                PdfObject dest = PdfReader.getPdfObjectRelease(outline.get(PdfName.DEST));
                if (dest != null) {
                    mapGotoBookmark(map, dest, pages);
                }
                else {
                    final PdfDictionary action = (PdfDictionary)PdfReader.getPdfObjectRelease(outline.get(PdfName.A));
                    if (action != null) {
                        if (PdfName.GOTO.equals(PdfReader.getPdfObjectRelease(action.get(PdfName.S)))) {
                            dest = PdfReader.getPdfObjectRelease(action.get(PdfName.D));
                            if (dest != null) {
                                mapGotoBookmark(map, dest, pages);
                            }
                        }
                        else if (PdfName.URI.equals(PdfReader.getPdfObjectRelease(action.get(PdfName.S)))) {
                            map.put("Action", "URI");
                            map.put("URI", ((PdfString)PdfReader.getPdfObjectRelease(action.get(PdfName.URI))).toUnicodeString());
                        }
                        else if (PdfName.GOTOR.equals(PdfReader.getPdfObjectRelease(action.get(PdfName.S)))) {
                            dest = PdfReader.getPdfObjectRelease(action.get(PdfName.D));
                            if (dest != null) {
                                if (dest.isString()) {
                                    map.put("Named", dest.toString());
                                }
                                else if (dest.isName()) {
                                    map.put("NamedN", PdfName.decodeName(dest.toString()));
                                }
                                else if (dest.isArray()) {
                                    final PdfArray arr = (PdfArray)dest;
                                    final StringBuffer s2 = new StringBuffer();
                                    s2.append(arr.getPdfObject(0).toString());
                                    s2.append(' ').append(arr.getPdfObject(1).toString());
                                    for (int k = 2; k < arr.size(); ++k) {
                                        s2.append(' ').append(arr.getPdfObject(k).toString());
                                    }
                                    map.put("Page", s2.toString());
                                }
                            }
                            map.put("Action", "GoToR");
                            PdfObject file = PdfReader.getPdfObjectRelease(action.get(PdfName.F));
                            if (file != null) {
                                if (file.isString()) {
                                    map.put("File", ((PdfString)file).toUnicodeString());
                                }
                                else if (file.isDictionary()) {
                                    file = PdfReader.getPdfObject(((PdfDictionary)file).get(PdfName.F));
                                    if (file.isString()) {
                                        map.put("File", ((PdfString)file).toUnicodeString());
                                    }
                                }
                            }
                            final PdfObject newWindow = PdfReader.getPdfObjectRelease(action.get(PdfName.NEWWINDOW));
                            if (newWindow != null) {
                                map.put("NewWindow", newWindow.toString());
                            }
                        }
                        else if (PdfName.LAUNCH.equals(PdfReader.getPdfObjectRelease(action.get(PdfName.S)))) {
                            map.put("Action", "Launch");
                            PdfObject file = PdfReader.getPdfObjectRelease(action.get(PdfName.F));
                            if (file == null) {
                                file = PdfReader.getPdfObjectRelease(action.get(PdfName.WIN));
                            }
                            if (file != null) {
                                if (file.isString()) {
                                    map.put("File", ((PdfString)file).toUnicodeString());
                                }
                                else if (file.isDictionary()) {
                                    file = PdfReader.getPdfObjectRelease(((PdfDictionary)file).get(PdfName.F));
                                    if (file.isString()) {
                                        map.put("File", ((PdfString)file).toUnicodeString());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (final Exception ex) {}
            final PdfDictionary first = (PdfDictionary)PdfReader.getPdfObjectRelease(outline.get(PdfName.FIRST));
            if (first != null) {
                map.put("Kids", bookmarkDepth(reader, first, pages));
            }
            list.add(map);
            outline = (PdfDictionary)PdfReader.getPdfObjectRelease(outline.get(PdfName.NEXT));
        }
        return list;
    }
    
    private static void mapGotoBookmark(final HashMap map, final PdfObject dest, final IntHashtable pages) {
        if (dest.isString()) {
            map.put("Named", dest.toString());
        }
        else if (dest.isName()) {
            map.put("Named", PdfName.decodeName(dest.toString()));
        }
        else if (dest.isArray()) {
            map.put("Page", makeBookmarkParam((PdfArray)dest, pages));
        }
        map.put("Action", "GoTo");
    }
    
    private static String makeBookmarkParam(final PdfArray dest, final IntHashtable pages) {
        final StringBuffer s = new StringBuffer();
        final PdfObject obj = dest.getPdfObject(0);
        if (obj.isNumber()) {
            s.append(((PdfNumber)obj).intValue() + 1);
        }
        else {
            s.append(pages.get(getNumber((PdfIndirectReference)obj)));
        }
        s.append(' ').append(dest.getPdfObject(1).toString().substring(1));
        for (int k = 2; k < dest.size(); ++k) {
            s.append(' ').append(dest.getPdfObject(k).toString());
        }
        return s.toString();
    }
    
    private static int getNumber(PdfIndirectReference indirect) {
        final PdfDictionary pdfObj = (PdfDictionary)PdfReader.getPdfObjectRelease(indirect);
        if (pdfObj.contains(PdfName.TYPE) && pdfObj.get(PdfName.TYPE).equals(PdfName.PAGES) && pdfObj.contains(PdfName.KIDS)) {
            final PdfArray kids = (PdfArray)pdfObj.get(PdfName.KIDS);
            indirect = (PdfIndirectReference)kids.getPdfObject(0);
        }
        return indirect.getNumber();
    }
    
    public static List getBookmark(final PdfReader reader) {
        final PdfDictionary catalog = reader.getCatalog();
        final PdfObject obj = PdfReader.getPdfObjectRelease(catalog.get(PdfName.OUTLINES));
        if (obj == null || !obj.isDictionary()) {
            return null;
        }
        final PdfDictionary outlines = (PdfDictionary)obj;
        final IntHashtable pages = new IntHashtable();
        for (int numPages = reader.getNumberOfPages(), k = 1; k <= numPages; ++k) {
            pages.put(reader.getPageOrigRef(k).getNumber(), k);
            reader.releasePage(k);
        }
        return bookmarkDepth(reader, (PdfDictionary)PdfReader.getPdfObjectRelease(outlines.get(PdfName.FIRST)), pages);
    }
    
    public static void eliminatePages(final List list, final int[] pageRange) {
        if (list == null) {
            return;
        }
        final Iterator it = list.listIterator();
        while (it.hasNext()) {
            final HashMap map = it.next();
            boolean hit = false;
            if ("GoTo".equals(map.get("Action"))) {
                String page = map.get("Page");
                if (page != null) {
                    page = page.trim();
                    final int idx = page.indexOf(32);
                    int pageNum;
                    if (idx < 0) {
                        pageNum = Integer.parseInt(page);
                    }
                    else {
                        pageNum = Integer.parseInt(page.substring(0, idx));
                    }
                    for (int len = pageRange.length & 0xFFFFFFFE, k = 0; k < len; k += 2) {
                        if (pageNum >= pageRange[k] && pageNum <= pageRange[k + 1]) {
                            hit = true;
                            break;
                        }
                    }
                }
            }
            List kids = map.get("Kids");
            if (kids != null) {
                eliminatePages(kids, pageRange);
                if (kids.isEmpty()) {
                    map.remove("Kids");
                    kids = null;
                }
            }
            if (hit) {
                if (kids == null) {
                    it.remove();
                }
                else {
                    map.remove("Action");
                    map.remove("Page");
                    map.remove("Named");
                }
            }
        }
    }
    
    public static void shiftPageNumbers(final List list, final int pageShift, final int[] pageRange) {
        if (list == null) {
            return;
        }
        final Iterator it = list.listIterator();
        while (it.hasNext()) {
            final HashMap map = it.next();
            if ("GoTo".equals(map.get("Action"))) {
                String page = map.get("Page");
                if (page != null) {
                    page = page.trim();
                    final int idx = page.indexOf(32);
                    int pageNum;
                    if (idx < 0) {
                        pageNum = Integer.parseInt(page);
                    }
                    else {
                        pageNum = Integer.parseInt(page.substring(0, idx));
                    }
                    boolean hit = false;
                    if (pageRange == null) {
                        hit = true;
                    }
                    else {
                        for (int len = pageRange.length & 0xFFFFFFFE, k = 0; k < len; k += 2) {
                            if (pageNum >= pageRange[k] && pageNum <= pageRange[k + 1]) {
                                hit = true;
                                break;
                            }
                        }
                    }
                    if (hit) {
                        if (idx < 0) {
                            page = Integer.toString(pageNum + pageShift);
                        }
                        else {
                            page = pageNum + pageShift + page.substring(idx);
                        }
                    }
                    map.put("Page", page);
                }
            }
            final List kids = map.get("Kids");
            if (kids != null) {
                shiftPageNumbers(kids, pageShift, pageRange);
            }
        }
    }
    
    static void createOutlineAction(final PdfDictionary outline, final HashMap map, final PdfWriter writer, final boolean namedAsNames) {
        try {
            final String action = map.get("Action");
            if ("GoTo".equals(action)) {
                String p;
                if ((p = map.get("Named")) != null) {
                    if (namedAsNames) {
                        outline.put(PdfName.DEST, new PdfName(p));
                    }
                    else {
                        outline.put(PdfName.DEST, new PdfString(p, null));
                    }
                }
                else if ((p = map.get("Page")) != null) {
                    final PdfArray ar = new PdfArray();
                    final StringTokenizer tk = new StringTokenizer(p);
                    final int n = Integer.parseInt(tk.nextToken());
                    ar.add(writer.getPageReference(n));
                    if (!tk.hasMoreTokens()) {
                        ar.add(PdfName.XYZ);
                        ar.add(new float[] { 0.0f, 10000.0f, 0.0f });
                    }
                    else {
                        String fn = tk.nextToken();
                        if (fn.startsWith("/")) {
                            fn = fn.substring(1);
                        }
                        ar.add(new PdfName(fn));
                        for (int k = 0; k < 4 && tk.hasMoreTokens(); ++k) {
                            fn = tk.nextToken();
                            if (fn.equals("null")) {
                                ar.add(PdfNull.PDFNULL);
                            }
                            else {
                                ar.add(new PdfNumber(fn));
                            }
                        }
                    }
                    outline.put(PdfName.DEST, ar);
                }
            }
            else if ("GoToR".equals(action)) {
                final PdfDictionary dic = new PdfDictionary();
                String p;
                if ((p = map.get("Named")) != null) {
                    dic.put(PdfName.D, new PdfString(p, null));
                }
                else if ((p = map.get("NamedN")) != null) {
                    dic.put(PdfName.D, new PdfName(p));
                }
                else if ((p = map.get("Page")) != null) {
                    final PdfArray ar2 = new PdfArray();
                    final StringTokenizer tk2 = new StringTokenizer(p);
                    ar2.add(new PdfNumber(tk2.nextToken()));
                    if (!tk2.hasMoreTokens()) {
                        ar2.add(PdfName.XYZ);
                        ar2.add(new float[] { 0.0f, 10000.0f, 0.0f });
                    }
                    else {
                        String fn = tk2.nextToken();
                        if (fn.startsWith("/")) {
                            fn = fn.substring(1);
                        }
                        ar2.add(new PdfName(fn));
                        for (int k = 0; k < 4 && tk2.hasMoreTokens(); ++k) {
                            fn = tk2.nextToken();
                            if (fn.equals("null")) {
                                ar2.add(PdfNull.PDFNULL);
                            }
                            else {
                                ar2.add(new PdfNumber(fn));
                            }
                        }
                    }
                    dic.put(PdfName.D, ar2);
                }
                final String file = map.get("File");
                if (dic.size() > 0 && file != null) {
                    dic.put(PdfName.S, PdfName.GOTOR);
                    dic.put(PdfName.F, new PdfString(file));
                    final String nw = map.get("NewWindow");
                    if (nw != null) {
                        if (nw.equals("true")) {
                            dic.put(PdfName.NEWWINDOW, PdfBoolean.PDFTRUE);
                        }
                        else if (nw.equals("false")) {
                            dic.put(PdfName.NEWWINDOW, PdfBoolean.PDFFALSE);
                        }
                    }
                    outline.put(PdfName.A, dic);
                }
            }
            else if ("URI".equals(action)) {
                final String uri = map.get("URI");
                if (uri != null) {
                    final PdfDictionary dic = new PdfDictionary();
                    dic.put(PdfName.S, PdfName.URI);
                    dic.put(PdfName.URI, new PdfString(uri));
                    outline.put(PdfName.A, dic);
                }
            }
            else if ("Launch".equals(action)) {
                final String file2 = map.get("File");
                if (file2 != null) {
                    final PdfDictionary dic = new PdfDictionary();
                    dic.put(PdfName.S, PdfName.LAUNCH);
                    dic.put(PdfName.F, new PdfString(file2));
                    outline.put(PdfName.A, dic);
                }
            }
        }
        catch (final Exception ex) {}
    }
    
    public static Object[] iterateOutlines(final PdfWriter writer, final PdfIndirectReference parent, final List kids, final boolean namedAsNames) throws IOException {
        final PdfIndirectReference[] refs = new PdfIndirectReference[kids.size()];
        for (int k = 0; k < refs.length; ++k) {
            refs[k] = writer.getPdfIndirectReference();
        }
        int ptr = 0;
        int count = 0;
        final Iterator it = kids.listIterator();
        while (it.hasNext()) {
            final HashMap map = it.next();
            Object[] lower = null;
            final List subKid = map.get("Kids");
            if (subKid != null && !subKid.isEmpty()) {
                lower = iterateOutlines(writer, refs[ptr], subKid, namedAsNames);
            }
            final PdfDictionary outline = new PdfDictionary();
            ++count;
            if (lower != null) {
                outline.put(PdfName.FIRST, (PdfObject)lower[0]);
                outline.put(PdfName.LAST, (PdfObject)lower[1]);
                final int n = (int)lower[2];
                if ("false".equals(map.get("Open"))) {
                    outline.put(PdfName.COUNT, new PdfNumber(-n));
                }
                else {
                    outline.put(PdfName.COUNT, new PdfNumber(n));
                    count += n;
                }
            }
            outline.put(PdfName.PARENT, parent);
            if (ptr > 0) {
                outline.put(PdfName.PREV, refs[ptr - 1]);
            }
            if (ptr < refs.length - 1) {
                outline.put(PdfName.NEXT, refs[ptr + 1]);
            }
            outline.put(PdfName.TITLE, new PdfString(map.get("Title"), "UnicodeBig"));
            final String color = map.get("Color");
            if (color != null) {
                try {
                    final PdfArray arr = new PdfArray();
                    final StringTokenizer tk = new StringTokenizer(color);
                    for (int i = 0; i < 3; ++i) {
                        float f = Float.parseFloat(tk.nextToken());
                        if (f < 0.0f) {
                            f = 0.0f;
                        }
                        if (f > 1.0f) {
                            f = 1.0f;
                        }
                        arr.add(new PdfNumber(f));
                    }
                    outline.put(PdfName.C, arr);
                }
                catch (final Exception ex) {}
            }
            String style = map.get("Style");
            if (style != null) {
                style = style.toLowerCase();
                int bits = 0;
                if (style.indexOf("italic") >= 0) {
                    bits |= 0x1;
                }
                if (style.indexOf("bold") >= 0) {
                    bits |= 0x2;
                }
                if (bits != 0) {
                    outline.put(PdfName.F, new PdfNumber(bits));
                }
            }
            createOutlineAction(outline, map, writer, namedAsNames);
            writer.addToBody(outline, refs[ptr]);
            ++ptr;
        }
        return new Object[] { refs[0], refs[refs.length - 1], new Integer(count) };
    }
    
    public static void exportToXMLNode(final List list, final Writer out, final int indent, final boolean onlyASCII) throws IOException {
        String dep = "";
        for (int k = 0; k < indent; ++k) {
            dep += "  ";
        }
        for (final HashMap map : list) {
            String title = null;
            out.write(dep);
            out.write("<Title ");
            List kids = null;
            for (final Map.Entry entry : map.entrySet()) {
                final String key = entry.getKey();
                if (key.equals("Title")) {
                    title = entry.getValue();
                }
                else if (key.equals("Kids")) {
                    kids = entry.getValue();
                }
                else {
                    out.write(key);
                    out.write("=\"");
                    String value = entry.getValue();
                    if (key.equals("Named") || key.equals("NamedN")) {
                        value = SimpleNamedDestination.escapeBinaryString(value);
                    }
                    out.write(StringEscapeUtils.escapeXml11(value));
                    out.write("\" ");
                }
            }
            out.write(">");
            if (title == null) {
                title = "";
            }
            out.write(StringEscapeUtils.escapeXml11(title));
            if (kids != null) {
                out.write("\n");
                exportToXMLNode(kids, out, indent + 1, onlyASCII);
                out.write(dep);
            }
            out.write("</Title>\n");
        }
    }
    
    public static void exportToXML(final List list, final OutputStream out, final String encoding, final boolean onlyASCII) throws IOException {
        final String jenc = IanaEncodings.getJavaEncoding(encoding);
        final Writer wrt = new BufferedWriter(new OutputStreamWriter(out, jenc));
        exportToXML(list, wrt, encoding, onlyASCII);
    }
    
    public static void exportToXML(final List list, final Writer wrt, final String encoding, final boolean onlyASCII) throws IOException {
        wrt.write("<?xml version=\"1.0\" encoding=\"");
        wrt.write(StringEscapeUtils.escapeXml11(encoding));
        wrt.write("\"?>\n<Bookmark>\n");
        exportToXMLNode(list, wrt, 1, onlyASCII);
        wrt.write("</Bookmark>\n");
        wrt.flush();
    }
    
    public static List importFromXML(final InputStream in) throws IOException {
        final SimpleBookmark book = new SimpleBookmark();
        SimpleXMLParser.parse(book, in);
        return book.topList;
    }
    
    public static List importFromXML(final Reader in) throws IOException {
        final SimpleBookmark book = new SimpleBookmark();
        SimpleXMLParser.parse(book, in);
        return book.topList;
    }
    
    @Override
    public void endDocument() {
    }
    
    @Override
    public void endElement(final String tag) {
        if (tag.equals("Bookmark")) {
            if (this.attr.isEmpty()) {
                return;
            }
            throw new RuntimeException(MessageLocalization.getComposedMessage("bookmark.end.tag.out.of.place"));
        }
        else {
            if (!tag.equals("Title")) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.end.tag.1", tag));
            }
            final HashMap attributes = this.attr.pop();
            final String title = attributes.get("Title");
            attributes.put("Title", title.trim());
            String named = attributes.get("Named");
            if (named != null) {
                attributes.put("Named", SimpleNamedDestination.unEscapeBinaryString(named));
            }
            named = attributes.get("NamedN");
            if (named != null) {
                attributes.put("NamedN", SimpleNamedDestination.unEscapeBinaryString(named));
            }
            if (this.attr.isEmpty()) {
                this.topList.add(attributes);
            }
            else {
                final HashMap parent = this.attr.peek();
                List kids = parent.get("Kids");
                if (kids == null) {
                    kids = new ArrayList();
                    parent.put("Kids", kids);
                }
                kids.add(attributes);
            }
        }
    }
    
    @Override
    public void startDocument() {
    }
    
    @Override
    public void startElement(final String tag, final HashMap h) {
        if (this.topList == null) {
            if (tag.equals("Bookmark")) {
                this.topList = new ArrayList();
                return;
            }
            throw new RuntimeException(MessageLocalization.getComposedMessage("root.element.is.not.bookmark.1", tag));
        }
        else {
            if (!tag.equals("Title")) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("tag.1.not.allowed", tag));
            }
            final HashMap attributes = new HashMap(h);
            attributes.put("Title", "");
            attributes.remove("Kids");
            this.attr.push(attributes);
        }
    }
    
    @Override
    public void text(final String str) {
        if (this.attr.isEmpty()) {
            return;
        }
        final HashMap attributes = this.attr.peek();
        String title = attributes.get("Title");
        title += str;
        attributes.put("Title", title);
    }
}
