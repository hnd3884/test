package org.eclipse.jdt.internal.compiler.apt.dispatch;

import java.util.ServiceConfigurationError;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import org.eclipse.jdt.internal.compiler.batch.Main;
import java.util.ServiceLoader;
import java.util.Iterator;
import javax.annotation.processing.Processor;
import java.util.List;

public class BatchAnnotationProcessorManager extends BaseAnnotationProcessorManager
{
    private List<Processor> _setProcessors;
    private Iterator<Processor> _setProcessorIter;
    private List<String> _commandLineProcessors;
    private Iterator<String> _commandLineProcessorIter;
    private ServiceLoader<Processor> _serviceLoader;
    private Iterator<Processor> _serviceLoaderIter;
    private ClassLoader _procLoader;
    private static final boolean VERBOSE_PROCESSOR_DISCOVERY = true;
    private boolean _printProcessorDiscovery;
    
    public BatchAnnotationProcessorManager() {
        this._setProcessors = null;
        this._setProcessorIter = null;
        this._commandLineProcessorIter = null;
        this._serviceLoader = null;
        this._printProcessorDiscovery = false;
    }
    
    @Override
    public void configure(final Object batchCompiler, final String[] commandLineArguments) {
        if (this._processingEnv != null) {
            throw new IllegalStateException("Calling configure() more than once on an AnnotationProcessorManager is not supported");
        }
        final BatchProcessingEnvImpl processingEnv = new BatchProcessingEnvImpl(this, (Main)batchCompiler, commandLineArguments);
        this._processingEnv = processingEnv;
        this._procLoader = processingEnv.getFileManager().getClassLoader(StandardLocation.ANNOTATION_PROCESSOR_PATH);
        this.parseCommandLine(commandLineArguments);
        this._round = 0;
    }
    
    private void parseCommandLine(final String[] commandLineArguments) {
        List<String> commandLineProcessors = null;
        for (int i = 0; i < commandLineArguments.length; ++i) {
            final String option = commandLineArguments[i];
            if ("-XprintProcessorInfo".equals(option)) {
                this._printProcessorInfo = true;
                this._printProcessorDiscovery = true;
            }
            else if ("-XprintRounds".equals(option)) {
                this._printRounds = true;
            }
            else if ("-processor".equals(option)) {
                commandLineProcessors = new ArrayList<String>();
                final String procs = commandLineArguments[++i];
                String[] split;
                for (int length = (split = procs.split(",")).length, j = 0; j < length; ++j) {
                    final String proc = split[j];
                    commandLineProcessors.add(proc);
                }
                break;
            }
        }
        this._commandLineProcessors = commandLineProcessors;
        if (this._commandLineProcessors != null) {
            this._commandLineProcessorIter = this._commandLineProcessors.iterator();
        }
    }
    
    @Override
    public ProcessorInfo discoverNextProcessor() {
        if (this._setProcessors != null) {
            if (this._setProcessorIter.hasNext()) {
                final Processor p = this._setProcessorIter.next();
                p.init(this._processingEnv);
                final ProcessorInfo pi = new ProcessorInfo(p);
                this._processors.add(pi);
                if (this._printProcessorDiscovery && this._out != null) {
                    this._out.println("API specified processor: " + pi);
                }
                return pi;
            }
            return null;
        }
        else {
            if (this._commandLineProcessors != null) {
                if (this._commandLineProcessorIter.hasNext()) {
                    final String proc = this._commandLineProcessorIter.next();
                    try {
                        final Class<?> clazz = this._procLoader.loadClass(proc);
                        final Object o = clazz.newInstance();
                        final Processor p2 = (Processor)o;
                        p2.init(this._processingEnv);
                        final ProcessorInfo pi2 = new ProcessorInfo(p2);
                        this._processors.add(pi2);
                        if (this._printProcessorDiscovery && this._out != null) {
                            this._out.println("Command line specified processor: " + pi2);
                        }
                        return pi2;
                    }
                    catch (final Exception e) {
                        throw new AbortCompilation((CompilationResult)null, e);
                    }
                }
                return null;
            }
            if (this._serviceLoader == null) {
                this._serviceLoader = ServiceLoader.load(Processor.class, this._procLoader);
                this._serviceLoaderIter = this._serviceLoader.iterator();
            }
            try {
                if (this._serviceLoaderIter.hasNext()) {
                    final Processor p = this._serviceLoaderIter.next();
                    p.init(this._processingEnv);
                    final ProcessorInfo pi = new ProcessorInfo(p);
                    this._processors.add(pi);
                    if (this._printProcessorDiscovery && this._out != null) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Discovered processor service ");
                        sb.append(pi);
                        sb.append("\n  supporting ");
                        sb.append(pi.getSupportedAnnotationTypesAsString());
                        sb.append("\n  in ");
                        sb.append(this.getProcessorLocation(p));
                        this._out.println(sb.toString());
                    }
                    return pi;
                }
            }
            catch (final ServiceConfigurationError e2) {
                throw new AbortCompilation((CompilationResult)null, e2);
            }
            return null;
        }
    }
    
    private String getProcessorLocation(final Processor p) {
        boolean isMember = false;
        Class<?> outerClass = p.getClass();
        final StringBuilder innerName = new StringBuilder();
        while (outerClass.isMemberClass()) {
            innerName.insert(0, outerClass.getSimpleName());
            innerName.insert(0, '$');
            isMember = true;
            outerClass = outerClass.getEnclosingClass();
        }
        String path = outerClass.getName();
        path = path.replace('.', '/');
        if (isMember) {
            path = String.valueOf(path) + (Object)innerName;
        }
        path = String.valueOf(path) + ".class";
        String location = this._procLoader.getResource(path).toString();
        if (location.endsWith(path)) {
            location = location.substring(0, location.length() - path.length());
        }
        return location;
    }
    
    @Override
    public void reportProcessorException(final Processor p, final Exception e) {
        throw new AbortCompilation((CompilationResult)null, e);
    }
    
    @Override
    public void setProcessors(final Object[] processors) {
        if (!this._isFirstRound) {
            throw new IllegalStateException("setProcessors() cannot be called after processing has begun");
        }
        this._setProcessors = new ArrayList<Processor>(processors.length);
        for (final Object o : processors) {
            final Processor p = (Processor)o;
            this._setProcessors.add(p);
        }
        this._setProcessorIter = this._setProcessors.iterator();
        this._commandLineProcessors = null;
        this._commandLineProcessorIter = null;
    }
}
