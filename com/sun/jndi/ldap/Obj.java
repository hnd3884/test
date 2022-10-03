package com.sun.jndi.ldap;

import java.lang.reflect.Proxy;
import java.io.ObjectStreamClass;
import javax.naming.spi.DirStateFactory;
import javax.naming.directory.BasicAttributes;
import javax.naming.spi.DirectoryManager;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import javax.naming.NamingEnumeration;
import sun.misc.CharacterDecoder;
import javax.naming.directory.InvalidAttributesException;
import sun.misc.BASE64Decoder;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.RefAddr;
import sun.misc.CharacterEncoder;
import sun.misc.BASE64Encoder;
import javax.naming.StringRefAddr;
import java.io.IOException;
import java.util.Vector;
import java.util.StringTokenizer;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.BasicAttribute;
import java.io.Serializable;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

final class Obj
{
    static VersionHelper helper;
    static final String[] JAVA_ATTRIBUTES;
    static final int OBJECT_CLASS = 0;
    static final int SERIALIZED_DATA = 1;
    static final int CLASSNAME = 2;
    static final int FACTORY = 3;
    static final int CODEBASE = 4;
    static final int REF_ADDR = 5;
    static final int TYPENAME = 6;
    @Deprecated
    private static final int REMOTE_LOC = 7;
    static final String[] JAVA_OBJECT_CLASSES;
    static final String[] JAVA_OBJECT_CLASSES_LOWER;
    static final int STRUCTURAL = 0;
    static final int BASE_OBJECT = 1;
    static final int REF_OBJECT = 2;
    static final int SER_OBJECT = 3;
    static final int MAR_OBJECT = 4;
    
    private Obj() {
    }
    
    private static Attributes encodeObject(final char c, final Object o, Attributes attributes, final Attribute attribute, final boolean b) throws NamingException {
        if (attribute.size() == 0 || (attribute.size() == 1 && attribute.contains("top"))) {
            attribute.add(Obj.JAVA_OBJECT_CLASSES[0]);
        }
        if (o instanceof Referenceable) {
            attribute.add(Obj.JAVA_OBJECT_CLASSES[1]);
            attribute.add(Obj.JAVA_OBJECT_CLASSES[2]);
            if (!b) {
                attributes = (Attributes)attributes.clone();
            }
            attributes.put(attribute);
            return encodeReference(c, ((Referenceable)o).getReference(), attributes, o);
        }
        if (o instanceof Reference) {
            attribute.add(Obj.JAVA_OBJECT_CLASSES[1]);
            attribute.add(Obj.JAVA_OBJECT_CLASSES[2]);
            if (!b) {
                attributes = (Attributes)attributes.clone();
            }
            attributes.put(attribute);
            return encodeReference(c, (Reference)o, attributes, null);
        }
        if (o instanceof Serializable) {
            attribute.add(Obj.JAVA_OBJECT_CLASSES[1]);
            if (!attribute.contains(Obj.JAVA_OBJECT_CLASSES[4]) && !attribute.contains(Obj.JAVA_OBJECT_CLASSES_LOWER[4])) {
                attribute.add(Obj.JAVA_OBJECT_CLASSES[3]);
            }
            if (!b) {
                attributes = (Attributes)attributes.clone();
            }
            attributes.put(attribute);
            attributes.put(new BasicAttribute(Obj.JAVA_ATTRIBUTES[1], serializeObject(o)));
            if (attributes.get(Obj.JAVA_ATTRIBUTES[2]) == null) {
                attributes.put(Obj.JAVA_ATTRIBUTES[2], o.getClass().getName());
            }
            if (attributes.get(Obj.JAVA_ATTRIBUTES[6]) == null) {
                final Attribute typeNameAttr = LdapCtxFactory.createTypeNameAttr(o.getClass());
                if (typeNameAttr != null) {
                    attributes.put(typeNameAttr);
                }
            }
        }
        else if (!(o instanceof DirContext)) {
            throw new IllegalArgumentException("can only bind Referenceable, Serializable, DirContext");
        }
        return attributes;
    }
    
    private static String[] getCodebases(final Attribute attribute) throws NamingException {
        if (attribute == null) {
            return null;
        }
        final StringTokenizer stringTokenizer = new StringTokenizer((String)attribute.get());
        final Vector vector = new Vector(10);
        while (stringTokenizer.hasMoreTokens()) {
            vector.addElement(stringTokenizer.nextToken());
        }
        final String[] array = new String[vector.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = (String)vector.elementAt(i);
        }
        return array;
    }
    
    static Object decodeObject(final Attributes attributes) throws NamingException {
        final String[] codebases = getCodebases(attributes.get(Obj.JAVA_ATTRIBUTES[4]));
        try {
            final Attribute value;
            if ((value = attributes.get(Obj.JAVA_ATTRIBUTES[1])) != null) {
                return deserializeObject((byte[])value.get(), Obj.helper.getURLClassLoader(codebases));
            }
            final Attribute value2;
            if ((value2 = attributes.get(Obj.JAVA_ATTRIBUTES[7])) != null) {
                return decodeRmiObject((String)attributes.get(Obj.JAVA_ATTRIBUTES[2]).get(), (String)value2.get(), codebases);
            }
            final Attribute value3 = attributes.get(Obj.JAVA_ATTRIBUTES[0]);
            if (value3 != null && (value3.contains(Obj.JAVA_OBJECT_CLASSES[2]) || value3.contains(Obj.JAVA_OBJECT_CLASSES_LOWER[2]))) {
                return decodeReference(attributes, codebases);
            }
            return null;
        }
        catch (final IOException rootCause) {
            final NamingException ex = new NamingException();
            ex.setRootCause(rootCause);
            throw ex;
        }
    }
    
    private static Attributes encodeReference(final char c, final Reference reference, final Attributes attributes, final Object o) throws NamingException {
        if (reference == null) {
            return attributes;
        }
        final String className;
        if ((className = reference.getClassName()) != null) {
            attributes.put(new BasicAttribute(Obj.JAVA_ATTRIBUTES[2], className));
        }
        final String factoryClassName;
        if ((factoryClassName = reference.getFactoryClassName()) != null) {
            attributes.put(new BasicAttribute(Obj.JAVA_ATTRIBUTES[3], factoryClassName));
        }
        final String factoryClassLocation;
        if ((factoryClassLocation = reference.getFactoryClassLocation()) != null) {
            attributes.put(new BasicAttribute(Obj.JAVA_ATTRIBUTES[4], factoryClassLocation));
        }
        if (o != null && attributes.get(Obj.JAVA_ATTRIBUTES[6]) != null) {
            final Attribute typeNameAttr = LdapCtxFactory.createTypeNameAttr(o.getClass());
            if (typeNameAttr != null) {
                attributes.put(typeNameAttr);
            }
        }
        final int size = reference.size();
        if (size > 0) {
            final BasicAttribute basicAttribute = new BasicAttribute(Obj.JAVA_ATTRIBUTES[5]);
            CharacterEncoder characterEncoder = null;
            for (int i = 0; i < size; ++i) {
                final RefAddr value = reference.get(i);
                if (value instanceof StringRefAddr) {
                    basicAttribute.add("" + c + i + c + value.getType() + c + value.getContent());
                }
                else {
                    if (characterEncoder == null) {
                        characterEncoder = new BASE64Encoder();
                    }
                    basicAttribute.add("" + c + i + c + value.getType() + c + c + characterEncoder.encodeBuffer(serializeObject(value)));
                }
            }
            attributes.put(basicAttribute);
        }
        return attributes;
    }
    
    private static Object decodeRmiObject(final String s, final String s2, final String[] array) throws NamingException {
        return new Reference(s, new StringRefAddr("URL", s2));
    }
    
    private static Reference decodeReference(final Attributes attributes, final String[] array) throws NamingException, IOException {
        String s = null;
        final Attribute value;
        if ((value = attributes.get(Obj.JAVA_ATTRIBUTES[2])) != null) {
            final String s2 = (String)value.get();
            final Attribute value2;
            if ((value2 = attributes.get(Obj.JAVA_ATTRIBUTES[3])) != null) {
                s = (String)value2.get();
            }
            final Reference reference = new Reference(s2, s, (array != null) ? array[0] : null);
            final Attribute value3;
            if ((value3 = attributes.get(Obj.JAVA_ATTRIBUTES[5])) != null) {
                CharacterDecoder characterDecoder = null;
                final ClassLoader urlClassLoader = Obj.helper.getURLClassLoader(array);
                final Vector vector = new Vector();
                vector.setSize(value3.size());
                final NamingEnumeration<?> all = value3.getAll();
                while (all.hasMore()) {
                    final String s3 = (String)all.next();
                    if (s3.length() == 0) {
                        throw new InvalidAttributeValueException("malformed " + Obj.JAVA_ATTRIBUTES[5] + " attribute - empty attribute value");
                    }
                    final char char1 = s3.charAt(0);
                    final int n = 1;
                    final int index;
                    if ((index = s3.indexOf(char1, n)) < 0) {
                        throw new InvalidAttributeValueException("malformed " + Obj.JAVA_ATTRIBUTES[5] + " attribute - separator '" + char1 + "'not found");
                    }
                    final String substring;
                    if ((substring = s3.substring(n, index)) == null) {
                        throw new InvalidAttributeValueException("malformed " + Obj.JAVA_ATTRIBUTES[5] + " attribute - empty RefAddr position");
                    }
                    int int1;
                    try {
                        int1 = Integer.parseInt(substring);
                    }
                    catch (final NumberFormatException ex) {
                        throw new InvalidAttributeValueException("malformed " + Obj.JAVA_ATTRIBUTES[5] + " attribute - RefAddr position not an integer");
                    }
                    final int n2 = index + 1;
                    final int index2;
                    if ((index2 = s3.indexOf(char1, n2)) < 0) {
                        throw new InvalidAttributeValueException("malformed " + Obj.JAVA_ATTRIBUTES[5] + " attribute - RefAddr type not found");
                    }
                    final String substring2;
                    if ((substring2 = s3.substring(n2, index2)) == null) {
                        throw new InvalidAttributeValueException("malformed " + Obj.JAVA_ATTRIBUTES[5] + " attribute - empty RefAddr type");
                    }
                    int n3 = index2 + 1;
                    if (n3 == s3.length()) {
                        vector.setElementAt(new StringRefAddr(substring2, null), int1);
                    }
                    else if (s3.charAt(n3) == char1) {
                        ++n3;
                        if (characterDecoder == null) {
                            characterDecoder = new BASE64Decoder();
                        }
                        vector.setElementAt(deserializeObject(characterDecoder.decodeBuffer(s3.substring(n3)), urlClassLoader), int1);
                    }
                    else {
                        vector.setElementAt(new StringRefAddr(substring2, s3.substring(n3)), int1);
                    }
                }
                for (int i = 0; i < vector.size(); ++i) {
                    reference.add((RefAddr)vector.elementAt(i));
                }
            }
            return reference;
        }
        throw new InvalidAttributesException(Obj.JAVA_ATTRIBUTES[2] + " attribute is required");
    }
    
    private static byte[] serializeObject(final Object o) throws NamingException {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
                objectOutputStream.writeObject(o);
            }
            return byteArrayOutputStream.toByteArray();
        }
        catch (final IOException rootCause) {
            final NamingException ex = new NamingException();
            ex.setRootCause(rootCause);
            throw ex;
        }
    }
    
    private static Object deserializeObject(final byte[] array, final ClassLoader classLoader) throws NamingException {
        try {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
            try (final ObjectInputStream objectInputStream = (classLoader == null) ? new ObjectInputStream(byteArrayInputStream) : new LoaderInputStream(byteArrayInputStream, classLoader)) {
                return objectInputStream.readObject();
            }
            catch (final ClassNotFoundException rootCause) {
                final NamingException ex = new NamingException();
                ex.setRootCause(rootCause);
                throw ex;
            }
        }
        catch (final IOException rootCause2) {
            final NamingException ex2 = new NamingException();
            ex2.setRootCause(rootCause2);
            throw ex2;
        }
    }
    
    static Attributes determineBindAttrs(final char c, Object object, Attributes attributes, boolean b, final Name name, final Context context, final Hashtable<?, ?> hashtable) throws NamingException {
        final DirStateFactory.Result stateToBind = DirectoryManager.getStateToBind(object, name, context, hashtable, attributes);
        object = stateToBind.getObject();
        attributes = stateToBind.getAttributes();
        if (object == null) {
            return attributes;
        }
        if (attributes == null && object instanceof DirContext) {
            b = true;
            attributes = ((DirContext)object).getAttributes("");
        }
        final boolean b2 = false;
        Object o;
        if (attributes == null || attributes.size() == 0) {
            attributes = new BasicAttributes(true);
            b = true;
            o = new BasicAttribute("objectClass", "top");
        }
        else {
            o = attributes.get("objectClass");
            if (o == null && !attributes.isCaseIgnored()) {
                o = attributes.get("objectclass");
            }
            if (o == null) {
                o = new BasicAttribute("objectClass", "top");
            }
            else if (b2 || !b) {
                o = ((Attribute)o).clone();
            }
        }
        attributes = encodeObject(c, object, attributes, (Attribute)o, b);
        return attributes;
    }
    
    static {
        Obj.helper = VersionHelper.getVersionHelper();
        JAVA_ATTRIBUTES = new String[] { "objectClass", "javaSerializedData", "javaClassName", "javaFactory", "javaCodeBase", "javaReferenceAddress", "javaClassNames", "javaRemoteLocation" };
        JAVA_OBJECT_CLASSES = new String[] { "javaContainer", "javaObject", "javaNamingReference", "javaSerializedObject", "javaMarshalledObject" };
        JAVA_OBJECT_CLASSES_LOWER = new String[] { "javacontainer", "javaobject", "javanamingreference", "javaserializedobject", "javamarshalledobject" };
    }
    
    private static final class LoaderInputStream extends ObjectInputStream
    {
        private ClassLoader classLoader;
        
        LoaderInputStream(final InputStream inputStream, final ClassLoader classLoader) throws IOException {
            super(inputStream);
            this.classLoader = classLoader;
        }
        
        @Override
        protected Class<?> resolveClass(final ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
            try {
                return this.classLoader.loadClass(objectStreamClass.getName());
            }
            catch (final ClassNotFoundException ex) {
                return super.resolveClass(objectStreamClass);
            }
        }
        
        @Override
        protected Class<?> resolveProxyClass(final String[] array) throws IOException, ClassNotFoundException {
            ClassLoader classLoader = null;
            int n = 0;
            final Class[] array2 = new Class[array.length];
            for (int i = 0; i < array.length; ++i) {
                final Class<?> forName = Class.forName(array[i], false, this.classLoader);
                if ((forName.getModifiers() & 0x1) == 0x0) {
                    if (n != 0) {
                        if (classLoader != forName.getClassLoader()) {
                            throw new IllegalAccessError("conflicting non-public interface class loaders");
                        }
                    }
                    else {
                        classLoader = forName.getClassLoader();
                        n = 1;
                    }
                }
                array2[i] = forName;
            }
            try {
                return Proxy.getProxyClass((n != 0) ? classLoader : this.classLoader, (Class<?>[])array2);
            }
            catch (final IllegalArgumentException ex) {
                throw new ClassNotFoundException(null, ex);
            }
        }
    }
}
