package sun.management;

import java.io.Serializable;

public class CompilerThreadStat implements Serializable
{
    private String name;
    private long taskCount;
    private long compileTime;
    private MethodInfo lastMethod;
    private static final long serialVersionUID = 6992337162326171013L;
    
    CompilerThreadStat(final String name, final long taskCount, final long compileTime, final MethodInfo lastMethod) {
        this.name = name;
        this.taskCount = taskCount;
        this.compileTime = compileTime;
        this.lastMethod = lastMethod;
    }
    
    public String getName() {
        return this.name;
    }
    
    public long getCompileTaskCount() {
        return this.taskCount;
    }
    
    public long getCompileTime() {
        return this.compileTime;
    }
    
    public MethodInfo getLastCompiledMethodInfo() {
        return this.lastMethod;
    }
    
    @Override
    public String toString() {
        return this.getName() + " compileTasks = " + this.getCompileTaskCount() + " compileTime = " + this.getCompileTime();
    }
}
