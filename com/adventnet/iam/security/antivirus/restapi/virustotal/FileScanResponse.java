package com.adventnet.iam.security.antivirus.restapi.virustotal;

import java.util.Collections;
import java.util.Map;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.gson.Gson;

public class FileScanResponse
{
    private final Gson gson;
    
    public FileScanResponse() {
        this.gson = new Gson();
    }
    
    public List<FileScanMetaData> parseFileScanMetadata(final String response, final boolean isMultiResponse) {
        if (isMultiResponse) {
            return Arrays.asList((FileScanMetaData[])this.gson.fromJson(response, (Class)FileScanMetaData[].class));
        }
        final List<FileScanMetaData> list = new ArrayList<FileScanMetaData>();
        list.add((FileScanMetaData)this.gson.fromJson(response, (Class)FileScanMetaData.class));
        return list;
    }
    
    public List<FileScanReport> parseFileReports(final String response, final boolean isMultiResponse) {
        if (isMultiResponse) {
            return Arrays.asList((FileScanReport[])this.gson.fromJson(response, (Class)FileScanReport[].class));
        }
        final List<FileScanReport> list = new ArrayList<FileScanReport>();
        list.add((FileScanReport)this.gson.fromJson(response, (Class)FileScanReport.class));
        return list;
    }
    
    public static class FileScan
    {
        @SerializedName("detected")
        private boolean detected;
        @SerializedName("version")
        private String version;
        @SerializedName("result")
        private String malware;
        @SerializedName("update")
        private String update;
        
        public boolean isDetected() {
            return this.detected;
        }
        
        public String getVersion() {
            return this.version;
        }
        
        public String getMalware() {
            return this.malware;
        }
        
        public String getUpdate() {
            return this.update;
        }
    }
    
    public static class FileScanReport
    {
        @SerializedName("scans")
        private Map<String, FileScan> scans;
        @SerializedName("scan_id")
        private String scanId;
        @SerializedName("sha1")
        private String sha1;
        @SerializedName("resource")
        private String resource;
        @SerializedName("response_code")
        private Integer responseCode;
        @SerializedName("scan_date")
        private String scanDate;
        @SerializedName("permalink")
        private String permalink;
        @SerializedName("verbose_msg")
        private String verboseMessage;
        @SerializedName("total")
        private Integer total;
        @SerializedName("positives")
        private Integer positives;
        @SerializedName("sha256")
        private String sha256;
        @SerializedName("md5")
        private String md5;
        
        public Map<String, FileScan> getScans() {
            return Collections.unmodifiableMap((Map<? extends String, ? extends FileScan>)this.scans);
        }
        
        public String getScanId() {
            return this.scanId;
        }
        
        public String getSHA1() {
            return this.sha1;
        }
        
        public String getResource() {
            return this.resource;
        }
        
        public Integer getResponseCode() {
            return this.responseCode;
        }
        
        public String getScanDate() {
            return this.scanDate;
        }
        
        public String getPermalink() {
            return this.permalink;
        }
        
        public String getVerboseMessage() {
            return this.verboseMessage;
        }
        
        public Integer getTotal() {
            return this.total;
        }
        
        public Integer getPositives() {
            return this.positives;
        }
        
        public String getSHA256() {
            return this.sha256;
        }
        
        public String getMD5() {
            return this.md5;
        }
    }
    
    public class FileScanMetaData
    {
        @SerializedName("scan_id")
        private String scanId;
        @SerializedName("sha1")
        private String sha1;
        @SerializedName("resource")
        private String resource;
        @SerializedName("response_code")
        private int responseCode;
        @SerializedName("sha256")
        private String sha256;
        @SerializedName("permalink")
        private String permalink;
        @SerializedName("md5")
        private String md5;
        @SerializedName("verbose_msg")
        private String verboseMessage;
        @SerializedName("scan_date")
        private String scanDate;
        
        public String getScanId() {
            return this.scanId;
        }
        
        public String getSHA1() {
            return this.sha1;
        }
        
        public String getResource() {
            return this.resource;
        }
        
        public int getResponseCode() {
            return this.responseCode;
        }
        
        public String getSHA256() {
            return this.sha256;
        }
        
        public String getPermalink() {
            return this.permalink;
        }
        
        public String getMD5() {
            return this.md5;
        }
        
        public String getVerboseMessage() {
            return this.verboseMessage;
        }
        
        public String getScanDate() {
            return this.scanDate;
        }
    }
}
