package com.sun.corba.se.impl.util;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA_2_3.portable.Delegate;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.stub.java.rmi._Remote_Stub;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.ValueFactory;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.BoxedValueHelper;
import javax.rmi.PortableRemoteObject;
import javax.rmi.CORBA.Tie;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import org.omg.CORBA.SystemException;
import javax.rmi.CORBA.Util;
import java.rmi.Remote;
import org.omg.CORBA.BAD_OPERATION;
import java.rmi.RemoteException;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import org.omg.CORBA.ORB;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.UtilSystemException;

public final class Utility
{
    public static final String STUB_PREFIX = "_";
    public static final String RMI_STUB_SUFFIX = "_Stub";
    public static final String DYNAMIC_STUB_SUFFIX = "_DynamicStub";
    public static final String IDL_STUB_SUFFIX = "Stub";
    public static final String TIE_SUFIX = "_Tie";
    private static IdentityHashtable tieCache;
    private static IdentityHashtable tieToStubCache;
    private static IdentityHashtable stubToTieCache;
    private static Object CACHE_MISS;
    private static UtilSystemException wrapper;
    private static OMGSystemException omgWrapper;
    
    public static Object autoConnect(final Object o, final ORB orb, final boolean b) {
        if (o == null) {
            return o;
        }
        if (StubAdapter.isStub(o)) {
            try {
                StubAdapter.getDelegate(o);
            }
            catch (final BAD_OPERATION bad_OPERATION) {
                try {
                    StubAdapter.connect(o, orb);
                }
                catch (final RemoteException ex) {
                    throw Utility.wrapper.objectNotConnected(ex, o.getClass().getName());
                }
            }
            return o;
        }
        if (!(o instanceof Remote)) {
            return o;
        }
        final Tie tie = Util.getTie((Remote)o);
        if (tie == null) {
            throw Utility.wrapper.objectNotExported(o.getClass().getName());
        }
        try {
            tie.orb();
        }
        catch (final SystemException ex2) {
            tie.orb(orb);
        }
        if (!b) {
            return StubAdapter.activateTie(tie);
        }
        final Remote loadStub = loadStub(tie, null, null, true);
        if (loadStub != null) {
            return loadStub;
        }
        throw Utility.wrapper.couldNotLoadStub(o.getClass().getName());
    }
    
    public static Tie loadTie(final Remote remote) {
        Tie tie = null;
        Class<? extends Remote> clazz = remote.getClass();
        synchronized (Utility.tieCache) {
            final Object value = Utility.tieCache.get(remote);
            if (value == null) {
                try {
                    for (tie = loadTie(clazz); tie == null && (clazz = clazz.getSuperclass()) != null && clazz != PortableRemoteObject.class && clazz != Object.class; tie = loadTie(clazz)) {}
                }
                catch (final Exception ex) {
                    Utility.wrapper.loadTieFailed(ex, clazz.getName());
                }
                if (tie == null) {
                    Utility.tieCache.put(remote, Utility.CACHE_MISS);
                }
                else {
                    Utility.tieCache.put(remote, tie);
                }
            }
            else if (value != Utility.CACHE_MISS) {
                try {
                    tie = (Tie)value.getClass().newInstance();
                }
                catch (final Exception ex2) {}
            }
        }
        return tie;
    }
    
    private static Tie loadTie(final Class clazz) {
        return com.sun.corba.se.spi.orb.ORB.getStubFactoryFactory().getTie(clazz);
    }
    
    public static void clearCaches() {
        synchronized (Utility.tieToStubCache) {
            Utility.tieToStubCache.clear();
        }
        synchronized (Utility.tieCache) {
            Utility.tieCache.clear();
        }
        synchronized (Utility.stubToTieCache) {
            Utility.stubToTieCache.clear();
        }
    }
    
    static Class loadClassOfType(final String s, final String s2, final ClassLoader classLoader, final Class clazz, final ClassLoader classLoader2) throws ClassNotFoundException {
        Class<?> clazz2 = null;
        try {
            try {
                if (!PackagePrefixChecker.hasOffendingPrefix(PackagePrefixChecker.withoutPackagePrefix(s))) {
                    clazz2 = Util.loadClass(PackagePrefixChecker.withoutPackagePrefix(s), s2, classLoader);
                }
                else {
                    clazz2 = Util.loadClass(s, s2, classLoader);
                }
            }
            catch (final ClassNotFoundException ex) {
                clazz2 = Util.loadClass(s, s2, classLoader);
            }
            if (clazz == null) {
                return clazz2;
            }
        }
        catch (final ClassNotFoundException ex2) {
            if (clazz == null) {
                throw ex2;
            }
        }
        if (clazz2 == null || !clazz.isAssignableFrom(clazz2)) {
            if (clazz.getClassLoader() != classLoader2) {
                throw new IllegalArgumentException("expectedTypeClassLoader not class loader of expected Type.");
            }
            if (classLoader2 != null) {
                clazz2 = classLoader2.loadClass(s);
            }
            else {
                ClassLoader classLoader3 = Thread.currentThread().getContextClassLoader();
                if (classLoader3 == null) {
                    classLoader3 = ClassLoader.getSystemClassLoader();
                }
                clazz2 = classLoader3.loadClass(s);
            }
        }
        return clazz2;
    }
    
    public static Class loadClassForClass(final String s, final String s2, final ClassLoader classLoader, final Class clazz, final ClassLoader classLoader2) throws ClassNotFoundException {
        if (clazz == null) {
            return Util.loadClass(s, s2, classLoader);
        }
        Class<?> clazz2 = null;
        try {
            clazz2 = Util.loadClass(s, s2, classLoader);
        }
        catch (final ClassNotFoundException ex) {
            if (clazz.getClassLoader() == null) {
                throw ex;
            }
        }
        if (clazz2 == null || (clazz2.getClassLoader() != null && clazz2.getClassLoader().loadClass(clazz.getName()) != clazz)) {
            if (clazz.getClassLoader() != classLoader2) {
                throw new IllegalArgumentException("relatedTypeClassLoader not class loader of relatedType.");
            }
            if (classLoader2 != null) {
                clazz2 = classLoader2.loadClass(s);
            }
        }
        return clazz2;
    }
    
    public static BoxedValueHelper getHelper(final Class clazz, String codebase, final String s) {
        String s2 = null;
        if (clazz != null) {
            s2 = clazz.getName();
            if (codebase == null) {
                codebase = Util.getCodebase(clazz);
            }
        }
        else {
            if (s != null) {
                s2 = RepositoryId.cache.getId(s).getClassName();
            }
            if (s2 == null) {
                throw Utility.wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE);
            }
        }
        try {
            final ClassLoader classLoader = (clazz == null) ? null : clazz.getClassLoader();
            return (BoxedValueHelper)loadClassForClass(s2 + "Helper", codebase, classLoader, clazz, classLoader).newInstance();
        }
        catch (final ClassNotFoundException ex) {
            throw Utility.wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE, ex);
        }
        catch (final IllegalAccessException ex2) {
            throw Utility.wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE, ex2);
        }
        catch (final InstantiationException ex3) {
            throw Utility.wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE, ex3);
        }
        catch (final ClassCastException ex4) {
            throw Utility.wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE, ex4);
        }
    }
    
    public static ValueFactory getFactory(final Class clazz, String codebase, final ORB orb, final String s) {
        ValueFactory lookup_value_factory = null;
        if (orb != null && s != null) {
            try {
                lookup_value_factory = ((org.omg.CORBA_2_3.ORB)orb).lookup_value_factory(s);
            }
            catch (final BAD_PARAM bad_PARAM) {}
        }
        String s2 = null;
        if (clazz != null) {
            s2 = clazz.getName();
            if (codebase == null) {
                codebase = Util.getCodebase(clazz);
            }
        }
        else {
            if (s != null) {
                s2 = RepositoryId.cache.getId(s).getClassName();
            }
            if (s2 == null) {
                throw Utility.omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE);
            }
        }
        if (lookup_value_factory != null && (!lookup_value_factory.getClass().getName().equals(s2 + "DefaultFactory") || (clazz == null && codebase == null))) {
            return lookup_value_factory;
        }
        try {
            final ClassLoader classLoader = (clazz == null) ? null : clazz.getClassLoader();
            return (ValueFactory)loadClassForClass(s2 + "DefaultFactory", codebase, classLoader, clazz, classLoader).newInstance();
        }
        catch (final ClassNotFoundException ex) {
            throw Utility.omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE, ex);
        }
        catch (final IllegalAccessException ex2) {
            throw Utility.omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE, ex2);
        }
        catch (final InstantiationException ex3) {
            throw Utility.omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE, ex3);
        }
        catch (final ClassCastException ex4) {
            throw Utility.omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE, ex4);
        }
    }
    
    public static Remote loadStub(final Tie tie, final PresentationManager.StubFactory stubFactory, final String s, final boolean b) {
        StubEntry stubEntry = null;
        synchronized (Utility.tieToStubCache) {
            final Object value = Utility.tieToStubCache.get(tie);
            if (value == null) {
                stubEntry = loadStubAndUpdateCache(tie, stubFactory, s, b);
            }
            else if (value != Utility.CACHE_MISS) {
                stubEntry = (StubEntry)value;
                if (!stubEntry.mostDerived && b) {
                    stubEntry = loadStubAndUpdateCache(tie, null, s, true);
                }
                else if (stubFactory != null && !StubAdapter.getTypeIds(stubEntry.stub)[0].equals(stubFactory.getTypeIds()[0])) {
                    stubEntry = loadStubAndUpdateCache(tie, null, s, true);
                    if (stubEntry == null) {
                        stubEntry = loadStubAndUpdateCache(tie, stubFactory, s, b);
                    }
                }
                else {
                    try {
                        StubAdapter.getDelegate(stubEntry.stub);
                    }
                    catch (final Exception ex) {
                        try {
                            StubAdapter.setDelegate(stubEntry.stub, StubAdapter.getDelegate(tie));
                        }
                        catch (final Exception ex2) {}
                    }
                }
            }
        }
        if (stubEntry != null) {
            return (Remote)stubEntry.stub;
        }
        return null;
    }
    
    private static StubEntry loadStubAndUpdateCache(final Tie tie, PresentationManager.StubFactory stubFactory, String codebase, final boolean b) {
        org.omg.CORBA.Object object = null;
        Object o = null;
        final boolean stub = StubAdapter.isStub(tie);
        if (stubFactory != null) {
            try {
                object = stubFactory.makeStub();
            }
            catch (final Throwable t) {
                Utility.wrapper.stubFactoryCouldNotMakeStub(t);
                if (t instanceof ThreadDeath) {
                    throw (ThreadDeath)t;
                }
            }
        }
        else {
            String[] array;
            if (stub) {
                array = StubAdapter.getTypeIds(tie);
            }
            else {
                array = ((Servant)tie)._all_interfaces(null, null);
            }
            if (codebase == null) {
                codebase = Util.getCodebase(tie.getClass());
            }
            if (array.length == 0) {
                object = new _Remote_Stub();
            }
            else {
                int i = 0;
                while (i < array.length) {
                    if (array[i].length() == 0) {
                        object = new _Remote_Stub();
                        break;
                    }
                    try {
                        final PresentationManager.StubFactoryFactory stubFactoryFactory = com.sun.corba.se.spi.orb.ORB.getStubFactoryFactory();
                        final RepositoryId id = RepositoryId.cache.getId(array[i]);
                        stubFactory = stubFactoryFactory.createStubFactory(id.getClassName(), id.isIDLType(), codebase, null, tie.getClass().getClassLoader());
                        object = stubFactory.makeStub();
                    }
                    catch (final Exception ex) {
                        Utility.wrapper.errorInMakeStubFromRepositoryId(ex);
                        if (!b) {
                            ++i;
                            continue;
                        }
                    }
                    break;
                }
            }
        }
        if (object == null) {
            Utility.tieToStubCache.put(tie, Utility.CACHE_MISS);
        }
        else {
            if (stub) {
                try {
                    StubAdapter.setDelegate(object, StubAdapter.getDelegate(tie));
                }
                catch (final Exception ex2) {
                    synchronized (Utility.stubToTieCache) {
                        Utility.stubToTieCache.put(object, tie);
                    }
                }
            }
            else {
                try {
                    StubAdapter.setDelegate(object, StubAdapter.getDelegate(tie));
                }
                catch (final BAD_INV_ORDER bad_INV_ORDER) {
                    synchronized (Utility.stubToTieCache) {
                        Utility.stubToTieCache.put(object, tie);
                    }
                }
                catch (final Exception ex3) {
                    throw Utility.wrapper.noPoa(ex3);
                }
            }
            o = new StubEntry(object, b);
            Utility.tieToStubCache.put(tie, o);
        }
        return (StubEntry)o;
    }
    
    public static Tie getAndForgetTie(final org.omg.CORBA.Object object) {
        synchronized (Utility.stubToTieCache) {
            return (Tie)Utility.stubToTieCache.remove(object);
        }
    }
    
    public static void purgeStubForTie(final Tie tie) {
        final StubEntry stubEntry;
        synchronized (Utility.tieToStubCache) {
            stubEntry = (StubEntry)Utility.tieToStubCache.remove(tie);
        }
        if (stubEntry != null) {
            synchronized (Utility.stubToTieCache) {
                Utility.stubToTieCache.remove(stubEntry.stub);
            }
        }
    }
    
    public static void purgeTieAndServant(final Tie tie) {
        synchronized (Utility.tieCache) {
            final Remote target = tie.getTarget();
            if (target != null) {
                Utility.tieCache.remove(target);
            }
        }
    }
    
    public static String stubNameFromRepID(final String s) {
        final RepositoryId id = RepositoryId.cache.getId(s);
        final String className = id.getClassName();
        String s2;
        if (id.isIDLType()) {
            s2 = idlStubName(className);
        }
        else {
            s2 = stubName(className);
        }
        return s2;
    }
    
    public static Remote loadStub(final org.omg.CORBA.Object object, final Class clazz) {
        Object o = null;
        try {
            String get_codebase = null;
            try {
                get_codebase = ((Delegate)StubAdapter.getDelegate(object)).get_codebase(object);
            }
            catch (final ClassCastException ex) {
                Utility.wrapper.classCastExceptionInLoadStub(ex);
            }
            o = com.sun.corba.se.spi.orb.ORB.getStubFactoryFactory().createStubFactory(clazz.getName(), false, get_codebase, clazz, clazz.getClassLoader()).makeStub();
            StubAdapter.setDelegate(o, StubAdapter.getDelegate(object));
        }
        catch (final Exception ex2) {
            Utility.wrapper.exceptionInLoadStub(ex2);
        }
        return (Remote)o;
    }
    
    public static Class loadStubClass(final String s, final String s2, final Class clazz) throws ClassNotFoundException {
        if (s.length() == 0) {
            throw new ClassNotFoundException();
        }
        final String stubNameFromRepID = stubNameFromRepID(s);
        final ClassLoader classLoader = (clazz == null) ? null : clazz.getClassLoader();
        try {
            return loadClassOfType(stubNameFromRepID, s2, classLoader, clazz, classLoader);
        }
        catch (final ClassNotFoundException ex) {
            return loadClassOfType(PackagePrefixChecker.packagePrefix() + stubNameFromRepID, s2, classLoader, clazz, classLoader);
        }
    }
    
    public static String stubName(final String s) {
        return stubName(s, false);
    }
    
    public static String dynamicStubName(final String s) {
        return stubName(s, true);
    }
    
    private static String stubName(final String s, final boolean b) {
        String s2 = stubNameForCompiler(s, b);
        if (PackagePrefixChecker.hasOffendingPrefix(s2)) {
            s2 = PackagePrefixChecker.packagePrefix() + s2;
        }
        return s2;
    }
    
    public static String stubNameForCompiler(final String s) {
        return stubNameForCompiler(s, false);
    }
    
    private static String stubNameForCompiler(final String s, final boolean b) {
        int n = s.indexOf(36);
        if (n < 0) {
            n = s.lastIndexOf(46);
        }
        final String s2 = b ? "_DynamicStub" : "_Stub";
        if (n > 0) {
            return s.substring(0, n + 1) + "_" + s.substring(n + 1) + s2;
        }
        return "_" + s + s2;
    }
    
    public static String tieName(final String s) {
        return PackagePrefixChecker.hasOffendingPrefix(tieNameForCompiler(s)) ? (PackagePrefixChecker.packagePrefix() + tieNameForCompiler(s)) : tieNameForCompiler(s);
    }
    
    public static String tieNameForCompiler(final String s) {
        int n = s.indexOf(36);
        if (n < 0) {
            n = s.lastIndexOf(46);
        }
        if (n > 0) {
            return s.substring(0, n + 1) + "_" + s.substring(n + 1) + "_Tie";
        }
        return "_" + s + "_Tie";
    }
    
    public static void throwNotSerializableForCorba(final String s) {
        throw Utility.omgWrapper.notSerializable(CompletionStatus.COMPLETED_MAYBE, s);
    }
    
    public static String idlStubName(final String s) {
        final int lastIndex = s.lastIndexOf(46);
        String s2;
        if (lastIndex > 0) {
            s2 = s.substring(0, lastIndex + 1) + "_" + s.substring(lastIndex + 1) + "Stub";
        }
        else {
            s2 = "_" + s + "Stub";
        }
        return s2;
    }
    
    public static void printStackTrace() {
        final Throwable t = new Throwable("Printing stack trace:");
        t.fillInStackTrace();
        t.printStackTrace();
    }
    
    public static Object readObjectAndNarrow(final InputStream inputStream, final Class clazz) throws ClassCastException {
        final org.omg.CORBA.Object read_Object = inputStream.read_Object();
        if (read_Object != null) {
            return PortableRemoteObject.narrow(read_Object, clazz);
        }
        return null;
    }
    
    public static Object readAbstractAndNarrow(final org.omg.CORBA_2_3.portable.InputStream inputStream, final Class clazz) throws ClassCastException {
        final Object read_abstract_interface = inputStream.read_abstract_interface();
        if (read_abstract_interface != null) {
            return PortableRemoteObject.narrow(read_abstract_interface, clazz);
        }
        return null;
    }
    
    static int hexOf(final char c) {
        final int n = c - '0';
        if (n >= 0 && n <= 9) {
            return n;
        }
        final int n2 = c - 'a' + 10;
        if (n2 >= 10 && n2 <= 15) {
            return n2;
        }
        final int n3 = c - 'A' + 10;
        if (n3 >= 10 && n3 <= 15) {
            return n3;
        }
        throw Utility.wrapper.badHexDigit();
    }
    
    static {
        Utility.tieCache = new IdentityHashtable();
        Utility.tieToStubCache = new IdentityHashtable();
        Utility.stubToTieCache = new IdentityHashtable();
        Utility.CACHE_MISS = new Object();
        Utility.wrapper = UtilSystemException.get("util");
        Utility.omgWrapper = OMGSystemException.get("util");
    }
}
