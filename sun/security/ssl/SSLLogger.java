package sun.security.ssl;

import java.io.IOException;
import java.io.InputStream;
import sun.misc.HexDumpEncoder;
import java.util.Iterator;
import java.security.cert.Extension;
import sun.security.x509.X509CertInfo;
import sun.security.x509.CertificateExtensions;
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
import java.util.logging.LogRecord;
import sun.security.action.GetPropertyAction;
import java.util.logging.Level;
import java.util.Locale;
import java.util.logging.Logger;

public final class SSLLogger
{
    private static final Logger logger;
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
    
    public static boolean isOn(final String s) {
        if (SSLLogger.property == null) {
            return false;
        }
        if (SSLLogger.property.isEmpty()) {
            return true;
        }
        final String[] split = s.split(",");
        for (int length = split.length, i = 0; i < length; ++i) {
            if (!hasOption(split[i].trim())) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean hasOption(String lowerCase) {
        lowerCase = lowerCase.toLowerCase(Locale.ENGLISH);
        if (SSLLogger.property.contains("all")) {
            return true;
        }
        final int index = SSLLogger.property.indexOf("ssl");
        return (index != -1 && SSLLogger.property.indexOf("sslctx", index) != -1 && !lowerCase.equals("data") && !lowerCase.equals("packet") && !lowerCase.equals("plaintext")) || SSLLogger.property.contains(lowerCase);
    }
    
    public static void severe(final String s, final Object... array) {
        log(Level.SEVERE, s, array);
    }
    
    public static void warning(final String s, final Object... array) {
        log(Level.WARNING, s, array);
    }
    
    public static void info(final String s, final Object... array) {
        log(Level.INFO, s, array);
    }
    
    public static void fine(final String s, final Object... array) {
        log(Level.FINE, s, array);
    }
    
    public static void finer(final String s, final Object... array) {
        log(Level.FINER, s, array);
    }
    
    public static void finest(final String s, final Object... array) {
        log(Level.ALL, s, array);
    }
    
    private static void log(final Level level, final String s, final Object... array) {
        if (SSLLogger.logger.isLoggable(level)) {
            if (array == null || array.length == 0) {
                SSLLogger.logger.log(level, s);
            }
            else {
                try {
                    SSLLogger.logger.log(level, s, formatParameters(array));
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    static String toString(final Object... array) {
        try {
            return formatParameters(array);
        }
        catch (final Exception ex) {
            return "unexpected exception thrown: " + ex.getMessage();
        }
    }
    
    static {
        final String privilegedGetProperty = GetPropertyAction.privilegedGetProperty("javax.net.debug");
        if (privilegedGetProperty != null) {
            if (privilegedGetProperty.isEmpty()) {
                property = "";
                logger = Logger.getLogger("javax.net.ssl");
            }
            else {
                property = privilegedGetProperty.toLowerCase(Locale.ENGLISH);
                if (SSLLogger.property.equals("help")) {
                    help();
                }
                logger = new SSLConsoleLogger("javax.net.ssl", privilegedGetProperty);
            }
            isOn = true;
        }
        else {
            property = null;
            logger = null;
            isOn = false;
        }
    }
    
    private static class SSLConsoleLogger extends Logger
    {
        private final String loggerName;
        private final boolean useCompactFormat;
        
        SSLConsoleLogger(final String loggerName, String lowerCase) {
            super(loggerName, null);
            this.loggerName = loggerName;
            lowerCase = lowerCase.toLowerCase(Locale.ENGLISH);
            this.useCompactFormat = !lowerCase.contains("expand");
        }
        
        @Override
        public String getName() {
            return this.loggerName;
        }
        
        @Override
        public boolean isLoggable(final Level level) {
            return level != Level.OFF;
        }
        
        @Override
        public void log(final LogRecord logRecord) {
            if (this.isLoggable(logRecord.getLevel())) {
                try {
                    String s;
                    if (logRecord.getThrown() != null) {
                        s = format(this, logRecord.getLevel(), logRecord.getMessage(), new Object[] { logRecord.getThrown() });
                    }
                    else {
                        s = format(this, logRecord.getLevel(), logRecord.getMessage(), logRecord.getParameters());
                    }
                    System.err.write(s.getBytes("UTF-8"));
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
        
        private static String format(final SSLConsoleLogger sslConsoleLogger, final Level level, final String s, final Object... array) {
            if (array == null || array.length == 0) {
                final Object[] array2 = { sslConsoleLogger.loggerName, level.getName(), Utilities.toHexString(Thread.currentThread().getId()), Thread.currentThread().getName(), SSLSimpleFormatter.dateFormat.get().format(new Date(System.currentTimeMillis())), formatCaller(), s };
                if (sslConsoleLogger.useCompactFormat) {
                    return SSLSimpleFormatter.messageCompactFormatNoParas.format(array2);
                }
                return SSLSimpleFormatter.messageFormatNoParas.format(array2);
            }
            else {
                final Object[] array3 = { sslConsoleLogger.loggerName, level.getName(), Utilities.toHexString(Thread.currentThread().getId()), Thread.currentThread().getName(), SSLSimpleFormatter.dateFormat.get().format(new Date(System.currentTimeMillis())), formatCaller(), s, sslConsoleLogger.useCompactFormat ? formatParameters(array) : Utilities.indent(formatParameters(array)) };
                if (sslConsoleLogger.useCompactFormat) {
                    return SSLSimpleFormatter.messageCompactFormatWithParas.format(array3);
                }
                return SSLSimpleFormatter.messageFormatWithParas.format(array3);
            }
        }
        
        private static String formatCaller() {
            final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (int i = 1; i < stackTrace.length; ++i) {
                final StackTraceElement stackTraceElement = stackTrace[i];
                if (!stackTraceElement.getClassName().startsWith(SSLLogger.class.getName()) && !stackTraceElement.getClassName().startsWith(Logger.class.getName())) {
                    return stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber();
                }
            }
            return "unknown caller";
        }
        
        private static String formatParameters(final Object... array) {
            final StringBuilder sb = new StringBuilder(512);
            int n = 1;
            for (final Object o : array) {
                if (n != 0) {
                    n = 0;
                }
                else {
                    sb.append(",\n");
                }
                if (o instanceof Throwable) {
                    sb.append(formatThrowable((Throwable)o));
                }
                else if (o instanceof Certificate) {
                    sb.append(formatCertificate((Certificate)o));
                }
                else if (o instanceof ByteArrayInputStream) {
                    sb.append(formatByteArrayInputStream((ByteArrayInputStream)o));
                }
                else if (o instanceof ByteBuffer) {
                    sb.append(formatByteBuffer((ByteBuffer)o));
                }
                else if (o instanceof byte[]) {
                    sb.append(formatByteArrayInputStream(new ByteArrayInputStream((byte[])o)));
                }
                else if (o instanceof Map.Entry) {
                    sb.append(formatMapEntry((Map.Entry<String, ?>)o));
                }
                else {
                    sb.append(formatObject(o));
                }
            }
            return sb.toString();
        }
        
        private static String formatThrowable(final Throwable t) {
            final StringBuilder sb = new StringBuilder(512);
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (final PrintStream printStream = new PrintStream(byteArrayOutputStream)) {
                t.printStackTrace(printStream);
                sb.append(Utilities.indent(byteArrayOutputStream.toString()));
            }
            return SSLSimpleFormatter.keyObjectFormat.format(new Object[] { "throwable", sb.toString() });
        }
        
        private static String formatCertificate(final Certificate certificate) {
            if (!(certificate instanceof X509Certificate)) {
                return Utilities.indent(certificate.toString());
            }
            final StringBuilder sb = new StringBuilder(512);
            try {
                final X509CertImpl impl = X509CertImpl.toImpl((X509Certificate)certificate);
                final CertificateExtensions certificateExtensions = (CertificateExtensions)((X509CertInfo)impl.get("x509.info")).get("extensions");
                if (certificateExtensions == null) {
                    sb.append(Utilities.indent(SSLSimpleFormatter.basicCertFormat.format(new Object[] { impl.getVersion(), Utilities.toHexString(impl.getSerialNumber().toByteArray()), impl.getSigAlgName(), impl.getIssuerX500Principal().toString(), SSLSimpleFormatter.dateFormat.get().format(impl.getNotBefore()), SSLSimpleFormatter.dateFormat.get().format(impl.getNotAfter()), impl.getSubjectX500Principal().toString(), impl.getPublicKey().getAlgorithm() })));
                }
                else {
                    final StringBuilder sb2 = new StringBuilder(512);
                    int n = 1;
                    for (final Extension extension : certificateExtensions.getAllExtensions()) {
                        if (n != 0) {
                            n = 0;
                        }
                        else {
                            sb2.append(",\n");
                        }
                        sb2.append("{\n" + Utilities.indent(extension.toString()) + "\n}");
                    }
                    sb.append(Utilities.indent(SSLSimpleFormatter.extendedCertFormart.format(new Object[] { impl.getVersion(), Utilities.toHexString(impl.getSerialNumber().toByteArray()), impl.getSigAlgName(), impl.getIssuerX500Principal().toString(), SSLSimpleFormatter.dateFormat.get().format(impl.getNotBefore()), SSLSimpleFormatter.dateFormat.get().format(impl.getNotAfter()), impl.getSubjectX500Principal().toString(), impl.getPublicKey().getAlgorithm(), Utilities.indent(sb2.toString()) })));
                }
            }
            catch (final Exception ex) {}
            return Utilities.indent(SSLSimpleFormatter.keyObjectFormat.format(new Object[] { "certificate", sb.toString() }));
        }
        
        private static String formatByteArrayInputStream(final ByteArrayInputStream byteArrayInputStream) {
            final StringBuilder sb = new StringBuilder(512);
            try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                new HexDumpEncoder().encodeBuffer(byteArrayInputStream, byteArrayOutputStream);
                sb.append(Utilities.indent(byteArrayOutputStream.toString()));
            }
            catch (final IOException ex) {}
            return sb.toString();
        }
        
        private static String formatByteBuffer(final ByteBuffer byteBuffer) {
            final StringBuilder sb = new StringBuilder(512);
            try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                new HexDumpEncoder().encodeBuffer(byteBuffer.duplicate(), byteArrayOutputStream);
                sb.append(Utilities.indent(byteArrayOutputStream.toString()));
            }
            catch (final IOException ex) {}
            return sb.toString();
        }
        
        private static String formatMapEntry(final Map.Entry<String, ?> entry) {
            final String s = entry.getKey();
            final Object value = entry.getValue();
            String s2;
            if (value instanceof String) {
                s2 = "\"" + s + "\": \"" + (String)value + "\"";
            }
            else if (value instanceof String[]) {
                final StringBuilder sb = new StringBuilder(512);
                final String[] array = (String[])value;
                sb.append("\"" + s + "\": [\n");
                for (final String s3 : array) {
                    sb.append("      \"" + s3 + "\"");
                    if (s3 != array[array.length - 1]) {
                        sb.append(",");
                    }
                    sb.append("\n");
                }
                sb.append("      ]");
                s2 = sb.toString();
            }
            else if (value instanceof byte[]) {
                s2 = "\"" + s + "\": \"" + Utilities.toHexString((byte[])value) + "\"";
            }
            else if (value instanceof Byte) {
                s2 = "\"" + s + "\": \"" + Utilities.toHexString((byte)value) + "\"";
            }
            else {
                s2 = "\"" + s + "\": \"" + value.toString() + "\"";
            }
            return Utilities.indent(s2);
        }
        
        private static String formatObject(final Object o) {
            return o.toString();
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
