package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.LocalObject;
import com.sun.corba.se.spi.ior.IOR;
import org.omg.CORBA.Object;
import com.sun.corba.se.spi.orb.ORB;

public class ForwardException extends RuntimeException
{
    private ORB orb;
    private org.omg.CORBA.Object obj;
    private IOR ior;
    
    public ForwardException(final ORB orb, final IOR ior) {
        this.orb = orb;
        this.obj = null;
        this.ior = ior;
    }
    
    public ForwardException(final ORB orb, final org.omg.CORBA.Object obj) {
        if (obj instanceof LocalObject) {
            throw new BAD_PARAM();
        }
        this.orb = orb;
        this.obj = obj;
        this.ior = null;
    }
    
    public synchronized org.omg.CORBA.Object getObject() {
        if (this.obj == null) {
            this.obj = ORBUtility.makeObjectReference(this.ior);
        }
        return this.obj;
    }
    
    public synchronized IOR getIOR() {
        if (this.ior == null) {
            this.ior = ORBUtility.getIOR(this.obj);
        }
        return this.ior;
    }
}
