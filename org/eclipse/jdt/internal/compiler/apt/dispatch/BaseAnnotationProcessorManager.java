package org.eclipse.jdt.internal.compiler.apt.dispatch;

import javax.annotation.processing.RoundEnvironment;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.Compiler;
import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;
import org.eclipse.jdt.internal.compiler.AbstractAnnotationProcessorManager;

public abstract class BaseAnnotationProcessorManager extends AbstractAnnotationProcessorManager implements IProcessorProvider
{
    protected PrintWriter _out;
    protected PrintWriter _err;
    protected BaseProcessingEnvImpl _processingEnv;
    protected boolean _isFirstRound;
    protected List<ProcessorInfo> _processors;
    protected boolean _printProcessorInfo;
    protected boolean _printRounds;
    protected int _round;
    
    public BaseAnnotationProcessorManager() {
        this._isFirstRound = true;
        this._processors = new ArrayList<ProcessorInfo>();
        this._printProcessorInfo = false;
        this._printRounds = false;
    }
    
    @Override
    public void configure(final Object batchCompiler, final String[] options) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void configureFromPlatform(final Compiler compiler, final Object compilationUnitLocator, final Object javaProject) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List<ProcessorInfo> getDiscoveredProcessors() {
        return this._processors;
    }
    
    @Override
    public ICompilationUnit[] getDeletedUnits() {
        return this._processingEnv.getDeletedUnits();
    }
    
    @Override
    public ICompilationUnit[] getNewUnits() {
        return this._processingEnv.getNewUnits();
    }
    
    @Override
    public ReferenceBinding[] getNewClassFiles() {
        return this._processingEnv.getNewClassFiles();
    }
    
    @Override
    public void reset() {
        this._processingEnv.reset();
    }
    
    @Override
    public void setErr(final PrintWriter err) {
        this._err = err;
    }
    
    @Override
    public void setOut(final PrintWriter out) {
        this._out = out;
    }
    
    @Override
    public void setProcessors(final Object[] processors) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void processAnnotations(final CompilationUnitDeclaration[] units, final ReferenceBinding[] referenceBindings, final boolean isLastRound) {
        final RoundEnvImpl roundEnv = new RoundEnvImpl(units, referenceBindings, isLastRound, this._processingEnv);
        if (this._isFirstRound) {
            this._isFirstRound = false;
        }
        final PrintWriter traceProcessorInfo = this._printProcessorInfo ? this._out : null;
        final PrintWriter traceRounds = this._printRounds ? this._out : null;
        if (traceRounds != null) {
            traceRounds.println("Round " + ++this._round + ':');
        }
        final RoundDispatcher dispatcher = new RoundDispatcher(this, roundEnv, roundEnv.getRootAnnotations(), traceProcessorInfo, traceRounds);
        dispatcher.round();
    }
}
