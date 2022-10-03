package com.adventnet.iam.security.antivirus;

import com.zoho.security.eventfw.ExecutionTimer;
import java.io.Serializable;

public abstract class VendorAV<T> implements AutoCloseable, Serializable
{
    private transient boolean isClosed;
    private final String scannerEngineName;
    
    protected VendorAV(final String scannerEngineName) {
        this.isClosed = true;
        this.scannerEngineName = scannerEngineName;
    }
    
    protected abstract AVScanResult<T> init();
    
    protected abstract AVScanResult<T> scanImpl(final T p0);
    
    protected abstract void closeImpl();
    
    public AVScanResult<T> scan(final T objectToScan) {
        if (!this.isClosed) {
            throw new IllegalStateException("AV Vendor scanner not closed");
        }
        this.isClosed = false;
        final ExecutionTimer timer = ExecutionTimer.startInstance();
        AVScanResult<T> result = this.init();
        if (result.status() == AVScanResult.Status.COMPLETED) {
            result = this.scanImpl(objectToScan);
        }
        timer.stop();
        result.finalize(this, timer.getExecutionTime());
        return result;
    }
    
    @Override
    public void close() {
        if (!this.isClosed) {
            this.isClosed = true;
            this.closeImpl();
        }
    }
    
    public String getScannerEngineName() {
        return this.scannerEngineName;
    }
    
    public boolean isClosed() {
        return this.isClosed;
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.close();
    }
}
