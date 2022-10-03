package org.eclipse.jdt.internal.compiler.apt.dispatch;

import java.util.Locale;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Collections;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.apt.util.EclipseFileManager;
import java.nio.charset.Charset;
import org.eclipse.jdt.internal.compiler.batch.Main;
import javax.tools.JavaFileManager;

public class BatchProcessingEnvImpl extends BaseProcessingEnvImpl
{
    protected final BaseAnnotationProcessorManager _dispatchManager;
    protected final JavaFileManager _fileManager;
    protected final Main _compilerOwner;
    
    public BatchProcessingEnvImpl(final BaseAnnotationProcessorManager dispatchManager, final Main batchCompiler, final String[] commandLineArguments) {
        this._compilerOwner = batchCompiler;
        this._compiler = batchCompiler.batchCompiler;
        this._dispatchManager = dispatchManager;
        Class<?> c = null;
        try {
            c = Class.forName("org.eclipse.jdt.internal.compiler.tool.EclipseCompilerImpl");
        }
        catch (final ClassNotFoundException ex) {}
        Field field = null;
        JavaFileManager javaFileManager = null;
        if (c != null) {
            try {
                field = c.getField("fileManager");
            }
            catch (final SecurityException ex2) {}
            catch (final IllegalArgumentException ex3) {}
            catch (final NoSuchFieldException ex4) {}
        }
        if (field != null) {
            try {
                javaFileManager = (JavaFileManager)field.get(batchCompiler);
            }
            catch (final IllegalArgumentException ex5) {}
            catch (final IllegalAccessException ex6) {}
        }
        if (javaFileManager != null) {
            this._fileManager = javaFileManager;
        }
        else {
            final String encoding = batchCompiler.options.get("org.eclipse.jdt.core.encoding");
            final Charset charset = (encoding != null) ? Charset.forName(encoding) : null;
            final JavaFileManager manager = new EclipseFileManager(batchCompiler.compilerLocale, charset);
            final ArrayList<String> options = new ArrayList<String>();
            for (final String argument : commandLineArguments) {
                options.add(argument);
            }
            final Iterator<String> iterator = options.iterator();
            while (iterator.hasNext()) {
                manager.handleOption(iterator.next(), iterator);
            }
            this._fileManager = manager;
        }
        this._processorOptions = Collections.unmodifiableMap((Map<? extends String, ? extends String>)this.parseProcessorOptions(commandLineArguments));
        this._filer = new BatchFilerImpl(this._dispatchManager, this);
        this._messager = new BatchMessagerImpl(this, this._compilerOwner);
    }
    
    private Map<String, String> parseProcessorOptions(final String[] args) {
        final Map<String, String> options = new LinkedHashMap<String, String>();
        for (final String arg : args) {
            if (arg.startsWith("-A")) {
                final int equals = arg.indexOf(61);
                if (equals == 2) {
                    final Exception e = new IllegalArgumentException("-A option must have a key before the equals sign");
                    throw new AbortCompilation((CompilationResult)null, e);
                }
                if (equals == arg.length() - 1) {
                    options.put(arg.substring(2, equals), null);
                }
                else if (equals == -1) {
                    options.put(arg.substring(2), null);
                }
                else {
                    options.put(arg.substring(2, equals), arg.substring(equals + 1));
                }
            }
        }
        return options;
    }
    
    public JavaFileManager getFileManager() {
        return this._fileManager;
    }
    
    @Override
    public Locale getLocale() {
        return this._compilerOwner.compilerLocale;
    }
}
