package org.apache.poi.hssf.eventusermodel;

import org.apache.poi.hssf.record.Record;

public interface HSSFListener
{
    void processRecord(final Record p0);
}
