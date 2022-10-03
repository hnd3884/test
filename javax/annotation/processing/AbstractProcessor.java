package javax.annotation.processing;

import java.util.HashSet;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Objects;
import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.Set;

public abstract class AbstractProcessor implements Processor
{
    protected ProcessingEnvironment processingEnv;
    private boolean initialized;
    
    protected AbstractProcessor() {
        this.initialized = false;
    }
    
    @Override
    public Set<String> getSupportedOptions() {
        final SupportedOptions supportedOptions = this.getClass().getAnnotation(SupportedOptions.class);
        if (supportedOptions == null) {
            return Collections.emptySet();
        }
        return arrayToSet(supportedOptions.value());
    }
    
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final SupportedAnnotationTypes supportedAnnotationTypes = this.getClass().getAnnotation(SupportedAnnotationTypes.class);
        if (supportedAnnotationTypes == null) {
            if (this.isInitialized()) {
                this.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "No SupportedAnnotationTypes annotation found on " + this.getClass().getName() + ", returning an empty set.");
            }
            return Collections.emptySet();
        }
        return arrayToSet(supportedAnnotationTypes.value());
    }
    
    @Override
    public SourceVersion getSupportedSourceVersion() {
        final SupportedSourceVersion supportedSourceVersion = this.getClass().getAnnotation(SupportedSourceVersion.class);
        SourceVersion sourceVersion;
        if (supportedSourceVersion == null) {
            sourceVersion = SourceVersion.RELEASE_6;
            if (this.isInitialized()) {
                this.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "No SupportedSourceVersion annotation found on " + this.getClass().getName() + ", returning " + sourceVersion + ".");
            }
        }
        else {
            sourceVersion = supportedSourceVersion.value();
        }
        return sourceVersion;
    }
    
    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        if (this.initialized) {
            throw new IllegalStateException("Cannot call init more than once.");
        }
        Objects.requireNonNull(processingEnv, "Tool provided null ProcessingEnvironment");
        this.processingEnv = processingEnv;
        this.initialized = true;
    }
    
    @Override
    public abstract boolean process(final Set<? extends TypeElement> p0, final RoundEnvironment p1);
    
    @Override
    public Iterable<? extends Completion> getCompletions(final Element element, final AnnotationMirror annotationMirror, final ExecutableElement executableElement, final String s) {
        return (Iterable<? extends Completion>)Collections.emptyList();
    }
    
    protected synchronized boolean isInitialized() {
        return this.initialized;
    }
    
    private static Set<String> arrayToSet(final String[] array) {
        assert array != null;
        final HashSet set = new HashSet(array.length);
        for (int length = array.length, i = 0; i < length; ++i) {
            set.add(array[i]);
        }
        return (Set<String>)Collections.unmodifiableSet((Set<?>)set);
    }
}
