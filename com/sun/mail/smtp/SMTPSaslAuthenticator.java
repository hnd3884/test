package com.sun.mail.smtp;

import com.sun.mail.auth.OAuth2SaslClientFactory;
import javax.mail.MessagingException;
import javax.security.sasl.SaslClient;
import java.util.Map;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64EncoderStream;
import javax.security.sasl.SaslException;
import javax.security.sasl.Sasl;
import javax.security.sasl.RealmChoiceCallback;
import javax.security.sasl.RealmCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import java.util.logging.Level;
import com.sun.mail.util.MailLogger;
import java.util.Properties;

public class SMTPSaslAuthenticator implements SaslAuthenticator
{
    private SMTPTransport pr;
    private String name;
    private Properties props;
    private MailLogger logger;
    private String host;
    
    public SMTPSaslAuthenticator(final SMTPTransport pr, final String name, final Properties props, final MailLogger logger, final String host) {
        this.pr = pr;
        this.name = name;
        this.props = props;
        this.logger = logger;
        this.host = host;
    }
    
    @Override
    public boolean authenticate(final String[] mechs, final String realm, final String authzid, final String u, final String p) throws MessagingException {
        boolean done = false;
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("SASL Mechanisms:");
            for (int i = 0; i < mechs.length; ++i) {
                this.logger.fine(" " + mechs[i]);
            }
            this.logger.fine("");
        }
        final CallbackHandler cbh = new CallbackHandler() {
            @Override
            public void handle(final Callback[] callbacks) {
                if (SMTPSaslAuthenticator.this.logger.isLoggable(Level.FINE)) {
                    SMTPSaslAuthenticator.this.logger.fine("SASL callback length: " + callbacks.length);
                }
                for (int i = 0; i < callbacks.length; ++i) {
                    if (SMTPSaslAuthenticator.this.logger.isLoggable(Level.FINE)) {
                        SMTPSaslAuthenticator.this.logger.fine("SASL callback " + i + ": " + callbacks[i]);
                    }
                    if (callbacks[i] instanceof NameCallback) {
                        final NameCallback ncb = (NameCallback)callbacks[i];
                        ncb.setName(u);
                    }
                    else if (callbacks[i] instanceof PasswordCallback) {
                        final PasswordCallback pcb = (PasswordCallback)callbacks[i];
                        pcb.setPassword(p.toCharArray());
                    }
                    else if (callbacks[i] instanceof RealmCallback) {
                        final RealmCallback rcb = (RealmCallback)callbacks[i];
                        rcb.setText((realm != null) ? realm : rcb.getDefaultText());
                    }
                    else if (callbacks[i] instanceof RealmChoiceCallback) {
                        final RealmChoiceCallback rcb2 = (RealmChoiceCallback)callbacks[i];
                        if (realm == null) {
                            rcb2.setSelectedIndex(rcb2.getDefaultChoice());
                        }
                        else {
                            final String[] choices = rcb2.getChoices();
                            for (int k = 0; k < choices.length; ++k) {
                                if (choices[k].equals(realm)) {
                                    rcb2.setSelectedIndex(k);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
        SaslClient sc;
        try {
            final Map<String, ?> propsMap = (Map<String, ?>)this.props;
            sc = Sasl.createSaslClient(mechs, authzid, this.name, this.host, propsMap, cbh);
        }
        catch (final SaslException sex) {
            this.logger.log(Level.FINE, "Failed to create SASL client", sex);
            throw new UnsupportedOperationException(sex.getMessage(), sex);
        }
        if (sc == null) {
            this.logger.fine("No SASL support");
            throw new UnsupportedOperationException("No SASL support");
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("SASL client " + sc.getMechanismName());
        }
        int resp;
        try {
            final String mech = sc.getMechanismName();
            String ir = null;
            if (sc.hasInitialResponse()) {
                byte[] ba = sc.evaluateChallenge(new byte[0]);
                if (ba.length > 0) {
                    ba = BASE64EncoderStream.encode(ba);
                    ir = ASCIIUtility.toString(ba, 0, ba.length);
                }
                else {
                    ir = "=";
                }
            }
            if (ir != null) {
                resp = this.pr.simpleCommand("AUTH " + mech + " " + ir);
            }
            else {
                resp = this.pr.simpleCommand("AUTH " + mech);
            }
            if (resp == 530) {
                this.pr.startTLS();
                if (ir != null) {
                    resp = this.pr.simpleCommand("AUTH " + mech + " " + ir);
                }
                else {
                    resp = this.pr.simpleCommand("AUTH " + mech);
                }
            }
            if (resp == 235) {
                return true;
            }
            if (resp != 334) {
                return false;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.FINE, "SASL AUTHENTICATE Exception", ex);
            return false;
        }
        while (!done) {
            try {
                if (resp == 334) {
                    byte[] ba2 = null;
                    if (!sc.isComplete()) {
                        ba2 = ASCIIUtility.getBytes(responseText(this.pr));
                        if (ba2.length > 0) {
                            ba2 = BASE64DecoderStream.decode(ba2);
                        }
                        if (this.logger.isLoggable(Level.FINE)) {
                            this.logger.fine("SASL challenge: " + ASCIIUtility.toString(ba2, 0, ba2.length) + " :");
                        }
                        ba2 = sc.evaluateChallenge(ba2);
                    }
                    if (ba2 == null) {
                        this.logger.fine("SASL: no response");
                        resp = this.pr.simpleCommand("");
                    }
                    else {
                        if (this.logger.isLoggable(Level.FINE)) {
                            this.logger.fine("SASL response: " + ASCIIUtility.toString(ba2, 0, ba2.length) + " :");
                        }
                        ba2 = BASE64EncoderStream.encode(ba2);
                        resp = this.pr.simpleCommand(ba2);
                    }
                }
                else {
                    done = true;
                }
            }
            catch (final Exception ioex) {
                this.logger.log(Level.FINE, "SASL Exception", ioex);
                done = true;
            }
        }
        if (resp != 235) {
            return false;
        }
        if (sc.isComplete()) {
            final String qop = (String)sc.getNegotiatedProperty("javax.security.sasl.qop");
            if (qop != null && (qop.equalsIgnoreCase("auth-int") || qop.equalsIgnoreCase("auth-conf"))) {
                this.logger.fine("SASL Mechanism requires integrity or confidentiality");
                return false;
            }
        }
        return true;
    }
    
    private static final String responseText(final SMTPTransport pr) {
        final String resp = pr.getLastServerResponse().trim();
        if (resp.length() > 4) {
            return resp.substring(4);
        }
        return "";
    }
    
    static {
        try {
            OAuth2SaslClientFactory.init();
        }
        catch (final Throwable t) {}
    }
}
