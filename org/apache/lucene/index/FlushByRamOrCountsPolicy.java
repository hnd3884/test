package org.apache.lucene.index;

class FlushByRamOrCountsPolicy extends FlushPolicy
{
    @Override
    public void onDelete(final DocumentsWriterFlushControl control, final DocumentsWriterPerThreadPool.ThreadState state) {
        if (this.flushOnDeleteTerms()) {
            final int maxBufferedDeleteTerms = this.indexWriterConfig.getMaxBufferedDeleteTerms();
            if (control.getNumGlobalTermDeletes() >= maxBufferedDeleteTerms) {
                control.setApplyAllDeletes();
            }
        }
        if (this.flushOnRAM() && control.getDeleteBytesUsed() > 1048576.0 * this.indexWriterConfig.getRAMBufferSizeMB()) {
            control.setApplyAllDeletes();
            if (this.infoStream.isEnabled("FP")) {
                this.infoStream.message("FP", "force apply deletes bytesUsed=" + control.getDeleteBytesUsed() + " vs ramBufferMB=" + this.indexWriterConfig.getRAMBufferSizeMB());
            }
        }
    }
    
    @Override
    public void onInsert(final DocumentsWriterFlushControl control, final DocumentsWriterPerThreadPool.ThreadState state) {
        if (this.flushOnDocCount() && state.dwpt.getNumDocsInRAM() >= this.indexWriterConfig.getMaxBufferedDocs()) {
            control.setFlushPending(state);
        }
        else if (this.flushOnRAM()) {
            final long limit = (long)(this.indexWriterConfig.getRAMBufferSizeMB() * 1024.0 * 1024.0);
            final long totalRam = control.activeBytes() + control.getDeleteBytesUsed();
            if (totalRam >= limit) {
                if (this.infoStream.isEnabled("FP")) {
                    this.infoStream.message("FP", "trigger flush: activeBytes=" + control.activeBytes() + " deleteBytes=" + control.getDeleteBytesUsed() + " vs limit=" + limit);
                }
                this.markLargestWriterPending(control, state, totalRam);
            }
        }
    }
    
    protected void markLargestWriterPending(final DocumentsWriterFlushControl control, final DocumentsWriterPerThreadPool.ThreadState perThreadState, final long currentBytesPerThread) {
        control.setFlushPending(this.findLargestNonPendingWriter(control, perThreadState));
    }
    
    protected boolean flushOnDocCount() {
        return this.indexWriterConfig.getMaxBufferedDocs() != -1;
    }
    
    protected boolean flushOnDeleteTerms() {
        return this.indexWriterConfig.getMaxBufferedDeleteTerms() != -1;
    }
    
    protected boolean flushOnRAM() {
        return this.indexWriterConfig.getRAMBufferSizeMB() != -1.0;
    }
}
