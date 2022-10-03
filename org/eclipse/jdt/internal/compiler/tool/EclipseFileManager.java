package org.eclipse.jdt.internal.compiler.tool;

import java.text.MessageFormat;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.AccessRule;
import java.util.Arrays;
import java.net.URISyntaxException;
import java.net.URI;
import javax.tools.FileObject;
import java.util.StringTokenizer;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipException;
import java.util.List;
import java.util.ArrayList;
import javax.tools.JavaFileObject;
import java.util.Set;
import java.util.Iterator;
import java.util.MissingResourceException;
import org.eclipse.jdt.internal.compiler.batch.Main;
import java.io.IOException;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Locale;
import java.nio.charset.Charset;
import java.io.File;
import java.util.Map;
import javax.tools.StandardJavaFileManager;

public class EclipseFileManager implements StandardJavaFileManager
{
    private static final String NO_EXTENSION = "";
    static final int HAS_EXT_DIRS = 1;
    static final int HAS_BOOTCLASSPATH = 2;
    static final int HAS_ENDORSED_DIRS = 4;
    static final int HAS_PROCESSORPATH = 8;
    Map<File, Archive> archivesCache;
    Charset charset;
    Locale locale;
    Map<String, Iterable<? extends File>> locations;
    int flags;
    public ResourceBundle bundle;
    
    public EclipseFileManager(final Locale locale, final Charset charset) {
        this.locale = ((locale == null) ? Locale.getDefault() : locale);
        this.charset = ((charset == null) ? Charset.defaultCharset() : charset);
        this.locations = new HashMap<String, Iterable<? extends File>>();
        this.archivesCache = new HashMap<File, Archive>();
        try {
            this.setLocation(StandardLocation.PLATFORM_CLASS_PATH, this.getDefaultBootclasspath());
            final Iterable<? extends File> defaultClasspath = this.getDefaultClasspath();
            this.setLocation(StandardLocation.CLASS_PATH, defaultClasspath);
            this.setLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH, defaultClasspath);
        }
        catch (final IOException ex) {}
        try {
            this.bundle = Main.ResourceBundleFactory.getBundle(this.locale);
        }
        catch (final MissingResourceException ex2) {
            System.out.println("Missing resource : " + "org.eclipse.jdt.internal.compiler.batch.messages".replace('.', '/') + ".properties for locale " + locale);
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.locations != null) {
            this.locations.clear();
        }
        for (final Archive archive : this.archivesCache.values()) {
            archive.close();
        }
        this.archivesCache.clear();
    }
    
    private void collectAllMatchingFiles(final File file, final String normalizedPackageName, final Set<JavaFileObject.Kind> kinds, final boolean recurse, final ArrayList<JavaFileObject> collector) {
        if (!this.isArchive(file)) {
            final File currentFile = new File(file, normalizedPackageName);
            if (!currentFile.exists()) {
                return;
            }
            String path;
            try {
                path = currentFile.getCanonicalPath();
            }
            catch (final IOException ex) {
                return;
            }
            if (File.separatorChar == '/') {
                if (!path.endsWith(normalizedPackageName)) {
                    return;
                }
            }
            else if (!path.endsWith(normalizedPackageName.replace('/', File.separatorChar))) {
                return;
            }
            final File[] files = currentFile.listFiles();
            if (files != null) {
                File[] array;
                for (int length = (array = files).length, i = 0; i < length; ++i) {
                    final File f = array[i];
                    if (f.isDirectory() && recurse) {
                        this.collectAllMatchingFiles(file, String.valueOf(normalizedPackageName) + '/' + f.getName(), kinds, recurse, collector);
                    }
                    else {
                        final JavaFileObject.Kind kind = this.getKind(f);
                        if (kinds.contains(kind)) {
                            collector.add(new EclipseFileObject(String.valueOf(normalizedPackageName) + f.getName(), f.toURI(), kind, this.charset));
                        }
                    }
                }
            }
        }
        else {
            final Archive archive = this.getArchive(file);
            if (archive == Archive.UNKNOWN_ARCHIVE) {
                return;
            }
            String key = normalizedPackageName;
            if (!normalizedPackageName.endsWith("/")) {
                key = String.valueOf(key) + '/';
            }
            if (recurse) {
                for (final String packageName : archive.allPackages()) {
                    if (packageName.startsWith(key)) {
                        final List<String> types = archive.getTypes(packageName);
                        if (types == null) {
                            continue;
                        }
                        for (final String typeName : types) {
                            final JavaFileObject.Kind kind = this.getKind(this.getExtension(typeName));
                            if (kinds.contains(kind)) {
                                collector.add(archive.getArchiveFileObject(String.valueOf(packageName) + typeName, this.charset));
                            }
                        }
                    }
                }
            }
            else {
                final List<String> types2 = archive.getTypes(key);
                if (types2 != null) {
                    for (final String typeName2 : types2) {
                        final JavaFileObject.Kind kind2 = this.getKind(this.getExtension(typeName2));
                        if (kinds.contains(kind2)) {
                            collector.add(archive.getArchiveFileObject(String.valueOf(key) + typeName2, this.charset));
                        }
                    }
                }
            }
        }
    }
    
    private Iterable<? extends File> concatFiles(final Iterable<? extends File> iterable, final Iterable<? extends File> iterable2) {
        final ArrayList<File> list = new ArrayList<File>();
        if (iterable2 == null) {
            return iterable;
        }
        Iterator<? extends File> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            list.add((File)iterator.next());
        }
        iterator = iterable2.iterator();
        while (iterator.hasNext()) {
            list.add((File)iterator.next());
        }
        return list;
    }
    
    @Override
    public void flush() throws IOException {
        for (final Archive archive : this.archivesCache.values()) {
            archive.flush();
        }
    }
    
    private Archive getArchive(final File f) {
        Archive archive = this.archivesCache.get(f);
        if (archive == null) {
            archive = Archive.UNKNOWN_ARCHIVE;
            if (f.exists()) {
                try {
                    archive = new Archive(f);
                }
                catch (final ZipException ex) {}
                catch (final IOException ex2) {}
                if (archive != null) {
                    this.archivesCache.put(f, archive);
                }
            }
            this.archivesCache.put(f, archive);
        }
        return archive;
    }
    
    @Override
    public ClassLoader getClassLoader(final JavaFileManager.Location location) {
        final Iterable<? extends File> files = this.getLocation(location);
        if (files == null) {
            return null;
        }
        final ArrayList<URL> allURLs = new ArrayList<URL>();
        for (final File f : files) {
            try {
                allURLs.add(f.toURI().toURL());
            }
            catch (final MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        final URL[] result = new URL[allURLs.size()];
        return new URLClassLoader(allURLs.toArray(result), this.getClass().getClassLoader());
    }
    
    private Iterable<? extends File> getPathsFrom(final String path) {
        final ArrayList<FileSystem.Classpath> paths = new ArrayList<FileSystem.Classpath>();
        final ArrayList<File> files = new ArrayList<File>();
        try {
            this.processPathEntries(4, paths, path, this.charset.name(), false, false);
        }
        catch (final IllegalArgumentException ex) {
            return null;
        }
        for (final FileSystem.Classpath classpath : paths) {
            files.add(new File(classpath.getPath()));
        }
        return files;
    }
    
    Iterable<? extends File> getDefaultBootclasspath() {
        final List<File> files = new ArrayList<File>();
        String javaversion = System.getProperty("java.version");
        if (javaversion.length() > 3) {
            javaversion = javaversion.substring(0, 3);
        }
        final long jdkLevel = CompilerOptions.versionToJdkLevel(javaversion);
        if (jdkLevel < 3276800L) {
            return null;
        }
        for (final String fileName : Util.collectFilesNames()) {
            files.add(new File(fileName));
        }
        return files;
    }
    
    Iterable<? extends File> getDefaultClasspath() {
        final ArrayList<File> files = new ArrayList<File>();
        final String classProp = System.getProperty("java.class.path");
        if (classProp == null || classProp.length() == 0) {
            return null;
        }
        final StringTokenizer tokenizer = new StringTokenizer(classProp, File.pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            final File file = new File(token);
            if (file.exists()) {
                files.add(file);
            }
        }
        return files;
    }
    
    private Iterable<? extends File> getEndorsedDirsFrom(final String path) {
        final ArrayList<FileSystem.Classpath> paths = new ArrayList<FileSystem.Classpath>();
        final ArrayList<File> files = new ArrayList<File>();
        try {
            this.processPathEntries(4, paths, path, this.charset.name(), false, false);
        }
        catch (final IllegalArgumentException ex) {
            return null;
        }
        for (final FileSystem.Classpath classpath : paths) {
            files.add(new File(classpath.getPath()));
        }
        return files;
    }
    
    private Iterable<? extends File> getExtdirsFrom(final String path) {
        final ArrayList<FileSystem.Classpath> paths = new ArrayList<FileSystem.Classpath>();
        final ArrayList<File> files = new ArrayList<File>();
        try {
            this.processPathEntries(4, paths, path, this.charset.name(), false, false);
        }
        catch (final IllegalArgumentException ex) {
            return null;
        }
        for (final FileSystem.Classpath classpath : paths) {
            files.add(new File(classpath.getPath()));
        }
        return files;
    }
    
    private String getExtension(final File file) {
        final String name = file.getName();
        return this.getExtension(name);
    }
    
    private String getExtension(final String name) {
        final int index = name.lastIndexOf(46);
        if (index == -1) {
            return "";
        }
        return name.substring(index);
    }
    
    @Override
    public FileObject getFileForInput(final JavaFileManager.Location location, final String packageName, final String relativeName) throws IOException {
        final Iterable<? extends File> files = this.getLocation(location);
        if (files == null) {
            throw new IllegalArgumentException("Unknown location : " + location);
        }
        final String normalizedFileName = String.valueOf(this.normalized(packageName)) + '/' + relativeName.replace('\\', '/');
        for (final File file : files) {
            if (file.isDirectory()) {
                final File f = new File(file, normalizedFileName);
                if (f.exists()) {
                    return new EclipseFileObject(String.valueOf(packageName) + File.separator + relativeName, f.toURI(), this.getKind(f), this.charset);
                }
                continue;
            }
            else {
                if (!this.isArchive(file)) {
                    continue;
                }
                final Archive archive = this.getArchive(file);
                if (archive != Archive.UNKNOWN_ARCHIVE && archive.contains(normalizedFileName)) {
                    return archive.getArchiveFileObject(normalizedFileName, this.charset);
                }
                continue;
            }
        }
        return null;
    }
    
    @Override
    public FileObject getFileForOutput(final JavaFileManager.Location location, final String packageName, final String relativeName, final FileObject sibling) throws IOException {
        final Iterable<? extends File> files = this.getLocation(location);
        if (files == null) {
            throw new IllegalArgumentException("Unknown location : " + location);
        }
        final Iterator<? extends File> iterator = files.iterator();
        if (iterator.hasNext()) {
            final File file = (File)iterator.next();
            final String normalizedFileName = String.valueOf(this.normalized(packageName)) + '/' + relativeName.replace('\\', '/');
            final File f = new File(file, normalizedFileName);
            return new EclipseFileObject(String.valueOf(packageName) + File.separator + relativeName, f.toURI(), this.getKind(f), this.charset);
        }
        throw new IllegalArgumentException("location is empty : " + location);
    }
    
    @Override
    public JavaFileObject getJavaFileForInput(final JavaFileManager.Location location, final String className, final JavaFileObject.Kind kind) throws IOException {
        if (kind != JavaFileObject.Kind.CLASS && kind != JavaFileObject.Kind.SOURCE) {
            throw new IllegalArgumentException("Invalid kind : " + kind);
        }
        final Iterable<? extends File> files = this.getLocation(location);
        if (files == null) {
            throw new IllegalArgumentException("Unknown location : " + location);
        }
        String normalizedFileName = this.normalized(className);
        normalizedFileName = String.valueOf(normalizedFileName) + kind.extension;
        for (final File file : files) {
            if (file.isDirectory()) {
                final File f = new File(file, normalizedFileName);
                if (f.exists()) {
                    return new EclipseFileObject(className, f.toURI(), kind, this.charset);
                }
                continue;
            }
            else {
                if (!this.isArchive(file)) {
                    continue;
                }
                final Archive archive = this.getArchive(file);
                if (archive != Archive.UNKNOWN_ARCHIVE && archive.contains(normalizedFileName)) {
                    return archive.getArchiveFileObject(normalizedFileName, this.charset);
                }
                continue;
            }
        }
        return null;
    }
    
    @Override
    public JavaFileObject getJavaFileForOutput(final JavaFileManager.Location location, final String className, final JavaFileObject.Kind kind, final FileObject sibling) throws IOException {
        if (kind != JavaFileObject.Kind.CLASS && kind != JavaFileObject.Kind.SOURCE) {
            throw new IllegalArgumentException("Invalid kind : " + kind);
        }
        final Iterable<? extends File> files = this.getLocation(location);
        if (files == null) {
            if (!location.equals(StandardLocation.CLASS_OUTPUT) && !location.equals(StandardLocation.SOURCE_OUTPUT)) {
                throw new IllegalArgumentException("Unknown location : " + location);
            }
            if (sibling != null) {
                String normalizedFileName = this.normalized(className);
                int index = normalizedFileName.lastIndexOf(47);
                if (index != -1) {
                    normalizedFileName = normalizedFileName.substring(index + 1);
                }
                normalizedFileName = String.valueOf(normalizedFileName) + kind.extension;
                final URI uri = sibling.toUri();
                URI uri2 = null;
                try {
                    String path = uri.getPath();
                    index = path.lastIndexOf(47);
                    if (index != -1) {
                        path = path.substring(0, index + 1);
                        path = String.valueOf(path) + normalizedFileName;
                    }
                    uri2 = new URI(uri.getScheme(), uri.getHost(), path, uri.getFragment());
                }
                catch (final URISyntaxException ex) {
                    throw new IllegalArgumentException("invalid sibling");
                }
                return new EclipseFileObject(className, uri2, kind, this.charset);
            }
            String normalizedFileName = this.normalized(className);
            normalizedFileName = String.valueOf(normalizedFileName) + kind.extension;
            final File f = new File(System.getProperty("user.dir"), normalizedFileName);
            return new EclipseFileObject(className, f.toURI(), kind, this.charset);
        }
        else {
            final Iterator<? extends File> iterator = files.iterator();
            if (iterator.hasNext()) {
                final File file = (File)iterator.next();
                String normalizedFileName2 = this.normalized(className);
                normalizedFileName2 = String.valueOf(normalizedFileName2) + kind.extension;
                final File f2 = new File(file, normalizedFileName2);
                return new EclipseFileObject(className, f2.toURI(), kind, this.charset);
            }
            throw new IllegalArgumentException("location is empty : " + location);
        }
    }
    
    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjects(final File... files) {
        return this.getJavaFileObjectsFromFiles(Arrays.asList(files));
    }
    
    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjects(final String... names) {
        return this.getJavaFileObjectsFromStrings(Arrays.asList(names));
    }
    
    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(final Iterable<? extends File> files) {
        final ArrayList<JavaFileObject> javaFileArrayList = new ArrayList<JavaFileObject>();
        for (final File f : files) {
            if (f.isDirectory()) {
                throw new IllegalArgumentException("file : " + f.getAbsolutePath() + " is a directory");
            }
            javaFileArrayList.add(new EclipseFileObject(f.getAbsolutePath(), f.toURI(), this.getKind(f), this.charset));
        }
        return javaFileArrayList;
    }
    
    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjectsFromStrings(final Iterable<String> names) {
        final ArrayList<File> files = new ArrayList<File>();
        for (final String name : names) {
            files.add(new File(name));
        }
        return this.getJavaFileObjectsFromFiles(files);
    }
    
    public JavaFileObject.Kind getKind(final File f) {
        return this.getKind(this.getExtension(f));
    }
    
    private JavaFileObject.Kind getKind(final String extension) {
        if (JavaFileObject.Kind.CLASS.extension.equals(extension)) {
            return JavaFileObject.Kind.CLASS;
        }
        if (JavaFileObject.Kind.SOURCE.extension.equals(extension)) {
            return JavaFileObject.Kind.SOURCE;
        }
        if (JavaFileObject.Kind.HTML.extension.equals(extension)) {
            return JavaFileObject.Kind.HTML;
        }
        return JavaFileObject.Kind.OTHER;
    }
    
    @Override
    public Iterable<? extends File> getLocation(final JavaFileManager.Location location) {
        if (this.locations == null) {
            return null;
        }
        return this.locations.get(location.getName());
    }
    
    private Iterable<? extends File> getOutputDir(final String string) {
        if ("none".equals(string)) {
            return null;
        }
        final File file = new File(string);
        if (file.exists() && !file.isDirectory()) {
            throw new IllegalArgumentException("file : " + file.getAbsolutePath() + " is not a directory");
        }
        final ArrayList<File> list = new ArrayList<File>(1);
        list.add(file);
        return list;
    }
    
    @Override
    public boolean handleOption(final String current, final Iterator<String> remaining) {
        try {
            if ("-bootclasspath".equals(current)) {
                if (remaining.hasNext()) {
                    final Iterable<? extends File> bootclasspaths = this.getPathsFrom(remaining.next());
                    if (bootclasspaths != null) {
                        final Iterable<? extends File> iterable = this.getLocation(StandardLocation.PLATFORM_CLASS_PATH);
                        if ((this.flags & 0x4) == 0x0 && (this.flags & 0x1) == 0x0) {
                            this.setLocation(StandardLocation.PLATFORM_CLASS_PATH, bootclasspaths);
                        }
                        else if ((this.flags & 0x4) != 0x0) {
                            this.setLocation(StandardLocation.PLATFORM_CLASS_PATH, this.concatFiles(iterable, bootclasspaths));
                        }
                        else {
                            this.setLocation(StandardLocation.PLATFORM_CLASS_PATH, this.prependFiles(iterable, bootclasspaths));
                        }
                    }
                    this.flags |= 0x2;
                    return true;
                }
                throw new IllegalArgumentException();
            }
            else if ("-classpath".equals(current) || "-cp".equals(current)) {
                if (remaining.hasNext()) {
                    final Iterable<? extends File> classpaths = this.getPathsFrom(remaining.next());
                    if (classpaths != null) {
                        final Iterable<? extends File> iterable = this.getLocation(StandardLocation.CLASS_PATH);
                        if (iterable != null) {
                            this.setLocation(StandardLocation.CLASS_PATH, this.concatFiles(iterable, classpaths));
                        }
                        else {
                            this.setLocation(StandardLocation.CLASS_PATH, classpaths);
                        }
                        if ((this.flags & 0x8) == 0x0) {
                            this.setLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH, classpaths);
                        }
                    }
                    return true;
                }
                throw new IllegalArgumentException();
            }
            else if ("-encoding".equals(current)) {
                if (remaining.hasNext()) {
                    this.charset = Charset.forName(remaining.next());
                    return true;
                }
                throw new IllegalArgumentException();
            }
            else if ("-sourcepath".equals(current)) {
                if (remaining.hasNext()) {
                    final Iterable<? extends File> sourcepaths = this.getPathsFrom(remaining.next());
                    if (sourcepaths != null) {
                        this.setLocation(StandardLocation.SOURCE_PATH, sourcepaths);
                    }
                    return true;
                }
                throw new IllegalArgumentException();
            }
            else if ("-extdirs".equals(current)) {
                if (remaining.hasNext()) {
                    final Iterable<? extends File> iterable2 = this.getLocation(StandardLocation.PLATFORM_CLASS_PATH);
                    this.setLocation(StandardLocation.PLATFORM_CLASS_PATH, this.concatFiles(iterable2, this.getExtdirsFrom(remaining.next())));
                    this.flags |= 0x1;
                    return true;
                }
                throw new IllegalArgumentException();
            }
            else if ("-endorseddirs".equals(current)) {
                if (remaining.hasNext()) {
                    final Iterable<? extends File> iterable2 = this.getLocation(StandardLocation.PLATFORM_CLASS_PATH);
                    this.setLocation(StandardLocation.PLATFORM_CLASS_PATH, this.prependFiles(iterable2, this.getEndorsedDirsFrom(remaining.next())));
                    this.flags |= 0x4;
                    return true;
                }
                throw new IllegalArgumentException();
            }
            else if ("-d".equals(current)) {
                if (remaining.hasNext()) {
                    final Iterable<? extends File> outputDir = this.getOutputDir(remaining.next());
                    if (outputDir != null) {
                        this.setLocation(StandardLocation.CLASS_OUTPUT, outputDir);
                    }
                    return true;
                }
                throw new IllegalArgumentException();
            }
            else if ("-s".equals(current)) {
                if (remaining.hasNext()) {
                    final Iterable<? extends File> outputDir = this.getOutputDir(remaining.next());
                    if (outputDir != null) {
                        this.setLocation(StandardLocation.SOURCE_OUTPUT, outputDir);
                    }
                    return true;
                }
                throw new IllegalArgumentException();
            }
            else if ("-processorpath".equals(current)) {
                if (remaining.hasNext()) {
                    final Iterable<? extends File> processorpaths = this.getPathsFrom(remaining.next());
                    if (processorpaths != null) {
                        this.setLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH, processorpaths);
                    }
                    this.flags |= 0x8;
                    return true;
                }
                throw new IllegalArgumentException();
            }
        }
        catch (final IOException ex) {}
        return false;
    }
    
    @Override
    public boolean hasLocation(final JavaFileManager.Location location) {
        return this.locations != null && this.locations.containsKey(location.getName());
    }
    
    @Override
    public String inferBinaryName(final JavaFileManager.Location location, final JavaFileObject file) {
        String name = file.getName();
        JavaFileObject javaFileObject = null;
        final int index = name.lastIndexOf(46);
        if (index != -1) {
            name = name.substring(0, index);
        }
        try {
            javaFileObject = this.getJavaFileForInput(location, name, file.getKind());
        }
        catch (final IOException ex) {}
        catch (final IllegalArgumentException ex2) {
            return null;
        }
        if (javaFileObject == null) {
            return null;
        }
        return name.replace('/', '.');
    }
    
    private boolean isArchive(final File f) {
        final String extension = this.getExtension(f);
        return extension.equalsIgnoreCase(".jar") || extension.equalsIgnoreCase(".zip");
    }
    
    @Override
    public boolean isSameFile(final FileObject fileObject1, final FileObject fileObject2) {
        if (!(fileObject1 instanceof EclipseFileObject)) {
            throw new IllegalArgumentException("Unsupported file object class : " + fileObject1.getClass());
        }
        if (!(fileObject2 instanceof EclipseFileObject)) {
            throw new IllegalArgumentException("Unsupported file object class : " + fileObject2.getClass());
        }
        return fileObject1.equals(fileObject2);
    }
    
    @Override
    public int isSupportedOption(final String option) {
        return Options.processOptionsFileManager(option);
    }
    
    @Override
    public Iterable<JavaFileObject> list(final JavaFileManager.Location location, final String packageName, final Set<JavaFileObject.Kind> kinds, final boolean recurse) throws IOException {
        final Iterable<? extends File> allFilesInLocations = this.getLocation(location);
        if (allFilesInLocations == null) {
            throw new IllegalArgumentException("Unknown location : " + location);
        }
        final ArrayList<JavaFileObject> collector = new ArrayList<JavaFileObject>();
        final String normalizedPackageName = this.normalized(packageName);
        for (final File file : allFilesInLocations) {
            this.collectAllMatchingFiles(file, normalizedPackageName, kinds, recurse, collector);
        }
        return collector;
    }
    
    private String normalized(final String className) {
        final char[] classNameChars = className.toCharArray();
        for (int i = 0, max = classNameChars.length; i < max; ++i) {
            switch (classNameChars[i]) {
                case '\\': {
                    classNameChars[i] = '/';
                    break;
                }
                case '.': {
                    classNameChars[i] = '/';
                    break;
                }
            }
        }
        return new String(classNameChars);
    }
    
    private Iterable<? extends File> prependFiles(final Iterable<? extends File> iterable, final Iterable<? extends File> iterable2) {
        if (iterable2 == null) {
            return iterable;
        }
        final ArrayList<File> list = new ArrayList<File>();
        Iterator<? extends File> iterator = iterable2.iterator();
        while (iterator.hasNext()) {
            list.add((File)iterator.next());
        }
        iterator = iterable.iterator();
        while (iterator.hasNext()) {
            list.add((File)iterator.next());
        }
        return list;
    }
    
    @Override
    public void setLocation(final JavaFileManager.Location location, final Iterable<? extends File> path) throws IOException {
        if (path != null) {
            if (location.isOutputLocation()) {
                int count = 0;
                final Iterator<? extends File> iterator = path.iterator();
                while (iterator.hasNext()) {
                    iterator.next();
                    ++count;
                }
                if (count != 1) {
                    throw new IllegalArgumentException("output location can only have one path");
                }
            }
            this.locations.put(location.getName(), path);
        }
    }
    
    public void setLocale(final Locale locale) {
        this.locale = ((locale == null) ? Locale.getDefault() : locale);
        try {
            this.bundle = Main.ResourceBundleFactory.getBundle(this.locale);
        }
        catch (final MissingResourceException e) {
            System.out.println("Missing resource : " + "org.eclipse.jdt.internal.compiler.batch.messages".replace('.', '/') + ".properties for locale " + locale);
            throw e;
        }
    }
    
    public void processPathEntries(final int defaultSize, final ArrayList paths, final String currentPath, final String customEncoding, final boolean isSourceOnly, final boolean rejectDestinationPathOnJars) {
        String currentClasspathName = null;
        String currentDestinationPath = null;
        final ArrayList currentRuleSpecs = new ArrayList(defaultSize);
        final StringTokenizer tokenizer = new StringTokenizer(currentPath, String.valueOf(File.pathSeparator) + "[]", true);
        final ArrayList tokens = new ArrayList();
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }
        int state = 0;
        String token = null;
        for (int cursor = 0, tokensNb = tokens.size(), bracket = -1; cursor < tokensNb && state != 99; cursor = bracket + 1, state = 5) {
            token = tokens.get(cursor++);
            if (token.equals(File.pathSeparator)) {
                switch (state) {
                    case 0:
                    case 3:
                    case 10: {
                        break;
                    }
                    case 1:
                    case 2:
                    case 8: {
                        state = 3;
                        this.addNewEntry(paths, currentClasspathName, currentRuleSpecs, customEncoding, currentDestinationPath, isSourceOnly, rejectDestinationPathOnJars);
                        currentRuleSpecs.clear();
                        break;
                    }
                    case 6: {
                        state = 4;
                        break;
                    }
                    case 7: {
                        throw new IllegalArgumentException(this.bind("configure.incorrectDestinationPathEntry", currentPath));
                    }
                    case 11: {
                        cursor = bracket + 1;
                        state = 5;
                        break;
                    }
                    default: {
                        state = 99;
                        break;
                    }
                }
            }
            else if (token.equals("[")) {
                switch (state) {
                    case 0: {
                        currentClasspathName = "";
                    }
                    case 1: {
                        bracket = cursor - 1;
                    }
                    case 11: {
                        state = 10;
                        break;
                    }
                    case 2: {
                        state = 9;
                        break;
                    }
                    case 8: {
                        state = 5;
                        break;
                    }
                    default: {
                        state = 99;
                        break;
                    }
                }
            }
            else if (token.equals("]")) {
                switch (state) {
                    case 6: {
                        state = 2;
                        break;
                    }
                    case 7: {
                        state = 8;
                        break;
                    }
                    case 10: {
                        state = 11;
                        break;
                    }
                    default: {
                        state = 99;
                        break;
                    }
                }
            }
            else {
                switch (state) {
                    case 0:
                    case 3: {
                        state = 1;
                        currentClasspathName = token;
                        break;
                    }
                    case 5: {
                        if (!token.startsWith("-d "))
                        if (currentDestinationPath != null) {
                            throw new IllegalArgumentException(this.bind("configure.duplicateDestinationPathEntry", currentPath));
                        }
                        currentDestinationPath = token.substring(3).trim();
                        state = 7;
                        break;
                    }
                    case 4: {
                        if (currentDestinationPath != null) {
                            throw new IllegalArgumentException(this.bind("configure.accessRuleAfterDestinationPath", currentPath));
                        }
                        state = 6;
                        currentRuleSpecs.add(token);
                        break;
                    }
                    case 9: {
                        if (!token.startsWith("-d ")) {
                            state = 99;
                            break;
                        }
                        currentDestinationPath = token.substring(3).trim();
                        state = 7;
                        break;
                    }
                    case 11: {
                        for (int i = bracket; i < cursor; ++i) {
                            currentClasspathName = String.valueOf(currentClasspathName) + tokens.get(i);
                        }
                        state = 1;
                        break;
                    }
                    case 10: {
                        break;
                    }
                    default: {
                        state = 99;
                        break;
                    }
                }
            }
            if (state == 11 && cursor == tokensNb) {}
        }
        switch (state) {
            case 1:
            case 2:
            case 8: {
                this.addNewEntry(paths, currentClasspathName, currentRuleSpecs, customEncoding, currentDestinationPath, isSourceOnly, rejectDestinationPathOnJars);
                break;
            }
        }
    }
    
    protected void addNewEntry(final ArrayList paths, final String currentClasspathName, final ArrayList currentRuleSpecs, final String customEncoding, String destPath, final boolean isSourceOnly, final boolean rejectDestinationPathOnJars) {
        final int rulesSpecsSize = currentRuleSpecs.size();
        AccessRuleSet accessRuleSet = null;
        if (rulesSpecsSize != 0) {
            final AccessRule[] accessRules = new AccessRule[currentRuleSpecs.size()];
            boolean rulesOK = true;
            final Iterator i = currentRuleSpecs.iterator();
            int j = 0;
            while (i.hasNext()) {
                final String ruleSpec = i.next();
                final char key = ruleSpec.charAt(0);
                final String pattern = ruleSpec.substring(1);
                if (pattern.length() > 0) {
                    switch (key) {
                        case '+': {
                            accessRules[j++] = new AccessRule(pattern.toCharArray(), 0);
                            continue;
                        }
                        case '~': {
                            accessRules[j++] = new AccessRule(pattern.toCharArray(), 16777496);
                            continue;
                        }
                        case '-': {
                            accessRules[j++] = new AccessRule(pattern.toCharArray(), 16777523);
                            continue;
                        }
                        case '?': {
                            accessRules[j++] = new AccessRule(pattern.toCharArray(), 16777523, true);
                            continue;
                        }
                        default: {
                            rulesOK = false;
                            continue;
                        }
                    }
                }
                else {
                    rulesOK = false;
                }
            }
            if (!rulesOK) {
                return;
            }
            accessRuleSet = new AccessRuleSet(accessRules, (byte)0, currentClasspathName);
        }
        if ("none".equals(destPath)) {
            destPath = "none";
        }
        if (rejectDestinationPathOnJars && destPath != null && (currentClasspathName.endsWith(".jar") || currentClasspathName.endsWith(".zip"))) {
            throw new IllegalArgumentException(this.bind("configure.unexpectedDestinationPathEntryFile", currentClasspathName));
        }
        final FileSystem.Classpath currentClasspath = FileSystem.getClasspath(currentClasspathName, customEncoding, isSourceOnly, accessRuleSet, destPath, null);
        if (currentClasspath != null) {
            paths.add(currentClasspath);
        }
    }
    
    private String bind(final String id, final String binding) {
        return this.bind(id, new String[] { binding });
    }
    
    private String bind(final String id, final String[] arguments) {
        if (id == null) {
            return "No message available";
        }
        String message = null;
        try {
            message = this.bundle.getString(id);
        }
        catch (final MissingResourceException ex) {
            return "Missing message: " + id + " in: " + "org.eclipse.jdt.internal.compiler.batch.messages";
        }
        return MessageFormat.format(message, (Object[])arguments);
    }
}
