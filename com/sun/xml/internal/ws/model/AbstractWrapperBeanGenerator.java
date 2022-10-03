package com.sun.xml.internal.ws.model;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlAttachmentRef;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.spi.db.BindingHelper;
import java.util.Collections;
import com.sun.xml.internal.ws.util.StringUtils;
import javax.xml.ws.WebServiceException;
import javax.xml.bind.annotation.XmlType;
import java.util.TreeMap;
import java.util.Collection;
import java.util.Iterator;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import javax.xml.bind.annotation.XmlElement;
import javax.jws.WebResult;
import javax.jws.WebParam;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import java.util.ArrayList;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import java.util.Set;
import java.util.logging.Logger;

public abstract class AbstractWrapperBeanGenerator<T, C, M, A extends Comparable>
{
    private static final Logger LOGGER;
    private static final String RETURN = "return";
    private static final String EMTPY_NAMESPACE_ID = "";
    private static final Class[] jaxbAnns;
    private static final Set<String> skipProperties;
    private final AnnotationReader<T, C, ?, M> annReader;
    private final Navigator<T, C, ?, M> nav;
    private final BeanMemberFactory<T, A> factory;
    private static final Map<String, String> reservedWords;
    
    protected AbstractWrapperBeanGenerator(final AnnotationReader<T, C, ?, M> annReader, final Navigator<T, C, ?, M> nav, final BeanMemberFactory<T, A> factory) {
        this.annReader = annReader;
        this.nav = nav;
        this.factory = factory;
    }
    
    private List<Annotation> collectJAXBAnnotations(final M method) {
        final List<Annotation> jaxbAnnotation = new ArrayList<Annotation>();
        for (final Class jaxbClass : AbstractWrapperBeanGenerator.jaxbAnns) {
            final Annotation ann = this.annReader.getMethodAnnotation((Class<Annotation>)jaxbClass, method, null);
            if (ann != null) {
                jaxbAnnotation.add(ann);
            }
        }
        return jaxbAnnotation;
    }
    
    private List<Annotation> collectJAXBAnnotations(final M method, final int paramIndex) {
        final List<Annotation> jaxbAnnotation = new ArrayList<Annotation>();
        for (final Class jaxbClass : AbstractWrapperBeanGenerator.jaxbAnns) {
            final Annotation ann = this.annReader.getMethodParameterAnnotation((Class<Annotation>)jaxbClass, method, paramIndex, null);
            if (ann != null) {
                jaxbAnnotation.add(ann);
            }
        }
        return jaxbAnnotation;
    }
    
    protected abstract T getSafeType(final T p0);
    
    protected abstract T getHolderValueType(final T p0);
    
    protected abstract boolean isVoidType(final T p0);
    
    public List<A> collectRequestBeanMembers(final M method) {
        final List<A> requestMembers = new ArrayList<A>();
        int paramIndex = -1;
        for (final T param : this.nav.getMethodParameters(method)) {
            ++paramIndex;
            final WebParam webParam = this.annReader.getMethodParameterAnnotation(WebParam.class, method, paramIndex, null);
            Label_0259: {
                if (webParam != null) {
                    if (webParam.header()) {
                        break Label_0259;
                    }
                    if (webParam.mode().equals(WebParam.Mode.OUT)) {
                        break Label_0259;
                    }
                }
                final T holderType = this.getHolderValueType(param);
                final T paramType = (T)((holderType != null) ? holderType : this.getSafeType(param));
                final String paramName = (webParam != null && webParam.name().length() > 0) ? webParam.name() : ("arg" + paramIndex);
                final String paramNamespace = (webParam != null && webParam.targetNamespace().length() > 0) ? webParam.targetNamespace() : "";
                final List<Annotation> jaxbAnnotation = this.collectJAXBAnnotations(method, paramIndex);
                this.processXmlElement(jaxbAnnotation, paramName, paramNamespace, paramType);
                final A member = this.factory.createWrapperBeanMember(paramType, getPropertyName(paramName), jaxbAnnotation);
                requestMembers.add(member);
            }
        }
        return requestMembers;
    }
    
    public List<A> collectResponseBeanMembers(final M method) {
        final List<A> responseMembers = new ArrayList<A>();
        String responseElementName = "return";
        String responseNamespace = "";
        boolean isResultHeader = false;
        final WebResult webResult = this.annReader.getMethodAnnotation(WebResult.class, method, null);
        if (webResult != null) {
            if (webResult.name().length() > 0) {
                responseElementName = webResult.name();
            }
            if (webResult.targetNamespace().length() > 0) {
                responseNamespace = webResult.targetNamespace();
            }
            isResultHeader = webResult.header();
        }
        final T returnType = this.getSafeType(this.nav.getReturnType(method));
        if (!this.isVoidType(returnType) && !isResultHeader) {
            final List<Annotation> jaxbRespAnnotations = this.collectJAXBAnnotations(method);
            this.processXmlElement(jaxbRespAnnotations, responseElementName, responseNamespace, returnType);
            responseMembers.add(this.factory.createWrapperBeanMember(returnType, getPropertyName(responseElementName), jaxbRespAnnotations));
        }
        int paramIndex = -1;
        for (final T param : this.nav.getMethodParameters(method)) {
            ++paramIndex;
            final T paramType = this.getHolderValueType(param);
            final WebParam webParam = this.annReader.getMethodParameterAnnotation(WebParam.class, method, paramIndex, null);
            if (paramType != null) {
                if (webParam == null || !webParam.header()) {
                    final String paramName = (webParam != null && webParam.name().length() > 0) ? webParam.name() : ("arg" + paramIndex);
                    final String paramNamespace = (webParam != null && webParam.targetNamespace().length() > 0) ? webParam.targetNamespace() : "";
                    final List<Annotation> jaxbAnnotation = this.collectJAXBAnnotations(method, paramIndex);
                    this.processXmlElement(jaxbAnnotation, paramName, paramNamespace, paramType);
                    final A member = this.factory.createWrapperBeanMember(paramType, getPropertyName(paramName), jaxbAnnotation);
                    responseMembers.add(member);
                }
            }
        }
        return responseMembers;
    }
    
    private void processXmlElement(final List<Annotation> jaxb, final String elemName, final String elemNS, final T type) {
        XmlElement elemAnn = null;
        for (final Annotation a : jaxb) {
            if (a.annotationType() == XmlElement.class) {
                elemAnn = (XmlElement)a;
                jaxb.remove(a);
                break;
            }
        }
        final String name = (elemAnn != null && !elemAnn.name().equals("##default")) ? elemAnn.name() : elemName;
        final String ns = (elemAnn != null && !elemAnn.namespace().equals("##default")) ? elemAnn.namespace() : elemNS;
        final boolean nillable = this.nav.isArray(type) || (elemAnn != null && elemAnn.nillable());
        final boolean required = elemAnn != null && elemAnn.required();
        final XmlElementHandler handler = new XmlElementHandler(name, ns, nillable, required);
        final XmlElement elem = (XmlElement)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { XmlElement.class }, handler);
        jaxb.add(elem);
    }
    
    public Collection<A> collectExceptionBeanMembers(final C exception) {
        return this.collectExceptionBeanMembers(exception, true);
    }
    
    public Collection<A> collectExceptionBeanMembers(final C exception, final boolean decapitalize) {
        final TreeMap<String, A> fields = new TreeMap<String, A>();
        this.getExceptionProperties(exception, fields, decapitalize);
        final XmlType xmlType = this.annReader.getClassAnnotation(XmlType.class, exception, null);
        if (xmlType != null) {
            final String[] propOrder = xmlType.propOrder();
            if (propOrder.length > 0 && propOrder[0].length() != 0) {
                final List<A> list = new ArrayList<A>();
                for (final String prop : propOrder) {
                    final A a = fields.get(prop);
                    if (a == null) {
                        throw new WebServiceException("Exception " + exception + " has @XmlType and its propOrder contains unknown property " + prop);
                    }
                    list.add(a);
                }
                return list;
            }
        }
        return fields.values();
    }
    
    private void getExceptionProperties(final C exception, final TreeMap<String, A> fields, final boolean decapitalize) {
        final C sc = this.nav.getSuperClass(exception);
        if (sc != null) {
            this.getExceptionProperties(sc, fields, decapitalize);
        }
        final Collection<? extends M> methods = this.nav.getDeclaredMethods(exception);
        for (final M method : methods) {
            if (this.nav.isPublicMethod(method)) {
                if (this.nav.isStaticMethod(method) && this.nav.isFinalMethod(method)) {
                    continue;
                }
                if (!this.nav.isPublicMethod(method)) {
                    continue;
                }
                final String name = this.nav.getMethodName(method);
                if ((!name.startsWith("get") && !name.startsWith("is")) || AbstractWrapperBeanGenerator.skipProperties.contains(name) || name.equals("get")) {
                    continue;
                }
                if (name.equals("is")) {
                    continue;
                }
                final T returnType = this.getSafeType(this.nav.getReturnType(method));
                if (this.nav.getMethodParameters(method).length != 0) {
                    continue;
                }
                String fieldName = name.startsWith("get") ? name.substring(3) : name.substring(2);
                if (decapitalize) {
                    fieldName = StringUtils.decapitalize(fieldName);
                }
                fields.put(fieldName, this.factory.createWrapperBeanMember(returnType, fieldName, Collections.emptyList()));
            }
        }
    }
    
    private static String getPropertyName(final String name) {
        final String propertyName = BindingHelper.mangleNameToVariableName(name);
        return getJavaReservedVarialbeName(propertyName);
    }
    
    @NotNull
    private static String getJavaReservedVarialbeName(@NotNull final String name) {
        final String reservedName = AbstractWrapperBeanGenerator.reservedWords.get(name);
        return (reservedName == null) ? name : reservedName;
    }
    
    static {
        LOGGER = Logger.getLogger(AbstractWrapperBeanGenerator.class.getName());
        jaxbAnns = new Class[] { XmlAttachmentRef.class, XmlMimeType.class, XmlJavaTypeAdapter.class, XmlList.class, XmlElement.class };
        (skipProperties = new HashSet<String>()).add("getCause");
        AbstractWrapperBeanGenerator.skipProperties.add("getLocalizedMessage");
        AbstractWrapperBeanGenerator.skipProperties.add("getClass");
        AbstractWrapperBeanGenerator.skipProperties.add("getStackTrace");
        AbstractWrapperBeanGenerator.skipProperties.add("getSuppressed");
        (reservedWords = new HashMap<String, String>()).put("abstract", "_abstract");
        AbstractWrapperBeanGenerator.reservedWords.put("assert", "_assert");
        AbstractWrapperBeanGenerator.reservedWords.put("boolean", "_boolean");
        AbstractWrapperBeanGenerator.reservedWords.put("break", "_break");
        AbstractWrapperBeanGenerator.reservedWords.put("byte", "_byte");
        AbstractWrapperBeanGenerator.reservedWords.put("case", "_case");
        AbstractWrapperBeanGenerator.reservedWords.put("catch", "_catch");
        AbstractWrapperBeanGenerator.reservedWords.put("char", "_char");
        AbstractWrapperBeanGenerator.reservedWords.put("class", "_class");
        AbstractWrapperBeanGenerator.reservedWords.put("const", "_const");
        AbstractWrapperBeanGenerator.reservedWords.put("continue", "_continue");
        AbstractWrapperBeanGenerator.reservedWords.put("default", "_default");
        AbstractWrapperBeanGenerator.reservedWords.put("do", "_do");
        AbstractWrapperBeanGenerator.reservedWords.put("double", "_double");
        AbstractWrapperBeanGenerator.reservedWords.put("else", "_else");
        AbstractWrapperBeanGenerator.reservedWords.put("extends", "_extends");
        AbstractWrapperBeanGenerator.reservedWords.put("false", "_false");
        AbstractWrapperBeanGenerator.reservedWords.put("final", "_final");
        AbstractWrapperBeanGenerator.reservedWords.put("finally", "_finally");
        AbstractWrapperBeanGenerator.reservedWords.put("float", "_float");
        AbstractWrapperBeanGenerator.reservedWords.put("for", "_for");
        AbstractWrapperBeanGenerator.reservedWords.put("goto", "_goto");
        AbstractWrapperBeanGenerator.reservedWords.put("if", "_if");
        AbstractWrapperBeanGenerator.reservedWords.put("implements", "_implements");
        AbstractWrapperBeanGenerator.reservedWords.put("import", "_import");
        AbstractWrapperBeanGenerator.reservedWords.put("instanceof", "_instanceof");
        AbstractWrapperBeanGenerator.reservedWords.put("int", "_int");
        AbstractWrapperBeanGenerator.reservedWords.put("interface", "_interface");
        AbstractWrapperBeanGenerator.reservedWords.put("long", "_long");
        AbstractWrapperBeanGenerator.reservedWords.put("native", "_native");
        AbstractWrapperBeanGenerator.reservedWords.put("new", "_new");
        AbstractWrapperBeanGenerator.reservedWords.put("null", "_null");
        AbstractWrapperBeanGenerator.reservedWords.put("package", "_package");
        AbstractWrapperBeanGenerator.reservedWords.put("private", "_private");
        AbstractWrapperBeanGenerator.reservedWords.put("protected", "_protected");
        AbstractWrapperBeanGenerator.reservedWords.put("public", "_public");
        AbstractWrapperBeanGenerator.reservedWords.put("return", "_return");
        AbstractWrapperBeanGenerator.reservedWords.put("short", "_short");
        AbstractWrapperBeanGenerator.reservedWords.put("static", "_static");
        AbstractWrapperBeanGenerator.reservedWords.put("strictfp", "_strictfp");
        AbstractWrapperBeanGenerator.reservedWords.put("super", "_super");
        AbstractWrapperBeanGenerator.reservedWords.put("switch", "_switch");
        AbstractWrapperBeanGenerator.reservedWords.put("synchronized", "_synchronized");
        AbstractWrapperBeanGenerator.reservedWords.put("this", "_this");
        AbstractWrapperBeanGenerator.reservedWords.put("throw", "_throw");
        AbstractWrapperBeanGenerator.reservedWords.put("throws", "_throws");
        AbstractWrapperBeanGenerator.reservedWords.put("transient", "_transient");
        AbstractWrapperBeanGenerator.reservedWords.put("true", "_true");
        AbstractWrapperBeanGenerator.reservedWords.put("try", "_try");
        AbstractWrapperBeanGenerator.reservedWords.put("void", "_void");
        AbstractWrapperBeanGenerator.reservedWords.put("volatile", "_volatile");
        AbstractWrapperBeanGenerator.reservedWords.put("while", "_while");
        AbstractWrapperBeanGenerator.reservedWords.put("enum", "_enum");
    }
    
    private static class XmlElementHandler implements InvocationHandler
    {
        private String name;
        private String namespace;
        private boolean nillable;
        private boolean required;
        
        XmlElementHandler(final String name, final String namespace, final boolean nillable, final boolean required) {
            this.name = name;
            this.namespace = namespace;
            this.nillable = nillable;
            this.required = required;
        }
        
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            final String methodName = method.getName();
            if (methodName.equals("name")) {
                return this.name;
            }
            if (methodName.equals("namespace")) {
                return this.namespace;
            }
            if (methodName.equals("nillable")) {
                return this.nillable;
            }
            if (methodName.equals("required")) {
                return this.required;
            }
            throw new WebServiceException("Not handling " + methodName);
        }
    }
    
    public interface BeanMemberFactory<T, A>
    {
        A createWrapperBeanMember(final T p0, final String p1, final List<Annotation> p2);
    }
}
