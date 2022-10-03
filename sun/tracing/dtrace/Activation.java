package sun.tracing.dtrace;

import java.security.Permission;

class Activation
{
    private SystemResource resource;
    private int referenceCount;
    
    Activation(final String s, final DTraceProvider[] array) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new RuntimePermission("com.sun.tracing.dtrace.createProvider"));
        }
        this.referenceCount = array.length;
        for (int length = array.length, i = 0; i < length; ++i) {
            array[i].setActivation(this);
        }
        this.resource = new SystemResource(this, JVM.activate(s, array));
    }
    
    void disposeProvider(final DTraceProvider dTraceProvider) {
        final int referenceCount = this.referenceCount - 1;
        this.referenceCount = referenceCount;
        if (referenceCount == 0) {
            this.resource.dispose();
        }
    }
}
