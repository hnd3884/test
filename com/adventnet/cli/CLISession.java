package com.adventnet.cli;

import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import com.maverick.ssh.LicenseManager;
import java.util.regex.Pattern;
import java.net.URL;
import java.util.StringTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.io.IOException;
import java.io.InterruptedIOException;
import com.adventnet.cli.transport.ssh.SshProtocolOptionsImpl;
import com.adventnet.cli.transport.TelnetProtocolOptionsImpl;
import java.util.Properties;
import com.adventnet.cli.transport.CLIProtocolOptions;
import java.util.concurrent.ThreadPoolExecutor;
import com.adventnet.cli.util.CLILogMgr;
import com.adventnet.cli.transport.CLITransportProvider;
import java.util.Vector;

public class CLISession extends Thread
{
    CLIAsynchSendThread asyncSend;
    Vector msgList;
    static CLIResourceManager cliResourceManager;
    CLITransportProvider cliTransportProvider;
    String cliTransportProviderClassName;
    static String providerNameFromFile;
    boolean ignoreSpecialCharacters;
    boolean cmdInProgress;
    boolean gotResponse;
    CallBackThread cbt;
    boolean removeCommandFromOutput;
    boolean isOPMSession;
    private static CLILogMgr clm;
    boolean isSessionAlive;
    private static String CLIHomeDir;
    private static boolean createLogs;
    int maxMatchReadConter;
    static ThreadPoolExecutor threadPoolExecutor;
    CLIProtocolOptions cliProtocolOptions;
    String cliPrompt;
    private String interruptCmd;
    private CLIMessage response;
    private String messagePrompt;
    private String[] messagePromptList;
    private Properties messagePromptAction;
    private boolean messagePromptEcho;
    boolean isRunMethodActive;
    boolean enablePooling;
    int maxConnections;
    boolean gotFromPool;
    Vector clients;
    static int prevId;
    static int msgID;
    int maxMsgCount;
    int requestTimeout;
    int keepAliveTimeout;
    String transportProviderFileName;
    static boolean debugFlag;
    static int debugLevel;
    private static final String HP_JUNK_SEQUENCE = "[24;";
    private static final String REGEX_HP_JUNK_SEQUENCE = "\\[24;";
    private String matchedPrompt;
    private static final int LEN = 40;
    private int prevPos;
    boolean isReadThreadAlive;
    private Properties cliPromptAction;
    private ConnectionHandler connectionHandler;
    
    public static synchronized void initializeLogging() {
        if (CLISession.createLogs && CLISession.clm == null) {
            final String s = "conf/logging_parameters.conf";
            try {
                CLISession.CLIHomeDir = setHomeDirectory();
            }
            catch (final Exception ex) {
                System.err.println(ex.getMessage());
                CLISession.CLIHomeDir = ".";
                CLISession.createLogs = true;
            }
            System.out.println(" CLI Home Directory: " + CLISession.CLIHomeDir);
            CLISession.clm = new CLILogMgr(CLISession.CLIHomeDir, s);
            CLISession.createLogs = false;
        }
    }
    
    public void setTransportProviderClassName(final String cliTransportProviderClassName) {
        if (this.cliTransportProviderClassName == null) {
            this.cliTransportProviderClassName = cliTransportProviderClassName;
        }
    }
    
    public String getTransportProviderClassName() {
        return this.cliTransportProviderClassName;
    }
    
    public void setCLIProtocolOptions(final CLIProtocolOptions cliProtocolOptions) {
        this.cliProtocolOptions = (CLIProtocolOptions)cliProtocolOptions.clone();
    }
    
    public CLIProtocolOptions getCLIProtocolOptions() {
        return this.cliProtocolOptions;
    }
    
    public void setCLIPrompt(final String cliPrompt) {
        this.cliPrompt = cliPrompt;
    }
    
    public String getCLIPrompt() {
        return this.cliPrompt;
    }
    
    CLISession() {
        this.asyncSend = null;
        this.msgList = new Vector();
        this.cliTransportProvider = null;
        this.cliTransportProviderClassName = null;
        this.ignoreSpecialCharacters = false;
        this.cmdInProgress = false;
        this.gotResponse = false;
        this.cbt = null;
        this.removeCommandFromOutput = false;
        this.isOPMSession = false;
        this.isSessionAlive = true;
        this.maxMatchReadConter = 65535;
        this.cliProtocolOptions = null;
        this.cliPrompt = null;
        this.interruptCmd = null;
        this.messagePrompt = null;
        this.messagePromptList = null;
        this.messagePromptAction = null;
        this.messagePromptEcho = true;
        this.isRunMethodActive = false;
        this.enablePooling = false;
        this.maxConnections = 1;
        this.clients = null;
        this.maxMsgCount = 1000;
        this.requestTimeout = 5000;
        this.keepAliveTimeout = 60;
        this.transportProviderFileName = "/cliTransport.conf";
        this.matchedPrompt = null;
        this.prevPos = 0;
        this.isReadThreadAlive = false;
        this.cliPromptAction = null;
    }
    
    public CLISession(final CLIProtocolOptions cliProtocolOptions) throws Exception {
        this.asyncSend = null;
        this.msgList = new Vector();
        this.cliTransportProvider = null;
        this.cliTransportProviderClassName = null;
        this.ignoreSpecialCharacters = false;
        this.cmdInProgress = false;
        this.gotResponse = false;
        this.cbt = null;
        this.removeCommandFromOutput = false;
        this.isOPMSession = false;
        this.isSessionAlive = true;
        this.maxMatchReadConter = 65535;
        this.cliProtocolOptions = null;
        this.cliPrompt = null;
        this.interruptCmd = null;
        this.messagePrompt = null;
        this.messagePromptList = null;
        this.messagePromptAction = null;
        this.messagePromptEcho = true;
        this.isRunMethodActive = false;
        this.enablePooling = false;
        this.maxConnections = 1;
        this.clients = null;
        this.maxMsgCount = 1000;
        this.requestTimeout = 5000;
        this.keepAliveTimeout = 60;
        this.transportProviderFileName = "/cliTransport.conf";
        this.matchedPrompt = null;
        this.prevPos = 0;
        this.isReadThreadAlive = false;
        this.cliPromptAction = null;
        if (CLISession.cliResourceManager == null) {
            CLISession.cliResourceManager = CLIResourceManager.getInstance();
        }
        this.init(cliProtocolOptions, CLISession.cliResourceManager.isSetPooling());
    }
    
    public CLISession(final CLIProtocolOptions cliProtocolOptions, final boolean b) throws Exception {
        this.asyncSend = null;
        this.msgList = new Vector();
        this.cliTransportProvider = null;
        this.cliTransportProviderClassName = null;
        this.ignoreSpecialCharacters = false;
        this.cmdInProgress = false;
        this.gotResponse = false;
        this.cbt = null;
        this.removeCommandFromOutput = false;
        this.isOPMSession = false;
        this.isSessionAlive = true;
        this.maxMatchReadConter = 65535;
        this.cliProtocolOptions = null;
        this.cliPrompt = null;
        this.interruptCmd = null;
        this.messagePrompt = null;
        this.messagePromptList = null;
        this.messagePromptAction = null;
        this.messagePromptEcho = true;
        this.isRunMethodActive = false;
        this.enablePooling = false;
        this.maxConnections = 1;
        this.clients = null;
        this.maxMsgCount = 1000;
        this.requestTimeout = 5000;
        this.keepAliveTimeout = 60;
        this.transportProviderFileName = "/cliTransport.conf";
        this.matchedPrompt = null;
        this.prevPos = 0;
        this.isReadThreadAlive = false;
        this.cliPromptAction = null;
        this.init(cliProtocolOptions, b);
    }
    
    private void init(final CLIProtocolOptions cliProtocolOptions, final boolean enablePooling) throws Exception {
        if (CLISession.debugFlag) {
            initializeLogging();
        }
        this.enablePooling = enablePooling;
        this.setCLIProtocolOptions(cliProtocolOptions);
        if (CLISession.cliResourceManager == null) {
            CLISession.cliResourceManager = CLIResourceManager.getInstance();
        }
        this.setKeepAliveTimeout(CLISession.cliResourceManager.getKeepAliveTimeout());
        this.setMaxConnections(CLISession.cliResourceManager.getMaxConnections());
        try {
            final Properties properties = this.cliProtocolOptions.getProperties();
            String s = null;
            if (properties != null) {
                s = ((Hashtable<K, String>)properties).get("RemoteHost");
            }
            final String property = System.getProperty("ncm.device.skip.junk.reponse");
            if (property != null) {
                final String[] split = property.split(",");
                for (int length = split.length, i = 0; i < length; ++i) {
                    if (split[i].equals(s)) {
                        CLILogMgr.setDebugMessage("CLIUSER", s + " contained in " + property + "!!!! hence maxMatchReadConter changed to 10", 2, null);
                        this.maxMatchReadConter = 10;
                        break;
                    }
                }
            }
            if (this.maxMatchReadConter != 10) {
                this.maxMatchReadConter = Integer.parseInt(System.getProperty("ncm.max.match.read.counter", "65535"));
            }
        }
        catch (final Exception ex) {}
    }
    
    public void open() throws Exception {
        if (CLISession.providerNameFromFile == null) {
            try {
                this.initTransportProvider();
            }
            catch (final Exception ex) {
                CLILogMgr.setDebugMessage("CLIERR", "CLISession: Cannot read transport provider file. Setting provider to com.adventnet.cli.transport.TelnetTransportImpl ", 4, ex);
                CLISession.providerNameFromFile = "com.adventnet.cli.transport.TelnetTransportImpl";
            }
        }
        if (this.cliTransportProviderClassName == null) {
            this.cliTransportProviderClassName = CLISession.providerNameFromFile;
        }
        if (this.cliProtocolOptions instanceof TelnetProtocolOptionsImpl) {
            this.connectionHandler = ((TelnetProtocolOptionsImpl)this.cliProtocolOptions).getConnectionHandler();
        }
        if (this.cliProtocolOptions instanceof SshProtocolOptionsImpl) {
            this.connectionHandler = ((SshProtocolOptionsImpl)this.cliProtocolOptions).getConnectionHandler();
        }
        if (this.connectionHandler != null) {
            this.connectionHandler.preConnect(this);
        }
        this.cliTransportProvider = CLISession.cliResourceManager.updateResourceManager(this.cliProtocolOptions, this.cliTransportProviderClassName, this, this.enablePooling);
        this.response = new CLIMessage((String)null);
        if (this.cliTransportProvider != null) {
            this.startReadThread();
            CLILogMgr.setDebugMessage("CLIUSER", "CLISession open: session opened successfully", 2, null);
        }
        if (this.connectionHandler != null) {
            this.connectionHandler.postLogin(this);
        }
    }
    
    public String getInterruptCmd() {
        return this.interruptCmd;
    }
    
    public void setInterruptCmd(final String interruptCmd) {
        this.interruptCmd = interruptCmd;
    }
    
    private void thread_sleep(final long n) {
        try {
            Thread.sleep(n);
        }
        catch (final Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    @Override
    public void run() {
        CLIMessage read = null;
        int n = 0;
        int n2 = 0;
        StringBuffer truncatePrompt = new StringBuffer();
        CLILogMgr.setDebugMessage("CLIUSER", "CLISession: enters the read thread", 4, null);
        int n3 = 0;
        this.isRunMethodActive = true;
        while (this.isReadThreadAlive) {
            try {
                if (!this.isSessionAlive) {
                    break;
                }
                if (this.cliTransportProvider == null) {
                    this.thread_sleep(100L);
                    continue;
                }
                synchronized (this.cliTransportProvider) {
                    if (this.response.getState() == 1) {
                        truncatePrompt = new StringBuffer();
                        n = 0;
                        this.prevPos = 0;
                        this.response.setState(0);
                    }
                    read = this.cliTransportProvider.read();
                }
            }
            catch (final InterruptedIOException ex) {
                read = null;
            }
            catch (final IOException ex2) {
                ex2.printStackTrace();
                CLILogMgr.setDebugMessage("CLIUSER", "Exception while reading data from the input stream", 2, ex2);
                final CLIResourceManager cliResourceManager = CLISession.cliResourceManager;
                final Vector connectionListeners = CLIResourceManager.cliTransportPool.getConnectionListeners();
                if (connectionListeners != null) {
                    final Enumeration elements = connectionListeners.elements();
                    while (elements.hasMoreElements()) {
                        ((ConnectionListener)elements.nextElement()).connectionDown(this.cliProtocolOptions);
                    }
                }
                try {
                    this.close();
                }
                catch (final Exception ex3) {
                    CLILogMgr.setDebugMessage("CLIUSER", "Exception whilw closing session inside run", 2, null);
                }
            }
            catch (final Exception ex4) {
                read = null;
            }
            if (read == null) {
                if (n == 0) {
                    this.thread_sleep(5L);
                }
                else {
                    if (this.isResponseReset()) {
                        continue;
                    }
                    this.response.setData(truncatePrompt.toString());
                    if (this.cmdInProgress) {
                        if (n2 != 0) {
                            n3 = 0;
                            CLILogMgr.setDebugMessage("CLIUSER", "CLISession run: match success. Sending synchronous notification in run", 4, null);
                            this.gotResponse = true;
                            n2 = 0;
                            this.notifyResponse();
                        }
                        else {
                            this.thread_sleep(5L);
                        }
                    }
                    else {
                        if (this.response.getState() != 0) {
                            continue;
                        }
                        CLILogMgr.setDebugMessage("CLIUSER", "CLISession: sending asynchronous notification in run", 4, null);
                        if (this.cbt == null) {
                            CLILogMgr.setDebugMessage("CLIUSER", "CLISession: No clients present, to notify async message", 0, null);
                        }
                        else {
                            this.cbt.notifyAsyncMessage(this.response);
                        }
                        this.resetResponse();
                    }
                }
            }
            else {
                truncatePrompt.append(read.getData());
                n += read.getData().length();
                if (!this.cmdInProgress) {
                    continue;
                }
                if (this.messagePrompt == null && this.messagePromptList != null) {
                    n2 = (this.checkForMatchList(this.messagePromptList, truncatePrompt.toString(), n) ? 1 : 0);
                }
                else if (this.isOPMSession) {
                    n2 = (this.checkForMatch(this.messagePrompt, truncatePrompt.toString(), n) ? 1 : 0);
                }
                else {
                    n2 = (this.checkForMatchPattern(this.messagePrompt, truncatePrompt.toString(), n) ? 1 : 0);
                }
                if (this.messagePromptAction != null && n2 == 0) {
                    n2 = (this.checkForMatches(this.messagePromptAction, truncatePrompt, n) ? 1 : 0);
                    if (n2 != 0) {
                        n2 = (this.multiplePromptAction() ? 1 : 0);
                        if (!this.messagePromptEcho) {
                            truncatePrompt = this.truncatePrompt(truncatePrompt, this.matchedPrompt);
                        }
                        this.prevPos = truncatePrompt.length();
                    }
                }
                if (n2 == 0 || ++n3 < this.maxMatchReadConter) {
                    continue;
                }
                n3 = 0;
                this.response.setData(truncatePrompt.toString());
                if (this.cmdInProgress) {
                    this.gotResponse = true;
                    n2 = 0;
                    this.notifyResponse();
                }
                else {
                    if (this.response.getState() != 0) {
                        continue;
                    }
                    CLILogMgr.setDebugMessage("CLIUSER", "CLISession: sending asynchronous notification in run", 4, null);
                    if (this.cbt == null) {
                        CLILogMgr.setDebugMessage("CLIUSER", "CLISession: No clients present, to notify async message", 0, null);
                    }
                    else {
                        this.cbt.notifyAsyncMessage(this.response);
                    }
                    this.resetResponse();
                }
            }
        }
        this.isRunMethodActive = false;
    }
    
    synchronized void notifyResponse() {
        try {
            this.notifyAll();
        }
        catch (final Exception ex) {
            CLILogMgr.setDebugMessage("CLIUSER", "CLISession: exception in notification of response", 4, ex);
            ex.printStackTrace();
        }
    }
    
    private boolean multiplePromptAction() {
        CLILogMgr.setDebugMessage("CLIUSER", "CLISession: matched prompt " + this.matchedPrompt, 4, null);
        final String property = this.messagePromptAction.getProperty(this.matchedPrompt);
        if (property.length() != 0) {
            final CLIMessage cliMessage = new CLIMessage(property);
            cliMessage.setMessageSuffix("");
            try {
                this.cliTransportProvider.write(cliMessage);
            }
            catch (final IOException ex) {
                ex.printStackTrace();
            }
            return false;
        }
        return true;
    }
    
    private void sendInterruptCmd() {
        if (this.getInterruptCmd() != null) {
            try {
                final CLIMessage cliMessage = new CLIMessage(this.getInterruptCmd());
                cliMessage.setMessageSuffix("");
                CLILogMgr.setDebugMessage("CLIUSER", "CLISession: sending interrupt cmd ", 4, null);
                this.cliTransportProvider.write(cliMessage);
            }
            catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void setPooling(final boolean enablePooling) {
        this.enablePooling = enablePooling;
    }
    
    public boolean isSetPooling() {
        return this.enablePooling;
    }
    
    public void setMaxConnections(final int maxConnections) {
        if (maxConnections >= 0) {
            this.maxConnections = maxConnections;
        }
    }
    
    public int getMaxConnections() {
        return this.maxConnections;
    }
    
    public CLIResourceManager getResourceManager() {
        return CLISession.cliResourceManager;
    }
    
    public CLITransportProvider getProviderFromResourceManager() throws Exception {
        if (!this.enablePooling) {
            return null;
        }
        this.gotFromPool = false;
        this.cliTransportProvider = CLISession.cliResourceManager.getProvider(this.cliProtocolOptions, this.cliTransportProviderClassName, this);
        if (!this.gotFromPool) {
            CLISession.cliResourceManager.addProviderToPool(this.cliTransportProvider, this.cliProtocolOptions, this, false);
        }
        else {
            CLISession.cliResourceManager.releaseProvider(this.cliTransportProvider, this.cliProtocolOptions);
        }
        return this.cliTransportProvider;
    }
    
    public CLIMessage syncSend(final CLIMessage cliMessage) throws Exception, MaxConnectionException {
        synchronized (this) {
            while (this.cmdInProgress) {
                try {
                    this.wait(100L);
                }
                catch (final InterruptedException ex) {
                    CLILogMgr.setDebugMessage("CLIERR", "CLISession syncSend(): exception in sleep", 4, ex);
                    ex.printStackTrace();
                }
            }
            this.cmdInProgress = true;
        }
        final CLIMessage cliMessage2 = (CLIMessage)cliMessage.clone();
        String s = null;
        CLIProtocolOptions cliProtocolOptions = null;
        CLIMessage cliMessage3 = null;
        if (cliMessage.getCLIPrompt() == null) {
            cliMessage2.setCLIPrompt(this.cliPrompt);
        }
        this.messagePrompt = cliMessage2.getCLIPrompt();
        this.messagePromptList = cliMessage.getCLIPromptList();
        if (cliMessage.getCLIPromptAction() != null) {
            this.messagePromptAction = cliMessage.getCLIPromptAction();
        }
        else {
            this.messagePromptAction = this.getCLIPromptAction();
        }
        if (cliMessage.isSetPromptEcho()) {
            this.messagePromptEcho = true;
        }
        else {
            this.messagePromptEcho = false;
        }
        if (cliMessage.getRequestTimeout() == 0) {
            cliMessage2.setRequestTimeout(this.requestTimeout);
        }
        if (!this.enablePooling) {
            if (this.cliTransportProvider == null) {
                this.resetCmdInProgress();
                throw new Exception("Session not established");
            }
            CLILogMgr.setDebugMessage("CLIUSER", "CLISession syncSend(): Writing the message: " + cliMessage.getData(), 2, null);
            try {
                synchronized (this.cliTransportProvider) {
                    this.cliTransportProvider.write(cliMessage2);
                }
                cliMessage3 = this.waitForResponse(cliMessage2.getRequestTimeout());
            }
            catch (final Exception ex2) {
                s = ex2.getMessage();
                if (s == null) {
                    s = "Socket Error ";
                }
            }
        }
        else {
            this.gotFromPool = false;
            cliProtocolOptions = cliMessage.getCLIProtocolOptions();
            if (cliProtocolOptions != null) {
                this.cliTransportProvider = CLISession.cliResourceManager.getProvider(cliProtocolOptions, this.cliTransportProviderClassName, this);
            }
            else {
                this.cliTransportProvider = CLISession.cliResourceManager.getProvider(this.cliProtocolOptions, this.cliTransportProviderClassName, this);
            }
            if (this.cliTransportProvider == null) {
                this.resetCmdInProgress();
                throw new Exception("Session not established");
            }
            CLILogMgr.setDebugMessage("CLIUSER", "CLISession syncSend(): Writing the message: " + cliMessage.getData(), 2, null);
            try {
                this.startReadThread();
                synchronized (this.cliTransportProvider) {
                    this.cliTransportProvider.write(cliMessage2);
                }
                cliMessage3 = this.waitForResponse(cliMessage2.getRequestTimeout());
            }
            catch (final Exception ex3) {
                ex3.printStackTrace();
                s = ex3.getMessage();
                if (s == null) {
                    s = "Socket Error ";
                }
            }
            if (!this.gotFromPool) {
                if (cliProtocolOptions != null) {
                    CLISession.cliResourceManager.addProviderToPool(this.cliTransportProvider, cliProtocolOptions, this, false);
                }
                else {
                    CLISession.cliResourceManager.addProviderToPool(this.cliTransportProvider, this.cliProtocolOptions, this, false);
                }
            }
            else if (cliProtocolOptions != null) {
                CLISession.cliResourceManager.releaseProvider(this.cliTransportProvider, cliProtocolOptions);
            }
            else {
                CLISession.cliResourceManager.releaseProvider(this.cliTransportProvider, this.cliProtocolOptions);
            }
        }
        if (s != null) {
            try {
                if (s.equals("Stream closed By Remote Peer") && this.enablePooling) {
                    if (cliProtocolOptions != null) {
                        CLISession.cliResourceManager.removeProvider(this.cliTransportProvider, cliProtocolOptions);
                    }
                    else {
                        CLISession.cliResourceManager.removeProvider(this.cliTransportProvider, this.cliProtocolOptions);
                    }
                }
            }
            catch (final Exception ex4) {
                ex4.printStackTrace();
            }
            if (this.enablePooling) {
                this.stopReadThread();
            }
            this.resetCmdInProgress();
            throw new Exception(s);
        }
        if (this.enablePooling) {
            this.stopReadThread();
        }
        final String data = cliMessage3.getData();
        if (this.removeCommandFromOutput) {
            cliMessage3.setData(data.substring(data.indexOf("\r\n") + 1));
        }
        else {
            cliMessage3.setData(data);
        }
        final CLIMessage truncate = this.truncate(cliMessage3, cliMessage2);
        if (this.ignoreSpecialCharacters) {
            truncate.setData(this.stripSpecialCharacters(truncate.getData().getBytes()));
        }
        CLILogMgr.setDebugMessage("CLIUSER", "CLISession syncSend(): Got the response " + truncate.getData() + "  for == " + this.cliProtocolOptions.getID(), 2, null);
        this.resetResponse();
        this.resetCmdInProgress();
        return truncate;
    }
    
    public void setCommandLineRemovalForOutputData(final boolean removeCommandFromOutput) {
        this.removeCommandFromOutput = removeCommandFromOutput;
    }
    
    public void setOPMSession(final boolean isOPMSession) {
        this.isOPMSession = isOPMSession;
    }
    
    private synchronized boolean isResponseReset() {
        return this.response.getState() == 1 && this.response.getData() == null;
    }
    
    private synchronized void resetResponse() {
        this.response.setState(1);
        this.response.setData((String)null);
    }
    
    private synchronized void resetCmdInProgress() {
        this.cmdInProgress = false;
        this.notifyAll();
    }
    
    synchronized CLIMessage waitForResponse(final int n) throws Exception {
        try {
            CLILogMgr.setDebugMessage("CLIUSER", "CLISession: waiting for response", 4, null);
            this.wait(n);
            CLILogMgr.setDebugMessage("CLIUSER", "CLISession: notified, proceeds...", 4, null);
        }
        catch (final Exception ex) {
            CLILogMgr.setDebugMessage("CLIERR", "CLISession: Exception while waiting for response from read thread", 4, ex);
            ex.printStackTrace();
        }
        final CLIMessage cliMessage = new CLIMessage("");
        if (!this.gotResponse && this.messagePrompt != null && this.messagePrompt.equals("$NO_RESPONSE")) {
            CLILogMgr.setDebugMessage("CLIUSER", "CLISession: prompt set to no response", 4, null);
            this.gotResponse = true;
        }
        if (!this.gotResponse) {
            if (this.response.getData() == null) {
                this.resetCmdInProgress();
                throw new Exception("Timed out");
            }
            cliMessage.setPartialResponse(true);
            this.sendInterruptCmd();
            CLILogMgr.setDebugMessage("CLIUSER", "CLISession: sending partial response in waitForResponse", 4, null);
        }
        else {
            this.gotResponse = false;
            if (this.response != null) {
                CLILogMgr.setDebugMessage("CLIUSER", "CLISession: sending partial response in waitForResponse", 4, null);
            }
        }
        cliMessage.setData(this.response.getData());
        return cliMessage;
    }
    
    public synchronized void addCLIClient(final CLIClient cliClient) {
        if (this.clients == null) {
            this.clients = new Vector();
            CLILogMgr.setDebugMessage("CLIUSER", "CLISession: Creating Callback Thread ", 2, null);
            this.cbt = new CallBackThread(this);
        }
        if (cliClient != null) {
            this.clients.addElement(cliClient);
        }
    }
    
    public synchronized void removeCLIClient(final CLIClient cliClient) {
        if (this.clients != null) {
            this.clients.removeElement(cliClient);
        }
    }
    
    public int getCLIClientsSize() {
        if (this.clients != null) {
            return this.clients.size();
        }
        return 0;
    }
    
    private void startAsyncSendThreadIfNotStartedAlready() {
        if (this.asyncSend == null) {
            this.asyncSend = new CLIAsynchSendThread(this);
        }
    }
    
    public int send(final CLIMessage cliMessage) throws Exception {
        this.startAsyncSendThreadIfNotStartedAlready();
        int msgID = cliMessage.getMsgID();
        final CLIMessage cliMessage2 = new CLIMessage(cliMessage.getData());
        if (cliMessage.getCLIProtocolOptions() != null) {
            cliMessage2.setCLIProtocolOptions(cliMessage.getCLIProtocolOptions());
        }
        else {
            cliMessage2.setCLIProtocolOptions(this.cliProtocolOptions);
        }
        cliMessage2.setCommandEcho(cliMessage.isSetCommandEcho());
        cliMessage2.setPromptEcho(cliMessage.isSetPromptEcho());
        cliMessage2.setCLIPrompt(cliMessage.getCLIPrompt());
        cliMessage2.setMessageSuffix(cliMessage.getMessageSuffix());
        cliMessage2.setRequestTimeout(cliMessage.getRequestTimeout());
        cliMessage2.setCLIPromptAction(cliMessage.getCLIPromptAction());
        CLILogMgr.setDebugMessage("CLIUSER", "CLISession send(): enQuing message ", 4, null);
        if (msgID == 0) {
            msgID = genMsgID();
        }
        cliMessage2.setMsgID(msgID);
        this.enQMessage(cliMessage2);
        return msgID;
    }
    
    static int genMsgID() {
        CLISession.msgID = ++CLISession.prevId;
        CLISession.msgID &= Integer.MAX_VALUE;
        if (CLISession.msgID > 1073741823) {
            CLISession.msgID -= 1073741823;
        }
        return CLISession.msgID;
    }
    
    public int getRequestQSize() {
        return this.maxMsgCount;
    }
    
    public void setRequestQSize(final int maxMsgCount) {
        this.maxMsgCount = maxMsgCount;
    }
    
    boolean enQMessage(final CLIMessage cliMessage) {
        synchronized (this.msgList) {
            if (this.msgList.size() == this.maxMsgCount) {
                return false;
            }
            this.msgList.addElement(cliMessage);
            this.msgList.notifyAll();
        }
        return true;
    }
    
    CLIMessage deQMessage() {
        synchronized (this.msgList) {
            if (this.msgList.size() == 0) {
                return null;
            }
            final CLIMessage cliMessage = this.msgList.elementAt(0);
            this.msgList.removeElementAt(0);
            return cliMessage;
        }
    }
    
    void wait_for_message() {
        synchronized (this.msgList) {
            if (this.msgList.size() == 0) {
                try {
                    this.msgList.wait();
                }
                catch (final InterruptedException ex) {
                    CLILogMgr.setDebugMessage("CLIERR", "CLISession: exception while waiting for message ", 4, ex);
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public void close() throws Exception {
        if (!this.isSessionAlive) {
            CLILogMgr.setDebugMessage("CLIUSER", "CLISession already closed it seems, so returning", 2, null);
            return;
        }
        if (this.asyncSend != null) {
            this.asyncSend.closeFlag = true;
        }
        if (this.connectionHandler != null) {
            this.connectionHandler.preDisconnect(this);
        }
        if (!this.enablePooling) {
            CLISession.cliResourceManager.removeProvider(this.cliTransportProvider, this.cliProtocolOptions);
        }
        if (this.connectionHandler != null) {
            this.connectionHandler.postDisconnect(this);
        }
        this.cliTransportProvider = null;
        this.isSessionAlive = false;
        this.asyncSend = null;
        CLILogMgr.setDebugMessage("CLIUSER", "CLISession close(): closing the session", 2, null);
        if (this.cbt != null) {
            this.cbt.close();
        }
        synchronized (this.msgList) {
            this.msgList.notify();
        }
    }
    
    public void closeConnection() throws Exception {
        CLISession.cliResourceManager.removeProvider(CLISession.cliResourceManager.getProvider(this.cliProtocolOptions, this.cliTransportProviderClassName, this), this.cliProtocolOptions);
    }
    
    public void setRequestTimeout(final int requestTimeout) {
        if (requestTimeout > 0) {
            this.requestTimeout = requestTimeout;
        }
    }
    
    public int getRequestTimeout() {
        return this.requestTimeout;
    }
    
    public void setKeepAliveTimeout(final int keepAliveTimeout) {
        if (keepAliveTimeout > 0) {
            this.keepAliveTimeout = keepAliveTimeout;
        }
    }
    
    public int getKeepAliveTimeout() {
        return this.keepAliveTimeout;
    }
    
    void initTransportProvider() throws Exception {
        String nextToken = null;
        final URL resource = this.getClass().getResource(this.transportProviderFileName);
        if (resource == null) {
            throw new Exception("URL Invalid");
        }
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.openStream()));
            while (true) {
                String line;
                try {
                    line = bufferedReader.readLine();
                }
                catch (final Exception ex) {
                    throw new Exception("Cannot read file");
                }
                if (line == null) {
                    break;
                }
                if (line.trim().startsWith(" ") || line.trim().equals("")) {
                    continue;
                }
                if (line.trim().startsWith("#")) {
                    continue;
                }
                nextToken = new StringTokenizer(line).nextToken();
                break;
            }
            CLISession.providerNameFromFile = nextToken;
        }
        catch (final Exception ex2) {
            throw new Exception("Invalid File Name ");
        }
    }
    
    public static void setDebug(final boolean debugFlag) {
        CLILogMgr.setDebugOption(CLISession.debugFlag = debugFlag);
    }
    
    public static boolean isSetDebug() {
        return CLISession.debugFlag;
    }
    
    public void setDebugLevel(final int debugLevel) {
        if (debugLevel == 2 || debugLevel == 1) {
            CLISession.debugLevel = debugLevel;
        }
        if (CLISession.debugFlag) {
            if (CLISession.debugLevel == 1) {
                CLISession.clm.setDebugLevelForLogging(2);
            }
            else if (CLISession.debugLevel == 2) {
                CLISession.clm.setDebugLevelForLogging(4);
            }
        }
    }
    
    public int getDebugLevel() {
        return CLISession.debugLevel;
    }
    
    public String getInitialMessage() {
        if (this.cliProtocolOptions != null) {
            return this.cliProtocolOptions.getInitialMessage();
        }
        return null;
    }
    
    public void setIgnoreSpecialCharacters(final boolean ignoreSpecialCharacters) {
        this.ignoreSpecialCharacters = ignoreSpecialCharacters;
    }
    
    public boolean isSetIgnoreSpecialCharacters() {
        return this.ignoreSpecialCharacters;
    }
    
    byte[] stripSpecialCharacters(final byte[] array) {
        final int length = array.length;
        final byte[] array2 = new byte[length];
        int n = 0;
        for (int i = 0; i < length; ++i) {
            if ((array[i] < 127 && array[i] >= 32) || array[i] == 9 || array[i] == 10 || array[i] == 11) {
                array2[n] = array[i];
                ++n;
            }
        }
        final byte[] array3 = new byte[n];
        System.arraycopy(array2, 0, array3, 0, n);
        return array3;
    }
    
    public String handleControlSeq(final byte[] array, final int n) {
        if (Boolean.getBoolean("cli.skip.control.char.handling")) {
            return new String(array);
        }
        int n2 = 0;
        final StringBuffer sb = new StringBuffer(n);
        for (int i = 0; i < n; ++i) {
            if (array[i] == 27) {
                ++i;
                if (i < n) {
                    if (array[i] == 79) {
                        if (i < n - 1 && (array[i + 1] == 77 || array[i + 1] == 80 || array[i + 1] == 81 || array[i + 1] == 82 || array[i + 1] == 83 || array[i + 1] == 65 || array[i + 1] == 66 || array[i + 1] == 67 || array[i + 1] == 68 || array[i + 1] == 108 || array[i + 1] == 109 || array[i + 1] == 110 || (array[i + 1] >= 112 && array[i + 1] <= 121))) {
                            ++i;
                        }
                    }
                    else if (array[i] == 23 && i < n - 1) {
                        ++i;
                    }
                    else if (array[i] != 60 && array[i] != 61 && array[i] != 62 && array[i] != 55 && array[i] != 56) {
                        if (array[i] == 91 && array[i + 1] == 68 && n2 > 0) {
                            ++i;
                            --n2;
                        }
                        else if (array[i] == 91 && array[i + 1] == 67 && n2 < sb.length()) {
                            ++i;
                            ++n2;
                        }
                        else if (array[i] == 91 && array[i + 1] == 75) {
                            ++i;
                            sb.replace(n2, sb.length(), "");
                        }
                        else if (array[i] == 40 || array[i] == 41) {
                            if (i < n - 1 && (array[i + 1] == 48 || array[i + 1] == 49 || array[i + 1] == 50)) {
                                ++i;
                            }
                        }
                        else if (array[i] == 69) {
                            try {
                                sb.append("\r");
                            }
                            catch (final Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        else {
                            while (i < n) {
                                if (array[i] >= 65 && array[i] <= 122 && (array[i] <= 90 || array[i] >= 97)) {
                                    break;
                                }
                                ++i;
                            }
                        }
                    }
                }
            }
            else if (array[i] == 8 && n2 > 0) {
                --n2;
            }
            else if (array[i] != 7) {
                if (array[i] == 13) {
                    try {
                        sb.append("\r");
                    }
                    catch (final Exception ex2) {
                        ex2.printStackTrace();
                    }
                }
                else {
                    sb.replace(n2, sb.length(), (char)(array[i] & 0xFF) + "");
                    ++n2;
                }
            }
        }
        return sb.toString();
    }
    
    private boolean checkForMatch(final String s, final String s2, final int n) {
        String s3;
        if (s2.indexOf("[24;") >= 0) {
            final String replaceAll = s2.replaceAll("\\[24;", "");
            if (replaceAll.length() <= this.prevPos) {
                this.prevPos = 0;
            }
            s3 = replaceAll.substring(this.prevPos);
        }
        else {
            s3 = s2.substring(this.prevPos);
        }
        if (s != null) {
            final int n2 = s.length() + 40;
            if (s3.length() > n2) {
                s3 = s3.substring(s3.length() - n2);
            }
        }
        s3.length();
        return s == null || s3.indexOf(s) >= 0;
    }
    
    private boolean checkForMatchPattern(final String s, final String s2, final int n) {
        String s3;
        if (s2.indexOf("[24;") >= 0) {
            final String replaceAll = s2.replaceAll("\\[24;", "");
            if (replaceAll.length() <= this.prevPos) {
                this.prevPos = 0;
            }
            s3 = replaceAll.substring(this.prevPos);
        }
        else {
            s3 = s2.substring(this.prevPos);
        }
        if (s != null) {
            final int n2 = s.length() + 40;
            if (s3.length() > n2) {
                s3 = s3.substring(s3.length() - n2);
            }
        }
        s3.length();
        if (s == null || s3.indexOf(s) >= 0) {
            return true;
        }
        if (s != null && s.length() > 1) {
            try {
                if (Pattern.compile(s).matcher(s3).find()) {
                    return true;
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }
    
    private boolean checkForMatchList(final String[] array, final String s, final int n) {
        String s2 = s.substring(this.prevPos);
        final int n2 = 40;
        if (s2.length() > n2) {
            s2 = s2.substring(s2.length() - n2);
        }
        s2.length();
        for (int i = 0; i < array.length; ++i) {
            if (s2.indexOf(array[i]) >= 0) {
                return true;
            }
        }
        return false;
    }
    
    private boolean checkForMatches(final Properties properties, final StringBuffer sb, final int n) {
        final Enumeration<?> propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            final String matchedPrompt = (String)propertyNames.nextElement();
            if (this.checkForMatch(matchedPrompt, sb.toString(), n)) {
                this.matchedPrompt = matchedPrompt;
                return true;
            }
        }
        this.matchedPrompt = null;
        return false;
    }
    
    private CLIMessage truncate(final CLIMessage cliMessage, final CLIMessage cliMessage2) {
        final CLIMessage cliMessage3 = (CLIMessage)cliMessage.clone();
        String s = cliMessage3.getData();
        final boolean setCommandEcho = cliMessage2.isSetCommandEcho();
        final boolean setPromptEcho = cliMessage2.isSetPromptEcho();
        final String data = cliMessage2.getData();
        if (!setCommandEcho) {
            if (data != null && cliMessage3.getData().startsWith(data)) {
                s = s.substring(data.length());
            }
            CLILogMgr.setDebugMessage("CLIUSER", "CLISession truncate(): the response after truncating cmd is " + s, 2, null);
        }
        if (!setPromptEcho) {
            if (this.messagePrompt != null && cliMessage3.getData().endsWith(this.messagePrompt)) {
                s = s.substring(0, s.length() - this.messagePrompt.length());
            }
            CLILogMgr.setDebugMessage("CLIUSER", "CLISession truncate(): the response after truncating the prompt is " + s, 2, null);
        }
        cliMessage3.setData(this.handleControlSeq(s.getBytes(), s.length()));
        cliMessage3.setMsgID(cliMessage2.getMsgID());
        return cliMessage3;
    }
    
    private StringBuffer truncatePrompt(final StringBuffer sb, final String s) {
        if (sb.toString().endsWith(s)) {
            sb.setLength(sb.length() - s.length());
        }
        return sb;
    }
    
    private void startReadThread() {
        if (!this.isReadThreadAlive) {
            this.isReadThreadAlive = true;
            if (this.isOPMSession) {
                this.start();
            }
            else {
                CLISession.threadPoolExecutor.submit(this);
            }
        }
    }
    
    private void stopReadThread() {
        int n = 1;
        this.cliTransportProvider = null;
        this.isReadThreadAlive = false;
        while (this.isRunMethodActive && n < 100) {
            ++n;
            this.thread_sleep(100L);
        }
    }
    
    private static String setHomeDirectory() throws Exception {
        String s = null;
        final StringTokenizer stringTokenizer = new StringTokenizer(System.getProperty("java.class.path"), System.getProperty("path.separator"));
        while (stringTokenizer.hasMoreTokens()) {
            final String nextToken = stringTokenizer.nextToken();
            final int index;
            if ((index = nextToken.indexOf("AdventNetCLI.jar")) != -1) {
                s = nextToken.substring(0, index - 1);
                break;
            }
        }
        final String property = System.getProperty("file.separator");
        if (s != null && s.indexOf(property) != -1) {
            s = s.substring(0, s.lastIndexOf(property) + 1);
        }
        return s;
    }
    
    public String getHomeDirectory() {
        return CLISession.CLIHomeDir;
    }
    
    public void addConnectionListener(final ConnectionListener connectionListener) {
        if (connectionListener != null) {
            CLISession.cliResourceManager.addConnectionListener(connectionListener);
        }
    }
    
    public void removeConnectionListener(final ConnectionListener connectionListener) {
        if (connectionListener != null) {
            CLISession.cliResourceManager.removeConnectionListener(connectionListener);
        }
    }
    
    public Properties getCLIPromptAction() {
        return this.cliPromptAction;
    }
    
    public void setCLIPromptAction(final Properties cliPromptAction) {
        this.cliPromptAction = cliPromptAction;
    }
    
    public void setConnectionHandler(final ConnectionHandler connectionHandler) {
        CLILogMgr.CLIUSER.log("CLISession: Connection Handler set in the session", 4);
        this.connectionHandler = connectionHandler;
    }
    
    public ConnectionHandler getConnectionHandler() {
        return this.connectionHandler;
    }
    
    static {
        CLISession.cliResourceManager = null;
        CLISession.providerNameFromFile = null;
        CLISession.clm = null;
        CLISession.CLIHomeDir = null;
        CLISession.createLogs = true;
        CLISession.threadPoolExecutor = null;
        LicenseManager.addLicense("----BEGIN 3SP LICENSE----\r\nProduct : Maverick Legacy Client\r\nLicensee: Zoho Corporation Private Limited\r\nComments: OEM Support\r\nType    : OEM Support (Client)\r\nCreated : 01-Jun-2020\r\n\r\n3787207B28BF925BA600F3CF9CCEF4C832D2567C31641964\r\n0B52E532771632BD4EE641EC7FEF6C999650A7C67CDC6945\r\n162EB760DAEA404AFE5DBB2F0828651A9D9ED2F13DA7D47A\r\n4F12B3140930567063390CAA81744242D9574322EBAA4E03\r\nAF08FC24C2F2DCE8CDED5E94C3924AAF3B0A923FF5B85FFA\r\n289392BBD8FB689E55E6229E41517D5ABC33D10B9443FC41\r\n----END 3SP LICENSE----\r\n");
        final int int1 = Integer.parseInt(System.getProperty("clisession.minthreadcount", "20"));
        final int n = (int1 < 200) ? 200 : int1;
        CLISession.threadPoolExecutor = new ThreadPoolExecutor(int1, n, 600L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        if (new Boolean(System.getProperty("cli.debug", "false"))) {
            System.out.println("Starting CLISession with : " + int1 + " " + n);
        }
        CLISession.prevId = 0;
        CLISession.debugFlag = true;
        CLISession.debugLevel = 1;
    }
}
