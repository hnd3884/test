package com.me.devicemanagement.onpremise.webclient.file;

import java.util.stream.Collectors;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.iam.security.UploadedFileItem;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.webclient.file.FormFileAPI;

public class FormFileImpl implements FormFileAPI
{
    public File getUploadedFile(final HttpServletRequest request, final String fieldName) {
        final UploadedFileItem uploadedFileItem = this.getUploadedFileItem(request, fieldName);
        return (uploadedFileItem != null) ? uploadedFileItem.getUploadedFile() : null;
    }
    
    public UploadedFileItem getUploadedFileItem(final HttpServletRequest request, final String fieldName) {
        if (request != null) {
            if (request instanceof SecurityRequestWrapper) {
                final UploadedFileItem uploadedFileItem = ((SecurityRequestWrapper)request).getMultipartParameter(fieldName);
                return uploadedFileItem;
            }
            if (request.getAttribute("MULTIPART_FORM_REQUEST") != null) {
                final ArrayList<UploadedFileItem> fileList = (ArrayList<UploadedFileItem>)request.getAttribute("MULTIPART_FORM_REQUEST");
                for (final UploadedFileItem fileItem : fileList) {
                    if (fieldName.equalsIgnoreCase(fileItem.getFieldName())) {
                        return fileItem;
                    }
                }
            }
        }
        else {
            final List<UploadedFileItem> filesList = DMThreadLocal.getUploadedFileItem();
            for (final UploadedFileItem fileItem : filesList) {
                if (fieldName.equalsIgnoreCase(fileItem.getFieldName())) {
                    return fileItem;
                }
            }
        }
        return null;
    }
    
    public Map<String, UploadedFileItem> getAllUploadedFileItem(final HttpServletRequest request) {
        if (request == null) {
            final List<UploadedFileItem> filesList = DMThreadLocal.getUploadedFileItem();
            return filesList.stream().collect(Collectors.toMap(fileItem -> fileItem.getFileName(), fileItem -> fileItem));
        }
        if (request.getAttribute("MULTIPART_FORM_REQUEST") != null) {
            final ArrayList<UploadedFileItem> fileList = (ArrayList<UploadedFileItem>)request.getAttribute("MULTIPART_FORM_REQUEST");
            return fileList.stream().collect(Collectors.toMap(fileItem -> fileItem.getFileName(), fileItem -> fileItem));
        }
        return null;
    }
}
