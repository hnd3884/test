package org.apache.lucene.index;

import java.io.IOException;

public class SerialMergeScheduler extends MergeScheduler
{
    @Override
    public synchronized void merge(final IndexWriter writer, final MergeTrigger trigger, final boolean newMergesFound) throws IOException {
        while (true) {
            final MergePolicy.OneMerge merge = writer.getNextMerge();
            if (merge == null) {
                break;
            }
            writer.merge(merge);
        }
    }
    
    @Override
    public void close() {
    }
}
