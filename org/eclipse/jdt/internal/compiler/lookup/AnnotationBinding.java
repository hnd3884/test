package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Arrays;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

public class AnnotationBinding
{
    ReferenceBinding type;
    ElementValuePair[] pairs;
    
    public static AnnotationBinding[] addStandardAnnotations(final AnnotationBinding[] recordedAnnotations, final long annotationTagBits, final LookupEnvironment env) {
        if ((annotationTagBits & 0x17FFFFF800000000L) == 0x0L) {
            return recordedAnnotations;
        }
        int count = 0;
        if ((annotationTagBits & 0x600FF800000000L) != 0x0L) {
            ++count;
        }
        if ((annotationTagBits & 0x300000000000L) != 0x0L) {
            ++count;
        }
        if ((annotationTagBits & 0x400000000000L) != 0x0L) {
            ++count;
        }
        if ((annotationTagBits & 0x800000000000L) != 0x0L) {
            ++count;
        }
        if ((annotationTagBits & 0x1000000000000L) != 0x0L) {
            ++count;
        }
        if ((annotationTagBits & 0x2000000000000L) != 0x0L) {
            ++count;
        }
        if ((annotationTagBits & 0x4000000000000L) != 0x0L) {
            ++count;
        }
        if ((annotationTagBits & 0x10000000000000L) != 0x0L) {
            ++count;
        }
        if ((annotationTagBits & 0x8000000000000L) != 0x0L) {
            ++count;
        }
        if (count == 0) {
            return recordedAnnotations;
        }
        int index = recordedAnnotations.length;
        final AnnotationBinding[] result = new AnnotationBinding[index + count];
        System.arraycopy(recordedAnnotations, 0, result, 0, index);
        if ((annotationTagBits & 0x600FF800000000L) != 0x0L) {
            result[index++] = buildTargetAnnotation(annotationTagBits, env);
        }
        if ((annotationTagBits & 0x300000000000L) != 0x0L) {
            result[index++] = buildRetentionAnnotation(annotationTagBits, env);
        }
        if ((annotationTagBits & 0x400000000000L) != 0x0L) {
            result[index++] = buildMarkerAnnotation(TypeConstants.JAVA_LANG_DEPRECATED, env);
        }
        if ((annotationTagBits & 0x800000000000L) != 0x0L) {
            result[index++] = buildMarkerAnnotation(TypeConstants.JAVA_LANG_ANNOTATION_DOCUMENTED, env);
        }
        if ((annotationTagBits & 0x1000000000000L) != 0x0L) {
            result[index++] = buildMarkerAnnotation(TypeConstants.JAVA_LANG_ANNOTATION_INHERITED, env);
        }
        if ((annotationTagBits & 0x2000000000000L) != 0x0L) {
            result[index++] = buildMarkerAnnotation(TypeConstants.JAVA_LANG_OVERRIDE, env);
        }
        if ((annotationTagBits & 0x4000000000000L) != 0x0L) {
            result[index++] = buildMarkerAnnotation(TypeConstants.JAVA_LANG_SUPPRESSWARNINGS, env);
        }
        if ((annotationTagBits & 0x10000000000000L) != 0x0L) {
            result[index++] = buildMarkerAnnotationForMemberType(TypeConstants.JAVA_LANG_INVOKE_METHODHANDLE_$_POLYMORPHICSIGNATURE, env);
        }
        if ((annotationTagBits & 0x8000000000000L) != 0x0L) {
            result[index++] = buildMarkerAnnotation(TypeConstants.JAVA_LANG_SAFEVARARGS, env);
        }
        return result;
    }
    
    private static AnnotationBinding buildMarkerAnnotationForMemberType(final char[][] compoundName, final LookupEnvironment env) {
        ReferenceBinding type = env.getResolvedType(compoundName, null);
        if (!type.isValidBinding()) {
            type = ((ProblemReferenceBinding)type).closestMatch;
        }
        return env.createAnnotation(type, Binding.NO_ELEMENT_VALUE_PAIRS);
    }
    
    private static AnnotationBinding buildMarkerAnnotation(final char[][] compoundName, final LookupEnvironment env) {
        final ReferenceBinding type = env.getResolvedType(compoundName, null);
        return env.createAnnotation(type, Binding.NO_ELEMENT_VALUE_PAIRS);
    }
    
    private static AnnotationBinding buildRetentionAnnotation(final long bits, final LookupEnvironment env) {
        final ReferenceBinding retentionPolicy = env.getResolvedType(TypeConstants.JAVA_LANG_ANNOTATION_RETENTIONPOLICY, null);
        Object value = null;
        if ((bits & 0x300000000000L) == 0x300000000000L) {
            value = retentionPolicy.getField(TypeConstants.UPPER_RUNTIME, true);
        }
        else if ((bits & 0x200000000000L) != 0x0L) {
            value = retentionPolicy.getField(TypeConstants.UPPER_CLASS, true);
        }
        else if ((bits & 0x100000000000L) != 0x0L) {
            value = retentionPolicy.getField(TypeConstants.UPPER_SOURCE, true);
        }
        return env.createAnnotation(env.getResolvedType(TypeConstants.JAVA_LANG_ANNOTATION_RETENTION, null), new ElementValuePair[] { new ElementValuePair(TypeConstants.VALUE, value, null) });
    }
    
    private static AnnotationBinding buildTargetAnnotation(final long bits, final LookupEnvironment env) {
        final ReferenceBinding target = env.getResolvedType(TypeConstants.JAVA_LANG_ANNOTATION_TARGET, null);
        if ((bits & 0x800000000L) != 0x0L) {
            return new AnnotationBinding(target, Binding.NO_ELEMENT_VALUE_PAIRS);
        }
        int arraysize = 0;
        if ((bits & 0x40000000000L) != 0x0L) {
            ++arraysize;
        }
        if ((bits & 0x10000000000L) != 0x0L) {
            ++arraysize;
        }
        if ((bits & 0x2000000000L) != 0x0L) {
            ++arraysize;
        }
        if ((bits & 0x20000000000L) != 0x0L) {
            ++arraysize;
        }
        if ((bits & 0x4000000000L) != 0x0L) {
            ++arraysize;
        }
        if ((bits & 0x80000000000L) != 0x0L) {
            ++arraysize;
        }
        if ((bits & 0x8000000000L) != 0x0L) {
            ++arraysize;
        }
        if ((bits & 0x1000000000L) != 0x0L) {
            ++arraysize;
        }
        if ((bits & 0x20000000000000L) != 0x0L) {
            ++arraysize;
        }
        if ((bits & 0x40000000000000L) != 0x0L) {
            ++arraysize;
        }
        final Object[] value = new Object[arraysize];
        if (arraysize > 0) {
            final ReferenceBinding elementType = env.getResolvedType(TypeConstants.JAVA_LANG_ANNOTATION_ELEMENTTYPE, null);
            int index = 0;
            if ((bits & 0x40000000000L) != 0x0L) {
                value[index++] = elementType.getField(TypeConstants.UPPER_ANNOTATION_TYPE, true);
            }
            if ((bits & 0x10000000000L) != 0x0L) {
                value[index++] = elementType.getField(TypeConstants.UPPER_CONSTRUCTOR, true);
            }
            if ((bits & 0x2000000000L) != 0x0L) {
                value[index++] = elementType.getField(TypeConstants.UPPER_FIELD, true);
            }
            if ((bits & 0x4000000000L) != 0x0L) {
                value[index++] = elementType.getField(TypeConstants.UPPER_METHOD, true);
            }
            if ((bits & 0x80000000000L) != 0x0L) {
                value[index++] = elementType.getField(TypeConstants.UPPER_PACKAGE, true);
            }
            if ((bits & 0x8000000000L) != 0x0L) {
                value[index++] = elementType.getField(TypeConstants.UPPER_PARAMETER, true);
            }
            if ((bits & 0x20000000000000L) != 0x0L) {
                value[index++] = elementType.getField(TypeConstants.TYPE_USE_TARGET, true);
            }
            if ((bits & 0x40000000000000L) != 0x0L) {
                value[index++] = elementType.getField(TypeConstants.TYPE_PARAMETER_TARGET, true);
            }
            if ((bits & 0x1000000000L) != 0x0L) {
                value[index++] = elementType.getField(TypeConstants.TYPE, true);
            }
            if ((bits & 0x20000000000L) != 0x0L) {
                value[index++] = elementType.getField(TypeConstants.UPPER_LOCAL_VARIABLE, true);
            }
        }
        return env.createAnnotation(target, new ElementValuePair[] { new ElementValuePair(TypeConstants.VALUE, value, null) });
    }
    
    public AnnotationBinding(final ReferenceBinding type, final ElementValuePair[] pairs) {
        this.type = type;
        this.pairs = pairs;
    }
    
    AnnotationBinding(final Annotation astAnnotation) {
        this((ReferenceBinding)astAnnotation.resolvedType, astAnnotation.computeElementValuePairs());
    }
    
    public char[] computeUniqueKey(final char[] recipientKey) {
        final char[] typeKey = this.type.computeUniqueKey(false);
        final int recipientKeyLength = recipientKey.length;
        final char[] uniqueKey = new char[recipientKeyLength + 1 + typeKey.length];
        System.arraycopy(recipientKey, 0, uniqueKey, 0, recipientKeyLength);
        uniqueKey[recipientKeyLength] = '@';
        System.arraycopy(typeKey, 0, uniqueKey, recipientKeyLength + 1, typeKey.length);
        return uniqueKey;
    }
    
    public ReferenceBinding getAnnotationType() {
        return this.type;
    }
    
    public void resolve() {
    }
    
    public ElementValuePair[] getElementValuePairs() {
        return this.pairs;
    }
    
    public static void setMethodBindings(final ReferenceBinding type, final ElementValuePair[] pairs) {
        int i = pairs.length;
        while (--i >= 0) {
            final ElementValuePair pair = pairs[i];
            final MethodBinding[] methods = type.getMethods(pair.getName());
            if (methods != null && methods.length == 1) {
                pair.setMethodBinding(methods[0]);
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer(5);
        buffer.append('@').append(this.type.sourceName);
        if (this.pairs != null && this.pairs.length > 0) {
            buffer.append('(');
            if (this.pairs.length == 1 && CharOperation.equals(this.pairs[0].getName(), TypeConstants.VALUE)) {
                buffer.append(this.pairs[0].value);
            }
            else {
                for (int i = 0, max = this.pairs.length; i < max; ++i) {
                    if (i > 0) {
                        buffer.append(", ");
                    }
                    buffer.append(this.pairs[i]);
                }
            }
            buffer.append(')');
        }
        return buffer.toString();
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        int c = this.type.hashCode();
        result = 31 * result + c;
        c = Arrays.hashCode(this.pairs);
        result = 31 * result + c;
        return result;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AnnotationBinding)) {
            return false;
        }
        final AnnotationBinding that = (AnnotationBinding)object;
        if (this.getAnnotationType() != that.getAnnotationType()) {
            return false;
        }
        final ElementValuePair[] thisElementValuePairs = this.getElementValuePairs();
        final ElementValuePair[] thatElementValuePairs = that.getElementValuePairs();
        final int length = thisElementValuePairs.length;
        if (length != thatElementValuePairs.length) {
            return false;
        }
        int i = 0;
    Label_0207:
        while (i < length) {
            final ElementValuePair thisPair = thisElementValuePairs[i];
            for (final ElementValuePair thatPair : thatElementValuePairs) {
                if (thisPair.binding == thatPair.binding) {
                    if (thisPair.value == null) {
                        if (thatPair.value != null) {
                            return false;
                        }
                    }
                    else {
                        if (thatPair.value == null) {
                            return false;
                        }
                        if (thatPair.value instanceof Object[] && thisPair.value instanceof Object[]) {
                            if (!Arrays.equals((Object[])thisPair.value, (Object[])thatPair.value)) {
                                return false;
                            }
                        }
                        else if (!thatPair.value.equals(thisPair.value)) {
                            return false;
                        }
                    }
                    ++i;
                    continue Label_0207;
                }
            }
            return false;
        }
        return true;
    }
}
