package org.apache.poi.xssf.streaming;

import java.util.NoSuchElementException;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.util.Removal;
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import java.util.List;
import org.apache.poi.ss.usermodel.Name;
import java.io.File;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.poi.openxml4j.util.ZipFileZipEntrySource;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import java.io.FileOutputStream;
import org.apache.poi.util.TempFile;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.util.NotImplemented;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFChartSheet;
import org.apache.poi.openxml4j.util.ZipArchiveThresholdInputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import java.io.OutputStream;
import org.apache.poi.openxml4j.util.ZipEntrySource;
import java.io.IOException;
import org.apache.poi.util.Internal;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Sheet;
import java.util.HashMap;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.usermodel.Workbook;

public class SXSSFWorkbook implements Workbook
{
    public static final int DEFAULT_WINDOW_SIZE = 100;
    private static final POILogger logger;
    private final XSSFWorkbook _wb;
    private final Map<SXSSFSheet, XSSFSheet> _sxFromXHash;
    private final Map<XSSFSheet, SXSSFSheet> _xFromSxHash;
    private int _randomAccessWindowSize;
    private boolean _compressTmpFiles;
    private final SharedStringsTable _sharedStringSource;
    private Zip64Mode zip64Mode;
    
    public SXSSFWorkbook() {
        this(null);
    }
    
    public SXSSFWorkbook(final XSSFWorkbook workbook) {
        this(workbook, 100);
    }
    
    public SXSSFWorkbook(final XSSFWorkbook workbook, final int rowAccessWindowSize) {
        this(workbook, rowAccessWindowSize, false);
    }
    
    public SXSSFWorkbook(final XSSFWorkbook workbook, final int rowAccessWindowSize, final boolean compressTmpFiles) {
        this(workbook, rowAccessWindowSize, compressTmpFiles, false);
    }
    
    public SXSSFWorkbook(final XSSFWorkbook workbook, final int rowAccessWindowSize, final boolean compressTmpFiles, final boolean useSharedStringsTable) {
        this._sxFromXHash = new HashMap<SXSSFSheet, XSSFSheet>();
        this._xFromSxHash = new HashMap<XSSFSheet, SXSSFSheet>();
        this._randomAccessWindowSize = 100;
        this.zip64Mode = Zip64Mode.AsNeeded;
        this.setRandomAccessWindowSize(rowAccessWindowSize);
        this.setCompressTempFiles(compressTmpFiles);
        if (workbook == null) {
            this._wb = new XSSFWorkbook();
            this._sharedStringSource = (useSharedStringsTable ? this._wb.getSharedStringSource() : null);
        }
        else {
            this._wb = workbook;
            this._sharedStringSource = (useSharedStringsTable ? this._wb.getSharedStringSource() : null);
            for (final Sheet sheet : this._wb) {
                this.createAndRegisterSXSSFSheet((XSSFSheet)sheet);
            }
        }
    }
    
    public SXSSFWorkbook(final int rowAccessWindowSize) {
        this(null, rowAccessWindowSize);
    }
    
    public int getRandomAccessWindowSize() {
        return this._randomAccessWindowSize;
    }
    
    private void setRandomAccessWindowSize(final int rowAccessWindowSize) {
        if (rowAccessWindowSize == 0 || rowAccessWindowSize < -1) {
            throw new IllegalArgumentException("rowAccessWindowSize must be greater than 0 or -1");
        }
        this._randomAccessWindowSize = rowAccessWindowSize;
    }
    
    public void setZip64Mode(final Zip64Mode zip64Mode) {
        this.zip64Mode = zip64Mode;
    }
    
    public boolean isCompressTempFiles() {
        return this._compressTmpFiles;
    }
    
    public void setCompressTempFiles(final boolean compress) {
        this._compressTmpFiles = compress;
    }
    
    @Internal
    protected SharedStringsTable getSharedStringSource() {
        return this._sharedStringSource;
    }
    
    protected SheetDataWriter createSheetDataWriter() throws IOException {
        if (this._compressTmpFiles) {
            return new GZIPSheetDataWriter(this._sharedStringSource);
        }
        return new SheetDataWriter(this._sharedStringSource);
    }
    
    XSSFSheet getXSSFSheet(final SXSSFSheet sheet) {
        return this._sxFromXHash.get(sheet);
    }
    
    SXSSFSheet getSXSSFSheet(final XSSFSheet sheet) {
        return this._xFromSxHash.get(sheet);
    }
    
    void registerSheetMapping(final SXSSFSheet sxSheet, final XSSFSheet xSheet) {
        this._sxFromXHash.put(sxSheet, xSheet);
        this._xFromSxHash.put(xSheet, sxSheet);
    }
    
    void deregisterSheetMapping(final XSSFSheet xSheet) {
        final SXSSFSheet sxSheet = this.getSXSSFSheet(xSheet);
        try {
            sxSheet.getSheetDataWriter().close();
        }
        catch (final IOException ex) {}
        this._sxFromXHash.remove(sxSheet);
        this._xFromSxHash.remove(xSheet);
    }
    
    private XSSFSheet getSheetFromZipEntryName(final String sheetRef) {
        for (final XSSFSheet sheet : this._sxFromXHash.values()) {
            if (sheetRef.equals(sheet.getPackagePart().getPartName().getName().substring(1))) {
                return sheet;
            }
        }
        return null;
    }
    
    protected void injectData(final ZipEntrySource zipEntrySource, final OutputStream out) throws IOException {
        final ArchiveOutputStream zos = (ArchiveOutputStream)this.createArchiveOutputStream(out);
        try {
            final Enumeration<? extends ZipArchiveEntry> en = zipEntrySource.getEntries();
            while (en.hasMoreElements()) {
                final ZipArchiveEntry ze = (ZipArchiveEntry)en.nextElement();
                final ZipArchiveEntry zeOut = new ZipArchiveEntry(ze.getName());
                zeOut.setSize(ze.getSize());
                zeOut.setTime(ze.getTime());
                zos.putArchiveEntry((ArchiveEntry)zeOut);
                try (final InputStream is = zipEntrySource.getInputStream(ze)) {
                    if (is instanceof ZipArchiveThresholdInputStream) {
                        ((ZipArchiveThresholdInputStream)is).setGuardState(false);
                    }
                    final XSSFSheet xSheet = this.getSheetFromZipEntryName(ze.getName());
                    if (xSheet != null && !(xSheet instanceof XSSFChartSheet)) {
                        final SXSSFSheet sxSheet = this.getSXSSFSheet(xSheet);
                        try (final InputStream xis = sxSheet.getWorksheetXMLInputStream()) {
                            copyStreamAndInjectWorksheet(is, (OutputStream)zos, xis);
                        }
                    }
                    else {
                        IOUtils.copy(is, (OutputStream)zos);
                    }
                }
                finally {
                    zos.closeArchiveEntry();
                }
            }
        }
        finally {
            zos.finish();
            zipEntrySource.close();
        }
    }
    
    protected ZipArchiveOutputStream createArchiveOutputStream(final OutputStream out) {
        if (Zip64Mode.Always.equals((Object)this.zip64Mode)) {
            return new OpcZipArchiveOutputStream(out);
        }
        final ZipArchiveOutputStream zos = new ZipArchiveOutputStream(out);
        zos.setUseZip64(this.zip64Mode);
        return zos;
    }
    
    private static void copyStreamAndInjectWorksheet(final InputStream in, final OutputStream out, final InputStream worksheetData) throws IOException {
        final InputStreamReader inReader = new InputStreamReader(in, StandardCharsets.UTF_8);
        final OutputStreamWriter outWriter = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        boolean needsStartTag = true;
        int pos = 0;
        String s = "<sheetData";
        int n = s.length();
        int c;
        while ((c = inReader.read()) != -1) {
            if (c == s.charAt(pos)) {
                if (++pos != n) {
                    continue;
                }
                if (!"<sheetData".equals(s)) {
                    break;
                }
                c = inReader.read();
                if (c == -1) {
                    outWriter.write(s);
                    break;
                }
                if (c == 62) {
                    outWriter.write(s);
                    outWriter.write(c);
                    s = "</sheetData>";
                    n = s.length();
                    pos = 0;
                    needsStartTag = false;
                }
                else if (c == 47) {
                    c = inReader.read();
                    if (c == -1) {
                        outWriter.write(s);
                        break;
                    }
                    if (c == 62) {
                        break;
                    }
                    outWriter.write(s);
                    outWriter.write(47);
                    outWriter.write(c);
                    pos = 0;
                }
                else {
                    outWriter.write(s);
                    outWriter.write(47);
                    outWriter.write(c);
                    pos = 0;
                }
            }
            else {
                if (pos > 0) {
                    outWriter.write(s, 0, pos);
                }
                if (c == s.charAt(0)) {
                    pos = 1;
                }
                else {
                    outWriter.write(c);
                    pos = 0;
                }
            }
        }
        outWriter.flush();
        if (needsStartTag) {
            outWriter.write("<sheetData>\n");
            outWriter.flush();
        }
        IOUtils.copy(worksheetData, out);
        outWriter.write("</sheetData>");
        outWriter.flush();
        while ((c = inReader.read()) != -1) {
            outWriter.write(c);
        }
        outWriter.flush();
    }
    
    public XSSFWorkbook getXSSFWorkbook() {
        return this._wb;
    }
    
    public int getActiveSheetIndex() {
        return this._wb.getActiveSheetIndex();
    }
    
    public void setActiveSheet(final int sheetIndex) {
        this._wb.setActiveSheet(sheetIndex);
    }
    
    public int getFirstVisibleTab() {
        return this._wb.getFirstVisibleTab();
    }
    
    public void setFirstVisibleTab(final int sheetIndex) {
        this._wb.setFirstVisibleTab(sheetIndex);
    }
    
    public void setSheetOrder(final String sheetname, final int pos) {
        this._wb.setSheetOrder(sheetname, pos);
    }
    
    public void setSelectedTab(final int index) {
        this._wb.setSelectedTab(index);
    }
    
    public void setSheetName(final int sheet, final String name) {
        this._wb.setSheetName(sheet, name);
    }
    
    public String getSheetName(final int sheet) {
        return this._wb.getSheetName(sheet);
    }
    
    public int getSheetIndex(final String name) {
        return this._wb.getSheetIndex(name);
    }
    
    public int getSheetIndex(final Sheet sheet) {
        return this._wb.getSheetIndex((Sheet)this.getXSSFSheet((SXSSFSheet)sheet));
    }
    
    public SXSSFSheet createSheet() {
        return this.createAndRegisterSXSSFSheet(this._wb.createSheet());
    }
    
    SXSSFSheet createAndRegisterSXSSFSheet(final XSSFSheet xSheet) {
        SXSSFSheet sxSheet;
        try {
            sxSheet = new SXSSFSheet(this, xSheet);
        }
        catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
        this.registerSheetMapping(sxSheet, xSheet);
        return sxSheet;
    }
    
    public SXSSFSheet createSheet(final String sheetname) {
        return this.createAndRegisterSXSSFSheet(this._wb.createSheet(sheetname));
    }
    
    @NotImplemented
    public Sheet cloneSheet(final int sheetNum) {
        throw new RuntimeException("Not Implemented");
    }
    
    public int getNumberOfSheets() {
        return this._wb.getNumberOfSheets();
    }
    
    public Iterator<Sheet> sheetIterator() {
        return new SheetIterator<Sheet>();
    }
    
    public Iterator<Sheet> iterator() {
        return this.sheetIterator();
    }
    
    public SXSSFSheet getSheetAt(final int index) {
        return this.getSXSSFSheet(this._wb.getSheetAt(index));
    }
    
    public SXSSFSheet getSheet(final String name) {
        return this.getSXSSFSheet(this._wb.getSheet(name));
    }
    
    public void removeSheetAt(final int index) {
        final XSSFSheet xSheet = this._wb.getSheetAt(index);
        final SXSSFSheet sxSheet = this.getSXSSFSheet(xSheet);
        this._wb.removeSheetAt(index);
        this.deregisterSheetMapping(xSheet);
        try {
            sxSheet.dispose();
        }
        catch (final IOException e) {
            SXSSFWorkbook.logger.log(5, new Object[] { e });
        }
    }
    
    public Font createFont() {
        return (Font)this._wb.createFont();
    }
    
    public Font findFont(final boolean bold, final short color, final short fontHeight, final String name, final boolean italic, final boolean strikeout, final short typeOffset, final byte underline) {
        return (Font)this._wb.findFont(bold, color, fontHeight, name, italic, strikeout, typeOffset, underline);
    }
    
    @Deprecated
    public short getNumberOfFonts() {
        return (short)this.getNumberOfFontsAsInt();
    }
    
    public int getNumberOfFontsAsInt() {
        return this._wb.getNumberOfFontsAsInt();
    }
    
    @Deprecated
    public Font getFontAt(final short idx) {
        return this.getFontAt((int)idx);
    }
    
    public Font getFontAt(final int idx) {
        return (Font)this._wb.getFontAt(idx);
    }
    
    public CellStyle createCellStyle() {
        return (CellStyle)this._wb.createCellStyle();
    }
    
    public int getNumCellStyles() {
        return this._wb.getNumCellStyles();
    }
    
    public CellStyle getCellStyleAt(final int idx) {
        return (CellStyle)this._wb.getCellStyleAt(idx);
    }
    
    public void close() throws IOException {
        for (final SXSSFSheet sheet : this._xFromSxHash.values()) {
            try {
                sheet.getSheetDataWriter().close();
            }
            catch (final IOException e) {
                SXSSFWorkbook.logger.log(5, new Object[] { "An exception occurred while closing sheet data writer for sheet " + sheet.getSheetName() + ".", e });
            }
        }
        this._wb.close();
    }
    
    public void write(final OutputStream stream) throws IOException {
        this.flushSheets();
        final File tmplFile = TempFile.createTempFile("poi-sxssf-template", ".xlsx");
        boolean deleted = false;
        try {
            try (final FileOutputStream os = new FileOutputStream(tmplFile)) {
                this._wb.write(os);
            }
            try (final ZipSecureFile zf = new ZipSecureFile(tmplFile);
                 final ZipFileZipEntrySource source = new ZipFileZipEntrySource(zf)) {
                this.injectData(source, stream);
            }
        }
        finally {
            deleted = tmplFile.delete();
        }
        if (!deleted) {
            throw new IOException("Could not delete temporary file after processing: " + tmplFile);
        }
    }
    
    protected void flushSheets() throws IOException {
        for (final SXSSFSheet sheet : this._xFromSxHash.values()) {
            sheet.flushRows();
        }
    }
    
    public boolean dispose() {
        boolean success = true;
        for (final SXSSFSheet sheet : this._sxFromXHash.keySet()) {
            try {
                success = (sheet.dispose() && success);
            }
            catch (final IOException e) {
                SXSSFWorkbook.logger.log(5, new Object[] { e });
                success = false;
            }
        }
        return success;
    }
    
    public int getNumberOfNames() {
        return this._wb.getNumberOfNames();
    }
    
    public Name getName(final String name) {
        return (Name)this._wb.getName(name);
    }
    
    public List<? extends Name> getNames(final String name) {
        return (List<? extends Name>)this._wb.getNames(name);
    }
    
    public List<? extends Name> getAllNames() {
        return (List<? extends Name>)this._wb.getAllNames();
    }
    
    public Name createName() {
        return (Name)this._wb.createName();
    }
    
    public void removeName(final Name name) {
        this._wb.removeName(name);
    }
    
    public void setPrintArea(final int sheetIndex, final String reference) {
        this._wb.setPrintArea(sheetIndex, reference);
    }
    
    public void setPrintArea(final int sheetIndex, final int startColumn, final int endColumn, final int startRow, final int endRow) {
        this._wb.setPrintArea(sheetIndex, startColumn, endColumn, startRow, endRow);
    }
    
    public String getPrintArea(final int sheetIndex) {
        return this._wb.getPrintArea(sheetIndex);
    }
    
    public void removePrintArea(final int sheetIndex) {
        this._wb.removePrintArea(sheetIndex);
    }
    
    public Row.MissingCellPolicy getMissingCellPolicy() {
        return this._wb.getMissingCellPolicy();
    }
    
    public void setMissingCellPolicy(final Row.MissingCellPolicy missingCellPolicy) {
        this._wb.setMissingCellPolicy(missingCellPolicy);
    }
    
    public DataFormat createDataFormat() {
        return (DataFormat)this._wb.createDataFormat();
    }
    
    public int addPicture(final byte[] pictureData, final int format) {
        return this._wb.addPicture(pictureData, format);
    }
    
    public List<? extends PictureData> getAllPictures() {
        return (List<? extends PictureData>)this._wb.getAllPictures();
    }
    
    public CreationHelper getCreationHelper() {
        return (CreationHelper)new SXSSFCreationHelper(this);
    }
    
    protected boolean isDate1904() {
        return this._wb.isDate1904();
    }
    
    @NotImplemented("XSSFWorkbook#isHidden is not implemented")
    public boolean isHidden() {
        return this._wb.isHidden();
    }
    
    @NotImplemented("XSSFWorkbook#setHidden is not implemented")
    public void setHidden(final boolean hiddenFlag) {
        this._wb.setHidden(hiddenFlag);
    }
    
    public boolean isSheetHidden(final int sheetIx) {
        return this._wb.isSheetHidden(sheetIx);
    }
    
    public boolean isSheetVeryHidden(final int sheetIx) {
        return this._wb.isSheetVeryHidden(sheetIx);
    }
    
    public SheetVisibility getSheetVisibility(final int sheetIx) {
        return this._wb.getSheetVisibility(sheetIx);
    }
    
    public void setSheetHidden(final int sheetIx, final boolean hidden) {
        this._wb.setSheetHidden(sheetIx, hidden);
    }
    
    public void setSheetVisibility(final int sheetIx, final SheetVisibility visibility) {
        this._wb.setSheetVisibility(sheetIx, visibility);
    }
    
    @Deprecated
    @Removal(version = "3.20")
    public Name getNameAt(final int nameIndex) {
        return (Name)this._wb.getNameAt(nameIndex);
    }
    
    @Deprecated
    @Removal(version = "3.20")
    public int getNameIndex(final String name) {
        return this._wb.getNameIndex(name);
    }
    
    @Deprecated
    @Removal(version = "3.20")
    public void removeName(final int index) {
        this._wb.removeName(index);
    }
    
    @Deprecated
    @Removal(version = "3.20")
    public void removeName(final String name) {
        this._wb.removeName(name);
    }
    
    @NotImplemented
    public int linkExternalWorkbook(final String name, final Workbook workbook) {
        throw new RuntimeException("Not Implemented");
    }
    
    public void addToolPack(final UDFFinder toopack) {
        this._wb.addToolPack(toopack);
    }
    
    public void setForceFormulaRecalculation(final boolean value) {
        this._wb.setForceFormulaRecalculation(value);
    }
    
    public boolean getForceFormulaRecalculation() {
        return this._wb.getForceFormulaRecalculation();
    }
    
    public SpreadsheetVersion getSpreadsheetVersion() {
        return SpreadsheetVersion.EXCEL2007;
    }
    
    public int addOlePackage(final byte[] oleData, final String label, final String fileName, final String command) throws IOException {
        return this._wb.addOlePackage(oleData, label, fileName, command);
    }
    
    static {
        logger = POILogFactory.getLogger((Class)SXSSFWorkbook.class);
    }
    
    private final class SheetIterator<T extends Sheet> implements Iterator<T>
    {
        private final Iterator<XSSFSheet> it;
        
        public SheetIterator() {
            this.it = (Iterator<XSSFSheet>)SXSSFWorkbook.this._wb.iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.it.hasNext();
        }
        
        @Override
        public T next() throws NoSuchElementException {
            final XSSFSheet xssfSheet = this.it.next();
            return (T)SXSSFWorkbook.this.getSXSSFSheet(xssfSheet);
        }
        
        @Override
        public void remove() throws IllegalStateException {
            throw new UnsupportedOperationException("remove method not supported on XSSFWorkbook.iterator(). Use Sheet.removeSheetAt(int) instead.");
        }
    }
}
