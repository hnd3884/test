package org.tanukisoftware.wrapper.jmx;

import org.tanukisoftware.wrapper.WrapperManager;

public class WrapperManagerTesting implements WrapperManagerTestingMBean
{
    public void appearHung() {
        WrapperManager.appearHung();
    }
    
    public void accessViolationNative() {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000L);
                }
                catch (final InterruptedException ex) {}
                WrapperManager.accessViolationNative();
            }
        }.start();
    }
    
    public void stopImmediate(final int exitCode) {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000L);
                }
                catch (final InterruptedException ex) {}
                WrapperManager.stopImmediate(exitCode);
            }
        }.start();
    }
}
