package com.me.devicemanagement.framework.server.fileaccess;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.List;
import java.nio.file.Paths;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.nio.file.FileVisitor;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.LinkOption;
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.io.Writer;
import java.io.BufferedWriter;
import java.util.Collection;
import java.util.ArrayList;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import org.json.simple.JSONObject;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.util.Iterator;
import java.util.Set;
import java.io.FileInputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.Map;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.logging.Logger;

public class FileAccessUtil
{
    private static Logger logger;
    
    public static void storeProperties(final Properties props, final String confFileName, final boolean append) throws Exception {
        storeProperties(props, confFileName, append, null);
    }
    
    public static void storeProperties(final Properties props, final String confFileName, final boolean append, final String comments) throws Exception {
        final Properties writeProps = new Properties();
        InputStream ism = null;
        OutputStream osm = null;
        try {
            if (confFileName.toLowerCase().contains("websettings.conf")) {
                ApiFactoryProvider.getUtilAccessAPI().updateWebServerSettings(props, append);
            }
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(confFileName) && append) {
                ism = ApiFactoryProvider.getFileAccessAPI().readFile(confFileName);
                writeProps.load(ism);
            }
            writeProps.putAll(props);
            osm = ApiFactoryProvider.getFileAccessAPI().writeFile(confFileName);
            writeProps.store(osm, comments);
        }
        catch (final Exception ex) {
            FileAccessUtil.logger.log(Level.WARNING, "Caught exception while storing properties: " + props + " with filename: " + confFileName, ex);
        }
        finally {
            try {
                if (ism != null) {
                    ism.close();
                }
                if (osm != null) {
                    osm.close();
                }
            }
            catch (final Exception ex2) {}
        }
    }
    
    public static Properties readProperties(final String confFileName) throws Exception {
        if (confFileName.toLowerCase().contains("websettings.conf")) {
            return ApiFactoryProvider.getUtilAccessAPI().getWebServerSettings();
        }
        final Properties props = new Properties();
        InputStream ism = null;
        try {
            if (new File(confFileName).exists()) {
                ism = new FileInputStream(confFileName);
                props.load(ism);
            }
        }
        catch (final Exception ex) {
            FileAccessUtil.logger.log(Level.WARNING, "Caught exception while reading properties from file: " + confFileName, ex);
        }
        finally {
            try {
                if (ism != null) {
                    ism.close();
                }
            }
            catch (final Exception ex2) {}
        }
        return props;
    }
    
    public static Properties readPropertiesFromFileList(final String filesListPath) {
        final Properties properties = new Properties();
        try {
            final String serverHome = System.getProperty("server.home");
            final Properties filesList = readProperties(filesListPath);
            final Set<String> filesSet = filesList.stringPropertyNames();
            for (final String module : filesSet) {
                final String filepath = serverHome + File.separator + filesList.getProperty(module);
                if (new File(filepath).exists()) {
                    final Properties tempProp = readProperties(filepath);
                    properties.putAll(tempProp);
                }
            }
        }
        catch (final Exception e) {
            FileAccessUtil.logger.log(Level.WARNING, "Caught exception while reading properties from files : " + e);
        }
        return properties;
    }
    
    public static void writeMapAsPropertiesIntoFile(final Map props, final String outFileName, String comments) throws Exception {
        try {
            if (props == null || props.size() <= 0) {
                FileAccessUtil.logger.log(Level.WARNING, "Unable to write the map: " + props + " into file: " + outFileName);
                return;
            }
            comments = "#" + comments;
            final StringBuffer sb = new StringBuffer(1000);
            sb.append(comments);
            for (final Object key : props.keySet()) {
                final Object value = props.get(key);
                sb.append("\n" + key + "=" + value);
            }
            ApiFactoryProvider.getFileAccessAPI().writeFile(outFileName, sb.toString().getBytes());
        }
        catch (final Exception ex) {
            FileAccessUtil.logger.log(Level.WARNING, "Caught exception while writing Map as properties with: " + props + " into file: " + outFileName, ex);
            throw ex;
        }
    }
    
    public static InputStream getFileAsInputStream(final String filePath) {
        InputStream fileInput = null;
        try {
            fileInput = new BufferedInputStream(new FileInputStream(filePath));
        }
        catch (final FileNotFoundException ex) {
            FileAccessUtil.logger.log(Level.WARNING, "FileNotFoundException while getFileAsInputStream", ex);
        }
        return fileInput;
    }
    
    public static byte[] getFileAsByteArray(final String filePath) {
        byte[] fileByte = null;
        try {
            final InputStream fileInput = new BufferedInputStream(new FileInputStream(filePath));
            fileByte = new byte[fileInput.available()];
            fileInput.read(fileByte);
        }
        catch (final FileNotFoundException ex) {
            FileAccessUtil.logger.log(Level.WARNING, "FileNotFoundException while getFileAsInputStream", ex);
        }
        catch (final IOException ex2) {
            FileAccessUtil.logger.log(Level.WARNING, "IOException while getFileAsInputStream", ex2);
        }
        return fileByte;
    }
    
    public static InputStream readFileFromServer(final String fileName) throws Exception {
        InputStream fis = null;
        try {
            if (new File(fileName).exists()) {
                fis = new FileInputStream(fileName);
            }
        }
        catch (final Exception e) {
            FileAccessUtil.logger.log(Level.WARNING, "Exception occurred while reading file", e);
            if (fis != null) {
                fis.close();
            }
            throw e;
        }
        return fis;
    }
    
    public static void writeFileInServer(final String fileName, final byte[] fileContent) {
        FileAccessUtil.logger.log(Level.INFO, "writeFile as byte array method is called");
        try {
            FileOutputStream fos = null;
            try {
                final File fname = new File(fileName).getParentFile();
                if (!fname.exists()) {
                    fname.mkdirs();
                }
                fos = new FileOutputStream(fileName);
                fos.write(fileContent);
            }
            catch (final IOException e) {
                FileAccessUtil.logger.log(Level.WARNING, "Exception occurred while writing file", e);
                throw e;
            }
            finally {
                if (fos != null) {
                    try {
                        fos.close();
                    }
                    catch (final Exception e2) {
                        FileAccessUtil.logger.log(Level.WARNING, "Exception occurred while closing file output stream", e2);
                    }
                }
            }
        }
        catch (final Exception e3) {
            FileAccessUtil.logger.log(Level.SEVERE, "Exception while writeFile method as byte array" + e3);
        }
    }
    
    public static void addOrUpdateJSON(final String filePath, final String key, final String value) {
        addOrUpdateJSON(new File(filePath), key, value);
    }
    
    public static void addOrUpdateJSON(final File file, final String key, final String value) {
        FileWriter writer = null;
        try {
            createJSONFileIfNotExist(file);
            JSONObject jsonObject = secureReadJSON(file);
            if (jsonObject == null) {
                jsonObject = new JSONObject();
            }
            jsonObject.put((Object)key, (Object)value);
            writer = new FileWriter(file);
            writer.write(jsonObject.toJSONString());
        }
        catch (final Exception e) {
            FileAccessUtil.logger.log(Level.WARNING, "caught error into addOrUpdateJSON() : ", e);
            try {
                writer.close();
            }
            catch (final IOException e2) {
                FileAccessUtil.logger.log(Level.WARNING, "caught error while close FileWriter into addOrUpdateJSON() : ", e2);
            }
        }
        finally {
            try {
                writer.close();
            }
            catch (final IOException e3) {
                FileAccessUtil.logger.log(Level.WARNING, "caught error while close FileWriter into addOrUpdateJSON() : ", e3);
            }
        }
    }
    
    public static void incrementJSONValue(final String file, final String key) {
        incrementJSONValue(new File(file), key, 1);
    }
    
    public static void incrementJSONValue(final String file, final String key, final int incrementBy) {
        incrementJSONValue(new File(file), key, incrementBy);
    }
    
    public static void incrementJSONValue(final File file, final String key, final int incrementBy) {
        try {
            createJSONFileIfNotExist(file);
            final JSONObject jsonObject = secureReadJSON(file);
            int value = Integer.parseInt((String)jsonObject.get((Object)key));
            value += incrementBy;
            addOrUpdateJSON(file, key, value + "");
        }
        catch (final Exception e) {
            FileAccessUtil.logger.log(Level.WARNING, "caught error into incrementJSONValue : ", e);
        }
    }
    
    private static void createJSONFileIfNotExist(final File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
            FileWriter fr = null;
            try {
                fr = new FileWriter(file);
                fr.write(String.valueOf(new JSONObject()));
            }
            finally {
                fr.close();
            }
        }
    }
    
    public static void writeFileInServer(final String fileName, final InputStream stream) {
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        final int BUF_LEN = 1024;
        try {
            final File destFile = new File(fileName).getParentFile();
            if (!destFile.exists()) {
                destFile.mkdirs();
            }
            if (destFile.exists()) {
                fos = new FileOutputStream(fileName);
                bis = new BufferedInputStream(stream, 1024);
                final byte[] bufr = new byte[1024];
                int c = 0;
                int br = 0;
                while ((c = bis.read(bufr, 0, 1024)) != -1) {
                    fos.write(bufr, 0, c);
                    br += c;
                }
                fos.flush();
            }
        }
        catch (final IOException ex) {
            FileAccessUtil.logger.log(Level.SEVERE, "IOException while writeFile method as InputStream" + ex);
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception ex2) {
                FileAccessUtil.logger.log(Level.WARNING, "Exception closing FileOutputStream fos", ex2);
            }
            try {
                if (bis != null) {
                    bis.close();
                }
            }
            catch (final Exception ex2) {
                FileAccessUtil.logger.log(Level.WARNING, "Exception closing BufferedInputStream bis", ex2);
            }
            try {
                if (stream != null) {
                    stream.close();
                }
            }
            catch (final Exception ex2) {
                FileAccessUtil.logger.log(Level.WARNING, "Exception closing InputStream stream", ex2);
            }
        }
        finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception ex3) {
                FileAccessUtil.logger.log(Level.WARNING, "Exception closing FileOutputStream fos", ex3);
            }
            try {
                if (bis != null) {
                    bis.close();
                }
            }
            catch (final Exception ex3) {
                FileAccessUtil.logger.log(Level.WARNING, "Exception closing BufferedInputStream bis", ex3);
            }
            try {
                if (stream != null) {
                    stream.close();
                }
            }
            catch (final Exception ex3) {
                FileAccessUtil.logger.log(Level.WARNING, "Exception closing InputStream stream", ex3);
            }
        }
    }
    
    public static boolean containsStringInFile(final File file, final String key) {
        final boolean returnvalue = Boolean.FALSE;
        FileReader reader = null;
        BufferedReader bReader = null;
        try {
            reader = new FileReader(file);
            bReader = new BufferedReader(reader);
            String line;
            while ((line = bReader.readLine()) != null) {
                if (line.contains(key)) {
                    return Boolean.TRUE;
                }
            }
        }
        catch (final Exception e) {
            FileAccessUtil.logger.log(Level.WARNING, "Exception in reading file : ", e);
            try {
                if (bReader != null) {
                    bReader.close();
                }
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final Exception ex) {
                FileAccessUtil.logger.log(Level.WARNING, "Exception in closing reader : ", ex);
            }
        }
        finally {
            try {
                if (bReader != null) {
                    bReader.close();
                }
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final Exception ex2) {
                FileAccessUtil.logger.log(Level.WARNING, "Exception in closing reader : ", ex2);
            }
        }
        return returnvalue;
    }
    
    public static boolean copyDirectoryWithinServer(final String sourceLocation, final String targetLocation) throws Exception {
        return copyDirectory(new File(sourceLocation), new File(targetLocation));
    }
    
    private static boolean copyDirectory(final File sourceLocation, final File targetLocation) throws Exception {
        try {
            if (sourceLocation.isDirectory()) {
                if (!targetLocation.exists()) {
                    targetLocation.mkdirs();
                }
                final String[] children = sourceLocation.list();
                for (int i = 0; i < children.length; ++i) {
                    if (!copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]))) {
                        return false;
                    }
                }
                return true;
            }
            return copyFile(sourceLocation, targetLocation);
        }
        catch (final Exception e) {
            FileAccessUtil.logger.log(Level.SEVERE, "Failed to copy folder " + sourceLocation + " to location " + targetLocation, e);
            return false;
        }
    }
    
    public static boolean copyFileWithinServer(final String sourceLocation, final String targetLocation) throws Exception {
        return copyFile(new File(sourceLocation), new File(targetLocation));
    }
    
    private static boolean copyFile(final File srcFile, final File destFile) throws Exception {
        boolean retType = false;
        InputStream inFile = null;
        OutputStream outFile = null;
        FileAccessUtil.logger.log(Level.INFO, "Going to copy file from {0} to {1} ", new Object[] { srcFile.getCanonicalPath(), destFile.getCanonicalPath() });
        try {
            final String parentLoc = destFile.getParent();
            if (parentLoc != null && !parentLoc.equals("") && !new File(parentLoc).exists()) {
                new File(parentLoc).mkdirs();
            }
            inFile = new FileInputStream(srcFile);
            outFile = new FileOutputStream(destFile);
            final byte[] buf = new byte[1024];
            int len;
            while ((len = inFile.read(buf)) > 0) {
                outFile.write(buf, 0, len);
            }
            retType = true;
        }
        catch (final Exception e) {
            FileAccessUtil.logger.log(Level.SEVERE, "Exception while copying file.......", e);
        }
        finally {
            if (inFile != null) {
                inFile.close();
            }
            if (outFile != null) {
                outFile.close();
            }
        }
        return retType;
    }
    
    public static ArrayList getFilesListInFolder(final String absolutePath) throws Exception {
        final ArrayList fileList = new ArrayList();
        try {
            final File rootDir = new File(absolutePath);
            final String[] allFiles = rootDir.list();
            for (int i = 0; i < allFiles.length; ++i) {
                final String filePath = absolutePath + File.separator + allFiles[i];
                final File temp = new File(filePath);
                if (temp.isDirectory()) {
                    fileList.addAll(getFilesListInFolder(absolutePath + File.separator + allFiles[i]));
                }
                else {
                    fileList.add(absolutePath + File.separator + allFiles[i]);
                }
            }
        }
        catch (final Exception e) {
            FileAccessUtil.logger.log(Level.WARNING, "Caught exception while getting data from directory " + absolutePath, e);
        }
        return fileList;
    }
    
    public static OutputStream writeFile(final String fileName) throws IOException {
        OutputStream fos = null;
        try {
            final File fname = new File(fileName).getParentFile();
            if (!fname.exists()) {
                fname.mkdirs();
            }
            fos = new FileOutputStream(fileName);
        }
        catch (final IOException e) {
            FileAccessUtil.logger.log(Level.WARNING, "Exception occurred while writing file", e);
            if (fos != null) {
                fos.close();
            }
            throw e;
        }
        return fos;
    }
    
    public static boolean copyFile(final String srcFileStr, final String destFileStr) throws Exception {
        final File srcFile = new File(srcFileStr);
        final File destFile = new File(destFileStr);
        boolean retType = false;
        InputStream inFile = null;
        OutputStream outFile = null;
        FileAccessUtil.logger.log(Level.INFO, "Going to copy file.......");
        try {
            final String parentLoc = destFile.getParent();
            if (parentLoc != null && !parentLoc.equals("") && !new File(parentLoc).exists()) {
                new File(parentLoc).mkdirs();
            }
            inFile = new FileInputStream(srcFile);
            outFile = new FileOutputStream(destFile);
            final byte[] buf = new byte[1024];
            int len;
            while ((len = inFile.read(buf)) > 0) {
                outFile.write(buf, 0, len);
            }
            retType = true;
        }
        catch (final Exception e) {
            FileAccessUtil.logger.log(Level.WARNING, "Exception while copying file.......", e);
        }
        finally {
            if (inFile != null) {
                inFile.close();
            }
            if (outFile != null) {
                outFile.close();
            }
        }
        return retType;
    }
    
    public static boolean deleteFile(final String filePath) throws Exception {
        boolean result = false;
        try {
            final File dataFile = new File(filePath);
            result = dataFile.delete();
            FileAccessUtil.logger.log(Level.INFO, "Deleted file: " + filePath + " Result: " + result);
        }
        catch (final Exception ex) {
            FileAccessUtil.logger.log(Level.INFO, "Caught exception while deleting file: " + filePath, ex);
        }
        return result;
    }
    
    public static boolean writeDataInFile(final String filePath, final String fileData) {
        BufferedWriter bw = null;
        try {
            FileAccessUtil.logger.log(Level.INFO, "Start of writeDataInFile: " + filePath);
            bw = new BufferedWriter(new FileWriter(filePath));
            bw.write(fileData);
            bw.close();
            FileAccessUtil.logger.log(Level.INFO, "End of writeDataInFile: " + filePath);
            return true;
        }
        catch (final Exception ex) {
            FileAccessUtil.logger.log(Level.WARNING, "Caught exception while writing the data in file ", ex);
            if (bw != null) {
                try {
                    bw.close();
                }
                catch (final Exception ex) {
                    FileAccessUtil.logger.log(Level.WARNING, "Exception occurred while closing BufferedWriter.. ", ex);
                }
            }
        }
        finally {
            if (bw != null) {
                try {
                    bw.close();
                }
                catch (final Exception ex2) {
                    FileAccessUtil.logger.log(Level.WARNING, "Exception occurred while closing BufferedWriter.. ", ex2);
                }
            }
        }
        return false;
    }
    
    public static void moveFolder(final Path srcFolder, final Path destFolder, final CopyOption... copyOptions) throws Exception {
        class MoveFileVisitor extends SimpleFileVisitor<Path>
        {
            private Path sourceDir = srcFolder;
            private Path targetDir = destFolder;
            private CopyOption[] copyOptions = copyOptions;
            
            public MoveFileVisitor(final Path sourceDir, final Path targetDir, final CopyOption... options) {
            }
            
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attributes) throws IOException {
                final Path targetFile = this.targetDir.resolve(this.sourceDir.relativize(file));
                Files.move(file, targetFile, this.copyOptions);
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attributes) throws IOException {
                final Path newDir = this.targetDir.resolve(this.sourceDir.relativize(dir));
                if (!Files.exists(newDir, new LinkOption[0])) {
                    Files.createDirectories(newDir, (FileAttribute<?>[])new FileAttribute[0]);
                }
                return FileVisitResult.CONTINUE;
            }
        }
        Files.walkFileTree(srcFolder, new MoveFileVisitor(destFolder));
    }
    
    public static Reader secureGetReader(final String filePath) throws IOException, SyMException {
        return secureGetReader(new File(filePath));
    }
    
    public static Reader secureGetReader(final File file) throws IOException, SyMException {
        if (SecurityUtil.validateCheckSumForFile(file)) {
            return new FileReader(file);
        }
        return null;
    }
    
    public static InputStream secureGetFileInputStream(final String filePath) throws SyMException, IOException {
        return secureGetFileInputStream(new File(filePath));
    }
    
    public static InputStream secureGetFileInputStream(final File file) throws SyMException, IOException {
        if (SecurityUtil.validateCheckSumForFile(file)) {
            return new FileInputStream(file);
        }
        return null;
    }
    
    public static byte[] secureReadFileToByte(final String filePath) throws IOException, SyMException {
        return secureReadFileToByte(new File(filePath));
    }
    
    public static byte[] secureReadFileToByte(final File file) throws IOException, SyMException {
        if (SecurityUtil.validateCheckSumForFile(file)) {
            return Files.readAllBytes(Paths.get(file.getCanonicalPath(), new String[0]));
        }
        return null;
    }
    
    public static String secureReadFileToString(final String filePath) throws IOException, SyMException {
        return secureReadFileToString(new File(filePath));
    }
    
    public static String secureReadFileToString(final File file) throws IOException, SyMException {
        final byte[] data = secureReadFileToByte(file);
        if (data != null) {
            return new String(data);
        }
        return null;
    }
    
    public static Properties secureReadProperties(final String filePath) throws IOException, SyMException {
        return secureReadProperties(new File(filePath));
    }
    
    public static Properties secureReadProperties(final File file) throws IOException, SyMException {
        try (final Reader reader = secureGetReader(file)) {
            if (reader != null) {
                final Properties properties = new Properties();
                properties.load(reader);
                return properties;
            }
        }
        return null;
    }
    
    public static List<String> secureReadFileAsList(final String filePath) throws IOException, SyMException {
        return secureReadFileAsList(new File(filePath));
    }
    
    public static List<String> secureReadFileAsList(final File file) throws IOException, SyMException {
        if (SecurityUtil.validateCheckSumForFile(file)) {
            return Files.readAllLines(Paths.get(file.getCanonicalPath(), new String[0]));
        }
        return null;
    }
    
    public static JSONObject secureReadJSON(final String filePath) throws IOException, ParseException, SyMException {
        return secureReadJSON(new File(filePath));
    }
    
    public static JSONObject secureReadJSON(final File file) throws IOException, ParseException, SyMException {
        JSONObject jsonObject = null;
        try (final Reader reader = secureGetReader(file)) {
            if (reader != null) {
                final JSONParser jsonParser = new JSONParser();
                jsonObject = (JSONObject)jsonParser.parse(reader);
            }
        }
        return jsonObject;
    }
    
    public static JSONArray secureReadJSONArray(final String filePath) throws IOException, ParseException, SyMException {
        return secureReadJSONArray(new File(filePath));
    }
    
    public static JSONArray secureReadJSONArray(final File file) throws IOException, ParseException, SyMException {
        JSONArray jsonArray = null;
        try (final Reader reader = secureGetReader(file)) {
            if (reader != null) {
                final JSONParser jsonParser = new JSONParser();
                jsonArray = (JSONArray)jsonParser.parse(reader);
            }
        }
        return jsonArray;
    }
    
    static {
        FileAccessUtil.logger = Logger.getLogger(FileAccessUtil.class.getName());
    }
}
