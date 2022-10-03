package org.eclipse.jdt.internal.compiler.batch;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.zip.ZipFile;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.util.HashSet;
import org.eclipse.jdt.internal.compiler.util.Util;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;

public class FileSystem implements INameEnvironment, SuffixConstants
{
    protected Classpath[] classpaths;
    Set knownFileNames;
    protected boolean annotationsFromClasspath;
    
    public FileSystem(final String[] classpathNames, final String[] initialFileNames, final String encoding) {
        final int classpathSize = classpathNames.length;
        this.classpaths = new Classpath[classpathSize];
        int counter = 0;
        for (int i = 0; i < classpathSize; ++i) {
            final Classpath classpath = getClasspath(classpathNames[i], encoding, null, null);
            try {
                classpath.initialize();
                this.classpaths[counter++] = classpath;
            }
            catch (final IOException ex) {}
        }
        if (counter != classpathSize) {
            System.arraycopy(this.classpaths, 0, this.classpaths = new Classpath[counter], 0, counter);
        }
        this.initializeKnownFileNames(initialFileNames);
    }
    
    protected FileSystem(final Classpath[] paths, final String[] initialFileNames, final boolean annotationsFromClasspath) {
        final int length = paths.length;
        int counter = 0;
        this.classpaths = new Classpath[length];
        for (final Classpath classpath : paths) {
            try {
                classpath.initialize();
                this.classpaths[counter++] = classpath;
            }
            catch (final IOException ex) {}
        }
        if (counter != length) {
            System.arraycopy(this.classpaths, 0, this.classpaths = new Classpath[counter], 0, counter);
        }
        this.initializeKnownFileNames(initialFileNames);
        this.annotationsFromClasspath = annotationsFromClasspath;
    }
    
    public static Classpath getClasspath(final String classpathName, final String encoding, final AccessRuleSet accessRuleSet) {
        return getClasspath(classpathName, encoding, false, accessRuleSet, null, null);
    }
    
    public static Classpath getClasspath(final String classpathName, final String encoding, final AccessRuleSet accessRuleSet, final Map options) {
        return getClasspath(classpathName, encoding, false, accessRuleSet, null, options);
    }
    
    public static Classpath getClasspath(final String classpathName, final String encoding, final boolean isSourceOnly, final AccessRuleSet accessRuleSet, final String destinationPath, final Map options) {
        Classpath result = null;
        final File file = new File(convertPathSeparators(classpathName));
        if (file.isDirectory()) {
            if (file.exists()) {
                result = new ClasspathDirectory(file, encoding, isSourceOnly ? 1 : 3, accessRuleSet, (destinationPath == null || destinationPath == "none") ? destinationPath : convertPathSeparators(destinationPath), options);
            }
        }
        else if (Util.isPotentialZipArchive(classpathName)) {
            if (isSourceOnly) {
                result = new ClasspathSourceJar(file, true, accessRuleSet, encoding, (destinationPath == null || destinationPath == "none") ? destinationPath : convertPathSeparators(destinationPath));
            }
            else if (destinationPath == null) {
                result = new ClasspathJar(file, true, accessRuleSet, null);
            }
        }
        return result;
    }
    
    private void initializeKnownFileNames(final String[] initialFileNames) {
        if (initialFileNames == null) {
            this.knownFileNames = new HashSet(0);
            return;
        }
        this.knownFileNames = new HashSet(initialFileNames.length * 2);
        int i = initialFileNames.length;
        while (--i >= 0) {
            final File compilationUnitFile = new File(initialFileNames[i]);
            char[] fileName = null;
            try {
                fileName = compilationUnitFile.getCanonicalPath().toCharArray();
            }
            catch (final IOException ex) {
                continue;
            }
            char[] matchingPathName = null;
            final int lastIndexOf = CharOperation.lastIndexOf('.', fileName);
            if (lastIndexOf != -1) {
                fileName = CharOperation.subarray(fileName, 0, lastIndexOf);
            }
            CharOperation.replace(fileName, '\\', '/');
            boolean globalPathMatches = false;
            for (int j = 0, max = this.classpaths.length; j < max; ++j) {
                final char[] matchCandidate = this.classpaths[j].normalizedPath();
                boolean currentPathMatch = false;
                if (this.classpaths[j] instanceof ClasspathDirectory && CharOperation.prefixEquals(matchCandidate, fileName)) {
                    currentPathMatch = true;
                    if (matchingPathName == null) {
                        matchingPathName = matchCandidate;
                    }
                    else if (currentPathMatch) {
                        if (matchCandidate.length > matchingPathName.length) {
                            matchingPathName = matchCandidate;
                        }
                    }
                    else if (!globalPathMatches && matchCandidate.length < matchingPathName.length) {
                        matchingPathName = matchCandidate;
                    }
                    if (currentPathMatch) {
                        globalPathMatches = true;
                    }
                }
            }
            if (matchingPathName == null) {
                this.knownFileNames.add(new String(fileName));
            }
            else {
                this.knownFileNames.add(new String(CharOperation.subarray(fileName, matchingPathName.length, fileName.length)));
            }
            matchingPathName = null;
        }
    }
    
    @Override
    public void cleanup() {
        for (int i = 0, max = this.classpaths.length; i < max; ++i) {
            this.classpaths[i].reset();
        }
    }
    
    private static String convertPathSeparators(final String path) {
        return (File.separatorChar == '/') ? path.replace('\\', '/') : path.replace('/', '\\');
    }
    
    private NameEnvironmentAnswer findClass(final String qualifiedTypeName, final char[] typeName, final boolean asBinaryOnly) {
        final NameEnvironmentAnswer answer = this.internalFindClass(qualifiedTypeName, typeName, asBinaryOnly);
        if (this.annotationsFromClasspath && answer != null && answer.getBinaryType() instanceof ClassFileReader) {
            for (int i = 0, length = this.classpaths.length; i < length; ++i) {
                final Classpath classpathEntry = this.classpaths[i];
                if (classpathEntry.hasAnnotationFileFor(qualifiedTypeName)) {
                    final ZipFile zip = (classpathEntry instanceof ClasspathJar) ? ((ClasspathJar)classpathEntry).zipFile : null;
                    try {
                        ((ClassFileReader)answer.getBinaryType()).setExternalAnnotationProvider(classpathEntry.getPath(), qualifiedTypeName, zip, null);
                        break;
                    }
                    catch (final IOException ex) {}
                }
            }
        }
        return answer;
    }
    
    private NameEnvironmentAnswer internalFindClass(final String qualifiedTypeName, final char[] typeName, final boolean asBinaryOnly) {
        if (this.knownFileNames.contains(qualifiedTypeName)) {
            return null;
        }
        final String qualifiedBinaryFileName = String.valueOf(qualifiedTypeName) + ".class";
        final String qualifiedPackageName = (qualifiedTypeName.length() == typeName.length) ? Util.EMPTY_STRING : qualifiedBinaryFileName.substring(0, qualifiedTypeName.length() - typeName.length - 1);
        final String qp2 = (File.separatorChar == '/') ? qualifiedPackageName : qualifiedPackageName.replace('/', File.separatorChar);
        NameEnvironmentAnswer suggestedAnswer = null;
        if (qualifiedPackageName == qp2) {
            for (int i = 0, length = this.classpaths.length; i < length; ++i) {
                final NameEnvironmentAnswer answer = this.classpaths[i].findClass(typeName, qualifiedPackageName, qualifiedBinaryFileName, asBinaryOnly);
                if (answer != null) {
                    if (!answer.ignoreIfBetter()) {
                        if (answer.isBetter(suggestedAnswer)) {
                            return answer;
                        }
                    }
                    else if (answer.isBetter(suggestedAnswer)) {
                        suggestedAnswer = answer;
                    }
                }
            }
        }
        else {
            final String qb2 = qualifiedBinaryFileName.replace('/', File.separatorChar);
            for (int j = 0, length2 = this.classpaths.length; j < length2; ++j) {
                final Classpath p = this.classpaths[j];
                final NameEnvironmentAnswer answer2 = (p instanceof ClasspathJar) ? p.findClass(typeName, qualifiedPackageName, qualifiedBinaryFileName, asBinaryOnly) : p.findClass(typeName, qp2, qb2, asBinaryOnly);
                if (answer2 != null) {
                    if (!answer2.ignoreIfBetter()) {
                        if (answer2.isBetter(suggestedAnswer)) {
                            return answer2;
                        }
                    }
                    else if (answer2.isBetter(suggestedAnswer)) {
                        suggestedAnswer = answer2;
                    }
                }
            }
        }
        if (suggestedAnswer != null) {
            return suggestedAnswer;
        }
        return null;
    }
    
    @Override
    public NameEnvironmentAnswer findType(final char[][] compoundName) {
        if (compoundName != null) {
            return this.findClass(new String(CharOperation.concatWith(compoundName, '/')), compoundName[compoundName.length - 1], false);
        }
        return null;
    }
    
    public char[][][] findTypeNames(final char[][] packageName) {
        char[][][] result = null;
        if (packageName != null) {
            final String qualifiedPackageName = new String(CharOperation.concatWith(packageName, '/'));
            final String qualifiedPackageName2 = (File.separatorChar == '/') ? qualifiedPackageName : qualifiedPackageName.replace('/', File.separatorChar);
            if (qualifiedPackageName == qualifiedPackageName2) {
                for (int i = 0, length = this.classpaths.length; i < length; ++i) {
                    final char[][][] answers = this.classpaths[i].findTypeNames(qualifiedPackageName);
                    if (answers != null) {
                        if (result == null) {
                            result = answers;
                        }
                        else {
                            final int resultLength = result.length;
                            final int answersLength = answers.length;
                            System.arraycopy(result, 0, result = new char[answersLength + resultLength][][], 0, resultLength);
                            System.arraycopy(answers, 0, result, resultLength, answersLength);
                        }
                    }
                }
            }
            else {
                for (int i = 0, length = this.classpaths.length; i < length; ++i) {
                    final Classpath p = this.classpaths[i];
                    final char[][][] answers2 = (p instanceof ClasspathJar) ? p.findTypeNames(qualifiedPackageName) : p.findTypeNames(qualifiedPackageName2);
                    if (answers2 != null) {
                        if (result == null) {
                            result = answers2;
                        }
                        else {
                            final int resultLength2 = result.length;
                            final int answersLength2 = answers2.length;
                            System.arraycopy(result, 0, result = new char[answersLength2 + resultLength2][][], 0, resultLength2);
                            System.arraycopy(answers2, 0, result, resultLength2, answersLength2);
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public NameEnvironmentAnswer findType(final char[][] compoundName, final boolean asBinaryOnly) {
        if (compoundName != null) {
            return this.findClass(new String(CharOperation.concatWith(compoundName, '/')), compoundName[compoundName.length - 1], asBinaryOnly);
        }
        return null;
    }
    
    @Override
    public NameEnvironmentAnswer findType(final char[] typeName, final char[][] packageName) {
        if (typeName != null) {
            return this.findClass(new String(CharOperation.concatWith(packageName, typeName, '/')), typeName, false);
        }
        return null;
    }
    
    @Override
    public boolean isPackage(final char[][] compoundName, final char[] packageName) {
        final String qualifiedPackageName = new String(CharOperation.concatWith(compoundName, packageName, '/'));
        final String qp2 = (File.separatorChar == '/') ? qualifiedPackageName : qualifiedPackageName.replace('/', File.separatorChar);
        if (qualifiedPackageName == qp2) {
            for (int i = 0, length = this.classpaths.length; i < length; ++i) {
                if (this.classpaths[i].isPackage(qualifiedPackageName)) {
                    return true;
                }
            }
        }
        else {
            for (int i = 0, length = this.classpaths.length; i < length; ++i) {
                final Classpath p = this.classpaths[i];
                if (p instanceof ClasspathJar) {
                    if (p.isPackage(qualifiedPackageName)) {
                        return true;
                    }
                }
                else if (p.isPackage(qp2)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static class ClasspathNormalizer
    {
        public static ArrayList normalize(final ArrayList classpaths) {
            final ArrayList normalizedClasspath = new ArrayList();
            final HashSet cache = new HashSet();
            for (final Classpath classpath : classpaths) {
                if (!cache.contains(classpath)) {
                    normalizedClasspath.add(classpath);
                    cache.add(classpath);
                }
            }
            return normalizedClasspath;
        }
    }
    
    public interface Classpath
    {
        char[][][] findTypeNames(final String p0);
        
        NameEnvironmentAnswer findClass(final char[] p0, final String p1, final String p2);
        
        NameEnvironmentAnswer findClass(final char[] p0, final String p1, final String p2, final boolean p3);
        
        boolean isPackage(final String p0);
        
        List fetchLinkedJars(final ClasspathSectionProblemReporter p0);
        
        void reset();
        
        char[] normalizedPath();
        
        String getPath();
        
        void initialize() throws IOException;
        
        boolean hasAnnotationFileFor(final String p0);
    }
    
    public interface ClasspathSectionProblemReporter
    {
        void invalidClasspathSection(final String p0);
        
        void multipleClasspathSections(final String p0);
    }
}
