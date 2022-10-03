package org.eclipse.jdt.internal.compiler.tool;

import org.eclipse.jdt.internal.compiler.problem.DefaultProblem;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJsr199;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import java.util.Collection;
import javax.tools.StandardJavaFileManager;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import java.io.OutputStream;
import org.eclipse.jdt.internal.compiler.ClassFile;
import java.io.BufferedOutputStream;
import javax.tools.FileObject;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
import javax.tools.StandardLocation;
import java.nio.charset.Charset;
import java.io.File;
import java.util.Locale;
import javax.tools.Diagnostic;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import java.util.Iterator;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import java.util.ArrayList;
import java.io.IOException;
import org.eclipse.jdt.core.compiler.CompilationProgress;
import java.util.Map;
import java.io.PrintWriter;
import javax.tools.DiagnosticListener;
import javax.annotation.processing.Processor;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.util.HashMap;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.batch.Main;

public class EclipseCompilerImpl extends Main
{
    private static final CompilationUnit[] NO_UNITS;
    private HashMap<CompilationUnit, JavaFileObject> javaFileObjectMap;
    Iterable<? extends JavaFileObject> compilationUnits;
    public JavaFileManager fileManager;
    protected Processor[] processors;
    public DiagnosticListener<? super JavaFileObject> diagnosticListener;
    
    static {
        NO_UNITS = new CompilationUnit[0];
    }
    
    public EclipseCompilerImpl(final PrintWriter out, final PrintWriter err, final boolean systemExitWhenFinished) {
        super(out, err, systemExitWhenFinished, null, null);
    }
    
    public boolean call() {
        try {
            if (this.proceed) {
                this.globalProblemsCount = 0;
                this.globalErrorsCount = 0;
                this.globalWarningsCount = 0;
                this.globalTasksCount = 0;
                this.exportedClassFilesCounter = 0;
                this.performCompilation();
            }
        }
        catch (final IllegalArgumentException e) {
            this.logger.logException(e);
            if (this.systemExitWhenFinished) {
                this.cleanup();
                System.exit(-1);
            }
            return false;
        }
        catch (final RuntimeException e2) {
            this.logger.logException(e2);
            return false;
        }
        finally {
            this.cleanup();
        }
        this.cleanup();
        return this.globalErrorsCount == 0;
    }
    
    private void cleanup() {
        this.logger.flush();
        this.logger.close();
        this.processors = null;
        try {
            if (this.fileManager != null) {
                this.fileManager.flush();
            }
        }
        catch (final IOException ex) {}
    }
    
    @Override
    public CompilationUnit[] getCompilationUnits() {
        if (this.compilationUnits == null) {
            return EclipseCompilerImpl.NO_UNITS;
        }
        final ArrayList<CompilationUnit> units = new ArrayList<CompilationUnit>();
        for (final JavaFileObject javaFileObject : this.compilationUnits) {
            if (javaFileObject.getKind() != JavaFileObject.Kind.SOURCE) {
                throw new IllegalArgumentException();
            }
            String name = javaFileObject.getName();
            name = name.replace('\\', '/');
            final CompilationUnit compilationUnit = new CompilationUnit(null, name, null) {
                @Override
                public char[] getContents() {
                    try {
                        return javaFileObject.getCharContent(true).toString().toCharArray();
                    }
                    catch (final IOException e) {
                        e.printStackTrace();
                        throw new AbortCompilationUnit(null, e, null);
                    }
                }
            };
            units.add(compilationUnit);
            this.javaFileObjectMap.put(compilationUnit, javaFileObject);
        }
        final CompilationUnit[] result = new CompilationUnit[units.size()];
        units.toArray(result);
        return result;
    }
    
    @Override
    public IErrorHandlingPolicy getHandlingPolicy() {
        return new IErrorHandlingPolicy() {
            @Override
            public boolean proceedOnErrors() {
                return false;
            }
            
            @Override
            public boolean stopOnFirstError() {
                return false;
            }
            
            @Override
            public boolean ignoreAllErrors() {
                return false;
            }
        };
    }
    
    @Override
    public IProblemFactory getProblemFactory() {
        return new DefaultProblemFactory() {
            @Override
            public CategorizedProblem createProblem(final char[] originatingFileName, final int problemId, final String[] problemArguments, final String[] messageArguments, final int severity, final int startPosition, final int endPosition, final int lineNumber, final int columnNumber) {
                final DiagnosticListener<? super JavaFileObject> diagListener = EclipseCompilerImpl.this.diagnosticListener;
                if (diagListener != null) {
                    diagListener.report(new Diagnostic<JavaFileObject>() {
                        @Override
                        public String getCode() {
                            return Integer.toString(problemId);
                        }
                        
                        @Override
                        public long getColumnNumber() {
                            return columnNumber;
                        }
                        
                        @Override
                        public long getEndPosition() {
                            return endPosition;
                        }
                        
                        @Override
                        public Kind getKind() {
                            if ((severity & 0x1) != 0x0) {
                                return Kind.ERROR;
                            }
                            if ((severity & 0x20) != 0x0) {
                                return Kind.WARNING;
                            }
                            if (false) {
                                return Kind.MANDATORY_WARNING;
                            }
                            return Kind.OTHER;
                        }
                        
                        @Override
                        public long getLineNumber() {
                            return lineNumber;
                        }
                        
                        @Override
                        public String getMessage(final Locale locale) {
                            if (locale != null) {
                                DefaultProblemFactory.this.setLocale(locale);
                            }
                            return DefaultProblemFactory.this.getLocalizedMessage(problemId, problemArguments);
                        }
                        
                        @Override
                        public long getPosition() {
                            return startPosition;
                        }
                        
                        @Override
                        public JavaFileObject getSource() {
                            final File f = new File(new String(originatingFileName));
                            if (f.exists()) {
                                return new EclipseFileObject(null, f.toURI(), JavaFileObject.Kind.SOURCE, null);
                            }
                            return null;
                        }
                        
                        @Override
                        public long getStartPosition() {
                            return startPosition;
                        }
                    });
                }
                return super.createProblem(originatingFileName, problemId, problemArguments, messageArguments, severity, startPosition, endPosition, lineNumber, columnNumber);
            }
            
            @Override
            public CategorizedProblem createProblem(final char[] originatingFileName, final int problemId, final String[] problemArguments, final int elaborationID, final String[] messageArguments, final int severity, final int startPosition, final int endPosition, final int lineNumber, final int columnNumber) {
                final DiagnosticListener<? super JavaFileObject> diagListener = EclipseCompilerImpl.this.diagnosticListener;
                if (diagListener != null) {
                    diagListener.report(new Diagnostic<JavaFileObject>() {
                        @Override
                        public String getCode() {
                            return Integer.toString(problemId);
                        }
                        
                        @Override
                        public long getColumnNumber() {
                            return columnNumber;
                        }
                        
                        @Override
                        public long getEndPosition() {
                            return endPosition;
                        }
                        
                        @Override
                        public Kind getKind() {
                            if ((severity & 0x1) != 0x0) {
                                return Kind.ERROR;
                            }
                            if ((severity & 0x400) != 0x0) {
                                return Kind.NOTE;
                            }
                            if ((severity & 0x20) != 0x0) {
                                return Kind.WARNING;
                            }
                            if (false) {
                                return Kind.MANDATORY_WARNING;
                            }
                            return Kind.OTHER;
                        }
                        
                        @Override
                        public long getLineNumber() {
                            return lineNumber;
                        }
                        
                        @Override
                        public String getMessage(final Locale locale) {
                            if (locale != null) {
                                DefaultProblemFactory.this.setLocale(locale);
                            }
                            return DefaultProblemFactory.this.getLocalizedMessage(problemId, problemArguments);
                        }
                        
                        @Override
                        public long getPosition() {
                            return startPosition;
                        }
                        
                        @Override
                        public JavaFileObject getSource() {
                            final File f = new File(new String(originatingFileName));
                            if (f.exists()) {
                                return new EclipseFileObject(null, f.toURI(), JavaFileObject.Kind.SOURCE, null);
                            }
                            return null;
                        }
                        
                        @Override
                        public long getStartPosition() {
                            return startPosition;
                        }
                    });
                }
                return super.createProblem(originatingFileName, problemId, problemArguments, elaborationID, messageArguments, severity, startPosition, endPosition, lineNumber, columnNumber);
            }
        };
    }
    
    @Override
    protected void initialize(final PrintWriter outWriter, final PrintWriter errWriter, final boolean systemExit, final Map<String, String> customDefaultOptions, final CompilationProgress compilationProgress) {
        super.initialize(outWriter, errWriter, systemExit, customDefaultOptions, compilationProgress);
        this.javaFileObjectMap = new HashMap<CompilationUnit, JavaFileObject>();
    }
    
    @Override
    protected void initializeAnnotationProcessorManager() {
        super.initializeAnnotationProcessorManager();
        if (this.batchCompiler.annotationProcessorManager != null && this.processors != null) {
            this.batchCompiler.annotationProcessorManager.setProcessors(this.processors);
        }
        else if (this.processors != null) {
            throw new UnsupportedOperationException("Cannot handle annotation processing");
        }
    }
    
    @Override
    public void outputClassFiles(final CompilationResult unitResult) {
        if (unitResult != null && (!unitResult.hasErrors() || this.proceedOnError)) {
            final ClassFile[] classFiles = unitResult.getClassFiles();
            final boolean generateClasspathStructure = this.fileManager.hasLocation(StandardLocation.CLASS_OUTPUT);
            final String currentDestinationPath = this.destinationPath;
            File outputLocation = null;
            if (currentDestinationPath != null) {
                outputLocation = new File(currentDestinationPath);
                outputLocation.mkdirs();
            }
            for (int i = 0, fileCount = classFiles.length; i < fileCount; ++i) {
                final ClassFile classFile = classFiles[i];
                final char[] filename = classFile.fileName();
                final int length = filename.length;
                final char[] relativeName = new char[length + 6];
                System.arraycopy(filename, 0, relativeName, 0, length);
                System.arraycopy(SuffixConstants.SUFFIX_class, 0, relativeName, length, 6);
                CharOperation.replace(relativeName, '/', File.separatorChar);
                final String relativeStringName = new String(relativeName);
                if (this.compilerOptions.verbose) {
                    this.out.println(Messages.bind(Messages.compilation_write, new String[] { String.valueOf(this.exportedClassFilesCounter + 1), relativeStringName }));
                }
                try {
                    final JavaFileObject javaFileForOutput = this.fileManager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, new String(filename), JavaFileObject.Kind.CLASS, this.javaFileObjectMap.get(unitResult.compilationUnit));
                    if (generateClasspathStructure) {
                        if (currentDestinationPath != null) {
                            final int index = CharOperation.lastIndexOf(File.separatorChar, relativeName);
                            if (index != -1) {
                                final File currentFolder = new File(currentDestinationPath, relativeStringName.substring(0, index));
                                currentFolder.mkdirs();
                            }
                        }
                        else {
                            final String path = javaFileForOutput.toUri().getPath();
                            final int index2 = path.lastIndexOf(47);
                            if (index2 != -1) {
                                final File file = new File(path.substring(0, index2));
                                file.mkdirs();
                            }
                        }
                    }
                    Throwable t = null;
                    try {
                        final OutputStream openOutputStream = javaFileForOutput.openOutputStream();
                        try {
                            final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(openOutputStream);
                            try {
                                bufferedOutputStream.write(classFile.header, 0, classFile.headerOffset);
                                bufferedOutputStream.write(classFile.contents, 0, classFile.contentsOffset);
                                bufferedOutputStream.flush();
                            }
                            finally {
                                if (bufferedOutputStream != null) {
                                    bufferedOutputStream.close();
                                }
                            }
                            if (openOutputStream != null) {
                                openOutputStream.close();
                            }
                        }
                        finally {
                            if (t == null) {
                                final Throwable t2;
                                t = t2;
                            }
                            else {
                                final Throwable t2;
                                if (t != t2) {
                                    t.addSuppressed(t2);
                                }
                            }
                            if (openOutputStream != null) {
                                openOutputStream.close();
                            }
                        }
                    }
                    finally {
                        if (t == null) {
                            final Throwable t3;
                            t = t3;
                        }
                        else {
                            final Throwable t3;
                            if (t != t3) {
                                t.addSuppressed(t3);
                            }
                        }
                    }
                }
                catch (final IOException e) {
                    this.logger.logNoClassFileCreated(currentDestinationPath, relativeStringName, e);
                }
                this.logger.logClassFile(generateClasspathStructure, currentDestinationPath, relativeStringName);
                ++this.exportedClassFilesCounter;
            }
            this.batchCompiler.lookupEnvironment.releaseClassFiles(classFiles);
        }
    }
    
    @Override
    protected void setPaths(final ArrayList bootclasspaths, final String sourcepathClasspathArg, final ArrayList sourcepathClasspaths, final ArrayList classpaths, final ArrayList extdirsClasspaths, final ArrayList endorsedDirClasspaths, final String customEncoding) {
        ArrayList<FileSystem.Classpath> fileSystemClasspaths = new ArrayList<FileSystem.Classpath>();
        EclipseFileManager eclipseJavaFileManager = null;
        StandardJavaFileManager standardJavaFileManager = null;
        JavaFileManager javaFileManager = null;
        boolean havePlatformPaths = false;
        boolean haveClassPaths = false;
        if (this.fileManager instanceof EclipseFileManager) {
            eclipseJavaFileManager = (EclipseFileManager)this.fileManager;
        }
        if (this.fileManager instanceof StandardJavaFileManager) {
            standardJavaFileManager = (StandardJavaFileManager)this.fileManager;
        }
        javaFileManager = this.fileManager;
        if (eclipseJavaFileManager != null && (eclipseJavaFileManager.flags & 0x4) == 0x0 && (eclipseJavaFileManager.flags & 0x2) != 0x0) {
            fileSystemClasspaths.addAll(this.handleEndorseddirs(null));
        }
        Iterable<? extends File> location = null;
        if (standardJavaFileManager != null) {
            location = standardJavaFileManager.getLocation(StandardLocation.PLATFORM_CLASS_PATH);
            if (location != null) {
                for (final File file : location) {
                    final FileSystem.Classpath classpath = FileSystem.getClasspath(file.getAbsolutePath(), null, null, this.options);
                    if (classpath != null) {
                        fileSystemClasspaths.add(classpath);
                        havePlatformPaths = true;
                    }
                }
            }
        }
        else if (javaFileManager != null) {
            final FileSystem.Classpath classpath2 = new ClasspathJsr199(this.fileManager, StandardLocation.PLATFORM_CLASS_PATH);
            fileSystemClasspaths.add(classpath2);
            havePlatformPaths = true;
        }
        if (eclipseJavaFileManager != null && (eclipseJavaFileManager.flags & 0x1) == 0x0 && (eclipseJavaFileManager.flags & 0x2) != 0x0) {
            fileSystemClasspaths.addAll(this.handleExtdirs(null));
        }
        if (standardJavaFileManager != null) {
            location = standardJavaFileManager.getLocation(StandardLocation.SOURCE_PATH);
            if (location != null) {
                for (final File file : location) {
                    final FileSystem.Classpath classpath = FileSystem.getClasspath(file.getAbsolutePath(), null, null, this.options);
                    if (classpath != null) {
                        fileSystemClasspaths.add(classpath);
                    }
                }
            }
            location = standardJavaFileManager.getLocation(StandardLocation.CLASS_PATH);
            if (location != null) {
                for (final File file : location) {
                    final FileSystem.Classpath classpath = FileSystem.getClasspath(file.getAbsolutePath(), null, null, this.options);
                    if (classpath != null) {
                        fileSystemClasspaths.add(classpath);
                        haveClassPaths = true;
                    }
                }
            }
        }
        else if (javaFileManager != null) {
            FileSystem.Classpath classpath2 = null;
            if (this.fileManager.hasLocation(StandardLocation.SOURCE_PATH)) {
                classpath2 = new ClasspathJsr199(this.fileManager, StandardLocation.SOURCE_PATH);
                fileSystemClasspaths.add(classpath2);
            }
            classpath2 = new ClasspathJsr199(this.fileManager, StandardLocation.CLASS_PATH);
            fileSystemClasspaths.add(classpath2);
            haveClassPaths = true;
        }
        if (this.checkedClasspaths == null) {
            if (!havePlatformPaths) {
                fileSystemClasspaths.addAll(this.handleBootclasspath(null, null));
            }
            if (!haveClassPaths) {
                fileSystemClasspaths.addAll(this.handleClasspath(null, null));
            }
        }
        fileSystemClasspaths = FileSystem.ClasspathNormalizer.normalize(fileSystemClasspaths);
        final int size = fileSystemClasspaths.size();
        if (size != 0) {
            this.checkedClasspaths = new FileSystem.Classpath[size];
            int i = 0;
            for (final FileSystem.Classpath classpath : fileSystemClasspaths) {
                this.checkedClasspaths[i++] = classpath;
            }
        }
    }
    
    @Override
    protected void loggingExtraProblems() {
        super.loggingExtraProblems();
        for (final CategorizedProblem problem : this.extraProblems) {
            if (this.diagnosticListener != null) {
                this.diagnosticListener.report(new Diagnostic<JavaFileObject>() {
                    @Override
                    public String getCode() {
                        return null;
                    }
                    
                    @Override
                    public long getColumnNumber() {
                        if (problem instanceof DefaultProblem) {
                            return ((DefaultProblem)problem).column;
                        }
                        return -1L;
                    }
                    
                    @Override
                    public long getEndPosition() {
                        if (problem instanceof DefaultProblem) {
                            return ((DefaultProblem)problem).getSourceEnd();
                        }
                        return -1L;
                    }
                    
                    @Override
                    public Kind getKind() {
                        if (problem.isError()) {
                            return Kind.ERROR;
                        }
                        if (problem.isWarning()) {
                            return Kind.WARNING;
                        }
                        if (problem instanceof DefaultProblem && ((DefaultProblem)problem).isInfo()) {
                            return Kind.NOTE;
                        }
                        return Kind.OTHER;
                    }
                    
                    @Override
                    public long getLineNumber() {
                        if (problem instanceof DefaultProblem) {
                            return ((DefaultProblem)problem).getSourceLineNumber();
                        }
                        return -1L;
                    }
                    
                    @Override
                    public String getMessage(final Locale locale) {
                        return problem.getMessage();
                    }
                    
                    @Override
                    public long getPosition() {
                        if (problem instanceof DefaultProblem) {
                            return ((DefaultProblem)problem).getSourceStart();
                        }
                        return -1L;
                    }
                    
                    @Override
                    public JavaFileObject getSource() {
                        if (!(problem instanceof DefaultProblem)) {
                            return null;
                        }
                        final File f = new File(new String(((DefaultProblem)problem).getOriginatingFileName()));
                        if (f.exists()) {
                            final Charset charset = (EclipseCompilerImpl.this.fileManager instanceof EclipseFileManager) ? ((EclipseFileManager)EclipseCompilerImpl.this.fileManager).charset : Charset.defaultCharset();
                            return new EclipseFileObject(null, f.toURI(), JavaFileObject.Kind.SOURCE, charset);
                        }
                        return null;
                    }
                    
                    @Override
                    public long getStartPosition() {
                        return this.getPosition();
                    }
                });
            }
        }
    }
}
