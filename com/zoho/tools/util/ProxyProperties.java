package com.zoho.tools.util;

import java.util.Hashtable;
import java.nio.file.Paths;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.io.OutputStream;
import java.util.Properties;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.io.File;
import java.util.logging.Logger;

public class ProxyProperties
{
    private static final Logger LOGGER;
    private String proxyAddress;
    private String proxyPort;
    private String proxyUserName;
    private String proxyPass;
    private static final File PROPS_FILE;
    private static final File PROPS_FILE_TEMP;
    
    public String getProxyAddress() {
        return this.proxyAddress;
    }
    
    public void setProxyAddress(final String proxyAddress) {
        this.proxyAddress = proxyAddress;
    }
    
    public String getProxyPort() {
        return this.proxyPort;
    }
    
    public void setProxyPort(final String proxyPort) {
        this.proxyPort = proxyPort;
    }
    
    public String getProxyUserName() {
        return this.proxyUserName;
    }
    
    public void setProxyUserName(final String proxyUserName) {
        this.proxyUserName = proxyUserName;
    }
    
    public String getProxyPass() {
        return this.proxyPass;
    }
    
    public void setProxyPass(final String proxyPass) {
        this.proxyPass = proxyPass;
    }
    
    public static void write(final ProxyProperties props) throws Exception {
        if (props == null) {
            return;
        }
        if (Files.exists(ProxyProperties.PROPS_FILE.toPath(), new LinkOption[0])) {
            FileUtil.moveDirectory(ProxyProperties.PROPS_FILE, ProxyProperties.PROPS_FILE_TEMP);
        }
        try (final FileOutputStream fos = new FileOutputStream(ProxyProperties.PROPS_FILE)) {
            final Properties properties = new Properties();
            ((Hashtable<String, String>)properties).put(ProxySettings.PROXY_ADDRESS.getKey(), (props.getProxyAddress() != null) ? props.getProxyAddress() : "");
            ((Hashtable<String, String>)properties).put(ProxySettings.PROXY_PORT.getKey(), (props.getProxyPort() != null) ? props.getProxyPort() : "");
            ((Hashtable<String, String>)properties).put(ProxySettings.PROXY_USERNAME.getKey(), (props.getProxyUserName() != null) ? props.getProxyUserName() : "");
            if (CryptoHelper.isInitialized()) {
                if (CryptoHelper.isEncrypted(props.getProxyPass())) {
                    props.setProxyPass(CryptoHelper.decrypt(props.getProxyPass()));
                }
                ((Hashtable<String, String>)properties).put(ProxySettings.PROXY_PASSWORD.getKey(), (props.getProxyPass() != null) ? CryptoHelper.encrypt(props.getProxyPass()) : "");
            }
            else {
                ((Hashtable<String, String>)properties).put(ProxySettings.PROXY_PASSWORD.getKey(), (props.getProxyPass() != null) ? props.getProxyPass() : "");
            }
            properties.store(fos, "Proxy Settings");
            Files.deleteIfExists(ProxyProperties.PROPS_FILE_TEMP.toPath());
            ProxyProperties.LOGGER.log(Level.INFO, "Stored Proxy information successfully");
        }
        catch (final Exception e) {
            Files.deleteIfExists(ProxyProperties.PROPS_FILE.toPath());
            FileUtil.moveDirectory(ProxyProperties.PROPS_FILE_TEMP, ProxyProperties.PROPS_FILE);
            throw e;
        }
    }
    
    public static ProxyProperties read() throws Exception {
        ProxyProperties props = null;
        if (Files.exists(ProxyProperties.PROPS_FILE.toPath(), new LinkOption[0])) {
            boolean isReWrite = false;
            try (final FileInputStream fis = new FileInputStream(ProxyProperties.PROPS_FILE)) {
                final Properties properties = new Properties();
                props = new ProxyProperties();
                properties.load(fis);
                props.setProxyAddress(properties.getProperty(ProxySettings.PROXY_ADDRESS.getKey(), ""));
                props.setProxyPort(properties.getProperty(ProxySettings.PROXY_PORT.getKey(), ""));
                props.setProxyUserName(properties.getProperty(ProxySettings.PROXY_USERNAME.getKey(), ""));
                String password = properties.getProperty(ProxySettings.PROXY_PASSWORD.getKey(), "");
                if (password != null && !password.isEmpty()) {
                    if (CryptoHelper.isEncrypted(password)) {
                        password = CryptoHelper.decrypt(password);
                    }
                    else {
                        isReWrite = true;
                    }
                }
                props.setProxyPass(password);
            }
            if (isReWrite) {
                write(props);
                ProxyProperties.LOGGER.log(Level.INFO, "Proxy password encrypted and successfully written to um_proxy.props file");
            }
        }
        return props;
    }
    
    static {
        LOGGER = Logger.getLogger(ProxyProperties.class.getName());
        PROPS_FILE = Paths.get(UpdateManagerUtil.getHomeDirectory(), "conf", "um_proxy.props").toFile();
        PROPS_FILE_TEMP = Paths.get(UpdateManagerUtil.getHomeDirectory(), "conf", "um_proxy.props.old").toFile();
    }
    
    private enum ProxySettings
    {
        PROXY_ADDRESS("PROXY_ADDRESS"), 
        PROXY_PORT("PROXY_PORT"), 
        PROXY_USERNAME("PROXY_USERNAME"), 
        PROXY_PASSWORD("PROXY_PASSWORD");
        
        String key;
        
        private ProxySettings(final String key) {
            this.key = key;
        }
        
        public String getKey() {
            return this.key;
        }
    }
}
