package com.zoho.clustering.failover;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.Enumeration;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.NetworkInterface;
import java.util.logging.Logger;

public class FOSUtil
{
    private static Logger logger;
    private static FOSUtil inst;
    private String cmd_ifCheck;
    private String cmd_ipAdd;
    private String cmd_ipDel;
    
    public static void main(final String[] args) {
        initialize(args[0]);
        System.out.print(getInst().deleteIP(args[1]));
    }
    
    private static void assertWindowsOS() {
        final String os = System.getProperty("os.name");
        if (!os.toLowerCase().contains("win")) {
            throw new UnsupportedOperationException("OS [" + os + "] Not yet supported");
        }
    }
    
    public static void initialize(final String toolsDir) {
        if (FOSUtil.inst != null) {
            throw new IllegalStateException("FOSUtil is already initialized");
        }
        FOSUtil.inst = new FOSUtil(toolsDir);
    }
    
    public static FOSUtil getInst() {
        if (FOSUtil.inst == null) {
            throw new IllegalStateException("FOSUtil is not yet initialized.");
        }
        return FOSUtil.inst;
    }
    
    private FOSUtil(final String toolsDir) {
        this.cmd_ifCheck = null;
        this.cmd_ipAdd = null;
        this.cmd_ipDel = null;
        assertWindowsOS();
        this.cmd_ifCheck = toolsDir + "/ifcheck";
        this.cmd_ipAdd = toolsDir + "/ipadd";
        this.cmd_ipDel = toolsDir + "/ipdel";
    }
    
    private void assertNotNull(final Object obj, final String objName) {
        if (obj == null) {
            throw new IllegalArgumentException("[" + objName + "] cannot be null");
        }
    }
    
    public int startService(final String serviceName) {
        this.assertNotNull(serviceName, "serviceName");
        final ProcessBuilder pb = new ProcessBuilder(new String[] { "net", "start", serviceName });
        return execute(pb);
    }
    
    public int stopService(final String serviceName) {
        this.assertNotNull(serviceName, "serviceName");
        final ProcessBuilder pb = new ProcessBuilder(new String[] { "net", "stop", serviceName });
        return execute(pb);
    }
    
    public boolean isInterfaceUp(final String ifName) {
        final ProcessBuilder pb = new ProcessBuilder(new String[] { this.cmd_ifCheck });
        if (ifName != null) {
            pb.command().add(ifName);
        }
        final int status = execute(pb);
        return status == 0;
    }
    
    public int addIP(final String ipAddr, final String ifName, final String netMask) {
        this.assertNotNull(ipAddr, "ipAddr");
        if (this.isIPPresent(ipAddr)) {
            return 0;
        }
        final ProcessBuilder pb = new ProcessBuilder(new String[] { this.cmd_ipAdd, ipAddr });
        if (ifName != null) {
            pb.command().add("-if");
            pb.command().add(ifName);
        }
        if (netMask != null) {
            pb.command().add("-mask");
            pb.command().add(netMask);
        }
        return execute(pb);
    }
    
    public int deleteIP(final String ipAddr) {
        this.assertNotNull(ipAddr, "ipAddr");
        if (!this.isIPPresent(ipAddr)) {
            return 0;
        }
        final ProcessBuilder pb = new ProcessBuilder(new String[] { this.cmd_ipDel, ipAddr });
        return execute(pb);
    }
    
    public boolean isIPPresent(final String ipAddr) {
        Enumeration<NetworkInterface> adapters;
        try {
            adapters = NetworkInterface.getNetworkInterfaces();
        }
        catch (final SocketException exp) {
            throw new RuntimeException(exp);
        }
        while (adapters != null && adapters.hasMoreElements()) {
            final NetworkInterface adapter = adapters.nextElement();
            final Enumeration<InetAddress> addrList = adapter.getInetAddresses();
            while (addrList.hasMoreElements()) {
                final InetAddress inetAddress = addrList.nextElement();
                if (ipAddr.equals(inetAddress.getHostAddress())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public int executeCommandFile(final String filePath, final String... cmdArgs) {
        this.assertNotNull(filePath, "filePath");
        final ProcessBuilder pb = new ProcessBuilder(new String[] { "cmd", "/C", filePath });
        if (cmdArgs != null) {
            for (final String cmdArg : cmdArgs) {
                pb.command().add(cmdArg);
            }
        }
        return execute(pb);
    }
    
    public boolean ping(final String remoteHost) {
        this.assertNotNull(remoteHost, "remoteHost");
        final ProcessBuilder pb = new ProcessBuilder(new String[] { "ping", "-n", "1", "-w", "1000", remoteHost });
        final int status = execute(pb);
        return status == 0;
    }
    
    private static int execute(final ProcessBuilder pb) {
        if (FOSUtil.logger.isLoggable(Level.FINE)) {
            FOSUtil.logger.fine("EXECUTE: " + concat(pb.command()));
        }
        pb.redirectErrorStream(true);
        try {
            final Process proc = pb.start();
            final int status = proc.waitFor();
            FOSUtil.logger.log(Level.INFO, "{0}. ret-code: {1}", new Object[] { concat(pb.command()), status });
            return status;
        }
        catch (final Exception exp) {
            throw new RuntimeException("Error while executing the command [" + concat(pb.command()) + "]", exp);
        }
    }
    
    private static String concat(final List<String> strList) {
        if (strList == null || strList.isEmpty()) {
            return "";
        }
        final StringBuilder buff = new StringBuilder();
        for (final String str : strList) {
            buff.append(str).append(' ');
        }
        buff.deleteCharAt(buff.length() - 1);
        return buff.toString();
    }
    
    static {
        FOSUtil.logger = Logger.getLogger(FOSUtil.class.getName());
        FOSUtil.inst = null;
    }
}
