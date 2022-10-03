package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;

public class AnnotationMethodInfo extends MethodInfo
{
    protected Object defaultValue;
    
    public static MethodInfo createAnnotationMethod(final byte[] classFileBytes, final int[] offsets, final int offset) {
        final MethodInfo methodInfo = new MethodInfo(classFileBytes, offsets, offset);
        final int attributesCount = methodInfo.u2At(6);
        int readOffset = 8;
        AnnotationInfo[] annotations = null;
        Object defaultValue = null;
        for (int i = 0; i < attributesCount; ++i) {
            final int utf8Offset = methodInfo.constantPoolOffsets[methodInfo.u2At(readOffset)] - methodInfo.structOffset;
            final char[] attributeName = methodInfo.utf8At(utf8Offset + 3, methodInfo.u2At(utf8Offset + 1));
            if (attributeName.length > 0) {
                switch (attributeName[0]) {
                    case 'A': {
                        if (CharOperation.equals(attributeName, AttributeNamesConstants.AnnotationDefaultName)) {
                            final AnnotationInfo info = new AnnotationInfo(methodInfo.reference, methodInfo.constantPoolOffsets, readOffset + 6 + methodInfo.structOffset);
                            defaultValue = info.decodeDefaultValue();
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
                        if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleAnnotationsName)) {
                            methodAnnotations = MethodInfo.decodeMethodAnnotations(readOffset, true, methodInfo);
                        }
                        else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleAnnotationsName)) {
                            methodAnnotations = MethodInfo.decodeMethodAnnotations(readOffset, false, methodInfo);
                        }
                        if (methodAnnotations == null) {
                            break;
                        }
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
                }
            }
            readOffset += (int)(6L + methodInfo.u4At(readOffset + 2));
        }
        methodInfo.attributeBytes = readOffset;
        if (defaultValue != null) {
            if (annotations != null) {
                return new AnnotationMethodInfoWithAnnotations(methodInfo, defaultValue, annotations);
            }
            return new AnnotationMethodInfo(methodInfo, defaultValue);
        }
        else {
            if (annotations != null) {
                return new MethodInfoWithAnnotations(methodInfo, annotations);
            }
            return methodInfo;
        }
    }
    
    AnnotationMethodInfo(final MethodInfo methodInfo, final Object defaultValue) {
        super(methodInfo.reference, methodInfo.constantPoolOffsets, methodInfo.structOffset);
        this.defaultValue = null;
        this.defaultValue = defaultValue;
        this.accessFlags = methodInfo.accessFlags;
        this.attributeBytes = methodInfo.attributeBytes;
        this.descriptor = methodInfo.descriptor;
        this.exceptionNames = methodInfo.exceptionNames;
        this.name = methodInfo.name;
        this.signature = methodInfo.signature;
        this.signatureUtf8Offset = methodInfo.signatureUtf8Offset;
        this.tagBits = methodInfo.tagBits;
    }
    
    @Override
    public Object getDefaultValue() {
        return this.defaultValue;
    }
    
    @Override
    protected void toStringContent(final StringBuffer buffer) {
        super.toStringContent(buffer);
        if (this.defaultValue != null) {
            buffer.append(" default ");
            if (this.defaultValue instanceof Object[]) {
                buffer.append('{');
                final Object[] elements = (Object[])this.defaultValue;
                for (int i = 0, len = elements.length; i < len; ++i) {
                    if (i > 0) {
                        buffer.append(", ");
                    }
                    buffer.append(elements[i]);
                }
                buffer.append('}');
            }
            else {
                buffer.append(this.defaultValue);
            }
            buffer.append('\n');
        }
    }
}
