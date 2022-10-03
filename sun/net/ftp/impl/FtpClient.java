package sun.net.ftp.impl;

import java.io.Closeable;
import java.util.Calendar;
import java.util.TimeZone;
import javax.net.ssl.SSLException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import sun.net.ftp.FtpDirEntry;
import java.text.ParseException;
import java.util.Date;
import javax.net.ssl.SSLSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.BufferedInputStream;
import java.io.UnsupportedEncodingException;
import java.io.BufferedOutputStream;
import java.util.Locale;
import sun.net.TelnetOutputStream;
import java.io.OutputStream;
import sun.net.TelnetInputStream;
import java.net.Inet6Address;
import java.net.ServerSocket;
import java.io.FileNotFoundException;
import java.net.SocketAddress;
import java.net.InetAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.net.ftp.FtpProtocolException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import sun.net.ftp.FtpDirParser;
import java.text.DateFormat;
import java.util.regex.Pattern;
import sun.net.ftp.FtpReplyCode;
import java.util.Vector;
import javax.net.ssl.SSLSocketFactory;
import java.net.InetSocketAddress;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.Proxy;
import sun.util.logging.PlatformLogger;

public class FtpClient extends sun.net.ftp.FtpClient
{
    private static int defaultSoTimeout;
    private static int defaultConnectTimeout;
    private static final PlatformLogger logger;
    private Proxy proxy;
    private Socket server;
    private PrintStream out;
    private InputStream in;
    private int readTimeout;
    private int connectTimeout;
    private static String encoding;
    private InetSocketAddress serverAddr;
    private boolean replyPending;
    private boolean loggedIn;
    private boolean useCrypto;
    private SSLSocketFactory sslFact;
    private Socket oldSocket;
    private Vector<String> serverResponse;
    private FtpReplyCode lastReplyCode;
    private String welcomeMsg;
    private final boolean passiveMode = true;
    private TransferType type;
    private long restartOffset;
    private long lastTransSize;
    private String lastFileName;
    private static String[] patStrings;
    private static int[][] patternGroups;
    private static Pattern[] patterns;
    private static Pattern linkp;
    private DateFormat df;
    private FtpDirParser parser;
    private FtpDirParser mlsxParser;
    private static Pattern transPat;
    private static Pattern epsvPat;
    private static Pattern pasvPat;
    private static String[] MDTMformats;
    private static SimpleDateFormat[] dateFormats;
    
    private static boolean isASCIISuperset(final String s) throws Exception {
        return Arrays.equals("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'();/?:@&=+$,".getBytes(s), new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 45, 95, 46, 33, 126, 42, 39, 40, 41, 59, 47, 63, 58, 64, 38, 61, 43, 36, 44 });
    }
    
    private void getTransferSize() {
        this.lastTransSize = -1L;
        final String lastResponseString = this.getLastResponseString();
        if (FtpClient.transPat == null) {
            FtpClient.transPat = Pattern.compile("150 Opening .*\\((\\d+) bytes\\).");
        }
        final Matcher matcher = FtpClient.transPat.matcher(lastResponseString);
        if (matcher.find()) {
            this.lastTransSize = Long.parseLong(matcher.group(1));
        }
    }
    
    private void getTransferName() {
        this.lastFileName = null;
        final String lastResponseString = this.getLastResponseString();
        int index = lastResponseString.indexOf("unique file name:");
        final int lastIndex = lastResponseString.lastIndexOf(41);
        if (index >= 0) {
            index += 17;
            this.lastFileName = lastResponseString.substring(index, lastIndex);
        }
    }
    
    private int readServerResponse() throws IOException {
        final StringBuffer sb = new StringBuffer(32);
        int n = -1;
        this.serverResponse.setSize(0);
        int int1;
        while (true) {
            int n2;
            if ((n2 = this.in.read()) != -1) {
                if (n2 == 13 && (n2 = this.in.read()) != 10) {
                    sb.append('\r');
                }
                sb.append((char)n2);
                if (n2 != 10) {
                    continue;
                }
            }
            final String string = sb.toString();
            sb.setLength(0);
            if (FtpClient.logger.isLoggable(PlatformLogger.Level.FINEST)) {
                FtpClient.logger.finest("Server [" + this.serverAddr + "] --> " + string);
            }
            if (string.length() == 0) {
                int1 = -1;
            }
            else {
                try {
                    int1 = Integer.parseInt(string.substring(0, 3));
                }
                catch (final NumberFormatException ex) {
                    int1 = -1;
                }
                catch (final StringIndexOutOfBoundsException ex2) {
                    continue;
                }
            }
            this.serverResponse.addElement(string);
            if (n != -1) {
                if (int1 != n) {
                    continue;
                }
                if (string.length() >= 4 && string.charAt(3) == '-') {
                    continue;
                }
                break;
            }
            else {
                if (string.length() < 4 || string.charAt(3) != '-') {
                    break;
                }
                n = int1;
            }
        }
        return int1;
    }
    
    private void sendServer(final String s) {
        this.out.print(s);
        if (FtpClient.logger.isLoggable(PlatformLogger.Level.FINEST)) {
            FtpClient.logger.finest("Server [" + this.serverAddr + "] <-- " + s);
        }
    }
    
    private String getResponseString() {
        return this.serverResponse.elementAt(0);
    }
    
    private Vector<String> getResponseStrings() {
        return this.serverResponse;
    }
    
    private boolean readReply() throws IOException {
        this.lastReplyCode = FtpReplyCode.find(this.readServerResponse());
        if (this.lastReplyCode.isPositivePreliminary()) {
            return this.replyPending = true;
        }
        if (this.lastReplyCode.isPositiveCompletion() || this.lastReplyCode.isPositiveIntermediate()) {
            if (this.lastReplyCode == FtpReplyCode.CLOSING_DATA_CONNECTION) {
                this.getTransferName();
            }
            return true;
        }
        return false;
    }
    
    private boolean issueCommand(final String s) throws IOException, FtpProtocolException {
        if (!this.isConnected()) {
            throw new IllegalStateException("Not connected");
        }
        if (this.replyPending) {
            try {
                this.completePending();
            }
            catch (final FtpProtocolException ex) {}
        }
        if (s.indexOf(10) != -1) {
            final FtpProtocolException ex2 = new FtpProtocolException("Illegal FTP command");
            ex2.initCause(new IllegalArgumentException("Illegal carriage return"));
            throw ex2;
        }
        this.sendServer(s + "\r\n");
        return this.readReply();
    }
    
    private void issueCommandCheck(final String s) throws FtpProtocolException, IOException {
        if (!this.issueCommand(s)) {
            throw new FtpProtocolException(s + ":" + this.getResponseString(), this.getLastReplyCode());
        }
    }
    
    private Socket openPassiveDataConnection(final String s) throws FtpProtocolException, IOException {
        InetSocketAddress unresolved;
        if (this.issueCommand("EPSV ALL")) {
            this.issueCommandCheck("EPSV");
            final String responseString = this.getResponseString();
            if (FtpClient.epsvPat == null) {
                FtpClient.epsvPat = Pattern.compile("^229 .* \\(\\|\\|\\|(\\d+)\\|\\)");
            }
            final Matcher matcher = FtpClient.epsvPat.matcher(responseString);
            if (!matcher.find()) {
                throw new FtpProtocolException("EPSV failed : " + responseString);
            }
            final int int1 = Integer.parseInt(matcher.group(1));
            final InetAddress inetAddress = this.server.getInetAddress();
            if (inetAddress != null) {
                unresolved = new InetSocketAddress(inetAddress, int1);
            }
            else {
                unresolved = InetSocketAddress.createUnresolved(this.serverAddr.getHostName(), int1);
            }
        }
        else {
            this.issueCommandCheck("PASV");
            final String responseString2 = this.getResponseString();
            if (FtpClient.pasvPat == null) {
                FtpClient.pasvPat = Pattern.compile("227 .* \\(?(\\d{1,3},\\d{1,3},\\d{1,3},\\d{1,3}),(\\d{1,3}),(\\d{1,3})\\)?");
            }
            final Matcher matcher2 = FtpClient.pasvPat.matcher(responseString2);
            if (!matcher2.find()) {
                throw new FtpProtocolException("PASV failed : " + responseString2);
            }
            unresolved = new InetSocketAddress(matcher2.group(1).replace(',', '.'), Integer.parseInt(matcher2.group(3)) + (Integer.parseInt(matcher2.group(2)) << 8));
        }
        Socket socket;
        if (this.proxy != null) {
            if (this.proxy.type() == Proxy.Type.SOCKS) {
                socket = AccessController.doPrivileged((PrivilegedAction<Socket>)new PrivilegedAction<Socket>() {
                    @Override
                    public Socket run() {
                        return new Socket(FtpClient.this.proxy);
                    }
                });
            }
            else {
                socket = new Socket(Proxy.NO_PROXY);
            }
        }
        else {
            socket = new Socket();
        }
        socket.bind(new InetSocketAddress(AccessController.doPrivileged((PrivilegedAction<InetAddress>)new PrivilegedAction<InetAddress>() {
            @Override
            public InetAddress run() {
                return FtpClient.this.server.getLocalAddress();
            }
        }), 0));
        if (this.connectTimeout >= 0) {
            socket.connect(unresolved, this.connectTimeout);
        }
        else if (FtpClient.defaultConnectTimeout > 0) {
            socket.connect(unresolved, FtpClient.defaultConnectTimeout);
        }
        else {
            socket.connect(unresolved);
        }
        if (this.readTimeout >= 0) {
            socket.setSoTimeout(this.readTimeout);
        }
        else if (FtpClient.defaultSoTimeout > 0) {
            socket.setSoTimeout(FtpClient.defaultSoTimeout);
        }
        if (this.useCrypto) {
            try {
                socket = this.sslFact.createSocket(socket, unresolved.getHostName(), unresolved.getPort(), true);
            }
            catch (final Exception ex) {
                throw new FtpProtocolException("Can't open secure data channel: " + ex);
            }
        }
        if (this.issueCommand(s)) {
            return socket;
        }
        socket.close();
        if (this.getLastReplyCode() == FtpReplyCode.FILE_UNAVAILABLE) {
            throw new FileNotFoundException(s);
        }
        throw new FtpProtocolException(s + ":" + this.getResponseString(), this.getLastReplyCode());
    }
    
    private Socket openDataConnection(final String s) throws FtpProtocolException, IOException {
        try {
            return this.openPassiveDataConnection(s);
        }
        catch (final FtpProtocolException ex) {
            final String message = ex.getMessage();
            if (!message.startsWith("PASV") && !message.startsWith("EPSV")) {
                throw ex;
            }
            if (this.proxy != null && this.proxy.type() == Proxy.Type.SOCKS) {
                throw new FtpProtocolException("Passive mode failed");
            }
            final ServerSocket serverSocket = new ServerSocket(0, 1, this.server.getLocalAddress());
            Socket socket;
            try {
                InetAddress inetAddress = serverSocket.getInetAddress();
                if (inetAddress.isAnyLocalAddress()) {
                    inetAddress = this.server.getLocalAddress();
                }
                if (!this.issueCommand("EPRT |" + ((inetAddress instanceof Inet6Address) ? "2" : "1") + "|" + inetAddress.getHostAddress() + "|" + serverSocket.getLocalPort() + "|") || !this.issueCommand(s)) {
                    String string = "PORT ";
                    final byte[] address = inetAddress.getAddress();
                    for (int i = 0; i < address.length; ++i) {
                        string = string + (address[i] & 0xFF) + ",";
                    }
                    this.issueCommandCheck(string + (serverSocket.getLocalPort() >>> 8 & 0xFF) + "," + (serverSocket.getLocalPort() & 0xFF));
                    this.issueCommandCheck(s);
                }
                if (this.connectTimeout >= 0) {
                    serverSocket.setSoTimeout(this.connectTimeout);
                }
                else if (FtpClient.defaultConnectTimeout > 0) {
                    serverSocket.setSoTimeout(FtpClient.defaultConnectTimeout);
                }
                socket = serverSocket.accept();
                if (this.readTimeout >= 0) {
                    socket.setSoTimeout(this.readTimeout);
                }
                else if (FtpClient.defaultSoTimeout > 0) {
                    socket.setSoTimeout(FtpClient.defaultSoTimeout);
                }
            }
            finally {
                serverSocket.close();
            }
            if (this.useCrypto) {
                try {
                    socket = this.sslFact.createSocket(socket, this.serverAddr.getHostName(), this.serverAddr.getPort(), true);
                }
                catch (final Exception ex2) {
                    throw new IOException(ex2.getLocalizedMessage());
                }
            }
            return socket;
        }
    }
    
    private InputStream createInputStream(final InputStream inputStream) {
        if (this.type == TransferType.ASCII) {
            return new TelnetInputStream(inputStream, false);
        }
        return inputStream;
    }
    
    private OutputStream createOutputStream(final OutputStream outputStream) {
        if (this.type == TransferType.ASCII) {
            return new TelnetOutputStream(outputStream, false);
        }
        return outputStream;
    }
    
    protected FtpClient() {
        this.readTimeout = -1;
        this.connectTimeout = -1;
        this.replyPending = false;
        this.loggedIn = false;
        this.useCrypto = false;
        this.serverResponse = new Vector<String>(1);
        this.lastReplyCode = null;
        this.type = TransferType.BINARY;
        this.restartOffset = 0L;
        this.lastTransSize = -1L;
        this.df = DateFormat.getDateInstance(2, Locale.US);
        this.parser = new DefaultParser();
        this.mlsxParser = new MLSxParser();
    }
    
    public static sun.net.ftp.FtpClient create() {
        return new FtpClient();
    }
    
    @Override
    public sun.net.ftp.FtpClient enablePassiveMode(final boolean b) {
        return this;
    }
    
    @Override
    public boolean isPassiveModeEnabled() {
        return true;
    }
    
    @Override
    public sun.net.ftp.FtpClient setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }
    
    @Override
    public int getConnectTimeout() {
        return this.connectTimeout;
    }
    
    @Override
    public sun.net.ftp.FtpClient setReadTimeout(final int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }
    
    @Override
    public int getReadTimeout() {
        return this.readTimeout;
    }
    
    @Override
    public sun.net.ftp.FtpClient setProxy(final Proxy proxy) {
        this.proxy = proxy;
        return this;
    }
    
    @Override
    public Proxy getProxy() {
        return this.proxy;
    }
    
    private void tryConnect(final InetSocketAddress inetSocketAddress, final int n) throws IOException {
        if (this.isConnected()) {
            this.disconnect();
        }
        this.server = this.doConnect(inetSocketAddress, n);
        try {
            this.out = new PrintStream(new BufferedOutputStream(this.server.getOutputStream()), true, FtpClient.encoding);
        }
        catch (final UnsupportedEncodingException ex) {
            throw new InternalError(FtpClient.encoding + "encoding not found", ex);
        }
        this.in = new BufferedInputStream(this.server.getInputStream());
    }
    
    private Socket doConnect(final InetSocketAddress inetSocketAddress, final int n) throws IOException {
        Socket socket;
        if (this.proxy != null) {
            if (this.proxy.type() == Proxy.Type.SOCKS) {
                socket = AccessController.doPrivileged((PrivilegedAction<Socket>)new PrivilegedAction<Socket>() {
                    @Override
                    public Socket run() {
                        return new Socket(FtpClient.this.proxy);
                    }
                });
            }
            else {
                socket = new Socket(Proxy.NO_PROXY);
            }
        }
        else {
            socket = new Socket();
        }
        if (n >= 0) {
            socket.connect(inetSocketAddress, n);
        }
        else if (this.connectTimeout >= 0) {
            socket.connect(inetSocketAddress, this.connectTimeout);
        }
        else if (FtpClient.defaultConnectTimeout > 0) {
            socket.connect(inetSocketAddress, FtpClient.defaultConnectTimeout);
        }
        else {
            socket.connect(inetSocketAddress);
        }
        if (this.readTimeout >= 0) {
            socket.setSoTimeout(this.readTimeout);
        }
        else if (FtpClient.defaultSoTimeout > 0) {
            socket.setSoTimeout(FtpClient.defaultSoTimeout);
        }
        return socket;
    }
    
    private void disconnect() throws IOException {
        if (this.isConnected()) {
            this.server.close();
        }
        this.server = null;
        this.in = null;
        this.out = null;
        this.lastTransSize = -1L;
        this.lastFileName = null;
        this.restartOffset = 0L;
        this.welcomeMsg = null;
        this.lastReplyCode = null;
        this.serverResponse.setSize(0);
    }
    
    @Override
    public boolean isConnected() {
        return this.server != null;
    }
    
    @Override
    public SocketAddress getServerAddress() {
        return (this.server == null) ? null : this.server.getRemoteSocketAddress();
    }
    
    @Override
    public sun.net.ftp.FtpClient connect(final SocketAddress socketAddress) throws FtpProtocolException, IOException {
        return this.connect(socketAddress, -1);
    }
    
    @Override
    public sun.net.ftp.FtpClient connect(final SocketAddress socketAddress, final int n) throws FtpProtocolException, IOException {
        if (!(socketAddress instanceof InetSocketAddress)) {
            throw new IllegalArgumentException("Wrong address type");
        }
        this.tryConnect(this.serverAddr = (InetSocketAddress)socketAddress, n);
        if (!this.readReply()) {
            throw new FtpProtocolException("Welcome message: " + this.getResponseString(), this.lastReplyCode);
        }
        this.welcomeMsg = this.getResponseString().substring(4);
        return this;
    }
    
    private void tryLogin(final String s, final char[] array) throws FtpProtocolException, IOException {
        this.issueCommandCheck("USER " + s);
        if (this.lastReplyCode == FtpReplyCode.NEED_PASSWORD && array != null && array.length > 0) {
            this.issueCommandCheck("PASS " + String.valueOf(array));
        }
    }
    
    @Override
    public sun.net.ftp.FtpClient login(final String s, final char[] array) throws FtpProtocolException, IOException {
        if (!this.isConnected()) {
            throw new FtpProtocolException("Not connected yet", FtpReplyCode.BAD_SEQUENCE);
        }
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("User name can't be null or empty");
        }
        this.tryLogin(s, array);
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.serverResponse.size(); ++i) {
            String substring = this.serverResponse.elementAt(i);
            if (substring != null) {
                if (substring.length() >= 4 && substring.startsWith("230")) {
                    substring = substring.substring(4);
                }
                sb.append(substring);
            }
        }
        this.welcomeMsg = sb.toString();
        this.loggedIn = true;
        return this;
    }
    
    @Override
    public sun.net.ftp.FtpClient login(final String s, final char[] array, final String s2) throws FtpProtocolException, IOException {
        if (!this.isConnected()) {
            throw new FtpProtocolException("Not connected yet", FtpReplyCode.BAD_SEQUENCE);
        }
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("User name can't be null or empty");
        }
        this.tryLogin(s, array);
        if (this.lastReplyCode == FtpReplyCode.NEED_ACCOUNT) {
            this.issueCommandCheck("ACCT " + s2);
        }
        final StringBuffer sb = new StringBuffer();
        if (this.serverResponse != null) {
            for (String substring : this.serverResponse) {
                if (substring != null) {
                    if (substring.length() >= 4 && substring.startsWith("230")) {
                        substring = substring.substring(4);
                    }
                    sb.append(substring);
                }
            }
        }
        this.welcomeMsg = sb.toString();
        this.loggedIn = true;
        return this;
    }
    
    @Override
    public void close() throws IOException {
        if (this.isConnected()) {
            try {
                this.issueCommand("QUIT");
            }
            catch (final FtpProtocolException ex) {}
            this.loggedIn = false;
        }
        this.disconnect();
    }
    
    @Override
    public boolean isLoggedIn() {
        return this.loggedIn;
    }
    
    @Override
    public sun.net.ftp.FtpClient changeDirectory(final String s) throws FtpProtocolException, IOException {
        if (s == null || "".equals(s)) {
            throw new IllegalArgumentException("directory can't be null or empty");
        }
        this.issueCommandCheck("CWD " + s);
        return this;
    }
    
    @Override
    public sun.net.ftp.FtpClient changeToParentDirectory() throws FtpProtocolException, IOException {
        this.issueCommandCheck("CDUP");
        return this;
    }
    
    @Override
    public String getWorkingDirectory() throws FtpProtocolException, IOException {
        this.issueCommandCheck("PWD");
        final String responseString = this.getResponseString();
        if (!responseString.startsWith("257")) {
            return null;
        }
        return responseString.substring(5, responseString.lastIndexOf(34));
    }
    
    @Override
    public sun.net.ftp.FtpClient setRestartOffset(final long restartOffset) {
        if (restartOffset < 0L) {
            throw new IllegalArgumentException("offset can't be negative");
        }
        this.restartOffset = restartOffset;
        return this;
    }
    
    @Override
    public sun.net.ftp.FtpClient getFile(final String s, final OutputStream outputStream) throws FtpProtocolException, IOException {
        final int n = 1500;
        if (this.restartOffset > 0L) {
            Socket openDataConnection;
            try {
                openDataConnection = this.openDataConnection("REST " + this.restartOffset);
            }
            finally {
                this.restartOffset = 0L;
            }
            this.issueCommandCheck("RETR " + s);
            this.getTransferSize();
            final InputStream inputStream = this.createInputStream(openDataConnection.getInputStream());
            final byte[] array = new byte[n * 10];
            int read;
            while ((read = inputStream.read(array)) >= 0) {
                if (read > 0) {
                    outputStream.write(array, 0, read);
                }
            }
            inputStream.close();
        }
        else {
            final Socket openDataConnection2 = this.openDataConnection("RETR " + s);
            this.getTransferSize();
            final InputStream inputStream2 = this.createInputStream(openDataConnection2.getInputStream());
            final byte[] array2 = new byte[n * 10];
            int read2;
            while ((read2 = inputStream2.read(array2)) >= 0) {
                if (read2 > 0) {
                    outputStream.write(array2, 0, read2);
                }
            }
            inputStream2.close();
        }
        return this.completePending();
    }
    
    @Override
    public InputStream getFileStream(final String s) throws FtpProtocolException, IOException {
        if (this.restartOffset > 0L) {
            Socket openDataConnection;
            try {
                openDataConnection = this.openDataConnection("REST " + this.restartOffset);
            }
            finally {
                this.restartOffset = 0L;
            }
            if (openDataConnection == null) {
                return null;
            }
            this.issueCommandCheck("RETR " + s);
            this.getTransferSize();
            return this.createInputStream(openDataConnection.getInputStream());
        }
        else {
            final Socket openDataConnection2 = this.openDataConnection("RETR " + s);
            if (openDataConnection2 == null) {
                return null;
            }
            this.getTransferSize();
            return this.createInputStream(openDataConnection2.getInputStream());
        }
    }
    
    @Override
    public OutputStream putFileStream(final String s, final boolean b) throws FtpProtocolException, IOException {
        final Socket openDataConnection = this.openDataConnection((b ? "STOU " : "STOR ") + s);
        if (openDataConnection == null) {
            return null;
        }
        return new TelnetOutputStream(openDataConnection.getOutputStream(), this.type == TransferType.BINARY);
    }
    
    @Override
    public sun.net.ftp.FtpClient putFile(final String s, final InputStream inputStream, final boolean b) throws FtpProtocolException, IOException {
        final String s2 = b ? "STOU " : "STOR ";
        final int n = 1500;
        if (this.type == TransferType.BINARY) {
            final OutputStream outputStream = this.createOutputStream(this.openDataConnection(s2 + s).getOutputStream());
            final byte[] array = new byte[n * 10];
            int read;
            while ((read = inputStream.read(array)) >= 0) {
                if (read > 0) {
                    outputStream.write(array, 0, read);
                }
            }
            outputStream.close();
        }
        return this.completePending();
    }
    
    @Override
    public sun.net.ftp.FtpClient appendFile(final String s, final InputStream inputStream) throws FtpProtocolException, IOException {
        final int n = 1500;
        final OutputStream outputStream = this.createOutputStream(this.openDataConnection("APPE " + s).getOutputStream());
        final byte[] array = new byte[n * 10];
        int read;
        while ((read = inputStream.read(array)) >= 0) {
            if (read > 0) {
                outputStream.write(array, 0, read);
            }
        }
        outputStream.close();
        return this.completePending();
    }
    
    @Override
    public sun.net.ftp.FtpClient rename(final String s, final String s2) throws FtpProtocolException, IOException {
        this.issueCommandCheck("RNFR " + s);
        this.issueCommandCheck("RNTO " + s2);
        return this;
    }
    
    @Override
    public sun.net.ftp.FtpClient deleteFile(final String s) throws FtpProtocolException, IOException {
        this.issueCommandCheck("DELE " + s);
        return this;
    }
    
    @Override
    public sun.net.ftp.FtpClient makeDirectory(final String s) throws FtpProtocolException, IOException {
        this.issueCommandCheck("MKD " + s);
        return this;
    }
    
    @Override
    public sun.net.ftp.FtpClient removeDirectory(final String s) throws FtpProtocolException, IOException {
        this.issueCommandCheck("RMD " + s);
        return this;
    }
    
    @Override
    public sun.net.ftp.FtpClient noop() throws FtpProtocolException, IOException {
        this.issueCommandCheck("NOOP");
        return this;
    }
    
    @Override
    public String getStatus(final String s) throws FtpProtocolException, IOException {
        this.issueCommandCheck((s == null) ? "STAT" : ("STAT " + s));
        final Vector<String> responseStrings = this.getResponseStrings();
        final StringBuffer sb = new StringBuffer();
        for (int i = 1; i < responseStrings.size() - 1; ++i) {
            sb.append((String)responseStrings.get(i));
        }
        return sb.toString();
    }
    
    @Override
    public List<String> getFeatures() throws FtpProtocolException, IOException {
        final ArrayList list = new ArrayList();
        this.issueCommandCheck("FEAT");
        final Vector<String> responseStrings = this.getResponseStrings();
        for (int i = 1; i < responseStrings.size() - 1; ++i) {
            final String s = responseStrings.get(i);
            list.add(s.substring(1, s.length() - 1));
        }
        return list;
    }
    
    @Override
    public sun.net.ftp.FtpClient abort() throws FtpProtocolException, IOException {
        this.issueCommandCheck("ABOR");
        return this;
    }
    
    @Override
    public sun.net.ftp.FtpClient completePending() throws FtpProtocolException, IOException {
        while (this.replyPending) {
            this.replyPending = false;
            if (!this.readReply()) {
                throw new FtpProtocolException(this.getLastResponseString(), this.lastReplyCode);
            }
        }
        return this;
    }
    
    @Override
    public sun.net.ftp.FtpClient reInit() throws FtpProtocolException, IOException {
        this.issueCommandCheck("REIN");
        this.loggedIn = false;
        if (this.useCrypto && this.server instanceof SSLSocket) {
            ((SSLSocket)this.server).getSession().invalidate();
            this.server = this.oldSocket;
            this.oldSocket = null;
            try {
                this.out = new PrintStream(new BufferedOutputStream(this.server.getOutputStream()), true, FtpClient.encoding);
            }
            catch (final UnsupportedEncodingException ex) {
                throw new InternalError(FtpClient.encoding + "encoding not found", ex);
            }
            this.in = new BufferedInputStream(this.server.getInputStream());
        }
        this.useCrypto = false;
        return this;
    }
    
    @Override
    public sun.net.ftp.FtpClient setType(final TransferType type) throws FtpProtocolException, IOException {
        String s = "NOOP";
        this.type = type;
        if (type == TransferType.ASCII) {
            s = "TYPE A";
        }
        if (type == TransferType.BINARY) {
            s = "TYPE I";
        }
        if (type == TransferType.EBCDIC) {
            s = "TYPE E";
        }
        this.issueCommandCheck(s);
        return this;
    }
    
    @Override
    public InputStream list(final String s) throws FtpProtocolException, IOException {
        final Socket openDataConnection = this.openDataConnection((s == null) ? "LIST" : ("LIST " + s));
        if (openDataConnection != null) {
            return this.createInputStream(openDataConnection.getInputStream());
        }
        return null;
    }
    
    @Override
    public InputStream nameList(final String s) throws FtpProtocolException, IOException {
        final Socket openDataConnection = this.openDataConnection((s == null) ? "NLST" : ("NLST " + s));
        if (openDataConnection != null) {
            return this.createInputStream(openDataConnection.getInputStream());
        }
        return null;
    }
    
    @Override
    public long getSize(final String s) throws FtpProtocolException, IOException {
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("path can't be null or empty");
        }
        this.issueCommandCheck("SIZE " + s);
        if (this.lastReplyCode == FtpReplyCode.FILE_STATUS) {
            final String responseString = this.getResponseString();
            return Long.parseLong(responseString.substring(4, responseString.length() - 1));
        }
        return -1L;
    }
    
    @Override
    public Date getLastModified(final String s) throws FtpProtocolException, IOException {
        this.issueCommandCheck("MDTM " + s);
        if (this.lastReplyCode == FtpReplyCode.FILE_STATUS) {
            final String substring = this.getResponseString().substring(4);
            Date parse = null;
            for (final SimpleDateFormat simpleDateFormat : FtpClient.dateFormats) {
                try {
                    parse = simpleDateFormat.parse(substring);
                }
                catch (final ParseException ex) {}
                if (parse != null) {
                    return parse;
                }
            }
        }
        return null;
    }
    
    @Override
    public sun.net.ftp.FtpClient setDirParser(final FtpDirParser parser) {
        this.parser = parser;
        return this;
    }
    
    @Override
    public Iterator<FtpDirEntry> listFiles(final String s) throws FtpProtocolException, IOException {
        Socket openDataConnection = null;
        try {
            openDataConnection = this.openDataConnection((s == null) ? "MLSD" : ("MLSD " + s));
        }
        catch (final FtpProtocolException ex) {}
        if (openDataConnection != null) {
            return new FtpFileIterator(this.mlsxParser, new BufferedReader(new InputStreamReader(openDataConnection.getInputStream())));
        }
        final Socket openDataConnection2 = this.openDataConnection((s == null) ? "LIST" : ("LIST " + s));
        if (openDataConnection2 != null) {
            return new FtpFileIterator(this.parser, new BufferedReader(new InputStreamReader(openDataConnection2.getInputStream())));
        }
        return null;
    }
    
    private boolean sendSecurityData(final byte[] array) throws IOException, FtpProtocolException {
        return this.issueCommand("ADAT " + new BASE64Encoder().encode(array));
    }
    
    private byte[] getSecurityData() {
        final String lastResponseString = this.getLastResponseString();
        if (lastResponseString.substring(4, 9).equalsIgnoreCase("ADAT=")) {
            final BASE64Decoder base64Decoder = new BASE64Decoder();
            try {
                return base64Decoder.decodeBuffer(lastResponseString.substring(9, lastResponseString.length() - 1));
            }
            catch (final IOException ex) {}
        }
        return null;
    }
    
    @Override
    public sun.net.ftp.FtpClient useKerberos() throws FtpProtocolException, IOException {
        return this;
    }
    
    @Override
    public String getWelcomeMsg() {
        return this.welcomeMsg;
    }
    
    @Override
    public FtpReplyCode getLastReplyCode() {
        return this.lastReplyCode;
    }
    
    @Override
    public String getLastResponseString() {
        final StringBuffer sb = new StringBuffer();
        if (this.serverResponse != null) {
            for (final String s : this.serverResponse) {
                if (s != null) {
                    sb.append(s);
                }
            }
        }
        return sb.toString();
    }
    
    @Override
    public long getLastTransferSize() {
        return this.lastTransSize;
    }
    
    @Override
    public String getLastFileName() {
        return this.lastFileName;
    }
    
    @Override
    public sun.net.ftp.FtpClient startSecureSession() throws FtpProtocolException, IOException {
        if (!this.isConnected()) {
            throw new FtpProtocolException("Not connected yet", FtpReplyCode.BAD_SEQUENCE);
        }
        if (this.sslFact == null) {
            try {
                this.sslFact = (SSLSocketFactory)SSLSocketFactory.getDefault();
            }
            catch (final Exception ex) {
                throw new IOException(ex.getLocalizedMessage());
            }
        }
        this.issueCommandCheck("AUTH TLS");
        Socket socket;
        try {
            socket = this.sslFact.createSocket(this.server, this.serverAddr.getHostName(), this.serverAddr.getPort(), true);
        }
        catch (final SSLException ex2) {
            try {
                this.disconnect();
            }
            catch (final Exception ex3) {}
            throw ex2;
        }
        this.oldSocket = this.server;
        this.server = socket;
        try {
            this.out = new PrintStream(new BufferedOutputStream(this.server.getOutputStream()), true, FtpClient.encoding);
        }
        catch (final UnsupportedEncodingException ex4) {
            throw new InternalError(FtpClient.encoding + "encoding not found", ex4);
        }
        this.in = new BufferedInputStream(this.server.getInputStream());
        this.issueCommandCheck("PBSZ 0");
        this.issueCommandCheck("PROT P");
        this.useCrypto = true;
        return this;
    }
    
    @Override
    public sun.net.ftp.FtpClient endSecureSession() throws FtpProtocolException, IOException {
        if (!this.useCrypto) {
            return this;
        }
        this.issueCommandCheck("CCC");
        this.issueCommandCheck("PROT C");
        this.useCrypto = false;
        this.server = this.oldSocket;
        this.oldSocket = null;
        try {
            this.out = new PrintStream(new BufferedOutputStream(this.server.getOutputStream()), true, FtpClient.encoding);
        }
        catch (final UnsupportedEncodingException ex) {
            throw new InternalError(FtpClient.encoding + "encoding not found", ex);
        }
        this.in = new BufferedInputStream(this.server.getInputStream());
        return this;
    }
    
    @Override
    public sun.net.ftp.FtpClient allocate(final long n) throws FtpProtocolException, IOException {
        this.issueCommandCheck("ALLO " + n);
        return this;
    }
    
    @Override
    public sun.net.ftp.FtpClient structureMount(final String s) throws FtpProtocolException, IOException {
        this.issueCommandCheck("SMNT " + s);
        return this;
    }
    
    @Override
    public String getSystem() throws FtpProtocolException, IOException {
        this.issueCommandCheck("SYST");
        return this.getResponseString().substring(4);
    }
    
    @Override
    public String getHelp(final String s) throws FtpProtocolException, IOException {
        this.issueCommandCheck("HELP " + s);
        final Vector<String> responseStrings = this.getResponseStrings();
        if (responseStrings.size() == 1) {
            return ((String)responseStrings.get(0)).substring(4);
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 1; i < responseStrings.size() - 1; ++i) {
            sb.append(((String)responseStrings.get(i)).substring(3));
        }
        return sb.toString();
    }
    
    @Override
    public sun.net.ftp.FtpClient siteCmd(final String s) throws FtpProtocolException, IOException {
        this.issueCommandCheck("SITE " + s);
        return this;
    }
    
    static {
        logger = PlatformLogger.getLogger("sun.net.ftp.FtpClient");
        FtpClient.encoding = "ISO8859_1";
        FtpClient.patStrings = new String[] { "([\\-ld](?:[r\\-][w\\-][x\\-]){3})\\s*\\d+ (\\w+)\\s*(\\w+)\\s*(\\d+)\\s*([A-Z][a-z][a-z]\\s*\\d+)\\s*(\\d\\d:\\d\\d)\\s*(\\p{Print}*)", "([\\-ld](?:[r\\-][w\\-][x\\-]){3})\\s*\\d+ (\\w+)\\s*(\\w+)\\s*(\\d+)\\s*([A-Z][a-z][a-z]\\s*\\d+)\\s*(\\d{4})\\s*(\\p{Print}*)", "(\\d{2}/\\d{2}/\\d{4})\\s*(\\d{2}:\\d{2}[ap])\\s*((?:[0-9,]+)|(?:<DIR>))\\s*(\\p{Graph}*)", "(\\d{2}-\\d{2}-\\d{2})\\s*(\\d{2}:\\d{2}[AP]M)\\s*((?:[0-9,]+)|(?:<DIR>))\\s*(\\p{Graph}*)" };
        FtpClient.patternGroups = new int[][] { { 7, 4, 5, 6, 0, 1, 2, 3 }, { 7, 4, 5, 0, 6, 1, 2, 3 }, { 4, 3, 1, 2, 0, 0, 0, 0 }, { 4, 3, 1, 2, 0, 0, 0, 0 } };
        FtpClient.linkp = Pattern.compile("(\\p{Print}+) \\-\\> (\\p{Print}+)$");
        final int[] array = { 0, 0 };
        final String[] array2 = { null };
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                array[0] = Integer.getInteger("sun.net.client.defaultReadTimeout", 300000);
                array[1] = Integer.getInteger("sun.net.client.defaultConnectTimeout", 300000);
                array2[0] = System.getProperty("file.encoding", "ISO8859_1");
                return null;
            }
        });
        if (array[0] == 0) {
            FtpClient.defaultSoTimeout = -1;
        }
        else {
            FtpClient.defaultSoTimeout = array[0];
        }
        if (array[1] == 0) {
            FtpClient.defaultConnectTimeout = -1;
        }
        else {
            FtpClient.defaultConnectTimeout = array[1];
        }
        FtpClient.encoding = array2[0];
        try {
            if (!isASCIISuperset(FtpClient.encoding)) {
                FtpClient.encoding = "ISO8859_1";
            }
        }
        catch (final Exception ex) {
            FtpClient.encoding = "ISO8859_1";
        }
        FtpClient.patterns = new Pattern[FtpClient.patStrings.length];
        for (int i = 0; i < FtpClient.patStrings.length; ++i) {
            FtpClient.patterns[i] = Pattern.compile(FtpClient.patStrings[i]);
        }
        FtpClient.transPat = null;
        FtpClient.epsvPat = null;
        FtpClient.pasvPat = null;
        FtpClient.MDTMformats = new String[] { "yyyyMMddHHmmss.SSS", "yyyyMMddHHmmss" };
        FtpClient.dateFormats = new SimpleDateFormat[FtpClient.MDTMformats.length];
        for (int j = 0; j < FtpClient.MDTMformats.length; ++j) {
            (FtpClient.dateFormats[j] = new SimpleDateFormat(FtpClient.MDTMformats[j])).setTimeZone(TimeZone.getTimeZone("GMT"));
        }
    }
    
    private class DefaultParser implements FtpDirParser
    {
        @Override
        public FtpDirEntry parseLine(final String s) {
            String s2 = null;
            String group = null;
            String group2 = null;
            String s3 = null;
            String group3 = null;
            String group4 = null;
            String group5 = null;
            boolean startsWith = false;
            final Calendar instance = Calendar.getInstance();
            final int value = instance.get(1);
            for (int i = 0; i < FtpClient.patterns.length; ++i) {
                final Matcher matcher = FtpClient.patterns[i].matcher(s);
                if (matcher.find()) {
                    s3 = matcher.group(FtpClient.patternGroups[i][0]);
                    group = matcher.group(FtpClient.patternGroups[i][1]);
                    s2 = matcher.group(FtpClient.patternGroups[i][2]);
                    if (FtpClient.patternGroups[i][4] > 0) {
                        s2 = s2 + ", " + matcher.group(FtpClient.patternGroups[i][4]);
                    }
                    else if (FtpClient.patternGroups[i][3] > 0) {
                        s2 = s2 + ", " + String.valueOf(value);
                    }
                    if (FtpClient.patternGroups[i][3] > 0) {
                        group2 = matcher.group(FtpClient.patternGroups[i][3]);
                    }
                    if (FtpClient.patternGroups[i][5] > 0) {
                        group3 = matcher.group(FtpClient.patternGroups[i][5]);
                        startsWith = group3.startsWith("d");
                    }
                    if (FtpClient.patternGroups[i][6] > 0) {
                        group4 = matcher.group(FtpClient.patternGroups[i][6]);
                    }
                    if (FtpClient.patternGroups[i][7] > 0) {
                        group5 = matcher.group(FtpClient.patternGroups[i][7]);
                    }
                    if ("<DIR>".equals(group)) {
                        startsWith = true;
                        group = null;
                    }
                }
            }
            if (s3 != null) {
                Date date;
                try {
                    date = FtpClient.this.df.parse(s2);
                }
                catch (final Exception ex) {
                    date = null;
                }
                if (date != null && group2 != null) {
                    final int index = group2.indexOf(":");
                    instance.setTime(date);
                    instance.set(10, Integer.parseInt(group2.substring(0, index)));
                    instance.set(12, Integer.parseInt(group2.substring(index + 1)));
                    date = instance.getTime();
                }
                final Matcher matcher2 = FtpClient.linkp.matcher(s3);
                if (matcher2.find()) {
                    s3 = matcher2.group(1);
                }
                final boolean[][] permissions = new boolean[3][3];
                for (int j = 0; j < 3; ++j) {
                    for (int k = 0; k < 3; ++k) {
                        permissions[j][k] = (group3.charAt(j * 3 + k) != '-');
                    }
                }
                final FtpDirEntry ftpDirEntry = new FtpDirEntry(s3);
                ftpDirEntry.setUser(group4).setGroup(group5);
                ftpDirEntry.setSize(Long.parseLong(group)).setLastModified(date);
                ftpDirEntry.setPermissions(permissions);
                ftpDirEntry.setType(startsWith ? FtpDirEntry.Type.DIR : ((s.charAt(0) == 'l') ? FtpDirEntry.Type.LINK : FtpDirEntry.Type.FILE));
                return ftpDirEntry;
            }
            return null;
        }
    }
    
    private class MLSxParser implements FtpDirParser
    {
        private SimpleDateFormat df;
        
        private MLSxParser() {
            this.df = new SimpleDateFormat("yyyyMMddhhmmss");
        }
        
        @Override
        public FtpDirEntry parseLine(String s) {
            final int lastIndex = s.lastIndexOf(";");
            String s2;
            if (lastIndex > 0) {
                s2 = s.substring(lastIndex + 1).trim();
                s = s.substring(0, lastIndex);
            }
            else {
                s2 = s.trim();
                s = "";
            }
            final FtpDirEntry ftpDirEntry = new FtpDirEntry(s2);
            while (!s.isEmpty()) {
                final int index = s.indexOf(";");
                String substring;
                if (index > 0) {
                    substring = s.substring(0, index);
                    s = s.substring(index + 1);
                }
                else {
                    substring = s;
                    s = "";
                }
                final int index2 = substring.indexOf("=");
                if (index2 > 0) {
                    ftpDirEntry.addFact(substring.substring(0, index2), substring.substring(index2 + 1));
                }
            }
            final String fact = ftpDirEntry.getFact("Size");
            if (fact != null) {
                ftpDirEntry.setSize(Long.parseLong(fact));
            }
            final String fact2 = ftpDirEntry.getFact("Modify");
            if (fact2 != null) {
                Date parse = null;
                try {
                    parse = this.df.parse(fact2);
                }
                catch (final ParseException ex) {}
                if (parse != null) {
                    ftpDirEntry.setLastModified(parse);
                }
            }
            final String fact3 = ftpDirEntry.getFact("Create");
            if (fact3 != null) {
                Date parse2 = null;
                try {
                    parse2 = this.df.parse(fact3);
                }
                catch (final ParseException ex2) {}
                if (parse2 != null) {
                    ftpDirEntry.setCreated(parse2);
                }
            }
            final String fact4 = ftpDirEntry.getFact("Type");
            if (fact4 != null) {
                if (fact4.equalsIgnoreCase("file")) {
                    ftpDirEntry.setType(FtpDirEntry.Type.FILE);
                }
                if (fact4.equalsIgnoreCase("dir")) {
                    ftpDirEntry.setType(FtpDirEntry.Type.DIR);
                }
                if (fact4.equalsIgnoreCase("cdir")) {
                    ftpDirEntry.setType(FtpDirEntry.Type.CDIR);
                }
                if (fact4.equalsIgnoreCase("pdir")) {
                    ftpDirEntry.setType(FtpDirEntry.Type.PDIR);
                }
            }
            return ftpDirEntry;
        }
    }
    
    private class FtpFileIterator implements Iterator<FtpDirEntry>, Closeable
    {
        private BufferedReader in;
        private FtpDirEntry nextFile;
        private FtpDirParser fparser;
        private boolean eof;
        
        public FtpFileIterator(final FtpDirParser fparser, final BufferedReader in) {
            this.in = null;
            this.nextFile = null;
            this.fparser = null;
            this.eof = false;
            this.in = in;
            this.fparser = fparser;
            this.readNext();
        }
        
        private void readNext() {
            this.nextFile = null;
            if (this.eof) {
                return;
            }
            try {
                String line;
                do {
                    line = this.in.readLine();
                    if (line != null) {
                        this.nextFile = this.fparser.parseLine(line);
                        if (this.nextFile != null) {
                            return;
                        }
                        continue;
                    }
                } while (line != null);
                this.in.close();
            }
            catch (final IOException ex) {}
            this.eof = true;
        }
        
        @Override
        public boolean hasNext() {
            return this.nextFile != null;
        }
        
        @Override
        public FtpDirEntry next() {
            final FtpDirEntry nextFile = this.nextFile;
            this.readNext();
            return nextFile;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        @Override
        public void close() throws IOException {
            if (this.in != null && !this.eof) {
                this.in.close();
            }
            this.eof = true;
            this.nextFile = null;
        }
    }
}
