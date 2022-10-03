package com.sun.corba.se.impl.io;

import java.util.Iterator;
import java.util.Stack;
import sun.corba.SharedSecrets;
import sun.corba.JavaCorbaAccess;
import org.omg.CORBA.TypeCode;
import com.sun.org.omg.SendingContext.CodeBase;
import com.sun.org.omg.CORBA._IDLTypeStub;
import org.omg.CORBA.TCKind;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.rmi.Remote;
import com.sun.org.omg.CORBA.Initializer;
import com.sun.org.omg.CORBA.AttributeDescription;
import com.sun.org.omg.CORBA.OperationDescription;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.ORB;
import com.sun.corba.se.impl.util.RepositoryId;
import org.omg.CORBA.ValueMember;

public class ValueUtility
{
    public static final short PRIVATE_MEMBER = 0;
    public static final short PUBLIC_MEMBER = 1;
    private static final String[] primitiveConstants;
    
    public static String getSignature(final ValueMember valueMember) throws ClassNotFoundException {
        if (valueMember.type.kind().value() == 30 || valueMember.type.kind().value() == 29 || valueMember.type.kind().value() == 14) {
            return ObjectStreamClass.getSignature(RepositoryId.cache.getId(valueMember.id).getClassFromType());
        }
        return ValueUtility.primitiveConstants[valueMember.type.kind().value()];
    }
    
    public static FullValueDescription translate(final ORB orb, final ObjectStreamClass objectStreamClass, final ValueHandler valueHandler) {
        final FullValueDescription fullValueDescription = new FullValueDescription();
        final Class<?> forClass = objectStreamClass.forClass();
        final ValueHandlerImpl valueHandlerImpl = (ValueHandlerImpl)valueHandler;
        final String forAnyType = valueHandlerImpl.createForAnyType(forClass);
        fullValueDescription.name = valueHandlerImpl.getUnqualifiedName(forAnyType);
        if (fullValueDescription.name == null) {
            fullValueDescription.name = "";
        }
        fullValueDescription.id = valueHandlerImpl.getRMIRepositoryID(forClass);
        if (fullValueDescription.id == null) {
            fullValueDescription.id = "";
        }
        fullValueDescription.is_abstract = ObjectStreamClassCorbaExt.isAbstractInterface(forClass);
        fullValueDescription.is_custom = (objectStreamClass.hasWriteObject() || objectStreamClass.isExternalizable());
        fullValueDescription.defined_in = valueHandlerImpl.getDefinedInId(forAnyType);
        if (fullValueDescription.defined_in == null) {
            fullValueDescription.defined_in = "";
        }
        fullValueDescription.version = valueHandlerImpl.getSerialVersionUID(forAnyType);
        if (fullValueDescription.version == null) {
            fullValueDescription.version = "";
        }
        fullValueDescription.operations = new OperationDescription[0];
        fullValueDescription.attributes = new AttributeDescription[0];
        fullValueDescription.members = translateMembers(orb, objectStreamClass, valueHandler, new IdentityKeyValueStack());
        fullValueDescription.initializers = new Initializer[0];
        final Class<?>[] interfaces = objectStreamClass.forClass().getInterfaces();
        int n = 0;
        fullValueDescription.supported_interfaces = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; ++i) {
            fullValueDescription.supported_interfaces[i] = valueHandlerImpl.createForAnyType(interfaces[i]);
            if (!Remote.class.isAssignableFrom(interfaces[i]) || !Modifier.isPublic(interfaces[i].getModifiers())) {
                ++n;
            }
        }
        fullValueDescription.abstract_base_values = new String[n];
        for (int j = 0; j < interfaces.length; ++j) {
            if (!Remote.class.isAssignableFrom(interfaces[j]) || !Modifier.isPublic(interfaces[j].getModifiers())) {
                fullValueDescription.abstract_base_values[j] = valueHandlerImpl.createForAnyType(interfaces[j]);
            }
        }
        fullValueDescription.is_truncatable = false;
        final Class<?> superclass = objectStreamClass.forClass().getSuperclass();
        if (Serializable.class.isAssignableFrom(superclass)) {
            fullValueDescription.base_value = valueHandlerImpl.getRMIRepositoryID(superclass);
        }
        else {
            fullValueDescription.base_value = "";
        }
        fullValueDescription.type = orb.get_primitive_tc(TCKind.tk_value);
        return fullValueDescription;
    }
    
    private static ValueMember[] translateMembers(final ORB orb, final ObjectStreamClass objectStreamClass, final ValueHandler valueHandler, final IdentityKeyValueStack identityKeyValueStack) {
        final ValueHandlerImpl valueHandlerImpl = (ValueHandlerImpl)valueHandler;
        final ObjectStreamField[] fields = objectStreamClass.getFields();
        final int length = fields.length;
        final ValueMember[] array = new ValueMember[length];
        for (int i = 0; i < length; ++i) {
            final String rmiRepositoryID = valueHandlerImpl.getRMIRepositoryID(fields[i].getClazz());
            array[i] = new ValueMember();
            array[i].name = fields[i].getName();
            array[i].id = rmiRepositoryID;
            array[i].defined_in = valueHandlerImpl.getDefinedInId(rmiRepositoryID);
            array[i].version = "1.0";
            array[i].type_def = new _IDLTypeStub();
            if (fields[i].getField() == null) {
                array[i].access = 0;
            }
            else if (Modifier.isPublic(fields[i].getField().getModifiers())) {
                array[i].access = 1;
            }
            else {
                array[i].access = 0;
            }
            switch (fields[i].getTypeCode()) {
                case 'B': {
                    array[i].type = orb.get_primitive_tc(TCKind.tk_octet);
                    break;
                }
                case 'C': {
                    array[i].type = orb.get_primitive_tc(valueHandlerImpl.getJavaCharTCKind());
                    break;
                }
                case 'F': {
                    array[i].type = orb.get_primitive_tc(TCKind.tk_float);
                    break;
                }
                case 'D': {
                    array[i].type = orb.get_primitive_tc(TCKind.tk_double);
                    break;
                }
                case 'I': {
                    array[i].type = orb.get_primitive_tc(TCKind.tk_long);
                    break;
                }
                case 'J': {
                    array[i].type = orb.get_primitive_tc(TCKind.tk_longlong);
                    break;
                }
                case 'S': {
                    array[i].type = orb.get_primitive_tc(TCKind.tk_short);
                    break;
                }
                case 'Z': {
                    array[i].type = orb.get_primitive_tc(TCKind.tk_boolean);
                    break;
                }
                default: {
                    array[i].type = createTypeCodeForClassInternal(orb, fields[i].getClazz(), valueHandlerImpl, identityKeyValueStack);
                    array[i].id = valueHandlerImpl.createForAnyType(fields[i].getType());
                    break;
                }
            }
        }
        return array;
    }
    
    private static boolean exists(final String s, final String[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (s.equals(array[i])) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAssignableFrom(final String s, final FullValueDescription fullValueDescription, final CodeBase codeBase) {
        return exists(s, fullValueDescription.supported_interfaces) || s.equals(fullValueDescription.id) || (fullValueDescription.base_value != null && !fullValueDescription.base_value.equals("") && isAssignableFrom(s, codeBase.meta(fullValueDescription.base_value), codeBase));
    }
    
    public static TypeCode createTypeCodeForClass(final ORB orb, final Class clazz, final ValueHandler valueHandler) {
        return createTypeCodeForClassInternal(orb, clazz, valueHandler, new IdentityKeyValueStack());
    }
    
    private static TypeCode createTypeCodeForClassInternal(final ORB orb, final Class clazz, final ValueHandler valueHandler, final IdentityKeyValueStack identityKeyValueStack) {
        final String s = (String)identityKeyValueStack.get(clazz);
        if (s != null) {
            return orb.create_recursive_tc(s);
        }
        String rmiRepositoryID = valueHandler.getRMIRepositoryID(clazz);
        if (rmiRepositoryID == null) {
            rmiRepositoryID = "";
        }
        identityKeyValueStack.push(clazz, rmiRepositoryID);
        final TypeCode typeCodeInternal = createTypeCodeInternal(orb, clazz, valueHandler, rmiRepositoryID, identityKeyValueStack);
        identityKeyValueStack.pop();
        return typeCodeInternal;
    }
    
    private static TypeCode createTypeCodeInternal(final ORB orb, final Class clazz, final ValueHandler valueHandler, final String s, final IdentityKeyValueStack identityKeyValueStack) {
        if (clazz.isArray()) {
            final Class componentType = clazz.getComponentType();
            TypeCode typeCode;
            if (componentType.isPrimitive()) {
                typeCode = getPrimitiveTypeCodeForClass(orb, componentType, valueHandler);
            }
            else {
                typeCode = createTypeCodeForClassInternal(orb, componentType, valueHandler, identityKeyValueStack);
            }
            return orb.create_value_box_tc(s, "Sequence", orb.create_sequence_tc(0, typeCode));
        }
        if (clazz == String.class) {
            return orb.create_value_box_tc(s, "StringValue", orb.create_string_tc(0));
        }
        if (Remote.class.isAssignableFrom(clazz)) {
            return orb.get_primitive_tc(TCKind.tk_objref);
        }
        if (org.omg.CORBA.Object.class.isAssignableFrom(clazz)) {
            return orb.get_primitive_tc(TCKind.tk_objref);
        }
        final ObjectStreamClass lookup = ObjectStreamClass.lookup(clazz);
        if (lookup == null) {
            return orb.create_value_box_tc(s, "Value", orb.get_primitive_tc(TCKind.tk_value));
        }
        final int customMarshaled = lookup.isCustomMarshaled() ? 1 : 0;
        TypeCode typeCodeForClassInternal = null;
        final Class superclass = clazz.getSuperclass();
        if (superclass != null && Serializable.class.isAssignableFrom(superclass)) {
            typeCodeForClassInternal = createTypeCodeForClassInternal(orb, superclass, valueHandler, identityKeyValueStack);
        }
        return orb.create_value_tc(s, clazz.getName(), (short)customMarshaled, typeCodeForClassInternal, translateMembers(orb, lookup, valueHandler, identityKeyValueStack));
    }
    
    public static TypeCode getPrimitiveTypeCodeForClass(final ORB orb, final Class clazz, final ValueHandler valueHandler) {
        if (clazz == Integer.TYPE) {
            return orb.get_primitive_tc(TCKind.tk_long);
        }
        if (clazz == Byte.TYPE) {
            return orb.get_primitive_tc(TCKind.tk_octet);
        }
        if (clazz == Long.TYPE) {
            return orb.get_primitive_tc(TCKind.tk_longlong);
        }
        if (clazz == Float.TYPE) {
            return orb.get_primitive_tc(TCKind.tk_float);
        }
        if (clazz == Double.TYPE) {
            return orb.get_primitive_tc(TCKind.tk_double);
        }
        if (clazz == Short.TYPE) {
            return orb.get_primitive_tc(TCKind.tk_short);
        }
        if (clazz == Character.TYPE) {
            return orb.get_primitive_tc(((ValueHandlerImpl)valueHandler).getJavaCharTCKind());
        }
        if (clazz == Boolean.TYPE) {
            return orb.get_primitive_tc(TCKind.tk_boolean);
        }
        return orb.get_primitive_tc(TCKind.tk_any);
    }
    
    static {
        primitiveConstants = new String[] { null, null, "S", "I", "S", "I", "F", "D", "Z", "C", "B", null, null, null, null, null, null, null, null, null, null, null, null, "J", "J", "D", "C", null, null, null, null, null, null };
        SharedSecrets.setJavaCorbaAccess(new JavaCorbaAccess() {
            @Override
            public ValueHandlerImpl newValueHandlerImpl() {
                return ValueHandlerImpl.getInstance();
            }
            
            @Override
            public Class<?> loadClass(final String s) throws ClassNotFoundException {
                if (Thread.currentThread().getContextClassLoader() != null) {
                    return Thread.currentThread().getContextClassLoader().loadClass(s);
                }
                return ClassLoader.getSystemClassLoader().loadClass(s);
            }
        });
    }
    
    private static class IdentityKeyValueStack
    {
        Stack pairs;
        
        private IdentityKeyValueStack() {
            this.pairs = null;
        }
        
        Object get(final Object o) {
            if (this.pairs == null) {
                return null;
            }
            for (final KeyValuePair keyValuePair : this.pairs) {
                if (keyValuePair.key == o) {
                    return keyValuePair.value;
                }
            }
            return null;
        }
        
        void push(final Object o, final Object o2) {
            if (this.pairs == null) {
                this.pairs = new Stack();
            }
            this.pairs.push(new KeyValuePair(o, o2));
        }
        
        void pop() {
            this.pairs.pop();
        }
        
        private static class KeyValuePair
        {
            Object key;
            Object value;
            
            KeyValuePair(final Object key, final Object value) {
                this.key = key;
                this.value = value;
            }
            
            boolean equals(final KeyValuePair keyValuePair) {
                return keyValuePair.key == this.key;
            }
        }
    }
}
