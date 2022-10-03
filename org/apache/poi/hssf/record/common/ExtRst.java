package org.apache.poi.hssf.record.common;

import org.apache.poi.util.POILogFactory;
import java.util.Arrays;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.hssf.record.cont.ContinuableRecordOutput;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.LittleEndianInput;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Internal;

@Internal
public class ExtRst implements Comparable<ExtRst>
{
    private static final POILogger _logger;
    private static final int MAX_RECORD_LENGTH = 100000;
    private short reserved;
    private short formattingFontIndex;
    private short formattingOptions;
    private int numberOfRuns;
    private String phoneticText;
    private PhRun[] phRuns;
    private byte[] extraData;
    
    protected ExtRst() {
        this.populateEmpty();
    }
    
    protected ExtRst(final ExtRst other) {
        this.reserved = other.reserved;
        this.formattingFontIndex = other.formattingFontIndex;
        this.formattingOptions = other.formattingOptions;
        this.numberOfRuns = other.numberOfRuns;
        this.phoneticText = other.phoneticText;
        this.phRuns = (PhRun[])((other.phRuns == null) ? null : ((PhRun[])Stream.of(other.phRuns).map((Function<? super PhRun, ?>)PhRun::new).toArray(PhRun[]::new)));
    }
    
    protected ExtRst(final LittleEndianInput in, final int expectedLength) {
        this.reserved = in.readShort();
        if (this.reserved == -1) {
            this.populateEmpty();
            return;
        }
        if (this.reserved != 1) {
            ExtRst._logger.log(5, "Warning - ExtRst has wrong magic marker, expecting 1 but found " + this.reserved + " - ignoring");
            for (int i = 0; i < expectedLength - 2; ++i) {
                in.readByte();
            }
            this.populateEmpty();
            return;
        }
        final short stringDataSize = in.readShort();
        this.formattingFontIndex = in.readShort();
        this.formattingOptions = in.readShort();
        this.numberOfRuns = in.readUShort();
        final short length1 = in.readShort();
        short length2 = in.readShort();
        if (length1 == 0 && length2 > 0) {
            length2 = 0;
        }
        if (length1 != length2) {
            throw new IllegalStateException("The two length fields of the Phonetic Text don't agree! " + length1 + " vs " + length2);
        }
        this.phoneticText = StringUtil.readUnicodeLE(in, length1);
        final int runData = stringDataSize - 4 - 6 - 2 * this.phoneticText.length();
        final int numRuns = runData / 6;
        this.phRuns = new PhRun[numRuns];
        for (int j = 0; j < this.phRuns.length; ++j) {
            this.phRuns[j] = new PhRun(in);
        }
        int extraDataLength = runData - numRuns * 6;
        if (extraDataLength < 0) {
            ExtRst._logger.log(5, "Warning - ExtRst overran by " + (0 - extraDataLength) + " bytes");
            extraDataLength = 0;
        }
        this.extraData = IOUtils.safelyAllocate(extraDataLength, 100000);
        for (int k = 0; k < this.extraData.length; ++k) {
            this.extraData[k] = in.readByte();
        }
    }
    
    private void populateEmpty() {
        this.reserved = 1;
        this.phoneticText = "";
        this.phRuns = new PhRun[0];
        this.extraData = new byte[0];
    }
    
    protected int getDataSize() {
        return 10 + 2 * this.phoneticText.length() + 6 * this.phRuns.length + this.extraData.length;
    }
    
    protected void serialize(final ContinuableRecordOutput out) {
        final int dataSize = this.getDataSize();
        out.writeContinueIfRequired(8);
        out.writeShort(this.reserved);
        out.writeShort(dataSize);
        out.writeShort(this.formattingFontIndex);
        out.writeShort(this.formattingOptions);
        out.writeContinueIfRequired(6);
        out.writeShort(this.numberOfRuns);
        out.writeShort(this.phoneticText.length());
        out.writeShort(this.phoneticText.length());
        out.writeContinueIfRequired(this.phoneticText.length() * 2);
        StringUtil.putUnicodeLE(this.phoneticText, out);
        for (int i = 0; i < this.phRuns.length; ++i) {
            this.phRuns[i].serialize(out);
        }
        out.write(this.extraData);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ExtRst)) {
            return false;
        }
        final ExtRst other = (ExtRst)obj;
        return this.compareTo(other) == 0;
    }
    
    @Override
    public int compareTo(final ExtRst o) {
        int result = this.reserved - o.reserved;
        if (result != 0) {
            return result;
        }
        result = this.formattingFontIndex - o.formattingFontIndex;
        if (result != 0) {
            return result;
        }
        result = this.formattingOptions - o.formattingOptions;
        if (result != 0) {
            return result;
        }
        result = this.numberOfRuns - o.numberOfRuns;
        if (result != 0) {
            return result;
        }
        result = this.phoneticText.compareTo(o.phoneticText);
        if (result != 0) {
            return result;
        }
        result = this.phRuns.length - o.phRuns.length;
        if (result != 0) {
            return result;
        }
        for (int i = 0; i < this.phRuns.length; ++i) {
            result = this.phRuns[i].phoneticTextFirstCharacterOffset - o.phRuns[i].phoneticTextFirstCharacterOffset;
            if (result != 0) {
                return result;
            }
            result = this.phRuns[i].realTextFirstCharacterOffset - o.phRuns[i].realTextFirstCharacterOffset;
            if (result != 0) {
                return result;
            }
            result = this.phRuns[i].realTextLength - o.phRuns[i].realTextLength;
            if (result != 0) {
                return result;
            }
        }
        result = Arrays.hashCode(this.extraData) - Arrays.hashCode(o.extraData);
        return result;
    }
    
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(new Object[] { this.reserved, this.formattingFontIndex, this.formattingOptions, this.numberOfRuns, this.phoneticText, this.phRuns });
    }
    
    public ExtRst copy() {
        return new ExtRst(this);
    }
    
    public short getFormattingFontIndex() {
        return this.formattingFontIndex;
    }
    
    public short getFormattingOptions() {
        return this.formattingOptions;
    }
    
    public int getNumberOfRuns() {
        return this.numberOfRuns;
    }
    
    public String getPhoneticText() {
        return this.phoneticText;
    }
    
    public PhRun[] getPhRuns() {
        return this.phRuns;
    }
    
    static {
        _logger = POILogFactory.getLogger(ExtRst.class);
    }
}
