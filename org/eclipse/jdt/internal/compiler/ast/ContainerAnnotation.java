package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

public class ContainerAnnotation extends SingleMemberAnnotation
{
    private Annotation[] containees;
    private ArrayInitializer memberValues;
    
    public ContainerAnnotation(final Annotation repeatingAnnotation, final ReferenceBinding containerAnnotationType, final BlockScope scope) {
        final char[][] containerTypeName = containerAnnotationType.compoundName;
        if (containerTypeName.length == 1) {
            this.type = new SingleTypeReference(containerTypeName[0], 0L);
        }
        else {
            this.type = new QualifiedTypeReference(containerTypeName, new long[containerTypeName.length]);
        }
        this.sourceStart = repeatingAnnotation.sourceStart;
        this.sourceEnd = repeatingAnnotation.sourceEnd;
        this.resolvedType = containerAnnotationType;
        this.recipient = repeatingAnnotation.recipient;
        this.containees = new Annotation[0];
        final ArrayInitializer arrayInitializer = new ArrayInitializer();
        this.memberValues = arrayInitializer;
        this.memberValue = arrayInitializer;
        this.addContainee(repeatingAnnotation);
    }
    
    public void addContainee(final Annotation repeatingAnnotation) {
        final int length = this.containees.length;
        System.arraycopy(this.containees, 0, this.containees = new Annotation[length + 1], 0, length);
        this.containees[length] = repeatingAnnotation;
        this.memberValues.expressions = this.containees;
        repeatingAnnotation.setPersistibleAnnotation((length == 0) ? this : null);
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        if (this.compilerAnnotation != null) {
            return this.resolvedType;
        }
        this.constant = Constant.NotAConstant;
        ReferenceBinding containerAnnotationType = (ReferenceBinding)this.resolvedType;
        if (!containerAnnotationType.isValidBinding()) {
            containerAnnotationType = (ReferenceBinding)containerAnnotationType.closestMatch();
        }
        final Annotation repeatingAnnotation = this.containees[0];
        final ReferenceBinding repeatingAnnotationType = (ReferenceBinding)repeatingAnnotation.resolvedType;
        if (!repeatingAnnotationType.isDeprecated() && this.isTypeUseDeprecated(containerAnnotationType, scope)) {
            scope.problemReporter().deprecatedType(containerAnnotationType, repeatingAnnotation);
        }
        Annotation.checkContainerAnnotationType(repeatingAnnotation, scope, containerAnnotationType, repeatingAnnotationType, true);
        containerAnnotationType = (ReferenceBinding)(this.resolvedType = repeatingAnnotationType.containerAnnotationType());
        if (!this.resolvedType.isValidBinding()) {
            return this.resolvedType;
        }
        final MethodBinding[] methods = containerAnnotationType.methods();
        final MemberValuePair pair = this.memberValuePairs()[0];
        for (int i = 0, length = methods.length; i < length; ++i) {
            final MethodBinding method = methods[i];
            if (CharOperation.equals(method.selector, TypeConstants.VALUE)) {
                pair.binding = method;
                pair.resolveTypeExpecting(scope, method.returnType);
            }
        }
        this.compilerAnnotation = scope.environment().createAnnotation((ReferenceBinding)this.resolvedType, this.computeElementValuePairs());
        return this.resolvedType;
    }
}
