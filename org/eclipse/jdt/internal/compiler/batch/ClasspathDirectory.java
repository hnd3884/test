package org.eclipse.jdt.internal.compiler.batch;

import java.io.FilenameFilter;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import java.util.List;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
import java.io.IOException;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import java.io.File;
import java.util.Map;
import java.util.Hashtable;

public class ClasspathDirectory extends ClasspathLocation
{
    private Hashtable directoryCache;
    private String[] missingPackageHolder;
    private int mode;
    private String encoding;
    private Hashtable<String, Hashtable<String, String>> packageSecondaryTypes;
    Map options;
    
    ClasspathDirectory(final File directory, final String encoding, final int mode, final AccessRuleSet accessRuleSet, final String destinationPath, final Map options) {
        super(accessRuleSet, destinationPath);
        this.missingPackageHolder = new String[1];
        this.packageSecondaryTypes = null;
        this.mode = mode;
        this.options = options;
        try {
            this.path = directory.getCanonicalPath();
        }
        catch (final IOException ex) {
            this.path = directory.getAbsolutePath();
        }
        if (!this.path.endsWith(File.separator)) {
            this.path = String.valueOf(this.path) + File.separator;
        }
        this.directoryCache = new Hashtable(11);
        this.encoding = encoding;
    }
    
    String[] directoryList(final String qualifiedPackageName) {
        String[] dirList = this.directoryCache.get(qualifiedPackageName);
        if (dirList == this.missingPackageHolder) {
            return null;
        }
        if (dirList != null) {
            return dirList;
        }
        final File dir = new File(String.valueOf(this.path) + qualifiedPackageName);
        Label_0186: {
            if (dir.isDirectory()) {
                int index = qualifiedPackageName.length();
                final int last = qualifiedPackageName.lastIndexOf(File.separatorChar);
                while (--index > last && !ScannerHelper.isUpperCase(qualifiedPackageName.charAt(index))) {}
                if (index > last) {
                    if (last == -1) {
                        if (!this.doesFileExist(qualifiedPackageName, Util.EMPTY_STRING)) {
                            break Label_0186;
                        }
                    }
                    else {
                        final String packageName = qualifiedPackageName.substring(last + 1);
                        final String parentPackage = qualifiedPackageName.substring(0, last);
                        if (!this.doesFileExist(packageName, parentPackage)) {
                            break Label_0186;
                        }
                    }
                }
                if ((dirList = dir.list()) == null) {
                    dirList = CharOperation.NO_STRINGS;
                }
                this.directoryCache.put(qualifiedPackageName, dirList);
                return dirList;
            }
        }
        this.directoryCache.put(qualifiedPackageName, this.missingPackageHolder);
        return null;
    }
    
    boolean doesFileExist(final String fileName, final String qualifiedPackageName) {
        final String[] dirList = this.directoryList(qualifiedPackageName);
        if (dirList == null) {
            return false;
        }
        int i = dirList.length;
        while (--i >= 0) {
            if (fileName.equals(dirList[i])) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List fetchLinkedJars(final FileSystem.ClasspathSectionProblemReporter problemReporter) {
        return null;
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
        final String fileName = new String(typeName);
        final boolean binaryExists = (this.mode & 0x2) != 0x0 && this.doesFileExist(String.valueOf(fileName) + ".class", qualifiedPackageName);
        final boolean sourceExists = (this.mode & 0x1) != 0x0 && this.doesFileExist(String.valueOf(fileName) + ".java", qualifiedPackageName);
        if (sourceExists && !asBinaryOnly) {
            final String fullSourcePath = String.valueOf(this.path) + qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - 6) + ".java";
            if (!binaryExists) {
                return new NameEnvironmentAnswer(new CompilationUnit(null, fullSourcePath, this.encoding, this.destinationPath), this.fetchAccessRestriction(qualifiedBinaryFileName));
            }
            final String fullBinaryPath = String.valueOf(this.path) + qualifiedBinaryFileName;
            final long binaryModified = new File(fullBinaryPath).lastModified();
            final long sourceModified = new File(fullSourcePath).lastModified();
            if (sourceModified > binaryModified) {
                return new NameEnvironmentAnswer(new CompilationUnit(null, fullSourcePath, this.encoding, this.destinationPath), this.fetchAccessRestriction(qualifiedBinaryFileName));
            }
        }
        if (binaryExists) {
            try {
                ClassFileReader reader = ClassFileReader.read(String.valueOf(this.path) + qualifiedBinaryFileName);
                final String typeSearched = (qualifiedPackageName.length() > 0) ? (String.valueOf(qualifiedPackageName.replace(File.separatorChar, '/')) + "/" + fileName) : fileName;
                if (!CharOperation.equals(reader.getName(), typeSearched.toCharArray())) {
                    reader = null;
                }
                if (reader != null) {
                    return new NameEnvironmentAnswer(reader, this.fetchAccessRestriction(qualifiedBinaryFileName));
                }
            }
            catch (final IOException ex) {}
            catch (final ClassFormatException ex2) {}
        }
        return null;
    }
    
    public NameEnvironmentAnswer findSecondaryInClass(final char[] typeName, final String qualifiedPackageName, final String qualifiedBinaryFileName) {
        if (TypeConstants.PACKAGE_INFO_NAME.equals(typeName)) {
            return null;
        }
        final String typeNameString = new String(typeName);
        final boolean prereqs = this.options != null && this.isPackage(qualifiedPackageName) && (this.mode & 0x1) != 0x0 && this.doesFileExist(String.valueOf(typeNameString) + ".java", qualifiedPackageName);
        return prereqs ? null : this.findSourceSecondaryType(typeNameString, qualifiedPackageName, qualifiedBinaryFileName);
    }
    
    @Override
    public boolean hasAnnotationFileFor(final String qualifiedTypeName) {
        final int pos = qualifiedTypeName.lastIndexOf(47);
        if (pos != -1 && pos + 1 < qualifiedTypeName.length()) {
            final String fileName = String.valueOf(qualifiedTypeName.substring(pos + 1)) + ".eea";
            return this.doesFileExist(fileName, qualifiedTypeName.substring(0, pos));
        }
        return false;
    }
    
    private Hashtable<String, String> getPackageTypes(final String qualifiedPackageName) {
        final Hashtable<String, String> packageEntry = new Hashtable<String, String>();
        final String[] dirList = this.directoryCache.get(qualifiedPackageName);
        if (dirList == this.missingPackageHolder || dirList == null) {
            return packageEntry;
        }
        final File dir = new File(String.valueOf(this.path) + qualifiedPackageName);
        final File[] listFiles = (File[])(dir.isDirectory() ? dir.listFiles() : null);
        if (listFiles == null) {
            return packageEntry;
        }
        for (int i = 0, l = listFiles.length; i < l; ++i) {
            final File f = listFiles[i];
            if (!f.isDirectory()) {
                final String s = f.getAbsolutePath();
                if (s != null) {
                    if (s.endsWith(".java") || s.endsWith(".JAVA")) {
                        final CompilationUnit cu = new CompilationUnit(null, s, this.encoding, this.destinationPath);
                        final CompilationResult compilationResult = new CompilationResult(cu.getContents(), 1, 1, 10);
                        final ProblemReporter problemReporter = new ProblemReporter(DefaultErrorHandlingPolicies.proceedWithAllProblems(), new CompilerOptions(this.options), new DefaultProblemFactory());
                        final Parser parser = new Parser(problemReporter, false);
                        parser.reportSyntaxErrorIsRequired = false;
                        final CompilationUnitDeclaration unit = parser.parse(cu, compilationResult);
                        final TypeDeclaration[] types = (TypeDeclaration[])((unit != null) ? unit.types : null);
                        if (types != null) {
                            for (int j = 0, k = types.length; j < k; ++j) {
                                final TypeDeclaration type = types[j];
                                final char[] name = (char[])(type.isSecondary() ? type.name : null);
                                if (name != null) {
                                    packageEntry.put(new String(name), s);
                                }
                            }
                        }
                    }
                }
            }
        }
        return packageEntry;
    }
    
    private NameEnvironmentAnswer findSourceSecondaryType(final String typeName, final String qualifiedPackageName, final String qualifiedBinaryFileName) {
        if (this.packageSecondaryTypes == null) {
            this.packageSecondaryTypes = new Hashtable<String, Hashtable<String, String>>();
        }
        Hashtable<String, String> packageEntry = this.packageSecondaryTypes.get(qualifiedPackageName);
        if (packageEntry == null) {
            packageEntry = this.getPackageTypes(qualifiedPackageName);
            this.packageSecondaryTypes.put(qualifiedPackageName, packageEntry);
        }
        final String fileName = packageEntry.get(typeName);
        return (fileName != null) ? new NameEnvironmentAnswer(new CompilationUnit(null, fileName, this.encoding, this.destinationPath), this.fetchAccessRestriction(qualifiedBinaryFileName)) : null;
    }
    
    @Override
    public char[][][] findTypeNames(final String qualifiedPackageName) {
        if (!this.isPackage(qualifiedPackageName)) {
            return null;
        }
        final File dir = new File(String.valueOf(this.path) + qualifiedPackageName);
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }
        final String[] listFiles = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(final File directory1, final String name) {
                final String fileName = name.toLowerCase();
                return fileName.endsWith(".class") || fileName.endsWith(".java");
            }
        });
        final int length;
        if (listFiles == null || (length = listFiles.length) == 0) {
            return null;
        }
        final char[][][] result = new char[length][][];
        final char[][] packageName = CharOperation.splitOn(File.separatorChar, qualifiedPackageName.toCharArray());
        for (int i = 0; i < length; ++i) {
            final String fileName = listFiles[i];
            final int indexOfLastDot = fileName.indexOf(46);
            result[i] = CharOperation.arrayConcat(packageName, fileName.substring(0, indexOfLastDot).toCharArray());
        }
        return result;
    }
    
    @Override
    public void initialize() throws IOException {
    }
    
    @Override
    public boolean isPackage(final String qualifiedPackageName) {
        return this.directoryList(qualifiedPackageName) != null;
    }
    
    @Override
    public void reset() {
        this.directoryCache = new Hashtable(11);
    }
    
    @Override
    public String toString() {
        return "ClasspathDirectory " + this.path;
    }
    
    @Override
    public char[] normalizedPath() {
        if (this.normalizedPath == null) {
            this.normalizedPath = this.path.toCharArray();
            if (File.separatorChar == '\\') {
                CharOperation.replace(this.normalizedPath, '\\', '/');
            }
        }
        return this.normalizedPath;
    }
    
    @Override
    public String getPath() {
        return this.path;
    }
    
    @Override
    public int getMode() {
        return this.mode;
    }
}
