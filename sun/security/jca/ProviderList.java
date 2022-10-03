package sun.security.jca;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.security.Security;
import java.util.AbstractList;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.security.Provider;
import sun.security.util.Debug;

public final class ProviderList
{
    static final Debug debug;
    private static final ProviderConfig[] PC0;
    private static final Provider[] P0;
    static final ProviderList EMPTY;
    private static final Provider EMPTY_PROVIDER;
    private final ProviderConfig[] configs;
    private volatile boolean allLoaded;
    private final List<Provider> userList;
    
    static ProviderList fromSecurityProperties() {
        return AccessController.doPrivileged((PrivilegedAction<ProviderList>)new PrivilegedAction<ProviderList>() {
            @Override
            public ProviderList run() {
                return new ProviderList(null);
            }
        });
    }
    
    public static ProviderList add(final ProviderList list, final Provider provider) {
        return insertAt(list, provider, -1);
    }
    
    public static ProviderList insertAt(final ProviderList list, final Provider provider, int n) {
        if (list.getProvider(provider.getName()) != null) {
            return list;
        }
        final ArrayList list2 = new ArrayList((Collection<? extends E>)Arrays.asList(list.configs));
        final int size = list2.size();
        if (n < 0 || n > size) {
            n = size;
        }
        list2.add(n, new ProviderConfig(provider));
        return new ProviderList((ProviderConfig[])list2.toArray(ProviderList.PC0), true);
    }
    
    public static ProviderList remove(final ProviderList list, final String s) {
        if (list.getProvider(s) == null) {
            return list;
        }
        final ProviderConfig[] array = new ProviderConfig[list.size() - 1];
        int n = 0;
        for (final ProviderConfig providerConfig : list.configs) {
            if (!providerConfig.getProvider().getName().equals(s)) {
                array[n++] = providerConfig;
            }
        }
        return new ProviderList(array, true);
    }
    
    public static ProviderList newList(final Provider... array) {
        final ProviderConfig[] array2 = new ProviderConfig[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = new ProviderConfig(array[i]);
        }
        return new ProviderList(array2, true);
    }
    
    private ProviderList(final ProviderConfig[] configs, final boolean allLoaded) {
        this.userList = new AbstractList<Provider>() {
            @Override
            public int size() {
                return ProviderList.this.configs.length;
            }
            
            @Override
            public Provider get(final int n) {
                return ProviderList.this.getProvider(n);
            }
        };
        this.configs = configs;
        this.allLoaded = allLoaded;
    }
    
    private ProviderList() {
        this.userList = new AbstractList<Provider>() {
            @Override
            public int size() {
                return ProviderList.this.configs.length;
            }
            
            @Override
            public Provider get(final int n) {
                return ProviderList.this.getProvider(n);
            }
        };
        final ArrayList list = new ArrayList();
        int n = 1;
        while (true) {
            final String property = Security.getProperty("security.provider." + n);
            if (property == null) {
                break;
            }
            final String trim = property.trim();
            if (trim.length() == 0) {
                System.err.println("invalid entry for security.provider." + n);
                break;
            }
            final int index = trim.indexOf(32);
            ProviderConfig providerConfig;
            if (index == -1) {
                providerConfig = new ProviderConfig(trim);
            }
            else {
                providerConfig = new ProviderConfig(trim.substring(0, index), trim.substring(index + 1).trim());
            }
            if (!list.contains(providerConfig)) {
                list.add(providerConfig);
            }
            ++n;
        }
        this.configs = (ProviderConfig[])list.toArray(ProviderList.PC0);
        if (ProviderList.debug != null) {
            ProviderList.debug.println("provider configuration: " + list);
        }
    }
    
    ProviderList getJarList(final String[] array) {
        final ArrayList list = new ArrayList();
        for (int length = array.length, i = 0; i < length; ++i) {
            ProviderConfig providerConfig = new ProviderConfig(array[i]);
            for (final ProviderConfig providerConfig2 : this.configs) {
                if (providerConfig2.equals(providerConfig)) {
                    providerConfig = providerConfig2;
                    break;
                }
            }
            list.add(providerConfig);
        }
        return new ProviderList((ProviderConfig[])list.toArray(ProviderList.PC0), false);
    }
    
    public int size() {
        return this.configs.length;
    }
    
    Provider getProvider(final int n) {
        final Provider provider = this.configs[n].getProvider();
        return (provider != null) ? provider : ProviderList.EMPTY_PROVIDER;
    }
    
    public List<Provider> providers() {
        return this.userList;
    }
    
    private ProviderConfig getProviderConfig(final String s) {
        final int index = this.getIndex(s);
        return (index != -1) ? this.configs[index] : null;
    }
    
    public Provider getProvider(final String s) {
        final ProviderConfig providerConfig = this.getProviderConfig(s);
        return (providerConfig == null) ? null : providerConfig.getProvider();
    }
    
    public int getIndex(final String s) {
        for (int i = 0; i < this.configs.length; ++i) {
            if (this.getProvider(i).getName().equals(s)) {
                return i;
            }
        }
        return -1;
    }
    
    private int loadAll() {
        if (this.allLoaded) {
            return this.configs.length;
        }
        if (ProviderList.debug != null) {
            ProviderList.debug.println("Loading all providers");
            new Exception("Debug Info. Call trace:").printStackTrace();
        }
        int n = 0;
        for (int i = 0; i < this.configs.length; ++i) {
            if (this.configs[i].getProvider() != null) {
                ++n;
            }
        }
        if (n == this.configs.length) {
            this.allLoaded = true;
        }
        return n;
    }
    
    ProviderList removeInvalid() {
        final int loadAll = this.loadAll();
        if (loadAll == this.configs.length) {
            return this;
        }
        final ProviderConfig[] array = new ProviderConfig[loadAll];
        int i = 0;
        int n = 0;
        while (i < this.configs.length) {
            final ProviderConfig providerConfig = this.configs[i];
            if (providerConfig.isLoaded()) {
                array[n++] = providerConfig;
            }
            ++i;
        }
        return new ProviderList(array, true);
    }
    
    public Provider[] toArray() {
        return this.providers().toArray(ProviderList.P0);
    }
    
    @Override
    public String toString() {
        return Arrays.asList(this.configs).toString();
    }
    
    public Provider.Service getService(final String s, final String s2) {
        for (int i = 0; i < this.configs.length; ++i) {
            final Provider.Service service = this.getProvider(i).getService(s, s2);
            if (service != null) {
                return service;
            }
        }
        return null;
    }
    
    public List<Provider.Service> getServices(final String s, final String s2) {
        return new ServiceList(s, s2);
    }
    
    @Deprecated
    public List<Provider.Service> getServices(final String s, final List<String> list) {
        final ArrayList list2 = new ArrayList();
        final Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            list2.add(new ServiceId(s, iterator.next()));
        }
        return this.getServices(list2);
    }
    
    public List<Provider.Service> getServices(final List<ServiceId> list) {
        return new ServiceList(list);
    }
    
    static {
        debug = Debug.getInstance("jca", "ProviderList");
        PC0 = new ProviderConfig[0];
        P0 = new Provider[0];
        EMPTY = new ProviderList(ProviderList.PC0, true);
        EMPTY_PROVIDER = new Provider("##Empty##", 1.0, "initialization in progress") {
            private static final long serialVersionUID = 1151354171352296389L;
            
            @Override
            public Service getService(final String s, final String s2) {
                return null;
            }
        };
    }
    
    private final class ServiceList extends AbstractList<Provider.Service>
    {
        private final String type;
        private final String algorithm;
        private final List<ServiceId> ids;
        private Provider.Service firstService;
        private List<Provider.Service> services;
        private int providerIndex;
        
        ServiceList(final String type, final String algorithm) {
            this.type = type;
            this.algorithm = algorithm;
            this.ids = null;
        }
        
        ServiceList(final List<ServiceId> ids) {
            this.type = null;
            this.algorithm = null;
            this.ids = ids;
        }
        
        private void addService(final Provider.Service firstService) {
            if (this.firstService == null) {
                this.firstService = firstService;
            }
            else {
                if (this.services == null) {
                    (this.services = new ArrayList<Provider.Service>(4)).add(this.firstService);
                }
                this.services.add(firstService);
            }
        }
        
        private Provider.Service tryGet(final int n) {
            while (n != 0 || this.firstService == null) {
                if (this.services != null && this.services.size() > n) {
                    return this.services.get(n);
                }
                if (this.providerIndex >= ProviderList.this.configs.length) {
                    return null;
                }
                final Provider provider = ProviderList.this.getProvider(this.providerIndex++);
                if (this.type != null) {
                    final Provider.Service service = provider.getService(this.type, this.algorithm);
                    if (service == null) {
                        continue;
                    }
                    this.addService(service);
                }
                else {
                    for (final ServiceId serviceId : this.ids) {
                        final Provider.Service service2 = provider.getService(serviceId.type, serviceId.algorithm);
                        if (service2 != null) {
                            this.addService(service2);
                        }
                    }
                }
            }
            return this.firstService;
        }
        
        @Override
        public Provider.Service get(final int n) {
            final Provider.Service tryGet = this.tryGet(n);
            if (tryGet == null) {
                throw new IndexOutOfBoundsException();
            }
            return tryGet;
        }
        
        @Override
        public int size() {
            int size;
            if (this.services != null) {
                size = this.services.size();
            }
            else {
                size = ((this.firstService != null) ? 1 : 0);
            }
            while (this.tryGet(size) != null) {
                ++size;
            }
            return size;
        }
        
        @Override
        public boolean isEmpty() {
            return this.tryGet(0) == null;
        }
        
        @Override
        public Iterator<Provider.Service> iterator() {
            return new Iterator<Provider.Service>() {
                int index;
                
                @Override
                public boolean hasNext() {
                    return ServiceList.this.tryGet(this.index) != null;
                }
                
                @Override
                public Provider.Service next() {
                    final Provider.Service access$200 = ServiceList.this.tryGet(this.index);
                    if (access$200 == null) {
                        throw new NoSuchElementException();
                    }
                    ++this.index;
                    return access$200;
                }
                
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
}
