package java.lang;

import java.util.WeakHashMap;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ClassValue<T>
{
    private static final Entry<?>[] EMPTY_CACHE;
    final int hashCodeForCache;
    private static final AtomicInteger nextHashCode;
    private static final int HASH_INCREMENT = 1640531527;
    static final int HASH_MASK = 1073741823;
    final Identity identity;
    private volatile Version<T> version;
    private static final Object CRITICAL_SECTION;
    
    protected ClassValue() {
        this.hashCodeForCache = (ClassValue.nextHashCode.getAndAdd(1640531527) & 0x3FFFFFFF);
        this.identity = new Identity();
        this.version = new Version<T>(this);
    }
    
    protected abstract T computeValue(final Class<?> p0);
    
    public T get(final Class<?> clazz) {
        final Entry<?>[] cacheCarefully;
        final Entry<T> probeHomeLocation = ClassValueMap.probeHomeLocation(cacheCarefully = getCacheCarefully(clazz), this);
        if (this.match(probeHomeLocation)) {
            return probeHomeLocation.value();
        }
        return this.getFromBackup(cacheCarefully, clazz);
    }
    
    public void remove(final Class<?> clazz) {
        getMap(clazz).removeEntry(this);
    }
    
    void put(final Class<?> clazz, final T t) {
        getMap(clazz).changeEntry(this, t);
    }
    
    private static Entry<?>[] getCacheCarefully(final Class<?> clazz) {
        final ClassValueMap classValueMap = clazz.classValueMap;
        if (classValueMap == null) {
            return ClassValue.EMPTY_CACHE;
        }
        return classValueMap.getCache();
    }
    
    private T getFromBackup(final Entry<?>[] array, final Class<?> clazz) {
        final Entry<T> probeBackupLocations = ClassValueMap.probeBackupLocations(array, this);
        if (probeBackupLocations != null) {
            return probeBackupLocations.value();
        }
        return this.getFromHashMap(clazz);
    }
    
    Entry<T> castEntry(final Entry<?> entry) {
        return (Entry<T>)entry;
    }
    
    private T getFromHashMap(final Class<?> clazz) {
        final ClassValueMap map = getMap(clazz);
        while (true) {
            Entry<Object> entry = map.startEntry((ClassValue<Object>)this);
            if (!entry.isPromise()) {
                return entry.value();
            }
            Entry<T> finishEntry;
            try {
                entry = makeEntry(entry.version(), this.computeValue(clazz));
            }
            finally {
                finishEntry = map.finishEntry(this, (Entry<T>)entry);
            }
            if (finishEntry != null) {
                return finishEntry.value();
            }
        }
    }
    
    boolean match(final Entry<?> entry) {
        return entry != null && entry.get() == this.version;
    }
    
    Version<T> version() {
        return this.version;
    }
    
    void bumpVersion() {
        this.version = new Version<T>(this);
    }
    
    private static ClassValueMap getMap(final Class<?> clazz) {
        final ClassValueMap classValueMap = clazz.classValueMap;
        if (classValueMap != null) {
            return classValueMap;
        }
        return initializeMap(clazz);
    }
    
    private static ClassValueMap initializeMap(final Class<?> clazz) {
        ClassValueMap classValueMap;
        synchronized (ClassValue.CRITICAL_SECTION) {
            if ((classValueMap = clazz.classValueMap) == null) {
                classValueMap = (clazz.classValueMap = new ClassValueMap(clazz));
            }
        }
        return classValueMap;
    }
    
    static <T> Entry<T> makeEntry(final Version<T> version, final T t) {
        return new Entry<T>(version, t);
    }
    
    static {
        EMPTY_CACHE = new Entry[] { null };
        nextHashCode = new AtomicInteger();
        CRITICAL_SECTION = new Object();
    }
    
    static class Identity
    {
    }
    
    static class Version<T>
    {
        private final ClassValue<T> classValue;
        private final Entry<T> promise;
        
        Version(final ClassValue<T> classValue) {
            this.promise = new Entry<T>(this);
            this.classValue = classValue;
        }
        
        ClassValue<T> classValue() {
            return this.classValue;
        }
        
        Entry<T> promise() {
            return this.promise;
        }
        
        boolean isLive() {
            return this.classValue.version() == this;
        }
    }
    
    static class Entry<T> extends WeakReference<Version<T>>
    {
        final Object value;
        static final Entry<?> DEAD_ENTRY;
        
        Entry(final Version<T> version, final T value) {
            super(version);
            this.value = value;
        }
        
        private void assertNotPromise() {
            assert !this.isPromise();
        }
        
        Entry(final Version<T> version) {
            super(version);
            this.value = this;
        }
        
        T value() {
            this.assertNotPromise();
            return (T)this.value;
        }
        
        boolean isPromise() {
            return this.value == this;
        }
        
        Version<T> version() {
            return this.get();
        }
        
        ClassValue<T> classValueOrNull() {
            final Version<T> version = this.version();
            return (version == null) ? null : version.classValue();
        }
        
        boolean isLive() {
            final Version<T> version = this.version();
            if (version == null) {
                return false;
            }
            if (version.isLive()) {
                return true;
            }
            this.clear();
            return false;
        }
        
        Entry<T> refreshVersion(final Version<T> version) {
            this.assertNotPromise();
            final Entry entry = new Entry((Version<Object>)version, this.value);
            this.clear();
            return entry;
        }
        
        static {
            DEAD_ENTRY = new Entry<Object>(null, (Object)null);
        }
    }
    
    static class ClassValueMap extends WeakHashMap<Identity, ClassValue.Entry<?>>
    {
        private final Class<?> type;
        private ClassValue.Entry<?>[] cacheArray;
        private int cacheLoad;
        private int cacheLoadLimit;
        private static final int INITIAL_ENTRIES = 32;
        private static final int CACHE_LOAD_LIMIT = 67;
        private static final int PROBE_LIMIT = 6;
        
        ClassValueMap(final Class<?> type) {
            this.type = type;
            this.sizeCache(32);
        }
        
        ClassValue.Entry<?>[] getCache() {
            return this.cacheArray;
        }
        
        synchronized <T> ClassValue.Entry<T> startEntry(final ClassValue<T> classValue) {
            ClassValue.Entry entry = ((WeakHashMap<K, ClassValue.Entry>)this).get(classValue.identity);
            final Version<T> version = classValue.version();
            if (entry == null) {
                final ClassValue.Entry<T> promise = version.promise();
                this.put(classValue.identity, promise);
                return promise;
            }
            if (entry.isPromise()) {
                if (entry.version() != version) {
                    entry = version.promise();
                    ((WeakHashMap<Identity, ClassValue.Entry>)this).put(classValue.identity, entry);
                }
                return entry;
            }
            if (entry.version() != version) {
                entry = entry.refreshVersion(version);
                ((WeakHashMap<Identity, ClassValue.Entry>)this).put(classValue.identity, entry);
            }
            this.checkCacheLoad();
            this.addToCache(classValue, entry);
            return entry;
        }
        
        synchronized <T> ClassValue.Entry<T> finishEntry(final ClassValue<T> classValue, ClassValue.Entry<T> refreshVersion) {
            final ClassValue.Entry entry = ((WeakHashMap<K, ClassValue.Entry>)this).get(classValue.identity);
            if (refreshVersion == entry) {
                assert refreshVersion.isPromise();
                this.remove(classValue.identity);
                return null;
            }
            else {
                if (entry != null && entry.isPromise() && entry.version() == refreshVersion.version()) {
                    final Version<T> version = classValue.version();
                    if (refreshVersion.version() != version) {
                        refreshVersion = refreshVersion.refreshVersion(version);
                    }
                    ((WeakHashMap<Identity, ClassValue.Entry>)this).put(classValue.identity, refreshVersion);
                    this.checkCacheLoad();
                    this.addToCache(classValue, refreshVersion);
                    return refreshVersion;
                }
                return null;
            }
        }
        
        synchronized void removeEntry(final ClassValue<?> classValue) {
            final ClassValue.Entry entry = ((WeakHashMap<K, ClassValue.Entry>)this).remove(classValue.identity);
            if (entry != null) {
                if (entry.isPromise()) {
                    ((WeakHashMap<Identity, ClassValue.Entry>)this).put(classValue.identity, entry);
                }
                else {
                    classValue.bumpVersion();
                    this.removeStaleEntries(classValue);
                }
            }
        }
        
        synchronized <T> void changeEntry(final ClassValue<T> classValue, final T t) {
            final ClassValue.Entry entry = ((WeakHashMap<K, ClassValue.Entry>)this).get(classValue.identity);
            final Version<Object> version = (Version<Object>)classValue.version();
            if (entry != null) {
                if (entry.version() == version && entry.value() == t) {
                    return;
                }
                classValue.bumpVersion();
                this.removeStaleEntries(classValue);
            }
            final ClassValue.Entry<T> entry2 = ClassValue.makeEntry((Version<T>)version, t);
            this.put(classValue.identity, entry2);
            this.checkCacheLoad();
            this.addToCache(classValue, entry2);
        }
        
        static ClassValue.Entry<?> loadFromCache(final ClassValue.Entry<?>[] array, final int n) {
            return array[n & array.length - 1];
        }
        
        static <T> ClassValue.Entry<T> probeHomeLocation(final ClassValue.Entry<?>[] array, final ClassValue<T> classValue) {
            return classValue.castEntry(loadFromCache(array, classValue.hashCodeForCache));
        }
        
        static <T> ClassValue.Entry<T> probeBackupLocations(final ClassValue.Entry<?>[] array, final ClassValue<T> classValue) {
            final int n = array.length - 1;
            final int n2 = classValue.hashCodeForCache & n;
            final ClassValue.Entry<?> entry = array[n2];
            if (entry == null) {
                return null;
            }
            int n3 = -1;
            for (int i = n2 + 1; i < n2 + 6; ++i) {
                final ClassValue.Entry<?> entry2 = array[i & n];
                if (entry2 == null) {
                    break;
                }
                if (classValue.match(entry2)) {
                    array[n2] = entry2;
                    if (n3 >= 0) {
                        array[i & n] = ClassValue.Entry.DEAD_ENTRY;
                    }
                    else {
                        n3 = i;
                    }
                    array[n3 & n] = ((entryDislocation(array, n3, entry) < 6) ? entry : ClassValue.Entry.DEAD_ENTRY);
                    return (ClassValue.Entry<T>)classValue.castEntry(entry2);
                }
                if (!entry2.isLive() && n3 < 0) {
                    n3 = i;
                }
            }
            return null;
        }
        
        private static int entryDislocation(final ClassValue.Entry<?>[] array, final int n, final ClassValue.Entry<?> entry) {
            final ClassValue<?> classValueOrNull = entry.classValueOrNull();
            if (classValueOrNull == null) {
                return 0;
            }
            return n - classValueOrNull.hashCodeForCache & array.length - 1;
        }
        
        private void sizeCache(final int n) {
            assert (n & n - 1) == 0x0;
            this.cacheLoad = 0;
            this.cacheLoadLimit = (int)(n * 67.0 / 100.0);
            this.cacheArray = new ClassValue.Entry[n];
        }
        
        private void checkCacheLoad() {
            if (this.cacheLoad >= this.cacheLoadLimit) {
                this.reduceCacheLoad();
            }
        }
        
        private void reduceCacheLoad() {
            this.removeStaleEntries();
            if (this.cacheLoad < this.cacheLoadLimit) {
                return;
            }
            final ClassValue.Entry<?>[] cache = this.getCache();
            if (cache.length > 1073741823) {
                return;
            }
            this.sizeCache(cache.length * 2);
            for (final ClassValue.Entry<?> entry : cache) {
                if (entry != null && entry.isLive()) {
                    this.addToCache(entry);
                }
            }
        }
        
        private void removeStaleEntries(final ClassValue.Entry<?>[] array, final int n, final int n2) {
            final int n3 = array.length - 1;
            int n4 = 0;
            for (int i = n; i < n + n2; ++i) {
                final ClassValue.Entry<?> entry = array[i & n3];
                if (entry != null) {
                    if (!entry.isLive()) {
                        if ((array[i & n3] = this.findReplacement(array, i)) == null) {
                            ++n4;
                        }
                    }
                }
            }
            this.cacheLoad = Math.max(0, this.cacheLoad - n4);
        }
        
        private ClassValue.Entry<?> findReplacement(final ClassValue.Entry<?>[] array, final int n) {
            ClassValue.Entry<?> entry = null;
            int n2 = -1;
            int n3 = 0;
            final int n4 = array.length - 1;
            for (int i = n + 1; i < n + 6; ++i) {
                final ClassValue.Entry<?> entry2 = array[i & n4];
                if (entry2 == null) {
                    break;
                }
                if (entry2.isLive()) {
                    final int entryDislocation = entryDislocation(array, i, entry2);
                    if (entryDislocation != 0) {
                        final int n5 = i - entryDislocation;
                        if (n5 <= n) {
                            if (n5 == n) {
                                n2 = 1;
                                n3 = i;
                                entry = entry2;
                            }
                            else if (n2 <= 0) {
                                n2 = 0;
                                n3 = i;
                                entry = entry2;
                            }
                        }
                    }
                }
            }
            if (n2 >= 0) {
                if (array[n3 + 1 & n4] != null) {
                    array[n3 & n4] = ClassValue.Entry.DEAD_ENTRY;
                }
                else {
                    array[n3 & n4] = null;
                    --this.cacheLoad;
                }
            }
            return entry;
        }
        
        private void removeStaleEntries(final ClassValue<?> classValue) {
            this.removeStaleEntries(this.getCache(), classValue.hashCodeForCache, 6);
        }
        
        private void removeStaleEntries() {
            final ClassValue.Entry<?>[] cache = this.getCache();
            this.removeStaleEntries(cache, 0, cache.length + 6 - 1);
        }
        
        private <T> void addToCache(final ClassValue.Entry<T> entry) {
            final ClassValue<T> classValueOrNull = entry.classValueOrNull();
            if (classValueOrNull != null) {
                this.addToCache(classValueOrNull, entry);
            }
        }
        
        private <T> void addToCache(final ClassValue<T> classValue, final ClassValue.Entry<T> entry) {
            final ClassValue.Entry<?>[] cache = this.getCache();
            final int n = cache.length - 1;
            final int n2 = classValue.hashCodeForCache & n;
            final ClassValue.Entry<?> placeInCache = this.placeInCache(cache, n2, entry, false);
            if (placeInCache == null) {
                return;
            }
            int i = 0;
            while (i < (i = n2 - entryDislocation(cache, n2, placeInCache)) + 6) {
                if (this.placeInCache(cache, i & n, placeInCache, true) == null) {
                    return;
                }
                ++i;
            }
        }
        
        private ClassValue.Entry<?> placeInCache(final ClassValue.Entry<?>[] array, final int n, final ClassValue.Entry<?> entry, final boolean b) {
            final ClassValue.Entry<?> overwrittenEntry = this.overwrittenEntry(array[n]);
            if (b && overwrittenEntry != null) {
                return entry;
            }
            array[n] = entry;
            return overwrittenEntry;
        }
        
        private <T> ClassValue.Entry<T> overwrittenEntry(final ClassValue.Entry<T> entry) {
            if (entry == null) {
                ++this.cacheLoad;
            }
            else if (entry.isLive()) {
                return entry;
            }
            return null;
        }
    }
}
