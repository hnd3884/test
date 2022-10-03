package com.sun.java.util.jar.pack;

import java.util.SortedMap;
import java.util.ListIterator;
import java.util.TreeMap;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.io.IOException;
import java.io.FilterInputStream;
import java.io.Closeable;
import java.util.Iterator;
import java.util.jar.JarOutputStream;
import java.util.zip.GZIPInputStream;
import java.io.BufferedInputStream;
import java.util.zip.GZIPOutputStream;
import java.io.BufferedOutputStream;
import java.util.jar.JarFile;
import java.io.File;
import java.util.jar.Pack200;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

class Driver
{
    private static final ResourceBundle RESOURCE;
    private static final String PACK200_OPTION_MAP = "--repack                 $ \n  -r +>- @--repack              $ \n--no-gzip                $ \n  -g +>- @--no-gzip             $ \n--strip-debug            $ \n  -G +>- @--strip-debug         $ \n--no-keep-file-order     $ \n  -O +>- @--no-keep-file-order  $ \n--segment-limit=      *> = \n  -S +>  @--segment-limit=      = \n--effort=             *> = \n  -E +>  @--effort=             = \n--deflate-hint=       *> = \n  -H +>  @--deflate-hint=       = \n--modification-time=  *> = \n  -m +>  @--modification-time=  = \n--pass-file=        *> &\u0000 \n  -P +>  @--pass-file=        &\u0000 \n--unknown-attribute=  *> = \n  -U +>  @--unknown-attribute=  = \n--class-attribute=  *> &\u0000 \n  -C +>  @--class-attribute=  &\u0000 \n--field-attribute=  *> &\u0000 \n  -F +>  @--field-attribute=  &\u0000 \n--method-attribute= *> &\u0000 \n  -M +>  @--method-attribute= &\u0000 \n--code-attribute=   *> &\u0000 \n  -D +>  @--code-attribute=   &\u0000 \n--config-file=      *>   . \n  -f +>  @--config-file=        . \n--no-strip-debug  !--strip-debug         \n--gzip            !--no-gzip             \n--keep-file-order !--no-keep-file-order  \n--verbose                $ \n  -v +>- @--verbose             $ \n--quiet        !--verbose  \n  -q +>- !--verbose               \n--log-file=           *> = \n  -l +>  @--log-file=           = \n--version                . \n  -V +>  @--version             . \n--help               . \n  -? +> @--help . \n  -h +> @--help . \n--           . \n-   +?    >- . \n";
    private static final String UNPACK200_OPTION_MAP = "--deflate-hint=       *> = \n  -H +>  @--deflate-hint=       = \n--verbose                $ \n  -v +>- @--verbose             $ \n--quiet        !--verbose  \n  -q +>- !--verbose               \n--remove-pack-file       $ \n  -r +>- @--remove-pack-file    $ \n--log-file=           *> = \n  -l +>  @--log-file=           = \n--config-file=        *> . \n  -f +>  @--config-file=        . \n--           . \n-   +?    >- . \n--version                . \n  -V +>  @--version             . \n--help               . \n  -? +> @--help . \n  -h +> @--help . \n";
    private static final String[] PACK200_PROPERTY_TO_OPTION;
    private static final String[] UNPACK200_PROPERTY_TO_OPTION;
    
    public static void main(final String[] array) throws IOException {
        final ArrayList list = new ArrayList((Collection<? extends E>)Arrays.asList(array));
        boolean b = true;
        boolean b2 = false;
        int n = 0;
        int n2 = 1;
        String s = null;
        final String s2 = "com.sun.java.util.jar.pack.verbose";
        final String s3 = (String)(list.isEmpty() ? "" : list.get(0));
        switch (s3) {
            case "--pack": {
                list.remove(0);
                break;
            }
            case "--unpack": {
                list.remove(0);
                b = false;
                b2 = true;
                break;
            }
        }
        final HashMap hashMap = new HashMap();
        hashMap.put(s2, System.getProperty(s2));
        String s4;
        String[] array2;
        if (b) {
            s4 = "--repack                 $ \n  -r +>- @--repack              $ \n--no-gzip                $ \n  -g +>- @--no-gzip             $ \n--strip-debug            $ \n  -G +>- @--strip-debug         $ \n--no-keep-file-order     $ \n  -O +>- @--no-keep-file-order  $ \n--segment-limit=      *> = \n  -S +>  @--segment-limit=      = \n--effort=             *> = \n  -E +>  @--effort=             = \n--deflate-hint=       *> = \n  -H +>  @--deflate-hint=       = \n--modification-time=  *> = \n  -m +>  @--modification-time=  = \n--pass-file=        *> &\u0000 \n  -P +>  @--pass-file=        &\u0000 \n--unknown-attribute=  *> = \n  -U +>  @--unknown-attribute=  = \n--class-attribute=  *> &\u0000 \n  -C +>  @--class-attribute=  &\u0000 \n--field-attribute=  *> &\u0000 \n  -F +>  @--field-attribute=  &\u0000 \n--method-attribute= *> &\u0000 \n  -M +>  @--method-attribute= &\u0000 \n--code-attribute=   *> &\u0000 \n  -D +>  @--code-attribute=   &\u0000 \n--config-file=      *>   . \n  -f +>  @--config-file=        . \n--no-strip-debug  !--strip-debug         \n--gzip            !--no-gzip             \n--keep-file-order !--no-keep-file-order  \n--verbose                $ \n  -v +>- @--verbose             $ \n--quiet        !--verbose  \n  -q +>- !--verbose               \n--log-file=           *> = \n  -l +>  @--log-file=           = \n--version                . \n  -V +>  @--version             . \n--help               . \n  -? +> @--help . \n  -h +> @--help . \n--           . \n-   +?    >- . \n";
            array2 = Driver.PACK200_PROPERTY_TO_OPTION;
        }
        else {
            s4 = "--deflate-hint=       *> = \n  -H +>  @--deflate-hint=       = \n--verbose                $ \n  -v +>- @--verbose             $ \n--quiet        !--verbose  \n  -q +>- !--verbose               \n--remove-pack-file       $ \n  -r +>- @--remove-pack-file    $ \n--log-file=           *> = \n  -l +>  @--log-file=           = \n--config-file=        *> . \n  -f +>  @--config-file=        . \n--           . \n-   +?    >- . \n--version                . \n  -V +>  @--version             . \n--help               . \n  -? +> @--help . \n  -h +> @--help . \n";
            array2 = Driver.UNPACK200_PROPERTY_TO_OPTION;
        }
        final HashMap<Object, String> hashMap2 = new HashMap<Object, String>();
        try {
            String commandOptions;
            while (true) {
                commandOptions = parseCommandOptions(list, s4, (Map<String, String>)hashMap2);
                final Iterator<String> iterator = hashMap2.keySet().iterator();
                while (iterator.hasNext()) {
                    final String s5 = iterator.next();
                    String s6 = null;
                    for (int i = 0; i < array2.length; i += 2) {
                        if (s5.equals(array2[1 + i])) {
                            s6 = array2[0 + i];
                            break;
                        }
                    }
                    if (s6 != null) {
                        String s7 = hashMap2.get(s5);
                        iterator.remove();
                        if (!s6.endsWith(".")) {
                            if (!s5.equals("--verbose") && !s5.endsWith("=")) {
                                boolean b3 = s7 != null;
                                if (s5.startsWith("--no-")) {
                                    b3 = !b3;
                                }
                                s7 = (b3 ? "true" : "false");
                            }
                            hashMap.put(s6, s7);
                        }
                        else if (s6.contains(".attribute.")) {
                            final String[] split = s7.split("\u0000");
                            for (int length = split.length, j = 0; j < length; ++j) {
                                final String[] split2 = split[j].split("=", 2);
                                hashMap.put(s6 + split2[0], split2[1]);
                            }
                        }
                        else {
                            int n4 = 1;
                            for (final String s8 : s7.split("\u0000")) {
                                String string;
                                do {
                                    string = s6 + "cli." + n4++;
                                } while (hashMap.containsKey(string));
                                hashMap.put(string, s8);
                            }
                        }
                    }
                }
                if (!"--config-file=".equals(commandOptions)) {
                    break;
                }
                final String s9 = (String)list.remove(0);
                final Properties properties = new Properties();
                try (final FileInputStream fileInputStream = new FileInputStream(s9)) {
                    properties.load(fileInputStream);
                }
                if (hashMap.get(s2) != null) {
                    properties.list(System.out);
                }
                for (final Map.Entry entry : properties.entrySet()) {
                    hashMap.put(entry.getKey(), entry.getValue());
                }
            }
            if ("--version".equals(commandOptions)) {
                System.out.println(MessageFormat.format(Driver.RESOURCE.getString("VERSION"), Driver.class.getName(), "1.31, 07/05/05"));
                return;
            }
            if ("--help".equals(commandOptions)) {
                printUsage(b, true, System.out);
                System.exit(1);
                return;
            }
        }
        catch (final IllegalArgumentException ex) {
            System.err.println(MessageFormat.format(Driver.RESOURCE.getString("BAD_ARGUMENT"), ex));
            printUsage(b, false, System.err);
            System.exit(2);
            return;
        }
        for (final String s10 : hashMap2.keySet()) {
            final String s11 = hashMap2.get(s10);
            final String s12 = s10;
            switch (s12) {
                case "--repack": {
                    n = 1;
                    continue;
                }
                case "--no-gzip": {
                    n2 = ((s11 == null) ? 1 : 0);
                    continue;
                }
                case "--log-file=": {
                    s = s11;
                    continue;
                }
                default: {
                    throw new InternalError(MessageFormat.format(Driver.RESOURCE.getString("BAD_OPTION"), s10, hashMap2.get(s10)));
                }
            }
        }
        if (s != null && !s.equals("")) {
            if (s.equals("-")) {
                System.setErr(System.out);
            }
            else {
                System.setErr(new PrintStream(new FileOutputStream(s)));
            }
        }
        final boolean b4 = hashMap.get(s2) != null;
        String path = "";
        if (!list.isEmpty()) {
            path = (String)list.remove(0);
        }
        String s13 = "";
        if (!list.isEmpty()) {
            s13 = (String)list.remove(0);
        }
        String s14 = "";
        String path2 = "";
        String s15 = "";
        if (n != 0) {
            if (path.toLowerCase().endsWith(".pack") || path.toLowerCase().endsWith(".pac") || path.toLowerCase().endsWith(".gz")) {
                System.err.println(MessageFormat.format(Driver.RESOURCE.getString("BAD_REPACK_OUTPUT"), path));
                printUsage(b, false, System.err);
                System.exit(2);
            }
            s14 = path;
            if (s13.equals("")) {
                s13 = s14;
            }
            s15 = (path = createTempFile(s14, ".pack").getPath());
            n2 = 0;
        }
        if (!list.isEmpty() || (!s13.toLowerCase().endsWith(".jar") && !s13.toLowerCase().endsWith(".zip") && (!s13.equals("-") || b))) {
            printUsage(b, false, System.err);
            System.exit(2);
            return;
        }
        if (n != 0) {
            b2 = (b = true);
        }
        else if (b) {
            b2 = false;
        }
        final Pack200.Packer packer = Pack200.newPacker();
        final Pack200.Unpacker unpacker = Pack200.newUnpacker();
        packer.properties().putAll((Map<?, ?>)hashMap);
        unpacker.properties().putAll((Map<?, ?>)hashMap);
        if (n != 0 && s14.equals(s13)) {
            final String zipComment = getZipComment(s13);
            if (b4 && zipComment.length() > 0) {
                System.out.println(MessageFormat.format(Driver.RESOURCE.getString("DETECTED_ZIP_COMMENT"), zipComment));
            }
            if (zipComment.indexOf("PACK200") >= 0) {
                System.out.println(MessageFormat.format(Driver.RESOURCE.getString("SKIP_FOR_REPACKED"), s13));
                b = false;
                b2 = false;
                n = 0;
            }
        }
        try {
            if (b) {
                final JarFile jarFile = new JarFile(new File(s13));
                Closeable out;
                if (path.equals("-")) {
                    out = System.out;
                    System.setOut(System.err);
                }
                else if (n2 != 0) {
                    if (!path.endsWith(".gz")) {
                        System.err.println(MessageFormat.format(Driver.RESOURCE.getString("WRITE_PACK_FILE"), path));
                        printUsage(b, false, System.err);
                        System.exit(2);
                    }
                    out = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(path)));
                }
                else {
                    if (!path.toLowerCase().endsWith(".pack") && !path.toLowerCase().endsWith(".pac")) {
                        System.err.println(MessageFormat.format(Driver.RESOURCE.getString("WRITE_PACKGZ_FILE"), path));
                        printUsage(b, false, System.err);
                        System.exit(2);
                    }
                    out = new BufferedOutputStream(new FileOutputStream(path));
                }
                packer.pack(jarFile, (OutputStream)out);
                ((OutputStream)out).close();
            }
            if (n != 0 && s14.equals(s13)) {
                final File tempFile = createTempFile(s13, ".bak");
                tempFile.delete();
                if (!new File(s13).renameTo(tempFile)) {
                    throw new Error(MessageFormat.format(Driver.RESOURCE.getString("SKIP_FOR_MOVE_FAILED"), path2));
                }
                path2 = tempFile.getPath();
            }
            if (b2) {
                InputStream in;
                if (path.equals("-")) {
                    in = System.in;
                }
                else {
                    in = new FileInputStream(new File(path));
                }
                FilterInputStream filterInputStream;
                if (Utils.isGZIPMagic(Utils.readMagic((BufferedInputStream)(filterInputStream = new BufferedInputStream(in))))) {
                    filterInputStream = new GZIPInputStream(filterInputStream);
                }
                final String s16 = s14.equals("") ? s13 : s14;
                Closeable out2;
                if (s16.equals("-")) {
                    out2 = System.out;
                }
                else {
                    out2 = new FileOutputStream(s16);
                }
                try (final JarOutputStream jarOutputStream = new JarOutputStream(new BufferedOutputStream((OutputStream)out2))) {
                    unpacker.unpack(filterInputStream, jarOutputStream);
                }
            }
            if (!path2.equals("")) {
                new File(path2).delete();
                path2 = "";
            }
        }
        finally {
            if (!path2.equals("")) {
                final File file = new File(s13);
                file.delete();
                new File(path2).renameTo(file);
            }
            if (!s15.equals("")) {
                new File(s15).delete();
            }
        }
    }
    
    private static File createTempFile(final String s, final String s2) throws IOException {
        final File file = new File(s);
        String s3 = file.getName();
        if (s3.length() < 3) {
            s3 += "tmp";
        }
        final File file2 = (file.getParentFile() == null && s2.equals(".bak")) ? new File(".").getAbsoluteFile() : file.getParentFile();
        return ((file2 == null) ? Files.createTempFile(s3, s2, (FileAttribute<?>[])new FileAttribute[0]) : Files.createTempFile(file2.toPath(), s3, s2, (FileAttribute<?>[])new FileAttribute[0])).toFile();
    }
    
    private static void printUsage(final boolean b, final boolean b2, final PrintStream printStream) {
        final String s = b ? "pack200" : "unpack200";
        final String[] array = (String[])Driver.RESOURCE.getObject("PACK_HELP");
        final String[] array2 = (String[])Driver.RESOURCE.getObject("UNPACK_HELP");
        final String[] array3 = b ? array : array2;
        for (int i = 0; i < array3.length; ++i) {
            printStream.println(array3[i]);
            if (!b2) {
                printStream.println(MessageFormat.format(Driver.RESOURCE.getString("MORE_INFO"), s));
                break;
            }
        }
    }
    
    private static String getZipComment(final String s) throws IOException {
        final byte[] array = new byte[1000];
        final long length = new File(s).length();
        if (length <= 0L) {
            return "";
        }
        final long max = Math.max(0L, length - array.length);
        try (final FileInputStream fileInputStream = new FileInputStream(new File(s))) {
            fileInputStream.skip(max);
            fileInputStream.read(array);
            int i = array.length - 4;
            while (i >= 0) {
                if (array[i + 0] == 80 && array[i + 1] == 75 && array[i + 2] == 5 && array[i + 3] == 6) {
                    i += 22;
                    if (i < array.length) {
                        return new String(array, i, array.length - i, "UTF8");
                    }
                    return "";
                }
                else {
                    --i;
                }
            }
            return "";
        }
    }
    
    private static String parseCommandOptions(final List<String> list, final String s, final Map<String, String> map) {
        String string = null;
        final TreeMap treeMap = new TreeMap();
        for (final String s2 : s.split("\n")) {
            final String[] split2 = s2.split("\\p{Space}+");
            if (split2.length != 0) {
                String s3 = split2[0];
                split2[0] = "";
                if (s3.length() == 0 && split2.length >= 1) {
                    s3 = split2[1];
                    split2[1] = "";
                }
                if (s3.length() != 0) {
                    if (treeMap.put(s3, split2) != null) {
                        throw new RuntimeException(MessageFormat.format(Driver.RESOURCE.getString("DUPLICATE_OPTION"), s2.trim()));
                    }
                }
            }
        }
        final ListIterator<String> listIterator = list.listIterator();
        final ListIterator listIterator2 = new ArrayList().listIterator();
    Label_1211:
        while (true) {
            String s4;
            if (listIterator2.hasPrevious()) {
                s4 = (String)listIterator2.previous();
                listIterator2.remove();
            }
            else {
                if (!listIterator.hasNext()) {
                    break;
                }
                s4 = listIterator.next();
            }
            int n = s4.length();
            while (true) {
                final String substring = s4.substring(0, n);
                if (treeMap.containsKey(substring)) {
                    String s5 = substring.intern();
                    assert s4.startsWith(s5);
                    assert s5.length() == n;
                    String substring2 = s4.substring(n);
                    boolean b = false;
                    boolean b2 = false;
                    final int nextIndex = listIterator2.nextIndex();
                Label_1128:
                    for (final String s6 : treeMap.get(s5)) {
                        if (s6.length() != 0) {
                            if (s6.startsWith("#")) {
                                break;
                            }
                            int n2 = 0;
                            char c = s6.charAt(n2++);
                            int n3 = 0;
                            switch (c) {
                                case 43: {
                                    n3 = ((substring2.length() != 0) ? 1 : 0);
                                    c = s6.charAt(n2++);
                                    break;
                                }
                                case 42: {
                                    n3 = 1;
                                    c = s6.charAt(n2++);
                                    break;
                                }
                                default: {
                                    n3 = ((substring2.length() == 0) ? 1 : 0);
                                    break;
                                }
                            }
                            if (n3 != 0) {
                                final String substring3 = s6.substring(n2);
                                switch (c) {
                                    case 46: {
                                        string = ((substring3.length() != 0) ? substring3.intern() : s5);
                                        break Label_1211;
                                    }
                                    case 63: {
                                        string = ((substring3.length() != 0) ? substring3.intern() : s4);
                                        b2 = true;
                                        break Label_1128;
                                    }
                                    case 64: {
                                        s5 = substring3.intern();
                                        break;
                                    }
                                    case 62: {
                                        listIterator2.add(substring3 + substring2);
                                        substring2 = "";
                                        break;
                                    }
                                    case 33: {
                                        final String s7 = (substring3.length() != 0) ? substring3.intern() : s5;
                                        map.remove(s7);
                                        map.put(s7, null);
                                        b = true;
                                        break;
                                    }
                                    case 36: {
                                        String string2;
                                        if (substring3.length() != 0) {
                                            string2 = substring3;
                                        }
                                        else {
                                            final String s8 = map.get(s5);
                                            if (s8 == null || s8.length() == 0) {
                                                string2 = "1";
                                            }
                                            else {
                                                string2 = "" + (1 + Integer.parseInt(s8));
                                            }
                                        }
                                        map.put(s5, string2);
                                        b = true;
                                        break;
                                    }
                                    case 38:
                                    case 61: {
                                        final boolean b3 = c == '&';
                                        String string3;
                                        if (listIterator2.hasPrevious()) {
                                            string3 = (String)listIterator2.previous();
                                            listIterator2.remove();
                                        }
                                        else {
                                            if (!listIterator.hasNext()) {
                                                string = s4 + " ?";
                                                b2 = true;
                                                break Label_1128;
                                            }
                                            string3 = listIterator.next();
                                        }
                                        if (b3) {
                                            final String s9 = map.get(s5);
                                            if (s9 != null) {
                                                if (substring3.length() == 0) {}
                                                string3 = s9 + substring3 + string3;
                                            }
                                        }
                                        map.put(s5, string3);
                                        b = true;
                                        break;
                                    }
                                    default: {
                                        throw new RuntimeException(MessageFormat.format(Driver.RESOURCE.getString("BAD_SPEC"), s5, s6));
                                    }
                                }
                            }
                        }
                    }
                    if (b && !b2) {
                        continue Label_1211;
                    }
                    while (listIterator2.nextIndex() > nextIndex) {
                        listIterator2.previous();
                        listIterator2.remove();
                    }
                    if (b2) {
                        throw new IllegalArgumentException(string);
                    }
                    if (n == 0) {
                        break;
                    }
                    --n;
                }
                else {
                    if (n == 0) {
                        break;
                    }
                    final SortedMap<String, Object> headMap = treeMap.headMap(substring);
                    n = Math.min(headMap.isEmpty() ? 0 : headMap.lastKey().length(), n - 1);
                    s4.substring(0, n);
                }
            }
            listIterator2.add(s4);
            break;
        }
        list.subList(0, listIterator.nextIndex()).clear();
        while (listIterator2.hasPrevious()) {
            list.add(0, (String)listIterator2.previous());
        }
        return string;
    }
    
    static {
        RESOURCE = ResourceBundle.getBundle("com.sun.java.util.jar.pack.DriverResource");
        PACK200_PROPERTY_TO_OPTION = new String[] { "pack.segment.limit", "--segment-limit=", "pack.keep.file.order", "--no-keep-file-order", "pack.effort", "--effort=", "pack.deflate.hint", "--deflate-hint=", "pack.modification.time", "--modification-time=", "pack.pass.file.", "--pass-file=", "pack.unknown.attribute", "--unknown-attribute=", "pack.class.attribute.", "--class-attribute=", "pack.field.attribute.", "--field-attribute=", "pack.method.attribute.", "--method-attribute=", "pack.code.attribute.", "--code-attribute=", "com.sun.java.util.jar.pack.verbose", "--verbose", "com.sun.java.util.jar.pack.strip.debug", "--strip-debug" };
        UNPACK200_PROPERTY_TO_OPTION = new String[] { "unpack.deflate.hint", "--deflate-hint=", "com.sun.java.util.jar.pack.verbose", "--verbose", "com.sun.java.util.jar.pack.unpack.remove.packfile", "--remove-pack-file" };
    }
}
