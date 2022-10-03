package javax.lang.model.util;

import javax.lang.model.element.AnnotationValue;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SimpleAnnotationValueVisitor6<R, P> extends AbstractAnnotationValueVisitor6<R, P>
{
    protected final R DEFAULT_VALUE;
    
    protected SimpleAnnotationValueVisitor6() {
        this.DEFAULT_VALUE = null;
    }
    
    protected SimpleAnnotationValueVisitor6(final R default_VALUE) {
        this.DEFAULT_VALUE = default_VALUE;
    }
    
    protected R defaultAction(final Object o, final P p2) {
        return this.DEFAULT_VALUE;
    }
    
    @Override
    public R visitBoolean(final boolean b, final P p2) {
        return this.defaultAction(b, p2);
    }
    
    @Override
    public R visitByte(final byte b, final P p2) {
        return this.defaultAction(b, p2);
    }
    
    @Override
    public R visitChar(final char c, final P p2) {
        return this.defaultAction(c, p2);
    }
    
    @Override
    public R visitDouble(final double n, final P p2) {
        return this.defaultAction(n, p2);
    }
    
    @Override
    public R visitFloat(final float n, final P p2) {
        return this.defaultAction(n, p2);
    }
    
    @Override
    public R visitInt(final int n, final P p2) {
        return this.defaultAction(n, p2);
    }
    
    @Override
    public R visitLong(final long n, final P p2) {
        return this.defaultAction(n, p2);
    }
    
    @Override
    public R visitShort(final short n, final P p2) {
        return this.defaultAction(n, p2);
    }
    
    @Override
    public R visitString(final String s, final P p2) {
        return this.defaultAction(s, p2);
    }
    
    @Override
    public R visitType(final TypeMirror typeMirror, final P p2) {
        return this.defaultAction(typeMirror, p2);
    }
    
    @Override
    public R visitEnumConstant(final VariableElement variableElement, final P p2) {
        return this.defaultAction(variableElement, p2);
    }
    
    @Override
    public R visitAnnotation(final AnnotationMirror annotationMirror, final P p2) {
        return this.defaultAction(annotationMirror, p2);
    }
    
    @Override
    public R visitArray(final List<? extends AnnotationValue> list, final P p2) {
        return this.defaultAction(list, p2);
    }
}
