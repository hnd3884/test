package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.List;

public class ExternSheetRecord extends StandardRecord
{
    public static final short sid = 23;
    private final List<RefSubRecord> _list;
    
    public ExternSheetRecord() {
        this._list = new ArrayList<RefSubRecord>();
    }
    
    public ExternSheetRecord(final ExternSheetRecord other) {
        this._list = new ArrayList<RefSubRecord>();
        other._list.stream().map((Function<? super Object, ?>)RefSubRecord::new).forEach(this._list::add);
    }
    
    public ExternSheetRecord(final RecordInputStream in) {
        this._list = new ArrayList<RefSubRecord>();
        for (int nItems = in.readShort(), i = 0; i < nItems; ++i) {
            final RefSubRecord rec = new RefSubRecord(in);
            this._list.add(rec);
        }
    }
    
    public int getNumOfRefs() {
        return this._list.size();
    }
    
    public void addREFRecord(final RefSubRecord rec) {
        this._list.add(rec);
    }
    
    public int getNumOfREFRecords() {
        return this._list.size();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final int nItems = this._list.size();
        sb.append("[EXTERNSHEET]\n");
        sb.append("   numOfRefs     = ").append(nItems).append("\n");
        for (int i = 0; i < nItems; ++i) {
            sb.append("refrec         #").append(i).append(": ");
            sb.append(this.getRef(i));
            sb.append('\n');
        }
        sb.append("[/EXTERNSHEET]\n");
        return sb.toString();
    }
    
    @Override
    protected int getDataSize() {
        return 2 + this._list.size() * 6;
    }
    
    public void serialize(final LittleEndianOutput out) {
        final int nItems = this._list.size();
        out.writeShort(nItems);
        for (int i = 0; i < nItems; ++i) {
            this.getRef(i).serialize(out);
        }
    }
    
    private RefSubRecord getRef(final int i) {
        return this._list.get(i);
    }
    
    public void removeSheet(final int sheetIdx) {
        for (int nItems = this._list.size(), i = 0; i < nItems; ++i) {
            final RefSubRecord refSubRecord = this._list.get(i);
            if (refSubRecord.getFirstSheetIndex() == sheetIdx && refSubRecord.getLastSheetIndex() == sheetIdx) {
                this._list.set(i, new RefSubRecord(refSubRecord.getExtBookIndex(), -1, -1));
            }
            else if (refSubRecord.getFirstSheetIndex() > sheetIdx && refSubRecord.getLastSheetIndex() > sheetIdx) {
                this._list.set(i, new RefSubRecord(refSubRecord.getExtBookIndex(), refSubRecord.getFirstSheetIndex() - 1, refSubRecord.getLastSheetIndex() - 1));
            }
        }
    }
    
    @Override
    public short getSid() {
        return 23;
    }
    
    public int getExtbookIndexFromRefIndex(final int refIndex) {
        final RefSubRecord refRec = this.getRef(refIndex);
        return refRec.getExtBookIndex();
    }
    
    public int findRefIndexFromExtBookIndex(final int extBookIndex) {
        for (int nItems = this._list.size(), i = 0; i < nItems; ++i) {
            if (this.getRef(i).getExtBookIndex() == extBookIndex) {
                return i;
            }
        }
        return -1;
    }
    
    public int getFirstSheetIndexFromRefIndex(final int extRefIndex) {
        return this.getRef(extRefIndex).getFirstSheetIndex();
    }
    
    public int getLastSheetIndexFromRefIndex(final int extRefIndex) {
        return this.getRef(extRefIndex).getLastSheetIndex();
    }
    
    public int addRef(final int extBookIndex, final int firstSheetIndex, final int lastSheetIndex) {
        this._list.add(new RefSubRecord(extBookIndex, firstSheetIndex, lastSheetIndex));
        return this._list.size() - 1;
    }
    
    public int getRefIxForSheet(final int externalBookIndex, final int firstSheetIndex, final int lastSheetIndex) {
        for (int nItems = this._list.size(), i = 0; i < nItems; ++i) {
            final RefSubRecord ref = this.getRef(i);
            if (ref.getExtBookIndex() == externalBookIndex) {
                if (ref.getFirstSheetIndex() == firstSheetIndex && ref.getLastSheetIndex() == lastSheetIndex) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public static ExternSheetRecord combine(final ExternSheetRecord[] esrs) {
        final ExternSheetRecord result = new ExternSheetRecord();
        for (final ExternSheetRecord esr : esrs) {
            for (int nRefs = esr.getNumOfREFRecords(), j = 0; j < nRefs; ++j) {
                result.addREFRecord(esr.getRef(j));
            }
        }
        return result;
    }
    
    @Override
    public ExternSheetRecord copy() {
        return new ExternSheetRecord(this);
    }
    
    private static final class RefSubRecord
    {
        public static final int ENCODED_SIZE = 6;
        private final int _extBookIndex;
        private int _firstSheetIndex;
        private int _lastSheetIndex;
        
        public RefSubRecord(final int extBookIndex, final int firstSheetIndex, final int lastSheetIndex) {
            this._extBookIndex = extBookIndex;
            this._firstSheetIndex = firstSheetIndex;
            this._lastSheetIndex = lastSheetIndex;
        }
        
        public RefSubRecord(final RefSubRecord other) {
            this._extBookIndex = other._extBookIndex;
            this._firstSheetIndex = other._firstSheetIndex;
            this._lastSheetIndex = other._lastSheetIndex;
        }
        
        public RefSubRecord(final RecordInputStream in) {
            this(in.readShort(), in.readShort(), in.readShort());
        }
        
        public void adjustIndex(final int offset) {
            this._firstSheetIndex += offset;
            this._lastSheetIndex += offset;
        }
        
        public int getExtBookIndex() {
            return this._extBookIndex;
        }
        
        public int getFirstSheetIndex() {
            return this._firstSheetIndex;
        }
        
        public int getLastSheetIndex() {
            return this._lastSheetIndex;
        }
        
        @Override
        public String toString() {
            final StringBuilder buffer = new StringBuilder();
            buffer.append("extBook=").append(this._extBookIndex);
            buffer.append(" firstSheet=").append(this._firstSheetIndex);
            buffer.append(" lastSheet=").append(this._lastSheetIndex);
            return buffer.toString();
        }
        
        public void serialize(final LittleEndianOutput out) {
            out.writeShort(this._extBookIndex);
            out.writeShort(this._firstSheetIndex);
            out.writeShort(this._lastSheetIndex);
        }
    }
}
