package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.IBinaryMethod;

public class MethodInfo extends ClassFileStruct implements IBinaryMethod, Comparable
{
    private static final char[][] noException;
    private static final char[][] noArgumentNames;
    private static final char[] ARG;
    protected int accessFlags;
    protected int attributeBytes;
    protected char[] descriptor;
    protected char[][] exceptionNames;
    protected char[] name;
    protected char[] signature;
    protected int signatureUtf8Offset;
    protected long tagBits;
    protected char[][] argumentNames;
    
    static {
        noException = CharOperation.NO_CHAR_CHAR;
        noArgumentNames = CharOperation.NO_CHAR_CHAR;
        ARG = "arg".toCharArray();
    }
    
    public static MethodInfo createMethod(final byte[] classFileBytes, final int[] offsets, final int offset) {
        final MethodInfo methodInfo = new MethodInfo(classFileBytes, offsets, offset);
        final int attributesCount = methodInfo.u2At(6);
        int readOffset = 8;
        AnnotationInfo[] annotations = null;
        AnnotationInfo[][] parameterAnnotations = null;
        TypeAnnotationInfo[] typeAnnotations = null;
        for (int i = 0; i < attributesCount; ++i) {
            final int utf8Offset = methodInfo.constantPoolOffsets[methodInfo.u2At(readOffset)] - methodInfo.structOffset;
            final char[] attributeName = methodInfo.utf8At(utf8Offset + 3, methodInfo.u2At(utf8Offset + 1));
            if (attributeName.length > 0) {
                switch (attributeName[0]) {
                    case 'M': {
                        if (CharOperation.equals(attributeName, AttributeNamesConstants.MethodParametersName)) {
                            methodInfo.decodeMethodParameters(readOffset, methodInfo);
                            break;
                        }
                        break;
                    }
                    case 'S': {
                        if (CharOperation.equals(AttributeNamesConstants.SignatureName, attributeName)) {
                            methodInfo.signatureUtf8Offset = methodInfo.constantPoolOffsets[methodInfo.u2At(readOffset + 6)] - methodInfo.structOffset;
                            break;
                        }
                        break;
                    }
                    case 'R': {
                        AnnotationInfo[] methodAnnotations = null;
                        AnnotationInfo[][] paramAnnotations = null;
                        TypeAnnotationInfo[] methodTypeAnnotations = null;
                        if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleAnnotationsName)) {
                            methodAnnotations = decodeMethodAnnotations(readOffset, true, methodInfo);
                        }
                        else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleAnnotationsName)) {
                            methodAnnotations = decodeMethodAnnotations(readOffset, false, methodInfo);
                        }
                        else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleParameterAnnotationsName)) {
                            paramAnnotations = decodeParamAnnotations(readOffset, true, methodInfo);
                        }
                        else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleParameterAnnotationsName)) {
                            paramAnnotations = decodeParamAnnotations(readOffset, false, methodInfo);
                        }
                        else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleTypeAnnotationsName)) {
                            methodTypeAnnotations = decodeTypeAnnotations(readOffset, true, methodInfo);
                        }
                        else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleTypeAnnotationsName)) {
                            methodTypeAnnotations = decodeTypeAnnotations(readOffset, false, methodInfo);
                        }
                        if (methodAnnotations != null) {
                            if (annotations == null) {
                                annotations = methodAnnotations;
                                break;
                            }
                            final int length = annotations.length;
                            final AnnotationInfo[] newAnnotations = new AnnotationInfo[length + methodAnnotations.length];
                            System.arraycopy(annotations, 0, newAnnotations, 0, length);
                            System.arraycopy(methodAnnotations, 0, newAnnotations, length, methodAnnotations.length);
                            annotations = newAnnotations;
                            break;
                        }
                        else if (paramAnnotations != null) {
                            final int numberOfParameters = paramAnnotations.length;
                            if (parameterAnnotations == null) {
                                parameterAnnotations = paramAnnotations;
                                break;
                            }
                            for (int p = 0; p < numberOfParameters; ++p) {
                                final int numberOfAnnotations = (paramAnnotations[p] == null) ? 0 : paramAnnotations[p].length;
                                if (numberOfAnnotations > 0) {
                                    if (parameterAnnotations[p] == null) {
                                        parameterAnnotations[p] = paramAnnotations[p];
                                    }
                                    else {
                                        final int length2 = parameterAnnotations[p].length;
                                        final AnnotationInfo[] newAnnotations2 = new AnnotationInfo[length2 + numberOfAnnotations];
                                        System.arraycopy(parameterAnnotations[p], 0, newAnnotations2, 0, length2);
                                        System.arraycopy(paramAnnotations[p], 0, newAnnotations2, length2, numberOfAnnotations);
                                        parameterAnnotations[p] = newAnnotations2;
                                    }
                                }
                            }
                            break;
                        }
                        else {
                            if (methodTypeAnnotations == null) {
                                break;
                            }
                            if (typeAnnotations == null) {
                                typeAnnotations = methodTypeAnnotations;
                                break;
                            }
                            final int length = typeAnnotations.length;
                            final TypeAnnotationInfo[] newAnnotations3 = new TypeAnnotationInfo[length + methodTypeAnnotations.length];
                            System.arraycopy(typeAnnotations, 0, newAnnotations3, 0, length);
                            System.arraycopy(methodTypeAnnotations, 0, newAnnotations3, length, methodTypeAnnotations.length);
                            typeAnnotations = newAnnotations3;
                            break;
                        }
                        break;
                    }
                }
            }
            readOffset += (int)(6L + methodInfo.u4At(readOffset + 2));
        }
        methodInfo.attributeBytes = readOffset;
        if (typeAnnotations != null) {
            return new MethodInfoWithTypeAnnotations(methodInfo, annotations, parameterAnnotations, typeAnnotations);
        }
        if (parameterAnnotations != null) {
            return new MethodInfoWithParameterAnnotations(methodInfo, annotations, parameterAnnotations);
        }
        if (annotations != null) {
            return new MethodInfoWithAnnotations(methodInfo, annotations);
        }
        return methodInfo;
    }
    
    static AnnotationInfo[] decodeAnnotations(final int offset, final boolean runtimeVisible, final int numberOfAnnotations, final MethodInfo methodInfo) {
        final AnnotationInfo[] result = new AnnotationInfo[numberOfAnnotations];
        int readOffset = offset;
        for (int i = 0; i < numberOfAnnotations; ++i) {
            result[i] = new AnnotationInfo(methodInfo.reference, methodInfo.constantPoolOffsets, readOffset + methodInfo.structOffset, runtimeVisible, false);
            readOffset += result[i].readOffset;
        }
        return result;
    }
    
    static AnnotationInfo[] decodeMethodAnnotations(final int offset, final boolean runtimeVisible, final MethodInfo methodInfo) {
        final int numberOfAnnotations = methodInfo.u2At(offset + 6);
        if (numberOfAnnotations > 0) {
            AnnotationInfo[] annos = decodeAnnotations(offset + 8, runtimeVisible, numberOfAnnotations, methodInfo);
            if (runtimeVisible) {
                int numStandardAnnotations = 0;
                for (int i = 0; i < numberOfAnnotations; ++i) {
                    final long standardAnnoTagBits = annos[i].standardAnnotationTagBits;
                    methodInfo.tagBits |= standardAnnoTagBits;
                    if (standardAnnoTagBits != 0L) {
                        annos[i] = null;
                        ++numStandardAnnotations;
                    }
                }
                if (numStandardAnnotations != 0) {
                    if (numStandardAnnotations == numberOfAnnotations) {
                        return null;
                    }
                    final AnnotationInfo[] temp = new AnnotationInfo[numberOfAnnotations - numStandardAnnotations];
                    int tmpIndex = 0;
                    for (int j = 0; j < numberOfAnnotations; ++j) {
                        if (annos[j] != null) {
                            temp[tmpIndex++] = annos[j];
                        }
                    }
                    annos = temp;
                }
            }
            return annos;
        }
        return null;
    }
    
    static TypeAnnotationInfo[] decodeTypeAnnotations(final int offset, final boolean runtimeVisible, final MethodInfo methodInfo) {
        final int numberOfAnnotations = methodInfo.u2At(offset + 6);
        if (numberOfAnnotations > 0) {
            int readOffset = offset + 8;
            final TypeAnnotationInfo[] typeAnnos = new TypeAnnotationInfo[numberOfAnnotations];
            for (int i = 0; i < numberOfAnnotations; ++i) {
                final TypeAnnotationInfo newInfo = new TypeAnnotationInfo(methodInfo.reference, methodInfo.constantPoolOffsets, readOffset + methodInfo.structOffset, runtimeVisible, false);
                readOffset += newInfo.readOffset;
                typeAnnos[i] = newInfo;
            }
            return typeAnnos;
        }
        return null;
    }
    
    static AnnotationInfo[][] decodeParamAnnotations(final int offset, final boolean runtimeVisible, final MethodInfo methodInfo) {
        AnnotationInfo[][] allParamAnnotations = null;
        final int numberOfParameters = methodInfo.u1At(offset + 6);
        if (numberOfParameters > 0) {
            int readOffset = offset + 7;
            for (int i = 0; i < numberOfParameters; ++i) {
                final int numberOfAnnotations = methodInfo.u2At(readOffset);
                readOffset += 2;
                if (numberOfAnnotations > 0) {
                    if (allParamAnnotations == null) {
                        allParamAnnotations = new AnnotationInfo[numberOfParameters][];
                    }
                    final AnnotationInfo[] annos = decodeAnnotations(readOffset, runtimeVisible, numberOfAnnotations, methodInfo);
                    allParamAnnotations[i] = annos;
                    for (int aIndex = 0; aIndex < annos.length; ++aIndex) {
                        readOffset += annos[aIndex].readOffset;
                    }
                }
            }
        }
        return allParamAnnotations;
    }
    
    protected MethodInfo(final byte[] classFileBytes, final int[] offsets, final int offset) {
        super(classFileBytes, offsets, offset);
        this.accessFlags = -1;
        this.signatureUtf8Offset = -1;
    }
    
    @Override
    public int compareTo(final Object o) {
        final MethodInfo otherMethod = (MethodInfo)o;
        final int result = new String(this.getSelector()).compareTo(new String(otherMethod.getSelector()));
        if (result != 0) {
            return result;
        }
        return new String(this.getMethodDescriptor()).compareTo(new String(otherMethod.getMethodDescriptor()));
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof MethodInfo)) {
            return false;
        }
        final MethodInfo otherMethod = (MethodInfo)o;
        return CharOperation.equals(this.getSelector(), otherMethod.getSelector()) && CharOperation.equals(this.getMethodDescriptor(), otherMethod.getMethodDescriptor());
    }
    
    @Override
    public int hashCode() {
        return CharOperation.hashCode(this.getSelector()) + CharOperation.hashCode(this.getMethodDescriptor());
    }
    
    @Override
    public IBinaryAnnotation[] getAnnotations() {
        return null;
    }
    
    @Override
    public char[][] getArgumentNames() {
        if (this.argumentNames == null) {
            this.readCodeAttribute();
        }
        return this.argumentNames;
    }
    
    @Override
    public Object getDefaultValue() {
        return null;
    }
    
    @Override
    public char[][] getExceptionTypeNames() {
        if (this.exceptionNames == null) {
            this.readExceptionAttributes();
        }
        return this.exceptionNames;
    }
    
    @Override
    public char[] getGenericSignature() {
        if (this.signatureUtf8Offset != -1) {
            if (this.signature == null) {
                this.signature = this.utf8At(this.signatureUtf8Offset + 3, this.u2At(this.signatureUtf8Offset + 1));
            }
            return this.signature;
        }
        return null;
    }
    
    @Override
    public char[] getMethodDescriptor() {
        if (this.descriptor == null) {
            final int utf8Offset = this.constantPoolOffsets[this.u2At(4)] - this.structOffset;
            this.descriptor = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
        }
        return this.descriptor;
    }
    
    @Override
    public int getModifiers() {
        if (this.accessFlags == -1) {
            this.accessFlags = this.u2At(0);
            this.readModifierRelatedAttributes();
        }
        return this.accessFlags;
    }
    
    @Override
    public IBinaryAnnotation[] getParameterAnnotations(final int index, final char[] classFileName) {
        return null;
    }
    
    @Override
    public int getAnnotatedParametersCount() {
        return 0;
    }
    
    @Override
    public IBinaryTypeAnnotation[] getTypeAnnotations() {
        return null;
    }
    
    @Override
    public char[] getSelector() {
        if (this.name == null) {
            final int utf8Offset = this.constantPoolOffsets[this.u2At(2)] - this.structOffset;
            this.name = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
        }
        return this.name;
    }
    
    @Override
    public long getTagBits() {
        return this.tagBits;
    }
    
    protected void initialize() {
        this.getModifiers();
        this.getSelector();
        this.getMethodDescriptor();
        this.getExceptionTypeNames();
        this.getGenericSignature();
        this.getArgumentNames();
        this.reset();
    }
    
    @Override
    public boolean isClinit() {
        final char[] selector = this.getSelector();
        return selector[0] == '<' && selector.length == 8;
    }
    
    @Override
    public boolean isConstructor() {
        final char[] selector = this.getSelector();
        return selector[0] == '<' && selector.length == 6;
    }
    
    public boolean isSynthetic() {
        return (this.getModifiers() & 0x1000) != 0x0;
    }
    
    private void readExceptionAttributes() {
        final int attributesCount = this.u2At(6);
        int readOffset = 8;
        for (int i = 0; i < attributesCount; ++i) {
            int utf8Offset = this.constantPoolOffsets[this.u2At(readOffset)] - this.structOffset;
            final char[] attributeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            if (CharOperation.equals(attributeName, AttributeNamesConstants.ExceptionsName)) {
                final int entriesNumber = this.u2At(readOffset + 6);
                readOffset += 8;
                if (entriesNumber == 0) {
                    this.exceptionNames = MethodInfo.noException;
                }
                else {
                    this.exceptionNames = new char[entriesNumber][];
                    for (int j = 0; j < entriesNumber; ++j) {
                        utf8Offset = this.constantPoolOffsets[this.u2At(this.constantPoolOffsets[this.u2At(readOffset)] - this.structOffset + 1)] - this.structOffset;
                        this.exceptionNames[j] = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                        readOffset += 2;
                    }
                }
            }
            else {
                readOffset += (int)(6L + this.u4At(readOffset + 2));
            }
        }
        if (this.exceptionNames == null) {
            this.exceptionNames = MethodInfo.noException;
        }
    }
    
    private void readModifierRelatedAttributes() {
        final int attributesCount = this.u2At(6);
        int readOffset = 8;
        for (int i = 0; i < attributesCount; ++i) {
            final int utf8Offset = this.constantPoolOffsets[this.u2At(readOffset)] - this.structOffset;
            final char[] attributeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            if (attributeName.length != 0) {
                switch (attributeName[0]) {
                    case 'D': {
                        if (CharOperation.equals(attributeName, AttributeNamesConstants.DeprecatedName)) {
                            this.accessFlags |= 0x100000;
                            break;
                        }
                        break;
                    }
                    case 'S': {
                        if (CharOperation.equals(attributeName, AttributeNamesConstants.SyntheticName)) {
                            this.accessFlags |= 0x1000;
                            break;
                        }
                        break;
                    }
                    case 'A': {
                        if (CharOperation.equals(attributeName, AttributeNamesConstants.AnnotationDefaultName)) {
                            this.accessFlags |= 0x20000;
                            break;
                        }
                        break;
                    }
                    case 'V': {
                        if (CharOperation.equals(attributeName, AttributeNamesConstants.VarargsName)) {
                            this.accessFlags |= 0x80;
                            break;
                        }
                        break;
                    }
                }
            }
            readOffset += (int)(6L + this.u4At(readOffset + 2));
        }
    }
    
    public int sizeInBytes() {
        return this.attributeBytes;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        this.toString(buffer);
        return buffer.toString();
    }
    
    void toString(final StringBuffer buffer) {
        buffer.append(this.getClass().getName());
        this.toStringContent(buffer);
    }
    
    protected void toStringContent(final StringBuffer buffer) {
        final int modifiers = this.getModifiers();
        char[] desc = this.getGenericSignature();
        if (desc == null) {
            desc = this.getMethodDescriptor();
        }
        buffer.append('{').append(String.valueOf(((modifiers & 0x100000) != 0x0) ? "deprecated " : Util.EMPTY_STRING) + (((modifiers & 0x1) == 0x1) ? "public " : Util.EMPTY_STRING) + (((modifiers & 0x2) == 0x2) ? "private " : Util.EMPTY_STRING) + (((modifiers & 0x4) == 0x4) ? "protected " : Util.EMPTY_STRING) + (((modifiers & 0x8) == 0x8) ? "static " : Util.EMPTY_STRING) + (((modifiers & 0x10) == 0x10) ? "final " : Util.EMPTY_STRING) + (((modifiers & 0x40) == 0x40) ? "bridge " : Util.EMPTY_STRING) + (((modifiers & 0x80) == 0x80) ? "varargs " : Util.EMPTY_STRING)).append(this.getSelector()).append(desc).append('}');
    }
    
    private void readCodeAttribute() {
        final int attributesCount = this.u2At(6);
        int readOffset = 8;
        if (attributesCount != 0) {
            for (int i = 0; i < attributesCount; ++i) {
                final int utf8Offset = this.constantPoolOffsets[this.u2At(readOffset)] - this.structOffset;
                final char[] attributeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                if (CharOperation.equals(attributeName, AttributeNamesConstants.CodeName)) {
                    this.decodeCodeAttribute(readOffset);
                    if (this.argumentNames == null) {
                        this.argumentNames = MethodInfo.noArgumentNames;
                    }
                    return;
                }
                readOffset += (int)(6L + this.u4At(readOffset + 2));
            }
        }
        this.argumentNames = MethodInfo.noArgumentNames;
    }
    
    private void decodeCodeAttribute(final int offset) {
        int readOffset = offset + 10;
        final int codeLength = (int)this.u4At(readOffset);
        readOffset += 4 + codeLength;
        final int exceptionTableLength = this.u2At(readOffset);
        readOffset += 2;
        if (exceptionTableLength != 0) {
            for (int i = 0; i < exceptionTableLength; ++i) {
                readOffset += 8;
            }
        }
        final int attributesCount = this.u2At(readOffset);
        readOffset += 2;
        for (int j = 0; j < attributesCount; ++j) {
            final int utf8Offset = this.constantPoolOffsets[this.u2At(readOffset)] - this.structOffset;
            final char[] attributeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            if (CharOperation.equals(attributeName, AttributeNamesConstants.LocalVariableTableName)) {
                this.decodeLocalVariableAttribute(readOffset, codeLength);
            }
            readOffset += (int)(6L + this.u4At(readOffset + 2));
        }
    }
    
    private void decodeLocalVariableAttribute(final int offset, final int codeLength) {
        int readOffset = offset + 6;
        final int length = this.u2At(readOffset);
        if (length != 0) {
            readOffset += 2;
            this.argumentNames = new char[length][];
            int argumentNamesIndex = 0;
            for (int i = 0; i < length; ++i) {
                final int startPC = this.u2At(readOffset);
                if (startPC != 0) {
                    break;
                }
                final int nameIndex = this.u2At(4 + readOffset);
                final int utf8Offset = this.constantPoolOffsets[nameIndex] - this.structOffset;
                final char[] localVariableName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                if (!CharOperation.equals(localVariableName, ConstantPool.This)) {
                    this.argumentNames[argumentNamesIndex++] = localVariableName;
                }
                readOffset += 10;
            }
            if (argumentNamesIndex != this.argumentNames.length) {
                System.arraycopy(this.argumentNames, 0, this.argumentNames = new char[argumentNamesIndex][], 0, argumentNamesIndex);
            }
        }
    }
    
    private void decodeMethodParameters(final int offset, final MethodInfo methodInfo) {
        int readOffset = offset + 6;
        final int length = this.u1At(readOffset);
        if (length != 0) {
            ++readOffset;
            this.argumentNames = new char[length][];
            for (int i = 0; i < length; ++i) {
                final int nameIndex = this.u2At(readOffset);
                if (nameIndex != 0) {
                    final int utf8Offset = this.constantPoolOffsets[nameIndex] - this.structOffset;
                    final char[] parameterName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                    this.argumentNames[i] = parameterName;
                }
                else {
                    this.argumentNames[i] = CharOperation.concat(MethodInfo.ARG, String.valueOf(i).toCharArray());
                }
                readOffset += 4;
            }
        }
    }
}
