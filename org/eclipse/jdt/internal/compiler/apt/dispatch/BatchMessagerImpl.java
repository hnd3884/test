package org.eclipse.jdt.internal.compiler.apt.dispatch;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import org.eclipse.jdt.internal.compiler.batch.Main;
import javax.annotation.processing.Messager;

public class BatchMessagerImpl extends BaseMessagerImpl implements Messager
{
    private final Main _compiler;
    private final BaseProcessingEnvImpl _processingEnv;
    
    public BatchMessagerImpl(final BaseProcessingEnvImpl processingEnv, final Main compiler) {
        this._compiler = compiler;
        this._processingEnv = processingEnv;
    }
    
    @Override
    public void printMessage(final Diagnostic.Kind kind, final CharSequence msg) {
        this.printMessage(kind, msg, null, null, null);
    }
    
    @Override
    public void printMessage(final Diagnostic.Kind kind, final CharSequence msg, final Element e) {
        this.printMessage(kind, msg, e, null, null);
    }
    
    @Override
    public void printMessage(final Diagnostic.Kind kind, final CharSequence msg, final Element e, final AnnotationMirror a) {
        this.printMessage(kind, msg, e, a, null);
    }
    
    @Override
    public void printMessage(final Diagnostic.Kind kind, final CharSequence msg, final Element e, final AnnotationMirror a, final AnnotationValue v) {
        if (kind == Diagnostic.Kind.ERROR) {
            this._processingEnv.setErrorRaised(true);
        }
        final CategorizedProblem problem = BaseMessagerImpl.createProblem(kind, msg, e, a, v);
        if (problem != null) {
            this._compiler.addExtraProblems(problem);
        }
    }
}
