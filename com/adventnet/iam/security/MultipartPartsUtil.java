package com.adventnet.iam.security;

import java.io.File;
import com.zoho.security.eventfw.pojos.log.ZSEC_PERFORMANCE_ANOMALY;
import com.zoho.security.eventfw.ExecutionTimer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.logging.Level;
import org.apache.commons.lang3.mutable.MutableLong;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Part;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class MultipartPartsUtil
{
    private static final Pattern FILE_NAME_SPLIT_PATTERN;
    private static final Logger LOGGER;
    
    public static void initParts(final SecurityRequestWrapper securityRequestWrapper, final Collection<Part> parts, final Map<String, SecurityRequestWrapper.FileUploadTracker> fileUploadRecord) throws IOException, ServletException {
        try {
            final Map<String, List<UploadedFilePart>> paramToPartsMap = new HashMap<String, List<UploadedFilePart>>();
            securityRequestWrapper.setPartsMap(paramToPartsMap);
            final MutableLong uploadedFilesSizeInBytes = new MutableLong();
            if (parts.isEmpty()) {
                MultipartPartsUtil.LOGGER.log(Level.WARNING, "Getting empty parts list, cause of this problem is whether multipart not confgured to this servlet or request not contains multipart data");
            }
            for (final Part part : parts) {
                final String submittedFileName = UploadedFilePart.getSubmittedFileName(part);
                if (submittedFileName == null) {
                    continue;
                }
                final UploadedFilePart uploadedFilePart = createBasicValidatedUploadedFilePart(securityRequestWrapper, part, submittedFileName, fileUploadRecord, uploadedFilesSizeInBytes);
                if (uploadedFilePart == null) {
                    continue;
                }
                if (paramToPartsMap.get(uploadedFilePart.getFieldName()) == null) {
                    paramToPartsMap.put(uploadedFilePart.getFieldName(), new ArrayList<UploadedFilePart>());
                }
                paramToPartsMap.get(uploadedFilePart.getName()).add(uploadedFilePart);
            }
            MultipartPartsUtil.LOGGER.log(Level.INFO, "Parts are successfully validated and mapped to UploadedFileParts");
        }
        finally {
            for (final Part part2 : parts) {
                part2.delete();
            }
            parts.clear();
        }
    }
    
    private static UploadedFilePart createBasicValidatedUploadedFilePart(final SecurityRequestWrapper securityRequestWrapper, final Part part, final String submittedFileName, final Map<String, SecurityRequestWrapper.FileUploadTracker> fileUploadRecord, final MutableLong uploadedFilesSizeInBytes) throws IOException {
        final File file = SecurityUtil.createTempFile(new TempFileName(securityRequestWrapper.getURLActionRule()));
        final String[] str = MultipartPartsUtil.FILE_NAME_SPLIT_PATTERN.split(submittedFileName);
        final String validatedFileName = str[str.length - 1];
        final UploadedFilePart uploadedFilePart = new UploadedFilePart(validatedFileName, part.getName(), part.getContentType());
        UploadFileRule uploadRule = securityRequestWrapper.getURLActionRule().getUploadFileRule(part.getName());
        if (uploadRule == null) {
            uploadRule = SecurityFilterProperties.getInstance((HttpServletRequest)securityRequestWrapper).getSecurityProvider().getDynamicFileRule((HttpServletRequest)securityRequestWrapper, securityRequestWrapper.getURLActionRule(), uploadedFilePart);
        }
        if (uploadRule == null) {
            MultipartPartsUtil.LOGGER.log(Level.INFO, "Upload rule for the file {0} is not configured for the URL {1}", new Object[] { part.getName(), securityRequestWrapper.getRequestURI() });
            throw new IAMSecurityException("UPLOAD_RULE_NOT_CONFIGURED", securityRequestWrapper.getRequestURI(), securityRequestWrapper.getRemoteAddr(), securityRequestWrapper.getHeader("Referer"), part.getName(), uploadRule);
        }
        try {
            final long maxAllowedSize = SecurityFilterProperties.getInstance((HttpServletRequest)securityRequestWrapper).getSecurityProvider().getMaximumUploadSize((HttpServletRequest)securityRequestWrapper, securityRequestWrapper.getURLActionRule());
            long maxAllowedSizeInBytes = (maxAllowedSize != -1L) ? maxAllowedSize : uploadRule.getMaxSizeInKB();
            if (maxAllowedSizeInBytes > 0L) {
                maxAllowedSizeInBytes *= 1024L;
            }
            if (part.getSize() > maxAllowedSizeInBytes) {
                MultipartPartsUtil.LOGGER.log(Level.SEVERE, "Size of uploaded file is more than the allowed-file-size limit of \"{0}\" kb ", new Object[] { maxAllowedSizeInBytes });
                throw new IAMSecurityException("FILE_SIZE_MORE_THAN_ALLOWED_SIZE", securityRequestWrapper.getRequestURI(), securityRequestWrapper.getRemoteAddr(), securityRequestWrapper.getHeader("Referer"), null, submittedFileName, part.getSize(), uploadRule.getFieldName(), uploadRule);
            }
            securityRequestWrapper.validateNumberOfUploads(uploadedFilePart, uploadRule, fileUploadRecord);
            if (part.getSize() > -1L) {
                uploadedFilesSizeInBytes.add(part.getSize());
                if (securityRequestWrapper.getURLActionRule().isFileUploadMaxSizeExceeded(uploadedFilesSizeInBytes.getValue() / 1024L)) {
                    throw new IAMSecurityException("URL_FILE_UPLOAD_MAX_SIZE_LIMIT_EXCEEDED");
                }
            }
            if (part.getSize() == 0L && submittedFileName.length() > 0 && !uploadRule.isAllowedEmptyFile()) {
                MultipartPartsUtil.LOGGER.log(Level.SEVERE, "Empty file is not allowed for the field : \"{0}\" for the request URI \"{1}\"", new Object[] { part.getName(), securityRequestWrapper.getRequestURI() });
                throw new IAMSecurityException("EMPTY_FILE_NOT_ALLOWED", securityRequestWrapper.getRequestURI(), securityRequestWrapper.getRemoteAddr(), securityRequestWrapper.getHeader("Referer"), null, validatedFileName, 0L, part.getName(), uploadRule);
            }
            if (part.getSize() <= 0L) {
                MultipartPartsUtil.LOGGER.log(Level.WARNING, "This uploaded file [filename: \"{0}\" filedname:\"{1}\"] skipped due to empty file content", new Object[] { submittedFileName, uploadRule.getFieldName() });
                return null;
            }
            part.write(file.getAbsolutePath());
            final ExecutionTimer cdtimer = ExecutionTimer.startInstance();
            final String contentType = SecurityUtil.getMimeType(securityRequestWrapper, file, validatedFileName);
            ZSEC_PERFORMANCE_ANOMALY.pushMimeDetection(securityRequestWrapper.getRequestURI(), validatedFileName, contentType, cdtimer);
            uploadedFilePart.setFileSize(part.getSize());
            uploadedFilePart.setUploadedFile(file);
            uploadedFilePart.setContentTypeDetected(contentType);
        }
        catch (final Exception e) {
            file.delete();
            IAMSecurityException ise = null;
            if (e instanceof IAMSecurityException) {
                ise = (IAMSecurityException)e;
            }
            else {
                if (!(e instanceof IOException)) {
                    throw e;
                }
                ise = new IAMSecurityException(e.getMessage());
            }
            if (uploadRule.continueOnError()) {
                uploadedFilePart.addSecurityException(ise);
                return uploadedFilePart;
            }
            throw ise;
        }
        return uploadedFilePart;
    }
    
    static {
        FILE_NAME_SPLIT_PATTERN = Pattern.compile("[\\/\\\\]");
        LOGGER = Logger.getLogger(MultipartPartsUtil.class.getName());
    }
}
