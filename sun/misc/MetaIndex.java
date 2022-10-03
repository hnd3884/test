package sun.misc;

import java.util.HashMap;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.Map;

public class MetaIndex
{
    private static volatile Map<File, MetaIndex> jarMap;
    private String[] contents;
    private boolean isClassOnlyJar;
    
    public static MetaIndex forJar(final File file) {
        return getJarMap().get(file);
    }
    
    public static synchronized void registerDirectory(File canonicalFile) {
        final File file = new File(canonicalFile, "meta-index");
        if (file.exists()) {
            try {
                final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String substring = null;
                boolean b = false;
                final ArrayList list = new ArrayList();
                final Map<File, MetaIndex> jarMap = getJarMap();
                canonicalFile = canonicalFile.getCanonicalFile();
                final String line = bufferedReader.readLine();
                if (line == null || !line.equals("% VERSION 2")) {
                    bufferedReader.close();
                    return;
                }
                String line2;
                while ((line2 = bufferedReader.readLine()) != null) {
                    switch (line2.charAt(0)) {
                        case '!':
                        case '#':
                        case '@': {
                            if (substring != null && list.size() > 0) {
                                jarMap.put(new File(canonicalFile, substring), new MetaIndex(list, b));
                                list.clear();
                            }
                            substring = line2.substring(2);
                            if (line2.charAt(0) == '!') {
                                b = true;
                                continue;
                            }
                            if (b) {
                                b = false;
                                continue;
                            }
                            continue;
                        }
                        case '%': {
                            continue;
                        }
                        default: {
                            list.add(line2);
                            continue;
                        }
                    }
                }
                if (substring != null && list.size() > 0) {
                    jarMap.put(new File(canonicalFile, substring), new MetaIndex(list, b));
                }
                bufferedReader.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    public boolean mayContain(final String s) {
        if (this.isClassOnlyJar && !s.endsWith(".class")) {
            return false;
        }
        final String[] contents = this.contents;
        for (int i = 0; i < contents.length; ++i) {
            if (s.startsWith(contents[i])) {
                return true;
            }
        }
        return false;
    }
    
    private MetaIndex(final List<String> list, final boolean isClassOnlyJar) throws IllegalArgumentException {
        if (list == null) {
            throw new IllegalArgumentException();
        }
        this.contents = list.toArray(new String[0]);
        this.isClassOnlyJar = isClassOnlyJar;
    }
    
    private static Map<File, MetaIndex> getJarMap() {
        if (MetaIndex.jarMap == null) {
            synchronized (MetaIndex.class) {
                if (MetaIndex.jarMap == null) {
                    MetaIndex.jarMap = new HashMap<File, MetaIndex>();
                }
            }
        }
        assert MetaIndex.jarMap != null;
        return MetaIndex.jarMap;
    }
}
