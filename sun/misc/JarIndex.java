package sun.misc;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.Map;
import java.util.Vector;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.io.File;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import java.util.jar.JarFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.HashMap;

public class JarIndex
{
    private HashMap<String, LinkedList<String>> indexMap;
    private HashMap<String, LinkedList<String>> jarMap;
    private String[] jarFiles;
    public static final String INDEX_NAME = "META-INF/INDEX.LIST";
    private static final boolean metaInfFilenames;
    
    public JarIndex() {
        this.indexMap = new HashMap<String, LinkedList<String>>();
        this.jarMap = new HashMap<String, LinkedList<String>>();
    }
    
    public JarIndex(final InputStream inputStream) throws IOException {
        this();
        this.read(inputStream);
    }
    
    public JarIndex(final String[] jarFiles) throws IOException {
        this();
        this.parseJars(this.jarFiles = jarFiles);
    }
    
    public static JarIndex getJarIndex(final JarFile jarFile) throws IOException {
        return getJarIndex(jarFile, null);
    }
    
    public static JarIndex getJarIndex(final JarFile jarFile, final MetaIndex metaIndex) throws IOException {
        JarIndex jarIndex = null;
        if (metaIndex != null && !metaIndex.mayContain("META-INF/INDEX.LIST")) {
            return null;
        }
        final JarEntry jarEntry = jarFile.getJarEntry("META-INF/INDEX.LIST");
        if (jarEntry != null) {
            jarIndex = new JarIndex(jarFile.getInputStream(jarEntry));
        }
        return jarIndex;
    }
    
    public String[] getJarFiles() {
        return this.jarFiles;
    }
    
    private void addToList(final String s, final String s2, final HashMap<String, LinkedList<String>> hashMap) {
        final LinkedList list = hashMap.get(s);
        if (list == null) {
            final LinkedList list2 = new LinkedList();
            list2.add(s2);
            hashMap.put(s, list2);
        }
        else if (!list.contains(s2)) {
            list.add(s2);
        }
    }
    
    public LinkedList<String> get(final String s) {
        LinkedList list;
        final int lastIndex;
        if ((list = this.indexMap.get(s)) == null && (lastIndex = s.lastIndexOf("/")) != -1) {
            list = this.indexMap.get(s.substring(0, lastIndex));
        }
        return list;
    }
    
    public void add(final String s, final String s2) {
        final int lastIndex;
        String substring;
        if ((lastIndex = s.lastIndexOf("/")) != -1) {
            substring = s.substring(0, lastIndex);
        }
        else {
            substring = s;
        }
        this.addMapping(substring, s2);
    }
    
    private void addMapping(final String s, final String s2) {
        this.addToList(s, s2, this.indexMap);
        this.addToList(s2, s, this.jarMap);
    }
    
    private void parseJars(final String[] array) throws IOException {
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            final String s = array[i];
            final ZipFile zipFile = new ZipFile(s.replace('/', File.separatorChar));
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry zipEntry = (ZipEntry)entries.nextElement();
                final String name = zipEntry.getName();
                if (!name.equals("META-INF/") && !name.equals("META-INF/INDEX.LIST")) {
                    if (name.equals("META-INF/MANIFEST.MF")) {
                        continue;
                    }
                    if (!JarIndex.metaInfFilenames || !name.startsWith("META-INF/")) {
                        this.add(name, s);
                    }
                    else {
                        if (zipEntry.isDirectory()) {
                            continue;
                        }
                        this.addMapping(name, s);
                    }
                }
            }
            zipFile.close();
        }
    }
    
    public void write(final OutputStream outputStream) throws IOException {
        final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF8"));
        bufferedWriter.write("JarIndex-Version: 1.0\n\n");
        if (this.jarFiles != null) {
            for (int i = 0; i < this.jarFiles.length; ++i) {
                final String s = this.jarFiles[i];
                bufferedWriter.write(s + "\n");
                final LinkedList list = this.jarMap.get(s);
                if (list != null) {
                    final Iterator iterator = list.iterator();
                    while (iterator.hasNext()) {
                        bufferedWriter.write((String)iterator.next() + "\n");
                    }
                }
                bufferedWriter.write("\n");
            }
            bufferedWriter.flush();
        }
    }
    
    public void read(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));
        String s = null;
        final Vector vector = new Vector();
        String s2;
        while ((s2 = bufferedReader.readLine()) != null && !s2.endsWith(".jar")) {}
        while (s2 != null) {
            if (s2.length() != 0) {
                if (s2.endsWith(".jar")) {
                    s = s2;
                    vector.add(s);
                }
                else {
                    this.addMapping(s2, s);
                }
            }
            s2 = bufferedReader.readLine();
        }
        this.jarFiles = vector.toArray(new String[vector.size()]);
    }
    
    public void merge(final JarIndex jarIndex, final String s) {
        for (final Map.Entry entry : this.indexMap.entrySet()) {
            final String s2 = (String)entry.getKey();
            for (String concat : (LinkedList)entry.getValue()) {
                if (s != null) {
                    concat = s.concat(concat);
                }
                jarIndex.addMapping(s2, concat);
            }
        }
    }
    
    static {
        metaInfFilenames = "true".equals(AccessController.doPrivileged((PrivilegedAction<Object>)new GetPropertyAction("sun.misc.JarIndex.metaInfFilenames")));
    }
}
