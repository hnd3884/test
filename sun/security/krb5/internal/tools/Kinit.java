package sun.security.krb5.internal.tools;

import sun.security.krb5.internal.Krb5;
import java.util.Arrays;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.Config;
import sun.security.krb5.internal.KDCOptions;
import java.io.File;
import javax.security.auth.kerberos.KeyTab;
import sun.security.krb5.KrbAsReqBuilder;
import sun.security.util.Password;
import sun.security.krb5.internal.ccache.Credentials;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.ccache.CredentialsCache;
import sun.security.krb5.RealmException;
import java.io.IOException;
import sun.security.krb5.KrbException;

public class Kinit
{
    private KinitOptions options;
    private static final boolean DEBUG;
    
    public static void main(final String[] array) {
        try {
            final Kinit kinit = new Kinit(array);
        }
        catch (final Exception ex) {
            String s;
            if (ex instanceof KrbException) {
                s = ((KrbException)ex).krbErrorMessage() + " " + ((KrbException)ex).returnCodeMessage();
            }
            else {
                s = ex.getMessage();
            }
            if (s != null) {
                System.err.println("Exception: " + s);
            }
            else {
                System.out.println("Exception: " + ex);
            }
            ex.printStackTrace();
            System.exit(-1);
        }
    }
    
    private Kinit(final String[] array) throws IOException, RealmException, KrbException {
        if (array == null || array.length == 0) {
            this.options = new KinitOptions();
        }
        else {
            this.options = new KinitOptions(array);
        }
        switch (this.options.action) {
            case 1: {
                this.acquire();
                break;
            }
            case 2: {
                this.renew();
                break;
            }
            default: {
                throw new KrbException("kinit does not support action " + this.options.action);
            }
        }
    }
    
    private void renew() throws IOException, RealmException, KrbException {
        final PrincipalName principal = this.options.getPrincipal();
        final String realmAsString = principal.getRealmAsString();
        final CredentialsCache instance = CredentialsCache.getInstance(this.options.cachename);
        if (instance == null) {
            throw new IOException("Unable to find existing cache file " + this.options.cachename);
        }
        final Credentials cCacheCreds = instance.getCreds(PrincipalName.tgsService(realmAsString, realmAsString)).setKrbCreds().renew().toCCacheCreds();
        final CredentialsCache create = CredentialsCache.create(principal, this.options.cachename);
        if (create == null) {
            throw new IOException("Unable to create the cache file " + this.options.cachename);
        }
        create.update(cCacheCreds);
        create.save();
    }
    
    private void acquire() throws IOException, RealmException, KrbException {
        String string = null;
        final PrincipalName principal = this.options.getPrincipal();
        if (principal != null) {
            string = principal.toString();
        }
        if (Kinit.DEBUG) {
            System.out.println("Principal is " + principal);
        }
        char[] array = this.options.password;
        KrbAsReqBuilder krbAsReqBuilder;
        if (!this.options.useKeytabFile()) {
            if (string == null) {
                throw new IllegalArgumentException(" Can not obtain principal name");
            }
            if (array == null) {
                System.out.print("Password for " + string + ":");
                System.out.flush();
                array = Password.readPassword(System.in);
                if (Kinit.DEBUG) {
                    System.out.println(">>> Kinit console input " + new String(array));
                }
            }
            krbAsReqBuilder = new KrbAsReqBuilder(principal, array);
        }
        else {
            if (Kinit.DEBUG) {
                System.out.println(">>> Kinit using keytab");
            }
            if (string == null) {
                throw new IllegalArgumentException("Principal name must be specified.");
            }
            final String keytabFileName = this.options.keytabFileName();
            if (keytabFileName != null && Kinit.DEBUG) {
                System.out.println(">>> Kinit keytab file name: " + keytabFileName);
            }
            krbAsReqBuilder = new KrbAsReqBuilder(principal, (keytabFileName == null) ? KeyTab.getInstance() : KeyTab.getInstance(new File(keytabFileName)));
        }
        final KDCOptions options = new KDCOptions();
        setOptions(1, this.options.forwardable, options);
        setOptions(3, this.options.proxiable, options);
        krbAsReqBuilder.setOptions(options);
        String s = this.options.getKDCRealm();
        if (s == null) {
            s = Config.getInstance().getDefaultRealm();
        }
        if (Kinit.DEBUG) {
            System.out.println(">>> Kinit realm name is " + s);
        }
        krbAsReqBuilder.setTarget(PrincipalName.tgsService(s, s));
        if (Kinit.DEBUG) {
            System.out.println(">>> Creating KrbAsReq");
        }
        if (this.options.getAddressOption()) {
            krbAsReqBuilder.setAddresses(HostAddresses.getLocalAddresses());
        }
        krbAsReqBuilder.setTill(this.options.lifetime);
        krbAsReqBuilder.setRTime(this.options.renewable_lifetime);
        krbAsReqBuilder.action();
        final Credentials cCreds = krbAsReqBuilder.getCCreds();
        krbAsReqBuilder.destroy();
        final CredentialsCache create = CredentialsCache.create(principal, this.options.cachename);
        if (create == null) {
            throw new IOException("Unable to create the cache file " + this.options.cachename);
        }
        create.update(cCreds);
        create.save();
        if (this.options.password == null) {
            System.out.println("New ticket is stored in cache file " + this.options.cachename);
        }
        else {
            Arrays.fill(this.options.password, '0');
        }
        if (array != null) {
            Arrays.fill(array, '0');
        }
        this.options = null;
    }
    
    private static void setOptions(final int n, final int n2, final KDCOptions kdcOptions) {
        switch (n2) {
            case -1: {
                kdcOptions.set(n, false);
                break;
            }
            case 1: {
                kdcOptions.set(n, true);
                break;
            }
        }
    }
    
    static {
        DEBUG = Krb5.DEBUG;
    }
}
