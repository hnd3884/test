package sun.security.provider;

import java.util.Locale;
import java.security.Provider;
import java.net.URISyntaxException;
import java.io.File;
import java.security.Security;
import java.net.MalformedURLException;
import java.io.Reader;
import java.io.InputStreamReader;
import sun.security.util.PolicyUtil;
import java.util.ArrayList;
import java.util.List;
import java.net.URI;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.security.DomainLoadStoreParameter;
import java.security.cert.CertificateException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Enumeration;
import java.util.Date;
import java.security.cert.Certificate;
import java.security.UnrecoverableKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.AbstractMap;
import java.security.KeyStoreException;
import java.util.Collection;
import java.security.Key;
import java.util.HashMap;
import java.security.KeyStore;
import java.util.Map;
import java.security.KeyStoreSpi;

abstract class DomainKeyStore extends KeyStoreSpi
{
    private static final String ENTRY_NAME_SEPARATOR = "entrynameseparator";
    private static final String KEYSTORE_PROVIDER_NAME = "keystoreprovidername";
    private static final String KEYSTORE_TYPE = "keystoretype";
    private static final String KEYSTORE_URI = "keystoreuri";
    private static final String KEYSTORE_PASSWORD_ENV = "keystorepasswordenv";
    private static final String REGEX_META = ".$|()[{^?*+\\";
    private static final String DEFAULT_STREAM_PREFIX = "iostream";
    private int streamCounter;
    private String entryNameSeparator;
    private String entryNameSeparatorRegEx;
    private static final String DEFAULT_KEYSTORE_TYPE;
    private final Map<String, KeyStore> keystores;
    
    DomainKeyStore() {
        this.streamCounter = 1;
        this.entryNameSeparator = " ";
        this.entryNameSeparatorRegEx = " ";
        this.keystores = new HashMap<String, KeyStore>();
    }
    
    abstract String convertAlias(final String p0);
    
    @Override
    public Key engineGetKey(final String s, final char[] array) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        final AbstractMap.SimpleEntry<String, Collection<KeyStore>> keystoresForReading = this.getKeystoresForReading(s);
        Key key = null;
        try {
            final String s2 = keystoresForReading.getKey();
            final Iterator iterator = keystoresForReading.getValue().iterator();
            while (iterator.hasNext()) {
                key = ((KeyStore)iterator.next()).getKey(s2, array);
                if (key != null) {
                    break;
                }
            }
        }
        catch (final KeyStoreException ex) {
            throw new IllegalStateException(ex);
        }
        return key;
    }
    
    @Override
    public Certificate[] engineGetCertificateChain(final String s) {
        final AbstractMap.SimpleEntry<String, Collection<KeyStore>> keystoresForReading = this.getKeystoresForReading(s);
        Certificate[] certificateChain = null;
        try {
            final String s2 = keystoresForReading.getKey();
            final Iterator iterator = keystoresForReading.getValue().iterator();
            while (iterator.hasNext()) {
                certificateChain = ((KeyStore)iterator.next()).getCertificateChain(s2);
                if (certificateChain != null) {
                    break;
                }
            }
        }
        catch (final KeyStoreException ex) {
            throw new IllegalStateException(ex);
        }
        return certificateChain;
    }
    
    @Override
    public Certificate engineGetCertificate(final String s) {
        final AbstractMap.SimpleEntry<String, Collection<KeyStore>> keystoresForReading = this.getKeystoresForReading(s);
        Certificate certificate = null;
        try {
            final String s2 = keystoresForReading.getKey();
            final Iterator iterator = keystoresForReading.getValue().iterator();
            while (iterator.hasNext()) {
                certificate = ((KeyStore)iterator.next()).getCertificate(s2);
                if (certificate != null) {
                    break;
                }
            }
        }
        catch (final KeyStoreException ex) {
            throw new IllegalStateException(ex);
        }
        return certificate;
    }
    
    @Override
    public Date engineGetCreationDate(final String s) {
        final AbstractMap.SimpleEntry<String, Collection<KeyStore>> keystoresForReading = this.getKeystoresForReading(s);
        Date creationDate = null;
        try {
            final String s2 = keystoresForReading.getKey();
            final Iterator iterator = keystoresForReading.getValue().iterator();
            while (iterator.hasNext()) {
                creationDate = ((KeyStore)iterator.next()).getCreationDate(s2);
                if (creationDate != null) {
                    break;
                }
            }
        }
        catch (final KeyStoreException ex) {
            throw new IllegalStateException(ex);
        }
        return creationDate;
    }
    
    @Override
    public void engineSetKeyEntry(final String s, final Key key, final char[] array, final Certificate[] array2) throws KeyStoreException {
        final AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<String, KeyStore>> keystoreForWriting = this.getKeystoreForWriting(s);
        if (keystoreForWriting == null) {
            throw new KeyStoreException("Error setting key entry for '" + s + "'");
        }
        ((Map.Entry<K, KeyStore>)keystoreForWriting.getValue()).getValue().setKeyEntry(keystoreForWriting.getKey(), key, array, array2);
    }
    
    @Override
    public void engineSetKeyEntry(final String s, final byte[] array, final Certificate[] array2) throws KeyStoreException {
        final AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<String, KeyStore>> keystoreForWriting = this.getKeystoreForWriting(s);
        if (keystoreForWriting == null) {
            throw new KeyStoreException("Error setting protected key entry for '" + s + "'");
        }
        ((Map.Entry<K, KeyStore>)keystoreForWriting.getValue()).getValue().setKeyEntry(keystoreForWriting.getKey(), array, array2);
    }
    
    @Override
    public void engineSetCertificateEntry(final String s, final Certificate certificate) throws KeyStoreException {
        final AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<String, KeyStore>> keystoreForWriting = this.getKeystoreForWriting(s);
        if (keystoreForWriting == null) {
            throw new KeyStoreException("Error setting certificate entry for '" + s + "'");
        }
        ((Map.Entry<K, KeyStore>)keystoreForWriting.getValue()).getValue().setCertificateEntry(keystoreForWriting.getKey(), certificate);
    }
    
    @Override
    public void engineDeleteEntry(final String s) throws KeyStoreException {
        final AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<String, KeyStore>> keystoreForWriting = this.getKeystoreForWriting(s);
        if (keystoreForWriting == null) {
            throw new KeyStoreException("Error deleting entry for '" + s + "'");
        }
        ((Map.Entry<K, KeyStore>)keystoreForWriting.getValue()).getValue().deleteEntry(keystoreForWriting.getKey());
    }
    
    @Override
    public Enumeration<String> engineAliases() {
        return new Enumeration<String>() {
            private int index = 0;
            private Map.Entry<String, KeyStore> keystoresEntry = null;
            private String prefix = null;
            private Enumeration<String> aliases = null;
            final /* synthetic */ Iterator val$iterator = DomainKeyStore.this.keystores.entrySet().iterator();
            
            @Override
            public boolean hasMoreElements() {
                try {
                    if (this.aliases == null) {
                        if (!this.val$iterator.hasNext()) {
                            return false;
                        }
                        this.keystoresEntry = this.val$iterator.next();
                        this.prefix = this.keystoresEntry.getKey() + DomainKeyStore.this.entryNameSeparator;
                        this.aliases = this.keystoresEntry.getValue().aliases();
                    }
                    if (this.aliases.hasMoreElements()) {
                        return true;
                    }
                    if (!this.val$iterator.hasNext()) {
                        return false;
                    }
                    this.keystoresEntry = this.val$iterator.next();
                    this.prefix = this.keystoresEntry.getKey() + DomainKeyStore.this.entryNameSeparator;
                    this.aliases = this.keystoresEntry.getValue().aliases();
                }
                catch (final KeyStoreException ex) {
                    return false;
                }
                return this.aliases.hasMoreElements();
            }
            
            @Override
            public String nextElement() {
                if (this.hasMoreElements()) {
                    return this.prefix + this.aliases.nextElement();
                }
                throw new NoSuchElementException();
            }
        };
    }
    
    @Override
    public boolean engineContainsAlias(final String s) {
        final AbstractMap.SimpleEntry<String, Collection<KeyStore>> keystoresForReading = this.getKeystoresForReading(s);
        try {
            final String s2 = keystoresForReading.getKey();
            final Iterator iterator = keystoresForReading.getValue().iterator();
            while (iterator.hasNext()) {
                if (((KeyStore)iterator.next()).containsAlias(s2)) {
                    return true;
                }
            }
        }
        catch (final KeyStoreException ex) {
            throw new IllegalStateException(ex);
        }
        return false;
    }
    
    @Override
    public int engineSize() {
        int n = 0;
        try {
            final Iterator<KeyStore> iterator = this.keystores.values().iterator();
            while (iterator.hasNext()) {
                n += iterator.next().size();
            }
        }
        catch (final KeyStoreException ex) {
            throw new IllegalStateException(ex);
        }
        return n;
    }
    
    @Override
    public boolean engineIsKeyEntry(final String s) {
        final AbstractMap.SimpleEntry<String, Collection<KeyStore>> keystoresForReading = this.getKeystoresForReading(s);
        try {
            final String s2 = keystoresForReading.getKey();
            final Iterator iterator = keystoresForReading.getValue().iterator();
            while (iterator.hasNext()) {
                if (((KeyStore)iterator.next()).isKeyEntry(s2)) {
                    return true;
                }
            }
        }
        catch (final KeyStoreException ex) {
            throw new IllegalStateException(ex);
        }
        return false;
    }
    
    @Override
    public boolean engineIsCertificateEntry(final String s) {
        final AbstractMap.SimpleEntry<String, Collection<KeyStore>> keystoresForReading = this.getKeystoresForReading(s);
        try {
            final String s2 = keystoresForReading.getKey();
            final Iterator iterator = keystoresForReading.getValue().iterator();
            while (iterator.hasNext()) {
                if (((KeyStore)iterator.next()).isCertificateEntry(s2)) {
                    return true;
                }
            }
        }
        catch (final KeyStoreException ex) {
            throw new IllegalStateException(ex);
        }
        return false;
    }
    
    private AbstractMap.SimpleEntry<String, Collection<KeyStore>> getKeystoresForReading(final String s) {
        final String[] split = s.split(this.entryNameSeparatorRegEx, 2);
        if (split.length == 2) {
            final KeyStore keyStore = this.keystores.get(split[0]);
            if (keyStore != null) {
                return new AbstractMap.SimpleEntry<String, Collection<KeyStore>>(split[1], Collections.singleton(keyStore));
            }
        }
        else if (split.length == 1) {
            return new AbstractMap.SimpleEntry<String, Collection<KeyStore>>(s, this.keystores.values());
        }
        return new AbstractMap.SimpleEntry<String, Collection<KeyStore>>("", (Collection<KeyStore>)Collections.emptyList());
    }
    
    private AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<String, KeyStore>> getKeystoreForWriting(final String s) {
        final String[] split = s.split(this.entryNameSeparator, 2);
        if (split.length == 2) {
            final KeyStore keyStore = this.keystores.get(split[0]);
            if (keyStore != null) {
                return new AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<String, KeyStore>>(split[1], new AbstractMap.SimpleEntry<String, KeyStore>(split[0], keyStore));
            }
        }
        return null;
    }
    
    @Override
    public String engineGetCertificateAlias(final Certificate certificate) {
        try {
            String certificateAlias = null;
            final Iterator<KeyStore> iterator = this.keystores.values().iterator();
            while (iterator.hasNext() && (certificateAlias = iterator.next().getCertificateAlias(certificate)) == null) {}
            return certificateAlias;
        }
        catch (final KeyStoreException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
    @Override
    public void engineStore(final OutputStream outputStream, final char[] array) throws IOException, NoSuchAlgorithmException, CertificateException {
        try {
            if (this.keystores.size() == 1) {
                this.keystores.values().iterator().next().store(outputStream, array);
                return;
            }
        }
        catch (final KeyStoreException ex) {
            throw new IllegalStateException(ex);
        }
        throw new UnsupportedOperationException("This keystore must be stored using a DomainLoadStoreParameter");
    }
    
    @Override
    public void engineStore(final KeyStore.LoadStoreParameter loadStoreParameter) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (loadStoreParameter instanceof DomainLoadStoreParameter) {
            final DomainLoadStoreParameter domainLoadStoreParameter = (DomainLoadStoreParameter)loadStoreParameter;
            for (final KeyStoreBuilderComponents keyStoreBuilderComponents : this.getBuilders(domainLoadStoreParameter.getConfiguration(), domainLoadStoreParameter.getProtectionParams())) {
                try {
                    if (!(keyStoreBuilderComponents.protection instanceof KeyStore.PasswordProtection)) {
                        throw new KeyStoreException(new IllegalArgumentException("ProtectionParameter must be a KeyStore.PasswordProtection"));
                    }
                    final char[] password = ((KeyStore.PasswordProtection)keyStoreBuilderComponents.protection).getPassword();
                    final KeyStore keyStore = this.keystores.get(keyStoreBuilderComponents.name);
                    try (final FileOutputStream fileOutputStream = new FileOutputStream(keyStoreBuilderComponents.file)) {
                        keyStore.store(fileOutputStream, password);
                    }
                }
                catch (final KeyStoreException ex) {
                    throw new IOException(ex);
                }
            }
            return;
        }
        throw new UnsupportedOperationException("This keystore must be stored using a DomainLoadStoreParameter");
    }
    
    @Override
    public void engineLoad(final InputStream inputStream, final char[] array) throws IOException, NoSuchAlgorithmException, CertificateException {
        try {
            KeyStore keyStore;
            try {
                keyStore = KeyStore.getInstance("JKS");
                keyStore.load(inputStream, array);
            }
            catch (final Exception ex) {
                if ("JKS".equalsIgnoreCase(DomainKeyStore.DEFAULT_KEYSTORE_TYPE)) {
                    throw ex;
                }
                keyStore = KeyStore.getInstance(DomainKeyStore.DEFAULT_KEYSTORE_TYPE);
                keyStore.load(inputStream, array);
            }
            this.keystores.put("iostream" + this.streamCounter++, keyStore);
        }
        catch (final Exception ex2) {
            throw new UnsupportedOperationException("This keystore must be loaded using a DomainLoadStoreParameter");
        }
    }
    
    @Override
    public void engineLoad(final KeyStore.LoadStoreParameter loadStoreParameter) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (loadStoreParameter instanceof DomainLoadStoreParameter) {
            final DomainLoadStoreParameter domainLoadStoreParameter = (DomainLoadStoreParameter)loadStoreParameter;
            for (final KeyStoreBuilderComponents keyStoreBuilderComponents : this.getBuilders(domainLoadStoreParameter.getConfiguration(), domainLoadStoreParameter.getProtectionParams())) {
                try {
                    if (keyStoreBuilderComponents.file != null) {
                        this.keystores.put(keyStoreBuilderComponents.name, KeyStore.Builder.newInstance(keyStoreBuilderComponents.type, keyStoreBuilderComponents.provider, keyStoreBuilderComponents.file, keyStoreBuilderComponents.protection).getKeyStore());
                    }
                    else {
                        this.keystores.put(keyStoreBuilderComponents.name, KeyStore.Builder.newInstance(keyStoreBuilderComponents.type, keyStoreBuilderComponents.provider, keyStoreBuilderComponents.protection).getKeyStore());
                    }
                }
                catch (final KeyStoreException ex) {
                    throw new IOException(ex);
                }
            }
            return;
        }
        throw new UnsupportedOperationException("This keystore must be loaded using a DomainLoadStoreParameter");
    }
    
    private List<KeyStoreBuilderComponents> getBuilders(final URI uri, final Map<String, KeyStore.ProtectionParameter> map) throws IOException {
        final PolicyParser policyParser = new PolicyParser(true);
        Collection<PolicyParser.DomainEntry> domainEntries = null;
        final ArrayList list = new ArrayList();
        final String fragment = uri.getFragment();
        try (final InputStreamReader inputStreamReader = new InputStreamReader(PolicyUtil.getInputStream(uri.toURL()), "UTF-8")) {
            policyParser.read(inputStreamReader);
            domainEntries = policyParser.getDomainEntries();
        }
        catch (final MalformedURLException ex) {
            throw new IOException(ex);
        }
        catch (final PolicyParser.ParsingException ex2) {
            throw new IOException(ex2);
        }
        for (final PolicyParser.DomainEntry domainEntry : domainEntries) {
            final Map<String, String> properties = domainEntry.getProperties();
            if (fragment != null && !fragment.equalsIgnoreCase(domainEntry.getName())) {
                continue;
            }
            if (properties.containsKey("entrynameseparator")) {
                this.entryNameSeparator = properties.get("entrynameseparator");
                final StringBuilder sb = new StringBuilder();
                for (int i = 0; i < this.entryNameSeparator.length(); ++i) {
                    final char char1 = this.entryNameSeparator.charAt(i);
                    if (".$|()[{^?*+\\".indexOf(char1) != -1) {
                        sb.append('\\');
                    }
                    sb.append(char1);
                }
                this.entryNameSeparatorRegEx = sb.toString();
            }
            for (final PolicyParser.KeyStoreEntry keyStoreEntry : domainEntry.getEntries()) {
                final String name = keyStoreEntry.getName();
                final HashMap hashMap = new HashMap(properties);
                hashMap.putAll((Map)keyStoreEntry.getProperties());
                String default_KEYSTORE_TYPE = DomainKeyStore.DEFAULT_KEYSTORE_TYPE;
                if (hashMap.containsKey("keystoretype")) {
                    default_KEYSTORE_TYPE = (String)hashMap.get("keystoretype");
                }
                Provider provider = null;
                if (hashMap.containsKey("keystoreprovidername")) {
                    final String s = (String)hashMap.get("keystoreprovidername");
                    provider = Security.getProvider(s);
                    if (provider == null) {
                        throw new IOException("Error locating JCE provider: " + s);
                    }
                }
                File file = null;
                if (hashMap.containsKey("keystoreuri")) {
                    final String s2 = (String)hashMap.get("keystoreuri");
                    try {
                        if (s2.startsWith("file://")) {
                            file = new File(new URI(s2));
                        }
                        else {
                            file = new File(s2);
                        }
                    }
                    catch (final URISyntaxException | IllegalArgumentException ex3) {
                        throw new IOException("Error processing keystore property: keystoreURI=\"" + s2 + "\"", (Throwable)ex3);
                    }
                }
                KeyStore.ProtectionParameter protectionParameter;
                if (map.containsKey(name)) {
                    protectionParameter = map.get(name);
                }
                else if (hashMap.containsKey("keystorepasswordenv")) {
                    final String s3 = (String)hashMap.get("keystorepasswordenv");
                    final String getenv = System.getenv(s3);
                    if (getenv == null) {
                        throw new IOException("Error processing keystore property: keystorePasswordEnv=\"" + s3 + "\"");
                    }
                    protectionParameter = new KeyStore.PasswordProtection(getenv.toCharArray());
                }
                else {
                    protectionParameter = new KeyStore.PasswordProtection(null);
                }
                list.add(new KeyStoreBuilderComponents(name, default_KEYSTORE_TYPE, provider, file, protectionParameter));
            }
            break;
        }
        if (list.isEmpty()) {
            throw new IOException("Error locating domain configuration data for: " + uri);
        }
        return list;
    }
    
    static {
        DEFAULT_KEYSTORE_TYPE = KeyStore.getDefaultType();
    }
    
    public static final class DKS extends DomainKeyStore
    {
        @Override
        String convertAlias(final String s) {
            return s.toLowerCase(Locale.ENGLISH);
        }
    }
    
    class KeyStoreBuilderComponents
    {
        String name;
        String type;
        Provider provider;
        File file;
        KeyStore.ProtectionParameter protection;
        
        KeyStoreBuilderComponents(final String name, final String type, final Provider provider, final File file, final KeyStore.ProtectionParameter protection) {
            this.name = name;
            this.type = type;
            this.provider = provider;
            this.file = file;
            this.protection = protection;
        }
    }
}
