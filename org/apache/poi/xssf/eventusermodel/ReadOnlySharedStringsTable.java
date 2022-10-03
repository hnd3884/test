package org.apache.poi.xssf.eventusermodel;

import org.xml.sax.Attributes;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.util.Removal;
import org.xml.sax.XMLReader;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.ContentHandler;
import org.apache.poi.util.XMLHelper;
import org.xml.sax.InputSource;
import java.io.PushbackInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import java.util.List;
import org.apache.poi.xssf.model.SharedStrings;
import org.xml.sax.helpers.DefaultHandler;

public class ReadOnlySharedStringsTable extends DefaultHandler implements SharedStrings
{
    protected final boolean includePhoneticRuns;
    protected int count;
    protected int uniqueCount;
    private List<String> strings;
    private StringBuilder characters;
    private boolean tIsOpen;
    private boolean inRPh;
    
    public ReadOnlySharedStringsTable(final OPCPackage pkg) throws IOException, SAXException {
        this(pkg, true);
    }
    
    public ReadOnlySharedStringsTable(final OPCPackage pkg, final boolean includePhoneticRuns) throws IOException, SAXException {
        this.includePhoneticRuns = includePhoneticRuns;
        final ArrayList<PackagePart> parts = pkg.getPartsByContentType(XSSFRelation.SHARED_STRINGS.getContentType());
        if (parts.size() > 0) {
            final PackagePart sstPart = parts.get(0);
            this.readFrom(sstPart.getInputStream());
        }
    }
    
    public ReadOnlySharedStringsTable(final PackagePart part) throws IOException, SAXException {
        this(part, true);
    }
    
    public ReadOnlySharedStringsTable(final PackagePart part, final boolean includePhoneticRuns) throws IOException, SAXException {
        this.includePhoneticRuns = includePhoneticRuns;
        this.readFrom(part.getInputStream());
    }
    
    public void readFrom(final InputStream is) throws IOException, SAXException {
        final PushbackInputStream pis = new PushbackInputStream(is, 1);
        final int emptyTest = pis.read();
        if (emptyTest > -1) {
            pis.unread(emptyTest);
            final InputSource sheetSource = new InputSource(pis);
            try {
                final XMLReader sheetParser = XMLHelper.newXMLReader();
                sheetParser.setContentHandler(this);
                sheetParser.parse(sheetSource);
            }
            catch (final ParserConfigurationException e) {
                throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
            }
        }
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
    public String getEntryAt(final int idx) {
        return this.strings.get(idx);
    }
    
    @Removal(version = "4.2")
    @Deprecated
    public List<String> getItems() {
        return this.strings;
    }
    
    @Override
    public RichTextString getItemAt(final int idx) {
        return (RichTextString)new XSSFRichTextString(this.getEntryAt(idx));
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String name, final Attributes attributes) throws SAXException {
        if (uri != null && !uri.equals("http://schemas.openxmlformats.org/spreadsheetml/2006/main")) {
            return;
        }
        if ("sst".equals(localName)) {
            final String count = attributes.getValue("count");
            if (count != null) {
                this.count = Integer.parseInt(count);
            }
            final String uniqueCount = attributes.getValue("uniqueCount");
            if (uniqueCount != null) {
                this.uniqueCount = Integer.parseInt(uniqueCount);
            }
            this.strings = new ArrayList<String>(this.uniqueCount);
            this.characters = new StringBuilder(64);
        }
        else if ("si".equals(localName)) {
            this.characters.setLength(0);
        }
        else if ("t".equals(localName)) {
            this.tIsOpen = true;
        }
        else if ("rPh".equals(localName)) {
            this.inRPh = true;
            if (this.includePhoneticRuns && this.characters.length() > 0) {
                this.characters.append(" ");
            }
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        if (uri != null && !uri.equals("http://schemas.openxmlformats.org/spreadsheetml/2006/main")) {
            return;
        }
        if ("si".equals(localName)) {
            this.strings.add(this.characters.toString());
        }
        else if ("t".equals(localName)) {
            this.tIsOpen = false;
        }
        else if ("rPh".equals(localName)) {
            this.inRPh = false;
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.tIsOpen) {
            if (this.inRPh && this.includePhoneticRuns) {
                this.characters.append(ch, start, length);
            }
            else if (!this.inRPh) {
                this.characters.append(ch, start, length);
            }
        }
    }
}
