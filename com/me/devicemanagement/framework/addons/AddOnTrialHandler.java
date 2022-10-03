package com.me.devicemanagement.framework.addons;

import java.util.Hashtable;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
import com.me.devicemanagement.framework.addons.crypto.AddOnCryptoAPI;
import java.util.Date;
import java.util.Calendar;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

public class AddOnTrialHandler
{
    private static AddOnTrialHandler addOnTrialHandler;
    private static Logger logger;
    private static String confFile;
    public static String addOnTrialExpKey;
    
    public static AddOnTrialHandler getInstance() {
        if (AddOnTrialHandler.addOnTrialHandler == null) {
            AddOnTrialHandler.addOnTrialHandler = new AddOnTrialHandler();
        }
        return AddOnTrialHandler.addOnTrialHandler;
    }
    
    private static Properties loadProperties(final String confFile) {
        final Properties properties = new Properties();
        FileInputStream fis = null;
        final FileOutputStream fos = null;
        try {
            if (new File(confFile).exists()) {
                fis = new FileInputStream(confFile);
                properties.load(fis);
                fis.close();
            }
        }
        catch (final Exception var16) {
            AddOnTrialHandler.logger.log(Level.SEVERE, "Caught exception: ", var16);
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception var17) {
                AddOnTrialHandler.logger.log(Level.SEVERE, "Caught exception while closing the file reader: ", var17);
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception var18) {
                AddOnTrialHandler.logger.log(Level.SEVERE, "Caught exception while closing the file reader: ", var18);
            }
        }
        return properties;
    }
    
    public void generateTrialConfFile(final String addOnName) {
        FileWriter fileWriter = null;
        try {
            if (this.isAddonEnabledForFirstTime(addOnName)) {
                final Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(5, 30);
                final String value = AddOnCryptoAPI.getInstance().encrypt(String.valueOf(c.getTimeInMillis()));
                final Properties props = loadProperties(AddOnTrialHandler.confFile);
                if (props.getProperty(addOnName + AddOnTrialHandler.addOnTrialExpKey) == null) {
                    ((Hashtable<String, String>)props).put(addOnName + AddOnTrialHandler.addOnTrialExpKey, value);
                    fileWriter = new FileWriter(AddOnTrialHandler.confFile, false);
                    props.store(fileWriter, "");
                }
            }
        }
        catch (final Exception ex) {
            AddOnTrialHandler.logger.log(Level.SEVERE, "Cannot generate Addons Trial File : ", ex);
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                }
                catch (final IOException e) {
                    AddOnTrialHandler.logger.log(Level.SEVERE, "Cannot close the filewriter : ", e);
                }
            }
        }
        finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                }
                catch (final IOException e2) {
                    AddOnTrialHandler.logger.log(Level.SEVERE, "Cannot close the filewriter : ", e2);
                }
            }
        }
    }
    
    public void disableTrialConfFile(final String addOnName) {
        FileWriter fileWriter = null;
        try {
            if (this.isAddOnTrialEnabled(addOnName)) {
                final Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(5, -1);
                AddOnTrialHandler.logger.log(Level.INFO, "New Time:" + c.getTime());
                final String value = AddOnCryptoAPI.getInstance().encrypt(String.valueOf(c.getTimeInMillis()));
                final Properties props = loadProperties(AddOnTrialHandler.confFile);
                ((Hashtable<String, String>)props).put(addOnName + AddOnTrialHandler.addOnTrialExpKey, value);
                fileWriter = new FileWriter(AddOnTrialHandler.confFile, false);
                props.store(fileWriter, "");
            }
        }
        catch (final Exception ex) {
            AddOnTrialHandler.logger.log(Level.SEVERE, "Cannot generate Addons Trial File : ", ex);
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                }
                catch (final IOException e) {
                    AddOnTrialHandler.logger.log(Level.SEVERE, "Cannot close the filewriter : ", e);
                }
            }
        }
        finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                }
                catch (final IOException e2) {
                    AddOnTrialHandler.logger.log(Level.SEVERE, "Cannot close the filewriter : ", e2);
                }
            }
        }
    }
    
    public boolean isAddonEnabledForFirstTime(final String name) throws DataAccessException {
        final SelectQuery addONQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AddOns"));
        addONQuery.addJoin(new Join("AddOns", "AddOnStatus", new String[] { "ADD_ON_ID" }, new String[] { "ADD_ON_ID" }, 2));
        addONQuery.addSelectColumn(new Column("AddOnStatus", "ADD_ON_STATUS_ID"));
        addONQuery.setCriteria(new Criteria(new Column("AddOns", "ADD_ON_NAME"), (Object)name, 0));
        final DataObject dataObject = DataAccess.get(addONQuery);
        return dataObject.isEmpty() || dataObject.getRow("AddOnStatus") == null;
    }
    
    public boolean isAddOnTrialEnabled(final String name) throws Exception {
        Boolean isAddonTrialStarted = Boolean.FALSE;
        if (new File(AddOnTrialHandler.confFile).exists()) {
            final Properties addonProps = loadProperties(AddOnTrialHandler.confFile);
            if (addonProps != null && addonProps.get(name + AddOnTrialHandler.addOnTrialExpKey) != null) {
                isAddonTrialStarted = addonProps.containsKey(name + AddOnTrialHandler.addOnTrialExpKey);
            }
        }
        return isAddonTrialStarted;
    }
    
    public long getAddOnTrialPeriodDiff(final String addOnName) {
        long dateDiff = -1L;
        try {
            if (new File(AddOnTrialHandler.confFile).exists()) {
                final Properties addOnProps = loadProperties(AddOnTrialHandler.confFile);
                if (addOnProps != null) {
                    final String addOnTrialValidity = addOnProps.getProperty(addOnName + AddOnTrialHandler.addOnTrialExpKey);
                    if (addOnTrialValidity != null) {
                        final String expiryDate = AddOnCryptoAPI.getInstance().decrypt(addOnTrialValidity);
                        final Date today = Calendar.getInstance().getTime();
                        dateDiff = getDateDiff(today.getTime(), Long.parseLong(expiryDate));
                        AddOnTrialHandler.logger.log(Level.INFO, " DB AddOn Trial expiry period " + dateDiff);
                        return dateDiff;
                    }
                }
            }
        }
        catch (final Exception exc) {
            AddOnTrialHandler.logger.log(Level.WARNING, " error while fetch AddOn trial license period ", exc);
        }
        AddOnTrialHandler.logger.log(Level.INFO, " DB AddOn Trial expiry period " + dateDiff);
        return dateDiff;
    }
    
    public static long getDateDiff(final long startTimeInMS, final long endTimeInMS) {
        return (endTimeInMS - startTimeInMS) / 86400000L;
    }
    
    static {
        AddOnTrialHandler.addOnTrialHandler = null;
        AddOnTrialHandler.logger = Logger.getLogger("SecurityAddonLogger");
        AddOnTrialHandler.confFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "AddOns" + File.separator + "addonsTrial.conf";
        AddOnTrialHandler.addOnTrialExpKey = "TrialStarted";
    }
}
