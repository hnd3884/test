package org.apache.xmlbeans.impl.tool;

import java.io.Reader;
import java.io.StringReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.xmlbeans.SystemProperties;
import java.io.FilenameFilter;
import java.util.Enumeration;
import java.util.Comparator;
import java.util.Arrays;
import org.apache.xmlbeans.impl.schema.SchemaTypeSystemImpl;
import java.util.zip.ZipEntry;
import java.util.List;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.ArrayList;
import java.io.File;

public class Diff
{
    public static void main(final String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: diff <jarname1> <jarname2> to compare two jars");
            System.out.println("  or   diff <dirname1> <dirname2> to compare two dirs");
            return;
        }
        final File file1 = new File(args[0]);
        if (!file1.exists()) {
            System.out.println("File \"" + args[0] + "\" not found.");
            return;
        }
        final File file2 = new File(args[1]);
        if (!file2.exists()) {
            System.out.println("File \"" + args[1] + "\" not found.");
            return;
        }
        final List result = new ArrayList();
        if (file1.isDirectory()) {
            if (!file2.isDirectory()) {
                System.out.println("Both parameters have to be directories if the first parameter is a directory.");
                return;
            }
            dirsAsTypeSystems(file1, file2, result);
        }
        else {
            if (file2.isDirectory()) {
                System.out.println("Both parameters have to be jar files if the first parameter is a jar file.");
                return;
            }
            try {
                final JarFile jar1 = new JarFile(file1);
                final JarFile jar2 = new JarFile(file2);
                jarsAsTypeSystems(jar1, jar2, result);
            }
            catch (final IOException ioe) {
                ioe.printStackTrace();
            }
        }
        if (result.size() < 1) {
            System.out.println("No differences encountered.");
        }
        else {
            System.out.println("Differences:");
            for (int i = 0; i < result.size(); ++i) {
                System.out.println(result.get(i).toString());
            }
        }
    }
    
    public static void jarsAsTypeSystems(final JarFile jar1, final JarFile jar2, final List diffs) {
        final Enumeration entries1 = jar1.entries();
        final Enumeration entries2 = jar2.entries();
        final List list1 = new ArrayList();
        final List list2 = new ArrayList();
        while (entries1.hasMoreElements()) {
            final ZipEntry ze = entries1.nextElement();
            final String name = ze.getName();
            if (name.startsWith("schema" + SchemaTypeSystemImpl.METADATA_PACKAGE_GEN + "/system/s") && name.endsWith(".xsb")) {
                list1.add(ze);
            }
        }
        while (entries2.hasMoreElements()) {
            final ZipEntry ze = entries2.nextElement();
            final String name = ze.getName();
            if (name.startsWith("schema" + SchemaTypeSystemImpl.METADATA_PACKAGE_GEN + "/system/s") && name.endsWith(".xsb")) {
                list2.add(ze);
            }
        }
        final ZipEntry[] files1 = list1.toArray(new ZipEntry[list1.size()]);
        final ZipEntry[] files2 = list2.toArray(new ZipEntry[list2.size()]);
        final ZipEntryNameComparator comparator = new ZipEntryNameComparator();
        Arrays.sort(files1, comparator);
        Arrays.sort(files2, comparator);
        int i1 = 0;
        int i2 = 0;
        while (i1 < files1.length && i2 < files2.length) {
            final String name2 = files1[i1].getName();
            final String name3 = files2[i2].getName();
            final int dif = name2.compareTo(name3);
            if (dif == 0) {
                zipEntriesAsXsb(files1[i1], jar1, files2[i2], jar2, diffs);
                ++i1;
                ++i2;
            }
            else if (dif < 0) {
                diffs.add("Jar \"" + jar1.getName() + "\" contains an extra file: \"" + name2 + "\"");
                ++i1;
            }
            else {
                if (dif <= 0) {
                    continue;
                }
                diffs.add("Jar \"" + jar2.getName() + "\" contains an extra file: \"" + name3 + "\"");
                ++i2;
            }
        }
        while (i1 < files1.length) {
            diffs.add("Jar \"" + jar1.getName() + "\" contains an extra file: \"" + files1[i1].getName() + "\"");
            ++i1;
        }
        while (i2 < files2.length) {
            diffs.add("Jar \"" + jar2.getName() + "\" contains an extra file: \"" + files2[i2].getName() + "\"");
            ++i2;
        }
    }
    
    public static void dirsAsTypeSystems(File dir1, File dir2, final List diffs) {
        assert dir1.isDirectory() : "Parameters must be directories";
        assert dir2.isDirectory() : "Parameters must be directories";
        File temp1 = new File(dir1, "schema" + SchemaTypeSystemImpl.METADATA_PACKAGE_GEN + "/system");
        File temp2 = new File(dir2, "schema" + SchemaTypeSystemImpl.METADATA_PACKAGE_GEN + "/system");
        if (temp1.exists() && temp2.exists()) {
            final File[] files1 = temp1.listFiles();
            final File[] files2 = temp2.listFiles();
            if (files1.length == 1 && files2.length == 1) {
                temp1 = files1[0];
                temp2 = files2[0];
            }
            else {
                if (files1.length == 0) {
                    temp1 = null;
                }
                if (files2.length == 0) {
                    temp2 = null;
                }
                if (files1.length > 1) {
                    diffs.add("More than one typesystem found in dir \"" + dir1.getName() + "\"");
                    return;
                }
                if (files2.length > 1) {
                    diffs.add("More than one typesystem found in dir \"" + dir2.getName() + "\"");
                    return;
                }
            }
        }
        else {
            if (!temp1.exists()) {
                temp1 = null;
            }
            if (!temp2.exists()) {
                temp2 = null;
            }
        }
        if (temp1 == null && temp2 == null) {
            return;
        }
        if (temp1 == null || temp2 == null) {
            if (temp1 == null) {
                diffs.add("No typesystems found in dir \"" + dir1 + "\"");
            }
            if (temp2 == null) {
                diffs.add("No typesystems found in dir \"" + dir2 + "\"");
            }
            return;
        }
        dir1 = temp1;
        dir2 = temp2;
        final boolean diffIndex = isDiffIndex();
        final XsbFilenameFilter xsbName = new XsbFilenameFilter();
        final File[] files3 = dir1.listFiles(xsbName);
        final File[] files4 = dir2.listFiles(xsbName);
        final FileNameComparator comparator = new FileNameComparator();
        Arrays.sort(files3, comparator);
        Arrays.sort(files4, comparator);
        int i1 = 0;
        int i2 = 0;
        while (i1 < files3.length && i2 < files4.length) {
            final String name1 = files3[i1].getName();
            final String name2 = files4[i2].getName();
            final int dif = name1.compareTo(name2);
            if (dif == 0) {
                if (diffIndex || !files3[i1].getName().equals("index.xsb")) {
                    filesAsXsb(files3[i1], files4[i2], diffs);
                }
                ++i1;
                ++i2;
            }
            else if (dif < 0) {
                diffs.add("Dir \"" + dir1.getName() + "\" contains an extra file: \"" + name1 + "\"");
                ++i1;
            }
            else {
                if (dif <= 0) {
                    continue;
                }
                diffs.add("Dir \"" + dir2.getName() + "\" contains an extra file: \"" + name2 + "\"");
                ++i2;
            }
        }
        while (i1 < files3.length) {
            diffs.add("Dir \"" + dir1.getName() + "\" contains an extra file: \"" + files3[i1].getName() + "\"");
            ++i1;
        }
        while (i2 < files4.length) {
            diffs.add("Dir \"" + dir2.getName() + "\" contains an extra file: \"" + files4[i2].getName() + "\"");
            ++i2;
        }
    }
    
    private static boolean isDiffIndex() {
        final String prop = SystemProperties.getProperty("xmlbeans.diff.diffIndex");
        return prop == null || (!"0".equals(prop) && !"false".equalsIgnoreCase(prop));
    }
    
    public static void filesAsXsb(final File file1, final File file2, final List diffs) {
        assert file1.exists() : "File \"" + file1.getAbsolutePath() + "\" does not exist.";
        assert file2.exists() : "File \"" + file2.getAbsolutePath() + "\" does not exist.";
        try {
            final FileInputStream stream1 = new FileInputStream(file1);
            final FileInputStream stream2 = new FileInputStream(file2);
            streamsAsXsb(stream1, file1.getName(), stream2, file2.getName(), diffs);
        }
        catch (final FileNotFoundException fnfe) {}
        catch (final IOException ex) {}
    }
    
    public static void zipEntriesAsXsb(final ZipEntry file1, final JarFile jar1, final ZipEntry file2, final JarFile jar2, final List diffs) {
        try {
            final InputStream stream1 = jar1.getInputStream(file1);
            final InputStream stream2 = jar2.getInputStream(file2);
            streamsAsXsb(stream1, file1.getName(), stream2, file2.getName(), diffs);
        }
        catch (final IOException ex) {}
    }
    
    public static void streamsAsXsb(final InputStream stream1, final String name1, final InputStream stream2, final String name2, final List diffs) throws IOException {
        final ByteArrayOutputStream buf1 = new ByteArrayOutputStream();
        final ByteArrayOutputStream buf2 = new ByteArrayOutputStream();
        XsbDumper.dump(stream1, "", new PrintStream(buf1));
        XsbDumper.dump(stream2, "", new PrintStream(buf2));
        stream1.close();
        stream2.close();
        readersAsText(new StringReader(buf1.toString()), name1, new StringReader(buf2.toString()), name2, diffs);
    }
    
    public static void readersAsText(final Reader r1, final String name1, final Reader r2, final String name2, final List diffs) throws IOException {
        org.apache.xmlbeans.impl.util.Diff.readersAsText(r1, name1, r2, name2, diffs);
    }
    
    private static class XsbFilenameFilter implements FilenameFilter
    {
        @Override
        public boolean accept(final File dir, final String name) {
            return name.endsWith(".xsb");
        }
    }
    
    private static class ZipEntryNameComparator implements Comparator
    {
        @Override
        public boolean equals(final Object object) {
            return this == object;
        }
        
        @Override
        public int compare(final Object object1, final Object object2) {
            assert object1 instanceof ZipEntry : "Must pass in a java.util.zip.ZipEntry as argument";
            assert object2 instanceof ZipEntry : "Must pass in a java.util.zip.ZipEntry as argument";
            final String name1 = ((ZipEntry)object1).getName();
            final String name2 = ((ZipEntry)object2).getName();
            return name1.compareTo(name2);
        }
    }
    
    private static class FileNameComparator implements Comparator
    {
        @Override
        public boolean equals(final Object object) {
            return this == object;
        }
        
        @Override
        public int compare(final Object object1, final Object object2) {
            assert object1 instanceof File : "Must pass in a java.io.File as argument";
            assert object2 instanceof File : "Must pass in a java.io.File as argument";
            final String name1 = ((File)object1).getName();
            final String name2 = ((File)object2).getName();
            return name1.compareTo(name2);
        }
    }
}
