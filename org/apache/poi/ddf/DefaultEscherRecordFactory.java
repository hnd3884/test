package org.apache.poi.ddf;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.Removal;
import java.util.function.Supplier;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.BitField;

public class DefaultEscherRecordFactory implements EscherRecordFactory
{
    private static final BitField IS_CONTAINER;
    
    @Override
    public EscherRecord createRecord(final byte[] data, final int offset) {
        final short options = LittleEndian.getShort(data, offset);
        final short recordId = LittleEndian.getShort(data, offset + 2);
        final EscherRecord escherRecord = (EscherRecord)this.getConstructor(options, recordId).get();
        escherRecord.setRecordId(recordId);
        escherRecord.setOptions(options);
        return escherRecord;
    }
    
    protected Supplier<? extends EscherRecord> getConstructor(final short options, final short recordId) {
        final EscherRecordTypes recordTypes = EscherRecordTypes.forTypeID(recordId);
        if (recordTypes == EscherRecordTypes.UNKNOWN && DefaultEscherRecordFactory.IS_CONTAINER.isAllSet(options)) {
            return EscherContainerRecord::new;
        }
        if (recordTypes.constructor != null) {
            return recordTypes.constructor;
        }
        if (EscherBlipRecord.RECORD_ID_START <= recordId && recordId <= EscherBlipRecord.RECORD_ID_END) {
            return EscherBlipRecord::new;
        }
        return UnknownEscherRecord::new;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public static boolean isContainer(final short options, final short recordId) {
        return (recordId >= EscherContainerRecord.DGG_CONTAINER && recordId <= EscherContainerRecord.SOLVER_CONTAINER) || (recordId != EscherTextboxRecord.RECORD_ID && (options & 0xF) == 0xF);
    }
    
    static {
        IS_CONTAINER = BitFieldFactory.getInstance(15);
    }
}
