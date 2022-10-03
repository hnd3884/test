package org.apache.poi.xssf.binary;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.util.Removal;
import java.util.Collection;
import java.io.InputStream;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.ArrayList;
import org.apache.poi.openxml4j.opc.OPCPackage;
import java.util.List;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.model.SharedStrings;

@Internal
public class XSSFBSharedStringsTable implements SharedStrings
{
    private int count;
    private int uniqueCount;
    private List<String> strings;
    
    public XSSFBSharedStringsTable(final OPCPackage pkg) throws IOException, SAXException {
        this.strings = new ArrayList<String>();
        final ArrayList<PackagePart> parts = pkg.getPartsByContentType(XSSFBRelation.SHARED_STRINGS_BINARY.getContentType());
        if (parts.size() > 0) {
            final PackagePart sstPart = parts.get(0);
            this.readFrom(sstPart.getInputStream());
        }
    }
    
    XSSFBSharedStringsTable(final PackagePart part) throws IOException, SAXException {
        this.strings = new ArrayList<String>();
        this.readFrom(part.getInputStream());
    }
    
    private void readFrom(final InputStream inputStream) throws IOException {
        final SSTBinaryReader reader = new SSTBinaryReader(inputStream);
        reader.parse();
    }
    
    @Removal(version = "4.2")
    @Deprecated
    public List<String> getItems() {
        final List<String> ret = new ArrayList<String>(this.strings.size());
        ret.addAll(this.strings);
        return ret;
    }
    
    @Removal(version = "4.2")
    @Deprecated
    public String getEntryAt(final int idx) {
        return this.strings.get(idx);
    }
    
    @Override
    public RichTextString getItemAt(final int idx) {
        return (RichTextString)new XSSFRichTextString(this.getEntryAt(idx));
    }
    
    @Override
    public int getCount() {
        return this.count;
    }
    
    @Override
    public int getUniqueCount() {
        return this.uniqueCount;
    }
    
    private class SSTBinaryReader extends XSSFBParser
    {
        SSTBinaryReader(final InputStream is) {
            super(is);
        }
        
        @Override
        public void handleRecord(final int recordType, final byte[] data) throws XSSFBParseException {
            final XSSFBRecordType type = XSSFBRecordType.lookup(recordType);
            switch (type) {
                case BrtSstItem: {
                    final XSSFBRichStr rstr = XSSFBRichStr.build(data, 0);
                    XSSFBSharedStringsTable.this.strings.add(rstr.getString());
                    break;
                }
                case BrtBeginSst: {
                    XSSFBSharedStringsTable.this.count = XSSFBUtils.castToInt(LittleEndian.getUInt(data, 0));
                    XSSFBSharedStringsTable.this.uniqueCount = XSSFBUtils.castToInt(LittleEndian.getUInt(data, 4));
                    break;
                }
            }
        }
    }
}
