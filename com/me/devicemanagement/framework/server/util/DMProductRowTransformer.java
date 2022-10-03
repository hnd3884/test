package com.me.devicemanagement.framework.server.util;

import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.persistence.Row;
import org.xml.sax.Attributes;
import java.util.logging.Level;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;
import com.adventnet.persistence.xml.DefaultRowTransformer;

public class DMProductRowTransformer extends DefaultRowTransformer
{
    static final Logger LOGGER;
    static Boolean isEMSFlowSupported;
    
    private static Properties readProperties(final String confFileName) throws Exception {
        final Properties props = new Properties();
        FileInputStream ism = null;
        try {
            if (new File(confFileName).exists()) {
                ism = new FileInputStream(confFileName);
                props.load(ism);
            }
        }
        catch (final Exception ex) {
            DMProductRowTransformer.LOGGER.log(Level.SEVERE, "Caught exception while reading properties from file: " + confFileName, ex);
            try {
                if (ism != null) {
                    ism.close();
                }
            }
            catch (final Exception ex) {
                DMProductRowTransformer.LOGGER.log(Level.SEVERE, "Caught exception while reading properties from file: " + confFileName, ex);
            }
        }
        finally {
            try {
                if (ism != null) {
                    ism.close();
                }
            }
            catch (final Exception ex2) {
                DMProductRowTransformer.LOGGER.log(Level.SEVERE, "Caught exception while reading properties from file: " + confFileName, ex2);
            }
        }
        return props;
    }
    
    public Row createRow(final String tName, final Attributes attributes) {
        final String serverType = attributes.getValue("EMS_SERVER_TYPE");
        if (serverType != null) {
            boolean allowRowPopulation = false;
            final boolean isNegated = serverType.contains("!");
            if (serverType.contains("PROBE")) {
                allowRowPopulation = ((!isNegated && SyMUtil.isProbeServer()) || (isNegated && !SyMUtil.isProbeServer()));
            }
            else if (serverType.contains("SUMMARY")) {
                allowRowPopulation = ((!isNegated && SyMUtil.isSummaryServer()) || (isNegated && !SyMUtil.isSummaryServer()));
            }
            else if (serverType.contains("STANDALONE")) {
                allowRowPopulation = ((!isNegated && !SyMUtil.isSummaryServer() && !SyMUtil.isProbeServer()) || (isNegated && (SyMUtil.isSummaryServer() || SyMUtil.isProbeServer())));
            }
            if (!allowRowPopulation) {
                return null;
            }
        }
        final String attr_envName = attributes.getValue("DM_ENVIRONMENT");
        if (attr_envName != null) {
            final String configSAS = PersistenceInitializer.getConfigurationValue("isSAS");
            boolean isSAS = false;
            if (configSAS != null && configSAS.equals("true")) {
                isSAS = true;
            }
            if ((isSAS && attr_envName.toUpperCase().contains("CLOUD")) || (!isSAS && attr_envName.toUpperCase().contains("ONPREMISE"))) {
                return new Row(tName);
            }
            return null;
        }
        else {
            final String attr_productName = attributes.getValue("DM_PRODUCT_CODES");
            if (attr_productName == null) {
                return new Row(tName);
            }
            final String[] productCodes = attr_productName.split(",");
            int value = 0;
            final String productName = this.getProductName();
            if (DMProductRowTransformer.isEMSFlowSupported == null) {
                DMProductRowTransformer.isEMSFlowSupported = EMSProductUtil.isEMSFlowSupportedForProductCode(productName);
            }
            Boolean updateBasedOnProductCode = false;
            for (int index = 0; index < productCodes.length; ++index) {
                if (!System.getProperty("ppmFlow", "no").equals("yes") && DMProductRowTransformer.isEMSFlowSupported && EMSProductUtil.isEMSFlowSupportedForProductCode(productCodes[index])) {
                    updateBasedOnProductCode = true;
                    value |= (int)(long)EMSProductUtil.getBitwiseValueForProductCode(productCodes[index]);
                }
            }
            if (tName.equalsIgnoreCase("MsgToGlobalStatus") || tName.equalsIgnoreCase("MsgToCustomerStatus") || tName.equalsIgnoreCase("MsgGroupToPage") || tName.equalsIgnoreCase("DCQuickLink") || tName.equalsIgnoreCase("ErrorCodeToKBUrl")) {
                if (updateBasedOnProductCode) {
                    return this.updateRowsBasedOnProductCode(tName, value);
                }
                return this.populateRowsBasedOnProductCode(productName, attr_productName, tName, true);
            }
            else {
                if (System.getProperty("ppmFlow", "no").equals("yes") && tName.equalsIgnoreCase("MsgContentUrl")) {
                    return this.populateRowsBasedOnProductCode(productName, attr_productName, tName, true);
                }
                return this.populateRowsBasedOnProductCode(productName, attr_productName, tName, false);
            }
        }
    }
    
    private Row updateRowsBasedOnProductCode(final String tName, final int value) {
        final Row row = new Row(tName);
        row.set("PRODUCT_CODE", (Object)value);
        return row;
    }
    
    private Row populateRowsBasedOnProductCode(final String productName, final String attr_productName, final String tName, final boolean bool) {
        if (productName == null) {
            DMProductRowTransformer.LOGGER.log(Level.INFO, "EXCEPTION :: Product Code missing in persistence-configuration file. Please add before proceeding ");
            return new Row(tName);
        }
        final List productList = Arrays.asList(productName.split(","));
        for (final String str : productList) {
            if (bool && str.equals("VMP") && (Arrays.asList(attr_productName.split(",")).contains(str) || Arrays.asList(attr_productName.split(",")).contains("PMP"))) {
                DMProductRowTransformer.LOGGER.log(Level.INFO, "Adding PMP rows to DO for VMP product in PPM");
                return new Row(tName);
            }
            if (str.equals("ACP") && (Arrays.asList(attr_productName.split(",")).contains(str) || Arrays.asList(attr_productName.split(",")).contains("DCEE"))) {
                return new Row(tName);
            }
            if (str.equals("DCP") && (Arrays.asList(attr_productName.split(",")).contains(str) || Arrays.asList(attr_productName.split(",")).contains("PMP"))) {
                return new Row(tName);
            }
            if (Arrays.asList(attr_productName.split(",")).contains(str)) {
                return new Row(tName);
            }
        }
        return null;
    }
    
    public String getProductName() {
        return PersistenceInitializer.getConfigurationValue("DMProductCode");
    }
    
    static {
        LOGGER = Logger.getLogger(DMProductRowTransformer.class.getName());
        DMProductRowTransformer.isEMSFlowSupported = null;
    }
}
