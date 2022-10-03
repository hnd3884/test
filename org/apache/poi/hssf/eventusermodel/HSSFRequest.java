package org.apache.poi.hssf.eventusermodel;

import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HSSFRequest
{
    private final Map<Short, List<HSSFListener>> _records;
    
    public HSSFRequest() {
        this._records = new HashMap<Short, List<HSSFListener>>(50);
    }
    
    public void addListener(final HSSFListener lsnr, final short sid) {
        final List<HSSFListener> list = this._records.computeIfAbsent(Short.valueOf(sid), k -> new ArrayList(1));
        list.add(lsnr);
    }
    
    public void addListenerForAllRecords(final HSSFListener lsnr) {
        final short[] allKnownRecordSIDs;
        final short[] rectypes = allKnownRecordSIDs = RecordFactory.getAllKnownRecordSIDs();
        for (final short rectype : allKnownRecordSIDs) {
            this.addListener(lsnr, rectype);
        }
    }
    
    protected short processRecord(final Record rec) throws HSSFUserException {
        final List<HSSFListener> listeners = this._records.get(rec.getSid());
        short userCode = 0;
        if (listeners != null) {
            for (int k = 0; k < listeners.size(); ++k) {
                final Object listenObj = listeners.get(k);
                if (listenObj instanceof AbortableHSSFListener) {
                    final AbortableHSSFListener listener = (AbortableHSSFListener)listenObj;
                    userCode = listener.abortableProcessRecord(rec);
                    if (userCode != 0) {
                        break;
                    }
                }
                else {
                    final HSSFListener listener2 = (HSSFListener)listenObj;
                    listener2.processRecord(rec);
                }
            }
        }
        return userCode;
    }
}
