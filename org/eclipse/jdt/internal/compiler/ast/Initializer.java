package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;

public class Initializer extends FieldDeclaration
{
    public Block block;
    public int lastVisibleFieldID;
    public int bodyStart;
    public int bodyEnd;
    private MethodBinding methodBinding;
    
    public Initializer(final Block block, final int modifiers) {
        this.block = block;
        this.modifiers = modifiers;
        if (block != null) {
            final int sourceStart = block.sourceStart;
            this.sourceStart = sourceStart;
            this.declarationSourceStart = sourceStart;
        }
    }
    
    @Override
    public FlowInfo analyseCode(final MethodScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        if (this.block != null) {
            return this.block.analyseCode(currentScope, flowContext, flowInfo);
        }
        return flowInfo;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0x0) {
            return;
        }
        final int pc = codeStream.position;
        if (this.block != null) {
            this.block.generateCode(currentScope, codeStream);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public int getKind() {
        return 2;
    }
    
    @Override
    public boolean isStatic() {
        return (this.modifiers & 0x8) != 0x0;
    }
    
    public void parseStatements(final Parser parser, final TypeDeclaration typeDeclaration, final CompilationUnitDeclaration unit) {
        parser.parse(this, typeDeclaration, unit);
    }
    
    @Override
    public StringBuffer printStatement(final int indent, final StringBuffer output) {
        if (this.modifiers != 0) {
            ASTNode.printIndent(indent, output);
            ASTNode.printModifiers(this.modifiers, output);
            if (this.annotations != null) {
                ASTNode.printAnnotations(this.annotations, output);
                output.append(' ');
            }
            output.append("{\n");
            if (this.block != null) {
                this.block.printBody(indent, output);
            }
            ASTNode.printIndent(indent, output).append('}');
            return output;
        }
        if (this.block != null) {
            this.block.printStatement(indent, output);
        }
        else {
            ASTNode.printIndent(indent, output).append("{}");
        }
        return output;
    }
    
    @Override
    public void resolve(final MethodScope scope) {
        final FieldBinding previousField = scope.initializedField;
        final int previousFieldID = scope.lastVisibleFieldID;
        try {
            scope.initializedField = null;
            scope.lastVisibleFieldID = this.lastVisibleFieldID;
            if (this.isStatic()) {
                final ReferenceBinding declaringType = scope.enclosingSourceType();
                if (declaringType.isNestedType() && !declaringType.isStatic()) {
                    scope.problemReporter().innerTypesCannotDeclareStaticInitializers(declaringType, this);
                }
            }
            if (this.block != null) {
                this.block.resolve(scope);
            }
        }
        finally {
            scope.initializedField = previousField;
            scope.lastVisibleFieldID = previousFieldID;
        }
        scope.initializedField = previousField;
        scope.lastVisibleFieldID = previousFieldID;
    }
    
    public MethodBinding getMethodBinding() {
        if (this.methodBinding == null) {
            final Scope scope = this.block.scope;
            this.methodBinding = (this.isStatic() ? new MethodBinding(8, CharOperation.NO_CHAR, TypeBinding.VOID, Binding.NO_PARAMETERS, Binding.NO_EXCEPTIONS, scope.enclosingSourceType()) : new MethodBinding(0, CharOperation.NO_CHAR, TypeBinding.VOID, Binding.NO_PARAMETERS, Binding.NO_EXCEPTIONS, scope.enclosingSourceType()));
        }
        return this.methodBinding;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final MethodScope scope) {
        if (visitor.visit(this, scope) && this.block != null) {
            this.block.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}
