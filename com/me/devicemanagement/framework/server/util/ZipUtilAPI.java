package com.me.devicemanagement.framework.server.util;

import com.adventnet.iam.security.UploadFileRule;

public interface ZipUtilAPI
{
    boolean unzip(final String p0, final String p1, final boolean p2, final boolean p3, final String... p4);
    
    boolean checkFileExistinZip(final String p0, final String p1);
    
    void createZipFile(final String p0, final String p1, final boolean p2, final String p3) throws Exception;
    
    void showMessage(final String p0, final String p1, final int p2);
    
    default void setZipSantizerName(final UploadFileRule uploadFileRule, final String zipSanitizer) {
        throw new UnsupportedOperationException();
    }
}
