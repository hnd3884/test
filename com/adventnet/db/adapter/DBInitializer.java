package com.adventnet.db.adapter;

import java.util.Iterator;
import java.util.Locale;
import com.zoho.conf.Configuration;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import com.adventnet.mfw.Starter;
import java.util.regex.Pattern;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.io.File;
import com.zoho.framework.utils.OSCheckUtil;
import com.zoho.conf.AppResources;
import java.util.logging.Logger;

public abstract class DBInitializer
{
    private static final String CLASS_NAME;
    private static Logger out;
    private static String server_home;
    public String osName;
    protected boolean isWindows;
    protected static final int MAX_RETRIES_COUNT;
    protected static final int CHECK_PID_STATUS_TIMEOUT;
    
    protected DBInitializer() {
        this.osName = AppResources.getString("os.name", "").trim();
        this.isWindows = OSCheckUtil.isWindows(OSCheckUtil.getOS());
    }
    
    public Process startDBServer(final String[] startBatchFileArgs) throws IOException {
        final String serverHome = DBInitializer.server_home + File.separator;
        final String binHome = serverHome + "bin" + File.separator;
        DBInitializer.out.log(Level.INFO, "{0} isWindows ::: {1}", new Object[] { this.osName, this.isWindows });
        final List commandList = new ArrayList();
        if (this.isWindows) {
            if (this.osName.indexOf("95") >= 0 || this.osName.indexOf("98") >= 0) {
                commandList.add("command");
                commandList.add("/c");
                commandList.add("\"" + new File(binHome + "startDB.bat").getCanonicalPath() + "\"");
            }
            else {
                commandList.add("cmd");
                commandList.add("/c");
                commandList.add(new File(binHome + "startDB.bat").getCanonicalPath());
            }
        }
        else if (!this.isWindows) {
            commandList.add("/usr/bin/nohup");
            commandList.add("sh");
            commandList.add(new File(binHome + "startDB.sh").getCanonicalPath());
        }
        for (int i = 0; i < startBatchFileArgs.length; ++i) {
            commandList.add(startBatchFileArgs[i]);
        }
        final int size = commandList.size();
        final String[] commandToExecute = commandList.toArray(new String[size]);
        DBInitializer.out.log(Level.FINE, "Command to start DB server {0} ", commandList);
        final Process p = Runtime.getRuntime().exec(commandToExecute);
        this.dump(p);
        return p;
    }
    
    public boolean startDBServer(final int port, final String host, final String userName, final String password) throws IOException {
        throw new UnsupportedOperationException("No default implementation available... ");
    }
    
    public void stopDBServer(final int port, final String host, final String userName, final String password) throws IOException {
        throw new UnsupportedOperationException("No default implementation available... ");
    }
    
    protected boolean isServerRunning(final int port, final String host) {
        return !this.isServerStarted(port, host);
    }
    
    public boolean isServerStarted() throws IOException {
        throw new UnsupportedOperationException("isServerStarted() is not implemented generically, it should be implemented DBAdapter specifically");
    }
    
    public void dump(final Process p) {
        this.dump(p, null);
    }
    
    public void dump(final Process p, final Logger logger) {
        final ProcessWriter pw = new ProcessWriter(new BufferedReader(new InputStreamReader(p.getErrorStream())), logger);
        final ProcessWriter pw2 = new ProcessWriter(new BufferedReader(new InputStreamReader(p.getInputStream())), logger);
        pw.start();
        pw2.start();
    }
    
    public void dump(final Process p, final Logger logger, final List<String> errMsg, final AtomicBoolean notifyObj) {
        final ProcessWriter pw = new ProcessWriter(new BufferedReader(new InputStreamReader(p.getErrorStream())), logger, errMsg, notifyObj);
        final ProcessWriter pw2 = new ProcessWriter(new BufferedReader(new InputStreamReader(p.getInputStream())), logger, errMsg, notifyObj);
        pw.start();
        pw2.start();
    }
    
    public boolean checkServerStatus(final String host, final int port) {
        boolean isDemonstarted = false;
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            isDemonstarted = true;
        }
        catch (final Exception ex) {}
        if (socket != null) {
            try {
                socket.close();
            }
            catch (final IOException iex) {
                DBInitializer.out.log(Level.INFO, "Exception while closing socket ", iex);
            }
        }
        for (int count = 0; count < 5; ++count) {
            try {
                System.out.println("checkServerStatus :: Trying to establish socket for host :: " + host + "  in port :: " + port);
                socket = new Socket(host, port);
                isDemonstarted = true;
            }
            catch (final Exception es) {
                try {
                    System.out.println("checkServerStatus :: Waiting for 1 second ...");
                    Thread.sleep(1000L);
                }
                catch (final InterruptedException ex2) {}
            }
            if (socket != null) {
                try {
                    socket.close();
                }
                catch (final IOException iex2) {
                    DBInitializer.out.log(Level.INFO, "Exception while closing socket ", iex2);
                }
            }
            if (isDemonstarted) {
                break;
            }
        }
        System.out.println("checkServerStatus :: going to return :: " + isDemonstarted);
        return isDemonstarted;
    }
    
    public boolean isServerStarted(final int port, final String host) {
        boolean serverStarted = false;
        Socket socket = null;
        for (int count = 0; count < DBInitializer.MAX_RETRIES_COUNT; ++count) {
            try {
                System.out.println("isServerStarted :: Trying to establish socket for host :: " + host + "  in port :: " + port);
                socket = new Socket(host, port);
                serverStarted = true;
            }
            catch (final Exception es) {
                try {
                    System.out.println("isServerStarted :: Waiting for 1 second ...");
                    Thread.sleep(1000L);
                }
                catch (final InterruptedException ex) {}
            }
            if (socket != null) {
                try {
                    socket.close();
                }
                catch (final IOException iex) {
                    DBInitializer.out.log(Level.INFO, "Exception while closing socket ", iex);
                }
            }
            if (serverStarted) {
                break;
            }
        }
        System.out.println("isServerStarted :: going to return :: " + serverStarted);
        return serverStarted;
    }
    
    public Process stopDBServer(final String[] stopBatchFileArgs) throws IOException {
        final String binHome = DBInitializer.server_home + File.separator + "bin" + File.separator;
        final List commandList = new ArrayList();
        final String stopDB = "stopDB";
        if (this.isWindows) {
            if (this.osName.indexOf("95") >= 0 || this.osName.indexOf("98") >= 0) {
                commandList.add("command");
                commandList.add("/c");
                commandList.add("\"" + new File(binHome + "stopDB.bat").getCanonicalPath() + "\"");
            }
            else {
                commandList.add("cmd");
                commandList.add("/c");
                commandList.add(new File(binHome + "stopDB.bat").getCanonicalPath());
            }
        }
        else if (!this.isWindows) {
            commandList.add("sh");
            commandList.add(new File(binHome + "stopDB.sh").getCanonicalPath());
        }
        for (int i = 0; i < stopBatchFileArgs.length; ++i) {
            commandList.add(stopBatchFileArgs[i]);
        }
        final int size = commandList.size();
        final String[] commandToExecute = commandList.toArray(new String[size]);
        DBInitializer.out.log(Level.FINE, "Command to stop DB server {0} ", commandList);
        final Process p = Runtime.getRuntime().exec(commandToExecute);
        this.dump(p);
        try {
            p.waitFor();
        }
        catch (final InterruptedException iex) {
            iex.printStackTrace();
        }
        return p;
    }
    
    protected String buildString(final String... args) {
        final StringBuilder buffer = new StringBuilder();
        for (final String str : args) {
            buffer.append(str);
        }
        return buffer.toString();
    }
    
    public static int checkAndChangeDBPort(final String fileNameWithAbsolutePath, final int newport) throws IOException {
        final File dbParamsFile = new File(fileNameWithAbsolutePath);
        final File tempDBParamsFile = new File(fileNameWithAbsolutePath + ".temp");
        if (!dbParamsFile.exists()) {
            throw new FileNotFoundException("Specified File Not Found :: [" + fileNameWithAbsolutePath + "]");
        }
        final StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        BufferedWriter bw = null;
        int port = newport;
        try {
            br = new BufferedReader(new FileReader(dbParamsFile));
            for (String str = br.readLine(); str != null; str = br.readLine()) {
                str = str.trim();
                final Pattern pat = Pattern.compile("(url=.*)(jdbc.*):([0-9]*)(.*)");
                final Matcher mat = pat.matcher(str);
                if (mat.matches()) {
                    port = Integer.parseInt(mat.group(3));
                    final String portString = ":" + port;
                    if (newport == -1) {
                        final boolean isAvailable = Starter.isPortFree(port);
                        if (!isAvailable) {
                            port = Starter.getAvailablePort(port);
                        }
                    }
                    final String newPortString = ":" + ((newport == -1) ? port : newport);
                    sb.append(str.replaceAll(portString, newPortString));
                    sb.append("\n");
                }
                else {
                    sb.append(str);
                    sb.append("\n");
                }
            }
            bw = new BufferedWriter(new FileWriter(tempDBParamsFile));
            bw.write(sb.toString());
            dbParamsFile.delete();
            tempDBParamsFile.renameTo(dbParamsFile);
        }
        catch (final IOException ex) {
            tempDBParamsFile.delete();
            DBInitializer.out.log(Level.INFO, "Problem while writing to file");
            ex.printStackTrace();
        }
        catch (final Exception ex2) {
            ex2.printStackTrace();
        }
        finally {
            if (br != null) {
                br.close();
            }
            if (bw != null) {
                bw.close();
            }
        }
        return port;
    }
    
    public String getHostAddressName(final String hostname) throws IOException {
        final String hostAddressName = InetAddress.getByName(hostname).getHostAddress();
        return hostAddressName;
    }
    
    public String getVersion() throws Exception {
        throw new UnsupportedOperationException("No default implementation available... ");
    }
    
    public byte getDBArchitecture() throws Exception {
        throw new UnsupportedOperationException("No default implementation available... ");
    }
    
    public String getDBDataDirectory() {
        final File file = new File(Configuration.getString("db.home") + File.separator + "data");
        if (file.exists()) {
            try {
                return file.getCanonicalPath();
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public boolean preDBServerStartUp() throws IOException {
        return true;
    }
    
    public boolean postDBServerStartUp() throws IOException {
        return true;
    }
    
    static {
        CLASS_NAME = DBInitializer.class.getName();
        DBInitializer.out = Logger.getLogger(DBInitializer.CLASS_NAME);
        DBInitializer.server_home = ((Configuration.getString("server.home", ".") != null) ? Configuration.getString("server.home", ".") : Configuration.getString("app.home", "."));
        MAX_RETRIES_COUNT = Integer.parseInt(Configuration.getString("DBStartupRetries", "120"));
        CHECK_PID_STATUS_TIMEOUT = Integer.parseInt(Configuration.getString("check.pid.status.timeout", "25"));
    }
    
    class ProcessWriter extends Thread
    {
        BufferedReader br;
        Logger log;
        List<String> errMsgList;
        AtomicBoolean notifyObj;
        
        ProcessWriter(final BufferedReader br, final Logger logger) {
            this.log = null;
            this.errMsgList = null;
            this.notifyObj = null;
            this.br = br;
            this.log = logger;
        }
        
        ProcessWriter(final BufferedReader br, final Logger logger, final List<String> errMsg, final AtomicBoolean notifyVariable) {
            this.log = null;
            this.errMsgList = null;
            this.notifyObj = null;
            this.br = br;
            this.log = logger;
            this.errMsgList = errMsg;
            this.notifyObj = notifyVariable;
        }
        
        @Override
        public void run() {
            String line = "";
            try {
                while ((line = this.br.readLine()) != null) {
                    if (this.errMsgList != null) {
                        for (final String errMsg : this.errMsgList) {
                            if (line.toLowerCase(Locale.ENGLISH).contains(errMsg)) {
                                DBInitializer.out.log(Level.WARNING, "Error :::: {0}", line);
                                this.notifyObj.set(true);
                            }
                        }
                    }
                    if (this.log == null) {
                        System.out.println(line);
                    }
                    else {
                        this.log.info(line);
                    }
                }
            }
            catch (final Exception exc) {
                DBInitializer.out.log(Level.FINE, "Error Stream :: " + exc.getMessage());
            }
        }
    }
}
