package sun.security.provider;

import java.text.MessageFormat;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.security.PrivilegedAction;
import java.security.Permission;
import javax.security.auth.AuthPermission;
import java.util.Iterator;
import java.io.FileNotFoundException;
import sun.security.util.ResourcesMgr;
import java.io.Reader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.io.File;
import sun.security.util.PropertyExpander;
import java.util.HashMap;
import java.security.Security;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.URIParameter;
import java.security.PrivilegedExceptionAction;
import java.net.URI;
import java.io.IOException;
import sun.security.util.Debug;
import java.io.StreamTokenizer;
import java.util.List;
import java.util.Map;
import java.net.URL;
import javax.security.auth.login.ConfigurationSpi;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

public final class ConfigFile extends Configuration
{
    private final Spi spi;
    
    public ConfigFile() {
        this.spi = new Spi();
    }
    
    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(final String s) {
        return this.spi.engineGetAppConfigurationEntry(s);
    }
    
    @Override
    public synchronized void refresh() {
        this.spi.engineRefresh();
    }
    
    public static final class Spi extends ConfigurationSpi
    {
        private URL url;
        private boolean expandProp;
        private Map<String, List<AppConfigurationEntry>> configuration;
        private int linenum;
        private StreamTokenizer st;
        private int lookahead;
        private static Debug debugConfig;
        private static Debug debugParser;
        
        public Spi() {
            this.expandProp = true;
            try {
                this.init();
            }
            catch (final IOException ex) {
                throw new SecurityException(ex);
            }
        }
        
        public Spi(final URI uri) {
            this.expandProp = true;
            try {
                this.url = uri.toURL();
                this.init();
            }
            catch (final IOException ex) {
                throw new SecurityException(ex);
            }
        }
        
        public Spi(final Parameters parameters) throws IOException {
            this.expandProp = true;
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws IOException {
                        if (parameters == null) {
                            Spi.this.init();
                        }
                        else {
                            if (!(parameters instanceof URIParameter)) {
                                throw new IllegalArgumentException("Unrecognized parameter: " + parameters);
                            }
                            Spi.this.url = ((URIParameter)parameters).getURI().toURL();
                            Spi.this.init();
                        }
                        return null;
                    }
                });
            }
            catch (final PrivilegedActionException ex) {
                throw (IOException)ex.getException();
            }
        }
        
        private void init() throws IOException {
            boolean b = false;
            String s = Security.getProperty("policy.expandProperties");
            if (s == null) {
                s = System.getProperty("policy.expandProperties");
            }
            if ("false".equals(s)) {
                this.expandProp = false;
            }
            final HashMap<String, List<AppConfigurationEntry>> configuration = new HashMap<String, List<AppConfigurationEntry>>();
            if (this.url != null) {
                if (Spi.debugConfig != null) {
                    Spi.debugConfig.println("reading " + this.url);
                }
                this.init(this.url, configuration);
                this.configuration = configuration;
                return;
            }
            if ("true".equalsIgnoreCase(Security.getProperty("policy.allowSystemProperty"))) {
                String s2 = System.getProperty("java.security.auth.login.config");
                if (s2 != null) {
                    boolean b2 = false;
                    if (s2.startsWith("=")) {
                        b2 = true;
                        s2 = s2.substring(1);
                    }
                    try {
                        s2 = PropertyExpander.expand(s2);
                    }
                    catch (final PropertyExpander.ExpandException ex) {
                        throw this.ioException("Unable.to.properly.expand.config", s2);
                    }
                    URL url;
                    try {
                        url = new URL(s2);
                    }
                    catch (final MalformedURLException ex2) {
                        final File file = new File(s2);
                        if (!file.exists()) {
                            throw this.ioException("extra.config.No.such.file.or.directory.", s2);
                        }
                        url = file.toURI().toURL();
                    }
                    if (Spi.debugConfig != null) {
                        Spi.debugConfig.println("reading " + url);
                    }
                    this.init(url, configuration);
                    b = true;
                    if (b2) {
                        if (Spi.debugConfig != null) {
                            Spi.debugConfig.println("overriding other policies!");
                        }
                        this.configuration = configuration;
                        return;
                    }
                }
            }
            int n;
            String s3;
            for (n = 1; (s3 = Security.getProperty("login.config.url." + n)) != null; ++n) {
                try {
                    s3 = PropertyExpander.expand(s3).replace(File.separatorChar, '/');
                    if (Spi.debugConfig != null) {
                        Spi.debugConfig.println("\tReading config: " + s3);
                    }
                    this.init(new URL(s3), configuration);
                    b = true;
                }
                catch (final PropertyExpander.ExpandException ex3) {
                    throw this.ioException("Unable.to.properly.expand.config", s3);
                }
            }
            if (!b && n == 1 && s3 == null) {
                if (Spi.debugConfig != null) {
                    Spi.debugConfig.println("\tReading Policy from ~/.java.login.config");
                }
                final String string = System.getProperty("user.home") + File.separatorChar + ".java.login.config";
                if (new File(string).exists()) {
                    this.init(new File(string).toURI().toURL(), configuration);
                }
            }
            this.configuration = configuration;
        }
        
        private void init(final URL url, final Map<String, List<AppConfigurationEntry>> map) throws IOException {
            try (final InputStreamReader inputStreamReader = new InputStreamReader(this.getInputStream(url), "UTF-8")) {
                this.readConfig(inputStreamReader, map);
            }
            catch (final FileNotFoundException ex) {
                if (Spi.debugConfig != null) {
                    Spi.debugConfig.println(ex.toString());
                }
                throw new IOException(ResourcesMgr.getString("Configuration.Error.No.such.file.or.directory", "sun.security.util.AuthResources"));
            }
        }
        
        public AppConfigurationEntry[] engineGetAppConfigurationEntry(final String s) {
            List list = null;
            synchronized (this.configuration) {
                list = this.configuration.get(s);
            }
            if (list == null || list.size() == 0) {
                return null;
            }
            final AppConfigurationEntry[] array = new AppConfigurationEntry[list.size()];
            final Iterator iterator = list.iterator();
            int n = 0;
            while (iterator.hasNext()) {
                final AppConfigurationEntry appConfigurationEntry = (AppConfigurationEntry)iterator.next();
                array[n] = new AppConfigurationEntry(appConfigurationEntry.getLoginModuleName(), appConfigurationEntry.getControlFlag(), appConfigurationEntry.getOptions());
                ++n;
            }
            return array;
        }
        
        public synchronized void engineRefresh() {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkPermission(new AuthPermission("refreshLoginConfiguration"));
            }
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    try {
                        Spi.this.init();
                    }
                    catch (final IOException ex) {
                        throw new SecurityException(ex.getLocalizedMessage(), ex);
                    }
                    return null;
                }
            });
        }
        
        private void readConfig(Reader reader, final Map<String, List<AppConfigurationEntry>> map) throws IOException {
            this.linenum = 1;
            if (!(reader instanceof BufferedReader)) {
                reader = new BufferedReader(reader);
            }
            (this.st = new StreamTokenizer(reader)).quoteChar(34);
            this.st.wordChars(36, 36);
            this.st.wordChars(95, 95);
            this.st.wordChars(45, 45);
            this.st.wordChars(42, 42);
            this.st.lowerCaseMode(false);
            this.st.slashSlashComments(true);
            this.st.slashStarComments(true);
            this.st.eolIsSignificant(true);
            this.lookahead = this.nextToken();
            while (this.lookahead != -1) {
                this.parseLoginEntry(map);
            }
        }
        
        private void parseLoginEntry(final Map<String, List<AppConfigurationEntry>> map) throws IOException {
            final LinkedList list = new LinkedList();
            final String sval = this.st.sval;
            this.lookahead = this.nextToken();
            if (Spi.debugParser != null) {
                Spi.debugParser.println("\tReading next config entry: " + sval);
            }
            this.match("{");
            while (!this.peek("}")) {
                final String match = this.match("module class name");
                final String upperCase;
                final String s = upperCase = this.match("controlFlag").toUpperCase(Locale.ENGLISH);
                AppConfigurationEntry.LoginModuleControlFlag loginModuleControlFlag = null;
                switch (upperCase) {
                    case "REQUIRED": {
                        loginModuleControlFlag = AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
                        break;
                    }
                    case "REQUISITE": {
                        loginModuleControlFlag = AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
                        break;
                    }
                    case "SUFFICIENT": {
                        loginModuleControlFlag = AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;
                        break;
                    }
                    case "OPTIONAL": {
                        loginModuleControlFlag = AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
                        break;
                    }
                    default: {
                        throw this.ioException("Configuration.Error.Invalid.control.flag.flag", s);
                    }
                }
                final HashMap hashMap = new HashMap();
                while (!this.peek(";")) {
                    final String match2 = this.match("option key");
                    this.match("=");
                    try {
                        hashMap.put(match2, this.expand(this.match("option value")));
                    }
                    catch (final PropertyExpander.ExpandException ex) {
                        throw new IOException(ex.getLocalizedMessage());
                    }
                }
                this.lookahead = this.nextToken();
                if (Spi.debugParser != null) {
                    Spi.debugParser.println("\t\t" + match + ", " + s);
                    for (final String s2 : hashMap.keySet()) {
                        Spi.debugParser.println("\t\t\t" + s2 + "=" + (String)hashMap.get(s2));
                    }
                }
                list.add(new AppConfigurationEntry(match, loginModuleControlFlag, hashMap));
            }
            this.match("}");
            this.match(";");
            if (map.containsKey(sval)) {
                throw this.ioException("Configuration.Error.Can.not.specify.multiple.entries.for.appName", sval);
            }
            map.put(sval, list);
        }
        
        private String match(final String s) throws IOException {
            String sval = null;
            switch (this.lookahead) {
                case -1: {
                    throw this.ioException("Configuration.Error.expected.expect.read.end.of.file.", s);
                }
                case -3:
                case 34: {
                    if (s.equalsIgnoreCase("module class name") || s.equalsIgnoreCase("controlFlag") || s.equalsIgnoreCase("option key") || s.equalsIgnoreCase("option value")) {
                        sval = this.st.sval;
                        this.lookahead = this.nextToken();
                        break;
                    }
                    throw this.ioException("Configuration.Error.Line.line.expected.expect.found.value.", new Integer(this.linenum), s, this.st.sval);
                }
                case 123: {
                    if (s.equalsIgnoreCase("{")) {
                        this.lookahead = this.nextToken();
                        break;
                    }
                    throw this.ioException("Configuration.Error.Line.line.expected.expect.", new Integer(this.linenum), s, this.st.sval);
                }
                case 59: {
                    if (s.equalsIgnoreCase(";")) {
                        this.lookahead = this.nextToken();
                        break;
                    }
                    throw this.ioException("Configuration.Error.Line.line.expected.expect.", new Integer(this.linenum), s, this.st.sval);
                }
                case 125: {
                    if (s.equalsIgnoreCase("}")) {
                        this.lookahead = this.nextToken();
                        break;
                    }
                    throw this.ioException("Configuration.Error.Line.line.expected.expect.", new Integer(this.linenum), s, this.st.sval);
                }
                case 61: {
                    if (s.equalsIgnoreCase("=")) {
                        this.lookahead = this.nextToken();
                        break;
                    }
                    throw this.ioException("Configuration.Error.Line.line.expected.expect.", new Integer(this.linenum), s, this.st.sval);
                }
                default: {
                    throw this.ioException("Configuration.Error.Line.line.expected.expect.found.value.", new Integer(this.linenum), s, this.st.sval);
                }
            }
            return sval;
        }
        
        private boolean peek(final String s) {
            switch (this.lookahead) {
                case 44: {
                    return s.equalsIgnoreCase(",");
                }
                case 59: {
                    return s.equalsIgnoreCase(";");
                }
                case 123: {
                    return s.equalsIgnoreCase("{");
                }
                case 125: {
                    return s.equalsIgnoreCase("}");
                }
                default: {
                    return false;
                }
            }
        }
        
        private int nextToken() throws IOException {
            int nextToken;
            while ((nextToken = this.st.nextToken()) == 10) {
                ++this.linenum;
            }
            return nextToken;
        }
        
        private InputStream getInputStream(final URL url) throws IOException {
            if ("file".equalsIgnoreCase(url.getProtocol())) {
                try {
                    return url.openStream();
                }
                catch (final Exception ex) {
                    String s = url.getPath();
                    if (url.getHost().length() > 0) {
                        s = "//" + url.getHost() + s;
                    }
                    if (Spi.debugConfig != null) {
                        Spi.debugConfig.println("cannot read " + url + ", try " + s);
                    }
                    return new FileInputStream(s);
                }
            }
            return url.openStream();
        }
        
        private String expand(final String s) throws PropertyExpander.ExpandException, IOException {
            if (s.isEmpty()) {
                return s;
            }
            if (!this.expandProp) {
                return s;
            }
            final String expand = PropertyExpander.expand(s);
            if (expand == null || expand.length() == 0) {
                throw this.ioException("Configuration.Error.Line.line.system.property.value.expanded.to.empty.value", new Integer(this.linenum), s);
            }
            return expand;
        }
        
        private IOException ioException(final String s, final Object... array) {
            return new IOException(new MessageFormat(ResourcesMgr.getString(s, "sun.security.util.AuthResources")).format(array));
        }
        
        static {
            Spi.debugConfig = Debug.getInstance("configfile");
            Spi.debugParser = Debug.getInstance("configparser");
        }
    }
}
