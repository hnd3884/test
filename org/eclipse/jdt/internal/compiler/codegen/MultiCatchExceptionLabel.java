package org.eclipse.jdt.internal.compiler.codegen;

import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.UnionTypeReference;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class MultiCatchExceptionLabel extends ExceptionLabel
{
    ExceptionLabel[] exceptionLabels;
    
    public MultiCatchExceptionLabel(final CodeStream codeStream, final TypeBinding exceptionType) {
        super(codeStream, exceptionType);
    }
    
    public void initialize(final UnionTypeReference typeReference, final Annotation[] annotations) {
        final TypeReference[] typeReferences = typeReference.typeReferences;
        final int length = typeReferences.length;
        this.exceptionLabels = new ExceptionLabel[length];
        for (int i = 0; i < length; ++i) {
            this.exceptionLabels[i] = new ExceptionLabel(this.codeStream, typeReferences[i].resolvedType, typeReferences[i], (Annotation[])((i == 0) ? annotations : null));
        }
    }
    
    @Override
    public void place() {
        for (int i = 0, max = this.exceptionLabels.length; i < max; ++i) {
            this.exceptionLabels[i].place();
        }
    }
    
    @Override
    public void placeEnd() {
        for (int i = 0, max = this.exceptionLabels.length; i < max; ++i) {
            this.exceptionLabels[i].placeEnd();
        }
    }
    
    @Override
    public void placeStart() {
        for (int i = 0, max = this.exceptionLabels.length; i < max; ++i) {
            this.exceptionLabels[i].placeStart();
        }
    }
    
    @Override
    public int getCount() {
        int temp = 0;
        for (int i = 0, max = this.exceptionLabels.length; i < max; ++i) {
            temp += this.exceptionLabels[i].getCount();
        }
        return temp;
    }
}
