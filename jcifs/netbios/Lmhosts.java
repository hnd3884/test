package jcifs.netbios;

import jcifs.Config;
import java.io.InputStream;
import java.io.InputStreamReader;
import jcifs.smb.SmbFileInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.FileReader;
import java.io.File;
import jcifs.util.LogStream;
import java.util.Hashtable;

public class Lmhosts
{
    private static final String FILENAME;
    private static final Hashtable TAB;
    private static long lastModified;
    private static int alt;
    private static LogStream log;
    
    public static synchronized NbtAddress getByName(final String host) {
        return getByName(new Name(host, 32, null));
    }
    
    static synchronized NbtAddress getByName(final Name name) {
        NbtAddress result = null;
        try {
            if (Lmhosts.FILENAME != null) {
                final File f = new File(Lmhosts.FILENAME);
                final long lm;
                if ((lm = f.lastModified()) > Lmhosts.lastModified) {
                    Lmhosts.lastModified = lm;
                    Lmhosts.TAB.clear();
                    Lmhosts.alt = 0;
                    populate(new FileReader(f));
                }
                result = Lmhosts.TAB.get(name);
            }
        }
        catch (final FileNotFoundException fnfe) {
            final LogStream log = Lmhosts.log;
            if (LogStream.level > 1) {
                Lmhosts.log.println("lmhosts file: " + Lmhosts.FILENAME);
                fnfe.printStackTrace(Lmhosts.log);
            }
        }
        catch (final IOException ioe) {
            final LogStream log2 = Lmhosts.log;
            if (LogStream.level > 0) {
                ioe.printStackTrace(Lmhosts.log);
            }
        }
        return result;
    }
    
    static void populate(final Reader r) throws IOException {
        final BufferedReader br = new BufferedReader(r);
        String line;
        while ((line = br.readLine()) != null) {
            line = line.toUpperCase().trim();
            if (line.length() == 0) {
                continue;
            }
            if (line.charAt(0) == '#') {
                if (line.startsWith("#INCLUDE ")) {
                    line = line.substring(line.indexOf(92));
                    final String url = "smb:" + line.replace('\\', '/');
                    if (Lmhosts.alt > 0) {
                        try {
                            populate(new InputStreamReader(new SmbFileInputStream(url)));
                        }
                        catch (final IOException ioe) {
                            Lmhosts.log.println("lmhosts URL: " + url);
                            ioe.printStackTrace(Lmhosts.log);
                            continue;
                        }
                        --Lmhosts.alt;
                        while ((line = br.readLine()) != null) {
                            line = line.toUpperCase().trim();
                            if (line.startsWith("#END_ALTERNATE")) {
                                break;
                            }
                        }
                    }
                    else {
                        populate(new InputStreamReader(new SmbFileInputStream(url)));
                    }
                }
                else if (line.startsWith("#BEGIN_ALTERNATE")) {
                    ++Lmhosts.alt;
                }
                else {
                    if (line.startsWith("#END_ALTERNATE") && Lmhosts.alt > 0) {
                        --Lmhosts.alt;
                        throw new IOException("no lmhosts alternate includes loaded");
                    }
                    continue;
                }
            }
            else {
                if (!Character.isDigit(line.charAt(0))) {
                    continue;
                }
                char[] data;
                char c;
                int ip;
                int i;
                int b;
                for (data = line.toCharArray(), c = '.', i = (ip = 0); i < data.length && c == '.'; ++i) {
                    b = 0;
                    while (i < data.length && (c = data[i]) >= '0' && c <= '9') {
                        b = b * 10 + c - 48;
                        ++i;
                    }
                    ip = (ip << 8) + b;
                }
                while (i < data.length && Character.isWhitespace(data[i])) {
                    ++i;
                }
                int j;
                for (j = i; j < data.length && !Character.isWhitespace(data[j]); ++j) {}
                final Name name = new Name(line.substring(i, j), 32, null);
                final NbtAddress addr = new NbtAddress(name, ip, false, 0, false, false, true, true, NbtAddress.UNKNOWN_MAC_ADDRESS);
                Lmhosts.TAB.put(name, addr);
            }
        }
    }
    
    static {
        FILENAME = Config.getProperty("jcifs.netbios.lmhosts");
        TAB = new Hashtable();
        Lmhosts.lastModified = 1L;
        Lmhosts.log = LogStream.getInstance();
    }
}
