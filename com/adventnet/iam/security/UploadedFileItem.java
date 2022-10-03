package com.adventnet.iam.security;

import com.zoho.security.agent.AppSenseAgent;
import java.util.HashMap;
import com.adventnet.iam.security.antivirus.AVScanResult;
import java.util.List;
import java.util.Map;
import org.apache.commons.fileupload.disk.DiskFileItem;
import java.io.File;

public class UploadedFileItem
{
    private String fileName;
    private long fileSize;
    private File uploadedFile;
    private String fieldName;
    private String contentTypeFromRequest;
    private String contentTypeDetected;
    private boolean isValidated;
    private DiskFileItem diskFileItem;
    private IAMSecurityException exception;
    private Map<String, List<String>> headerFields;
    private List<AVScanResult<File>> virusScanResult;
    private DataFormatValidator validator;
    private ZSecConstants.DataType type;
    private String fileHash;
    private String extension;
    private Map<String, Object> infoMap;
    
    public UploadedFileItem(final String fileName, final long fileSize, final File uploadedFile, final String fieldName, final String contentTypeFromRequest, final String contentTypeDetected, final DiskFileItem item) {
        this.fileName = null;
        this.fileSize = 0L;
        this.uploadedFile = null;
        this.fieldName = null;
        this.contentTypeFromRequest = null;
        this.contentTypeDetected = null;
        this.isValidated = false;
        this.diskFileItem = null;
        this.exception = null;
        this.headerFields = null;
        this.virusScanResult = null;
        this.validator = null;
        this.type = null;
        this.fileHash = null;
        this.extension = null;
        this.infoMap = null;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.uploadedFile = uploadedFile;
        this.fieldName = fieldName;
        this.contentTypeFromRequest = contentTypeFromRequest;
        this.contentTypeDetected = contentTypeDetected;
        this.diskFileItem = item;
    }
    
    UploadedFileItem(final String fileName, final String fieldName, final String contentType, final DiskFileItem fileItem) {
        this(fileName, -1L, null, fieldName, contentType, null, fileItem);
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public void setFileName(final String newName) {
        this.fileName = newName;
    }
    
    public long getFileSize() {
        return this.fileSize;
    }
    
    public void setFileSize(final long fileSize) {
        this.fileSize = fileSize;
    }
    
    public File getUploadedFile() {
        if (this.isValidated) {
            return this.uploadedFile;
        }
        return null;
    }
    
    protected File getUploadedFileForValidation() {
        return this.uploadedFile;
    }
    
    protected void setValidated(final boolean isValidated) {
        this.isValidated = isValidated;
    }
    
    public boolean isValidated() {
        return this.isValidated;
    }
    
    public void setValidatorType(final ZSecConstants.DataType type) {
        this.type = type;
    }
    
    public ZSecConstants.DataType getValidatorType() {
        return this.type;
    }
    
    public void setValidator(final DataFormatValidator validator) {
        this.validator = validator;
    }
    
    public DataFormatValidator getValidator() {
        return this.validator;
    }
    
    public String getFieldName() {
        return this.fieldName;
    }
    
    public String getRequestContentType() {
        return this.contentTypeFromRequest;
    }
    
    public String getDetectedContentType() {
        return this.contentTypeDetected;
    }
    
    public IAMSecurityException getException() {
        return this.exception;
    }
    
    public void addSecurityException(final IAMSecurityException exception) {
        if (exception != null) {
            this.exception = exception;
            if (this.uploadedFile != null && this.uploadedFile.exists()) {
                this.uploadedFile.delete();
            }
        }
    }
    
    void deleteFile() {
        if (this.uploadedFile != null && this.uploadedFile.exists()) {
            this.uploadedFile.delete();
        }
    }
    
    public boolean hasErrors() {
        return this.exception != null;
    }
    
    public void setUploadedFile(final File uploadedFile) {
        this.uploadedFile = uploadedFile;
    }
    
    public void setContentTypeDetected(final String contentTypeDetected) {
        this.contentTypeDetected = contentTypeDetected;
    }
    
    public DiskFileItem getDiskFileItem() {
        return this.diskFileItem;
    }
    
    public void setDiskFileItem(final DiskFileItem diskFileItem) {
        this.diskFileItem = diskFileItem;
    }
    
    public Map<String, List<String>> getHeaderFields() {
        return this.headerFields;
    }
    
    public void setHeaderFields(final Map<String, List<String>> headerFields) {
        this.headerFields = headerFields;
    }
    
    public String getHeader(final String header) {
        if (this.headerFields != null && this.headerFields.containsKey(header)) {
            final List<String> values = this.headerFields.get(header);
            return (values == null || values.size() == 0) ? null : values.get(0);
        }
        return null;
    }
    
    public List<String> getHeaders(final String header) {
        if (this.headerFields != null) {
            return this.headerFields.get(header);
        }
        return null;
    }
    
    protected void setVirusScanResult(final List<AVScanResult<File>> virusScanResult) {
        this.virusScanResult = virusScanResult;
    }
    
    public List<AVScanResult<File>> getAVScanResults() {
        return this.virusScanResult;
    }
    
    public AVScanResult<File> getAVScanResult() {
        return this.virusScanResult.get(0);
    }
    
    public Map<String, Object> toMap() {
        if (this.infoMap != null) {
            return this.infoMap;
        }
        (this.infoMap = new HashMap<String, Object>()).put("field", this.fieldName);
        this.infoMap.put("name", this.fileName);
        this.infoMap.put("size", this.fileSize);
        this.infoMap.put("r_ctype", this.contentTypeFromRequest);
        this.infoMap.put("d_ctype", this.contentTypeDetected);
        if (this.fileHash != null) {
            this.infoMap.put("hash", this.fileHash);
        }
        if (this.extension != null) {
            this.infoMap.put("extension", this.extension);
            this.infoMap.put("algorithm", AppSenseAgent.getFileHashAlgorithm());
        }
        return this.infoMap;
    }
    
    @Override
    public String toString() {
        return this.toMap().toString();
    }
    
    public void setFileHash(final String hash) {
        this.fileHash = hash;
    }
    
    public String getFileHash() {
        return this.fileHash;
    }
    
    public String getExtension() {
        return this.extension;
    }
    
    public void setExtension(final String extension) {
        this.extension = extension;
    }
}
