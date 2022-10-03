package org.apache.poi.hssf.record.common;

import java.util.Objects;
import org.apache.poi.hssf.record.cont.ContinuableRecordOutput;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.Internal;

@Internal
public class PhRun
{
    final int phoneticTextFirstCharacterOffset;
    final int realTextFirstCharacterOffset;
    final int realTextLength;
    
    public PhRun(final PhRun other) {
        this.phoneticTextFirstCharacterOffset = other.phoneticTextFirstCharacterOffset;
        this.realTextFirstCharacterOffset = other.realTextFirstCharacterOffset;
        this.realTextLength = other.realTextLength;
    }
    
    public PhRun(final int phoneticTextFirstCharacterOffset, final int realTextFirstCharacterOffset, final int realTextLength) {
        this.phoneticTextFirstCharacterOffset = phoneticTextFirstCharacterOffset;
        this.realTextFirstCharacterOffset = realTextFirstCharacterOffset;
        this.realTextLength = realTextLength;
    }
    
    PhRun(final LittleEndianInput in) {
        this.phoneticTextFirstCharacterOffset = in.readUShort();
        this.realTextFirstCharacterOffset = in.readUShort();
        this.realTextLength = in.readUShort();
    }
    
    void serialize(final ContinuableRecordOutput out) {
        out.writeContinueIfRequired(6);
        out.writeShort(this.phoneticTextFirstCharacterOffset);
        out.writeShort(this.realTextFirstCharacterOffset);
        out.writeShort(this.realTextLength);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.phoneticTextFirstCharacterOffset, this.realTextFirstCharacterOffset, this.realTextLength);
    }
}
