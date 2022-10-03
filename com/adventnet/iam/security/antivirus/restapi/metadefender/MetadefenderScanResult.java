package com.adventnet.iam.security.antivirus.restapi.metadefender;

import java.util.HashMap;
import java.util.Map;

public enum MetadefenderScanResult
{
    SCAN_NOT_STARTED(-1, "Scan Not Started"), 
    NO_THREAD_FOUND(0, "No Threats Found"), 
    INFECTED(1, "Infected/Known"), 
    SUSPICIOUS(2, "Suspicious"), 
    FAILED_TO_SCAN(3, "Failed To Scan"), 
    CLEANED(4, "Cleaned / Deleted"), 
    UNKNOWN(5, "Unknown"), 
    QUARANTINED(6, "Quarantined"), 
    SKIPPED_CLEAN(7, "Skipped Clean"), 
    SKIPPED_INFECTED(8, "Skipped Infected"), 
    EXCEEDED_ARCHIVE_DEPTH(9, "Exceeded Archive Depth"), 
    NOT_SCANNED(10, "Not Scanned / No scan results"), 
    ABORTED(11, "Aborted"), 
    ENCRYPTED(12, "Encrypted"), 
    EXCEEDED_ARCHIVE_SIZE(13, "Exceeded Archive Size"), 
    EXCEEDED_ARCHIVE_FILE_NUMBER(14, "Exceeded Archive File Number"), 
    PASSWORD_PROTECTED_DOCUMENT(15, "Password Protected Document"), 
    MISMATCH(17, "Mismatch"), 
    POTENTIALLY_VULNERABLE_FILE(17, "Potentially Vulnerable File"), 
    DISABLED_OR_SKIPPED(100, "Scan is disabled or skipped"), 
    IN_PROGRESS(255, "In Progress");
    
    private int value;
    private String desc;
    private static Map<Integer, MetadefenderScanResult> map;
    
    private MetadefenderScanResult(final int value, final String desc) {
        this.value = value;
        this.desc = desc;
    }
    
    public static MetadefenderScanResult getScanResultFromCode(final Integer code) {
        if (MetadefenderScanResult.map == null) {
            MetadefenderScanResult.map = new HashMap<Integer, MetadefenderScanResult>();
            for (final MetadefenderScanResult result : values()) {
                MetadefenderScanResult.map.put(result.code(), result);
            }
        }
        return MetadefenderScanResult.map.get(code);
    }
    
    public int code() {
        return this.value;
    }
    
    public String description() {
        return this.desc;
    }
}
