package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.PortableServer._ServantLocatorStub;
import org.omg.PortableServer.ServantLocator;
import org.omg.PortableServer._ServantActivatorStub;
import org.omg.PortableServer.ServantActivator;
import org.omg.DynamicAny.DynValue;
import org.omg.DynamicAny._DynValueStub;
import org.omg.DynamicAny._DynUnionStub;
import org.omg.DynamicAny.DynUnion;
import org.omg.DynamicAny._DynStructStub;
import org.omg.DynamicAny.DynStruct;
import org.omg.DynamicAny._DynSequenceStub;
import org.omg.DynamicAny.DynSequence;
import org.omg.DynamicAny._DynFixedStub;
import org.omg.DynamicAny.DynFixed;
import org.omg.DynamicAny._DynEnumStub;
import org.omg.DynamicAny.DynEnum;
import org.omg.DynamicAny._DynArrayStub;
import org.omg.DynamicAny.DynArray;
import org.omg.DynamicAny._DynAnyStub;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny._DynAnyFactoryStub;
import org.omg.DynamicAny.DynAnyFactory;
import org.omg.CosNaming._NamingContextStub;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming._NamingContextExtStub;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming._BindingIteratorStub;
import org.omg.CosNaming.BindingIterator;
import com.sun.corba.se.spi.activation._ServerStub;
import com.sun.corba.se.spi.activation._ServerManagerStub;
import com.sun.corba.se.spi.activation._RepositoryStub;
import com.sun.corba.se.spi.activation._LocatorStub;
import com.sun.corba.se.spi.activation._InitialNameServiceStub;
import com.sun.corba.se.spi.activation._ActivatorStub;
import com.sun.corba.se.spi.activation.Activator;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Set;
import com.sun.corba.se.spi.ior.IORTypeCheckRegistry;

public class IORTypeCheckRegistryImpl implements IORTypeCheckRegistry
{
    private final Set<String> iorTypeNames;
    private static final Set<String> builtinIorTypeNames;
    private ORB theOrb;
    
    public IORTypeCheckRegistryImpl(final String s, final ORB theOrb) {
        this.theOrb = theOrb;
        this.iorTypeNames = this.parseIorClassNameList(s);
    }
    
    @Override
    public boolean isValidIORType(final String s) {
        this.dprintTransport(".isValidIORType : iorClassName == " + s);
        return this.validateIorTypeByName(s);
    }
    
    private boolean validateIorTypeByName(final String s) {
        this.dprintTransport(".validateIorTypeByName : iorClassName == " + s);
        boolean b = this.checkIorTypeNames(s);
        if (!b) {
            b = this.checkBuiltinClassNames(s);
        }
        this.dprintTransport(".validateIorTypeByName : isValidType == " + b);
        return b;
    }
    
    private boolean checkIorTypeNames(final String s) {
        return this.iorTypeNames != null && this.iorTypeNames.contains(s);
    }
    
    private boolean checkBuiltinClassNames(final String s) {
        return IORTypeCheckRegistryImpl.builtinIorTypeNames.contains(s);
    }
    
    private Set<String> parseIorClassNameList(final String s) {
        Set<String> unmodifiableSet = null;
        if (s != null) {
            unmodifiableSet = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList(s.split(";"))));
            if (this.theOrb.orbInitDebugFlag) {
                this.dprintConfiguredIorTypeNames();
            }
        }
        return unmodifiableSet;
    }
    
    private static Set<String> initBuiltinIorTypeNames() {
        final Set<Class<?>> initBuiltInCorbaStubTypes = initBuiltInCorbaStubTypes();
        final String[] array = new String[initBuiltInCorbaStubTypes.size()];
        int n = 0;
        final Iterator iterator = initBuiltInCorbaStubTypes.iterator();
        while (iterator.hasNext()) {
            array[n++] = ((Class)iterator.next()).getName();
        }
        return (Set<String>)Collections.unmodifiableSet((Set<?>)new HashSet<Object>(Arrays.asList(array)));
    }
    
    private static Set<Class<?>> initBuiltInCorbaStubTypes() {
        return new HashSet<Class<?>>(Arrays.asList(Activator.class, _ActivatorStub.class, _InitialNameServiceStub.class, _LocatorStub.class, _RepositoryStub.class, _ServerManagerStub.class, _ServerStub.class, BindingIterator.class, _BindingIteratorStub.class, NamingContextExt.class, _NamingContextExtStub.class, NamingContext.class, _NamingContextStub.class, DynAnyFactory.class, _DynAnyFactoryStub.class, DynAny.class, _DynAnyStub.class, DynArray.class, _DynArrayStub.class, DynEnum.class, _DynEnumStub.class, DynFixed.class, _DynFixedStub.class, DynSequence.class, _DynSequenceStub.class, DynStruct.class, _DynStructStub.class, DynUnion.class, _DynUnionStub.class, _DynValueStub.class, DynValue.class, ServantActivator.class, _ServantActivatorStub.class, ServantLocator.class, _ServantLocatorStub.class));
    }
    
    private void dprintConfiguredIorTypeNames() {
        if (this.iorTypeNames != null) {
            final Iterator<String> iterator = this.iorTypeNames.iterator();
            while (iterator.hasNext()) {
                ORBUtility.dprint(this, ".dprintConfiguredIorTypeNames: " + iterator.next());
            }
        }
    }
    
    private void dprintTransport(final String s) {
        if (this.theOrb.transportDebugFlag) {
            ORBUtility.dprint(this, s);
        }
    }
    
    static {
        builtinIorTypeNames = initBuiltinIorTypeNames();
    }
}
