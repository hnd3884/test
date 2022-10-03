package org.apache.lucene.index;

import java.util.Iterator;
import org.apache.lucene.util.InfoStream;

abstract class FlushPolicy
{
    protected LiveIndexWriterConfig indexWriterConfig;
    protected InfoStream infoStream;
    
    public abstract void onDelete(final DocumentsWriterFlushControl p0, final DocumentsWriterPerThreadPool.ThreadState p1);
    
    public void onUpdate(final DocumentsWriterFlushControl control, final DocumentsWriterPerThreadPool.ThreadState state) {
        this.onInsert(control, state);
        this.onDelete(control, state);
    }
    
    public abstract void onInsert(final DocumentsWriterFlushControl p0, final DocumentsWriterPerThreadPool.ThreadState p1);
    
    protected synchronized void init(final LiveIndexWriterConfig indexWriterConfig) {
        this.indexWriterConfig = indexWriterConfig;
        this.infoStream = indexWriterConfig.getInfoStream();
    }
    
    protected DocumentsWriterPerThreadPool.ThreadState findLargestNonPendingWriter(final DocumentsWriterFlushControl control, final DocumentsWriterPerThreadPool.ThreadState perThreadState) {
        assert perThreadState.dwpt.getNumDocsInRAM() > 0;
        long maxRamSoFar = perThreadState.bytesUsed;
        DocumentsWriterPerThreadPool.ThreadState maxRamUsingThreadState = perThreadState;
        assert !perThreadState.flushPending : "DWPT should have flushed";
        final Iterator<DocumentsWriterPerThreadPool.ThreadState> activePerThreadsIterator = control.allActiveThreadStates();
        int count = 0;
        while (activePerThreadsIterator.hasNext()) {
            final DocumentsWriterPerThreadPool.ThreadState next = activePerThreadsIterator.next();
            if (!next.flushPending) {
                final long nextRam = next.bytesUsed;
                if (nextRam <= 0L || next.dwpt.getNumDocsInRAM() <= 0) {
                    continue;
                }
                if (this.infoStream.isEnabled("FP")) {
                    this.infoStream.message("FP", "thread state has " + nextRam + " bytes; docInRAM=" + next.dwpt.getNumDocsInRAM());
                }
                ++count;
                if (nextRam <= maxRamSoFar) {
                    continue;
                }
                maxRamSoFar = nextRam;
                maxRamUsingThreadState = next;
            }
        }
        if (this.infoStream.isEnabled("FP")) {
            this.infoStream.message("FP", count + " in-use non-flushing threads states");
        }
        assert this.assertMessage("set largest ram consuming thread pending on lower watermark");
        return maxRamUsingThreadState;
    }
    
    private boolean assertMessage(final String s) {
        if (this.infoStream.isEnabled("FP")) {
            this.infoStream.message("FP", s);
        }
        return true;
    }
}
