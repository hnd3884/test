package com.sun.corba.se.impl.transport;

import com.sun.corba.se.spi.transport.SocketInfo;
import java.util.Iterator;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.AlternateIIOPAddressComponent;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import java.util.ArrayList;
import java.util.List;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.transport.IORToSocketInfo;

public class DefaultIORToSocketInfoImpl implements IORToSocketInfo
{
    @Override
    public List getSocketInfo(final IOR ior) {
        final ArrayList list = new ArrayList();
        final IIOPProfileTemplate iiopProfileTemplate = (IIOPProfileTemplate)ior.getProfile().getTaggedProfileTemplate();
        final IIOPAddress primaryAddress = iiopProfileTemplate.getPrimaryAddress();
        list.add(this.createSocketInfo(primaryAddress.getHost().toLowerCase(), primaryAddress.getPort()));
        final Iterator iteratorById = iiopProfileTemplate.iteratorById(3);
        while (iteratorById.hasNext()) {
            final AlternateIIOPAddressComponent alternateIIOPAddressComponent = iteratorById.next();
            list.add(this.createSocketInfo(alternateIIOPAddressComponent.getAddress().getHost().toLowerCase(), alternateIIOPAddressComponent.getAddress().getPort()));
        }
        return list;
    }
    
    private SocketInfo createSocketInfo(final String s, final int n) {
        return new SocketInfo() {
            @Override
            public String getType() {
                return "IIOP_CLEAR_TEXT";
            }
            
            @Override
            public String getHost() {
                return s;
            }
            
            @Override
            public int getPort() {
                return n;
            }
        };
    }
}
