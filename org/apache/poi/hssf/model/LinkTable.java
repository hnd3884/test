package org.apache.poi.hssf.model;

import org.apache.poi.hssf.record.CRNRecord;
import org.apache.poi.hssf.record.CRNCountRecord;
import org.apache.poi.ss.formula.ptg.ErrPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.hssf.record.ExternalNameRecord;
import org.apache.poi.ss.formula.ptg.NameXPtg;
import org.apache.poi.ss.formula.SheetNameFormatter;
import org.apache.poi.ss.usermodel.Workbook;
import java.util.Iterator;
import java.util.Collection;
import org.apache.poi.hssf.record.SupBookRecord;
import java.util.ArrayList;
import org.apache.poi.hssf.record.NameCommentRecord;
import java.util.Map;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.NameRecord;
import java.util.List;
import org.apache.poi.hssf.record.ExternSheetRecord;

final class LinkTable
{
    private ExternalBookBlock[] _externalBookBlocks;
    private final ExternSheetRecord _externSheetRecord;
    private final List<NameRecord> _definedNames;
    private final int _recordCount;
    private final WorkbookRecordList _workbookRecordList;
    
    public LinkTable(final List<Record> inputList, final int startIndex, final WorkbookRecordList workbookRecordList, final Map<String, NameCommentRecord> commentRecords) {
        this._workbookRecordList = workbookRecordList;
        final RecordStream rs = new RecordStream(inputList, startIndex);
        final List<ExternalBookBlock> temp = new ArrayList<ExternalBookBlock>();
        while (rs.peekNextClass() == SupBookRecord.class) {
            temp.add(new ExternalBookBlock(rs));
        }
        temp.toArray(this._externalBookBlocks = new ExternalBookBlock[temp.size()]);
        temp.clear();
        if (this._externalBookBlocks.length > 0) {
            if (rs.peekNextClass() != ExternSheetRecord.class) {
                this._externSheetRecord = null;
            }
            else {
                this._externSheetRecord = readExtSheetRecord(rs);
            }
        }
        else {
            this._externSheetRecord = null;
        }
        this._definedNames = new ArrayList<NameRecord>();
        while (true) {
            final Class<? extends Record> nextClass = rs.peekNextClass();
            if (nextClass == NameRecord.class) {
                final NameRecord nr = (NameRecord)rs.getNext();
                this._definedNames.add(nr);
            }
            else {
                if (nextClass != NameCommentRecord.class) {
                    break;
                }
                final NameCommentRecord ncr = (NameCommentRecord)rs.getNext();
                commentRecords.put(ncr.getNameText(), ncr);
            }
        }
        this._recordCount = rs.getCountRead();
        this._workbookRecordList.getRecords().addAll(inputList.subList(startIndex, startIndex + this._recordCount));
    }
    
    private static ExternSheetRecord readExtSheetRecord(final RecordStream rs) {
        final List<ExternSheetRecord> temp = new ArrayList<ExternSheetRecord>(2);
        while (rs.peekNextClass() == ExternSheetRecord.class) {
            temp.add((ExternSheetRecord)rs.getNext());
        }
        final int nItems = temp.size();
        if (nItems < 1) {
            throw new RuntimeException("Expected an EXTERNSHEET record but got (" + rs.peekNextClass().getName() + ")");
        }
        if (nItems == 1) {
            return temp.get(0);
        }
        final ExternSheetRecord[] esrs = new ExternSheetRecord[nItems];
        temp.toArray(esrs);
        return ExternSheetRecord.combine(esrs);
    }
    
    public LinkTable(final int numberOfSheets, final WorkbookRecordList workbookRecordList) {
        this._workbookRecordList = workbookRecordList;
        this._definedNames = new ArrayList<NameRecord>();
        this._externalBookBlocks = new ExternalBookBlock[] { new ExternalBookBlock(numberOfSheets) };
        this._externSheetRecord = new ExternSheetRecord();
        this._recordCount = 2;
        final SupBookRecord supbook = this._externalBookBlocks[0].getExternalBookRecord();
        final int idx = this.findFirstRecordLocBySid((short)140);
        if (idx < 0) {
            throw new RuntimeException("CountryRecord not found");
        }
        this._workbookRecordList.add(idx + 1, this._externSheetRecord);
        this._workbookRecordList.add(idx + 1, supbook);
    }
    
    public int getRecordCount() {
        return this._recordCount;
    }
    
    public NameRecord getSpecificBuiltinRecord(final byte builtInCode, final int sheetNumber) {
        for (final NameRecord record : this._definedNames) {
            if (record.getBuiltInName() == builtInCode && record.getSheetNumber() == sheetNumber) {
                return record;
            }
        }
        return null;
    }
    
    public void removeBuiltinRecord(final byte name, final int sheetIndex) {
        final NameRecord record = this.getSpecificBuiltinRecord(name, sheetIndex);
        if (record != null) {
            this._definedNames.remove(record);
        }
    }
    
    public int getNumNames() {
        return this._definedNames.size();
    }
    
    public NameRecord getNameRecord(final int index) {
        return this._definedNames.get(index);
    }
    
    public void addName(final NameRecord name) {
        this._definedNames.add(name);
        int idx = this.findFirstRecordLocBySid((short)23);
        if (idx == -1) {
            idx = this.findFirstRecordLocBySid((short)430);
        }
        if (idx == -1) {
            idx = this.findFirstRecordLocBySid((short)140);
        }
        final int countNames = this._definedNames.size();
        this._workbookRecordList.add(idx + countNames, name);
    }
    
    public void removeName(final int namenum) {
        this._definedNames.remove(namenum);
    }
    
    public boolean nameAlreadyExists(final NameRecord name) {
        for (int i = this.getNumNames() - 1; i >= 0; --i) {
            final NameRecord rec = this.getNameRecord(i);
            if (rec != name && isDuplicatedNames(name, rec)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isDuplicatedNames(final NameRecord firstName, final NameRecord lastName) {
        return lastName.getNameText().equalsIgnoreCase(firstName.getNameText()) && isSameSheetNames(firstName, lastName);
    }
    
    private static boolean isSameSheetNames(final NameRecord firstName, final NameRecord lastName) {
        return lastName.getSheetNumber() == firstName.getSheetNumber();
    }
    
    public String[] getExternalBookAndSheetName(final int extRefIndex) {
        final int ebIx = this._externSheetRecord.getExtbookIndexFromRefIndex(extRefIndex);
        final SupBookRecord ebr = this._externalBookBlocks[ebIx].getExternalBookRecord();
        if (!ebr.isExternalReferences()) {
            return null;
        }
        final int shIx1 = this._externSheetRecord.getFirstSheetIndexFromRefIndex(extRefIndex);
        final int shIx2 = this._externSheetRecord.getLastSheetIndexFromRefIndex(extRefIndex);
        String firstSheetName = null;
        String lastSheetName = null;
        if (shIx1 >= 0) {
            firstSheetName = ebr.getSheetNames()[shIx1];
        }
        if (shIx2 >= 0) {
            lastSheetName = ebr.getSheetNames()[shIx2];
        }
        if (shIx1 == shIx2) {
            return new String[] { ebr.getURL(), firstSheetName };
        }
        return new String[] { ebr.getURL(), firstSheetName, lastSheetName };
    }
    
    private int getExternalWorkbookIndex(final String workbookName) {
        for (int i = 0; i < this._externalBookBlocks.length; ++i) {
            final SupBookRecord ebr = this._externalBookBlocks[i].getExternalBookRecord();
            if (ebr.isExternalReferences()) {
                if (workbookName.equals(ebr.getURL())) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public int linkExternalWorkbook(final String name, final Workbook externalWorkbook) {
        int extBookIndex = this.getExternalWorkbookIndex(name);
        if (extBookIndex != -1) {
            return extBookIndex;
        }
        final String[] sheetNames = new String[externalWorkbook.getNumberOfSheets()];
        for (int sn = 0; sn < sheetNames.length; ++sn) {
            sheetNames[sn] = externalWorkbook.getSheetName(sn);
        }
        final String url = "\u0000" + name;
        final ExternalBookBlock block = new ExternalBookBlock(url, sheetNames);
        extBookIndex = this.extendExternalBookBlocks(block);
        int idx = this.findFirstRecordLocBySid((short)23);
        if (idx == -1) {
            idx = this._workbookRecordList.size();
        }
        this._workbookRecordList.add(idx, block.getExternalBookRecord());
        for (int sn2 = 0; sn2 < sheetNames.length; ++sn2) {
            this._externSheetRecord.addRef(extBookIndex, sn2, sn2);
        }
        return extBookIndex;
    }
    
    public int getExternalSheetIndex(final String workbookName, final String firstSheetName, final String lastSheetName) {
        final int externalBookIndex = this.getExternalWorkbookIndex(workbookName);
        if (externalBookIndex == -1) {
            throw new RuntimeException("No external workbook with name '" + workbookName + "'");
        }
        final SupBookRecord ebrTarget = this._externalBookBlocks[externalBookIndex].getExternalBookRecord();
        final int firstSheetIndex = getSheetIndex(ebrTarget.getSheetNames(), firstSheetName);
        final int lastSheetIndex = getSheetIndex(ebrTarget.getSheetNames(), lastSheetName);
        int result = this._externSheetRecord.getRefIxForSheet(externalBookIndex, firstSheetIndex, lastSheetIndex);
        if (result < 0) {
            result = this._externSheetRecord.addRef(externalBookIndex, firstSheetIndex, lastSheetIndex);
        }
        return result;
    }
    
    private static int getSheetIndex(final String[] sheetNames, final String sheetName) {
        for (int i = 0; i < sheetNames.length; ++i) {
            if (sheetNames[i].equals(sheetName)) {
                return i;
            }
        }
        throw new RuntimeException("External workbook does not contain sheet '" + sheetName + "'");
    }
    
    public int getFirstInternalSheetIndexForExtIndex(final int extRefIndex) {
        if (extRefIndex >= this._externSheetRecord.getNumOfRefs() || extRefIndex < 0) {
            return -1;
        }
        return this._externSheetRecord.getFirstSheetIndexFromRefIndex(extRefIndex);
    }
    
    public int getLastInternalSheetIndexForExtIndex(final int extRefIndex) {
        if (extRefIndex >= this._externSheetRecord.getNumOfRefs() || extRefIndex < 0) {
            return -1;
        }
        return this._externSheetRecord.getLastSheetIndexFromRefIndex(extRefIndex);
    }
    
    public void removeSheet(final int sheetIdx) {
        this._externSheetRecord.removeSheet(sheetIdx);
    }
    
    public int checkExternSheet(final int sheetIndex) {
        return this.checkExternSheet(sheetIndex, sheetIndex);
    }
    
    public int checkExternSheet(final int firstSheetIndex, final int lastSheetIndex) {
        int thisWbIndex = -1;
        for (int i = 0; i < this._externalBookBlocks.length; ++i) {
            final SupBookRecord ebr = this._externalBookBlocks[i].getExternalBookRecord();
            if (ebr.isInternalReferences()) {
                thisWbIndex = i;
                break;
            }
        }
        if (thisWbIndex < 0) {
            throw new RuntimeException("Could not find 'internal references' EXTERNALBOOK");
        }
        int i = this._externSheetRecord.getRefIxForSheet(thisWbIndex, firstSheetIndex, lastSheetIndex);
        if (i >= 0) {
            return i;
        }
        return this._externSheetRecord.addRef(thisWbIndex, firstSheetIndex, lastSheetIndex);
    }
    
    private int findFirstRecordLocBySid(final short sid) {
        int index = 0;
        for (final Record record : this._workbookRecordList.getRecords()) {
            if (record.getSid() == sid) {
                return index;
            }
            ++index;
        }
        return -1;
    }
    
    public String resolveNameXText(final int refIndex, final int definedNameIndex, final InternalWorkbook workbook) {
        final int extBookIndex = this._externSheetRecord.getExtbookIndexFromRefIndex(refIndex);
        final int firstTabIndex = this._externSheetRecord.getFirstSheetIndexFromRefIndex(refIndex);
        if (firstTabIndex == -1) {
            throw new RuntimeException("Referenced sheet could not be found");
        }
        final ExternalBookBlock externalBook = this._externalBookBlocks[extBookIndex];
        if (externalBook._externalNameRecords.length > definedNameIndex) {
            return this._externalBookBlocks[extBookIndex].getNameText(definedNameIndex);
        }
        if (firstTabIndex == -2) {
            final NameRecord nr = this.getNameRecord(definedNameIndex);
            final int sheetNumber = nr.getSheetNumber();
            final StringBuilder text = new StringBuilder(64);
            if (sheetNumber > 0) {
                final String sheetName = workbook.getSheetName(sheetNumber - 1);
                SheetNameFormatter.appendFormat(text, sheetName);
                text.append("!");
            }
            text.append(nr.getNameText());
            return text.toString();
        }
        throw new ArrayIndexOutOfBoundsException("Ext Book Index relative but beyond the supported length, was " + extBookIndex + " but maximum is " + this._externalBookBlocks.length);
    }
    
    public int resolveNameXIx(final int refIndex, final int definedNameIndex) {
        final int extBookIndex = this._externSheetRecord.getExtbookIndexFromRefIndex(refIndex);
        return this._externalBookBlocks[extBookIndex].getNameIx(definedNameIndex);
    }
    
    public NameXPtg getNameXPtg(final String name, final int sheetRefIndex) {
        for (int i = 0; i < this._externalBookBlocks.length; ++i) {
            final int definedNameIndex = this._externalBookBlocks[i].getIndexOfName(name);
            if (definedNameIndex >= 0) {
                final int thisSheetRefIndex = this.findRefIndexFromExtBookIndex(i);
                if (thisSheetRefIndex >= 0 && (sheetRefIndex == -1 || thisSheetRefIndex == sheetRefIndex)) {
                    return new NameXPtg(thisSheetRefIndex, definedNameIndex);
                }
            }
        }
        return null;
    }
    
    public NameXPtg addNameXPtg(final String name) {
        int extBlockIndex = -1;
        ExternalBookBlock extBlock = null;
        for (int i = 0; i < this._externalBookBlocks.length; ++i) {
            final SupBookRecord ebr = this._externalBookBlocks[i].getExternalBookRecord();
            if (ebr.isAddInFunctions()) {
                extBlock = this._externalBookBlocks[i];
                extBlockIndex = i;
                break;
            }
        }
        if (extBlock == null) {
            extBlock = new ExternalBookBlock();
            extBlockIndex = this.extendExternalBookBlocks(extBlock);
            final int idx = this.findFirstRecordLocBySid((short)23);
            this._workbookRecordList.add(idx, extBlock.getExternalBookRecord());
            this._externSheetRecord.addRef(this._externalBookBlocks.length - 1, -2, -2);
        }
        final ExternalNameRecord extNameRecord = new ExternalNameRecord();
        extNameRecord.setText(name);
        extNameRecord.setParsedExpression(new Ptg[] { ErrPtg.REF_INVALID });
        final int nameIndex = extBlock.addExternalName(extNameRecord);
        int supLinkIndex = 0;
        for (final Record record : this._workbookRecordList.getRecords()) {
            if (record instanceof SupBookRecord && ((SupBookRecord)record).isAddInFunctions()) {
                break;
            }
            ++supLinkIndex;
        }
        final int numberOfNames = extBlock.getNumberOfNames();
        this._workbookRecordList.add(supLinkIndex + numberOfNames, extNameRecord);
        final int fakeSheetIdx = -2;
        final int ix = this._externSheetRecord.getRefIxForSheet(extBlockIndex, fakeSheetIdx, fakeSheetIdx);
        return new NameXPtg(ix, nameIndex);
    }
    
    private int extendExternalBookBlocks(final ExternalBookBlock newBlock) {
        final ExternalBookBlock[] tmp = new ExternalBookBlock[this._externalBookBlocks.length + 1];
        System.arraycopy(this._externalBookBlocks, 0, tmp, 0, this._externalBookBlocks.length);
        tmp[tmp.length - 1] = newBlock;
        this._externalBookBlocks = tmp;
        return this._externalBookBlocks.length - 1;
    }
    
    private int findRefIndexFromExtBookIndex(final int extBookIndex) {
        return this._externSheetRecord.findRefIndexFromExtBookIndex(extBookIndex);
    }
    
    public boolean changeExternalReference(final String oldUrl, final String newUrl) {
        for (final ExternalBookBlock ex : this._externalBookBlocks) {
            final SupBookRecord externalRecord = ex.getExternalBookRecord();
            if (externalRecord.isExternalReferences() && externalRecord.getURL().equals(oldUrl)) {
                externalRecord.setURL(newUrl);
                return true;
            }
        }
        return false;
    }
    
    private static final class CRNBlock
    {
        private final CRNCountRecord _countRecord;
        private final CRNRecord[] _crns;
        
        public CRNBlock(final RecordStream rs) {
            this._countRecord = (CRNCountRecord)rs.getNext();
            final int nCRNs = this._countRecord.getNumberOfCRNs();
            final CRNRecord[] crns = new CRNRecord[nCRNs];
            for (int i = 0; i < crns.length; ++i) {
                crns[i] = (CRNRecord)rs.getNext();
            }
            this._crns = crns;
        }
        
        public CRNRecord[] getCrns() {
            return this._crns.clone();
        }
    }
    
    private static final class ExternalBookBlock
    {
        private final SupBookRecord _externalBookRecord;
        private ExternalNameRecord[] _externalNameRecords;
        private final CRNBlock[] _crnBlocks;
        
        public ExternalBookBlock(final RecordStream rs) {
            this._externalBookRecord = (SupBookRecord)rs.getNext();
            final List<Object> temp = new ArrayList<Object>();
            while (rs.peekNextClass() == ExternalNameRecord.class) {
                temp.add(rs.getNext());
            }
            temp.toArray(this._externalNameRecords = new ExternalNameRecord[temp.size()]);
            temp.clear();
            while (rs.peekNextClass() == CRNCountRecord.class) {
                temp.add(new CRNBlock(rs));
            }
            temp.toArray(this._crnBlocks = new CRNBlock[temp.size()]);
        }
        
        public ExternalBookBlock(final String url, final String[] sheetNames) {
            this._externalBookRecord = SupBookRecord.createExternalReferences(url, sheetNames);
            this._crnBlocks = new CRNBlock[0];
        }
        
        public ExternalBookBlock(final int numberOfSheets) {
            this._externalBookRecord = SupBookRecord.createInternalReferences((short)numberOfSheets);
            this._externalNameRecords = new ExternalNameRecord[0];
            this._crnBlocks = new CRNBlock[0];
        }
        
        public ExternalBookBlock() {
            this._externalBookRecord = SupBookRecord.createAddInFunctions();
            this._externalNameRecords = new ExternalNameRecord[0];
            this._crnBlocks = new CRNBlock[0];
        }
        
        public SupBookRecord getExternalBookRecord() {
            return this._externalBookRecord;
        }
        
        public String getNameText(final int definedNameIndex) {
            return this._externalNameRecords[definedNameIndex].getText();
        }
        
        public int getNameIx(final int definedNameIndex) {
            return this._externalNameRecords[definedNameIndex].getIx();
        }
        
        public int getIndexOfName(final String name) {
            for (int i = 0; i < this._externalNameRecords.length; ++i) {
                if (this._externalNameRecords[i].getText().equalsIgnoreCase(name)) {
                    return i;
                }
            }
            return -1;
        }
        
        public int getNumberOfNames() {
            return this._externalNameRecords.length;
        }
        
        public int addExternalName(final ExternalNameRecord rec) {
            final ExternalNameRecord[] tmp = new ExternalNameRecord[this._externalNameRecords.length + 1];
            System.arraycopy(this._externalNameRecords, 0, tmp, 0, this._externalNameRecords.length);
            tmp[tmp.length - 1] = rec;
            this._externalNameRecords = tmp;
            return this._externalNameRecords.length - 1;
        }
    }
}
