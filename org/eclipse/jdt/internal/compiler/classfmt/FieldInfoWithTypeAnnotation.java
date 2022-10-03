package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;

public final class FieldInfoWithTypeAnnotation extends FieldInfoWithAnnotation
{
    private TypeAnnotationInfo[] typeAnnotations;
    
    FieldInfoWithTypeAnnotation(final FieldInfo info, final AnnotationInfo[] annos, final TypeAnnotationInfo[] typeAnnos) {
        super(info, annos);
        this.typeAnnotations = typeAnnos;
    }
    
    @Override
    public IBinaryTypeAnnotation[] getTypeAnnotations() {
        return this.typeAnnotations;
    }
    
    @Override
    protected void initialize() {
        for (int i = 0, max = this.typeAnnotations.length; i < max; ++i) {
            this.typeAnnotations[i].initialize();
        }
        super.initialize();
    }
    
    @Override
    protected void reset() {
        if (this.typeAnnotations != null) {
            for (int i = 0, max = this.typeAnnotations.length; i < max; ++i) {
                this.typeAnnotations[i].reset();
            }
        }
        super.reset();
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer(this.getClass().getName());
        if (this.typeAnnotations != null) {
            buffer.append('\n');
            buffer.append("type annotations:");
            for (int i = 0; i < this.typeAnnotations.length; ++i) {
                buffer.append(this.typeAnnotations[i]);
                buffer.append('\n');
            }
        }
        this.toStringContent(buffer);
        return buffer.toString();
    }
}
