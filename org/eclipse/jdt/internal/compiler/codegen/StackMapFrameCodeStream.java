package org.eclipse.jdt.internal.compiler.codegen;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import java.util.Iterator;
import java.util.Arrays;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import java.util.HashSet;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.ClassFile;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;

public class StackMapFrameCodeStream extends CodeStream
{
    public int[] stateIndexes;
    public int stateIndexesCounter;
    private HashMap framePositions;
    public Set exceptionMarkers;
    public ArrayList stackDepthMarkers;
    public ArrayList stackMarkers;
    
    public StackMapFrameCodeStream(final ClassFile givenClassFile) {
        super(givenClassFile);
        this.generateAttributes |= 0x10;
    }
    
    @Override
    public void addDefinitelyAssignedVariables(final Scope scope, final int initStateIndex) {
        for (int i = 0; i < this.visibleLocalsCount; ++i) {
            final LocalVariableBinding localBinding = this.visibleLocals[i];
            if (localBinding != null) {
                final boolean isDefinitelyAssigned = this.isDefinitelyAssigned(scope, initStateIndex, localBinding);
                if (!isDefinitelyAssigned) {
                    if (this.stateIndexes != null) {
                        int j = 0;
                        final int max = this.stateIndexesCounter;
                        while (j < max) {
                            if (this.isDefinitelyAssigned(scope, this.stateIndexes[j], localBinding)) {
                                if (localBinding.initializationCount == 0 || localBinding.initializationPCs[(localBinding.initializationCount - 1 << 1) + 1] != -1) {
                                    localBinding.recordInitializationStartPC(this.position);
                                    break;
                                }
                                break;
                            }
                            else {
                                ++j;
                            }
                        }
                    }
                }
                else if (localBinding.initializationCount == 0 || localBinding.initializationPCs[(localBinding.initializationCount - 1 << 1) + 1] != -1) {
                    localBinding.recordInitializationStartPC(this.position);
                }
            }
        }
    }
    
    public void addExceptionMarker(final int pc, final TypeBinding typeBinding) {
        if (this.exceptionMarkers == null) {
            this.exceptionMarkers = new HashSet();
        }
        if (typeBinding == null) {
            this.exceptionMarkers.add(new ExceptionMarker(pc, ConstantPool.JavaLangThrowableConstantPoolName));
        }
        else {
            switch (typeBinding.id) {
                case 12: {
                    this.exceptionMarkers.add(new ExceptionMarker(pc, ConstantPool.JavaLangClassNotFoundExceptionConstantPoolName));
                    break;
                }
                case 7: {
                    this.exceptionMarkers.add(new ExceptionMarker(pc, ConstantPool.JavaLangNoSuchFieldErrorConstantPoolName));
                    break;
                }
                default: {
                    this.exceptionMarkers.add(new ExceptionMarker(pc, typeBinding.constantPoolName()));
                    break;
                }
            }
        }
    }
    
    public void addFramePosition(final int pc) {
        final Integer newEntry = pc;
        final FramePosition value;
        if ((value = this.framePositions.get(newEntry)) != null) {
            final FramePosition framePosition = value;
            ++framePosition.counter;
        }
        else {
            this.framePositions.put(newEntry, new FramePosition());
        }
    }
    
    @Override
    public void optimizeBranch(final int oldPosition, final BranchLabel lbl) {
        super.optimizeBranch(oldPosition, lbl);
        this.removeFramePosition(oldPosition);
    }
    
    public void removeFramePosition(final int pc) {
        final Integer entry = pc;
        final FramePosition value;
        if ((value = this.framePositions.get(entry)) != null) {
            final FramePosition framePosition = value;
            --framePosition.counter;
            if (value.counter <= 0) {
                this.framePositions.remove(entry);
            }
        }
    }
    
    @Override
    public void addVariable(final LocalVariableBinding localBinding) {
        if (localBinding.initializationPCs == null) {
            this.record(localBinding);
        }
        localBinding.recordInitializationStartPC(this.position);
    }
    
    private void addStackMarker(final int pc, final int destinationPC) {
        if (this.stackMarkers == null) {
            (this.stackMarkers = new ArrayList()).add(new StackMarker(pc, destinationPC));
        }
        else {
            final int size = this.stackMarkers.size();
            if (size == 0 || this.stackMarkers.get(size - 1).pc != this.position) {
                this.stackMarkers.add(new StackMarker(pc, destinationPC));
            }
        }
    }
    
    private void addStackDepthMarker(final int pc, final int delta, final TypeBinding typeBinding) {
        if (this.stackDepthMarkers == null) {
            (this.stackDepthMarkers = new ArrayList()).add(new StackDepthMarker(pc, delta, typeBinding));
        }
        else {
            final int size = this.stackDepthMarkers.size();
            if (size == 0) {
                this.stackDepthMarkers.add(new StackDepthMarker(pc, delta, typeBinding));
            }
            else {
                final StackDepthMarker stackDepthMarker = this.stackDepthMarkers.get(size - 1);
                if (stackDepthMarker.pc != this.position) {
                    this.stackDepthMarkers.add(new StackDepthMarker(pc, delta, typeBinding));
                }
                else {
                    this.stackDepthMarkers.set(size - 1, new StackDepthMarker(pc, delta, typeBinding));
                }
            }
        }
    }
    
    @Override
    public void decrStackSize(final int offset) {
        super.decrStackSize(offset);
        this.addStackDepthMarker(this.position, -1, null);
    }
    
    @Override
    public void recordExpressionType(final TypeBinding typeBinding) {
        this.addStackDepthMarker(this.position, 0, typeBinding);
    }
    
    @Override
    public void generateClassLiteralAccessForType(final TypeBinding accessedType, final FieldBinding syntheticFieldBinding) {
        if (accessedType.isBaseType() && accessedType != TypeBinding.NULL) {
            this.getTYPE(accessedType.id);
            return;
        }
        if (this.targetLevel >= 3211264L) {
            this.ldc(accessedType);
        }
        else {
            final BranchLabel endLabel = new BranchLabel(this);
            if (syntheticFieldBinding != null) {
                this.fieldAccess((byte)(-78), syntheticFieldBinding, null);
                this.dup();
                this.ifnonnull(endLabel);
                this.pop();
            }
            final ExceptionLabel classNotFoundExceptionHandler = new ExceptionLabel(this, TypeBinding.NULL);
            classNotFoundExceptionHandler.placeStart();
            this.ldc((accessedType == TypeBinding.NULL) ? "java.lang.Object" : String.valueOf(accessedType.constantPoolName()).replace('/', '.'));
            this.invokeClassForName();
            classNotFoundExceptionHandler.placeEnd();
            if (syntheticFieldBinding != null) {
                this.dup();
                this.fieldAccess((byte)(-77), syntheticFieldBinding, null);
            }
            final int fromPC = this.position;
            this.goto_(endLabel);
            final int savedStackDepth = this.stackDepth;
            this.pushExceptionOnStack(TypeBinding.NULL);
            classNotFoundExceptionHandler.place();
            this.newNoClassDefFoundError();
            this.dup_x1();
            this.swap();
            this.invokeThrowableGetMessage();
            this.invokeNoClassDefFoundErrorStringConstructor();
            this.athrow();
            endLabel.place();
            this.addStackMarker(fromPC, this.position);
            this.stackDepth = savedStackDepth;
        }
    }
    
    @Override
    public void generateOuterAccess(final Object[] mappingSequence, final ASTNode invocationSite, final Binding target, final Scope scope) {
        final int currentPosition = this.position;
        super.generateOuterAccess(mappingSequence, invocationSite, target, scope);
        if (currentPosition == this.position) {
            throw new AbortMethod(scope.referenceCompilationUnit().compilationResult, (CategorizedProblem)null);
        }
    }
    
    public ExceptionMarker[] getExceptionMarkers() {
        final Set exceptionMarkerSet = this.exceptionMarkers;
        if (this.exceptionMarkers == null) {
            return null;
        }
        final int size = exceptionMarkerSet.size();
        final ExceptionMarker[] markers = new ExceptionMarker[size];
        int n = 0;
        final Iterator iterator = exceptionMarkerSet.iterator();
        while (iterator.hasNext()) {
            markers[n++] = iterator.next();
        }
        Arrays.sort(markers);
        return markers;
    }
    
    public int[] getFramePositions() {
        final Set set = this.framePositions.keySet();
        final int size = set.size();
        final int[] positions = new int[size];
        int n = 0;
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            positions[n++] = iterator.next();
        }
        Arrays.sort(positions);
        return positions;
    }
    
    public StackDepthMarker[] getStackDepthMarkers() {
        if (this.stackDepthMarkers == null) {
            return null;
        }
        final int length = this.stackDepthMarkers.size();
        if (length == 0) {
            return null;
        }
        final StackDepthMarker[] result = new StackDepthMarker[length];
        this.stackDepthMarkers.toArray(result);
        return result;
    }
    
    public StackMarker[] getStackMarkers() {
        if (this.stackMarkers == null) {
            return null;
        }
        final int length = this.stackMarkers.size();
        if (length == 0) {
            return null;
        }
        final StackMarker[] result = new StackMarker[length];
        this.stackMarkers.toArray(result);
        return result;
    }
    
    public boolean hasFramePositions() {
        return this.framePositions.size() != 0;
    }
    
    @Override
    public void init(final ClassFile targetClassFile) {
        super.init(targetClassFile);
        this.stateIndexesCounter = 0;
        if (this.framePositions != null) {
            this.framePositions.clear();
        }
        if (this.exceptionMarkers != null) {
            this.exceptionMarkers.clear();
        }
        if (this.stackDepthMarkers != null) {
            this.stackDepthMarkers.clear();
        }
        if (this.stackMarkers != null) {
            this.stackMarkers.clear();
        }
    }
    
    @Override
    public void initializeMaxLocals(final MethodBinding methodBinding) {
        super.initializeMaxLocals(methodBinding);
        if (this.framePositions == null) {
            this.framePositions = new HashMap();
        }
        else {
            this.framePositions.clear();
        }
    }
    
    public void popStateIndex() {
        --this.stateIndexesCounter;
    }
    
    public void pushStateIndex(final int naturalExitMergeInitStateIndex) {
        if (this.stateIndexes == null) {
            this.stateIndexes = new int[3];
        }
        final int length = this.stateIndexes.length;
        if (length == this.stateIndexesCounter) {
            System.arraycopy(this.stateIndexes, 0, this.stateIndexes = new int[length * 2], 0, length);
        }
        this.stateIndexes[this.stateIndexesCounter++] = naturalExitMergeInitStateIndex;
    }
    
    @Override
    public void removeNotDefinitelyAssignedVariables(final Scope scope, final int initStateIndex) {
    Label_0106:
        for (int index = this.visibleLocalsCount, i = 0; i < index; ++i) {
            final LocalVariableBinding localBinding = this.visibleLocals[i];
            if (localBinding != null && localBinding.initializationCount > 0) {
                final boolean isDefinitelyAssigned = this.isDefinitelyAssigned(scope, initStateIndex, localBinding);
                if (!isDefinitelyAssigned) {
                    if (this.stateIndexes != null) {
                        for (int j = 0, max = this.stateIndexesCounter; j < max; ++j) {
                            if (this.isDefinitelyAssigned(scope, this.stateIndexes[j], localBinding)) {
                                continue Label_0106;
                            }
                        }
                    }
                    localBinding.recordInitializationEndPC(this.position);
                }
            }
        }
    }
    
    @Override
    public void reset(final ClassFile givenClassFile) {
        super.reset(givenClassFile);
        this.stateIndexesCounter = 0;
        if (this.framePositions != null) {
            this.framePositions.clear();
        }
        if (this.exceptionMarkers != null) {
            this.exceptionMarkers.clear();
        }
        if (this.stackDepthMarkers != null) {
            this.stackDepthMarkers.clear();
        }
        if (this.stackMarkers != null) {
            this.stackMarkers.clear();
        }
    }
    
    @Override
    protected void writePosition(final BranchLabel label) {
        super.writePosition(label);
        this.addFramePosition(label.position);
    }
    
    @Override
    protected void writePosition(final BranchLabel label, final int forwardReference) {
        super.writePosition(label, forwardReference);
        this.addFramePosition(label.position);
    }
    
    @Override
    protected void writeSignedWord(final int pos, final int value) {
        super.writeSignedWord(pos, value);
        this.addFramePosition(this.position);
    }
    
    @Override
    protected void writeWidePosition(final BranchLabel label) {
        super.writeWidePosition(label);
        this.addFramePosition(label.position);
    }
    
    @Override
    public void areturn() {
        super.areturn();
        this.addFramePosition(this.position);
    }
    
    @Override
    public void ireturn() {
        super.ireturn();
        this.addFramePosition(this.position);
    }
    
    @Override
    public void lreturn() {
        super.lreturn();
        this.addFramePosition(this.position);
    }
    
    @Override
    public void freturn() {
        super.freturn();
        this.addFramePosition(this.position);
    }
    
    @Override
    public void dreturn() {
        super.dreturn();
        this.addFramePosition(this.position);
    }
    
    @Override
    public void return_() {
        super.return_();
        this.addFramePosition(this.position);
    }
    
    @Override
    public void athrow() {
        super.athrow();
        this.addFramePosition(this.position);
    }
    
    @Override
    public void pushOnStack(final TypeBinding binding) {
        super.pushOnStack(binding);
        this.addStackDepthMarker(this.position, 1, binding);
    }
    
    @Override
    public void pushExceptionOnStack(final TypeBinding binding) {
        super.pushExceptionOnStack(binding);
        this.addExceptionMarker(this.position, binding);
    }
    
    @Override
    public void goto_(final BranchLabel label) {
        super.goto_(label);
        this.addFramePosition(this.position);
    }
    
    @Override
    public void goto_w(final BranchLabel label) {
        super.goto_w(label);
        this.addFramePosition(this.position);
    }
    
    @Override
    public void resetInWideMode() {
        this.resetSecretLocals();
        super.resetInWideMode();
    }
    
    @Override
    public void resetForCodeGenUnusedLocals() {
        this.resetSecretLocals();
        super.resetForCodeGenUnusedLocals();
    }
    
    public void resetSecretLocals() {
        for (int i = 0, max = this.locals.length; i < max; ++i) {
            final LocalVariableBinding localVariableBinding = this.locals[i];
            if (localVariableBinding != null && localVariableBinding.isSecret()) {
                localVariableBinding.resetInitializations();
            }
        }
    }
    
    public static class ExceptionMarker implements Comparable
    {
        public char[] constantPoolName;
        public int pc;
        
        public ExceptionMarker(final int pc, final char[] constantPoolName) {
            this.pc = pc;
            this.constantPoolName = constantPoolName;
        }
        
        @Override
        public int compareTo(final Object o) {
            if (o instanceof ExceptionMarker) {
                return this.pc - ((ExceptionMarker)o).pc;
            }
            return 0;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof ExceptionMarker) {
                final ExceptionMarker marker = (ExceptionMarker)obj;
                return this.pc == marker.pc && CharOperation.equals(this.constantPoolName, marker.constantPoolName);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return this.pc + this.constantPoolName.hashCode();
        }
        
        @Override
        public String toString() {
            final StringBuffer buffer = new StringBuffer();
            buffer.append('(').append(this.pc).append(',').append(this.constantPoolName).append(')');
            return String.valueOf(buffer);
        }
    }
    
    public static class StackDepthMarker
    {
        public int pc;
        public int delta;
        public TypeBinding typeBinding;
        
        public StackDepthMarker(final int pc, final int delta, final TypeBinding typeBinding) {
            this.pc = pc;
            this.typeBinding = typeBinding;
            this.delta = delta;
        }
        
        public StackDepthMarker(final int pc, final int delta) {
            this.pc = pc;
            this.delta = delta;
        }
        
        @Override
        public String toString() {
            final StringBuffer buffer = new StringBuffer();
            buffer.append('(').append(this.pc).append(',').append(this.delta);
            if (this.typeBinding != null) {
                buffer.append(',').append(this.typeBinding.qualifiedPackageName()).append(this.typeBinding.qualifiedSourceName());
            }
            buffer.append(')');
            return String.valueOf(buffer);
        }
    }
    
    public static class StackMarker
    {
        public int pc;
        public int destinationPC;
        public VerificationTypeInfo[] infos;
        
        public StackMarker(final int pc, final int destinationPC) {
            this.pc = pc;
            this.destinationPC = destinationPC;
        }
        
        public void setInfos(final VerificationTypeInfo[] infos) {
            this.infos = infos;
        }
        
        @Override
        public String toString() {
            final StringBuffer buffer = new StringBuffer();
            buffer.append("[copy stack items from ").append(this.pc).append(" to ").append(this.destinationPC);
            if (this.infos != null) {
                for (int i = 0, max = this.infos.length; i < max; ++i) {
                    if (i > 0) {
                        buffer.append(',');
                    }
                    buffer.append(this.infos[i]);
                }
            }
            buffer.append(']');
            return String.valueOf(buffer);
        }
    }
    
    static class FramePosition
    {
        int counter;
    }
}
