package com.zoho.framework.utils.archive;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.nio.file.Path;
import java.util.Arrays;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.Properties;
import com.zoho.framework.utils.FileUtils;
import com.zoho.conf.Configuration;
import com.zoho.framework.utils.OSCheckUtil;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.logging.Level;
import java.io.IOException;
import java.util.List;
import java.io.File;
import java.util.logging.Logger;

public class SevenZipUtils
{
    private static final Logger LOGGER;
    private static String server_home;
    private static final boolean IS_WINDOWS;
    private static String sevenZPath;
    
    public static int zip(final File zipFolder, final String zipFileName, final File contentDirectory, final boolean includeContentDirectoryToo, final boolean includeFilesInContentDirectory, final List<String> includeFileList, final List<String> excludeFileList) throws IOException, InterruptedException {
        return zip(zipFolder, zipFileName, contentDirectory, includeContentDirectoryToo, includeFilesInContentDirectory, includeFileList, excludeFileList, null, null);
    }
    
    public static int zip(final File zipFile, final File entryToInclude, final String archivePassword) throws IOException, InterruptedException {
        return zip(zipFile.getParentFile(), zipFile.getName(), entryToInclude, true, false, null, null, archivePassword, null);
    }
    
    public static int zip(final File zipFolder, final String zipFileName, final File contentDirectory, final boolean includeContentDirectoryToo, final boolean includeFilesInContentDirectory, final List<String> includeFileList, final List<String> excludeFileList, final String archivePassword, final String encAlgo) throws IOException, InterruptedException {
        SevenZipUtils.LOGGER.log(Level.INFO, "Entered createZip(), backupDir :: [{0}], zipFileName :: [{1}], contentDirectory :: [{2}], includeContentDirectoryToo :: [{3}], includeFileList :: [{4}], excludeFileList :: [{5}]", new Object[] { zipFolder, zipFileName, contentDirectory, includeContentDirectoryToo, includeFileList, excludeFileList });
        if (!zipFolder.exists()) {
            zipFolder.mkdirs();
        }
        SevenZipUtils.LOGGER.log(Level.INFO, "Going to create the Incremental/Full BackUpFile :: [{0}]", zipFileName);
        final List<String> commandList = new ArrayList<String>();
        commandList.add(get7zPath());
        commandList.add("u");
        commandList.add(zipFolder.getAbsolutePath() + File.separator + zipFileName);
        commandList.add("-tzip");
        if (includeContentDirectoryToo) {
            commandList.add(contentDirectory.getAbsolutePath());
        }
        if (includeFilesInContentDirectory) {
            commandList.add(contentDirectory.getAbsolutePath() + File.separator + "*");
        }
        if (includeFileList != null) {
            for (final String file : includeFileList) {
                commandList.add("-i!" + file);
            }
        }
        if (excludeFileList != null) {
            for (final String file : excludeFileList) {
                commandList.add("-x!" + file);
            }
        }
        addEncryptionToCommand(commandList, archivePassword, encAlgo);
        final int exitValue = executeProcess(commandList);
        SevenZipUtils.LOGGER.log(Level.INFO, "Exiting createZip() with value :: [{0}]", exitValue);
        return exitValue;
    }
    
    public static int appendInZip(final String zipFilePath, final List<String> includeFileList) throws IOException, InterruptedException {
        return appendInZip(zipFilePath, includeFileList, null, null);
    }
    
    public static int appendInZip(final String zipFilePath, final List<String> includeFileList, final String archivePassword, final String encAlgo) throws IOException, InterruptedException {
        SevenZipUtils.LOGGER.log(Level.INFO, "Entered appendInZip(), zipFileName :: [{0}], includeFileList :: [{1}],", new Object[] { zipFilePath, includeFileList });
        if (!new File(zipFilePath).exists()) {
            throw new IOException("Zip File does not exists :: " + zipFilePath);
        }
        SevenZipUtils.LOGGER.log(Level.INFO, "Going to append files to zip :: [{0}]", zipFilePath);
        final List<String> commandList = new ArrayList<String>();
        commandList.add(get7zPath());
        commandList.add("u");
        commandList.add(new File(zipFilePath).getAbsolutePath());
        commandList.add("-tzip");
        if (includeFileList != null) {
            for (final String file : includeFileList) {
                commandList.add("-i!" + file);
            }
        }
        addEncryptionToCommand(commandList, archivePassword, encAlgo);
        final int exitValue = executeProcess(commandList);
        SevenZipUtils.LOGGER.log(Level.INFO, "Exiting appendInZip() with value :: [{0}]", exitValue);
        return exitValue;
    }
    
    public static int unZip(final File zipFile, final File destinationFolder, final List<String> includeFileList, final List<String> excludeFileList) throws IOException, InterruptedException {
        return unZip(zipFile, destinationFolder, includeFileList, excludeFileList, null);
    }
    
    public static int unZip(final File zipFile, final File destinationFolder, final List<String> includeFileList, final List<String> excludeFileList, final String archivePassword) throws IOException, InterruptedException {
        SevenZipUtils.LOGGER.log(Level.INFO, "Entered extract7zipFile() :: zipFileName :: [{0}]", zipFile);
        final List<String> commandList = new ArrayList<String>();
        commandList.add(get7zPath());
        commandList.add("x");
        commandList.add(zipFile.getCanonicalPath());
        commandList.add("-y");
        commandList.add("-o" + ((destinationFolder == null) ? "tmp" : destinationFolder.getCanonicalPath()));
        if (includeFileList != null) {
            for (final String file : includeFileList) {
                commandList.add("-i!" + file);
            }
        }
        if (excludeFileList != null) {
            for (final String file : excludeFileList) {
                commandList.add("-x!" + file);
            }
        }
        if (archivePassword != null && archivePassword.length() > 0) {
            commandList.add(getPasswordArgument(archivePassword));
        }
        final int exitValue = executeProcess(commandList);
        SevenZipUtils.LOGGER.log(Level.INFO, "Exiting extract7zipFile() with value :: [{0}]", exitValue);
        return exitValue;
    }
    
    private static void addEncryptionToCommand(final List<String> commandList, final String archivePassword, final String encAlgo) {
        if (archivePassword != null && archivePassword.length() > 0) {
            commandList.add(getPasswordArgument(archivePassword));
            if (encAlgo != null) {
                commandList.add("-mem=" + encAlgo);
            }
        }
    }
    
    private static String getPasswordArgument(final String archivePassword) {
        if (SevenZipUtils.IS_WINDOWS) {
            return "-p\"" + archivePassword + '\"';
        }
        return "-p" + archivePassword;
    }
    
    private static String get7zPath() throws IOException, InterruptedException {
        if (SevenZipUtils.sevenZPath != null) {
            return SevenZipUtils.sevenZPath;
        }
        final File sevenZip64 = new File(SevenZipUtils.server_home + File.separator + "tools" + File.separator + "archiver" + File.separator + "7za_64");
        final File sevenZip65 = new File(SevenZipUtils.server_home + File.separator + "tools" + File.separator + "archiver" + File.separator + "7za");
        final File sevenZipMac = new File(SevenZipUtils.server_home + File.separator + "tools" + File.separator + "archiver" + File.separator + "7za_mac");
        final File sevenZipWindows = new File(SevenZipUtils.server_home + File.separator + "tools" + File.separator + "archiver" + File.separator + "7za.exe");
        if (OSCheckUtil.isWindows(OSCheckUtil.getOS())) {
            if (sevenZip64.exists()) {
                SevenZipUtils.LOGGER.log(Level.INFO, "Deleting file :: " + sevenZip64.getCanonicalPath());
                sevenZip64.delete();
            }
            if (sevenZip65.exists()) {
                SevenZipUtils.LOGGER.log(Level.INFO, "Deleting file :: " + sevenZip65.getCanonicalPath());
                sevenZip65.delete();
            }
            if (sevenZipMac.exists()) {
                SevenZipUtils.LOGGER.log(Level.INFO, "Deleting file :: " + sevenZipMac.getCanonicalPath());
                sevenZipMac.delete();
            }
            SevenZipUtils.sevenZPath = Configuration.getString("tools.7zip.win.path", SevenZipUtils.server_home + File.separator + "tools" + File.separator + "archiver" + File.separator + "7za.exe");
        }
        else if (OSCheckUtil.getOSName().contains("linux")) {
            if (sevenZipWindows.exists()) {
                SevenZipUtils.LOGGER.log(Level.INFO, "Deleting file :: " + sevenZipWindows.getCanonicalPath());
                sevenZipWindows.delete();
            }
            if (sevenZipMac.exists()) {
                SevenZipUtils.LOGGER.log(Level.INFO, "Deleting file :: " + sevenZipMac.getCanonicalPath());
                sevenZipMac.delete();
            }
            if (Integer.parseInt(System.getProperty("sun.arch.data.model")) == 64) {
                if (sevenZip64.exists()) {
                    if (sevenZip65.exists()) {
                        SevenZipUtils.LOGGER.log(Level.INFO, "Deleting file :: " + sevenZip65.getCanonicalPath());
                        sevenZip65.delete();
                    }
                    if (!sevenZip64.renameTo(sevenZip65)) {
                        throw new IOException("Rename operation failed for file :: " + sevenZip64.getCanonicalPath());
                    }
                    SevenZipUtils.LOGGER.log(Level.INFO, "Renamed file :: " + sevenZip64.getCanonicalPath() + " to " + sevenZip65.getCanonicalPath());
                }
            }
            else if (sevenZip64.exists()) {
                SevenZipUtils.LOGGER.log(Level.INFO, "Deleting file :: " + sevenZip64.getCanonicalPath());
                sevenZip64.delete();
            }
            FileUtils.changePermissionForFile(Configuration.getString("tools.7zip.lin.path", SevenZipUtils.server_home + File.separator + "tools" + File.separator + "archiver" + File.separator + "7za"));
            SevenZipUtils.sevenZPath = Configuration.getString("tools.7zip.lin.path", SevenZipUtils.server_home + File.separator + "tools" + File.separator + "archiver" + File.separator + "7za");
        }
        else {
            if (sevenZipWindows.exists()) {
                SevenZipUtils.LOGGER.log(Level.INFO, "Deleting file :: " + sevenZipWindows.getCanonicalPath());
                sevenZipWindows.delete();
            }
            if (sevenZip64.exists()) {
                SevenZipUtils.LOGGER.log(Level.INFO, "Deleting file :: " + sevenZip64.getCanonicalPath());
                sevenZip64.delete();
            }
            if (sevenZipMac.exists()) {
                if (sevenZip65.exists()) {
                    SevenZipUtils.LOGGER.log(Level.INFO, "Deleting file :: " + sevenZip65.getCanonicalPath());
                    sevenZip65.delete();
                }
                if (!sevenZipMac.renameTo(sevenZip65)) {
                    throw new IOException("Rename operation failed for file :: " + sevenZip64.getCanonicalPath());
                }
                SevenZipUtils.LOGGER.log(Level.INFO, "Renamed file :: " + sevenZip64.getCanonicalPath() + " to " + sevenZip65.getCanonicalPath());
            }
            FileUtils.changePermissionForFile(Configuration.getString("tools.7zip.mac.path", SevenZipUtils.server_home + File.separator + "tools" + File.separator + "archiver" + File.separator + "7za"));
            SevenZipUtils.sevenZPath = Configuration.getString("tools.7zip.mac.path", SevenZipUtils.server_home + File.separator + "tools" + File.separator + "archiver" + File.separator + "7za");
        }
        return SevenZipUtils.sevenZPath;
    }
    
    private static Process executeCommand(final List<String> commandList) throws IOException {
        ProcessBuilder processBuilder = null;
        final StringBuilder sb = new StringBuilder("[");
        for (String string : commandList) {
            if (string.startsWith("-p")) {
                string = "-p*****";
            }
            sb.append(string).append(", ");
        }
        sb.append("]");
        SevenZipUtils.LOGGER.log(Level.INFO, "Command to be executed ::: {0}", sb);
        processBuilder = new ProcessBuilder(commandList);
        return processBuilder.start();
    }
    
    public static Properties readProperty(final String zipNameWithFullPath, final String entryNameWithPackage, final String archivePassword) throws IOException, InterruptedException {
        final Path folder = Files.createTempDirectory("temp_", (FileAttribute<?>[])new FileAttribute[0]);
        try {
            unZip(new File(zipNameWithFullPath), folder.toFile(), Arrays.asList(entryNameWithPackage), null, archivePassword);
            final File file = new File(folder + File.separator + entryNameWithPackage);
            return FileUtils.readPropertyFile(file);
        }
        finally {
            FileUtils.deleteDir(folder.toFile());
        }
    }
    
    public static boolean isFileExistsInZip(final String zipNameWithFullPath, final String entryNameWithPackage) throws IOException, InterruptedException {
        final List<String> commands = listArchiveEntriesCommand(zipNameWithFullPath, Arrays.asList(entryNameWithPackage));
        return checkCommandStreamForDelimiter(commands, "Encrypted = ");
    }
    
    public static boolean isZipFileEncrypted(final String zipNameWithFullPath) throws IOException, InterruptedException {
        return isZipFileEncrypted(zipNameWithFullPath, Collections.emptyList());
    }
    
    public static boolean isZipFileEncrypted(final String zipNameWithFullPath, final String entryNameWithPackage) throws IOException, InterruptedException {
        return isZipFileEncrypted(zipNameWithFullPath, Arrays.asList(entryNameWithPackage));
    }
    
    public static boolean isZipFileEncrypted(final String zipNameWithFullPath, final List<String> entriesNameWithPackage) throws IOException, InterruptedException {
        final List<String> commands = listArchiveEntriesCommand(zipNameWithFullPath, entriesNameWithPackage);
        return checkCommandStreamForDelimiter(commands, "Encrypted = +");
    }
    
    public static boolean canOpen(final String zipNameWithFullPath, final String password) throws IOException, InterruptedException {
        final List<String> commands = new ArrayList<String>(4);
        commands.add(get7zPath());
        commands.add("t");
        commands.add(zipNameWithFullPath);
        commands.add(getPasswordArgument(password));
        return !checkCommandStreamForDelimiter(commands, "Data Error in encrypted file. Wrong password?");
    }
    
    public static boolean canOpen(final String zipNameWithFullPath, final List<String> includeFileList, final String password) throws IOException, InterruptedException {
        final List<String> commands = new ArrayList<String>(4);
        commands.add(get7zPath());
        commands.add("t");
        commands.add(zipNameWithFullPath);
        if (includeFileList != null) {
            for (final String file : includeFileList) {
                commands.add("-i!" + file);
            }
        }
        commands.add(getPasswordArgument(password));
        return !checkCommandStreamForDelimiter(commands, "Data Error in encrypted file. Wrong password?");
    }
    
    private static List<String> listArchiveEntriesCommand(final String zipNameWithFullPath, final List<String> entriesNameWithPackage) throws IOException, InterruptedException {
        final List<String> commands = new ArrayList<String>(4 + entriesNameWithPackage.size());
        commands.add(get7zPath());
        commands.add("l");
        commands.add(zipNameWithFullPath);
        commands.add("-slt");
        commands.addAll(entriesNameWithPackage);
        return commands;
    }
    
    private static int executeProcess(final List<String> commands) throws IOException, InterruptedException {
        int exitValue = -1;
        Process start = null;
        try {
            start = executeCommand(commands);
            try (final BufferedReader ipBuf = new BufferedReader(new InputStreamReader(start.getInputStream()))) {
                String line = null;
                while ((line = ipBuf.readLine()) != null) {
                    SevenZipUtils.LOGGER.info(line);
                }
            }
            try (final BufferedReader errBuf = new BufferedReader(new InputStreamReader(start.getErrorStream()))) {
                String line = null;
                while ((line = errBuf.readLine()) != null) {
                    SevenZipUtils.LOGGER.warning(line);
                }
            }
            start.waitFor();
            exitValue = start.exitValue();
        }
        finally {
            if (start != null) {
                start.destroy();
            }
        }
        return exitValue;
    }
    
    private static boolean checkCommandStreamForDelimiter(final List<String> commands, final String delimiter) throws IOException, InterruptedException {
        boolean isEncrypted = false;
        Process start = null;
        try {
            start = executeCommand(commands);
            try (final BufferedReader ipBuf = new BufferedReader(new InputStreamReader(start.getInputStream()))) {
                String line = null;
                while ((line = ipBuf.readLine()) != null) {
                    if (!isEncrypted && line.contains(delimiter)) {
                        isEncrypted = true;
                        break;
                    }
                    SevenZipUtils.LOGGER.fine(line);
                }
            }
            try (final BufferedReader errBuf = new BufferedReader(new InputStreamReader(start.getErrorStream()))) {
                String line = null;
                while ((line = errBuf.readLine()) != null) {
                    SevenZipUtils.LOGGER.warning(line);
                }
            }
            start.waitFor();
            SevenZipUtils.LOGGER.info("Exit value :: " + start.exitValue());
        }
        finally {
            if (start != null) {
                start.destroy();
            }
        }
        return isEncrypted;
    }
    
    static {
        LOGGER = Logger.getLogger(SevenZipUtils.class.getName());
        SevenZipUtils.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
        IS_WINDOWS = OSCheckUtil.isWindows(OSCheckUtil.getOS());
        SevenZipUtils.sevenZPath = null;
    }
}
