package com.adventnet.iam.security;

import java.util.regex.Pattern;
import com.adventnet.iam.security.antivirus.clamav.CLAMAVConfiguration;
import com.zoho.security.eventfw.pojos.log.ZSEC_PERFORMANCE_ANOMALY;
import com.adventnet.iam.security.antivirus.VendorAV;
import com.zoho.security.eventfw.ExecutionTimer;
import com.adventnet.iam.security.antivirus.VendorAVProvider;
import com.zoho.security.validator.zip.ZipSanitizerRule;
import java.util.Iterator;
import com.zoho.security.validator.zip.ZSecZipSanitizer;
import java.util.logging.Level;
import java.util.List;
import java.io.File;
import com.adventnet.iam.security.antivirus.AVScanResult;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import com.zoho.security.util.RangeUtil;
import java.util.logging.Logger;
import com.zoho.security.api.Range;

public class UploadFileRule
{
    private String fieldName;
    private boolean nameRegex;
    private String[] allowedContentTypesName;
    private String[] allowedContentTypes;
    private String[] allowedExtensions;
    private String[] disAllowedExtensions;
    private ZSecConstants.DataType type;
    private String template;
    private String format;
    private long maxSizeInKB;
    private String xssPattern;
    private boolean removeExif;
    private boolean checkForVirus;
    private boolean continueOnError;
    private boolean isImportURL;
    private long avthreshold;
    private boolean allowEmptyFile;
    private ParameterRule fileNameRule;
    private Range limit;
    private static final int DEFAULT_RANGE_EXTENSION = 10;
    private boolean mandatory;
    private String zipSanitizerName;
    private static final Logger logger;
    
    public UploadFileRule(final String fieldName, final long maxSizeInKB) {
        this(fieldName, null, maxSizeInKB, null, null, null);
    }
    
    public UploadFileRule(final String fieldName, final String allowedContentTypeName, final long maxSizeInKB) {
        this(fieldName, allowedContentTypeName, maxSizeInKB, null, null, null);
    }
    
    public UploadFileRule(final String fieldName, final String allowedContentTypeName, final long maxSizeInKB, final String[] allowedExtensions, final String xssPattern, final String fileNameRegex) {
        this(fieldName, allowedContentTypeName, maxSizeInKB, allowedExtensions, xssPattern, fileNameRegex, RangeUtil.createRange("0-1", "int"), false);
    }
    
    public UploadFileRule(final String fieldName, final String allowedContentTypeName, final long maxSizeInKB, final String[] allowedExtensions, final String xssPattern, final String fileNameRegex, final boolean mandatory) {
        this(fieldName, allowedContentTypeName, maxSizeInKB, allowedExtensions, xssPattern, fileNameRegex, RangeUtil.createRange("0-1", "int"), mandatory);
    }
    
    public UploadFileRule(final String fieldName, final String allowedContentTypeName, final long maxSizeInKB, final String[] allowedExtensions, final String xssPattern, final String fileNameRegex, final Range limit, final boolean mandatory) {
        this.fieldName = null;
        this.allowedContentTypesName = null;
        this.allowedExtensions = null;
        this.disAllowedExtensions = null;
        this.type = null;
        this.template = null;
        this.format = null;
        this.maxSizeInKB = -1L;
        this.xssPattern = null;
        this.removeExif = false;
        this.checkForVirus = true;
        this.continueOnError = false;
        this.isImportURL = false;
        this.avthreshold = 300L;
        this.allowEmptyFile = true;
        this.fileNameRule = null;
        this.limit = null;
        this.mandatory = false;
        this.zipSanitizerName = null;
        this.fieldName = fieldName;
        this.setAllowedContentTypeName(allowedContentTypeName);
        this.maxSizeInKB = maxSizeInKB;
        this.xssPattern = xssPattern;
        this.setAllowedExtensions(allowedExtensions);
        if (SecurityUtil.isValid(fileNameRegex)) {
            this.fileNameRule = new ParameterRule(fieldName, fileNameRegex);
        }
        this.limit = limit;
        this.mandatory = mandatory;
    }
    
    public UploadFileRule(final Element element) {
        this.fieldName = null;
        this.allowedContentTypesName = null;
        this.allowedExtensions = null;
        this.disAllowedExtensions = null;
        this.type = null;
        this.template = null;
        this.format = null;
        this.maxSizeInKB = -1L;
        this.xssPattern = null;
        this.removeExif = false;
        this.checkForVirus = true;
        this.continueOnError = false;
        this.isImportURL = false;
        this.avthreshold = 300L;
        this.allowEmptyFile = true;
        this.fileNameRule = null;
        this.limit = null;
        this.mandatory = false;
        this.zipSanitizerName = null;
        final Element parent = (Element)element.getParentNode();
        this.fieldName = element.getAttribute("name");
        if (SecurityUtil.isValid(element.getAttribute("name-regex"))) {
            this.setNameRegex(Boolean.parseBoolean(element.getAttribute("name-regex")));
        }
        final String maxSize_file = element.getAttribute("max-size");
        final String max_import_file_size = element.getAttribute("max-import-file-size");
        final String maxSize = SecurityUtil.isValid(maxSize_file) ? maxSize_file : max_import_file_size;
        if (SecurityUtil.isValid(maxSize)) {
            this.maxSizeInKB = Long.parseLong(maxSize);
        }
        if (!(this.isImportURL = "true".equalsIgnoreCase(element.getAttribute("import-url")))) {
            this.setType(element.getAttribute("type"));
            this.setTemplate(element.getAttribute("template"));
            this.setFormat(element.getAttribute("format"));
        }
        this.setAllowedContentTypeName(element.getAttribute("content-type-name"));
        this.setAllowedContentTypes(element.getAttribute("allowed-content-types"));
        this.mandatory = Boolean.valueOf(element.getAttribute("mandatory"));
        final String limitStr = element.getAttribute("limit");
        this.setLimit(limitStr);
        if (element.getAttribute("allowed-extensions") != null && !"".equals(element.getAttribute("allowed-extensions"))) {
            this.setAllowedExtensions(element.getAttribute("allowed-extensions").split(","));
        }
        if (SecurityUtil.isValid(element.getAttribute("disallowed-extensions"))) {
            this.setDisAllowedExtensions(element.getAttribute("disallowed-extensions").split(","));
        }
        this.xssPattern = element.getAttribute("xss");
        if (element.getAttribute("antivirus") != null && !"".equals(element.getAttribute("antivirus"))) {
            this.checkForVirus = "true".equalsIgnoreCase(element.getAttribute("antivirus"));
        }
        if (element.getAttribute("av-threshold") != null && !"".equals(element.getAttribute("av-threshold"))) {
            this.avthreshold = Long.parseLong(element.getAttribute("av-threshold"));
            if (this.avthreshold <= 0L) {
                throw new RuntimeException("configured av-threshold [" + this.avthreshold + "] value should be positive");
            }
        }
        if (element.getAttribute("remove-exif") != null && !"".equals(element.getAttribute("remove-exif"))) {
            this.removeExif = "true".equalsIgnoreCase(element.getAttribute("remove-exif"));
        }
        this.continueOnError = "true".equalsIgnoreCase(element.getAttribute("continue-onerror"));
        final String allowEmpty = element.getAttribute("allow-empty");
        if (SecurityUtil.isValid(allowEmpty)) {
            this.setAllowEmptyFile("true".equalsIgnoreCase(allowEmpty));
        }
        final String zipSanitizer = element.getAttribute("zip-sanitizer-name");
        if (SecurityUtil.isValid(zipSanitizer)) {
            this.setZipSanitizerName(zipSanitizer);
        }
        this.initFileNameRule(element);
    }
    
    public void setZipSanitizerName(final String zipSanitizer) {
        this.zipSanitizerName = zipSanitizer;
    }
    
    public String getZipSanitizerName() {
        return this.zipSanitizerName;
    }
    
    private void initFileNameRule(final Element element) {
        final NodeList list = element.getElementsByTagName("filename");
        if (SecurityUtil.isValid(list)) {
            if (list.getLength() == 1) {
                final Element paramEle = (Element)list.item(0);
                paramEle.setAttribute("name", this.fieldName);
                this.fileNameRule = new ParameterRule(paramEle);
            }
            else if (list.getLength() > 1) {
                throw new RuntimeException("FileName rule configuration more than one is not allowed for the file rule '" + this.fieldName + "'");
            }
        }
    }
    
    public ParameterRule getFileNameRule() {
        return this.fileNameRule;
    }
    
    public String getFieldName() {
        return this.fieldName;
    }
    
    public void setMandatory(final boolean mandatory) {
        this.mandatory = mandatory;
    }
    
    public boolean isMandatory() {
        return this.mandatory;
    }
    
    public void setAsImportURL(final boolean isImportURL) {
        this.isImportURL = isImportURL;
    }
    
    public boolean isImportURL() {
        return this.isImportURL;
    }
    
    public String getAllowedContentTypeName() {
        if (this.allowedContentTypesName != null) {
            return this.allowedContentTypesName[0];
        }
        return null;
    }
    
    public String[] getAllowedContentTypesName() {
        return this.allowedContentTypesName;
    }
    
    public long getMaxSizeInKB() {
        return this.maxSizeInKB;
    }
    
    public String getXssPattern() {
        return this.xssPattern;
    }
    
    public Range getUploadLimit() {
        return this.limit;
    }
    
    public String getXssPattern(final SecurityRequestWrapper request, final String matchedContentType) {
        if (this.xssPattern != null && !"".equals(this.xssPattern)) {
            return this.xssPattern;
        }
        return SecurityFilterProperties.getInstance((HttpServletRequest)request).getContentTypesXSS(matchedContentType);
    }
    
    public String getFileNameRegex() {
        return (this.fileNameRule == null) ? null : this.fileNameRule.getAllowedValueRegex();
    }
    
    public void validate(final SecurityRequestWrapper request, final UploadedFileItem fileItem) {
        final List<AVScanResult<File>> virusScanResultList = new ArrayList<AVScanResult<File>>();
        try {
            fileItem.setValidated(true);
            if (this.fileNameRule == null) {
                final String fileNameRegex = this.isImportURL ? "url" : "filename";
                this.fileNameRule = new ParameterRule(fileItem.getFieldName(), fileNameRegex);
            }
            try {
                final String fieldName = fileItem.getFieldName();
                String fileName = fileItem.getFileName();
                if (this.fileNameRule.isContentReplacementEnabled()) {
                    fileName = this.fileNameRule.matchAndReplace(request, fieldName, fileName);
                }
                fileName = this.fileNameRule.validateParamValue(request, fieldName, fileName, this.fileNameRule);
                fileItem.setFileName(fileName);
            }
            catch (final IAMSecurityException e) {
                fileItem.setFileName("");
                throw e;
            }
            if (fileItem.hasErrors()) {
                return;
            }
            this.validateFileItem(request, fileItem, virusScanResultList);
            fileItem.setVirusScanResult(virusScanResultList.isEmpty() ? null : virusScanResultList);
        }
        catch (final IAMSecurityException e) {
            if (!this.continueOnError()) {
                throw e;
            }
            fileItem.addSecurityException(e);
            if (e.getErrorCode().equals("VIRUS_DETECTED")) {
                fileItem.setVirusScanResult(virusScanResultList);
            }
        }
    }
    
    private void validateFileItem(final SecurityRequestWrapper request, final UploadedFileItem fileItem, final List<AVScanResult<File>> virusScanResultList) {
        final File f = fileItem.getUploadedFileForValidation();
        final SecurityFilterProperties filterConfig = SecurityFilterProperties.getInstance((HttpServletRequest)request);
        final String contentType = fileItem.getDetectedContentType();
        final long fileSize = fileItem.getFileSize();
        final String fileName = fileItem.getFileName();
        try {
            this.validateFile(filterConfig, request.getRequestURI(), fileItem, f, contentType, fileSize, fileName, virusScanResultList);
            if (this.zipSanitizerName != null && SecurityUtil.isZip(contentType) && f.exists()) {
                fileItem.setFileSize(f.length());
            }
        }
        catch (final IAMSecurityException ex) {
            throw new IAMSecurityException(ex.getErrorCode(), request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), contentType, fileName, fileSize, this.fieldName, this);
        }
    }
    
    void validateFile(final SecurityFilterProperties filterConfig, final String uri, final UploadedFileItem uploadedFileItem, final File file, final String extractedContentType, final long fileSize, final String fileName, final List<AVScanResult<File>> virusScanResultList) {
        if (!file.exists()) {
            return;
        }
        if (!this.validateExtension(fileName)) {
            UploadFileRule.logger.log(Level.SEVERE, "File extension do not match with the allowed extenions ");
            throw new IAMSecurityException("INVALID_FILE_EXTENSION");
        }
        if (uploadedFileItem != null && !this.isImportURL) {
            final String extension = getExtension(fileName);
            if (extension != null) {
                uploadedFileItem.setExtension(extension);
            }
        }
        if (filterConfig == null) {
            UploadFileRule.logger.log(Level.SEVERE, "context Path is invalid ");
            throw new IAMSecurityException("INVALID_CONTEXT_PATH");
        }
        final long fileSizeInMB = fileSize / 1048576L;
        final String matchedContentTypeName = this.getMatchedContentTypeName(filterConfig, extractedContentType);
        if ((this.getAllowedContentTypeName() != null || this.isAllowedContentTypeRuleConfigured()) && matchedContentTypeName == null) {
            UploadFileRule.logger.log(Level.SEVERE, "file content type \"{0}\" is not allowed ", extractedContentType);
            throw new IAMSecurityException("UNMATCHED_FILE_CONTENT_TYPE");
        }
        if (this.checkForVirus) {
            if (this.avthreshold > 0L && fileSizeInMB > this.avthreshold) {
                UploadFileRule.logger.log(Level.WARNING, "anti-virus check has been skipped due to the file size exceeded the configured av-threshold value {0} MB and the file [{1}] size is {2} MB", new Object[] { String.valueOf(this.avthreshold), fileName, String.valueOf(fileSizeInMB) });
                virusScanResultList.add(AVScanResult.skipped());
            }
            else {
                final long startTime = System.currentTimeMillis();
                this.scanForVirus(filterConfig.getVendorAVProvier(), file, fileName, extractedContentType, fileSize, uri, virusScanResultList);
                for (final AVScanResult<File> avScanResult : virusScanResultList) {
                    if (avScanResult.getScanEngineName().equals("Clam AV")) {
                        this.logScanInfo(avScanResult, filterConfig.getClamAvConfig(), extractedContentType, startTime, fileSize);
                    }
                }
            }
        }
        if (this.zipSanitizerName != null && SecurityUtil.isZip(extractedContentType)) {
            final ZipSanitizerRule zipSanitizerRule = filterConfig.getZipSanitizerRule(this.zipSanitizerName);
            ZSecZipSanitizer.extract(zipSanitizerRule, file, "sanitize".equals(zipSanitizerRule.getAction()) ? ZSecConstants.DESTINATION_TYPE.ZIP : ZSecConstants.DESTINATION_TYPE.NONE, SecurityUtil.is7Zip(extractedContentType));
        }
        if (this.type != null && this.isCSVFile(extractedContentType, fileName)) {
            if (uploadedFileItem != null) {
                uploadedFileItem.setValidatorType(ZSecConstants.DataType.Csv);
            }
            CsvValidator validator = null;
            try {
                validator = TemplateRule.csvValidation(this.fieldName, filterConfig.getSecurityProvider().getUploadedFileContent(SecurityUtil.getCurrentRequest(), fileName, file), this.template, this.format, false);
            }
            catch (final IAMSecurityException e) {
                throw e;
            }
            catch (final Exception e2) {
                UploadFileRule.logger.log(Level.WARNING, "Unknown exception occurred while csv validation: Error: {0}", e2.getMessage());
                throw new IAMSecurityException(ZSecConstants.DataType.errorcodeMap.get(this.type));
            }
            if (uploadedFileItem != null) {
                uploadedFileItem.setValidator(validator);
            }
        }
        if (!"API_CALL".equals(uri)) {
            this.sanitizeFileContent((SecurityRequestWrapper)SecurityUtil.getCurrentRequest(), file, fileName, matchedContentTypeName);
        }
        if (this.removeExif && (extractedContentType.equalsIgnoreCase("image/jpeg") || extractedContentType.equalsIgnoreCase("image/tiff"))) {
            MetadataRemover.removeExif(file, extractedContentType);
        }
    }
    
    private boolean isCSVFile(final String contentType, final String fileName) {
        int lastDotIndex = -1;
        return "text/csv".equals(contentType) || (fileName != null && "text/plain".equals(contentType) && (lastDotIndex = fileName.lastIndexOf(".")) != -1 && "csv".equalsIgnoreCase(fileName.substring(lastDotIndex + 1, fileName.length())));
    }
    
    void scanForVirus(final VendorAVProvider config, final File file, final String fileName, final String contentType, final long fileSize, final String uri, final List<AVScanResult<File>> virusScanResultList) {
        if (config != null && virusScanResultList != null) {
            String exceptionMessage = null;
            final ExecutionTimer avtimer = ExecutionTimer.startInstance();
            AVScanResult<File> result = null;
            try {
                final List<VendorAV<File>> vendorAVs = config.getVendorAVs();
                if (vendorAVs == null) {
                    UploadFileRule.logger.log(Level.SEVERE, "Invalid AV Vendor Provider");
                    throw new IAMSecurityException("INVALID_AV_CONFIGURATION");
                }
                for (final VendorAV<File> vendorAV : vendorAVs) {
                    if (vendorAV == null) {
                        UploadFileRule.logger.log(Level.WARNING, "{0} Exception Message: AV Vendor From Provider is null", "INVALID_AV_CONFIGURATION");
                        throw new IAMSecurityException("INVALID_AV_CONFIGURATION");
                    }
                    try {
                        result = vendorAV.scan(file);
                    }
                    finally {
                        vendorAV.close();
                    }
                    if (result == null) {
                        throw new IAMSecurityException("Return Null Result From Antivirus Scanner");
                    }
                    virusScanResultList.add(result);
                    if (result.status() == AVScanResult.Status.VIRUS_DETECTED) {
                        UploadFileRule.logger.log(Level.SEVERE, "Virus: \"{0}\" detected for file: \"{1}\"", new Object[] { result.getDetectedVirusName(), fileName });
                        throw new IAMSecurityException("VIRUS_DETECTED");
                    }
                    if (result.status() != AVScanResult.Status.FAILED) {
                        continue;
                    }
                    UploadFileRule.logger.log(Level.WARNING, "Virus detection failed: \"{0}\" for file: \"{1}\"", new Object[] { result.getScanFailureInfo(), fileName });
                }
                UploadFileRule.logger.log(Level.FINE, "NO virus detected for file: {0}", fileName);
                ZSEC_PERFORMANCE_ANOMALY.pushAvScan(uri, fileName, (String)null, contentType, (String)null, avtimer);
            }
            catch (final IAMSecurityException e) {
                exceptionMessage = e.getMessage();
                ZSEC_PERFORMANCE_ANOMALY.pushAvScan(uri, fileName, (result != null) ? result.getDetectedVirusName() : null, contentType, exceptionMessage, avtimer);
                UploadFileRule.logger.log(Level.WARNING, "Virus scan failed -> Exception : {0}", exceptionMessage);
                throw new IAMSecurityException(e.getErrorCode());
            }
        }
    }
    
    public void logScanInfo(final AVScanResult<File> avResult, final CLAMAVConfiguration config, final String fileContentType, final long startTime, final long fileSize) {
        try {
            final String scanType = config.isNonPersistentScan() ? "NonPersistentScan" : "PersistentScan";
            String exceptionMessage = null;
            final Throwable thrown = null;
            if (avResult != null && avResult.getScanFailureInfo() != null) {
                exceptionMessage = avResult.getScanFailureInfo().message();
            }
            UploadFileRule.logger.log(Level.INFO, "ZohoSecurity ClamAV fileupload stats: ZSEC ContentType: {0} ZSEC FileSize(in bytes): {1} ZSEC StartTime(in millisec): {2} ZSEC Time taken for Scan(in millisec): {3} ZSEC Type of Scan: {4} ZSEC Host: {5} ZSEC Port: {6} ZSEC Exception: {7}", new Object[] { fileContentType, fileSize, startTime, avResult.getTotalTime(), scanType, config.getHost(), config.getPort(), exceptionMessage });
        }
        catch (final Exception e) {
            UploadFileRule.logger.log(Level.INFO, "Exception at ClamAV scan info logging", e);
        }
    }
    
    void sanitizeFileContent(final SecurityRequestWrapper request, final File file, final String fileName, final String matchedContentTypeName) {
        String content = null;
        final String xssPatternName = this.getXssPattern(request, matchedContentTypeName);
        if (xssPatternName != null && !"".equals(xssPatternName)) {
            try {
                content = SecurityFilterProperties.getInstance((HttpServletRequest)request).getSecurityProvider().getUploadedFileContent((HttpServletRequest)request, fileName, file);
            }
            catch (final Exception e) {
                file.delete();
                UploadFileRule.logger.log(Level.SEVERE, "Exception while reading the file content {0}", e);
                return;
            }
            try {
                content = SecurityUtil.applyXSSPattern(request, xssPatternName, this.getFieldName(), content);
                SecurityUtil.writeToFile(file, content);
            }
            catch (final IAMSecurityException ex) {
                UploadFileRule.logger.log(Level.SEVERE, "XSS detected in the file {0} in the request URI {1}.", new Object[] { this.getFieldName(), request.getRequestURI() });
                throw new IAMSecurityException("XSS_DETECTED");
            }
            catch (final Exception ex2) {
                file.delete();
                UploadFileRule.logger.log(Level.SEVERE, "Exception while reading the file content {0}", ex2);
            }
        }
    }
    
    void validate(final UploadedFileItem fileItem) {
        final List<AVScanResult<File>> virusScanResultList = new ArrayList<AVScanResult<File>>(2);
        try {
            fileItem.setValidated(true);
            final SecurityRequestWrapper request = (SecurityRequestWrapper)SecurityUtil.getCurrentRequest();
            this.validateFileItem(request, fileItem, virusScanResultList);
            fileItem.setVirusScanResult(virusScanResultList.isEmpty() ? null : virusScanResultList);
        }
        catch (final IAMSecurityException e) {
            if (!this.continueOnError()) {
                throw e;
            }
            fileItem.addSecurityException(e);
            if (e.getErrorCode().equals("VIRUS_DETECTED")) {
                fileItem.setVirusScanResult(virusScanResultList);
            }
        }
    }
    
    public String getMatchedContentTypeName(final SecurityFilterProperties filterConfig, final String contentType) {
        if (!SecurityUtil.isValid(contentType)) {
            return null;
        }
        if (this.allowedContentTypesName != null) {
            for (final String contentTypeName : this.allowedContentTypesName) {
                final Pattern allowedContentTypeRegex = filterConfig.getContentTypes(contentTypeName);
                if (allowedContentTypeRegex == null) {
                    throw new IAMSecurityException("Content type rule for \"" + contentTypeName + "\" is not configured");
                }
                if (allowedContentTypeRegex.matcher(contentType).matches()) {
                    return contentTypeName;
                }
            }
        }
        if (this.isAllowedContentTypeRuleConfigured()) {
            for (final String contentTypeName : this.allowedContentTypes) {
                final ContentTypeRule contentTypeRule = filterConfig.getContentTypeRule(contentTypeName);
                if (contentTypeRule.contains(contentType)) {
                    return contentTypeName;
                }
            }
        }
        return null;
    }
    
    public boolean validateExtension(String fileName) {
        if (this.allowedExtensions == null && this.disAllowedExtensions == null) {
            return true;
        }
        fileName = fileName.toLowerCase();
        if (this.allowedExtensions != null) {
            for (final String allowedExtension : this.allowedExtensions) {
                if (fileName.endsWith(allowedExtension)) {
                    return true;
                }
            }
        }
        else if (this.disAllowedExtensions != null) {
            for (final String disAllowedExtension : this.disAllowedExtensions) {
                if (fileName.endsWith(disAllowedExtension)) {
                    return false;
                }
            }
        }
        return this.allowedExtensions == null;
    }
    
    String validateConfiguration(final SecurityFilterProperties sfp) {
        if (this.fileNameRule != null && this.getFileNameXSSPattern() == null && (this.getFileNameRegex() == null || this.getFileNameRegex().length() == 0) && this.fileNameRule.getDataType() == null) {
            return "Missing input validation. At least any one of \"type\", \"regex\" or \"xss\" should be configured for the filename rule :\n" + this.toString() + "\n";
        }
        if (this.getAllowedContentTypeName() == null) {
            return null;
        }
        String errorString = "";
        for (final String contentType : this.allowedContentTypesName) {
            final Pattern allowedContentTypes = sfp.getContentTypes(contentType);
            if (allowedContentTypes == null) {
                errorString = errorString + "Content type rule is not configured for the UploadFileRule :\n" + this.toString() + "\n";
            }
        }
        if (!"".equals(errorString)) {
            return errorString;
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "UploadFileRule ::  fieldName : \"" + this.fieldName + "\" allowedContentTypeName : \"" + ActionRule.PRINT(this.allowedContentTypesName) + "\" allowedFileTypes: \"" + ActionRule.PRINT(this.allowedContentTypes) + "\" allowedExtensions  : \"" + ActionRule.PRINT(this.allowedExtensions) + "\" maxSizeInKB : \"" + this.maxSizeInKB + "\" xssPattern : \"" + this.xssPattern + "\" antiVirusCheck : \"" + this.checkForVirus + "\" fileNameRule :: \"" + this.fileNameRule + "\"";
    }
    
    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }
    
    public void setNameRegex(final boolean nameRegex) {
        this.nameRegex = nameRegex;
    }
    
    public boolean isNameRegex() {
        return this.nameRegex;
    }
    
    public void setContinueOnError(final boolean continueOnError) {
        this.continueOnError = continueOnError;
    }
    
    public boolean continueOnError() {
        return this.continueOnError;
    }
    
    public void setType(final String type) {
        if (SecurityUtil.isValid(type)) {
            if (!ZSecConstants.DataType.Csv.value.equalsIgnoreCase(type)) {
                UploadFileRule.logger.log(Level.WARNING, "Invalid file rule ''type'' configuration: {0}", new Object[] { type });
                throw new IAMSecurityException("INVALID_CONFIGURATION");
            }
            this.type = ZSecConstants.DataType.Csv;
        }
    }
    
    public ZSecConstants.DataType getType() {
        return this.type;
    }
    
    public void setFormat(final String format) {
        this.format = format;
    }
    
    public String getFormat() {
        return this.format;
    }
    
    public void setTemplate(final String template) {
        this.template = template;
    }
    
    public String getTemplate() {
        return this.template;
    }
    
    public void setAllowedContentTypeName(final String allowedContentTypes) {
        if (allowedContentTypes != null && !"".equals(allowedContentTypes)) {
            this.allowedContentTypesName = allowedContentTypes.split(",");
        }
    }
    
    public void setAllowedContentTypes(final String contentTypeRulesName) {
        if (SecurityUtil.isValid(contentTypeRulesName)) {
            this.allowedContentTypes = contentTypeRulesName.split(",");
        }
    }
    
    public String[] getAllowedContentTypes() {
        return this.allowedContentTypes;
    }
    
    public boolean isAllowedContentTypeRuleConfigured() {
        return this.allowedContentTypes != null;
    }
    
    public void setAllowedExtensions(final String[] extensions) {
        if (extensions == null || extensions.length < 1) {
            return;
        }
        this.allowedExtensions = this.formatExtensions(extensions);
    }
    
    public void setDisAllowedExtensions(final String[] extensions) {
        if (extensions == null || extensions.length < 1) {
            return;
        }
        this.disAllowedExtensions = this.formatExtensions(extensions);
    }
    
    private String[] formatExtensions(final String[] extensions) {
        final String[] formatedExtensions = new String[extensions.length];
        for (int i = 0; i < extensions.length; ++i) {
            String extension = extensions[i];
            if (!extension.startsWith(".")) {
                extension = "." + extension;
            }
            extension = extension.toLowerCase();
            formatedExtensions[i] = extension;
        }
        return formatedExtensions;
    }
    
    public void setAvthreshold(final long avthreshold) {
        this.avthreshold = avthreshold;
    }
    
    public void setXssPattern(final String xssPattern) {
        this.xssPattern = xssPattern;
    }
    
    public void checkForVirus(final boolean checkForVirus) {
        this.checkForVirus = checkForVirus;
    }
    
    public void setFileNameRegex(final String fileNameRegex) {
        if (this.fileNameRule == null) {
            this.fileNameRule = new ParameterRule();
        }
        this.fileNameRule.setAllowedValueRegex(fileNameRegex);
    }
    
    public void setFileNameXSSPattern(final String fileNameXSSPattern) {
        if (this.fileNameRule == null) {
            this.fileNameRule = new ParameterRule();
        }
        this.fileNameRule.setXSSValidation(fileNameXSSPattern);
    }
    
    public String getFileNameXSSPattern() {
        return (this.fileNameRule == null) ? null : this.fileNameRule.getXSSValidation();
    }
    
    public String[] getAllowedExtensions() {
        return this.allowedExtensions;
    }
    
    public String[] getDisAllowedExtensions() {
        return this.disAllowedExtensions;
    }
    
    public void setFileNameRule(final ParameterRule fileNameRule) {
        this.fileNameRule = fileNameRule;
    }
    
    public boolean isAllowedEmptyFile() {
        return this.allowEmptyFile;
    }
    
    public void setAllowEmptyFile(final boolean allowEmptyFile) {
        this.allowEmptyFile = allowEmptyFile;
    }
    
    public void setRemoveExif(final boolean removeExif) {
        this.removeExif = removeExif;
    }
    
    public boolean removeExif() {
        return this.removeExif;
    }
    
    public String getLimit() {
        if (this.limit != null) {
            return this.limit.getRangeNotation();
        }
        return null;
    }
    
    public void setLimit(final String limitStr) {
        this.limit = RangeUtil.createFixedRangeForInteger("limit", limitStr, 10, this.mandatory);
    }
    
    public boolean isVirusCheckEnabled() {
        return this.checkForVirus;
    }
    
    public long getAVThreshold() {
        return this.avthreshold;
    }
    
    public void setAVThreshold(final long avThreshold) {
        this.avthreshold = avThreshold;
    }
    
    static String getExtension(final String fileName) {
        if (fileName != null && fileName.length() > 0) {
            final int lastindex = fileName.lastIndexOf(".");
            if (lastindex != -1 && lastindex != 0) {
                return fileName.substring(lastindex + 1);
            }
        }
        return null;
    }
    
    static {
        logger = Logger.getLogger(SecurityRequestWrapper.class.getName());
    }
}
