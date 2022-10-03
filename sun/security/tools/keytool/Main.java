package sun.security.tools.keytool;

import java.util.EnumSet;
import sun.security.x509.SerialNumber;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.SubjectKeyIdentifierExtension;
import sun.security.x509.KeyIdentifier;
import sun.security.util.DerValue;
import sun.security.x509.AuthorityInfoAccessExtension;
import sun.security.x509.SubjectInfoAccessExtension;
import sun.security.x509.AccessDescription;
import sun.security.x509.IssuerAlternativeNameExtension;
import sun.security.x509.SubjectAlternativeNameExtension;
import sun.security.x509.ExtendedKeyUsageExtension;
import sun.security.x509.KeyUsageExtension;
import sun.security.x509.BasicConstraintsExtension;
import sun.security.x509.PKIXExtensions;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.OIDName;
import sun.security.x509.IPAddressName;
import sun.security.x509.DNSName;
import sun.security.x509.RFC822Name;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.security.Principal;
import java.util.Hashtable;
import java.security.MessageDigest;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import sun.misc.HexDumpEncoder;
import sun.security.x509.Extension;
import sun.security.util.KeyUtil;
import java.security.spec.ECParameterSpec;
import sun.security.util.NamedCurve;
import java.security.interfaces.ECKey;
import java.security.AlgorithmParameters;
import java.security.cert.CertStore;
import java.security.Timestamp;
import java.security.CodeSigner;
import java.security.cert.CertStoreException;
import java.security.cert.CertSelector;
import java.util.zip.ZipEntry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.security.cert.CertificateException;
import sun.security.x509.GeneralNames;
import sun.security.x509.CRLDistributionPointsExtension;
import java.security.cert.X509CRL;
import sun.security.x509.URIName;
import sun.security.x509.GeneralName;
import sun.security.x509.DistributionPoint;
import java.security.cert.CRLSelector;
import javax.security.auth.x500.X500Principal;
import java.security.cert.X509CRLSelector;
import sun.security.provider.certpath.CertStoreHelper;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Enumeration;
import java.security.KeyStoreException;
import java.nio.file.Files;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import sun.security.util.SecurityProviderConstants;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import java.security.spec.KeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import java.util.Locale;
import java.security.PublicKey;
import java.security.Key;
import java.security.cert.CRL;
import java.util.Base64;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CRLEntryImpl;
import java.math.BigInteger;
import sun.security.x509.CRLReasonCodeExtension;
import sun.security.x509.CRLExtensions;
import java.security.cert.X509CRLEntry;
import java.security.spec.PSSParameterSpec;
import java.security.cert.X509Certificate;
import java.security.cert.Certificate;
import sun.security.x509.CertificateExtensions;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.pkcs10.PKCS10Attribute;
import sun.security.x509.CertificateX509Key;
import sun.security.pkcs10.PKCS10;
import sun.security.util.Pem;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateSerialNumber;
import java.util.Random;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.util.SignatureUtil;
import sun.security.x509.AlgorithmId;
import java.security.Signature;
import java.security.PrivateKey;
import sun.security.x509.CertificateValidity;
import java.util.Date;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;
import sun.security.x509.X500Name;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.Collections;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import sun.security.util.Password;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.security.Security;
import java.security.Provider;
import java.net.URLClassLoader;
import sun.security.tools.PathList;
import java.text.MessageFormat;
import sun.security.tools.KeyStoreUtil;
import java.util.Iterator;
import java.util.Arrays;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.text.Collator;
import java.util.ResourceBundle;
import java.security.CryptoPrimitive;
import sun.security.util.DisabledAlgorithmConstraints;
import java.util.List;
import java.security.cert.CertificateFactory;
import java.security.KeyStore;
import java.io.InputStream;
import java.io.File;
import java.util.Set;

public final class Main
{
    private static final byte[] CRLF;
    private boolean debug;
    private Command command;
    private String sigAlgName;
    private String keyAlgName;
    private boolean verbose;
    private int keysize;
    private boolean rfc;
    private long validity;
    private String alias;
    private String dname;
    private String dest;
    private String filename;
    private String infilename;
    private String outfilename;
    private String srcksfname;
    private Set<Pair<String, String>> providers;
    private String storetype;
    private String srcProviderName;
    private String providerName;
    private String pathlist;
    private char[] storePass;
    private char[] storePassNew;
    private char[] keyPass;
    private char[] keyPassNew;
    private char[] newPass;
    private char[] destKeyPass;
    private char[] srckeyPass;
    private String ksfname;
    private File ksfile;
    private InputStream ksStream;
    private String sslserver;
    private String jarfile;
    private KeyStore keyStore;
    private boolean token;
    private boolean nullStream;
    private boolean kssave;
    private boolean noprompt;
    private boolean trustcacerts;
    private boolean nowarn;
    private boolean protectedPath;
    private boolean srcprotectedPath;
    private CertificateFactory cf;
    private KeyStore caks;
    private char[] srcstorePass;
    private String srcstoretype;
    private Set<char[]> passwords;
    private String startDate;
    private List<String> ids;
    private List<String> v3ext;
    private boolean inplaceImport;
    private String inplaceBackupName;
    private List<String> weakWarnings;
    private static final DisabledAlgorithmConstraints DISABLED_CHECK;
    private static final Set<CryptoPrimitive> SIG_PRIMITIVE_SET;
    private static final Class<?>[] PARAM_STRING;
    private static final String NONE = "NONE";
    private static final String P11KEYSTORE = "PKCS11";
    private static final String P12KEYSTORE = "PKCS12";
    private static final String keyAlias = "mykey";
    private static final ResourceBundle rb;
    private static final Collator collator;
    private static final String[] extSupported;
    
    private Main() {
        this.debug = false;
        this.command = null;
        this.sigAlgName = null;
        this.keyAlgName = null;
        this.verbose = false;
        this.keysize = -1;
        this.rfc = false;
        this.validity = 90L;
        this.alias = null;
        this.dname = null;
        this.dest = null;
        this.filename = null;
        this.infilename = null;
        this.outfilename = null;
        this.srcksfname = null;
        this.providers = null;
        this.storetype = null;
        this.srcProviderName = null;
        this.providerName = null;
        this.pathlist = null;
        this.storePass = null;
        this.storePassNew = null;
        this.keyPass = null;
        this.keyPassNew = null;
        this.newPass = null;
        this.destKeyPass = null;
        this.srckeyPass = null;
        this.ksfname = null;
        this.ksfile = null;
        this.ksStream = null;
        this.sslserver = null;
        this.jarfile = null;
        this.keyStore = null;
        this.token = false;
        this.nullStream = false;
        this.kssave = false;
        this.noprompt = false;
        this.trustcacerts = false;
        this.nowarn = false;
        this.protectedPath = false;
        this.srcprotectedPath = false;
        this.cf = null;
        this.caks = null;
        this.srcstorePass = null;
        this.srcstoretype = null;
        this.passwords = new HashSet<char[]>();
        this.startDate = null;
        this.ids = new ArrayList<String>();
        this.v3ext = new ArrayList<String>();
        this.inplaceImport = false;
        this.inplaceBackupName = null;
        this.weakWarnings = new ArrayList<String>();
    }
    
    public static void main(final String[] array) throws Exception {
        new Main().run(array, System.out);
    }
    
    private void run(final String[] array, final PrintStream printStream) throws Exception {
        try {
            this.parseArgs(array);
            if (this.command != null) {
                this.doCommands(printStream);
            }
        }
        catch (final Exception ex) {
            System.out.println(Main.rb.getString("keytool.error.") + ex);
            if (this.verbose) {
                ex.printStackTrace(System.out);
            }
            if (this.debug) {
                throw ex;
            }
            System.exit(1);
        }
        finally {
            this.printWeakWarnings(false);
            for (final char[] array2 : this.passwords) {
                if (array2 != null) {
                    Arrays.fill(array2, ' ');
                }
            }
            if (this.ksStream != null) {
                this.ksStream.close();
            }
        }
    }
    
    void parseArgs(final String[] array) {
        boolean b = array.length == 0;
        int n;
        for (n = 0; n < array.length && array[n].startsWith("-"); ++n) {
            String substring = array[n];
            if (n == array.length - 1) {
                final Option[] values = Option.values();
                final int length = values.length;
                int i = 0;
                while (i < length) {
                    final Option option = values[i];
                    if (Main.collator.compare(substring, option.toString()) == 0) {
                        if (option.arg != null) {
                            this.errorNeedArgument(substring);
                            break;
                        }
                        break;
                    }
                    else {
                        ++i;
                    }
                }
            }
            String substring2 = null;
            final int index = substring.indexOf(58);
            if (index > 0) {
                substring2 = substring.substring(index + 1);
                substring = substring.substring(0, index);
            }
            boolean b2 = false;
            for (final Command command : Command.values()) {
                if (Main.collator.compare(substring, command.toString()) == 0) {
                    this.command = command;
                    b2 = true;
                    break;
                }
            }
            if (!b2) {
                if (Main.collator.compare(substring, "-export") == 0) {
                    this.command = Command.EXPORTCERT;
                }
                else if (Main.collator.compare(substring, "-genkey") == 0) {
                    this.command = Command.GENKEYPAIR;
                }
                else if (Main.collator.compare(substring, "-import") == 0) {
                    this.command = Command.IMPORTCERT;
                }
                else if (Main.collator.compare(substring, "-importpassword") == 0) {
                    this.command = Command.IMPORTPASS;
                }
                else if (Main.collator.compare(substring, "-help") == 0) {
                    b = true;
                }
                else if (Main.collator.compare(substring, "-nowarn") == 0) {
                    this.nowarn = true;
                }
                else if (Main.collator.compare(substring, "-keystore") == 0 || Main.collator.compare(substring, "-destkeystore") == 0) {
                    this.ksfname = array[++n];
                }
                else if (Main.collator.compare(substring, "-storepass") == 0 || Main.collator.compare(substring, "-deststorepass") == 0) {
                    this.storePass = this.getPass(substring2, array[++n]);
                    this.passwords.add(this.storePass);
                }
                else if (Main.collator.compare(substring, "-storetype") == 0 || Main.collator.compare(substring, "-deststoretype") == 0) {
                    this.storetype = KeyStoreUtil.niceStoreTypeName(array[++n]);
                }
                else if (Main.collator.compare(substring, "-srcstorepass") == 0) {
                    this.srcstorePass = this.getPass(substring2, array[++n]);
                    this.passwords.add(this.srcstorePass);
                }
                else if (Main.collator.compare(substring, "-srcstoretype") == 0) {
                    this.srcstoretype = KeyStoreUtil.niceStoreTypeName(array[++n]);
                }
                else if (Main.collator.compare(substring, "-srckeypass") == 0) {
                    this.srckeyPass = this.getPass(substring2, array[++n]);
                    this.passwords.add(this.srckeyPass);
                }
                else if (Main.collator.compare(substring, "-srcprovidername") == 0) {
                    this.srcProviderName = array[++n];
                }
                else if (Main.collator.compare(substring, "-providername") == 0 || Main.collator.compare(substring, "-destprovidername") == 0) {
                    this.providerName = array[++n];
                }
                else if (Main.collator.compare(substring, "-providerpath") == 0) {
                    this.pathlist = array[++n];
                }
                else if (Main.collator.compare(substring, "-keypass") == 0) {
                    this.keyPass = this.getPass(substring2, array[++n]);
                    this.passwords.add(this.keyPass);
                }
                else if (Main.collator.compare(substring, "-new") == 0) {
                    this.newPass = this.getPass(substring2, array[++n]);
                    this.passwords.add(this.newPass);
                }
                else if (Main.collator.compare(substring, "-destkeypass") == 0) {
                    this.destKeyPass = this.getPass(substring2, array[++n]);
                    this.passwords.add(this.destKeyPass);
                }
                else if (Main.collator.compare(substring, "-alias") == 0 || Main.collator.compare(substring, "-srcalias") == 0) {
                    this.alias = array[++n];
                }
                else if (Main.collator.compare(substring, "-dest") == 0 || Main.collator.compare(substring, "-destalias") == 0) {
                    this.dest = array[++n];
                }
                else if (Main.collator.compare(substring, "-dname") == 0) {
                    this.dname = array[++n];
                }
                else if (Main.collator.compare(substring, "-keysize") == 0) {
                    this.keysize = Integer.parseInt(array[++n]);
                }
                else if (Main.collator.compare(substring, "-keyalg") == 0) {
                    this.keyAlgName = array[++n];
                }
                else if (Main.collator.compare(substring, "-sigalg") == 0) {
                    this.sigAlgName = array[++n];
                }
                else if (Main.collator.compare(substring, "-startdate") == 0) {
                    this.startDate = array[++n];
                }
                else if (Main.collator.compare(substring, "-validity") == 0) {
                    this.validity = Long.parseLong(array[++n]);
                }
                else if (Main.collator.compare(substring, "-ext") == 0) {
                    this.v3ext.add(array[++n]);
                }
                else if (Main.collator.compare(substring, "-id") == 0) {
                    this.ids.add(array[++n]);
                }
                else if (Main.collator.compare(substring, "-file") == 0) {
                    this.filename = array[++n];
                }
                else if (Main.collator.compare(substring, "-infile") == 0) {
                    this.infilename = array[++n];
                }
                else if (Main.collator.compare(substring, "-outfile") == 0) {
                    this.outfilename = array[++n];
                }
                else if (Main.collator.compare(substring, "-sslserver") == 0) {
                    this.sslserver = array[++n];
                }
                else if (Main.collator.compare(substring, "-jarfile") == 0) {
                    this.jarfile = array[++n];
                }
                else if (Main.collator.compare(substring, "-srckeystore") == 0) {
                    this.srcksfname = array[++n];
                }
                else if (Main.collator.compare(substring, "-provider") == 0 || Main.collator.compare(substring, "-providerclass") == 0) {
                    if (this.providers == null) {
                        this.providers = new HashSet<Pair<String, String>>(3);
                    }
                    final String s = array[++n];
                    String s2 = null;
                    if (array.length > n + 1) {
                        final String s3 = array[n + 1];
                        if (Main.collator.compare(s3, "-providerarg") == 0) {
                            if (array.length == n + 2) {
                                this.errorNeedArgument(s3);
                            }
                            s2 = array[n + 2];
                            n += 2;
                        }
                    }
                    this.providers.add(Pair.of(s, s2));
                }
                else if (Main.collator.compare(substring, "-v") == 0) {
                    this.verbose = true;
                }
                else if (Main.collator.compare(substring, "-debug") == 0) {
                    this.debug = true;
                }
                else if (Main.collator.compare(substring, "-rfc") == 0) {
                    this.rfc = true;
                }
                else if (Main.collator.compare(substring, "-noprompt") == 0) {
                    this.noprompt = true;
                }
                else if (Main.collator.compare(substring, "-trustcacerts") == 0) {
                    this.trustcacerts = true;
                }
                else if (Main.collator.compare(substring, "-protected") == 0 || Main.collator.compare(substring, "-destprotected") == 0) {
                    this.protectedPath = true;
                }
                else if (Main.collator.compare(substring, "-srcprotected") == 0) {
                    this.srcprotectedPath = true;
                }
                else {
                    System.err.println(Main.rb.getString("Illegal.option.") + substring);
                    this.tinyHelp();
                }
            }
        }
        if (n < array.length) {
            System.err.println(Main.rb.getString("Illegal.option.") + array[n]);
            this.tinyHelp();
        }
        if (this.command == null) {
            if (b) {
                this.usage();
            }
            else {
                System.err.println(Main.rb.getString("Usage.error.no.command.provided"));
                this.tinyHelp();
            }
        }
        else if (b) {
            this.usage();
            this.command = null;
        }
    }
    
    boolean isKeyStoreRelated(final Command command) {
        return command != Command.PRINTCERT && command != Command.PRINTCERTREQ;
    }
    
    void doCommands(PrintStream printStream) throws Exception {
        if ("PKCS11".equalsIgnoreCase(this.storetype) || KeyStoreUtil.isWindowsKeyStore(this.storetype)) {
            this.token = true;
            if (this.ksfname == null) {
                this.ksfname = "NONE";
            }
        }
        if ("NONE".equals(this.ksfname)) {
            this.nullStream = true;
        }
        if (this.token && !this.nullStream) {
            System.err.println(MessageFormat.format(Main.rb.getString(".keystore.must.be.NONE.if.storetype.is.{0}"), this.storetype));
            System.err.println();
            this.tinyHelp();
        }
        if (this.token && (this.command == Command.KEYPASSWD || this.command == Command.STOREPASSWD)) {
            throw new UnsupportedOperationException(MessageFormat.format(Main.rb.getString(".storepasswd.and.keypasswd.commands.not.supported.if.storetype.is.{0}"), this.storetype));
        }
        if (this.token && (this.keyPass != null || this.newPass != null || this.destKeyPass != null)) {
            throw new IllegalArgumentException(MessageFormat.format(Main.rb.getString(".keypass.and.new.can.not.be.specified.if.storetype.is.{0}"), this.storetype));
        }
        if (this.protectedPath && (this.storePass != null || this.keyPass != null || this.newPass != null || this.destKeyPass != null)) {
            throw new IllegalArgumentException(Main.rb.getString("if.protected.is.specified.then.storepass.keypass.and.new.must.not.be.specified"));
        }
        if (this.srcprotectedPath && (this.srcstorePass != null || this.srckeyPass != null)) {
            throw new IllegalArgumentException(Main.rb.getString("if.srcprotected.is.specified.then.srcstorepass.and.srckeypass.must.not.be.specified"));
        }
        if (KeyStoreUtil.isWindowsKeyStore(this.storetype) && (this.storePass != null || this.keyPass != null || this.newPass != null || this.destKeyPass != null)) {
            throw new IllegalArgumentException(Main.rb.getString("if.keystore.is.not.password.protected.then.storepass.keypass.and.new.must.not.be.specified"));
        }
        if (KeyStoreUtil.isWindowsKeyStore(this.srcstoretype) && (this.srcstorePass != null || this.srckeyPass != null)) {
            throw new IllegalArgumentException(Main.rb.getString("if.source.keystore.is.not.password.protected.then.srcstorepass.and.srckeypass.must.not.be.specified"));
        }
        if (this.validity <= 0L) {
            throw new Exception(Main.rb.getString("Validity.must.be.greater.than.zero"));
        }
        if (this.providers != null) {
            ClassLoader systemClassLoader;
            if (this.pathlist != null) {
                systemClassLoader = new URLClassLoader(PathList.pathToURLs(PathList.appendPath(PathList.appendPath(PathList.appendPath(null, System.getProperty("java.class.path")), System.getProperty("env.class.path")), this.pathlist)));
            }
            else {
                systemClassLoader = ClassLoader.getSystemClassLoader();
            }
            for (final Pair pair : this.providers) {
                final String s = (String)pair.fst;
                Class<?> clazz;
                if (systemClassLoader != null) {
                    clazz = systemClassLoader.loadClass(s);
                }
                else {
                    clazz = Class.forName(s);
                }
                final String s2 = (String)pair.snd;
                Object o;
                if (s2 == null) {
                    o = clazz.newInstance();
                }
                else {
                    o = clazz.getConstructor(Main.PARAM_STRING).newInstance(s2);
                }
                if (!(o instanceof Provider)) {
                    throw new Exception(new MessageFormat(Main.rb.getString("provName.not.a.provider")).format(new Object[] { s }));
                }
                Security.addProvider((Provider)o);
            }
        }
        if (this.command == Command.LIST && this.verbose && this.rfc) {
            System.err.println(Main.rb.getString("Must.not.specify.both.v.and.rfc.with.list.command"));
            this.tinyHelp();
        }
        if (this.command == Command.GENKEYPAIR && this.keyPass != null && this.keyPass.length < 6) {
            throw new Exception(Main.rb.getString("Key.password.must.be.at.least.6.characters"));
        }
        if (this.newPass != null && this.newPass.length < 6) {
            throw new Exception(Main.rb.getString("New.password.must.be.at.least.6.characters"));
        }
        if (this.destKeyPass != null && this.destKeyPass.length < 6) {
            throw new Exception(Main.rb.getString("New.password.must.be.at.least.6.characters"));
        }
        if (this.ksfname == null) {
            this.ksfname = System.getProperty("user.home") + File.separator + ".keystore";
        }
        KeyStore keyStore = null;
        if (this.command == Command.IMPORTKEYSTORE) {
            this.inplaceImport = this.inplaceImportCheck();
            if (this.inplaceImport) {
                keyStore = this.loadSourceKeyStore();
                if (this.storePass == null) {
                    this.storePass = this.srcstorePass;
                }
            }
        }
        if (this.isKeyStoreRelated(this.command) && !this.nullStream && !this.inplaceImport) {
            try {
                this.ksfile = new File(this.ksfname);
                if (this.ksfile.exists() && this.ksfile.length() == 0L) {
                    throw new Exception(Main.rb.getString("Keystore.file.exists.but.is.empty.") + this.ksfname);
                }
                this.ksStream = new FileInputStream(this.ksfile);
            }
            catch (final FileNotFoundException ex) {
                if (this.command != Command.GENKEYPAIR && this.command != Command.GENSECKEY && this.command != Command.IDENTITYDB && this.command != Command.IMPORTCERT && this.command != Command.IMPORTPASS && this.command != Command.IMPORTKEYSTORE && this.command != Command.PRINTCRL) {
                    throw new Exception(Main.rb.getString("Keystore.file.does.not.exist.") + this.ksfname);
                }
            }
        }
        if ((this.command == Command.KEYCLONE || this.command == Command.CHANGEALIAS) && this.dest == null) {
            this.dest = this.getAlias("destination");
            if ("".equals(this.dest)) {
                throw new Exception(Main.rb.getString("Must.specify.destination.alias"));
            }
        }
        if (this.command == Command.DELETE && this.alias == null) {
            this.alias = this.getAlias(null);
            if ("".equals(this.alias)) {
                throw new Exception(Main.rb.getString("Must.specify.alias"));
            }
        }
        if (this.storetype == null) {
            this.storetype = KeyStore.getDefaultType();
        }
        if (this.providerName == null) {
            this.keyStore = KeyStore.getInstance(this.storetype);
        }
        else {
            this.keyStore = KeyStore.getInstance(this.storetype, this.providerName);
        }
        if (!this.nullStream) {
            if (this.inplaceImport) {
                this.keyStore.load(null, this.storePass);
            }
            else {
                this.keyStore.load(this.ksStream, this.storePass);
            }
            if (this.ksStream != null) {
                this.ksStream.close();
            }
        }
        if ("PKCS12".equalsIgnoreCase(this.storetype) && this.command == Command.KEYPASSWD) {
            throw new UnsupportedOperationException(Main.rb.getString(".keypasswd.commands.not.supported.if.storetype.is.PKCS12"));
        }
        if (this.nullStream && this.storePass != null) {
            this.keyStore.load(null, this.storePass);
        }
        else if (!this.nullStream && this.storePass != null) {
            if (this.ksStream == null && this.storePass.length < 6) {
                throw new Exception(Main.rb.getString("Keystore.password.must.be.at.least.6.characters"));
            }
        }
        else if (this.storePass == null) {
            if (!this.protectedPath && !KeyStoreUtil.isWindowsKeyStore(this.storetype) && (this.command == Command.CERTREQ || this.command == Command.DELETE || this.command == Command.GENKEYPAIR || this.command == Command.GENSECKEY || this.command == Command.IMPORTCERT || this.command == Command.IMPORTPASS || this.command == Command.IMPORTKEYSTORE || this.command == Command.KEYCLONE || this.command == Command.CHANGEALIAS || this.command == Command.SELFCERT || this.command == Command.STOREPASSWD || this.command == Command.KEYPASSWD || this.command == Command.IDENTITYDB)) {
                int n = 0;
                do {
                    if (this.command == Command.IMPORTKEYSTORE) {
                        System.err.print(Main.rb.getString("Enter.destination.keystore.password."));
                    }
                    else {
                        System.err.print(Main.rb.getString("Enter.keystore.password."));
                    }
                    System.err.flush();
                    this.storePass = Password.readPassword(System.in);
                    this.passwords.add(this.storePass);
                    if (!this.nullStream && (this.storePass == null || this.storePass.length < 6)) {
                        System.err.println(Main.rb.getString("Keystore.password.is.too.short.must.be.at.least.6.characters"));
                        this.storePass = null;
                    }
                    if (this.storePass != null && !this.nullStream && this.ksStream == null) {
                        System.err.print(Main.rb.getString("Re.enter.new.password."));
                        final char[] password = Password.readPassword(System.in);
                        this.passwords.add(password);
                        if (!Arrays.equals(this.storePass, password)) {
                            System.err.println(Main.rb.getString("They.don.t.match.Try.again"));
                            this.storePass = null;
                        }
                    }
                    ++n;
                } while (this.storePass == null && n < 3);
                if (this.storePass == null) {
                    System.err.println(Main.rb.getString("Too.many.failures.try.later"));
                    return;
                }
            }
            else if (!this.protectedPath && !KeyStoreUtil.isWindowsKeyStore(this.storetype) && this.isKeyStoreRelated(this.command) && this.command != Command.PRINTCRL) {
                System.err.print(Main.rb.getString("Enter.keystore.password."));
                System.err.flush();
                this.storePass = Password.readPassword(System.in);
                this.passwords.add(this.storePass);
            }
            if (this.nullStream) {
                this.keyStore.load(null, this.storePass);
            }
            else if (this.ksStream != null) {
                this.ksStream = new FileInputStream(this.ksfile);
                this.keyStore.load(this.ksStream, this.storePass);
                this.ksStream.close();
            }
        }
        if (this.storePass != null && "PKCS12".equalsIgnoreCase(this.storetype)) {
            final MessageFormat messageFormat = new MessageFormat(Main.rb.getString("Warning.Different.store.and.key.passwords.not.supported.for.PKCS12.KeyStores.Ignoring.user.specified.command.value."));
            if (this.keyPass != null && !Arrays.equals(this.storePass, this.keyPass)) {
                System.err.println(messageFormat.format(new Object[] { "-keypass" }));
                this.keyPass = this.storePass;
            }
            if (this.newPass != null && !Arrays.equals(this.storePass, this.newPass)) {
                System.err.println(messageFormat.format(new Object[] { "-new" }));
                this.newPass = this.storePass;
            }
            if (this.destKeyPass != null && !Arrays.equals(this.storePass, this.destKeyPass)) {
                System.err.println(messageFormat.format(new Object[] { "-destkeypass" }));
                this.destKeyPass = this.storePass;
            }
        }
        if (this.command == Command.PRINTCERT || this.command == Command.IMPORTCERT || this.command == Command.IDENTITYDB || this.command == Command.PRINTCRL) {
            this.cf = CertificateFactory.getInstance("X509");
        }
        if (this.command != Command.IMPORTCERT) {
            this.trustcacerts = false;
        }
        if (this.trustcacerts) {
            this.caks = KeyStoreUtil.getCacertsKeyStore();
        }
        if (this.command == Command.CERTREQ) {
            if (this.filename != null) {
                try (final PrintStream printStream2 = new PrintStream(new FileOutputStream(this.filename))) {
                    this.doCertReq(this.alias, this.sigAlgName, printStream2);
                }
            }
            else {
                this.doCertReq(this.alias, this.sigAlgName, printStream);
            }
            if (this.verbose && this.filename != null) {
                System.err.println(new MessageFormat(Main.rb.getString("Certification.request.stored.in.file.filename.")).format(new Object[] { this.filename }));
                System.err.println(Main.rb.getString("Submit.this.to.your.CA"));
            }
        }
        else if (this.command == Command.DELETE) {
            this.doDeleteEntry(this.alias);
            this.kssave = true;
        }
        else if (this.command == Command.EXPORTCERT) {
            if (this.filename != null) {
                try (final PrintStream printStream3 = new PrintStream(new FileOutputStream(this.filename))) {
                    this.doExportCert(this.alias, printStream3);
                }
            }
            else {
                this.doExportCert(this.alias, printStream);
            }
            if (this.filename != null) {
                System.err.println(new MessageFormat(Main.rb.getString("Certificate.stored.in.file.filename.")).format(new Object[] { this.filename }));
            }
        }
        else if (this.command == Command.GENKEYPAIR) {
            if (this.keyAlgName == null) {
                this.keyAlgName = "DSA";
            }
            this.doGenKeyPair(this.alias, this.dname, this.keyAlgName, this.keysize, this.sigAlgName);
            this.kssave = true;
        }
        else if (this.command == Command.GENSECKEY) {
            if (this.keyAlgName == null) {
                this.keyAlgName = "DES";
            }
            this.doGenSecretKey(this.alias, this.keyAlgName, this.keysize);
            this.kssave = true;
        }
        else if (this.command == Command.IMPORTPASS) {
            if (this.keyAlgName == null) {
                this.keyAlgName = "PBE";
            }
            this.doGenSecretKey(this.alias, this.keyAlgName, this.keysize);
            this.kssave = true;
        }
        else if (this.command == Command.IDENTITYDB) {
            if (this.filename != null) {
                try (final FileInputStream fileInputStream = new FileInputStream(this.filename)) {
                    this.doImportIdentityDatabase(fileInputStream);
                }
            }
            else {
                this.doImportIdentityDatabase(System.in);
            }
        }
        else if (this.command == Command.IMPORTCERT) {
            InputStream in = System.in;
            if (this.filename != null) {
                in = new FileInputStream(this.filename);
            }
            final String s3 = (this.alias != null) ? this.alias : "mykey";
            try {
                if (this.keyStore.entryInstanceOf(s3, KeyStore.PrivateKeyEntry.class)) {
                    this.kssave = this.installReply(s3, in);
                    if (this.kssave) {
                        System.err.println(Main.rb.getString("Certificate.reply.was.installed.in.keystore"));
                    }
                    else {
                        System.err.println(Main.rb.getString("Certificate.reply.was.not.installed.in.keystore"));
                    }
                }
                else if (!this.keyStore.containsAlias(s3) || this.keyStore.entryInstanceOf(s3, KeyStore.TrustedCertificateEntry.class)) {
                    this.kssave = this.addTrustedCert(s3, in);
                    if (this.kssave) {
                        System.err.println(Main.rb.getString("Certificate.was.added.to.keystore"));
                    }
                    else {
                        System.err.println(Main.rb.getString("Certificate.was.not.added.to.keystore"));
                    }
                }
            }
            finally {
                if (in != System.in) {
                    in.close();
                }
            }
        }
        else if (this.command == Command.IMPORTKEYSTORE) {
            if (keyStore == null) {
                keyStore = this.loadSourceKeyStore();
            }
            this.doImportKeyStore(keyStore);
            this.kssave = true;
        }
        else if (this.command == Command.KEYCLONE) {
            this.keyPassNew = this.newPass;
            if (this.alias == null) {
                this.alias = "mykey";
            }
            if (!this.keyStore.containsAlias(this.alias)) {
                throw new Exception(new MessageFormat(Main.rb.getString("Alias.alias.does.not.exist")).format(new Object[] { this.alias }));
            }
            if (!this.keyStore.entryInstanceOf(this.alias, KeyStore.PrivateKeyEntry.class)) {
                throw new Exception(new MessageFormat(Main.rb.getString("Alias.alias.references.an.entry.type.that.is.not.a.private.key.entry.The.keyclone.command.only.supports.cloning.of.private.key")).format(new Object[] { this.alias }));
            }
            this.doCloneEntry(this.alias, this.dest, true);
            this.kssave = true;
        }
        else if (this.command == Command.CHANGEALIAS) {
            if (this.alias == null) {
                this.alias = "mykey";
            }
            this.doCloneEntry(this.alias, this.dest, false);
            if (this.keyStore.containsAlias(this.alias)) {
                this.doDeleteEntry(this.alias);
            }
            this.kssave = true;
        }
        else if (this.command == Command.KEYPASSWD) {
            this.keyPassNew = this.newPass;
            this.doChangeKeyPasswd(this.alias);
            this.kssave = true;
        }
        else if (this.command == Command.LIST) {
            if (this.storePass == null && !KeyStoreUtil.isWindowsKeyStore(this.storetype)) {
                this.printNoIntegrityWarning();
            }
            if (this.alias != null) {
                this.doPrintEntry(Main.rb.getString("the.certificate"), this.alias, printStream);
            }
            else {
                this.doPrintEntries(printStream);
            }
        }
        else if (this.command == Command.PRINTCERT) {
            this.doPrintCert(printStream);
        }
        else if (this.command == Command.SELFCERT) {
            this.doSelfCert(this.alias, this.dname, this.sigAlgName);
            this.kssave = true;
        }
        else if (this.command == Command.STOREPASSWD) {
            this.storePassNew = this.newPass;
            if (this.storePassNew == null) {
                this.storePassNew = this.getNewPasswd("keystore password", this.storePass);
            }
            this.kssave = true;
        }
        else if (this.command == Command.GENCERT) {
            if (this.alias == null) {
                this.alias = "mykey";
            }
            InputStream in2 = System.in;
            if (this.infilename != null) {
                in2 = new FileInputStream(this.infilename);
            }
            PrintStream printStream4 = null;
            if (this.outfilename != null) {
                printStream4 = (printStream = new PrintStream(new FileOutputStream(this.outfilename)));
            }
            try {
                this.doGenCert(this.alias, this.sigAlgName, in2, printStream);
            }
            finally {
                if (in2 != System.in) {
                    in2.close();
                }
                if (printStream4 != null) {
                    printStream4.close();
                }
            }
        }
        else if (this.command == Command.GENCRL) {
            if (this.alias == null) {
                this.alias = "mykey";
            }
            if (this.filename != null) {
                try (final PrintStream printStream5 = new PrintStream(new FileOutputStream(this.filename))) {
                    this.doGenCRL(printStream5);
                }
            }
            else {
                this.doGenCRL(printStream);
            }
        }
        else if (this.command == Command.PRINTCERTREQ) {
            if (this.filename != null) {
                try (final FileInputStream fileInputStream2 = new FileInputStream(this.filename)) {
                    this.doPrintCertReq(fileInputStream2, printStream);
                }
            }
            else {
                this.doPrintCertReq(System.in, printStream);
            }
        }
        else if (this.command == Command.PRINTCRL) {
            this.doPrintCRL(this.filename, printStream);
        }
        if (this.kssave) {
            if (this.verbose) {
                System.err.println(new MessageFormat(Main.rb.getString(".Storing.ksfname.")).format(new Object[] { this.nullStream ? "keystore" : this.ksfname }));
            }
            if (this.token) {
                this.keyStore.store(null, null);
            }
            else {
                final char[] array = (this.storePassNew != null) ? this.storePassNew : this.storePass;
                if (this.nullStream) {
                    this.keyStore.store(null, array);
                }
                else {
                    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    this.keyStore.store(byteArrayOutputStream, array);
                    try (final FileOutputStream fileOutputStream = new FileOutputStream(this.ksfname)) {
                        fileOutputStream.write(byteArrayOutputStream.toByteArray());
                    }
                }
            }
        }
        if (this.isKeyStoreRelated(this.command) && !this.token && !this.nullStream && this.ksfname != null) {
            final File file = new File(this.ksfname);
            if (file.exists()) {
                final String keyStoreType = this.keyStoreType(file);
                if (keyStoreType.equalsIgnoreCase("JKS") || keyStoreType.equalsIgnoreCase("JCEKS")) {
                    boolean b = true;
                    final Iterator<String> iterator2 = Collections.list(this.keyStore.aliases()).iterator();
                    while (iterator2.hasNext()) {
                        if (!this.keyStore.entryInstanceOf(iterator2.next(), KeyStore.TrustedCertificateEntry.class)) {
                            b = false;
                            break;
                        }
                    }
                    if (!b) {
                        this.weakWarnings.add(String.format(Main.rb.getString("jks.storetype.warning"), keyStoreType, this.ksfname));
                    }
                }
                if (this.inplaceImport) {
                    final String keyStoreType2 = this.keyStoreType(new File(this.inplaceBackupName));
                    this.weakWarnings.add(String.format(keyStoreType.equalsIgnoreCase(keyStoreType2) ? Main.rb.getString("backup.keystore.warning") : Main.rb.getString("migrate.keystore.warning"), this.srcksfname, keyStoreType2, this.inplaceBackupName, keyStoreType));
                }
            }
        }
    }
    
    private String keyStoreType(final File file) throws IOException {
        final int n = -17957139;
        final int n2 = -825307442;
        try (final DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file))) {
            final int int1 = dataInputStream.readInt();
            if (int1 == n) {
                return "JKS";
            }
            if (int1 == n2) {
                return "JCEKS";
            }
            return "Non JKS/JCEKS";
        }
    }
    
    private void doGenCert(final String s, String compatibleSigAlgName, final InputStream inputStream, final PrintStream printStream) throws Exception {
        if (!this.keyStore.containsAlias(s)) {
            throw new Exception(new MessageFormat(Main.rb.getString("Alias.alias.does.not.exist")).format(new Object[] { s }));
        }
        final Certificate certificate = this.keyStore.getCertificate(s);
        final X500Name x500Name = (X500Name)((X509CertInfo)new X509CertImpl(certificate.getEncoded()).get("x509.info")).get("subject.dname");
        final Date startDate = getStartDate(this.startDate);
        final Date date = new Date();
        date.setTime(startDate.getTime() + this.validity * 1000L * 24L * 60L * 60L);
        final CertificateValidity certificateValidity = new CertificateValidity(startDate, date);
        final PrivateKey privateKey = (PrivateKey)this.recoverKey(s, this.storePass, this.keyPass).fst;
        if (compatibleSigAlgName == null) {
            compatibleSigAlgName = getCompatibleSigAlgName(privateKey.getAlgorithm());
        }
        final Signature instance = Signature.getInstance(compatibleSigAlgName);
        final PSSParameterSpec defaultAlgorithmParameterSpec = AlgorithmId.getDefaultAlgorithmParameterSpec(compatibleSigAlgName, privateKey);
        SignatureUtil.initSignWithParam(instance, privateKey, defaultAlgorithmParameterSpec, null);
        final X509CertInfo x509CertInfo = new X509CertInfo();
        final AlgorithmId withParameterSpec = AlgorithmId.getWithParameterSpec(compatibleSigAlgName, defaultAlgorithmParameterSpec);
        x509CertInfo.set("validity", certificateValidity);
        x509CertInfo.set("serialNumber", new CertificateSerialNumber(new Random().nextInt() & Integer.MAX_VALUE));
        x509CertInfo.set("version", new CertificateVersion(2));
        x509CertInfo.set("algorithmID", new CertificateAlgorithmId(withParameterSpec));
        x509CertInfo.set("issuer", x500Name);
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        boolean b = false;
        final StringBuffer sb = new StringBuffer();
        while (true) {
            final String line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            if (line.startsWith("-----BEGIN") && line.indexOf("REQUEST") >= 0) {
                b = true;
            }
            else {
                if (line.startsWith("-----END") && line.indexOf("REQUEST") >= 0) {
                    break;
                }
                if (!b) {
                    continue;
                }
                sb.append(line);
            }
        }
        final PKCS10 pkcs10 = new PKCS10(Pem.decode(new String(sb)));
        this.checkWeak(Main.rb.getString("the.certificate.request"), pkcs10);
        x509CertInfo.set("key", new CertificateX509Key(pkcs10.getSubjectPublicKeyInfo()));
        x509CertInfo.set("subject", (this.dname == null) ? pkcs10.getSubjectName() : new X500Name(this.dname));
        CertificateExtensions certificateExtensions = null;
        for (final PKCS10Attribute pkcs10Attribute : pkcs10.getAttributes().getAttributes()) {
            if (pkcs10Attribute.getAttributeId().equals((Object)PKCS9Attribute.EXTENSION_REQUEST_OID)) {
                certificateExtensions = (CertificateExtensions)pkcs10Attribute.getAttributeValue();
            }
        }
        x509CertInfo.set("extensions", this.createV3Extensions(certificateExtensions, null, this.v3ext, pkcs10.getSubjectPublicKeyInfo(), certificate.getPublicKey()));
        final X509CertImpl x509CertImpl = new X509CertImpl(x509CertInfo);
        x509CertImpl.sign(privateKey, defaultAlgorithmParameterSpec, compatibleSigAlgName, null);
        this.dumpCert(x509CertImpl, printStream);
        for (final Certificate certificate2 : this.keyStore.getCertificateChain(s)) {
            if (certificate2 instanceof X509Certificate) {
                final X509Certificate x509Certificate = (X509Certificate)certificate2;
                if (!KeyStoreUtil.isSelfSigned(x509Certificate)) {
                    this.dumpCert(x509Certificate, printStream);
                }
            }
        }
        this.checkWeak(Main.rb.getString("the.issuer"), this.keyStore.getCertificateChain(s));
        this.checkWeak(Main.rb.getString("the.generated.certificate"), x509CertImpl);
    }
    
    private void doGenCRL(final PrintStream printStream) throws Exception {
        if (this.ids == null) {
            throw new Exception("Must provide -id when -gencrl");
        }
        final X500Name x500Name = (X500Name)((X509CertInfo)new X509CertImpl(this.keyStore.getCertificate(this.alias).getEncoded()).get("x509.info")).get("subject.dname");
        final Date startDate = getStartDate(this.startDate);
        final Date date = (Date)startDate.clone();
        date.setTime(date.getTime() + this.validity * 1000L * 24L * 60L * 60L);
        final CertificateValidity certificateValidity = new CertificateValidity(startDate, date);
        final PrivateKey privateKey = (PrivateKey)this.recoverKey(this.alias, this.storePass, this.keyPass).fst;
        if (this.sigAlgName == null) {
            this.sigAlgName = getCompatibleSigAlgName(privateKey.getAlgorithm());
        }
        final X509CRLEntry[] array = new X509CRLEntry[this.ids.size()];
        for (int i = 0; i < this.ids.size(); ++i) {
            final String s = this.ids.get(i);
            final int index = s.indexOf(58);
            if (index >= 0) {
                final CRLExtensions crlExtensions = new CRLExtensions();
                crlExtensions.set("Reason", new CRLReasonCodeExtension(Integer.parseInt(s.substring(index + 1))));
                array[i] = new X509CRLEntryImpl(new BigInteger(s.substring(0, index)), startDate, crlExtensions);
            }
            else {
                array[i] = new X509CRLEntryImpl(new BigInteger(this.ids.get(i)), startDate);
            }
        }
        final X509CRLImpl x509CRLImpl = new X509CRLImpl(x500Name, startDate, date, array);
        x509CRLImpl.sign(privateKey, this.sigAlgName);
        if (this.rfc) {
            printStream.println("-----BEGIN X509 CRL-----");
            printStream.println(Base64.getMimeEncoder(64, Main.CRLF).encodeToString(x509CRLImpl.getEncodedInternal()));
            printStream.println("-----END X509 CRL-----");
        }
        else {
            printStream.write(x509CRLImpl.getEncodedInternal());
        }
        this.checkWeak(Main.rb.getString("the.generated.crl"), x509CRLImpl, privateKey);
    }
    
    private void doCertReq(String s, String compatibleSigAlgName, final PrintStream printStream) throws Exception {
        if (s == null) {
            s = "mykey";
        }
        final Pair<Key, char[]> recoverKey = this.recoverKey(s, this.storePass, this.keyPass);
        final PrivateKey privateKey = (PrivateKey)recoverKey.fst;
        if (this.keyPass == null) {
            this.keyPass = recoverKey.snd;
        }
        final Certificate certificate = this.keyStore.getCertificate(s);
        if (certificate == null) {
            throw new Exception(new MessageFormat(Main.rb.getString("alias.has.no.public.key.certificate.")).format(new Object[] { s }));
        }
        final PKCS10 pkcs10 = new PKCS10(certificate.getPublicKey());
        pkcs10.getAttributes().setAttribute("extensions", new PKCS10Attribute(PKCS9Attribute.EXTENSION_REQUEST_OID, this.createV3Extensions(null, null, this.v3ext, certificate.getPublicKey(), null)));
        if (compatibleSigAlgName == null) {
            compatibleSigAlgName = getCompatibleSigAlgName(privateKey.getAlgorithm());
        }
        final Signature instance = Signature.getInstance(compatibleSigAlgName);
        SignatureUtil.initSignWithParam(instance, privateKey, AlgorithmId.getDefaultAlgorithmParameterSpec(compatibleSigAlgName, privateKey), null);
        pkcs10.encodeAndSign((this.dname == null) ? new X500Name(((X509Certificate)certificate).getSubjectDN().toString()) : new X500Name(this.dname), instance);
        pkcs10.print(printStream);
        this.checkWeak(Main.rb.getString("the.generated.certificate.request"), pkcs10);
    }
    
    private void doDeleteEntry(final String s) throws Exception {
        if (!this.keyStore.containsAlias(s)) {
            throw new Exception(new MessageFormat(Main.rb.getString("Alias.alias.does.not.exist")).format(new Object[] { s }));
        }
        this.keyStore.deleteEntry(s);
    }
    
    private void doExportCert(String s, final PrintStream printStream) throws Exception {
        if (this.storePass == null && !KeyStoreUtil.isWindowsKeyStore(this.storetype)) {
            this.printNoIntegrityWarning();
        }
        if (s == null) {
            s = "mykey";
        }
        if (!this.keyStore.containsAlias(s)) {
            throw new Exception(new MessageFormat(Main.rb.getString("Alias.alias.does.not.exist")).format(new Object[] { s }));
        }
        final X509Certificate x509Certificate = (X509Certificate)this.keyStore.getCertificate(s);
        if (x509Certificate == null) {
            throw new Exception(new MessageFormat(Main.rb.getString("Alias.alias.has.no.certificate")).format(new Object[] { s }));
        }
        this.dumpCert(x509Certificate, printStream);
        this.checkWeak(Main.rb.getString("the.certificate"), x509Certificate);
    }
    
    private char[] promptForKeyPass(final String s, final String s2, final char[] array) throws Exception {
        if ("PKCS12".equalsIgnoreCase(this.storetype)) {
            return array;
        }
        if (!this.token && !this.protectedPath) {
            int i;
            for (i = 0; i < 3; ++i) {
                System.err.println(new MessageFormat(Main.rb.getString("Enter.key.password.for.alias.")).format(new Object[] { s }));
                if (s2 == null) {
                    System.err.print(Main.rb.getString(".RETURN.if.same.as.keystore.password."));
                }
                else {
                    System.err.print(new MessageFormat(Main.rb.getString(".RETURN.if.same.as.for.otherAlias.")).format(new Object[] { s2 }));
                }
                System.err.flush();
                final char[] password = Password.readPassword(System.in);
                this.passwords.add(password);
                if (password == null) {
                    return array;
                }
                if (password.length >= 6) {
                    System.err.print(Main.rb.getString("Re.enter.new.password."));
                    final char[] password2 = Password.readPassword(System.in);
                    this.passwords.add(password2);
                    if (Arrays.equals(password, password2)) {
                        return password;
                    }
                    System.err.println(Main.rb.getString("They.don.t.match.Try.again"));
                }
                else {
                    System.err.println(Main.rb.getString("Key.password.is.too.short.must.be.at.least.6.characters"));
                }
            }
            if (i == 3) {
                if (this.command == Command.KEYCLONE) {
                    throw new Exception(Main.rb.getString("Too.many.failures.Key.entry.not.cloned"));
                }
                throw new Exception(Main.rb.getString("Too.many.failures.key.not.added.to.keystore"));
            }
        }
        return null;
    }
    
    private char[] promptForCredential() throws Exception {
        if (System.console() == null) {
            final char[] password = Password.readPassword(System.in);
            this.passwords.add(password);
            return password;
        }
        int i;
        for (i = 0; i < 3; ++i) {
            System.err.print(Main.rb.getString("Enter.the.password.to.be.stored."));
            System.err.flush();
            final char[] password2 = Password.readPassword(System.in);
            this.passwords.add(password2);
            System.err.print(Main.rb.getString("Re.enter.password."));
            final char[] password3 = Password.readPassword(System.in);
            this.passwords.add(password3);
            if (Arrays.equals(password2, password3)) {
                return password2;
            }
            System.err.println(Main.rb.getString("They.don.t.match.Try.again"));
        }
        if (i == 3) {
            throw new Exception(Main.rb.getString("Too.many.failures.key.not.added.to.keystore"));
        }
        return null;
    }
    
    private void doGenSecretKey(String s, final String s2, int n) throws Exception {
        if (s == null) {
            s = "mykey";
        }
        if (this.keyStore.containsAlias(s)) {
            throw new Exception(new MessageFormat(Main.rb.getString("Secret.key.not.generated.alias.alias.already.exists")).format(new Object[] { s }));
        }
        boolean b = true;
        SecretKey secretKey;
        if (s2.toUpperCase(Locale.ENGLISH).startsWith("PBE")) {
            secretKey = SecretKeyFactory.getInstance("PBE").generateSecret(new PBEKeySpec(this.promptForCredential()));
            if (!"PBE".equalsIgnoreCase(s2)) {
                b = false;
            }
            if (this.verbose) {
                System.err.println(new MessageFormat(Main.rb.getString("Generated.keyAlgName.secret.key")).format(new Object[] { b ? "PBE" : secretKey.getAlgorithm() }));
            }
        }
        else {
            final KeyGenerator instance = KeyGenerator.getInstance(s2);
            if (n == -1) {
                if ("DES".equalsIgnoreCase(s2)) {
                    n = 56;
                }
                else {
                    if (!"DESede".equalsIgnoreCase(s2)) {
                        throw new Exception(Main.rb.getString("Please.provide.keysize.for.secret.key.generation"));
                    }
                    n = 168;
                }
            }
            instance.init(n);
            secretKey = instance.generateKey();
            if (this.verbose) {
                System.err.println(new MessageFormat(Main.rb.getString("Generated.keysize.bit.keyAlgName.secret.key")).format(new Object[] { new Integer(n), secretKey.getAlgorithm() }));
            }
        }
        if (this.keyPass == null) {
            this.keyPass = this.promptForKeyPass(s, null, this.storePass);
        }
        if (b) {
            this.keyStore.setKeyEntry(s, secretKey, this.keyPass, null);
        }
        else {
            this.keyStore.setEntry(s, new KeyStore.SecretKeyEntry(secretKey), new KeyStore.PasswordProtection(this.keyPass, s2, null));
        }
    }
    
    private static String getCompatibleSigAlgName(final String s) throws Exception {
        if ("DSA".equalsIgnoreCase(s)) {
            return "SHA256WithDSA";
        }
        if ("RSA".equalsIgnoreCase(s)) {
            return "SHA256WithRSA";
        }
        if ("EC".equalsIgnoreCase(s)) {
            return "SHA256withECDSA";
        }
        throw new Exception(Main.rb.getString("Cannot.derive.signature.algorithm"));
    }
    
    private void doGenKeyPair(String s, final String s2, final String s3, int n, String compatibleSigAlgName) throws Exception {
        if (n == -1) {
            if ("EC".equalsIgnoreCase(s3)) {
                n = SecurityProviderConstants.DEF_EC_KEY_SIZE;
            }
            else if ("RSA".equalsIgnoreCase(s3)) {
                n = SecurityProviderConstants.DEF_RSA_KEY_SIZE;
            }
            else if ("RSASSA-PSS".equalsIgnoreCase(s3)) {
                n = SecurityProviderConstants.DEF_RSASSA_PSS_KEY_SIZE;
            }
            else if ("DSA".equalsIgnoreCase(s3)) {
                n = SecurityProviderConstants.DEF_DSA_KEY_SIZE;
            }
        }
        if (s == null) {
            s = "mykey";
        }
        if (this.keyStore.containsAlias(s)) {
            throw new Exception(new MessageFormat(Main.rb.getString("Key.pair.not.generated.alias.alias.already.exists")).format(new Object[] { s }));
        }
        if (compatibleSigAlgName == null) {
            compatibleSigAlgName = getCompatibleSigAlgName(s3);
        }
        final CertAndKeyGen certAndKeyGen = new CertAndKeyGen(s3, compatibleSigAlgName, this.providerName);
        X500Name x500Name;
        if (s2 == null) {
            x500Name = this.getX500Name();
        }
        else {
            x500Name = new X500Name(s2);
        }
        certAndKeyGen.generate(n);
        final PrivateKey privateKey = certAndKeyGen.getPrivateKey();
        final X509Certificate[] array = { certAndKeyGen.getSelfCertificate(x500Name, getStartDate(this.startDate), this.validity * 24L * 60L * 60L, this.createV3Extensions(null, null, this.v3ext, certAndKeyGen.getPublicKeyAnyway(), null)) };
        if (this.verbose) {
            System.err.println(new MessageFormat(Main.rb.getString("Generating.keysize.bit.keyAlgName.key.pair.and.self.signed.certificate.sigAlgName.with.a.validity.of.validality.days.for")).format(new Object[] { new Integer(n), privateKey.getAlgorithm(), array[0].getSigAlgName(), new Long(this.validity), x500Name }));
        }
        if (this.keyPass == null) {
            this.keyPass = this.promptForKeyPass(s, null, this.storePass);
        }
        this.checkWeak(Main.rb.getString("the.generated.certificate"), array[0]);
        this.keyStore.setKeyEntry(s, privateKey, this.keyPass, array);
    }
    
    private void doCloneEntry(String s, final String s2, final boolean b) throws Exception {
        if (s == null) {
            s = "mykey";
        }
        if (this.keyStore.containsAlias(s2)) {
            throw new Exception(new MessageFormat(Main.rb.getString("Destination.alias.dest.already.exists")).format(new Object[] { s2 }));
        }
        final Pair<KeyStore.Entry, char[]> recoverEntry = this.recoverEntry(this.keyStore, s, this.storePass, this.keyPass);
        final KeyStore.Entry entry = recoverEntry.fst;
        this.keyPass = recoverEntry.snd;
        KeyStore.ProtectionParameter protectionParameter = null;
        if (this.keyPass != null) {
            if (!b || "PKCS12".equalsIgnoreCase(this.storetype)) {
                this.keyPassNew = this.keyPass;
            }
            else if (this.keyPassNew == null) {
                this.keyPassNew = this.promptForKeyPass(s2, s, this.keyPass);
            }
            protectionParameter = new KeyStore.PasswordProtection(this.keyPassNew);
        }
        this.keyStore.setEntry(s2, entry, protectionParameter);
    }
    
    private void doChangeKeyPasswd(String s) throws Exception {
        if (s == null) {
            s = "mykey";
        }
        final Pair<Key, char[]> recoverKey = this.recoverKey(s, this.storePass, this.keyPass);
        final Key key = recoverKey.fst;
        if (this.keyPass == null) {
            this.keyPass = recoverKey.snd;
        }
        if (this.keyPassNew == null) {
            this.keyPassNew = this.getNewPasswd(new MessageFormat(Main.rb.getString("key.password.for.alias.")).format(new Object[] { s }), this.keyPass);
        }
        this.keyStore.setKeyEntry(s, key, this.keyPassNew, this.keyStore.getCertificateChain(s));
    }
    
    private void doImportIdentityDatabase(final InputStream inputStream) throws Exception {
        System.err.println(Main.rb.getString("No.entries.from.identity.database.added"));
    }
    
    private void doPrintEntry(final String s, final String s2, final PrintStream printStream) throws Exception {
        if (!this.keyStore.containsAlias(s2)) {
            throw new Exception(new MessageFormat(Main.rb.getString("Alias.alias.does.not.exist")).format(new Object[] { s2 }));
        }
        if (this.verbose || this.rfc || this.debug) {
            printStream.println(new MessageFormat(Main.rb.getString("Alias.name.alias")).format(new Object[] { s2 }));
            if (!this.token) {
                printStream.println(new MessageFormat(Main.rb.getString("Creation.date.keyStore.getCreationDate.alias.")).format(new Object[] { this.keyStore.getCreationDate(s2) }));
            }
        }
        else if (!this.token) {
            printStream.print(new MessageFormat(Main.rb.getString("alias.keyStore.getCreationDate.alias.")).format(new Object[] { s2, this.keyStore.getCreationDate(s2) }));
        }
        else {
            printStream.print(new MessageFormat(Main.rb.getString("alias.")).format(new Object[] { s2 }));
        }
        if (this.keyStore.entryInstanceOf(s2, KeyStore.SecretKeyEntry.class)) {
            if (this.verbose || this.rfc || this.debug) {
                printStream.println(new MessageFormat(Main.rb.getString("Entry.type.type.")).format(new Object[] { "SecretKeyEntry" }));
            }
            else {
                printStream.println("SecretKeyEntry, ");
            }
        }
        else if (this.keyStore.entryInstanceOf(s2, KeyStore.PrivateKeyEntry.class)) {
            if (this.verbose || this.rfc || this.debug) {
                printStream.println(new MessageFormat(Main.rb.getString("Entry.type.type.")).format(new Object[] { "PrivateKeyEntry" }));
            }
            else {
                printStream.println("PrivateKeyEntry, ");
            }
            final Certificate[] certificateChain = this.keyStore.getCertificateChain(s2);
            if (certificateChain != null) {
                if (this.verbose || this.rfc || this.debug) {
                    printStream.println(Main.rb.getString("Certificate.chain.length.") + certificateChain.length);
                    for (int i = 0; i < certificateChain.length; ++i) {
                        printStream.println(new MessageFormat(Main.rb.getString("Certificate.i.1.")).format(new Object[] { new Integer(i + 1) }));
                        if (this.verbose && certificateChain[i] instanceof X509Certificate) {
                            this.printX509Cert((X509Certificate)certificateChain[i], printStream);
                        }
                        else if (this.debug) {
                            printStream.println(certificateChain[i].toString());
                        }
                        else {
                            this.dumpCert(certificateChain[i], printStream);
                        }
                        this.checkWeak(s, certificateChain[i]);
                    }
                }
                else {
                    printStream.println(Main.rb.getString("Certificate.fingerprint.SHA1.") + this.getCertFingerPrint("SHA1", certificateChain[0]));
                    this.checkWeak(s, certificateChain[0]);
                }
            }
        }
        else if (this.keyStore.entryInstanceOf(s2, KeyStore.TrustedCertificateEntry.class)) {
            final Certificate certificate = this.keyStore.getCertificate(s2);
            final String string = new MessageFormat(Main.rb.getString("Entry.type.type.")).format(new Object[] { "trustedCertEntry" }) + "\n";
            if (this.verbose && certificate instanceof X509Certificate) {
                printStream.println(string);
                this.printX509Cert((X509Certificate)certificate, printStream);
            }
            else if (this.rfc) {
                printStream.println(string);
                this.dumpCert(certificate, printStream);
            }
            else if (this.debug) {
                printStream.println(certificate.toString());
            }
            else {
                printStream.println("trustedCertEntry, ");
                printStream.println(Main.rb.getString("Certificate.fingerprint.SHA1.") + this.getCertFingerPrint("SHA1", certificate));
            }
            this.checkWeak(s, certificate);
        }
        else {
            printStream.println(Main.rb.getString("Unknown.Entry.Type"));
        }
    }
    
    boolean inplaceImportCheck() throws Exception {
        if ("PKCS11".equalsIgnoreCase(this.srcstoretype) || KeyStoreUtil.isWindowsKeyStore(this.srcstoretype)) {
            return false;
        }
        if (this.srcksfname == null) {
            throw new Exception(Main.rb.getString("Please.specify.srckeystore"));
        }
        final File file = new File(this.srcksfname);
        if (file.exists() && file.length() == 0L) {
            throw new Exception(Main.rb.getString("Source.keystore.file.exists.but.is.empty.") + this.srcksfname);
        }
        if (file.getCanonicalFile().equals(new File(this.ksfname).getCanonicalFile())) {
            return true;
        }
        System.err.println(String.format(Main.rb.getString("importing.keystore.status"), this.srcksfname, this.ksfname));
        return false;
    }
    
    KeyStore loadSourceKeyStore() throws Exception {
        InputStream inputStream = null;
        if ("PKCS11".equalsIgnoreCase(this.srcstoretype) || KeyStoreUtil.isWindowsKeyStore(this.srcstoretype)) {
            if (!"NONE".equals(this.srcksfname)) {
                System.err.println(MessageFormat.format(Main.rb.getString(".keystore.must.be.NONE.if.storetype.is.{0}"), this.srcstoretype));
                System.err.println();
                this.tinyHelp();
            }
        }
        else {
            inputStream = new FileInputStream(new File(this.srcksfname));
        }
        KeyStore keyStore;
        try {
            if (this.srcstoretype == null) {
                this.srcstoretype = KeyStore.getDefaultType();
            }
            if (this.srcProviderName == null) {
                keyStore = KeyStore.getInstance(this.srcstoretype);
            }
            else {
                keyStore = KeyStore.getInstance(this.srcstoretype, this.srcProviderName);
            }
            if (this.srcstorePass == null && !this.srcprotectedPath && !KeyStoreUtil.isWindowsKeyStore(this.srcstoretype)) {
                System.err.print(Main.rb.getString("Enter.source.keystore.password."));
                System.err.flush();
                this.srcstorePass = Password.readPassword(System.in);
                this.passwords.add(this.srcstorePass);
            }
            if ("PKCS12".equalsIgnoreCase(this.srcstoretype) && this.srckeyPass != null && this.srcstorePass != null && !Arrays.equals(this.srcstorePass, this.srckeyPass)) {
                System.err.println(new MessageFormat(Main.rb.getString("Warning.Different.store.and.key.passwords.not.supported.for.PKCS12.KeyStores.Ignoring.user.specified.command.value.")).format(new Object[] { "-srckeypass" }));
                this.srckeyPass = this.srcstorePass;
            }
            keyStore.load(inputStream, this.srcstorePass);
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        if (this.srcstorePass == null && !KeyStoreUtil.isWindowsKeyStore(this.srcstoretype)) {
            System.err.println();
            System.err.println(Main.rb.getString(".WARNING.WARNING.WARNING."));
            System.err.println(Main.rb.getString(".The.integrity.of.the.information.stored.in.the.srckeystore."));
            System.err.println(Main.rb.getString(".WARNING.WARNING.WARNING."));
            System.err.println();
        }
        return keyStore;
    }
    
    private void doImportKeyStore(final KeyStore keyStore) throws Exception {
        if (this.alias != null) {
            this.doImportKeyStoreSingle(keyStore, this.alias);
        }
        else {
            if (this.dest != null || this.srckeyPass != null) {
                throw new Exception(Main.rb.getString("if.alias.not.specified.destalias.and.srckeypass.must.not.be.specified"));
            }
            this.doImportKeyStoreAll(keyStore);
        }
        if (this.inplaceImport) {
            int n = 1;
            File file;
            while (true) {
                this.inplaceBackupName = this.srcksfname + ".old" + ((n == 1) ? "" : Integer.valueOf(n));
                file = new File(this.inplaceBackupName);
                if (!file.exists()) {
                    break;
                }
                ++n;
            }
            Files.copy(Paths.get(this.srcksfname, new String[0]), file.toPath(), new CopyOption[0]);
        }
    }
    
    private int doImportKeyStoreSingle(final KeyStore keyStore, final String s) throws Exception {
        String inputStringFromStdin = (this.dest == null) ? s : this.dest;
        if (this.keyStore.containsAlias(inputStringFromStdin)) {
            final Object[] array = { s };
            if (this.noprompt) {
                System.err.println(new MessageFormat(Main.rb.getString("Warning.Overwriting.existing.alias.alias.in.destination.keystore")).format(array));
            }
            else if ("NO".equals(this.getYesNoReply(new MessageFormat(Main.rb.getString("Existing.entry.alias.alias.exists.overwrite.no.")).format(array)))) {
                inputStringFromStdin = this.inputStringFromStdin(Main.rb.getString("Enter.new.alias.name.RETURN.to.cancel.import.for.this.entry."));
                if ("".equals(inputStringFromStdin)) {
                    System.err.println(new MessageFormat(Main.rb.getString("Entry.for.alias.alias.not.imported.")).format(array));
                    return 0;
                }
            }
        }
        final Pair<KeyStore.Entry, char[]> recoverEntry = this.recoverEntry(keyStore, s, this.srcstorePass, this.srckeyPass);
        final KeyStore.Entry entry = recoverEntry.fst;
        KeyStore.ProtectionParameter protectionParameter = null;
        char[] destKeyPass = null;
        if (this.destKeyPass != null) {
            destKeyPass = this.destKeyPass;
            protectionParameter = new KeyStore.PasswordProtection(this.destKeyPass);
        }
        else if (recoverEntry.snd != null) {
            destKeyPass = recoverEntry.snd;
            protectionParameter = new KeyStore.PasswordProtection(recoverEntry.snd);
        }
        try {
            final Certificate certificate = keyStore.getCertificate(s);
            if (certificate != null) {
                this.checkWeak("<" + inputStringFromStdin + ">", certificate);
            }
            this.keyStore.setEntry(inputStringFromStdin, entry, protectionParameter);
            if ("PKCS12".equalsIgnoreCase(this.storetype) && destKeyPass != null && !Arrays.equals(destKeyPass, this.storePass)) {
                throw new Exception(Main.rb.getString("The.destination.pkcs12.keystore.has.different.storepass.and.keypass.Please.retry.with.destkeypass.specified."));
            }
            return 1;
        }
        catch (final KeyStoreException ex) {
            System.err.println(new MessageFormat(Main.rb.getString("Problem.importing.entry.for.alias.alias.exception.Entry.for.alias.alias.not.imported.")).format(new Object[] { s, ex.toString() }));
            return 2;
        }
    }
    
    private void doImportKeyStoreAll(final KeyStore keyStore) throws Exception {
        int n = 0;
        final int size = keyStore.size();
        final Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            final String s = aliases.nextElement();
            final int doImportKeyStoreSingle = this.doImportKeyStoreSingle(keyStore, s);
            if (doImportKeyStoreSingle == 1) {
                ++n;
                System.err.println(new MessageFormat(Main.rb.getString("Entry.for.alias.alias.successfully.imported.")).format(new Object[] { s }));
            }
            else {
                if (doImportKeyStoreSingle == 2 && !this.noprompt && "YES".equals(this.getYesNoReply("Do you want to quit the import process? [no]:  "))) {
                    break;
                }
                continue;
            }
        }
        System.err.println(new MessageFormat(Main.rb.getString("Import.command.completed.ok.entries.successfully.imported.fail.entries.failed.or.cancelled")).format(new Object[] { n, size - n }));
    }
    
    private void doPrintEntries(final PrintStream printStream) throws Exception {
        String type = this.keyStore.getType();
        if ("JKS".equalsIgnoreCase(type) && this.ksfile != null && this.ksfile.exists() && !"JKS".equalsIgnoreCase(this.keyStoreType(this.ksfile))) {
            type = "PKCS12";
        }
        printStream.println(Main.rb.getString("Keystore.type.") + type);
        printStream.println(Main.rb.getString("Keystore.provider.") + this.keyStore.getProvider().getName());
        printStream.println();
        printStream.println(((this.keyStore.size() == 1) ? new MessageFormat(Main.rb.getString("Your.keystore.contains.keyStore.size.entry")) : new MessageFormat(Main.rb.getString("Your.keystore.contains.keyStore.size.entries"))).format(new Object[] { new Integer(this.keyStore.size()) }));
        printStream.println();
        final ArrayList<String> list = Collections.list(this.keyStore.aliases());
        list.sort(String::compareTo);
        for (final String s : list) {
            this.doPrintEntry("<" + s + ">", s, printStream);
            if (this.verbose || this.rfc) {
                printStream.println(Main.rb.getString("NEWLINE"));
                printStream.println(Main.rb.getString("STAR"));
                printStream.println(Main.rb.getString("STARNN"));
            }
        }
    }
    
    private static <T> Iterable<T> e2i(final Enumeration<T> enumeration) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return enumeration.hasMoreElements();
                    }
                    
                    @Override
                    public T next() {
                        return enumeration.nextElement();
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                };
            }
        };
    }
    
    public static Collection<? extends CRL> loadCRLs(final String s) throws Exception {
        InputStream inputStream = null;
        URI uri = null;
        if (s == null) {
            inputStream = System.in;
        }
        else {
            try {
                uri = new URI(s);
                if (!uri.getScheme().equals("ldap")) {
                    inputStream = uri.toURL().openStream();
                }
            }
            catch (final Exception ex) {
                try {
                    inputStream = new FileInputStream(s);
                }
                catch (final Exception ex2) {
                    if (uri == null || uri.getScheme() == null) {
                        throw ex2;
                    }
                    throw ex;
                }
            }
        }
        if (inputStream != null) {
            try {
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                final byte[] array = new byte[4096];
                while (true) {
                    final int read = inputStream.read(array);
                    if (read < 0) {
                        break;
                    }
                    byteArrayOutputStream.write(array, 0, read);
                }
                return CertificateFactory.getInstance("X509").generateCRLs(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            }
            finally {
                if (inputStream != System.in) {
                    inputStream.close();
                }
            }
        }
        final CertStoreHelper instance = CertStoreHelper.getInstance("LDAP");
        String s2 = uri.getPath();
        if (s2.charAt(0) == '/') {
            s2 = s2.substring(1);
        }
        return instance.getCertStore(uri).getCRLs(instance.wrap(new X509CRLSelector(), null, s2));
    }
    
    public static List<CRL> readCRLsFromCert(final X509Certificate x509Certificate) throws Exception {
        final ArrayList list = new ArrayList();
        final CRLDistributionPointsExtension crlDistributionPointsExtension = X509CertImpl.toImpl(x509Certificate).getCRLDistributionPointsExtension();
        if (crlDistributionPointsExtension == null) {
            return list;
        }
        final Iterator<DistributionPoint> iterator = crlDistributionPointsExtension.get("points").iterator();
        while (iterator.hasNext()) {
            final GeneralNames fullName = iterator.next().getFullName();
            if (fullName != null) {
                for (final GeneralName generalName : fullName.names()) {
                    if (generalName.getType() == 6) {
                        for (final CRL crl : loadCRLs(((URIName)generalName.getName()).getName())) {
                            if (crl instanceof X509CRL) {
                                list.add(crl);
                            }
                        }
                        break;
                    }
                }
            }
        }
        return list;
    }
    
    private static String verifyCRL(final KeyStore keyStore, final CRL crl) throws Exception {
        final X500Principal issuerX500Principal = ((X509CRLImpl)crl).getIssuerX500Principal();
        for (final String s : e2i(keyStore.aliases())) {
            final Certificate certificate = keyStore.getCertificate(s);
            if (certificate instanceof X509Certificate && ((X509Certificate)certificate).getSubjectX500Principal().equals(issuerX500Principal)) {
                try {
                    ((X509CRLImpl)crl).verify(certificate.getPublicKey());
                    return s;
                }
                catch (final Exception ex) {}
            }
        }
        return null;
    }
    
    private void doPrintCRL(final String s, final PrintStream printStream) throws Exception {
        for (final CRL crl : loadCRLs(s)) {
            this.printCRL(crl, printStream);
            String s2 = null;
            Certificate certificate = null;
            if (this.caks != null) {
                s2 = verifyCRL(this.caks, crl);
                if (s2 != null) {
                    certificate = this.caks.getCertificate(s2);
                    printStream.printf(Main.rb.getString("verified.by.s.in.s.weak"), s2, "cacerts", this.withWeak(certificate.getPublicKey()));
                    printStream.println();
                }
            }
            if (s2 == null && this.keyStore != null) {
                s2 = verifyCRL(this.keyStore, crl);
                if (s2 != null) {
                    certificate = this.keyStore.getCertificate(s2);
                    printStream.printf(Main.rb.getString("verified.by.s.in.s.weak"), s2, "keystore", this.withWeak(certificate.getPublicKey()));
                    printStream.println();
                }
            }
            if (s2 == null) {
                printStream.println(Main.rb.getString("STAR"));
                printStream.println(Main.rb.getString("warning.not.verified.make.sure.keystore.is.correct"));
                printStream.println(Main.rb.getString("STARNN"));
            }
            this.checkWeak(Main.rb.getString("the.crl"), crl, (certificate == null) ? null : certificate.getPublicKey());
        }
    }
    
    private void printCRL(final CRL crl, final PrintStream printStream) throws Exception {
        final X509CRL x509CRL = (X509CRL)crl;
        if (this.rfc) {
            printStream.println("-----BEGIN X509 CRL-----");
            printStream.println(Base64.getMimeEncoder(64, Main.CRLF).encodeToString(x509CRL.getEncoded()));
            printStream.println("-----END X509 CRL-----");
        }
        else {
            String s;
            if (crl instanceof X509CRLImpl) {
                final X509CRLImpl x509CRLImpl = (X509CRLImpl)crl;
                s = x509CRLImpl.toStringWithAlgName(this.withWeak("" + x509CRLImpl.getSigAlgId()));
            }
            else {
                s = crl.toString();
            }
            printStream.println(s);
        }
    }
    
    private void doPrintCertReq(final InputStream inputStream, final PrintStream printStream) throws Exception {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuffer sb = new StringBuffer();
        int n = 0;
        while (true) {
            final String line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            if (n == 0) {
                if (!line.startsWith("-----")) {
                    continue;
                }
                n = 1;
            }
            else {
                if (line.startsWith("-----")) {
                    break;
                }
                sb.append(line);
            }
        }
        final PKCS10 pkcs10 = new PKCS10(Pem.decode(new String(sb)));
        final PublicKey subjectPublicKeyInfo = pkcs10.getSubjectPublicKeyInfo();
        printStream.printf(Main.rb.getString("PKCS.10.with.weak"), pkcs10.getSubjectName(), subjectPublicKeyInfo.getFormat(), this.withWeak(subjectPublicKeyInfo), this.withWeak(pkcs10.getSigAlg()));
        for (final PKCS10Attribute pkcs10Attribute : pkcs10.getAttributes().getAttributes()) {
            if (pkcs10Attribute.getAttributeId().equals((Object)PKCS9Attribute.EXTENSION_REQUEST_OID)) {
                final CertificateExtensions certificateExtensions = (CertificateExtensions)pkcs10Attribute.getAttributeValue();
                if (certificateExtensions == null) {
                    continue;
                }
                printExtensions(Main.rb.getString("Extension.Request."), certificateExtensions, printStream);
            }
            else {
                printStream.println("Attribute: " + pkcs10Attribute.getAttributeId());
                printStream.print(new PKCS9Attribute(pkcs10Attribute.getAttributeId(), pkcs10Attribute.getAttributeValue()).getName() + ": ");
                final Object attributeValue = pkcs10Attribute.getAttributeValue();
                printStream.println((Object)((attributeValue instanceof String[]) ? Arrays.toString((Object[])attributeValue) : attributeValue));
            }
        }
        if (this.debug) {
            printStream.println(pkcs10);
        }
        this.checkWeak(Main.rb.getString("the.certificate.request"), pkcs10);
    }
    
    private void printCertFromStream(final InputStream inputStream, final PrintStream printStream) throws Exception {
        Collection<? extends Certificate> generateCertificates;
        try {
            generateCertificates = this.cf.generateCertificates(inputStream);
        }
        catch (final CertificateException ex) {
            throw new Exception(Main.rb.getString("Failed.to.parse.input"), ex);
        }
        if (generateCertificates.isEmpty()) {
            throw new Exception(Main.rb.getString("Empty.input"));
        }
        final Certificate[] array = generateCertificates.toArray(new Certificate[generateCertificates.size()]);
        for (int i = 0; i < array.length; ++i) {
            X509Certificate x509Certificate;
            try {
                x509Certificate = (X509Certificate)array[i];
            }
            catch (final ClassCastException ex2) {
                throw new Exception(Main.rb.getString("Not.X.509.certificate"));
            }
            if (array.length > 1) {
                printStream.println(new MessageFormat(Main.rb.getString("Certificate.i.1.")).format(new Object[] { new Integer(i + 1) }));
            }
            if (this.rfc) {
                this.dumpCert(x509Certificate, printStream);
            }
            else {
                this.printX509Cert(x509Certificate, printStream);
            }
            if (i < array.length - 1) {
                printStream.println();
            }
            this.checkWeak(oneInMany(Main.rb.getString("the.certificate"), i, array.length), x509Certificate);
        }
    }
    
    private static String oneInMany(final String s, final int n, final int n2) {
        if (n2 == 1) {
            return s;
        }
        return String.format(Main.rb.getString("one.in.many"), s, n + 1, n2);
    }
    
    private void doPrintCert(final PrintStream printStream) throws Exception {
        if (this.jarfile != null) {
            final JarFile jarFile = new JarFile(this.jarfile, true);
            final Enumeration<JarEntry> entries = jarFile.entries();
            final HashSet set = new HashSet();
            final byte[] array = new byte[8192];
            int n = 0;
            while (entries.hasMoreElements()) {
                final JarEntry jarEntry = entries.nextElement();
                try (final InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                    while (inputStream.read(array) != -1) {}
                }
                final CodeSigner[] codeSigners = jarEntry.getCodeSigners();
                if (codeSigners != null) {
                    for (final CodeSigner codeSigner : codeSigners) {
                        if (!set.contains(codeSigner)) {
                            set.add(codeSigner);
                            printStream.printf(Main.rb.getString("Signer.d."), ++n);
                            printStream.println();
                            printStream.println();
                            printStream.println(Main.rb.getString("Signature."));
                            printStream.println();
                            final List<? extends Certificate> certificates = codeSigner.getSignerCertPath().getCertificates();
                            int n2 = 0;
                            for (final X509Certificate x509Certificate : certificates) {
                                if (this.rfc) {
                                    printStream.println(Main.rb.getString("Certificate.owner.") + x509Certificate.getSubjectDN() + "\n");
                                    this.dumpCert(x509Certificate, printStream);
                                }
                                else {
                                    this.printX509Cert(x509Certificate, printStream);
                                }
                                printStream.println();
                                this.checkWeak(oneInMany(Main.rb.getString("the.certificate"), n2++, certificates.size()), x509Certificate);
                            }
                            final Timestamp timestamp = codeSigner.getTimestamp();
                            if (timestamp != null) {
                                printStream.println(Main.rb.getString("Timestamp."));
                                printStream.println();
                                final List<? extends Certificate> certificates2 = timestamp.getSignerCertPath().getCertificates();
                                int n3 = 0;
                                for (final X509Certificate x509Certificate2 : certificates2) {
                                    if (this.rfc) {
                                        printStream.println(Main.rb.getString("Certificate.owner.") + x509Certificate2.getSubjectDN() + "\n");
                                        this.dumpCert(x509Certificate2, printStream);
                                    }
                                    else {
                                        this.printX509Cert(x509Certificate2, printStream);
                                    }
                                    printStream.println();
                                    this.checkWeak(oneInMany(Main.rb.getString("the.tsa.certificate"), n3++, certificates2.size()), x509Certificate2);
                                }
                            }
                        }
                    }
                }
            }
            jarFile.close();
            if (set.isEmpty()) {
                printStream.println(Main.rb.getString("Not.a.signed.jar.file"));
            }
        }
        else if (this.sslserver != null) {
            final CertStore certStore = CertStoreHelper.getInstance("SSLServer").getCertStore(new URI("https://" + this.sslserver));
            Collection<? extends Certificate> certificates3;
            try {
                certificates3 = certStore.getCertificates(null);
                if (certificates3.isEmpty()) {
                    throw new Exception(Main.rb.getString("No.certificate.from.the.SSL.server"));
                }
            }
            catch (final CertStoreException ex) {
                if (ex.getCause() instanceof IOException) {
                    throw new Exception(Main.rb.getString("No.certificate.from.the.SSL.server"), ex.getCause());
                }
                throw ex;
            }
            int n4 = 0;
            for (final Certificate certificate : certificates3) {
                try {
                    if (this.rfc) {
                        this.dumpCert(certificate, printStream);
                    }
                    else {
                        printStream.println("Certificate #" + n4);
                        printStream.println("====================================");
                        this.printX509Cert((X509Certificate)certificate, printStream);
                        printStream.println();
                    }
                    this.checkWeak(oneInMany(Main.rb.getString("the.certificate"), n4++, certificates3.size()), certificate);
                }
                catch (final Exception ex2) {
                    if (!this.debug) {
                        continue;
                    }
                    ex2.printStackTrace();
                }
            }
        }
        else if (this.filename != null) {
            try (final FileInputStream fileInputStream = new FileInputStream(this.filename)) {
                this.printCertFromStream(fileInputStream, printStream);
            }
        }
        else {
            this.printCertFromStream(System.in, printStream);
        }
    }
    
    private void doSelfCert(String s, final String s2, String compatibleSigAlgName) throws Exception {
        if (s == null) {
            s = "mykey";
        }
        final Pair<Key, char[]> recoverKey = this.recoverKey(s, this.storePass, this.keyPass);
        final PrivateKey privateKey = (PrivateKey)recoverKey.fst;
        if (this.keyPass == null) {
            this.keyPass = recoverKey.snd;
        }
        if (compatibleSigAlgName == null) {
            compatibleSigAlgName = getCompatibleSigAlgName(privateKey.getAlgorithm());
        }
        final Certificate certificate = this.keyStore.getCertificate(s);
        if (certificate == null) {
            throw new Exception(new MessageFormat(Main.rb.getString("alias.has.no.public.key")).format(new Object[] { s }));
        }
        if (!(certificate instanceof X509Certificate)) {
            throw new Exception(new MessageFormat(Main.rb.getString("alias.has.no.X.509.certificate")).format(new Object[] { s }));
        }
        final X509CertInfo x509CertInfo = (X509CertInfo)new X509CertImpl(certificate.getEncoded()).get("x509.info");
        final Date startDate = getStartDate(this.startDate);
        final Date date = new Date();
        date.setTime(startDate.getTime() + this.validity * 1000L * 24L * 60L * 60L);
        x509CertInfo.set("validity", new CertificateValidity(startDate, date));
        x509CertInfo.set("serialNumber", new CertificateSerialNumber(new Random().nextInt() & Integer.MAX_VALUE));
        Object o;
        if (s2 == null) {
            o = x509CertInfo.get("subject.dname");
        }
        else {
            o = new X500Name(s2);
            x509CertInfo.set("subject.dname", o);
        }
        x509CertInfo.set("issuer.dname", o);
        final X509CertImpl x509CertImpl = new X509CertImpl(x509CertInfo);
        final PSSParameterSpec defaultAlgorithmParameterSpec = AlgorithmId.getDefaultAlgorithmParameterSpec(compatibleSigAlgName, privateKey);
        x509CertImpl.sign(privateKey, defaultAlgorithmParameterSpec, compatibleSigAlgName, null);
        x509CertInfo.set("algorithmID.algorithm", x509CertImpl.get("x509.algorithm"));
        x509CertInfo.set("version", new CertificateVersion(2));
        x509CertInfo.set("extensions", this.createV3Extensions(null, (CertificateExtensions)x509CertInfo.get("extensions"), this.v3ext, certificate.getPublicKey(), null));
        final X509CertImpl x509CertImpl2 = new X509CertImpl(x509CertInfo);
        x509CertImpl2.sign(privateKey, defaultAlgorithmParameterSpec, compatibleSigAlgName, null);
        this.keyStore.setKeyEntry(s, privateKey, (this.keyPass != null) ? this.keyPass : this.storePass, new Certificate[] { x509CertImpl2 });
        if (this.verbose) {
            System.err.println(Main.rb.getString("New.certificate.self.signed."));
            System.err.print(x509CertImpl2.toString());
            System.err.println();
        }
    }
    
    private boolean installReply(String s, final InputStream inputStream) throws Exception {
        if (s == null) {
            s = "mykey";
        }
        final Pair<Key, char[]> recoverKey = this.recoverKey(s, this.storePass, this.keyPass);
        final PrivateKey privateKey = (PrivateKey)recoverKey.fst;
        if (this.keyPass == null) {
            this.keyPass = recoverKey.snd;
        }
        final Certificate certificate = this.keyStore.getCertificate(s);
        if (certificate == null) {
            throw new Exception(new MessageFormat(Main.rb.getString("alias.has.no.public.key.certificate.")).format(new Object[] { s }));
        }
        final Collection<? extends Certificate> generateCertificates = this.cf.generateCertificates(inputStream);
        if (generateCertificates.isEmpty()) {
            throw new Exception(Main.rb.getString("Reply.has.no.certificates"));
        }
        final Certificate[] array = generateCertificates.toArray(new Certificate[generateCertificates.size()]);
        Certificate[] array2;
        if (array.length == 1) {
            array2 = this.establishCertChain(certificate, array[0]);
        }
        else {
            array2 = this.validateReply(s, certificate, array);
        }
        if (array2 != null) {
            this.keyStore.setKeyEntry(s, privateKey, (this.keyPass != null) ? this.keyPass : this.storePass, array2);
            return true;
        }
        return false;
    }
    
    private boolean addTrustedCert(final String s, final InputStream inputStream) throws Exception {
        if (s == null) {
            throw new Exception(Main.rb.getString("Must.specify.alias"));
        }
        if (this.keyStore.containsAlias(s)) {
            throw new Exception(new MessageFormat(Main.rb.getString("Certificate.not.imported.alias.alias.already.exists")).format(new Object[] { s }));
        }
        X509Certificate x509Certificate;
        try {
            x509Certificate = (X509Certificate)this.cf.generateCertificate(inputStream);
        }
        catch (final ClassCastException | CertificateException ex) {
            throw new Exception(Main.rb.getString("Input.not.an.X.509.certificate"));
        }
        if (this.noprompt) {
            this.checkWeak(Main.rb.getString("the.input"), x509Certificate);
            this.keyStore.setCertificateEntry(s, x509Certificate);
            return true;
        }
        boolean b = false;
        if (KeyStoreUtil.isSelfSigned(x509Certificate)) {
            x509Certificate.verify(x509Certificate.getPublicKey());
            b = true;
        }
        Object o = null;
        String s2 = this.keyStore.getCertificateAlias(x509Certificate);
        if (s2 != null) {
            System.err.println(new MessageFormat(Main.rb.getString("Certificate.already.exists.in.keystore.under.alias.trustalias.")).format(new Object[] { s2 }));
            this.checkWeak(Main.rb.getString("the.input"), x509Certificate);
            this.printWeakWarnings(true);
            o = this.getYesNoReply(Main.rb.getString("Do.you.still.want.to.add.it.no."));
        }
        else if (b) {
            if (this.trustcacerts && this.caks != null && (s2 = this.caks.getCertificateAlias(x509Certificate)) != null) {
                System.err.println(new MessageFormat(Main.rb.getString("Certificate.already.exists.in.system.wide.CA.keystore.under.alias.trustalias.")).format(new Object[] { s2 }));
                this.checkWeak(Main.rb.getString("the.input"), x509Certificate);
                this.printWeakWarnings(true);
                o = this.getYesNoReply(Main.rb.getString("Do.you.still.want.to.add.it.to.your.own.keystore.no."));
            }
            if (s2 == null) {
                this.printX509Cert(x509Certificate, System.out);
                this.checkWeak(Main.rb.getString("the.input"), x509Certificate);
                this.printWeakWarnings(true);
                o = this.getYesNoReply(Main.rb.getString("Trust.this.certificate.no."));
            }
        }
        if (o == null) {
            try {
                if (this.establishCertChain(null, x509Certificate) != null) {
                    this.keyStore.setCertificateEntry(s, x509Certificate);
                    return true;
                }
            }
            catch (final Exception ex2) {
                this.printX509Cert(x509Certificate, System.out);
                this.checkWeak(Main.rb.getString("the.input"), x509Certificate);
                this.printWeakWarnings(true);
                if ("YES".equals(this.getYesNoReply(Main.rb.getString("Trust.this.certificate.no.")))) {
                    this.keyStore.setCertificateEntry(s, x509Certificate);
                    return true;
                }
                return false;
            }
            return false;
        }
        if ("YES".equals(o)) {
            this.keyStore.setCertificateEntry(s, x509Certificate);
            return true;
        }
        return false;
    }
    
    private char[] getNewPasswd(final String s, final char[] array) throws Exception {
        char[] password = null;
        for (int i = 0; i < 3; ++i) {
            System.err.print(new MessageFormat(Main.rb.getString("New.prompt.")).format(new Object[] { s }));
            final char[] password2 = Password.readPassword(System.in);
            this.passwords.add(password2);
            if (password2 == null || password2.length < 6) {
                System.err.println(Main.rb.getString("Password.is.too.short.must.be.at.least.6.characters"));
            }
            else if (Arrays.equals(password2, array)) {
                System.err.println(Main.rb.getString("Passwords.must.differ"));
            }
            else {
                System.err.print(new MessageFormat(Main.rb.getString("Re.enter.new.prompt.")).format(new Object[] { s }));
                password = Password.readPassword(System.in);
                this.passwords.add(password);
                if (Arrays.equals(password2, password)) {
                    Arrays.fill(password, ' ');
                    return password2;
                }
                System.err.println(Main.rb.getString("They.don.t.match.Try.again"));
            }
            if (password2 != null) {
                Arrays.fill(password2, ' ');
            }
            if (password != null) {
                Arrays.fill(password, ' ');
                password = null;
            }
        }
        throw new Exception(Main.rb.getString("Too.many.failures.try.later"));
    }
    
    private String getAlias(final String s) throws Exception {
        if (s != null) {
            System.err.print(new MessageFormat(Main.rb.getString("Enter.prompt.alias.name.")).format(new Object[] { s }));
        }
        else {
            System.err.print(Main.rb.getString("Enter.alias.name."));
        }
        return new BufferedReader(new InputStreamReader(System.in)).readLine();
    }
    
    private String inputStringFromStdin(final String s) throws Exception {
        System.err.print(s);
        return new BufferedReader(new InputStreamReader(System.in)).readLine();
    }
    
    private char[] getKeyPasswd(final String s, final String s2, final char[] array) throws Exception {
        int n = 0;
        char[] password;
        do {
            if (array != null) {
                System.err.println(new MessageFormat(Main.rb.getString("Enter.key.password.for.alias.")).format(new Object[] { s }));
                System.err.print(new MessageFormat(Main.rb.getString(".RETURN.if.same.as.for.otherAlias.")).format(new Object[] { s2 }));
            }
            else {
                System.err.print(new MessageFormat(Main.rb.getString("Enter.key.password.for.alias.")).format(new Object[] { s }));
            }
            System.err.flush();
            password = Password.readPassword(System.in);
            this.passwords.add(password);
            if (password == null) {
                password = array;
            }
            ++n;
        } while (password == null && n < 3);
        if (password == null) {
            throw new Exception(Main.rb.getString("Too.many.failures.try.later"));
        }
        return password;
    }
    
    private String withWeak(final String s) {
        if (Main.DISABLED_CHECK.permits(Main.SIG_PRIMITIVE_SET, s, null)) {
            return s;
        }
        return String.format(Main.rb.getString("with.weak"), s);
    }
    
    private String fullDisplayAlgName(final Key key) {
        String s = key.getAlgorithm();
        if (key instanceof ECKey) {
            final ECParameterSpec params = ((ECKey)key).getParams();
            if (params instanceof NamedCurve) {
                s = s + " (" + params.toString().split(" ")[0] + ")";
            }
        }
        return s;
    }
    
    private String withWeak(final PublicKey publicKey) {
        if (Main.DISABLED_CHECK.permits(Main.SIG_PRIMITIVE_SET, publicKey)) {
            return String.format(Main.rb.getString("key.bit"), KeyUtil.getKeySize(publicKey), publicKey.getAlgorithm());
        }
        return String.format(Main.rb.getString("key.bit.weak"), KeyUtil.getKeySize(publicKey), publicKey.getAlgorithm());
    }
    
    private void printX509Cert(final X509Certificate x509Certificate, final PrintStream printStream) throws Exception {
        final MessageFormat messageFormat = new MessageFormat(Main.rb.getString(".PATTERN.printX509Cert.with.weak"));
        final PublicKey publicKey = x509Certificate.getPublicKey();
        String s = x509Certificate.getSigAlgName();
        if (!this.isTrustedCert(x509Certificate)) {
            s = this.withWeak(s);
        }
        printStream.println(messageFormat.format(new Object[] { x509Certificate.getSubjectDN().toString(), x509Certificate.getIssuerDN().toString(), x509Certificate.getSerialNumber().toString(16), x509Certificate.getNotBefore().toString(), x509Certificate.getNotAfter().toString(), this.getCertFingerPrint("MD5", x509Certificate), this.getCertFingerPrint("SHA1", x509Certificate), this.getCertFingerPrint("SHA-256", x509Certificate), s, this.withWeak(publicKey), x509Certificate.getVersion() }));
        if (x509Certificate instanceof X509CertImpl) {
            final CertificateExtensions certificateExtensions = (CertificateExtensions)((X509CertInfo)((X509CertImpl)x509Certificate).get("x509.info")).get("extensions");
            if (certificateExtensions != null) {
                printExtensions(Main.rb.getString("Extensions."), certificateExtensions, printStream);
            }
        }
    }
    
    private static void printExtensions(final String s, final CertificateExtensions certificateExtensions, final PrintStream printStream) throws Exception {
        int n = 0;
        final Iterator<Extension> iterator = certificateExtensions.getAllExtensions().iterator();
        final Iterator<Extension> iterator2 = certificateExtensions.getUnparseableExtensions().values().iterator();
        while (iterator.hasNext() || iterator2.hasNext()) {
            final Extension extension = iterator.hasNext() ? iterator.next() : iterator2.next();
            if (n == 0) {
                printStream.println();
                printStream.println(s);
                printStream.println();
            }
            printStream.print("#" + ++n + ": " + extension);
            if (extension.getClass() == Extension.class) {
                if (extension.getExtensionValue().length == 0) {
                    printStream.println(Main.rb.getString(".Empty.value."));
                }
                else {
                    new HexDumpEncoder().encodeBuffer(extension.getExtensionValue(), printStream);
                    printStream.println();
                }
            }
            printStream.println();
        }
    }
    
    private static Pair<String, Certificate> getSigner(final Certificate certificate, final KeyStore keyStore) throws Exception {
        if (keyStore.getCertificateAlias(certificate) != null) {
            return new Pair<String, Certificate>("", certificate);
        }
        final Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            final String s = aliases.nextElement();
            final Certificate certificate2 = keyStore.getCertificate(s);
            if (certificate2 != null) {
                try {
                    certificate.verify(certificate2.getPublicKey());
                    return new Pair<String, Certificate>(s, certificate2);
                }
                catch (final Exception ex) {}
            }
        }
        return null;
    }
    
    private X500Name getX500Name() throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String inputString = "Unknown";
        String inputString2 = "Unknown";
        String inputString3 = "Unknown";
        String inputString4 = "Unknown";
        String inputString5 = "Unknown";
        String inputString6 = "Unknown";
        int n = 20;
        while (n-- >= 0) {
            inputString = this.inputString(bufferedReader, Main.rb.getString("What.is.your.first.and.last.name."), inputString);
            inputString2 = this.inputString(bufferedReader, Main.rb.getString("What.is.the.name.of.your.organizational.unit."), inputString2);
            inputString3 = this.inputString(bufferedReader, Main.rb.getString("What.is.the.name.of.your.organization."), inputString3);
            inputString4 = this.inputString(bufferedReader, Main.rb.getString("What.is.the.name.of.your.City.or.Locality."), inputString4);
            inputString5 = this.inputString(bufferedReader, Main.rb.getString("What.is.the.name.of.your.State.or.Province."), inputString5);
            inputString6 = this.inputString(bufferedReader, Main.rb.getString("What.is.the.two.letter.country.code.for.this.unit."), inputString6);
            final X500Name x500Name = new X500Name(inputString, inputString2, inputString3, inputString4, inputString5, inputString6);
            final String inputString7 = this.inputString(bufferedReader, new MessageFormat(Main.rb.getString("Is.name.correct.")).format(new Object[] { x500Name }), Main.rb.getString("no"));
            if (Main.collator.compare(inputString7, Main.rb.getString("yes")) == 0 || Main.collator.compare(inputString7, Main.rb.getString("y")) == 0) {
                System.err.println();
                return x500Name;
            }
        }
        throw new RuntimeException(Main.rb.getString("Too.many.retries.program.terminated"));
    }
    
    private String inputString(final BufferedReader bufferedReader, final String s, final String s2) throws IOException {
        System.err.println(s);
        System.err.print(new MessageFormat(Main.rb.getString(".defaultValue.")).format(new Object[] { s2 }));
        System.err.flush();
        String line = bufferedReader.readLine();
        if (line == null || Main.collator.compare(line, "") == 0) {
            line = s2;
        }
        return line;
    }
    
    private void dumpCert(final Certificate certificate, final PrintStream printStream) throws IOException, CertificateException {
        if (this.rfc) {
            printStream.println("-----BEGIN CERTIFICATE-----");
            printStream.println(Base64.getMimeEncoder(64, Main.CRLF).encodeToString(certificate.getEncoded()));
            printStream.println("-----END CERTIFICATE-----");
        }
        else {
            printStream.write(certificate.getEncoded());
        }
    }
    
    private void byte2hex(final byte b, final StringBuffer sb) {
        final char[] array = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        final int n = (b & 0xF0) >> 4;
        final int n2 = b & 0xF;
        sb.append(array[n]);
        sb.append(array[n2]);
    }
    
    private String toHexString(final byte[] array) {
        final StringBuffer sb = new StringBuffer();
        for (int length = array.length, i = 0; i < length; ++i) {
            this.byte2hex(array[i], sb);
            if (i < length - 1) {
                sb.append(":");
            }
        }
        return sb.toString();
    }
    
    private Pair<Key, char[]> recoverKey(final String s, final char[] array, char[] keyPasswd) throws Exception {
        if (KeyStoreUtil.isWindowsKeyStore(this.storetype)) {
            return Pair.of(this.keyStore.getKey(s, null), (char[])null);
        }
        if (!this.keyStore.containsAlias(s)) {
            throw new Exception(new MessageFormat(Main.rb.getString("Alias.alias.does.not.exist")).format(new Object[] { s }));
        }
        if (!this.keyStore.entryInstanceOf(s, KeyStore.PrivateKeyEntry.class) && !this.keyStore.entryInstanceOf(s, KeyStore.SecretKeyEntry.class)) {
            throw new Exception(new MessageFormat(Main.rb.getString("Alias.alias.has.no.key")).format(new Object[] { s }));
        }
        Key key;
        if (keyPasswd == null) {
            try {
                key = this.keyStore.getKey(s, array);
                keyPasswd = array;
                this.passwords.add(keyPasswd);
            }
            catch (final UnrecoverableKeyException ex) {
                if (this.token) {
                    throw ex;
                }
                keyPasswd = this.getKeyPasswd(s, null, null);
                key = this.keyStore.getKey(s, keyPasswd);
            }
        }
        else {
            key = this.keyStore.getKey(s, keyPasswd);
        }
        return Pair.of(key, keyPasswd);
    }
    
    private Pair<KeyStore.Entry, char[]> recoverEntry(final KeyStore keyStore, final String s, final char[] array, char[] keyPasswd) throws Exception {
        if (!keyStore.containsAlias(s)) {
            throw new Exception(new MessageFormat(Main.rb.getString("Alias.alias.does.not.exist")).format(new Object[] { s }));
        }
        final KeyStore.ProtectionParameter protectionParameter = null;
        KeyStore.Entry entry;
        try {
            entry = keyStore.getEntry(s, protectionParameter);
            keyPasswd = null;
        }
        catch (final UnrecoverableEntryException ex) {
            if ("PKCS11".equalsIgnoreCase(keyStore.getType()) || KeyStoreUtil.isWindowsKeyStore(keyStore.getType())) {
                throw ex;
            }
            if (keyPasswd != null) {
                entry = keyStore.getEntry(s, new KeyStore.PasswordProtection(keyPasswd));
            }
            else {
                try {
                    entry = keyStore.getEntry(s, new KeyStore.PasswordProtection(array));
                    keyPasswd = array;
                }
                catch (final UnrecoverableEntryException ex2) {
                    if ("PKCS12".equalsIgnoreCase(keyStore.getType())) {
                        throw ex2;
                    }
                    keyPasswd = this.getKeyPasswd(s, null, null);
                    entry = keyStore.getEntry(s, new KeyStore.PasswordProtection(keyPasswd));
                }
            }
        }
        return Pair.of(entry, keyPasswd);
    }
    
    private String getCertFingerPrint(final String s, final Certificate certificate) throws Exception {
        return this.toHexString(MessageDigest.getInstance(s).digest(certificate.getEncoded()));
    }
    
    private void printNoIntegrityWarning() {
        System.err.println();
        System.err.println(Main.rb.getString(".WARNING.WARNING.WARNING."));
        System.err.println(Main.rb.getString(".The.integrity.of.the.information.stored.in.your.keystore."));
        System.err.println(Main.rb.getString(".WARNING.WARNING.WARNING."));
        System.err.println();
    }
    
    private Certificate[] validateReply(final String s, final Certificate certificate, Certificate[] array) throws Exception {
        this.checkWeak(Main.rb.getString("reply"), array);
        PublicKey publicKey;
        int n;
        for (publicKey = certificate.getPublicKey(), n = 0; n < array.length && !publicKey.equals(array[n].getPublicKey()); ++n) {}
        if (n == array.length) {
            throw new Exception(new MessageFormat(Main.rb.getString("Certificate.reply.does.not.contain.public.key.for.alias.")).format(new Object[] { s }));
        }
        final Certificate certificate2 = array[0];
        array[0] = array[n];
        array[n] = certificate2;
        X509Certificate x509Certificate = (X509Certificate)array[0];
        for (int i = 1; i < array.length - 1; ++i) {
            int j;
            for (j = i; j < array.length; ++j) {
                if (KeyStoreUtil.signedBy(x509Certificate, (X509Certificate)array[j])) {
                    final Certificate certificate3 = array[i];
                    array[i] = array[j];
                    array[j] = certificate3;
                    x509Certificate = (X509Certificate)array[i];
                    break;
                }
            }
            if (j == array.length) {
                throw new Exception(Main.rb.getString("Incomplete.certificate.chain.in.reply"));
            }
        }
        if (this.noprompt) {
            return array;
        }
        final Certificate certificate4 = array[array.length - 1];
        boolean b = true;
        Pair<String, Certificate> pair = getSigner(certificate4, this.keyStore);
        if (pair == null && this.trustcacerts && this.caks != null) {
            pair = getSigner(certificate4, this.caks);
            b = false;
        }
        if (pair == null) {
            System.err.println();
            System.err.println(Main.rb.getString("Top.level.certificate.in.reply."));
            this.printX509Cert((X509Certificate)certificate4, System.out);
            System.err.println();
            System.err.print(Main.rb.getString(".is.not.trusted."));
            this.printWeakWarnings(true);
            if ("NO".equals(this.getYesNoReply(Main.rb.getString("Install.reply.anyway.no.")))) {
                return null;
            }
        }
        else if (pair.snd != certificate4) {
            final Certificate[] array2 = new Certificate[array.length + 1];
            System.arraycopy(array, 0, array2, 0, array.length);
            array2[array2.length - 1] = pair.snd;
            array = array2;
            this.checkWeak(String.format(Main.rb.getString(b ? "alias.in.keystore" : "alias.in.cacerts"), pair.fst), pair.snd);
        }
        return array;
    }
    
    private Certificate[] establishCertChain(final Certificate certificate, final Certificate certificate2) throws Exception {
        if (certificate != null) {
            if (!certificate.getPublicKey().equals(certificate2.getPublicKey())) {
                throw new Exception(Main.rb.getString("Public.keys.in.reply.and.keystore.don.t.match"));
            }
            if (certificate2.equals(certificate)) {
                throw new Exception(Main.rb.getString("Certificate.reply.and.certificate.in.keystore.are.identical"));
            }
        }
        Hashtable<Principal, Vector<Pair<String, X509Certificate>>> hashtable = null;
        if (this.keyStore.size() > 0) {
            hashtable = new Hashtable<Principal, Vector<Pair<String, X509Certificate>>>(11);
            this.keystorecerts2Hashtable(this.keyStore, hashtable);
        }
        if (this.trustcacerts && this.caks != null && this.caks.size() > 0) {
            if (hashtable == null) {
                hashtable = new Hashtable<Principal, Vector<Pair<String, X509Certificate>>>(11);
            }
            this.keystorecerts2Hashtable(this.caks, hashtable);
        }
        final Vector vector = new Vector<Pair<String, X509Certificate>>(2);
        if (this.buildChain(new Pair<String, X509Certificate>(Main.rb.getString("the.input"), (X509Certificate)certificate2), vector, hashtable)) {
            for (final Pair pair : vector) {
                this.checkWeak((String)pair.fst, (Certificate)pair.snd);
            }
            final Certificate[] array = new Certificate[vector.size()];
            int n = 0;
            for (int i = vector.size() - 1; i >= 0; --i) {
                array[n] = (Certificate)((Pair)vector.elementAt(i)).snd;
                ++n;
            }
            return array;
        }
        throw new Exception(Main.rb.getString("Failed.to.establish.chain.from.reply"));
    }
    
    private boolean buildChain(final Pair<String, X509Certificate> pair, final Vector<Pair<String, X509Certificate>> vector, final Hashtable<Principal, Vector<Pair<String, X509Certificate>>> hashtable) {
        if (KeyStoreUtil.isSelfSigned(pair.snd)) {
            vector.addElement(pair);
            return true;
        }
        final Vector vector2 = hashtable.get(pair.snd.getIssuerDN());
        if (vector2 == null) {
            return false;
        }
        final Enumeration elements = vector2.elements();
        while (elements.hasMoreElements()) {
            final Pair pair2 = (Pair)elements.nextElement();
            final PublicKey publicKey = ((X509Certificate)pair2.snd).getPublicKey();
            try {
                pair.snd.verify(publicKey);
            }
            catch (final Exception ex) {
                continue;
            }
            if (this.buildChain(pair2, vector, hashtable)) {
                vector.addElement(pair);
                return true;
            }
        }
        return false;
    }
    
    private String getYesNoReply(final String s) throws IOException {
        int n = 20;
        while (n-- >= 0) {
            System.err.print(s);
            System.err.flush();
            final String line = new BufferedReader(new InputStreamReader(System.in)).readLine();
            String s2;
            if (Main.collator.compare(line, "") == 0 || Main.collator.compare(line, Main.rb.getString("n")) == 0 || Main.collator.compare(line, Main.rb.getString("no")) == 0) {
                s2 = "NO";
            }
            else if (Main.collator.compare(line, Main.rb.getString("y")) == 0 || Main.collator.compare(line, Main.rb.getString("yes")) == 0) {
                s2 = "YES";
            }
            else {
                System.err.println(Main.rb.getString("Wrong.answer.try.again"));
                s2 = null;
            }
            if (s2 != null) {
                return s2;
            }
        }
        throw new RuntimeException(Main.rb.getString("Too.many.retries.program.terminated"));
    }
    
    private void keystorecerts2Hashtable(final KeyStore keyStore, final Hashtable<Principal, Vector<Pair<String, X509Certificate>>> hashtable) throws Exception {
        final Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            final String s = aliases.nextElement();
            final Certificate certificate = keyStore.getCertificate(s);
            if (certificate != null) {
                final Principal subjectDN = ((X509Certificate)certificate).getSubjectDN();
                final Pair pair = new Pair(String.format(Main.rb.getString((keyStore == this.caks) ? "alias.in.cacerts" : "alias.in.keystore"), s), certificate);
                Vector<Pair> vector = hashtable.get(subjectDN);
                if (vector == null) {
                    vector = new Vector<Pair>();
                    vector.addElement(pair);
                }
                else if (!vector.contains(pair)) {
                    vector.addElement(pair);
                }
                hashtable.put(subjectDN, vector);
            }
        }
    }
    
    private static Date getStartDate(final String s) throws IOException {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        if (s != null) {
            final IOException ex = new IOException(Main.rb.getString("Illegal.startdate.value"));
            final int length = s.length();
            if (length == 0) {
                throw ex;
            }
            if (s.charAt(0) == '-' || s.charAt(0) == '+') {
                int j;
                for (int i = 0; i < length; i = j + 1) {
                    int n = 0;
                    switch (s.charAt(i)) {
                        case '+': {
                            n = 1;
                            break;
                        }
                        case '-': {
                            n = -1;
                            break;
                        }
                        default: {
                            throw ex;
                        }
                    }
                    for (j = i + 1; j < length; ++j) {
                        final char char1 = s.charAt(j);
                        if (char1 < '0') {
                            break;
                        }
                        if (char1 > '9') {
                            break;
                        }
                    }
                    if (j == i + 1) {
                        throw ex;
                    }
                    final int int1 = Integer.parseInt(s.substring(i + 1, j));
                    if (j >= length) {
                        throw ex;
                    }
                    int n2 = 0;
                    switch (s.charAt(j)) {
                        case 'y': {
                            n2 = 1;
                            break;
                        }
                        case 'm': {
                            n2 = 2;
                            break;
                        }
                        case 'd': {
                            n2 = 5;
                            break;
                        }
                        case 'H': {
                            n2 = 10;
                            break;
                        }
                        case 'M': {
                            n2 = 12;
                            break;
                        }
                        case 'S': {
                            n2 = 13;
                            break;
                        }
                        default: {
                            throw ex;
                        }
                    }
                    gregorianCalendar.add(n2, n * int1);
                }
            }
            else {
                String substring = null;
                String substring2 = null;
                if (length == 19) {
                    substring = s.substring(0, 10);
                    substring2 = s.substring(11);
                    if (s.charAt(10) != ' ') {
                        throw ex;
                    }
                }
                else if (length == 10) {
                    substring = s;
                }
                else {
                    if (length != 8) {
                        throw ex;
                    }
                    substring2 = s;
                }
                if (substring != null) {
                    if (!substring.matches("\\d\\d\\d\\d\\/\\d\\d\\/\\d\\d")) {
                        throw ex;
                    }
                    gregorianCalendar.set(Integer.valueOf(substring.substring(0, 4)), Integer.valueOf(substring.substring(5, 7)) - 1, Integer.valueOf(substring.substring(8, 10)));
                }
                if (substring2 != null) {
                    if (!substring2.matches("\\d\\d:\\d\\d:\\d\\d")) {
                        throw ex;
                    }
                    gregorianCalendar.set(11, Integer.valueOf(substring2.substring(0, 2)));
                    gregorianCalendar.set(12, Integer.valueOf(substring2.substring(0, 2)));
                    gregorianCalendar.set(13, Integer.valueOf(substring2.substring(0, 2)));
                    gregorianCalendar.set(14, 0);
                }
            }
        }
        return gregorianCalendar.getTime();
    }
    
    private static int oneOf(final String s, final String... array) throws Exception {
        final int[] array2 = new int[array.length];
        int n = 0;
        int n2 = Integer.MAX_VALUE;
        for (int i = 0; i < array.length; ++i) {
            final String s2 = array[i];
            if (s2 == null) {
                n2 = i;
            }
            else if (s2.toLowerCase(Locale.ENGLISH).startsWith(s.toLowerCase(Locale.ENGLISH))) {
                array2[n++] = i;
            }
            else {
                final StringBuffer sb = new StringBuffer();
                int n3 = 1;
                for (final char c : s2.toCharArray()) {
                    if (n3 != 0) {
                        sb.append(c);
                        n3 = 0;
                    }
                    else if (!Character.isLowerCase(c)) {
                        sb.append(c);
                    }
                }
                if (sb.toString().equalsIgnoreCase(s)) {
                    array2[n++] = i;
                }
            }
        }
        if (n == 0) {
            return -1;
        }
        if (n == 1) {
            return array2[0];
        }
        if (array2[1] > n2) {
            return array2[0];
        }
        final StringBuffer sb2 = new StringBuffer();
        sb2.append(new MessageFormat(Main.rb.getString("command.{0}.is.ambiguous.")).format(new Object[] { s }));
        sb2.append("\n    ");
        for (int n4 = 0; n4 < n && array2[n4] < n2; ++n4) {
            sb2.append(' ');
            sb2.append(array[array2[n4]]);
        }
        throw new Exception(sb2.toString());
    }
    
    private GeneralName createGeneralName(final String s, final String s2) throws Exception {
        final int one = oneOf(s, "EMAIL", "URI", "DNS", "IP", "OID");
        if (one < 0) {
            throw new Exception(Main.rb.getString("Unrecognized.GeneralName.type.") + s);
        }
        GeneralNameInterface generalNameInterface = null;
        switch (one) {
            case 0: {
                generalNameInterface = new RFC822Name(s2);
                break;
            }
            case 1: {
                generalNameInterface = new URIName(s2);
                break;
            }
            case 2: {
                generalNameInterface = new DNSName(s2);
                break;
            }
            case 3: {
                generalNameInterface = new IPAddressName(s2);
                break;
            }
            default: {
                generalNameInterface = new OIDName(s2);
                break;
            }
        }
        return new GeneralName(generalNameInterface);
    }
    
    private ObjectIdentifier findOidForExtName(final String s) throws Exception {
        switch (oneOf(s, Main.extSupported)) {
            case 0: {
                return PKIXExtensions.BasicConstraints_Id;
            }
            case 1: {
                return PKIXExtensions.KeyUsage_Id;
            }
            case 2: {
                return PKIXExtensions.ExtendedKeyUsage_Id;
            }
            case 3: {
                return PKIXExtensions.SubjectAlternativeName_Id;
            }
            case 4: {
                return PKIXExtensions.IssuerAlternativeName_Id;
            }
            case 5: {
                return PKIXExtensions.SubjectInfoAccess_Id;
            }
            case 6: {
                return PKIXExtensions.AuthInfoAccess_Id;
            }
            case 8: {
                return PKIXExtensions.CRLDistributionPoints_Id;
            }
            default: {
                return new ObjectIdentifier(s);
            }
        }
    }
    
    private CertificateExtensions createV3Extensions(final CertificateExtensions certificateExtensions, CertificateExtensions certificateExtensions2, final List<String> list, final PublicKey publicKey, final PublicKey publicKey2) throws Exception {
        if (certificateExtensions2 != null && certificateExtensions != null) {
            throw new Exception("One of request and original should be null.");
        }
        if (certificateExtensions2 == null) {
            certificateExtensions2 = new CertificateExtensions();
        }
        try {
            if (certificateExtensions != null) {
                for (final String s : list) {
                    if (s.toLowerCase(Locale.ENGLISH).startsWith("honored=")) {
                        final List<String> list2 = Arrays.asList(s.toLowerCase(Locale.ENGLISH).substring(8).split(","));
                        if (list2.contains("all")) {
                            certificateExtensions2 = certificateExtensions;
                        }
                        for (final String s2 : list2) {
                            if (s2.equals("all")) {
                                continue;
                            }
                            boolean b = true;
                            int one = -1;
                            String s3 = null;
                            if (s2.startsWith("-")) {
                                b = false;
                                s3 = s2.substring(1);
                            }
                            else {
                                final int index = s2.indexOf(58);
                                if (index >= 0) {
                                    s3 = s2.substring(0, index);
                                    one = oneOf(s2.substring(index + 1), "critical", "non-critical");
                                    if (one == -1) {
                                        throw new Exception(Main.rb.getString("Illegal.value.") + s2);
                                    }
                                }
                            }
                            final String nameByOid = certificateExtensions.getNameByOid(this.findOidForExtName(s3));
                            if (b) {
                                final Extension value = certificateExtensions.get(nameByOid);
                                if ((value.isCritical() || one != 0) && (!value.isCritical() || one != 1)) {
                                    continue;
                                }
                                certificateExtensions2.set(nameByOid, Extension.newExtension(value.getExtensionId(), !value.isCritical(), value.getExtensionValue()));
                            }
                            else {
                                certificateExtensions2.delete(nameByOid);
                            }
                        }
                        break;
                    }
                }
            }
            for (final String s4 : list) {
                boolean b2 = false;
                final int index2 = s4.indexOf(61);
                String s5;
                String substring;
                if (index2 >= 0) {
                    s5 = s4.substring(0, index2);
                    substring = s4.substring(index2 + 1);
                }
                else {
                    s5 = s4;
                    substring = null;
                }
                final int index3 = s5.indexOf(58);
                if (index3 >= 0) {
                    if (oneOf(s5.substring(index3 + 1), "critical") == 0) {
                        b2 = true;
                    }
                    s5 = s5.substring(0, index3);
                }
                if (s5.equalsIgnoreCase("honored")) {
                    continue;
                }
                final int one2 = oneOf(s5, Main.extSupported);
                switch (one2) {
                    case 0: {
                        int n = -1;
                        boolean boolean1 = false;
                        if (substring == null) {
                            boolean1 = true;
                        }
                        else {
                            try {
                                n = Integer.parseInt(substring);
                                boolean1 = true;
                            }
                            catch (final NumberFormatException ex) {
                                final String[] split = substring.split(",");
                                for (int length = split.length, i = 0; i < length; ++i) {
                                    final String[] split2 = split[i].split(":");
                                    if (split2.length != 2) {
                                        throw new Exception(Main.rb.getString("Illegal.value.") + s4);
                                    }
                                    if (split2[0].equalsIgnoreCase("ca")) {
                                        boolean1 = Boolean.parseBoolean(split2[1]);
                                    }
                                    else {
                                        if (!split2[0].equalsIgnoreCase("pathlen")) {
                                            throw new Exception(Main.rb.getString("Illegal.value.") + s4);
                                        }
                                        n = Integer.parseInt(split2[1]);
                                    }
                                }
                            }
                        }
                        certificateExtensions2.set("BasicConstraints", new BasicConstraintsExtension(b2, boolean1, n));
                        continue;
                    }
                    case 1: {
                        if (substring != null) {
                            final boolean[] array = new boolean[9];
                            for (final String s6 : substring.split(",")) {
                                int one3 = oneOf(s6, "digitalSignature", "nonRepudiation", "keyEncipherment", "dataEncipherment", "keyAgreement", "keyCertSign", "cRLSign", "encipherOnly", "decipherOnly", "contentCommitment");
                                if (one3 < 0) {
                                    throw new Exception(Main.rb.getString("Unknown.keyUsage.type.") + s6);
                                }
                                if (one3 == 9) {
                                    one3 = 1;
                                }
                                array[one3] = true;
                            }
                            final KeyUsageExtension keyUsageExtension = new KeyUsageExtension(array);
                            certificateExtensions2.set("KeyUsage", Extension.newExtension(keyUsageExtension.getExtensionId(), b2, keyUsageExtension.getExtensionValue()));
                            continue;
                        }
                        throw new Exception(Main.rb.getString("Illegal.value.") + s4);
                    }
                    case 2: {
                        if (substring != null) {
                            final Vector<ObjectIdentifier> vector = new Vector<ObjectIdentifier>();
                            for (final String s7 : substring.split(",")) {
                                final int one4 = oneOf(s7, "anyExtendedKeyUsage", "serverAuth", "clientAuth", "codeSigning", "emailProtection", "", "", "", "timeStamping", "OCSPSigning");
                                Label_1334: {
                                    if (one4 < 0) {
                                        try {
                                            vector.add(new ObjectIdentifier(s7));
                                            break Label_1334;
                                        }
                                        catch (final Exception ex2) {
                                            throw new Exception(Main.rb.getString("Unknown.extendedkeyUsage.type.") + s7);
                                        }
                                    }
                                    if (one4 == 0) {
                                        vector.add(new ObjectIdentifier("2.5.29.37.0"));
                                    }
                                    else {
                                        vector.add(new ObjectIdentifier("1.3.6.1.5.5.7.3." + one4));
                                    }
                                }
                            }
                            certificateExtensions2.set("ExtendedKeyUsage", new ExtendedKeyUsageExtension(b2, vector));
                            continue;
                        }
                        throw new Exception(Main.rb.getString("Illegal.value.") + s4);
                    }
                    case 3:
                    case 4: {
                        if (substring != null) {
                            final String[] split5 = substring.split(",");
                            final GeneralNames generalNames = new GeneralNames();
                            for (final String s8 : split5) {
                                final int index4 = s8.indexOf(58);
                                if (index4 < 0) {
                                    throw new Exception("Illegal item " + s8 + " in " + s4);
                                }
                                generalNames.add(this.createGeneralName(s8.substring(0, index4), s8.substring(index4 + 1)));
                            }
                            if (one2 == 3) {
                                certificateExtensions2.set("SubjectAlternativeName", new SubjectAlternativeNameExtension(b2, generalNames));
                            }
                            else {
                                certificateExtensions2.set("IssuerAlternativeName", new IssuerAlternativeNameExtension(b2, generalNames));
                            }
                            continue;
                        }
                        throw new Exception(Main.rb.getString("Illegal.value.") + s4);
                    }
                    case 5:
                    case 6: {
                        if (b2) {
                            throw new Exception(Main.rb.getString("This.extension.cannot.be.marked.as.critical.") + s4);
                        }
                        if (substring != null) {
                            final ArrayList list3 = new ArrayList();
                            for (final String s9 : substring.split(",")) {
                                final int index5 = s9.indexOf(58);
                                final int index6 = s9.indexOf(58, index5 + 1);
                                if (index5 < 0 || index6 < 0) {
                                    throw new Exception(Main.rb.getString("Illegal.value.") + s4);
                                }
                                final String substring2 = s9.substring(0, index5);
                                final String substring3 = s9.substring(index5 + 1, index6);
                                final String substring4 = s9.substring(index6 + 1);
                                final int one5 = oneOf(substring2, "", "ocsp", "caIssuers", "timeStamping", "", "caRepository");
                                ObjectIdentifier objectIdentifier = null;
                                Label_1956: {
                                    if (one5 < 0) {
                                        try {
                                            objectIdentifier = new ObjectIdentifier(substring2);
                                            break Label_1956;
                                        }
                                        catch (final Exception ex3) {
                                            throw new Exception(Main.rb.getString("Unknown.AccessDescription.type.") + substring2);
                                        }
                                    }
                                    objectIdentifier = new ObjectIdentifier("1.3.6.1.5.5.7.48." + one5);
                                }
                                list3.add(new AccessDescription(objectIdentifier, this.createGeneralName(substring3, substring4)));
                            }
                            if (one2 == 5) {
                                certificateExtensions2.set("SubjectInfoAccess", new SubjectInfoAccessExtension(list3));
                            }
                            else {
                                certificateExtensions2.set("AuthorityInfoAccess", new AuthorityInfoAccessExtension(list3));
                            }
                            continue;
                        }
                        throw new Exception(Main.rb.getString("Illegal.value.") + s4);
                    }
                    case 8: {
                        if (substring != null) {
                            final String[] split7 = substring.split(",");
                            final GeneralNames generalNames2 = new GeneralNames();
                            for (final String s10 : split7) {
                                final int index7 = s10.indexOf(58);
                                if (index7 < 0) {
                                    throw new Exception("Illegal item " + s10 + " in " + s4);
                                }
                                generalNames2.add(this.createGeneralName(s10.substring(0, index7), s10.substring(index7 + 1)));
                            }
                            certificateExtensions2.set("CRLDistributionPoints", new CRLDistributionPointsExtension(b2, Collections.singletonList(new DistributionPoint(generalNames2, null, null))));
                            continue;
                        }
                        throw new Exception(Main.rb.getString("Illegal.value.") + s4);
                    }
                    case -1: {
                        final ObjectIdentifier objectIdentifier2 = new ObjectIdentifier(s5);
                        byte[] copy;
                        if (substring != null) {
                            final byte[] array4 = new byte[substring.length() / 2 + 1];
                            int n4 = 0;
                            for (final char c : substring.toCharArray()) {
                                Label_2456: {
                                    int n6;
                                    if (c >= '0' && c <= '9') {
                                        n6 = c - '0';
                                    }
                                    else if (c >= 'A' && c <= 'F') {
                                        n6 = c - 'A' + 10;
                                    }
                                    else {
                                        if (c < 'a' || c > 'f') {
                                            break Label_2456;
                                        }
                                        n6 = c - 'a' + 10;
                                    }
                                    if (n4 % 2 == 0) {
                                        array4[n4 / 2] = (byte)(n6 << 4);
                                    }
                                    else {
                                        final byte[] array5 = array4;
                                        final int n7 = n4 / 2;
                                        array5[n7] += (byte)n6;
                                    }
                                    ++n4;
                                }
                            }
                            if (n4 % 2 != 0) {
                                throw new Exception(Main.rb.getString("Odd.number.of.hex.digits.found.") + s4);
                            }
                            copy = Arrays.copyOf(array4, n4 / 2);
                        }
                        else {
                            copy = new byte[0];
                        }
                        certificateExtensions2.set(objectIdentifier2.toString(), new Extension(objectIdentifier2, b2, new DerValue((byte)4, copy).toByteArray()));
                        continue;
                    }
                    default: {
                        throw new Exception(Main.rb.getString("Unknown.extension.type.") + s4);
                    }
                }
            }
            certificateExtensions2.set("SubjectKeyIdentifier", new SubjectKeyIdentifierExtension(new KeyIdentifier(publicKey).getIdentifier()));
            if (publicKey2 != null && !publicKey.equals(publicKey2)) {
                certificateExtensions2.set("AuthorityKeyIdentifier", new AuthorityKeyIdentifierExtension(new KeyIdentifier(publicKey2), null, null));
            }
        }
        catch (final IOException ex4) {
            throw new RuntimeException(ex4);
        }
        return certificateExtensions2;
    }
    
    private boolean isTrustedCert(final Certificate certificate) throws KeyStoreException {
        if (this.caks != null && this.caks.getCertificateAlias(certificate) != null) {
            return true;
        }
        final String certificateAlias = this.keyStore.getCertificateAlias(certificate);
        return certificateAlias != null && this.keyStore.isCertificateEntry(certificateAlias);
    }
    
    private void checkWeak(final String s, final String s2, final Key key) {
        if (s2 != null && !Main.DISABLED_CHECK.permits(Main.SIG_PRIMITIVE_SET, s2, null)) {
            this.weakWarnings.add(String.format(Main.rb.getString("whose.sigalg.risk"), s, s2));
        }
        if (key != null && !Main.DISABLED_CHECK.permits(Main.SIG_PRIMITIVE_SET, key)) {
            this.weakWarnings.add(String.format(Main.rb.getString("whose.key.risk"), s, String.format(Main.rb.getString("key.bit"), KeyUtil.getKeySize(key), this.fullDisplayAlgName(key))));
        }
    }
    
    private void checkWeak(final String s, final Certificate[] array) throws KeyStoreException {
        for (int i = 0; i < array.length; ++i) {
            final Certificate certificate = array[i];
            if (certificate instanceof X509Certificate) {
                final X509Certificate x509Certificate = (X509Certificate)certificate;
                String oneInMany = s;
                if (array.length > 1) {
                    oneInMany = oneInMany(s, i, array.length);
                }
                this.checkWeak(oneInMany, x509Certificate);
            }
        }
    }
    
    private void checkWeak(final String s, final Certificate certificate) throws KeyStoreException {
        if (certificate instanceof X509Certificate) {
            final X509Certificate x509Certificate = (X509Certificate)certificate;
            this.checkWeak(s, this.isTrustedCert(certificate) ? null : x509Certificate.getSigAlgName(), x509Certificate.getPublicKey());
        }
    }
    
    private void checkWeak(final String s, final PKCS10 pkcs10) {
        this.checkWeak(s, pkcs10.getSigAlg(), pkcs10.getSubjectPublicKeyInfo());
    }
    
    private void checkWeak(final String s, final CRL crl, final Key key) {
        if (crl instanceof X509CRLImpl) {
            this.checkWeak(s, ((X509CRLImpl)crl).getSigAlgName(), key);
        }
    }
    
    private void printWeakWarnings(final boolean b) {
        if (!this.weakWarnings.isEmpty() && !this.nowarn) {
            System.err.println("\nWarning:");
            final Iterator<String> iterator = this.weakWarnings.iterator();
            while (iterator.hasNext()) {
                System.err.println(iterator.next());
            }
            if (b) {
                System.err.println();
            }
        }
        this.weakWarnings.clear();
    }
    
    private void usage() {
        if (this.command != null) {
            System.err.println("keytool " + this.command + Main.rb.getString(".OPTION."));
            System.err.println();
            System.err.println(Main.rb.getString(this.command.description));
            System.err.println();
            System.err.println(Main.rb.getString("Options."));
            System.err.println();
            final String[] array = new String[this.command.options.length];
            final String[] array2 = new String[this.command.options.length];
            int length = 0;
            for (int i = 0; i < array.length; ++i) {
                final Option option = this.command.options[i];
                array[i] = option.toString();
                if (option.arg != null) {
                    final StringBuilder sb = new StringBuilder();
                    final String[] array3 = array;
                    final int n = i;
                    array3[n] = sb.append(array3[n]).append(" ").append(option.arg).toString();
                }
                if (array[i].length() > length) {
                    length = array[i].length();
                }
                array2[i] = Main.rb.getString(option.description);
            }
            for (int j = 0; j < array.length; ++j) {
                System.err.printf(" %-" + length + "s  %s\n", array[j], array2[j]);
            }
            System.err.println();
            System.err.println(Main.rb.getString("Use.keytool.help.for.all.available.commands"));
        }
        else {
            System.err.println(Main.rb.getString("Key.and.Certificate.Management.Tool"));
            System.err.println();
            System.err.println(Main.rb.getString("Commands."));
            System.err.println();
            for (final Command command : Command.values()) {
                if (command == Command.KEYCLONE) {
                    break;
                }
                System.err.printf(" %-20s%s\n", command, Main.rb.getString(command.description));
            }
            System.err.println();
            System.err.println(Main.rb.getString("Use.keytool.command.name.help.for.usage.of.command.name"));
        }
    }
    
    private void tinyHelp() {
        this.usage();
        if (this.debug) {
            throw new RuntimeException("NO BIG ERROR, SORRY");
        }
        System.exit(1);
    }
    
    private void errorNeedArgument(final String s) {
        System.err.println(new MessageFormat(Main.rb.getString("Command.option.flag.needs.an.argument.")).format(new Object[] { s }));
        this.tinyHelp();
    }
    
    private char[] getPass(final String s, final String s2) {
        final char[] passWithModifier = KeyStoreUtil.getPassWithModifier(s, s2, Main.rb);
        if (passWithModifier != null) {
            return passWithModifier;
        }
        this.tinyHelp();
        return null;
    }
    
    static {
        CRLF = new byte[] { 13, 10 };
        DISABLED_CHECK = new DisabledAlgorithmConstraints("jdk.certpath.disabledAlgorithms");
        SIG_PRIMITIVE_SET = Collections.unmodifiableSet((Set<? extends CryptoPrimitive>)EnumSet.of(CryptoPrimitive.SIGNATURE));
        PARAM_STRING = new Class[] { String.class };
        rb = ResourceBundle.getBundle("sun.security.tools.keytool.Resources");
        (collator = Collator.getInstance()).setStrength(0);
        extSupported = new String[] { "BasicConstraints", "KeyUsage", "ExtendedKeyUsage", "SubjectAlternativeName", "IssuerAlternativeName", "SubjectInfoAccess", "AuthorityInfoAccess", null, "CRLDistributionPoints" };
    }
    
    enum Command
    {
        CERTREQ("Generates.a.certificate.request", new Option[] { Option.ALIAS, Option.SIGALG, Option.FILEOUT, Option.KEYPASS, Option.KEYSTORE, Option.DNAME, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }), 
        CHANGEALIAS("Changes.an.entry.s.alias", new Option[] { Option.ALIAS, Option.DESTALIAS, Option.KEYPASS, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }), 
        DELETE("Deletes.an.entry", new Option[] { Option.ALIAS, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }), 
        EXPORTCERT("Exports.certificate", new Option[] { Option.RFC, Option.ALIAS, Option.FILEOUT, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }), 
        GENKEYPAIR("Generates.a.key.pair", new Option[] { Option.ALIAS, Option.KEYALG, Option.KEYSIZE, Option.SIGALG, Option.DESTALIAS, Option.DNAME, Option.STARTDATE, Option.EXT, Option.VALIDITY, Option.KEYPASS, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }), 
        GENSECKEY("Generates.a.secret.key", new Option[] { Option.ALIAS, Option.KEYPASS, Option.KEYALG, Option.KEYSIZE, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }), 
        GENCERT("Generates.certificate.from.a.certificate.request", new Option[] { Option.RFC, Option.INFILE, Option.OUTFILE, Option.ALIAS, Option.SIGALG, Option.DNAME, Option.STARTDATE, Option.EXT, Option.VALIDITY, Option.KEYPASS, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }), 
        IMPORTCERT("Imports.a.certificate.or.a.certificate.chain", new Option[] { Option.NOPROMPT, Option.TRUSTCACERTS, Option.PROTECTED, Option.ALIAS, Option.FILEIN, Option.KEYPASS, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V }), 
        IMPORTPASS("Imports.a.password", new Option[] { Option.ALIAS, Option.KEYPASS, Option.KEYALG, Option.KEYSIZE, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }), 
        IMPORTKEYSTORE("Imports.one.or.all.entries.from.another.keystore", new Option[] { Option.SRCKEYSTORE, Option.DESTKEYSTORE, Option.SRCSTORETYPE, Option.DESTSTORETYPE, Option.SRCSTOREPASS, Option.DESTSTOREPASS, Option.SRCPROTECTED, Option.SRCPROVIDERNAME, Option.DESTPROVIDERNAME, Option.SRCALIAS, Option.DESTALIAS, Option.SRCKEYPASS, Option.DESTKEYPASS, Option.NOPROMPT, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V }), 
        KEYPASSWD("Changes.the.key.password.of.an.entry", new Option[] { Option.ALIAS, Option.KEYPASS, Option.NEW, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V }), 
        LIST("Lists.entries.in.a.keystore", new Option[] { Option.RFC, Option.ALIAS, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }), 
        PRINTCERT("Prints.the.content.of.a.certificate", new Option[] { Option.RFC, Option.FILEIN, Option.SSLSERVER, Option.JARFILE, Option.V }), 
        PRINTCERTREQ("Prints.the.content.of.a.certificate.request", new Option[] { Option.FILEIN, Option.V }), 
        PRINTCRL("Prints.the.content.of.a.CRL.file", new Option[] { Option.FILEIN, Option.V }), 
        STOREPASSWD("Changes.the.store.password.of.a.keystore", new Option[] { Option.NEW, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V }), 
        KEYCLONE("Clones.a.key.entry", new Option[] { Option.ALIAS, Option.DESTALIAS, Option.KEYPASS, Option.NEW, Option.STORETYPE, Option.KEYSTORE, Option.STOREPASS, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V }), 
        SELFCERT("Generates.a.self.signed.certificate", new Option[] { Option.ALIAS, Option.SIGALG, Option.DNAME, Option.STARTDATE, Option.VALIDITY, Option.KEYPASS, Option.STORETYPE, Option.KEYSTORE, Option.STOREPASS, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V }), 
        GENCRL("Generates.CRL", new Option[] { Option.RFC, Option.FILEOUT, Option.ID, Option.ALIAS, Option.SIGALG, Option.EXT, Option.KEYPASS, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }), 
        IDENTITYDB("Imports.entries.from.a.JDK.1.1.x.style.identity.database", new Option[] { Option.FILEIN, Option.STORETYPE, Option.KEYSTORE, Option.STOREPASS, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V });
        
        final String description;
        final Option[] options;
        
        private Command(final String description, final Option[] options) {
            this.description = description;
            this.options = options;
        }
        
        @Override
        public String toString() {
            return "-" + this.name().toLowerCase(Locale.ENGLISH);
        }
    }
    
    enum Option
    {
        ALIAS("alias", "<alias>", "alias.name.of.the.entry.to.process"), 
        DESTALIAS("destalias", "<destalias>", "destination.alias"), 
        DESTKEYPASS("destkeypass", "<arg>", "destination.key.password"), 
        DESTKEYSTORE("destkeystore", "<destkeystore>", "destination.keystore.name"), 
        DESTPROTECTED("destprotected", (String)null, "destination.keystore.password.protected"), 
        DESTPROVIDERNAME("destprovidername", "<destprovidername>", "destination.keystore.provider.name"), 
        DESTSTOREPASS("deststorepass", "<arg>", "destination.keystore.password"), 
        DESTSTORETYPE("deststoretype", "<deststoretype>", "destination.keystore.type"), 
        DNAME("dname", "<dname>", "distinguished.name"), 
        EXT("ext", "<value>", "X.509.extension"), 
        FILEOUT("file", "<filename>", "output.file.name"), 
        FILEIN("file", "<filename>", "input.file.name"), 
        ID("id", "<id:reason>", "Serial.ID.of.cert.to.revoke"), 
        INFILE("infile", "<filename>", "input.file.name"), 
        KEYALG("keyalg", "<keyalg>", "key.algorithm.name"), 
        KEYPASS("keypass", "<arg>", "key.password"), 
        KEYSIZE("keysize", "<keysize>", "key.bit.size"), 
        KEYSTORE("keystore", "<keystore>", "keystore.name"), 
        NEW("new", "<arg>", "new.password"), 
        NOPROMPT("noprompt", (String)null, "do.not.prompt"), 
        OUTFILE("outfile", "<filename>", "output.file.name"), 
        PROTECTED("protected", (String)null, "password.through.protected.mechanism"), 
        PROVIDERARG("providerarg", "<arg>", "provider.argument"), 
        PROVIDERCLASS("providerclass", "<providerclass>", "provider.class.name"), 
        PROVIDERNAME("providername", "<providername>", "provider.name"), 
        PROVIDERPATH("providerpath", "<pathlist>", "provider.classpath"), 
        RFC("rfc", (String)null, "output.in.RFC.style"), 
        SIGALG("sigalg", "<sigalg>", "signature.algorithm.name"), 
        SRCALIAS("srcalias", "<srcalias>", "source.alias"), 
        SRCKEYPASS("srckeypass", "<arg>", "source.key.password"), 
        SRCKEYSTORE("srckeystore", "<srckeystore>", "source.keystore.name"), 
        SRCPROTECTED("srcprotected", (String)null, "source.keystore.password.protected"), 
        SRCPROVIDERNAME("srcprovidername", "<srcprovidername>", "source.keystore.provider.name"), 
        SRCSTOREPASS("srcstorepass", "<arg>", "source.keystore.password"), 
        SRCSTORETYPE("srcstoretype", "<srcstoretype>", "source.keystore.type"), 
        SSLSERVER("sslserver", "<server[:port]>", "SSL.server.host.and.port"), 
        JARFILE("jarfile", "<filename>", "signed.jar.file"), 
        STARTDATE("startdate", "<startdate>", "certificate.validity.start.date.time"), 
        STOREPASS("storepass", "<arg>", "keystore.password"), 
        STORETYPE("storetype", "<storetype>", "keystore.type"), 
        TRUSTCACERTS("trustcacerts", (String)null, "trust.certificates.from.cacerts"), 
        V("v", (String)null, "verbose.output"), 
        VALIDITY("validity", "<valDays>", "validity.number.of.days");
        
        final String name;
        final String arg;
        final String description;
        
        private Option(final String name, final String arg, final String description) {
            this.name = name;
            this.arg = arg;
            this.description = description;
        }
        
        @Override
        public String toString() {
            return "-" + this.name;
        }
    }
}
