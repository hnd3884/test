package com.zoho.framework.utils;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.CopyOption;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.io.PrintWriter;
import java.io.FileFilter;
import java.security.MessageDigest;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.io.RandomAccessFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.io.File;
import java.util.logging.Logger;

public class FileUtils
{
    private static final Logger LOGGER;
    private static final char[] HEXARRAY;
    
    public static boolean deleteDir(final File fileToDelete) {
        if (!fileToDelete.exists()) {
            FileUtils.LOGGER.log(Level.INFO, "File :: [{0}], Type :: Directory, Exists? :: false", fileToDelete);
            return true;
        }
        FileUtils.LOGGER.log(Level.FINE, "Attempting to Delete Directory :: [{0}]", fileToDelete);
        if (fileToDelete.isDirectory()) {
            boolean deleteDir = true;
            final File[] filesInDir = fileToDelete.listFiles();
            if (filesInDir != null && filesInDir.length != 0) {
                for (final File file : filesInDir) {
                    boolean deleteSubEntry = false;
                    if (file.isFile()) {
                        deleteSubEntry = deleteFile(file);
                        deleteDir = (deleteDir && deleteSubEntry);
                    }
                    else {
                        deleteSubEntry = deleteDir(file);
                        deleteDir = (deleteDir && deleteSubEntry);
                    }
                    if (!deleteSubEntry) {
                        FileUtils.LOGGER.log(Level.SEVERE, "Error occurred while deleting the file :: {0}", file.getName());
                    }
                    else {
                        FileUtils.LOGGER.log(Level.FINE, "Cleaning up the File :: [{0}]", file.getAbsolutePath());
                    }
                }
            }
            final boolean deleteCurrentEntry = fileToDelete.delete();
            deleteDir = (deleteDir && deleteCurrentEntry);
            if (!deleteCurrentEntry) {
                FileUtils.LOGGER.log(Level.SEVERE, "Error occurred while deleting the file :: {0}", fileToDelete.getName());
            }
            else {
                FileUtils.LOGGER.log(Level.FINE, "Cleaning up the File :: [{0}]", fileToDelete.getAbsolutePath());
            }
            return deleteDir;
        }
        FileUtils.LOGGER.log(Level.WARNING, "File :: [{0}], Type :: File", fileToDelete.getAbsolutePath());
        return false;
    }
    
    public static boolean deleteDir(final String fileToDelete) {
        final File fileToBeDeleted = new File(fileToDelete);
        return deleteDir(fileToBeDeleted);
    }
    
    public static boolean deleteFile(final File fileToBeDeleted) {
        if (!fileToBeDeleted.exists()) {
            FileUtils.LOGGER.log(Level.FINE, "File :: [{0}], Type :: File, Exists? :: false", fileToBeDeleted);
            return true;
        }
        FileUtils.LOGGER.log(Level.FINE, "Attempting to Delete File :: [{0}]", fileToBeDeleted);
        if (fileToBeDeleted.isFile()) {
            final boolean deleted = fileToBeDeleted.delete();
            if (!deleted) {
                FileUtils.LOGGER.log(Level.SEVERE, "Error occurred while deleting the file :: {0}", fileToBeDeleted.getName());
            }
            else {
                FileUtils.LOGGER.log(Level.FINE, "Cleaning up the File :: [{0}]", fileToBeDeleted.getAbsolutePath());
            }
            return deleted;
        }
        FileUtils.LOGGER.log(Level.WARNING, "File :: [{0}], Type :: Directory", fileToBeDeleted.getAbsolutePath());
        return false;
    }
    
    public static boolean deleteFile(final String fileToBeDeleted) {
        final File fileToDelete = new File(fileToBeDeleted);
        return deleteFile(fileToDelete);
    }
    
    public static boolean deleteFiles(final List<String> filesToBeDeleted) {
        boolean deleteFiles = true;
        for (final String filePath : filesToBeDeleted) {
            final File fileToDelete = new File(filePath);
            if (fileToDelete.isFile()) {
                deleteFiles = (deleteFile(fileToDelete) && deleteFiles);
            }
            else {
                deleteFiles = (deleteDir(fileToDelete) && deleteFiles);
            }
        }
        return deleteFiles;
    }
    
    public static boolean deleteFiles(final File... filesToBeDeleted) {
        boolean deleteFiles = true;
        for (final File fileToDelete : filesToBeDeleted) {
            if (fileToDelete.isFile()) {
                deleteFiles = (deleteFile(fileToDelete) && deleteFiles);
            }
            else {
                deleteFiles = (deleteDir(fileToDelete) && deleteFiles);
            }
        }
        return deleteFiles;
    }
    
    public static boolean deleteFiles(final String... filesToBeDeleted) {
        return deleteFiles(Arrays.asList(filesToBeDeleted));
    }
    
    public static void copyFile(final String sourceFilePath, final String destinationFilePath) throws IOException {
        copyFile(new File(sourceFilePath), new File(destinationFilePath));
    }
    
    public static void copyFile(final File sourceFile, final File destinationFile) throws IOException {
        FileUtils.LOGGER.log(Level.INFO, "Entered into copyFile :: sourceFile :: [{0}], destinationFile :: [{1}]", new Object[] { sourceFile, destinationFile });
        final int BUFFER_LENGTH = 2048;
        destinationFile.getParentFile().mkdirs();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(sourceFile);
            out = new FileOutputStream(destinationFile);
            final byte[] data = new byte[BUFFER_LENGTH];
            int count;
            while ((count = in.read(data, 0, BUFFER_LENGTH)) != -1) {
                out.write(data, 0, count);
            }
            out.flush();
        }
        finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
    
    private static void validateInput(final File dir) {
        if (dir == null) {
            throw new IllegalArgumentException("The given dir should not be null");
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("The given file should be a directory");
        }
        final File[] filesInDir = dir.listFiles();
        if (filesInDir != null && filesInDir.length == 0) {
            throw new IllegalArgumentException("The given directory is empty");
        }
    }
    
    public static File[] listFiles(final File dir, final String startsWith, final String endsWith) {
        validateInput(dir);
        final FileNameFilter filter = new FileNameFilter(startsWith, endsWith);
        return dir.listFiles(filter);
    }
    
    public static File[] listFiles(final File dir, final boolean isDirectory) {
        validateInput(dir);
        final FileNameFilter filter = new FileNameFilter(isDirectory);
        return dir.listFiles(filter);
    }
    
    public static File[] listFiles(final File dir, final String pattern) {
        return listFiles(dir, pattern, true);
    }
    
    public static File[] listFiles(final File dir, final String pattern, final boolean isCaseSensitive) {
        validateInput(dir);
        int flag = 0;
        if (!isCaseSensitive) {
            flag = 2;
        }
        return listFiles(dir, getPatternAsList(Pattern.compile(pattern, flag)), null);
    }
    
    private static List<Pattern> getPatternAsList(final Pattern pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern cannot be null");
        }
        final List<Pattern> patternList = new ArrayList<Pattern>();
        patternList.add(pattern);
        return patternList;
    }
    
    public static File[] listFiles(final File dir, final Pattern pattern) {
        return listFiles(dir, getPatternAsList(pattern), null);
    }
    
    public static File[] listFiles(final File dir, final List<Pattern> includePattern, final List<Pattern> excludePattern) {
        validateInput(dir);
        final FileNameFilter filter = new FileNameFilter(includePattern, excludePattern);
        return dir.listFiles(filter);
    }
    
    public static Properties readPropertyFile(final File propertyFile) throws IOException {
        FileUtils.LOGGER.log(Level.INFO, "readPropertyFile :: {0}", propertyFile);
        try (final InputStream is = new FileInputStream(propertyFile)) {
            return readPropertyFile(is);
        }
    }
    
    public static Properties readPropertyFromZip(final String zipFile, final String fileInZip) throws IOException {
        if (zipFile == null || fileInZip == null) {
            throw new IllegalArgumentException("Zip File or file to read properties from cannot be null");
        }
        final File file = new File(zipFile);
        if (!file.exists()) {
            throw new FileNotFoundException("The zip file " + file + " is not present");
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("The value " + zipFile + " is not a file");
        }
        try (final ZipFile zipFileInstance = new ZipFile(file)) {
            final ZipEntry entry = zipFileInstance.getEntry(fileInZip);
            if (entry == null) {
                throw new FileNotFoundException("Given zip " + zipFile + " has no file " + fileInZip);
            }
            FileUtils.LOGGER.log(Level.FINE, "Reading properties from file [{0}] in zip [{1}]", new Object[] { fileInZip, zipFile });
            try (final InputStream is = zipFileInstance.getInputStream(entry)) {
                return readPropertyFile(is);
            }
        }
    }
    
    private static Properties readPropertyFile(final InputStream is) throws IOException {
        final Properties indexProps = new Properties();
        indexProps.load(is);
        FileUtils.LOGGER.log(Level.FINE, "readProperty :: returning props :: {0}", indexProps);
        return indexProps;
    }
    
    public static void writeToFile(final File outFile, final Properties props, final String header) throws IOException {
        FileUtils.LOGGER.log(Level.INFO, "writeToFile :: props :: {0} outFile :: [{1}] header :: [{2}]", new Object[] { props, outFile, header });
        OutputStream os = null;
        try {
            os = new FileOutputStream(outFile);
            props.store(os, (header == null) ? "" : header);
        }
        finally {
            if (os != null) {
                os.close();
            }
        }
    }
    
    public static void changePermissionForFile(final String filePath) throws IOException, InterruptedException {
        if (OSCheckUtil.getOS() != 2) {
            final List<String> commandList = new ArrayList<String>();
            commandList.add("chmod");
            commandList.add("-R");
            commandList.add("755");
            commandList.add(filePath);
            final boolean writeToFile = true;
            final Process p = executeCommand(commandList, writeToFile, true);
            p.waitFor();
            FileUtils.LOGGER.log(Level.INFO, "File Permissions successfully changed for the file :: [{0}]", filePath);
        }
        else {
            FileUtils.LOGGER.log(Level.SEVERE, "changePermissionForFile not implemented for Windows");
        }
    }
    
    private static Process executeCommand(final List<String> commandList, final boolean writeToFile, final boolean executeCmd) throws IOException {
        if (!writeToFile || OSCheckUtil.getOS() == 2) {
            FileUtils.LOGGER.log(Level.INFO, "Command to be executed ::: {0}", commandList);
            final ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            return processBuilder.start();
        }
        final File serverHome = new File(System.getProperty("server.home", ".."));
        final File extFile = new File(serverHome.getAbsolutePath() + File.separator + "ext.sh");
        FileUtils.LOGGER.log(Level.INFO, "Writing comman to ext.sh file ::: {0}", commandList);
        final RandomAccessFile f = new RandomAccessFile(extFile.getAbsolutePath(), "rw");
        if (extFile.length() != 0L) {
            f.seek(extFile.length());
            f.write(System.getProperty("line.separator").getBytes());
        }
        for (final String cmd : commandList) {
            f.write(cmd.toString().getBytes());
            f.write(" ".getBytes());
        }
        f.close();
        if (executeCmd) {
            FileUtils.LOGGER.info("Executing all commands in ext.sh ");
            final List<String> extCmdList = new ArrayList<String>();
            extCmdList.add("sh");
            extCmdList.add(extFile.getAbsolutePath());
            FileUtils.LOGGER.log(Level.INFO, "Command to be executed ::: {0}", extCmdList);
            final ProcessBuilder processBuilder = new ProcessBuilder(extCmdList);
            return processBuilder.start();
        }
        return null;
    }
    
    private static boolean createParentDirs(final File dir) {
        try {
            Files.createDirectories(dir.getParentFile().toPath(), (FileAttribute<?>[])new FileAttribute[0]);
        }
        catch (final IOException e) {
            FileUtils.LOGGER.log(Level.INFO, "unable to create parent directories :: {0} ", e.getMessage());
            return false;
        }
        return true;
    }
    
    public static boolean moveDirectory(final String srcDirPath, final String destDirPath) {
        if (!createParentDirs(new File(destDirPath))) {
            return false;
        }
        final File fromDir = new File(srcDirPath);
        if (fromDir.exists() && fromDir.isDirectory()) {
            final File toDir = new File(destDirPath);
            FileUtils.LOGGER.log(Level.INFO, "Renaming directory {0} to {1}.", new String[] { fromDir.getAbsolutePath(), toDir.getAbsolutePath() });
            return fromDir.renameTo(toDir);
        }
        FileUtils.LOGGER.log(Level.WARNING, "Unable to peform the rename of directory {0} to {1}", new String[] { srcDirPath, destDirPath });
        return false;
    }
    
    public static String getDigest(final String filePath) {
        try {
            final byte[] b = createChecksum(filePath);
            return bytesToHex(b);
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static byte[] createChecksum(final String filename) throws Exception {
        InputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            final byte[] buffer = new byte[1024];
            final MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead;
            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);
            return complete.digest();
        }
        finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
    
    public static String bytesToHex(final byte[] bytes) {
        final char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; ++i) {
            final int v = bytes[i] & 0xFF;
            hexChars[i * 2] = FileUtils.HEXARRAY[v >>> 4];
            hexChars[i * 2 + 1] = FileUtils.HEXARRAY[v & 0xF];
        }
        return new String(hexChars);
    }
    
    public static void main(final String[] args) {
        if (args.length > 0) {
            final String s = args[0];
            switch (s) {
                case "digest": {
                    if (args.length > 1) {
                        for (int i = 1; i < args.length; ++i) {
                            final File inputFile = new File(args[i]);
                            if (inputFile.exists() && inputFile.isDirectory()) {
                                final File[] fileList = inputFile.listFiles(new FileFilter() {
                                    @Override
                                    public boolean accept(final File pathname) {
                                        return !pathname.getName().endsWith(".digest");
                                    }
                                });
                                if (fileList != null) {
                                    for (final File file : fileList) {
                                        generateDigestFile(file.getAbsolutePath());
                                    }
                                }
                            }
                            else if (inputFile.exists() && inputFile.isFile()) {
                                generateDigestFile(inputFile.getAbsolutePath());
                            }
                        }
                        break;
                    }
                    throw new IllegalArgumentException("No file specified");
                }
            }
        }
    }
    
    public static void generateDigestFile(final String filePath) {
        if (new File(filePath).isDirectory()) {
            throw new IllegalArgumentException("Digest cannot be created for directory");
        }
        final File digestFile = new File(filePath + ".digest");
        final String digestValue = getDigest(filePath);
        try {
            final PrintWriter pw = new PrintWriter(digestFile);
            pw.write(digestValue);
            pw.close();
        }
        catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static boolean moveWithRetry(final File source, final File destination) {
        final boolean canMove = checkEntityCanBeMoved(source, destination);
        if (!canMove) {
            return false;
        }
        boolean moved = moveDirectory(source, destination);
        if (moved) {
            return true;
        }
        try {
            FileUtils.LOGGER.info("Will try move after 2 sec with NIO");
            Thread.sleep(TimeUnit.SECONDS.toMillis(2L));
        }
        catch (final InterruptedException e) {
            e.printStackTrace();
        }
        moved = moveDirectoryNIO(source, destination);
        if (moved) {
            return true;
        }
        try {
            FileUtils.LOGGER.info("Will try move after 3 sec with native command");
            Thread.sleep(TimeUnit.SECONDS.toMillis(3L));
        }
        catch (final InterruptedException e) {
            e.printStackTrace();
        }
        moved = moveDirectoryWithNativeCommand(source, destination);
        return moved;
    }
    
    public static boolean moveDirectoryWithRetry(final File from, final File to) {
        final boolean canMove = checkDirectoryCanBeMoved(from, to);
        return canMove && moveWithRetry(from, to);
    }
    
    public static boolean moveDirectoryWithNativeCommand(final File from, final File to) {
        try {
            if (!createParentDirs(to)) {
                return false;
            }
            String[] cmds;
            if (OSCheckUtil.isWindows(OSCheckUtil.getOS())) {
                cmds = new String[] { "cmd", "/c", "MOVE", "\"" + from.getCanonicalPath() + "\"", "\"" + to.getCanonicalPath() + "\"" };
            }
            else {
                cmds = new String[] { "mv", from.getCanonicalPath(), to.getCanonicalPath() };
            }
            final int exitValue = executeCommand(cmds);
            final boolean moved = exitValue == 0;
            FileUtils.LOGGER.log(Level.INFO, "Move {0} when trying to move folder from [{1}] to [{2}]", new Object[] { moved ? "success" : "failed", from.getCanonicalPath(), to.getCanonicalFile() });
            return moved;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean copyWithNativeCommand(final File source, final File destination) {
        try {
            final boolean isWindows = OSCheckUtil.isWindows(OSCheckUtil.getOS());
            if (Objects.isNull(source) || Objects.isNull(destination)) {
                FileUtils.LOGGER.log(Level.WARNING, "Either source or destination is null");
            }
            if (!source.exists()) {
                FileUtils.LOGGER.log(Level.WARNING, "source does not exist");
            }
            if (source.isDirectory() && !destination.exists()) {
                Files.createDirectories(destination.toPath(), (FileAttribute<?>[])new FileAttribute[0]);
            }
            else if (!source.isDirectory() || !destination.getParentFile().exists()) {
                createParentDirs(destination);
            }
            boolean isRoboCopy = false;
            String[] cmds;
            if (isWindows) {
                if (source.isDirectory()) {
                    cmds = new String[] { "ROBOCOPY", "/E", "\"" + source.getCanonicalPath() + "\"", "\"" + destination.getCanonicalPath() + "\"" };
                    isRoboCopy = true;
                }
                else {
                    cmds = new String[] { "cmd", "/c", "COPY", "\"" + source.getCanonicalPath() + "\"", "\"" + destination.getCanonicalPath() + "\"" };
                }
            }
            else {
                cmds = new String[] { "bash", "-c", "cp -rfv '" + source.getCanonicalPath() + (source.isDirectory() ? "'/*" : "'") + " '" + destination.getCanonicalPath() + "'" };
            }
            final int exitValue = executeCommand(cmds);
            final boolean copied = (isRoboCopy && exitValue < 7) || exitValue == 0;
            FileUtils.LOGGER.log(Level.INFO, "Copy {0} when trying to copy file from [{1}] to [{2}]", new Object[] { copied ? "success" : "failed", source.getCanonicalPath(), destination.getCanonicalFile() });
            return copied;
        }
        catch (final Exception e) {
            FileUtils.LOGGER.log(Level.WARNING, e.getMessage(), e);
            return false;
        }
    }
    
    public static boolean moveDirectoryNIO(final File from, final File to) {
        try {
            if (!createParentDirs(to)) {
                return false;
            }
            Files.move(from.toPath(), to.toPath(), new CopyOption[0]);
            final boolean moved = checkMoved(from, to);
            FileUtils.LOGGER.log(Level.INFO, "Move {0} when trying to move folder from [{1}] to [{2}]", new Object[] { moved ? "success" : "failed", from.getCanonicalPath(), to.getCanonicalFile() });
            return moved;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean moveDirectory(final File from, final File to) {
        if (!createParentDirs(to)) {
            return false;
        }
        boolean moved = from.renameTo(to);
        if (moved) {
            try {
                moved = checkMoved(from, to);
                FileUtils.LOGGER.log(Level.INFO, "Move {0} when trying to move folder from [{1}] to [{2}]", new Object[] { moved ? "success" : "failed", from.getCanonicalPath(), to.getCanonicalFile() });
            }
            catch (final IOException e) {
                e.printStackTrace();
                moved = false;
            }
        }
        return moved;
    }
    
    public static boolean checkMoved(final File from, final File to) throws IOException {
        boolean moved = true;
        if (!to.exists()) {
            FileUtils.LOGGER.log(Level.INFO, "Rename to [{0}] failed", from.getCanonicalPath());
            moved = false;
        }
        if (from.exists()) {
            FileUtils.LOGGER.log(Level.INFO, "Rename from [{0}] failed", to.getCanonicalPath());
            moved = false;
        }
        return moved;
    }
    
    public static boolean checkDirectoryCanBeMoved(final File from, final File to) {
        if (from == null) {
            FileUtils.LOGGER.warning("Directory to be moved cannot be null");
            return false;
        }
        if (to == null) {
            FileUtils.LOGGER.warning("New Directory cannot be null");
            return false;
        }
        if (!from.isDirectory()) {
            FileUtils.LOGGER.warning("File to be moved should be a directory");
            return false;
        }
        if (to.exists()) {
            FileUtils.LOGGER.warning("New direcotry to be moved-to must not exists");
            return false;
        }
        return true;
    }
    
    private static boolean checkEntityCanBeMoved(final File source, final File destination) {
        if (source == null) {
            FileUtils.LOGGER.warning("Directory to be moved cannot be null");
            return false;
        }
        if (destination == null) {
            FileUtils.LOGGER.warning("New Directory cannot be null");
            return false;
        }
        if (!source.exists()) {
            FileUtils.LOGGER.warning("source file/folder does not exist");
            return false;
        }
        if (destination.exists()) {
            FileUtils.LOGGER.warning("New file/folder to be moved-to must not exist");
            return false;
        }
        return true;
    }
    
    protected static int executeCommand(final String[] cmds) {
        Process process = null;
        try {
            final ProcessBuilder pb = new ProcessBuilder(cmds);
            process = pb.start();
            String line = null;
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = br.readLine()) != null) {
                FileUtils.LOGGER.info(line);
            }
            br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = br.readLine()) != null) {
                FileUtils.LOGGER.warning(line);
            }
            FileUtils.LOGGER.log(Level.INFO, "Process waitFor Returns :: [{0}]", process.waitFor());
            final int exitValue = process.exitValue();
            FileUtils.LOGGER.log(Level.INFO, "Process exit value :: [{0}]", exitValue);
            return exitValue;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return -1;
        }
        finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
    
    public static boolean isExistsInJar(final String jarNameWithFullPath, final String entryNameWithPackage) throws Exception {
        final File jarFile = new File(jarNameWithFullPath);
        if (!jarFile.exists()) {
            return false;
        }
        final JarFile jar = new JarFile(jarFile);
        try {
            final JarEntry entry = jar.getJarEntry(entryNameWithPackage);
            return entry != null;
        }
        finally {
            jar.close();
        }
    }
    
    public static boolean isExistsInZip(final String zipNameWithFullPath, final String entryNameWithPackage) throws Exception {
        final ZipFile zip = new ZipFile(zipNameWithFullPath);
        try {
            final ZipEntry entry = zip.getEntry(entryNameWithPackage);
            return entry != null;
        }
        finally {
            zip.close();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(FileUtils.class.getName());
        HEXARRAY = "0123456789ABCDEF".toCharArray();
    }
}
