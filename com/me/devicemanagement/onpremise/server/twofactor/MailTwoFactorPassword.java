package com.me.devicemanagement.onpremise.server.twofactor;

import java.util.Iterator;
import java.util.Hashtable;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import com.me.devicemanagement.framework.server.util.Utils;
import java.util.Calendar;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.TimerTask;
import java.util.Timer;
import java.security.SecureRandom;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import java.util.HashMap;

public class MailTwoFactorPassword
{
    public static HashMap passwordMap;
    private static Logger logger;
    
    public boolean handleAuth(final Long userId, final HttpServletRequest request) throws Exception {
        MailTwoFactorPassword.logger.log(Level.INFO, "In handle method of MailTwoFactorPassword");
        final String userEmail = this.sendMailPassword(userId);
        request.getSession().setAttribute("userEmailForMailTFA", (Object)userEmail);
        return true;
    }
    
    public boolean validate(final Long userId, final HttpServletRequest request) throws Exception {
        final String password = request.getParameter("2factor_password");
        if (password == null) {
            MailTwoFactorPassword.logger.log(Level.INFO, "In validate MailTwoFactorPassword... Password is null");
            return false;
        }
        if (MailTwoFactorPassword.passwordMap.get(userId) == null) {
            MailTwoFactorPassword.logger.log(Level.INFO, "In validate MailTwoFactorPassword...UserID not found in PasswordMap");
            return false;
        }
        final String comp = MailTwoFactorPassword.passwordMap.get(userId);
        if (comp.equals(password)) {
            MailTwoFactorPassword.logger.log(Level.INFO, "In validate MailTwoFactorPassword... Second Factor verification completed successfully");
            return true;
        }
        MailTwoFactorPassword.logger.log(Level.INFO, "In validate MailTwoFactorPassword...Account is locked");
        return false;
    }
    
    public String generateRandomPassword() {
        final SecureRandom gen = new SecureRandom();
        final int in = 100000 + gen.nextInt(900000);
        return in + "";
    }
    
    void actionAfterTimeout(final Long timeout) {
        MailTwoFactorPassword.passwordMap.remove(timeout);
    }
    
    public String sendMailPassword(final Long userId) throws Exception {
        String userEmail = null;
        try {
            final String pass = this.generateRandomPassword();
            String userName = "";
            final Timer timer = new Timer();
            final long timeout = 900000L;
            final Long passwordTimeout = userId;
            MailTwoFactorPassword.passwordMap.put(userId, pass);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    MailTwoFactorPassword.this.actionAfterTimeout(passwordTimeout);
                }
            }, timeout);
            try {
                final Criteria twoFactorParamsCri = new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)userId, 0);
                final DataObject twoFactorParamDO = SyMUtil.getPersistence().get("AaaUser", twoFactorParamsCri);
                if (!twoFactorParamDO.isEmpty()) {
                    final Row twoFactorParamRow = twoFactorParamDO.getFirstRow("AaaUser");
                    final String TwoFactorUser = userName = (String)twoFactorParamRow.get("FIRST_NAME");
                }
            }
            catch (final Exception e) {
                MailTwoFactorPassword.logger.log(Level.SEVERE, "Exception in fetching UserMgmtParams data: " + e);
            }
            final Hashtable<String, String> mailSenderDetails = ApiFactoryProvider.getMailSettingAPI().getMailSenderDetails();
            final String fromName = mailSenderDetails.get("mail.fromName");
            final String fromAddress = mailSenderDetails.get("mail.fromAddress");
            String strToAddress = null;
            final String productName = ProductUrlLoader.getInstance().getValue("displayname");
            final String subject = I18N.getMsg("dc.tfa.mail_subject", new Object[] { productName });
            final DataObject dataObject = DMUserHandler.getContactInfoDO(userId);
            if (!dataObject.isEmpty()) {
                final Iterator aaaContactInfoIter = dataObject.getRows("AaaContactInfo");
                final Row aaaContactInfoRow = aaaContactInfoIter.next();
                strToAddress = (String)aaaContactInfoRow.get("EMAILID");
            }
            final String[] filesToAttach = null;
            final Calendar calender = Calendar.getInstance();
            calender.setTimeInMillis(System.currentTimeMillis());
            calender.add(12, 15);
            final Long lasttime = calender.getTimeInMillis();
            final String ExpireTime = Utils.longdateToString((long)lasttime, "MMM d, yyyy hh:mm aaa");
            final String strMessage = I18N.getMsg("dc.tfa.mail_content", new Object[] { userName, pass, ExpireTime });
            final MailDetails mailDetails = new MailDetails(fromAddress, strToAddress);
            mailDetails.bodyContent = strMessage;
            mailDetails.senderDisplayName = fromName;
            mailDetails.subject = subject;
            mailDetails.attachment = filesToAttach;
            ApiFactoryProvider.getMailSettingAPI().addToMailQueue(mailDetails, 0);
            int strIter = 2;
            userEmail = strToAddress;
            try {
                while (strIter < userEmail.length() && userEmail.charAt(strIter + 1) != '@') {
                    userEmail = userEmail.substring(0, strIter) + '*' + userEmail.substring(strIter + 1, userEmail.length());
                    ++strIter;
                }
            }
            catch (final Exception e2) {
                userEmail = strToAddress;
            }
            MailTwoFactorPassword.logger.log(Level.INFO, "Two Factor Password has been sent to the user successfully ");
        }
        catch (final Exception ex) {
            MailTwoFactorPassword.logger.log(Level.SEVERE, "Caught Exception in sending TwoFactorPassword " + ex);
        }
        return userEmail;
    }
    
    static {
        MailTwoFactorPassword.passwordMap = new HashMap();
        MailTwoFactorPassword.logger = Logger.getLogger("UserManagementLogger");
    }
}
