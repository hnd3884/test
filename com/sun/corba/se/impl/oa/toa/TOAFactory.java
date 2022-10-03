package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import java.util.HashMap;
import com.sun.corba.se.impl.ior.ObjectKeyTemplateBase;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import java.util.Map;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;

public class TOAFactory implements ObjectAdapterFactory
{
    private ORB orb;
    private ORBUtilSystemException wrapper;
    private TOAImpl toa;
    private Map codebaseToTOA;
    private TransientObjectManager tom;
    
    @Override
    public ObjectAdapter find(final ObjectAdapterId objectAdapterId) {
        if (objectAdapterId.equals(ObjectKeyTemplateBase.JIDL_OAID)) {
            return this.getTOA();
        }
        throw this.wrapper.badToaOaid();
    }
    
    @Override
    public void init(final ORB orb) {
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "oa.lifecycle");
        this.tom = new TransientObjectManager(orb);
        this.codebaseToTOA = new HashMap();
    }
    
    @Override
    public void shutdown(final boolean b) {
        if (Util.isInstanceDefined()) {
            Util.getInstance().unregisterTargetsForORB(this.orb);
        }
    }
    
    public synchronized TOA getTOA(final String s) {
        TOA toa = this.codebaseToTOA.get(s);
        if (toa == null) {
            toa = new TOAImpl(this.orb, this.tom, s);
            this.codebaseToTOA.put(s, toa);
        }
        return toa;
    }
    
    public synchronized TOA getTOA() {
        if (this.toa == null) {
            this.toa = new TOAImpl(this.orb, this.tom, null);
        }
        return this.toa;
    }
    
    @Override
    public ORB getORB() {
        return this.orb;
    }
}
