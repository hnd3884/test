package com.unboundid.util;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.io.Serializable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ResultCodeCounter implements Serializable
{
    private static final long serialVersionUID = -2280620218815022241L;
    private final AtomicReference<ConcurrentHashMap<ResultCode, AtomicLong>> rcMap;
    
    public ResultCodeCounter() {
        (this.rcMap = new AtomicReference<ConcurrentHashMap<ResultCode, AtomicLong>>()).set(new ConcurrentHashMap<ResultCode, AtomicLong>(StaticUtils.computeMapCapacity(ResultCode.values().length)));
    }
    
    public void increment(final ResultCode resultCode) {
        this.increment(resultCode, 1);
    }
    
    public void increment(final ResultCode resultCode, final int amount) {
        final ConcurrentHashMap<ResultCode, AtomicLong> m = this.rcMap.get();
        AtomicLong l = m.get(resultCode);
        if (l == null) {
            l = new AtomicLong(0L);
            final AtomicLong l2 = m.putIfAbsent(resultCode, l);
            if (l2 != null) {
                l = l2;
            }
        }
        l.addAndGet(amount);
    }
    
    public void reset() {
        this.rcMap.set(new ConcurrentHashMap<ResultCode, AtomicLong>(StaticUtils.computeMapCapacity(ResultCode.values().length)));
    }
    
    public List<ObjectPair<ResultCode, Long>> getCounts(final boolean reset) {
        ConcurrentHashMap<ResultCode, AtomicLong> m;
        if (reset) {
            m = this.rcMap.getAndSet(new ConcurrentHashMap<ResultCode, AtomicLong>(StaticUtils.computeMapCapacity(ResultCode.values().length)));
        }
        else {
            m = new ConcurrentHashMap<ResultCode, AtomicLong>(this.rcMap.get());
        }
        if (m.isEmpty()) {
            return Collections.emptyList();
        }
        final TreeMap<Long, TreeMap<Integer, ResultCode>> sortedMap = new TreeMap<Long, TreeMap<Integer, ResultCode>>(new ReverseComparator<Object>());
        for (final Map.Entry<ResultCode, AtomicLong> e : m.entrySet()) {
            final long l = e.getValue().longValue();
            TreeMap<Integer, ResultCode> rcByValue = sortedMap.get(l);
            if (rcByValue == null) {
                rcByValue = new TreeMap<Integer, ResultCode>();
                sortedMap.put(l, rcByValue);
            }
            final ResultCode rc = e.getKey();
            rcByValue.put(rc.intValue(), rc);
        }
        final ArrayList<ObjectPair<ResultCode, Long>> rcCounts = new ArrayList<ObjectPair<ResultCode, Long>>(2 * sortedMap.size());
        for (final Map.Entry<Long, TreeMap<Integer, ResultCode>> e2 : sortedMap.entrySet()) {
            final long count = e2.getKey();
            for (final ResultCode rc2 : e2.getValue().values()) {
                rcCounts.add(new ObjectPair<ResultCode, Long>(rc2, count));
            }
        }
        return Collections.unmodifiableList((List<? extends ObjectPair<ResultCode, Long>>)rcCounts);
    }
}
