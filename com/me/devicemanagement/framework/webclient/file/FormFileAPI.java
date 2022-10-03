package com.me.devicemanagement.framework.webclient.file;

import java.util.Map;
import com.adventnet.iam.security.UploadedFileItem;
import java.io.File;
import javax.servlet.http.HttpServletRequest;

public interface FormFileAPI
{
    File getUploadedFile(final HttpServletRequest p0, final String p1);
    
    UploadedFileItem getUploadedFileItem(final HttpServletRequest p0, final String p1);
    
    Map<String, UploadedFileItem> getAllUploadedFileItem(final HttpServletRequest p0);
}
