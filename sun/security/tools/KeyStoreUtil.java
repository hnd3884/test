package sun.security.tools;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.security.KeyStore;
import java.util.Locale;
import java.security.cert.X509Certificate;
import java.text.Collator;

public class KeyStoreUtil
{
    private static final String JKS = "jks";
    private static final Collator collator;
    
    private KeyStoreUtil() {
    }
    
    public static boolean isSelfSigned(final X509Certificate x509Certificate) {
        return signedBy(x509Certificate, x509Certificate);
    }
    
    public static boolean signedBy(final X509Certificate x509Certificate, final X509Certificate x509Certificate2) {
        if (!x509Certificate2.getSubjectX500Principal().equals(x509Certificate.getIssuerX500Principal())) {
            return false;
        }
        try {
            x509Certificate.verify(x509Certificate2.getPublicKey());
            return true;
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    public static boolean isWindowsKeyStore(final String s) {
        return s != null && (s.equalsIgnoreCase("Windows-MY") || s.equalsIgnoreCase("Windows-ROOT"));
    }
    
    public static String niceStoreTypeName(final String s) {
        if (s.equalsIgnoreCase("Windows-MY")) {
            return "Windows-MY";
        }
        if (s.equalsIgnoreCase("Windows-ROOT")) {
            return "Windows-ROOT";
        }
        return s.toUpperCase(Locale.ENGLISH);
    }
    
    public static KeyStore getCacertsKeyStore() throws Exception {
        final String separator = File.separator;
        final File file = new File(System.getProperty("java.home") + separator + "lib" + separator + "security" + separator + "cacerts");
        if (!file.exists()) {
            return null;
        }
        KeyStore instance = null;
        try (final FileInputStream fileInputStream = new FileInputStream(file)) {
            instance = KeyStore.getInstance("jks");
            instance.load(fileInputStream, null);
        }
        return instance;
    }
    
    public static char[] getPassWithModifier(final String s, final String s2, final ResourceBundle resourceBundle) {
        if (s == null) {
            return s2.toCharArray();
        }
        if (KeyStoreUtil.collator.compare(s, "env") != 0) {
            if (KeyStoreUtil.collator.compare(s, "file") == 0) {
                try {
                    URL url;
                    try {
                        url = new URL(s2);
                    }
                    catch (final MalformedURLException ex) {
                        final File file = new File(s2);
                        if (!file.exists()) {
                            System.err.println(resourceBundle.getString("Cannot.find.file.") + s2);
                            return null;
                        }
                        url = file.toURI().toURL();
                    }
                    try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                        final String line = bufferedReader.readLine();
                        if (line == null) {
                            return new char[0];
                        }
                        return line.toCharArray();
                    }
                }
                catch (final IOException ex2) {
                    System.err.println(ex2);
                    return null;
                }
            }
            System.err.println(resourceBundle.getString("Unknown.password.type.") + s);
            return null;
        }
        final String getenv = System.getenv(s2);
        if (getenv == null) {
            System.err.println(resourceBundle.getString("Cannot.find.environment.variable.") + s2);
            return null;
        }
        return getenv.toCharArray();
    }
    
    static {
        (collator = Collator.getInstance()).setStrength(0);
    }
}
