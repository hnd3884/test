package com.zoho.clustering.failover;

public class FOSAgentShutdownHook extends Thread
{
    private String fosServiceName;
    
    public FOSAgentShutdownHook(final String fosServiceName) {
        super("FOS-ShutdownHook");
        this.fosServiceName = fosServiceName;
    }
    
    @Override
    public void run() {
        this.stopService(this.fosServiceName);
    }
    
    public int stopService(final String serviceName) {
        final ProcessBuilder pb = new ProcessBuilder(new String[] { "net", "stop", serviceName });
        pb.redirectErrorStream(true);
        try {
            final Process proc = pb.start();
            final int status = proc.waitFor();
            return status;
        }
        catch (final Exception exp) {
            throw new RuntimeException("Error while stopping the FOS service.", exp);
        }
    }
}
