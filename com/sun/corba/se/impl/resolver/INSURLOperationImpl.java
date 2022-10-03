package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import java.util.Iterator;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectKey;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.Comparator;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.impl.naming.namingutil.IIOPEndpointInfo;
import com.sun.corba.se.spi.ior.IORFactories;
import java.util.ArrayList;
import java.util.HashMap;
import org.omg.CosNaming.NamingContextExtHelper;
import com.sun.corba.se.impl.naming.namingutil.CorbalocURL;
import com.sun.corba.se.impl.naming.namingutil.CorbanameURL;
import com.sun.corba.se.impl.naming.namingutil.INSURL;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import sun.corba.EncapsInputStreamFactory;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.naming.namingutil.INSURLHandler;
import org.omg.CosNaming.NamingContextExt;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.Operation;

public class INSURLOperationImpl implements Operation
{
    ORB orb;
    ORBUtilSystemException wrapper;
    OMGSystemException omgWrapper;
    Resolver bootstrapResolver;
    private NamingContextExt rootNamingContextExt;
    private Object rootContextCacheLock;
    private INSURLHandler insURLHandler;
    private static final int NIBBLES_PER_BYTE = 2;
    private static final int UN_SHIFT = 4;
    
    public INSURLOperationImpl(final ORB orb, final Resolver bootstrapResolver) {
        this.rootContextCacheLock = new Object();
        this.insURLHandler = INSURLHandler.getINSURLHandler();
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "orb.resolver");
        this.omgWrapper = OMGSystemException.get(orb, "orb.resolver");
        this.bootstrapResolver = bootstrapResolver;
    }
    
    private org.omg.CORBA.Object getIORFromString(final String s) {
        if ((s.length() & 0x1) == 0x1) {
            throw this.wrapper.badStringifiedIorLen();
        }
        final byte[] array = new byte[(s.length() - "IOR:".length()) / 2];
        for (int i = "IOR:".length(), n = 0; i < s.length(); i += 2, ++n) {
            array[n] = (byte)(ORBUtility.hexOf(s.charAt(i)) << 4 & 0xF0);
            final byte[] array2 = array;
            final int n2 = n;
            array2[n2] |= (byte)(ORBUtility.hexOf(s.charAt(i + 1)) & 0xF);
        }
        final EncapsInputStream encapsInputStream = EncapsInputStreamFactory.newEncapsInputStream(this.orb, array, array.length, this.orb.getORBData().getGIOPVersion());
        encapsInputStream.consumeEndian();
        return encapsInputStream.read_Object();
    }
    
    @Override
    public Object operate(final Object o) {
        if (!(o instanceof String)) {
            throw this.wrapper.stringExpected();
        }
        final String s = (String)o;
        if (s.startsWith("IOR:")) {
            return this.getIORFromString(s);
        }
        final INSURL url = this.insURLHandler.parseURL(s);
        if (url == null) {
            throw this.omgWrapper.soBadSchemeName();
        }
        return this.resolveINSURL(url);
    }
    
    private org.omg.CORBA.Object resolveINSURL(final INSURL insurl) {
        if (insurl.isCorbanameURL()) {
            return this.resolveCorbaname((CorbanameURL)insurl);
        }
        return this.resolveCorbaloc((CorbalocURL)insurl);
    }
    
    private org.omg.CORBA.Object resolveCorbaloc(final CorbalocURL corbalocURL) {
        org.omg.CORBA.Object object;
        if (corbalocURL.getRIRFlag()) {
            object = this.bootstrapResolver.resolve(corbalocURL.getKeyString());
        }
        else {
            object = this.getIORUsingCorbaloc(corbalocURL);
        }
        return object;
    }
    
    private org.omg.CORBA.Object resolveCorbaname(final CorbanameURL corbanameURL) {
        try {
            NamingContextExt namingContextExt;
            if (corbanameURL.getRIRFlag()) {
                namingContextExt = this.getDefaultRootNamingContext();
            }
            else {
                final org.omg.CORBA.Object iorUsingCorbaloc = this.getIORUsingCorbaloc(corbanameURL);
                if (iorUsingCorbaloc == null) {
                    return null;
                }
                namingContextExt = NamingContextExtHelper.narrow(iorUsingCorbaloc);
            }
            final String stringifiedName = corbanameURL.getStringifiedName();
            if (stringifiedName == null) {
                return namingContextExt;
            }
            return namingContextExt.resolve_str(stringifiedName);
        }
        catch (final Exception ex) {
            this.clearRootNamingContextCache();
            return null;
        }
    }
    
    private org.omg.CORBA.Object getIORUsingCorbaloc(final INSURL insurl) {
        final HashMap hashMap = new HashMap();
        final ArrayList list = new ArrayList();
        final List endpointInfo = insurl.getEndpointInfo();
        final String keyString = insurl.getKeyString();
        if (keyString == null) {
            return null;
        }
        final ObjectKey create = this.orb.getObjectKeyFactory().create(keyString.getBytes());
        final IORTemplate iorTemplate = IORFactories.makeIORTemplate(create.getTemplate());
        for (final IIOPEndpointInfo iiopEndpointInfo : endpointInfo) {
            final IIOPAddress iiopAddress = IIOPFactories.makeIIOPAddress(this.orb, iiopEndpointInfo.getHost(), iiopEndpointInfo.getPort());
            final GIOPVersion instance = GIOPVersion.getInstance((byte)iiopEndpointInfo.getMajor(), (byte)iiopEndpointInfo.getMinor());
            if (instance.equals(GIOPVersion.V1_0)) {
                list.add(IIOPFactories.makeIIOPProfileTemplate(this.orb, instance, iiopAddress));
            }
            else if (hashMap.get(instance) == null) {
                hashMap.put(instance, IIOPFactories.makeIIOPProfileTemplate(this.orb, instance, iiopAddress));
            }
            else {
                ((IIOPProfileTemplate)hashMap.get(instance)).add(IIOPFactories.makeAlternateIIOPAddressComponent(iiopAddress));
            }
        }
        final GIOPVersion giopVersion = this.orb.getORBData().getGIOPVersion();
        final IIOPProfileTemplate iiopProfileTemplate = (IIOPProfileTemplate)hashMap.get(giopVersion);
        if (iiopProfileTemplate != null) {
            iorTemplate.add(iiopProfileTemplate);
            hashMap.remove(giopVersion);
        }
        final Comparator comparator = new Comparator() {
            @Override
            public int compare(final Object o, final Object o2) {
                final GIOPVersion giopVersion = (GIOPVersion)o;
                final GIOPVersion giopVersion2 = (GIOPVersion)o2;
                return giopVersion.lessThan(giopVersion2) ? 1 : (giopVersion.equals(giopVersion2) ? 0 : -1);
            }
        };
        final ArrayList list2 = new ArrayList<Object>(hashMap.keySet());
        Collections.sort((List<E>)list2, comparator);
        final Iterator<Object> iterator2 = list2.iterator();
        while (iterator2.hasNext()) {
            iorTemplate.add((IIOPProfileTemplate)hashMap.get(iterator2.next()));
        }
        iorTemplate.addAll(list);
        return ORBUtility.makeObjectReference(iorTemplate.makeIOR(this.orb, "", create.getId()));
    }
    
    private NamingContextExt getDefaultRootNamingContext() {
        synchronized (this.rootContextCacheLock) {
            if (this.rootNamingContextExt == null) {
                try {
                    this.rootNamingContextExt = NamingContextExtHelper.narrow(this.orb.getLocalResolver().resolve("NameService"));
                }
                catch (final Exception ex) {
                    this.rootNamingContextExt = null;
                }
            }
        }
        return this.rootNamingContextExt;
    }
    
    private void clearRootNamingContextCache() {
        synchronized (this.rootContextCacheLock) {
            this.rootNamingContextExt = null;
        }
    }
}
