package com.sun.xml.internal.ws.server;

import javax.management.ObjectName;
import java.io.IOException;
import com.sun.org.glassfish.gmbal.InheritedAttributes;
import com.sun.org.glassfish.gmbal.Description;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import com.sun.org.glassfish.gmbal.ManagedData;
import javax.xml.ws.WebServiceFeature;
import com.sun.org.glassfish.external.amx.AMXGlassfish;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedClientAssertion;
import com.sun.org.glassfish.gmbal.ManagedObjectManagerFactory;
import com.sun.xml.internal.ws.client.Stub;
import java.lang.reflect.Method;
import com.sun.xml.internal.ws.api.server.Container;
import java.util.logging.Level;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedServiceAssertion;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion;
import java.util.logging.Logger;

public abstract class MonitorBase
{
    private static final Logger logger;
    private static ManagementAssertion.Setting clientMonitoring;
    private static ManagementAssertion.Setting endpointMonitoring;
    private static int typelibDebug;
    private static String registrationDebug;
    private static boolean runtimeDebug;
    private static int maxUniqueEndpointRootNameRetries;
    private static final String monitorProperty = "com.sun.xml.internal.ws.monitoring.";
    
    @NotNull
    public ManagedObjectManager createManagedObjectManager(final WSEndpoint endpoint) {
        String rootName = endpoint.getServiceName().getLocalPart() + "-" + endpoint.getPortName().getLocalPart();
        if (rootName.equals("-")) {
            rootName = "provider";
        }
        final String contextPath = this.getContextPath(endpoint);
        if (contextPath != null) {
            rootName = contextPath + "-" + rootName;
        }
        final ManagedServiceAssertion assertion = ManagedServiceAssertion.getAssertion(endpoint);
        if (assertion != null) {
            final String id = assertion.getId();
            if (id != null) {
                rootName = id;
            }
            if (assertion.monitoringAttribute() == ManagementAssertion.Setting.OFF) {
                return this.disabled("This endpoint", rootName);
            }
        }
        if (MonitorBase.endpointMonitoring.equals(ManagementAssertion.Setting.OFF)) {
            return this.disabled("Global endpoint", rootName);
        }
        return this.createMOMLoop(rootName, 0);
    }
    
    private String getContextPath(final WSEndpoint endpoint) {
        try {
            final Container container = endpoint.getContainer();
            final Method getSPI = container.getClass().getDeclaredMethod("getSPI", Class.class);
            getSPI.setAccessible(true);
            final Class servletContextClass = Class.forName("javax.servlet.ServletContext");
            final Object servletContext = getSPI.invoke(container, servletContextClass);
            if (servletContext != null) {
                final Method getContextPath = servletContextClass.getDeclaredMethod("getContextPath", (Class[])new Class[0]);
                getContextPath.setAccessible(true);
                return (String)getContextPath.invoke(servletContext, new Object[0]);
            }
            return null;
        }
        catch (final Throwable t) {
            MonitorBase.logger.log(Level.FINEST, "getContextPath", t);
            return null;
        }
    }
    
    @NotNull
    public ManagedObjectManager createManagedObjectManager(final Stub stub) {
        final EndpointAddress ea = stub.requestContext.getEndpointAddress();
        if (ea == null) {
            return ManagedObjectManagerFactory.createNOOP();
        }
        String rootName = ea.toString();
        final ManagedClientAssertion assertion = ManagedClientAssertion.getAssertion(stub.getPortInfo());
        if (assertion != null) {
            final String id = assertion.getId();
            if (id != null) {
                rootName = id;
            }
            if (assertion.monitoringAttribute() == ManagementAssertion.Setting.OFF) {
                return this.disabled("This client", rootName);
            }
            if (assertion.monitoringAttribute() == ManagementAssertion.Setting.ON && MonitorBase.clientMonitoring != ManagementAssertion.Setting.OFF) {
                return this.createMOMLoop(rootName, 0);
            }
        }
        if (MonitorBase.clientMonitoring == ManagementAssertion.Setting.NOT_SET || MonitorBase.clientMonitoring == ManagementAssertion.Setting.OFF) {
            return this.disabled("Global client", rootName);
        }
        return this.createMOMLoop(rootName, 0);
    }
    
    @NotNull
    private ManagedObjectManager disabled(final String x, final String rootName) {
        final String msg = x + " monitoring disabled. " + rootName + " will not be monitored";
        MonitorBase.logger.log(Level.CONFIG, msg);
        return ManagedObjectManagerFactory.createNOOP();
    }
    
    @NotNull
    private ManagedObjectManager createMOMLoop(final String rootName, final int unique) {
        final boolean isFederated = AMXGlassfish.getGlassfishVersion() != null;
        ManagedObjectManager mom = this.createMOM(isFederated);
        mom = this.initMOM(mom);
        mom = this.createRoot(mom, rootName, unique);
        return mom;
    }
    
    @NotNull
    private ManagedObjectManager createMOM(final boolean isFederated) {
        try {
            return new RewritingMOM(isFederated ? ManagedObjectManagerFactory.createFederated(AMXGlassfish.DEFAULT.serverMon(AMXGlassfish.DEFAULT.dasName())) : ManagedObjectManagerFactory.createStandalone("com.sun.metro"));
        }
        catch (final Throwable t) {
            if (isFederated) {
                MonitorBase.logger.log(Level.CONFIG, "Problem while attempting to federate with GlassFish AMX monitoring.  Trying standalone.", t);
                return this.createMOM(false);
            }
            MonitorBase.logger.log(Level.WARNING, "Ignoring exception - starting up without monitoring", t);
            return ManagedObjectManagerFactory.createNOOP();
        }
    }
    
    @NotNull
    private ManagedObjectManager initMOM(final ManagedObjectManager mom) {
        try {
            if (MonitorBase.typelibDebug != -1) {
                mom.setTypelibDebug(MonitorBase.typelibDebug);
            }
            if (MonitorBase.registrationDebug.equals("FINE")) {
                mom.setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel.FINE);
            }
            else if (MonitorBase.registrationDebug.equals("NORMAL")) {
                mom.setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel.NORMAL);
            }
            else {
                mom.setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel.NONE);
            }
            mom.setRuntimeDebug(MonitorBase.runtimeDebug);
            mom.suppressDuplicateRootReport(true);
            mom.stripPrefix("com.sun.xml.internal.ws.server", "com.sun.xml.internal.ws.rx.rm.runtime.sequence");
            mom.addAnnotation(WebServiceFeature.class, DummyWebServiceFeature.class.getAnnotation(ManagedData.class));
            mom.addAnnotation(WebServiceFeature.class, DummyWebServiceFeature.class.getAnnotation(Description.class));
            mom.addAnnotation(WebServiceFeature.class, DummyWebServiceFeature.class.getAnnotation(InheritedAttributes.class));
            mom.suspendJMXRegistration();
        }
        catch (final Throwable t) {
            try {
                mom.close();
            }
            catch (final IOException e) {
                MonitorBase.logger.log(Level.CONFIG, "Ignoring exception caught when closing unused ManagedObjectManager", e);
            }
            MonitorBase.logger.log(Level.WARNING, "Ignoring exception - starting up without monitoring", t);
            return ManagedObjectManagerFactory.createNOOP();
        }
        return mom;
    }
    
    private ManagedObjectManager createRoot(final ManagedObjectManager mom, final String rootName, int unique) {
        final String name = rootName + ((unique == 0) ? "" : ("-" + String.valueOf(unique)));
        try {
            final Object ignored = mom.createRoot(this, name);
            if (ignored != null) {
                final ObjectName ignoredName = mom.getObjectName(mom.getRoot());
                if (ignoredName != null) {
                    MonitorBase.logger.log(Level.INFO, "Metro monitoring rootname successfully set to: {0}", ignoredName);
                }
                return mom;
            }
            try {
                mom.close();
            }
            catch (final IOException e) {
                MonitorBase.logger.log(Level.CONFIG, "Ignoring exception caught when closing unused ManagedObjectManager", e);
            }
            final String basemsg = "Duplicate Metro monitoring rootname: " + name + " : ";
            if (unique > MonitorBase.maxUniqueEndpointRootNameRetries) {
                final String msg = basemsg + "Giving up.";
                MonitorBase.logger.log(Level.INFO, msg);
                return ManagedObjectManagerFactory.createNOOP();
            }
            final String msg = basemsg + "Will try to make unique";
            MonitorBase.logger.log(Level.CONFIG, msg);
            return this.createMOMLoop(rootName, ++unique);
        }
        catch (final Throwable t) {
            MonitorBase.logger.log(Level.WARNING, "Error while creating monitoring root with name: " + rootName, t);
            return ManagedObjectManagerFactory.createNOOP();
        }
    }
    
    private static ManagementAssertion.Setting propertyToSetting(final String propName) {
        String s = System.getProperty(propName);
        if (s == null) {
            return ManagementAssertion.Setting.NOT_SET;
        }
        s = s.toLowerCase();
        if (s.equals("false") || s.equals("off")) {
            return ManagementAssertion.Setting.OFF;
        }
        if (s.equals("true") || s.equals("on")) {
            return ManagementAssertion.Setting.ON;
        }
        return ManagementAssertion.Setting.NOT_SET;
    }
    
    static {
        logger = Logger.getLogger("com.sun.xml.internal.ws.monitoring");
        MonitorBase.clientMonitoring = ManagementAssertion.Setting.NOT_SET;
        MonitorBase.endpointMonitoring = ManagementAssertion.Setting.NOT_SET;
        MonitorBase.typelibDebug = -1;
        MonitorBase.registrationDebug = "NONE";
        MonitorBase.runtimeDebug = false;
        MonitorBase.maxUniqueEndpointRootNameRetries = 100;
        try {
            MonitorBase.endpointMonitoring = propertyToSetting("com.sun.xml.internal.ws.monitoring.endpoint");
            MonitorBase.clientMonitoring = propertyToSetting("com.sun.xml.internal.ws.monitoring.client");
            Integer i = Integer.getInteger("com.sun.xml.internal.ws.monitoring.typelibDebug");
            if (i != null) {
                MonitorBase.typelibDebug = i;
            }
            String s = System.getProperty("com.sun.xml.internal.ws.monitoring.registrationDebug");
            if (s != null) {
                MonitorBase.registrationDebug = s.toUpperCase();
            }
            s = System.getProperty("com.sun.xml.internal.ws.monitoring.runtimeDebug");
            if (s != null && s.toLowerCase().equals("true")) {
                MonitorBase.runtimeDebug = true;
            }
            i = Integer.getInteger("com.sun.xml.internal.ws.monitoring.maxUniqueEndpointRootNameRetries");
            if (i != null) {
                MonitorBase.maxUniqueEndpointRootNameRetries = i;
            }
        }
        catch (final Exception e) {
            MonitorBase.logger.log(Level.WARNING, "Error while reading monitoring properties", e);
        }
    }
}
