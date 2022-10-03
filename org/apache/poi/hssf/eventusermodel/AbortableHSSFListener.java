package org.apache.poi.hssf.eventusermodel;

import org.apache.poi.hssf.record.Record;

public abstract class AbortableHSSFListener implements HSSFListener
{
    @Override
    public void processRecord(final Record record) {
    }
    
    public abstract short abortableProcessRecord(final Record p0) throws HSSFUserException;
}
