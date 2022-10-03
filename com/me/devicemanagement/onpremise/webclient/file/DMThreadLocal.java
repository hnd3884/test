package com.me.devicemanagement.onpremise.webclient.file;

import com.adventnet.iam.security.UploadedFileItem;
import java.util.List;
import java.util.logging.Logger;

public class DMThreadLocal
{
    private static final Logger LOGGER;
    static ThreadLocal<List<UploadedFileItem>> uploadedFileItem;
    
    public static List<UploadedFileItem> getUploadedFileItem() {
        return DMThreadLocal.uploadedFileItem.get();
    }
    
    public static void setUploadedFileItem(final List<UploadedFileItem> uploadedFileItemList) {
        DMThreadLocal.uploadedFileItem.set(uploadedFileItemList);
    }
    
    public static void clearUploadFiles() {
        DMThreadLocal.uploadedFileItem.remove();
    }
    
    static {
        LOGGER = Logger.getLogger(DMThreadLocal.class.getName());
        DMThreadLocal.uploadedFileItem = new ThreadLocal<List<UploadedFileItem>>();
    }
}
