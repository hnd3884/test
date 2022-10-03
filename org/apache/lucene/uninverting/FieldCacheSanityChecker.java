package org.apache.lucene.uninverting;

import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.IndexReader;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import org.apache.lucene.util.MapOfSets;
import java.util.HashMap;

final class FieldCacheSanityChecker
{
    public FieldCacheSanityChecker() {
    }
    
    public static Insanity[] checkSanity(final FieldCache cache) {
        return checkSanity(cache.getCacheEntries());
    }
    
    public static Insanity[] checkSanity(final FieldCache.CacheEntry... cacheEntries) {
        final FieldCacheSanityChecker sanityChecker = new FieldCacheSanityChecker();
        return sanityChecker.check(cacheEntries);
    }
    
    public Insanity[] check(final FieldCache.CacheEntry... cacheEntries) {
        if (null == cacheEntries || 0 == cacheEntries.length) {
            return new Insanity[0];
        }
        final MapOfSets<Integer, FieldCache.CacheEntry> valIdToItems = (MapOfSets<Integer, FieldCache.CacheEntry>)new MapOfSets((Map)new HashMap(17));
        final MapOfSets<ReaderField, Integer> readerFieldToValIds = (MapOfSets<ReaderField, Integer>)new MapOfSets((Map)new HashMap(17));
        final Set<ReaderField> valMismatchKeys = new HashSet<ReaderField>();
        for (int i = 0; i < cacheEntries.length; ++i) {
            final FieldCache.CacheEntry item = cacheEntries[i];
            final Object val = item.getValue();
            if (!(val instanceof FieldCacheImpl.BitsEntry)) {
                if (!(val instanceof FieldCache.CreationPlaceholder)) {
                    final ReaderField rf = new ReaderField(item.getReaderKey(), item.getFieldName());
                    final Integer valId = System.identityHashCode(val);
                    valIdToItems.put((Object)valId, (Object)item);
                    if (1 < readerFieldToValIds.put((Object)rf, (Object)valId)) {
                        valMismatchKeys.add(rf);
                    }
                }
            }
        }
        final List<Insanity> insanity = new ArrayList<Insanity>(valMismatchKeys.size() * 3);
        insanity.addAll(this.checkValueMismatch(valIdToItems, readerFieldToValIds, valMismatchKeys));
        insanity.addAll(this.checkSubreaders(valIdToItems, readerFieldToValIds));
        return insanity.toArray(new Insanity[insanity.size()]);
    }
    
    private Collection<Insanity> checkValueMismatch(final MapOfSets<Integer, FieldCache.CacheEntry> valIdToItems, final MapOfSets<ReaderField, Integer> readerFieldToValIds, final Set<ReaderField> valMismatchKeys) {
        final List<Insanity> insanity = new ArrayList<Insanity>(valMismatchKeys.size() * 3);
        if (!valMismatchKeys.isEmpty()) {
            final Map<ReaderField, Set<Integer>> rfMap = readerFieldToValIds.getMap();
            final Map<Integer, Set<FieldCache.CacheEntry>> valMap = valIdToItems.getMap();
            for (final ReaderField rf : valMismatchKeys) {
                final List<FieldCache.CacheEntry> badEntries = new ArrayList<FieldCache.CacheEntry>(valMismatchKeys.size() * 2);
                for (final Integer value : rfMap.get(rf)) {
                    for (final FieldCache.CacheEntry cacheEntry : valMap.get(value)) {
                        badEntries.add(cacheEntry);
                    }
                }
                FieldCache.CacheEntry[] badness = new FieldCache.CacheEntry[badEntries.size()];
                badness = badEntries.toArray(badness);
                insanity.add(new Insanity(InsanityType.VALUEMISMATCH, "Multiple distinct value objects for " + rf.toString(), badness));
            }
        }
        return insanity;
    }
    
    private Collection<Insanity> checkSubreaders(final MapOfSets<Integer, FieldCache.CacheEntry> valIdToItems, final MapOfSets<ReaderField, Integer> readerFieldToValIds) {
        final List<Insanity> insanity = new ArrayList<Insanity>(23);
        final Map<ReaderField, Set<ReaderField>> badChildren = new HashMap<ReaderField, Set<ReaderField>>(17);
        final MapOfSets<ReaderField, ReaderField> badKids = (MapOfSets<ReaderField, ReaderField>)new MapOfSets((Map)badChildren);
        final Map<Integer, Set<FieldCache.CacheEntry>> viToItemSets = valIdToItems.getMap();
        final Map<ReaderField, Set<Integer>> rfToValIdSets = readerFieldToValIds.getMap();
        final Set<ReaderField> seen = new HashSet<ReaderField>(17);
        final Set<ReaderField> readerFields = rfToValIdSets.keySet();
        for (final ReaderField rf : readerFields) {
            if (seen.contains(rf)) {
                continue;
            }
            final List<Object> kids = this.getAllDescendantReaderKeys(rf.readerKey);
            for (final Object kidKey : kids) {
                final ReaderField kid = new ReaderField(kidKey, rf.fieldName);
                if (badChildren.containsKey(kid)) {
                    badKids.put((Object)rf, (Object)kid);
                    badKids.putAll((Object)rf, (Collection)badChildren.get(kid));
                    badChildren.remove(kid);
                }
                else if (rfToValIdSets.containsKey(kid)) {
                    badKids.put((Object)rf, (Object)kid);
                }
                seen.add(kid);
            }
            seen.add(rf);
        }
        for (final ReaderField parent : badChildren.keySet()) {
            final Set<ReaderField> kids2 = badChildren.get(parent);
            final List<FieldCache.CacheEntry> badEntries = new ArrayList<FieldCache.CacheEntry>(kids2.size() * 2);
            for (final Integer value : rfToValIdSets.get(parent)) {
                badEntries.addAll(viToItemSets.get(value));
            }
            for (final ReaderField kid : kids2) {
                for (final Integer value2 : rfToValIdSets.get(kid)) {
                    badEntries.addAll(viToItemSets.get(value2));
                }
            }
            FieldCache.CacheEntry[] badness = new FieldCache.CacheEntry[badEntries.size()];
            badness = badEntries.toArray(badness);
            insanity.add(new Insanity(InsanityType.SUBREADER, "Found caches for descendants of " + parent.toString(), badness));
        }
        return insanity;
    }
    
    private List<Object> getAllDescendantReaderKeys(final Object seed) {
        final List<Object> all = new ArrayList<Object>(17);
        all.add(seed);
        for (int i = 0; i < all.size(); ++i) {
            final Object obj = all.get(i);
            if (obj instanceof IndexReader) {
                try {
                    final List<IndexReaderContext> childs = ((IndexReader)obj).getContext().children();
                    if (childs != null) {
                        for (final IndexReaderContext ctx : childs) {
                            all.add(ctx.reader().getCoreCacheKey());
                        }
                    }
                }
                catch (final AlreadyClosedException ex) {}
            }
        }
        return all.subList(1, all.size());
    }
    
    private static final class ReaderField
    {
        public final Object readerKey;
        public final String fieldName;
        
        public ReaderField(final Object readerKey, final String fieldName) {
            this.readerKey = readerKey;
            this.fieldName = fieldName;
        }
        
        @Override
        public int hashCode() {
            return System.identityHashCode(this.readerKey) * this.fieldName.hashCode();
        }
        
        @Override
        public boolean equals(final Object that) {
            if (!(that instanceof ReaderField)) {
                return false;
            }
            final ReaderField other = (ReaderField)that;
            return this.readerKey == other.readerKey && this.fieldName.equals(other.fieldName);
        }
        
        @Override
        public String toString() {
            return this.readerKey.toString() + "+" + this.fieldName;
        }
    }
    
    public static final class Insanity
    {
        private final InsanityType type;
        private final String msg;
        private final FieldCache.CacheEntry[] entries;
        
        public Insanity(final InsanityType type, final String msg, final FieldCache.CacheEntry... entries) {
            if (null == type) {
                throw new IllegalArgumentException("Insanity requires non-null InsanityType");
            }
            if (null == entries || 0 == entries.length) {
                throw new IllegalArgumentException("Insanity requires non-null/non-empty CacheEntry[]");
            }
            this.type = type;
            this.msg = msg;
            this.entries = entries;
        }
        
        public InsanityType getType() {
            return this.type;
        }
        
        public String getMsg() {
            return this.msg;
        }
        
        public FieldCache.CacheEntry[] getCacheEntries() {
            return this.entries;
        }
        
        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder();
            buf.append(this.getType()).append(": ");
            final String m = this.getMsg();
            if (null != m) {
                buf.append(m);
            }
            buf.append('\n');
            final FieldCache.CacheEntry[] ce = this.getCacheEntries();
            for (int i = 0; i < ce.length; ++i) {
                buf.append('\t').append(ce[i].toString()).append('\n');
            }
            return buf.toString();
        }
    }
    
    public static final class InsanityType
    {
        private final String label;
        public static final InsanityType SUBREADER;
        public static final InsanityType VALUEMISMATCH;
        public static final InsanityType EXPECTED;
        
        private InsanityType(final String label) {
            this.label = label;
        }
        
        @Override
        public String toString() {
            return this.label;
        }
        
        static {
            SUBREADER = new InsanityType("SUBREADER");
            VALUEMISMATCH = new InsanityType("VALUEMISMATCH");
            EXPECTED = new InsanityType("EXPECTED");
        }
    }
}
