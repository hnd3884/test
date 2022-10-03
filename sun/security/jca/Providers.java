package sun.security.jca;

import java.security.Provider;

public class Providers
{
    private static final ThreadLocal<ProviderList> threadLists;
    private static volatile int threadListsUsed;
    private static volatile ProviderList providerList;
    private static final String BACKUP_PROVIDER_CLASSNAME = "sun.security.provider.VerificationProvider";
    private static final String[] jarVerificationProviders;
    
    private Providers() {
    }
    
    public static Provider getSunProvider() {
        try {
            return (Provider)Class.forName(Providers.jarVerificationProviders[0]).newInstance();
        }
        catch (final Exception ex) {
            try {
                return (Provider)Class.forName("sun.security.provider.VerificationProvider").newInstance();
            }
            catch (final Exception ex2) {
                throw new RuntimeException("Sun provider not found", ex);
            }
        }
    }
    
    public static Object startJarVerification() {
        return beginThreadProviderList(getProviderList().getJarList(Providers.jarVerificationProviders));
    }
    
    public static void stopJarVerification(final Object o) {
        endThreadProviderList((ProviderList)o);
    }
    
    public static ProviderList getProviderList() {
        ProviderList list = getThreadProviderList();
        if (list == null) {
            list = getSystemProviderList();
        }
        return list;
    }
    
    public static void setProviderList(final ProviderList systemProviderList) {
        if (getThreadProviderList() == null) {
            setSystemProviderList(systemProviderList);
        }
        else {
            changeThreadProviderList(systemProviderList);
        }
    }
    
    public static ProviderList getFullProviderList() {
        synchronized (Providers.class) {
            ProviderList threadProviderList = getThreadProviderList();
            if (threadProviderList != null) {
                final ProviderList removeInvalid = threadProviderList.removeInvalid();
                if (removeInvalid != threadProviderList) {
                    changeThreadProviderList(removeInvalid);
                    threadProviderList = removeInvalid;
                }
                return threadProviderList;
            }
        }
        ProviderList systemProviderList = getSystemProviderList();
        final ProviderList removeInvalid2 = systemProviderList.removeInvalid();
        if (removeInvalid2 != systemProviderList) {
            setSystemProviderList(removeInvalid2);
            systemProviderList = removeInvalid2;
        }
        return systemProviderList;
    }
    
    private static ProviderList getSystemProviderList() {
        return Providers.providerList;
    }
    
    private static void setSystemProviderList(final ProviderList providerList) {
        Providers.providerList = providerList;
    }
    
    public static ProviderList getThreadProviderList() {
        if (Providers.threadListsUsed == 0) {
            return null;
        }
        return Providers.threadLists.get();
    }
    
    private static void changeThreadProviderList(final ProviderList list) {
        Providers.threadLists.set(list);
    }
    
    public static synchronized ProviderList beginThreadProviderList(final ProviderList list) {
        if (ProviderList.debug != null) {
            ProviderList.debug.println("ThreadLocal providers: " + list);
        }
        final ProviderList list2 = Providers.threadLists.get();
        ++Providers.threadListsUsed;
        Providers.threadLists.set(list);
        return list2;
    }
    
    public static synchronized void endThreadProviderList(final ProviderList list) {
        if (list == null) {
            if (ProviderList.debug != null) {
                ProviderList.debug.println("Disabling ThreadLocal providers");
            }
            Providers.threadLists.remove();
        }
        else {
            if (ProviderList.debug != null) {
                ProviderList.debug.println("Restoring previous ThreadLocal providers: " + list);
            }
            Providers.threadLists.set(list);
        }
        --Providers.threadListsUsed;
    }
    
    static {
        threadLists = new InheritableThreadLocal<ProviderList>();
        Providers.providerList = ProviderList.EMPTY;
        Providers.providerList = ProviderList.fromSecurityProperties();
        jarVerificationProviders = new String[] { "sun.security.provider.Sun", "sun.security.rsa.SunRsaSign", "sun.security.ec.SunEC", "sun.security.provider.VerificationProvider" };
    }
}
