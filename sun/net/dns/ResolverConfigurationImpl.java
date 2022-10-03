package sun.net.dns;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.StringTokenizer;
import java.util.LinkedList;

public class ResolverConfigurationImpl extends ResolverConfiguration
{
    private static Object lock;
    private final Options opts;
    private static boolean changed;
    private static long lastRefresh;
    private static final int TIMEOUT = 120000;
    private static String os_searchlist;
    private static String os_nameservers;
    private static LinkedList<String> searchlist;
    private static LinkedList<String> nameservers;
    
    private LinkedList<String> stringToList(final String s) {
        final LinkedList list = new LinkedList();
        final StringTokenizer stringTokenizer = new StringTokenizer(s, ", ");
        while (stringTokenizer.hasMoreTokens()) {
            final String nextToken = stringTokenizer.nextToken();
            if (!list.contains(nextToken)) {
                list.add(nextToken);
            }
        }
        return list;
    }
    
    private void loadConfig() {
        assert Thread.holdsLock(ResolverConfigurationImpl.lock);
        if (ResolverConfigurationImpl.changed) {
            ResolverConfigurationImpl.changed = false;
        }
        else if (ResolverConfigurationImpl.lastRefresh >= 0L && System.currentTimeMillis() - ResolverConfigurationImpl.lastRefresh < 120000L) {
            return;
        }
        loadDNSconfig0();
        ResolverConfigurationImpl.lastRefresh = System.currentTimeMillis();
        ResolverConfigurationImpl.searchlist = this.stringToList(ResolverConfigurationImpl.os_searchlist);
        ResolverConfigurationImpl.nameservers = this.stringToList(ResolverConfigurationImpl.os_nameservers);
        ResolverConfigurationImpl.os_searchlist = null;
        ResolverConfigurationImpl.os_nameservers = null;
    }
    
    ResolverConfigurationImpl() {
        this.opts = new OptionsImpl();
    }
    
    @Override
    public List<String> searchlist() {
        synchronized (ResolverConfigurationImpl.lock) {
            this.loadConfig();
            return (List)ResolverConfigurationImpl.searchlist.clone();
        }
    }
    
    @Override
    public List<String> nameservers() {
        synchronized (ResolverConfigurationImpl.lock) {
            this.loadConfig();
            return (List)ResolverConfigurationImpl.nameservers.clone();
        }
    }
    
    @Override
    public Options options() {
        return this.opts;
    }
    
    static native void init0();
    
    static native void loadDNSconfig0();
    
    static native int notifyAddrChange0();
    
    static {
        ResolverConfigurationImpl.lock = new Object();
        ResolverConfigurationImpl.changed = false;
        ResolverConfigurationImpl.lastRefresh = -1L;
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("net");
                return null;
            }
        });
        init0();
        final AddressChangeListener addressChangeListener = new AddressChangeListener();
        addressChangeListener.setDaemon(true);
        addressChangeListener.start();
    }
    
    static class AddressChangeListener extends Thread
    {
        @Override
        public void run() {
            while (ResolverConfigurationImpl.notifyAddrChange0() == 0) {
                synchronized (ResolverConfigurationImpl.lock) {
                    ResolverConfigurationImpl.changed = true;
                }
            }
        }
    }
}
