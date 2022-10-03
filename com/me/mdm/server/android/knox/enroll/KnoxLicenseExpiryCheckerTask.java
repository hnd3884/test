package com.me.mdm.server.android.knox.enroll;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.mailmanager.MailContentGeneratorUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.util.Utils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.Date;
import com.me.mdm.server.alerts.MDMAlertConstants;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.Properties;
import com.me.mdm.server.alerts.MDMAlertMailGeneratorUtil;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Calendar;
import java.util.logging.Logger;

public class KnoxLicenseExpiryCheckerTask
{
    private static final Logger LOGGER;
    
    public void executeTask() {
        if (Calendar.getInstance().get(7) == 1) {
            try {
                final SelectQuery testLicenseQuery = (SelectQuery)new SelectQueryImpl(new Table("KnoxLicenseDetail"));
                testLicenseQuery.addSelectColumn(new Column((String)null, "*"));
                KnoxLicenseExpiryCheckerTask.LOGGER.info("Knox License Expiry weekly check Begins");
                final DataObject dO = SyMUtil.getReadOnlyPersistence().get(testLicenseQuery);
                if (!dO.isEmpty()) {
                    final Iterator iter = dO.getRows("KnoxLicenseDetail");
                    while (iter.hasNext()) {
                        final Row row = iter.next();
                        final Long customerId = (Long)row.get("CUSTOMER_ID");
                        final int expiryStatus = KnoxLicenseHandler.getInstance().shouldAlertForKnoxLicenseExpiry(customerId);
                        if (expiryStatus == 3) {
                            KnoxLicenseExpiryCheckerTask.LOGGER.log(Level.INFO, "Knox License Expiry Alert check for customer :{0}", customerId);
                            MessageProvider.getInstance().unhideMessage("KNOX_LICENSE_EXPIRED", customerId);
                            MessageProvider.getInstance().hideMessage("KNOX_LICENSE_EXPIRY", customerId);
                            final MDMAlertMailGeneratorUtil mailGenerator = new MDMAlertMailGeneratorUtil(KnoxLicenseExpiryCheckerTask.LOGGER);
                            KnoxLicenseExpiryCheckerTask.LOGGER.log(Level.INFO, "Knox License Expired for customer :{0}", customerId);
                            final int i = 0;
                            final String emailContent = this.getSummaryDetails(customerId, row);
                            final Properties prop = new Properties();
                            final String productName = ProductUrlLoader.getInstance().getValue("displayname");
                            ((Hashtable<String, String>)prop).put("$productName$", productName);
                            ((Hashtable<String, String>)prop).put("$knox_license_expired_email$", emailContent);
                            mailGenerator.sendMail(MDMAlertConstants.MDM_ANDROID_KNOX_LICENSE_EXPIRED, "MDM-Knox", customerId, prop);
                        }
                        else {
                            if (expiryStatus != 2) {
                                continue;
                            }
                            KnoxLicenseExpiryCheckerTask.LOGGER.log(Level.INFO, "Knox License Expiry Alert for customer :{0}", customerId);
                            MessageProvider.getInstance().unhideMessage("KNOX_LICENSE_EXPIRY", customerId);
                            MessageProvider.getInstance().hideMessage("KNOX_LICENSE_EXPIRED", customerId);
                            final MDMAlertMailGeneratorUtil mailGenerator = new MDMAlertMailGeneratorUtil(KnoxLicenseExpiryCheckerTask.LOGGER);
                            KnoxLicenseExpiryCheckerTask.LOGGER.log(Level.INFO, "Knox License Expiry Alert for customer :{0}", customerId);
                            final Properties prop2 = new Properties();
                            ((Hashtable<String, Integer>)prop2).put("days_to_expire", KnoxLicenseHandler.getInstance().knoxLicenseDaysToExpire(customerId));
                            ((Hashtable<String, String>)prop2).put("expiry_date", new Date((long)row.get("EXPIRY_DATE")).toString());
                            mailGenerator.sendMail(MDMAlertConstants.MDM_ANDROID_KNOX_LICENSE_ABOUT_TO_EXPIRY, "MDM-Knox", customerId, prop2);
                        }
                    }
                }
            }
            catch (final DataAccessException ex) {
                Logger.getLogger(KnoxLicenseHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            }
            KnoxLicenseExpiryCheckerTask.LOGGER.info("Knox License Expiry Daily check Ends");
        }
    }
    
    private String getSummaryDetails(final Long customerId, final Row row) {
        String expiredLicense = "a";
        try {
            if (row != null) {
                final DataObject expiredLicenseDO = MDMUtil.getPersistence().constructDataObject();
                final Row mailRow = new Row("KnoxLicenseDetail");
                mailRow.set("LICENSE_DATA", (Object)row.get("LICENSE_DATA"));
                mailRow.set("EXPIRY_DATE", (Object)Utils.getTime((Long)row.get("EXPIRY_DATE")));
                mailRow.set("MAX_COUNT", (Object)row.get("MAX_COUNT"));
                expiredLicenseDO.addRow(mailRow);
                String productURL = ProductUrlLoader.getInstance().getValue("mdmUrl");
                final String helpDocument = "/help/enrollment/knox_management_prerequisites.html#KNOX_License_Expiry_and_Renewal";
                productURL = productURL.concat(helpDocument);
                final Row docLink = new Row("SystemParams");
                docLink.set("PARAM_VALUE", (Object)productURL);
                expiredLicenseDO.addRow(docLink);
                final String xslFile = MDMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "MDM" + File.separator + "xsl" + File.separator + "ExpiredKnoxLicense.xsl";
                final String xslFile2 = MDMUtil.createI18NxslFile(xslFile, "ExpiredKnoxLicense-temp.xsl");
                final MailContentGeneratorUtil mailGenerator = new MailContentGeneratorUtil(MDMUtil.logger);
                expiredLicense = mailGenerator.getHTMLContent(xslFile2, expiredLicenseDO, "ExpiredMail");
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.SEVERE, " Exception while getting App Names", ex);
        }
        return expiredLicense;
    }
    
    private String generateFooter(String description) {
        String prodName = "";
        String hostName = ApiFactoryProvider.getUtilAccessAPI().getServerURL();
        if (!hostName.startsWith("http")) {
            hostName = "http://" + hostName + "/";
        }
        prodName = ProductUrlLoader.getInstance().getValue("displayname");
        if (prodName == null || prodName.equals("")) {
            prodName = " Desktop Central";
        }
        final Properties prop = new Properties();
        ((Hashtable<String, String>)prop).put("$baseUrl$", hostName);
        ((Hashtable<String, String>)prop).put("$productName$", prodName);
        description = ApiFactoryProvider.getMailSettingAPI().appendFooterNote(description, prop);
        return description;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
