package org.apache.axiom.om;

import org.apache.axiom.soap.SOAPFactory;

public class OMAbstractFactory
{
    public static final String META_FACTORY_NAME_PROPERTY = "org.apache.axiom.om.OMMetaFactory";
    public static final String FEATURE_DEFAULT = "default";
    public static final String FEATURE_DOM = "dom";
    private static final String DEFAULT_LOCATOR_CLASS_NAME = "org.apache.axiom.locator.DefaultOMMetaFactoryLocator";
    private static final OMMetaFactoryLocator defaultMetaFactoryLocator;
    private static volatile OMMetaFactoryLocator metaFactoryLocator;
    
    private OMAbstractFactory() {
    }
    
    public static void setMetaFactoryLocator(final OMMetaFactoryLocator locator) {
        OMAbstractFactory.metaFactoryLocator = locator;
    }
    
    public static OMMetaFactory getMetaFactory() {
        return getMetaFactory("default");
    }
    
    public static OMMetaFactory getMetaFactory(final String feature) {
        OMMetaFactoryLocator locator = OMAbstractFactory.metaFactoryLocator;
        if (locator == null) {
            locator = OMAbstractFactory.defaultMetaFactoryLocator;
        }
        final OMMetaFactory metaFactory = locator.getOMMetaFactory(feature);
        if (metaFactory == null) {
            String jarHint;
            if (feature.equals("default")) {
                jarHint = "axiom-impl.jar";
            }
            else if (feature.equals("dom")) {
                jarHint = "axiom-dom.jar";
            }
            else {
                jarHint = null;
            }
            final StringBuilder buffer = new StringBuilder();
            buffer.append("No meta factory found for feature '").append(feature).append("'");
            if (jarHint != null) {
                buffer.append("; this usually means that ").append(jarHint).append(" is not in the classpath or that the META-INF/axiom.xml resource can't be read");
            }
            throw new OMException(buffer.toString());
        }
        return metaFactory;
    }
    
    public static OMFactory getOMFactory() {
        return getMetaFactory().getOMFactory();
    }
    
    public static SOAPFactory getSOAP11Factory() {
        return getMetaFactory().getSOAP11Factory();
    }
    
    public static SOAPFactory getSOAP12Factory() {
        return getMetaFactory().getSOAP12Factory();
    }
    
    static {
        try {
            defaultMetaFactoryLocator = (OMMetaFactoryLocator)Class.forName("org.apache.axiom.locator.DefaultOMMetaFactoryLocator").newInstance();
        }
        catch (final InstantiationException ex) {
            throw new InstantiationError(ex.getMessage());
        }
        catch (final IllegalAccessException ex2) {
            throw new IllegalAccessError(ex2.getMessage());
        }
        catch (final ClassNotFoundException ex3) {
            throw new NoClassDefFoundError(ex3.getMessage());
        }
    }
}
