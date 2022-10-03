package com.sun.xml.internal.ws.model;

import javax.xml.ws.Holder;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeInlineAnnotationReader;
import java.io.IOException;
import java.io.FileOutputStream;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import javax.xml.namespace.QName;
import java.lang.reflect.Method;
import com.sun.xml.internal.ws.org.objectweb.asm.MethodVisitor;
import com.sun.xml.internal.ws.org.objectweb.asm.FieldVisitor;
import java.util.Iterator;
import com.sun.xml.internal.ws.org.objectweb.asm.AnnotationVisitor;
import java.util.logging.Level;
import javax.xml.ws.WebServiceException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlAttachmentRef;
import java.lang.reflect.Type;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlMimeType;
import java.lang.annotation.Annotation;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassWriter;
import java.util.Collection;
import java.util.logging.Logger;

public class WrapperBeanGenerator
{
    private static final Logger LOGGER;
    private static final FieldFactory FIELD_FACTORY;
    private static final AbstractWrapperBeanGenerator RUNTIME_GENERATOR;
    
    private static byte[] createBeanImage(final String className, final String rootName, final String rootNS, final String typeName, final String typeNS, final Collection<Field> fields) throws Exception {
        final ClassWriter cw = new ClassWriter(0);
        cw.visit(49, 33, replaceDotWithSlash(className), null, "java/lang/Object", null);
        final AnnotationVisitor root = cw.visitAnnotation("Ljavax/xml/bind/annotation/XmlRootElement;", true);
        root.visit("name", rootName);
        root.visit("namespace", rootNS);
        root.visitEnd();
        final AnnotationVisitor type = cw.visitAnnotation("Ljavax/xml/bind/annotation/XmlType;", true);
        type.visit("name", typeName);
        type.visit("namespace", typeNS);
        if (fields.size() > 1) {
            final AnnotationVisitor propVisitor = type.visitArray("propOrder");
            for (final Field field : fields) {
                propVisitor.visit("propOrder", field.fieldName);
            }
            propVisitor.visitEnd();
        }
        type.visitEnd();
        for (final Field field2 : fields) {
            final FieldVisitor fv = cw.visitField(1, field2.fieldName, field2.asmType.getDescriptor(), field2.getSignature(), null);
            for (final Annotation ann : field2.jaxbAnnotations) {
                if (ann instanceof XmlMimeType) {
                    final AnnotationVisitor mime = fv.visitAnnotation("Ljavax/xml/bind/annotation/XmlMimeType;", true);
                    mime.visit("value", ((XmlMimeType)ann).value());
                    mime.visitEnd();
                }
                else if (ann instanceof XmlJavaTypeAdapter) {
                    final AnnotationVisitor ada = fv.visitAnnotation("Ljavax/xml/bind/annotation/adapters/XmlJavaTypeAdapter;", true);
                    ada.visit("value", getASMType(((XmlJavaTypeAdapter)ann).value()));
                    ada.visitEnd();
                }
                else if (ann instanceof XmlAttachmentRef) {
                    final AnnotationVisitor att = fv.visitAnnotation("Ljavax/xml/bind/annotation/XmlAttachmentRef;", true);
                    att.visitEnd();
                }
                else if (ann instanceof XmlList) {
                    final AnnotationVisitor list = fv.visitAnnotation("Ljavax/xml/bind/annotation/XmlList;", true);
                    list.visitEnd();
                }
                else {
                    if (!(ann instanceof XmlElement)) {
                        throw new WebServiceException("Unknown JAXB annotation " + ann);
                    }
                    final AnnotationVisitor elem = fv.visitAnnotation("Ljavax/xml/bind/annotation/XmlElement;", true);
                    final XmlElement xmlElem = (XmlElement)ann;
                    elem.visit("name", xmlElem.name());
                    elem.visit("namespace", xmlElem.namespace());
                    if (xmlElem.nillable()) {
                        elem.visit("nillable", true);
                    }
                    if (xmlElem.required()) {
                        elem.visit("required", true);
                    }
                    elem.visitEnd();
                }
            }
            fv.visitEnd();
        }
        final MethodVisitor mv = cw.visitMethod(1, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(183, "java/lang/Object", "<init>", "()V");
        mv.visitInsn(177);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        cw.visitEnd();
        if (WrapperBeanGenerator.LOGGER.isLoggable(Level.FINE)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append("@XmlRootElement(name=").append(rootName).append(", namespace=").append(rootNS).append(")");
            sb.append("\n");
            sb.append("@XmlType(name=").append(typeName).append(", namespace=").append(typeNS);
            if (fields.size() > 1) {
                sb.append(", propOrder={");
                for (final Field field3 : fields) {
                    sb.append(" ");
                    sb.append(field3.fieldName);
                }
                sb.append(" }");
            }
            sb.append(")");
            sb.append("\n");
            sb.append("public class ").append(className).append(" {");
            for (final Field field3 : fields) {
                sb.append("\n");
                for (final Annotation ann2 : field3.jaxbAnnotations) {
                    sb.append("\n    ");
                    if (ann2 instanceof XmlMimeType) {
                        sb.append("@XmlMimeType(value=").append(((XmlMimeType)ann2).value()).append(")");
                    }
                    else if (ann2 instanceof XmlJavaTypeAdapter) {
                        sb.append("@XmlJavaTypeAdapter(value=").append(getASMType(((XmlJavaTypeAdapter)ann2).value())).append(")");
                    }
                    else if (ann2 instanceof XmlAttachmentRef) {
                        sb.append("@XmlAttachmentRef");
                    }
                    else if (ann2 instanceof XmlList) {
                        sb.append("@XmlList");
                    }
                    else {
                        if (!(ann2 instanceof XmlElement)) {
                            throw new WebServiceException("Unknown JAXB annotation " + ann2);
                        }
                        final XmlElement xmlElem = (XmlElement)ann2;
                        sb.append("\n    ");
                        sb.append("@XmlElement(name=").append(xmlElem.name()).append(", namespace=").append(xmlElem.namespace());
                        if (xmlElem.nillable()) {
                            sb.append(", nillable=true");
                        }
                        if (xmlElem.required()) {
                            sb.append(", required=true");
                        }
                        sb.append(")");
                    }
                }
                sb.append("\n    ");
                sb.append("public ");
                if (field3.getSignature() == null) {
                    sb.append(field3.asmType.getDescriptor());
                }
                else {
                    sb.append(field3.getSignature());
                }
                sb.append(" ");
                sb.append(field3.fieldName);
            }
            sb.append("\n\n}");
            WrapperBeanGenerator.LOGGER.fine(sb.toString());
        }
        return cw.toByteArray();
    }
    
    private static String replaceDotWithSlash(final String name) {
        return name.replace('.', '/');
    }
    
    static Class createRequestWrapperBean(final String className, final Method method, final QName reqElemName, final ClassLoader cl) {
        if (WrapperBeanGenerator.LOGGER.isLoggable(Level.FINE)) {
            WrapperBeanGenerator.LOGGER.log(Level.FINE, "Request Wrapper Class : {0}", className);
        }
        final List<Field> requestMembers = WrapperBeanGenerator.RUNTIME_GENERATOR.collectRequestBeanMembers(method);
        byte[] image;
        try {
            image = createBeanImage(className, reqElemName.getLocalPart(), reqElemName.getNamespaceURI(), reqElemName.getLocalPart(), reqElemName.getNamespaceURI(), requestMembers);
        }
        catch (final Exception e) {
            throw new WebServiceException(e);
        }
        return Injector.inject(cl, className, image);
    }
    
    static Class createResponseWrapperBean(final String className, final Method method, final QName resElemName, final ClassLoader cl) {
        if (WrapperBeanGenerator.LOGGER.isLoggable(Level.FINE)) {
            WrapperBeanGenerator.LOGGER.log(Level.FINE, "Response Wrapper Class : {0}", className);
        }
        final List<Field> responseMembers = WrapperBeanGenerator.RUNTIME_GENERATOR.collectResponseBeanMembers(method);
        byte[] image;
        try {
            image = createBeanImage(className, resElemName.getLocalPart(), resElemName.getNamespaceURI(), resElemName.getLocalPart(), resElemName.getNamespaceURI(), responseMembers);
        }
        catch (final Exception e) {
            throw new WebServiceException(e);
        }
        return Injector.inject(cl, className, image);
    }
    
    private static com.sun.xml.internal.ws.org.objectweb.asm.Type getASMType(final Type t) {
        assert t != null;
        if (t instanceof Class) {
            return com.sun.xml.internal.ws.org.objectweb.asm.Type.getType((Class)t);
        }
        if (t instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType)t;
            if (pt.getRawType() instanceof Class) {
                return com.sun.xml.internal.ws.org.objectweb.asm.Type.getType((Class)pt.getRawType());
            }
        }
        if (t instanceof GenericArrayType) {
            return com.sun.xml.internal.ws.org.objectweb.asm.Type.getType(FieldSignature.vms(t));
        }
        if (t instanceof WildcardType) {
            return com.sun.xml.internal.ws.org.objectweb.asm.Type.getType(FieldSignature.vms(t));
        }
        if (t instanceof TypeVariable) {
            final TypeVariable tv = (TypeVariable)t;
            if (tv.getBounds()[0] instanceof Class) {
                return com.sun.xml.internal.ws.org.objectweb.asm.Type.getType((Class)tv.getBounds()[0]);
            }
        }
        throw new IllegalArgumentException("Not creating ASM Type for type = " + t);
    }
    
    static Class createExceptionBean(final String className, final Class exception, final String typeNS, final String elemName, final String elemNS, final ClassLoader cl) {
        return createExceptionBean(className, exception, typeNS, elemName, elemNS, cl, true);
    }
    
    static Class createExceptionBean(final String className, final Class exception, final String typeNS, final String elemName, final String elemNS, final ClassLoader cl, final boolean decapitalizeExceptionBeanProperties) {
        final Collection<Field> fields = WrapperBeanGenerator.RUNTIME_GENERATOR.collectExceptionBeanMembers(exception, decapitalizeExceptionBeanProperties);
        byte[] image;
        try {
            image = createBeanImage(className, elemName, elemNS, exception.getSimpleName(), typeNS, fields);
        }
        catch (final Exception e) {
            throw new WebServiceException(e);
        }
        return Injector.inject(cl, className, image);
    }
    
    static void write(final byte[] b, String className) {
        className = className.substring(className.lastIndexOf(".") + 1);
        try {
            final FileOutputStream fo = new FileOutputStream(className + ".class");
            fo.write(b);
            fo.flush();
            fo.close();
        }
        catch (final IOException e) {
            WrapperBeanGenerator.LOGGER.log(Level.INFO, "Error Writing class", e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(WrapperBeanGenerator.class.getName());
        FIELD_FACTORY = new FieldFactory();
        RUNTIME_GENERATOR = new RuntimeWrapperBeanGenerator(new RuntimeInlineAnnotationReader(), Utils.REFLECTION_NAVIGATOR, WrapperBeanGenerator.FIELD_FACTORY);
    }
    
    private static final class RuntimeWrapperBeanGenerator extends AbstractWrapperBeanGenerator<Type, Class, Method, Field>
    {
        protected RuntimeWrapperBeanGenerator(final AnnotationReader<Type, Class, ?, Method> annReader, final Navigator<Type, Class, ?, Method> nav, final BeanMemberFactory<Type, Field> beanMemberFactory) {
            super(annReader, nav, beanMemberFactory);
        }
        
        @Override
        protected Type getSafeType(final Type type) {
            return type;
        }
        
        @Override
        protected Type getHolderValueType(final Type paramType) {
            if (paramType instanceof ParameterizedType) {
                final ParameterizedType p = (ParameterizedType)paramType;
                if (p.getRawType().equals(Holder.class)) {
                    return p.getActualTypeArguments()[0];
                }
            }
            return null;
        }
        
        @Override
        protected boolean isVoidType(final Type type) {
            return type == Void.TYPE;
        }
    }
    
    private static final class FieldFactory implements AbstractWrapperBeanGenerator.BeanMemberFactory<Type, Field>
    {
        @Override
        public Field createWrapperBeanMember(final Type paramType, final String paramName, final List<Annotation> jaxb) {
            return new Field(paramName, paramType, getASMType(paramType), jaxb);
        }
    }
    
    private static class Field implements Comparable<Field>
    {
        private final Type reflectType;
        private final com.sun.xml.internal.ws.org.objectweb.asm.Type asmType;
        private final String fieldName;
        private final List<Annotation> jaxbAnnotations;
        
        Field(final String paramName, final Type paramType, final com.sun.xml.internal.ws.org.objectweb.asm.Type asmType, final List<Annotation> jaxbAnnotations) {
            this.reflectType = paramType;
            this.asmType = asmType;
            this.fieldName = paramName;
            this.jaxbAnnotations = jaxbAnnotations;
        }
        
        String getSignature() {
            if (this.reflectType instanceof Class) {
                return null;
            }
            if (this.reflectType instanceof TypeVariable) {
                return null;
            }
            return FieldSignature.vms(this.reflectType);
        }
        
        @Override
        public int compareTo(final Field o) {
            return this.fieldName.compareTo(o.fieldName);
        }
    }
}
