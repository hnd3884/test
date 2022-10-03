package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import java.io.IOException;
import org.eclipse.jdt.internal.compiler.util.Messages;
import java.io.FileNotFoundException;
import org.eclipse.jdt.internal.compiler.classfmt.ExternalAnnotationProvider;
import java.util.zip.ZipFile;
import java.io.FileInputStream;
import java.io.File;
import org.eclipse.jdt.internal.compiler.env.ITypeAnnotationWalker;

class ExternalAnnotationSuperimposer extends TypeBindingVisitor
{
    private ITypeAnnotationWalker currentWalker;
    private TypeBinding typeReplacement;
    private LookupEnvironment environment;
    private boolean isReplacing;
    
    public static void apply(final SourceTypeBinding typeBinding, final String externalAnnotationPath) {
        ZipFile zipFile = null;
        try {
            final File annotationBase = new File(externalAnnotationPath);
            if (annotationBase.exists()) {
                final String binaryTypeName = String.valueOf(typeBinding.constantPoolName());
                final String relativeFileName = String.valueOf(binaryTypeName.replace('.', '/')) + ".eea";
                InputStream input;
                if (annotationBase.isDirectory()) {
                    input = new FileInputStream(String.valueOf(externalAnnotationPath) + '/' + relativeFileName);
                }
                else {
                    zipFile = new ZipFile(externalAnnotationPath);
                    final ZipEntry zipEntry = zipFile.getEntry(relativeFileName);
                    if (zipEntry == null) {
                        return;
                    }
                    input = zipFile.getInputStream(zipEntry);
                }
                annotateType(typeBinding, new ExternalAnnotationProvider(input, binaryTypeName), typeBinding.environment);
            }
        }
        catch (final FileNotFoundException ex) {}
        catch (final IOException e) {
            typeBinding.scope.problemReporter().abortDueToInternalError(Messages.bind(Messages.abort_externaAnnotationFile, new String[] { String.valueOf(typeBinding.readableName()), externalAnnotationPath, e.getMessage() }));
        }
        finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                }
                catch (final IOException ex2) {}
            }
        }
        if (zipFile != null) {
            try {
                zipFile.close();
            }
            catch (final IOException ex3) {}
        }
    }
    
    static void annotateType(final SourceTypeBinding binding, final ExternalAnnotationProvider provider, final LookupEnvironment environment) {
        final ITypeAnnotationWalker typeWalker = provider.forTypeHeader(environment);
        if (typeWalker != null && typeWalker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) {
            final ExternalAnnotationSuperimposer visitor = new ExternalAnnotationSuperimposer(environment);
            final TypeVariableBinding[] typeParameters = binding.typeVariables();
            for (int i = 0; i < typeParameters.length; ++i) {
                if (visitor.go(typeWalker.toTypeParameter(true, i))) {
                    typeParameters[i] = visitor.superimpose(typeParameters[i], TypeVariableBinding.class);
                }
            }
        }
        binding.externalAnnotationProvider = provider;
    }
    
    public static void annotateFieldBinding(final FieldBinding field, final ExternalAnnotationProvider provider, final LookupEnvironment environment) {
        char[] fieldSignature = field.genericSignature();
        if (fieldSignature == null && field.type != null) {
            fieldSignature = field.type.signature();
        }
        final ITypeAnnotationWalker walker = provider.forField(field.name, fieldSignature, environment);
        final ExternalAnnotationSuperimposer visitor = new ExternalAnnotationSuperimposer(environment);
        if (visitor.go(walker)) {
            field.type = visitor.superimpose(field.type, TypeBinding.class);
        }
    }
    
    public static void annotateMethodBinding(final MethodBinding method, final ExternalAnnotationProvider provider, final LookupEnvironment environment) {
        char[] methodSignature = method.genericSignature();
        if (methodSignature == null) {
            methodSignature = method.signature();
        }
        final ITypeAnnotationWalker walker = provider.forMethod(method.selector, methodSignature, environment);
        if (walker != null && walker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) {
            final ExternalAnnotationSuperimposer visitor = new ExternalAnnotationSuperimposer(environment);
            final TypeVariableBinding[] typeParams = method.typeVariables;
            for (short i = 0; i < typeParams.length; ++i) {
                if (visitor.go(walker.toTypeParameter(false, i))) {
                    typeParams[i] = visitor.superimpose(typeParams[i], TypeVariableBinding.class);
                }
            }
            if (!method.isConstructor() && visitor.go(walker.toMethodReturn())) {
                method.returnType = visitor.superimpose(method.returnType, TypeBinding.class);
            }
            final TypeBinding[] parameters = method.parameters;
            for (short j = 0; j < parameters.length; ++j) {
                if (visitor.go(walker.toMethodParameter(j))) {
                    parameters[j] = visitor.superimpose(parameters[j], TypeBinding.class);
                }
            }
        }
    }
    
    ExternalAnnotationSuperimposer(final LookupEnvironment environment) {
        this.environment = environment;
    }
    
    private ExternalAnnotationSuperimposer(final TypeBinding typeReplacement, final boolean isReplacing, final ITypeAnnotationWalker walker) {
        this.typeReplacement = typeReplacement;
        this.isReplacing = isReplacing;
        this.currentWalker = walker;
    }
    
    private ExternalAnnotationSuperimposer snapshot() {
        final ExternalAnnotationSuperimposer memento = new ExternalAnnotationSuperimposer(this.typeReplacement, this.isReplacing, this.currentWalker);
        this.typeReplacement = null;
        this.isReplacing = false;
        return memento;
    }
    
    private void restore(final ExternalAnnotationSuperimposer memento) {
        this.isReplacing = memento.isReplacing;
        this.currentWalker = memento.currentWalker;
    }
    
    boolean go(final ITypeAnnotationWalker walker) {
        this.reset();
        this.typeReplacement = null;
        this.isReplacing = false;
        this.currentWalker = walker;
        return walker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
    }
    
     <T extends TypeBinding> T superimpose(final T type, final Class<? extends T> cl) {
        TypeBindingVisitor.visit(this, type);
        if (cl.isInstance(this.typeReplacement)) {
            return (T)cl.cast(this.typeReplacement);
        }
        return type;
    }
    
    private TypeBinding goAndSuperimpose(final ITypeAnnotationWalker walker, final TypeBinding type) {
        if (walker == ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) {
            return type;
        }
        this.currentWalker = walker;
        TypeBindingVisitor.visit(this, type);
        if (this.typeReplacement == null) {
            return type;
        }
        this.isReplacing = true;
        final TypeBinding answer = this.typeReplacement;
        this.typeReplacement = null;
        return answer;
    }
    
    @Override
    public boolean visit(final ArrayBinding arrayBinding) {
        final ExternalAnnotationSuperimposer memento = this.snapshot();
        try {
            final int dims = arrayBinding.dimensions;
            final AnnotationBinding[][] annotsOnDims = new AnnotationBinding[dims][];
            ITypeAnnotationWalker walker = this.currentWalker;
            for (int i = 0; i < dims; ++i) {
                final IBinaryAnnotation[] binaryAnnotations = walker.getAnnotationsAtCursor(arrayBinding.id);
                if (binaryAnnotations != ITypeAnnotationWalker.NO_ANNOTATIONS) {
                    annotsOnDims[i] = BinaryTypeBinding.createAnnotations(binaryAnnotations, this.environment, null);
                    this.isReplacing = true;
                }
                else {
                    annotsOnDims[i] = Binding.NO_ANNOTATIONS;
                }
                walker = walker.toNextArrayDimension();
            }
            final TypeBinding leafComponentType = this.goAndSuperimpose(walker, arrayBinding.leafComponentType());
            if (this.isReplacing) {
                this.typeReplacement = this.environment.createArrayType(leafComponentType, dims, AnnotatableTypeSystem.flattenedAnnotations(annotsOnDims));
            }
        }
        finally {
            this.restore(memento);
        }
        this.restore(memento);
        return false;
    }
    
    @Override
    public boolean visit(final BaseTypeBinding baseTypeBinding) {
        return false;
    }
    
    @Override
    public boolean visit(final IntersectionTypeBinding18 intersectionTypeBinding18) {
        return false;
    }
    
    @Override
    public boolean visit(final ParameterizedTypeBinding parameterizedTypeBinding) {
        final ExternalAnnotationSuperimposer memento = this.snapshot();
        try {
            final IBinaryAnnotation[] binaryAnnotations = this.currentWalker.getAnnotationsAtCursor(parameterizedTypeBinding.id);
            AnnotationBinding[] annotations = Binding.NO_ANNOTATIONS;
            if (binaryAnnotations != ITypeAnnotationWalker.NO_ANNOTATIONS) {
                annotations = BinaryTypeBinding.createAnnotations(binaryAnnotations, this.environment, null);
                this.isReplacing = true;
            }
            final TypeBinding[] typeArguments = parameterizedTypeBinding.typeArguments();
            final TypeBinding[] newArguments = new TypeBinding[typeArguments.length];
            for (int i = 0; i < typeArguments.length; ++i) {
                newArguments[i] = this.goAndSuperimpose(memento.currentWalker.toTypeArgument(i), typeArguments[i]);
            }
            if (this.isReplacing) {
                this.typeReplacement = this.environment.createParameterizedType(parameterizedTypeBinding.genericType(), newArguments, parameterizedTypeBinding.enclosingType(), annotations);
            }
            return false;
        }
        finally {
            this.restore(memento);
        }
    }
    
    @Override
    public boolean visit(final RawTypeBinding rawTypeBinding) {
        return this.visit((ReferenceBinding)rawTypeBinding);
    }
    
    @Override
    public boolean visit(final ReferenceBinding referenceBinding) {
        final IBinaryAnnotation[] binaryAnnotations = this.currentWalker.getAnnotationsAtCursor(referenceBinding.id);
        if (binaryAnnotations != ITypeAnnotationWalker.NO_ANNOTATIONS) {
            this.typeReplacement = this.environment.createAnnotatedType(referenceBinding, BinaryTypeBinding.createAnnotations(binaryAnnotations, this.environment, null));
        }
        return false;
    }
    
    @Override
    public boolean visit(final TypeVariableBinding typeVariable) {
        return this.visit((ReferenceBinding)typeVariable);
    }
    
    @Override
    public boolean visit(final WildcardBinding wildcardBinding) {
        TypeBinding bound = wildcardBinding.bound;
        final ExternalAnnotationSuperimposer memento = this.snapshot();
        try {
            if (bound != null) {
                bound = this.goAndSuperimpose(memento.currentWalker.toWildcardBound(), bound);
            }
            final IBinaryAnnotation[] binaryAnnotations = memento.currentWalker.getAnnotationsAtCursor(-1);
            if (this.isReplacing || binaryAnnotations != ITypeAnnotationWalker.NO_ANNOTATIONS) {
                final TypeBinding[] otherBounds = wildcardBinding.otherBounds;
                if (binaryAnnotations != ITypeAnnotationWalker.NO_ANNOTATIONS) {
                    final AnnotationBinding[] annotations = BinaryTypeBinding.createAnnotations(binaryAnnotations, this.environment, null);
                    this.typeReplacement = this.environment.createWildcard(wildcardBinding.genericType, wildcardBinding.rank, bound, otherBounds, wildcardBinding.boundKind, annotations);
                }
                else {
                    this.typeReplacement = this.environment.createWildcard(wildcardBinding.genericType, wildcardBinding.rank, bound, otherBounds, wildcardBinding.boundKind);
                }
            }
        }
        finally {
            this.restore(memento);
        }
        this.restore(memento);
        return false;
    }
}
