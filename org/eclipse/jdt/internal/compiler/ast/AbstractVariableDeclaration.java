package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;

public abstract class AbstractVariableDeclaration extends Statement implements InvocationSite
{
    public int declarationEnd;
    public int declarationSourceEnd;
    public int declarationSourceStart;
    public int hiddenVariableDepth;
    public Expression initialization;
    public int modifiers;
    public int modifiersSourceStart;
    public Annotation[] annotations;
    public char[] name;
    public TypeReference type;
    public static final int FIELD = 1;
    public static final int INITIALIZER = 2;
    public static final int ENUM_CONSTANT = 3;
    public static final int LOCAL_VARIABLE = 4;
    public static final int PARAMETER = 5;
    public static final int TYPE_PARAMETER = 6;
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        return flowInfo;
    }
    
    @Override
    public TypeBinding[] genericTypeArguments() {
        return null;
    }
    
    public abstract int getKind();
    
    @Override
    public InferenceContext18 freshInferenceContext(final Scope scope) {
        return null;
    }
    
    @Override
    public boolean isSuperAccess() {
        return false;
    }
    
    @Override
    public boolean isTypeAccess() {
        return false;
    }
    
    @Override
    public StringBuffer printStatement(final int indent, final StringBuffer output) {
        this.printAsExpression(indent, output);
        switch (this.getKind()) {
            case 3: {
                return output.append(',');
            }
            default: {
                return output.append(';');
            }
        }
    }
    
    public StringBuffer printAsExpression(final int indent, final StringBuffer output) {
        ASTNode.printIndent(indent, output);
        ASTNode.printModifiers(this.modifiers, output);
        if (this.annotations != null) {
            ASTNode.printAnnotations(this.annotations, output);
            output.append(' ');
        }
        if (this.type != null) {
            this.type.print(0, output).append(' ');
        }
        output.append(this.name);
        switch (this.getKind()) {
            case 3: {
                if (this.initialization != null) {
                    this.initialization.printExpression(indent, output);
                    break;
                }
                break;
            }
            default: {
                if (this.initialization != null) {
                    output.append(" = ");
                    this.initialization.printExpression(indent, output);
                    break;
                }
                break;
            }
        }
        return output;
    }
    
    @Override
    public void resolve(final BlockScope scope) {
    }
    
    @Override
    public void setActualReceiverType(final ReferenceBinding receiverType) {
    }
    
    @Override
    public void setDepth(final int depth) {
        this.hiddenVariableDepth = depth;
    }
    
    @Override
    public void setFieldIndex(final int depth) {
    }
}
