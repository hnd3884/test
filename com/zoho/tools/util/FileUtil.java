package com.zoho.tools.util;

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.nio.file.DirectoryStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.logging.Level;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.logging.Logger;

public class FileUtil
{
    public static final Logger LOGGER;
    
    public static boolean isFilesDiffer(final File f1, final File f2) {
        final long crc1 = determineCRC(f1);
        final long crc2 = determineCRC(f2);
        return crc1 != crc2;
    }
    
    public static long determineCRC(final File file) {
        FileInputStream fis = null;
        if (file != null && file.exists()) {
            try {
                fis = new FileInputStream(file);
                final long crc = determineCRC(fis);
                return crc;
            }
            catch (final Exception ex) {
                FileUtil.LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                return -1L;
            }
            finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                }
                catch (final Exception e) {
                    FileUtil.LOGGER.log(Level.WARNING, "Exception occured while closing the streams.", e);
                }
            }
        }
        return -1L;
    }
    
    public static long determineCRC(final InputStream in) throws IOException {
        final CRC32 crc32 = new CRC32();
        final byte[] newBytes = new byte[2048];
        int read;
        while ((read = in.read(newBytes)) != -1) {
            crc32.update(newBytes, 0, read);
        }
        final long crc33 = crc32.getValue();
        return crc33;
    }
    
    @Deprecated
    public static void copyFile(final File source, final File dest) {
        try {
            dest.getParentFile().mkdirs();
            if (!dest.exists()) {
                dest.createNewFile();
            }
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(source);
                out = new FileOutputStream(dest);
                final int BUFFER_LENGTH = 2048;
                final byte[] buf = new byte[BUFFER_LENGTH];
                int len;
                while ((len = in.read(buf, 0, BUFFER_LENGTH)) > 0) {
                    out.write(buf, 0, len);
                }
                out.flush();
            }
            finally {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }
        }
        catch (final IOException ioe) {
            FileUtil.LOGGER.log(Level.SEVERE, ioe.getMessage(), ioe);
        }
    }
    
    public static boolean copy(final File srcFile, final File destFile) {
        try {
            if (!destFile.getCanonicalPath().startsWith(new File(System.getProperty("user.dir")).getCanonicalPath() + File.separator)) {
                throw new IOException("Entry is outside of the target dir: " + destFile.getName());
            }
            if (destFile.getParentFile().mkdirs()) {
                FileUtil.LOGGER.log(Level.INFO, "Parent directories doesnot exist for Destination file :: {0} so creating them.", destFile.getAbsolutePath());
            }
            else {
                FileUtil.LOGGER.log(Level.INFO, "Parent directories for Destination file :: {0} is not created. Either those directories must be present or there could be some permission issues ", destFile.getAbsolutePath());
            }
            if (!destFile.exists()) {
                FileUtil.LOGGER.log(Level.INFO, "Creating Destination file :: {0} as it doesnot exist. Is Destination file :: {0} file created :: {1} ", new Object[] { destFile.getAbsolutePath(), destFile.createNewFile() });
            }
            try (final InputStream in = new FileInputStream(srcFile);
                 final OutputStream out = new FileOutputStream(destFile)) {
                final int BUFFER_LENGTH = 2048;
                final byte[] buf = new byte[BUFFER_LENGTH];
                int len;
                while ((len = in.read(buf, 0, BUFFER_LENGTH)) > 0) {
                    out.write(buf, 0, len);
                }
                out.flush();
            }
        }
        catch (final Exception e) {
            FileUtil.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
        return true;
    }
    
    public static void moveDirectory(final File srcFile, final File destFile) throws IOException {
        if (Files.isDirectory(srcFile.toPath(), new LinkOption[0])) {
            if (!destFile.exists()) {
                destFile.mkdirs();
            }
            for (final File subFile : srcFile.listFiles()) {
                final String fileName = subFile.getCanonicalPath().substring(srcFile.getCanonicalPath().length());
                moveDirectory(subFile, Paths.get(destFile.getPath(), fileName).toFile());
            }
            deleteFiles(srcFile.getCanonicalPath());
        }
        else {
            Files.move(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
    
    public static void deleteFiles(final String... dirNames) throws IOException {
        if (dirNames != null) {
            for (final String dirName : dirNames) {
                if (dirName != null) {
                    final File directoryToList = new File(dirName);
                    if (Files.isDirectory(directoryToList.toPath(), new LinkOption[0])) {
                        final List<Path> folderContentsPaths = getFolderContentsPaths(directoryToList);
                        for (final Path p : folderContentsPaths) {
                            deleteFiles(p.toFile().getCanonicalPath());
                        }
                    }
                    Files.deleteIfExists(directoryToList.toPath());
                    FileUtil.LOGGER.info("Deleted File/Folder Path(if it exists):: " + dirName);
                }
            }
        }
    }
    
    private static List<Path> getFolderContentsPaths(final File directoryFile) throws IOException {
        final List<Path> subFilesPath = new ArrayList<Path>();
        try (final DirectoryStream<Path> newDirectoryStream = Files.newDirectoryStream(directoryFile.toPath())) {
            for (final Path p : newDirectoryStream) {
                subFilesPath.add(p);
            }
        }
        return subFilesPath;
    }
    
    public static String convertfilenameToOsFilename(final String filename) {
        if (filename == null) {
            return null;
        }
        final char thisOsFileSeperator = File.separatorChar;
        char checkForIndex;
        if (thisOsFileSeperator == '/') {
            checkForIndex = '\\';
        }
        else {
            checkForIndex = '/';
        }
        final String newFilename = filename.replace(checkForIndex, thisOsFileSeperator);
        return newFilename;
    }
    
    public static void createAllSubDirectories(final String filepath) {
        final String osSpecificFilepath = convertfilenameToOsFilename(filepath);
        final File t_filePath = new File(osSpecificFilepath);
        if (osSpecificFilepath.indexOf("/") != -1 || osSpecificFilepath.indexOf("\\") != -1) {
            if (!t_filePath.exists()) {
                String subDirectory;
                if (osSpecificFilepath.indexOf("/") != -1) {
                    subDirectory = osSpecificFilepath.substring(0, osSpecificFilepath.lastIndexOf("/"));
                }
                else {
                    subDirectory = osSpecificFilepath.substring(0, osSpecificFilepath.lastIndexOf("\\"));
                }
                createAllSubDirectories(subDirectory);
                final File temp = new File(subDirectory);
                temp.mkdir();
            }
        }
        else if (!t_filePath.exists()) {
            final File temp2 = new File(osSpecificFilepath);
            temp2.mkdir();
        }
    }
    
    public static boolean isFileExists(final File existingFile, final String expectedFileName) {
        if (existingFile.exists()) {
            for (final File file : existingFile.getParentFile().listFiles()) {
                if (file.getName().equals(expectedFileName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static String getSHA512Checksum(final File file) throws IOException, NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("SHA-512");
        try (final FileInputStream fis = new FileInputStream(file)) {
            final byte[] dataBytes = new byte[4096];
            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            final byte[] mdbytes = md.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mdbytes.length; ++i) {
                sb.append(Integer.toString((mdbytes[i] & 0xFF) + 256, 16).substring(1));
            }
            return sb.toString();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(FileUtil.class.getName());
    }
}
