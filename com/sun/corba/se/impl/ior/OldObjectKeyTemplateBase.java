package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;

public abstract class OldObjectKeyTemplateBase extends ObjectKeyTemplateBase
{
    public OldObjectKeyTemplateBase(final ORB orb, final int n, final int n2, final int n3, final String s, final ObjectAdapterId objectAdapterId) {
        super(orb, n, n2, n3, s, objectAdapterId);
        if (n == -1347695874) {
            this.setORBVersion(ORBVersionFactory.getOLD());
        }
        else {
            if (n != -1347695873) {
                throw this.wrapper.badMagic(new Integer(n));
            }
            this.setORBVersion(ORBVersionFactory.getNEW());
        }
    }
}
