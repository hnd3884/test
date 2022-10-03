package com.me.tools.zcutil;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.logging.Level;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class AddorUpdateInstallation
{
    private static Logger logger;
    
    public Properties getBaseData(Properties meTrackData) {
        final String[] excludeArr = METrack.getZCUtil().getProductConf().getBaseFormExcludeFileds();
        final Properties detailsFromProduct = this.getInstalaltionDetailsFromProduct();
        if (detailsFromProduct != null && detailsFromProduct.size() > 0) {
            meTrackData = this.overWriteProductData(meTrackData, detailsFromProduct);
        }
        if (excludeArr != null) {
            for (int i = 0; i < excludeArr.length; ++i) {
                if (meTrackData.get(excludeArr[i].toString()) != null) {
                    meTrackData.remove(excludeArr[i].toString());
                }
            }
        }
        final Properties oldInsProp = this.getOldInstallationProps();
        if (oldInsProp != null && oldInsProp.size() > 0 && oldInsProp.getProperty("ID") != null) {
            meTrackData.setProperty("old_ins_id", oldInsProp.getProperty("ID"));
            if (oldInsProp.getProperty("installedtime") != null) {
                meTrackData.setProperty("old_ins_time", oldInsProp.getProperty("installedtime"));
            }
        }
        return meTrackData;
    }
    
    private Properties overWriteProductData(final Properties meTrackData, final Properties productData) {
        if (productData != null && productData.size() > 0) {
            meTrackData.putAll(productData);
        }
        return meTrackData;
    }
    
    private Properties getInstalaltionDetailsFromProduct() {
        try {
            if (METrack.getZCUtil().getConfValue().getProperty("dataHandler") != null) {
                final ZCDataHandler customDataHandler = (ZCDataHandler)Class.forName(METrack.getZCUtil().getConfValue().getProperty("dataHandler")).newInstance();
                return customDataHandler.getInstallationDetails();
            }
        }
        catch (final Exception e) {
            AddorUpdateInstallation.logger.log(Level.INFO, "Exception while getInstalaltionDetailsFromProduct : ", e.toString());
        }
        return null;
    }
    
    private Properties getOldInstallationProps() {
        final File f = new File(METrack.getConfDir() + File.separator + "old_zohocreator.properties");
        FileInputStream fis = null;
        try {
            if (f.exists()) {
                final Properties eProp = new Properties();
                fis = new FileInputStream(f);
                eProp.load(fis);
                return eProp;
            }
        }
        catch (final Exception e) {
            AddorUpdateInstallation.logger.log(Level.INFO, "Exception while getOldInstallationProps : ", e.toString());
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final IOException e2) {
                AddorUpdateInstallation.logger.log(Level.INFO, "Exception while getOldInstallationProps finally : ", e2.toString());
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final IOException e3) {
                AddorUpdateInstallation.logger.log(Level.INFO, "Exception while getOldInstallationProps finally : ", e3.toString());
            }
        }
        return null;
    }
    
    static {
        AddorUpdateInstallation.logger = Logger.getLogger(AddorUpdateInstallation.class.getName());
    }
}
