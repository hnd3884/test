package sun.security.krb5.internal.tools;

import java.time.Instant;
import java.io.InputStream;
import sun.security.krb5.internal.ccache.CCacheInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import sun.security.krb5.KrbException;
import sun.security.krb5.Config;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.ccache.FileCredentialsCache;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.KerberosTime;

class KinitOptions
{
    public int action;
    public short forwardable;
    public short proxiable;
    public KerberosTime lifetime;
    public KerberosTime renewable_lifetime;
    public String target_service;
    public String keytab_file;
    public String cachename;
    private PrincipalName principal;
    public String realm;
    char[] password;
    public boolean keytab;
    private boolean DEBUG;
    private boolean includeAddresses;
    private boolean useKeytab;
    private String ktabName;
    
    public KinitOptions() throws RuntimeException, RealmException {
        this.action = 1;
        this.forwardable = 0;
        this.proxiable = 0;
        this.password = null;
        this.DEBUG = Krb5.DEBUG;
        this.includeAddresses = true;
        this.useKeytab = false;
        this.cachename = FileCredentialsCache.getDefaultCacheName();
        if (this.cachename == null) {
            throw new RuntimeException("default cache name error");
        }
        this.principal = this.getDefaultPrincipal();
    }
    
    public void setKDCRealm(final String realm) throws RealmException {
        this.realm = realm;
    }
    
    public String getKDCRealm() {
        if (this.realm == null && this.principal != null) {
            return this.principal.getRealmString();
        }
        return null;
    }
    
    public KinitOptions(final String[] array) throws KrbException, RuntimeException, IOException {
        this.action = 1;
        this.forwardable = 0;
        this.proxiable = 0;
        this.password = null;
        this.DEBUG = Krb5.DEBUG;
        this.includeAddresses = true;
        this.useKeytab = false;
        String s = null;
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals("-f")) {
                this.forwardable = 1;
            }
            else if (array[i].equals("-p")) {
                this.proxiable = 1;
            }
            else if (array[i].equals("-c")) {
                if (array[i + 1].startsWith("-")) {
                    throw new IllegalArgumentException("input format  not correct:  -c  option must be followed by the cache name");
                }
                this.cachename = array[++i];
                if (this.cachename.length() >= 5 && this.cachename.substring(0, 5).equalsIgnoreCase("FILE:")) {
                    this.cachename = this.cachename.substring(5);
                }
            }
            else if (array[i].equals("-A")) {
                this.includeAddresses = false;
            }
            else if (array[i].equals("-k")) {
                this.useKeytab = true;
            }
            else if (array[i].equals("-t")) {
                if (this.ktabName != null) {
                    throw new IllegalArgumentException("-t option/keytab file name repeated");
                }
                if (i + 1 >= array.length) {
                    throw new IllegalArgumentException("-t option requires keytab file name");
                }
                this.ktabName = array[++i];
                this.useKeytab = true;
            }
            else if (array[i].equals("-R")) {
                this.action = 2;
            }
            else if (array[i].equals("-l")) {
                this.lifetime = this.getTime(Config.duration(array[++i]));
            }
            else if (array[i].equals("-r")) {
                this.renewable_lifetime = this.getTime(Config.duration(array[++i]));
            }
            else if (array[i].equalsIgnoreCase("-help")) {
                this.printHelp();
                System.exit(0);
            }
            else {
                if (s == null) {
                    s = array[i];
                    try {
                        this.principal = new PrincipalName(s);
                        continue;
                    }
                    catch (final Exception ex) {
                        throw new IllegalArgumentException("invalid Principal name: " + s + ex.getMessage());
                    }
                }
                if (this.password != null) {
                    throw new IllegalArgumentException("too many parameters");
                }
                this.password = array[i].toCharArray();
            }
        }
        if (this.cachename == null) {
            this.cachename = FileCredentialsCache.getDefaultCacheName();
            if (this.cachename == null) {
                throw new RuntimeException("default cache name error");
            }
        }
        if (this.principal == null) {
            this.principal = this.getDefaultPrincipal();
        }
    }
    
    PrincipalName getDefaultPrincipal() {
        try {
            final CCacheInputStream cCacheInputStream = new CCacheInputStream(new FileInputStream(this.cachename));
            final int version;
            if ((version = cCacheInputStream.readVersion()) == 1284) {
                cCacheInputStream.readTag();
            }
            else if (version == 1281 || version == 1282) {
                cCacheInputStream.setNativeByteOrder();
            }
            final PrincipalName principal = cCacheInputStream.readPrincipal(version);
            cCacheInputStream.close();
            if (this.DEBUG) {
                System.out.println(">>>KinitOptions principal name from the cache is :" + principal);
            }
            return principal;
        }
        catch (final IOException ex) {
            if (this.DEBUG) {
                ex.printStackTrace();
            }
        }
        catch (final RealmException ex2) {
            if (this.DEBUG) {
                ex2.printStackTrace();
            }
        }
        final String property = System.getProperty("user.name");
        if (this.DEBUG) {
            System.out.println(">>>KinitOptions default username is :" + property);
        }
        try {
            return new PrincipalName(property);
        }
        catch (final RealmException ex3) {
            if (this.DEBUG) {
                System.out.println("Exception in getting principal name " + ex3.getMessage());
                ex3.printStackTrace();
            }
            return null;
        }
    }
    
    void printHelp() {
        System.out.println("Usage:\n\n1. Initial ticket request:\n    kinit [-A] [-f] [-p] [-c cachename] [-l lifetime] [-r renewable_time]\n          [[-k [-t keytab_file_name]] [principal] [password]");
        System.out.println("2. Renew a ticket:\n    kinit -R [-c cachename] [principal]");
        System.out.println("\nAvailable options to Kerberos 5 ticket request:");
        System.out.println("\t-A   do not include addresses");
        System.out.println("\t-f   forwardable");
        System.out.println("\t-p   proxiable");
        System.out.println("\t-c   cache name (i.e., FILE:\\d:\\myProfiles\\mykrb5cache)");
        System.out.println("\t-l   lifetime");
        System.out.println("\t-r   renewable time (total lifetime a ticket can be renewed)");
        System.out.println("\t-k   use keytab");
        System.out.println("\t-t   keytab file name");
        System.out.println("\tprincipal   the principal name (i.e., qweadf@ATHENA.MIT.EDU qweadf)");
        System.out.println("\tpassword    the principal's Kerberos password");
    }
    
    public boolean getAddressOption() {
        return this.includeAddresses;
    }
    
    public boolean useKeytabFile() {
        return this.useKeytab;
    }
    
    public String keytabFileName() {
        return this.ktabName;
    }
    
    public PrincipalName getPrincipal() {
        return this.principal;
    }
    
    private KerberosTime getTime(final int n) {
        return new KerberosTime(Instant.now().plusSeconds(n));
    }
}
