package com.adventnet.authorization.service;

import java.util.List;
import com.adventnet.mfw.message.MessageFilter;
import com.adventnet.mfw.message.MessageListener;
import com.adventnet.mfw.message.Messenger;
import java.util.ArrayList;
import com.adventnet.mfw.message.DataObjectFilter;
import com.adventnet.authentication.RoleCache;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.xml.Xml2DoConverter;
import com.adventnet.persistence.DataAccessException;
import java.net.URL;
import com.adventnet.authentication.util.AuthDBUtil;
import java.io.File;
import java.util.logging.Level;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.authorization.AuthorizationEngine;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.mfw.service.Service;

public class AuthorizationService implements Service
{
    private static Logger logger;
    private static String policyFileName;
    private static String authConfFileName;
    private static boolean fgaEnabled;
    private static boolean cgaEnabled;
    private String dataModelTopicName;
    
    public AuthorizationService() {
        this.dataModelTopicName = "DataModelTopic";
    }
    
    public void create(final DataObject serviceDO) throws Exception {
        AuthorizationEngine.isLocal = true;
        if (serviceDO == null) {
            throw new Exception("Authorization Service not initialized as service dataobject is empty");
        }
        final Iterator itr = serviceDO.getRows("ServiceProperties");
        Row temp = null;
        String fga = null;
        String cga = null;
        while (itr.hasNext()) {
            temp = itr.next();
            if (((String)temp.get("PROPERTY")).equals("FGA")) {
                fga = (String)temp.get("VALUE");
            }
            if (((String)temp.get("PROPERTY")).equals("CGA")) {
                cga = (String)temp.get("VALUE");
            }
        }
        AuthorizationService.fgaEnabled = Boolean.valueOf(fga);
        AuthorizationService.cgaEnabled = Boolean.valueOf(cga);
    }
    
    public void start() throws Exception {
        AuthorizationEngine.updateTableCache("security", true);
        if (PersistenceInitializer.isColdStart()) {}
        AuthorizationEngine.setFGAEnabled(AuthorizationService.fgaEnabled);
        AuthorizationEngine.setCGAEnabled(AuthorizationService.cgaEnabled);
        AuthorizationEngine.addTableCacheForUnModelledModules();
    }
    
    public void stop() {
        AuthorizationService.logger.log(Level.FINEST, "authorizationService.stopService invoked");
    }
    
    public void destroy() throws Exception {
        AuthorizationEngine.setCGAEnabled(false);
        AuthorizationEngine.setFGAEnabled(false);
    }
    
    public void refresh() {
    }
    
    private void populateDefaultAuthConf(final String module) throws Exception {
        try {
            final String homeDir = System.getProperty("server.dir") + "/";
            AuthorizationService.logger.log(Level.FINEST, "home dir obtained is : {0}", homeDir);
            final URL url = new File(homeDir + "conf/Authentication/" + AuthorizationService.authConfFileName).toURL();
            AuthorizationService.logger.log(Level.FINEST, "auth conf file url obtained is : {0}", url);
            if (url != null) {
                final Row dao = null;
                AuthDBUtil.populateAuthConf(url, dao);
            }
        }
        catch (final Exception e) {
            AuthorizationService.logger.log(Level.FINEST, "Exception thrown while populating default configuration : {0}", e);
            throw new RuntimeException(e);
        }
    }
    
    private void populateDefaultAuthPolicy(final String module) throws Exception {
        URL url = null;
        final File file = new File(System.getProperty("server.dir") + "/conf/Authorization/" + AuthorizationService.policyFileName);
        if (file.exists()) {
            url = file.toURL();
            AuthorizationService.logger.log(Level.FINEST, "default authorization policy file url obtained is : {0}", url);
            if (url == null) {
                AuthorizationService.logger.log(Level.FINEST, "No authorization policy found, ignored");
                return;
            }
            this.populate(url, module);
        }
    }
    
    private Row getModuleRow(final String moduleName) throws DataAccessException {
        if (moduleName == null) {
            return null;
        }
        final Row confToModuleRow = new Row("ConfFileToAppln");
        confToModuleRow.set(2, (Object)moduleName);
        return confToModuleRow;
    }
    
    private void populate(final URL url, final String module) throws Exception {
        try {
            final DataObject urlDO = Xml2DoConverter.transform(url);
            AuthorizationService.logger.log(Level.FINEST, "Dataobject obtained from url : {0} is : {1}", new Object[] { url, urlDO });
            DataAccess.add(urlDO);
        }
        catch (final DataAccessException dae) {
            AuthorizationService.logger.log(Level.SEVERE, "DataAccessException caught while trying to populate url : {0}", url);
            AuthorizationService.logger.log(Level.SEVERE, "Exception  : {0}", (Throwable)dae);
            throw new RuntimeException("DataAccessException occured while populating dataobject", (Throwable)dae);
        }
    }
    
    private void registerForRoleCaching() throws Exception {
        RoleCache.populateRoleCache();
        final DataObjectFilter dof = new DataObjectFilter();
        final List tblList = new ArrayList();
        tblList.add("AaaAuthorizedRole");
        tblList.add("AaaImpliedRole");
        dof.setTableList(tblList);
        Messenger.subscribe(this.dataModelTopicName, (MessageListener)new DataModelNotificationListener(), true, (MessageFilter)dof);
    }
    
    static {
        AuthorizationService.logger = Logger.getLogger(AuthorizationService.class.getName());
        AuthorizationService.policyFileName = "auth-policy.xml";
        AuthorizationService.authConfFileName = "auth-conf.xml";
        AuthorizationService.fgaEnabled = false;
        AuthorizationService.cgaEnabled = false;
    }
    
    public class DataModelNotificationListener implements MessageListener
    {
        public void onMessage(final Object msgArg) {
            try {
                System.out.println("Notification received .......... ");
                RoleCache.populateRoleCache();
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
