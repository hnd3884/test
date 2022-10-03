package org.apache.poi.xssf.binary;

import java.io.Serializable;
import java.io.InputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.ss.util.CellRangeUtil;
import org.apache.poi.ss.util.CellRangeAddress;
import java.util.Iterator;
import java.util.Comparator;
import java.util.TreeMap;
import org.apache.poi.ss.util.CellAddress;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.Map;
import java.util.List;
import com.zaxxer.sparsebits.SparseBitSet;
import org.apache.poi.util.Internal;

@Internal
public class XSSFBHyperlinksTable
{
    private static final SparseBitSet RECORDS;
    private final List<XSSFHyperlinkRecord> hyperlinkRecords;
    private Map<String, String> relIdToHyperlink;
    
    public XSSFBHyperlinksTable(final PackagePart sheetPart) throws IOException {
        this.hyperlinkRecords = new ArrayList<XSSFHyperlinkRecord>();
        this.relIdToHyperlink = new HashMap<String, String>();
        this.loadUrlsFromSheetRels(sheetPart);
        final HyperlinkSheetScraper scraper = new HyperlinkSheetScraper(sheetPart.getInputStream());
        scraper.parse();
    }
    
    public Map<CellAddress, List<XSSFHyperlinkRecord>> getHyperLinks() {
        final Map<CellAddress, List<XSSFHyperlinkRecord>> hyperlinkMap = new TreeMap<CellAddress, List<XSSFHyperlinkRecord>>(new TopLeftCellAddressComparator());
        for (final XSSFHyperlinkRecord hyperlinkRecord : this.hyperlinkRecords) {
            final CellAddress cellAddress = new CellAddress(hyperlinkRecord.getCellRangeAddress().getFirstRow(), hyperlinkRecord.getCellRangeAddress().getFirstColumn());
            List<XSSFHyperlinkRecord> list = hyperlinkMap.get(cellAddress);
            if (list == null) {
                list = new ArrayList<XSSFHyperlinkRecord>();
            }
            list.add(hyperlinkRecord);
            hyperlinkMap.put(cellAddress, list);
        }
        return hyperlinkMap;
    }
    
    public List<XSSFHyperlinkRecord> findHyperlinkRecord(final CellAddress cellAddress) {
        List<XSSFHyperlinkRecord> overlapping = null;
        final CellRangeAddress targetCellRangeAddress = new CellRangeAddress(cellAddress.getRow(), cellAddress.getRow(), cellAddress.getColumn(), cellAddress.getColumn());
        for (final XSSFHyperlinkRecord record : this.hyperlinkRecords) {
            if (CellRangeUtil.intersect(targetCellRangeAddress, record.getCellRangeAddress()) != 1) {
                if (overlapping == null) {
                    overlapping = new ArrayList<XSSFHyperlinkRecord>();
                }
                overlapping.add(record);
            }
        }
        return overlapping;
    }
    
    private void loadUrlsFromSheetRels(final PackagePart sheetPart) {
        try {
            for (final PackageRelationship rel : sheetPart.getRelationshipsByType(XSSFRelation.SHEET_HYPERLINKS.getRelation())) {
                this.relIdToHyperlink.put(rel.getId(), rel.getTargetURI().toString());
            }
        }
        catch (final InvalidFormatException ex) {}
    }
    
    static {
        (RECORDS = new SparseBitSet()).set(XSSFBRecordType.BrtHLink.getId());
    }
    
    private class HyperlinkSheetScraper extends XSSFBParser
    {
        private XSSFBCellRange hyperlinkCellRange;
        private final StringBuilder xlWideStringBuffer;
        
        HyperlinkSheetScraper(final InputStream is) {
            super(is, XSSFBHyperlinksTable.RECORDS);
            this.hyperlinkCellRange = new XSSFBCellRange();
            this.xlWideStringBuffer = new StringBuilder();
        }
        
        @Override
        public void handleRecord(final int recordType, final byte[] data) throws XSSFBParseException {
            if (recordType != XSSFBRecordType.BrtHLink.getId()) {
                return;
            }
            int offset = 0;
            this.hyperlinkCellRange = XSSFBCellRange.parse(data, offset, this.hyperlinkCellRange);
            offset += 16;
            this.xlWideStringBuffer.setLength(0);
            offset += XSSFBUtils.readXLNullableWideString(data, offset, this.xlWideStringBuffer);
            final String relId = this.xlWideStringBuffer.toString();
            this.xlWideStringBuffer.setLength(0);
            offset += XSSFBUtils.readXLWideString(data, offset, this.xlWideStringBuffer);
            String location = this.xlWideStringBuffer.toString();
            this.xlWideStringBuffer.setLength(0);
            offset += XSSFBUtils.readXLWideString(data, offset, this.xlWideStringBuffer);
            final String toolTip = this.xlWideStringBuffer.toString();
            this.xlWideStringBuffer.setLength(0);
            XSSFBUtils.readXLWideString(data, offset, this.xlWideStringBuffer);
            final String display = this.xlWideStringBuffer.toString();
            final CellRangeAddress cellRangeAddress = new CellRangeAddress(this.hyperlinkCellRange.firstRow, this.hyperlinkCellRange.lastRow, this.hyperlinkCellRange.firstCol, this.hyperlinkCellRange.lastCol);
            final String url = XSSFBHyperlinksTable.this.relIdToHyperlink.get(relId);
            if (location.length() == 0) {
                location = url;
            }
            XSSFBHyperlinksTable.this.hyperlinkRecords.add(new XSSFHyperlinkRecord(cellRangeAddress, relId, location, toolTip, display));
        }
    }
    
    private static class TopLeftCellAddressComparator implements Comparator<CellAddress>, Serializable
    {
        private static final long serialVersionUID = 1L;
        
        @Override
        public int compare(final CellAddress o1, final CellAddress o2) {
            if (o1.getRow() < o2.getRow()) {
                return -1;
            }
            if (o1.getRow() > o2.getRow()) {
                return 1;
            }
            if (o1.getColumn() < o2.getColumn()) {
                return -1;
            }
            if (o1.getColumn() > o2.getColumn()) {
                return 1;
            }
            return 0;
        }
    }
}
