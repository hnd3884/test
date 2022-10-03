package sun.misc;

public final class ThreadGroupUtils
{
    private ThreadGroupUtils() {
    }
    
    public static ThreadGroup getRootThreadGroup() {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        for (ThreadGroup threadGroup2 = threadGroup.getParent(); threadGroup2 != null; threadGroup2 = threadGroup.getParent()) {
            threadGroup = threadGroup2;
        }
        return threadGroup;
    }
}
