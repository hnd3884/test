package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.ast.SubRoutineStatement;
import org.eclipse.jdt.internal.compiler.lookup.CatchParameterBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnionTypeReference;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.codegen.ObjectCache;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

public class ExceptionHandlingFlowContext extends FlowContext
{
    public static final int BitCacheSize = 32;
    public ReferenceBinding[] handledExceptions;
    int[] isReached;
    int[] isNeeded;
    UnconditionalFlowInfo[] initsOnExceptions;
    ObjectCache indexes;
    boolean isMethodContext;
    public UnconditionalFlowInfo initsOnReturn;
    public FlowContext initializationParent;
    public ArrayList extendedExceptions;
    private static final Argument[] NO_ARGUMENTS;
    public Argument[] catchArguments;
    private int[] exceptionToCatchBlockMap;
    
    static {
        NO_ARGUMENTS = new Argument[0];
    }
    
    public ExceptionHandlingFlowContext(final FlowContext parent, final ASTNode associatedNode, final ReferenceBinding[] handledExceptions, final FlowContext initializationParent, final BlockScope scope, final UnconditionalFlowInfo flowInfo) {
        this(parent, associatedNode, handledExceptions, null, ExceptionHandlingFlowContext.NO_ARGUMENTS, initializationParent, scope, flowInfo);
    }
    
    public ExceptionHandlingFlowContext(final FlowContext parent, final TryStatement tryStatement, final ReferenceBinding[] handledExceptions, final int[] exceptionToCatchBlockMap, final FlowContext initializationParent, final BlockScope scope, final FlowInfo flowInfo) {
        this(parent, tryStatement, handledExceptions, exceptionToCatchBlockMap, tryStatement.catchArguments, initializationParent, scope, flowInfo.unconditionalInits());
        final UnconditionalFlowInfo unconditionalCopy = flowInfo.unconditionalCopy();
        unconditionalCopy.iNBit = -1L;
        unconditionalCopy.iNNBit = -1L;
        final UnconditionalFlowInfo unconditionalFlowInfo = unconditionalCopy;
        unconditionalFlowInfo.tagBits |= 0x40;
        this.initsOnFinally = unconditionalCopy;
    }
    
    ExceptionHandlingFlowContext(final FlowContext parent, final ASTNode associatedNode, final ReferenceBinding[] handledExceptions, final int[] exceptionToCatchBlockMap, final Argument[] catchArguments, final FlowContext initializationParent, final BlockScope scope, final UnconditionalFlowInfo flowInfo) {
        super(parent, associatedNode);
        this.indexes = new ObjectCache();
        this.isMethodContext = (scope == scope.methodScope());
        this.handledExceptions = handledExceptions;
        this.catchArguments = catchArguments;
        this.exceptionToCatchBlockMap = exceptionToCatchBlockMap;
        final int count = handledExceptions.length;
        final int cacheSize = count / 32 + 1;
        this.isReached = new int[cacheSize];
        this.isNeeded = new int[cacheSize];
        this.initsOnExceptions = new UnconditionalFlowInfo[count];
        final boolean markExceptionsAndThrowableAsReached = !this.isMethodContext || scope.compilerOptions().reportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable;
        for (int i = 0; i < count; ++i) {
            final ReferenceBinding handledException = handledExceptions[i];
            final int catchBlock = (this.exceptionToCatchBlockMap != null) ? this.exceptionToCatchBlockMap[i] : i;
            this.indexes.put(handledException, i);
            if (handledException.isUncheckedException(true)) {
                if (markExceptionsAndThrowableAsReached || (handledException.id != 21 && handledException.id != 25)) {
                    final int[] isReached = this.isReached;
                    final int n = i / 32;
                    isReached[n] |= 1 << i % 32;
                }
                this.initsOnExceptions[catchBlock] = flowInfo.unconditionalCopy();
            }
            else {
                this.initsOnExceptions[catchBlock] = FlowInfo.DEAD_END;
            }
        }
        if (!this.isMethodContext) {
            System.arraycopy(this.isReached, 0, this.isNeeded, 0, cacheSize);
        }
        this.initsOnReturn = FlowInfo.DEAD_END;
        this.initializationParent = initializationParent;
    }
    
    public void complainIfUnusedExceptionHandlers(final AbstractMethodDeclaration method) {
        final MethodScope scope = method.scope;
        if ((method.binding.modifiers & 0x30000000) != 0x0 && !scope.compilerOptions().reportUnusedDeclaredThrownExceptionWhenOverriding) {
            return;
        }
        TypeBinding[] docCommentReferences = null;
        int docCommentReferencesLength = 0;
        if (scope.compilerOptions().reportUnusedDeclaredThrownExceptionIncludeDocCommentReference && method.javadoc != null && method.javadoc.exceptionReferences != null && (docCommentReferencesLength = method.javadoc.exceptionReferences.length) > 0) {
            docCommentReferences = new TypeBinding[docCommentReferencesLength];
            for (int i = 0; i < docCommentReferencesLength; ++i) {
                docCommentReferences[i] = method.javadoc.exceptionReferences[i].resolvedType;
            }
        }
    Label_0222:
        for (int i = 0, count = this.handledExceptions.length; i < count; ++i) {
            final int index = this.indexes.get(this.handledExceptions[i]);
            if ((this.isReached[index / 32] & 1 << index % 32) == 0x0) {
                for (int j = 0; j < docCommentReferencesLength; ++j) {
                    if (TypeBinding.equalsEquals(docCommentReferences[j], this.handledExceptions[i])) {
                        continue Label_0222;
                    }
                }
                scope.problemReporter().unusedDeclaredThrownException(this.handledExceptions[index], method, method.thrownExceptions[index]);
            }
        }
    }
    
    public void complainIfUnusedExceptionHandlers(final BlockScope scope, final TryStatement tryStatement) {
        for (int index = 0, count = this.handledExceptions.length; index < count; ++index) {
            final int cacheIndex = index / 32;
            final int bitMask = 1 << index % 32;
            if ((this.isReached[cacheIndex] & bitMask) == 0x0) {
                scope.problemReporter().unreachableCatchBlock(this.handledExceptions[index], this.getExceptionType(index));
            }
            else if ((this.isNeeded[cacheIndex] & bitMask) == 0x0) {
                scope.problemReporter().hiddenCatchBlock(this.handledExceptions[index], this.getExceptionType(index));
            }
        }
    }
    
    private ASTNode getExceptionType(final int index) {
        if (this.exceptionToCatchBlockMap == null) {
            return this.catchArguments[index].type;
        }
        final int catchBlock = this.exceptionToCatchBlockMap[index];
        final ASTNode node = this.catchArguments[catchBlock].type;
        if (node instanceof UnionTypeReference) {
            final TypeReference[] typeRefs = ((UnionTypeReference)node).typeReferences;
            for (int i = 0, len = typeRefs.length; i < len; ++i) {
                final TypeReference typeRef = typeRefs[i];
                if (TypeBinding.equalsEquals(typeRef.resolvedType, this.handledExceptions[index])) {
                    return typeRef;
                }
            }
        }
        return node;
    }
    
    @Override
    public FlowContext getInitializationContext() {
        return this.initializationParent;
    }
    
    @Override
    public String individualToString() {
        final StringBuffer buffer = new StringBuffer("Exception flow context");
        for (int length = this.handledExceptions.length, i = 0; i < length; ++i) {
            final int cacheIndex = i / 32;
            final int bitMask = 1 << i % 32;
            buffer.append('[').append(this.handledExceptions[i].readableName());
            if ((this.isReached[cacheIndex] & bitMask) != 0x0) {
                if ((this.isNeeded[cacheIndex] & bitMask) == 0x0) {
                    buffer.append("-masked");
                }
                else {
                    buffer.append("-reached");
                }
            }
            else {
                buffer.append("-not reached");
            }
            final int catchBlock = (this.exceptionToCatchBlockMap != null) ? this.exceptionToCatchBlockMap[i] : i;
            buffer.append('-').append(this.initsOnExceptions[catchBlock].toString()).append(']');
        }
        buffer.append("[initsOnReturn -").append(this.initsOnReturn.toString()).append(']');
        return buffer.toString();
    }
    
    public UnconditionalFlowInfo initsOnException(final int index) {
        return this.initsOnExceptions[index];
    }
    
    @Override
    public UnconditionalFlowInfo initsOnReturn() {
        return this.initsOnReturn;
    }
    
    public void mergeUnhandledException(final TypeBinding newException) {
        if (this.extendedExceptions == null) {
            this.extendedExceptions = new ArrayList(5);
            for (int i = 0; i < this.handledExceptions.length; ++i) {
                this.extendedExceptions.add(this.handledExceptions[i]);
            }
        }
        boolean isRedundant = false;
        for (int j = this.extendedExceptions.size() - 1; j >= 0; --j) {
            switch (Scope.compareTypes(newException, this.extendedExceptions.get(j))) {
                case 1: {
                    this.extendedExceptions.remove(j);
                    break;
                }
                case -1: {
                    isRedundant = true;
                    break;
                }
            }
        }
        if (!isRedundant) {
            this.extendedExceptions.add(newException);
        }
    }
    
    public void recordHandlingException(final ReferenceBinding exceptionType, final UnconditionalFlowInfo flowInfo, final TypeBinding raisedException, final TypeBinding caughtException, final ASTNode invocationSite, final boolean wasAlreadyDefinitelyCaught) {
        final int index = this.indexes.get(exceptionType);
        final int cacheIndex = index / 32;
        final int bitMask = 1 << index % 32;
        if (!wasAlreadyDefinitelyCaught) {
            final int[] isNeeded = this.isNeeded;
            final int n = cacheIndex;
            isNeeded[n] |= bitMask;
        }
        final int[] isReached = this.isReached;
        final int n2 = cacheIndex;
        isReached[n2] |= bitMask;
        final int catchBlock = (this.exceptionToCatchBlockMap != null) ? this.exceptionToCatchBlockMap[index] : index;
        if (caughtException != null && this.catchArguments != null && this.catchArguments.length > 0 && !wasAlreadyDefinitelyCaught) {
            final CatchParameterBinding catchParameter = (CatchParameterBinding)this.catchArguments[catchBlock].binding;
            catchParameter.setPreciseType(caughtException);
        }
        this.initsOnExceptions[catchBlock] = (((this.initsOnExceptions[catchBlock].tagBits & 0x3) == 0x0) ? this.initsOnExceptions[catchBlock].mergedWith(flowInfo) : flowInfo.unconditionalCopy());
    }
    
    @Override
    public void recordReturnFrom(final UnconditionalFlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x1) == 0x0) {
            if ((this.initsOnReturn.tagBits & 0x1) == 0x0) {
                this.initsOnReturn = this.initsOnReturn.mergedWith(flowInfo);
            }
            else {
                this.initsOnReturn = (UnconditionalFlowInfo)flowInfo.copy();
            }
        }
    }
    
    @Override
    public SubRoutineStatement subroutine() {
        if (!(this.associatedNode instanceof SubRoutineStatement)) {
            return null;
        }
        if (this.parent.subroutine() == this.associatedNode) {
            return null;
        }
        return (SubRoutineStatement)this.associatedNode;
    }
}
