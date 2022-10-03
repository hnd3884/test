package javax.management.loading;

import java.io.File;
import java.net.URLConnection;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.io.Reader;

class MLetParser
{
    private int c;
    private static String tag;
    
    public MLetParser() {
    }
    
    public void skipSpace(final Reader reader) throws IOException {
        while (this.c >= 0 && (this.c == 32 || this.c == 9 || this.c == 10 || this.c == 13)) {
            this.c = reader.read();
        }
    }
    
    public String scanIdentifier(final Reader reader) throws IOException {
        final StringBuilder sb = new StringBuilder();
        while ((this.c >= 97 && this.c <= 122) || (this.c >= 65 && this.c <= 90) || (this.c >= 48 && this.c <= 57) || this.c == 95) {
            sb.append((char)this.c);
            this.c = reader.read();
        }
        return sb.toString();
    }
    
    public Map<String, String> scanTag(final Reader reader) throws IOException {
        final HashMap hashMap = new HashMap();
        this.skipSpace(reader);
        while (this.c >= 0 && this.c != 62) {
            if (this.c == 60) {
                throw new IOException("Missing '>' in tag");
            }
            final String scanIdentifier = this.scanIdentifier(reader);
            String string = "";
            this.skipSpace(reader);
            if (this.c == 61) {
                int c = -1;
                this.c = reader.read();
                this.skipSpace(reader);
                if (this.c == 39 || this.c == 34) {
                    c = this.c;
                    this.c = reader.read();
                }
                final StringBuilder sb = new StringBuilder();
                while (this.c > 0 && ((c < 0 && this.c != 32 && this.c != 9 && this.c != 10 && this.c != 13 && this.c != 62) || (c >= 0 && this.c != c))) {
                    sb.append((char)this.c);
                    this.c = reader.read();
                }
                if (this.c == c) {
                    this.c = reader.read();
                }
                this.skipSpace(reader);
                string = sb.toString();
            }
            hashMap.put(scanIdentifier.toLowerCase(), string);
            this.skipSpace(reader);
        }
        return hashMap;
    }
    
    public List<MLetContent> parse(URL url) throws IOException {
        final String s = "parse";
        final String s2 = "<arg type=... value=...> tag requires type parameter.";
        final String s3 = "<arg type=... value=...> tag requires value parameter.";
        final String s4 = "<arg> tag outside <mlet> ... </mlet>.";
        final String s5 = "<mlet> tag requires either code or object parameter.";
        final String s6 = "<mlet> tag requires archive parameter.";
        final URLConnection openConnection = url.openConnection();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openConnection.getInputStream(), "UTF-8"));
        url = openConnection.getURL();
        final ArrayList list = new ArrayList();
        Map<String, String> scanTag = null;
        ArrayList list2 = new ArrayList();
        ArrayList list3 = new ArrayList();
        while (true) {
            this.c = bufferedReader.read();
            if (this.c == -1) {
                bufferedReader.close();
                return list;
            }
            if (this.c != 60) {
                continue;
            }
            this.c = bufferedReader.read();
            if (this.c == 47) {
                this.c = bufferedReader.read();
                final String scanIdentifier = this.scanIdentifier(bufferedReader);
                if (this.c != 62) {
                    throw new IOException("Missing '>' in tag");
                }
                if (!scanIdentifier.equalsIgnoreCase(MLetParser.tag)) {
                    continue;
                }
                if (scanTag != null) {
                    list.add(new MLetContent(url, scanTag, list2, list3));
                }
                scanTag = null;
                list2 = new ArrayList();
                list3 = new ArrayList();
            }
            else {
                final String scanIdentifier2 = this.scanIdentifier(bufferedReader);
                if (scanIdentifier2.equalsIgnoreCase("arg")) {
                    final Map<String, String> scanTag2 = this.scanTag(bufferedReader);
                    final String s7 = scanTag2.get("type");
                    if (s7 == null) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), s, s2);
                        throw new IOException(s2);
                    }
                    if (scanTag == null) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), s, s4);
                        throw new IOException(s4);
                    }
                    list2.add(s7);
                    final String s8 = scanTag2.get("value");
                    if (s8 == null) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), s, s3);
                        throw new IOException(s3);
                    }
                    if (scanTag == null) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), s, s4);
                        throw new IOException(s4);
                    }
                    list3.add(s8);
                }
                else {
                    if (!scanIdentifier2.equalsIgnoreCase(MLetParser.tag)) {
                        continue;
                    }
                    scanTag = this.scanTag(bufferedReader);
                    if (scanTag.get("code") == null && scanTag.get("object") == null) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), s, s5);
                        throw new IOException(s5);
                    }
                    if (scanTag.get("archive") == null) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), s, s6);
                        throw new IOException(s6);
                    }
                    continue;
                }
            }
        }
    }
    
    public List<MLetContent> parseURL(final String s) throws IOException {
        URL url;
        if (s.indexOf(58) <= 1) {
            final String property = System.getProperty("user.dir");
            String s2;
            if (property.charAt(0) == '/' || property.charAt(0) == File.separatorChar) {
                s2 = "file:";
            }
            else {
                s2 = "file:/";
            }
            url = new URL(new URL(s2 + property.replace(File.separatorChar, '/') + "/"), s);
        }
        else {
            url = new URL(s);
        }
        return this.parse(url);
    }
    
    static {
        MLetParser.tag = "mlet";
    }
}
