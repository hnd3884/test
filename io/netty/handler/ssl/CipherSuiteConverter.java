package io.netty.handler.ssl;

import java.util.Collections;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import io.netty.util.internal.logging.InternalLogger;

public final class CipherSuiteConverter
{
    private static final InternalLogger logger;
    private static final Pattern JAVA_CIPHERSUITE_PATTERN;
    private static final Pattern OPENSSL_CIPHERSUITE_PATTERN;
    private static final Pattern JAVA_AES_CBC_PATTERN;
    private static final Pattern JAVA_AES_PATTERN;
    private static final Pattern OPENSSL_AES_CBC_PATTERN;
    private static final Pattern OPENSSL_AES_PATTERN;
    private static final ConcurrentMap<String, String> j2o;
    private static final ConcurrentMap<String, Map<String, String>> o2j;
    private static final Map<String, String> j2oTls13;
    private static final Map<String, Map<String, String>> o2jTls13;
    
    static void clearCache() {
        CipherSuiteConverter.j2o.clear();
        CipherSuiteConverter.o2j.clear();
    }
    
    static boolean isJ2OCached(final String key, final String value) {
        return value.equals(CipherSuiteConverter.j2o.get(key));
    }
    
    static boolean isO2JCached(final String key, final String protocol, final String value) {
        final Map<String, String> p2j = CipherSuiteConverter.o2j.get(key);
        return p2j != null && value.equals(p2j.get(protocol));
    }
    
    public static String toOpenSsl(final String javaCipherSuite, final boolean boringSSL) {
        final String converted = CipherSuiteConverter.j2o.get(javaCipherSuite);
        if (converted != null) {
            return converted;
        }
        return cacheFromJava(javaCipherSuite, boringSSL);
    }
    
    private static String cacheFromJava(final String javaCipherSuite, final boolean boringSSL) {
        final String converted = CipherSuiteConverter.j2oTls13.get(javaCipherSuite);
        if (converted != null) {
            return boringSSL ? converted : javaCipherSuite;
        }
        final String openSslCipherSuite = toOpenSslUncached(javaCipherSuite, boringSSL);
        if (openSslCipherSuite == null) {
            return null;
        }
        CipherSuiteConverter.j2o.putIfAbsent(javaCipherSuite, openSslCipherSuite);
        final String javaCipherSuiteSuffix = javaCipherSuite.substring(4);
        final Map<String, String> p2j = new HashMap<String, String>(4);
        p2j.put("", javaCipherSuiteSuffix);
        p2j.put("SSL", "SSL_" + javaCipherSuiteSuffix);
        p2j.put("TLS", "TLS_" + javaCipherSuiteSuffix);
        CipherSuiteConverter.o2j.put(openSslCipherSuite, p2j);
        CipherSuiteConverter.logger.debug("Cipher suite mapping: {} => {}", javaCipherSuite, openSslCipherSuite);
        return openSslCipherSuite;
    }
    
    static String toOpenSslUncached(final String javaCipherSuite, final boolean boringSSL) {
        final String converted = CipherSuiteConverter.j2oTls13.get(javaCipherSuite);
        if (converted != null) {
            return boringSSL ? converted : javaCipherSuite;
        }
        final Matcher m = CipherSuiteConverter.JAVA_CIPHERSUITE_PATTERN.matcher(javaCipherSuite);
        if (!m.matches()) {
            return null;
        }
        final String handshakeAlgo = toOpenSslHandshakeAlgo(m.group(1));
        final String bulkCipher = toOpenSslBulkCipher(m.group(2));
        final String hmacAlgo = toOpenSslHmacAlgo(m.group(3));
        if (handshakeAlgo.isEmpty()) {
            return bulkCipher + '-' + hmacAlgo;
        }
        if (bulkCipher.contains("CHACHA20")) {
            return handshakeAlgo + '-' + bulkCipher;
        }
        return handshakeAlgo + '-' + bulkCipher + '-' + hmacAlgo;
    }
    
    private static String toOpenSslHandshakeAlgo(String handshakeAlgo) {
        final boolean export = handshakeAlgo.endsWith("_EXPORT");
        if (export) {
            handshakeAlgo = handshakeAlgo.substring(0, handshakeAlgo.length() - 7);
        }
        if ("RSA".equals(handshakeAlgo)) {
            handshakeAlgo = "";
        }
        else if (handshakeAlgo.endsWith("_anon")) {
            handshakeAlgo = 'A' + handshakeAlgo.substring(0, handshakeAlgo.length() - 5);
        }
        if (export) {
            if (handshakeAlgo.isEmpty()) {
                handshakeAlgo = "EXP";
            }
            else {
                handshakeAlgo = "EXP-" + handshakeAlgo;
            }
        }
        return handshakeAlgo.replace('_', '-');
    }
    
    private static String toOpenSslBulkCipher(final String bulkCipher) {
        if (bulkCipher.startsWith("AES_")) {
            Matcher m = CipherSuiteConverter.JAVA_AES_CBC_PATTERN.matcher(bulkCipher);
            if (m.matches()) {
                return m.replaceFirst("$1$2");
            }
            m = CipherSuiteConverter.JAVA_AES_PATTERN.matcher(bulkCipher);
            if (m.matches()) {
                return m.replaceFirst("$1$2-$3");
            }
        }
        if ("3DES_EDE_CBC".equals(bulkCipher)) {
            return "DES-CBC3";
        }
        if ("RC4_128".equals(bulkCipher) || "RC4_40".equals(bulkCipher)) {
            return "RC4";
        }
        if ("DES40_CBC".equals(bulkCipher) || "DES_CBC_40".equals(bulkCipher)) {
            return "DES-CBC";
        }
        if ("RC2_CBC_40".equals(bulkCipher)) {
            return "RC2-CBC";
        }
        return bulkCipher.replace('_', '-');
    }
    
    private static String toOpenSslHmacAlgo(final String hmacAlgo) {
        return hmacAlgo;
    }
    
    public static String toJava(final String openSslCipherSuite, final String protocol) {
        Map<String, String> p2j = CipherSuiteConverter.o2j.get(openSslCipherSuite);
        if (p2j == null) {
            p2j = cacheFromOpenSsl(openSslCipherSuite);
            if (p2j == null) {
                return null;
            }
        }
        String javaCipherSuite = p2j.get(protocol);
        if (javaCipherSuite == null) {
            final String cipher = p2j.get("");
            if (cipher == null) {
                return null;
            }
            javaCipherSuite = protocol + '_' + cipher;
        }
        return javaCipherSuite;
    }
    
    private static Map<String, String> cacheFromOpenSsl(final String openSslCipherSuite) {
        final Map<String, String> converted = CipherSuiteConverter.o2jTls13.get(openSslCipherSuite);
        if (converted != null) {
            return converted;
        }
        final String javaCipherSuiteSuffix = toJavaUncached0(openSslCipherSuite, false);
        if (javaCipherSuiteSuffix == null) {
            return null;
        }
        final String javaCipherSuiteSsl = "SSL_" + javaCipherSuiteSuffix;
        final String javaCipherSuiteTls = "TLS_" + javaCipherSuiteSuffix;
        final Map<String, String> p2j = new HashMap<String, String>(4);
        p2j.put("", javaCipherSuiteSuffix);
        p2j.put("SSL", javaCipherSuiteSsl);
        p2j.put("TLS", javaCipherSuiteTls);
        CipherSuiteConverter.o2j.putIfAbsent(openSslCipherSuite, p2j);
        CipherSuiteConverter.j2o.putIfAbsent(javaCipherSuiteTls, openSslCipherSuite);
        CipherSuiteConverter.j2o.putIfAbsent(javaCipherSuiteSsl, openSslCipherSuite);
        CipherSuiteConverter.logger.debug("Cipher suite mapping: {} => {}", javaCipherSuiteTls, openSslCipherSuite);
        CipherSuiteConverter.logger.debug("Cipher suite mapping: {} => {}", javaCipherSuiteSsl, openSslCipherSuite);
        return p2j;
    }
    
    static String toJavaUncached(final String openSslCipherSuite) {
        return toJavaUncached0(openSslCipherSuite, true);
    }
    
    private static String toJavaUncached0(final String openSslCipherSuite, final boolean checkTls13) {
        if (checkTls13) {
            final Map<String, String> converted = CipherSuiteConverter.o2jTls13.get(openSslCipherSuite);
            if (converted != null) {
                return converted.get("TLS");
            }
        }
        final Matcher m = CipherSuiteConverter.OPENSSL_CIPHERSUITE_PATTERN.matcher(openSslCipherSuite);
        if (!m.matches()) {
            return null;
        }
        String handshakeAlgo = m.group(1);
        boolean export;
        if (handshakeAlgo == null) {
            handshakeAlgo = "";
            export = false;
        }
        else if (handshakeAlgo.startsWith("EXP-")) {
            handshakeAlgo = handshakeAlgo.substring(4);
            export = true;
        }
        else if ("EXP".equals(handshakeAlgo)) {
            handshakeAlgo = "";
            export = true;
        }
        else {
            export = false;
        }
        handshakeAlgo = toJavaHandshakeAlgo(handshakeAlgo, export);
        final String bulkCipher = toJavaBulkCipher(m.group(2), export);
        final String hmacAlgo = toJavaHmacAlgo(m.group(3));
        final String javaCipherSuite = handshakeAlgo + "_WITH_" + bulkCipher + '_' + hmacAlgo;
        return bulkCipher.contains("CHACHA20") ? (javaCipherSuite + "_SHA256") : javaCipherSuite;
    }
    
    private static String toJavaHandshakeAlgo(String handshakeAlgo, final boolean export) {
        if (handshakeAlgo.isEmpty()) {
            handshakeAlgo = "RSA";
        }
        else if ("ADH".equals(handshakeAlgo)) {
            handshakeAlgo = "DH_anon";
        }
        else if ("AECDH".equals(handshakeAlgo)) {
            handshakeAlgo = "ECDH_anon";
        }
        handshakeAlgo = handshakeAlgo.replace('-', '_');
        if (export) {
            return handshakeAlgo + "_EXPORT";
        }
        return handshakeAlgo;
    }
    
    private static String toJavaBulkCipher(final String bulkCipher, final boolean export) {
        if (bulkCipher.startsWith("AES")) {
            Matcher m = CipherSuiteConverter.OPENSSL_AES_CBC_PATTERN.matcher(bulkCipher);
            if (m.matches()) {
                return m.replaceFirst("$1_$2_CBC");
            }
            m = CipherSuiteConverter.OPENSSL_AES_PATTERN.matcher(bulkCipher);
            if (m.matches()) {
                return m.replaceFirst("$1_$2_$3");
            }
        }
        if ("DES-CBC3".equals(bulkCipher)) {
            return "3DES_EDE_CBC";
        }
        if ("RC4".equals(bulkCipher)) {
            if (export) {
                return "RC4_40";
            }
            return "RC4_128";
        }
        else if ("DES-CBC".equals(bulkCipher)) {
            if (export) {
                return "DES_CBC_40";
            }
            return "DES_CBC";
        }
        else {
            if (!"RC2-CBC".equals(bulkCipher)) {
                return bulkCipher.replace('-', '_');
            }
            if (export) {
                return "RC2_CBC_40";
            }
            return "RC2_CBC";
        }
    }
    
    private static String toJavaHmacAlgo(final String hmacAlgo) {
        return hmacAlgo;
    }
    
    static void convertToCipherStrings(final Iterable<String> cipherSuites, final StringBuilder cipherBuilder, final StringBuilder cipherTLSv13Builder, final boolean boringSSL) {
        for (final String c : cipherSuites) {
            if (c == null) {
                break;
            }
            String converted = toOpenSsl(c, boringSSL);
            if (converted == null) {
                converted = c;
            }
            if (!OpenSsl.isCipherSuiteAvailable(converted)) {
                throw new IllegalArgumentException("unsupported cipher suite: " + c + '(' + converted + ')');
            }
            if (SslUtils.isTLSv13Cipher(converted) || SslUtils.isTLSv13Cipher(c)) {
                cipherTLSv13Builder.append(converted);
                cipherTLSv13Builder.append(':');
            }
            else {
                cipherBuilder.append(converted);
                cipherBuilder.append(':');
            }
        }
        if (cipherBuilder.length() == 0 && cipherTLSv13Builder.length() == 0) {
            throw new IllegalArgumentException("empty cipher suites");
        }
        if (cipherBuilder.length() > 0) {
            cipherBuilder.setLength(cipherBuilder.length() - 1);
        }
        if (cipherTLSv13Builder.length() > 0) {
            cipherTLSv13Builder.setLength(cipherTLSv13Builder.length() - 1);
        }
    }
    
    private CipherSuiteConverter() {
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(CipherSuiteConverter.class);
        JAVA_CIPHERSUITE_PATTERN = Pattern.compile("^(?:TLS|SSL)_((?:(?!_WITH_).)+)_WITH_(.*)_(.*)$");
        OPENSSL_CIPHERSUITE_PATTERN = Pattern.compile("^(?:((?:(?:EXP-)?(?:(?:DHE|EDH|ECDH|ECDHE|SRP|RSA)-(?:DSS|RSA|ECDSA|PSK)|(?:ADH|AECDH|KRB5|PSK|SRP)))|EXP)-)?(.*)-(.*)$");
        JAVA_AES_CBC_PATTERN = Pattern.compile("^(AES)_([0-9]+)_CBC$");
        JAVA_AES_PATTERN = Pattern.compile("^(AES)_([0-9]+)_(.*)$");
        OPENSSL_AES_CBC_PATTERN = Pattern.compile("^(AES)([0-9]+)$");
        OPENSSL_AES_PATTERN = Pattern.compile("^(AES)([0-9]+)-(.*)$");
        j2o = PlatformDependent.newConcurrentHashMap();
        o2j = PlatformDependent.newConcurrentHashMap();
        final Map<String, String> j2oTls13Map = new HashMap<String, String>();
        j2oTls13Map.put("TLS_AES_128_GCM_SHA256", "AEAD-AES128-GCM-SHA256");
        j2oTls13Map.put("TLS_AES_256_GCM_SHA384", "AEAD-AES256-GCM-SHA384");
        j2oTls13Map.put("TLS_CHACHA20_POLY1305_SHA256", "AEAD-CHACHA20-POLY1305-SHA256");
        j2oTls13 = Collections.unmodifiableMap((Map<? extends String, ? extends String>)j2oTls13Map);
        final Map<String, Map<String, String>> o2jTls13Map = new HashMap<String, Map<String, String>>();
        o2jTls13Map.put("TLS_AES_128_GCM_SHA256", Collections.singletonMap("TLS", "TLS_AES_128_GCM_SHA256"));
        o2jTls13Map.put("TLS_AES_256_GCM_SHA384", Collections.singletonMap("TLS", "TLS_AES_256_GCM_SHA384"));
        o2jTls13Map.put("TLS_CHACHA20_POLY1305_SHA256", Collections.singletonMap("TLS", "TLS_CHACHA20_POLY1305_SHA256"));
        o2jTls13Map.put("AEAD-AES128-GCM-SHA256", Collections.singletonMap("TLS", "TLS_AES_128_GCM_SHA256"));
        o2jTls13Map.put("AEAD-AES256-GCM-SHA384", Collections.singletonMap("TLS", "TLS_AES_256_GCM_SHA384"));
        o2jTls13Map.put("AEAD-CHACHA20-POLY1305-SHA256", Collections.singletonMap("TLS", "TLS_CHACHA20_POLY1305_SHA256"));
        o2jTls13 = Collections.unmodifiableMap((Map<? extends String, ? extends Map<String, String>>)o2jTls13Map);
    }
}
