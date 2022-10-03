package com.me.ems.onpremise.security.securegatewayserver.core;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.ems.onpremise.security.securegatewayserver.api.v1.model.EmailInformation;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Logger;

public class SecureGatewayServerEmailNotificationUtils
{
    private static final Logger LOGGER;
    
    public static synchronized Boolean isEmailNotificationEnabled() throws DataAccessException {
        return getSGSEmailNotficationInformation().getEnabled();
    }
    
    public static synchronized void enableEmailNotification(final Boolean emailStatus) throws DataAccessException {
        final DataObject mailDObj = getSecureGatewayServerEmailAddDO();
        if (mailDObj.isEmpty()) {
            SecureGatewayServerEmailNotificationUtils.LOGGER.info("There is no Email address in the DB so the EMAIL NOTIFICATION cannot be enabled");
            return;
        }
        final Row row = mailDObj.getRow("EMailAddr");
        row.set("SEND_MAIL", (Object)emailStatus);
        mailDObj.updateRow(row);
        SyMUtil.getPersistence().update(mailDObj);
    }
    
    public static synchronized void addOrUpdateEmailAddr(final Boolean enableEmailNotification, final String emailAddresses) throws DataAccessException {
        final DataObject mailDObj = getSecureGatewayServerEmailAddDO();
        if (!mailDObj.isEmpty()) {
            final Row row = mailDObj.getRow("EMailAddr");
            row.set("SEND_MAIL", (Object)enableEmailNotification);
            row.set("EMAIL_ADDR", (Object)emailAddresses);
            mailDObj.updateRow(row);
        }
        else {
            final Row row = new Row("EMailAddr");
            row.set("SEND_MAIL", (Object)enableEmailNotification);
            row.set("MODULE", (Object)"FwServer");
            row.set("EMAIL_ADDR", (Object)emailAddresses);
            mailDObj.addRow(row);
        }
        SyMUtil.getPersistence().update(mailDObj);
    }
    
    public static Boolean isValidCommaSeparatedEmailAddresses(final String emailAddresses) {
        SecureGatewayServerEmailNotificationUtils.LOGGER.info("Email address to validate " + emailAddresses);
        Boolean isValidCommaSeparatedEmailAddresses = Boolean.TRUE;
        final Pattern pattern = Pattern.compile("^([\\w-$+'!#%&*\u00e2\u0080\u0093=?^_.{|}~]+(?:\\.[\\w-$+]+)*)@((?:[\\w-]+\\.)*\\w[\\w-]{0,66})\\.([a-z]{2,}(?:\\.[a-z]{2})?)$", 2);
        for (final String emailAddress : emailAddresses.split(",")) {
            final Matcher matcher = pattern.matcher(emailAddress);
            if (!matcher.matches()) {
                isValidCommaSeparatedEmailAddresses = Boolean.FALSE;
                SecureGatewayServerEmailNotificationUtils.LOGGER.info("Email address found to be invalid " + emailAddress);
            }
        }
        return isValidCommaSeparatedEmailAddresses;
    }
    
    public static DataObject getEmailAddDO(final String module) throws Exception {
        final Column col = Column.getColumn("EMailAddr", "MODULE");
        final Criteria crit = new Criteria(col, (Object)module, 0, false);
        final DataObject mailDObj = SyMUtil.getPersistence().get("EMailAddr", crit);
        return mailDObj;
    }
    
    public static DataObject getSecureGatewayServerEmailAddDO() throws DataAccessException {
        final Column col = Column.getColumn("EMailAddr", "MODULE");
        final Criteria crit = new Criteria(col, (Object)"FwServer", 0, false);
        final DataObject mailDObj = SyMUtil.getPersistence().get("EMailAddr", crit);
        return mailDObj;
    }
    
    public static EmailInformation getSGSEmailNotficationInformation() throws DataAccessException {
        final EmailInformation emailInformation = new EmailInformation();
        Boolean enabled = Boolean.FALSE;
        String EmailAddr = "";
        final DataObject mailDObj = getSecureGatewayServerEmailAddDO();
        final Row emailInfo = mailDObj.getRow("EMailAddr");
        if (emailInfo != null) {
            enabled = (Boolean)emailInfo.get("SEND_MAIL");
            EmailAddr = (String)emailInfo.get("EMAIL_ADDR");
        }
        emailInformation.setIsMailServerConfigured(ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured());
        emailInformation.setEnabled(enabled);
        emailInformation.setEmailAddress(EmailAddr);
        return emailInformation;
    }
    
    static {
        LOGGER = Logger.getLogger("SecurityLogger");
    }
}
