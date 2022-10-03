package java.lang.ref;

import java.util.Map;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.HashMap;

final class FinalizerHistogram
{
    static Entry[] getFinalizerHistogram() {
        final HashMap hashMap = new HashMap();
        Finalizer.getQueue().forEach(reference -> {
            reference.get();
            final Object o;
            if (o != null) {
                map.computeIfAbsent(o.getClass().getName(), Entry::new).increment();
            }
            return;
        });
        final Entry[] array = (Entry[])hashMap.values().toArray(new Entry[hashMap.size()]);
        Arrays.sort(array, Comparator.comparingInt(Entry::getInstanceCount).reversed());
        return array;
    }
    
    private static final class Entry
    {
        private int instanceCount;
        private final String className;
        
        int getInstanceCount() {
            return this.instanceCount;
        }
        
        void increment() {
            ++this.instanceCount;
        }
        
        Entry(final String className) {
            this.className = className;
        }
    }
}
