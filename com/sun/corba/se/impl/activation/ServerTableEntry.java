package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation.InvalidORBid;
import java.util.Iterator;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import java.util.NoSuchElementException;
import com.sun.corba.se.spi.activation.ORBPortInfo;
import com.sun.corba.se.spi.activation.ServerOperations;
import com.sun.corba.se.spi.activation.ORBAlreadyRegistered;
import com.sun.corba.se.spi.activation.EndPointInfo;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.impl.logging.ActivationSystemException;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.Server;
import java.util.HashMap;

public class ServerTableEntry
{
    private static final int DE_ACTIVATED = 0;
    private static final int ACTIVATING = 1;
    private static final int ACTIVATED = 2;
    private static final int RUNNING = 3;
    private static final int HELD_DOWN = 4;
    private static final long waitTime = 2000L;
    private static final int ActivationRetryMax = 5;
    private int state;
    private int serverId;
    private HashMap orbAndPortInfo;
    private Server serverObj;
    private ServerDef serverDef;
    private Process process;
    private int activateRetryCount;
    private String activationCmd;
    private ActivationSystemException wrapper;
    private static String javaHome;
    private static String classPath;
    private static String fileSep;
    private static String pathSep;
    private boolean debug;
    
    private String printState() {
        String s = "UNKNOWN";
        switch (this.state) {
            case 0: {
                s = "DE_ACTIVATED";
                break;
            }
            case 1: {
                s = "ACTIVATING  ";
                break;
            }
            case 2: {
                s = "ACTIVATED   ";
                break;
            }
            case 3: {
                s = "RUNNING     ";
                break;
            }
            case 4: {
                s = "HELD_DOWN   ";
                break;
            }
        }
        return s;
    }
    
    @Override
    public String toString() {
        return "ServerTableEntry[state=" + this.printState() + " serverId=" + this.serverId + " activateRetryCount=" + this.activateRetryCount + "]";
    }
    
    ServerTableEntry(final ActivationSystemException wrapper, final int serverId, final ServerDef serverDef, final int n, final String s, final boolean b, final boolean debug) {
        this.activateRetryCount = 0;
        this.debug = false;
        this.wrapper = wrapper;
        this.serverId = serverId;
        this.serverDef = serverDef;
        this.debug = debug;
        this.orbAndPortInfo = new HashMap(255);
        this.activateRetryCount = 0;
        this.state = 1;
        this.activationCmd = ServerTableEntry.javaHome + ServerTableEntry.fileSep + "bin" + ServerTableEntry.fileSep + "java " + serverDef.serverVmArgs + " -Dioser=" + System.getProperty("ioser") + " -D" + "org.omg.CORBA.ORBInitialPort" + "=" + n + " -D" + "com.sun.CORBA.activation.DbDir" + "=" + s + " -D" + "com.sun.CORBA.POA.ORBActivated" + "=true -D" + "com.sun.CORBA.POA.ORBServerId" + "=" + serverId + " -D" + "com.sun.CORBA.POA.ORBServerName" + "=" + serverDef.serverName + " " + (b ? "-Dcom.sun.CORBA.activation.ORBServerVerify=true " : "") + "-classpath " + ServerTableEntry.classPath + (serverDef.serverClassPath.equals("") ? "" : ServerTableEntry.pathSep) + serverDef.serverClassPath + " com.sun.corba.se.impl.activation.ServerMain " + serverDef.serverArgs + (debug ? " -debug" : "");
        if (debug) {
            System.out.println("ServerTableEntry constructed with activation command " + this.activationCmd);
        }
    }
    
    public int verify() {
        try {
            if (this.debug) {
                System.out.println("Server being verified w/" + this.activationCmd);
            }
            this.process = Runtime.getRuntime().exec(this.activationCmd);
            final int wait = this.process.waitFor();
            if (this.debug) {
                this.printDebug("verify", "returns " + ServerMain.printResult(wait));
            }
            return wait;
        }
        catch (final Exception ex) {
            if (this.debug) {
                this.printDebug("verify", "returns unknown error because of exception " + ex);
            }
            return 4;
        }
    }
    
    private void printDebug(final String s, final String s2) {
        System.out.println("ServerTableEntry: method  =" + s);
        System.out.println("ServerTableEntry: server  =" + this.serverId);
        System.out.println("ServerTableEntry: state   =" + this.printState());
        System.out.println("ServerTableEntry: message =" + s2);
        System.out.println();
    }
    
    synchronized void activate() throws SystemException {
        this.state = 2;
        try {
            if (this.debug) {
                this.printDebug("activate", "activating server");
            }
            this.process = Runtime.getRuntime().exec(this.activationCmd);
        }
        catch (final Exception ex) {
            this.deActivate();
            if (this.debug) {
                this.printDebug("activate", "throwing premature process exit");
            }
            throw this.wrapper.unableToStartProcess();
        }
    }
    
    synchronized void register(final Server serverObj) {
        if (this.state == 2) {
            this.serverObj = serverObj;
            if (this.debug) {
                this.printDebug("register", "process registered back");
            }
            return;
        }
        if (this.debug) {
            this.printDebug("register", "throwing premature process exit");
        }
        throw this.wrapper.serverNotExpectedToRegister();
    }
    
    synchronized void registerPorts(final String s, final EndPointInfo[] array) throws ORBAlreadyRegistered {
        if (this.orbAndPortInfo.containsKey(s)) {
            throw new ORBAlreadyRegistered(s);
        }
        final int length = array.length;
        final EndPointInfo[] array2 = new EndPointInfo[length];
        for (int i = 0; i < length; ++i) {
            array2[i] = new EndPointInfo(array[i].endpointType, array[i].port);
            if (this.debug) {
                System.out.println("registering type: " + array2[i].endpointType + "  port  " + array2[i].port);
            }
        }
        this.orbAndPortInfo.put(s, array2);
        if (this.state == 2) {
            this.state = 3;
            this.notifyAll();
        }
        if (this.debug) {
            this.printDebug("registerPorts", "process registered Ports");
        }
    }
    
    void install() {
        ServerOperations serverObj = null;
        synchronized (this) {
            if (this.state != 3) {
                throw this.wrapper.serverNotRunning();
            }
            serverObj = this.serverObj;
        }
        if (serverObj != null) {
            serverObj.install();
        }
    }
    
    void uninstall() {
        ServerOperations serverObj = null;
        Process process = null;
        synchronized (this) {
            serverObj = this.serverObj;
            process = this.process;
            if (this.state != 3) {
                throw this.wrapper.serverNotRunning();
            }
            this.deActivate();
        }
        try {
            if (serverObj != null) {
                serverObj.shutdown();
                serverObj.uninstall();
            }
            if (process != null) {
                process.destroy();
            }
        }
        catch (final Exception ex) {}
    }
    
    synchronized void holdDown() {
        this.state = 4;
        if (this.debug) {
            this.printDebug("holdDown", "server held down");
        }
        this.notifyAll();
    }
    
    synchronized void deActivate() {
        this.state = 0;
        if (this.debug) {
            this.printDebug("deActivate", "server deactivated");
        }
        this.notifyAll();
    }
    
    synchronized void checkProcessHealth() {
        if (this.state == 3) {
            try {
                this.process.exitValue();
            }
            catch (final IllegalThreadStateException ex) {
                return;
            }
            synchronized (this) {
                this.orbAndPortInfo.clear();
                this.deActivate();
            }
        }
    }
    
    synchronized boolean isValid() {
        if (this.state == 1 || this.state == 4) {
            if (this.debug) {
                this.printDebug("isValid", "returns true");
            }
            return true;
        }
        try {
            this.process.exitValue();
        }
        catch (final IllegalThreadStateException ex) {
            return true;
        }
        if (this.state != 2) {
            this.deActivate();
            return false;
        }
        if (this.activateRetryCount < 5) {
            if (this.debug) {
                this.printDebug("isValid", "reactivating server");
            }
            ++this.activateRetryCount;
            this.activate();
            return true;
        }
        if (this.debug) {
            this.printDebug("isValid", "holding server down");
        }
        this.holdDown();
        return true;
    }
    
    synchronized ORBPortInfo[] lookup(final String s) throws ServerHeldDown {
        while (true) {
            if (this.state != 1) {
                if (this.state != 2) {
                    break;
                }
            }
            try {
                this.wait(2000L);
                if (!this.isValid()) {
                    break;
                }
                continue;
            }
            catch (final Exception ex) {}
        }
        if (this.state == 3) {
            final ORBPortInfo[] array = new ORBPortInfo[this.orbAndPortInfo.size()];
            final Iterator iterator = this.orbAndPortInfo.keySet().iterator();
            try {
                int n = 0;
                while (iterator.hasNext()) {
                    final String s2 = (String)iterator.next();
                    final EndPointInfo[] array2 = this.orbAndPortInfo.get(s2);
                    int port = -1;
                    for (int i = 0; i < array2.length; ++i) {
                        if (this.debug) {
                            System.out.println("lookup num-ports " + array2.length + "   " + array2[i].endpointType + "   " + array2[i].port);
                        }
                        if (array2[i].endpointType.equals(s)) {
                            port = array2[i].port;
                            break;
                        }
                    }
                    array[n] = new ORBPortInfo(s2, port);
                    ++n;
                }
            }
            catch (final NoSuchElementException ex2) {}
            return array;
        }
        if (this.debug) {
            this.printDebug("lookup", "throwing server held down error");
        }
        throw new ServerHeldDown(this.serverId);
    }
    
    synchronized EndPointInfo[] lookupForORB(final String s) throws ServerHeldDown, InvalidORBid {
        while (true) {
            if (this.state != 1) {
                if (this.state != 2) {
                    break;
                }
            }
            try {
                this.wait(2000L);
                if (!this.isValid()) {
                    break;
                }
                continue;
            }
            catch (final Exception ex) {}
        }
        if (this.state == 3) {
            EndPointInfo[] array2;
            try {
                final EndPointInfo[] array = this.orbAndPortInfo.get(s);
                array2 = new EndPointInfo[array.length];
                for (int i = 0; i < array.length; ++i) {
                    if (this.debug) {
                        System.out.println("lookup num-ports " + array.length + "   " + array[i].endpointType + "   " + array[i].port);
                    }
                    array2[i] = new EndPointInfo(array[i].endpointType, array[i].port);
                }
            }
            catch (final NoSuchElementException ex2) {
                throw new InvalidORBid();
            }
            return array2;
        }
        if (this.debug) {
            this.printDebug("lookup", "throwing server held down error");
        }
        throw new ServerHeldDown(this.serverId);
    }
    
    synchronized String[] getORBList() {
        final String[] array = new String[this.orbAndPortInfo.size()];
        final Iterator iterator = this.orbAndPortInfo.keySet().iterator();
        try {
            int n = 0;
            while (iterator.hasNext()) {
                array[n++] = (String)iterator.next();
            }
        }
        catch (final NoSuchElementException ex) {}
        return array;
    }
    
    int getServerId() {
        return this.serverId;
    }
    
    boolean isActive() {
        return this.state == 3 || this.state == 2;
    }
    
    synchronized void destroy() {
        ServerOperations serverObj = null;
        Process process = null;
        synchronized (this) {
            serverObj = this.serverObj;
            process = this.process;
            this.deActivate();
        }
        try {
            if (serverObj != null) {
                serverObj.shutdown();
            }
            if (this.debug) {
                this.printDebug("destroy", "server shutdown successfully");
            }
        }
        catch (final Exception ex) {
            if (this.debug) {
                this.printDebug("destroy", "server shutdown threw exception" + ex);
            }
        }
        try {
            if (process != null) {
                process.destroy();
            }
            if (this.debug) {
                this.printDebug("destroy", "process destroyed successfully");
            }
        }
        catch (final Exception ex2) {
            if (this.debug) {
                this.printDebug("destroy", "process destroy threw exception" + ex2);
            }
        }
    }
    
    static {
        ServerTableEntry.javaHome = System.getProperty("java.home");
        ServerTableEntry.classPath = System.getProperty("java.class.path");
        ServerTableEntry.fileSep = System.getProperty("file.separator");
        ServerTableEntry.pathSep = System.getProperty("path.separator");
    }
}
