package com.adventnet.mfw.logging;

import java.util.Locale;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.zip.ZipOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.text.ParseException;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.List;

public class LogsArchiver
{
    private static List<HandlerInfo> fileHandlersInfo;
    private static Logger logger;
    
    public static void addHandlerInfo(final HandlerInfo info) {
        LogsArchiver.fileHandlersInfo.add(info);
    }
    
    static void flushHandlersInfo() {
        LogsArchiver.fileHandlersInfo = new ArrayList<HandlerInfo>();
        LogsArchiver.logger = null;
    }
    
    public static void archiveLogs() throws Exception {
        archiveLogs(LogsArchiver.fileHandlersInfo);
    }
    
    private static void archiveLogs(final List<HandlerInfo> fileHandlersInfo) throws Exception {
        final Map<String, Map<String, List<File>>> archDirVsLogFiles = new HashMap<String, Map<String, List<File>>>();
        final Map<String, List<File>> logsDirVsDeleteLogFiles = new HashMap<String, List<File>>();
        configure(fileHandlersInfo, archDirVsLogFiles, logsDirVsDeleteLogFiles);
        cleanup(archDirVsLogFiles, logsDirVsDeleteLogFiles);
    }
    
    private static void cleanup(final Map<String, Map<String, List<File>>> archDirVsLogFiles, final Map<String, List<File>> logsDirVsDeleteLogFiles) throws Exception {
        for (final String archiveDir : archDirVsLogFiles.keySet()) {
            final Map<String, List<File>> filePatternVsFiles = archDirVsLogFiles.get(archiveDir);
            for (final String prefix : filePatternVsFiles.keySet()) {
                compressAndDeleteLogFiles(prefix, archiveDir, filePatternVsFiles.get(prefix));
            }
        }
        for (final String dirName : logsDirVsDeleteLogFiles.keySet()) {
            final List<File> deleteFileList = logsDirVsDeleteLogFiles.get(dirName);
            deleteLogFiles(deleteFileList);
        }
    }
    
    private static void configure(final List<HandlerInfo> fileHandlersInfo, final Map<String, Map<String, List<File>>> archDirVsLogFiles, final Map<String, List<File>> logsDirVsDeleteLogFiles) throws ParseException {
        for (final HandlerInfo info : fileHandlersInfo) {
            final FilenameFilter filter = (dir, name) -> name != null && name.startsWith(info.getPrefix());
            configure(info, filter, archDirVsLogFiles, logsDirVsDeleteLogFiles);
        }
    }
    
    private static void configure(final HandlerInfo info, final FilenameFilter filter, final Map<String, Map<String, List<File>>> archDirVsLogFiles, final Map<String, List<File>> logsDirVsDeleteLogFiles) throws ParseException {
        final String dirName = info.getLogDir();
        final String prefixStr = info.getPrefix();
        final File logDir = new File(dirName);
        final File[] filesInLogDir = logDir.listFiles(filter);
        getLogger().log(Level.INFO, "isArchive enabled for handler {0} ::: {1}", new Object[] { info.getHandlerName(), info.isArchiveEnabled() });
        if (!info.isArchiveEnabled()) {
            List<File> deleteFileList = logsDirVsDeleteLogFiles.get(dirName);
            if (deleteFileList == null) {
                deleteFileList = new ArrayList<File>();
            }
            final List<File> excludedFiles = new LinkedList<File>();
            for (final File logFile : filesInLogDir) {
                if (isLogFileOlderThan(logFile.getName(), info)) {
                    deleteFileList.add(logFile);
                }
                else {
                    excludedFiles.add(logFile);
                }
            }
            deleteFileList.addAll(logFilesOlderOnCurrentDate(excludedFiles, info));
            logsDirVsDeleteLogFiles.put(dirName, deleteFileList);
        }
        else {
            final String archDir = info.getArchiveDir();
            Map<String, List<File>> filePatternVsFiles = archDirVsLogFiles.get(archDir);
            if (filePatternVsFiles == null) {
                filePatternVsFiles = new HashMap<String, List<File>>();
            }
            List<File> listOfFilesToBeCompress = filePatternVsFiles.get(prefixStr);
            if (listOfFilesToBeCompress == null) {
                listOfFilesToBeCompress = new ArrayList<File>();
            }
            final List<File> excludedFiles2 = new LinkedList<File>();
            for (final File logFile2 : filesInLogDir) {
                if (isLogFileOlderThan(logFile2.getName(), info)) {
                    listOfFilesToBeCompress.add(logFile2);
                }
                else {
                    excludedFiles2.add(logFile2);
                }
            }
            listOfFilesToBeCompress.addAll(logFilesOlderOnCurrentDate(excludedFiles2, info));
            filePatternVsFiles.put(prefixStr, listOfFilesToBeCompress);
            archDirVsLogFiles.put(archDir, filePatternVsFiles);
        }
    }
    
    private static List<File> logFilesOlderOnCurrentDate(final List<File> filesInLogDir, final HandlerInfo info) {
        if (filesInLogDir.size() > info.getMaxFilesPerDay()) {
            filesInLogDir.sort(Comparator.comparingLong(File::lastModified).reversed());
            return filesInLogDir.subList(info.getMaxFilesPerDay(), filesInLogDir.size());
        }
        return Collections.emptyList();
    }
    
    private static void compressAndDeleteLogFiles(final String prefix, final String archiveDir, final List<File> fileNamesList) throws Exception {
        final File dir = new File(archiveDir);
        if (fileNamesList.isEmpty()) {
            getLogger().info("No files in the compress file list hence archive creation ignored");
            return;
        }
        final SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        final String dateStr = currentDate.format(System.currentTimeMillis());
        String zipFileName = prefix + dateStr + ".zip";
        if (dir.exists()) {
            final String[] existingArchiveFiles = dir.list();
            final List<String> zipFileNamesList = Arrays.asList(existingArchiveFiles);
            for (int i = 0; zipFileNamesList.contains(zipFileName); zipFileName = prefix + dateStr + "_" + i + ".zip") {
                ++i;
            }
        }
        getLogger().log(Level.INFO, "Going to create file :: {0}", zipFileName);
        dir.mkdirs();
        zipLogFiles(archiveDir + zipFileName, fileNamesList);
        deleteLogFiles(fileNamesList);
    }
    
    private static void zipLogFiles(final String zipFileName, final List<File> fileNamesList) throws Exception {
        try (final ZipOutputStream zOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFileName, true)))) {
            final int buffer = 2048;
            final byte[] data = new byte[buffer];
            final List<String> addedNames = new ArrayList<String>();
            for (final File fileName : fileNamesList) {
                if (addedNames.contains(fileName.getAbsolutePath())) {
                    getLogger().warning("Duplicate log file name [" + fileName + "] received, hence ignoring.");
                }
                else {
                    addedNames.add(fileName.getAbsolutePath());
                    getLogger().log(Level.INFO, "Adding: {0}", fileName);
                    try (final BufferedInputStream buffInputStream = new BufferedInputStream(new FileInputStream(fileName), buffer)) {
                        final ZipEntry entry = new ZipEntry(fileName.getName());
                        entry.setMethod(8);
                        zOut.putNextEntry(entry);
                        int count;
                        while ((count = buffInputStream.read(data, 0, buffer)) != -1) {
                            zOut.write(data, 0, count);
                        }
                    }
                }
            }
        }
    }
    
    private static void deleteLogFiles(final List<File> fileNamesList) {
        final List<File> deletedFileList = new ArrayList<File>();
        try {
            for (final File logFile : fileNamesList) {
                getLogger().log(Level.INFO, "Deleting old log file {0}", logFile.getName());
                logFile.delete();
                deletedFileList.add(logFile);
            }
        }
        finally {
            fileNamesList.removeAll(deletedFileList);
        }
    }
    
    private static boolean isLogFileOlderThan(final String fileName, final HandlerInfo info) throws ParseException {
        final int interval = info.getArchiveInterval();
        final Calendar modifiedDate = Calendar.getInstance();
        final Calendar currentDate = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        final StringBuilder strBuff = new StringBuilder(fileName);
        strBuff.delete(0, info.getPrefix().length());
        strBuff.delete(strBuff.length() - info.getSuffix().length(), strBuff.length());
        final String createdDate = strBuff.toString();
        currentDate.setTimeInMillis(System.currentTimeMillis());
        modifiedDate.setTimeInMillis(dateFormatter.parse(createdDate).getTime());
        final long olderThan = (currentDate.getTime().getTime() - modifiedDate.getTime().getTime()) / 86400000L;
        return olderThan >= interval;
    }
    
    private static Logger getLogger() {
        if (LogsArchiver.logger == null) {
            LogsArchiver.logger = Logger.getLogger(LogsArchiver.class.getName());
        }
        return LogsArchiver.logger;
    }
    
    static void archiveLogs(final HandlerInfo info, final FilenameFilter filter) throws Exception {
        final Map<String, Map<String, List<File>>> archDirVsLogFiles = new HashMap<String, Map<String, List<File>>>();
        final Map<String, List<File>> logsDirVsDeleteLogFiles = new HashMap<String, List<File>>();
        configure(info, filter, archDirVsLogFiles, logsDirVsDeleteLogFiles);
        cleanup(archDirVsLogFiles, logsDirVsDeleteLogFiles);
    }
    
    static {
        LogsArchiver.fileHandlersInfo = new ArrayList<HandlerInfo>();
        LogsArchiver.logger = null;
    }
}
