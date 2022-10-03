package org.apache.poi.xssf.usermodel;

import java.util.NoSuchElementException;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.hpsf.ClassIDPredefined;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.poifs.filesystem.Ole10Native;
import org.apache.poi.ss.SpreadsheetVersion;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCaches;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCalcMode;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbookProtection;
import org.apache.poi.xssf.usermodel.helpers.XSSFPasswordHelper;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSheetState;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheets;
import org.apache.poi.xssf.usermodel.helpers.XSSFFormulaUtils;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDefinedNames;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.formula.SheetNameFormatter;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Sheet;
import java.util.Collections;
import java.util.Collection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDialogsheet;
import org.apache.poi.ss.util.WorkbookUtil;
import java.util.Locale;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDefinedName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import java.io.ByteArrayInputStream;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.util.Internal;
import org.apache.poi.openxml4j.opc.PackagePartName;
import java.io.Closeable;
import org.apache.poi.util.IOUtils;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookViews;
import org.apache.poi.ooxml.POIXMLProperties;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbookPr;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalReference;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheet;
import java.util.Iterator;
import java.util.Map;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import java.util.HashMap;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.WorkbookDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.util.ArrayList;
import org.apache.poi.xssf.XLSBUnsupportedException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.File;
import org.apache.poi.ooxml.util.PackageHelper;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.ooxml.POIXMLFactory;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.formula.udf.AggregatingUDFFinder;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCache;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.model.MapInfo;
import org.apache.poi.xssf.model.ExternalLinksTable;
import org.apache.poi.xssf.model.CalculationChain;
import org.apache.poi.ss.formula.udf.IndexedUDFFinder;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.commons.collections4.ListValuedMap;
import java.util.List;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;
import org.apache.poi.util.Removal;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Date1904Support;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ooxml.POIXMLDocument;

public class XSSFWorkbook extends POIXMLDocument implements Workbook, Date1904Support
{
    private static final Pattern COMMA_PATTERN;
    @Deprecated
    @Removal(version = "4.1")
    public static final float DEFAULT_CHARACTER_WIDTH = 7.0017f;
    private static final int MAX_SENSITIVE_SHEET_NAME_LEN = 31;
    public static final int PICTURE_TYPE_GIF = 8;
    public static final int PICTURE_TYPE_TIFF = 9;
    public static final int PICTURE_TYPE_EPS = 10;
    public static final int PICTURE_TYPE_BMP = 11;
    public static final int PICTURE_TYPE_WPG = 12;
    private CTWorkbook workbook;
    private List<XSSFSheet> sheets;
    private ListValuedMap<String, XSSFName> namedRangesByName;
    private List<XSSFName> namedRanges;
    private SharedStringsTable sharedStringSource;
    private StylesTable stylesSource;
    private IndexedUDFFinder _udfFinder;
    private CalculationChain calcChain;
    private List<ExternalLinksTable> externalLinks;
    private MapInfo mapInfo;
    private XSSFDataFormat formatter;
    private Row.MissingCellPolicy _missingCellPolicy;
    private boolean cellFormulaValidation;
    private List<XSSFPictureData> pictures;
    private static POILogger logger;
    private XSSFCreationHelper _creationHelper;
    private List<XSSFPivotTable> pivotTables;
    private List<CTPivotCache> pivotCaches;
    private final XSSFFactory xssfFactory;
    
    public XSSFWorkbook() {
        this(XSSFWorkbookType.XLSX);
    }
    
    public XSSFWorkbook(final XSSFFactory factory) {
        this(XSSFWorkbookType.XLSX, factory);
    }
    
    public XSSFWorkbook(final XSSFWorkbookType workbookType) {
        this(workbookType, null);
    }
    
    private XSSFWorkbook(final XSSFWorkbookType workbookType, final XSSFFactory factory) {
        super(newPackage(workbookType));
        this._udfFinder = new IndexedUDFFinder(new UDFFinder[] { AggregatingUDFFinder.DEFAULT });
        this._missingCellPolicy = Row.MissingCellPolicy.RETURN_NULL_AND_BLANK;
        this.cellFormulaValidation = true;
        this.xssfFactory = ((factory == null) ? XSSFFactory.getInstance() : factory);
        this.onWorkbookCreate();
    }
    
    public XSSFWorkbook(final OPCPackage pkg) throws IOException {
        super(pkg);
        this._udfFinder = new IndexedUDFFinder(new UDFFinder[] { AggregatingUDFFinder.DEFAULT });
        this._missingCellPolicy = Row.MissingCellPolicy.RETURN_NULL_AND_BLANK;
        this.cellFormulaValidation = true;
        this.xssfFactory = XSSFFactory.getInstance();
        this.beforeDocumentRead();
        this.load(this.xssfFactory);
        this.setBookViewsIfMissing();
    }
    
    public XSSFWorkbook(final InputStream is) throws IOException {
        this(PackageHelper.open(is));
    }
    
    public XSSFWorkbook(final File file) throws IOException, InvalidFormatException {
        this(OPCPackage.open(file));
    }
    
    public XSSFWorkbook(final String path) throws IOException {
        this(POIXMLDocument.openPackage(path));
    }
    
    public XSSFWorkbook(final PackagePart part) throws IOException {
        this(part.getInputStream());
    }
    
    protected void beforeDocumentRead() {
        if (this.getCorePart().getContentType().equals(XSSFRelation.XLSB_BINARY_WORKBOOK.getContentType())) {
            throw new XLSBUnsupportedException();
        }
        this.pivotTables = new ArrayList<XSSFPivotTable>();
        this.pivotCaches = new ArrayList<CTPivotCache>();
    }
    
    protected void onDocumentRead() throws IOException {
        try {
            final WorkbookDocument doc = WorkbookDocument.Factory.parse(this.getPackagePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.workbook = doc.getWorkbook();
            ThemesTable theme = null;
            final Map<String, XSSFSheet> shIdMap = new HashMap<String, XSSFSheet>();
            final Map<String, ExternalLinksTable> elIdMap = new HashMap<String, ExternalLinksTable>();
            for (final RelationPart rp : this.getRelationParts()) {
                final POIXMLDocumentPart p = rp.getDocumentPart();
                if (p instanceof SharedStringsTable) {
                    this.sharedStringSource = (SharedStringsTable)p;
                }
                else if (p instanceof StylesTable) {
                    this.stylesSource = (StylesTable)p;
                }
                else if (p instanceof ThemesTable) {
                    theme = (ThemesTable)p;
                }
                else if (p instanceof CalculationChain) {
                    this.calcChain = (CalculationChain)p;
                }
                else if (p instanceof MapInfo) {
                    this.mapInfo = (MapInfo)p;
                }
                else if (p instanceof XSSFSheet) {
                    shIdMap.put(rp.getRelationship().getId(), (XSSFSheet)p);
                }
                else {
                    if (!(p instanceof ExternalLinksTable)) {
                        continue;
                    }
                    elIdMap.put(rp.getRelationship().getId(), (ExternalLinksTable)p);
                }
            }
            final boolean packageReadOnly = this.getPackage().getPackageAccess() == PackageAccess.READ;
            if (this.stylesSource == null) {
                if (packageReadOnly) {
                    this.stylesSource = new StylesTable();
                }
                else {
                    this.stylesSource = (StylesTable)this.createRelationship(XSSFRelation.STYLES, this.xssfFactory);
                }
            }
            this.stylesSource.setWorkbook(this);
            this.stylesSource.setTheme(theme);
            if (this.sharedStringSource == null) {
                if (packageReadOnly) {
                    this.sharedStringSource = new SharedStringsTable();
                }
                else {
                    this.sharedStringSource = (SharedStringsTable)this.createRelationship(XSSFRelation.SHARED_STRINGS, this.xssfFactory);
                }
            }
            this.sheets = new ArrayList<XSSFSheet>(shIdMap.size());
            for (final CTSheet ctSheet : this.workbook.getSheets().getSheetArray()) {
                this.parseSheet(shIdMap, ctSheet);
            }
            this.externalLinks = new ArrayList<ExternalLinksTable>(elIdMap.size());
            if (this.workbook.isSetExternalReferences()) {
                for (final CTExternalReference er : this.workbook.getExternalReferences().getExternalReferenceArray()) {
                    final ExternalLinksTable el = elIdMap.get(er.getId());
                    if (el == null) {
                        XSSFWorkbook.logger.log(5, new Object[] { "ExternalLinksTable with r:id " + er.getId() + " was defined, but didn't exist in package, skipping" });
                    }
                    else {
                        this.externalLinks.add(el);
                    }
                }
            }
            this.reprocessNamedRanges();
        }
        catch (final XmlException e) {
            throw new POIXMLException((Throwable)e);
        }
    }
    
    public void parseSheet(final Map<String, XSSFSheet> shIdMap, final CTSheet ctSheet) {
        final XSSFSheet sh = shIdMap.get(ctSheet.getId());
        if (sh == null) {
            XSSFWorkbook.logger.log(5, new Object[] { "Sheet with name " + ctSheet.getName() + " and r:id " + ctSheet.getId() + " was defined, but didn't exist in package, skipping" });
            return;
        }
        sh.sheet = ctSheet;
        sh.onDocumentRead();
        this.sheets.add(sh);
    }
    
    private void onWorkbookCreate() {
        this.workbook = CTWorkbook.Factory.newInstance();
        final CTWorkbookPr workbookPr = this.workbook.addNewWorkbookPr();
        workbookPr.setDate1904(false);
        this.setBookViewsIfMissing();
        this.workbook.addNewSheets();
        final POIXMLProperties.ExtendedProperties expProps = this.getProperties().getExtendedProperties();
        expProps.getUnderlyingProperties().setApplication("Apache POI");
        this.sharedStringSource = (SharedStringsTable)this.createRelationship(XSSFRelation.SHARED_STRINGS, this.xssfFactory);
        (this.stylesSource = (StylesTable)this.createRelationship(XSSFRelation.STYLES, this.xssfFactory)).setWorkbook(this);
        this.namedRanges = new ArrayList<XSSFName>();
        this.namedRangesByName = (ListValuedMap<String, XSSFName>)new ArrayListValuedHashMap();
        this.sheets = new ArrayList<XSSFSheet>();
        this.pivotTables = new ArrayList<XSSFPivotTable>();
    }
    
    private void setBookViewsIfMissing() {
        if (!this.workbook.isSetBookViews()) {
            final CTBookViews bvs = this.workbook.addNewBookViews();
            final CTBookView bv = bvs.addNewWorkbookView();
            bv.setActiveTab(0L);
        }
    }
    
    protected static OPCPackage newPackage(final XSSFWorkbookType workbookType) {
        OPCPackage pkg = null;
        try {
            pkg = OPCPackage.create(new ByteArrayOutputStream());
            final PackagePartName corePartName = PackagingURIHelper.createPartName(XSSFRelation.WORKBOOK.getDefaultFileName());
            pkg.addRelationship(corePartName, TargetMode.INTERNAL, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument");
            pkg.createPart(corePartName, workbookType.getContentType());
            pkg.getPackageProperties().setCreatorProperty("Apache POI");
        }
        catch (final Exception e) {
            IOUtils.closeQuietly((Closeable)pkg);
            throw new POIXMLException(e);
        }
        return pkg;
    }
    
    @Internal
    public CTWorkbook getCTWorkbook() {
        return this.workbook;
    }
    
    public int addPicture(final byte[] pictureData, final int format) {
        final int imageNumber = this.getAllPictures().size() + 1;
        final XSSFPictureData img = this.createRelationship(XSSFPictureData.RELATIONS[format], this.xssfFactory, imageNumber, true).getDocumentPart();
        try (final OutputStream out = img.getPackagePart().getOutputStream()) {
            out.write(pictureData);
        }
        catch (final IOException e) {
            throw new POIXMLException(e);
        }
        this.pictures.add(img);
        return imageNumber - 1;
    }
    
    public int addPicture(final InputStream is, final int format) throws IOException {
        final int imageNumber = this.getAllPictures().size() + 1;
        final XSSFPictureData img = this.createRelationship(XSSFPictureData.RELATIONS[format], this.xssfFactory, imageNumber, true).getDocumentPart();
        try (final OutputStream out = img.getPackagePart().getOutputStream()) {
            IOUtils.copy(is, out);
        }
        this.pictures.add(img);
        return imageNumber - 1;
    }
    
    public XSSFSheet cloneSheet(final int sheetNum) {
        return this.cloneSheet(sheetNum, null);
    }
    
    @Override
    public void close() throws IOException {
        try {
            super.close();
        }
        finally {
            IOUtils.closeQuietly((Closeable)this.sharedStringSource);
        }
    }
    
    public XSSFSheet cloneSheet(final int sheetNum, String newName) {
        this.validateSheetIndex(sheetNum);
        final XSSFSheet srcSheet = this.sheets.get(sheetNum);
        if (newName == null) {
            final String srcName = srcSheet.getSheetName();
            newName = this.getUniqueSheetName(srcName);
        }
        else {
            this.validateSheetName(newName);
        }
        final XSSFSheet clonedSheet = this.createSheet(newName);
        final List<RelationPart> rels = srcSheet.getRelationParts();
        XSSFDrawing dg = null;
        for (final RelationPart rp : rels) {
            final POIXMLDocumentPart r = rp.getDocumentPart();
            if (r instanceof XSSFDrawing) {
                dg = (XSSFDrawing)r;
            }
            else {
                addRelation(rp, clonedSheet);
            }
        }
        try {
            for (final PackageRelationship pr : srcSheet.getPackagePart().getRelationships()) {
                if (pr.getTargetMode() == TargetMode.EXTERNAL) {
                    clonedSheet.getPackagePart().addExternalRelationship(pr.getTargetURI().toASCIIString(), pr.getRelationshipType(), pr.getId());
                }
            }
        }
        catch (final InvalidFormatException e) {
            throw new POIXMLException("Failed to clone sheet", e);
        }
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            srcSheet.write(out);
            try (final ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray())) {
                clonedSheet.read(bis);
            }
        }
        catch (final IOException e2) {
            throw new POIXMLException("Failed to clone sheet", e2);
        }
        final CTWorksheet ct = clonedSheet.getCTWorksheet();
        if (ct.isSetLegacyDrawing()) {
            XSSFWorkbook.logger.log(5, new Object[] { "Cloning sheets with comments is not yet supported." });
            ct.unsetLegacyDrawing();
        }
        if (ct.isSetPageSetup()) {
            XSSFWorkbook.logger.log(5, new Object[] { "Cloning sheets with page setup is not yet supported." });
            ct.unsetPageSetup();
        }
        clonedSheet.setSelected(false);
        if (dg != null) {
            if (ct.isSetDrawing()) {
                ct.unsetDrawing();
            }
            final XSSFDrawing clonedDg = clonedSheet.createDrawingPatriarch();
            clonedDg.getCTDrawing().set(dg.getCTDrawing().copy());
            final List<RelationPart> srcRels = srcSheet.createDrawingPatriarch().getRelationParts();
            for (final RelationPart rp2 : srcRels) {
                addRelation(rp2, clonedDg);
            }
        }
        return clonedSheet;
    }
    
    private static void addRelation(final RelationPart rp, final POIXMLDocumentPart target) {
        final PackageRelationship rel = rp.getRelationship();
        if (rel.getTargetMode() == TargetMode.EXTERNAL) {
            target.getPackagePart().addRelationship(rel.getTargetURI(), rel.getTargetMode(), rel.getRelationshipType(), rel.getId());
        }
        else {
            final XSSFRelation xssfRel = XSSFRelation.getInstance(rel.getRelationshipType());
            if (xssfRel == null) {
                throw new POIXMLException("Can't clone sheet - unknown relation type found: " + rel.getRelationshipType());
            }
            target.addRelation(rel.getId(), xssfRel, rp.getDocumentPart());
        }
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
        } while (this.getSheetIndex(name) != -1);
        return name;
    }
    
    public XSSFCellStyle createCellStyle() {
        return this.stylesSource.createCellStyle();
    }
    
    public XSSFDataFormat createDataFormat() {
        if (this.formatter == null) {
            this.formatter = new XSSFDataFormat(this.stylesSource);
        }
        return this.formatter;
    }
    
    public XSSFFont createFont() {
        final XSSFFont font = new XSSFFont();
        font.registerTo(this.stylesSource);
        return font;
    }
    
    public XSSFName createName() {
        final CTDefinedName ctName = CTDefinedName.Factory.newInstance();
        ctName.setName("");
        return this.createAndStoreName(ctName);
    }
    
    private XSSFName createAndStoreName(final CTDefinedName ctName) {
        final XSSFName name = new XSSFName(ctName, this);
        this.namedRanges.add(name);
        this.namedRangesByName.put((Object)ctName.getName().toLowerCase(Locale.ENGLISH), (Object)name);
        return name;
    }
    
    public XSSFSheet createSheet() {
        String sheetname = "Sheet" + this.sheets.size();
        for (int idx = 0; this.getSheet(sheetname) != null; sheetname = "Sheet" + idx, ++idx) {}
        return this.createSheet(sheetname);
    }
    
    public XSSFSheet createSheet(String sheetname) {
        if (sheetname == null) {
            throw new IllegalArgumentException("sheetName must not be null");
        }
        this.validateSheetName(sheetname);
        if (sheetname.length() > 31) {
            sheetname = sheetname.substring(0, 31);
        }
        WorkbookUtil.validateSheetName(sheetname);
        final CTSheet sheet = this.addSheet(sheetname);
        int sheetNumber = 1;
    Label_0048:
        while (true) {
            for (final XSSFSheet sh : this.sheets) {
                sheetNumber = (int)Math.max(sh.sheet.getSheetId() + 1L, sheetNumber);
            }
            final String sheetName = XSSFRelation.WORKSHEET.getFileName(sheetNumber);
            for (final POIXMLDocumentPart relation : this.getRelations()) {
                if (relation.getPackagePart() != null && sheetName.equals(relation.getPackagePart().getPartName().getName())) {
                    ++sheetNumber;
                    continue Label_0048;
                }
            }
            break;
        }
        final RelationPart rp = this.createRelationship(XSSFRelation.WORKSHEET, this.xssfFactory, sheetNumber, false);
        final XSSFSheet wrapper = rp.getDocumentPart();
        (wrapper.sheet = sheet).setId(rp.getRelationship().getId());
        sheet.setSheetId((long)sheetNumber);
        if (this.sheets.isEmpty()) {
            wrapper.setSelected(true);
        }
        this.sheets.add(wrapper);
        return wrapper;
    }
    
    private void validateSheetName(final String sheetName) throws IllegalArgumentException {
        if (this.containsSheet(sheetName, this.sheets.size())) {
            throw new IllegalArgumentException("The workbook already contains a sheet named '" + sheetName + "'");
        }
    }
    
    protected XSSFDialogsheet createDialogsheet(final String sheetname, final CTDialogsheet dialogsheet) {
        final XSSFSheet sheet = this.createSheet(sheetname);
        return new XSSFDialogsheet(sheet);
    }
    
    private CTSheet addSheet(final String sheetname) {
        final CTSheet sheet = this.workbook.getSheets().addNewSheet();
        sheet.setName(sheetname);
        return sheet;
    }
    
    public XSSFFont findFont(final boolean bold, final short color, final short fontHeight, final String name, final boolean italic, final boolean strikeout, final short typeOffset, final byte underline) {
        return this.stylesSource.findFont(bold, color, fontHeight, name, italic, strikeout, typeOffset, underline);
    }
    
    public int getActiveSheetIndex() {
        return (int)this.workbook.getBookViews().getWorkbookViewArray(0).getActiveTab();
    }
    
    public List<XSSFPictureData> getAllPictures() {
        if (this.pictures == null) {
            final List<PackagePart> mediaParts = this.getPackage().getPartsByName(Pattern.compile("/xl/media/.*?"));
            this.pictures = new ArrayList<XSSFPictureData>(mediaParts.size());
            for (final PackagePart part : mediaParts) {
                this.pictures.add(new XSSFPictureData(part));
            }
        }
        return this.pictures;
    }
    
    public XSSFCellStyle getCellStyleAt(final int idx) {
        return this.stylesSource.getStyleAt(idx);
    }
    
    public XSSFFont getFontAt(final short idx) {
        return this.stylesSource.getFontAt(idx);
    }
    
    public XSSFFont getFontAt(final int idx) {
        return this.stylesSource.getFontAt(idx);
    }
    
    public XSSFName getName(final String name) {
        final Collection<XSSFName> list = this.getNames(name);
        if (list.isEmpty()) {
            return null;
        }
        return list.iterator().next();
    }
    
    public List<XSSFName> getNames(final String name) {
        return Collections.unmodifiableList((List<? extends XSSFName>)this.namedRangesByName.get((Object)name.toLowerCase(Locale.ENGLISH)));
    }
    
    @Deprecated
    public XSSFName getNameAt(final int nameIndex) {
        final int nNames = this.namedRanges.size();
        if (nNames < 1) {
            throw new IllegalStateException("There are no defined names in this workbook");
        }
        if (nameIndex < 0 || nameIndex > nNames) {
            throw new IllegalArgumentException("Specified name index " + nameIndex + " is outside the allowable range (0.." + (nNames - 1) + ").");
        }
        return this.namedRanges.get(nameIndex);
    }
    
    public List<XSSFName> getAllNames() {
        return Collections.unmodifiableList((List<? extends XSSFName>)this.namedRanges);
    }
    
    @Deprecated
    public int getNameIndex(final String name) {
        final XSSFName nm = this.getName(name);
        if (nm != null) {
            return this.namedRanges.indexOf(nm);
        }
        return -1;
    }
    
    public int getNumCellStyles() {
        return this.stylesSource.getNumCellStyles();
    }
    
    public short getNumberOfFonts() {
        return (short)this.getNumberOfFontsAsInt();
    }
    
    public int getNumberOfFontsAsInt() {
        return (short)this.stylesSource.getFonts().size();
    }
    
    public int getNumberOfNames() {
        return this.namedRanges.size();
    }
    
    public int getNumberOfSheets() {
        return this.sheets.size();
    }
    
    public String getPrintArea(final int sheetIndex) {
        final XSSFName name = this.getBuiltInName("_xlnm.Print_Area", sheetIndex);
        if (name == null) {
            return null;
        }
        return name.getRefersToFormula();
    }
    
    public XSSFSheet getSheet(final String name) {
        for (final XSSFSheet sheet : this.sheets) {
            if (name.equalsIgnoreCase(sheet.getSheetName())) {
                return sheet;
            }
        }
        return null;
    }
    
    public XSSFSheet getSheetAt(final int index) {
        this.validateSheetIndex(index);
        return this.sheets.get(index);
    }
    
    public int getSheetIndex(final String name) {
        int idx = 0;
        for (final XSSFSheet sh : this.sheets) {
            if (name.equalsIgnoreCase(sh.getSheetName())) {
                return idx;
            }
            ++idx;
        }
        return -1;
    }
    
    public int getSheetIndex(final Sheet sheet) {
        int idx = 0;
        for (final XSSFSheet sh : this.sheets) {
            if (sh == sheet) {
                return idx;
            }
            ++idx;
        }
        return -1;
    }
    
    public String getSheetName(final int sheetIx) {
        this.validateSheetIndex(sheetIx);
        return this.sheets.get(sheetIx).getSheetName();
    }
    
    public Iterator<Sheet> sheetIterator() {
        return new SheetIterator<Sheet>();
    }
    
    public Iterator<Sheet> iterator() {
        return this.sheetIterator();
    }
    
    public boolean isMacroEnabled() {
        return this.getPackagePart().getContentType().equals(XSSFRelation.MACROS_WORKBOOK.getContentType());
    }
    
    @Deprecated
    public void removeName(final int nameIndex) {
        this.removeName((Name)this.getNameAt(nameIndex));
    }
    
    @Deprecated
    public void removeName(final String name) {
        final List<XSSFName> names = this.namedRangesByName.get((Object)name.toLowerCase(Locale.ENGLISH));
        if (names.isEmpty()) {
            throw new IllegalArgumentException("Named range was not found: " + name);
        }
        this.removeName((Name)names.get(0));
    }
    
    public void removeName(final Name name) {
        if (!this.namedRangesByName.removeMapping((Object)name.getNameName().toLowerCase(Locale.ENGLISH), (Object)name) || !this.namedRanges.remove(name)) {
            throw new IllegalArgumentException("Name was not found: " + name);
        }
    }
    
    void updateName(final XSSFName name, final String oldName) {
        if (!this.namedRangesByName.removeMapping((Object)oldName.toLowerCase(Locale.ENGLISH), (Object)name)) {
            throw new IllegalArgumentException("Name was not found: " + name);
        }
        this.namedRangesByName.put((Object)name.getNameName().toLowerCase(Locale.ENGLISH), (Object)name);
    }
    
    public void removePrintArea(final int sheetIndex) {
        final XSSFName name = this.getBuiltInName("_xlnm.Print_Area", sheetIndex);
        if (name != null) {
            this.removeName((Name)name);
        }
    }
    
    public void removeSheetAt(final int index) {
        this.validateSheetIndex(index);
        this.onSheetDelete(index);
        final XSSFSheet sheet = this.getSheetAt(index);
        this.removeRelation(sheet);
        this.sheets.remove(index);
        if (this.sheets.size() == 0) {
            return;
        }
        int newSheetIndex = index;
        if (newSheetIndex >= this.sheets.size()) {
            newSheetIndex = this.sheets.size() - 1;
        }
        final int active = this.getActiveSheetIndex();
        if (active == index) {
            this.setActiveSheet(newSheetIndex);
        }
        else if (active > index) {
            this.setActiveSheet(active - 1);
        }
    }
    
    private void onSheetDelete(final int index) {
        final XSSFSheet sheet = this.getSheetAt(index);
        sheet.onSheetDelete();
        this.workbook.getSheets().removeSheet(index);
        if (this.calcChain != null) {
            this.removeRelation(this.calcChain);
            this.calcChain = null;
        }
        final List<XSSFName> toRemove = new ArrayList<XSSFName>();
        for (final XSSFName nm : this.namedRanges) {
            final CTDefinedName ct = nm.getCTName();
            if (!ct.isSetLocalSheetId()) {
                continue;
            }
            if (ct.getLocalSheetId() == index) {
                toRemove.add(nm);
            }
            else {
                if (ct.getLocalSheetId() <= index) {
                    continue;
                }
                ct.setLocalSheetId(ct.getLocalSheetId() - 1L);
            }
        }
        for (final XSSFName nm : toRemove) {
            this.removeName((Name)nm);
        }
    }
    
    public Row.MissingCellPolicy getMissingCellPolicy() {
        return this._missingCellPolicy;
    }
    
    public void setMissingCellPolicy(final Row.MissingCellPolicy missingCellPolicy) {
        this._missingCellPolicy = missingCellPolicy;
    }
    
    public void setActiveSheet(final int index) {
        this.validateSheetIndex(index);
        for (final CTBookView arrayBook : this.workbook.getBookViews().getWorkbookViewArray()) {
            arrayBook.setActiveTab((long)index);
        }
    }
    
    private void validateSheetIndex(final int index) {
        final int lastSheetIx = this.sheets.size() - 1;
        if (index < 0 || index > lastSheetIx) {
            String range = "(0.." + lastSheetIx + ")";
            if (lastSheetIx == -1) {
                range = "(no sheets)";
            }
            throw new IllegalArgumentException("Sheet index (" + index + ") is out of range " + range);
        }
    }
    
    public int getFirstVisibleTab() {
        final CTBookViews bookViews = this.workbook.getBookViews();
        final CTBookView bookView = bookViews.getWorkbookViewArray(0);
        return (short)bookView.getFirstSheet();
    }
    
    public void setFirstVisibleTab(final int index) {
        final CTBookViews bookViews = this.workbook.getBookViews();
        final CTBookView bookView = bookViews.getWorkbookViewArray(0);
        bookView.setFirstSheet((long)index);
    }
    
    public void setPrintArea(final int sheetIndex, final String reference) {
        XSSFName name = this.getBuiltInName("_xlnm.Print_Area", sheetIndex);
        if (name == null) {
            name = this.createBuiltInName("_xlnm.Print_Area", sheetIndex);
        }
        final String[] parts = XSSFWorkbook.COMMA_PATTERN.split(reference);
        final StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < parts.length; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            SheetNameFormatter.appendFormat(sb, this.getSheetName(sheetIndex));
            sb.append('!');
            sb.append(parts[i]);
        }
        name.setRefersToFormula(sb.toString());
    }
    
    public void setPrintArea(final int sheetIndex, final int startColumn, final int endColumn, final int startRow, final int endRow) {
        final String reference = getReferencePrintArea(this.getSheetName(sheetIndex), startColumn, endColumn, startRow, endRow);
        this.setPrintArea(sheetIndex, reference);
    }
    
    private static String getReferencePrintArea(final String sheetName, final int startC, final int endC, final int startR, final int endR) {
        final CellReference colRef = new CellReference(sheetName, startR, startC, true, true);
        final CellReference colRef2 = new CellReference(sheetName, endR, endC, true, true);
        return "$" + colRef.getCellRefParts()[2] + "$" + colRef.getCellRefParts()[1] + ":$" + colRef2.getCellRefParts()[2] + "$" + colRef2.getCellRefParts()[1];
    }
    
    XSSFName getBuiltInName(final String builtInCode, final int sheetNumber) {
        for (final XSSFName name : this.namedRangesByName.get((Object)builtInCode.toLowerCase(Locale.ENGLISH))) {
            if (name.getSheetIndex() == sheetNumber) {
                return name;
            }
        }
        return null;
    }
    
    XSSFName createBuiltInName(final String builtInName, final int sheetNumber) {
        this.validateSheetIndex(sheetNumber);
        final CTDefinedNames names = (this.workbook.getDefinedNames() == null) ? this.workbook.addNewDefinedNames() : this.workbook.getDefinedNames();
        final CTDefinedName nameRecord = names.addNewDefinedName();
        nameRecord.setName(builtInName);
        nameRecord.setLocalSheetId((long)sheetNumber);
        if (this.getBuiltInName(builtInName, sheetNumber) != null) {
            throw new POIXMLException("Builtin (" + builtInName + ") already exists for sheet (" + sheetNumber + ")");
        }
        return this.createAndStoreName(nameRecord);
    }
    
    public void setSelectedTab(final int index) {
        int idx = 0;
        for (final XSSFSheet sh : this.sheets) {
            sh.setSelected(idx == index);
            ++idx;
        }
    }
    
    public void setSheetName(final int sheetIndex, String sheetname) {
        if (sheetname == null) {
            throw new IllegalArgumentException("sheetName must not be null");
        }
        this.validateSheetIndex(sheetIndex);
        final String oldSheetName = this.getSheetName(sheetIndex);
        if (sheetname.length() > 31) {
            sheetname = sheetname.substring(0, 31);
        }
        WorkbookUtil.validateSheetName(sheetname);
        if (sheetname.equals(oldSheetName)) {
            return;
        }
        if (this.containsSheet(sheetname, sheetIndex)) {
            throw new IllegalArgumentException("The workbook already contains a sheet of this name");
        }
        final XSSFFormulaUtils utils = new XSSFFormulaUtils(this);
        utils.updateSheetName(sheetIndex, oldSheetName, sheetname);
        this.workbook.getSheets().getSheetArray(sheetIndex).setName(sheetname);
    }
    
    public void setSheetOrder(final String sheetname, final int pos) {
        final int idx = this.getSheetIndex(sheetname);
        this.sheets.add(pos, this.sheets.remove(idx));
        final CTSheets ct = this.workbook.getSheets();
        final XmlObject cts = ct.getSheetArray(idx).copy();
        this.workbook.getSheets().removeSheet(idx);
        final CTSheet newcts = ct.insertNewSheet(pos);
        newcts.set(cts);
        final CTSheet[] sheetArray = ct.getSheetArray();
        for (int i = 0; i < sheetArray.length; ++i) {
            this.sheets.get(i).sheet = sheetArray[i];
        }
        this.updateNamedRangesAfterSheetReorder(idx, pos);
        this.updateActiveSheetAfterSheetReorder(idx, pos);
    }
    
    private void updateNamedRangesAfterSheetReorder(final int oldIndex, final int newIndex) {
        for (final XSSFName name : this.namedRanges) {
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
    
    private void saveNamedRanges() {
        if (this.namedRanges.size() > 0) {
            final CTDefinedNames names = CTDefinedNames.Factory.newInstance();
            final CTDefinedName[] nr = new CTDefinedName[this.namedRanges.size()];
            int i = 0;
            for (final XSSFName name : this.namedRanges) {
                nr[i] = name.getCTName();
                ++i;
            }
            names.setDefinedNameArray(nr);
            if (this.workbook.isSetDefinedNames()) {
                this.workbook.unsetDefinedNames();
            }
            this.workbook.setDefinedNames(names);
            this.reprocessNamedRanges();
        }
        else if (this.workbook.isSetDefinedNames()) {
            this.workbook.unsetDefinedNames();
        }
    }
    
    private void reprocessNamedRanges() {
        this.namedRangesByName = (ListValuedMap<String, XSSFName>)new ArrayListValuedHashMap();
        this.namedRanges = new ArrayList<XSSFName>();
        if (this.workbook.isSetDefinedNames()) {
            for (final CTDefinedName ctName : this.workbook.getDefinedNames().getDefinedNameArray()) {
                this.createAndStoreName(ctName);
            }
        }
    }
    
    private void saveCalculationChain() {
        if (this.calcChain != null) {
            final int count = this.calcChain.getCTCalcChain().sizeOfCArray();
            if (count == 0) {
                this.removeRelation(this.calcChain);
                this.calcChain = null;
            }
        }
    }
    
    protected void commit() throws IOException {
        this.saveNamedRanges();
        this.saveCalculationChain();
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTWorkbook.type.getName().getNamespaceURI(), "workbook"));
        final PackagePart part = this.getPackagePart();
        try (final OutputStream out = part.getOutputStream()) {
            this.workbook.save(out, xmlOptions);
        }
    }
    
    @Internal
    public SharedStringsTable getSharedStringSource() {
        return this.sharedStringSource;
    }
    
    public StylesTable getStylesSource() {
        return this.stylesSource;
    }
    
    public ThemesTable getTheme() {
        if (this.stylesSource == null) {
            return null;
        }
        return this.stylesSource.getTheme();
    }
    
    public XSSFCreationHelper getCreationHelper() {
        if (this._creationHelper == null) {
            this._creationHelper = new XSSFCreationHelper(this);
        }
        return this._creationHelper;
    }
    
    private boolean containsSheet(String name, final int excludeSheetIdx) {
        final CTSheet[] ctSheetArray = this.workbook.getSheets().getSheetArray();
        if (name.length() > 31) {
            name = name.substring(0, 31);
        }
        for (int i = 0; i < ctSheetArray.length; ++i) {
            String ctName = ctSheetArray[i].getName();
            if (ctName.length() > 31) {
                ctName = ctName.substring(0, 31);
            }
            if (excludeSheetIdx != i && name.equalsIgnoreCase(ctName)) {
                return true;
            }
        }
        return false;
    }
    
    @Internal
    public boolean isDate1904() {
        final CTWorkbookPr workbookPr = this.workbook.getWorkbookPr();
        return workbookPr != null && workbookPr.getDate1904();
    }
    
    @Override
    public List<PackagePart> getAllEmbeddedParts() throws OpenXML4JException {
        final List<PackagePart> embedds = new LinkedList<PackagePart>();
        for (final XSSFSheet sheet : this.sheets) {
            for (final PackageRelationship rel : sheet.getPackagePart().getRelationshipsByType(XSSFRelation.OLEEMBEDDINGS.getRelation())) {
                embedds.add(sheet.getPackagePart().getRelatedPart(rel));
            }
            for (final PackageRelationship rel : sheet.getPackagePart().getRelationshipsByType(XSSFRelation.PACKEMBEDDINGS.getRelation())) {
                embedds.add(sheet.getPackagePart().getRelatedPart(rel));
            }
        }
        return embedds;
    }
    
    @NotImplemented
    public boolean isHidden() {
        throw new RuntimeException("Not implemented yet");
    }
    
    @NotImplemented
    public void setHidden(final boolean hiddenFlag) {
        throw new RuntimeException("Not implemented yet");
    }
    
    public boolean isSheetHidden(final int sheetIx) {
        this.validateSheetIndex(sheetIx);
        final CTSheet ctSheet = this.sheets.get(sheetIx).sheet;
        return ctSheet.getState() == STSheetState.HIDDEN;
    }
    
    public boolean isSheetVeryHidden(final int sheetIx) {
        this.validateSheetIndex(sheetIx);
        final CTSheet ctSheet = this.sheets.get(sheetIx).sheet;
        return ctSheet.getState() == STSheetState.VERY_HIDDEN;
    }
    
    public SheetVisibility getSheetVisibility(final int sheetIx) {
        this.validateSheetIndex(sheetIx);
        final CTSheet ctSheet = this.sheets.get(sheetIx).sheet;
        final STSheetState.Enum state = ctSheet.getState();
        if (state == STSheetState.VISIBLE) {
            return SheetVisibility.VISIBLE;
        }
        if (state == STSheetState.HIDDEN) {
            return SheetVisibility.HIDDEN;
        }
        if (state == STSheetState.VERY_HIDDEN) {
            return SheetVisibility.VERY_HIDDEN;
        }
        throw new IllegalArgumentException("This should never happen");
    }
    
    public void setSheetHidden(final int sheetIx, final boolean hidden) {
        this.setSheetVisibility(sheetIx, hidden ? SheetVisibility.HIDDEN : SheetVisibility.VISIBLE);
    }
    
    public void setSheetVisibility(final int sheetIx, final SheetVisibility visibility) {
        this.validateSheetIndex(sheetIx);
        final CTSheet ctSheet = this.sheets.get(sheetIx).sheet;
        switch (visibility) {
            case VISIBLE: {
                ctSheet.setState(STSheetState.VISIBLE);
                break;
            }
            case HIDDEN: {
                ctSheet.setState(STSheetState.HIDDEN);
                break;
            }
            case VERY_HIDDEN: {
                ctSheet.setState(STSheetState.VERY_HIDDEN);
                break;
            }
            default: {
                throw new IllegalArgumentException("This should never happen");
            }
        }
    }
    
    protected void onDeleteFormula(final XSSFCell cell) {
        if (this.calcChain != null) {
            final int sheetId = (int)cell.getSheet().sheet.getSheetId();
            this.calcChain.removeItem(sheetId, cell.getReference());
        }
    }
    
    @Internal
    public CalculationChain getCalculationChain() {
        return this.calcChain;
    }
    
    @Internal
    public List<ExternalLinksTable> getExternalLinksTable() {
        return this.externalLinks;
    }
    
    public Collection<XSSFMap> getCustomXMLMappings() {
        return (this.mapInfo == null) ? new ArrayList<XSSFMap>() : this.mapInfo.getAllXSSFMaps();
    }
    
    @Internal
    public MapInfo getMapInfo() {
        return this.mapInfo;
    }
    
    @NotImplemented
    public int linkExternalWorkbook(final String name, final Workbook workbook) {
        throw new RuntimeException("Not Implemented - see bug #57184");
    }
    
    public boolean isStructureLocked() {
        return this.workbookProtectionPresent() && this.workbook.getWorkbookProtection().getLockStructure();
    }
    
    public boolean isWindowsLocked() {
        return this.workbookProtectionPresent() && this.workbook.getWorkbookProtection().getLockWindows();
    }
    
    public boolean isRevisionLocked() {
        return this.workbookProtectionPresent() && this.workbook.getWorkbookProtection().getLockRevision();
    }
    
    public void lockStructure() {
        this.safeGetWorkbookProtection().setLockStructure(true);
    }
    
    public void unLockStructure() {
        this.safeGetWorkbookProtection().setLockStructure(false);
    }
    
    public void lockWindows() {
        this.safeGetWorkbookProtection().setLockWindows(true);
    }
    
    public void unLockWindows() {
        this.safeGetWorkbookProtection().setLockWindows(false);
    }
    
    public void lockRevision() {
        this.safeGetWorkbookProtection().setLockRevision(true);
    }
    
    public void unLockRevision() {
        this.safeGetWorkbookProtection().setLockRevision(false);
    }
    
    public void setWorkbookPassword(final String password, final HashAlgorithm hashAlgo) {
        if (password == null && !this.workbookProtectionPresent()) {
            return;
        }
        XSSFPasswordHelper.setPassword((XmlObject)this.safeGetWorkbookProtection(), password, hashAlgo, "workbook");
    }
    
    public boolean validateWorkbookPassword(final String password) {
        if (!this.workbookProtectionPresent()) {
            return password == null;
        }
        return XSSFPasswordHelper.validatePassword((XmlObject)this.safeGetWorkbookProtection(), password, "workbook");
    }
    
    public void setRevisionsPassword(final String password, final HashAlgorithm hashAlgo) {
        if (password == null && !this.workbookProtectionPresent()) {
            return;
        }
        XSSFPasswordHelper.setPassword((XmlObject)this.safeGetWorkbookProtection(), password, hashAlgo, "revisions");
    }
    
    public boolean validateRevisionsPassword(final String password) {
        if (!this.workbookProtectionPresent()) {
            return password == null;
        }
        return XSSFPasswordHelper.validatePassword((XmlObject)this.safeGetWorkbookProtection(), password, "revisions");
    }
    
    public void unLock() {
        if (this.workbookProtectionPresent()) {
            this.workbook.unsetWorkbookProtection();
        }
    }
    
    private boolean workbookProtectionPresent() {
        return this.workbook.isSetWorkbookProtection();
    }
    
    private CTWorkbookProtection safeGetWorkbookProtection() {
        if (!this.workbookProtectionPresent()) {
            return this.workbook.addNewWorkbookProtection();
        }
        return this.workbook.getWorkbookProtection();
    }
    
    UDFFinder getUDFFinder() {
        return (UDFFinder)this._udfFinder;
    }
    
    public void addToolPack(final UDFFinder toopack) {
        this._udfFinder.add(toopack);
    }
    
    public void setForceFormulaRecalculation(final boolean value) {
        final CTWorkbook ctWorkbook = this.getCTWorkbook();
        final CTCalcPr calcPr = ctWorkbook.isSetCalcPr() ? ctWorkbook.getCalcPr() : ctWorkbook.addNewCalcPr();
        calcPr.setFullCalcOnLoad(value);
        if (value && calcPr.getCalcMode() == STCalcMode.MANUAL) {
            calcPr.setCalcMode(STCalcMode.AUTO);
        }
    }
    
    public boolean getForceFormulaRecalculation() {
        final CTWorkbook ctWorkbook = this.getCTWorkbook();
        final CTCalcPr calcPr = ctWorkbook.getCalcPr();
        return calcPr != null && calcPr.isSetFullCalcOnLoad() && calcPr.getFullCalcOnLoad();
    }
    
    protected CTPivotCache addPivotCache(final String rId) {
        final CTWorkbook ctWorkbook = this.getCTWorkbook();
        CTPivotCaches caches;
        if (ctWorkbook.isSetPivotCaches()) {
            caches = ctWorkbook.getPivotCaches();
        }
        else {
            caches = ctWorkbook.addNewPivotCaches();
        }
        final CTPivotCache cache = caches.addNewPivotCache();
        final int tableId = this.getPivotTables().size() + 1;
        cache.setCacheId((long)tableId);
        cache.setId(rId);
        if (this.pivotCaches == null) {
            this.pivotCaches = new ArrayList<CTPivotCache>();
        }
        this.pivotCaches.add(cache);
        return cache;
    }
    
    public List<XSSFPivotTable> getPivotTables() {
        return this.pivotTables;
    }
    
    protected void setPivotTables(final List<XSSFPivotTable> pivotTables) {
        this.pivotTables = pivotTables;
    }
    
    public XSSFWorkbookType getWorkbookType() {
        return this.isMacroEnabled() ? XSSFWorkbookType.XLSM : XSSFWorkbookType.XLSX;
    }
    
    public void setWorkbookType(final XSSFWorkbookType type) {
        try {
            this.getPackagePart().setContentType(type.getContentType());
        }
        catch (final InvalidFormatException e) {
            throw new POIXMLException(e);
        }
    }
    
    public void setVBAProject(final InputStream vbaProjectStream) throws IOException {
        if (!this.isMacroEnabled()) {
            this.setWorkbookType(XSSFWorkbookType.XLSM);
        }
        PackagePartName ppName;
        try {
            ppName = PackagingURIHelper.createPartName(XSSFRelation.VBA_MACROS.getDefaultFileName());
        }
        catch (final InvalidFormatException e) {
            throw new POIXMLException(e);
        }
        final OPCPackage opc = this.getPackage();
        OutputStream outputStream;
        if (!opc.containPart(ppName)) {
            final POIXMLDocumentPart relationship = this.createRelationship(XSSFRelation.VBA_MACROS, this.xssfFactory);
            outputStream = relationship.getPackagePart().getOutputStream();
        }
        else {
            final PackagePart part = opc.getPart(ppName);
            outputStream = part.getOutputStream();
        }
        try {
            IOUtils.copy(vbaProjectStream, outputStream);
        }
        finally {
            IOUtils.closeQuietly((Closeable)outputStream);
        }
    }
    
    public void setVBAProject(final XSSFWorkbook macroWorkbook) throws IOException, InvalidFormatException {
        if (!macroWorkbook.isMacroEnabled()) {
            return;
        }
        final InputStream vbaProjectStream = XSSFRelation.VBA_MACROS.getContents(macroWorkbook.getCorePart());
        if (vbaProjectStream != null) {
            this.setVBAProject(vbaProjectStream);
        }
    }
    
    public SpreadsheetVersion getSpreadsheetVersion() {
        return SpreadsheetVersion.EXCEL2007;
    }
    
    public XSSFTable getTable(final String name) {
        if (name != null && this.sheets != null) {
            for (final XSSFSheet sheet : this.sheets) {
                for (final XSSFTable tbl : sheet.getTables()) {
                    if (name.equalsIgnoreCase(tbl.getName())) {
                        return tbl;
                    }
                }
            }
        }
        return null;
    }
    
    public int addOlePackage(final byte[] oleData, final String label, final String fileName, final String command) throws IOException {
        final OPCPackage opc = this.getPackage();
        int oleId = 0;
        PackagePartName pnOLE;
        do {
            try {
                pnOLE = PackagingURIHelper.createPartName("/xl/embeddings/oleObject" + ++oleId + ".bin");
            }
            catch (final InvalidFormatException e) {
                throw new IOException("ole object name not recognized", e);
            }
        } while (opc.containPart(pnOLE));
        final PackagePart pp = opc.createPart(pnOLE, "application/vnd.openxmlformats-officedocument.oleObject");
        final Ole10Native ole10 = new Ole10Native(label, fileName, command, oleData);
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream(oleData.length + 500)) {
            ole10.writeOut((OutputStream)bos);
            try (final POIFSFileSystem poifs = new POIFSFileSystem()) {
                final DirectoryNode root = poifs.getRoot();
                root.createDocument("\u0001Ole10Native", (InputStream)new ByteArrayInputStream(bos.toByteArray()));
                root.setStorageClsid(ClassIDPredefined.OLE_V1_PACKAGE.getClassID());
                try (final OutputStream os = pp.getOutputStream()) {
                    poifs.writeFilesystem(os);
                }
            }
        }
        return oleId;
    }
    
    public void setCellFormulaValidation(final boolean value) {
        this.cellFormulaValidation = value;
    }
    
    public boolean getCellFormulaValidation() {
        return this.cellFormulaValidation;
    }
    
    static {
        COMMA_PATTERN = Pattern.compile(",");
        XSSFWorkbook.logger = POILogFactory.getLogger((Class)XSSFWorkbook.class);
    }
    
    private final class SheetIterator<T extends Sheet> implements Iterator<T>
    {
        private final Iterator<T> it;
        
        public SheetIterator() {
            this.it = XSSFWorkbook.this.sheets.iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.it.hasNext();
        }
        
        @Override
        public T next() throws NoSuchElementException {
            return this.it.next();
        }
        
        @Override
        public void remove() throws IllegalStateException {
            throw new UnsupportedOperationException("remove method not supported on XSSFWorkbook.iterator(). Use Sheet.removeSheetAt(int) instead.");
        }
    }
}
