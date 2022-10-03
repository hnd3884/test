package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;

public class Clinit extends AbstractMethodDeclaration
{
    private static int ENUM_CONSTANTS_THRESHOLD;
    private FieldBinding assertionSyntheticFieldBinding;
    private FieldBinding classLiteralSyntheticField;
    
    static {
        Clinit.ENUM_CONSTANTS_THRESHOLD = 2000;
    }
    
    public Clinit(final CompilationResult compilationResult) {
        super(compilationResult);
        this.assertionSyntheticFieldBinding = null;
        this.classLiteralSyntheticField = null;
        this.modifiers = 0;
        this.selector = TypeConstants.CLINIT;
    }
    
    public void analyseCode(final ClassScope classScope, final InitializationFlowContext staticInitializerFlowContext, FlowInfo flowInfo) {
        if (this.ignoreFurtherInvestigation) {
            return;
        }
        try {
            final ExceptionHandlingFlowContext clinitContext = new ExceptionHandlingFlowContext(staticInitializerFlowContext.parent, this, Binding.NO_EXCEPTIONS, staticInitializerFlowContext, this.scope, FlowInfo.DEAD_END);
            if ((flowInfo.tagBits & 0x1) == 0x0) {
                this.bits |= 0x40;
            }
            flowInfo = flowInfo.mergedWith(staticInitializerFlowContext.initsOnReturn);
            final FieldBinding[] fields = this.scope.enclosingSourceType().fields();
            for (int i = 0, count = fields.length; i < count; ++i) {
                final FieldBinding field = fields[i];
                if (field.isStatic() && !flowInfo.isDefinitelyAssigned(field)) {
                    if (field.isFinal()) {
                        this.scope.problemReporter().uninitializedBlankFinalField(field, this.scope.referenceType().declarationOf(field.original()));
                    }
                    else if (field.isNonNull()) {
                        this.scope.problemReporter().uninitializedNonNullField(field, this.scope.referenceType().declarationOf(field.original()));
                    }
                }
            }
            staticInitializerFlowContext.checkInitializerExceptions(this.scope, clinitContext, flowInfo);
        }
        catch (final AbortMethod abortMethod) {
            this.ignoreFurtherInvestigation = true;
        }
    }
    
    @Override
    public void generateCode(final ClassScope classScope, final ClassFile classFile) {
        int clinitOffset = 0;
        if (this.ignoreFurtherInvestigation) {
            return;
        }
        CompilationResult unitResult = null;
        int problemCount = 0;
        if (classScope != null) {
            final TypeDeclaration referenceContext = classScope.referenceContext;
            if (referenceContext != null) {
                unitResult = referenceContext.compilationResult();
                problemCount = unitResult.problemCount;
            }
        }
        boolean restart = false;
        do {
            try {
                clinitOffset = classFile.contentsOffset;
                this.generateCode(classScope, classFile, clinitOffset);
                restart = false;
            }
            catch (final AbortMethod e) {
                if (e.compilationResult == CodeStream.RESTART_IN_WIDE_MODE) {
                    classFile.contentsOffset = clinitOffset;
                    --classFile.methodCount;
                    classFile.codeStream.resetInWideMode();
                    if (unitResult != null) {
                        unitResult.problemCount = problemCount;
                    }
                    restart = true;
                }
                else if (e.compilationResult == CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE) {
                    classFile.contentsOffset = clinitOffset;
                    --classFile.methodCount;
                    classFile.codeStream.resetForCodeGenUnusedLocals();
                    if (unitResult != null) {
                        unitResult.problemCount = problemCount;
                    }
                    restart = true;
                }
                else {
                    classFile.contentsOffset = clinitOffset;
                    --classFile.methodCount;
                    restart = false;
                }
            }
        } while (restart);
    }
    
    private void generateCode(final ClassScope classScope, final ClassFile classFile, final int clinitOffset) {
        final ConstantPool constantPool = classFile.constantPool;
        final int constantPoolOffset = constantPool.currentOffset;
        final int constantPoolIndex = constantPool.currentIndex;
        classFile.generateMethodInfoHeaderForClinit();
        final int codeAttributeOffset = classFile.contentsOffset;
        classFile.generateCodeAttributeHeader();
        final CodeStream codeStream = classFile.codeStream;
        this.resolve(classScope);
        codeStream.reset(this, classFile);
        final TypeDeclaration declaringType = classScope.referenceContext;
        final MethodScope staticInitializerScope = declaringType.staticInitializerScope;
        staticInitializerScope.computeLocalVariablePositions(0, codeStream);
        if (this.assertionSyntheticFieldBinding != null) {
            codeStream.generateClassLiteralAccessForType(classScope.outerMostClassScope().enclosingSourceType(), this.classLiteralSyntheticField);
            codeStream.invokeJavaLangClassDesiredAssertionStatus();
            final BranchLabel falseLabel = new BranchLabel(codeStream);
            codeStream.ifne(falseLabel);
            codeStream.iconst_1();
            final BranchLabel jumpLabel = new BranchLabel(codeStream);
            codeStream.decrStackSize(1);
            codeStream.goto_(jumpLabel);
            falseLabel.place();
            codeStream.iconst_0();
            jumpLabel.place();
            codeStream.fieldAccess((byte)(-77), this.assertionSyntheticFieldBinding, null);
        }
        final FieldDeclaration[] fieldDeclarations = declaringType.fields;
        int sourcePosition = -1;
        int remainingFieldCount = 0;
        if (TypeDeclaration.kind(declaringType.modifiers) == 3) {
            final int enumCount = declaringType.enumConstantsCounter;
            if (enumCount > Clinit.ENUM_CONSTANTS_THRESHOLD) {
                int begin = -1;
                int count = 0;
                if (fieldDeclarations != null) {
                    final int max = fieldDeclarations.length;
                    for (int i = 0; i < max; ++i) {
                        final FieldDeclaration fieldDecl = fieldDeclarations[i];
                        if (fieldDecl.isStatic()) {
                            if (fieldDecl.getKind() == 3) {
                                if (begin == -1) {
                                    begin = i;
                                }
                                if (++count > Clinit.ENUM_CONSTANTS_THRESHOLD) {
                                    final SyntheticMethodBinding syntheticMethod = declaringType.binding.addSyntheticMethodForEnumInitialization(begin, i);
                                    codeStream.invoke((byte)(-72), syntheticMethod, null);
                                    begin = i;
                                    count = 1;
                                }
                            }
                            else {
                                ++remainingFieldCount;
                            }
                        }
                    }
                    if (count != 0) {
                        final SyntheticMethodBinding syntheticMethod2 = declaringType.binding.addSyntheticMethodForEnumInitialization(begin, max);
                        codeStream.invoke((byte)(-72), syntheticMethod2, null);
                    }
                }
            }
            else if (fieldDeclarations != null) {
                for (int j = 0, max2 = fieldDeclarations.length; j < max2; ++j) {
                    final FieldDeclaration fieldDecl2 = fieldDeclarations[j];
                    if (fieldDecl2.isStatic()) {
                        if (fieldDecl2.getKind() == 3) {
                            fieldDecl2.generateCode(staticInitializerScope, codeStream);
                        }
                        else {
                            ++remainingFieldCount;
                        }
                    }
                }
            }
            codeStream.generateInlinedValue(enumCount);
            codeStream.anewarray(declaringType.binding);
            if (enumCount > 0 && fieldDeclarations != null) {
                for (int j = 0, max2 = fieldDeclarations.length; j < max2; ++j) {
                    final FieldDeclaration fieldDecl2 = fieldDeclarations[j];
                    if (fieldDecl2.getKind() == 3) {
                        codeStream.dup();
                        codeStream.generateInlinedValue(fieldDecl2.binding.id);
                        codeStream.fieldAccess((byte)(-78), fieldDecl2.binding, null);
                        codeStream.aastore();
                    }
                }
            }
            codeStream.fieldAccess((byte)(-77), declaringType.enumValuesSyntheticfield, null);
            if (remainingFieldCount != 0) {
                for (int j = 0, max2 = fieldDeclarations.length; j < max2; ++j) {
                    if (remainingFieldCount < 0) {
                        break;
                    }
                    final FieldDeclaration fieldDecl2 = fieldDeclarations[j];
                    switch (fieldDecl2.getKind()) {
                        case 2: {
                            if (!fieldDecl2.isStatic()) {
                                break;
                            }
                            --remainingFieldCount;
                            sourcePosition = ((Initializer)fieldDecl2).block.sourceEnd;
                            fieldDecl2.generateCode(staticInitializerScope, codeStream);
                            break;
                        }
                        case 1: {
                            if (!fieldDecl2.binding.isStatic()) {
                                break;
                            }
                            --remainingFieldCount;
                            sourcePosition = fieldDecl2.declarationEnd;
                            fieldDecl2.generateCode(staticInitializerScope, codeStream);
                            break;
                        }
                    }
                }
            }
        }
        else if (fieldDeclarations != null) {
            for (int k = 0, max3 = fieldDeclarations.length; k < max3; ++k) {
                final FieldDeclaration fieldDecl3 = fieldDeclarations[k];
                switch (fieldDecl3.getKind()) {
                    case 2: {
                        if (!fieldDecl3.isStatic()) {
                            break;
                        }
                        sourcePosition = ((Initializer)fieldDecl3).block.sourceEnd;
                        fieldDecl3.generateCode(staticInitializerScope, codeStream);
                        break;
                    }
                    case 1: {
                        if (!fieldDecl3.binding.isStatic()) {
                            break;
                        }
                        sourcePosition = fieldDecl3.declarationEnd;
                        fieldDecl3.generateCode(staticInitializerScope, codeStream);
                        break;
                    }
                }
            }
        }
        if (codeStream.position == 0) {
            classFile.contentsOffset = clinitOffset;
            --classFile.methodCount;
            constantPool.resetForClinit(constantPoolIndex, constantPoolOffset);
        }
        else {
            if ((this.bits & 0x40) != 0x0) {
                final int before = codeStream.position;
                codeStream.return_();
                if (sourcePosition != -1) {
                    codeStream.recordPositionsFrom(before, sourcePosition);
                }
            }
            codeStream.recordPositionsFrom(0, declaringType.sourceStart);
            classFile.completeCodeAttributeForClinit(codeAttributeOffset);
        }
    }
    
    @Override
    public boolean isClinit() {
        return true;
    }
    
    @Override
    public boolean isInitializationMethod() {
        return true;
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public void parseStatements(final Parser parser, final CompilationUnitDeclaration unit) {
    }
    
    @Override
    public StringBuffer print(final int tab, final StringBuffer output) {
        ASTNode.printIndent(tab, output).append("<clinit>()");
        this.printBody(tab + 1, output);
        return output;
    }
    
    @Override
    public void resolve(final ClassScope classScope) {
        this.scope = new MethodScope(classScope, classScope.referenceContext, true);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final ClassScope classScope) {
        visitor.visit(this, classScope);
        visitor.endVisit(this, classScope);
    }
    
    public void setAssertionSupport(final FieldBinding assertionSyntheticFieldBinding, final boolean needClassLiteralField) {
        this.assertionSyntheticFieldBinding = assertionSyntheticFieldBinding;
        if (needClassLiteralField) {
            final SourceTypeBinding sourceType = this.scope.outerMostClassScope().enclosingSourceType();
            if (!sourceType.isInterface() && !sourceType.isBaseType()) {
                this.classLiteralSyntheticField = sourceType.addSyntheticFieldForClassLiteral(sourceType, this.scope);
            }
        }
    }
}
