package com.sun.xml.internal.ws.binding;

import java.util.NoSuchElementException;
import java.util.Stack;
import com.sun.xml.internal.ws.api.WSBinding;
import com.oracle.webservices.internal.api.EnvelopeStyleFeature;
import com.sun.xml.internal.ws.api.SOAPVersion;
import java.util.Set;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.ImpliesWebServiceFeature;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import com.sun.xml.internal.ws.api.BindingID;
import javax.xml.ws.RespectBindingFeature;
import javax.xml.ws.RespectBinding;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.MTOM;
import com.sun.xml.internal.ws.model.RuntimeModelerException;
import com.sun.xml.internal.ws.resources.ModelerMessages;
import com.sun.xml.internal.bind.util.Which;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;
import java.lang.annotation.Annotation;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.FeatureListValidator;
import com.sun.xml.internal.ws.api.FeatureListValidatorAnnotation;
import java.util.Iterator;
import com.sun.istack.internal.NotNull;
import java.util.HashMap;
import java.util.logging.Logger;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFeaturedObject;
import java.util.Map;
import com.sun.xml.internal.ws.api.WSFeatureList;
import javax.xml.ws.WebServiceFeature;
import java.util.AbstractMap;

public final class WebServiceFeatureList extends AbstractMap<Class<? extends WebServiceFeature>, WebServiceFeature> implements WSFeatureList
{
    private Map<Class<? extends WebServiceFeature>, WebServiceFeature> wsfeatures;
    private boolean isValidating;
    @Nullable
    private WSDLFeaturedObject parent;
    private static final Logger LOGGER;
    
    public static WebServiceFeatureList toList(final Iterable<WebServiceFeature> features) {
        if (features instanceof WebServiceFeatureList) {
            return (WebServiceFeatureList)features;
        }
        final WebServiceFeatureList w = new WebServiceFeatureList();
        if (features != null) {
            w.addAll(features);
        }
        return w;
    }
    
    public WebServiceFeatureList() {
        this.wsfeatures = new HashMap<Class<? extends WebServiceFeature>, WebServiceFeature>();
        this.isValidating = false;
    }
    
    public WebServiceFeatureList(@NotNull final WebServiceFeature... features) {
        this.wsfeatures = new HashMap<Class<? extends WebServiceFeature>, WebServiceFeature>();
        this.isValidating = false;
        if (features != null) {
            for (final WebServiceFeature f : features) {
                this.addNoValidate(f);
            }
        }
    }
    
    public void validate() {
        if (!this.isValidating) {
            this.isValidating = true;
            for (final WebServiceFeature ff : this) {
                this.validate(ff);
            }
        }
    }
    
    private void validate(final WebServiceFeature feature) {
        final FeatureListValidatorAnnotation fva = feature.getClass().getAnnotation(FeatureListValidatorAnnotation.class);
        if (fva != null) {
            final Class<? extends FeatureListValidator> beanClass = fva.bean();
            try {
                final FeatureListValidator validator = (FeatureListValidator)beanClass.newInstance();
                validator.validate(this);
            }
            catch (final InstantiationException e) {
                throw new WebServiceException(e);
            }
            catch (final IllegalAccessException e2) {
                throw new WebServiceException(e2);
            }
        }
    }
    
    public WebServiceFeatureList(final WebServiceFeatureList features) {
        this.wsfeatures = new HashMap<Class<? extends WebServiceFeature>, WebServiceFeature>();
        this.isValidating = false;
        if (features != null) {
            this.wsfeatures.putAll(features.wsfeatures);
            this.parent = features.parent;
            this.isValidating = features.isValidating;
        }
    }
    
    public WebServiceFeatureList(@NotNull final Class<?> endpointClass) {
        this.wsfeatures = new HashMap<Class<? extends WebServiceFeature>, WebServiceFeature>();
        this.isValidating = false;
        this.parseAnnotations(endpointClass);
    }
    
    public void parseAnnotations(final Iterable<Annotation> annIt) {
        for (final Annotation ann : annIt) {
            final WebServiceFeature feature = getFeature(ann);
            if (feature != null) {
                this.add(feature);
            }
        }
    }
    
    public static WebServiceFeature getFeature(final Annotation a) {
        WebServiceFeature ftr = null;
        if (!a.annotationType().isAnnotationPresent(WebServiceFeatureAnnotation.class)) {
            ftr = null;
        }
        else if (a instanceof Addressing) {
            final Addressing addAnn = (Addressing)a;
            try {
                ftr = new AddressingFeature(addAnn.enabled(), addAnn.required(), addAnn.responses());
            }
            catch (final NoSuchMethodError e) {
                throw new RuntimeModelerException(ModelerMessages.RUNTIME_MODELER_ADDRESSING_RESPONSES_NOSUCHMETHOD(toJar(Which.which(Addressing.class))), new Object[0]);
            }
        }
        else if (a instanceof MTOM) {
            final MTOM mtomAnn = (MTOM)a;
            ftr = new MTOMFeature(mtomAnn.enabled(), mtomAnn.threshold());
        }
        else if (a instanceof RespectBinding) {
            final RespectBinding rbAnn = (RespectBinding)a;
            ftr = new RespectBindingFeature(rbAnn.enabled());
        }
        else {
            ftr = getWebServiceFeatureBean(a);
        }
        return ftr;
    }
    
    public void parseAnnotations(final Class<?> endpointClass) {
        for (final Annotation a : endpointClass.getAnnotations()) {
            final WebServiceFeature ftr = getFeature(a);
            if (ftr != null) {
                if (ftr instanceof MTOMFeature) {
                    final BindingID bindingID = BindingID.parse(endpointClass);
                    final MTOMFeature bindingMtomSetting = bindingID.createBuiltinFeatureList().get(MTOMFeature.class);
                    if (bindingMtomSetting != null && (bindingMtomSetting.isEnabled() ^ ftr.isEnabled())) {
                        throw new RuntimeModelerException(ModelerMessages.RUNTIME_MODELER_MTOM_CONFLICT(bindingID, ftr.isEnabled()), new Object[0]);
                    }
                }
                this.add(ftr);
            }
        }
    }
    
    private static String toJar(String url) {
        if (!url.startsWith("jar:")) {
            return url;
        }
        url = url.substring(4);
        return url.substring(0, url.lastIndexOf(33));
    }
    
    private static WebServiceFeature getWebServiceFeatureBean(final Annotation a) {
        final WebServiceFeatureAnnotation wsfa = a.annotationType().getAnnotation(WebServiceFeatureAnnotation.class);
        final Class<? extends WebServiceFeature> beanClass = wsfa.bean();
        Constructor ftrCtr = null;
        String[] paramNames = null;
        for (final Constructor con : beanClass.getConstructors()) {
            final FeatureConstructor ftrCtrAnn = con.getAnnotation(FeatureConstructor.class);
            if (ftrCtrAnn != null) {
                if (ftrCtr != null) {
                    throw new WebServiceException(ModelerMessages.RUNTIME_MODELER_WSFEATURE_MORETHANONE_FTRCONSTRUCTOR(a, beanClass));
                }
                ftrCtr = con;
                paramNames = ftrCtrAnn.value();
            }
        }
        if (ftrCtr == null) {
            final WebServiceFeature bean = getWebServiceFeatureBeanViaBuilder(a, beanClass);
            if (bean != null) {
                return bean;
            }
            throw new WebServiceException(ModelerMessages.RUNTIME_MODELER_WSFEATURE_NO_FTRCONSTRUCTOR(a, beanClass));
        }
        else {
            if (ftrCtr.getParameterTypes().length != paramNames.length) {
                throw new WebServiceException(ModelerMessages.RUNTIME_MODELER_WSFEATURE_ILLEGAL_FTRCONSTRUCTOR(a, beanClass));
            }
            WebServiceFeature bean;
            try {
                final Object[] params = new Object[paramNames.length];
                for (int i = 0; i < paramNames.length; ++i) {
                    final Method m = a.annotationType().getDeclaredMethod(paramNames[i], (Class<?>[])new Class[0]);
                    params[i] = m.invoke(a, new Object[0]);
                }
                bean = ftrCtr.newInstance(params);
            }
            catch (final Exception e) {
                throw new WebServiceException(e);
            }
            return bean;
        }
    }
    
    private static WebServiceFeature getWebServiceFeatureBeanViaBuilder(final Annotation annotation, final Class<? extends WebServiceFeature> beanClass) {
        try {
            final Method featureBuilderMethod = beanClass.getDeclaredMethod("builder", (Class<?>[])new Class[0]);
            final Object builder = featureBuilderMethod.invoke(beanClass, new Object[0]);
            final Method buildMethod = builder.getClass().getDeclaredMethod("build", (Class<?>[])new Class[0]);
            for (final Method builderMethod : builder.getClass().getDeclaredMethods()) {
                if (!builderMethod.equals(buildMethod)) {
                    final String methodName = builderMethod.getName();
                    final Method annotationMethod = annotation.annotationType().getDeclaredMethod(methodName, (Class<?>[])new Class[0]);
                    final Object annotationFieldValue = annotationMethod.invoke(annotation, new Object[0]);
                    final Object[] arg = { annotationFieldValue };
                    if (!skipDuringOrgJvnetWsToComOracleWebservicesPackageMove(builderMethod, annotationFieldValue)) {
                        builderMethod.invoke(builder, arg);
                    }
                }
            }
            final Object result = buildMethod.invoke(builder, new Object[0]);
            if (result instanceof WebServiceFeature) {
                return (WebServiceFeature)result;
            }
            throw new WebServiceException("Not a WebServiceFeature: " + result);
        }
        catch (final NoSuchMethodException e) {
            return null;
        }
        catch (final IllegalAccessException e2) {
            throw new WebServiceException(e2);
        }
        catch (final InvocationTargetException e3) {
            throw new WebServiceException(e3);
        }
    }
    
    private static boolean skipDuringOrgJvnetWsToComOracleWebservicesPackageMove(final Method builderMethod, final Object annotationFieldValue) {
        final Class<?> annotationFieldValueClass = annotationFieldValue.getClass();
        if (!annotationFieldValueClass.isEnum()) {
            return false;
        }
        final Class<?>[] builderMethodParameterTypes = builderMethod.getParameterTypes();
        if (builderMethodParameterTypes.length != 1) {
            throw new WebServiceException("expected only 1 parameter");
        }
        final String builderParameterTypeName = builderMethodParameterTypes[0].getName();
        return !builderParameterTypeName.startsWith("com.oracle.webservices.internal.test.features_annotations_enums.apinew") && !builderParameterTypeName.startsWith("com.oracle.webservices.internal.api") && false;
    }
    
    @Override
    public Iterator<WebServiceFeature> iterator() {
        if (this.parent != null) {
            return new MergedFeatures(this.parent.getFeatures());
        }
        return this.wsfeatures.values().iterator();
    }
    
    @NotNull
    @Override
    public WebServiceFeature[] toArray() {
        if (this.parent != null) {
            return new MergedFeatures(this.parent.getFeatures()).toArray();
        }
        return this.wsfeatures.values().toArray(new WebServiceFeature[0]);
    }
    
    @Override
    public boolean isEnabled(@NotNull final Class<? extends WebServiceFeature> feature) {
        final WebServiceFeature ftr = this.get(feature);
        return ftr != null && ftr.isEnabled();
    }
    
    public boolean contains(@NotNull final Class<? extends WebServiceFeature> feature) {
        final WebServiceFeature ftr = this.get(feature);
        return ftr != null;
    }
    
    @Nullable
    @Override
    public <F extends WebServiceFeature> F get(@NotNull final Class<F> featureType) {
        final WebServiceFeature f = featureType.cast(this.wsfeatures.get(featureType));
        if (f == null && this.parent != null) {
            return this.parent.getFeatures().get(featureType);
        }
        return (F)f;
    }
    
    public void add(@NotNull final WebServiceFeature f) {
        if (this.addNoValidate(f) && this.isValidating) {
            this.validate(f);
        }
    }
    
    private boolean addNoValidate(@NotNull final WebServiceFeature f) {
        if (!this.wsfeatures.containsKey(f.getClass())) {
            this.wsfeatures.put(f.getClass(), f);
            if (f instanceof ImpliesWebServiceFeature) {
                ((ImpliesWebServiceFeature)f).implyFeatures(this);
            }
            return true;
        }
        return false;
    }
    
    public void addAll(@NotNull final Iterable<WebServiceFeature> list) {
        for (final WebServiceFeature f : list) {
            this.add(f);
        }
    }
    
    void setMTOMEnabled(final boolean b) {
        this.wsfeatures.put(MTOMFeature.class, new MTOMFeature(b));
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof WebServiceFeatureList)) {
            return false;
        }
        final WebServiceFeatureList w = (WebServiceFeatureList)other;
        return this.wsfeatures.equals(w.wsfeatures) && this.parent == w.parent;
    }
    
    @Override
    public String toString() {
        return this.wsfeatures.toString();
    }
    
    @Override
    public void mergeFeatures(@NotNull final Iterable<WebServiceFeature> features, final boolean reportConflicts) {
        for (final WebServiceFeature wsdlFtr : features) {
            if (this.get(wsdlFtr.getClass()) == null) {
                this.add(wsdlFtr);
            }
            else {
                if (!reportConflicts || this.isEnabled(wsdlFtr.getClass()) == wsdlFtr.isEnabled()) {
                    continue;
                }
                WebServiceFeatureList.LOGGER.warning(ModelerMessages.RUNTIME_MODELER_FEATURE_CONFLICT(this.get((Class<Object>)wsdlFtr.getClass()), wsdlFtr));
            }
        }
    }
    
    @Override
    public void mergeFeatures(final WebServiceFeature[] features, final boolean reportConflicts) {
        for (final WebServiceFeature wsdlFtr : features) {
            if (this.get(wsdlFtr.getClass()) == null) {
                this.add(wsdlFtr);
            }
            else if (reportConflicts && this.isEnabled(wsdlFtr.getClass()) != wsdlFtr.isEnabled()) {
                WebServiceFeatureList.LOGGER.warning(ModelerMessages.RUNTIME_MODELER_FEATURE_CONFLICT(this.get((Class<Object>)wsdlFtr.getClass()), wsdlFtr));
            }
        }
    }
    
    public void mergeFeatures(@NotNull final WSDLPort wsdlPort, final boolean honorWsdlRequired, final boolean reportConflicts) {
        if (honorWsdlRequired && !this.isEnabled(RespectBindingFeature.class)) {
            return;
        }
        if (!honorWsdlRequired) {
            this.addAll(wsdlPort.getFeatures());
            return;
        }
        for (final WebServiceFeature wsdlFtr : wsdlPort.getFeatures()) {
            if (this.get(wsdlFtr.getClass()) == null) {
                try {
                    final Method m = wsdlFtr.getClass().getMethod("isRequired", (Class<?>[])new Class[0]);
                    try {
                        final boolean required = (boolean)m.invoke(wsdlFtr, new Object[0]);
                        if (!required) {
                            continue;
                        }
                        this.add(wsdlFtr);
                    }
                    catch (final IllegalAccessException e) {
                        throw new WebServiceException(e);
                    }
                    catch (final InvocationTargetException e2) {
                        throw new WebServiceException(e2);
                    }
                }
                catch (final NoSuchMethodException e3) {
                    this.add(wsdlFtr);
                }
            }
            else {
                if (!reportConflicts || this.isEnabled(wsdlFtr.getClass()) == wsdlFtr.isEnabled()) {
                    continue;
                }
                WebServiceFeatureList.LOGGER.warning(ModelerMessages.RUNTIME_MODELER_FEATURE_CONFLICT(this.get((Class<Object>)wsdlFtr.getClass()), wsdlFtr));
            }
        }
    }
    
    public void setParentFeaturedObject(@NotNull final WSDLFeaturedObject parent) {
        this.parent = parent;
    }
    
    @Nullable
    public static <F extends WebServiceFeature> F getFeature(@NotNull final WebServiceFeature[] features, @NotNull final Class<F> featureType) {
        for (final WebServiceFeature f : features) {
            if (f.getClass() == featureType) {
                return (F)f;
            }
        }
        return null;
    }
    
    @Override
    public Set<Map.Entry<Class<? extends WebServiceFeature>, WebServiceFeature>> entrySet() {
        return this.wsfeatures.entrySet();
    }
    
    @Override
    public WebServiceFeature put(final Class<? extends WebServiceFeature> key, final WebServiceFeature value) {
        return this.wsfeatures.put(key, value);
    }
    
    public static SOAPVersion getSoapVersion(final WSFeatureList features) {
        EnvelopeStyleFeature env = features.get(EnvelopeStyleFeature.class);
        if (env != null) {
            return SOAPVersion.from(env);
        }
        env = features.get(EnvelopeStyleFeature.class);
        return (env != null) ? SOAPVersion.from(env) : null;
    }
    
    public static boolean isFeatureEnabled(final Class<? extends WebServiceFeature> type, final WebServiceFeature[] features) {
        final WebServiceFeature ftr = getFeature(features, type);
        return ftr != null && ftr.isEnabled();
    }
    
    public static WebServiceFeature[] toFeatureArray(final WSBinding binding) {
        if (!binding.isFeatureEnabled(EnvelopeStyleFeature.class)) {
            final WebServiceFeature[] f = { binding.getSOAPVersion().toFeature() };
            binding.getFeatures().mergeFeatures(f, false);
        }
        return binding.getFeatures().toArray();
    }
    
    static {
        LOGGER = Logger.getLogger(WebServiceFeatureList.class.getName());
    }
    
    private final class MergedFeatures implements Iterator<WebServiceFeature>
    {
        private final Stack<WebServiceFeature> features;
        
        public MergedFeatures(final WSFeatureList parent) {
            this.features = new Stack<WebServiceFeature>();
            for (final WebServiceFeature f : WebServiceFeatureList.this.wsfeatures.values()) {
                this.features.push(f);
            }
            for (final WebServiceFeature f : parent) {
                if (!WebServiceFeatureList.this.wsfeatures.containsKey(f.getClass())) {
                    this.features.push(f);
                }
            }
        }
        
        @Override
        public boolean hasNext() {
            return !this.features.empty();
        }
        
        @Override
        public WebServiceFeature next() {
            if (!this.features.empty()) {
                return this.features.pop();
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public void remove() {
            if (!this.features.empty()) {
                this.features.pop();
            }
        }
        
        public WebServiceFeature[] toArray() {
            return this.features.toArray(new WebServiceFeature[0]);
        }
    }
}
