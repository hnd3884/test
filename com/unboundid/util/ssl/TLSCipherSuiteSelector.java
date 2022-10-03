package com.unboundid.util.ssl;

import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.io.PrintStream;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import java.util.ArrayList;
import java.util.TreeMap;
import com.unboundid.util.ObjectPair;
import javax.net.ssl.SSLParameters;
import com.unboundid.ldap.sdk.LDAPRuntimeException;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import java.util.Collections;
import java.util.Collection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;
import javax.net.ssl.SSLContext;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import java.util.SortedSet;
import java.util.List;
import java.util.SortedMap;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.CommandLineTool;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class TLSCipherSuiteSelector extends CommandLineTool
{
    private static final TLSCipherSuiteSelector INSTANCE;
    private final SortedMap<String, List<String>> nonRecommendedCipherSuites;
    private final SortedSet<String> defaultCipherSuites;
    private final SortedSet<String> recommendedCipherSuites;
    private final SortedSet<String> supportedCipherSuites;
    private final String[] recommendedCipherSuiteArray;
    
    public static void main(final String... args) {
        final ResultCode resultCode = main(System.out, System.err, args);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final OutputStream out, final OutputStream err, final String... args) {
        final TLSCipherSuiteSelector tool = new TLSCipherSuiteSelector(out, err);
        return tool.runTool(args);
    }
    
    private TLSCipherSuiteSelector() {
        this(null, null);
    }
    
    public TLSCipherSuiteSelector(final OutputStream out, final OutputStream err) {
        super(out, err);
        try {
            final SSLContext sslContext = SSLContext.getDefault();
            final SSLParameters supportedParameters = sslContext.getSupportedSSLParameters();
            final TreeSet<String> supportedSet = new TreeSet<String>(TLSCipherSuiteComparator.getInstance());
            supportedSet.addAll(Arrays.asList(supportedParameters.getCipherSuites()));
            this.supportedCipherSuites = Collections.unmodifiableSortedSet(supportedSet);
            final SSLParameters defaultParameters = sslContext.getDefaultSSLParameters();
            final TreeSet<String> defaultSet = new TreeSet<String>(TLSCipherSuiteComparator.getInstance());
            defaultSet.addAll(Arrays.asList(defaultParameters.getCipherSuites()));
            this.defaultCipherSuites = Collections.unmodifiableSortedSet(supportedSet);
            final ObjectPair<SortedSet<String>, SortedMap<String, List<String>>> selectedPair = selectCipherSuites(supportedParameters.getCipherSuites());
            this.recommendedCipherSuites = Collections.unmodifiableSortedSet(selectedPair.getFirst());
            this.nonRecommendedCipherSuites = Collections.unmodifiableSortedMap((SortedMap<String, ? extends List<String>>)selectedPair.getSecond());
            this.recommendedCipherSuiteArray = this.recommendedCipherSuites.toArray(StaticUtils.NO_STRINGS);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPRuntimeException(new LDAPException(ResultCode.LOCAL_ERROR, SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_INIT_ERROR.get(StaticUtils.getExceptionMessage(e)), e));
        }
        final String debugProperty = StaticUtils.getSystemProperty("javax.net.debug");
        if (debugProperty != null && debugProperty.equals("all")) {
            System.err.println();
            System.err.println(this.getClass().getName() + " Results:");
            this.generateOutput(System.err);
            System.err.println();
        }
    }
    
    public static SortedSet<String> getSupportedCipherSuites() {
        return TLSCipherSuiteSelector.INSTANCE.supportedCipherSuites;
    }
    
    public static SortedSet<String> getDefaultCipherSuites() {
        return TLSCipherSuiteSelector.INSTANCE.defaultCipherSuites;
    }
    
    public static SortedSet<String> getRecommendedCipherSuites() {
        return TLSCipherSuiteSelector.INSTANCE.recommendedCipherSuites;
    }
    
    public static String[] getRecommendedCipherSuiteArray() {
        return TLSCipherSuiteSelector.INSTANCE.recommendedCipherSuiteArray.clone();
    }
    
    public static SortedMap<String, List<String>> getNonRecommendedCipherSuites() {
        return TLSCipherSuiteSelector.INSTANCE.nonRecommendedCipherSuites;
    }
    
    static ObjectPair<SortedSet<String>, SortedMap<String, List<String>>> selectCipherSuites(final String[] cipherSuiteArray) {
        final SortedSet<String> recommendedSet = new TreeSet<String>(TLSCipherSuiteComparator.getInstance());
        final SortedMap<String, List<String>> nonRecommendedMap = new TreeMap<String, List<String>>(TLSCipherSuiteComparator.getInstance());
        for (final String cipherSuiteName : cipherSuiteArray) {
            final String name = StaticUtils.toUpperCase(cipherSuiteName).replace('-', '_');
            if (name.endsWith("_SCSV")) {
                recommendedSet.add(cipherSuiteName);
            }
            else {
                final List<String> nonRecommendedReasons = new ArrayList<String>(5);
                if (name.startsWith("SSL_")) {
                    nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_LEGACY_SSL_PROTOCOL.get());
                }
                else if (name.startsWith("TLS_")) {
                    if (!name.startsWith("TLS_AES_") && !name.startsWith("TLS_CHACHA20_") && !name.startsWith("TLS_ECDHE_") && !name.startsWith("TLS_DHE_")) {
                        if (!name.startsWith("TLS_RSA_")) {
                            if (name.startsWith("TLS_ECDH_")) {
                                nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_KNOWN_KE_ALG.get("ECDH"));
                            }
                            else if (name.startsWith("TLS_DH_")) {
                                nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_KNOWN_KE_ALG.get("DH"));
                            }
                            else if (name.startsWith("TLS_KRB5_")) {
                                nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_KNOWN_KE_ALG.get("KRB5"));
                            }
                            else {
                                nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_UNKNOWN_KE_ALG.get());
                            }
                        }
                    }
                }
                else {
                    nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_UNRECOGNIZED_PROTOCOL.get());
                }
                if (name.contains("_PSK")) {
                    nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_PSK.get());
                }
                if (name.contains("_NULL")) {
                    nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_NULL_COMPONENT.get());
                }
                if (name.contains("_ANON")) {
                    nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_ANON_AUTH.get());
                }
                if (name.contains("_EXPORT")) {
                    nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_EXPORT_ENCRYPTION.get());
                }
                if (!name.contains("_AES")) {
                    if (!name.contains("_CHACHA20")) {
                        if (name.contains("_RC4")) {
                            nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_KNOWN_BE_ALG.get("RC4"));
                        }
                        else if (name.contains("_3DES")) {
                            nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_KNOWN_BE_ALG.get("3DES"));
                        }
                        else if (name.contains("_DES")) {
                            nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_KNOWN_BE_ALG.get("DES"));
                        }
                        else if (name.contains("_IDEA")) {
                            nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_KNOWN_BE_ALG.get("IDEA"));
                        }
                        else if (name.contains("_CAMELLIA")) {
                            nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_KNOWN_BE_ALG.get("Camellia"));
                        }
                        else if (name.contains("_ARIA")) {
                            nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_KNOWN_BE_ALG.get("ARIA"));
                        }
                        else {
                            nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_UNKNOWN_BE_ALG.get());
                        }
                    }
                }
                if (!name.endsWith("_SHA512") && !name.endsWith("_SHA384") && !name.endsWith("_SHA256")) {
                    if (!name.endsWith("_SHA")) {
                        if (name.endsWith("_MD5")) {
                            nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_KNOWN_DIGEST_ALG.get("MD5"));
                        }
                        else {
                            nonRecommendedReasons.add(SSLMessages.ERR_TLS_CIPHER_SUITE_SELECTOR_NON_RECOMMENDED_UNKNOWN_DIGEST_ALG.get());
                        }
                    }
                }
                if (nonRecommendedReasons.isEmpty()) {
                    recommendedSet.add(cipherSuiteName);
                }
                else {
                    nonRecommendedMap.put(cipherSuiteName, Collections.unmodifiableList((List<? extends String>)nonRecommendedReasons));
                }
            }
        }
        return new ObjectPair<SortedSet<String>, SortedMap<String, List<String>>>(recommendedSet, nonRecommendedMap);
    }
    
    @Override
    public String getToolName() {
        return "tls-cipher-suite-selector";
    }
    
    @Override
    public String getToolDescription() {
        return SSLMessages.INFO_TLS_CIPHER_SUITE_SELECTOR_TOOL_DESC.get();
    }
    
    @Override
    public String getToolVersion() {
        return "4.0.14";
    }
    
    @Override
    public void addToolArguments(final ArgumentParser parser) throws ArgumentException {
    }
    
    @Override
    public ResultCode doToolProcessing() {
        this.generateOutput(this.getOut());
        return ResultCode.SUCCESS;
    }
    
    private void generateOutput(final PrintStream s) {
        s.println("Supported TLS Cipher Suites:");
        for (final String cipherSuite : this.supportedCipherSuites) {
            s.println("* " + cipherSuite);
        }
        s.println();
        s.println("JVM-Default TLS Cipher Suites:");
        for (final String cipherSuite : this.defaultCipherSuites) {
            s.println("* " + cipherSuite);
        }
        s.println();
        s.println("Non-Recommended TLS Cipher Suites:");
        for (final Map.Entry<String, List<String>> e : this.nonRecommendedCipherSuites.entrySet()) {
            s.println("* " + e.getKey());
            for (final String reason : e.getValue()) {
                s.println("  - " + reason);
            }
        }
        s.println();
        s.println("Recommended TLS Cipher Suites:");
        for (final String cipherSuite : this.recommendedCipherSuites) {
            s.println("* " + cipherSuite);
        }
    }
    
    public static Set<String> selectSupportedCipherSuites(final Collection<String> potentialSuiteNames) {
        if (potentialSuiteNames == null) {
            return Collections.emptySet();
        }
        final int capacity = StaticUtils.computeMapCapacity(TLSCipherSuiteSelector.INSTANCE.supportedCipherSuites.size());
        final Map<String, String> supportedMap = new HashMap<String, String>(capacity);
        for (final String supportedSuite : TLSCipherSuiteSelector.INSTANCE.supportedCipherSuites) {
            supportedMap.put(StaticUtils.toUpperCase(supportedSuite).replace('-', '_'), supportedSuite);
        }
        final Set<String> selectedSet = new LinkedHashSet<String>(capacity);
        for (final String potentialSuite : potentialSuiteNames) {
            final String supportedName = supportedMap.get(StaticUtils.toUpperCase(potentialSuite).replace('-', '_'));
            if (supportedName != null) {
                selectedSet.add(supportedName);
            }
        }
        return Collections.unmodifiableSet((Set<? extends String>)selectedSet);
    }
    
    static {
        INSTANCE = new TLSCipherSuiteSelector();
    }
}
