package org.openjsse.sun.security.ssl;

import java.io.IOException;
import java.io.InputStream;
import org.openjsse.sun.security.util.HexDumpEncoder;
import java.util.Iterator;
import java.security.cert.Extension;
import sun.security.x509.CertificateExtensions;
import sun.security.x509.X509CertInfo;
import sun.security.x509.X509CertImpl;
import java.security.cert.X509Certificate;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.nio.ByteBuffer;
import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.util.Date;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import sun.security.action.GetPropertyAction;
import java.util.Locale;

public final class SSLLogger
{
    private static final SSLConsoleLogger logger;
    private static final String property;
    public static final boolean isOn;
    
    private static void help() {
        System.err.println();
        System.err.println("help           print the help messages");
        System.err.println("expand         expand debugging information");
        System.err.println();
        System.err.println("all            turn on all debugging");
        System.err.println("ssl            turn on ssl debugging");
        System.err.println();
        System.err.println("The following can be used with ssl:");
        System.err.println("\trecord       enable per-record tracing");
        System.err.println("\thandshake    print each handshake message");
        System.err.println("\tkeygen       print key generation data");
        System.err.println("\tsession      print session activity");
        System.err.println("\tdefaultctx   print default SSL initialization");
        System.err.println("\tsslctx       print SSLContext tracing");
        System.err.println("\tsessioncache print session cache tracing");
        System.err.println("\tkeymanager   print key manager tracing");
        System.err.println("\ttrustmanager print trust manager tracing");
        System.err.println("\tpluggability print pluggability tracing");
        System.err.println();
        System.err.println("\thandshake debugging can be widened with:");
        System.err.println("\tdata         hex dump of each handshake message");
        System.err.println("\tverbose      verbose handshake message printing");
        System.err.println();
        System.err.println("\trecord debugging can be widened with:");
        System.err.println("\tplaintext    hex dump of record plaintext");
        System.err.println("\tpacket       print raw SSL/TLS packets");
        System.err.println();
        System.exit(0);
    }
    
    public static boolean isOn(final String checkPoints) {
        if (SSLLogger.property == null) {
            return false;
        }
        if (SSLLogger.property.isEmpty()) {
            return true;
        }
        final String[] split;
        final String[] options = split = checkPoints.split(",");
        for (String option : split) {
            option = option.trim();
            if (!hasOption(option)) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean hasOption(String option) {
        option = option.toLowerCase(Locale.ENGLISH);
        if (SSLLogger.property.contains("all")) {
            return true;
        }
        final int offset = SSLLogger.property.indexOf("ssl");
        return (offset != -1 && SSLLogger.property.indexOf("sslctx", offset) != -1 && !option.equals("data") && !option.equals("packet") && !option.equals("plaintext")) || SSLLogger.property.contains(option);
    }
    
    public static void severe(final String msg, final Object... params) {
        log(Level.ERROR, msg, params);
    }
    
    public static void warning(final String msg, final Object... params) {
        log(Level.WARNING, msg, params);
    }
    
    public static void info(final String msg, final Object... params) {
        log(Level.INFO, msg, params);
    }
    
    public static void fine(final String msg, final Object... params) {
        log(Level.DEBUG, msg, params);
    }
    
    public static void finer(final String msg, final Object... params) {
        log(Level.TRACE, msg, params);
    }
    
    public static void finest(final String msg, final Object... params) {
        log(Level.ALL, msg, params);
    }
    
    private static void log(final Level level, final String msg, final Object... params) {
        if (SSLLogger.logger.isLoggable(level)) {
            if (params == null || params.length == 0) {
                SSLLogger.logger.log(level, msg, new Object[0]);
            }
            else {
                try {
                    final String formatted = formatParameters(params);
                    SSLLogger.logger.log(level, msg, formatted);
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    static String toString(final Object... params) {
        try {
            return formatParameters(params);
        }
        catch (final Exception exp) {
            return "unexpected exception thrown: " + exp.getMessage();
        }
    }
    
    static {
        final String p = GetPropertyAction.privilegedGetProperty("javax.net.debug");
        if (p != null) {
            if (p.isEmpty()) {
                property = "";
                logger = new SSLConsoleLogger("javax.net.ssl", p);
            }
            else {
                property = p.toLowerCase(Locale.ENGLISH);
                if (SSLLogger.property.equals("help")) {
                    help();
                }
                logger = new SSLConsoleLogger("javax.net.ssl", p);
            }
            isOn = true;
        }
        else {
            property = null;
            logger = null;
            isOn = false;
        }
    }
    
    public enum Level
    {
        ALL(Integer.MIN_VALUE), 
        TRACE(400), 
        DEBUG(500), 
        INFO(800), 
        WARNING(900), 
        ERROR(1000), 
        OFF(Integer.MAX_VALUE);
        
        private final int severity;
        
        private Level(final int severity) {
            this.severity = severity;
        }
        
        public final String getName() {
            return this.name();
        }
        
        public final int getSeverity() {
            return this.severity;
        }
    }
    
    private static class SSLConsoleLogger
    {
        private final String loggerName;
        private final boolean useCompactFormat;
        
        SSLConsoleLogger(final String loggerName, String options) {
            this.loggerName = loggerName;
            options = options.toLowerCase(Locale.ENGLISH);
            this.useCompactFormat = !options.contains("expand");
        }
        
        public String getName() {
            return this.loggerName;
        }
        
        public boolean isLoggable(final Level level) {
            return level != Level.OFF;
        }
        
        public void log(final Level level, final String message, final Object... params) {
            if (this.isLoggable(level)) {
                try {
                    final String formatted = format(this, level, message, params);
                    System.err.write(formatted.getBytes("UTF-8"));
                }
                catch (final Exception ex) {}
            }
        }
        
        public void log(final Level level, final ResourceBundle rb, final String message, final Throwable thrwbl) {
            if (this.isLoggable(level)) {
                try {
                    final String formatted = format(this, level, message, new Object[] { thrwbl });
                    System.err.write(formatted.getBytes("UTF-8"));
                }
                catch (final Exception ex) {}
            }
        }
        
        public void log(final Level level, final ResourceBundle rb, final String message, final Object... params) {
            if (this.isLoggable(level)) {
                try {
                    final String formatted = format(this, level, message, params);
                    System.err.write(formatted.getBytes("UTF-8"));
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    private static class SSLSimpleFormatter
    {
        private static final ThreadLocal<SimpleDateFormat> dateFormat;
        private static final MessageFormat basicCertFormat;
        private static final MessageFormat extendedCertFormart;
        private static final MessageFormat messageFormatNoParas;
        private static final MessageFormat messageCompactFormatNoParas;
        private static final MessageFormat messageFormatWithParas;
        private static final MessageFormat messageCompactFormatWithParas;
        private static final MessageFormat keyObjectFormat;
        
        private static String format(final SSLConsoleLogger logger, final Level level, final String message, final Object... parameters) {
            if (parameters == null || parameters.length == 0) {
                final Object[] messageFields = { logger.loggerName, level.getName(), Utilities.toHexString(Thread.currentThread().getId()), Thread.currentThread().getName(), SSLSimpleFormatter.dateFormat.get().format(new Date(System.currentTimeMillis())), formatCaller(), message };
                if (logger.useCompactFormat) {
                    return SSLSimpleFormatter.messageCompactFormatNoParas.format(messageFields);
                }
                return SSLSimpleFormatter.messageFormatNoParas.format(messageFields);
            }
            else {
                final Object[] messageFields = { logger.loggerName, level.getName(), Utilities.toHexString(Thread.currentThread().getId()), Thread.currentThread().getName(), SSLSimpleFormatter.dateFormat.get().format(new Date(System.currentTimeMillis())), formatCaller(), message, logger.useCompactFormat ? formatParameters(parameters) : Utilities.indent(formatParameters(parameters)) };
                if (logger.useCompactFormat) {
                    return SSLSimpleFormatter.messageCompactFormatWithParas.format(messageFields);
                }
                return SSLSimpleFormatter.messageFormatWithParas.format(messageFields);
            }
        }
        
        private static String formatCaller() {
            final StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
            for (int i = 1; i < stElements.length; ++i) {
                final StackTraceElement ste = stElements[i];
                if (!ste.getClassName().startsWith(SSLLogger.class.getName()) && !ste.getClassName().startsWith("java.lang.System")) {
                    return ste.getFileName() + ":" + ste.getLineNumber();
                }
            }
            return "unknown caller";
        }
        
        private static String formatParameters(final Object... parameters) {
            final StringBuilder builder = new StringBuilder(512);
            boolean isFirst = true;
            for (final Object parameter : parameters) {
                if (isFirst) {
                    isFirst = false;
                }
                else {
                    builder.append(",\n");
                }
                if (parameter instanceof Throwable) {
                    builder.append(formatThrowable((Throwable)parameter));
                }
                else if (parameter instanceof Certificate) {
                    builder.append(formatCertificate((Certificate)parameter));
                }
                else if (parameter instanceof ByteArrayInputStream) {
                    builder.append(formatByteArrayInputStream((ByteArrayInputStream)parameter));
                }
                else if (parameter instanceof ByteBuffer) {
                    builder.append(formatByteBuffer((ByteBuffer)parameter));
                }
                else if (parameter instanceof byte[]) {
                    builder.append(formatByteArrayInputStream(new ByteArrayInputStream((byte[])parameter)));
                }
                else if (parameter instanceof Map.Entry) {
                    final Map.Entry<String, ?> mapParameter = (Map.Entry<String, ?>)parameter;
                    builder.append(formatMapEntry(mapParameter));
                }
                else {
                    builder.append(formatObject(parameter));
                }
            }
            return builder.toString();
        }
        
        private static String formatThrowable(final Throwable throwable) {
            final StringBuilder builder = new StringBuilder(512);
            final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            try (final PrintStream out = new PrintStream(bytesOut)) {
                throwable.printStackTrace(out);
                builder.append(Utilities.indent(bytesOut.toString()));
            }
            final Object[] fields = { "throwable", builder.toString() };
            return SSLSimpleFormatter.keyObjectFormat.format(fields);
        }
        
        private static String formatCertificate(final Certificate certificate) {
            if (!(certificate instanceof X509Certificate)) {
                return Utilities.indent(certificate.toString());
            }
            final StringBuilder builder = new StringBuilder(512);
            try {
                final X509CertImpl x509 = X509CertImpl.toImpl((X509Certificate)certificate);
                final X509CertInfo certInfo = (X509CertInfo)x509.get("x509.info");
                final CertificateExtensions certExts = (CertificateExtensions)certInfo.get("extensions");
                if (certExts == null) {
                    final Object[] certFields = { x509.getVersion(), Utilities.toHexString(x509.getSerialNumber().toByteArray()), x509.getSigAlgName(), x509.getIssuerX500Principal().toString(), SSLSimpleFormatter.dateFormat.get().format(x509.getNotBefore()), SSLSimpleFormatter.dateFormat.get().format(x509.getNotAfter()), x509.getSubjectX500Principal().toString(), x509.getPublicKey().getAlgorithm() };
                    builder.append(Utilities.indent(SSLSimpleFormatter.basicCertFormat.format(certFields)));
                }
                else {
                    final StringBuilder extBuilder = new StringBuilder(512);
                    boolean isFirst = true;
                    for (final Extension certExt : certExts.getAllExtensions()) {
                        if (isFirst) {
                            isFirst = false;
                        }
                        else {
                            extBuilder.append(",\n");
                        }
                        extBuilder.append("{\n" + Utilities.indent(certExt.toString()) + "\n}");
                    }
                    final Object[] certFields2 = { x509.getVersion(), Utilities.toHexString(x509.getSerialNumber().toByteArray()), x509.getSigAlgName(), x509.getIssuerX500Principal().toString(), SSLSimpleFormatter.dateFormat.get().format(x509.getNotBefore()), SSLSimpleFormatter.dateFormat.get().format(x509.getNotAfter()), x509.getSubjectX500Principal().toString(), x509.getPublicKey().getAlgorithm(), Utilities.indent(extBuilder.toString()) };
                    builder.append(Utilities.indent(SSLSimpleFormatter.extendedCertFormart.format(certFields2)));
                }
            }
            catch (final Exception ex) {}
            final Object[] fields = { "certificate", builder.toString() };
            return Utilities.indent(SSLSimpleFormatter.keyObjectFormat.format(fields));
        }
        
        private static String formatByteArrayInputStream(final ByteArrayInputStream bytes) {
            final StringBuilder builder = new StringBuilder(512);
            try (final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream()) {
                final HexDumpEncoder hexEncoder = new HexDumpEncoder();
                hexEncoder.encodeBuffer(bytes, bytesOut);
                builder.append(Utilities.indent(bytesOut.toString()));
            }
            catch (final IOException ex) {}
            return builder.toString();
        }
        
        private static String formatByteBuffer(final ByteBuffer byteBuffer) {
            final StringBuilder builder = new StringBuilder(512);
            try (final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream()) {
                final HexDumpEncoder hexEncoder = new HexDumpEncoder();
                hexEncoder.encodeBuffer(byteBuffer.duplicate(), bytesOut);
                builder.append(Utilities.indent(bytesOut.toString()));
            }
            catch (final IOException ex) {}
            return builder.toString();
        }
        
        private static String formatMapEntry(final Map.Entry<String, ?> entry) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            String formatted;
            if (value instanceof String) {
                formatted = "\"" + key + "\": \"" + (String)value + "\"";
            }
            else if (value instanceof String[]) {
                final StringBuilder builder = new StringBuilder(512);
                final String[] strings = (String[])value;
                builder.append("\"" + key + "\": [\n");
                for (final String string : strings) {
                    builder.append("      \"" + string + "\"");
                    if (string != strings[strings.length - 1]) {
                        builder.append(",");
                    }
                    builder.append("\n");
                }
                builder.append("      ]");
                formatted = builder.toString();
            }
            else if (value instanceof byte[]) {
                formatted = "\"" + key + "\": \"" + Utilities.toHexString((byte[])value) + "\"";
            }
            else if (value instanceof Byte) {
                formatted = "\"" + key + "\": \"" + Utilities.toHexString((byte)value) + "\"";
            }
            else {
                formatted = "\"" + key + "\": \"" + value.toString() + "\"";
            }
            return Utilities.indent(formatted);
        }
        
        private static String formatObject(final Object obj) {
            return obj.toString();
        }
        
        static {
            dateFormat = new ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    return new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS z", Locale.ENGLISH);
                }
            };
            basicCertFormat = new MessageFormat("\"version\"            : \"v{0}\",\n\"serial number\"      : \"{1}\",\n\"signature algorithm\": \"{2}\",\n\"issuer\"             : \"{3}\",\n\"not before\"         : \"{4}\",\n\"not  after\"         : \"{5}\",\n\"subject\"            : \"{6}\",\n\"subject public key\" : \"{7}\"\n", Locale.ENGLISH);
            extendedCertFormart = new MessageFormat("\"version\"            : \"v{0}\",\n\"serial number\"      : \"{1}\",\n\"signature algorithm\": \"{2}\",\n\"issuer\"             : \"{3}\",\n\"not before\"         : \"{4}\",\n\"not  after\"         : \"{5}\",\n\"subject\"            : \"{6}\",\n\"subject public key\" : \"{7}\",\n\"extensions\"         : [\n{8}\n]\n", Locale.ENGLISH);
            messageFormatNoParas = new MessageFormat("'{'\n  \"logger\"      : \"{0}\",\n  \"level\"       : \"{1}\",\n  \"thread id\"   : \"{2}\",\n  \"thread name\" : \"{3}\",\n  \"time\"        : \"{4}\",\n  \"caller\"      : \"{5}\",\n  \"message\"     : \"{6}\"\n'}'\n", Locale.ENGLISH);
            messageCompactFormatNoParas = new MessageFormat("{0}|{1}|{2}|{3}|{4}|{5}|{6}\n", Locale.ENGLISH);
            messageFormatWithParas = new MessageFormat("'{'\n  \"logger\"      : \"{0}\",\n  \"level\"       : \"{1}\",\n  \"thread id\"   : \"{2}\",\n  \"thread name\" : \"{3}\",\n  \"time\"        : \"{4}\",\n  \"caller\"      : \"{5}\",\n  \"message\"     : \"{6}\",\n  \"specifics\"   : [\n{7}\n  ]\n'}'\n", Locale.ENGLISH);
            messageCompactFormatWithParas = new MessageFormat("{0}|{1}|{2}|{3}|{4}|{5}|{6} (\n{7}\n)\n", Locale.ENGLISH);
            keyObjectFormat = new MessageFormat("\"{0}\" : '{'\n{1}'}'\n", Locale.ENGLISH);
        }
    }
}
