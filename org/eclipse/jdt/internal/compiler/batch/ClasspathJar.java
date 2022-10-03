package org.eclipse.jdt.internal.compiler.batch;

import org.eclipse.jdt.internal.compiler.util.Util;
import java.util.Enumeration;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import java.io.IOException;
import org.eclipse.jdt.internal.compiler.util.ManifestAnalyzer;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import java.util.List;
import java.util.Hashtable;
import java.util.zip.ZipFile;
import java.io.File;

public class ClasspathJar extends ClasspathLocation
{
    protected File file;
    protected ZipFile zipFile;
    protected ZipFile annotationZipFile;
    protected boolean closeZipFileAtEnd;
    protected Hashtable packageCache;
    protected List<String> annotationPaths;
    
    public ClasspathJar(final File file, final boolean closeZipFileAtEnd, final AccessRuleSet accessRuleSet, final String destinationPath) {
        super(accessRuleSet, destinationPath);
        this.file = file;
        this.closeZipFileAtEnd = closeZipFileAtEnd;
    }
    
    @Override
    public List fetchLinkedJars(final FileSystem.ClasspathSectionProblemReporter problemReporter) {
        InputStream inputStream = null;
        try {
            this.initialize();
            final ArrayList result = new ArrayList();
            final ZipEntry manifest = this.zipFile.getEntry("META-INF/MANIFEST.MF");
            if (manifest != null) {
                inputStream = this.zipFile.getInputStream(manifest);
                final ManifestAnalyzer analyzer = new ManifestAnalyzer();
                final boolean success = analyzer.analyzeManifestContents(inputStream);
                final List calledFileNames = analyzer.getCalledFileNames();
                if (problemReporter != null) {
                    if (!success || (analyzer.getClasspathSectionsCount() == 1 && calledFileNames == null)) {
                        problemReporter.invalidClasspathSection(this.getPath());
                    }
                    else if (analyzer.getClasspathSectionsCount() > 1) {
                        problemReporter.multipleClasspathSections(this.getPath());
                    }
                }
                if (calledFileNames != null) {
                    final Iterator calledFilesIterator = calledFileNames.iterator();
                    String directoryPath = this.getPath();
                    final int lastSeparator = directoryPath.lastIndexOf(File.separatorChar);
                    directoryPath = directoryPath.substring(0, lastSeparator + 1);
                    while (calledFilesIterator.hasNext()) {
                        result.add(new ClasspathJar(new File(String.valueOf(directoryPath) + calledFilesIterator.next()), this.closeZipFileAtEnd, this.accessRuleSet, this.destinationPath));
                    }
                }
            }
            return result;
        }
        catch (final IOException ex) {
            return null;
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final IOException ex2) {}
            }
        }
    }
    
    @Override
    public NameEnvironmentAnswer findClass(final char[] typeName, final String qualifiedPackageName, final String qualifiedBinaryFileName) {
        return this.findClass(typeName, qualifiedPackageName, qualifiedBinaryFileName, false);
    }
    
    @Override
    public NameEnvironmentAnswer findClass(final char[] typeName, final String qualifiedPackageName, final String qualifiedBinaryFileName, final boolean asBinaryOnly) {
        if (!this.isPackage(qualifiedPackageName)) {
            return null;
        }
        try {
            final ClassFileReader reader = ClassFileReader.read(this.zipFile, qualifiedBinaryFileName);
            if (reader != null) {
                if (this.annotationPaths != null) {
                    final String qualifiedClassName = qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - "CLASS".length() - 1);
                    for (final String annotationPath : this.annotationPaths) {
                        try {
                            this.annotationZipFile = reader.setExternalAnnotationProvider(annotationPath, qualifiedClassName, this.annotationZipFile, null);
                            if (reader.hasAnnotationProvider()) {
                                break;
                            }
                            continue;
                        }
                        catch (final IOException ex) {}
                    }
                }
                return new NameEnvironmentAnswer(reader, this.fetchAccessRestriction(qualifiedBinaryFileName));
            }
        }
        catch (final ClassFormatException ex2) {}
        catch (final IOException ex3) {}
        return null;
    }
    
    @Override
    public boolean hasAnnotationFileFor(final String qualifiedTypeName) {
        return this.zipFile.getEntry(String.valueOf(qualifiedTypeName) + ".eea") != null;
    }
    
    @Override
    public char[][][] findTypeNames(final String qualifiedPackageName) {
        if (!this.isPackage(qualifiedPackageName)) {
            return null;
        }
        final ArrayList answers = new ArrayList();
        final Enumeration e = this.zipFile.entries();
        while (e.hasMoreElements()) {
            final String fileName = e.nextElement().getName();
            final int last = fileName.lastIndexOf(47);
            while (last > 0) {
                final String packageName = fileName.substring(0, last);
                if (!qualifiedPackageName.equals(packageName)) {
                    break;
                }
                final int indexOfDot = fileName.lastIndexOf(46);
                if (indexOfDot == -1) {
                    continue;
                }
                final String typeName = fileName.substring(last + 1, indexOfDot);
                final char[] packageArray = packageName.toCharArray();
                answers.add(CharOperation.arrayConcat(CharOperation.splitOn('/', packageArray), typeName.toCharArray()));
            }
        }
        final int size = answers.size();
        if (size != 0) {
            final char[][][] result = new char[size][][];
            answers.toArray(result);
            return null;
        }
        return null;
    }
    
    @Override
    public void initialize() throws IOException {
        if (this.zipFile == null) {
            this.zipFile = new ZipFile(this.file);
        }
    }
    
    @Override
    public boolean isPackage(final String qualifiedPackageName) {
        if (this.packageCache != null) {
            return this.packageCache.containsKey(qualifiedPackageName);
        }
        (this.packageCache = new Hashtable(41)).put(Util.EMPTY_STRING, Util.EMPTY_STRING);
        final Enumeration e = this.zipFile.entries();
        while (e.hasMoreElements()) {
            final String fileName = e.nextElement().getName();
            String packageName;
            for (int last = fileName.lastIndexOf(47); last > 0; last = packageName.lastIndexOf(47)) {
                packageName = fileName.substring(0, last);
                if (this.packageCache.containsKey(packageName)) {
                    break;
                }
                this.packageCache.put(packageName, packageName);
            }
        }
        return this.packageCache.containsKey(qualifiedPackageName);
    }
    
    @Override
    public void reset() {
        if (this.closeZipFileAtEnd) {
            if (this.zipFile != null) {
                try {
                    this.zipFile.close();
                }
                catch (final IOException ex) {}
                this.zipFile = null;
            }
            if (this.annotationZipFile != null) {
                try {
                    this.annotationZipFile.close();
                }
                catch (final IOException ex2) {}
                this.annotationZipFile = null;
            }
        }
        this.packageCache = null;
    }
    
    @Override
    public String toString() {
        return "Classpath for jar file " + this.file.getPath();
    }
    
    @Override
    public char[] normalizedPath() {
        if (this.normalizedPath == null) {
            final String path2 = this.getPath();
            final char[] rawName = path2.toCharArray();
            if (File.separatorChar == '\\') {
                CharOperation.replace(rawName, '\\', '/');
            }
            this.normalizedPath = CharOperation.subarray(rawName, 0, CharOperation.lastIndexOf('.', rawName));
        }
        return this.normalizedPath;
    }
    
    @Override
    public String getPath() {
        if (this.path == null) {
            try {
                this.path = this.file.getCanonicalPath();
            }
            catch (final IOException ex) {
                this.path = this.file.getAbsolutePath();
            }
        }
        return this.path;
    }
    
    @Override
    public int getMode() {
        return 2;
    }
}
