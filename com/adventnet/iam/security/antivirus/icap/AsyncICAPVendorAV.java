package com.adventnet.iam.security.antivirus.icap;

import com.adventnet.iam.security.antivirus.AVScanResult;
import java.io.File;
import com.adventnet.iam.security.antivirus.VendorAV;

public class AsyncICAPVendorAV extends VendorAV<File>
{
    private final ICAPVendorAV icapVendorAV;
    private boolean isAsyncScanInitialted;
    private boolean isAsyncResultArrived;
    
    private AsyncICAPVendorAV(final String scannerName, final ICAPVendorAV icapVendorAV) {
        super(scannerName);
        this.icapVendorAV = icapVendorAV;
    }
    
    public static AsyncICAPVendorAV newInstance(final IcapAvConfiguration icapConfig) {
        final ICAPVendorAV icapVendorAV = ICAPVendorAV.newAsyncInstance(icapConfig);
        return new AsyncICAPVendorAV(icapVendorAV.getScannerEngineName(), icapVendorAV);
    }
    
    public static AVScanResult<File> getAVScanResult(final AVScanResult<File> priorAVScanResult) {
        if (priorAVScanResult == null || !(priorAVScanResult.getScanner() instanceof AsyncICAPVendorAV)) {
            throw new IllegalArgumentException("Invalid prior AVScanResult");
        }
        final AsyncICAPVendorAV asyncIcapVendorAV = (AsyncICAPVendorAV)priorAVScanResult.getScanner();
        if (!asyncIcapVendorAV.isAsyncScanInitialted) {
            throw new IllegalStateException("Scan not yet initiated");
        }
        if (!asyncIcapVendorAV.isClosed()) {
            asyncIcapVendorAV.close();
        }
        asyncIcapVendorAV.isAsyncResultArrived = true;
        try {
            return asyncIcapVendorAV.scan(null);
        }
        finally {
            asyncIcapVendorAV.close();
        }
    }
    
    @Override
    protected AVScanResult<File> init() {
        if (this.isAsyncResultArrived) {
            return AVScanResult.completed();
        }
        return this.icapVendorAV.init();
    }
    
    @Override
    protected AVScanResult<File> scanImpl(final File file) {
        if (this.isAsyncResultArrived) {
            return ICAPVendorAV.getAsyncScanInitiatedResult(this.icapVendorAV);
        }
        this.isAsyncScanInitialted = true;
        return this.icapVendorAV.scanImpl(file);
    }
    
    @Override
    protected void closeImpl() {
        if (this.isAsyncResultArrived) {
            this.isAsyncResultArrived = false;
            this.isAsyncScanInitialted = false;
            this.icapVendorAV.closeImpl();
        }
    }
}
