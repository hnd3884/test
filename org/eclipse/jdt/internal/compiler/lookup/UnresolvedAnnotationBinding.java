package org.eclipse.jdt.internal.compiler.lookup;

public class UnresolvedAnnotationBinding extends AnnotationBinding
{
    private LookupEnvironment env;
    private boolean typeUnresolved;
    
    UnresolvedAnnotationBinding(final ReferenceBinding type, final ElementValuePair[] pairs, final LookupEnvironment env) {
        super(type, pairs);
        this.typeUnresolved = true;
        this.env = env;
    }
    
    @Override
    public void resolve() {
        if (this.typeUnresolved) {
            final boolean wasToleratingMissingTypeProcessingAnnotations = this.env.mayTolerateMissingType;
            this.env.mayTolerateMissingType = true;
            try {
                this.type = (ReferenceBinding)BinaryTypeBinding.resolveType(this.type, this.env, false);
            }
            finally {
                this.env.mayTolerateMissingType = wasToleratingMissingTypeProcessingAnnotations;
            }
            this.env.mayTolerateMissingType = wasToleratingMissingTypeProcessingAnnotations;
            this.typeUnresolved = false;
        }
    }
    
    @Override
    public ReferenceBinding getAnnotationType() {
        this.resolve();
        return this.type;
    }
    
    @Override
    public ElementValuePair[] getElementValuePairs() {
        if (this.env != null) {
            if (this.typeUnresolved) {
                this.resolve();
            }
            int i = this.pairs.length;
            while (--i >= 0) {
                final ElementValuePair pair = this.pairs[i];
                final MethodBinding[] methods = this.type.getMethods(pair.getName());
                if (methods != null && methods.length == 1) {
                    pair.setMethodBinding(methods[0]);
                }
                final Object value = pair.getValue();
                if (value instanceof UnresolvedReferenceBinding) {
                    pair.setValue(((UnresolvedReferenceBinding)value).resolve(this.env, false));
                }
            }
            this.env = null;
        }
        return this.pairs;
    }
}
