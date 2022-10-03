package org.eclipse.jdt.internal.compiler.apt.model;

import java.util.Iterator;
import java.util.Collection;
import org.eclipse.jdt.internal.compiler.util.Util;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.element.VariableElement;
import javax.lang.model.element.AnnotationValueVisitor;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.problem.ShouldNotImplement;
import org.eclipse.jdt.internal.compiler.impl.FloatConstant;
import org.eclipse.jdt.internal.compiler.impl.DoubleConstant;
import org.eclipse.jdt.internal.compiler.impl.LongConstant;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import javax.lang.model.element.AnnotationValue;

public class AnnotationValueImpl implements AnnotationValue, TypeIds
{
    private static final int T_AnnotationMirror = -1;
    private static final int T_EnumConstant = -2;
    private static final int T_ClassObject = -3;
    private static final int T_ArrayType = -4;
    private final BaseProcessingEnvImpl _env;
    private final Object _value;
    private final int _kind;
    
    public AnnotationValueImpl(final BaseProcessingEnvImpl env, final Object value, final TypeBinding type) {
        this._env = env;
        final int[] kind = { 0 };
        if (type == null) {
            this._value = this.convertToMirrorType(value, type, kind);
            this._kind = kind[0];
        }
        else if (type.isArrayType()) {
            List<AnnotationValue> convertedValues = null;
            final TypeBinding valueType = ((ArrayBinding)type).elementsType();
            if (value instanceof Object[]) {
                final Object[] values = (Object[])value;
                convertedValues = new ArrayList<AnnotationValue>(values.length);
                Object[] array;
                for (int length = (array = values).length, i = 0; i < length; ++i) {
                    final Object oneValue = array[i];
                    convertedValues.add(new AnnotationValueImpl(this._env, oneValue, valueType));
                }
            }
            else {
                convertedValues = new ArrayList<AnnotationValue>(1);
                convertedValues.add(new AnnotationValueImpl(this._env, value, valueType));
            }
            this._value = Collections.unmodifiableList((List<?>)convertedValues);
            this._kind = -4;
        }
        else {
            this._value = this.convertToMirrorType(value, type, kind);
            this._kind = kind[0];
        }
    }
    
    private Object convertToMirrorType(final Object value, final TypeBinding type, final int[] kind) {
        if (type == null) {
            kind[0] = 11;
            return "<error>";
        }
        if (type instanceof BaseTypeBinding || type.id == 11) {
            if (value == null) {
                if (type instanceof BaseTypeBinding || type.id == 11) {
                    kind[0] = 11;
                    return "<error>";
                }
                if (type.isAnnotationType()) {
                    kind[0] = -1;
                    return this._env.getFactory().newAnnotationMirror(null);
                }
            }
            else if (value instanceof Constant) {
                if (type instanceof BaseTypeBinding) {
                    kind[0] = ((BaseTypeBinding)type).id;
                }
                else {
                    if (type.id != 11) {
                        kind[0] = 11;
                        return "<error>";
                    }
                    kind[0] = ((Constant)value).typeID();
                }
                switch (kind[0]) {
                    case 5: {
                        return ((Constant)value).booleanValue();
                    }
                    case 3: {
                        return ((Constant)value).byteValue();
                    }
                    case 2: {
                        return ((Constant)value).charValue();
                    }
                    case 8: {
                        return ((Constant)value).doubleValue();
                    }
                    case 9: {
                        return ((Constant)value).floatValue();
                    }
                    case 10: {
                        try {
                            if (value instanceof LongConstant || value instanceof DoubleConstant || value instanceof FloatConstant) {
                                kind[0] = 11;
                                return "<error>";
                            }
                            return ((Constant)value).intValue();
                        }
                        catch (final ShouldNotImplement shouldNotImplement) {
                            kind[0] = 11;
                            return "<error>";
                        }
                    }
                    case 11: {
                        return ((Constant)value).stringValue();
                    }
                    case 7: {
                        return ((Constant)value).longValue();
                    }
                    case 4: {
                        return ((Constant)value).shortValue();
                    }
                }
            }
        }
        else if (type.isEnum()) {
            if (value instanceof FieldBinding) {
                kind[0] = -2;
                return this._env.getFactory().newElement((Binding)value);
            }
            kind[0] = 11;
            return "<error>";
        }
        else if (type.isAnnotationType()) {
            if (value instanceof AnnotationBinding) {
                kind[0] = -1;
                return this._env.getFactory().newAnnotationMirror((AnnotationBinding)value);
            }
        }
        else if (value instanceof TypeBinding) {
            kind[0] = -3;
            return this._env.getFactory().newTypeMirror((Binding)value);
        }
        kind[0] = 11;
        return "<error>";
    }
    
    @Override
    public <R, P> R accept(final AnnotationValueVisitor<R, P> v, final P p) {
        switch (this._kind) {
            case 5: {
                return v.visitBoolean((boolean)this._value, p);
            }
            case 3: {
                return v.visitByte((byte)this._value, p);
            }
            case 2: {
                return v.visitChar((char)this._value, p);
            }
            case 8: {
                return v.visitDouble((double)this._value, p);
            }
            case 9: {
                return v.visitFloat((float)this._value, p);
            }
            case 10: {
                return v.visitInt((int)this._value, p);
            }
            case 11: {
                return v.visitString((String)this._value, p);
            }
            case 7: {
                return v.visitLong((long)this._value, p);
            }
            case 4: {
                return v.visitShort((short)this._value, p);
            }
            case -2: {
                return v.visitEnumConstant((VariableElement)this._value, p);
            }
            case -3: {
                return v.visitType((TypeMirror)this._value, p);
            }
            case -1: {
                return v.visitAnnotation((AnnotationMirror)this._value, p);
            }
            case -4: {
                return v.visitArray((List<? extends AnnotationValue>)this._value, p);
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public Object getValue() {
        return this._value;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof AnnotationValueImpl && this._value.equals(((AnnotationValueImpl)obj)._value);
    }
    
    @Override
    public int hashCode() {
        return this._value.hashCode() + this._kind;
    }
    
    @Override
    public String toString() {
        if (this._value == null) {
            return "null";
        }
        if (this._value instanceof String) {
            final String value = (String)this._value;
            final StringBuffer sb = new StringBuffer();
            sb.append('\"');
            for (int i = 0; i < value.length(); ++i) {
                Util.appendEscapedChar(sb, value.charAt(i), true);
            }
            sb.append('\"');
            return sb.toString();
        }
        if (this._value instanceof Character) {
            final StringBuffer sb2 = new StringBuffer();
            sb2.append('\'');
            Util.appendEscapedChar(sb2, (char)this._value, false);
            sb2.append('\'');
            return sb2.toString();
        }
        if (this._value instanceof VariableElement) {
            final VariableElement enumDecl = (VariableElement)this._value;
            return String.valueOf(enumDecl.asType().toString()) + "." + (Object)enumDecl.getSimpleName();
        }
        if (this._value instanceof Collection) {
            final Collection<AnnotationValue> values = (Collection<AnnotationValue>)this._value;
            final StringBuilder sb3 = new StringBuilder();
            sb3.append('{');
            boolean first = true;
            for (final AnnotationValue annoValue : values) {
                if (!first) {
                    sb3.append(", ");
                }
                first = false;
                sb3.append(annoValue.toString());
            }
            sb3.append('}');
            return sb3.toString();
        }
        if (this._value instanceof TypeMirror) {
            return String.valueOf(this._value.toString()) + ".class";
        }
        return this._value.toString();
    }
}
