package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import java.util.StringTokenizer;
import java.io.Reader;
import com.lowagie.text.xml.simpleparser.SimpleXMLParser;
import java.io.InputStream;
import org.apache.commons.text.StringEscapeUtils;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import com.lowagie.text.xml.simpleparser.IanaEncodings;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler;

public final class SimpleNamedDestination implements SimpleXMLDocHandler
{
    private HashMap xmlNames;
    private HashMap xmlLast;
    
    private SimpleNamedDestination() {
    }
    
    public static HashMap getNamedDestination(final PdfReader reader, final boolean fromNames) {
        final IntHashtable pages = new IntHashtable();
        for (int numPages = reader.getNumberOfPages(), k = 1; k <= numPages; ++k) {
            pages.put(reader.getPageOrigRef(k).getNumber(), k);
        }
        final HashMap names = fromNames ? reader.getNamedDestinationFromNames() : reader.getNamedDestinationFromStrings();
        final Iterator it = names.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry entry = it.next();
            final PdfArray arr = entry.getValue();
            final StringBuffer s = new StringBuffer();
            try {
                s.append(pages.get(arr.getAsIndirectObject(0).getNumber()));
                s.append(' ').append(arr.getPdfObject(1).toString().substring(1));
                for (int i = 2; i < arr.size(); ++i) {
                    s.append(' ').append(arr.getPdfObject(i).toString());
                }
                entry.setValue(s.toString());
            }
            catch (final Exception e) {
                it.remove();
            }
        }
        return names;
    }
    
    public static void exportToXML(final HashMap names, final OutputStream out, final String encoding, final boolean onlyASCII) throws IOException {
        final String jenc = IanaEncodings.getJavaEncoding(encoding);
        final Writer wrt = new BufferedWriter(new OutputStreamWriter(out, jenc));
        exportToXML(names, wrt, encoding, onlyASCII);
    }
    
    public static void exportToXML(final HashMap names, final Writer wrt, final String encoding, final boolean onlyASCII) throws IOException {
        wrt.write("<?xml version=\"1.0\" encoding=\"");
        wrt.write(StringEscapeUtils.escapeXml11(encoding));
        wrt.write("\"?>\n<Destination>\n");
        for (final Map.Entry entry : names.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            wrt.write("  <Name Page=\"");
            wrt.write(StringEscapeUtils.escapeXml11(value));
            wrt.write("\">");
            wrt.write(StringEscapeUtils.escapeXml11(escapeBinaryString(key)));
            wrt.write("</Name>\n");
        }
        wrt.write("</Destination>\n");
        wrt.flush();
    }
    
    public static HashMap importFromXML(final InputStream in) throws IOException {
        final SimpleNamedDestination names = new SimpleNamedDestination();
        SimpleXMLParser.parse(names, in);
        return names.xmlNames;
    }
    
    public static HashMap importFromXML(final Reader in) throws IOException {
        final SimpleNamedDestination names = new SimpleNamedDestination();
        SimpleXMLParser.parse(names, in);
        return names.xmlNames;
    }
    
    static PdfArray createDestinationArray(final String value, final PdfWriter writer) {
        final PdfArray ar = new PdfArray();
        final StringTokenizer tk = new StringTokenizer(value);
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
        return ar;
    }
    
    public static PdfDictionary outputNamedDestinationAsNames(final HashMap names, final PdfWriter writer) {
        final PdfDictionary dic = new PdfDictionary();
        for (final Map.Entry entry : names.entrySet()) {
            try {
                final String key = entry.getKey();
                final String value = entry.getValue();
                final PdfArray ar = createDestinationArray(value, writer);
                final PdfName kn = new PdfName(key);
                dic.put(kn, ar);
            }
            catch (final Exception ex) {}
        }
        return dic;
    }
    
    public static PdfDictionary outputNamedDestinationAsStrings(final HashMap names, final PdfWriter writer) throws IOException {
        final HashMap n2 = new HashMap(names);
        final Iterator it = n2.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry entry = it.next();
            try {
                final String value = entry.getValue();
                final PdfArray ar = createDestinationArray(value, writer);
                entry.setValue(writer.addToBody(ar).getIndirectReference());
            }
            catch (final Exception e) {
                it.remove();
            }
        }
        return PdfNameTree.writeTree(n2, writer);
    }
    
    public static String escapeBinaryString(final String s) {
        final StringBuffer buf = new StringBuffer();
        for (final char c : s.toCharArray()) {
            if (c < ' ') {
                buf.append('\\');
                final String octal = "00" + Integer.toOctalString(c);
                buf.append(octal.substring(octal.length() - 3));
            }
            else if (c == '\\') {
                buf.append("\\\\");
            }
            else {
                buf.append(c);
            }
        }
        return buf.toString();
    }
    
    public static String unEscapeBinaryString(final String s) {
        final StringBuffer buf = new StringBuffer();
        final char[] cc = s.toCharArray();
        for (int len = cc.length, k = 0; k < len; ++k) {
            char c = cc[k];
            if (c == '\\') {
                if (++k >= len) {
                    buf.append('\\');
                    break;
                }
                c = cc[k];
                if (c >= '0' && c <= '7') {
                    int n = c - '0';
                    ++k;
                    for (int j = 0; j < 2 && k < len; ++k, n = n * 8 + c - 48, ++j) {
                        c = cc[k];
                        if (c < '0' || c > '7') {
                            break;
                        }
                    }
                    --k;
                    buf.append((char)n);
                }
                else {
                    buf.append(c);
                }
            }
            else {
                buf.append(c);
            }
        }
        return buf.toString();
    }
    
    @Override
    public void endDocument() {
    }
    
    @Override
    public void endElement(final String tag) {
        if (tag.equals("Destination")) {
            if (this.xmlLast == null && this.xmlNames != null) {
                return;
            }
            throw new RuntimeException(MessageLocalization.getComposedMessage("destination.end.tag.out.of.place"));
        }
        else {
            if (!tag.equals("Name")) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.end.tag.1", tag));
            }
            if (this.xmlLast == null || this.xmlNames == null) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("name.end.tag.out.of.place"));
            }
            if (!this.xmlLast.containsKey("Page")) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("page.attribute.missing"));
            }
            this.xmlNames.put(unEscapeBinaryString(this.xmlLast.get("Name")), this.xmlLast.get("Page"));
            this.xmlLast = null;
        }
    }
    
    @Override
    public void startDocument() {
    }
    
    @Override
    public void startElement(final String tag, final HashMap h) {
        if (this.xmlNames == null) {
            if (tag.equals("Destination")) {
                this.xmlNames = new HashMap();
                return;
            }
            throw new RuntimeException(MessageLocalization.getComposedMessage("root.element.is.not.destination"));
        }
        else {
            if (!tag.equals("Name")) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("tag.1.not.allowed", tag));
            }
            if (this.xmlLast != null) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("nested.tags.are.not.allowed"));
            }
            (this.xmlLast = new HashMap(h)).put("Name", "");
        }
    }
    
    @Override
    public void text(final String str) {
        if (this.xmlLast == null) {
            return;
        }
        String name = this.xmlLast.get("Name");
        name += str;
        this.xmlLast.put("Name", name);
    }
}
