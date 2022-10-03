package com.me.devicemanagement.onpremise.server.sensitive.data;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.util.Properties;
import org.w3c.dom.Element;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.io.File;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Map;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.redis.RedisServerUtil;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.admin.CredentialManager;
import java.util.Collection;
import java.util.logging.Level;
import java.util.HashSet;
import java.util.logging.Logger;

public class SensitiveDataProvider implements SensitiveData
{
    public Logger logger;
    
    public SensitiveDataProvider() {
        this.logger = Logger.getLogger(SensitiveDataProvider.class.getName());
    }
    
    @Override
    public HashSet collectDataList() {
        this.logger.log(Level.INFO, "started collecting sensitive data list form SensitiveDataProvider !");
        final HashSet dataList = new HashSet();
        dataList.addAll(this.getCredentialPwdList());
        dataList.addAll(this.addData("DBBackupInfo", "BACKUP_PASSWORD"));
        dataList.addAll(this.addData("APIKeyInfo", "API_KEY"));
        dataList.addAll(this.addData("ApplnServerSettings", "AUTHENDICATION_KEY"));
        dataList.addAll(this.redisPwd());
        dataList.addAll(this.getDBPwdList());
        return dataList;
    }
    
    private HashSet getCredentialPwdList() {
        final HashSet dataList = new HashSet();
        try {
            final DataObject credDO = new CredentialManager().getCredentialDO((Criteria)null);
            final Iterator iterable = credDO.getRows("Credential");
            while (iterable.hasNext()) {
                final Row row = iterable.next();
                String password = (String)row.get("CRD_PASSWORD");
                if (password != null && !password.isEmpty() && !password.equals("")) {
                    dataList.add(password);
                    password = ApiFactoryProvider.getCryptoAPI().decrypt(password, (Integer)row.get("CRD_ENC_TYPE"));
                    dataList.add(password);
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Exception while getting Credential Password List !", (Throwable)e);
        }
        return dataList;
    }
    
    private HashSet redisPwd() {
        final HashSet dataList = new HashSet();
        final String redisPassword = RedisServerUtil.getPasswordFromDBorCache();
        if (redisPassword != null && !redisPassword.isEmpty()) {
            dataList.add(redisPassword);
        }
        return dataList;
    }
    
    protected DataObject getTableDO(final String table, final String column) throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(table));
        query.addSelectColumn(new Column(table, (column != null) ? column : "*"));
        return SyMUtil.getPersistence().get(query);
    }
    
    protected HashSet addData(final String table, final String column) {
        final HashSet dataList = new HashSet();
        try {
            final DataObject data = this.getTableDO(table, column);
            final Iterator iterable = data.getRows(table);
            while (iterable.hasNext()) {
                final Row row = iterable.next();
                final String key = (String)row.get(column);
                if (key != null && !key.isEmpty()) {
                    dataList.add(key);
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Exception while adding or getting DataObject for the given Table !", (Throwable)e);
        }
        return dataList;
    }
    
    private HashSet getDBPwdList() {
        final HashSet dataList = new HashSet();
        Properties dbProps = null;
        try {
            dbProps = PersistenceInitializer.getDBProps(PersistenceInitializer.getDBParamsFilePath());
            dataList.add(dbProps.getProperty("password"));
            dbProps.putAll(PersistenceInitializer.getConfigurationProps(PersistenceInitializer.getConfigurationValue("DBName")));
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while getting DB Password !", e);
        }
        if (dbProps != null && DBUtil.getActiveDBName().equalsIgnoreCase("mssql")) {
            final String DEFAULT_MASTERKEY_PASSWORD = "9c41214edd6852e46b05a4848ab325a7";
            final String DEFAULT_CERTIFICATE_NAME = "fa2217c8f89bafd21c1f742e14cfaf36";
            final String DEFAULT_SYMM_KEY_NAME = "d58be16e0295ed44430dbca186a1e14c";
            final String DEFAULT_SYMM_KEY_ALGO = "36f6b0bcb40f13f3cf0ba3310ab7340e";
            final String DEFAULT_SYMM_KEY_ALTALGO = "fcbe24dc39fe4ee858348c7765ba86d3";
            final String DEFAULT_IDENTITY_VALUE = "f61a29466a4c66cabc449d050468a6b54b45a0d6439c91eadc57920dbc58e353";
            final String DEFAULT_KEY_SOURCE = "00849df82df643c79c0c29d8bfa31160";
            final String masterkeyPass = dbProps.getProperty("masterkey.password");
            dataList.add(masterkeyPass);
            dataList.add((masterkeyPass != null) ? CryptoUtil.decrypt(masterkeyPass) : ApiFactoryProvider.getCryptoAPI().decrypt("9c41214edd6852e46b05a4848ab325a7", (String)null, (String)null));
            dataList.add(dbProps.getProperty("certificate.name", ApiFactoryProvider.getCryptoAPI().decrypt("fa2217c8f89bafd21c1f742e14cfaf36", (String)null, (String)null)));
            dataList.add(dbProps.getProperty("symmetrickey.name", ApiFactoryProvider.getCryptoAPI().decrypt("d58be16e0295ed44430dbca186a1e14c", (String)null, (String)null)));
            dataList.add(dbProps.getProperty("symmetrickey.algo", ApiFactoryProvider.getCryptoAPI().decrypt("36f6b0bcb40f13f3cf0ba3310ab7340e", (String)null, (String)null)));
            dataList.add(dbProps.getProperty("symmetrickey.altalgo", ApiFactoryProvider.getCryptoAPI().decrypt("fcbe24dc39fe4ee858348c7765ba86d3", (String)null, (String)null)));
            dataList.add(dbProps.getProperty("identity.value", ApiFactoryProvider.getCryptoAPI().decrypt("f61a29466a4c66cabc449d050468a6b54b45a0d6439c91eadc57920dbc58e353", (String)null, (String)null)));
            dataList.add(dbProps.getProperty("key.source", ApiFactoryProvider.getCryptoAPI().decrypt("00849df82df643c79c0c29d8bfa31160", (String)null, (String)null)));
        }
        else if (dbProps != null && DBUtil.getActiveDBName().equalsIgnoreCase("postgres")) {
            if (dbProps.getProperty("r_password") != null && dbProps.containsKey("r_password")) {
                final String medcPwd = dbProps.getProperty("r_password");
                try {
                    dataList.add(CryptoUtil.decrypt(medcPwd, 2));
                }
                catch (final Exception e2) {
                    this.logger.log(Level.WARNING, "Exception while decrypting medc user Password !");
                }
            }
            final String pgPwd = "95cf4a1422fd57a4f70fcdc1c7a11303";
            dataList.add(ApiFactoryProvider.getCryptoAPI().decrypt("95cf4a1422fd57a4f70fcdc1c7a11303", (String)null, (String)null));
            try {
                final File fXmlFile = new File(System.getProperty("server.home") + File.separator + "conf" + File.separator + "customer-config.xml");
                final DocumentBuilder dBuilder = XMLUtils.getDocumentBuilderInstance();
                final Document doc = dBuilder.parse(fXmlFile);
                final Element rootElement = (Element)doc.getElementsByTagName("extended-configurations").item(0);
                final NodeList exeOrder = rootElement.getChildNodes();
                for (int taskNumber = 0; taskNumber < exeOrder.getLength(); ++taskNumber) {
                    final Node node = exeOrder.item(taskNumber);
                    if (node.getNodeType() == 1) {
                        final Element eElement = (Element)node;
                        final String ecTag = eElement.getAttribute("name");
                        if (ecTag.equalsIgnoreCase("ECTag")) {
                            dataList.add(CryptoUtil.decrypt(eElement.getAttribute("value")));
                        }
                    }
                }
            }
            catch (final Exception e2) {
                this.logger.log(Level.WARNING, "Exception while parsing and getting ECTag values ", e2);
            }
        }
        return dataList;
    }
}
