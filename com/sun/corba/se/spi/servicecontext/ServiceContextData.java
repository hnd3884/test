package com.sun.corba.se.spi.servicecontext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.BAD_PARAM;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import java.lang.reflect.Constructor;

public class ServiceContextData
{
    private Class scClass;
    private Constructor scConstructor;
    private int scId;
    
    private void dprint(final String s) {
        ORBUtility.dprint(this, s);
    }
    
    private void throwBadParam(final String s, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(s);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        throw bad_PARAM;
    }
    
    public ServiceContextData(final Class scClass) {
        if (ORB.ORBInitDebug) {
            this.dprint("ServiceContextData constructor called for class " + scClass);
        }
        this.scClass = scClass;
        try {
            if (ORB.ORBInitDebug) {
                this.dprint("Finding constructor for " + scClass);
            }
            final Class[] array = { InputStream.class, GIOPVersion.class };
            try {
                this.scConstructor = scClass.getConstructor((Class[])array);
            }
            catch (final NoSuchMethodException ex) {
                this.throwBadParam("Class does not have an InputStream constructor", ex);
            }
            if (ORB.ORBInitDebug) {
                this.dprint("Finding SERVICE_CONTEXT_ID field in " + scClass);
            }
            Field field = null;
            try {
                field = scClass.getField("SERVICE_CONTEXT_ID");
            }
            catch (final NoSuchFieldException ex2) {
                this.throwBadParam("Class does not have a SERVICE_CONTEXT_ID member", ex2);
            }
            catch (final SecurityException ex3) {
                this.throwBadParam("Could not access SERVICE_CONTEXT_ID member", ex3);
            }
            if (ORB.ORBInitDebug) {
                this.dprint("Checking modifiers of SERVICE_CONTEXT_ID field in " + scClass);
            }
            final int modifiers = field.getModifiers();
            if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers)) {
                this.throwBadParam("SERVICE_CONTEXT_ID field is not public static final", null);
            }
            if (ORB.ORBInitDebug) {
                this.dprint("Getting value of SERVICE_CONTEXT_ID in " + scClass);
            }
            try {
                this.scId = field.getInt(null);
            }
            catch (final IllegalArgumentException ex4) {
                this.throwBadParam("SERVICE_CONTEXT_ID not convertible to int", ex4);
            }
            catch (final IllegalAccessException ex5) {
                this.throwBadParam("Could not access value of SERVICE_CONTEXT_ID", ex5);
            }
        }
        catch (final BAD_PARAM bad_PARAM) {
            if (ORB.ORBInitDebug) {
                this.dprint("Exception in ServiceContextData constructor: " + bad_PARAM);
            }
            throw bad_PARAM;
        }
        catch (final Throwable t) {
            if (ORB.ORBInitDebug) {
                this.dprint("Unexpected Exception in ServiceContextData constructor: " + t);
            }
        }
        if (ORB.ORBInitDebug) {
            this.dprint("ServiceContextData constructor completed");
        }
    }
    
    public ServiceContext makeServiceContext(final InputStream inputStream, final GIOPVersion giopVersion) {
        final Object[] array = { inputStream, giopVersion };
        ServiceContext serviceContext = null;
        try {
            serviceContext = this.scConstructor.newInstance(array);
        }
        catch (final IllegalArgumentException ex) {
            this.throwBadParam("InputStream constructor argument error", ex);
        }
        catch (final IllegalAccessException ex2) {
            this.throwBadParam("InputStream constructor argument error", ex2);
        }
        catch (final InstantiationException ex3) {
            this.throwBadParam("InputStream constructor called for abstract class", ex3);
        }
        catch (final InvocationTargetException ex4) {
            this.throwBadParam("InputStream constructor threw exception " + ex4.getTargetException(), ex4);
        }
        return serviceContext;
    }
    
    int getId() {
        return this.scId;
    }
    
    @Override
    public String toString() {
        return "ServiceContextData[ scClass=" + this.scClass + " scConstructor=" + this.scConstructor + " scId=" + this.scId + " ]";
    }
}
