package org.eclipse.jdt.internal.compiler.tool;

import org.eclipse.jdt.internal.compiler.batch.Main;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.util.Iterator;
import javax.annotation.processing.Processor;
import javax.tools.StandardLocation;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import org.eclipse.jdt.core.compiler.CompilationProgress;
import java.util.Map;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.tools.JavaFileManager;
import java.io.Writer;
import javax.tools.StandardJavaFileManager;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Collections;
import java.util.EnumSet;
import javax.tools.JavaFileObject;
import javax.tools.DiagnosticListener;
import java.util.WeakHashMap;
import javax.lang.model.SourceVersion;
import java.util.Set;
import javax.tools.JavaCompiler;

public class EclipseCompiler implements JavaCompiler
{
    private static Set<SourceVersion> SupportedSourceVersions;
    WeakHashMap<Thread, EclipseCompilerImpl> threadCache;
    public DiagnosticListener<? super JavaFileObject> diagnosticListener;
    
    static {
        final EnumSet<SourceVersion> enumSet = EnumSet.range(SourceVersion.RELEASE_0, SourceVersion.RELEASE_6);
        EclipseCompiler.SupportedSourceVersions = Collections.unmodifiableSet((Set<? extends SourceVersion>)enumSet);
    }
    
    public EclipseCompiler() {
        this.threadCache = new WeakHashMap<Thread, EclipseCompilerImpl>();
    }
    
    @Override
    public Set<SourceVersion> getSourceVersions() {
        return EclipseCompiler.SupportedSourceVersions;
    }
    
    @Override
    public StandardJavaFileManager getStandardFileManager(final DiagnosticListener<? super JavaFileObject> someDiagnosticListener, final Locale locale, final Charset charset) {
        this.diagnosticListener = someDiagnosticListener;
        return new EclipseFileManager(locale, charset);
    }
    
    @Override
    public CompilationTask getTask(final Writer out, final JavaFileManager fileManager, final DiagnosticListener<? super JavaFileObject> someDiagnosticListener, final Iterable<String> options, final Iterable<String> classes, final Iterable<? extends JavaFileObject> compilationUnits) {
        PrintWriter writerOut = null;
        PrintWriter writerErr = null;
        if (out == null) {
            writerOut = new PrintWriter(System.err);
            writerErr = new PrintWriter(System.err);
        }
        else {
            writerOut = new PrintWriter(out);
            writerErr = new PrintWriter(out);
        }
        final Thread currentThread = Thread.currentThread();
        EclipseCompilerImpl eclipseCompiler = this.threadCache.get(currentThread);
        if (eclipseCompiler == null) {
            eclipseCompiler = new EclipseCompilerImpl(writerOut, writerErr, false);
            this.threadCache.put(currentThread, eclipseCompiler);
        }
        else {
            eclipseCompiler.initialize(writerOut, writerErr, false, null, null);
        }
        final EclipseCompilerImpl eclipseCompiler2 = new EclipseCompilerImpl(writerOut, writerErr, false);
        eclipseCompiler2.compilationUnits = compilationUnits;
        eclipseCompiler2.diagnosticListener = someDiagnosticListener;
        if (fileManager != null) {
            eclipseCompiler2.fileManager = fileManager;
        }
        else {
            eclipseCompiler2.fileManager = this.getStandardFileManager(someDiagnosticListener, null, null);
        }
        eclipseCompiler2.options.put("org.eclipse.jdt.core.compiler.compliance", "1.6");
        eclipseCompiler2.options.put("org.eclipse.jdt.core.compiler.source", "1.6");
        eclipseCompiler2.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.6");
        final ArrayList<String> allOptions = new ArrayList<String>();
        if (options != null) {
            final Iterator<String> iterator = options.iterator();
            while (iterator.hasNext()) {
                eclipseCompiler2.fileManager.handleOption(iterator.next(), iterator);
            }
            for (final String option : options) {
                allOptions.add(option);
            }
        }
        if (compilationUnits != null) {
            for (final JavaFileObject javaFileObject : compilationUnits) {
                URI uri = javaFileObject.toUri();
                if (!uri.isAbsolute()) {
                    uri = URI.create("file://" + uri.toString());
                }
                if (uri.getScheme().equals("file")) {
                    allOptions.add(new File(uri).getAbsolutePath());
                }
                else {
                    allOptions.add(uri.toString());
                }
            }
        }
        if (classes != null) {
            allOptions.add("-classNames");
            final StringBuilder builder = new StringBuilder();
            int i = 0;
            for (final String className : classes) {
                if (i != 0) {
                    builder.append(',');
                }
                builder.append(className);
                ++i;
            }
            allOptions.add(String.valueOf(builder));
        }
        final String[] optionsToProcess = new String[allOptions.size()];
        allOptions.toArray(optionsToProcess);
        try {
            eclipseCompiler2.configure(optionsToProcess);
        }
        catch (final IllegalArgumentException e) {
            throw e;
        }
        if (eclipseCompiler2.fileManager instanceof StandardJavaFileManager) {
            final StandardJavaFileManager javaFileManager = (StandardJavaFileManager)eclipseCompiler2.fileManager;
            final Iterable<? extends File> location = javaFileManager.getLocation(StandardLocation.CLASS_OUTPUT);
            if (location != null) {
                eclipseCompiler2.setDestinationPath(((File)location.iterator().next()).getAbsolutePath());
            }
        }
        return new CompilationTask() {
            private boolean hasRun = false;
            
            @Override
            public Boolean call() {
                if (this.hasRun) {
                    throw new IllegalStateException("This task has already been run");
                }
                final Boolean value = eclipseCompiler2.call() ? Boolean.TRUE : Boolean.FALSE;
                this.hasRun = true;
                return value;
            }
            
            @Override
            public void setLocale(final Locale locale) {
                eclipseCompiler2.setLocale(locale);
            }
            
            @Override
            public void setProcessors(final Iterable<? extends Processor> processors) {
                final ArrayList<Processor> temp = new ArrayList<Processor>();
                for (final Processor processor : processors) {
                    temp.add(processor);
                }
                final Processor[] processors2 = new Processor[temp.size()];
                temp.toArray(processors2);
                eclipseCompiler2.processors = processors2;
            }
        };
    }
    
    @Override
    public int isSupportedOption(final String option) {
        return Options.processOptions(option);
    }
    
    @Override
    public int run(final InputStream in, final OutputStream out, final OutputStream err, final String... arguments) {
        final boolean succeed = new Main(new PrintWriter(new OutputStreamWriter((out != null) ? out : System.out)), new PrintWriter(new OutputStreamWriter((err != null) ? err : System.err)), true, null, null).compile(arguments);
        return succeed ? 0 : -1;
    }
}
