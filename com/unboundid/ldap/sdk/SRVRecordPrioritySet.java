package com.unboundid.ldap.sdk;

import java.util.Random;
import com.unboundid.util.ThreadLocalRandom;
import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class SRVRecordPrioritySet implements Serializable
{
    private static final long serialVersionUID = -7722028520625558942L;
    private final long priority;
    private final long totalWeight;
    private final List<SRVRecord> allRecords;
    private final List<SRVRecord> nonzeroWeightRecords;
    private final List<SRVRecord> zeroWeightRecords;
    
    SRVRecordPrioritySet(final long priority, final List<SRVRecord> records) {
        this.priority = priority;
        long w = 0L;
        final ArrayList<SRVRecord> nRecords = new ArrayList<SRVRecord>(records.size());
        final ArrayList<SRVRecord> zRecords = new ArrayList<SRVRecord>(records.size());
        for (final SRVRecord r : records) {
            if (r.getWeight() == 0L) {
                zRecords.add(r);
            }
            else {
                nRecords.add(r);
                w += r.getWeight();
            }
        }
        this.totalWeight = w;
        this.allRecords = Collections.unmodifiableList((List<? extends SRVRecord>)records);
        this.nonzeroWeightRecords = Collections.unmodifiableList((List<? extends SRVRecord>)nRecords);
        this.zeroWeightRecords = Collections.unmodifiableList((List<? extends SRVRecord>)zRecords);
    }
    
    long getPriority() {
        return this.priority;
    }
    
    List<SRVRecord> getOrderedRecords() {
        final ArrayList<SRVRecord> records = new ArrayList<SRVRecord>(this.allRecords.size());
        if (!this.nonzeroWeightRecords.isEmpty()) {
            if (this.nonzeroWeightRecords.size() == 1) {
                records.addAll(this.nonzeroWeightRecords);
            }
            else {
                final Random r = ThreadLocalRandom.get();
                long tw = this.totalWeight;
                final ArrayList<SRVRecord> rl = new ArrayList<SRVRecord>(this.nonzeroWeightRecords);
                while (!rl.isEmpty()) {
                    long w = (r.nextLong() & Long.MAX_VALUE) % tw;
                    final Iterator<SRVRecord> iterator = rl.iterator();
                    while (iterator.hasNext()) {
                        final SRVRecord record = iterator.next();
                        if (w < record.getWeight() || !iterator.hasNext()) {
                            iterator.remove();
                            records.add(record);
                            tw -= record.getWeight();
                            break;
                        }
                        w -= record.getWeight();
                    }
                }
            }
        }
        records.addAll(this.zeroWeightRecords);
        return records;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    private void toString(final StringBuilder buffer) {
        buffer.append("SRVRecordPrioritySet(records={");
        final Iterator<SRVRecord> iterator = this.allRecords.iterator();
        while (iterator.hasNext()) {
            buffer.append(iterator.next().toString());
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("})");
    }
}
