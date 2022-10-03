package org.eclipse.jdt.internal.compiler.codegen;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FunctionalExpression;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import java.util.Map;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;

public class CodeStream
{
    public static FieldBinding[] ImplicitThis;
    public static final int LABELS_INCREMENT = 5;
    public static final int LOCALS_INCREMENT = 10;
    public static final CompilationResult RESTART_IN_WIDE_MODE;
    public static final CompilationResult RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE;
    public int allLocalsCounter;
    public byte[] bCodeStream;
    public ClassFile classFile;
    public int classFileOffset;
    public ConstantPool constantPool;
    public int countLabels;
    public ExceptionLabel[] exceptionLabels;
    public int exceptionLabelsCounter;
    public int generateAttributes;
    static final int L_UNKNOWN = 0;
    static final int L_OPTIMIZABLE = 2;
    static final int L_CANNOT_OPTIMIZE = 4;
    public BranchLabel[] labels;
    public int lastEntryPC;
    public int lastAbruptCompletion;
    public int[] lineSeparatorPositions;
    public int lineNumberStart;
    public int lineNumberEnd;
    public LocalVariableBinding[] locals;
    public int maxFieldCount;
    public int maxLocals;
    public AbstractMethodDeclaration methodDeclaration;
    public LambdaExpression lambdaExpression;
    public int[] pcToSourceMap;
    public int pcToSourceMapSize;
    public int position;
    public boolean preserveUnusedLocals;
    public int stackDepth;
    public int stackMax;
    public int startingClassFileOffset;
    protected long targetLevel;
    public LocalVariableBinding[] visibleLocals;
    int visibleLocalsCount;
    public boolean wideMode;
    
    static {
        CodeStream.ImplicitThis = new FieldBinding[0];
        RESTART_IN_WIDE_MODE = new CompilationResult((char[])null, 0, 0, 0);
        RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE = new CompilationResult((char[])null, 0, 0, 0);
    }
    
    public CodeStream(final ClassFile givenClassFile) {
        this.exceptionLabels = new ExceptionLabel[5];
        this.labels = new BranchLabel[5];
        this.locals = new LocalVariableBinding[10];
        this.pcToSourceMap = new int[24];
        this.visibleLocals = new LocalVariableBinding[10];
        this.wideMode = false;
        this.targetLevel = givenClassFile.targetJDK;
        this.generateAttributes = givenClassFile.produceAttributes;
        if ((givenClassFile.produceAttributes & 0x2) != 0x0) {
            this.lineSeparatorPositions = givenClassFile.referenceBinding.scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions();
        }
    }
    
    public static int insertionIndex(final int[] pcToSourceMap, final int length, final int pc) {
        int g = 0;
        int d = length - 2;
        int m = 0;
        while (g <= d) {
            m = (g + d) / 2;
            if ((m & 0x1) != 0x0) {
                --m;
            }
            final int currentPC = pcToSourceMap[m];
            if (pc < currentPC) {
                d = m - 2;
            }
            else {
                if (pc <= currentPC) {
                    return -1;
                }
                g = m + 2;
            }
        }
        if (pc < pcToSourceMap[m]) {
            return m;
        }
        return m + 2;
    }
    
    public static final void sort(final int[] tab, final int lo0, final int hi0, final int[] result) {
        int lo = lo0;
        int hi = hi0;
        if (hi0 > lo0) {
            final int mid = tab[lo0 + (hi0 - lo0) / 2];
            while (lo <= hi) {
                while (lo < hi0) {
                    if (tab[lo] >= mid) {
                        break;
                    }
                    ++lo;
                }
                while (hi > lo0 && tab[hi] > mid) {
                    --hi;
                }
                if (lo <= hi) {
                    swap(tab, lo, hi, result);
                    ++lo;
                    --hi;
                }
            }
            if (lo0 < hi) {
                sort(tab, lo0, hi, result);
            }
            if (lo < hi0) {
                sort(tab, lo, hi0, result);
            }
        }
    }
    
    private static final void swap(final int[] a, final int i, final int j, final int[] result) {
        int T = a[i];
        a[i] = a[j];
        a[j] = T;
        T = result[j];
        result[j] = result[i];
        result[i] = T;
    }
    
    public void aaload() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 50;
    }
    
    public void aastore() {
        this.countLabels = 0;
        this.stackDepth -= 3;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 83;
    }
    
    public void aconst_null() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 1;
    }
    
    public void addDefinitelyAssignedVariables(final Scope scope, final int initStateIndex) {
        if ((this.generateAttributes & 0x1C) == 0x0) {
            return;
        }
        for (int i = 0; i < this.visibleLocalsCount; ++i) {
            final LocalVariableBinding localBinding = this.visibleLocals[i];
            if (localBinding != null && this.isDefinitelyAssigned(scope, initStateIndex, localBinding) && (localBinding.initializationCount == 0 || localBinding.initializationPCs[(localBinding.initializationCount - 1 << 1) + 1] != -1)) {
                localBinding.recordInitializationStartPC(this.position);
            }
        }
    }
    
    public void addLabel(final BranchLabel aLabel) {
        if (this.countLabels == this.labels.length) {
            System.arraycopy(this.labels, 0, this.labels = new BranchLabel[this.countLabels + 5], 0, this.countLabels);
        }
        this.labels[this.countLabels++] = aLabel;
    }
    
    public void addVariable(final LocalVariableBinding localBinding) {
    }
    
    public void addVisibleLocalVariable(final LocalVariableBinding localBinding) {
        if ((this.generateAttributes & 0x1C) == 0x0) {
            return;
        }
        if (this.visibleLocalsCount >= this.visibleLocals.length) {
            System.arraycopy(this.visibleLocals, 0, this.visibleLocals = new LocalVariableBinding[this.visibleLocalsCount * 2], 0, this.visibleLocalsCount);
        }
        this.visibleLocals[this.visibleLocalsCount++] = localBinding;
    }
    
    public void aload(final int iArg) {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals <= iArg) {
            this.maxLocals = iArg + 1;
        }
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 25;
            this.writeUnsignedShort(iArg);
        }
        else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 25;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }
    
    public void aload_0() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals == 0) {
            this.maxLocals = 1;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 42;
    }
    
    public void aload_1() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals <= 1) {
            this.maxLocals = 2;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 43;
    }
    
    public void aload_2() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals <= 2) {
            this.maxLocals = 3;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 44;
    }
    
    public void aload_3() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals <= 3) {
            this.maxLocals = 4;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 45;
    }
    
    public void anewarray(final TypeBinding typeBinding) {
        this.countLabels = 0;
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -67;
        this.writeUnsignedShort(this.constantPool.literalIndexForType(typeBinding));
    }
    
    public void areturn() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -80;
        this.lastAbruptCompletion = this.position;
    }
    
    public void arrayAt(final int typeBindingID) {
        switch (typeBindingID) {
            case 10: {
                this.iaload();
                break;
            }
            case 3:
            case 5: {
                this.baload();
                break;
            }
            case 4: {
                this.saload();
                break;
            }
            case 2: {
                this.caload();
                break;
            }
            case 7: {
                this.laload();
                break;
            }
            case 9: {
                this.faload();
                break;
            }
            case 8: {
                this.daload();
                break;
            }
            default: {
                this.aaload();
                break;
            }
        }
    }
    
    public void arrayAtPut(final int elementTypeID, final boolean valueRequired) {
        switch (elementTypeID) {
            case 10: {
                if (valueRequired) {
                    this.dup_x2();
                }
                this.iastore();
                break;
            }
            case 3:
            case 5: {
                if (valueRequired) {
                    this.dup_x2();
                }
                this.bastore();
                break;
            }
            case 4: {
                if (valueRequired) {
                    this.dup_x2();
                }
                this.sastore();
                break;
            }
            case 2: {
                if (valueRequired) {
                    this.dup_x2();
                }
                this.castore();
                break;
            }
            case 7: {
                if (valueRequired) {
                    this.dup2_x2();
                }
                this.lastore();
                break;
            }
            case 9: {
                if (valueRequired) {
                    this.dup_x2();
                }
                this.fastore();
                break;
            }
            case 8: {
                if (valueRequired) {
                    this.dup2_x2();
                }
                this.dastore();
                break;
            }
            default: {
                if (valueRequired) {
                    this.dup_x2();
                }
                this.aastore();
                break;
            }
        }
    }
    
    public void arraylength() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -66;
    }
    
    public void astore(final int iArg) {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.maxLocals <= iArg) {
            this.maxLocals = iArg + 1;
        }
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 58;
            this.writeUnsignedShort(iArg);
        }
        else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 58;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }
    
    public void astore_0() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.maxLocals == 0) {
            this.maxLocals = 1;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 75;
    }
    
    public void astore_1() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.maxLocals <= 1) {
            this.maxLocals = 2;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 76;
    }
    
    public void astore_2() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.maxLocals <= 2) {
            this.maxLocals = 3;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 77;
    }
    
    public void astore_3() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.maxLocals <= 3) {
            this.maxLocals = 4;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 78;
    }
    
    public void athrow() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -65;
        this.lastAbruptCompletion = this.position;
    }
    
    public void baload() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 51;
    }
    
    public void bastore() {
        this.countLabels = 0;
        this.stackDepth -= 3;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 84;
    }
    
    public void bipush(final byte b) {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 1 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        this.position += 2;
        this.bCodeStream[this.classFileOffset++] = 16;
        this.bCodeStream[this.classFileOffset++] = b;
    }
    
    public void caload() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 52;
    }
    
    public void castore() {
        this.countLabels = 0;
        this.stackDepth -= 3;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 85;
    }
    
    public void checkcast(final int baseId) {
        this.countLabels = 0;
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -64;
        switch (baseId) {
            case 3: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangByteConstantPoolName));
                break;
            }
            case 4: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangShortConstantPoolName));
                break;
            }
            case 2: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangCharacterConstantPoolName));
                break;
            }
            case 10: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangIntegerConstantPoolName));
                break;
            }
            case 7: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangLongConstantPoolName));
                break;
            }
            case 9: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangFloatConstantPoolName));
                break;
            }
            case 8: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangDoubleConstantPoolName));
                break;
            }
            case 5: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangBooleanConstantPoolName));
                break;
            }
        }
    }
    
    public void checkcast(final TypeBinding typeBinding) {
        this.checkcast(null, typeBinding, -1);
    }
    
    public void checkcast(final TypeReference typeReference, final TypeBinding typeBinding, final int currentPosition) {
        this.countLabels = 0;
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -64;
        this.writeUnsignedShort(this.constantPool.literalIndexForType(typeBinding));
    }
    
    public void d2f() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -112;
    }
    
    public void d2i() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -114;
    }
    
    public void d2l() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -113;
    }
    
    public void dadd() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 99;
    }
    
    public void daload() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 49;
    }
    
    public void dastore() {
        this.countLabels = 0;
        this.stackDepth -= 4;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 82;
    }
    
    public void dcmpg() {
        this.countLabels = 0;
        this.stackDepth -= 3;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -104;
    }
    
    public void dcmpl() {
        this.countLabels = 0;
        this.stackDepth -= 3;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -105;
    }
    
    public void dconst_0() {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 14;
    }
    
    public void dconst_1() {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 15;
    }
    
    public void ddiv() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 111;
    }
    
    public void decrStackSize(final int offset) {
        this.stackDepth -= offset;
    }
    
    public void dload(final int iArg) {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals < iArg + 2) {
            this.maxLocals = iArg + 2;
        }
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 24;
            this.writeUnsignedShort(iArg);
        }
        else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 24;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }
    
    public void dload_0() {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals < 2) {
            this.maxLocals = 2;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 38;
    }
    
    public void dload_1() {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals < 3) {
            this.maxLocals = 3;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 39;
    }
    
    public void dload_2() {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals < 4) {
            this.maxLocals = 4;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 40;
    }
    
    public void dload_3() {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.maxLocals < 5) {
            this.maxLocals = 5;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 41;
    }
    
    public void dmul() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 107;
    }
    
    public void dneg() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 119;
    }
    
    public void drem() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 115;
    }
    
    public void dreturn() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -81;
        this.lastAbruptCompletion = this.position;
    }
    
    public void dstore(final int iArg) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.maxLocals <= iArg + 1) {
            this.maxLocals = iArg + 2;
        }
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 57;
            this.writeUnsignedShort(iArg);
        }
        else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 57;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }
    
    public void dstore_0() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.maxLocals < 2) {
            this.maxLocals = 2;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 71;
    }
    
    public void dstore_1() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.maxLocals < 3) {
            this.maxLocals = 3;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 72;
    }
    
    public void dstore_2() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.maxLocals < 4) {
            this.maxLocals = 4;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 73;
    }
    
    public void dstore_3() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.maxLocals < 5) {
            this.maxLocals = 5;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 74;
    }
    
    public void dsub() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 103;
    }
    
    public void dup() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 89;
    }
    
    public void dup_x1() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 90;
    }
    
    public void dup_x2() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 91;
    }
    
    public void dup2() {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 92;
    }
    
    public void dup2_x1() {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 93;
    }
    
    public void dup2_x2() {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 94;
    }
    
    public void exitUserScope(final BlockScope currentScope) {
        if ((this.generateAttributes & 0x1C) == 0x0) {
            return;
        }
        int index = this.visibleLocalsCount - 1;
        while (index >= 0) {
            final LocalVariableBinding visibleLocal = this.visibleLocals[index];
            if (visibleLocal == null || visibleLocal.declaringScope != currentScope) {
                --index;
            }
            else {
                if (visibleLocal.initializationCount > 0) {
                    visibleLocal.recordInitializationEndPC(this.position);
                }
                this.visibleLocals[index--] = null;
            }
        }
    }
    
    public void exitUserScope(final BlockScope currentScope, final LocalVariableBinding binding) {
        if ((this.generateAttributes & 0x1C) == 0x0) {
            return;
        }
        int index = this.visibleLocalsCount - 1;
        while (index >= 0) {
            final LocalVariableBinding visibleLocal = this.visibleLocals[index];
            if (visibleLocal == null || visibleLocal.declaringScope != currentScope || visibleLocal == binding) {
                --index;
            }
            else {
                if (visibleLocal.initializationCount > 0) {
                    visibleLocal.recordInitializationEndPC(this.position);
                }
                this.visibleLocals[index--] = null;
            }
        }
    }
    
    public void f2d() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -115;
    }
    
    public void f2i() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -117;
    }
    
    public void f2l() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -116;
    }
    
    public void fadd() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 98;
    }
    
    public void faload() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 48;
    }
    
    public void fastore() {
        this.countLabels = 0;
        this.stackDepth -= 3;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 81;
    }
    
    public void fcmpg() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -106;
    }
    
    public void fcmpl() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -107;
    }
    
    public void fconst_0() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 11;
    }
    
    public void fconst_1() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 12;
    }
    
    public void fconst_2() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 13;
    }
    
    public void fdiv() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 110;
    }
    
    public void fieldAccess(final byte opcode, final FieldBinding fieldBinding, TypeBinding declaringClass) {
        if (declaringClass == null) {
            declaringClass = fieldBinding.declaringClass;
        }
        if ((declaringClass.tagBits & 0x800L) != 0x0L) {
            Util.recordNestedType(this.classFile, declaringClass);
        }
        final TypeBinding returnType = fieldBinding.type;
        int returnTypeSize = 0;
        switch (returnType.id) {
            case 7:
            case 8: {
                returnTypeSize = 2;
                break;
            }
            default: {
                returnTypeSize = 1;
                break;
            }
        }
        this.fieldAccess(opcode, returnTypeSize, declaringClass.constantPoolName(), fieldBinding.name, returnType.signature());
    }
    
    private void fieldAccess(final byte opcode, final int returnTypeSize, final char[] declaringClass, final char[] fieldName, final char[] signature) {
        this.countLabels = 0;
        switch (opcode) {
            case -76: {
                if (returnTypeSize == 2) {
                    ++this.stackDepth;
                    break;
                }
                break;
            }
            case -78: {
                if (returnTypeSize == 2) {
                    this.stackDepth += 2;
                    break;
                }
                ++this.stackDepth;
                break;
            }
            case -75: {
                if (returnTypeSize == 2) {
                    this.stackDepth -= 3;
                    break;
                }
                this.stackDepth -= 2;
                break;
            }
            case -77: {
                if (returnTypeSize == 2) {
                    this.stackDepth -= 2;
                    break;
                }
                --this.stackDepth;
                break;
            }
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = opcode;
        this.writeUnsignedShort(this.constantPool.literalIndexForField(declaringClass, fieldName, signature));
    }
    
    public void fload(final int iArg) {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.maxLocals <= iArg) {
            this.maxLocals = iArg + 1;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 23;
            this.writeUnsignedShort(iArg);
        }
        else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 23;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }
    
    public void fload_0() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.maxLocals == 0) {
            this.maxLocals = 1;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 34;
    }
    
    public void fload_1() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.maxLocals <= 1) {
            this.maxLocals = 2;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 35;
    }
    
    public void fload_2() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.maxLocals <= 2) {
            this.maxLocals = 3;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 36;
    }
    
    public void fload_3() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.maxLocals <= 3) {
            this.maxLocals = 4;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 37;
    }
    
    public void fmul() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 106;
    }
    
    public void fneg() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 118;
    }
    
    public void frem() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 114;
    }
    
    public void freturn() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -82;
        this.lastAbruptCompletion = this.position;
    }
    
    public void fstore(final int iArg) {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.maxLocals <= iArg) {
            this.maxLocals = iArg + 1;
        }
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 56;
            this.writeUnsignedShort(iArg);
        }
        else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 56;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }
    
    public void fstore_0() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.maxLocals == 0) {
            this.maxLocals = 1;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 67;
    }
    
    public void fstore_1() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.maxLocals <= 1) {
            this.maxLocals = 2;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 68;
    }
    
    public void fstore_2() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.maxLocals <= 2) {
            this.maxLocals = 3;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 69;
    }
    
    public void fstore_3() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.maxLocals <= 3) {
            this.maxLocals = 4;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 70;
    }
    
    public void fsub() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 102;
    }
    
    public void generateBoxingConversion(final int unboxedTypeID) {
        switch (unboxedTypeID) {
            case 3: {
                if (this.targetLevel >= 3211264L) {
                    this.invoke((byte)(-72), 1, 1, ConstantPool.JavaLangByteConstantPoolName, ConstantPool.ValueOf, ConstantPool.byteByteSignature);
                    break;
                }
                this.newWrapperFor(unboxedTypeID);
                this.dup_x1();
                this.swap();
                this.invoke((byte)(-73), 2, 0, ConstantPool.JavaLangByteConstantPoolName, ConstantPool.Init, ConstantPool.ByteConstrSignature);
                break;
            }
            case 4: {
                if (this.targetLevel >= 3211264L) {
                    this.invoke((byte)(-72), 1, 1, ConstantPool.JavaLangShortConstantPoolName, ConstantPool.ValueOf, ConstantPool.shortShortSignature);
                    break;
                }
                this.newWrapperFor(unboxedTypeID);
                this.dup_x1();
                this.swap();
                this.invoke((byte)(-73), 2, 0, ConstantPool.JavaLangShortConstantPoolName, ConstantPool.Init, ConstantPool.ShortConstrSignature);
                break;
            }
            case 2: {
                if (this.targetLevel >= 3211264L) {
                    this.invoke((byte)(-72), 1, 1, ConstantPool.JavaLangCharacterConstantPoolName, ConstantPool.ValueOf, ConstantPool.charCharacterSignature);
                    break;
                }
                this.newWrapperFor(unboxedTypeID);
                this.dup_x1();
                this.swap();
                this.invoke((byte)(-73), 2, 0, ConstantPool.JavaLangCharacterConstantPoolName, ConstantPool.Init, ConstantPool.CharConstrSignature);
                break;
            }
            case 10: {
                if (this.targetLevel >= 3211264L) {
                    this.invoke((byte)(-72), 1, 1, ConstantPool.JavaLangIntegerConstantPoolName, ConstantPool.ValueOf, ConstantPool.IntIntegerSignature);
                    break;
                }
                this.newWrapperFor(unboxedTypeID);
                this.dup_x1();
                this.swap();
                this.invoke((byte)(-73), 2, 0, ConstantPool.JavaLangIntegerConstantPoolName, ConstantPool.Init, ConstantPool.IntConstrSignature);
                break;
            }
            case 7: {
                if (this.targetLevel >= 3211264L) {
                    this.invoke((byte)(-72), 2, 1, ConstantPool.JavaLangLongConstantPoolName, ConstantPool.ValueOf, ConstantPool.longLongSignature);
                    break;
                }
                this.newWrapperFor(unboxedTypeID);
                this.dup_x2();
                this.dup_x2();
                this.pop();
                this.invoke((byte)(-73), 3, 0, ConstantPool.JavaLangLongConstantPoolName, ConstantPool.Init, ConstantPool.LongConstrSignature);
                break;
            }
            case 9: {
                if (this.targetLevel >= 3211264L) {
                    this.invoke((byte)(-72), 1, 1, ConstantPool.JavaLangFloatConstantPoolName, ConstantPool.ValueOf, ConstantPool.floatFloatSignature);
                    break;
                }
                this.newWrapperFor(unboxedTypeID);
                this.dup_x1();
                this.swap();
                this.invoke((byte)(-73), 2, 0, ConstantPool.JavaLangFloatConstantPoolName, ConstantPool.Init, ConstantPool.FloatConstrSignature);
                break;
            }
            case 8: {
                if (this.targetLevel >= 3211264L) {
                    this.invoke((byte)(-72), 2, 1, ConstantPool.JavaLangDoubleConstantPoolName, ConstantPool.ValueOf, ConstantPool.doubleDoubleSignature);
                    break;
                }
                this.newWrapperFor(unboxedTypeID);
                this.dup_x2();
                this.dup_x2();
                this.pop();
                this.invoke((byte)(-73), 3, 0, ConstantPool.JavaLangDoubleConstantPoolName, ConstantPool.Init, ConstantPool.DoubleConstrSignature);
                break;
            }
            case 5: {
                if (this.targetLevel >= 3211264L) {
                    this.invoke((byte)(-72), 1, 1, ConstantPool.JavaLangBooleanConstantPoolName, ConstantPool.ValueOf, ConstantPool.booleanBooleanSignature);
                    break;
                }
                this.newWrapperFor(unboxedTypeID);
                this.dup_x1();
                this.swap();
                this.invoke((byte)(-73), 2, 0, ConstantPool.JavaLangBooleanConstantPoolName, ConstantPool.Init, ConstantPool.BooleanConstrSignature);
                break;
            }
        }
    }
    
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
            this.stackDepth = savedStackDepth;
        }
    }
    
    public final void generateCodeAttributeForProblemMethod(final String problemMessage) {
        this.newJavaLangError();
        this.dup();
        this.ldc(problemMessage);
        this.invokeJavaLangErrorConstructor();
        this.athrow();
    }
    
    public void generateConstant(final Constant constant, final int implicitConversionCode) {
        int targetTypeID = (implicitConversionCode & 0xFF) >> 4;
        if (targetTypeID == 0) {
            targetTypeID = constant.typeID();
        }
        switch (targetTypeID) {
            case 5: {
                this.generateInlinedValue(constant.booleanValue());
                break;
            }
            case 2: {
                this.generateInlinedValue(constant.charValue());
                break;
            }
            case 3: {
                this.generateInlinedValue(constant.byteValue());
                break;
            }
            case 4: {
                this.generateInlinedValue(constant.shortValue());
                break;
            }
            case 10: {
                this.generateInlinedValue(constant.intValue());
                break;
            }
            case 7: {
                this.generateInlinedValue(constant.longValue());
                break;
            }
            case 9: {
                this.generateInlinedValue(constant.floatValue());
                break;
            }
            case 8: {
                this.generateInlinedValue(constant.doubleValue());
                break;
            }
            case 11: {
                this.ldc(constant.stringValue());
                break;
            }
        }
        if ((implicitConversionCode & 0x200) != 0x0) {
            this.generateBoxingConversion(targetTypeID);
        }
    }
    
    public void generateEmulatedReadAccessForField(final FieldBinding fieldBinding) {
        this.generateEmulationForField(fieldBinding);
        this.swap();
        this.invokeJavaLangReflectFieldGetter(fieldBinding.type.id);
        if (!fieldBinding.type.isBaseType()) {
            this.checkcast(fieldBinding.type);
        }
    }
    
    public void generateEmulatedWriteAccessForField(final FieldBinding fieldBinding) {
        this.invokeJavaLangReflectFieldSetter(fieldBinding.type.id);
    }
    
    public void generateEmulationForConstructor(final Scope scope, final MethodBinding methodBinding) {
        this.ldc(String.valueOf(methodBinding.declaringClass.constantPoolName()).replace('/', '.'));
        this.invokeClassForName();
        final int paramLength = methodBinding.parameters.length;
        this.generateInlinedValue(paramLength);
        this.newArray(scope.createArrayType(scope.getType(TypeConstants.JAVA_LANG_CLASS, 3), 1));
        if (paramLength > 0) {
            this.dup();
            for (int i = 0; i < paramLength; ++i) {
                this.generateInlinedValue(i);
                final TypeBinding parameter = methodBinding.parameters[i];
                if (parameter.isBaseType()) {
                    this.getTYPE(parameter.id);
                }
                else if (parameter.isArrayType()) {
                    final ArrayBinding array = (ArrayBinding)parameter;
                    if (array.leafComponentType.isBaseType()) {
                        this.getTYPE(array.leafComponentType.id);
                    }
                    else {
                        this.ldc(String.valueOf(array.leafComponentType.constantPoolName()).replace('/', '.'));
                        this.invokeClassForName();
                    }
                    final int dimensions = array.dimensions;
                    this.generateInlinedValue(dimensions);
                    this.newarray(10);
                    this.invokeArrayNewInstance();
                    this.invokeObjectGetClass();
                }
                else {
                    this.ldc(String.valueOf(methodBinding.declaringClass.constantPoolName()).replace('/', '.'));
                    this.invokeClassForName();
                }
                this.aastore();
                if (i < paramLength - 1) {
                    this.dup();
                }
            }
        }
        this.invokeClassGetDeclaredConstructor();
        this.dup();
        this.iconst_1();
        this.invokeAccessibleObjectSetAccessible();
    }
    
    public void generateEmulationForField(final FieldBinding fieldBinding) {
        this.ldc(String.valueOf(fieldBinding.declaringClass.constantPoolName()).replace('/', '.'));
        this.invokeClassForName();
        this.ldc(String.valueOf(fieldBinding.name));
        this.invokeClassGetDeclaredField();
        this.dup();
        this.iconst_1();
        this.invokeAccessibleObjectSetAccessible();
    }
    
    public void generateEmulationForMethod(final Scope scope, final MethodBinding methodBinding) {
        this.ldc(String.valueOf(methodBinding.declaringClass.constantPoolName()).replace('/', '.'));
        this.invokeClassForName();
        this.ldc(String.valueOf(methodBinding.selector));
        final int paramLength = methodBinding.parameters.length;
        this.generateInlinedValue(paramLength);
        this.newArray(scope.createArrayType(scope.getType(TypeConstants.JAVA_LANG_CLASS, 3), 1));
        if (paramLength > 0) {
            this.dup();
            for (int i = 0; i < paramLength; ++i) {
                this.generateInlinedValue(i);
                final TypeBinding parameter = methodBinding.parameters[i];
                if (parameter.isBaseType()) {
                    this.getTYPE(parameter.id);
                }
                else if (parameter.isArrayType()) {
                    final ArrayBinding array = (ArrayBinding)parameter;
                    if (array.leafComponentType.isBaseType()) {
                        this.getTYPE(array.leafComponentType.id);
                    }
                    else {
                        this.ldc(String.valueOf(array.leafComponentType.constantPoolName()).replace('/', '.'));
                        this.invokeClassForName();
                    }
                    final int dimensions = array.dimensions;
                    this.generateInlinedValue(dimensions);
                    this.newarray(10);
                    this.invokeArrayNewInstance();
                    this.invokeObjectGetClass();
                }
                else {
                    this.ldc(String.valueOf(methodBinding.declaringClass.constantPoolName()).replace('/', '.'));
                    this.invokeClassForName();
                }
                this.aastore();
                if (i < paramLength - 1) {
                    this.dup();
                }
            }
        }
        this.invokeClassGetDeclaredMethod();
        this.dup();
        this.iconst_1();
        this.invokeAccessibleObjectSetAccessible();
    }
    
    public void generateImplicitConversion(final int implicitConversionCode) {
        if ((implicitConversionCode & 0x400) != 0x0) {
            final int typeId = implicitConversionCode & 0xF;
            this.generateUnboxingConversion(typeId);
        }
        switch (implicitConversionCode & 0xFF) {
            case 41: {
                this.f2i();
                this.i2c();
                break;
            }
            case 40: {
                this.d2i();
                this.i2c();
                break;
            }
            case 35:
            case 36:
            case 42: {
                this.i2c();
                break;
            }
            case 39: {
                this.l2i();
                this.i2c();
                break;
            }
            case 146:
            case 147:
            case 148:
            case 154: {
                this.i2f();
                break;
            }
            case 152: {
                this.d2f();
                break;
            }
            case 151: {
                this.l2f();
                break;
            }
            case 57: {
                this.f2i();
                this.i2b();
                break;
            }
            case 56: {
                this.d2i();
                this.i2b();
                break;
            }
            case 50:
            case 52:
            case 58: {
                this.i2b();
                break;
            }
            case 55: {
                this.l2i();
                this.i2b();
                break;
            }
            case 130:
            case 131:
            case 132:
            case 138: {
                this.i2d();
                break;
            }
            case 137: {
                this.f2d();
                break;
            }
            case 135: {
                this.l2d();
                break;
            }
            case 66:
            case 67:
            case 74: {
                this.i2s();
                break;
            }
            case 72: {
                this.d2i();
                this.i2s();
                break;
            }
            case 71: {
                this.l2i();
                this.i2s();
                break;
            }
            case 73: {
                this.f2i();
                this.i2s();
                break;
            }
            case 168: {
                this.d2i();
                break;
            }
            case 169: {
                this.f2i();
                break;
            }
            case 167: {
                this.l2i();
                break;
            }
            case 114:
            case 115:
            case 116:
            case 122: {
                this.i2l();
                break;
            }
            case 120: {
                this.d2l();
                break;
            }
            case 121: {
                this.f2l();
                break;
            }
            case 33:
            case 49:
            case 65:
            case 81:
            case 113:
            case 129:
            case 145:
            case 161: {
                final int runtimeType = (implicitConversionCode & 0xFF) >> 4;
                this.checkcast(runtimeType);
                this.generateUnboxingConversion(runtimeType);
                break;
            }
        }
        if ((implicitConversionCode & 0x200) != 0x0) {
            final int typeId = (implicitConversionCode & 0xFF) >> 4;
            this.generateBoxingConversion(typeId);
        }
    }
    
    public void generateInlinedValue(final boolean inlinedValue) {
        if (inlinedValue) {
            this.iconst_1();
        }
        else {
            this.iconst_0();
        }
    }
    
    public void generateInlinedValue(final byte inlinedValue) {
        switch (inlinedValue) {
            case -1: {
                this.iconst_m1();
                break;
            }
            case 0: {
                this.iconst_0();
                break;
            }
            case 1: {
                this.iconst_1();
                break;
            }
            case 2: {
                this.iconst_2();
                break;
            }
            case 3: {
                this.iconst_3();
                break;
            }
            case 4: {
                this.iconst_4();
                break;
            }
            case 5: {
                this.iconst_5();
                break;
            }
            default: {
                if (-128 <= inlinedValue && inlinedValue <= 127) {
                    this.bipush(inlinedValue);
                    return;
                }
                break;
            }
        }
    }
    
    public void generateInlinedValue(final char inlinedValue) {
        switch (inlinedValue) {
            case '\0': {
                this.iconst_0();
                break;
            }
            case '\u0001': {
                this.iconst_1();
                break;
            }
            case '\u0002': {
                this.iconst_2();
                break;
            }
            case '\u0003': {
                this.iconst_3();
                break;
            }
            case '\u0004': {
                this.iconst_4();
                break;
            }
            case '\u0005': {
                this.iconst_5();
                break;
            }
            default: {
                if ('\u0006' <= inlinedValue && inlinedValue <= '\u007f') {
                    this.bipush((byte)inlinedValue);
                    return;
                }
                if ('\u0080' <= inlinedValue && inlinedValue <= '\u7fff') {
                    this.sipush(inlinedValue);
                    return;
                }
                this.ldc(inlinedValue);
                break;
            }
        }
    }
    
    public void generateInlinedValue(final double inlinedValue) {
        if (inlinedValue == 0.0) {
            if (Double.doubleToLongBits(inlinedValue) != 0L) {
                this.ldc2_w(inlinedValue);
            }
            else {
                this.dconst_0();
            }
            return;
        }
        if (inlinedValue == 1.0) {
            this.dconst_1();
            return;
        }
        this.ldc2_w(inlinedValue);
    }
    
    public void generateInlinedValue(final float inlinedValue) {
        if (inlinedValue == 0.0f) {
            if (Float.floatToIntBits(inlinedValue) != 0) {
                this.ldc(inlinedValue);
            }
            else {
                this.fconst_0();
            }
            return;
        }
        if (inlinedValue == 1.0f) {
            this.fconst_1();
            return;
        }
        if (inlinedValue == 2.0f) {
            this.fconst_2();
            return;
        }
        this.ldc(inlinedValue);
    }
    
    public void generateInlinedValue(final int inlinedValue) {
        switch (inlinedValue) {
            case -1: {
                this.iconst_m1();
                break;
            }
            case 0: {
                this.iconst_0();
                break;
            }
            case 1: {
                this.iconst_1();
                break;
            }
            case 2: {
                this.iconst_2();
                break;
            }
            case 3: {
                this.iconst_3();
                break;
            }
            case 4: {
                this.iconst_4();
                break;
            }
            case 5: {
                this.iconst_5();
                break;
            }
            default: {
                if (-128 <= inlinedValue && inlinedValue <= 127) {
                    this.bipush((byte)inlinedValue);
                    return;
                }
                if (-32768 <= inlinedValue && inlinedValue <= 32767) {
                    this.sipush(inlinedValue);
                    return;
                }
                this.ldc(inlinedValue);
                break;
            }
        }
    }
    
    public void generateInlinedValue(final long inlinedValue) {
        if (inlinedValue == 0L) {
            this.lconst_0();
            return;
        }
        if (inlinedValue == 1L) {
            this.lconst_1();
            return;
        }
        this.ldc2_w(inlinedValue);
    }
    
    public void generateInlinedValue(final short inlinedValue) {
        switch (inlinedValue) {
            case -1: {
                this.iconst_m1();
                break;
            }
            case 0: {
                this.iconst_0();
                break;
            }
            case 1: {
                this.iconst_1();
                break;
            }
            case 2: {
                this.iconst_2();
                break;
            }
            case 3: {
                this.iconst_3();
                break;
            }
            case 4: {
                this.iconst_4();
                break;
            }
            case 5: {
                this.iconst_5();
                break;
            }
            default: {
                if (-128 <= inlinedValue && inlinedValue <= 127) {
                    this.bipush((byte)inlinedValue);
                    return;
                }
                this.sipush(inlinedValue);
                break;
            }
        }
    }
    
    public void generateOuterAccess(final Object[] mappingSequence, final ASTNode invocationSite, final Binding target, final Scope scope) {
        if (mappingSequence == null) {
            if (target instanceof LocalVariableBinding) {
                scope.problemReporter().needImplementation(invocationSite);
            }
            else {
                scope.problemReporter().noSuchEnclosingInstance((TypeBinding)target, invocationSite, false);
            }
            return;
        }
        if (mappingSequence == BlockScope.NoEnclosingInstanceInConstructorCall) {
            scope.problemReporter().noSuchEnclosingInstance((TypeBinding)target, invocationSite, true);
            return;
        }
        if (mappingSequence == BlockScope.NoEnclosingInstanceInStaticContext) {
            scope.problemReporter().noSuchEnclosingInstance((TypeBinding)target, invocationSite, false);
            return;
        }
        if (mappingSequence == BlockScope.EmulationPathToImplicitThis) {
            this.aload_0();
            return;
        }
        if (mappingSequence[0] instanceof FieldBinding) {
            final FieldBinding fieldBinding = (FieldBinding)mappingSequence[0];
            this.aload_0();
            this.fieldAccess((byte)(-76), fieldBinding, null);
        }
        else {
            this.load((LocalVariableBinding)mappingSequence[0]);
        }
        for (int i = 1, length = mappingSequence.length; i < length; ++i) {
            if (mappingSequence[i] instanceof FieldBinding) {
                final FieldBinding fieldBinding2 = (FieldBinding)mappingSequence[i];
                this.fieldAccess((byte)(-76), fieldBinding2, null);
            }
            else {
                this.invoke((byte)(-72), (MethodBinding)mappingSequence[i], null);
            }
        }
    }
    
    public void generateReturnBytecode(final Expression expression) {
        if (expression == null) {
            this.return_();
        }
        else {
            final int implicitConversion = expression.implicitConversion;
            if ((implicitConversion & 0x200) != 0x0) {
                this.areturn();
                return;
            }
            final int runtimeType = (implicitConversion & 0xFF) >> 4;
            switch (runtimeType) {
                case 5:
                case 10: {
                    this.ireturn();
                    break;
                }
                case 9: {
                    this.freturn();
                    break;
                }
                case 7: {
                    this.lreturn();
                    break;
                }
                case 8: {
                    this.dreturn();
                    break;
                }
                default: {
                    this.areturn();
                    break;
                }
            }
        }
    }
    
    public void generateStringConcatenationAppend(final BlockScope blockScope, final Expression oper1, final Expression oper2) {
        if (oper1 == null) {
            this.newStringContatenation();
            this.dup_x1();
            this.swap();
            this.invokeStringValueOf(1);
            this.invokeStringConcatenationStringConstructor();
        }
        else {
            final int pc = this.position;
            oper1.generateOptimizedStringConcatenationCreation(blockScope, this, oper1.implicitConversion & 0xF);
            this.recordPositionsFrom(pc, oper1.sourceStart);
        }
        final int pc = this.position;
        oper2.generateOptimizedStringConcatenation(blockScope, this, oper2.implicitConversion & 0xF);
        this.recordPositionsFrom(pc, oper2.sourceStart);
        this.invokeStringConcatenationToString();
    }
    
    public void generateSyntheticBodyForConstructorAccess(final SyntheticMethodBinding accessBinding) {
        this.initializeMaxLocals(accessBinding);
        final MethodBinding constructorBinding = accessBinding.targetMethod;
        final TypeBinding[] parameters = constructorBinding.parameters;
        final int length = parameters.length;
        int resolvedPosition = 1;
        this.aload_0();
        final TypeBinding declaringClass = constructorBinding.declaringClass;
        if (declaringClass.erasure().id == 41 || declaringClass.isEnum()) {
            this.aload_1();
            this.iload_2();
            resolvedPosition += 2;
        }
        if (declaringClass.isNestedType()) {
            final NestedTypeBinding nestedType = (NestedTypeBinding)declaringClass;
            final SyntheticArgumentBinding[] syntheticArguments = nestedType.syntheticEnclosingInstances();
            for (int i = 0; i < ((syntheticArguments == null) ? 0 : syntheticArguments.length); ++i) {
                final TypeBinding type;
                this.load(type = syntheticArguments[i].type, resolvedPosition);
                switch (type.id) {
                    case 7:
                    case 8: {
                        resolvedPosition += 2;
                        break;
                    }
                    default: {
                        ++resolvedPosition;
                        break;
                    }
                }
            }
        }
        for (int j = 0; j < length; ++j) {
            final TypeBinding parameter;
            this.load(parameter = parameters[j], resolvedPosition);
            switch (parameter.id) {
                case 7:
                case 8: {
                    resolvedPosition += 2;
                    break;
                }
                default: {
                    ++resolvedPosition;
                    break;
                }
            }
        }
        if (declaringClass.isNestedType()) {
            final NestedTypeBinding nestedType = (NestedTypeBinding)declaringClass;
            final SyntheticArgumentBinding[] syntheticArguments = nestedType.syntheticOuterLocalVariables();
            for (int i = 0; i < ((syntheticArguments == null) ? 0 : syntheticArguments.length); ++i) {
                final TypeBinding type;
                this.load(type = syntheticArguments[i].type, resolvedPosition);
                switch (type.id) {
                    case 7:
                    case 8: {
                        resolvedPosition += 2;
                        break;
                    }
                    default: {
                        ++resolvedPosition;
                        break;
                    }
                }
            }
        }
        this.invoke((byte)(-73), constructorBinding, null);
        this.return_();
    }
    
    public void generateSyntheticBodyForArrayConstructor(final SyntheticMethodBinding methodBinding) {
        this.initializeMaxLocals(methodBinding);
        this.iload_0();
        this.newArray(null, null, (ArrayBinding)methodBinding.returnType);
        this.areturn();
    }
    
    public void generateSyntheticBodyForArrayClone(final SyntheticMethodBinding methodBinding) {
        this.initializeMaxLocals(methodBinding);
        final TypeBinding arrayType = methodBinding.parameters[0];
        this.aload_0();
        this.invoke((byte)(-74), 1, 1, arrayType.signature(), ConstantPool.Clone, ConstantPool.CloneSignature);
        this.checkcast(arrayType);
        this.areturn();
    }
    
    public void generateSyntheticBodyForFactoryMethod(final SyntheticMethodBinding methodBinding) {
        this.initializeMaxLocals(methodBinding);
        final MethodBinding constructorBinding = methodBinding.targetMethod;
        final TypeBinding[] parameters = methodBinding.parameters;
        final int length = parameters.length;
        this.new_(constructorBinding.declaringClass);
        this.dup();
        int resolvedPosition = 0;
        for (int i = 0; i < length; ++i) {
            final TypeBinding parameter;
            this.load(parameter = parameters[i], resolvedPosition);
            switch (parameter.id) {
                case 7:
                case 8: {
                    resolvedPosition += 2;
                    break;
                }
                default: {
                    ++resolvedPosition;
                    break;
                }
            }
        }
        for (int i = 0; i < methodBinding.fakePaddedParameters; ++i) {
            this.aconst_null();
        }
        this.invoke((byte)(-73), constructorBinding, null);
        this.areturn();
    }
    
    public void generateSyntheticBodyForEnumValueOf(final SyntheticMethodBinding methodBinding) {
        this.initializeMaxLocals(methodBinding);
        final ReferenceBinding declaringClass = methodBinding.declaringClass;
        this.generateClassLiteralAccessForType(declaringClass, null);
        this.aload_0();
        this.invokeJavaLangEnumvalueOf(declaringClass);
        this.checkcast(declaringClass);
        this.areturn();
    }
    
    public void generateSyntheticBodyForDeserializeLambda(final SyntheticMethodBinding methodBinding, final SyntheticMethodBinding[] syntheticMethodBindings) {
        this.initializeMaxLocals(methodBinding);
        final Map hashcodesTosynthetics = new LinkedHashMap();
        for (int i = 0, max = syntheticMethodBindings.length; i < max; ++i) {
            final SyntheticMethodBinding syntheticMethodBinding = syntheticMethodBindings[i];
            if ((syntheticMethodBinding.lambda != null && syntheticMethodBinding.lambda.isSerializable) || syntheticMethodBinding.serializableMethodRef != null) {
                final Integer hashcode = new String(syntheticMethodBinding.selector).hashCode();
                List syntheticssForThisHashcode = hashcodesTosynthetics.get(hashcode);
                if (syntheticssForThisHashcode == null) {
                    syntheticssForThisHashcode = new ArrayList();
                    hashcodesTosynthetics.put(hashcode, syntheticssForThisHashcode);
                }
                syntheticssForThisHashcode.add(syntheticMethodBinding);
            }
        }
        final ClassScope scope = ((SourceTypeBinding)methodBinding.declaringClass).scope;
        this.aload_0();
        this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangInvokeSerializedLambdaConstantPoolName, ConstantPool.GetImplMethodName, ConstantPool.GetImplMethodNameSignature);
        this.astore_1();
        final LocalVariableBinding lvb1 = new LocalVariableBinding("hashcode".toCharArray(), scope.getJavaLangString(), 0, false);
        lvb1.resolvedPosition = 1;
        this.addVariable(lvb1);
        this.iconst_m1();
        this.istore_2();
        final LocalVariableBinding lvb2 = new LocalVariableBinding("id".toCharArray(), TypeBinding.INT, 0, false);
        lvb2.resolvedPosition = 2;
        this.addVariable(lvb2);
        this.aload_1();
        this.invokeStringHashCode();
        final BranchLabel label = new BranchLabel(this);
        CaseLabel defaultLabel = new CaseLabel(this);
        final int numberOfHashcodes = hashcodesTosynthetics.size();
        CaseLabel[] switchLabels = new CaseLabel[numberOfHashcodes];
        int[] keys = new int[numberOfHashcodes];
        int[] sortedIndexes = new int[numberOfHashcodes];
        final Set hashcodes = hashcodesTosynthetics.keySet();
        Iterator hashcodeIterator = hashcodes.iterator();
        int index = 0;
        while (hashcodeIterator.hasNext()) {
            final Integer hashcode2 = hashcodeIterator.next();
            switchLabels[index] = new CaseLabel(this);
            keys[index] = hashcode2;
            sortedIndexes[index] = index;
            ++index;
        }
        int[] localKeysCopy;
        System.arraycopy(keys, 0, localKeysCopy = new int[numberOfHashcodes], 0, numberOfHashcodes);
        sort(localKeysCopy, 0, numberOfHashcodes - 1, sortedIndexes);
        this.lookupswitch(defaultLabel, keys, sortedIndexes, switchLabels);
        hashcodeIterator = hashcodes.iterator();
        index = 0;
        while (hashcodeIterator.hasNext()) {
            final Integer hashcode3 = hashcodeIterator.next();
            final List synthetics = hashcodesTosynthetics.get(hashcode3);
            switchLabels[index].place();
            BranchLabel nextOne = new BranchLabel(this);
            for (int j = 0, max2 = synthetics.size(); j < max2; ++j) {
                final SyntheticMethodBinding syntheticMethodBinding2 = synthetics.get(j);
                this.aload_1();
                this.ldc(new String(syntheticMethodBinding2.selector));
                this.invokeStringEquals();
                this.ifeq(nextOne);
                this.loadInt(index);
                this.istore_2();
                this.goto_(label);
                nextOne.place();
                nextOne = new BranchLabel(this);
            }
            ++index;
            this.goto_(label);
        }
        defaultLabel.place();
        label.place();
        final int syntheticsCount = hashcodes.size();
        switchLabels = new CaseLabel[syntheticsCount];
        keys = new int[syntheticsCount];
        sortedIndexes = new int[syntheticsCount];
        final BranchLabel errorLabel = new BranchLabel(this);
        defaultLabel = new CaseLabel(this);
        this.iload_2();
        for (int k = 0; k < syntheticsCount; ++k) {
            switchLabels[k] = new CaseLabel(this);
            sortedIndexes[keys[k] = k] = k;
        }
        System.arraycopy(keys, 0, localKeysCopy = new int[syntheticsCount], 0, syntheticsCount);
        sort(localKeysCopy, 0, syntheticsCount - 1, sortedIndexes);
        this.lookupswitch(defaultLabel, keys, sortedIndexes, switchLabels);
        hashcodeIterator = hashcodes.iterator();
        int hashcodeIndex = 0;
        while (hashcodeIterator.hasNext()) {
            final Integer hashcode4 = hashcodeIterator.next();
            final List synthetics2 = hashcodesTosynthetics.get(hashcode4);
            switchLabels[hashcodeIndex++].place();
            BranchLabel nextOne2 = (synthetics2.size() > 1) ? new BranchLabel(this) : errorLabel;
            for (int l = 0, count = synthetics2.size(); l < count; ++l) {
                final SyntheticMethodBinding syntheticMethodBinding3 = synthetics2.get(l);
                this.aload_0();
                final FunctionalExpression funcEx = (FunctionalExpression)((syntheticMethodBinding3.lambda != null) ? syntheticMethodBinding3.lambda : syntheticMethodBinding3.serializableMethodRef);
                final MethodBinding mb = funcEx.binding;
                this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangInvokeSerializedLambdaConstantPoolName, ConstantPool.GetImplMethodKind, ConstantPool.GetImplMethodKindSignature);
                byte methodKind = 0;
                if (mb.isStatic()) {
                    methodKind = 6;
                }
                else if (mb.isPrivate()) {
                    methodKind = 7;
                }
                else if (mb.isConstructor()) {
                    methodKind = 8;
                }
                else if (mb.declaringClass.isInterface()) {
                    methodKind = 9;
                }
                else {
                    methodKind = 5;
                }
                this.bipush(methodKind);
                this.if_icmpne(nextOne2);
                this.aload_0();
                this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangInvokeSerializedLambdaConstantPoolName, ConstantPool.GetFunctionalInterfaceClass, ConstantPool.GetFunctionalInterfaceClassSignature);
                String functionalInterface = null;
                final TypeBinding expectedType = funcEx.expectedType();
                if (expectedType instanceof IntersectionTypeBinding18) {
                    functionalInterface = new String(((IntersectionTypeBinding18)expectedType).getSAMType(scope).constantPoolName());
                }
                else {
                    functionalInterface = new String(expectedType.constantPoolName());
                }
                this.ldc(functionalInterface);
                this.invokeObjectEquals();
                this.ifeq(nextOne2);
                this.aload_0();
                this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangInvokeSerializedLambdaConstantPoolName, ConstantPool.GetFunctionalInterfaceMethodName, ConstantPool.GetFunctionalInterfaceMethodNameSignature);
                this.ldc(new String(funcEx.descriptor.selector));
                this.invokeObjectEquals();
                this.ifeq(nextOne2);
                this.aload_0();
                this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangInvokeSerializedLambdaConstantPoolName, ConstantPool.GetFunctionalInterfaceMethodSignature, ConstantPool.GetFunctionalInterfaceMethodSignatureSignature);
                this.ldc(new String(funcEx.descriptor.original().signature()));
                this.invokeObjectEquals();
                this.ifeq(nextOne2);
                this.aload_0();
                this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangInvokeSerializedLambdaConstantPoolName, ConstantPool.GetImplClass, ConstantPool.GetImplClassSignature);
                this.ldc(new String(mb.declaringClass.constantPoolName()));
                this.invokeObjectEquals();
                this.ifeq(nextOne2);
                this.aload_0();
                this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangInvokeSerializedLambdaConstantPoolName, ConstantPool.GetImplMethodSignature, ConstantPool.GetImplMethodSignatureSignature);
                this.ldc(new String(mb.signature()));
                this.invokeObjectEquals();
                this.ifeq(nextOne2);
                final StringBuffer sig = new StringBuffer("(");
                index = 0;
                final boolean isLambda = funcEx instanceof LambdaExpression;
                TypeBinding receiverType = null;
                SyntheticArgumentBinding[] outerLocalVariables = null;
                if (isLambda) {
                    final LambdaExpression lambdaEx = (LambdaExpression)funcEx;
                    if (lambdaEx.shouldCaptureInstance) {
                        receiverType = mb.declaringClass;
                    }
                    outerLocalVariables = lambdaEx.outerLocalVariables;
                }
                else {
                    final ReferenceExpression refEx = (ReferenceExpression)funcEx;
                    if (refEx.haveReceiver) {
                        receiverType = ((ReferenceExpression)funcEx).receiverType;
                    }
                }
                if (receiverType != null) {
                    this.aload_0();
                    this.loadInt(index++);
                    this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangInvokeSerializedLambdaConstantPoolName, ConstantPool.GetCapturedArg, ConstantPool.GetCapturedArgSignature);
                    this.checkcast(mb.declaringClass);
                    sig.append(mb.declaringClass.signature());
                }
                for (int p = 0, max3 = (outerLocalVariables == null) ? 0 : outerLocalVariables.length; p < max3; ++p) {
                    final TypeBinding varType = outerLocalVariables[p].type;
                    this.aload_0();
                    this.loadInt(index);
                    this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangInvokeSerializedLambdaConstantPoolName, ConstantPool.GetCapturedArg, ConstantPool.GetCapturedArgSignature);
                    if (varType.isBaseType()) {
                        this.checkcast(scope.boxing(varType));
                        this.generateUnboxingConversion(varType.id);
                        if (varType.id == 30 || varType.id == 32) {
                            ++index;
                        }
                    }
                    else {
                        this.checkcast(varType);
                    }
                    ++index;
                    sig.append(varType.signature());
                }
                sig.append(")");
                if (funcEx.resolvedType instanceof IntersectionTypeBinding18) {
                    sig.append(((IntersectionTypeBinding18)funcEx.resolvedType).getSAMType(scope).signature());
                }
                else {
                    sig.append(funcEx.resolvedType.signature());
                }
                this.invokeDynamic(funcEx.bootstrapMethodNumber, index, 1, funcEx.descriptor.selector, sig.toString().toCharArray());
                this.areturn();
                if (l < count - 1) {
                    nextOne2.place();
                    nextOne2 = ((l < count - 2) ? new BranchLabel(this) : errorLabel);
                }
            }
        }
        this.removeVariable(lvb1);
        this.removeVariable(lvb2);
        defaultLabel.place();
        errorLabel.place();
        this.new_(scope.getJavaLangIllegalArgumentException());
        this.dup();
        this.ldc("Invalid lambda deserialization");
        this.invoke((byte)(-73), 2, 0, ConstantPool.JavaLangIllegalArgumentExceptionConstantPoolName, ConstantPool.Init, ConstantPool.IllegalArgumentExceptionConstructorSignature);
        this.athrow();
    }
    
    public void loadInt(final int value) {
        if (value < 6) {
            if (value == 0) {
                this.iconst_0();
            }
            else if (value == 1) {
                this.iconst_1();
            }
            else if (value == 2) {
                this.iconst_2();
            }
            else if (value == 3) {
                this.iconst_3();
            }
            else if (value == 4) {
                this.iconst_4();
            }
            else if (value == 5) {
                this.iconst_5();
            }
        }
        else if (value < 128) {
            this.bipush((byte)value);
        }
        else {
            this.ldc(value);
        }
    }
    
    public void generateSyntheticBodyForEnumValues(final SyntheticMethodBinding methodBinding) {
        final ClassScope scope = ((SourceTypeBinding)methodBinding.declaringClass).scope;
        this.initializeMaxLocals(methodBinding);
        final TypeBinding enumArray = methodBinding.returnType;
        this.fieldAccess((byte)(-78), scope.referenceContext.enumValuesSyntheticfield, null);
        this.dup();
        this.astore_0();
        this.iconst_0();
        this.aload_0();
        this.arraylength();
        this.dup();
        this.istore_1();
        this.newArray((ArrayBinding)enumArray);
        this.dup();
        this.astore_2();
        this.iconst_0();
        this.iload_1();
        this.invokeSystemArraycopy();
        this.aload_2();
        this.areturn();
    }
    
    public void generateSyntheticBodyForEnumInitializationMethod(final SyntheticMethodBinding methodBinding) {
        this.maxLocals = 0;
        final SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)methodBinding.declaringClass;
        final TypeDeclaration typeDeclaration = sourceTypeBinding.scope.referenceContext;
        final BlockScope staticInitializerScope = typeDeclaration.staticInitializerScope;
        final FieldDeclaration[] fieldDeclarations = typeDeclaration.fields;
        for (int i = methodBinding.startIndex, max = methodBinding.endIndex; i < max; ++i) {
            final FieldDeclaration fieldDecl = fieldDeclarations[i];
            if (fieldDecl.isStatic() && fieldDecl.getKind() == 3) {
                fieldDecl.generateCode(staticInitializerScope, this);
            }
        }
        this.return_();
    }
    
    public void generateSyntheticBodyForFieldReadAccess(final SyntheticMethodBinding accessMethod) {
        this.initializeMaxLocals(accessMethod);
        final FieldBinding fieldBinding = accessMethod.targetReadField;
        final TypeBinding declaringClass = (accessMethod.purpose == 3) ? accessMethod.declaringClass.superclass() : accessMethod.declaringClass;
        if (fieldBinding.isStatic()) {
            this.fieldAccess((byte)(-78), fieldBinding, declaringClass);
        }
        else {
            this.aload_0();
            this.fieldAccess((byte)(-76), fieldBinding, declaringClass);
        }
        switch (fieldBinding.type.id) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 10: {
                this.ireturn();
                break;
            }
            case 7: {
                this.lreturn();
                break;
            }
            case 9: {
                this.freturn();
                break;
            }
            case 8: {
                this.dreturn();
                break;
            }
            default: {
                this.areturn();
                break;
            }
        }
    }
    
    public void generateSyntheticBodyForFieldWriteAccess(final SyntheticMethodBinding accessMethod) {
        this.initializeMaxLocals(accessMethod);
        final FieldBinding fieldBinding = accessMethod.targetWriteField;
        final TypeBinding declaringClass = (accessMethod.purpose == 4) ? accessMethod.declaringClass.superclass() : accessMethod.declaringClass;
        if (fieldBinding.isStatic()) {
            this.load(fieldBinding.type, 0);
            this.fieldAccess((byte)(-77), fieldBinding, declaringClass);
        }
        else {
            this.aload_0();
            this.load(fieldBinding.type, 1);
            this.fieldAccess((byte)(-75), fieldBinding, declaringClass);
        }
        this.return_();
    }
    
    public void generateSyntheticBodyForMethodAccess(final SyntheticMethodBinding accessMethod) {
        this.initializeMaxLocals(accessMethod);
        final MethodBinding targetMethod = accessMethod.targetMethod;
        final TypeBinding[] parameters = targetMethod.parameters;
        final int length = parameters.length;
        final TypeBinding[] arguments = (TypeBinding[])((accessMethod.purpose == 8) ? accessMethod.parameters : null);
        int resolvedPosition;
        if (targetMethod.isStatic()) {
            resolvedPosition = 0;
        }
        else {
            this.aload_0();
            resolvedPosition = 1;
        }
        for (int i = 0; i < length; ++i) {
            final TypeBinding parameter = parameters[i];
            if (arguments != null) {
                final TypeBinding argument = arguments[i];
                this.load(argument, resolvedPosition);
                if (TypeBinding.notEquals(argument, parameter)) {
                    this.checkcast(parameter);
                }
            }
            else {
                this.load(parameter, resolvedPosition);
            }
            switch (parameter.id) {
                case 7:
                case 8: {
                    resolvedPosition += 2;
                    break;
                }
                default: {
                    ++resolvedPosition;
                    break;
                }
            }
        }
        if (targetMethod.isStatic()) {
            this.invoke((byte)(-72), targetMethod, accessMethod.declaringClass);
        }
        else if (targetMethod.isConstructor() || targetMethod.isPrivate() || accessMethod.purpose == 7) {
            final TypeBinding declaringClass = (accessMethod.purpose == 7) ? this.findDirectSuperTypeTowards(accessMethod, targetMethod) : accessMethod.declaringClass;
            this.invoke((byte)(-73), targetMethod, declaringClass);
        }
        else if (targetMethod.declaringClass.isInterface()) {
            this.invoke((byte)(-71), targetMethod, null);
        }
        else {
            this.invoke((byte)(-74), targetMethod, accessMethod.declaringClass);
        }
        switch (targetMethod.returnType.id) {
            case 6: {
                this.return_();
                break;
            }
            case 2:
            case 3:
            case 4:
            case 5:
            case 10: {
                this.ireturn();
                break;
            }
            case 7: {
                this.lreturn();
                break;
            }
            case 9: {
                this.freturn();
                break;
            }
            case 8: {
                this.dreturn();
                break;
            }
            default: {
                final TypeBinding accessErasure = accessMethod.returnType.erasure();
                final TypeBinding match = targetMethod.returnType.findSuperTypeOriginatingFrom(accessErasure);
                if (match == null) {
                    this.checkcast(accessErasure);
                }
                this.areturn();
                break;
            }
        }
    }
    
    ReferenceBinding findDirectSuperTypeTowards(final SyntheticMethodBinding accessMethod, final MethodBinding targetMethod) {
        final ReferenceBinding currentType = accessMethod.declaringClass;
        final ReferenceBinding superclass = currentType.superclass();
        if (!targetMethod.isDefaultMethod()) {
            return superclass;
        }
        final ReferenceBinding targetType = targetMethod.declaringClass;
        if (superclass.isCompatibleWith(targetType)) {
            return superclass;
        }
        final ReferenceBinding[] superInterfaces = currentType.superInterfaces();
        if (superInterfaces != null) {
            for (int i = 0; i < superInterfaces.length; ++i) {
                final ReferenceBinding superIfc = superInterfaces[i];
                if (superIfc.isCompatibleWith(targetType)) {
                    return superIfc;
                }
            }
        }
        throw new RuntimeException("Assumption violated: some super type must be conform to the declaring class of a super method");
    }
    
    public void generateSyntheticBodyForSwitchTable(final SyntheticMethodBinding methodBinding) {
        final ClassScope scope = ((SourceTypeBinding)methodBinding.declaringClass).scope;
        this.initializeMaxLocals(methodBinding);
        final BranchLabel nullLabel = new BranchLabel(this);
        final FieldBinding syntheticFieldBinding = methodBinding.targetReadField;
        this.fieldAccess((byte)(-78), syntheticFieldBinding, null);
        this.dup();
        this.ifnull(nullLabel);
        this.areturn();
        this.pushOnStack(syntheticFieldBinding.type);
        nullLabel.place();
        this.pop();
        final ReferenceBinding enumBinding = (ReferenceBinding)methodBinding.targetEnumType;
        final ArrayBinding arrayBinding = scope.createArrayType(enumBinding, 1);
        this.invokeJavaLangEnumValues(enumBinding, arrayBinding);
        this.arraylength();
        this.newarray(10);
        this.astore_0();
        final LocalVariableBinding localVariableBinding = new LocalVariableBinding(" tab".toCharArray(), scope.createArrayType(TypeBinding.INT, 1), 0, false);
        this.addVariable(localVariableBinding);
        final FieldBinding[] fields = enumBinding.fields();
        if (fields != null) {
            for (int i = 0, max = fields.length; i < max; ++i) {
                final FieldBinding fieldBinding = fields[i];
                if ((fieldBinding.getAccessFlags() & 0x4000) != 0x0) {
                    final BranchLabel endLabel = new BranchLabel(this);
                    final ExceptionLabel anyExceptionHandler = new ExceptionLabel(this, TypeBinding.LONG);
                    anyExceptionHandler.placeStart();
                    this.aload_0();
                    this.fieldAccess((byte)(-78), fieldBinding, null);
                    this.invokeEnumOrdinal(enumBinding.constantPoolName());
                    this.generateInlinedValue(fieldBinding.id + 1);
                    this.iastore();
                    anyExceptionHandler.placeEnd();
                    this.goto_(endLabel);
                    this.pushExceptionOnStack(TypeBinding.LONG);
                    anyExceptionHandler.place();
                    this.pop();
                    endLabel.place();
                }
            }
        }
        this.aload_0();
        this.dup();
        this.fieldAccess((byte)(-77), syntheticFieldBinding, null);
        this.areturn();
        this.removeVariable(localVariableBinding);
    }
    
    public void generateSyntheticEnclosingInstanceValues(final BlockScope currentScope, final ReferenceBinding targetType, final Expression enclosingInstance, final ASTNode invocationSite) {
        final ReferenceBinding checkedTargetType = (ReferenceBinding)(targetType.isAnonymousType() ? targetType.superclass().erasure() : targetType);
        boolean hasExtraEnclosingInstance = enclosingInstance != null;
        if (hasExtraEnclosingInstance && (!checkedTargetType.isNestedType() || checkedTargetType.isStatic())) {
            currentScope.problemReporter().unnecessaryEnclosingInstanceSpecification(enclosingInstance, checkedTargetType);
            return;
        }
        final ReferenceBinding[] syntheticArgumentTypes;
        if ((syntheticArgumentTypes = targetType.syntheticEnclosingInstanceTypes()) != null) {
            final ReferenceBinding targetEnclosingType = checkedTargetType.enclosingType();
            final long compliance = currentScope.compilerOptions().complianceLevel;
            boolean denyEnclosingArgInConstructorCall;
            if (compliance <= 3080192L) {
                denyEnclosingArgInConstructorCall = (invocationSite instanceof AllocationExpression);
            }
            else if (compliance == 3145728L) {
                denyEnclosingArgInConstructorCall = (invocationSite instanceof AllocationExpression || (invocationSite instanceof ExplicitConstructorCall && ((ExplicitConstructorCall)invocationSite).isSuperAccess()));
            }
            else {
                denyEnclosingArgInConstructorCall = ((invocationSite instanceof AllocationExpression || (invocationSite instanceof ExplicitConstructorCall && ((ExplicitConstructorCall)invocationSite).isSuperAccess())) && !targetType.isLocalType());
            }
            final boolean complyTo14 = compliance >= 3145728L;
            for (int i = 0, max = syntheticArgumentTypes.length; i < max; ++i) {
                final ReferenceBinding syntheticArgType = syntheticArgumentTypes[i];
                if (hasExtraEnclosingInstance && TypeBinding.equalsEquals(syntheticArgType, targetEnclosingType)) {
                    hasExtraEnclosingInstance = false;
                    enclosingInstance.generateCode(currentScope, this, true);
                    if (complyTo14) {
                        this.dup();
                        this.invokeObjectGetClass();
                        this.pop();
                    }
                }
                else {
                    final Object[] emulationPath = currentScope.getEmulationPath(syntheticArgType, false, denyEnclosingArgInConstructorCall);
                    this.generateOuterAccess(emulationPath, invocationSite, syntheticArgType, currentScope);
                }
            }
            if (hasExtraEnclosingInstance) {
                currentScope.problemReporter().unnecessaryEnclosingInstanceSpecification(enclosingInstance, checkedTargetType);
            }
        }
    }
    
    public void generateSyntheticOuterArgumentValues(final BlockScope currentScope, final ReferenceBinding targetType, final ASTNode invocationSite) {
        final SyntheticArgumentBinding[] syntheticArguments;
        if ((syntheticArguments = targetType.syntheticOuterLocalVariables()) != null) {
            for (int i = 0, max = syntheticArguments.length; i < max; ++i) {
                final LocalVariableBinding targetVariable = syntheticArguments[i].actualOuterLocalVariable;
                final VariableBinding[] emulationPath = currentScope.getEmulationPath(targetVariable);
                this.generateOuterAccess(emulationPath, invocationSite, targetVariable, currentScope);
            }
        }
    }
    
    public void generateUnboxingConversion(final int unboxedTypeID) {
        switch (unboxedTypeID) {
            case 3: {
                this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangByteConstantPoolName, ConstantPool.BYTEVALUE_BYTE_METHOD_NAME, ConstantPool.BYTEVALUE_BYTE_METHOD_SIGNATURE);
                break;
            }
            case 4: {
                this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangShortConstantPoolName, ConstantPool.SHORTVALUE_SHORT_METHOD_NAME, ConstantPool.SHORTVALUE_SHORT_METHOD_SIGNATURE);
                break;
            }
            case 2: {
                this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangCharacterConstantPoolName, ConstantPool.CHARVALUE_CHARACTER_METHOD_NAME, ConstantPool.CHARVALUE_CHARACTER_METHOD_SIGNATURE);
                break;
            }
            case 10: {
                this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangIntegerConstantPoolName, ConstantPool.INTVALUE_INTEGER_METHOD_NAME, ConstantPool.INTVALUE_INTEGER_METHOD_SIGNATURE);
                break;
            }
            case 7: {
                this.invoke((byte)(-74), 1, 2, ConstantPool.JavaLangLongConstantPoolName, ConstantPool.LONGVALUE_LONG_METHOD_NAME, ConstantPool.LONGVALUE_LONG_METHOD_SIGNATURE);
                break;
            }
            case 9: {
                this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangFloatConstantPoolName, ConstantPool.FLOATVALUE_FLOAT_METHOD_NAME, ConstantPool.FLOATVALUE_FLOAT_METHOD_SIGNATURE);
                break;
            }
            case 8: {
                this.invoke((byte)(-74), 1, 2, ConstantPool.JavaLangDoubleConstantPoolName, ConstantPool.DOUBLEVALUE_DOUBLE_METHOD_NAME, ConstantPool.DOUBLEVALUE_DOUBLE_METHOD_SIGNATURE);
                break;
            }
            case 5: {
                this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangBooleanConstantPoolName, ConstantPool.BOOLEANVALUE_BOOLEAN_METHOD_NAME, ConstantPool.BOOLEANVALUE_BOOLEAN_METHOD_SIGNATURE);
                break;
            }
        }
    }
    
    public void generateWideRevertedConditionalBranch(final byte revertedOpcode, final BranchLabel wideTarget) {
        final BranchLabel intermediate = new BranchLabel(this);
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = revertedOpcode;
        intermediate.branch();
        this.goto_w(wideTarget);
        intermediate.place();
    }
    
    public void getBaseTypeValue(final int baseTypeID) {
        switch (baseTypeID) {
            case 3: {
                this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangByteConstantPoolName, ConstantPool.BYTEVALUE_BYTE_METHOD_NAME, ConstantPool.BYTEVALUE_BYTE_METHOD_SIGNATURE);
                break;
            }
            case 4: {
                this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangShortConstantPoolName, ConstantPool.SHORTVALUE_SHORT_METHOD_NAME, ConstantPool.SHORTVALUE_SHORT_METHOD_SIGNATURE);
                break;
            }
            case 2: {
                this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangCharacterConstantPoolName, ConstantPool.CHARVALUE_CHARACTER_METHOD_NAME, ConstantPool.CHARVALUE_CHARACTER_METHOD_SIGNATURE);
                break;
            }
            case 10: {
                this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangIntegerConstantPoolName, ConstantPool.INTVALUE_INTEGER_METHOD_NAME, ConstantPool.INTVALUE_INTEGER_METHOD_SIGNATURE);
                break;
            }
            case 7: {
                this.invoke((byte)(-74), 1, 2, ConstantPool.JavaLangLongConstantPoolName, ConstantPool.LONGVALUE_LONG_METHOD_NAME, ConstantPool.LONGVALUE_LONG_METHOD_SIGNATURE);
                break;
            }
            case 9: {
                this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangFloatConstantPoolName, ConstantPool.FLOATVALUE_FLOAT_METHOD_NAME, ConstantPool.FLOATVALUE_FLOAT_METHOD_SIGNATURE);
                break;
            }
            case 8: {
                this.invoke((byte)(-74), 1, 2, ConstantPool.JavaLangDoubleConstantPoolName, ConstantPool.DOUBLEVALUE_DOUBLE_METHOD_NAME, ConstantPool.DOUBLEVALUE_DOUBLE_METHOD_SIGNATURE);
                break;
            }
            case 5: {
                this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangBooleanConstantPoolName, ConstantPool.BOOLEANVALUE_BOOLEAN_METHOD_NAME, ConstantPool.BOOLEANVALUE_BOOLEAN_METHOD_SIGNATURE);
                break;
            }
        }
    }
    
    public final byte[] getContents() {
        final byte[] contents;
        System.arraycopy(this.bCodeStream, 0, contents = new byte[this.position], 0, this.position);
        return contents;
    }
    
    public static TypeBinding getConstantPoolDeclaringClass(final Scope currentScope, final FieldBinding codegenBinding, final TypeBinding actualReceiverType, final boolean isImplicitThisReceiver) {
        final ReferenceBinding constantPoolDeclaringClass = codegenBinding.declaringClass;
        if (TypeBinding.notEquals(constantPoolDeclaringClass, actualReceiverType.erasure()) && !actualReceiverType.isArrayType() && constantPoolDeclaringClass != null && codegenBinding.constant() == Constant.NotAConstant) {
            final CompilerOptions options = currentScope.compilerOptions();
            if ((options.targetJDK >= 3014656L && (options.complianceLevel >= 3145728L || !isImplicitThisReceiver || !codegenBinding.isStatic()) && constantPoolDeclaringClass.id != 1) || !constantPoolDeclaringClass.canBeSeenBy(currentScope)) {
                return actualReceiverType.erasure();
            }
        }
        return constantPoolDeclaringClass;
    }
    
    public static TypeBinding getConstantPoolDeclaringClass(final Scope currentScope, final MethodBinding codegenBinding, final TypeBinding actualReceiverType, final boolean isImplicitThisReceiver) {
        TypeBinding constantPoolDeclaringClass = codegenBinding.declaringClass;
        if (codegenBinding == currentScope.environment().arrayClone) {
            final CompilerOptions options = currentScope.compilerOptions();
            if (options.sourceLevel > 3145728L) {
                constantPoolDeclaringClass = actualReceiverType.erasure();
            }
        }
        else if (TypeBinding.notEquals(constantPoolDeclaringClass, actualReceiverType.erasure()) && !actualReceiverType.isArrayType()) {
            final CompilerOptions options = currentScope.compilerOptions();
            if ((options.targetJDK >= 3014656L && (options.complianceLevel >= 3145728L || !isImplicitThisReceiver || !codegenBinding.isStatic()) && codegenBinding.declaringClass.id != 1) || !codegenBinding.declaringClass.canBeSeenBy(currentScope)) {
                if (actualReceiverType.isIntersectionType18()) {
                    final TypeBinding[] intersectingTypes = ((IntersectionTypeBinding18)actualReceiverType).getIntersectingTypes();
                    for (int i = 0; i < intersectingTypes.length; ++i) {
                        if (intersectingTypes[i].findSuperTypeOriginatingFrom(constantPoolDeclaringClass) != null) {
                            constantPoolDeclaringClass = intersectingTypes[i];
                            break;
                        }
                    }
                }
                else {
                    constantPoolDeclaringClass = actualReceiverType.erasure();
                }
            }
        }
        return constantPoolDeclaringClass;
    }
    
    protected int getPosition() {
        return this.position;
    }
    
    public void getTYPE(final int baseTypeID) {
        this.countLabels = 0;
        switch (baseTypeID) {
            case 3: {
                this.fieldAccess((byte)(-78), 1, ConstantPool.JavaLangByteConstantPoolName, ConstantPool.TYPE, ConstantPool.JavaLangClassSignature);
                break;
            }
            case 4: {
                this.fieldAccess((byte)(-78), 1, ConstantPool.JavaLangShortConstantPoolName, ConstantPool.TYPE, ConstantPool.JavaLangClassSignature);
                break;
            }
            case 2: {
                this.fieldAccess((byte)(-78), 1, ConstantPool.JavaLangCharacterConstantPoolName, ConstantPool.TYPE, ConstantPool.JavaLangClassSignature);
                break;
            }
            case 10: {
                this.fieldAccess((byte)(-78), 1, ConstantPool.JavaLangIntegerConstantPoolName, ConstantPool.TYPE, ConstantPool.JavaLangClassSignature);
                break;
            }
            case 7: {
                this.fieldAccess((byte)(-78), 1, ConstantPool.JavaLangLongConstantPoolName, ConstantPool.TYPE, ConstantPool.JavaLangClassSignature);
                break;
            }
            case 9: {
                this.fieldAccess((byte)(-78), 1, ConstantPool.JavaLangFloatConstantPoolName, ConstantPool.TYPE, ConstantPool.JavaLangClassSignature);
                break;
            }
            case 8: {
                this.fieldAccess((byte)(-78), 1, ConstantPool.JavaLangDoubleConstantPoolName, ConstantPool.TYPE, ConstantPool.JavaLangClassSignature);
                break;
            }
            case 5: {
                this.fieldAccess((byte)(-78), 1, ConstantPool.JavaLangBooleanConstantPoolName, ConstantPool.TYPE, ConstantPool.JavaLangClassSignature);
                break;
            }
            case 6: {
                this.fieldAccess((byte)(-78), 1, ConstantPool.JavaLangVoidConstantPoolName, ConstantPool.TYPE, ConstantPool.JavaLangClassSignature);
                break;
            }
        }
    }
    
    public void goto_(final BranchLabel label) {
        if (this.wideMode) {
            this.goto_w(label);
            return;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        final boolean chained = this.inlineForwardReferencesFromLabelsTargeting(label, this.position);
        if (chained && this.lastAbruptCompletion == this.position) {
            if (label.position != -1) {
                final int[] forwardRefs = label.forwardReferences();
                for (int i = 0, max = label.forwardReferenceCount(); i < max; ++i) {
                    this.writePosition(label, forwardRefs[i]);
                }
                this.countLabels = 0;
            }
            return;
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -89;
        label.branch();
        this.lastAbruptCompletion = this.position;
    }
    
    public void goto_w(final BranchLabel label) {
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -56;
        label.branchWide();
        this.lastAbruptCompletion = this.position;
    }
    
    public void i2b() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -111;
    }
    
    public void i2c() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -110;
    }
    
    public void i2d() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -121;
    }
    
    public void i2f() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -122;
    }
    
    public void i2l() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -123;
    }
    
    public void i2s() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -109;
    }
    
    public void iadd() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 96;
    }
    
    public void iaload() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 46;
    }
    
    public void iand() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 126;
    }
    
    public void iastore() {
        this.countLabels = 0;
        this.stackDepth -= 3;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 79;
    }
    
    public void iconst_0() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 3;
    }
    
    public void iconst_1() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 4;
    }
    
    public void iconst_2() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 5;
    }
    
    public void iconst_3() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 6;
    }
    
    public void iconst_4() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 7;
    }
    
    public void iconst_5() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 8;
    }
    
    public void iconst_m1() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 2;
    }
    
    public void idiv() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 108;
    }
    
    public void if_acmpeq(final BranchLabel lbl) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)(-90), lbl);
        }
        else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -91;
            lbl.branch();
        }
    }
    
    public void if_acmpne(final BranchLabel lbl) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)(-91), lbl);
        }
        else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -90;
            lbl.branch();
        }
    }
    
    public void if_icmpeq(final BranchLabel lbl) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)(-96), lbl);
        }
        else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -97;
            lbl.branch();
        }
    }
    
    public void if_icmpge(final BranchLabel lbl) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)(-95), lbl);
        }
        else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -94;
            lbl.branch();
        }
    }
    
    public void if_icmpgt(final BranchLabel lbl) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)(-92), lbl);
        }
        else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -93;
            lbl.branch();
        }
    }
    
    public void if_icmple(final BranchLabel lbl) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)(-93), lbl);
        }
        else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -92;
            lbl.branch();
        }
    }
    
    public void if_icmplt(final BranchLabel lbl) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)(-94), lbl);
        }
        else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -95;
            lbl.branch();
        }
    }
    
    public void if_icmpne(final BranchLabel lbl) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)(-97), lbl);
        }
        else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -96;
            lbl.branch();
        }
    }
    
    public void ifeq(final BranchLabel lbl) {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)(-102), lbl);
        }
        else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -103;
            lbl.branch();
        }
    }
    
    public void ifge(final BranchLabel lbl) {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)(-101), lbl);
        }
        else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -100;
            lbl.branch();
        }
    }
    
    public void ifgt(final BranchLabel lbl) {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)(-98), lbl);
        }
        else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -99;
            lbl.branch();
        }
    }
    
    public void ifle(final BranchLabel lbl) {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)(-99), lbl);
        }
        else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -98;
            lbl.branch();
        }
    }
    
    public void iflt(final BranchLabel lbl) {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)(-100), lbl);
        }
        else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -101;
            lbl.branch();
        }
    }
    
    public void ifne(final BranchLabel lbl) {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)(-103), lbl);
        }
        else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -102;
            lbl.branch();
        }
    }
    
    public void ifnonnull(final BranchLabel lbl) {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)(-58), lbl);
        }
        else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -57;
            lbl.branch();
        }
    }
    
    public void ifnull(final BranchLabel lbl) {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.wideMode) {
            this.generateWideRevertedConditionalBranch((byte)(-57), lbl);
        }
        else {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = -58;
            lbl.branch();
        }
    }
    
    public final void iinc(final int index, final int value) {
        this.countLabels = 0;
        if (index > 255 || value < -128 || value > 127) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = -124;
            this.writeUnsignedShort(index);
            this.writeSignedShort(value);
        }
        else {
            if (this.classFileOffset + 2 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 3;
            this.bCodeStream[this.classFileOffset++] = -124;
            this.bCodeStream[this.classFileOffset++] = (byte)index;
            this.bCodeStream[this.classFileOffset++] = (byte)value;
        }
    }
    
    public void iload(final int iArg) {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.maxLocals <= iArg) {
            this.maxLocals = iArg + 1;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 21;
            this.writeUnsignedShort(iArg);
        }
        else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 21;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }
    
    public void iload_0() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.maxLocals <= 0) {
            this.maxLocals = 1;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 26;
    }
    
    public void iload_1() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.maxLocals <= 1) {
            this.maxLocals = 2;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 27;
    }
    
    public void iload_2() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.maxLocals <= 2) {
            this.maxLocals = 3;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 28;
    }
    
    public void iload_3() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.maxLocals <= 3) {
            this.maxLocals = 4;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 29;
    }
    
    public void imul() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 104;
    }
    
    public int indexOfSameLineEntrySincePC(final int pc, final int line) {
        for (int index = pc, max = this.pcToSourceMapSize; index < max; index += 2) {
            if (this.pcToSourceMap[index + 1] == line) {
                return index;
            }
        }
        return -1;
    }
    
    public void ineg() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 116;
    }
    
    public void init(final ClassFile targetClassFile) {
        this.classFile = targetClassFile;
        this.constantPool = targetClassFile.constantPool;
        this.bCodeStream = targetClassFile.contents;
        this.classFileOffset = targetClassFile.contentsOffset;
        this.startingClassFileOffset = this.classFileOffset;
        this.pcToSourceMapSize = 0;
        this.lastEntryPC = 0;
        this.visibleLocalsCount = 0;
        this.allLocalsCounter = 0;
        this.exceptionLabelsCounter = 0;
        this.countLabels = 0;
        this.lastAbruptCompletion = -1;
        this.stackMax = 0;
        this.stackDepth = 0;
        this.maxLocals = 0;
        this.position = 0;
    }
    
    public void initializeMaxLocals(final MethodBinding methodBinding) {
        if (methodBinding == null) {
            this.maxLocals = 0;
            return;
        }
        this.maxLocals = (methodBinding.isStatic() ? 0 : 1);
        final ReferenceBinding declaringClass = methodBinding.declaringClass;
        if (methodBinding.isConstructor() && declaringClass.isEnum()) {
            this.maxLocals += 2;
        }
        if (methodBinding.isConstructor() && declaringClass.isNestedType()) {
            this.maxLocals += declaringClass.getEnclosingInstancesSlotSize();
            this.maxLocals += declaringClass.getOuterLocalVariablesSlotSize();
        }
        final TypeBinding[] parameterTypes;
        if ((parameterTypes = methodBinding.parameters) != null) {
            for (int i = 0, max = parameterTypes.length; i < max; ++i) {
                switch (parameterTypes[i].id) {
                    case 7:
                    case 8: {
                        this.maxLocals += 2;
                        break;
                    }
                    default: {
                        ++this.maxLocals;
                        break;
                    }
                }
            }
        }
    }
    
    public boolean inlineForwardReferencesFromLabelsTargeting(final BranchLabel targetLabel, final int gotoLocation) {
        if (targetLabel.delegate != null) {
            return false;
        }
        int chaining = 0;
        for (int i = this.countLabels - 1; i >= 0; --i) {
            final BranchLabel currentLabel = this.labels[i];
            if (currentLabel.position != gotoLocation) {
                break;
            }
            if (currentLabel == targetLabel) {
                chaining |= 0x4;
            }
            else if (currentLabel.isStandardLabel()) {
                if (currentLabel.delegate == null) {
                    targetLabel.becomeDelegateFor(currentLabel);
                    chaining |= 0x2;
                }
            }
            else {
                chaining |= 0x4;
            }
        }
        return (chaining & 0x6) == 0x2;
    }
    
    public void instance_of(final TypeBinding typeBinding) {
        this.instance_of(null, typeBinding);
    }
    
    public void instance_of(final TypeReference typeReference, final TypeBinding typeBinding) {
        this.countLabels = 0;
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -63;
        this.writeUnsignedShort(this.constantPool.literalIndexForType(typeBinding));
    }
    
    protected void invoke(final byte opcode, final int receiverAndArgsSize, final int returnTypeSize, final char[] declaringClass, final char[] selector, final char[] signature) {
        this.invoke18(opcode, receiverAndArgsSize, returnTypeSize, declaringClass, opcode == -71, selector, signature);
    }
    
    private void invoke18(final byte opcode, final int receiverAndArgsSize, final int returnTypeSize, final char[] declaringClass, final boolean isInterface, final char[] selector, final char[] signature) {
        this.countLabels = 0;
        if (opcode == -71) {
            if (this.classFileOffset + 4 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 3;
            this.bCodeStream[this.classFileOffset++] = opcode;
            this.writeUnsignedShort(this.constantPool.literalIndexForMethod(declaringClass, selector, signature, true));
            this.bCodeStream[this.classFileOffset++] = (byte)receiverAndArgsSize;
            this.bCodeStream[this.classFileOffset++] = 0;
        }
        else {
            if (this.classFileOffset + 2 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = opcode;
            this.writeUnsignedShort(this.constantPool.literalIndexForMethod(declaringClass, selector, signature, isInterface));
        }
        this.stackDepth += returnTypeSize - receiverAndArgsSize;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
    }
    
    public void invokeDynamic(final int bootStrapIndex, final int argsSize, final int returnTypeSize, final char[] selector, final char[] signature) {
        this.invokeDynamic(bootStrapIndex, argsSize, returnTypeSize, selector, signature, false, null, null);
    }
    
    public void invokeDynamic(final int bootStrapIndex, final int argsSize, final int returnTypeSize, final char[] selector, final char[] signature, final boolean isConstructorReference, final TypeReference lhsTypeReference, final TypeReference[] typeArguments) {
        if (this.classFileOffset + 4 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        final int invokeDynamicIndex = this.constantPool.literalIndexForInvokeDynamic(bootStrapIndex, selector, signature);
        this.position += 3;
        this.bCodeStream[this.classFileOffset++] = -70;
        this.writeUnsignedShort(invokeDynamicIndex);
        this.bCodeStream[this.classFileOffset++] = 0;
        this.bCodeStream[this.classFileOffset++] = 0;
        this.stackDepth += returnTypeSize - argsSize;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
    }
    
    public void invoke(final byte opcode, final MethodBinding methodBinding, final TypeBinding declaringClass) {
        this.invoke(opcode, methodBinding, declaringClass, null);
    }
    
    public void invoke(final byte opcode, final MethodBinding methodBinding, TypeBinding declaringClass, final TypeReference[] typeArguments) {
        if (declaringClass == null) {
            declaringClass = methodBinding.declaringClass;
        }
        if ((declaringClass.tagBits & 0x800L) != 0x0L) {
            Util.recordNestedType(this.classFile, declaringClass);
        }
        int receiverAndArgsSize = 0;
        switch (opcode) {
            case -72: {
                receiverAndArgsSize = 0;
                break;
            }
            case -74:
            case -71: {
                receiverAndArgsSize = 1;
                break;
            }
            case -73: {
                receiverAndArgsSize = 1;
                if (!methodBinding.isConstructor()) {
                    break;
                }
                if (declaringClass.isNestedType()) {
                    final ReferenceBinding nestedType = (ReferenceBinding)declaringClass;
                    receiverAndArgsSize += nestedType.getEnclosingInstancesSlotSize();
                    final SyntheticArgumentBinding[] syntheticArguments = nestedType.syntheticOuterLocalVariables();
                    if (syntheticArguments != null) {
                        for (int i = 0, max = syntheticArguments.length; i < max; ++i) {
                            switch (syntheticArguments[i].id) {
                                case 7:
                                case 8: {
                                    receiverAndArgsSize += 2;
                                    break;
                                }
                                default: {
                                    ++receiverAndArgsSize;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (declaringClass.isEnum()) {
                    receiverAndArgsSize += 2;
                    break;
                }
                break;
            }
            default: {
                return;
            }
        }
        for (int j = methodBinding.parameters.length - 1; j >= 0; --j) {
            switch (methodBinding.parameters[j].id) {
                case 7:
                case 8: {
                    receiverAndArgsSize += 2;
                    break;
                }
                default: {
                    ++receiverAndArgsSize;
                    break;
                }
            }
        }
        int returnTypeSize = 0;
        switch (methodBinding.returnType.id) {
            case 7:
            case 8: {
                returnTypeSize = 2;
                break;
            }
            case 6: {
                returnTypeSize = 0;
                break;
            }
            default: {
                returnTypeSize = 1;
                break;
            }
        }
        this.invoke18(opcode, receiverAndArgsSize, returnTypeSize, declaringClass.constantPoolName(), declaringClass.isInterface(), methodBinding.selector, methodBinding.signature(this.classFile));
    }
    
    protected void invokeAccessibleObjectSetAccessible() {
        this.invoke((byte)(-74), 2, 0, ConstantPool.JAVALANGREFLECTACCESSIBLEOBJECT_CONSTANTPOOLNAME, ConstantPool.SETACCESSIBLE_NAME, ConstantPool.SETACCESSIBLE_SIGNATURE);
    }
    
    protected void invokeArrayNewInstance() {
        this.invoke((byte)(-72), 2, 1, ConstantPool.JAVALANGREFLECTARRAY_CONSTANTPOOLNAME, ConstantPool.NewInstance, ConstantPool.NewInstanceSignature);
    }
    
    public void invokeClassForName() {
        this.invoke((byte)(-72), 1, 1, ConstantPool.JavaLangClassConstantPoolName, ConstantPool.ForName, ConstantPool.ForNameSignature);
    }
    
    protected void invokeClassGetDeclaredConstructor() {
        this.invoke((byte)(-74), 2, 1, ConstantPool.JavaLangClassConstantPoolName, ConstantPool.GETDECLAREDCONSTRUCTOR_NAME, ConstantPool.GETDECLAREDCONSTRUCTOR_SIGNATURE);
    }
    
    protected void invokeClassGetDeclaredField() {
        this.invoke((byte)(-74), 2, 1, ConstantPool.JavaLangClassConstantPoolName, ConstantPool.GETDECLAREDFIELD_NAME, ConstantPool.GETDECLAREDFIELD_SIGNATURE);
    }
    
    protected void invokeClassGetDeclaredMethod() {
        this.invoke((byte)(-74), 3, 1, ConstantPool.JavaLangClassConstantPoolName, ConstantPool.GETDECLAREDMETHOD_NAME, ConstantPool.GETDECLAREDMETHOD_SIGNATURE);
    }
    
    public void invokeEnumOrdinal(final char[] enumTypeConstantPoolName) {
        this.invoke((byte)(-74), 1, 1, enumTypeConstantPoolName, ConstantPool.Ordinal, ConstantPool.OrdinalSignature);
    }
    
    public void invokeIterableIterator(final TypeBinding iterableReceiverType) {
        if ((iterableReceiverType.tagBits & 0x800L) != 0x0L) {
            Util.recordNestedType(this.classFile, iterableReceiverType);
        }
        this.invoke((byte)(iterableReceiverType.isInterface() ? -71 : -74), 1, 1, iterableReceiverType.constantPoolName(), ConstantPool.ITERATOR_NAME, ConstantPool.ITERATOR_SIGNATURE);
    }
    
    public void invokeAutoCloseableClose(final TypeBinding resourceType) {
        this.invoke((byte)(resourceType.erasure().isInterface() ? -71 : -74), 1, 0, resourceType.constantPoolName(), ConstantPool.Close, ConstantPool.CloseSignature);
    }
    
    public void invokeThrowableAddSuppressed() {
        this.invoke((byte)(-74), 2, 0, ConstantPool.JavaLangThrowableConstantPoolName, ConstantPool.AddSuppressed, ConstantPool.AddSuppressedSignature);
    }
    
    public void invokeJavaLangAssertionErrorConstructor(final int typeBindingID) {
        char[] signature = null;
        int receiverAndArgsSize = 0;
        switch (typeBindingID) {
            case 3:
            case 4:
            case 10: {
                signature = ConstantPool.IntConstrSignature;
                receiverAndArgsSize = 2;
                break;
            }
            case 7: {
                signature = ConstantPool.LongConstrSignature;
                receiverAndArgsSize = 3;
                break;
            }
            case 9: {
                signature = ConstantPool.FloatConstrSignature;
                receiverAndArgsSize = 2;
                break;
            }
            case 8: {
                signature = ConstantPool.DoubleConstrSignature;
                receiverAndArgsSize = 3;
                break;
            }
            case 2: {
                signature = ConstantPool.CharConstrSignature;
                receiverAndArgsSize = 2;
                break;
            }
            case 5: {
                signature = ConstantPool.BooleanConstrSignature;
                receiverAndArgsSize = 2;
                break;
            }
            case 1:
            case 11:
            case 12: {
                signature = ConstantPool.ObjectConstrSignature;
                receiverAndArgsSize = 2;
                break;
            }
            default: {
                return;
            }
        }
        this.invoke((byte)(-73), receiverAndArgsSize, 0, ConstantPool.JavaLangAssertionErrorConstantPoolName, ConstantPool.Init, signature);
    }
    
    public void invokeJavaLangAssertionErrorDefaultConstructor() {
        this.invoke((byte)(-73), 1, 0, ConstantPool.JavaLangAssertionErrorConstantPoolName, ConstantPool.Init, ConstantPool.DefaultConstructorSignature);
    }
    
    public void invokeJavaLangClassDesiredAssertionStatus() {
        this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangClassConstantPoolName, ConstantPool.DesiredAssertionStatus, ConstantPool.DesiredAssertionStatusSignature);
    }
    
    public void invokeJavaLangEnumvalueOf(final ReferenceBinding binding) {
        this.invoke((byte)(-72), 2, 1, ConstantPool.JavaLangEnumConstantPoolName, ConstantPool.ValueOf, ConstantPool.ValueOfStringClassSignature);
    }
    
    public void invokeJavaLangEnumValues(final TypeBinding enumBinding, final ArrayBinding arrayBinding) {
        char[] signature = "()".toCharArray();
        signature = CharOperation.concat(signature, arrayBinding.constantPoolName());
        this.invoke((byte)(-72), 0, 1, enumBinding.constantPoolName(), TypeConstants.VALUES, signature);
    }
    
    public void invokeJavaLangErrorConstructor() {
        this.invoke((byte)(-73), 2, 0, ConstantPool.JavaLangErrorConstantPoolName, ConstantPool.Init, ConstantPool.StringConstructorSignature);
    }
    
    public void invokeJavaLangReflectConstructorNewInstance() {
        this.invoke((byte)(-74), 2, 1, ConstantPool.JavaLangReflectConstructorConstantPoolName, ConstantPool.NewInstance, ConstantPool.JavaLangReflectConstructorNewInstanceSignature);
    }
    
    protected void invokeJavaLangReflectFieldGetter(final int typeID) {
        char[] selector = null;
        char[] signature = null;
        int returnTypeSize = 0;
        switch (typeID) {
            case 10: {
                selector = ConstantPool.GET_INT_METHOD_NAME;
                signature = ConstantPool.GET_INT_METHOD_SIGNATURE;
                returnTypeSize = 1;
                break;
            }
            case 3: {
                selector = ConstantPool.GET_BYTE_METHOD_NAME;
                signature = ConstantPool.GET_BYTE_METHOD_SIGNATURE;
                returnTypeSize = 1;
                break;
            }
            case 4: {
                selector = ConstantPool.GET_SHORT_METHOD_NAME;
                signature = ConstantPool.GET_SHORT_METHOD_SIGNATURE;
                returnTypeSize = 1;
                break;
            }
            case 7: {
                selector = ConstantPool.GET_LONG_METHOD_NAME;
                signature = ConstantPool.GET_LONG_METHOD_SIGNATURE;
                returnTypeSize = 2;
                break;
            }
            case 9: {
                selector = ConstantPool.GET_FLOAT_METHOD_NAME;
                signature = ConstantPool.GET_FLOAT_METHOD_SIGNATURE;
                returnTypeSize = 1;
                break;
            }
            case 8: {
                selector = ConstantPool.GET_DOUBLE_METHOD_NAME;
                signature = ConstantPool.GET_DOUBLE_METHOD_SIGNATURE;
                returnTypeSize = 2;
                break;
            }
            case 2: {
                selector = ConstantPool.GET_CHAR_METHOD_NAME;
                signature = ConstantPool.GET_CHAR_METHOD_SIGNATURE;
                returnTypeSize = 1;
                break;
            }
            case 5: {
                selector = ConstantPool.GET_BOOLEAN_METHOD_NAME;
                signature = ConstantPool.GET_BOOLEAN_METHOD_SIGNATURE;
                returnTypeSize = 1;
                break;
            }
            default: {
                selector = ConstantPool.GET_OBJECT_METHOD_NAME;
                signature = ConstantPool.GET_OBJECT_METHOD_SIGNATURE;
                returnTypeSize = 1;
                break;
            }
        }
        this.invoke((byte)(-74), 2, returnTypeSize, ConstantPool.JAVALANGREFLECTFIELD_CONSTANTPOOLNAME, selector, signature);
    }
    
    protected void invokeJavaLangReflectFieldSetter(final int typeID) {
        char[] selector = null;
        char[] signature = null;
        int receiverAndArgsSize = 0;
        switch (typeID) {
            case 10: {
                selector = ConstantPool.SET_INT_METHOD_NAME;
                signature = ConstantPool.SET_INT_METHOD_SIGNATURE;
                receiverAndArgsSize = 3;
                break;
            }
            case 3: {
                selector = ConstantPool.SET_BYTE_METHOD_NAME;
                signature = ConstantPool.SET_BYTE_METHOD_SIGNATURE;
                receiverAndArgsSize = 3;
                break;
            }
            case 4: {
                selector = ConstantPool.SET_SHORT_METHOD_NAME;
                signature = ConstantPool.SET_SHORT_METHOD_SIGNATURE;
                receiverAndArgsSize = 3;
                break;
            }
            case 7: {
                selector = ConstantPool.SET_LONG_METHOD_NAME;
                signature = ConstantPool.SET_LONG_METHOD_SIGNATURE;
                receiverAndArgsSize = 4;
                break;
            }
            case 9: {
                selector = ConstantPool.SET_FLOAT_METHOD_NAME;
                signature = ConstantPool.SET_FLOAT_METHOD_SIGNATURE;
                receiverAndArgsSize = 3;
                break;
            }
            case 8: {
                selector = ConstantPool.SET_DOUBLE_METHOD_NAME;
                signature = ConstantPool.SET_DOUBLE_METHOD_SIGNATURE;
                receiverAndArgsSize = 4;
                break;
            }
            case 2: {
                selector = ConstantPool.SET_CHAR_METHOD_NAME;
                signature = ConstantPool.SET_CHAR_METHOD_SIGNATURE;
                receiverAndArgsSize = 3;
                break;
            }
            case 5: {
                selector = ConstantPool.SET_BOOLEAN_METHOD_NAME;
                signature = ConstantPool.SET_BOOLEAN_METHOD_SIGNATURE;
                receiverAndArgsSize = 3;
                break;
            }
            default: {
                selector = ConstantPool.SET_OBJECT_METHOD_NAME;
                signature = ConstantPool.SET_OBJECT_METHOD_SIGNATURE;
                receiverAndArgsSize = 3;
                break;
            }
        }
        this.invoke((byte)(-74), receiverAndArgsSize, 0, ConstantPool.JAVALANGREFLECTFIELD_CONSTANTPOOLNAME, selector, signature);
    }
    
    public void invokeJavaLangReflectMethodInvoke() {
        this.invoke((byte)(-74), 3, 1, ConstantPool.JAVALANGREFLECTMETHOD_CONSTANTPOOLNAME, ConstantPool.INVOKE_METHOD_METHOD_NAME, ConstantPool.INVOKE_METHOD_METHOD_SIGNATURE);
    }
    
    public void invokeJavaUtilIteratorHasNext() {
        this.invoke((byte)(-71), 1, 1, ConstantPool.JavaUtilIteratorConstantPoolName, ConstantPool.HasNext, ConstantPool.HasNextSignature);
    }
    
    public void invokeJavaUtilIteratorNext() {
        this.invoke((byte)(-71), 1, 1, ConstantPool.JavaUtilIteratorConstantPoolName, ConstantPool.Next, ConstantPool.NextSignature);
    }
    
    public void invokeNoClassDefFoundErrorStringConstructor() {
        this.invoke((byte)(-73), 2, 0, ConstantPool.JavaLangNoClassDefFoundErrorConstantPoolName, ConstantPool.Init, ConstantPool.StringConstructorSignature);
    }
    
    public void invokeObjectGetClass() {
        this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangObjectConstantPoolName, ConstantPool.GetClass, ConstantPool.GetClassSignature);
    }
    
    public void invokeStringConcatenationAppendForType(final int typeID) {
        char[] declaringClass = null;
        final char[] selector = ConstantPool.Append;
        char[] signature = null;
        int receiverAndArgsSize = 0;
        switch (typeID) {
            case 3:
            case 4:
            case 10: {
                if (this.targetLevel >= 3211264L) {
                    declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
                    signature = ConstantPool.StringBuilderAppendIntSignature;
                }
                else {
                    declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
                    signature = ConstantPool.StringBufferAppendIntSignature;
                }
                receiverAndArgsSize = 2;
                break;
            }
            case 7: {
                if (this.targetLevel >= 3211264L) {
                    declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
                    signature = ConstantPool.StringBuilderAppendLongSignature;
                }
                else {
                    declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
                    signature = ConstantPool.StringBufferAppendLongSignature;
                }
                receiverAndArgsSize = 3;
                break;
            }
            case 9: {
                if (this.targetLevel >= 3211264L) {
                    declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
                    signature = ConstantPool.StringBuilderAppendFloatSignature;
                }
                else {
                    declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
                    signature = ConstantPool.StringBufferAppendFloatSignature;
                }
                receiverAndArgsSize = 2;
                break;
            }
            case 8: {
                if (this.targetLevel >= 3211264L) {
                    declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
                    signature = ConstantPool.StringBuilderAppendDoubleSignature;
                }
                else {
                    declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
                    signature = ConstantPool.StringBufferAppendDoubleSignature;
                }
                receiverAndArgsSize = 3;
                break;
            }
            case 2: {
                if (this.targetLevel >= 3211264L) {
                    declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
                    signature = ConstantPool.StringBuilderAppendCharSignature;
                }
                else {
                    declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
                    signature = ConstantPool.StringBufferAppendCharSignature;
                }
                receiverAndArgsSize = 2;
                break;
            }
            case 5: {
                if (this.targetLevel >= 3211264L) {
                    declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
                    signature = ConstantPool.StringBuilderAppendBooleanSignature;
                }
                else {
                    declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
                    signature = ConstantPool.StringBufferAppendBooleanSignature;
                }
                receiverAndArgsSize = 2;
                break;
            }
            case 11: {
                if (this.targetLevel >= 3211264L) {
                    declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
                    signature = ConstantPool.StringBuilderAppendStringSignature;
                }
                else {
                    declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
                    signature = ConstantPool.StringBufferAppendStringSignature;
                }
                receiverAndArgsSize = 2;
                break;
            }
            default: {
                if (this.targetLevel >= 3211264L) {
                    declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
                    signature = ConstantPool.StringBuilderAppendObjectSignature;
                }
                else {
                    declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
                    signature = ConstantPool.StringBufferAppendObjectSignature;
                }
                receiverAndArgsSize = 2;
                break;
            }
        }
        this.invoke((byte)(-74), receiverAndArgsSize, 1, declaringClass, selector, signature);
    }
    
    public void invokeStringConcatenationDefaultConstructor() {
        char[] declaringClass;
        if (this.targetLevel < 3211264L) {
            declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
        }
        else {
            declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
        }
        this.invoke((byte)(-73), 1, 0, declaringClass, ConstantPool.Init, ConstantPool.DefaultConstructorSignature);
    }
    
    public void invokeStringConcatenationStringConstructor() {
        char[] declaringClass;
        if (this.targetLevel < 3211264L) {
            declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
        }
        else {
            declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
        }
        this.invoke((byte)(-73), 2, 0, declaringClass, ConstantPool.Init, ConstantPool.StringConstructorSignature);
    }
    
    public void invokeStringConcatenationToString() {
        char[] declaringClass;
        if (this.targetLevel < 3211264L) {
            declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
        }
        else {
            declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
        }
        this.invoke((byte)(-74), 1, 1, declaringClass, ConstantPool.ToString, ConstantPool.ToStringSignature);
    }
    
    public void invokeStringEquals() {
        this.invoke((byte)(-74), 2, 1, ConstantPool.JavaLangStringConstantPoolName, ConstantPool.Equals, ConstantPool.EqualsSignature);
    }
    
    public void invokeObjectEquals() {
        this.invoke((byte)(-74), 2, 1, ConstantPool.JavaLangObjectConstantPoolName, ConstantPool.Equals, ConstantPool.EqualsSignature);
    }
    
    public void invokeStringHashCode() {
        this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangStringConstantPoolName, ConstantPool.HashCode, ConstantPool.HashCodeSignature);
    }
    
    public void invokeStringIntern() {
        this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangStringConstantPoolName, ConstantPool.Intern, ConstantPool.InternSignature);
    }
    
    public void invokeStringValueOf(final int typeID) {
        char[] signature = null;
        int receiverAndArgsSize = 0;
        switch (typeID) {
            case 3:
            case 4:
            case 10: {
                signature = ConstantPool.ValueOfIntSignature;
                receiverAndArgsSize = 1;
                break;
            }
            case 7: {
                signature = ConstantPool.ValueOfLongSignature;
                receiverAndArgsSize = 2;
                break;
            }
            case 9: {
                signature = ConstantPool.ValueOfFloatSignature;
                receiverAndArgsSize = 1;
                break;
            }
            case 8: {
                signature = ConstantPool.ValueOfDoubleSignature;
                receiverAndArgsSize = 2;
                break;
            }
            case 2: {
                signature = ConstantPool.ValueOfCharSignature;
                receiverAndArgsSize = 1;
                break;
            }
            case 5: {
                signature = ConstantPool.ValueOfBooleanSignature;
                receiverAndArgsSize = 1;
                break;
            }
            case 0:
            case 1:
            case 11:
            case 12: {
                signature = ConstantPool.ValueOfObjectSignature;
                receiverAndArgsSize = 1;
                break;
            }
            default: {
                return;
            }
        }
        this.invoke((byte)(-72), receiverAndArgsSize, 1, ConstantPool.JavaLangStringConstantPoolName, ConstantPool.ValueOf, signature);
    }
    
    public void invokeSystemArraycopy() {
        this.invoke((byte)(-72), 5, 0, ConstantPool.JavaLangSystemConstantPoolName, ConstantPool.ArrayCopy, ConstantPool.ArrayCopySignature);
    }
    
    public void invokeThrowableGetMessage() {
        this.invoke((byte)(-74), 1, 1, ConstantPool.JavaLangThrowableConstantPoolName, ConstantPool.GetMessage, ConstantPool.GetMessageSignature);
    }
    
    public void ior() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -128;
    }
    
    public void irem() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 112;
    }
    
    public void ireturn() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -84;
        this.lastAbruptCompletion = this.position;
    }
    
    public boolean isDefinitelyAssigned(final Scope scope, final int initStateIndex, final LocalVariableBinding local) {
        if ((local.tagBits & 0x400L) != 0x0L) {
            return true;
        }
        if (initStateIndex == -1) {
            return false;
        }
        final int localPosition = local.id + this.maxFieldCount;
        final MethodScope methodScope = scope.methodScope();
        if (localPosition < 64) {
            return (methodScope.definiteInits[initStateIndex] & 1L << localPosition) != 0x0L;
        }
        final long[] extraInits = methodScope.extraDefiniteInits[initStateIndex];
        final int vectorIndex;
        return extraInits != null && (vectorIndex = localPosition / 64 - 1) < extraInits.length && (extraInits[vectorIndex] & 1L << localPosition % 64) != 0x0L;
    }
    
    public void ishl() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 120;
    }
    
    public void ishr() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 122;
    }
    
    public void istore(final int iArg) {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.maxLocals <= iArg) {
            this.maxLocals = iArg + 1;
        }
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 54;
            this.writeUnsignedShort(iArg);
        }
        else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 54;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }
    
    public void istore_0() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.maxLocals == 0) {
            this.maxLocals = 1;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 59;
    }
    
    public void istore_1() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.maxLocals <= 1) {
            this.maxLocals = 2;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 60;
    }
    
    public void istore_2() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.maxLocals <= 2) {
            this.maxLocals = 3;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 61;
    }
    
    public void istore_3() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.maxLocals <= 3) {
            this.maxLocals = 4;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 62;
    }
    
    public void isub() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 100;
    }
    
    public void iushr() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 124;
    }
    
    public void ixor() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -126;
    }
    
    public final void jsr(final BranchLabel lbl) {
        if (this.wideMode) {
            this.jsr_w(lbl);
            return;
        }
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -88;
        lbl.branch();
    }
    
    public final void jsr_w(final BranchLabel lbl) {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -55;
        lbl.branchWide();
    }
    
    public void l2d() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -118;
    }
    
    public void l2f() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -119;
    }
    
    public void l2i() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -120;
    }
    
    public void ladd() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 97;
    }
    
    public void laload() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 47;
    }
    
    public void land() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 127;
    }
    
    public void lastore() {
        this.countLabels = 0;
        this.stackDepth -= 4;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 80;
    }
    
    public void lcmp() {
        this.countLabels = 0;
        this.stackDepth -= 3;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -108;
    }
    
    public void lconst_0() {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 9;
    }
    
    public void lconst_1() {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 10;
    }
    
    public void ldc(final float constant) {
        this.countLabels = 0;
        final int index = this.constantPool.literalIndex(constant);
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (index > 255) {
            if (this.classFileOffset + 2 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = 19;
            this.writeUnsignedShort(index);
        }
        else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 18;
            this.bCodeStream[this.classFileOffset++] = (byte)index;
        }
    }
    
    public void ldc(final int constant) {
        this.countLabels = 0;
        final int index = this.constantPool.literalIndex(constant);
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (index > 255) {
            if (this.classFileOffset + 2 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = 19;
            this.writeUnsignedShort(index);
        }
        else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 18;
            this.bCodeStream[this.classFileOffset++] = (byte)index;
        }
    }
    
    public void ldc(final String constant) {
        this.countLabels = 0;
        final int currentCodeStreamPosition = this.position;
        final char[] constantChars = constant.toCharArray();
        int index = this.constantPool.literalIndexForLdc(constantChars);
        if (index > 0) {
            this.ldcForIndex(index);
        }
        else {
            this.position = currentCodeStreamPosition;
            int i = 0;
            int length = 0;
            final int constantLength = constant.length();
            byte[] utf8encoding = new byte[Math.min(constantLength + 100, 65535)];
            int utf8encodingLength = 0;
            while (length < 65532 && i < constantLength) {
                final char current = constantChars[i];
                if (length + 3 > (utf8encodingLength = utf8encoding.length)) {
                    System.arraycopy(utf8encoding, 0, utf8encoding = new byte[Math.min(utf8encodingLength + 100, 65535)], 0, length);
                }
                if (current >= '\u0001' && current <= '\u007f') {
                    utf8encoding[length++] = (byte)current;
                }
                else if (current > '\u07ff') {
                    utf8encoding[length++] = (byte)(0xE0 | (current >> 12 & 0xF));
                    utf8encoding[length++] = (byte)(0x80 | (current >> 6 & 0x3F));
                    utf8encoding[length++] = (byte)(0x80 | (current & '?'));
                }
                else {
                    utf8encoding[length++] = (byte)(0xC0 | (current >> 6 & 0x1F));
                    utf8encoding[length++] = (byte)(0x80 | (current & '?'));
                }
                ++i;
            }
            this.newStringContatenation();
            this.dup();
            char[] subChars = new char[i];
            System.arraycopy(constantChars, 0, subChars, 0, i);
            System.arraycopy(utf8encoding, 0, utf8encoding = new byte[length], 0, length);
            index = this.constantPool.literalIndex(subChars, utf8encoding);
            this.ldcForIndex(index);
            this.invokeStringConcatenationStringConstructor();
            while (i < constantLength) {
                length = 0;
                utf8encoding = new byte[Math.min(constantLength - i + 100, 65535)];
                final int startIndex = i;
                while (length < 65532 && i < constantLength) {
                    final char current2 = constantChars[i];
                    if (length + 3 > (utf8encodingLength = utf8encoding.length)) {
                        System.arraycopy(utf8encoding, 0, utf8encoding = new byte[Math.min(utf8encodingLength + 100, 65535)], 0, length);
                    }
                    if (current2 >= '\u0001' && current2 <= '\u007f') {
                        utf8encoding[length++] = (byte)current2;
                    }
                    else if (current2 > '\u07ff') {
                        utf8encoding[length++] = (byte)(0xE0 | (current2 >> 12 & 0xF));
                        utf8encoding[length++] = (byte)(0x80 | (current2 >> 6 & 0x3F));
                        utf8encoding[length++] = (byte)(0x80 | (current2 & '?'));
                    }
                    else {
                        utf8encoding[length++] = (byte)(0xC0 | (current2 >> 6 & 0x1F));
                        utf8encoding[length++] = (byte)(0x80 | (current2 & '?'));
                    }
                    ++i;
                }
                final int newCharLength = i - startIndex;
                subChars = new char[newCharLength];
                System.arraycopy(constantChars, startIndex, subChars, 0, newCharLength);
                System.arraycopy(utf8encoding, 0, utf8encoding = new byte[length], 0, length);
                index = this.constantPool.literalIndex(subChars, utf8encoding);
                this.ldcForIndex(index);
                this.invokeStringConcatenationAppendForType(11);
            }
            this.invokeStringConcatenationToString();
            this.invokeStringIntern();
        }
    }
    
    public void ldc(final TypeBinding typeBinding) {
        this.countLabels = 0;
        final int index = this.constantPool.literalIndexForType(typeBinding);
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (index > 255) {
            if (this.classFileOffset + 2 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = 19;
            this.writeUnsignedShort(index);
        }
        else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 18;
            this.bCodeStream[this.classFileOffset++] = (byte)index;
        }
    }
    
    public void ldc2_w(final double constant) {
        this.countLabels = 0;
        final int index = this.constantPool.literalIndex(constant);
        this.stackDepth += 2;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 20;
        this.writeUnsignedShort(index);
    }
    
    public void ldc2_w(final long constant) {
        this.countLabels = 0;
        final int index = this.constantPool.literalIndex(constant);
        this.stackDepth += 2;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 20;
        this.writeUnsignedShort(index);
    }
    
    public void ldcForIndex(final int index) {
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (index > 255) {
            if (this.classFileOffset + 2 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = 19;
            this.writeUnsignedShort(index);
        }
        else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 18;
            this.bCodeStream[this.classFileOffset++] = (byte)index;
        }
    }
    
    public void ldiv() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 109;
    }
    
    public void lload(final int iArg) {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.maxLocals <= iArg + 1) {
            this.maxLocals = iArg + 2;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 22;
            this.writeUnsignedShort(iArg);
        }
        else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 22;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }
    
    public void lload_0() {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.maxLocals < 2) {
            this.maxLocals = 2;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 30;
    }
    
    public void lload_1() {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.maxLocals < 3) {
            this.maxLocals = 3;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 31;
    }
    
    public void lload_2() {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.maxLocals < 4) {
            this.maxLocals = 4;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 32;
    }
    
    public void lload_3() {
        this.countLabels = 0;
        this.stackDepth += 2;
        if (this.maxLocals < 5) {
            this.maxLocals = 5;
        }
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 33;
    }
    
    public void lmul() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 105;
    }
    
    public void lneg() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 117;
    }
    
    public final void load(final LocalVariableBinding localBinding) {
        this.load(localBinding.type, localBinding.resolvedPosition);
    }
    
    protected final void load(final TypeBinding typeBinding, final int resolvedPosition) {
        this.countLabels = 0;
        Label_0397: {
            switch (typeBinding.id) {
                case 2:
                case 3:
                case 4:
                case 5:
                case 10: {
                    switch (resolvedPosition) {
                        case 0: {
                            this.iload_0();
                            break Label_0397;
                        }
                        case 1: {
                            this.iload_1();
                            break Label_0397;
                        }
                        case 2: {
                            this.iload_2();
                            break Label_0397;
                        }
                        case 3: {
                            this.iload_3();
                            break Label_0397;
                        }
                        default: {
                            this.iload(resolvedPosition);
                            break Label_0397;
                        }
                    }
                    break;
                }
                case 9: {
                    switch (resolvedPosition) {
                        case 0: {
                            this.fload_0();
                            break Label_0397;
                        }
                        case 1: {
                            this.fload_1();
                            break Label_0397;
                        }
                        case 2: {
                            this.fload_2();
                            break Label_0397;
                        }
                        case 3: {
                            this.fload_3();
                            break Label_0397;
                        }
                        default: {
                            this.fload(resolvedPosition);
                            break Label_0397;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (resolvedPosition) {
                        case 0: {
                            this.lload_0();
                            break Label_0397;
                        }
                        case 1: {
                            this.lload_1();
                            break Label_0397;
                        }
                        case 2: {
                            this.lload_2();
                            break Label_0397;
                        }
                        case 3: {
                            this.lload_3();
                            break Label_0397;
                        }
                        default: {
                            this.lload(resolvedPosition);
                            break Label_0397;
                        }
                    }
                    break;
                }
                case 8: {
                    switch (resolvedPosition) {
                        case 0: {
                            this.dload_0();
                            break Label_0397;
                        }
                        case 1: {
                            this.dload_1();
                            break Label_0397;
                        }
                        case 2: {
                            this.dload_2();
                            break Label_0397;
                        }
                        case 3: {
                            this.dload_3();
                            break Label_0397;
                        }
                        default: {
                            this.dload(resolvedPosition);
                            break Label_0397;
                        }
                    }
                    break;
                }
                default: {
                    switch (resolvedPosition) {
                        case 0: {
                            this.aload_0();
                            break Label_0397;
                        }
                        case 1: {
                            this.aload_1();
                            break Label_0397;
                        }
                        case 2: {
                            this.aload_2();
                            break Label_0397;
                        }
                        case 3: {
                            this.aload_3();
                            break Label_0397;
                        }
                        default: {
                            this.aload(resolvedPosition);
                            break Label_0397;
                        }
                    }
                    break;
                }
            }
        }
    }
    
    public void lookupswitch(final CaseLabel defaultLabel, final int[] keys, final int[] sortedIndexes, final CaseLabel[] casesLabel) {
        this.countLabels = 0;
        --this.stackDepth;
        final int length = keys.length;
        final int pos = this.position;
        defaultLabel.placeInstruction();
        for (int i = 0; i < length; ++i) {
            casesLabel[i].placeInstruction();
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -85;
        for (int i = 3 - (pos & 0x3); i > 0; --i) {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = 0;
        }
        defaultLabel.branch();
        this.writeSignedWord(length);
        for (int i = 0; i < length; ++i) {
            this.writeSignedWord(keys[sortedIndexes[i]]);
            casesLabel[sortedIndexes[i]].branch();
        }
    }
    
    public void lor() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -127;
    }
    
    public void lrem() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 113;
    }
    
    public void lreturn() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -83;
        this.lastAbruptCompletion = this.position;
    }
    
    public void lshl() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 121;
    }
    
    public void lshr() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 123;
    }
    
    public void lstore(final int iArg) {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.maxLocals <= iArg + 1) {
            this.maxLocals = iArg + 2;
        }
        if (iArg > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = 55;
            this.writeUnsignedShort(iArg);
        }
        else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = 55;
            this.bCodeStream[this.classFileOffset++] = (byte)iArg;
        }
    }
    
    public void lstore_0() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.maxLocals < 2) {
            this.maxLocals = 2;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 63;
    }
    
    public void lstore_1() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.maxLocals < 3) {
            this.maxLocals = 3;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 64;
    }
    
    public void lstore_2() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.maxLocals < 4) {
            this.maxLocals = 4;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 65;
    }
    
    public void lstore_3() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.maxLocals < 5) {
            this.maxLocals = 5;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 66;
    }
    
    public void lsub() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 101;
    }
    
    public void lushr() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 125;
    }
    
    public void lxor() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -125;
    }
    
    public void monitorenter() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -62;
    }
    
    public void monitorexit() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -61;
    }
    
    public void multianewarray(final TypeReference typeReference, final TypeBinding typeBinding, final int dimensions, final ArrayAllocationExpression allocationExpression) {
        this.countLabels = 0;
        this.stackDepth += 1 - dimensions;
        if (this.classFileOffset + 3 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        this.position += 2;
        this.bCodeStream[this.classFileOffset++] = -59;
        this.writeUnsignedShort(this.constantPool.literalIndexForType(typeBinding));
        this.bCodeStream[this.classFileOffset++] = (byte)dimensions;
    }
    
    public void new_(final TypeBinding typeBinding) {
        this.new_(null, typeBinding);
    }
    
    public void new_(final TypeReference typeReference, final TypeBinding typeBinding) {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -69;
        this.writeUnsignedShort(this.constantPool.literalIndexForType(typeBinding));
    }
    
    public void newarray(final int array_Type) {
        this.countLabels = 0;
        if (this.classFileOffset + 1 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        this.position += 2;
        this.bCodeStream[this.classFileOffset++] = -68;
        this.bCodeStream[this.classFileOffset++] = (byte)array_Type;
    }
    
    public void newArray(final ArrayBinding arrayBinding) {
        this.newArray(null, null, arrayBinding);
    }
    
    public void newArray(final TypeReference typeReference, final ArrayAllocationExpression allocationExpression, final ArrayBinding arrayBinding) {
        final TypeBinding component = arrayBinding.elementsType();
        switch (component.id) {
            case 10: {
                this.newarray(10);
                break;
            }
            case 3: {
                this.newarray(8);
                break;
            }
            case 5: {
                this.newarray(4);
                break;
            }
            case 4: {
                this.newarray(9);
                break;
            }
            case 2: {
                this.newarray(5);
                break;
            }
            case 7: {
                this.newarray(11);
                break;
            }
            case 9: {
                this.newarray(6);
                break;
            }
            case 8: {
                this.newarray(7);
                break;
            }
            default: {
                this.anewarray(component);
                break;
            }
        }
    }
    
    public void newJavaLangAssertionError() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -69;
        this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangAssertionErrorConstantPoolName));
    }
    
    public void newJavaLangError() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -69;
        this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangErrorConstantPoolName));
    }
    
    public void newNoClassDefFoundError() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -69;
        this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangNoClassDefFoundErrorConstantPoolName));
    }
    
    public void newStringContatenation() {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -69;
        if (this.targetLevel >= 3211264L) {
            this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangStringBuilderConstantPoolName));
        }
        else {
            this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangStringBufferConstantPoolName));
        }
    }
    
    public void newWrapperFor(final int typeID) {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset + 2 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -69;
        switch (typeID) {
            case 10: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangIntegerConstantPoolName));
                break;
            }
            case 5: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangBooleanConstantPoolName));
                break;
            }
            case 3: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangByteConstantPoolName));
                break;
            }
            case 2: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangCharacterConstantPoolName));
                break;
            }
            case 9: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangFloatConstantPoolName));
                break;
            }
            case 8: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangDoubleConstantPoolName));
                break;
            }
            case 4: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangShortConstantPoolName));
                break;
            }
            case 7: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangLongConstantPoolName));
                break;
            }
            case 6: {
                this.writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangVoidConstantPoolName));
                break;
            }
        }
    }
    
    public void nop() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 0;
    }
    
    public void optimizeBranch(final int oldPosition, final BranchLabel lbl) {
        for (int i = 0; i < this.countLabels; ++i) {
            final BranchLabel label = this.labels[i];
            if (oldPosition == label.position) {
                label.position = this.position;
                if (label instanceof CaseLabel) {
                    final int offset = this.position - ((CaseLabel)label).instructionPosition;
                    final int[] forwardRefs = label.forwardReferences();
                    for (int j = 0, length = label.forwardReferenceCount(); j < length; ++j) {
                        final int forwardRef = forwardRefs[j];
                        this.writeSignedWord(forwardRef, offset);
                    }
                }
                else {
                    final int[] forwardRefs2 = label.forwardReferences();
                    for (int k = 0, length2 = label.forwardReferenceCount(); k < length2; ++k) {
                        final int forwardRef2 = forwardRefs2[k];
                        this.writePosition(lbl, forwardRef2);
                    }
                }
            }
        }
    }
    
    public void pop() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 87;
    }
    
    public void pop2() {
        this.countLabels = 0;
        this.stackDepth -= 2;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 88;
    }
    
    public void pushExceptionOnStack(final TypeBinding binding) {
        this.stackDepth = 1;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
    }
    
    public void pushOnStack(final TypeBinding binding) {
        if (++this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
    }
    
    public void record(final LocalVariableBinding local) {
        if ((this.generateAttributes & 0x1C) == 0x0) {
            return;
        }
        if (this.allLocalsCounter == this.locals.length) {
            System.arraycopy(this.locals, 0, this.locals = new LocalVariableBinding[this.allLocalsCounter + 10], 0, this.allLocalsCounter);
        }
        this.locals[this.allLocalsCounter++] = local;
        local.initializationPCs = new int[4];
        local.initializationCount = 0;
    }
    
    public void recordExpressionType(final TypeBinding typeBinding) {
    }
    
    public void recordPositionsFrom(final int startPC, final int sourcePos) {
        this.recordPositionsFrom(startPC, sourcePos, false);
    }
    
    public void recordPositionsFrom(final int startPC, final int sourcePos, final boolean widen) {
        if ((this.generateAttributes & 0x2) == 0x0 || sourcePos == 0 || (startPC == this.position && !widen) || startPC > this.position) {
            return;
        }
        if (this.pcToSourceMapSize + 4 > this.pcToSourceMap.length) {
            System.arraycopy(this.pcToSourceMap, 0, this.pcToSourceMap = new int[this.pcToSourceMapSize << 1], 0, this.pcToSourceMapSize);
        }
        if (this.pcToSourceMapSize > 0) {
            final int previousLineNumber = this.pcToSourceMap[this.pcToSourceMapSize - 1];
            int lineNumber;
            if (this.lineNumberStart == this.lineNumberEnd) {
                lineNumber = this.lineNumberStart;
            }
            else {
                final int[] lineSeparatorPositions2 = this.lineSeparatorPositions;
                final int length = lineSeparatorPositions2.length;
                if (previousLineNumber == 1) {
                    if (sourcePos < lineSeparatorPositions2[0]) {
                        lineNumber = 1;
                        if (startPC < this.pcToSourceMap[this.pcToSourceMapSize - 2]) {
                            int insertionIndex = insertionIndex(this.pcToSourceMap, this.pcToSourceMapSize, startPC);
                            if (insertionIndex != -1 && (insertionIndex <= 1 || this.pcToSourceMap[insertionIndex - 1] != lineNumber)) {
                                if (this.pcToSourceMapSize > 4 && this.pcToSourceMap[this.pcToSourceMapSize - 4] > startPC) {
                                    System.arraycopy(this.pcToSourceMap, insertionIndex, this.pcToSourceMap, insertionIndex + 2, this.pcToSourceMapSize - 2 - insertionIndex);
                                    this.pcToSourceMap[insertionIndex++] = startPC;
                                    this.pcToSourceMap[insertionIndex] = lineNumber;
                                }
                                else {
                                    this.pcToSourceMap[this.pcToSourceMapSize - 2] = startPC;
                                }
                            }
                        }
                        this.lastEntryPC = this.position;
                        return;
                    }
                    if (length == 1 || sourcePos < lineSeparatorPositions2[1]) {
                        lineNumber = 2;
                        if (startPC <= this.lastEntryPC) {
                            int insertionIndex = insertionIndex(this.pcToSourceMap, this.pcToSourceMapSize, startPC);
                            if (insertionIndex != -1) {
                                final int existingEntryIndex = this.indexOfSameLineEntrySincePC(startPC, lineNumber);
                                if (existingEntryIndex != -1) {
                                    this.pcToSourceMap[existingEntryIndex] = startPC;
                                }
                                else if (insertionIndex < 1 || this.pcToSourceMap[insertionIndex - 1] != lineNumber) {
                                    System.arraycopy(this.pcToSourceMap, insertionIndex, this.pcToSourceMap, insertionIndex + 2, this.pcToSourceMapSize - insertionIndex);
                                    this.pcToSourceMap[insertionIndex++] = startPC;
                                    this.pcToSourceMap[insertionIndex] = lineNumber;
                                    this.pcToSourceMapSize += 2;
                                }
                            }
                            else if (this.position != this.lastEntryPC) {
                                if (this.lastEntryPC == startPC || this.lastEntryPC == this.pcToSourceMap[this.pcToSourceMapSize - 2]) {
                                    this.pcToSourceMap[this.pcToSourceMapSize - 1] = lineNumber;
                                }
                                else {
                                    this.pcToSourceMap[this.pcToSourceMapSize++] = this.lastEntryPC;
                                    this.pcToSourceMap[this.pcToSourceMapSize++] = lineNumber;
                                }
                            }
                            else if (this.pcToSourceMap[this.pcToSourceMapSize - 1] < lineNumber && widen) {
                                this.pcToSourceMap[this.pcToSourceMapSize - 1] = lineNumber;
                            }
                        }
                        else {
                            this.pcToSourceMap[this.pcToSourceMapSize++] = startPC;
                            this.pcToSourceMap[this.pcToSourceMapSize++] = lineNumber;
                        }
                        this.lastEntryPC = this.position;
                        return;
                    }
                    lineNumber = Util.getLineNumber(sourcePos, this.lineSeparatorPositions, this.lineNumberStart - 1, this.lineNumberEnd - 1);
                }
                else if (previousLineNumber < length) {
                    if (lineSeparatorPositions2[previousLineNumber - 2] < sourcePos) {
                        if (sourcePos < lineSeparatorPositions2[previousLineNumber - 1]) {
                            lineNumber = previousLineNumber;
                            if (startPC < this.pcToSourceMap[this.pcToSourceMapSize - 2]) {
                                int insertionIndex = insertionIndex(this.pcToSourceMap, this.pcToSourceMapSize, startPC);
                                if (insertionIndex != -1 && (insertionIndex <= 1 || this.pcToSourceMap[insertionIndex - 1] != lineNumber)) {
                                    if (this.pcToSourceMapSize > 4 && this.pcToSourceMap[this.pcToSourceMapSize - 4] > startPC) {
                                        System.arraycopy(this.pcToSourceMap, insertionIndex, this.pcToSourceMap, insertionIndex + 2, this.pcToSourceMapSize - 2 - insertionIndex);
                                        this.pcToSourceMap[insertionIndex++] = startPC;
                                        this.pcToSourceMap[insertionIndex] = lineNumber;
                                    }
                                    else {
                                        this.pcToSourceMap[this.pcToSourceMapSize - 2] = startPC;
                                    }
                                }
                            }
                            this.lastEntryPC = this.position;
                            return;
                        }
                        if (sourcePos < lineSeparatorPositions2[previousLineNumber]) {
                            lineNumber = previousLineNumber + 1;
                            if (startPC <= this.lastEntryPC) {
                                int insertionIndex = insertionIndex(this.pcToSourceMap, this.pcToSourceMapSize, startPC);
                                if (insertionIndex != -1) {
                                    final int existingEntryIndex = this.indexOfSameLineEntrySincePC(startPC, lineNumber);
                                    if (existingEntryIndex != -1) {
                                        this.pcToSourceMap[existingEntryIndex] = startPC;
                                    }
                                    else if (insertionIndex < 1 || this.pcToSourceMap[insertionIndex - 1] != lineNumber) {
                                        System.arraycopy(this.pcToSourceMap, insertionIndex, this.pcToSourceMap, insertionIndex + 2, this.pcToSourceMapSize - insertionIndex);
                                        this.pcToSourceMap[insertionIndex++] = startPC;
                                        this.pcToSourceMap[insertionIndex] = lineNumber;
                                        this.pcToSourceMapSize += 2;
                                    }
                                }
                                else if (this.position != this.lastEntryPC) {
                                    if (this.lastEntryPC == startPC || this.lastEntryPC == this.pcToSourceMap[this.pcToSourceMapSize - 2]) {
                                        this.pcToSourceMap[this.pcToSourceMapSize - 1] = lineNumber;
                                    }
                                    else {
                                        this.pcToSourceMap[this.pcToSourceMapSize++] = this.lastEntryPC;
                                        this.pcToSourceMap[this.pcToSourceMapSize++] = lineNumber;
                                    }
                                }
                                else if (this.pcToSourceMap[this.pcToSourceMapSize - 1] < lineNumber && widen) {
                                    this.pcToSourceMap[this.pcToSourceMapSize - 1] = lineNumber;
                                }
                            }
                            else {
                                this.pcToSourceMap[this.pcToSourceMapSize++] = startPC;
                                this.pcToSourceMap[this.pcToSourceMapSize++] = lineNumber;
                            }
                            this.lastEntryPC = this.position;
                            return;
                        }
                        lineNumber = Util.getLineNumber(sourcePos, this.lineSeparatorPositions, this.lineNumberStart - 1, this.lineNumberEnd - 1);
                    }
                    else {
                        lineNumber = Util.getLineNumber(sourcePos, this.lineSeparatorPositions, this.lineNumberStart - 1, this.lineNumberEnd - 1);
                    }
                }
                else {
                    if (lineSeparatorPositions2[length - 1] < sourcePos) {
                        lineNumber = length + 1;
                        if (startPC <= this.lastEntryPC) {
                            int insertionIndex = insertionIndex(this.pcToSourceMap, this.pcToSourceMapSize, startPC);
                            if (insertionIndex != -1) {
                                final int existingEntryIndex = this.indexOfSameLineEntrySincePC(startPC, lineNumber);
                                if (existingEntryIndex != -1) {
                                    this.pcToSourceMap[existingEntryIndex] = startPC;
                                }
                                else if (insertionIndex < 1 || this.pcToSourceMap[insertionIndex - 1] != lineNumber) {
                                    System.arraycopy(this.pcToSourceMap, insertionIndex, this.pcToSourceMap, insertionIndex + 2, this.pcToSourceMapSize - insertionIndex);
                                    this.pcToSourceMap[insertionIndex++] = startPC;
                                    this.pcToSourceMap[insertionIndex] = lineNumber;
                                    this.pcToSourceMapSize += 2;
                                }
                            }
                            else if (this.position != this.lastEntryPC) {
                                if (this.lastEntryPC == startPC || this.lastEntryPC == this.pcToSourceMap[this.pcToSourceMapSize - 2]) {
                                    this.pcToSourceMap[this.pcToSourceMapSize - 1] = lineNumber;
                                }
                                else {
                                    this.pcToSourceMap[this.pcToSourceMapSize++] = this.lastEntryPC;
                                    this.pcToSourceMap[this.pcToSourceMapSize++] = lineNumber;
                                }
                            }
                            else if (this.pcToSourceMap[this.pcToSourceMapSize - 1] < lineNumber && widen) {
                                this.pcToSourceMap[this.pcToSourceMapSize - 1] = lineNumber;
                            }
                        }
                        else {
                            this.pcToSourceMap[this.pcToSourceMapSize++] = startPC;
                            this.pcToSourceMap[this.pcToSourceMapSize++] = lineNumber;
                        }
                        this.lastEntryPC = this.position;
                        return;
                    }
                    lineNumber = Util.getLineNumber(sourcePos, this.lineSeparatorPositions, this.lineNumberStart - 1, this.lineNumberEnd - 1);
                }
            }
            if (previousLineNumber != lineNumber) {
                if (startPC <= this.lastEntryPC) {
                    int insertionIndex2 = insertionIndex(this.pcToSourceMap, this.pcToSourceMapSize, startPC);
                    if (insertionIndex2 != -1) {
                        final int existingEntryIndex2 = this.indexOfSameLineEntrySincePC(startPC, lineNumber);
                        if (existingEntryIndex2 != -1) {
                            this.pcToSourceMap[existingEntryIndex2] = startPC;
                        }
                        else if (insertionIndex2 < 1 || this.pcToSourceMap[insertionIndex2 - 1] != lineNumber) {
                            System.arraycopy(this.pcToSourceMap, insertionIndex2, this.pcToSourceMap, insertionIndex2 + 2, this.pcToSourceMapSize - insertionIndex2);
                            this.pcToSourceMap[insertionIndex2++] = startPC;
                            this.pcToSourceMap[insertionIndex2] = lineNumber;
                            this.pcToSourceMapSize += 2;
                        }
                    }
                    else if (this.position != this.lastEntryPC) {
                        if (this.lastEntryPC == startPC || this.lastEntryPC == this.pcToSourceMap[this.pcToSourceMapSize - 2]) {
                            this.pcToSourceMap[this.pcToSourceMapSize - 1] = lineNumber;
                        }
                        else {
                            this.pcToSourceMap[this.pcToSourceMapSize++] = this.lastEntryPC;
                            this.pcToSourceMap[this.pcToSourceMapSize++] = lineNumber;
                        }
                    }
                    else if (this.pcToSourceMap[this.pcToSourceMapSize - 1] < lineNumber && widen) {
                        this.pcToSourceMap[this.pcToSourceMapSize - 1] = lineNumber;
                    }
                }
                else {
                    this.pcToSourceMap[this.pcToSourceMapSize++] = startPC;
                    this.pcToSourceMap[this.pcToSourceMapSize++] = lineNumber;
                }
            }
            else if (startPC < this.pcToSourceMap[this.pcToSourceMapSize - 2]) {
                int insertionIndex2 = insertionIndex(this.pcToSourceMap, this.pcToSourceMapSize, startPC);
                if (insertionIndex2 != -1 && (insertionIndex2 <= 1 || this.pcToSourceMap[insertionIndex2 - 1] != lineNumber)) {
                    if (this.pcToSourceMapSize > 4 && this.pcToSourceMap[this.pcToSourceMapSize - 4] > startPC) {
                        System.arraycopy(this.pcToSourceMap, insertionIndex2, this.pcToSourceMap, insertionIndex2 + 2, this.pcToSourceMapSize - 2 - insertionIndex2);
                        this.pcToSourceMap[insertionIndex2++] = startPC;
                        this.pcToSourceMap[insertionIndex2] = lineNumber;
                    }
                    else {
                        this.pcToSourceMap[this.pcToSourceMapSize - 2] = startPC;
                    }
                }
            }
            this.lastEntryPC = this.position;
        }
        else {
            int lineNumber = 0;
            if (this.lineNumberStart == this.lineNumberEnd) {
                lineNumber = this.lineNumberStart;
            }
            else {
                lineNumber = Util.getLineNumber(sourcePos, this.lineSeparatorPositions, this.lineNumberStart - 1, this.lineNumberEnd - 1);
            }
            this.pcToSourceMap[this.pcToSourceMapSize++] = startPC;
            this.pcToSourceMap[this.pcToSourceMapSize++] = lineNumber;
            this.lastEntryPC = this.position;
        }
    }
    
    public void registerExceptionHandler(final ExceptionLabel anExceptionLabel) {
        final int length;
        if (this.exceptionLabelsCounter == (length = this.exceptionLabels.length)) {
            System.arraycopy(this.exceptionLabels, 0, this.exceptionLabels = new ExceptionLabel[length + 5], 0, length);
        }
        this.exceptionLabels[this.exceptionLabelsCounter++] = anExceptionLabel;
    }
    
    public void removeNotDefinitelyAssignedVariables(final Scope scope, final int initStateIndex) {
        if ((this.generateAttributes & 0x1C) == 0x0) {
            return;
        }
        for (int i = 0; i < this.visibleLocalsCount; ++i) {
            final LocalVariableBinding localBinding = this.visibleLocals[i];
            if (localBinding != null && !this.isDefinitelyAssigned(scope, initStateIndex, localBinding) && localBinding.initializationCount > 0) {
                localBinding.recordInitializationEndPC(this.position);
            }
        }
    }
    
    public void removeUnusedPcToSourceMapEntries() {
        if (this.pcToSourceMapSize != 0) {
            while (this.pcToSourceMapSize >= 2 && this.pcToSourceMap[this.pcToSourceMapSize - 2] > this.position) {
                this.pcToSourceMapSize -= 2;
            }
        }
    }
    
    public void removeVariable(final LocalVariableBinding localBinding) {
        if (localBinding == null) {
            return;
        }
        if (localBinding.initializationCount > 0) {
            localBinding.recordInitializationEndPC(this.position);
        }
        for (int i = this.visibleLocalsCount - 1; i >= 0; --i) {
            final LocalVariableBinding visibleLocal = this.visibleLocals[i];
            if (visibleLocal == localBinding) {
                this.visibleLocals[i] = null;
                return;
            }
        }
    }
    
    public void reset(final AbstractMethodDeclaration referenceMethod, final ClassFile targetClassFile) {
        this.init(targetClassFile);
        this.methodDeclaration = referenceMethod;
        this.lambdaExpression = null;
        final int[] lineSeparatorPositions2 = this.lineSeparatorPositions;
        if (lineSeparatorPositions2 != null) {
            final int length = lineSeparatorPositions2.length;
            final int lineSeparatorPositionsEnd = length - 1;
            if (referenceMethod.isClinit() || referenceMethod.isConstructor()) {
                this.lineNumberStart = 1;
                this.lineNumberEnd = ((length == 0) ? 1 : length);
            }
            else {
                final int start = Util.getLineNumber(referenceMethod.bodyStart, lineSeparatorPositions2, 0, lineSeparatorPositionsEnd);
                if ((this.lineNumberStart = start) > lineSeparatorPositionsEnd) {
                    this.lineNumberEnd = start;
                }
                else {
                    int end = Util.getLineNumber(referenceMethod.bodyEnd, lineSeparatorPositions2, start - 1, lineSeparatorPositionsEnd);
                    if (end >= lineSeparatorPositionsEnd) {
                        end = length;
                    }
                    this.lineNumberEnd = ((end == 0) ? 1 : end);
                }
            }
        }
        this.preserveUnusedLocals = referenceMethod.scope.compilerOptions().preserveAllLocalVariables;
        this.initializeMaxLocals(referenceMethod.binding);
    }
    
    public void reset(final LambdaExpression lambda, final ClassFile targetClassFile) {
        this.init(targetClassFile);
        this.lambdaExpression = lambda;
        this.methodDeclaration = null;
        final int[] lineSeparatorPositions2 = this.lineSeparatorPositions;
        if (lineSeparatorPositions2 != null) {
            final int length = lineSeparatorPositions2.length;
            final int lineSeparatorPositionsEnd = length - 1;
            final int start = Util.getLineNumber(lambda.body().sourceStart, lineSeparatorPositions2, 0, lineSeparatorPositionsEnd);
            if ((this.lineNumberStart = start) > lineSeparatorPositionsEnd) {
                this.lineNumberEnd = start;
            }
            else {
                int end = Util.getLineNumber(lambda.body().sourceEnd, lineSeparatorPositions2, start - 1, lineSeparatorPositionsEnd);
                if (end >= lineSeparatorPositionsEnd) {
                    end = length;
                }
                this.lineNumberEnd = ((end == 0) ? 1 : end);
            }
        }
        this.preserveUnusedLocals = lambda.scope.compilerOptions().preserveAllLocalVariables;
        this.initializeMaxLocals(lambda.binding);
    }
    
    public void reset(final ClassFile givenClassFile) {
        this.targetLevel = givenClassFile.targetJDK;
        final int produceAttributes = givenClassFile.produceAttributes;
        this.generateAttributes = produceAttributes;
        if ((produceAttributes & 0x2) != 0x0) {
            this.lineSeparatorPositions = givenClassFile.referenceBinding.scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions();
        }
        else {
            this.lineSeparatorPositions = null;
        }
    }
    
    public void resetForProblemClinit(final ClassFile targetClassFile) {
        this.init(targetClassFile);
        this.initializeMaxLocals(null);
    }
    
    public void resetInWideMode() {
        this.wideMode = true;
    }
    
    public void resetForCodeGenUnusedLocals() {
    }
    
    private final void resizeByteArray() {
        final int length = this.bCodeStream.length;
        int requiredSize = length + length;
        if (this.classFileOffset >= requiredSize) {
            requiredSize = this.classFileOffset + length;
        }
        System.arraycopy(this.bCodeStream, 0, this.bCodeStream = new byte[requiredSize], 0, length);
    }
    
    public final void ret(final int index) {
        this.countLabels = 0;
        if (index > 255) {
            if (this.classFileOffset + 3 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -60;
            this.bCodeStream[this.classFileOffset++] = -87;
            this.writeUnsignedShort(index);
        }
        else {
            if (this.classFileOffset + 1 >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            this.position += 2;
            this.bCodeStream[this.classFileOffset++] = -87;
            this.bCodeStream[this.classFileOffset++] = (byte)index;
        }
    }
    
    public void return_() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -79;
        this.lastAbruptCompletion = this.position;
    }
    
    public void saload() {
        this.countLabels = 0;
        --this.stackDepth;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 53;
    }
    
    public void sastore() {
        this.countLabels = 0;
        this.stackDepth -= 3;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 86;
    }
    
    public void sendOperator(final int operatorConstant, final int type_ID) {
        Label_0544: {
            switch (type_ID) {
                case 2:
                case 3:
                case 4:
                case 5:
                case 10: {
                    switch (operatorConstant) {
                        case 14: {
                            this.iadd();
                            break;
                        }
                        case 13: {
                            this.isub();
                            break;
                        }
                        case 15: {
                            this.imul();
                            break;
                        }
                        case 9: {
                            this.idiv();
                            break;
                        }
                        case 16: {
                            this.irem();
                            break;
                        }
                        case 10: {
                            this.ishl();
                            break;
                        }
                        case 17: {
                            this.ishr();
                            break;
                        }
                        case 19: {
                            this.iushr();
                            break;
                        }
                        case 2: {
                            this.iand();
                            break;
                        }
                        case 3: {
                            this.ior();
                            break;
                        }
                        case 8: {
                            this.ixor();
                            break;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (operatorConstant) {
                        case 14: {
                            this.ladd();
                            break;
                        }
                        case 13: {
                            this.lsub();
                            break;
                        }
                        case 15: {
                            this.lmul();
                            break;
                        }
                        case 9: {
                            this.ldiv();
                            break;
                        }
                        case 16: {
                            this.lrem();
                            break;
                        }
                        case 10: {
                            this.lshl();
                            break;
                        }
                        case 17: {
                            this.lshr();
                            break;
                        }
                        case 19: {
                            this.lushr();
                            break;
                        }
                        case 2: {
                            this.land();
                            break;
                        }
                        case 3: {
                            this.lor();
                            break;
                        }
                        case 8: {
                            this.lxor();
                            break;
                        }
                    }
                    break;
                }
                case 9: {
                    switch (operatorConstant) {
                        case 14: {
                            this.fadd();
                            break;
                        }
                        case 13: {
                            this.fsub();
                            break;
                        }
                        case 15: {
                            this.fmul();
                            break;
                        }
                        case 9: {
                            this.fdiv();
                            break;
                        }
                        case 16: {
                            this.frem();
                            break;
                        }
                    }
                    break;
                }
                case 8: {
                    switch (operatorConstant) {
                        case 14: {
                            this.dadd();
                            break Label_0544;
                        }
                        case 13: {
                            this.dsub();
                            break Label_0544;
                        }
                        case 15: {
                            this.dmul();
                            break Label_0544;
                        }
                        case 9: {
                            this.ddiv();
                            break Label_0544;
                        }
                        case 16: {
                            this.drem();
                            break Label_0544;
                        }
                    }
                    break;
                }
            }
        }
    }
    
    public void sipush(final int s) {
        this.countLabels = 0;
        ++this.stackDepth;
        if (this.stackDepth > this.stackMax) {
            this.stackMax = this.stackDepth;
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 17;
        this.writeSignedShort(s);
    }
    
    public void store(final LocalVariableBinding localBinding, final boolean valueRequired) {
        final int localPosition = localBinding.resolvedPosition;
        Label_0441: {
            switch (localBinding.type.id) {
                case 2:
                case 3:
                case 4:
                case 5:
                case 10: {
                    if (valueRequired) {
                        this.dup();
                    }
                    switch (localPosition) {
                        case 0: {
                            this.istore_0();
                            break Label_0441;
                        }
                        case 1: {
                            this.istore_1();
                            break Label_0441;
                        }
                        case 2: {
                            this.istore_2();
                            break Label_0441;
                        }
                        case 3: {
                            this.istore_3();
                            break Label_0441;
                        }
                        default: {
                            this.istore(localPosition);
                            break Label_0441;
                        }
                    }
                    break;
                }
                case 9: {
                    if (valueRequired) {
                        this.dup();
                    }
                    switch (localPosition) {
                        case 0: {
                            this.fstore_0();
                            break Label_0441;
                        }
                        case 1: {
                            this.fstore_1();
                            break Label_0441;
                        }
                        case 2: {
                            this.fstore_2();
                            break Label_0441;
                        }
                        case 3: {
                            this.fstore_3();
                            break Label_0441;
                        }
                        default: {
                            this.fstore(localPosition);
                            break Label_0441;
                        }
                    }
                    break;
                }
                case 8: {
                    if (valueRequired) {
                        this.dup2();
                    }
                    switch (localPosition) {
                        case 0: {
                            this.dstore_0();
                            break Label_0441;
                        }
                        case 1: {
                            this.dstore_1();
                            break Label_0441;
                        }
                        case 2: {
                            this.dstore_2();
                            break Label_0441;
                        }
                        case 3: {
                            this.dstore_3();
                            break Label_0441;
                        }
                        default: {
                            this.dstore(localPosition);
                            break Label_0441;
                        }
                    }
                    break;
                }
                case 7: {
                    if (valueRequired) {
                        this.dup2();
                    }
                    switch (localPosition) {
                        case 0: {
                            this.lstore_0();
                            break Label_0441;
                        }
                        case 1: {
                            this.lstore_1();
                            break Label_0441;
                        }
                        case 2: {
                            this.lstore_2();
                            break Label_0441;
                        }
                        case 3: {
                            this.lstore_3();
                            break Label_0441;
                        }
                        default: {
                            this.lstore(localPosition);
                            break Label_0441;
                        }
                    }
                    break;
                }
                default: {
                    if (valueRequired) {
                        this.dup();
                    }
                    switch (localPosition) {
                        case 0: {
                            this.astore_0();
                            break Label_0441;
                        }
                        case 1: {
                            this.astore_1();
                            break Label_0441;
                        }
                        case 2: {
                            this.astore_2();
                            break Label_0441;
                        }
                        case 3: {
                            this.astore_3();
                            break Label_0441;
                        }
                        default: {
                            this.astore(localPosition);
                            break Label_0441;
                        }
                    }
                    break;
                }
            }
        }
    }
    
    public void swap() {
        this.countLabels = 0;
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = 95;
    }
    
    public void tableswitch(final CaseLabel defaultLabel, final int low, final int high, final int[] keys, final int[] sortedIndexes, final CaseLabel[] casesLabel) {
        this.countLabels = 0;
        --this.stackDepth;
        final int length = casesLabel.length;
        final int pos = this.position;
        defaultLabel.placeInstruction();
        for (int i = 0; i < length; ++i) {
            casesLabel[i].placeInstruction();
        }
        if (this.classFileOffset >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        ++this.position;
        this.bCodeStream[this.classFileOffset++] = -86;
        for (int i = 3 - (pos & 0x3); i > 0; --i) {
            if (this.classFileOffset >= this.bCodeStream.length) {
                this.resizeByteArray();
            }
            ++this.position;
            this.bCodeStream[this.classFileOffset++] = 0;
        }
        defaultLabel.branch();
        this.writeSignedWord(low);
        this.writeSignedWord(high);
        int i = low;
        int j = low;
        while (true) {
            final int index;
            final int key = keys[index = sortedIndexes[j - low]];
            if (key == i) {
                casesLabel[index].branch();
                ++j;
                if (i == high) {
                    break;
                }
            }
            else {
                defaultLabel.branch();
            }
            ++i;
        }
    }
    
    public void throwAnyException(final LocalVariableBinding anyExceptionVariable) {
        this.load(anyExceptionVariable);
        this.athrow();
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer("( position:");
        buffer.append(this.position);
        buffer.append(",\nstackDepth:");
        buffer.append(this.stackDepth);
        buffer.append(",\nmaxStack:");
        buffer.append(this.stackMax);
        buffer.append(",\nmaxLocals:");
        buffer.append(this.maxLocals);
        buffer.append(")");
        return buffer.toString();
    }
    
    protected void writePosition(final BranchLabel label) {
        final int offset = label.position - this.position + 1;
        if (Math.abs(offset) > 32767 && !this.wideMode) {
            throw new AbortMethod(CodeStream.RESTART_IN_WIDE_MODE, (CategorizedProblem)null);
        }
        this.writeSignedShort(offset);
        final int[] forwardRefs = label.forwardReferences();
        for (int i = 0, max = label.forwardReferenceCount(); i < max; ++i) {
            this.writePosition(label, forwardRefs[i]);
        }
    }
    
    protected void writePosition(final BranchLabel label, final int forwardReference) {
        final int offset = label.position - forwardReference + 1;
        if (Math.abs(offset) > 32767 && !this.wideMode) {
            throw new AbortMethod(CodeStream.RESTART_IN_WIDE_MODE, (CategorizedProblem)null);
        }
        if (this.wideMode) {
            if ((label.tagBits & 0x1) != 0x0) {
                this.writeSignedWord(forwardReference, offset);
            }
            else {
                this.writeSignedShort(forwardReference, offset);
            }
        }
        else {
            this.writeSignedShort(forwardReference, offset);
        }
    }
    
    private final void writeSignedShort(final int value) {
        if (this.classFileOffset + 1 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        this.position += 2;
        this.bCodeStream[this.classFileOffset++] = (byte)(value >> 8);
        this.bCodeStream[this.classFileOffset++] = (byte)value;
    }
    
    private final void writeSignedShort(final int pos, final int value) {
        final int currentOffset = this.startingClassFileOffset + pos;
        if (currentOffset + 1 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        this.bCodeStream[currentOffset] = (byte)(value >> 8);
        this.bCodeStream[currentOffset + 1] = (byte)value;
    }
    
    protected final void writeSignedWord(final int value) {
        if (this.classFileOffset + 3 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        this.position += 4;
        this.bCodeStream[this.classFileOffset++] = (byte)((value & 0xFF000000) >> 24);
        this.bCodeStream[this.classFileOffset++] = (byte)((value & 0xFF0000) >> 16);
        this.bCodeStream[this.classFileOffset++] = (byte)((value & 0xFF00) >> 8);
        this.bCodeStream[this.classFileOffset++] = (byte)(value & 0xFF);
    }
    
    protected void writeSignedWord(final int pos, final int value) {
        int currentOffset = this.startingClassFileOffset + pos;
        if (currentOffset + 3 >= this.bCodeStream.length) {
            this.resizeByteArray();
        }
        this.bCodeStream[currentOffset++] = (byte)((value & 0xFF000000) >> 24);
        this.bCodeStream[currentOffset++] = (byte)((value & 0xFF0000) >> 16);
        this.bCodeStream[currentOffset++] = (byte)((value & 0xFF00) >> 8);
        this.bCodeStream[currentOffset++] = (byte)(value & 0xFF);
    }
    
    private final void writeUnsignedShort(final int value) {
        this.position += 2;
        this.bCodeStream[this.classFileOffset++] = (byte)(value >>> 8);
        this.bCodeStream[this.classFileOffset++] = (byte)value;
    }
    
    protected void writeWidePosition(final BranchLabel label) {
        final int labelPos = label.position;
        int offset = labelPos - this.position + 1;
        this.writeSignedWord(offset);
        final int[] forwardRefs = label.forwardReferences();
        for (int i = 0, max = label.forwardReferenceCount(); i < max; ++i) {
            final int forward = forwardRefs[i];
            offset = labelPos - forward + 1;
            this.writeSignedWord(forward, offset);
        }
    }
}
