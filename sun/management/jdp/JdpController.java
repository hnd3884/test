package sun.management.jdp;

import java.io.IOException;
import java.util.UUID;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.management.RuntimeMXBean;
import sun.management.VMManagement;
import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;
import java.net.InetAddress;

public final class JdpController
{
    private static JDPControllerRunner controller;
    
    private JdpController() {
    }
    
    private static int getInteger(final String s, final int n, final String s2) throws JdpException {
        try {
            return (s == null) ? n : Integer.parseInt(s);
        }
        catch (final NumberFormatException ex) {
            throw new JdpException(s2);
        }
    }
    
    private static InetAddress getInetAddress(final String s, final InetAddress inetAddress, final String s2) throws JdpException {
        try {
            return (s == null) ? inetAddress : InetAddress.getByName(s);
        }
        catch (final UnknownHostException ex) {
            throw new JdpException(s2);
        }
    }
    
    private static Integer getProcessId() {
        try {
            final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            final Field declaredField = runtimeMXBean.getClass().getDeclaredField("jvm");
            declaredField.setAccessible(true);
            final VMManagement vmManagement = (VMManagement)declaredField.get(runtimeMXBean);
            final Method declaredMethod = vmManagement.getClass().getDeclaredMethod("getProcessId", (Class<?>[])new Class[0]);
            declaredMethod.setAccessible(true);
            return (Integer)declaredMethod.invoke(vmManagement, new Object[0]);
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public static synchronized void startDiscoveryService(final InetAddress inetAddress, final int n, final String instanceName, final String s) throws IOException, JdpException {
        final int integer = getInteger(System.getProperty("com.sun.management.jdp.ttl"), 1, "Invalid jdp packet ttl");
        final int n2 = getInteger(System.getProperty("com.sun.management.jdp.pause"), 5, "Invalid jdp pause") * 1000;
        final InetAddress inetAddress2 = getInetAddress(System.getProperty("com.sun.management.jdp.source_addr"), null, "Invalid source address provided");
        final JdpJmxPacket jdpJmxPacket = new JdpJmxPacket(UUID.randomUUID(), s);
        final String property = System.getProperty("sun.java.command");
        if (property != null) {
            jdpJmxPacket.setMainClass(property.split(" ", 2)[0]);
        }
        jdpJmxPacket.setInstanceName(instanceName);
        jdpJmxPacket.setRmiHostname(System.getProperty("java.rmi.server.hostname"));
        jdpJmxPacket.setBroadcastInterval(new Integer(n2).toString());
        final Integer processId = getProcessId();
        if (processId != null) {
            jdpJmxPacket.setProcessId(processId.toString());
        }
        final JdpBroadcaster jdpBroadcaster = new JdpBroadcaster(inetAddress, inetAddress2, n, integer);
        stopDiscoveryService();
        JdpController.controller = new JDPControllerRunner(jdpBroadcaster, jdpJmxPacket, n2);
        final Thread thread = new Thread(JdpController.controller, "JDP broadcaster");
        thread.setDaemon(true);
        thread.start();
    }
    
    public static synchronized void stopDiscoveryService() {
        if (JdpController.controller != null) {
            JdpController.controller.stop();
            JdpController.controller = null;
        }
    }
    
    static {
        JdpController.controller = null;
    }
    
    private static class JDPControllerRunner implements Runnable
    {
        private final JdpJmxPacket packet;
        private final JdpBroadcaster bcast;
        private final int pause;
        private volatile boolean shutdown;
        
        private JDPControllerRunner(final JdpBroadcaster bcast, final JdpJmxPacket packet, final int pause) {
            this.shutdown = false;
            this.bcast = bcast;
            this.packet = packet;
            this.pause = pause;
        }
        
        @Override
        public void run() {
            try {
                while (!this.shutdown) {
                    this.bcast.sendPacket(this.packet);
                    try {
                        Thread.sleep(this.pause);
                    }
                    catch (final InterruptedException ex) {}
                }
            }
            catch (final IOException ex2) {}
            try {
                this.stop();
                this.bcast.shutdown();
            }
            catch (final IOException ex3) {}
        }
        
        public void stop() {
            this.shutdown = true;
        }
    }
}
