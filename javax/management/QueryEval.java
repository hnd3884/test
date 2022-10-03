package javax.management;

import java.io.Serializable;

public abstract class QueryEval implements Serializable
{
    private static final long serialVersionUID = 2675899265640874796L;
    private static ThreadLocal<MBeanServer> server;
    
    public void setMBeanServer(final MBeanServer mBeanServer) {
        QueryEval.server.set(mBeanServer);
    }
    
    public static MBeanServer getMBeanServer() {
        return QueryEval.server.get();
    }
    
    static {
        QueryEval.server = new InheritableThreadLocal<MBeanServer>();
    }
}
