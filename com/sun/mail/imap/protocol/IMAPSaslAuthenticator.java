package com.sun.mail.imap.protocol;

import com.sun.mail.auth.OAuth2SaslClientFactory;
import com.sun.mail.iap.ProtocolException;
import java.io.OutputStream;
import javax.security.sasl.SaslClient;
import java.util.Map;
import java.util.List;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.PropUtil;
import java.io.ByteArrayOutputStream;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64EncoderStream;
import com.sun.mail.iap.Argument;
import javax.security.sasl.SaslException;
import javax.security.sasl.Sasl;
import javax.security.sasl.RealmChoiceCallback;
import javax.security.sasl.RealmCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import java.util.logging.Level;
import com.sun.mail.iap.Response;
import java.util.ArrayList;
import com.sun.mail.util.MailLogger;
import java.util.Properties;

public class IMAPSaslAuthenticator implements SaslAuthenticator
{
    private IMAPProtocol pr;
    private String name;
    private Properties props;
    private MailLogger logger;
    private String host;
    
    public IMAPSaslAuthenticator(final IMAPProtocol pr, final String name, final Properties props, final MailLogger logger, final String host) {
        this.pr = pr;
        this.name = name;
        this.props = props;
        this.logger = logger;
        this.host = host;
    }
    
    @Override
    public boolean authenticate(final String[] mechs, final String realm, final String authzid, final String u, final String p) throws ProtocolException {
        synchronized (this.pr) {
            final List<Response> v = new ArrayList<Response>();
            String tag = null;
            Response r = null;
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
                    if (IMAPSaslAuthenticator.this.logger.isLoggable(Level.FINE)) {
                        IMAPSaslAuthenticator.this.logger.fine("SASL callback length: " + callbacks.length);
                    }
                    for (int i = 0; i < callbacks.length; ++i) {
                        if (IMAPSaslAuthenticator.this.logger.isLoggable(Level.FINE)) {
                            IMAPSaslAuthenticator.this.logger.fine("SASL callback " + i + ": " + callbacks[i]);
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
            try {
                final Argument args = new Argument();
                args.writeAtom(sc.getMechanismName());
                if (this.pr.hasCapability("SASL-IR") && sc.hasInitialResponse()) {
                    byte[] ba = sc.evaluateChallenge(new byte[0]);
                    String irs;
                    if (ba.length > 0) {
                        ba = BASE64EncoderStream.encode(ba);
                        irs = ASCIIUtility.toString(ba, 0, ba.length);
                    }
                    else {
                        irs = "=";
                    }
                    args.writeAtom(irs);
                }
                tag = this.pr.writeCommand("AUTHENTICATE", args);
            }
            catch (final Exception ex) {
                this.logger.log(Level.FINE, "SASL AUTHENTICATE Exception", ex);
                return false;
            }
            final OutputStream os = this.pr.getIMAPOutputStream();
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final byte[] CRLF = { 13, 10 };
            final boolean isXGWTRUSTEDAPP = sc.getMechanismName().equals("XGWTRUSTEDAPP") && PropUtil.getBooleanProperty(this.props, "mail." + this.name + ".sasl.xgwtrustedapphack.enable", true);
            while (!done) {
                try {
                    r = this.pr.readResponse();
                    if (r.isContinuation()) {
                        byte[] ba2 = null;
                        if (!sc.isComplete()) {
                            ba2 = r.readByteArray().getNewBytes();
                            if (ba2.length > 0) {
                                ba2 = BASE64DecoderStream.decode(ba2);
                            }
                            if (this.logger.isLoggable(Level.FINE)) {
                                this.logger.fine("SASL challenge: " + ASCIIUtility.toString(ba2, 0, ba2.length) + " :");
                            }
                            ba2 = sc.evaluateChallenge(ba2);
                        }
                        if (ba2 == null) {
                            this.logger.fine("SASL no response");
                            os.write(CRLF);
                            os.flush();
                            bos.reset();
                        }
                        else {
                            if (this.logger.isLoggable(Level.FINE)) {
                                this.logger.fine("SASL response: " + ASCIIUtility.toString(ba2, 0, ba2.length) + " :");
                            }
                            ba2 = BASE64EncoderStream.encode(ba2);
                            if (isXGWTRUSTEDAPP) {
                                bos.write(ASCIIUtility.getBytes("XGWTRUSTEDAPP "));
                            }
                            bos.write(ba2);
                            bos.write(CRLF);
                            os.write(bos.toByteArray());
                            os.flush();
                            bos.reset();
                        }
                    }
                    else if (r.isTagged() && r.getTag().equals(tag)) {
                        done = true;
                    }
                    else if (r.isBYE()) {
                        done = true;
                    }
                    else {
                        v.add(r);
                    }
                }
                catch (final Exception ioex) {
                    this.logger.log(Level.FINE, "SASL Exception", ioex);
                    r = Response.byeResponse(ioex);
                    done = true;
                }
            }
            if (sc.isComplete()) {
                final String qop = (String)sc.getNegotiatedProperty("javax.security.sasl.qop");
                if (qop != null && (qop.equalsIgnoreCase("auth-int") || qop.equalsIgnoreCase("auth-conf"))) {
                    this.logger.fine("SASL Mechanism requires integrity or confidentiality");
                    return false;
                }
            }
            Response[] responses = v.toArray(new Response[v.size()]);
            this.pr.handleCapabilityResponse(responses);
            this.pr.notifyResponseHandlers(responses);
            this.pr.handleLoginResult(r);
            this.pr.setCapabilities(r);
            if (isXGWTRUSTEDAPP && authzid != null) {
                final Argument args2 = new Argument();
                args2.writeString(authzid);
                responses = this.pr.command("LOGIN", args2);
                this.pr.notifyResponseHandlers(responses);
                this.pr.handleResult(responses[responses.length - 1]);
                this.pr.setCapabilities(responses[responses.length - 1]);
            }
            return true;
        }
    }
    
    static {
        try {
            OAuth2SaslClientFactory.init();
        }
        catch (final Throwable t) {}
    }
}
