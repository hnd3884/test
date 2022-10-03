package com.adventnet.sym.server.mdm.apps.android.apkextractor;

import java.util.Arrays;
import com.me.devicemanagement.framework.server.general.UtilAccessAPI;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Pattern;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.logging.Level;
import java.io.FileOutputStream;
import java.util.zip.ZipFile;
import org.apache.tika.Tika;
import org.apache.tika.io.FilenameUtils;
import com.zoho.security.api.wrapper.ZipInputStreamWrapper;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.ArrayList;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class ApkExtractionUtilities
{
    private static String prompt;
    private static String c;
    private static final Logger LOGGER;
    private static final String AAPT_CMD_PATH;
    public static final int FILE_TYPE_ICON = 1;
    public static final int FILE_TYPE_CERT = 2;
    public static final int FILE_TYPE_XML = 3;
    private static String aaptFilePath;
    
    public static String getAaptFilePath() {
        return ApkExtractionUtilities.aaptFilePath;
    }
    
    public static void execute(final List<String> command, final File outputFile) throws Exception {
        final List finalCommand = new ArrayList();
        if (!MDMStringUtils.isEmpty(ApkExtractionUtilities.prompt)) {
            finalCommand.add(ApkExtractionUtilities.prompt);
            finalCommand.add(ApkExtractionUtilities.c);
        }
        finalCommand.addAll(command);
        final ProcessBuilder pb = new ProcessBuilder(finalCommand);
        Process proc = null;
        try {
            if (outputFile != null) {
                pb.redirectOutput(outputFile);
            }
            proc = pb.start();
            final InputStream errorStream = proc.getErrorStream();
            final String error = getContentFromInputStream(errorStream);
            if (error != null && !error.isEmpty()) {
                throw new Exception("Error : " + error);
            }
        }
        finally {
            if (proc != null) {
                proc.destroy();
            }
        }
    }
    
    public static String unzip(final String zipFilePath, final String fileToUnzip, final String destDir, final int fileType) throws IOException {
        if (fileToUnzip == null) {
            return null;
        }
        final File dir = new File(destDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final FileInputStream fis = new FileInputStream(zipFilePath);
        ZipInputStreamWrapper zis = null;
        String newFilePath = null;
        validateFileExtenstion(fileToUnzip, fileType);
        try {
            zis = new ZipInputStreamWrapper((InputStream)fis);
            for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()) {
                final String fileName = ze.getName();
                final String pathName = FilenameUtils.getName(fileName);
                if (fileToUnzip.endsWith(pathName)) {
                    final File newFile = new File(destDir + File.separator + new File(pathName).getName());
                    validateFilePathLocation(newFile, dir);
                    validateContentType(new Tika().detect((InputStream)zis.getStream()), fileType);
                    final InputStream in = new ZipFile(zipFilePath).getInputStream(ze);
                    final byte[] buffer = new byte[1024];
                    final FileOutputStream out = new FileOutputStream(newFile);
                    try {
                        int len;
                        while ((len = in.read(buffer, 0, buffer.length)) > 0) {
                            out.write(buffer, 0, len);
                        }
                    }
                    finally {
                        out.close();
                    }
                    newFilePath = newFile.getAbsolutePath();
                    break;
                }
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (zis != null) {
                    zis.closeEntry();
                    zis.close();
                }
            }
            catch (final Exception e) {
                getLogger().log(Level.SEVERE, "Exception when closing streams", e);
            }
        }
        return newFilePath;
    }
    
    private static void validateContentType(final String mimeType, final int fileType) throws SecurityException {
        switch (fileType) {
            case 1: {
                if (!mimeType.equals("image/png") && !mimeType.equals("image/jpeg") && !mimeType.equals("image/bmp")) {
                    getLogger().log(Level.SEVERE, "Invalid mime type for icon{0}", mimeType);
                    throw new SecurityException("Invalid mime type for icon " + mimeType);
                }
                break;
            }
            case 2: {
                if (!mimeType.equals("application/pkcs7-signature")) {
                    getLogger().log(Level.SEVERE, "Invalid mime type for signature {0}", mimeType);
                    throw new SecurityException("Invalid mime type for signature " + mimeType);
                }
                break;
            }
            default: {
                getLogger().log(Level.SEVERE, "APK extracted for unknown operation {0} {1}", new Object[] { fileType, mimeType });
                throw new SecurityException("APK extracted for unknown operation " + fileType + " " + mimeType);
            }
        }
    }
    
    private static void validateFilePathLocation(final File file, final File dirLocation) throws SecurityException, IOException {
        final String canonicalDestinationDirPath = dirLocation.getCanonicalPath();
        final String canonicalDestinationFile = file.getCanonicalPath();
        if (!canonicalDestinationFile.startsWith(canonicalDestinationDirPath + File.separator)) {
            getLogger().log(Level.SEVERE, "Entry is outside of the target directory:{0}", file.getAbsolutePath());
            throw new SecurityException("Entry is outside of the target directory: " + file.getAbsolutePath());
        }
    }
    
    public static void validateFileExtenstion(String fileName, final int fileType) {
        fileName = fileName.toLowerCase();
        switch (fileType) {
            case 1: {
                if (!fileName.endsWith(".png") && !fileName.endsWith(".jpeg") && !fileName.endsWith(".bmp") && !fileName.endsWith(".jpg") && !fileName.endsWith(".webp")) {
                    getLogger().log(Level.SEVERE, "Icon file is not a valid image file{0}", fileName);
                    throw new SecurityException("Icon file is not a valid image file" + fileName);
                }
                break;
            }
            case 2: {
                break;
            }
            default: {
                getLogger().log(Level.SEVERE, "APK extracted for unknown operation {0} {1}", new Object[] { fileType, fileName });
                throw new SecurityException("APK extracted for unknown operation " + fileType + " " + fileName);
            }
        }
    }
    
    public static String parseSingleLine(final Pattern pattern, final String filePath) {
        return parseSingleLine(pattern, filePath, 1);
    }
    
    public static String parseSingleLine(final Pattern pattern, final String filePath, final int group_num) {
        String info = null;
        String line = null;
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filePath);
            bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                final Matcher m = pattern.matcher(line);
                if (m.find()) {
                    info = m.group(group_num);
                    break;
                }
            }
        }
        catch (final Exception e) {
            ApkExtractionUtilities.LOGGER.log(Level.WARNING, "Unable to find the pattern in input", e);
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (final IOException e2) {
                    ApkExtractionUtilities.LOGGER.log(Level.WARNING, "Unable to close the BufferedReader ", e2);
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                }
                catch (final IOException e2) {
                    ApkExtractionUtilities.LOGGER.log(Level.WARNING, "Unable to close the FileReader", e2);
                }
            }
        }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (final IOException e3) {
                    ApkExtractionUtilities.LOGGER.log(Level.WARNING, "Unable to close the BufferedReader ", e3);
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                }
                catch (final IOException e3) {
                    ApkExtractionUtilities.LOGGER.log(Level.WARNING, "Unable to close the FileReader", e3);
                }
            }
        }
        return info;
    }
    
    public static ArrayList<String> parseMultiLines(final Pattern pattern, final String filePath) {
        return parseMultiLines(pattern, filePath, 1);
    }
    
    public static ArrayList<String> parseMultiLines(final Pattern pattern, final String filePath, final int group_num) {
        final ArrayList<String> prm = new ArrayList<String>();
        String line = null;
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filePath);
            bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                final Matcher m = pattern.matcher(line);
                if (m.find()) {
                    prm.add(m.group(group_num));
                }
            }
            bufferedReader.close();
        }
        catch (final Exception e) {
            ApkExtractionUtilities.LOGGER.log(Level.WARNING, "Unable to parse multiple lines in input", e);
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (final IOException e2) {
                    ApkExtractionUtilities.LOGGER.log(Level.WARNING, "Unable to close the BufferedReader ", e2);
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                }
                catch (final IOException e2) {
                    ApkExtractionUtilities.LOGGER.log(Level.WARNING, "Unable to close the FileReader", e2);
                }
            }
        }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (final IOException e3) {
                    ApkExtractionUtilities.LOGGER.log(Level.WARNING, "Unable to close the BufferedReader ", e3);
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                }
                catch (final IOException e3) {
                    ApkExtractionUtilities.LOGGER.log(Level.WARNING, "Unable to close the FileReader", e3);
                }
            }
        }
        return prm;
    }
    
    private static String getContentFromInputStream(final InputStream stream) throws Exception {
        final StringBuilder content = new StringBuilder();
        String line = "";
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(stream));
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }
        }
        catch (final Exception e) {
            throw new Exception("Error while reading stream: " + e);
        }
        finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return content.toString();
    }
    
    public static Logger getLogger() {
        return ApkExtractionUtilities.LOGGER;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
        AAPT_CMD_PATH = "scripts" + File.separator + "aapt";
        final String OS = System.getProperty("os.name").toLowerCase();
        final boolean isWindows = OS.indexOf("win") >= 0;
        if (isWindows) {
            ApkExtractionUtilities.prompt = "cmd.exe";
            ApkExtractionUtilities.c = "/c";
            ApkExtractionUtilities.LOGGER.info("Validated os : Windows");
        }
        final UtilAccessAPI utilAccessAPI = ApiFactoryProvider.getUtilAccessAPI();
        ApkExtractionUtilities.aaptFilePath = utilAccessAPI.getServerBinUrl() + File.separator + ApkExtractionUtilities.AAPT_CMD_PATH;
        ApkExtractionUtilities.LOGGER.info("Initialization successful");
    }
    
    public static class Defaults
    {
        public static final String[] DANGEROUS_PERMISSIONS;
        
        static {
            DANGEROUS_PERMISSIONS = new String[] { "android.permission.READ_CALENDAR", "android.permission.WRITE_CALENDAR", "android.permission.CAMERA", "android.permission.READ_CONTACTS", "android.permission.WRITE_CONTACTS", "android.permission.GET_ACCOUNTS", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_BACKGROUND_LOCATION", "android.permission.RECORD_AUDIO", "android.permission.READ_PHONE_STATE", "android.permission.READ_PHONE_NUMBERS", "android.permission.CALL_PHONE", "android.permission.ANSWER_PHONE_CALLS", "android.permission.READ_CALL_LOG", "android.permission.WRITE_CALL_LOG", "android.permission.ADD_VOICEMAIL", "android.permission.USE_SIP", "android.permission.PROCESS_OUTGOING_CALLS", "android.permission.BODY_SENSORS", "android.permission.SEND_SMS", "android.permission.RECEIVE_SMS", "android.permission.READ_SMS", "android.permission.RECEIVE_WAP_PUSH", "android.permission.RECEIVE_MMS", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE" };
        }
    }
    
    public static class Patterns
    {
        public static final String PKG_NAME = "package: name='([a-zA-Z0-9\\._]+)'";
        public static final String PKG_LABEL = "application-label[a-z-]*:'(.+?)'.*$";
        public static final String VERSION_NAME = "versionName='(.+?)'.*$";
        public static final String PERMISSIONS = "uses-permission: ?(name=)?'(android.permission.[A-Z_0-9]+)'";
        public static final String ICON = "icon='(.+?)'.*$";
        public static final String MIN_SDK = "sdkVersion:'([0-9]+)'";
        public static final String TARGET_SDK = "targetSdkVersion:'([0-9]+)'";
        public static final String VERSION_CODE = "versionCode='([0-9]+)'";
        public static final String SUPPORTED_SCREENS = "supports-screens:([ a-zA-Z']+)";
        public static final String SIGNATURE_INFO_PATTERN = "([ a-zA-Z0-9]+): ?(.+)?";
        public static final String COUNT = "Count=([0-9]+)";
        public static final String ENTRY = "\\(([a-z0-9]+)\\) (.+)";
        public static final String APP_RESTRICTIONS = ".*android.content.APP_RESTRICTIONS.*";
        public static final String APP_RESTRICTIONS_RESOURCE = "A:\\s.*@([0-9a-fA-Fx]+)";
        public static final String RESOURCE_STRING = "[ a-z0-9()]+\"(.*)\"";
        public static final String RESOURCE_TYPE_FINDER = "spec resource [0-9a-fA-FxX()]+.+:(.+)/.+";
        public static final String RESOURCE_BOOL_OR_INTEGER = ".+d=0x(.+) \\(";
        public static final String RESOURCE_REFERENCE = "\\(reference\\) ([0-9a-fA-FxX()]+)";
        public static final String ATTRIBUTE = "[ ]+A:.+android:([a-zA-Z]+)[0-9a-fA-FxX()]+=@([0-9a-fA-FxX]*)";
        public static final String ATTRIBUTE_RAW = ".+android:([a-zA-Z]+)[0-9a-fA-FxX()]+.+Raw: \"(.+)\"";
        public static final String ATTRIBUTE_DEFAULT_VALUE = "[ ]+A: [a-z:/\\.]*android:([a-zA-Z]+)[ 0-9a-fA-FxX()]+=\\(type 0x([0-9]+)\\)0x([a-fA-F0-9]*)";
        public static final String CONFIGS = "(ldpi|mdpi|hdpi|xhdpi|xxhdpi|xxxhdpi|anydpi)";
        public static final String ATTRIBUTE_RAW_EMPTY_VALUE = ".+android:([a-zA-Z]+)[0-9a-fA-FxX()]+=\"\".+\\(Raw: (\"\")\\)";
        public static final String SIGNATURE_FILE = "META-INF/[a-zA-Z0-9_]+.[D|R]SA";
        public static final String RESTRICTION_FILE_PATH = "^res/xml[/a-zA-Z0-9_-]+.xml$";
    }
    
    public static class Commands
    {
        private static String[] cmd_DELETE_FOLDER_COMMAND;
        private static String[] cmd_AAPT_DUMP_XMLTREE;
        private static String[] cmd_AAPT_DUMP_RESOURCE;
        private static String[] cmd_AAPT_DUMP_BADGING;
        private static String[] cmd_CREATE_FOLDER_COMMAND;
        private static String[] cmd_AAPT_LIST;
        
        public static List<String> getAAPT_DUMP_XMLTREE() {
            return Arrays.asList(Commands.cmd_AAPT_DUMP_XMLTREE);
        }
        
        public static List<String> getDUMP_RESOURCE_COMMAND() {
            return Arrays.asList(Commands.cmd_AAPT_DUMP_RESOURCE);
        }
        
        public static List<String> getCREATE_FOLDER_COMMAND() {
            return Arrays.asList(Commands.cmd_CREATE_FOLDER_COMMAND);
        }
        
        public static List<String> getAAPT_DUMP_BADGING() {
            return Arrays.asList(Commands.cmd_AAPT_DUMP_BADGING);
        }
        
        public static List<String> getDELETE_FOLDER_COMMAND() {
            return Arrays.asList(Commands.cmd_DELETE_FOLDER_COMMAND);
        }
        
        public static List<String> getLIST_COMMAND() {
            return Arrays.asList(Commands.cmd_AAPT_LIST);
        }
        
        static {
            Commands.cmd_DELETE_FOLDER_COMMAND = new String[] { "rm", "-R" };
            Commands.cmd_AAPT_DUMP_XMLTREE = new String[] { "dump", "xmltree" };
            Commands.cmd_AAPT_DUMP_RESOURCE = new String[] { "dump", "--values", "resources" };
            Commands.cmd_AAPT_DUMP_BADGING = new String[] { "dump", "badging" };
            Commands.cmd_CREATE_FOLDER_COMMAND = new String[] { "mkdir" };
            Commands.cmd_AAPT_LIST = new String[] { "list" };
        }
    }
}
