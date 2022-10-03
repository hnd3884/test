package com.sun.corba.se.spi.copyobject;

import com.sun.corba.se.impl.copyobject.ReferenceObjectCopierImpl;
import com.sun.corba.se.impl.copyobject.FallbackObjectCopierImpl;
import com.sun.corba.se.impl.copyobject.JavaStreamObjectCopierImpl;
import com.sun.corba.se.impl.copyobject.ORBStreamObjectCopierImpl;
import com.sun.corba.se.spi.orb.ORB;

public abstract class CopyobjectDefaults
{
    private static final ObjectCopier referenceObjectCopier;
    private static ObjectCopierFactory referenceObjectCopierFactory;
    
    private CopyobjectDefaults() {
    }
    
    public static ObjectCopierFactory makeORBStreamObjectCopierFactory(final ORB orb) {
        return new ObjectCopierFactory() {
            @Override
            public ObjectCopier make() {
                return new ORBStreamObjectCopierImpl(orb);
            }
        };
    }
    
    public static ObjectCopierFactory makeJavaStreamObjectCopierFactory(final ORB orb) {
        return new ObjectCopierFactory() {
            @Override
            public ObjectCopier make() {
                return new JavaStreamObjectCopierImpl(orb);
            }
        };
    }
    
    public static ObjectCopierFactory getReferenceObjectCopierFactory() {
        return CopyobjectDefaults.referenceObjectCopierFactory;
    }
    
    public static ObjectCopierFactory makeFallbackObjectCopierFactory(final ObjectCopierFactory objectCopierFactory, final ObjectCopierFactory objectCopierFactory2) {
        return new ObjectCopierFactory() {
            @Override
            public ObjectCopier make() {
                return new FallbackObjectCopierImpl(objectCopierFactory.make(), objectCopierFactory2.make());
            }
        };
    }
    
    static {
        referenceObjectCopier = new ReferenceObjectCopierImpl();
        CopyobjectDefaults.referenceObjectCopierFactory = new ObjectCopierFactory() {
            @Override
            public ObjectCopier make() {
                return CopyobjectDefaults.referenceObjectCopier;
            }
        };
    }
}
