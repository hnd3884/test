package javax.lang.model.element;

import java.util.List;
import javax.lang.model.type.TypeMirror;

public interface AnnotationValueVisitor<R, P>
{
    R visit(final AnnotationValue p0, final P p1);
    
    R visit(final AnnotationValue p0);
    
    R visitBoolean(final boolean p0, final P p1);
    
    R visitByte(final byte p0, final P p1);
    
    R visitChar(final char p0, final P p1);
    
    R visitDouble(final double p0, final P p1);
    
    R visitFloat(final float p0, final P p1);
    
    R visitInt(final int p0, final P p1);
    
    R visitLong(final long p0, final P p1);
    
    R visitShort(final short p0, final P p1);
    
    R visitString(final String p0, final P p1);
    
    R visitType(final TypeMirror p0, final P p1);
    
    R visitEnumConstant(final VariableElement p0, final P p1);
    
    R visitAnnotation(final AnnotationMirror p0, final P p1);
    
    R visitArray(final List<? extends AnnotationValue> p0, final P p1);
    
    R visitUnknown(final AnnotationValue p0, final P p1);
}
