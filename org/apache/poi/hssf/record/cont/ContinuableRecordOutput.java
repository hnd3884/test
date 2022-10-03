package org.apache.poi.hssf.record.cont;

import org.apache.poi.util.DelayableLittleEndianOutput;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class ContinuableRecordOutput implements LittleEndianOutput
{
    private final LittleEndianOutput _out;
    private UnknownLengthRecordOutput _ulrOutput;
    private int _totalPreviousRecordsSize;
    private static final LittleEndianOutput NOPOutput;
    
    public ContinuableRecordOutput(final LittleEndianOutput out, final int sid) {
        this._ulrOutput = new UnknownLengthRecordOutput(out, sid);
        this._out = out;
        this._totalPreviousRecordsSize = 0;
    }
    
    public static ContinuableRecordOutput createForCountingOnly() {
        return new ContinuableRecordOutput(ContinuableRecordOutput.NOPOutput, -777);
    }
    
    public int getTotalSize() {
        return this._totalPreviousRecordsSize + this._ulrOutput.getTotalSize();
    }
    
    void terminate() {
        this._ulrOutput.terminate();
    }
    
    public int getAvailableSpace() {
        return this._ulrOutput.getAvailableSpace();
    }
    
    public void writeContinue() {
        this._ulrOutput.terminate();
        this._totalPreviousRecordsSize += this._ulrOutput.getTotalSize();
        this._ulrOutput = new UnknownLengthRecordOutput(this._out, 60);
    }
    
    public void writeContinueIfRequired(final int requiredContinuousSize) {
        if (this._ulrOutput.getAvailableSpace() < requiredContinuousSize) {
            this.writeContinue();
        }
    }
    
    public void writeStringData(final String text) {
        final boolean is16bitEncoded = StringUtil.hasMultibyte(text);
        int keepTogetherSize = 2;
        int optionFlags = 0;
        if (is16bitEncoded) {
            optionFlags |= 0x1;
            ++keepTogetherSize;
        }
        this.writeContinueIfRequired(keepTogetherSize);
        this.writeByte(optionFlags);
        this.writeCharacterData(text, is16bitEncoded);
    }
    
    public void writeString(final String text, final int numberOfRichTextRuns, final int extendedDataSize) {
        final boolean is16bitEncoded = StringUtil.hasMultibyte(text);
        int keepTogetherSize = 4;
        int optionFlags = 0;
        if (is16bitEncoded) {
            optionFlags |= 0x1;
            ++keepTogetherSize;
        }
        if (numberOfRichTextRuns > 0) {
            optionFlags |= 0x8;
            keepTogetherSize += 2;
        }
        if (extendedDataSize > 0) {
            optionFlags |= 0x4;
            keepTogetherSize += 4;
        }
        this.writeContinueIfRequired(keepTogetherSize);
        this.writeShort(text.length());
        this.writeByte(optionFlags);
        if (numberOfRichTextRuns > 0) {
            this.writeShort(numberOfRichTextRuns);
        }
        if (extendedDataSize > 0) {
            this.writeInt(extendedDataSize);
        }
        this.writeCharacterData(text, is16bitEncoded);
    }
    
    private void writeCharacterData(final String text, final boolean is16bitEncoded) {
        final int nChars = text.length();
        int i = 0;
        if (is16bitEncoded) {
            while (true) {
                for (int nWritableChars = Math.min(nChars - i, this._ulrOutput.getAvailableSpace() / 2); nWritableChars > 0; --nWritableChars) {
                    this._ulrOutput.writeShort(text.charAt(i++));
                }
                if (i >= nChars) {
                    break;
                }
                this.writeContinue();
                this.writeByte(1);
            }
        }
        else {
            while (true) {
                for (int nWritableChars = Math.min(nChars - i, this._ulrOutput.getAvailableSpace() / 1); nWritableChars > 0; --nWritableChars) {
                    this._ulrOutput.writeByte(text.charAt(i++));
                }
                if (i >= nChars) {
                    break;
                }
                this.writeContinue();
                this.writeByte(0);
            }
        }
    }
    
    @Override
    public void write(final byte[] b) {
        this.writeContinueIfRequired(b.length);
        this._ulrOutput.write(b);
    }
    
    @Override
    public void write(final byte[] b, final int offset, final int len) {
        int i = 0;
        while (true) {
            for (int nWritableChars = Math.min(len - i, this._ulrOutput.getAvailableSpace() / 1); nWritableChars > 0; --nWritableChars) {
                this._ulrOutput.writeByte(b[offset + i++]);
            }
            if (i >= len) {
                break;
            }
            this.writeContinue();
        }
    }
    
    @Override
    public void writeByte(final int v) {
        this.writeContinueIfRequired(1);
        this._ulrOutput.writeByte(v);
    }
    
    @Override
    public void writeDouble(final double v) {
        this.writeContinueIfRequired(8);
        this._ulrOutput.writeDouble(v);
    }
    
    @Override
    public void writeInt(final int v) {
        this.writeContinueIfRequired(4);
        this._ulrOutput.writeInt(v);
    }
    
    @Override
    public void writeLong(final long v) {
        this.writeContinueIfRequired(8);
        this._ulrOutput.writeLong(v);
    }
    
    @Override
    public void writeShort(final int v) {
        this.writeContinueIfRequired(2);
        this._ulrOutput.writeShort(v);
    }
    
    static {
        NOPOutput = new DelayableLittleEndianOutput() {
            @Override
            public LittleEndianOutput createDelayedOutput(final int size) {
                return this;
            }
            
            @Override
            public void write(final byte[] b) {
            }
            
            @Override
            public void write(final byte[] b, final int offset, final int len) {
            }
            
            @Override
            public void writeByte(final int v) {
            }
            
            @Override
            public void writeDouble(final double v) {
            }
            
            @Override
            public void writeInt(final int v) {
            }
            
            @Override
            public void writeLong(final long v) {
            }
            
            @Override
            public void writeShort(final int v) {
            }
        };
    }
}
