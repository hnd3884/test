package sun.reflect;

import java.lang.reflect.Modifier;
import java.lang.reflect.Field;

class UnsafeFieldAccessorFactory
{
    static FieldAccessor newFieldAccessor(final Field field, final boolean b) {
        final Class<?> type = field.getType();
        final boolean static1 = Modifier.isStatic(field.getModifiers());
        final boolean final1 = Modifier.isFinal(field.getModifiers());
        final boolean volatile1 = Modifier.isVolatile(field.getModifiers());
        final boolean b2 = final1 || volatile1;
        final boolean b3 = final1 && (static1 || !b);
        if (static1) {
            UnsafeFieldAccessorImpl.unsafe.ensureClassInitialized(field.getDeclaringClass());
            if (!b2) {
                if (type == Boolean.TYPE) {
                    return new UnsafeStaticBooleanFieldAccessorImpl(field);
                }
                if (type == Byte.TYPE) {
                    return new UnsafeStaticByteFieldAccessorImpl(field);
                }
                if (type == Short.TYPE) {
                    return new UnsafeStaticShortFieldAccessorImpl(field);
                }
                if (type == Character.TYPE) {
                    return new UnsafeStaticCharacterFieldAccessorImpl(field);
                }
                if (type == Integer.TYPE) {
                    return new UnsafeStaticIntegerFieldAccessorImpl(field);
                }
                if (type == Long.TYPE) {
                    return new UnsafeStaticLongFieldAccessorImpl(field);
                }
                if (type == Float.TYPE) {
                    return new UnsafeStaticFloatFieldAccessorImpl(field);
                }
                if (type == Double.TYPE) {
                    return new UnsafeStaticDoubleFieldAccessorImpl(field);
                }
                return new UnsafeStaticObjectFieldAccessorImpl(field);
            }
            else {
                if (type == Boolean.TYPE) {
                    return new UnsafeQualifiedStaticBooleanFieldAccessorImpl(field, b3);
                }
                if (type == Byte.TYPE) {
                    return new UnsafeQualifiedStaticByteFieldAccessorImpl(field, b3);
                }
                if (type == Short.TYPE) {
                    return new UnsafeQualifiedStaticShortFieldAccessorImpl(field, b3);
                }
                if (type == Character.TYPE) {
                    return new UnsafeQualifiedStaticCharacterFieldAccessorImpl(field, b3);
                }
                if (type == Integer.TYPE) {
                    return new UnsafeQualifiedStaticIntegerFieldAccessorImpl(field, b3);
                }
                if (type == Long.TYPE) {
                    return new UnsafeQualifiedStaticLongFieldAccessorImpl(field, b3);
                }
                if (type == Float.TYPE) {
                    return new UnsafeQualifiedStaticFloatFieldAccessorImpl(field, b3);
                }
                if (type == Double.TYPE) {
                    return new UnsafeQualifiedStaticDoubleFieldAccessorImpl(field, b3);
                }
                return new UnsafeQualifiedStaticObjectFieldAccessorImpl(field, b3);
            }
        }
        else if (!b2) {
            if (type == Boolean.TYPE) {
                return new UnsafeBooleanFieldAccessorImpl(field);
            }
            if (type == Byte.TYPE) {
                return new UnsafeByteFieldAccessorImpl(field);
            }
            if (type == Short.TYPE) {
                return new UnsafeShortFieldAccessorImpl(field);
            }
            if (type == Character.TYPE) {
                return new UnsafeCharacterFieldAccessorImpl(field);
            }
            if (type == Integer.TYPE) {
                return new UnsafeIntegerFieldAccessorImpl(field);
            }
            if (type == Long.TYPE) {
                return new UnsafeLongFieldAccessorImpl(field);
            }
            if (type == Float.TYPE) {
                return new UnsafeFloatFieldAccessorImpl(field);
            }
            if (type == Double.TYPE) {
                return new UnsafeDoubleFieldAccessorImpl(field);
            }
            return new UnsafeObjectFieldAccessorImpl(field);
        }
        else {
            if (type == Boolean.TYPE) {
                return new UnsafeQualifiedBooleanFieldAccessorImpl(field, b3);
            }
            if (type == Byte.TYPE) {
                return new UnsafeQualifiedByteFieldAccessorImpl(field, b3);
            }
            if (type == Short.TYPE) {
                return new UnsafeQualifiedShortFieldAccessorImpl(field, b3);
            }
            if (type == Character.TYPE) {
                return new UnsafeQualifiedCharacterFieldAccessorImpl(field, b3);
            }
            if (type == Integer.TYPE) {
                return new UnsafeQualifiedIntegerFieldAccessorImpl(field, b3);
            }
            if (type == Long.TYPE) {
                return new UnsafeQualifiedLongFieldAccessorImpl(field, b3);
            }
            if (type == Float.TYPE) {
                return new UnsafeQualifiedFloatFieldAccessorImpl(field, b3);
            }
            if (type == Double.TYPE) {
                return new UnsafeQualifiedDoubleFieldAccessorImpl(field, b3);
            }
            return new UnsafeQualifiedObjectFieldAccessorImpl(field, b3);
        }
    }
}
