package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDefinedName;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.usermodel.Table;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.Sheet;
import java.util.HashMap;
import java.util.Locale;
import org.apache.poi.ss.formula.ptg.NamePtg;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.ss.formula.ptg.NameXPtg;
import org.apache.poi.ss.formula.ptg.Area3DPxg;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.formula.ptg.Ref3DPxg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.udf.IndexedUDFFinder;
import org.apache.poi.ss.formula.ptg.NameXPxg;
import org.apache.poi.ss.formula.SheetIdentifier;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.formula.EvaluationName;
import java.util.Iterator;
import org.apache.poi.xssf.model.ExternalLinksTable;
import java.util.List;
import java.util.Map;
import org.apache.poi.util.Internal;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;

@Internal
public abstract class BaseXSSFEvaluationWorkbook implements FormulaRenderingWorkbook, EvaluationWorkbook, FormulaParsingWorkbook
{
    protected final XSSFWorkbook _uBook;
    private Map<String, XSSFTable> _tableCache;
    
    protected BaseXSSFEvaluationWorkbook(final XSSFWorkbook book) {
        this._uBook = book;
    }
    
    public void clearAllCachedResultValues() {
        this._tableCache = null;
    }
    
    private int convertFromExternalSheetIndex(final int externSheetIndex) {
        return externSheetIndex;
    }
    
    public int convertFromExternSheetIndex(final int externSheetIndex) {
        return externSheetIndex;
    }
    
    private int convertToExternalSheetIndex(final int sheetIndex) {
        return sheetIndex;
    }
    
    public int getExternalSheetIndex(final String sheetName) {
        final int sheetIndex = this._uBook.getSheetIndex(sheetName);
        return this.convertToExternalSheetIndex(sheetIndex);
    }
    
    private int resolveBookIndex(String bookName) {
        if (bookName.startsWith("[") && bookName.endsWith("]")) {
            bookName = bookName.substring(1, bookName.length() - 2);
        }
        try {
            return Integer.parseInt(bookName);
        }
        catch (final NumberFormatException ex) {
            final List<ExternalLinksTable> tables = this._uBook.getExternalLinksTable();
            int index = this.findExternalLinkIndex(bookName, tables);
            if (index != -1) {
                return index;
            }
            if (!bookName.startsWith("'file:///") || !bookName.endsWith("'")) {
                throw new RuntimeException("Book not linked for filename " + bookName);
            }
            String relBookName = bookName.substring(bookName.lastIndexOf(47) + 1);
            relBookName = relBookName.substring(0, relBookName.length() - 1);
            index = this.findExternalLinkIndex(relBookName, tables);
            if (index != -1) {
                return index;
            }
            final ExternalLinksTable fakeLinkTable = new FakeExternalLinksTable(relBookName);
            tables.add(fakeLinkTable);
            return tables.size();
        }
    }
    
    private int findExternalLinkIndex(final String bookName, final List<ExternalLinksTable> tables) {
        int i = 0;
        for (final ExternalLinksTable table : tables) {
            if (table.getLinkedFileName().equals(bookName)) {
                return i + 1;
            }
            ++i;
        }
        return -1;
    }
    
    public EvaluationName getName(final String name, final int sheetIndex) {
        for (int i = 0; i < this._uBook.getNumberOfNames(); ++i) {
            final XSSFName nm = this._uBook.getNameAt(i);
            final String nameText = nm.getNameName();
            final int nameSheetindex = nm.getSheetIndex();
            if (name.equalsIgnoreCase(nameText) && (nameSheetindex == -1 || nameSheetindex == sheetIndex)) {
                return (EvaluationName)new Name(nm, i, (FormulaParsingWorkbook)this);
            }
        }
        return (sheetIndex == -1) ? null : this.getName(name, -1);
    }
    
    public String getSheetName(final int sheetIndex) {
        return this._uBook.getSheetName(sheetIndex);
    }
    
    public EvaluationWorkbook.ExternalName getExternalName(final int externSheetIndex, final int externNameIndex) {
        throw new IllegalStateException("HSSF-style external references are not supported for XSSF");
    }
    
    public EvaluationWorkbook.ExternalName getExternalName(final String nameName, final String sheetName, final int externalWorkbookNumber) {
        if (externalWorkbookNumber > 0) {
            final int linkNumber = externalWorkbookNumber - 1;
            final ExternalLinksTable linkTable = this._uBook.getExternalLinksTable().get(linkNumber);
            for (final org.apache.poi.ss.usermodel.Name name : linkTable.getDefinedNames()) {
                if (name.getNameName().equals(nameName)) {
                    final int nameSheetIndex = name.getSheetIndex() + 1;
                    return new EvaluationWorkbook.ExternalName(nameName, -1, nameSheetIndex);
                }
            }
            throw new IllegalArgumentException("Name '" + nameName + "' not found in reference to " + linkTable.getLinkedFileName());
        }
        final int nameIdx = this._uBook.getNameIndex(nameName);
        return new EvaluationWorkbook.ExternalName(nameName, nameIdx, 0);
    }
    
    public NameXPxg getNameXPtg(final String name, final SheetIdentifier sheet) {
        final IndexedUDFFinder udfFinder = (IndexedUDFFinder)this.getUDFFinder();
        final FreeRefFunction func = udfFinder.findFunction(name);
        if (func != null) {
            return new NameXPxg((String)null, name);
        }
        if (sheet == null) {
            if (!this._uBook.getNames(name).isEmpty()) {
                return new NameXPxg((String)null, name);
            }
            return null;
        }
        else {
            if (sheet._sheetIdentifier == null) {
                final int bookIndex = this.resolveBookIndex(sheet._bookName);
                return new NameXPxg(bookIndex, (String)null, name);
            }
            final String sheetName = sheet._sheetIdentifier.getName();
            if (sheet._bookName != null) {
                final int bookIndex2 = this.resolveBookIndex(sheet._bookName);
                return new NameXPxg(bookIndex2, sheetName, name);
            }
            return new NameXPxg(sheetName, name);
        }
    }
    
    public Ptg get3DReferencePtg(final CellReference cell, final SheetIdentifier sheet) {
        if (sheet._bookName != null) {
            final int bookIndex = this.resolveBookIndex(sheet._bookName);
            return (Ptg)new Ref3DPxg(bookIndex, sheet, cell);
        }
        return (Ptg)new Ref3DPxg(sheet, cell);
    }
    
    public Ptg get3DReferencePtg(final AreaReference area, final SheetIdentifier sheet) {
        if (sheet._bookName != null) {
            final int bookIndex = this.resolveBookIndex(sheet._bookName);
            return (Ptg)new Area3DPxg(bookIndex, sheet, area);
        }
        return (Ptg)new Area3DPxg(sheet, area);
    }
    
    public String resolveNameXText(final NameXPtg n) {
        final int idx = n.getNameIndex();
        String name = null;
        final IndexedUDFFinder udfFinder = (IndexedUDFFinder)this.getUDFFinder();
        name = udfFinder.getFunctionName(idx);
        if (name != null) {
            return name;
        }
        final XSSFName xname = this._uBook.getNameAt(idx);
        if (xname != null) {
            name = xname.getNameName();
        }
        return name;
    }
    
    public EvaluationWorkbook.ExternalSheet getExternalSheet(final int externSheetIndex) {
        throw new IllegalStateException("HSSF-style external references are not supported for XSSF");
    }
    
    public EvaluationWorkbook.ExternalSheet getExternalSheet(final String firstSheetName, final String lastSheetName, final int externalWorkbookNumber) {
        String workbookName;
        if (externalWorkbookNumber > 0) {
            final int linkNumber = externalWorkbookNumber - 1;
            final ExternalLinksTable linkTable = this._uBook.getExternalLinksTable().get(linkNumber);
            workbookName = linkTable.getLinkedFileName();
        }
        else {
            workbookName = null;
        }
        if (lastSheetName == null || firstSheetName.equals(lastSheetName)) {
            return new EvaluationWorkbook.ExternalSheet(workbookName, firstSheetName);
        }
        return (EvaluationWorkbook.ExternalSheet)new EvaluationWorkbook.ExternalSheetRange(workbookName, firstSheetName, lastSheetName);
    }
    
    @NotImplemented
    public int getExternalSheetIndex(final String workbookName, final String sheetName) {
        throw new RuntimeException("not implemented yet");
    }
    
    public int getSheetIndex(final String sheetName) {
        return this._uBook.getSheetIndex(sheetName);
    }
    
    public String getSheetFirstNameByExternSheet(final int externSheetIndex) {
        final int sheetIndex = this.convertFromExternalSheetIndex(externSheetIndex);
        return this._uBook.getSheetName(sheetIndex);
    }
    
    public String getSheetLastNameByExternSheet(final int externSheetIndex) {
        return this.getSheetFirstNameByExternSheet(externSheetIndex);
    }
    
    public String getNameText(final NamePtg namePtg) {
        return this._uBook.getNameAt(namePtg.getIndex()).getNameName();
    }
    
    public EvaluationName getName(final NamePtg namePtg) {
        final int ix = namePtg.getIndex();
        return (EvaluationName)new Name(this._uBook.getNameAt(ix), ix, (FormulaParsingWorkbook)this);
    }
    
    public XSSFName createName() {
        return this._uBook.createName();
    }
    
    private static String caseInsensitive(final String s) {
        return s.toUpperCase(Locale.ROOT);
    }
    
    private Map<String, XSSFTable> getTableCache() {
        if (this._tableCache != null) {
            return this._tableCache;
        }
        this._tableCache = new HashMap<String, XSSFTable>();
        for (final Sheet sheet : this._uBook) {
            for (final XSSFTable tbl : ((XSSFSheet)sheet).getTables()) {
                final String lname = caseInsensitive(tbl.getName());
                this._tableCache.put(lname, tbl);
            }
        }
        return this._tableCache;
    }
    
    public XSSFTable getTable(final String name) {
        if (name == null) {
            return null;
        }
        final String lname = caseInsensitive(name);
        return this.getTableCache().get(lname);
    }
    
    public UDFFinder getUDFFinder() {
        return this._uBook.getUDFFinder();
    }
    
    public SpreadsheetVersion getSpreadsheetVersion() {
        return SpreadsheetVersion.EXCEL2007;
    }
    
    private static class FakeExternalLinksTable extends ExternalLinksTable
    {
        private final String fileName;
        
        private FakeExternalLinksTable(final String fileName) {
            this.fileName = fileName;
        }
        
        @Override
        public String getLinkedFileName() {
            return this.fileName;
        }
    }
    
    private static final class Name implements EvaluationName
    {
        private final XSSFName _nameRecord;
        private final int _index;
        private final FormulaParsingWorkbook _fpBook;
        
        public Name(final XSSFName name, final int index, final FormulaParsingWorkbook fpBook) {
            this._nameRecord = name;
            this._index = index;
            this._fpBook = fpBook;
        }
        
        public Ptg[] getNameDefinition() {
            return FormulaParser.parse(this._nameRecord.getRefersToFormula(), this._fpBook, FormulaType.NAMEDRANGE, this._nameRecord.getSheetIndex());
        }
        
        public String getNameText() {
            return this._nameRecord.getNameName();
        }
        
        public boolean hasFormula() {
            final CTDefinedName ctn = this._nameRecord.getCTName();
            final String strVal = ctn.getStringValue();
            return !ctn.getFunction() && strVal != null && strVal.length() > 0;
        }
        
        public boolean isFunctionName() {
            return this._nameRecord.isFunctionName();
        }
        
        public boolean isRange() {
            return this.hasFormula();
        }
        
        public NamePtg createPtg() {
            return new NamePtg(this._index);
        }
    }
}
