package com.lowagie.text.xml.xmp;

import java.util.Map;
import com.lowagie.text.pdf.PdfObject;
import java.util.Iterator;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfDictionary;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class XmpWriter
{
    public static final String UTF8 = "UTF-8";
    public static final String UTF16 = "UTF-16";
    public static final String UTF16BE = "UTF-16BE";
    public static final String UTF16LE = "UTF-16LE";
    public static final String EXTRASPACE = "                                                                                                   \n";
    protected int extraSpace;
    protected OutputStreamWriter writer;
    protected String about;
    public static final String XPACKET_PI_BEGIN = "<?xpacket begin=\"\ufeff\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n";
    public static final String XPACKET_PI_END_W = "<?xpacket end=\"w\"?>";
    public static final String XPACKET_PI_END_R = "<?xpacket end=\"r\"?>";
    protected char end;
    
    public XmpWriter(final OutputStream os, final String utfEncoding, final int extraSpace) throws IOException {
        this.end = 'w';
        this.extraSpace = extraSpace;
        (this.writer = new OutputStreamWriter(os, utfEncoding)).write("<?xpacket begin=\"\ufeff\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n");
        this.writer.write("<x:xmpmeta xmlns:x=\"adobe:ns:meta/\">\n");
        this.writer.write("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n");
        this.about = "";
    }
    
    public XmpWriter(final OutputStream os) throws IOException {
        this(os, "UTF-8", 20);
    }
    
    public XmpWriter(final OutputStream os, final PdfDictionary info, final int PdfXConformance) throws IOException {
        this(os);
        if (info != null) {
            final DublinCoreSchema dc = new DublinCoreSchema();
            final PdfSchema p = new PdfSchema();
            final XmpBasicSchema basic = new XmpBasicSchema();
            for (final PdfName key : info.getKeys()) {
                final PdfObject obj = info.get(key);
                if (obj == null) {
                    continue;
                }
                if (PdfName.TITLE.equals(key)) {
                    dc.addTitle(((PdfString)obj).toUnicodeString());
                }
                if (PdfName.AUTHOR.equals(key)) {
                    dc.addAuthor(((PdfString)obj).toUnicodeString());
                }
                if (PdfName.SUBJECT.equals(key)) {
                    dc.addSubject(((PdfString)obj).toUnicodeString());
                    dc.addDescription(((PdfString)obj).toUnicodeString());
                }
                if (PdfName.KEYWORDS.equals(key)) {
                    p.addKeywords(((PdfString)obj).toUnicodeString());
                }
                if (PdfName.CREATOR.equals(key)) {
                    basic.addCreatorTool(((PdfString)obj).toUnicodeString());
                }
                if (PdfName.PRODUCER.equals(key)) {
                    p.addProducer(((PdfString)obj).toUnicodeString());
                }
                if (PdfName.CREATIONDATE.equals(key)) {
                    basic.addCreateDate(((PdfDate)obj).getW3CDate());
                }
                if (!PdfName.MODDATE.equals(key)) {
                    continue;
                }
                basic.addModDate(((PdfDate)obj).getW3CDate());
            }
            if (dc.size() > 0) {
                this.addRdfDescription(dc);
            }
            if (p.size() > 0) {
                this.addRdfDescription(p);
            }
            if (basic.size() > 0) {
                this.addRdfDescription(basic);
            }
            if (PdfXConformance == 3 || PdfXConformance == 4) {
                final PdfA1Schema a1 = new PdfA1Schema();
                if (PdfXConformance == 3) {
                    a1.addConformance("A");
                }
                else {
                    a1.addConformance("B");
                }
                this.addRdfDescription(a1);
            }
        }
    }
    
    public XmpWriter(final OutputStream os, final Map info) throws IOException {
        this(os);
        if (info != null) {
            final DublinCoreSchema dc = new DublinCoreSchema();
            final PdfSchema p = new PdfSchema();
            final XmpBasicSchema basic = new XmpBasicSchema();
            for (final Map.Entry entry : info.entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                if (value == null) {
                    continue;
                }
                if ("Title".equals(key)) {
                    dc.addTitle(value);
                }
                if ("Author".equals(key)) {
                    dc.addAuthor(value);
                }
                if ("Subject".equals(key)) {
                    dc.addSubject(value);
                    dc.addDescription(value);
                }
                if ("Keywords".equals(key)) {
                    p.addKeywords(value);
                }
                if ("Creator".equals(key)) {
                    basic.addCreatorTool(value);
                }
                if ("Producer".equals(key)) {
                    p.addProducer(value);
                }
                if ("CreationDate".equals(key)) {
                    basic.addCreateDate(PdfDate.getW3CDate(value));
                }
                if (!"ModDate".equals(key)) {
                    continue;
                }
                basic.addModDate(PdfDate.getW3CDate(value));
            }
            if (dc.size() > 0) {
                this.addRdfDescription(dc);
            }
            if (p.size() > 0) {
                this.addRdfDescription(p);
            }
            if (basic.size() > 0) {
                this.addRdfDescription(basic);
            }
        }
    }
    
    public void setReadOnly() {
        this.end = 'r';
    }
    
    public void setAbout(final String about) {
        this.about = about;
    }
    
    public void addRdfDescription(final String xmlns, final String content) throws IOException {
        this.writer.write("<rdf:Description rdf:about=\"");
        this.writer.write(this.about);
        this.writer.write("\" ");
        this.writer.write(xmlns);
        this.writer.write(">");
        this.writer.write(content);
        this.writer.write("</rdf:Description>\n");
    }
    
    public void addRdfDescription(final XmpSchema s) throws IOException {
        this.writer.write("<rdf:Description rdf:about=\"");
        this.writer.write(this.about);
        this.writer.write("\" ");
        this.writer.write(s.getXmlns());
        this.writer.write(">");
        this.writer.write(s.toString());
        this.writer.write("</rdf:Description>\n");
    }
    
    public void close() throws IOException {
        this.writer.write("</rdf:RDF>");
        this.writer.write("</x:xmpmeta>\n");
        for (int i = 0; i < this.extraSpace; ++i) {
            this.writer.write("                                                                                                   \n");
        }
        this.writer.write((this.end == 'r') ? "<?xpacket end=\"r\"?>" : "<?xpacket end=\"w\"?>");
        this.writer.flush();
        this.writer.close();
    }
}
