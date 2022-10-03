package com.sun.corba.se.impl.activation;

import java.lang.reflect.Method;
import org.omg.CORBA.ORB;
import com.sun.corba.se.spi.activation._ServerImplBase;

class ServerCallback extends _ServerImplBase
{
    private ORB orb;
    private transient Method installMethod;
    private transient Method uninstallMethod;
    private transient Method shutdownMethod;
    private Object[] methodArgs;
    
    ServerCallback(final ORB orb, final Method installMethod, final Method uninstallMethod, final Method shutdownMethod) {
        this.orb = orb;
        this.installMethod = installMethod;
        this.uninstallMethod = uninstallMethod;
        this.shutdownMethod = shutdownMethod;
        orb.connect(this);
        this.methodArgs = new Object[] { orb };
    }
    
    private void invokeMethod(final Method method) {
        if (method != null) {
            try {
                method.invoke(null, this.methodArgs);
            }
            catch (final Exception ex) {
                ServerMain.logError("could not invoke " + method.getName() + " method: " + ex.getMessage());
            }
        }
    }
    
    @Override
    public void shutdown() {
        ServerMain.logInformation("Shutdown starting");
        this.invokeMethod(this.shutdownMethod);
        this.orb.shutdown(true);
        ServerMain.logTerminal("Shutdown completed", 0);
    }
    
    @Override
    public void install() {
        ServerMain.logInformation("Install starting");
        this.invokeMethod(this.installMethod);
        ServerMain.logInformation("Install completed");
    }
    
    @Override
    public void uninstall() {
        ServerMain.logInformation("uninstall starting");
        this.invokeMethod(this.uninstallMethod);
        ServerMain.logInformation("uninstall completed");
    }
}
