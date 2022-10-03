package sun.net.www.http;

import java.util.Random;
import sun.util.logging.PlatformLogger;
import java.io.Writer;
import java.io.FileWriter;
import java.net.URL;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.security.AccessController;
import sun.net.NetProperties;
import java.security.PrivilegedAction;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.File;

public class HttpCapture
{
    private File file;
    private boolean incoming;
    private BufferedWriter out;
    private static boolean initialized;
    private static volatile ArrayList<Pattern> patterns;
    private static volatile ArrayList<String> capFiles;
    
    private static synchronized void init() {
        HttpCapture.initialized = true;
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return NetProperties.get("sun.net.http.captureRules");
            }
        });
        if (s != null && !s.isEmpty()) {
            BufferedReader bufferedReader;
            try {
                bufferedReader = new BufferedReader(new FileReader(s));
            }
            catch (final FileNotFoundException ex) {
                return;
            }
            try {
                for (String s2 = bufferedReader.readLine(); s2 != null; s2 = bufferedReader.readLine()) {
                    final String trim = s2.trim();
                    if (!trim.startsWith("#")) {
                        final String[] split = trim.split(",");
                        if (split.length == 2) {
                            if (HttpCapture.patterns == null) {
                                HttpCapture.patterns = new ArrayList<Pattern>();
                                HttpCapture.capFiles = new ArrayList<String>();
                            }
                            HttpCapture.patterns.add(Pattern.compile(split[0].trim()));
                            HttpCapture.capFiles.add(split[1].trim());
                        }
                    }
                }
            }
            catch (final IOException ex2) {}
            finally {
                try {
                    bufferedReader.close();
                }
                catch (final IOException ex3) {}
            }
        }
    }
    
    private static synchronized boolean isInitialized() {
        return HttpCapture.initialized;
    }
    
    private HttpCapture(final File file, final URL url) {
        this.file = null;
        this.incoming = true;
        this.out = null;
        this.file = file;
        try {
            (this.out = new BufferedWriter(new FileWriter(this.file, true))).write("URL: " + url + "\n");
        }
        catch (final IOException ex) {
            PlatformLogger.getLogger(HttpCapture.class.getName()).severe(null, ex);
        }
    }
    
    public synchronized void sent(final int n) throws IOException {
        if (this.incoming) {
            this.out.write("\n------>\n");
            this.incoming = false;
            this.out.flush();
        }
        this.out.write(n);
    }
    
    public synchronized void received(final int n) throws IOException {
        if (!this.incoming) {
            this.out.write("\n<------\n");
            this.incoming = true;
            this.out.flush();
        }
        this.out.write(n);
    }
    
    public synchronized void flush() throws IOException {
        this.out.flush();
    }
    
    public static HttpCapture getCapture(final URL url) {
        if (!isInitialized()) {
            init();
        }
        if (HttpCapture.patterns == null || HttpCapture.patterns.isEmpty()) {
            return null;
        }
        final String string = url.toString();
        for (int i = 0; i < HttpCapture.patterns.size(); ++i) {
            if (HttpCapture.patterns.get(i).matcher(string).find()) {
                final String s = HttpCapture.capFiles.get(i);
                File file;
                if (s.indexOf("%d") >= 0) {
                    final Random random = new Random();
                    do {
                        file = new File(s.replace("%d", Integer.toString(random.nextInt())));
                    } while (file.exists());
                }
                else {
                    file = new File(s);
                }
                return new HttpCapture(file, url);
            }
        }
        return null;
    }
    
    static {
        HttpCapture.initialized = false;
        HttpCapture.patterns = null;
        HttpCapture.capFiles = null;
    }
}
