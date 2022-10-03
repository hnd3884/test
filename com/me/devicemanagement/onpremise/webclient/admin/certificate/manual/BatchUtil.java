package com.me.devicemanagement.onpremise.webclient.admin.certificate.manual;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.Properties;
import java.util.Map;
import com.adventnet.mfw.ConsoleOut;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.File;
import java.util.logging.Logger;

class BatchUtil
{
    private static Logger logger;
    private static BatchLogHandler logHandler;
    
    public static File deleteCRTAndRenameTempToCRT(final File confDirectory, final File serverCertificateFile) {
        final ArrayList<String> deleteOrgFile = new ArrayList<String>();
        final ArrayList<String> deleteTempFile = new ArrayList<String>();
        deleteOrgFile.add(serverCertificateFile.getName());
        final String serverFile = removeExtension(serverCertificateFile.getName());
        final String extension = getExtension(serverCertificateFile);
        deleteFiles(deleteOrgFile);
        final ArrayList<String> commands = Command.getRenameCommand(confDirectory + File.separator + serverFile + "_temp" + extension, confDirectory + File.separator + serverFile + extension);
        Command.executeCommand(commands, confDirectory);
        deleteTempFile.add(confDirectory + File.separator + serverFile + "_temp" + extension);
        deleteFiles(deleteTempFile);
        return new File(confDirectory + File.separator + serverFile + extension);
    }
    
    public static boolean deleteFiles(final ArrayList<String> toDeleteFilesName) {
        boolean deletionFlag = true;
        if (!toDeleteFilesName.isEmpty()) {
            final Iterator<String> it = toDeleteFilesName.iterator();
            while (it.hasNext()) {
                if (!new File(it.next()).delete()) {
                    deletionFlag = false;
                }
            }
        }
        return deletionFlag;
    }
    
    public static String getExtension(final File serverCertificateFile) {
        final String serverCertificateName = serverCertificateFile.getName();
        if (serverCertificateName.lastIndexOf(".") == -1 && serverCertificateName.length() >= 5) {
            return serverCertificateName.substring(serverCertificateName.length() - 4, serverCertificateName.length());
        }
        if (serverCertificateName.contains(".")) {
            return serverCertificateName.substring(serverCertificateName.lastIndexOf("."));
        }
        return serverCertificateName;
    }
    
    public static void copyFileUsingChannel(final File source, final File dest) throws IOException, NullPointerException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0L, sourceChannel.size());
        }
        finally {
            sourceChannel.close();
            destChannel.close();
        }
    }
    
    public static void copyAsTempToConf(final File confDirectory, final File certificateFile) {
        try {
            copyFileUsingChannel(certificateFile, new File(confDirectory + File.separator + removeExtension(certificateFile.getName()) + "_temp" + getExtension(certificateFile)));
        }
        catch (final IOException ex) {
            BatchUtil.logger.log(Level.SEVERE, certificateFile + " File may not be accessible / in use.. ", ex);
            BatchUtil.logHandler.logExceptionTrace(ex);
        }
        catch (final NullPointerException ex2) {
            BatchUtil.logger.log(Level.SEVERE, certificateFile + " File doesn't exist..", ex2);
            BatchUtil.logHandler.logExceptionTrace(ex2);
        }
    }
    
    public static void copySSLToConf(final File confDirectory, final File certificateFile) {
        try {
            copyFileUsingChannel(certificateFile, new File(confDirectory + File.separator + certificateFile.getName()));
        }
        catch (final IOException ex) {
            BatchUtil.logger.log(Level.SEVERE, certificateFile + " File may not be accessible / in use.. ", ex);
            BatchUtil.logHandler.logExceptionTrace(ex);
        }
        catch (final NullPointerException ex2) {
            BatchUtil.logger.log(Level.SEVERE, certificateFile + " File doesn't exist..", ex2);
            BatchUtil.logHandler.logExceptionTrace(ex2);
        }
    }
    
    public static String removeExtension(final String fileName) {
        final int lastIndex = fileName.lastIndexOf(".");
        return fileName.substring(0, lastIndex);
    }
    
    public static File getFileInput(final String string, File filePath) {
        boolean fileExistence = false;
        final BufferedReader bin = new BufferedReader(new InputStreamReader(System.in));
        do {
            try {
                ConsoleOut.println("\nProvide a location to the " + string + " file");
                filePath = new File(bin.readLine());
                if (checkExistenceOfFile(filePath)) {
                    fileExistence = false;
                }
                else {
                    ConsoleOut.println("File not found..or you may have entered an invalid file path..");
                    fileExistence = true;
                }
            }
            catch (final IOException ex) {
                BatchUtil.logger.log(Level.SEVERE, filePath + " file not accessible / in use.. ", ex);
                BatchUtil.logHandler.logExceptionTrace(ex);
            }
            catch (final Exception ex2) {
                BatchUtil.logger.log(Level.SEVERE, "Unrecognised exception in getting the file input " + filePath, ex2);
                BatchUtil.logHandler.logExceptionTrace(ex2);
            }
        } while (fileExistence);
        return filePath;
    }
    
    public static boolean checkExistenceOfFile(final File file) {
        return !file.isDirectory() && file.exists();
    }
    
    public static Properties mapToProperties(final Map<String, String> map) {
        final Properties p = new Properties();
        final Set<Map.Entry<String, String>> set = map.entrySet();
        for (final Map.Entry<String, String> entry : set) {
            p.setProperty(entry.getKey(), entry.getValue());
        }
        return p;
    }
    
    public static Map<String, String> propertiesToMap(final Properties props) {
        final HashMap<String, String> hm = new HashMap<String, String>();
        final Enumeration<Object> e = ((Hashtable<Object, V>)props).keys();
        while (e.hasMoreElements()) {
            final String s = e.nextElement();
            hm.put(s, props.getProperty(s));
        }
        return hm;
    }
    
    static {
        BatchUtil.logger = BatchCertificateConverter.logger;
        BatchUtil.logHandler = BatchCertificateConverter.logHandler;
    }
}
