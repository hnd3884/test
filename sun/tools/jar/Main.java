package sun.tools.jar;

import java.util.zip.CRC32;
import java.util.Date;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.util.jar.Attributes;
import java.util.zip.ZipInputStream;
import java.util.Locale;
import java.util.Iterator;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.io.FileNotFoundException;
import sun.misc.JarIndex;
import java.util.jar.JarOutputStream;
import java.util.jar.JarFile;
import java.util.jar.Pack200;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.jar.Manifest;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.io.File;
import java.util.Map;
import java.io.PrintStream;

public class Main
{
    String program;
    PrintStream out;
    PrintStream err;
    String fname;
    String mname;
    String ename;
    String zname;
    String[] files;
    String rootjar;
    Map<String, File> entryMap;
    Set<File> entries;
    Set<String> paths;
    boolean cflag;
    boolean uflag;
    boolean xflag;
    boolean tflag;
    boolean vflag;
    boolean flag0;
    boolean Mflag;
    boolean iflag;
    boolean nflag;
    boolean pflag;
    static final String MANIFEST_DIR = "META-INF/";
    static final String VERSION = "1.0";
    private static ResourceBundle rsrc;
    private static final boolean useExtractionTime;
    private boolean ok;
    private byte[] copyBuf;
    private HashSet<String> jarPaths;
    
    private String getMsg(final String s) {
        try {
            return Main.rsrc.getString(s);
        }
        catch (final MissingResourceException ex) {
            throw new Error("Error in message file");
        }
    }
    
    private String formatMsg(final String s, final String s2) {
        return MessageFormat.format(this.getMsg(s), s2);
    }
    
    private String formatMsg2(final String s, final String s2, final String s3) {
        return MessageFormat.format(this.getMsg(s), s2, s3);
    }
    
    public Main(final PrintStream out, final PrintStream err, final String program) {
        this.zname = "";
        this.rootjar = null;
        this.entryMap = new HashMap<String, File>();
        this.entries = new LinkedHashSet<File>();
        this.paths = new HashSet<String>();
        this.copyBuf = new byte[8192];
        this.jarPaths = new HashSet<String>();
        this.out = out;
        this.err = err;
        this.program = program;
    }
    
    private static File createTempFileInSameDirectoryAs(final File file) throws IOException {
        File parentFile = file.getParentFile();
        if (parentFile == null) {
            parentFile = new File(".");
        }
        return File.createTempFile("jartmp", null, parentFile);
    }
    
    public synchronized boolean run(final String[] array) {
        this.ok = true;
        if (!this.parseArgs(array)) {
            return false;
        }
        try {
            if ((this.cflag || this.uflag) && this.fname != null) {
                this.zname = this.fname.replace(File.separatorChar, '/');
                if (this.zname.startsWith("./")) {
                    this.zname = this.zname.substring(2);
                }
            }
            if (this.cflag) {
                Manifest manifest = null;
                InputStream inputStream = null;
                if (!this.Mflag) {
                    if (this.mname != null) {
                        inputStream = new FileInputStream(this.mname);
                        manifest = new Manifest(new BufferedInputStream(inputStream));
                    }
                    else {
                        manifest = new Manifest();
                    }
                    this.addVersion(manifest);
                    this.addCreatedBy(manifest);
                    if (this.isAmbiguousMainClass(manifest)) {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        return false;
                    }
                    if (this.ename != null) {
                        this.addMainClass(manifest, this.ename);
                    }
                }
                this.expand(null, this.files, false);
                FileOutputStream fileOutputStream;
                if (this.fname != null) {
                    fileOutputStream = new FileOutputStream(this.fname);
                }
                else {
                    fileOutputStream = new FileOutputStream(FileDescriptor.out);
                    if (this.vflag) {
                        this.vflag = false;
                    }
                }
                File temporaryFile = null;
                final FileOutputStream fileOutputStream2 = fileOutputStream;
                final String s = (this.fname == null) ? "tmpjar" : this.fname.substring(this.fname.indexOf(File.separatorChar) + 1);
                if (this.nflag) {
                    temporaryFile = this.createTemporaryFile(s, ".jar");
                    fileOutputStream = new FileOutputStream(temporaryFile);
                }
                this.create(new BufferedOutputStream(fileOutputStream, 4096), manifest);
                if (inputStream != null) {
                    inputStream.close();
                }
                fileOutputStream.close();
                if (this.nflag) {
                    JarFile jarFile = null;
                    File temporaryFile2 = null;
                    JarOutputStream jarOutputStream = null;
                    try {
                        final Pack200.Packer packer = Pack200.newPacker();
                        packer.properties().put("pack.effort", "1");
                        jarFile = new JarFile(temporaryFile.getCanonicalPath());
                        temporaryFile2 = this.createTemporaryFile(s, ".pack");
                        fileOutputStream = new FileOutputStream(temporaryFile2);
                        packer.pack(jarFile, fileOutputStream);
                        jarOutputStream = new JarOutputStream(fileOutputStream2);
                        Pack200.newUnpacker().unpack(temporaryFile2, jarOutputStream);
                    }
                    catch (final IOException ex) {
                        this.fatalError(ex);
                    }
                    finally {
                        if (jarFile != null) {
                            jarFile.close();
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                        if (jarOutputStream != null) {
                            jarOutputStream.close();
                        }
                        if (temporaryFile != null && temporaryFile.exists()) {
                            temporaryFile.delete();
                        }
                        if (temporaryFile2 != null && temporaryFile2.exists()) {
                            temporaryFile2.delete();
                        }
                    }
                }
            }
            else if (this.uflag) {
                File file = null;
                File tempFileInSameDirectoryAs = null;
                FileInputStream fileInputStream;
                FileOutputStream fileOutputStream3;
                if (this.fname != null) {
                    file = new File(this.fname);
                    tempFileInSameDirectoryAs = createTempFileInSameDirectoryAs(file);
                    fileInputStream = new FileInputStream(file);
                    fileOutputStream3 = new FileOutputStream(tempFileInSameDirectoryAs);
                }
                else {
                    fileInputStream = new FileInputStream(FileDescriptor.in);
                    fileOutputStream3 = new FileOutputStream(FileDescriptor.out);
                    this.vflag = false;
                }
                final FileInputStream fileInputStream2 = (!this.Mflag && this.mname != null) ? new FileInputStream(this.mname) : null;
                this.expand(null, this.files, true);
                final boolean update = this.update(fileInputStream, new BufferedOutputStream(fileOutputStream3), fileInputStream2, null);
                if (this.ok) {
                    this.ok = update;
                }
                fileInputStream.close();
                fileOutputStream3.close();
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                if (this.ok && this.fname != null) {
                    file.delete();
                    if (!tempFileInSameDirectoryAs.renameTo(file)) {
                        tempFileInSameDirectoryAs.delete();
                        throw new IOException(this.getMsg("error.write.file"));
                    }
                    tempFileInSameDirectoryAs.delete();
                }
            }
            else if (this.tflag) {
                this.replaceFSC(this.files);
                if (this.fname != null) {
                    this.list(this.fname, this.files);
                }
                else {
                    final FileInputStream fileInputStream3 = new FileInputStream(FileDescriptor.in);
                    try {
                        this.list(new BufferedInputStream(fileInputStream3), this.files);
                    }
                    finally {
                        fileInputStream3.close();
                    }
                }
            }
            else if (this.xflag) {
                this.replaceFSC(this.files);
                if (this.fname != null && this.files != null) {
                    this.extract(this.fname, this.files);
                }
                else {
                    final FileInputStream fileInputStream4 = (this.fname == null) ? new FileInputStream(FileDescriptor.in) : new FileInputStream(this.fname);
                    try {
                        this.extract(new BufferedInputStream(fileInputStream4), this.files);
                    }
                    finally {
                        fileInputStream4.close();
                    }
                }
            }
            else if (this.iflag) {
                this.genIndex(this.rootjar, this.files);
            }
        }
        catch (final IOException ex2) {
            this.fatalError(ex2);
            this.ok = false;
        }
        catch (final Error error) {
            error.printStackTrace();
            this.ok = false;
        }
        catch (final Throwable t) {
            t.printStackTrace();
            this.ok = false;
        }
        this.out.flush();
        this.err.flush();
        return this.ok;
    }
    
    boolean parseArgs(String[] parse) {
        try {
            parse = CommandLine.parse(parse);
        }
        catch (final FileNotFoundException ex) {
            this.fatalError(this.formatMsg("error.cant.open", ex.getMessage()));
            return false;
        }
        catch (final IOException ex2) {
            this.fatalError(ex2);
            return false;
        }
        int n = 1;
        try {
            String substring = parse[0];
            if (substring.startsWith("-")) {
                substring = substring.substring(1);
            }
            for (int i = 0; i < substring.length(); ++i) {
                switch (substring.charAt(i)) {
                    case 'c': {
                        if (this.xflag || this.tflag || this.uflag || this.iflag) {
                            this.usageError();
                            return false;
                        }
                        this.cflag = true;
                        break;
                    }
                    case 'u': {
                        if (this.cflag || this.xflag || this.tflag || this.iflag) {
                            this.usageError();
                            return false;
                        }
                        this.uflag = true;
                        break;
                    }
                    case 'x': {
                        if (this.cflag || this.uflag || this.tflag || this.iflag) {
                            this.usageError();
                            return false;
                        }
                        this.xflag = true;
                        break;
                    }
                    case 't': {
                        if (this.cflag || this.uflag || this.xflag || this.iflag) {
                            this.usageError();
                            return false;
                        }
                        this.tflag = true;
                        break;
                    }
                    case 'M': {
                        this.Mflag = true;
                        break;
                    }
                    case 'v': {
                        this.vflag = true;
                        break;
                    }
                    case 'f': {
                        this.fname = parse[n++];
                        break;
                    }
                    case 'm': {
                        this.mname = parse[n++];
                        break;
                    }
                    case '0': {
                        this.flag0 = true;
                        break;
                    }
                    case 'i': {
                        if (this.cflag || this.uflag || this.xflag || this.tflag) {
                            this.usageError();
                            return false;
                        }
                        this.rootjar = parse[n++];
                        this.iflag = true;
                        break;
                    }
                    case 'n': {
                        this.nflag = true;
                        break;
                    }
                    case 'e': {
                        this.ename = parse[n++];
                        break;
                    }
                    case 'P': {
                        this.pflag = true;
                        break;
                    }
                    default: {
                        this.error(this.formatMsg("error.illegal.option", String.valueOf(substring.charAt(i))));
                        this.usageError();
                        return false;
                    }
                }
            }
        }
        catch (final ArrayIndexOutOfBoundsException ex3) {
            this.usageError();
            return false;
        }
        if (!this.cflag && !this.tflag && !this.xflag && !this.uflag && !this.iflag) {
            this.error(this.getMsg("error.bad.option"));
            this.usageError();
            return false;
        }
        final int n2 = parse.length - n;
        if (n2 > 0) {
            int n3 = 0;
            final String[] array = new String[n2];
            try {
                for (int j = n; j < parse.length; ++j) {
                    if (parse[j].equals("-C")) {
                        final String s = parse[++j];
                        String s2;
                        for (s2 = (s.endsWith(File.separator) ? s : (s + File.separator)).replace(File.separatorChar, '/'); s2.indexOf("//") > -1; s2 = s2.replace("//", "/")) {}
                        this.paths.add(s2.replace(File.separatorChar, '/'));
                        array[n3++] = s2 + parse[++j];
                    }
                    else {
                        array[n3++] = parse[j];
                    }
                }
            }
            catch (final ArrayIndexOutOfBoundsException ex4) {
                this.usageError();
                return false;
            }
            System.arraycopy(array, 0, this.files = new String[n3], 0, n3);
        }
        else {
            if (this.cflag && this.mname == null) {
                this.error(this.getMsg("error.bad.cflag"));
                this.usageError();
                return false;
            }
            if (this.uflag) {
                if (this.mname != null || this.ename != null) {
                    return true;
                }
                this.error(this.getMsg("error.bad.uflag"));
                this.usageError();
                return false;
            }
        }
        return true;
    }
    
    void expand(final File file, final String[] array, final boolean b) {
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            File file2;
            if (file == null) {
                file2 = new File(array[i]);
            }
            else {
                file2 = new File(file, array[i]);
            }
            if (file2.isFile()) {
                if (this.entries.add(file2) && b) {
                    this.entryMap.put(this.entryName(file2.getPath()), file2);
                }
            }
            else if (file2.isDirectory()) {
                if (this.entries.add(file2)) {
                    if (b) {
                        final String path = file2.getPath();
                        this.entryMap.put(this.entryName(path.endsWith(File.separator) ? path : (path + File.separator)), file2);
                    }
                    this.expand(file2, file2.list(), b);
                }
            }
            else {
                this.error(this.formatMsg("error.nosuch.fileordir", String.valueOf(file2)));
                this.ok = false;
            }
        }
    }
    
    void create(final OutputStream outputStream, final Manifest manifest) throws IOException {
        final JarOutputStream jarOutputStream = new JarOutputStream(outputStream);
        if (this.flag0) {
            jarOutputStream.setMethod(0);
        }
        if (manifest != null) {
            if (this.vflag) {
                this.output(this.getMsg("out.added.manifest"));
            }
            final ZipEntry zipEntry = new ZipEntry("META-INF/");
            zipEntry.setTime(System.currentTimeMillis());
            zipEntry.setSize(0L);
            zipEntry.setCrc(0L);
            jarOutputStream.putNextEntry(zipEntry);
            final ZipEntry zipEntry2 = new ZipEntry("META-INF/MANIFEST.MF");
            zipEntry2.setTime(System.currentTimeMillis());
            if (this.flag0) {
                this.crc32Manifest(zipEntry2, manifest);
            }
            jarOutputStream.putNextEntry(zipEntry2);
            manifest.write(jarOutputStream);
            jarOutputStream.closeEntry();
        }
        final Iterator<File> iterator = this.entries.iterator();
        while (iterator.hasNext()) {
            this.addFile(jarOutputStream, iterator.next());
        }
        jarOutputStream.close();
    }
    
    private char toUpperCaseASCII(final char c) {
        return (c < 'a' || c > 'z') ? c : ((char)(c + 'A' - 97));
    }
    
    private boolean equalsIgnoreCase(final String s, final String s2) {
        assert s2.toUpperCase(Locale.ENGLISH).equals(s2);
        final int length;
        if ((length = s.length()) != s2.length()) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            final char char2 = s2.charAt(i);
            if (char1 != char2 && this.toUpperCaseASCII(char1) != char2) {
                return false;
            }
        }
        return true;
    }
    
    boolean update(final InputStream inputStream, final OutputStream outputStream, final InputStream inputStream2, final JarIndex jarIndex) throws IOException {
        final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        final JarOutputStream jarOutputStream = new JarOutputStream(outputStream);
        boolean b = false;
        boolean b2 = true;
        if (jarIndex != null) {
            this.addIndex(jarIndex, jarOutputStream);
        }
        ZipEntry nextEntry;
        while ((nextEntry = zipInputStream.getNextEntry()) != null) {
            final String name = nextEntry.getName();
            final boolean equalsIgnoreCase = this.equalsIgnoreCase(name, "META-INF/MANIFEST.MF");
            if (jarIndex == null || !this.equalsIgnoreCase(name, "META-INF/INDEX.LIST")) {
                if (this.Mflag && equalsIgnoreCase) {
                    continue;
                }
                if (equalsIgnoreCase && (inputStream2 != null || this.ename != null)) {
                    b = true;
                    if (inputStream2 != null) {
                        final FileInputStream fileInputStream = new FileInputStream(this.mname);
                        final boolean ambiguousMainClass = this.isAmbiguousMainClass(new Manifest(fileInputStream));
                        fileInputStream.close();
                        if (ambiguousMainClass) {
                            return false;
                        }
                    }
                    final Manifest manifest = new Manifest(zipInputStream);
                    if (inputStream2 != null) {
                        manifest.read(inputStream2);
                    }
                    if (!this.updateManifest(manifest, jarOutputStream)) {
                        return false;
                    }
                    continue;
                }
                else if (!this.entryMap.containsKey(name)) {
                    final ZipEntry zipEntry = new ZipEntry(name);
                    zipEntry.setMethod(nextEntry.getMethod());
                    zipEntry.setTime(nextEntry.getTime());
                    zipEntry.setComment(nextEntry.getComment());
                    zipEntry.setExtra(nextEntry.getExtra());
                    if (nextEntry.getMethod() == 0) {
                        zipEntry.setSize(nextEntry.getSize());
                        zipEntry.setCrc(nextEntry.getCrc());
                    }
                    jarOutputStream.putNextEntry(zipEntry);
                    this.copy(zipInputStream, jarOutputStream);
                }
                else {
                    final File file = this.entryMap.get(name);
                    this.addFile(jarOutputStream, file);
                    this.entryMap.remove(name);
                    this.entries.remove(file);
                }
            }
        }
        final Iterator<File> iterator = this.entries.iterator();
        while (iterator.hasNext()) {
            this.addFile(jarOutputStream, iterator.next());
        }
        if (!b) {
            if (inputStream2 != null) {
                final Manifest manifest2 = new Manifest(inputStream2);
                b2 = !this.isAmbiguousMainClass(manifest2);
                if (b2 && !this.updateManifest(manifest2, jarOutputStream)) {
                    b2 = false;
                }
            }
            else if (this.ename != null && !this.updateManifest(new Manifest(), jarOutputStream)) {
                b2 = false;
            }
        }
        zipInputStream.close();
        jarOutputStream.close();
        return b2;
    }
    
    private void addIndex(final JarIndex jarIndex, final ZipOutputStream zipOutputStream) throws IOException {
        final ZipEntry zipEntry = new ZipEntry("META-INF/INDEX.LIST");
        zipEntry.setTime(System.currentTimeMillis());
        if (this.flag0) {
            final CRC32OutputStream crc32OutputStream = new CRC32OutputStream();
            jarIndex.write(crc32OutputStream);
            crc32OutputStream.updateEntry(zipEntry);
        }
        zipOutputStream.putNextEntry(zipEntry);
        jarIndex.write(zipOutputStream);
        zipOutputStream.closeEntry();
    }
    
    private boolean updateManifest(final Manifest manifest, final ZipOutputStream zipOutputStream) throws IOException {
        this.addVersion(manifest);
        this.addCreatedBy(manifest);
        if (this.ename != null) {
            this.addMainClass(manifest, this.ename);
        }
        final ZipEntry zipEntry = new ZipEntry("META-INF/MANIFEST.MF");
        zipEntry.setTime(System.currentTimeMillis());
        if (this.flag0) {
            this.crc32Manifest(zipEntry, manifest);
        }
        zipOutputStream.putNextEntry(zipEntry);
        manifest.write(zipOutputStream);
        if (this.vflag) {
            this.output(this.getMsg("out.update.manifest"));
        }
        return true;
    }
    
    private static final boolean isWinDriveLetter(final char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
    
    private String safeName(String substring) {
        if (!this.pflag) {
            final int length = substring.length();
            int i = substring.lastIndexOf("../");
            if (i == -1) {
                i = 0;
            }
            else {
                i += 3;
            }
            if (File.separatorChar == '\\') {
                while (i < length) {
                    final int n = i;
                    if (i + 1 < length && substring.charAt(i + 1) == ':' && isWinDriveLetter(substring.charAt(i))) {
                        i += 2;
                    }
                    while (i < length && substring.charAt(i) == '/') {
                        ++i;
                    }
                    if (i == n) {
                        break;
                    }
                }
            }
            else {
                while (i < length && substring.charAt(i) == '/') {
                    ++i;
                }
            }
            if (i != 0) {
                substring = substring.substring(i);
            }
        }
        return substring;
    }
    
    private String entryName(String s) {
        s = s.replace(File.separatorChar, '/');
        String s2 = "";
        for (final String s3 : this.paths) {
            if (s.startsWith(s3) && s3.length() > s2.length()) {
                s2 = s3;
            }
        }
        s = s.substring(s2.length());
        s = this.safeName(s);
        if (s.startsWith("./")) {
            s = s.substring(2);
        }
        return s;
    }
    
    private void addVersion(final Manifest manifest) {
        final Attributes mainAttributes = manifest.getMainAttributes();
        if (mainAttributes.getValue(Attributes.Name.MANIFEST_VERSION) == null) {
            mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        }
    }
    
    private void addCreatedBy(final Manifest manifest) {
        final Attributes mainAttributes = manifest.getMainAttributes();
        if (mainAttributes.getValue(new Attributes.Name("Created-By")) == null) {
            mainAttributes.put(new Attributes.Name("Created-By"), System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")");
        }
    }
    
    private void addMainClass(final Manifest manifest, final String s) {
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, s);
    }
    
    private boolean isAmbiguousMainClass(final Manifest manifest) {
        if (this.ename != null && manifest.getMainAttributes().get(Attributes.Name.MAIN_CLASS) != null) {
            this.error(this.getMsg("error.bad.eflag"));
            this.usageError();
            return true;
        }
        return false;
    }
    
    void addFile(final ZipOutputStream zipOutputStream, final File file) throws IOException {
        String path = file.getPath();
        final boolean directory = file.isDirectory();
        if (directory) {
            path = (path.endsWith(File.separator) ? path : (path + File.separator));
        }
        final String entryName = this.entryName(path);
        if (entryName.equals("") || entryName.equals(".") || entryName.equals(this.zname)) {
            return;
        }
        if ((entryName.equals("META-INF/") || entryName.equals("META-INF/MANIFEST.MF")) && !this.Mflag) {
            if (this.vflag) {
                this.output(this.formatMsg("out.ignore.entry", entryName));
            }
            return;
        }
        final long n = directory ? 0L : file.length();
        if (this.vflag) {
            this.out.print(this.formatMsg("out.adding", entryName));
        }
        final ZipEntry zipEntry = new ZipEntry(entryName);
        zipEntry.setTime(file.lastModified());
        if (n == 0L) {
            zipEntry.setMethod(0);
            zipEntry.setSize(0L);
            zipEntry.setCrc(0L);
        }
        else if (this.flag0) {
            this.crc32File(zipEntry, file);
        }
        zipOutputStream.putNextEntry(zipEntry);
        if (!directory) {
            this.copy(file, zipOutputStream);
        }
        zipOutputStream.closeEntry();
        if (this.vflag) {
            final long size = zipEntry.getSize();
            final long compressedSize = zipEntry.getCompressedSize();
            this.out.print(this.formatMsg2("out.size", String.valueOf(size), String.valueOf(compressedSize)));
            if (zipEntry.getMethod() == 8) {
                long n2 = 0L;
                if (size != 0L) {
                    n2 = (size - compressedSize) * 100L / size;
                }
                this.output(this.formatMsg("out.deflated", String.valueOf(n2)));
            }
            else {
                this.output(this.getMsg("out.stored"));
            }
        }
    }
    
    private void copy(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        int read;
        while ((read = inputStream.read(this.copyBuf)) != -1) {
            outputStream.write(this.copyBuf, 0, read);
        }
    }
    
    private void copy(final File file, final OutputStream outputStream) throws IOException {
        final FileInputStream fileInputStream = new FileInputStream(file);
        try {
            this.copy(fileInputStream, outputStream);
        }
        finally {
            fileInputStream.close();
        }
    }
    
    private void copy(final InputStream inputStream, final File file) throws IOException {
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            this.copy(inputStream, fileOutputStream);
        }
        finally {
            fileOutputStream.close();
        }
    }
    
    private void crc32Manifest(final ZipEntry zipEntry, final Manifest manifest) throws IOException {
        final CRC32OutputStream crc32OutputStream = new CRC32OutputStream();
        manifest.write(crc32OutputStream);
        crc32OutputStream.updateEntry(zipEntry);
    }
    
    private void crc32File(final ZipEntry zipEntry, final File file) throws IOException {
        final CRC32OutputStream crc32OutputStream = new CRC32OutputStream();
        this.copy(file, crc32OutputStream);
        if (crc32OutputStream.n != file.length()) {
            throw new JarException(this.formatMsg("error.incorrect.length", file.getPath()));
        }
        crc32OutputStream.updateEntry(zipEntry);
    }
    
    void replaceFSC(final String[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                array[i] = array[i].replace(File.separatorChar, '/');
            }
        }
    }
    
    Set<ZipEntry> newDirSet() {
        return new HashSet<ZipEntry>() {
            @Override
            public boolean add(final ZipEntry zipEntry) {
                return zipEntry != null && !Main.useExtractionTime && super.add(zipEntry);
            }
        };
    }
    
    void updateLastModifiedTime(final Set<ZipEntry> set) throws IOException {
        for (final ZipEntry zipEntry : set) {
            final long time = zipEntry.getTime();
            if (time != -1L) {
                final String safeName = this.safeName(zipEntry.getName().replace(File.separatorChar, '/'));
                if (safeName.length() == 0) {
                    continue;
                }
                new File(safeName.replace('/', File.separatorChar)).setLastModified(time);
            }
        }
    }
    
    void extract(final InputStream inputStream, final String[] array) throws IOException {
        final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        final Set<ZipEntry> dirSet = this.newDirSet();
        ZipEntry nextEntry;
        while ((nextEntry = zipInputStream.getNextEntry()) != null) {
            if (array == null) {
                dirSet.add(this.extractFile(zipInputStream, nextEntry));
            }
            else {
                final String name = nextEntry.getName();
                for (int length = array.length, i = 0; i < length; ++i) {
                    if (name.startsWith(array[i])) {
                        dirSet.add(this.extractFile(zipInputStream, nextEntry));
                        break;
                    }
                }
            }
        }
        this.updateLastModifiedTime(dirSet);
    }
    
    void extract(final String s, final String[] array) throws IOException {
        final ZipFile zipFile = new ZipFile(s);
        final Set<ZipEntry> dirSet = this.newDirSet();
        final Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            final ZipEntry zipEntry = (ZipEntry)entries.nextElement();
            if (array == null) {
                dirSet.add(this.extractFile(zipFile.getInputStream(zipEntry), zipEntry));
            }
            else {
                final String name = zipEntry.getName();
                for (int length = array.length, i = 0; i < length; ++i) {
                    if (name.startsWith(array[i])) {
                        dirSet.add(this.extractFile(zipFile.getInputStream(zipEntry), zipEntry));
                        break;
                    }
                }
            }
        }
        zipFile.close();
        this.updateLastModifiedTime(dirSet);
    }
    
    ZipEntry extractFile(final InputStream inputStream, final ZipEntry zipEntry) throws IOException {
        ZipEntry zipEntry2 = null;
        final String safeName = this.safeName(zipEntry.getName().replace(File.separatorChar, '/'));
        if (safeName.length() == 0) {
            return zipEntry2;
        }
        final File file = new File(safeName.replace('/', File.separatorChar));
        if (zipEntry.isDirectory()) {
            if (file.exists()) {
                if (!file.isDirectory()) {
                    throw new IOException(this.formatMsg("error.create.dir", file.getPath()));
                }
            }
            else {
                if (!file.mkdirs()) {
                    throw new IOException(this.formatMsg("error.create.dir", file.getPath()));
                }
                zipEntry2 = zipEntry;
            }
            if (this.vflag) {
                this.output(this.formatMsg("out.create", safeName));
            }
        }
        else {
            if (file.getParent() != null) {
                final File file2 = new File(file.getParent());
                if ((!file2.exists() && !file2.mkdirs()) || !file2.isDirectory()) {
                    throw new IOException(this.formatMsg("error.create.dir", file2.getPath()));
                }
            }
            try {
                this.copy(inputStream, file);
            }
            finally {
                if (inputStream instanceof ZipInputStream) {
                    ((ZipInputStream)inputStream).closeEntry();
                }
                else {
                    inputStream.close();
                }
            }
            if (this.vflag) {
                if (zipEntry.getMethod() == 8) {
                    this.output(this.formatMsg("out.inflated", safeName));
                }
                else {
                    this.output(this.formatMsg("out.extracted", safeName));
                }
            }
        }
        if (!Main.useExtractionTime) {
            final long time = zipEntry.getTime();
            if (time != -1L) {
                file.setLastModified(time);
            }
        }
        return zipEntry2;
    }
    
    void list(final InputStream inputStream, final String[] array) throws IOException {
        final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry nextEntry;
        while ((nextEntry = zipInputStream.getNextEntry()) != null) {
            zipInputStream.closeEntry();
            this.printEntry(nextEntry, array);
        }
    }
    
    void list(final String s, final String[] array) throws IOException {
        final ZipFile zipFile = new ZipFile(s);
        final Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            this.printEntry((ZipEntry)entries.nextElement(), array);
        }
        zipFile.close();
    }
    
    void dumpIndex(final String s, final JarIndex jarIndex) throws IOException {
        final File file = new File(s);
        final Path path = file.toPath();
        final Path path2 = createTempFileInSameDirectoryAs(file).toPath();
        try {
            if (this.update(Files.newInputStream(path, new OpenOption[0]), Files.newOutputStream(path2, new OpenOption[0]), null, jarIndex)) {
                try {
                    Files.move(path2, path, StandardCopyOption.REPLACE_EXISTING);
                }
                catch (final IOException ex) {
                    throw new IOException(this.getMsg("error.write.file"), ex);
                }
            }
        }
        finally {
            Files.deleteIfExists(path2);
        }
    }
    
    List<String> getJarPath(final String s) throws IOException {
        final ArrayList list = new ArrayList();
        list.add(s);
        this.jarPaths.add(s);
        final String substring = s.substring(0, Math.max(0, s.lastIndexOf(47) + 1));
        final JarFile jarFile = new JarFile(s.replace('/', File.separatorChar));
        if (jarFile != null) {
            final Manifest manifest = jarFile.getManifest();
            if (manifest != null) {
                final Attributes mainAttributes = manifest.getMainAttributes();
                if (mainAttributes != null) {
                    final String value = mainAttributes.getValue(Attributes.Name.CLASS_PATH);
                    if (value != null) {
                        final StringTokenizer stringTokenizer = new StringTokenizer(value);
                        while (stringTokenizer.hasMoreTokens()) {
                            final String nextToken = stringTokenizer.nextToken();
                            if (!nextToken.endsWith("/")) {
                                final String concat = substring.concat(nextToken);
                                if (this.jarPaths.contains(concat)) {
                                    continue;
                                }
                                list.addAll(this.getJarPath(concat));
                            }
                        }
                    }
                }
            }
        }
        jarFile.close();
        return list;
    }
    
    void genIndex(final String s, final String[] array) throws IOException {
        final List<String> jarPath = this.getJarPath(s);
        int n = jarPath.size();
        if (n == 1 && array != null) {
            for (int i = 0; i < array.length; ++i) {
                jarPath.addAll(this.getJarPath(array[i]));
            }
            n = jarPath.size();
        }
        this.dumpIndex(s, new JarIndex(jarPath.toArray(new String[n])));
    }
    
    void printEntry(final ZipEntry zipEntry, final String[] array) throws IOException {
        if (array == null) {
            this.printEntry(zipEntry);
        }
        else {
            final String name = zipEntry.getName();
            for (int length = array.length, i = 0; i < length; ++i) {
                if (name.startsWith(array[i])) {
                    this.printEntry(zipEntry);
                    return;
                }
            }
        }
    }
    
    void printEntry(final ZipEntry zipEntry) throws IOException {
        if (this.vflag) {
            final StringBuilder sb = new StringBuilder();
            final String string = Long.toString(zipEntry.getSize());
            for (int i = 6 - string.length(); i > 0; --i) {
                sb.append(' ');
            }
            sb.append(string).append(' ').append(new Date(zipEntry.getTime()).toString());
            sb.append(' ').append(zipEntry.getName());
            this.output(sb.toString());
        }
        else {
            this.output(zipEntry.getName());
        }
    }
    
    void usageError() {
        this.error(this.getMsg("usage"));
    }
    
    void fatalError(final Exception ex) {
        ex.printStackTrace();
    }
    
    void fatalError(final String s) {
        this.error(this.program + ": " + s);
    }
    
    protected void output(final String s) {
        this.out.println(s);
    }
    
    protected void error(final String s) {
        this.err.println(s);
    }
    
    public static void main(final String[] array) {
        System.exit(new Main(System.out, System.err, "jar").run(array) ? 0 : 1);
    }
    
    private File createTemporaryFile(final String s, final String s2) {
        File file = null;
        try {
            file = File.createTempFile(s, s2);
        }
        catch (final IOException | SecurityException ex) {}
        if (file == null) {
            if (this.fname != null) {
                try {
                    file = File.createTempFile(this.fname, ".tmp" + s2, new File(this.fname).getAbsoluteFile().getParentFile());
                }
                catch (final IOException ex2) {
                    this.fatalError(ex2);
                }
            }
            else {
                this.fatalError(new IOException(this.getMsg("error.create.tempfile")));
            }
        }
        return file;
    }
    
    static {
        useExtractionTime = Boolean.getBoolean("sun.tools.jar.useExtractionTime");
        try {
            Main.rsrc = ResourceBundle.getBundle("sun.tools.jar.resources.jar");
        }
        catch (final MissingResourceException ex) {
            throw new Error("Fatal: Resource for jar is missing");
        }
    }
    
    private static class CRC32OutputStream extends OutputStream
    {
        final CRC32 crc;
        long n;
        
        CRC32OutputStream() {
            this.crc = new CRC32();
            this.n = 0L;
        }
        
        @Override
        public void write(final int n) throws IOException {
            this.crc.update(n);
            ++this.n;
        }
        
        @Override
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            this.crc.update(array, n, n2);
            this.n += n2;
        }
        
        public void updateEntry(final ZipEntry zipEntry) {
            zipEntry.setMethod(0);
            zipEntry.setSize(this.n);
            zipEntry.setCrc(this.crc.getValue());
        }
    }
}
