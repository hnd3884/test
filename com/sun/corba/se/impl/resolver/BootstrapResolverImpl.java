package com.sun.corba.se.impl.resolver;

import java.util.HashSet;
import java.util.Set;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import org.omg.CORBA.portable.Delegate;
import com.sun.corba.se.spi.resolver.Resolver;

public class BootstrapResolverImpl implements Resolver
{
    private Delegate bootstrapDelegate;
    private ORBUtilSystemException wrapper;
    
    public BootstrapResolverImpl(final ORB orb, final String s, final int n) {
        this.wrapper = ORBUtilSystemException.get(orb, "orb.resolver");
        final ObjectKey create = orb.getObjectKeyFactory().create("INIT".getBytes());
        final IIOPProfileTemplate iiopProfileTemplate = IIOPFactories.makeIIOPProfileTemplate(orb, GIOPVersion.V1_0, IIOPFactories.makeIIOPAddress(orb, s, n));
        final IORTemplate iorTemplate = IORFactories.makeIORTemplate(create.getTemplate());
        iorTemplate.add(iiopProfileTemplate);
        this.bootstrapDelegate = ORBUtility.makeClientDelegate(iorTemplate.makeIOR(orb, "", create.getId()));
    }
    
    private InputStream invoke(final String s, final String s2) {
        int i = 1;
        InputStream invoke = null;
        while (i != 0) {
            final org.omg.CORBA.Object object = null;
            i = 0;
            final OutputStream request = this.bootstrapDelegate.request(object, s, true);
            if (s2 != null) {
                request.write_string(s2);
            }
            try {
                invoke = this.bootstrapDelegate.invoke(object, request);
            }
            catch (final ApplicationException ex) {
                throw this.wrapper.bootstrapApplicationException(ex);
            }
            catch (final RemarshalException ex2) {
                i = 1;
            }
        }
        return invoke;
    }
    
    @Override
    public org.omg.CORBA.Object resolve(final String s) {
        InputStream invoke = null;
        org.omg.CORBA.Object read_Object = null;
        try {
            invoke = this.invoke("get", s);
            read_Object = invoke.read_Object();
        }
        finally {
            this.bootstrapDelegate.releaseReply(null, invoke);
        }
        return read_Object;
    }
    
    @Override
    public Set list() {
        InputStream invoke = null;
        final HashSet set = new HashSet();
        try {
            invoke = this.invoke("list", null);
            for (int read_long = invoke.read_long(), i = 0; i < read_long; ++i) {
                set.add(invoke.read_string());
            }
        }
        finally {
            this.bootstrapDelegate.releaseReply(null, invoke);
        }
        return set;
    }
}
