package com.sun.corba.se.impl.presentation.rmi;

import javax.rmi.CORBA.Tie;
import org.omg.CORBA.CompletionStatus;
import javax.rmi.CORBA.Util;
import com.sun.corba.se.impl.util.PackagePrefixChecker;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;

public class StubFactoryFactoryStaticImpl extends StubFactoryFactoryBase
{
    private ORBUtilSystemException wrapper;
    
    public StubFactoryFactoryStaticImpl() {
        this.wrapper = ORBUtilSystemException.get("rpc.presentation");
    }
    
    @Override
    public PresentationManager.StubFactory createStubFactory(final String s, final boolean b, final String s2, final Class clazz, final ClassLoader classLoader) {
        String s3;
        if (b) {
            s3 = Utility.idlStubName(s);
        }
        else {
            s3 = Utility.stubNameForCompiler(s);
        }
        final ClassLoader classLoader2 = (clazz == null) ? classLoader : clazz.getClassLoader();
        String string = s3;
        String string2 = s3;
        if (PackagePrefixChecker.hasOffendingPrefix(s3)) {
            string = PackagePrefixChecker.packagePrefix() + s3;
        }
        else {
            string2 = PackagePrefixChecker.packagePrefix() + s3;
        }
        Class<?> clazz2;
        try {
            clazz2 = Util.loadClass(string, s2, classLoader2);
        }
        catch (final ClassNotFoundException ex) {
            this.wrapper.classNotFound1(CompletionStatus.COMPLETED_MAYBE, ex, string);
            try {
                clazz2 = Util.loadClass(string2, s2, classLoader2);
            }
            catch (final ClassNotFoundException ex2) {
                throw this.wrapper.classNotFound2(CompletionStatus.COMPLETED_MAYBE, ex2, string2);
            }
        }
        if (clazz2 != null) {
            if (clazz == null || clazz.isAssignableFrom(clazz2)) {
                return new StubFactoryStaticImpl(clazz2);
            }
        }
        try {
            ClassLoader classLoader3 = Thread.currentThread().getContextClassLoader();
            if (classLoader3 == null) {
                classLoader3 = ClassLoader.getSystemClassLoader();
            }
            clazz2 = classLoader3.loadClass(s);
        }
        catch (final Exception ex3) {
            final IllegalStateException ex4 = new IllegalStateException("Could not load class " + s3);
            ex4.initCause(ex3);
            throw ex4;
        }
        return new StubFactoryStaticImpl(clazz2);
    }
    
    @Override
    public Tie getTie(final Class clazz) {
        final String tieName = Utility.tieName(clazz.getName());
        try {
            try {
                return (Tie)Utility.loadClassForClass(tieName, Util.getCodebase(clazz), null, clazz, clazz.getClassLoader()).newInstance();
            }
            catch (final Exception ex) {
                return (Tie)Utility.loadClassForClass(PackagePrefixChecker.packagePrefix() + tieName, Util.getCodebase(clazz), null, clazz, clazz.getClassLoader()).newInstance();
            }
        }
        catch (final Exception ex2) {
            return null;
        }
    }
    
    @Override
    public boolean createsDynamicStubs() {
        return false;
    }
}
