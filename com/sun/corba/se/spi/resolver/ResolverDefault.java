package com.sun.corba.se.spi.resolver;

import com.sun.corba.se.impl.resolver.FileResolverImpl;
import java.io.File;
import com.sun.corba.se.impl.resolver.SplitLocalResolverImpl;
import com.sun.corba.se.impl.resolver.INSURLOperationImpl;
import com.sun.corba.se.impl.resolver.CompositeResolverImpl;
import com.sun.corba.se.impl.resolver.BootstrapResolverImpl;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.resolver.ORBDefaultInitRefResolverImpl;
import com.sun.corba.se.impl.resolver.ORBInitRefResolverImpl;
import com.sun.corba.se.spi.orb.StringPair;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.impl.resolver.LocalResolverImpl;

public class ResolverDefault
{
    public static LocalResolver makeLocalResolver() {
        return new LocalResolverImpl();
    }
    
    public static Resolver makeORBInitRefResolver(final Operation operation, final StringPair[] array) {
        return new ORBInitRefResolverImpl(operation, array);
    }
    
    public static Resolver makeORBDefaultInitRefResolver(final Operation operation, final String s) {
        return new ORBDefaultInitRefResolverImpl(operation, s);
    }
    
    public static Resolver makeBootstrapResolver(final ORB orb, final String s, final int n) {
        return new BootstrapResolverImpl(orb, s, n);
    }
    
    public static Resolver makeCompositeResolver(final Resolver resolver, final Resolver resolver2) {
        return new CompositeResolverImpl(resolver, resolver2);
    }
    
    public static Operation makeINSURLOperation(final ORB orb, final Resolver resolver) {
        return new INSURLOperationImpl(orb, resolver);
    }
    
    public static LocalResolver makeSplitLocalResolver(final Resolver resolver, final LocalResolver localResolver) {
        return new SplitLocalResolverImpl(resolver, localResolver);
    }
    
    public static Resolver makeFileResolver(final ORB orb, final File file) {
        return new FileResolverImpl(orb, file);
    }
}
