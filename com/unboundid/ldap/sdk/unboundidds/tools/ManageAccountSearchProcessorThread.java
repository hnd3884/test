package com.unboundid.ldap.sdk.unboundidds.tools;

final class ManageAccountSearchProcessorThread extends Thread
{
    private final ManageAccountSearchProcessor searchProcessor;
    private volatile ManageAccountSearchOperation activeSearchOperation;
    
    ManageAccountSearchProcessorThread(final int threadNumber, final ManageAccountSearchProcessor searchProcessor) {
        this.setName("manage-account Search Processor Thread " + threadNumber);
        this.searchProcessor = searchProcessor;
        this.activeSearchOperation = null;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                this.activeSearchOperation = this.searchProcessor.getSearchOperation();
                if (this.activeSearchOperation == null) {
                    return;
                }
                this.activeSearchOperation.doSearch();
            }
            finally {
                this.activeSearchOperation = null;
            }
        }
    }
    
    void cancelSearch() {
        final ManageAccountSearchOperation o = this.activeSearchOperation;
        if (o != null) {
            o.cancelSearch();
        }
    }
}
