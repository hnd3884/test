package org.apache.poi.xssf.eventusermodel;

import org.apache.poi.util.LittleEndian;
import java.util.LinkedList;
import java.util.List;
import org.apache.poi.xssf.binary.XSSFBParseException;
import org.apache.poi.xssf.binary.XSSFBUtils;
import org.apache.poi.xssf.binary.XSSFBRecordType;
import com.zaxxer.sparsebits.SparseBitSet;
import org.apache.poi.xssf.binary.XSSFBParser;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.xssf.binary.XSSFBCommentsTable;
import org.apache.poi.xssf.model.CommentsTable;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.util.POILogFactory;
import java.util.ArrayList;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.binary.XSSFBRelation;
import org.apache.poi.xssf.binary.XSSFBStylesTable;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.util.Iterator;
import java.io.InputStream;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import java.util.Set;
import org.apache.poi.util.POILogger;

public class XSSFBReader extends XSSFReader
{
    private static final POILogger log;
    private static final Set<String> WORKSHEET_RELS;
    
    public XSSFBReader(final OPCPackage pkg) throws IOException, OpenXML4JException {
        super(pkg);
    }
    
    public String getAbsPathMetadata() throws IOException {
        try (final InputStream is = this.workbookPart.getInputStream()) {
            final PathExtractor p = new PathExtractor(is);
            p.parse();
            return p.getPath();
        }
    }
    
    @Override
    public Iterator<InputStream> getSheetsData() throws IOException, InvalidFormatException {
        return new SheetIterator(this.workbookPart);
    }
    
    public XSSFBStylesTable getXSSFBStylesTable() throws IOException {
        final ArrayList<PackagePart> parts = this.pkg.getPartsByContentType(XSSFBRelation.STYLES_BINARY.getContentType());
        if (parts.size() == 0) {
            return null;
        }
        return new XSSFBStylesTable(parts.get(0).getInputStream());
    }
    
    static {
        log = POILogFactory.getLogger((Class)XSSFBReader.class);
        WORKSHEET_RELS = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList(XSSFRelation.WORKSHEET.getRelation(), XSSFRelation.CHARTSHEET.getRelation(), XSSFRelation.MACRO_SHEET_BIN.getRelation(), XSSFRelation.INTL_MACRO_SHEET_BIN.getRelation(), XSSFRelation.DIALOG_SHEET_BIN.getRelation())));
    }
    
    public static class SheetIterator extends XSSFReader.SheetIterator
    {
        private SheetIterator(final PackagePart wb) throws IOException {
            super(wb);
        }
        
        @Override
        Set<String> getSheetRelationships() {
            return XSSFBReader.WORKSHEET_RELS;
        }
        
        @Override
        Iterator<XSSFSheetRef> createSheetIteratorFromWB(final PackagePart wb) throws IOException {
            final SheetRefLoader sheetRefLoader = new SheetRefLoader(wb.getInputStream());
            sheetRefLoader.parse();
            return sheetRefLoader.getSheets().iterator();
        }
        
        @Override
        public CommentsTable getSheetComments() {
            throw new IllegalArgumentException("Please use getXSSFBSheetComments");
        }
        
        public XSSFBCommentsTable getXSSFBSheetComments() {
            final PackagePart sheetPkg = this.getSheetPart();
            try {
                final PackageRelationshipCollection commentsList = sheetPkg.getRelationshipsByType(XSSFRelation.SHEET_COMMENTS.getRelation());
                if (commentsList.size() > 0) {
                    final PackageRelationship comments = commentsList.getRelationship(0);
                    if (comments == null || comments.getTargetURI() == null) {
                        return null;
                    }
                    final PackagePartName commentsName = PackagingURIHelper.createPartName(comments.getTargetURI());
                    final PackagePart commentsPart = sheetPkg.getPackage().getPart(commentsName);
                    return new XSSFBCommentsTable(commentsPart.getInputStream());
                }
            }
            catch (final InvalidFormatException | IOException e) {
                return null;
            }
            return null;
        }
    }
    
    private static class PathExtractor extends XSSFBParser
    {
        private static SparseBitSet RECORDS;
        private String path;
        
        public PathExtractor(final InputStream is) {
            super(is, PathExtractor.RECORDS);
        }
        
        @Override
        public void handleRecord(final int recordType, final byte[] data) throws XSSFBParseException {
            if (recordType != XSSFBRecordType.BrtAbsPath15.getId()) {
                return;
            }
            final StringBuilder sb = new StringBuilder();
            XSSFBUtils.readXLWideString(data, 0, sb);
            this.path = sb.toString();
        }
        
        String getPath() {
            return this.path;
        }
        
        static {
            (PathExtractor.RECORDS = new SparseBitSet()).set(XSSFBRecordType.BrtAbsPath15.getId());
        }
    }
    
    private static class SheetRefLoader extends XSSFBParser
    {
        List<XSSFSheetRef> sheets;
        
        private SheetRefLoader(final InputStream is) {
            super(is);
            this.sheets = new LinkedList<XSSFSheetRef>();
        }
        
        @Override
        public void handleRecord(final int recordType, final byte[] data) throws XSSFBParseException {
            if (recordType == XSSFBRecordType.BrtBundleSh.getId()) {
                this.addWorksheet(data);
            }
        }
        
        private void addWorksheet(final byte[] data) {
            try {
                this.tryToAddWorksheet(data);
            }
            catch (final XSSFBParseException e) {
                if (!this.tryOldFormat(data)) {
                    throw e;
                }
                XSSFBReader.log.log(5, new Object[] { "This file was written with a beta version of Excel. POI will try to parse the file as a regular xlsb." });
            }
        }
        
        private void tryToAddWorksheet(final byte[] data) throws XSSFBParseException {
            int offset = 0;
            LittleEndian.getUInt(data, offset);
            offset += 4;
            final long iTabID = LittleEndian.getUInt(data, offset);
            offset += 4;
            if (iTabID < 1L || iTabID > 65535L) {
                throw new XSSFBParseException("table id out of range: " + iTabID);
            }
            final StringBuilder sb = new StringBuilder();
            offset += XSSFBUtils.readXLWideString(data, offset, sb);
            final String relId = sb.toString();
            sb.setLength(0);
            XSSFBUtils.readXLWideString(data, offset, sb);
            final String name = sb.toString();
            if (relId.trim().length() > 0) {
                this.sheets.add(new XSSFSheetRef(relId, name));
            }
        }
        
        private boolean tryOldFormat(final byte[] data) throws XSSFBParseException {
            int offset = 8;
            final long iTabID = LittleEndian.getUInt(data, offset);
            offset += 4;
            if (iTabID < 1L || iTabID > 65535L) {
                throw new XSSFBParseException("table id out of range: " + iTabID);
            }
            final StringBuilder sb = new StringBuilder();
            offset += XSSFBUtils.readXLWideString(data, offset, sb);
            final String relId = sb.toString();
            sb.setLength(0);
            offset += XSSFBUtils.readXLWideString(data, offset, sb);
            final String name = sb.toString();
            if (relId.trim().length() > 0) {
                this.sheets.add(new XSSFSheetRef(relId, name));
            }
            return offset == data.length;
        }
        
        List<XSSFSheetRef> getSheets() {
            return this.sheets;
        }
    }
}
