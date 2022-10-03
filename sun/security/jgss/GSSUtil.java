package sun.security.jgss;

import sun.security.action.GetBooleanAction;
import java.security.PrivilegedActionException;
import java.security.AccessControlContext;
import java.security.PrivilegedExceptionAction;
import java.util.Vector;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import javax.security.auth.login.LoginException;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import com.sun.security.auth.callback.TextCallbackHandler;
import java.security.Security;
import sun.net.www.protocol.http.spnego.NegotiateCallbackHandler;
import java.util.Iterator;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosTicket;
import sun.security.jgss.spnego.SpNegoCredElement;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import java.security.Principal;
import java.util.Set;
import javax.security.auth.kerberos.KerberosPrincipal;
import sun.security.jgss.krb5.Krb5NameElement;
import java.util.HashSet;
import javax.security.auth.Subject;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

public class GSSUtil
{
    public static final Oid GSS_KRB5_MECH_OID;
    public static final Oid GSS_KRB5_MECH_OID2;
    public static final Oid GSS_KRB5_MECH_OID_MS;
    public static final Oid GSS_SPNEGO_MECH_OID;
    public static final Oid NT_GSS_KRB5_PRINCIPAL;
    private static final String DEFAULT_HANDLER = "auth.login.defaultCallbackHandler";
    static final boolean DEBUG;
    
    static void debug(final String s) {
        if (GSSUtil.DEBUG) {
            assert s != null;
            System.out.println(s);
        }
    }
    
    public static Oid createOid(final String s) {
        try {
            return new Oid(s);
        }
        catch (final GSSException ex) {
            debug("Ignored invalid OID: " + s);
            return null;
        }
    }
    
    public static boolean isSpNegoMech(final Oid oid) {
        return GSSUtil.GSS_SPNEGO_MECH_OID.equals(oid);
    }
    
    public static boolean isKerberosMech(final Oid oid) {
        return GSSUtil.GSS_KRB5_MECH_OID.equals(oid) || GSSUtil.GSS_KRB5_MECH_OID2.equals(oid) || GSSUtil.GSS_KRB5_MECH_OID_MS.equals(oid);
    }
    
    public static String getMechStr(final Oid oid) {
        if (isSpNegoMech(oid)) {
            return "SPNEGO";
        }
        if (isKerberosMech(oid)) {
            return "Kerberos V5";
        }
        return oid.toString();
    }
    
    public static Subject getSubject(final GSSName gssName, final GSSCredential gssCredential) {
        final HashSet set = new HashSet();
        final HashSet set2 = new HashSet();
        if (gssName instanceof GSSNameImpl) {
            try {
                final GSSNameSpi element = ((GSSNameImpl)gssName).getElement(GSSUtil.GSS_KRB5_MECH_OID);
                String s = element.toString();
                if (element instanceof Krb5NameElement) {
                    s = ((Krb5NameElement)element).getKrb5PrincipalName().getName();
                }
                set2.add(new KerberosPrincipal(s));
            }
            catch (final GSSException ex) {
                debug("Skipped name " + gssName + " due to " + ex);
            }
        }
        HashSet<Object> set3;
        if (gssCredential instanceof GSSCredentialImpl) {
            final Set<GSSCredentialSpi> elements = ((GSSCredentialImpl)gssCredential).getElements();
            set3 = new HashSet<Object>(elements.size());
            populateCredentials(set3, elements);
        }
        else {
            set3 = new HashSet<Object>();
        }
        debug("Created Subject with the following");
        debug("principals=" + set2);
        debug("public creds=" + set);
        debug("private creds=" + set3);
        return new Subject(false, set2, set, set3);
    }
    
    private static void populateCredentials(final Set<Object> set, final Set<?> set2) {
        for (Object o : set2) {
            if (o instanceof SpNegoCredElement) {
                o = ((SpNegoCredElement)o).getInternalCred();
            }
            if (o instanceof KerberosTicket) {
                if (!((SpNegoCredElement)o).getClass().getName().equals("javax.security.auth.kerberos.KerberosTicket")) {
                    final KerberosTicket kerberosTicket = (KerberosTicket)o;
                    o = new KerberosTicket(kerberosTicket.getEncoded(), kerberosTicket.getClient(), kerberosTicket.getServer(), kerberosTicket.getSessionKey().getEncoded(), kerberosTicket.getSessionKeyType(), kerberosTicket.getFlags(), kerberosTicket.getAuthTime(), kerberosTicket.getStartTime(), kerberosTicket.getEndTime(), kerberosTicket.getRenewTill(), kerberosTicket.getClientAddresses());
                }
                set.add(o);
            }
            else if (o instanceof KerberosKey) {
                if (!((KerberosTicket)o).getClass().getName().equals("javax.security.auth.kerberos.KerberosKey")) {
                    final KerberosKey kerberosKey = (KerberosKey)o;
                    o = new KerberosKey(kerberosKey.getPrincipal(), kerberosKey.getEncoded(), kerberosKey.getKeyType(), kerberosKey.getVersionNumber());
                }
                set.add(o);
            }
            else {
                debug("Skipped cred element: " + o);
            }
        }
    }
    
    public static Subject login(final GSSCaller gssCaller, final Oid oid) throws LoginException {
        CallbackHandler callbackHandler;
        if (gssCaller instanceof HttpCaller) {
            callbackHandler = new NegotiateCallbackHandler(((HttpCaller)gssCaller).info());
        }
        else {
            final String property = Security.getProperty("auth.login.defaultCallbackHandler");
            if (property != null && property.length() != 0) {
                callbackHandler = null;
            }
            else {
                callbackHandler = new TextCallbackHandler();
            }
        }
        final LoginContext loginContext = new LoginContext("", null, callbackHandler, new LoginConfigImpl(gssCaller, oid));
        loginContext.login();
        return loginContext.getSubject();
    }
    
    public static boolean useSubjectCredsOnly(final GSSCaller gssCaller) {
        final String privilegedGetProperty = GetPropertyAction.privilegedGetProperty("javax.security.auth.useSubjectCredsOnly");
        if (gssCaller instanceof HttpCaller) {
            return "true".equalsIgnoreCase(privilegedGetProperty);
        }
        return !"false".equalsIgnoreCase(privilegedGetProperty);
    }
    
    public static boolean useMSInterop() {
        return !AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.security.spnego.msinterop", "true")).equalsIgnoreCase("false");
    }
    
    public static <T extends GSSCredentialSpi> Vector<T> searchSubject(final GSSNameSpi gssNameSpi, final Oid oid, final boolean b, final Class<? extends T> clazz) {
        debug("Search Subject for " + getMechStr(oid) + (b ? " INIT" : " ACCEPT") + " cred (" + ((gssNameSpi == null) ? "<<DEF>>" : gssNameSpi.toString()) + ", " + clazz.getName() + ")");
        final AccessControlContext context = AccessController.getContext();
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Vector<T>>)new PrivilegedExceptionAction<Vector<T>>() {
                @Override
                public Vector<T> run() throws Exception {
                    final Subject subject = Subject.getSubject(context);
                    Vector<Object> vector = null;
                    if (subject != null) {
                        vector = (Vector<Object>)new Vector<T>();
                        for (final GSSCredentialImpl gssCredentialImpl : subject.getPrivateCredentials(GSSCredentialImpl.class)) {
                            GSSUtil.debug("...Found cred" + gssCredentialImpl);
                            try {
                                final GSSCredentialSpi element = gssCredentialImpl.getElement(oid, b);
                                GSSUtil.debug("......Found element: " + element);
                                if (element.getClass().equals(clazz) && (gssNameSpi == null || gssNameSpi.equals((Object)element.getName()))) {
                                    vector.add(clazz.cast(element));
                                }
                                else {
                                    GSSUtil.debug("......Discard element");
                                }
                            }
                            catch (final GSSException ex) {
                                GSSUtil.debug("...Discard cred (" + ex + ")");
                            }
                        }
                    }
                    else {
                        GSSUtil.debug("No Subject");
                    }
                    return (Vector<T>)vector;
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            debug("Unexpected exception when searching Subject:");
            if (GSSUtil.DEBUG) {
                ex.printStackTrace();
            }
            return null;
        }
    }
    
    static {
        GSS_KRB5_MECH_OID = createOid("1.2.840.113554.1.2.2");
        GSS_KRB5_MECH_OID2 = createOid("1.3.5.1.5.2");
        GSS_KRB5_MECH_OID_MS = createOid("1.2.840.48018.1.2.2");
        GSS_SPNEGO_MECH_OID = createOid("1.3.6.1.5.5.2");
        NT_GSS_KRB5_PRINCIPAL = createOid("1.2.840.113554.1.2.2.1");
        DEBUG = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.security.jgss.debug"));
    }
}
