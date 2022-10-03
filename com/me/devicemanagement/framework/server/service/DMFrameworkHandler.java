package com.me.devicemanagement.framework.server.service;

import java.util.Hashtable;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.authentication.UserListener;
import com.me.ems.framework.server.tabcomponents.core.TabComponentUserListener;
import com.me.devicemanagement.framework.server.authentication.UserListenerHandler;
import com.me.devicemanagement.framework.server.customer.CustomerListener;
import com.me.devicemanagement.framework.server.customer.CustomerHandler;
import com.me.devicemanagement.framework.server.customer.GeneralCustomerListenerBaseImpl;
import java.util.Properties;
import sun.net.www.protocol.http.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Logger;

public class DMFrameworkHandler
{
    private static Logger logger;
    
    public static void initiate() {
        initializeCommonEnv();
        CustomerInfoUtil.setMSPProperties();
        CustomerInfoUtil.setSASProperties();
        registerGeenralCustomerListenerBase();
        registerTabComponentUserListener();
        try {
            DMFrameworkHandler.logger.log(Level.INFO, "updateHttpURLConnectionJava Version() Started");
            updateHttpURLConnectionJavaVerion();
            DMFrameworkHandler.logger.log(Level.INFO, "updateHttpURLConnectionJava Version() Ended");
        }
        catch (final Exception e) {
            DMFrameworkHandler.logger.log(Level.SEVERE, "Exception while JavaVersion Initialization", e);
        }
    }
    
    private static void updateHttpURLConnectionJavaVerion() {
        final String systemProperties = System.getProperty("server.home") + "/conf/system_properties.conf";
        if (new File(systemProperties).exists()) {
            Properties prop = null;
            try {
                prop = FileAccessUtil.readProperties(systemProperties);
                final String http_agent_value = prop.getProperty("http.agent");
                final String java_version = System.getProperty("java.version");
                System.setProperty("java.version", "");
                System.setProperty("http.agent", http_agent_value);
                final HttpURLConnection connection = new HttpURLConnection(new URL("https://"), Proxy.NO_PROXY);
                System.setProperty("java.version", java_version);
            }
            catch (final Exception e) {
                DMFrameworkHandler.logger.log(Level.SEVERE, "Exception while establishing the connection", e);
            }
        }
    }
    
    private static void registerGeenralCustomerListenerBase() {
        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
        CustomerInfoUtil.getInstance();
        final boolean isSAS = CustomerInfoUtil.isSAS();
        if (isMSP || isSAS) {
            final CustomerListener commonCustomerListener = new GeneralCustomerListenerBaseImpl();
            CustomerHandler.getInstance().addCustomerListener(commonCustomerListener);
        }
    }
    
    private static void registerTabComponentUserListener() {
        UserListenerHandler.getInstance().addUserListener(new TabComponentUserListener());
    }
    
    private static void initializeCommonEnv() {
        ApiFactoryProvider.getCacheAccessAPI().initializeCache();
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "DDExtnParserTask");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "startupPool");
        final Properties properties = new Properties();
        ((Hashtable<String, String>)properties).put("poolName", "startupPool");
        ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.devicemanagement.framework.server.ddextension.DDExtnParserTask", taskInfoMap, properties);
    }
    
    public static void stop() {
    }
    
    public static void destroy() {
    }
    
    static {
        DMFrameworkHandler.logger = Logger.getLogger("DCServiceLogger");
    }
}
