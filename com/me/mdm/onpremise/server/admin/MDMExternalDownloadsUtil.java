package com.me.mdm.onpremise.server.admin;

import java.util.Hashtable;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.nio.file.DirectoryStream;
import java.util.List;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URL;
import java.util.ArrayList;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.HashMap;
import javax.crypto.SecretKey;
import org.apache.commons.codec.binary.Base64;
import java.security.Key;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.FileOutputStream;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.Iterator;
import java.util.jar.Manifest;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.io.File;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Properties;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MDMExternalDownloadsUtil
{
    private static Logger logger;
    
    public int copyRemotefiles(final String sourceUrl, final String destinationFilePath, final String fileLastModifiedTime) throws Exception {
        int statusCode = 10008;
        try {
            MDMExternalDownloadsUtil.logger.log(Level.INFO, "Going to access the URL : {0} to find out the latest updates.", sourceUrl);
            MDMExternalDownloadsUtil.logger.log(Level.INFO, "Destination File : {0}", destinationFilePath);
            MDMUtil.addOrUpdateDCServerInfoCache((DataObject)null);
            final Properties headers = new Properties();
            ((Hashtable<String, String>)headers).put("Pragma", "no-cache");
            ((Hashtable<String, String>)headers).put("Cache-Control", "no-cache");
            if (fileLastModifiedTime != null) {
                final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                ((Hashtable<String, String>)headers).put("If-Modified-Since", sdf.format(new Date(Long.valueOf(fileLastModifiedTime))));
            }
            final DownloadManager downloadMgr = DownloadManager.getInstance();
            final DownloadStatus downloadStatus = downloadMgr.downloadFile(sourceUrl, destinationFilePath, (Properties)null, headers, new SSLValidationType[0]);
            statusCode = downloadStatus.getStatus();
            if (statusCode == 0) {
                MDMExternalDownloadsUtil.logger.log(Level.INFO, "Successfully Downloaded the  File : {0}", sourceUrl);
            }
            else {
                MDMExternalDownloadsUtil.logger.log(Level.INFO, " Download Failed for  File : {0}with ErrorCode:{1}", new Object[] { sourceUrl, statusCode });
            }
        }
        catch (final Exception ee) {
            MDMExternalDownloadsUtil.logger.log(Level.INFO, "Exception while Downloading File From{0}with Error code {1}", new Object[] { sourceUrl, ee });
        }
        return statusCode;
    }
    
    public String generateChecksum(final File filePath) {
        String Checksum = "";
        FileInputStream fis = null;
        try {
            final String filepath = filePath.getAbsolutePath();
            final StringBuilder sb = new StringBuilder();
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            fis = new FileInputStream(filepath);
            final byte[] dataBytes = new byte[1024];
            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            final byte[] mdbytes = md.digest();
            for (int i = 0; i < mdbytes.length; ++i) {
                sb.append(Integer.toString((mdbytes[i] & 0xFF) + 256, 16).substring(1));
            }
            Checksum = sb.toString();
        }
        catch (final Exception ex) {
            MDMExternalDownloadsUtil.logger.log(Level.SEVERE, null, ex);
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex) {
                MDMExternalDownloadsUtil.logger.log(Level.SEVERE, null, ex);
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex2) {
                MDMExternalDownloadsUtil.logger.log(Level.SEVERE, null, ex2);
            }
        }
        return Checksum;
    }
    
    public boolean jarSignVerification(final String jarFilePath) {
        try {
            boolean SFFileverified = false;
            boolean RSAFileverified = false;
            boolean digestVerified = false;
            final JarFile jar = new JarFile(jarFilePath);
            final Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                try {
                    final InputStream input = jar.getInputStream(entry);
                    final String entryName = entry.getName();
                    if (entryName.equalsIgnoreCase("META-INF/ZOHO_COR.SF")) {
                        SFFileverified = true;
                    }
                    if (!entryName.equalsIgnoreCase("META-INF/ZOHO_COR.RSA")) {
                        continue;
                    }
                    RSAFileverified = true;
                }
                catch (final SecurityException se) {
                    MDMExternalDownloadsUtil.logger.log(Level.SEVERE, null, se);
                    return false;
                }
            }
            if (SFFileverified && RSAFileverified) {
                final Manifest manifest = new JarFile(jarFilePath).getManifest();
                final Map<String, Attributes> manifestEntries = manifest.getEntries();
                for (final Map.Entry<String, Attributes> entry2 : manifestEntries.entrySet()) {
                    final Iterator<Object> attribIter = entry2.getValue().keySet().iterator();
                    while (attribIter.hasNext()) {
                        final String attribute = attribIter.next().toString();
                        if (attribute.equalsIgnoreCase("SHA-256-Digest")) {
                            digestVerified = true;
                        }
                    }
                }
            }
            else {
                MDMExternalDownloadsUtil.logger.log(Level.INFO, "********************Error in SHA and SF verification********************");
            }
            if (digestVerified) {
                return true;
            }
            MDMExternalDownloadsUtil.logger.log(Level.INFO, "********************Error in manifest verification********************");
        }
        catch (final Exception ex) {
            MDMExternalDownloadsUtil.logger.log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean jarCheckSumVerification(final String checkSumFilePath, final String localJarFilePath) throws Exception {
        try {
            String getEncryptedCheckSum = null;
            final Properties checkSumPropertyfile = FileAccessUtil.readProperties(checkSumFilePath);
            if (checkSumPropertyfile != null) {
                getEncryptedCheckSum = checkSumPropertyfile.getProperty("jarValidator");
            }
            final File temporaryJarFile = new File(localJarFilePath);
            String currentChecksum = this.generateChecksum(temporaryJarFile);
            currentChecksum = this.checksumEncryption_hmacsha1("memdm2014", currentChecksum);
            if (getEncryptedCheckSum != null && getEncryptedCheckSum.equals(currentChecksum)) {
                return true;
            }
        }
        catch (final Exception ex) {
            MDMExternalDownloadsUtil.logger.log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public Boolean FileReplacement(final String sourceFilePath, final String destinationFilePath) {
        FileOutputStream outFile = null;
        try {
            final InputStream is = new FileInputStream(sourceFilePath);
            if (destinationFilePath != null) {
                outFile = (FileOutputStream)ApiFactoryProvider.getFileAccessAPI().writeFile(destinationFilePath);
                final byte[] buf = new byte[8192];
                int len;
                while ((len = is.read(buf)) != -1) {
                    outFile.write(buf, 0, len);
                }
            }
        }
        catch (final Exception e) {
            MDMExternalDownloadsUtil.logger.log(Level.SEVERE, null, e);
            return false;
        }
        finally {
            try {
                if (outFile != null) {
                    outFile.close();
                }
            }
            catch (final Exception ex) {
                MDMExternalDownloadsUtil.logger.log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
    
    public String checksumEncryption_hmacsha1(final String key, final String plainText) {
        try {
            SecretKey secretKey = null;
            final byte[] keyBytes = key.getBytes();
            secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");
            final Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKey);
            final byte[] text = plainText.getBytes();
            return new String(Base64.encodeBase64(mac.doFinal(text))).trim();
        }
        catch (final Exception e) {
            MDMExternalDownloadsUtil.logger.log(Level.SEVERE, null, e);
            return null;
        }
    }
    
    public HashMap dynamicJarExecution() {
        HashMap flashMsgFiles = new HashMap();
        try {
            final ClassLoader cLoader = this.getClass().getClassLoader();
            final String BASE_DIRECTORY = SyMUtil.getInstallationDir() + File.separator + "lib";
            final List<URL> urls = new ArrayList<URL>();
            try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(BASE_DIRECTORY, new String[0]), "*.jar")) {
                for (final Path path : directoryStream) {
                    urls.add(path.toUri().toURL());
                }
            }
            final String Jarfile = this.mdmpermenantDownloadspath() + "MEMDMDynamicTaskHandler.jar";
            final File myFile = new File(Jarfile);
            final URL url = myFile.toURI().toURL();
            urls.add(url);
            final URLClassLoader urlClassLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]), cLoader);
            final Class<?> loadedMyClass = urlClassLoader.loadClass("com.me.mdm.onpremise.server.flash.FlashMessageJsonParser");
            final Constructor<?> constructor = loadedMyClass.getConstructor((Class<?>[])new Class[0]);
            final Object myClassObject = constructor.newInstance(new Object[0]);
            final Method method = loadedMyClass.getMethod("parseJson", (Class<?>[])new Class[0]);
            flashMsgFiles = (HashMap)method.invoke(myClassObject, new Object[0]);
        }
        catch (final ClassNotFoundException ex) {
            MDMExternalDownloadsUtil.logger.log(Level.SEVERE, "Exception occurs in Class MDMDynamicJarHandlerUtil  : FlashMessageJsonParser cannot be found");
        }
        catch (final Exception ex2) {
            MDMExternalDownloadsUtil.logger.log(Level.SEVERE, null, ex2);
        }
        return flashMsgFiles;
    }
    
    public String mdmtemporaryDownloadspath() {
        String downloadsFilePath = null;
        try {
            downloadsFilePath = SyMUtil.getInstallationDir() + File.separator + "mdm" + File.separator + "mdmtempfiles" + File.separator;
        }
        catch (final Exception ex) {
            MDMExternalDownloadsUtil.logger.log(Level.SEVERE, null, ex);
        }
        return downloadsFilePath;
    }
    
    public String mdmpermenantDownloadspath() {
        String downloadsFilePath = null;
        try {
            downloadsFilePath = SyMUtil.getInstallationDir() + File.separator + "mdm" + File.separator + "mdmPermenantfiles" + File.separator;
        }
        catch (final Exception ex) {
            MDMExternalDownloadsUtil.logger.log(Level.SEVERE, null, ex);
        }
        return downloadsFilePath;
    }
    
    static {
        MDMExternalDownloadsUtil.logger = Logger.getLogger("MDMExternalDownloadsLogger");
    }
}
