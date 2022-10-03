package org.apache.poi.hssf.model;

import org.apache.poi.hssf.record.DimensionsRecord;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.aggregates.ColumnInfoRecordsAggregate;
import org.apache.poi.hssf.record.aggregates.WorksheetProtectionBlock;
import org.apache.poi.hssf.record.aggregates.PageSettingsBlock;
import org.apache.poi.hssf.record.GutsRecord;
import org.apache.poi.hssf.record.aggregates.ConditionalFormattingTable;
import org.apache.poi.hssf.record.aggregates.MergedCellsTable;
import org.apache.poi.hssf.record.aggregates.DataValidityTable;
import org.apache.poi.hssf.record.RecordBase;
import java.util.List;

final class RecordOrderer
{
    private RecordOrderer() {
    }
    
    public static void addNewSheetRecord(final List<RecordBase> sheetRecords, final RecordBase newRecord) {
        final int index = findSheetInsertPos(sheetRecords, newRecord.getClass());
        sheetRecords.add(index, newRecord);
    }
    
    private static int findSheetInsertPos(final List<RecordBase> records, final Class<? extends RecordBase> recClass) {
        if (recClass == DataValidityTable.class) {
            return findDataValidationTableInsertPos(records);
        }
        if (recClass == MergedCellsTable.class) {
            return findInsertPosForNewMergedRecordTable(records);
        }
        if (recClass == ConditionalFormattingTable.class) {
            return findInsertPosForNewCondFormatTable(records);
        }
        if (recClass == GutsRecord.class) {
            return getGutsRecordInsertPos(records);
        }
        if (recClass == PageSettingsBlock.class) {
            return getPageBreakRecordInsertPos(records);
        }
        if (recClass == WorksheetProtectionBlock.class) {
            return getWorksheetProtectionBlockInsertPos(records);
        }
        throw new RuntimeException("Unexpected record class (" + recClass.getName() + ")");
    }
    
    private static int getWorksheetProtectionBlockInsertPos(final List<RecordBase> records) {
        int i = getDimensionsIndex(records);
        while (i > 0) {
            --i;
            final Object rb = records.get(i);
            if (!isProtectionSubsequentRecord(rb)) {
                return i + 1;
            }
        }
        throw new IllegalStateException("did not find insert pos for protection block");
    }
    
    private static boolean isProtectionSubsequentRecord(final Object rb) {
        if (rb instanceof ColumnInfoRecordsAggregate) {
            return true;
        }
        if (rb instanceof Record) {
            final Record record = (Record)rb;
            switch (record.getSid()) {
                case 85:
                case 144: {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static int getPageBreakRecordInsertPos(final List<RecordBase> records) {
        final int dimensionsIndex = getDimensionsIndex(records);
        int i = dimensionsIndex - 1;
        while (i > 0) {
            --i;
            final Object rb = records.get(i);
            if (isPageBreakPriorRecord(rb)) {
                return i + 1;
            }
        }
        throw new RuntimeException("Did not find insert point for GUTS");
    }
    
    private static boolean isPageBreakPriorRecord(final Object rb) {
        if (rb instanceof Record) {
            final Record record = (Record)rb;
            switch (record.getSid()) {
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 34:
                case 42:
                case 43:
                case 94:
                case 95:
                case 129:
                case 130:
                case 523:
                case 549:
                case 2057: {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static int findInsertPosForNewCondFormatTable(final List<RecordBase> records) {
        for (int i = records.size() - 2; i >= 0; --i) {
            final Object rb = records.get(i);
            if (rb instanceof MergedCellsTable) {
                return i + 1;
            }
            if (!(rb instanceof DataValidityTable)) {
                final Record rec = (Record)rb;
                switch (rec.getSid()) {
                    case 29:
                    case 65:
                    case 153:
                    case 160:
                    case 239:
                    case 351:
                    case 574: {
                        return i + 1;
                    }
                }
            }
        }
        throw new RuntimeException("Did not find Window2 record");
    }
    
    private static int findInsertPosForNewMergedRecordTable(final List<RecordBase> records) {
        for (int i = records.size() - 2; i >= 0; --i) {
            final Object rb = records.get(i);
            if (rb instanceof Record) {
                final Record rec = (Record)rb;
                switch (rec.getSid()) {
                    case 29:
                    case 65:
                    case 153:
                    case 160:
                    case 574: {
                        return i + 1;
                    }
                }
            }
        }
        throw new RuntimeException("Did not find Window2 record");
    }
    
    private static int findDataValidationTableInsertPos(final List<RecordBase> records) {
        int i = records.size() - 1;
        if (!(records.get(i) instanceof EOFRecord)) {
            throw new IllegalStateException("Last sheet record should be EOFRecord");
        }
        while (i > 0) {
            --i;
            final RecordBase rb = records.get(i);
            if (isDVTPriorRecord(rb)) {
                final Record nextRec = records.get(i + 1);
                if (!isDVTSubsequentRecord(nextRec.getSid())) {
                    throw new IllegalStateException("Unexpected (" + nextRec.getClass().getName() + ") found after (" + rb.getClass().getName() + ")");
                }
                return i + 1;
            }
            else {
                final Record rec = (Record)rb;
                if (!isDVTSubsequentRecord(rec.getSid())) {
                    throw new IllegalStateException("Unexpected (" + rec.getClass().getName() + ") while looking for DV Table insert pos");
                }
                continue;
            }
        }
        return 0;
    }
    
    private static boolean isDVTPriorRecord(final RecordBase rb) {
        if (rb instanceof MergedCellsTable || rb instanceof ConditionalFormattingTable) {
            return true;
        }
        final short sid = ((Record)rb).getSid();
        switch (sid) {
            case 29:
            case 65:
            case 153:
            case 160:
            case 239:
            case 351:
            case 440:
            case 442:
            case 574:
            case 2048: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static boolean isDVTSubsequentRecord(final short sid) {
        switch (sid) {
            case 10:
            case 2146:
            case 2151:
            case 2152:
            case 2248: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static int getDimensionsIndex(final List<RecordBase> records) {
        for (int nRecs = records.size(), i = 0; i < nRecs; ++i) {
            if (records.get(i) instanceof DimensionsRecord) {
                return i;
            }
        }
        throw new RuntimeException("DimensionsRecord not found");
    }
    
    private static int getGutsRecordInsertPos(final List<RecordBase> records) {
        final int dimensionsIndex = getDimensionsIndex(records);
        int i = dimensionsIndex - 1;
        while (i > 0) {
            --i;
            final RecordBase rb = records.get(i);
            if (isGutsPriorRecord(rb)) {
                return i + 1;
            }
        }
        throw new RuntimeException("Did not find insert point for GUTS");
    }
    
    private static boolean isGutsPriorRecord(final RecordBase rb) {
        if (rb instanceof Record) {
            final Record record = (Record)rb;
            switch (record.getSid()) {
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 34:
                case 42:
                case 43:
                case 94:
                case 95:
                case 130:
                case 523:
                case 2057: {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean isEndOfRowBlock(final int sid) {
        switch (sid) {
            case 61:
            case 93:
            case 125:
            case 128:
            case 176:
            case 236:
            case 237:
            case 438:
            case 574: {
                return true;
            }
            case 434: {
                return true;
            }
            case 10: {
                throw new RuntimeException("Found EOFRecord before WindowTwoRecord was encountered");
            }
            default: {
                return PageSettingsBlock.isComponentRecord(sid);
            }
        }
    }
    
    public static boolean isRowBlockRecord(final int sid) {
        switch (sid) {
            case 6:
            case 253:
            case 513:
            case 515:
            case 516:
            case 517:
            case 520:
            case 545:
            case 566:
            case 638:
            case 1212: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
