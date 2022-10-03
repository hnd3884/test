package org.apache.poi.hssf.usermodel;

import java.util.NoSuchElementException;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.Configurator;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import org.apache.poi.hssf.model.WorkbookRecordList;
import java.security.GeneralSecurityException;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.record.FilePassRecord;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.util.Internal;
import org.apache.poi.hssf.record.RecalcIdRecord;
import org.apache.poi.poifs.filesystem.Ole10Native;
import org.apache.poi.util.HexDump;
import java.io.ByteArrayOutputStream;
import org.apache.poi.hpsf.ClassIDPredefined;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.hssf.record.AbstractEscherHolderRecord;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherBlipRecord;
import org.apache.poi.ddf.EscherBitmapBlip;
import org.apache.poi.ddf.EscherMetafileBlip;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.hssf.model.DrawingManager2;
import org.apache.poi.ddf.EscherRecord;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import org.apache.poi.hssf.record.DrawingGroupRecord;
import org.apache.poi.hssf.record.UnknownRecord;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.SheetNameFormatter;
import org.apache.poi.poifs.crypt.ChunkedCipherOutputStream;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.hssf.record.crypto.Biff8DecryptingStream;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;
import org.apache.poi.poifs.filesystem.EntryUtils;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.FilteringDirectoryNode;
import java.util.Arrays;
import java.io.OutputStream;
import java.io.File;
import java.io.ByteArrayInputStream;
import org.apache.poi.poifs.filesystem.POIFSDocument;
import org.apache.poi.poifs.filesystem.DocumentNode;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.FontRecord;
import java.util.HashMap;
import org.apache.poi.hssf.record.NameCommentRecord;
import org.apache.poi.hssf.record.BackupRecord;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetVisibility;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.Record;
import java.io.InputStream;
import org.apache.poi.hssf.model.InternalSheet;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.RecordFactory;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.EncryptedDocumentException;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.formula.udf.IndexedUDFFinder;
import org.apache.poi.ss.formula.udf.AggregatingUDFFinder;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.usermodel.Row;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.model.InternalWorkbook;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.POIDocument;

public final class HSSFWorkbook extends POIDocument implements Workbook
{
    private static final int MAX_RECORD_LENGTH = 100000;
    private static final Pattern COMMA_PATTERN;
    private static final int MAX_STYLES = 4030;
    private static final int DEBUG = 1;
    public static final int INITIAL_CAPACITY;
    private InternalWorkbook workbook;
    protected List<HSSFSheet> _sheets;
    private ArrayList<HSSFName> names;
    private Map<Integer, HSSFFont> fonts;
    private boolean preserveNodes;
    private HSSFDataFormat formatter;
    private Row.MissingCellPolicy missingCellPolicy;
    private static final POILogger log;
    private UDFFinder _udfFinder;
    
    public static HSSFWorkbook create(final InternalWorkbook book) {
        return new HSSFWorkbook(book);
    }
    
    public HSSFWorkbook() {
        this(InternalWorkbook.createWorkbook());
    }
    
    private HSSFWorkbook(final InternalWorkbook book) {
        super((DirectoryNode)null);
        this.missingCellPolicy = Row.MissingCellPolicy.RETURN_NULL_AND_BLANK;
        this._udfFinder = new IndexedUDFFinder(new UDFFinder[] { AggregatingUDFFinder.DEFAULT });
        this.workbook = book;
        this._sheets = new ArrayList<HSSFSheet>(HSSFWorkbook.INITIAL_CAPACITY);
        this.names = new ArrayList<HSSFName>(HSSFWorkbook.INITIAL_CAPACITY);
    }
    
    public HSSFWorkbook(final POIFSFileSystem fs) throws IOException {
        this(fs, true);
    }
    
    public HSSFWorkbook(final POIFSFileSystem fs, final boolean preserveNodes) throws IOException {
        this(fs.getRoot(), fs, preserveNodes);
    }
    
    public static String getWorkbookDirEntryName(final DirectoryNode directory) {
        for (final String wbName : InternalWorkbook.WORKBOOK_DIR_ENTRY_NAMES) {
            if (directory.hasEntry(wbName)) {
                return wbName;
            }
        }
        if (directory.hasEntry("EncryptedPackage")) {
            throw new EncryptedDocumentException("The supplied spreadsheet seems to be an Encrypted .xlsx file. It must be decrypted before use by XSSF, it cannot be used by HSSF");
        }
        if (directory.hasEntry("Book")) {
            throw new OldExcelFormatException("The supplied spreadsheet seems to be Excel 5.0/7.0 (BIFF5) format. POI only supports BIFF8 format (from Excel versions 97/2000/XP/2003)");
        }
        if (directory.hasEntry("WordDocument")) {
            throw new IllegalArgumentException("The document is really a DOC file");
        }
        throw new IllegalArgumentException("The supplied POIFSFileSystem does not contain a BIFF8 'Workbook' entry. Is it really an excel file? Had: " + directory.getEntryNames());
    }
    
    public HSSFWorkbook(final DirectoryNode directory, final POIFSFileSystem fs, final boolean preserveNodes) throws IOException {
        this(directory, preserveNodes);
    }
    
    public HSSFWorkbook(final DirectoryNode directory, final boolean preserveNodes) throws IOException {
        super(directory);
        this.missingCellPolicy = Row.MissingCellPolicy.RETURN_NULL_AND_BLANK;
        this._udfFinder = new IndexedUDFFinder(new UDFFinder[] { AggregatingUDFFinder.DEFAULT });
        final String workbookName = getWorkbookDirEntryName(directory);
        if (!(this.preserveNodes = preserveNodes)) {
            this.clearDirectory();
        }
        this._sheets = new ArrayList<HSSFSheet>(HSSFWorkbook.INITIAL_CAPACITY);
        this.names = new ArrayList<HSSFName>(HSSFWorkbook.INITIAL_CAPACITY);
        final InputStream stream = directory.createDocumentInputStream(workbookName);
        final List<Record> records = RecordFactory.createRecords(stream);
        this.setPropertiesFromWorkbook(this.workbook = InternalWorkbook.createWorkbook(records));
        final int recOffset = this.workbook.getNumRecords();
        this.convertLabelRecords(records, recOffset);
        final RecordStream rs = new RecordStream(records, recOffset);
        while (rs.hasNext()) {
            try {
                final InternalSheet sheet = InternalSheet.createSheet(rs);
                this._sheets.add(new HSSFSheet(this, sheet));
            }
            catch (final InternalSheet.UnsupportedBOFType eb) {
                HSSFWorkbook.log.log(5, "Unsupported BOF found of type " + eb.getType());
            }
        }
        for (int i = 0; i < this.workbook.getNumNames(); ++i) {
            final NameRecord nameRecord = this.workbook.getNameRecord(i);
            final HSSFName name = new HSSFName(this, nameRecord, this.workbook.getNameCommentRecord(nameRecord));
            this.names.add(name);
        }
    }
    
    public HSSFWorkbook(final InputStream s) throws IOException {
        this(s, true);
    }
    
    public HSSFWorkbook(final InputStream s, final boolean preserveNodes) throws IOException {
        this(new POIFSFileSystem(s).getRoot(), preserveNodes);
    }
    
    private void setPropertiesFromWorkbook(final InternalWorkbook book) {
        this.workbook = book;
    }
    
    private void convertLabelRecords(final List<Record> records, final int offset) {
        if (HSSFWorkbook.log.check(1)) {
            HSSFWorkbook.log.log(1, "convertLabelRecords called");
        }
        for (int k = offset; k < records.size(); ++k) {
            final Record rec = records.get(k);
            if (rec.getSid() == 516) {
                final LabelRecord oldrec = (LabelRecord)rec;
                records.remove(k);
                final LabelSSTRecord newrec = new LabelSSTRecord();
                final int stringid = this.workbook.addSSTString(new UnicodeString(oldrec.getValue()));
                newrec.setRow(oldrec.getRow());
                newrec.setColumn(oldrec.getColumn());
                newrec.setXFIndex(oldrec.getXFIndex());
                newrec.setSSTIndex(stringid);
                records.add(k, newrec);
            }
        }
        if (HSSFWorkbook.log.check(1)) {
            HSSFWorkbook.log.log(1, "convertLabelRecords exit");
        }
    }
    
    @Override
    public Row.MissingCellPolicy getMissingCellPolicy() {
        return this.missingCellPolicy;
    }
    
    @Override
    public void setMissingCellPolicy(final Row.MissingCellPolicy missingCellPolicy) {
        this.missingCellPolicy = missingCellPolicy;
    }
    
    @Override
    public void setSheetOrder(final String sheetname, final int pos) {
        final int oldSheetIndex = this.getSheetIndex(sheetname);
        this._sheets.add(pos, this._sheets.remove(oldSheetIndex));
        this.workbook.setSheetOrder(sheetname, pos);
        final FormulaShifter shifter = FormulaShifter.createForSheetShift(oldSheetIndex, pos);
        for (final HSSFSheet sheet : this._sheets) {
            sheet.getSheet().updateFormulasAfterCellShift(shifter, -1);
        }
        this.workbook.updateNamesAfterCellShift(shifter);
        this.updateNamedRangesAfterSheetReorder(oldSheetIndex, pos);
        this.updateActiveSheetAfterSheetReorder(oldSheetIndex, pos);
    }
    
    private void updateNamedRangesAfterSheetReorder(final int oldIndex, final int newIndex) {
        for (final HSSFName name : this.names) {
            final int i = name.getSheetIndex();
            if (i != -1) {
                if (i == oldIndex) {
                    name.setSheetIndex(newIndex);
                }
                else if (newIndex <= i && i < oldIndex) {
                    name.setSheetIndex(i + 1);
                }
                else {
                    if (oldIndex >= i || i > newIndex) {
                        continue;
                    }
                    name.setSheetIndex(i - 1);
                }
            }
        }
    }
    
    private void updateActiveSheetAfterSheetReorder(final int oldIndex, final int newIndex) {
        final int active = this.getActiveSheetIndex();
        if (active == oldIndex) {
            this.setActiveSheet(newIndex);
        }
        else if (active >= oldIndex || active >= newIndex) {
            if (active <= oldIndex || active <= newIndex) {
                if (newIndex > oldIndex) {
                    this.setActiveSheet(active - 1);
                }
                else {
                    this.setActiveSheet(active + 1);
                }
            }
        }
    }
    
    private void validateSheetIndex(final int index) {
        final int lastSheetIx = this._sheets.size() - 1;
        if (index < 0 || index > lastSheetIx) {
            String range = "(0.." + lastSheetIx + ")";
            if (lastSheetIx == -1) {
                range = "(no sheets)";
            }
            throw new IllegalArgumentException("Sheet index (" + index + ") is out of range " + range);
        }
    }
    
    @Override
    public void setSelectedTab(final int index) {
        this.validateSheetIndex(index);
        for (int nSheets = this._sheets.size(), i = 0; i < nSheets; ++i) {
            this.getSheetAt(i).setSelected(i == index);
        }
        this.workbook.getWindowOne().setNumSelectedTabs((short)1);
    }
    
    public void setSelectedTabs(final int[] indexes) {
        final Collection<Integer> list = new ArrayList<Integer>(indexes.length);
        for (final int index : indexes) {
            list.add(index);
        }
        this.setSelectedTabs(list);
    }
    
    public void setSelectedTabs(final Collection<Integer> indexes) {
        for (final int index : indexes) {
            this.validateSheetIndex(index);
        }
        final Set<Integer> set = new HashSet<Integer>(indexes);
        for (int nSheets = this._sheets.size(), i = 0; i < nSheets; ++i) {
            final boolean bSelect = set.contains(i);
            this.getSheetAt(i).setSelected(bSelect);
        }
        final short nSelected = (short)set.size();
        this.workbook.getWindowOne().setNumSelectedTabs(nSelected);
    }
    
    public Collection<Integer> getSelectedTabs() {
        final Collection<Integer> indexes = new ArrayList<Integer>();
        for (int nSheets = this._sheets.size(), i = 0; i < nSheets; ++i) {
            final HSSFSheet sheet = this.getSheetAt(i);
            if (sheet.isSelected()) {
                indexes.add(i);
            }
        }
        return Collections.unmodifiableCollection((Collection<? extends Integer>)indexes);
    }
    
    @Override
    public void setActiveSheet(final int index) {
        this.validateSheetIndex(index);
        for (int nSheets = this._sheets.size(), i = 0; i < nSheets; ++i) {
            this.getSheetAt(i).setActive(i == index);
        }
        this.workbook.getWindowOne().setActiveSheetIndex(index);
    }
    
    @Override
    public int getActiveSheetIndex() {
        return this.workbook.getWindowOne().getActiveSheetIndex();
    }
    
    @Override
    public void setFirstVisibleTab(final int index) {
        this.workbook.getWindowOne().setFirstVisibleTab(index);
    }
    
    @Override
    public int getFirstVisibleTab() {
        return this.workbook.getWindowOne().getFirstVisibleTab();
    }
    
    @Override
    public void setSheetName(final int sheetIx, final String name) {
        if (name == null) {
            throw new IllegalArgumentException("sheetName must not be null");
        }
        if (this.workbook.doesContainsSheetName(name, sheetIx)) {
            throw new IllegalArgumentException("The workbook already contains a sheet named '" + name + "'");
        }
        this.validateSheetIndex(sheetIx);
        this.workbook.setSheetName(sheetIx, name);
    }
    
    @Override
    public String getSheetName(final int sheetIndex) {
        this.validateSheetIndex(sheetIndex);
        return this.workbook.getSheetName(sheetIndex);
    }
    
    @Override
    public boolean isHidden() {
        return this.workbook.getWindowOne().getHidden();
    }
    
    @Override
    public void setHidden(final boolean hiddenFlag) {
        this.workbook.getWindowOne().setHidden(hiddenFlag);
    }
    
    @Override
    public boolean isSheetHidden(final int sheetIx) {
        this.validateSheetIndex(sheetIx);
        return this.workbook.isSheetHidden(sheetIx);
    }
    
    @Override
    public boolean isSheetVeryHidden(final int sheetIx) {
        this.validateSheetIndex(sheetIx);
        return this.workbook.isSheetVeryHidden(sheetIx);
    }
    
    @Override
    public SheetVisibility getSheetVisibility(final int sheetIx) {
        return this.workbook.getSheetVisibility(sheetIx);
    }
    
    @Override
    public void setSheetHidden(final int sheetIx, final boolean hidden) {
        this.setSheetVisibility(sheetIx, hidden ? SheetVisibility.HIDDEN : SheetVisibility.VISIBLE);
    }
    
    @Override
    public void setSheetVisibility(final int sheetIx, final SheetVisibility visibility) {
        this.validateSheetIndex(sheetIx);
        this.workbook.setSheetHidden(sheetIx, visibility);
    }
    
    @Override
    public int getSheetIndex(final String name) {
        return this.workbook.getSheetIndex(name);
    }
    
    @Override
    public int getSheetIndex(final Sheet sheet) {
        return this._sheets.indexOf(sheet);
    }
    
    @Override
    public HSSFSheet createSheet() {
        final HSSFSheet sheet = new HSSFSheet(this);
        this._sheets.add(sheet);
        this.workbook.setSheetName(this._sheets.size() - 1, "Sheet" + (this._sheets.size() - 1));
        final boolean isOnlySheet = this._sheets.size() == 1;
        sheet.setSelected(isOnlySheet);
        sheet.setActive(isOnlySheet);
        return sheet;
    }
    
    @Override
    public HSSFSheet cloneSheet(final int sheetIndex) {
        this.validateSheetIndex(sheetIndex);
        final HSSFSheet srcSheet = this._sheets.get(sheetIndex);
        final String srcName = this.workbook.getSheetName(sheetIndex);
        final HSSFSheet clonedSheet = srcSheet.cloneSheet(this);
        clonedSheet.setSelected(false);
        clonedSheet.setActive(false);
        final String name = this.getUniqueSheetName(srcName);
        final int newSheetIndex = this._sheets.size();
        this._sheets.add(clonedSheet);
        this.workbook.setSheetName(newSheetIndex, name);
        final int filterDbNameIndex = this.findExistingBuiltinNameRecordIdx(sheetIndex, (byte)13);
        if (filterDbNameIndex != -1) {
            final NameRecord newNameRecord = this.workbook.cloneFilter(filterDbNameIndex, newSheetIndex);
            final HSSFName newName = new HSSFName(this, newNameRecord);
            this.names.add(newName);
        }
        return clonedSheet;
    }
    
    private String getUniqueSheetName(final String srcName) {
        int uniqueIndex = 2;
        String baseName = srcName;
        final int bracketPos = srcName.lastIndexOf(40);
        if (bracketPos > 0 && srcName.endsWith(")")) {
            final String suffix = srcName.substring(bracketPos + 1, srcName.length() - ")".length());
            try {
                uniqueIndex = Integer.parseInt(suffix.trim());
                ++uniqueIndex;
                baseName = srcName.substring(0, bracketPos).trim();
            }
            catch (final NumberFormatException ex) {}
        }
        String name;
        do {
            final String index = Integer.toString(uniqueIndex++);
            if (baseName.length() + index.length() + 2 < 31) {
                name = baseName + " (" + index + ")";
            }
            else {
                name = baseName.substring(0, 31 - index.length() - 2) + "(" + index + ")";
            }
        } while (this.workbook.getSheetIndex(name) != -1);
        return name;
    }
    
    @Override
    public HSSFSheet createSheet(final String sheetname) {
        if (sheetname == null) {
            throw new IllegalArgumentException("sheetName must not be null");
        }
        if (this.workbook.doesContainsSheetName(sheetname, this._sheets.size())) {
            throw new IllegalArgumentException("The workbook already contains a sheet named '" + sheetname + "'");
        }
        final HSSFSheet sheet = new HSSFSheet(this);
        this.workbook.setSheetName(this._sheets.size(), sheetname);
        this._sheets.add(sheet);
        final boolean isOnlySheet = this._sheets.size() == 1;
        sheet.setSelected(isOnlySheet);
        sheet.setActive(isOnlySheet);
        return sheet;
    }
    
    @Override
    public Iterator<Sheet> sheetIterator() {
        return new SheetIterator<Sheet>();
    }
    
    @Override
    public Iterator<Sheet> iterator() {
        return this.sheetIterator();
    }
    
    @Override
    public int getNumberOfSheets() {
        return this._sheets.size();
    }
    
    private HSSFSheet[] getSheets() {
        final HSSFSheet[] result = new HSSFSheet[this._sheets.size()];
        this._sheets.toArray(result);
        return result;
    }
    
    @Override
    public HSSFSheet getSheetAt(final int index) {
        this.validateSheetIndex(index);
        return this._sheets.get(index);
    }
    
    @Override
    public HSSFSheet getSheet(final String name) {
        HSSFSheet retval = null;
        for (int k = 0; k < this._sheets.size(); ++k) {
            final String sheetname = this.workbook.getSheetName(k);
            if (sheetname.equalsIgnoreCase(name)) {
                retval = this._sheets.get(k);
            }
        }
        return retval;
    }
    
    @Override
    public void removeSheetAt(final int index) {
        this.validateSheetIndex(index);
        final boolean wasSelected = this.getSheetAt(index).isSelected();
        this._sheets.remove(index);
        this.workbook.removeSheet(index);
        final int nSheets = this._sheets.size();
        if (nSheets < 1) {
            return;
        }
        int newSheetIndex = index;
        if (newSheetIndex >= nSheets) {
            newSheetIndex = nSheets - 1;
        }
        if (wasSelected) {
            boolean someOtherSheetIsStillSelected = false;
            for (int i = 0; i < nSheets; ++i) {
                if (this.getSheetAt(i).isSelected()) {
                    someOtherSheetIsStillSelected = true;
                    break;
                }
            }
            if (!someOtherSheetIsStillSelected) {
                this.setSelectedTab(newSheetIndex);
            }
        }
        final int active = this.getActiveSheetIndex();
        if (active == index) {
            this.setActiveSheet(newSheetIndex);
        }
        else if (active > index) {
            this.setActiveSheet(active - 1);
        }
    }
    
    public void setBackupFlag(final boolean backupValue) {
        final BackupRecord backupRecord = this.workbook.getBackupRecord();
        backupRecord.setBackup((short)(backupValue ? 1 : 0));
    }
    
    public boolean getBackupFlag() {
        final BackupRecord backupRecord = this.workbook.getBackupRecord();
        return backupRecord.getBackup() != 0;
    }
    
    int findExistingBuiltinNameRecordIdx(final int sheetIndex, final byte builtinCode) {
        for (int defNameIndex = 0; defNameIndex < this.names.size(); ++defNameIndex) {
            final NameRecord r = this.workbook.getNameRecord(defNameIndex);
            if (r == null) {
                throw new RuntimeException("Unable to find all defined names to iterate over");
            }
            if (r.isBuiltInName()) {
                if (r.getBuiltInName() == builtinCode) {
                    if (r.getSheetNumber() - 1 == sheetIndex) {
                        return defNameIndex;
                    }
                }
            }
        }
        return -1;
    }
    
    HSSFName createBuiltInName(final byte builtinCode, final int sheetIndex) {
        final NameRecord nameRecord = this.workbook.createBuiltInName(builtinCode, sheetIndex + 1);
        final HSSFName newName = new HSSFName(this, nameRecord, null);
        this.names.add(newName);
        return newName;
    }
    
    HSSFName getBuiltInName(final byte builtinCode, final int sheetIndex) {
        final int index = this.findExistingBuiltinNameRecordIdx(sheetIndex, builtinCode);
        return (index < 0) ? null : this.names.get(index);
    }
    
    @Override
    public HSSFFont createFont() {
        this.workbook.createNewFont();
        int fontindex = this.getNumberOfFontsAsInt() - 1;
        if (fontindex > 3) {
            ++fontindex;
        }
        if (fontindex >= 32767) {
            throw new IllegalArgumentException("Maximum number of fonts was exceeded");
        }
        return this.getFontAt(fontindex);
    }
    
    @Override
    public HSSFFont findFont(final boolean bold, final short color, final short fontHeight, final String name, final boolean italic, final boolean strikeout, final short typeOffset, final byte underline) {
        for (int numberOfFonts = this.getNumberOfFontsAsInt(), i = 0; i <= numberOfFonts; ++i) {
            if (i != 4) {
                final HSSFFont hssfFont = this.getFontAt(i);
                if (hssfFont.getBold() == bold && hssfFont.getColor() == color && hssfFont.getFontHeight() == fontHeight && hssfFont.getFontName().equals(name) && hssfFont.getItalic() == italic && hssfFont.getStrikeout() == strikeout && hssfFont.getTypeOffset() == typeOffset && hssfFont.getUnderline() == underline) {
                    return hssfFont;
                }
            }
        }
        return null;
    }
    
    @Deprecated
    @Override
    public short getNumberOfFonts() {
        return (short)this.getNumberOfFontsAsInt();
    }
    
    @Override
    public int getNumberOfFontsAsInt() {
        return this.workbook.getNumberOfFontRecords();
    }
    
    @Deprecated
    @Override
    public HSSFFont getFontAt(final short idx) {
        return this.getFontAt((int)idx);
    }
    
    @Override
    public HSSFFont getFontAt(final int idx) {
        if (this.fonts == null) {
            this.fonts = new HashMap<Integer, HSSFFont>();
        }
        final Integer sIdx = idx;
        if (this.fonts.containsKey(sIdx)) {
            return this.fonts.get(sIdx);
        }
        final FontRecord font = this.workbook.getFontRecordAt(idx);
        final HSSFFont retval = new HSSFFont(idx, font);
        this.fonts.put(sIdx, retval);
        return retval;
    }
    
    void resetFontCache() {
        this.fonts = new HashMap<Integer, HSSFFont>();
    }
    
    @Override
    public HSSFCellStyle createCellStyle() {
        if (this.workbook.getNumExFormats() == 4030) {
            throw new IllegalStateException("The maximum number of cell styles was exceeded. You can define up to 4000 styles in a .xls workbook");
        }
        final ExtendedFormatRecord xfr = this.workbook.createCellXF();
        final short index = (short)(this.getNumCellStyles() - 1);
        return new HSSFCellStyle(index, xfr, this);
    }
    
    @Override
    public int getNumCellStyles() {
        return this.workbook.getNumExFormats();
    }
    
    @Override
    public HSSFCellStyle getCellStyleAt(final int idx) {
        final ExtendedFormatRecord xfr = this.workbook.getExFormatAt(idx);
        return new HSSFCellStyle((short)idx, xfr, this);
    }
    
    @Override
    public void close() throws IOException {
        super.close();
    }
    
    @Override
    public void write() throws IOException {
        this.validateInPlaceWritePossible();
        final DirectoryNode dir = this.getDirectory();
        final DocumentNode workbookNode = (DocumentNode)dir.getEntry(getWorkbookDirEntryName(dir));
        final POIFSDocument workbookDoc = new POIFSDocument(workbookNode);
        workbookDoc.replaceContents(new ByteArrayInputStream(this.getBytes()));
        this.writeProperties();
        dir.getFileSystem().writeFilesystem();
    }
    
    @Override
    public void write(final File newFile) throws IOException {
        try (final POIFSFileSystem fs = POIFSFileSystem.create(newFile)) {
            this.write(fs);
            fs.writeFilesystem();
        }
    }
    
    @Override
    public void write(final OutputStream stream) throws IOException {
        try (final POIFSFileSystem fs = new POIFSFileSystem()) {
            this.write(fs);
            fs.writeFilesystem(stream);
        }
    }
    
    private void write(final POIFSFileSystem fs) throws IOException {
        final List<String> excepts = new ArrayList<String>(1);
        fs.createDocument(new ByteArrayInputStream(this.getBytes()), "Workbook");
        this.writeProperties(fs, excepts);
        if (this.preserveNodes) {
            excepts.addAll(Arrays.asList(InternalWorkbook.WORKBOOK_DIR_ENTRY_NAMES));
            excepts.addAll(Arrays.asList("\u0005DocumentSummaryInformation", "\u0005SummaryInformation", this.getEncryptedPropertyStreamName()));
            EntryUtils.copyNodes(new FilteringDirectoryNode(this.getDirectory(), excepts), new FilteringDirectoryNode(fs.getRoot(), excepts));
            fs.getRoot().setStorageClsid(this.getDirectory().getStorageClsid());
        }
    }
    
    public byte[] getBytes() {
        if (HSSFWorkbook.log.check(1)) {
            HSSFWorkbook.log.log(1, "HSSFWorkbook.getBytes()");
        }
        final HSSFSheet[] sheets = this.getSheets();
        final int nSheets = sheets.length;
        this.updateEncryptionInfo();
        this.workbook.preSerialize();
        for (final HSSFSheet sheet : sheets) {
            sheet.getSheet().preSerialize();
            sheet.preSerialize();
        }
        int totalsize = this.workbook.getSize();
        final SheetRecordCollector[] srCollectors = new SheetRecordCollector[nSheets];
        for (int k = 0; k < nSheets; ++k) {
            this.workbook.setSheetBof(k, totalsize);
            final SheetRecordCollector src = new SheetRecordCollector();
            sheets[k].getSheet().visitContainedRecords(src, totalsize);
            totalsize += src.getTotalSize();
            srCollectors[k] = src;
        }
        final byte[] retval = new byte[totalsize];
        int pos = this.workbook.serialize(0, retval);
        for (int i = 0; i < nSheets; ++i) {
            final SheetRecordCollector src2 = srCollectors[i];
            final int serializedSize = src2.serialize(pos, retval);
            if (serializedSize != src2.getTotalSize()) {
                throw new IllegalStateException("Actual serialized sheet size (" + serializedSize + ") differs from pre-calculated size (" + src2.getTotalSize() + ") for sheet (" + i + ")");
            }
            pos += serializedSize;
        }
        this.encryptBytes(retval);
        return retval;
    }
    
    void encryptBytes(final byte[] buf) {
        final EncryptionInfo ei = this.getEncryptionInfo();
        if (ei == null) {
            return;
        }
        final Encryptor enc = ei.getEncryptor();
        final int initialOffset = 0;
        final LittleEndianByteArrayInputStream plain = new LittleEndianByteArrayInputStream(buf, 0);
        final LittleEndianByteArrayOutputStream leos = new LittleEndianByteArrayOutputStream(buf, 0);
        enc.setChunkSize(1024);
        final byte[] tmp = new byte[1024];
        try {
            final ChunkedCipherOutputStream os = enc.getDataStream(leos, initialOffset);
            int len;
            for (int totalBytes = 0; totalBytes < buf.length; totalBytes += 4 + len) {
                IOUtils.readFully(plain, tmp, 0, 4);
                final int sid = LittleEndian.getUShort(tmp, 0);
                len = LittleEndian.getUShort(tmp, 2);
                final boolean isPlain = Biff8DecryptingStream.isNeverEncryptedRecord(sid);
                os.setNextRecordSize(len, isPlain);
                os.writePlain(tmp, 0, 4);
                if (sid == 133) {
                    final byte[] bsrBuf = IOUtils.safelyAllocate(len, 100000);
                    plain.readFully(bsrBuf);
                    os.writePlain(bsrBuf, 0, 4);
                    os.write(bsrBuf, 4, len - 4);
                }
                else {
                    int nextLen;
                    for (int todo = len; todo > 0; todo -= nextLen) {
                        nextLen = Math.min(todo, tmp.length);
                        plain.readFully(tmp, 0, nextLen);
                        if (isPlain) {
                            os.writePlain(tmp, 0, nextLen);
                        }
                        else {
                            os.write(tmp, 0, nextLen);
                        }
                    }
                }
            }
            os.close();
        }
        catch (final Exception e) {
            throw new EncryptedDocumentException(e);
        }
    }
    
    InternalWorkbook getWorkbook() {
        return this.workbook;
    }
    
    @Override
    public int getNumberOfNames() {
        return this.names.size();
    }
    
    @Override
    public HSSFName getName(final String name) {
        final int nameIndex = this.getNameIndex(name);
        if (nameIndex < 0) {
            return null;
        }
        return this.names.get(nameIndex);
    }
    
    @Override
    public List<HSSFName> getNames(final String name) {
        final List<HSSFName> nameList = new ArrayList<HSSFName>();
        for (final HSSFName nr : this.names) {
            if (nr.getNameName().equals(name)) {
                nameList.add(nr);
            }
        }
        return Collections.unmodifiableList((List<? extends HSSFName>)nameList);
    }
    
    @Override
    public HSSFName getNameAt(final int nameIndex) {
        final int nNames = this.names.size();
        if (nNames < 1) {
            throw new IllegalStateException("There are no defined names in this workbook");
        }
        if (nameIndex < 0 || nameIndex > nNames) {
            throw new IllegalArgumentException("Specified name index " + nameIndex + " is outside the allowable range (0.." + (nNames - 1) + ").");
        }
        return this.names.get(nameIndex);
    }
    
    @Override
    public List<HSSFName> getAllNames() {
        return Collections.unmodifiableList((List<? extends HSSFName>)this.names);
    }
    
    public NameRecord getNameRecord(final int nameIndex) {
        return this.getWorkbook().getNameRecord(nameIndex);
    }
    
    public String getNameName(final int index) {
        return this.getNameAt(index).getNameName();
    }
    
    @Override
    public void setPrintArea(final int sheetIndex, final String reference) {
        NameRecord name = this.workbook.getSpecificBuiltinRecord((byte)6, sheetIndex + 1);
        if (name == null) {
            name = this.workbook.createBuiltInName((byte)6, sheetIndex + 1);
        }
        final String[] parts = HSSFWorkbook.COMMA_PATTERN.split(reference);
        final StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < parts.length; ++i) {
            if (i > 0) {
                sb.append(",");
            }
            SheetNameFormatter.appendFormat(sb, this.getSheetName(sheetIndex));
            sb.append("!");
            sb.append(parts[i]);
        }
        name.setNameDefinition(HSSFFormulaParser.parse(sb.toString(), this, FormulaType.NAMEDRANGE, sheetIndex));
    }
    
    @Override
    public void setPrintArea(final int sheetIndex, final int startColumn, final int endColumn, final int startRow, final int endRow) {
        CellReference cell = new CellReference(startRow, startColumn, true, true);
        String reference = cell.formatAsString();
        cell = new CellReference(endRow, endColumn, true, true);
        reference = reference + ":" + cell.formatAsString();
        this.setPrintArea(sheetIndex, reference);
    }
    
    @Override
    public String getPrintArea(final int sheetIndex) {
        final NameRecord name = this.workbook.getSpecificBuiltinRecord((byte)6, sheetIndex + 1);
        if (name == null) {
            return null;
        }
        return HSSFFormulaParser.toFormulaString(this, name.getNameDefinition());
    }
    
    @Override
    public void removePrintArea(final int sheetIndex) {
        this.getWorkbook().removeBuiltinRecord((byte)6, sheetIndex + 1);
    }
    
    @Override
    public HSSFName createName() {
        final NameRecord nameRecord = this.workbook.createName();
        final HSSFName newName = new HSSFName(this, nameRecord);
        this.names.add(newName);
        return newName;
    }
    
    @Override
    public int getNameIndex(final String name) {
        for (int k = 0; k < this.names.size(); ++k) {
            final String nameName = this.getNameName(k);
            if (nameName.equalsIgnoreCase(name)) {
                return k;
            }
        }
        return -1;
    }
    
    int getNameIndex(final HSSFName name) {
        for (int k = 0; k < this.names.size(); ++k) {
            if (name == this.names.get(k)) {
                return k;
            }
        }
        return -1;
    }
    
    @Override
    public void removeName(final int index) {
        this.names.remove(index);
        this.workbook.removeName(index);
    }
    
    @Override
    public HSSFDataFormat createDataFormat() {
        if (this.formatter == null) {
            this.formatter = new HSSFDataFormat(this.workbook);
        }
        return this.formatter;
    }
    
    @Override
    public void removeName(final String name) {
        final int index = this.getNameIndex(name);
        this.removeName(index);
    }
    
    @Override
    public void removeName(final Name name) {
        final int index = this.getNameIndex((HSSFName)name);
        this.removeName(index);
    }
    
    public HSSFPalette getCustomPalette() {
        return new HSSFPalette(this.workbook.getCustomPalette());
    }
    
    public void insertChartRecord() {
        final int loc = this.workbook.findFirstRecordLocBySid((short)252);
        final byte[] data = { 15, 0, 0, -16, 82, 0, 0, 0, 0, 0, 6, -16, 24, 0, 0, 0, 1, 8, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 0, 51, 0, 11, -16, 18, 0, 0, 0, -65, 0, 8, 0, 8, 0, -127, 1, 9, 0, 0, 8, -64, 1, 64, 0, 0, 8, 64, 0, 30, -15, 16, 0, 0, 0, 13, 0, 0, 8, 12, 0, 0, 8, 23, 0, 0, 8, -9, 0, 0, 16 };
        final UnknownRecord r = new UnknownRecord(235, data);
        this.workbook.getRecords().add(loc, r);
    }
    
    public void dumpDrawingGroupRecords(final boolean fat) {
        final DrawingGroupRecord r = (DrawingGroupRecord)this.workbook.findFirstRecordBySid((short)235);
        if (r == null) {
            return;
        }
        r.decode();
        final List<EscherRecord> escherRecords = r.getEscherRecords();
        final PrintWriter w = new PrintWriter(new OutputStreamWriter(System.out, Charset.defaultCharset()));
        for (final EscherRecord escherRecord : escherRecords) {
            if (fat) {
                System.out.println(escherRecord);
            }
            else {
                escherRecord.display(w, 0);
            }
        }
        w.flush();
    }
    
    void initDrawings() {
        final DrawingManager2 mgr = this.workbook.findDrawingGroup();
        if (mgr != null) {
            for (final HSSFSheet sh : this._sheets) {
                sh.getDrawingPatriarch();
            }
        }
        else {
            this.workbook.createDrawingGroup();
        }
    }
    
    @Override
    public int addPicture(byte[] pictureData, final int format) {
        this.initDrawings();
        final byte[] uid = DigestUtils.md5(pictureData);
        EscherBlipRecord blipRecord = null;
        int blipSize = 0;
        short escherTag = 0;
        switch (format) {
            case 3: {
                if (LittleEndian.getInt(pictureData) == -1698247209) {
                    final byte[] picDataNoHeader = new byte[pictureData.length - 22];
                    System.arraycopy(pictureData, 22, picDataNoHeader, 0, pictureData.length - 22);
                    pictureData = picDataNoHeader;
                }
            }
            case 2: {
                final EscherMetafileBlip blipRecordMeta = (EscherMetafileBlip)(blipRecord = new EscherMetafileBlip());
                blipRecordMeta.setUID(uid);
                blipRecordMeta.setPictureData(pictureData);
                blipRecordMeta.setFilter((byte)(-2));
                blipSize = blipRecordMeta.getCompressedSize() + 58;
                escherTag = 0;
                break;
            }
            default: {
                final EscherBitmapBlip blipRecordBitmap = (EscherBitmapBlip)(blipRecord = new EscherBitmapBlip());
                blipRecordBitmap.setUID(uid);
                blipRecordBitmap.setMarker((byte)(-1));
                blipRecordBitmap.setPictureData(pictureData);
                blipSize = pictureData.length + 25;
                escherTag = 255;
                break;
            }
        }
        blipRecord.setRecordId((short)(EscherBlipRecord.RECORD_ID_START + format));
        switch (format) {
            case 2: {
                blipRecord.setOptions((short)15680);
                break;
            }
            case 3: {
                blipRecord.setOptions((short)8544);
                break;
            }
            case 4: {
                blipRecord.setOptions((short)21536);
                break;
            }
            case 6: {
                blipRecord.setOptions((short)28160);
                break;
            }
            case 5: {
                blipRecord.setOptions((short)18080);
                break;
            }
            case 7: {
                blipRecord.setOptions((short)31360);
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected picture format: " + format);
            }
        }
        final EscherBSERecord r = new EscherBSERecord();
        r.setRecordId(EscherBSERecord.RECORD_ID);
        r.setOptions((short)(0x2 | format << 4));
        r.setBlipTypeMacOS((byte)format);
        r.setBlipTypeWin32((byte)format);
        r.setUid(uid);
        r.setTag(escherTag);
        r.setSize(blipSize);
        r.setRef(0);
        r.setOffset(0);
        r.setBlipRecord(blipRecord);
        return this.workbook.addBSERecord(r);
    }
    
    @Override
    public List<HSSFPictureData> getAllPictures() {
        final List<HSSFPictureData> pictures = new ArrayList<HSSFPictureData>();
        for (final Record r : this.workbook.getRecords()) {
            if (r instanceof AbstractEscherHolderRecord) {
                ((AbstractEscherHolderRecord)r).decode();
                final List<EscherRecord> escherRecords = ((AbstractEscherHolderRecord)r).getEscherRecords();
                this.searchForPictures(escherRecords, pictures);
            }
        }
        return Collections.unmodifiableList((List<? extends HSSFPictureData>)pictures);
    }
    
    private void searchForPictures(final List<EscherRecord> escherRecords, final List<HSSFPictureData> pictures) {
        for (final EscherRecord escherRecord : escherRecords) {
            if (escherRecord instanceof EscherBSERecord) {
                final EscherBlipRecord blip = ((EscherBSERecord)escherRecord).getBlipRecord();
                if (blip != null) {
                    final HSSFPictureData picture = new HSSFPictureData(blip);
                    pictures.add(picture);
                }
            }
            this.searchForPictures(escherRecord.getChildRecords(), pictures);
        }
    }
    
    static Map<String, ClassID> getOleMap() {
        final Map<String, ClassID> olemap = new HashMap<String, ClassID>();
        olemap.put("PowerPoint Document", ClassIDPredefined.POWERPOINT_V8.getClassID());
        for (final String str : InternalWorkbook.WORKBOOK_DIR_ENTRY_NAMES) {
            olemap.put(str, ClassIDPredefined.EXCEL_V7_WORKBOOK.getClassID());
        }
        return olemap;
    }
    
    public int addOlePackage(final POIFSFileSystem poiData, final String label, final String fileName, final String command) throws IOException {
        final DirectoryNode root = poiData.getRoot();
        final Map<String, ClassID> olemap = getOleMap();
        for (final Map.Entry<String, ClassID> entry : olemap.entrySet()) {
            if (root.hasEntry(entry.getKey())) {
                root.setStorageClsid(entry.getValue());
                break;
            }
        }
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        poiData.writeFilesystem(bos);
        return this.addOlePackage(bos.toByteArray(), label, fileName, command);
    }
    
    @Override
    public int addOlePackage(final byte[] oleData, final String label, final String fileName, final String command) throws IOException {
        if (this.initDirectory()) {
            this.preserveNodes = true;
        }
        int storageId = 0;
        DirectoryEntry oleDir = null;
        do {
            final String storageStr = "MBD" + HexDump.toHex(++storageId);
            if (!this.getDirectory().hasEntry(storageStr)) {
                oleDir = this.getDirectory().createDirectory(storageStr);
                oleDir.setStorageClsid(ClassIDPredefined.OLE_V1_PACKAGE.getClassID());
            }
        } while (oleDir == null);
        Ole10Native.createOleMarkerEntry(oleDir);
        final Ole10Native oleNative = new Ole10Native(label, fileName, command, oleData);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        oleNative.writeOut(bos);
        oleDir.createDocument("\u0001Ole10Native", new ByteArrayInputStream(bos.toByteArray()));
        return storageId;
    }
    
    @Override
    public int linkExternalWorkbook(final String name, final Workbook workbook) {
        return this.workbook.linkExternalWorkbook(name, workbook);
    }
    
    public boolean isWriteProtected() {
        return this.workbook.isWriteProtected();
    }
    
    public void writeProtectWorkbook(final String password, final String username) {
        this.workbook.writeProtectWorkbook(password, username);
    }
    
    public void unwriteProtectWorkbook() {
        this.workbook.unwriteProtectWorkbook();
    }
    
    public List<HSSFObjectData> getAllEmbeddedObjects() {
        final List<HSSFObjectData> objects = new ArrayList<HSSFObjectData>();
        for (final HSSFSheet sheet : this._sheets) {
            this.getAllEmbeddedObjects(sheet, objects);
        }
        return Collections.unmodifiableList((List<? extends HSSFObjectData>)objects);
    }
    
    private void getAllEmbeddedObjects(final HSSFSheet sheet, final List<HSSFObjectData> objects) {
        final HSSFPatriarch patriarch = sheet.getDrawingPatriarch();
        if (null == patriarch) {
            return;
        }
        this.getAllEmbeddedObjects(patriarch, objects);
    }
    
    private void getAllEmbeddedObjects(final HSSFShapeContainer parent, final List<HSSFObjectData> objects) {
        for (final HSSFShape shape : parent.getChildren()) {
            if (shape instanceof HSSFObjectData) {
                objects.add((HSSFObjectData)shape);
            }
            else {
                if (!(shape instanceof HSSFShapeContainer)) {
                    continue;
                }
                this.getAllEmbeddedObjects((HSSFShapeContainer)shape, objects);
            }
        }
    }
    
    @Override
    public HSSFCreationHelper getCreationHelper() {
        return new HSSFCreationHelper(this);
    }
    
    UDFFinder getUDFFinder() {
        return this._udfFinder;
    }
    
    @Override
    public void addToolPack(final UDFFinder toopack) {
        final AggregatingUDFFinder udfs = (AggregatingUDFFinder)this._udfFinder;
        udfs.add(toopack);
    }
    
    @Override
    public void setForceFormulaRecalculation(final boolean value) {
        final InternalWorkbook iwb = this.getWorkbook();
        final RecalcIdRecord recalc = iwb.getRecalcId();
        recalc.setEngineId(0);
    }
    
    @Override
    public boolean getForceFormulaRecalculation() {
        final InternalWorkbook iwb = this.getWorkbook();
        final RecalcIdRecord recalc = (RecalcIdRecord)iwb.findFirstRecordBySid((short)449);
        return recalc != null && recalc.getEngineId() != 0;
    }
    
    public boolean changeExternalReference(final String oldUrl, final String newUrl) {
        return this.workbook.changeExternalReference(oldUrl, newUrl);
    }
    
    @Internal
    public InternalWorkbook getInternalWorkbook() {
        return this.workbook;
    }
    
    @Override
    public SpreadsheetVersion getSpreadsheetVersion() {
        return SpreadsheetVersion.EXCEL97;
    }
    
    @Override
    public EncryptionInfo getEncryptionInfo() {
        final FilePassRecord fpr = (FilePassRecord)this.workbook.findFirstRecordBySid((short)47);
        return (fpr != null) ? fpr.getEncryptionInfo() : null;
    }
    
    private void updateEncryptionInfo() {
        this.readProperties();
        FilePassRecord fpr = (FilePassRecord)this.workbook.findFirstRecordBySid((short)47);
        final String password = Biff8EncryptionKey.getCurrentUserPassword();
        final WorkbookRecordList wrl = this.workbook.getWorkbookRecordList();
        if (password == null) {
            if (fpr != null) {
                wrl.remove(fpr);
            }
        }
        else {
            if (fpr == null) {
                fpr = new FilePassRecord(EncryptionMode.cryptoAPI);
                wrl.add(1, fpr);
            }
            final EncryptionInfo ei = fpr.getEncryptionInfo();
            final EncryptionVerifier ver = ei.getVerifier();
            final byte[] encVer = ver.getEncryptedVerifier();
            final Decryptor dec = ei.getDecryptor();
            final Encryptor enc = ei.getEncryptor();
            try {
                if (encVer == null || !dec.verifyPassword(password)) {
                    enc.confirmPassword(password);
                }
                else {
                    final byte[] verifier = dec.getVerifier();
                    final byte[] salt = ver.getSalt();
                    enc.confirmPassword(password, null, null, verifier, salt, null);
                }
            }
            catch (final GeneralSecurityException e) {
                throw new EncryptedDocumentException("can't validate/update encryption setting", e);
            }
        }
    }
    
    static {
        COMMA_PATTERN = Pattern.compile(",");
        INITIAL_CAPACITY = Configurator.getIntValue("HSSFWorkbook.SheetInitialCapacity", 3);
        log = POILogFactory.getLogger(HSSFWorkbook.class);
    }
    
    private final class SheetIterator<T extends Sheet> implements Iterator<T>
    {
        private final Iterator<T> it;
        private T cursor;
        
        public SheetIterator() {
            this.it = (Iterator<T>)HSSFWorkbook.this._sheets.iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.it.hasNext();
        }
        
        @Override
        public T next() throws NoSuchElementException {
            return this.cursor = this.it.next();
        }
        
        @Override
        public void remove() throws IllegalStateException {
            throw new UnsupportedOperationException("remove method not supported on HSSFWorkbook.iterator(). Use Sheet.removeSheetAt(int) instead.");
        }
    }
    
    private static final class SheetRecordCollector implements RecordAggregate.RecordVisitor
    {
        private List<Record> _list;
        private int _totalSize;
        
        public SheetRecordCollector() {
            this._totalSize = 0;
            this._list = new ArrayList<Record>(128);
        }
        
        public int getTotalSize() {
            return this._totalSize;
        }
        
        @Override
        public void visitRecord(final Record r) {
            this._list.add(r);
            this._totalSize += r.getRecordSize();
        }
        
        public int serialize(final int offset, final byte[] data) {
            int result = 0;
            for (final Record rec : this._list) {
                result += rec.serialize(offset + result, data);
            }
            return result;
        }
    }
}
