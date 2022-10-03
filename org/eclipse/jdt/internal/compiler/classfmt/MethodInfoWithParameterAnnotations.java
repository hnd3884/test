package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;

class MethodInfoWithParameterAnnotations extends MethodInfoWithAnnotations
{
    private AnnotationInfo[][] parameterAnnotations;
    
    MethodInfoWithParameterAnnotations(final MethodInfo methodInfo, final AnnotationInfo[] annotations, final AnnotationInfo[][] parameterAnnotations) {
        super(methodInfo, annotations);
        this.parameterAnnotations = parameterAnnotations;
    }
    
    @Override
    public IBinaryAnnotation[] getParameterAnnotations(final int index, final char[] classFileName) {
        try {
            return (IBinaryAnnotation[])((this.parameterAnnotations == null) ? null : this.parameterAnnotations[index]);
        }
        catch (final ArrayIndexOutOfBoundsException aioobe) {
            final StringBuffer message = new StringBuffer("Mismatching number of parameter annotations, ");
            message.append(index);
            message.append('>');
            message.append(this.parameterAnnotations.length - 1);
            message.append(" in ");
            message.append(this.getSelector());
            final char[] desc = this.getGenericSignature();
            if (desc != null) {
                message.append(desc);
            }
            else {
                message.append(this.getMethodDescriptor());
            }
            if (classFileName != null) {
                message.append(" in ").append(classFileName);
            }
            throw new IllegalStateException(message.toString(), aioobe);
        }
    }
    
    @Override
    public int getAnnotatedParametersCount() {
        return (this.parameterAnnotations == null) ? 0 : this.parameterAnnotations.length;
    }
    
    @Override
    protected void initialize() {
        for (int i = 0, l = (this.parameterAnnotations == null) ? 0 : this.parameterAnnotations.length; i < l; ++i) {
            final AnnotationInfo[] infos = this.parameterAnnotations[i];
            for (int j = 0, k = (infos == null) ? 0 : infos.length; j < k; ++j) {
                infos[j].initialize();
            }
        }
        super.initialize();
    }
    
    @Override
    protected void reset() {
        for (int i = 0, l = (this.parameterAnnotations == null) ? 0 : this.parameterAnnotations.length; i < l; ++i) {
            final AnnotationInfo[] infos = this.parameterAnnotations[i];
            for (int j = 0, k = (infos == null) ? 0 : infos.length; j < k; ++j) {
                infos[j].reset();
            }
        }
        super.reset();
    }
    
    @Override
    protected void toStringContent(final StringBuffer buffer) {
        super.toStringContent(buffer);
        for (int i = 0, l = (this.parameterAnnotations == null) ? 0 : this.parameterAnnotations.length; i < l; ++i) {
            buffer.append("param" + (i - 1));
            buffer.append('\n');
            final AnnotationInfo[] infos = this.parameterAnnotations[i];
            for (int j = 0, k = (infos == null) ? 0 : infos.length; j < k; ++j) {
                buffer.append(infos[j]);
                buffer.append('\n');
            }
        }
    }
}
