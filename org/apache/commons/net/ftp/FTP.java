package org.apache.commons.net.ftp;

import java.net.Inet6Address;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Iterator;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import org.apache.commons.net.io.CRLFLineReader;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.commons.net.MalformedServerReplyException;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import org.apache.commons.net.ProtocolCommandSupport;
import java.util.ArrayList;
import org.apache.commons.net.SocketClient;

public class FTP extends SocketClient
{
    public static final int DEFAULT_DATA_PORT = 20;
    public static final int DEFAULT_PORT = 21;
    public static final int ASCII_FILE_TYPE = 0;
    public static final int EBCDIC_FILE_TYPE = 1;
    public static final int BINARY_FILE_TYPE = 2;
    public static final int LOCAL_FILE_TYPE = 3;
    public static final int NON_PRINT_TEXT_FORMAT = 4;
    public static final int TELNET_TEXT_FORMAT = 5;
    public static final int CARRIAGE_CONTROL_TEXT_FORMAT = 6;
    public static final int FILE_STRUCTURE = 7;
    public static final int RECORD_STRUCTURE = 8;
    public static final int PAGE_STRUCTURE = 9;
    public static final int STREAM_TRANSFER_MODE = 10;
    public static final int BLOCK_TRANSFER_MODE = 11;
    public static final int COMPRESSED_TRANSFER_MODE = 12;
    public static final String DEFAULT_CONTROL_ENCODING = "ISO-8859-1";
    public static final int REPLY_CODE_LEN = 3;
    private static final String __modes = "AEILNTCFRPSBC";
    protected int _replyCode;
    protected ArrayList<String> _replyLines;
    protected boolean _newReplyString;
    protected String _replyString;
    protected String _controlEncoding;
    protected ProtocolCommandSupport _commandSupport_;
    protected boolean strictMultilineParsing;
    private boolean strictReplyParsing;
    protected BufferedReader _controlInput_;
    protected BufferedWriter _controlOutput_;
    
    public FTP() {
        this.strictMultilineParsing = false;
        this.strictReplyParsing = true;
        this.setDefaultPort(21);
        this._replyLines = new ArrayList<String>();
        this._newReplyString = false;
        this._replyString = null;
        this._controlEncoding = "ISO-8859-1";
        this._commandSupport_ = new ProtocolCommandSupport(this);
    }
    
    private boolean __strictCheck(final String line, final String code) {
        return !line.startsWith(code) || line.charAt(3) != ' ';
    }
    
    private boolean __lenientCheck(final String line) {
        return line.length() <= 3 || line.charAt(3) == '-' || !Character.isDigit(line.charAt(0));
    }
    
    private void __getReply() throws IOException {
        this.__getReply(true);
    }
    
    protected void __getReplyNoReport() throws IOException {
        this.__getReply(false);
    }
    
    private void __getReply(final boolean reportReply) throws IOException {
        this._newReplyString = true;
        this._replyLines.clear();
        String line = this._controlInput_.readLine();
        if (line == null) {
            throw new FTPConnectionClosedException("Connection closed without indication.");
        }
        final int length = line.length();
        if (length < 3) {
            throw new MalformedServerReplyException("Truncated server reply: " + line);
        }
        String code = null;
        try {
            code = line.substring(0, 3);
            this._replyCode = Integer.parseInt(code);
        }
        catch (final NumberFormatException e) {
            throw new MalformedServerReplyException("Could not parse response code.\nServer Reply: " + line);
        }
        this._replyLines.add(line);
        if (length > 3) {
            final char sep = line.charAt(3);
            if (sep == '-') {
                while (true) {
                    line = this._controlInput_.readLine();
                    if (line == null) {
                        throw new FTPConnectionClosedException("Connection closed without indication.");
                    }
                    this._replyLines.add(line);
                    if (this.isStrictMultilineParsing()) {
                        if (this.__strictCheck(line, code)) {
                            continue;
                        }
                        break;
                    }
                    else {
                        if (!this.__lenientCheck(line)) {
                            break;
                        }
                        continue;
                    }
                }
            }
            else if (this.isStrictReplyParsing()) {
                if (length == 4) {
                    throw new MalformedServerReplyException("Truncated server reply: '" + line + "'");
                }
                if (sep != ' ') {
                    throw new MalformedServerReplyException("Invalid server reply: '" + line + "'");
                }
            }
        }
        else if (this.isStrictReplyParsing()) {
            throw new MalformedServerReplyException("Truncated server reply: '" + line + "'");
        }
        if (reportReply) {
            this.fireReplyReceived(this._replyCode, this.getReplyString());
        }
        if (this._replyCode == 421) {
            throw new FTPConnectionClosedException("FTP response 421 received.  Server closed connection.");
        }
    }
    
    @Override
    protected void _connectAction_() throws IOException {
        this._connectAction_(null);
    }
    
    protected void _connectAction_(final Reader socketIsReader) throws IOException {
        super._connectAction_();
        if (socketIsReader == null) {
            this._controlInput_ = new CRLFLineReader(new InputStreamReader(this._input_, this.getControlEncoding()));
        }
        else {
            this._controlInput_ = new CRLFLineReader(socketIsReader);
        }
        this._controlOutput_ = new BufferedWriter(new OutputStreamWriter(this._output_, this.getControlEncoding()));
        if (this.connectTimeout > 0) {
            final int original = this._socket_.getSoTimeout();
            this._socket_.setSoTimeout(this.connectTimeout);
            try {
                this.__getReply();
                if (FTPReply.isPositivePreliminary(this._replyCode)) {
                    this.__getReply();
                }
            }
            catch (final SocketTimeoutException e) {
                final IOException ioe = new IOException("Timed out waiting for initial connect reply");
                ioe.initCause(e);
                throw ioe;
            }
            finally {
                this._socket_.setSoTimeout(original);
            }
        }
        else {
            this.__getReply();
            if (FTPReply.isPositivePreliminary(this._replyCode)) {
                this.__getReply();
            }
        }
    }
    
    public void setControlEncoding(final String encoding) {
        this._controlEncoding = encoding;
    }
    
    public String getControlEncoding() {
        return this._controlEncoding;
    }
    
    @Override
    public void disconnect() throws IOException {
        super.disconnect();
        this._controlInput_ = null;
        this._controlOutput_ = null;
        this._newReplyString = false;
        this._replyString = null;
    }
    
    public int sendCommand(final String command, final String args) throws IOException {
        if (this._controlOutput_ == null) {
            throw new IOException("Connection is not open");
        }
        final String message = this.__buildMessage(command, args);
        this.__send(message);
        this.fireCommandSent(command, message);
        this.__getReply();
        return this._replyCode;
    }
    
    private String __buildMessage(final String command, final String args) {
        final StringBuilder __commandBuffer = new StringBuilder();
        __commandBuffer.append(command);
        if (args != null) {
            __commandBuffer.append(' ');
            __commandBuffer.append(args);
        }
        __commandBuffer.append("\r\n");
        return __commandBuffer.toString();
    }
    
    private void __send(final String message) throws IOException, FTPConnectionClosedException, SocketException {
        try {
            this._controlOutput_.write(message);
            this._controlOutput_.flush();
        }
        catch (final SocketException e) {
            if (!this.isConnected()) {
                throw new FTPConnectionClosedException("Connection unexpectedly closed.");
            }
            throw e;
        }
    }
    
    protected void __noop() throws IOException {
        final String msg = this.__buildMessage(FTPCmd.NOOP.getCommand(), null);
        this.__send(msg);
        this.__getReplyNoReport();
    }
    
    @Deprecated
    public int sendCommand(final int command, final String args) throws IOException {
        return this.sendCommand(FTPCommand.getCommand(command), args);
    }
    
    public int sendCommand(final FTPCmd command) throws IOException {
        return this.sendCommand(command, null);
    }
    
    public int sendCommand(final FTPCmd command, final String args) throws IOException {
        return this.sendCommand(command.getCommand(), args);
    }
    
    public int sendCommand(final String command) throws IOException {
        return this.sendCommand(command, null);
    }
    
    public int sendCommand(final int command) throws IOException {
        return this.sendCommand(command, null);
    }
    
    public int getReplyCode() {
        return this._replyCode;
    }
    
    public int getReply() throws IOException {
        this.__getReply();
        return this._replyCode;
    }
    
    public String[] getReplyStrings() {
        return this._replyLines.toArray(new String[this._replyLines.size()]);
    }
    
    public String getReplyString() {
        if (!this._newReplyString) {
            return this._replyString;
        }
        final StringBuilder buffer = new StringBuilder(256);
        for (final String line : this._replyLines) {
            buffer.append(line);
            buffer.append("\r\n");
        }
        this._newReplyString = false;
        return this._replyString = buffer.toString();
    }
    
    public int user(final String username) throws IOException {
        return this.sendCommand(FTPCmd.USER, username);
    }
    
    public int pass(final String password) throws IOException {
        return this.sendCommand(FTPCmd.PASS, password);
    }
    
    public int acct(final String account) throws IOException {
        return this.sendCommand(FTPCmd.ACCT, account);
    }
    
    public int abor() throws IOException {
        return this.sendCommand(FTPCmd.ABOR);
    }
    
    public int cwd(final String directory) throws IOException {
        return this.sendCommand(FTPCmd.CWD, directory);
    }
    
    public int cdup() throws IOException {
        return this.sendCommand(FTPCmd.CDUP);
    }
    
    public int quit() throws IOException {
        return this.sendCommand(FTPCmd.QUIT);
    }
    
    public int rein() throws IOException {
        return this.sendCommand(FTPCmd.REIN);
    }
    
    public int smnt(final String dir) throws IOException {
        return this.sendCommand(FTPCmd.SMNT, dir);
    }
    
    public int port(final InetAddress host, final int port) throws IOException {
        final StringBuilder info = new StringBuilder(24);
        info.append(host.getHostAddress().replace('.', ','));
        int num = port >>> 8;
        info.append(',');
        info.append(num);
        info.append(',');
        num = (port & 0xFF);
        info.append(num);
        return this.sendCommand(FTPCmd.PORT, info.toString());
    }
    
    public int eprt(final InetAddress host, final int port) throws IOException {
        final StringBuilder info = new StringBuilder();
        String h = host.getHostAddress();
        final int num = h.indexOf("%");
        if (num > 0) {
            h = h.substring(0, num);
        }
        info.append("|");
        if (host instanceof Inet4Address) {
            info.append("1");
        }
        else if (host instanceof Inet6Address) {
            info.append("2");
        }
        info.append("|");
        info.append(h);
        info.append("|");
        info.append(port);
        info.append("|");
        return this.sendCommand(FTPCmd.EPRT, info.toString());
    }
    
    public int pasv() throws IOException {
        return this.sendCommand(FTPCmd.PASV);
    }
    
    public int epsv() throws IOException {
        return this.sendCommand(FTPCmd.EPSV);
    }
    
    public int type(final int fileType, final int formatOrByteSize) throws IOException {
        final StringBuilder arg = new StringBuilder();
        arg.append("AEILNTCFRPSBC".charAt(fileType));
        arg.append(' ');
        if (fileType == 3) {
            arg.append(formatOrByteSize);
        }
        else {
            arg.append("AEILNTCFRPSBC".charAt(formatOrByteSize));
        }
        return this.sendCommand(FTPCmd.TYPE, arg.toString());
    }
    
    public int type(final int fileType) throws IOException {
        return this.sendCommand(FTPCmd.TYPE, "AEILNTCFRPSBC".substring(fileType, fileType + 1));
    }
    
    public int stru(final int structure) throws IOException {
        return this.sendCommand(FTPCmd.STRU, "AEILNTCFRPSBC".substring(structure, structure + 1));
    }
    
    public int mode(final int mode) throws IOException {
        return this.sendCommand(FTPCmd.MODE, "AEILNTCFRPSBC".substring(mode, mode + 1));
    }
    
    public int retr(final String pathname) throws IOException {
        return this.sendCommand(FTPCmd.RETR, pathname);
    }
    
    public int stor(final String pathname) throws IOException {
        return this.sendCommand(FTPCmd.STOR, pathname);
    }
    
    public int stou() throws IOException {
        return this.sendCommand(FTPCmd.STOU);
    }
    
    public int stou(final String pathname) throws IOException {
        return this.sendCommand(FTPCmd.STOU, pathname);
    }
    
    public int appe(final String pathname) throws IOException {
        return this.sendCommand(FTPCmd.APPE, pathname);
    }
    
    public int allo(final int bytes) throws IOException {
        return this.sendCommand(FTPCmd.ALLO, Integer.toString(bytes));
    }
    
    public int feat() throws IOException {
        return this.sendCommand(FTPCmd.FEAT);
    }
    
    public int allo(final int bytes, final int recordSize) throws IOException {
        return this.sendCommand(FTPCmd.ALLO, Integer.toString(bytes) + " R " + Integer.toString(recordSize));
    }
    
    public int rest(final String marker) throws IOException {
        return this.sendCommand(FTPCmd.REST, marker);
    }
    
    public int mdtm(final String file) throws IOException {
        return this.sendCommand(FTPCmd.MDTM, file);
    }
    
    public int mfmt(final String pathname, final String timeval) throws IOException {
        return this.sendCommand(FTPCmd.MFMT, timeval + " " + pathname);
    }
    
    public int rnfr(final String pathname) throws IOException {
        return this.sendCommand(FTPCmd.RNFR, pathname);
    }
    
    public int rnto(final String pathname) throws IOException {
        return this.sendCommand(FTPCmd.RNTO, pathname);
    }
    
    public int dele(final String pathname) throws IOException {
        return this.sendCommand(FTPCmd.DELE, pathname);
    }
    
    public int rmd(final String pathname) throws IOException {
        return this.sendCommand(FTPCmd.RMD, pathname);
    }
    
    public int mkd(final String pathname) throws IOException {
        return this.sendCommand(FTPCmd.MKD, pathname);
    }
    
    public int pwd() throws IOException {
        return this.sendCommand(FTPCmd.PWD);
    }
    
    public int list() throws IOException {
        return this.sendCommand(FTPCmd.LIST);
    }
    
    public int list(final String pathname) throws IOException {
        return this.sendCommand(FTPCmd.LIST, pathname);
    }
    
    public int mlsd() throws IOException {
        return this.sendCommand(FTPCmd.MLSD);
    }
    
    public int mlsd(final String path) throws IOException {
        return this.sendCommand(FTPCmd.MLSD, path);
    }
    
    public int mlst() throws IOException {
        return this.sendCommand(FTPCmd.MLST);
    }
    
    public int mlst(final String path) throws IOException {
        return this.sendCommand(FTPCmd.MLST, path);
    }
    
    public int nlst() throws IOException {
        return this.sendCommand(FTPCmd.NLST);
    }
    
    public int nlst(final String pathname) throws IOException {
        return this.sendCommand(FTPCmd.NLST, pathname);
    }
    
    public int site(final String parameters) throws IOException {
        return this.sendCommand(FTPCmd.SITE, parameters);
    }
    
    public int syst() throws IOException {
        return this.sendCommand(FTPCmd.SYST);
    }
    
    public int stat() throws IOException {
        return this.sendCommand(FTPCmd.STAT);
    }
    
    public int stat(final String pathname) throws IOException {
        return this.sendCommand(FTPCmd.STAT, pathname);
    }
    
    public int help() throws IOException {
        return this.sendCommand(FTPCmd.HELP);
    }
    
    public int help(final String command) throws IOException {
        return this.sendCommand(FTPCmd.HELP, command);
    }
    
    public int noop() throws IOException {
        return this.sendCommand(FTPCmd.NOOP);
    }
    
    public boolean isStrictMultilineParsing() {
        return this.strictMultilineParsing;
    }
    
    public void setStrictMultilineParsing(final boolean strictMultilineParsing) {
        this.strictMultilineParsing = strictMultilineParsing;
    }
    
    public boolean isStrictReplyParsing() {
        return this.strictReplyParsing;
    }
    
    public void setStrictReplyParsing(final boolean strictReplyParsing) {
        this.strictReplyParsing = strictReplyParsing;
    }
    
    @Override
    protected ProtocolCommandSupport getCommandSupport() {
        return this._commandSupport_;
    }
}
