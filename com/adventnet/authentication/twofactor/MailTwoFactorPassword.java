package com.adventnet.authentication.twofactor;

import java.util.Hashtable;
import java.security.SecureRandom;
import javax.mail.MessagingException;
import java.util.logging.Level;
import javax.mail.Transport;
import java.util.Date;
import javax.mail.Message;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.Session;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.authentication.util.AuthUtil;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.logging.Logger;
import java.util.Properties;
import java.util.HashMap;

public class MailTwoFactorPassword extends TwoFactorAuthImpl
{
    public static HashMap passwordMap;
    private static Properties smtpProps;
    private static Logger logger;
    
    @Override
    public boolean handle(final Long userId, final ServletRequest request, final ServletResponse response) throws Exception {
        final String pass = this.generateRandomPassword(userId);
        MailTwoFactorPassword.passwordMap.put(userId, pass);
        this.sendEmail(AuthUtil.getEmailId(userId), "Your Second Password: " + pass);
        return true;
    }
    
    @Override
    public boolean validate(final Long userId, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String password = request.getParameter("2factor_password");
        if (password == null) {
            return false;
        }
        if (MailTwoFactorPassword.passwordMap.get(userId) != null) {
            final String comp = MailTwoFactorPassword.passwordMap.get(userId);
            return comp.equals(password);
        }
        return false;
    }
    
    private void sendEmail(final String emailId, final String message) {
        try {
            final Message msg = (Message)new MimeMessage(Session.getInstance(MailTwoFactorPassword.smtpProps, (Authenticator)null));
            msg.setFrom((Address)new InternetAddress("noreply@mickey.com"));
            msg.setRecipient(Message.RecipientType.TO, (Address)new InternetAddress(emailId));
            final Address[] replyTo = { (Address)new InternetAddress("noreply@mickey.com") };
            msg.setReplyTo(replyTo);
            msg.setSubject("Your Second Password");
            msg.setSentDate(new Date());
            msg.setContent((Object)message, "text/plain; charset=UTF-8");
            Transport.send(msg);
            MailTwoFactorPassword.logger.log(Level.INFO, "Mail successfully send to " + emailId);
        }
        catch (final MessagingException mex) {
            mex.printStackTrace();
            MailTwoFactorPassword.logger.log(Level.SEVERE, "mail sending to " + emailId + " failed");
        }
    }
    
    private String generateRandomPassword(final Long userId) {
        final SecureRandom gen = new SecureRandom();
        int in = gen.nextInt();
        if (in < 0) {
            in *= -1;
        }
        return in + "";
    }
    
    static {
        MailTwoFactorPassword.passwordMap = new HashMap();
        ((Hashtable<String, String>)(MailTwoFactorPassword.smtpProps = new Properties())).put("mail.smtp.host", "smtp.india.adventnet.com");
        MailTwoFactorPassword.logger = Logger.getLogger(MailTwoFactorPassword.class.getName());
    }
}
