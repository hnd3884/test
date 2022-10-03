package com.adventnet.iam.security.antivirus;

import org.json.JSONObject;
import java.io.Serializable;

public class AVScanResult<T> implements Serializable
{
    private VendorAV<T> vendorAV;
    private long totalExecutionTimer;
    private final Status status;
    private final String virusName;
    private final AVScanFailureInfo scanFailureInfo;
    
    private AVScanResult(final Status status, final String virusName, final AVScanFailureInfo scanFailureInfo) {
        this.status = status;
        this.virusName = virusName;
        this.scanFailureInfo = scanFailureInfo;
    }
    
    protected void finalize(final VendorAV<T> vendorAV, final long executionTime) {
        this.vendorAV = vendorAV;
        this.totalExecutionTimer = executionTime;
    }
    
    public long getTotalTime() {
        return this.totalExecutionTimer;
    }
    
    public Status status() {
        return this.status;
    }
    
    public VendorAV<T> getScanner() {
        return this.vendorAV;
    }
    
    public String getScanEngineName() {
        if (this.vendorAV == null) {
            return "";
        }
        return this.vendorAV.getScannerEngineName();
    }
    
    public String getDetectedVirusName() {
        return this.virusName;
    }
    
    public AVScanFailureInfo getScanFailureInfo() {
        return this.scanFailureInfo;
    }
    
    public static <T> AVScanResult<T> completed() {
        return new AVScanResult<T>(Status.COMPLETED, null, null);
    }
    
    public static <T> AVScanResult<T> skipped() {
        return new AVScanResult<T>(Status.SKIPPED, null, null);
    }
    
    public static <T> AVScanResult<T> failed(final AVScanFailureInfo.FailedCause cause, final String message) {
        return new AVScanResult<T>(Status.FAILED, null, new AVScanFailureInfo(cause, message));
    }
    
    public static <T> AVScanResult<T> virusDetected(final String virusName) {
        return new AVScanResult<T>(Status.VIRUS_DETECTED, virusName, null);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Antivirus Scan Status: ").append(this.status.name());
        sb.append("\tScanner Engine Name: ").append(this.getScanEngineName());
        if (this.status == Status.VIRUS_DETECTED) {
            sb.append("\tDetected Virus Name: ").append(this.virusName);
        }
        if (this.status == Status.FAILED) {
            sb.append("\tScan Failure Info: ").append(this.scanFailureInfo.toString());
        }
        sb.append("\tTotal Execution Time: ").append(this.getTotalTime()).append(" ms");
        return sb.toString();
    }
    
    public JSONObject toJson() {
        final JSONObject jsonObj = new JSONObject();
        jsonObj.put(JsonKeys.SCAN_STATUS.name(), (Object)this.status);
        jsonObj.put(JsonKeys.SCAN_ENGINE_NAME.name(), (Object)this.getScanEngineName());
        if (this.status == Status.VIRUS_DETECTED) {
            jsonObj.put(JsonKeys.DETECTED_VIRUS_NAME.name(), (Object)this.virusName);
        }
        if (this.status == Status.FAILED) {
            jsonObj.put(JsonKeys.SCAN_FAILURE_INFO.name(), (Object)this.scanFailureInfo.toJson());
        }
        jsonObj.put(JsonKeys.SCAN_EXECUTION_TIME_IN_MS.name(), this.getTotalTime());
        return jsonObj;
    }
    
    public enum Status
    {
        VIRUS_DETECTED, 
        SKIPPED, 
        FAILED, 
        COMPLETED;
    }
    
    public enum JsonKeys
    {
        SCAN_STATUS, 
        SCAN_ENGINE_NAME, 
        DETECTED_VIRUS_NAME, 
        SCAN_FAILURE_INFO, 
        SCAN_EXECUTION_TIME_IN_MS;
    }
}
