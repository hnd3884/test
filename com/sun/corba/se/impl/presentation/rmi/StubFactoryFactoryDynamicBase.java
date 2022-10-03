package com.sun.corba.se.impl.presentation.rmi;

import javax.rmi.CORBA.Tie;
import java.rmi.Remote;
import org.omg.CORBA.portable.IDLEntity;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.CompletionStatus;
import javax.rmi.CORBA.Util;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;

public abstract class StubFactoryFactoryDynamicBase extends StubFactoryFactoryBase
{
    protected final ORBUtilSystemException wrapper;
    
    public StubFactoryFactoryDynamicBase() {
        this.wrapper = ORBUtilSystemException.get("rpc.presentation");
    }
    
    @Override
    public PresentationManager.StubFactory createStubFactory(final String s, final boolean b, final String s2, final Class clazz, final ClassLoader classLoader) {
        Class loadClass;
        try {
            loadClass = Util.loadClass(s, s2, classLoader);
        }
        catch (final ClassNotFoundException ex) {
            throw this.wrapper.classNotFound3(CompletionStatus.COMPLETED_MAYBE, ex, s);
        }
        final PresentationManager presentationManager = ORB.getPresentationManager();
        if (IDLEntity.class.isAssignableFrom(loadClass) && !Remote.class.isAssignableFrom(loadClass)) {
            return presentationManager.getStubFactoryFactory(false).createStubFactory(s, true, s2, clazz, classLoader);
        }
        return this.makeDynamicStubFactory(presentationManager, presentationManager.getClassData(loadClass), classLoader);
    }
    
    public abstract PresentationManager.StubFactory makeDynamicStubFactory(final PresentationManager p0, final PresentationManager.ClassData p1, final ClassLoader p2);
    
    @Override
    public Tie getTie(final Class clazz) {
        return new ReflectiveTie(ORB.getPresentationManager(), this.wrapper);
    }
    
    @Override
    public boolean createsDynamicStubs() {
        return true;
    }
}
