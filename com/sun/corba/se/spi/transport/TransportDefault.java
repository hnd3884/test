package com.sun.corba.se.spi.transport;

import com.sun.corba.se.impl.transport.ReadTCPTimeoutsImpl;
import com.sun.corba.se.impl.protocol.CorbaClientDelegateImpl;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import com.sun.corba.se.spi.protocol.ClientDelegateFactory;
import com.sun.corba.se.impl.transport.CorbaContactInfoListImpl;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;

public abstract class TransportDefault
{
    private TransportDefault() {
    }
    
    public static CorbaContactInfoListFactory makeCorbaContactInfoListFactory(final ORB orb) {
        return new CorbaContactInfoListFactory() {
            @Override
            public void setORB(final ORB orb) {
            }
            
            @Override
            public CorbaContactInfoList create(final IOR ior) {
                return new CorbaContactInfoListImpl(orb, ior);
            }
        };
    }
    
    public static ClientDelegateFactory makeClientDelegateFactory(final ORB orb) {
        return new ClientDelegateFactory() {
            @Override
            public CorbaClientDelegate create(final CorbaContactInfoList list) {
                return new CorbaClientDelegateImpl(orb, list);
            }
        };
    }
    
    public static IORTransformer makeIORTransformer(final ORB orb) {
        return null;
    }
    
    public static ReadTimeoutsFactory makeReadTimeoutsFactory() {
        return new ReadTimeoutsFactory() {
            @Override
            public ReadTimeouts create(final int n, final int n2, final int n3, final int n4) {
                return new ReadTCPTimeoutsImpl(n, n2, n3, n4);
            }
        };
    }
}
