package com.sun.corba.se.impl.protocol;

import javax.rmi.CORBA.Tie;
import org.omg.CORBA.Object;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.portable.ServantObject;

public class JIDLLocalCRDImpl extends LocalClientRequestDispatcherBase
{
    protected ServantObject servant;
    
    public JIDLLocalCRDImpl(final ORB orb, final int n, final IOR ior) {
        super(orb, n, ior);
    }
    
    @Override
    public ServantObject servant_preinvoke(final org.omg.CORBA.Object object, final String s, final Class clazz) {
        if (!this.checkForCompatibleServant(this.servant, clazz)) {
            return null;
        }
        return this.servant;
    }
    
    @Override
    public void servant_postinvoke(final org.omg.CORBA.Object object, final ServantObject servantObject) {
    }
    
    public void setServant(final Object o) {
        if (o != null && o instanceof Tie) {
            this.servant = new ServantObject();
            this.servant.servant = ((Tie)o).getTarget();
        }
        else {
            this.servant = null;
        }
    }
    
    public void unexport() {
        this.servant = null;
    }
}
