package sun.management;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.TreeMap;
import sun.management.counter.Counter;
import java.util.Map;
import sun.management.counter.StringCounter;
import sun.management.counter.LongCounter;

class HotspotCompilation implements HotspotCompilationMBean
{
    private VMManagement jvm;
    private static final String JAVA_CI = "java.ci.";
    private static final String COM_SUN_CI = "com.sun.ci.";
    private static final String SUN_CI = "sun.ci.";
    private static final String CI_COUNTER_NAME_PATTERN = "java.ci.|com.sun.ci.|sun.ci.";
    private LongCounter compilerThreads;
    private LongCounter totalCompiles;
    private LongCounter totalBailouts;
    private LongCounter totalInvalidates;
    private LongCounter nmethodCodeSize;
    private LongCounter nmethodSize;
    private StringCounter lastMethod;
    private LongCounter lastSize;
    private LongCounter lastType;
    private StringCounter lastFailedMethod;
    private LongCounter lastFailedType;
    private StringCounter lastInvalidatedMethod;
    private LongCounter lastInvalidatedType;
    private CompilerThreadInfo[] threads;
    private int numActiveThreads;
    private Map<String, Counter> counters;
    
    HotspotCompilation(final VMManagement jvm) {
        this.jvm = jvm;
        this.initCompilerCounters();
    }
    
    private Counter lookup(final String s) {
        final Counter counter;
        if ((counter = this.counters.get("sun.ci." + s)) != null) {
            return counter;
        }
        final Counter counter2;
        if ((counter2 = this.counters.get("com.sun.ci." + s)) != null) {
            return counter2;
        }
        final Counter counter3;
        if ((counter3 = this.counters.get("java.ci." + s)) != null) {
            return counter3;
        }
        throw new AssertionError((Object)("Counter " + s + " does not exist"));
    }
    
    private void initCompilerCounters() {
        this.counters = new TreeMap<String, Counter>();
        for (final Counter counter : this.getInternalCompilerCounters()) {
            this.counters.put(counter.getName(), counter);
        }
        this.compilerThreads = (LongCounter)this.lookup("threads");
        this.totalCompiles = (LongCounter)this.lookup("totalCompiles");
        this.totalBailouts = (LongCounter)this.lookup("totalBailouts");
        this.totalInvalidates = (LongCounter)this.lookup("totalInvalidates");
        this.nmethodCodeSize = (LongCounter)this.lookup("nmethodCodeSize");
        this.nmethodSize = (LongCounter)this.lookup("nmethodSize");
        this.lastMethod = (StringCounter)this.lookup("lastMethod");
        this.lastSize = (LongCounter)this.lookup("lastSize");
        this.lastType = (LongCounter)this.lookup("lastType");
        this.lastFailedMethod = (StringCounter)this.lookup("lastFailedMethod");
        this.lastFailedType = (LongCounter)this.lookup("lastFailedType");
        this.lastInvalidatedMethod = (StringCounter)this.lookup("lastInvalidatedMethod");
        this.lastInvalidatedType = (LongCounter)this.lookup("lastInvalidatedType");
        this.numActiveThreads = (int)this.compilerThreads.longValue();
        this.threads = new CompilerThreadInfo[this.numActiveThreads + 1];
        if (this.counters.containsKey("sun.ci.adapterThread.compiles")) {
            this.threads[0] = new CompilerThreadInfo("adapterThread", 0);
            ++this.numActiveThreads;
        }
        else {
            this.threads[0] = null;
        }
        for (int i = 1; i < this.threads.length; ++i) {
            this.threads[i] = new CompilerThreadInfo("compilerThread", i - 1);
        }
    }
    
    @Override
    public int getCompilerThreadCount() {
        return this.numActiveThreads;
    }
    
    @Override
    public long getTotalCompileCount() {
        return this.totalCompiles.longValue();
    }
    
    @Override
    public long getBailoutCompileCount() {
        return this.totalBailouts.longValue();
    }
    
    @Override
    public long getInvalidatedCompileCount() {
        return this.totalInvalidates.longValue();
    }
    
    @Override
    public long getCompiledMethodCodeSize() {
        return this.nmethodCodeSize.longValue();
    }
    
    @Override
    public long getCompiledMethodSize() {
        return this.nmethodSize.longValue();
    }
    
    @Override
    public List<CompilerThreadStat> getCompilerThreadStats() {
        final ArrayList list = new ArrayList(this.threads.length);
        int i = 0;
        if (this.threads[0] == null) {
            i = 1;
        }
        while (i < this.threads.length) {
            list.add(this.threads[i].getCompilerThreadStat());
            ++i;
        }
        return list;
    }
    
    @Override
    public MethodInfo getLastCompile() {
        return new MethodInfo(this.lastMethod.stringValue(), (int)this.lastType.longValue(), (int)this.lastSize.longValue());
    }
    
    @Override
    public MethodInfo getFailedCompile() {
        return new MethodInfo(this.lastFailedMethod.stringValue(), (int)this.lastFailedType.longValue(), -1);
    }
    
    @Override
    public MethodInfo getInvalidatedCompile() {
        return new MethodInfo(this.lastInvalidatedMethod.stringValue(), (int)this.lastInvalidatedType.longValue(), -1);
    }
    
    @Override
    public List<Counter> getInternalCompilerCounters() {
        return this.jvm.getInternalCounters("java.ci.|com.sun.ci.|sun.ci.");
    }
    
    private class CompilerThreadInfo
    {
        int index;
        String name;
        StringCounter method;
        LongCounter type;
        LongCounter compiles;
        LongCounter time;
        
        CompilerThreadInfo(final String s, final int n) {
            final String string = s + "." + n + ".";
            this.name = s + "-" + n;
            this.method = (StringCounter)HotspotCompilation.this.lookup(string + "method");
            this.type = (LongCounter)HotspotCompilation.this.lookup(string + "type");
            this.compiles = (LongCounter)HotspotCompilation.this.lookup(string + "compiles");
            this.time = (LongCounter)HotspotCompilation.this.lookup(string + "time");
        }
        
        CompilerThreadInfo(final String name) {
            final String string = name + ".";
            this.name = name;
            this.method = (StringCounter)HotspotCompilation.this.lookup(string + "method");
            this.type = (LongCounter)HotspotCompilation.this.lookup(string + "type");
            this.compiles = (LongCounter)HotspotCompilation.this.lookup(string + "compiles");
            this.time = (LongCounter)HotspotCompilation.this.lookup(string + "time");
        }
        
        CompilerThreadStat getCompilerThreadStat() {
            return new CompilerThreadStat(this.name, this.compiles.longValue(), this.time.longValue(), new MethodInfo(this.method.stringValue(), (int)this.type.longValue(), -1));
        }
    }
}
