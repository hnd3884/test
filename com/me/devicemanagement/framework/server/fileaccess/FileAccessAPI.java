package com.me.devicemanagement.framework.server.fileaccess;

import java.util.ArrayList;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.HashMap;

public interface FileAccessAPI
{
    boolean createDirectory(final String p0);
    
    @Deprecated
    boolean copyFile(final String p0, final String p1) throws Exception;
    
    HashMap copyFileWithResponse(final String p0, final String p1) throws Exception;
    
    boolean deleteFile(final String p0) throws Exception;
    
    boolean deleteFile(final String p0, final boolean p1) throws Exception;
    
    boolean deleteDirectory(final String p0) throws Exception;
    
    Boolean deleteFilesInDirectory(final String p0, final boolean p1) throws Exception;
    
    boolean isFileExists(final String p0);
    
    long getFileSize(final String p0);
    
    String getFileName(final String p0);
    
    String getFileNameFromFilePath(final String p0);
    
    InputStream readFile(final String p0) throws Exception;
    
    String readFileIntoString(final String p0, final boolean p1);
    
    String readFileIntoString(final String p0);
    
    byte[] readFileContentAsArray(final String p0) throws Exception;
    
    Long writeFile(final String p0, final byte[] p1) throws Exception;
    
    Long writeFile(final String p0, final byte[] p1, final boolean p2) throws Exception;
    
    Long writeFile(final String p0, final InputStream p1) throws Exception;
    
    long lastModified(final String p0);
    
    @Deprecated
    OutputStream writeFile(final String p0) throws Exception;
    
    ArrayList getAllFilesList(final String p0, final String p1, final String p2) throws Exception;
    
    boolean renameFolder(final String p0, final String p1) throws Exception;
    
    boolean copyDirectory(final String p0, final String p1) throws Exception;
    
    boolean isDirectory(final String p0) throws Exception;
    
    String getCanonicalPath(final String p0) throws Exception;
    
    String getParent(final String p0) throws Exception;
    
    void forceDeleteDirectory(final String p0) throws Exception;
    
    HashMap getOutputStream(final String p0) throws Exception;
    
    boolean createNewFile(final String p0);
    
    InputStream getInputStream(final String p0) throws Exception;
    
    String constructFileURL(final HashMap p0);
    
    byte[] readImageContentAsArray(final String p0) throws Exception;
    
    boolean isFile(final String p0) throws Exception;
    
    ArrayList<String> getAllOldFilesList(final String p0, final String p1, final Long p2, final String p3) throws Exception;
    
    ArrayList<String> getAllFilesList(final String p0, final String p1, final Long p2, final Long p3, final String p4) throws Exception;
    
    default void handleUploadTimeOut(final Long fileID) throws Exception {
    }
}
