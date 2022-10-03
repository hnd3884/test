package com.sshtools.sftp;

import com.maverick.util.IOUtil;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.maverick.util.UnsignedInteger64;
import com.maverick.ssh.SshIOException;
import com.maverick.sftp.SftpFileOutputStream;
import java.io.FileInputStream;
import com.maverick.sftp.SftpFileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import com.maverick.util.EOLProcessor;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import com.maverick.sftp.TransferCancelledException;
import java.io.FileNotFoundException;
import com.maverick.sftp.FileTransferProgress;
import java.io.IOException;
import java.util.StringTokenizer;
import com.maverick.util.UnsignedInteger32;
import com.maverick.sftp.SftpFileAttributes;
import java.util.Enumeration;
import java.io.File;
import com.maverick.sftp.SftpFile;
import com.maverick.events.Event;
import com.maverick.events.EventServiceImplementation;
import java.io.UnsupportedEncodingException;
import com.maverick.ssh2.Ssh2Session;
import com.maverick.ssh.ChannelEventListener;
import com.maverick.ssh2.Ssh2Client;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.maverick.ssh1.Ssh1Client;
import com.maverick.ssh.SshSession;
import com.maverick.ssh.ChannelOpenException;
import com.maverick.ssh.SshException;
import com.maverick.sftp.SftpStatusException;
import com.maverick.ssh.SshClient;
import java.util.Vector;
import com.maverick.sftp.SftpSubsystemChannel;
import com.maverick.ssh.Client;

public class SftpClient implements Client
{
    SftpSubsystemChannel ic;
    String ec;
    String fc;
    private int dc;
    private int jc;
    private int cc;
    int lc;
    public static final int MODE_BINARY = 1;
    public static final int MODE_TEXT = 2;
    public static final int EOL_CRLF = 1;
    public static final int EOL_LF = 2;
    public static final int EOL_CR = 3;
    private int gc;
    private int hc;
    private Vector mc;
    public static final int NoSyntax = 0;
    public static final int GlobSyntax = 1;
    public static final int Perl5Syntax = 2;
    private int kc;
    
    public SftpClient(final SshClient sshClient) throws SftpStatusException, SshException, ChannelOpenException {
        this(sshClient, SftpSubsystemChannel.MAX_VERSION);
    }
    
    public SftpClient(final SshSession sshSession) throws SftpStatusException, SshException {
        this(sshSession, SftpSubsystemChannel.MAX_VERSION);
    }
    
    public SftpClient(final SshSession sshSession, final int n) throws SftpStatusException, SshException {
        this.dc = 4096;
        this.jc = 100;
        this.cc = -1;
        this.lc = 18;
        this.gc = 1;
        this.hc = 1;
        this.mc = new Vector();
        this.kc = 1;
        this.b(sshSession, n);
    }
    
    public SftpClient(final SshClient sshClient, final int n) throws SftpStatusException, SshException, ChannelOpenException {
        this.dc = 4096;
        this.jc = 100;
        this.cc = -1;
        this.lc = 18;
        this.gc = 1;
        this.hc = 1;
        this.mc = new Vector();
        this.kc = 1;
        SshSession openSessionChannel = null;
        SshSession sshSession = null;
        if (sshClient instanceof Ssh1Client) {
            try {
                openSessionChannel = sshClient.openSessionChannel();
                if (openSessionChannel.executeCommand("find / -name 'sftp-server'")) {
                    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openSessionChannel.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.startsWith("/") && line.endsWith("sftp-server")) {
                            sshClient.getContext().setSFTPProvider(line);
                        }
                    }
                    bufferedReader.close();
                }
            }
            catch (final Exception ex) {}
            finally {
                if (openSessionChannel != null) {
                    openSessionChannel.close();
                }
                sshSession = sshClient.openSessionChannel();
            }
        }
        else {
            sshSession = ((Ssh2Client)sshClient).openSessionChannel(131072, 16384, null);
        }
        if (sshSession instanceof Ssh2Session) {
            final Ssh2Session ssh2Session = (Ssh2Session)sshSession;
            if (!ssh2Session.startSubsystem("sftp") && !ssh2Session.executeCommand(sshClient.getContext().getSFTPProvider())) {
                ssh2Session.close();
                throw new SshException("Failed to start SFTP subsystem or SFTP provider " + sshClient.getContext().getSFTPProvider(), 6);
            }
        }
        else if (!sshSession.executeCommand(sshClient.getContext().getSFTPProvider())) {
            sshSession.close();
            throw new SshException("Failed to launch SFTP provider " + sshClient.getContext().getSFTPProvider(), 6);
        }
        this.b(sshSession, n);
    }
    
    private void b(final SshSession sshSession, final int n) throws SftpStatusException, SshException {
        this.ic = new SftpSubsystemChannel(sshSession, n);
        try {
            this.ic.initialize();
        }
        catch (final UnsupportedEncodingException ex) {}
        this.ec = this.ic.getDefaultDirectory();
        String property = "";
        try {
            property = System.getProperty("user.home");
        }
        catch (final SecurityException ex2) {}
        this.fc = property;
        EventServiceImplementation.getInstance().fireEvent(new Event(this, 22, true));
    }
    
    public void setBlockSize(final int dc) {
        if (dc < 512) {
            throw new IllegalArgumentException("Block size must be greater than 512");
        }
        this.dc = dc;
    }
    
    public SftpSubsystemChannel getSubsystemChannel() {
        return this.ic;
    }
    
    public void setTransferMode(final int hc) {
        if (hc != 1 && hc != 2) {
            throw new IllegalArgumentException("Mode can only be either binary or text");
        }
        this.hc = hc;
    }
    
    public void setRemoteEOL(final int gc) {
        this.gc = gc;
    }
    
    public int getTransferMode() {
        return this.hc;
    }
    
    public void setBufferSize(final int cc) {
        this.cc = cc;
    }
    
    public void setMaxAsyncRequests(final int jc) {
        if (jc < 1) {
            throw new IllegalArgumentException("Maximum asynchronous requests must be greater or equal to 1");
        }
        this.jc = jc;
    }
    
    public int umask(final int lc) {
        final int lc2 = this.lc;
        this.lc = lc;
        return lc2;
    }
    
    public SftpFile openFile(final String s) throws SftpStatusException, SshException {
        if (this.hc == 2 && this.ic.getVersion() > 3) {
            return this.ic.openFile(this.c(s), 65);
        }
        return this.ic.openFile(this.c(s), 1);
    }
    
    public void cd(final String s) throws SftpStatusException, SshException {
        String ec;
        if (s == null || s.equals("")) {
            ec = this.ic.getDefaultDirectory();
        }
        else {
            ec = this.ic.getAbsolutePath(this.c(s));
        }
        if (!ec.equals("") && !this.ic.getAttributes(ec).isDirectory()) {
            throw new SftpStatusException(4, s + " is not a directory");
        }
        this.ec = ec;
    }
    
    public String getDefaultDirectory() throws SftpStatusException, SshException {
        return this.ic.getDefaultDirectory();
    }
    
    public void cdup() throws SftpStatusException, SshException {
        final SftpFile parent = this.ic.getFile(this.ec).getParent();
        if (parent != null) {
            this.ec = parent.getAbsolutePath();
        }
    }
    
    private File e(final String s) {
        File file = new File(s);
        if (!file.isAbsolute()) {
            file = new File(this.fc, s);
        }
        return file;
    }
    
    private boolean i(String trim) {
        trim = trim.trim();
        return trim.length() > 2 && ((((trim.charAt(0) >= 'a' && trim.charAt(0) <= 'z') || (trim.charAt(0) >= 'A' && trim.charAt(0) <= 'Z')) && trim.charAt(1) == ':' && trim.charAt(2) == '/') || trim.charAt(2) == '\\');
    }
    
    public void addCustomRoot(final String s) {
        this.mc.addElement(s);
    }
    
    public void removeCustomRoot(final String s) {
        this.mc.removeElement(s);
    }
    
    private boolean h(final String s) {
        final Enumeration elements = this.mc.elements();
        while (elements != null && elements.hasMoreElements()) {
            if (s.startsWith((String)elements.nextElement())) {
                return true;
            }
        }
        return false;
    }
    
    private String c(final String s) throws SftpStatusException {
        this.c();
        String string;
        if (!s.startsWith("/") && !s.startsWith(this.ec) && !this.i(s) && !this.h(s)) {
            string = this.ec + (this.ec.endsWith("/") ? "" : "/") + s;
        }
        else {
            string = s;
        }
        if (!string.equals("/") && string.endsWith("/")) {
            return string.substring(0, string.length() - 1);
        }
        return string;
    }
    
    private void c() throws SftpStatusException {
        if (this.ic.isClosed()) {
            throw new SftpStatusException(7, "The SFTP connection has been closed");
        }
    }
    
    public void mkdir(final String s) throws SftpStatusException, SshException {
        final String c = this.c(s);
        SftpFileAttributes attributes;
        try {
            attributes = this.ic.getAttributes(c);
        }
        catch (final SftpStatusException ex) {
            final SftpFileAttributes sftpFileAttributes = new SftpFileAttributes(this.ic, 2);
            sftpFileAttributes.setPermissions(new UnsignedInteger32(0x1FF ^ this.lc));
            this.ic.makeDirectory(c, sftpFileAttributes);
            return;
        }
        if (!attributes.isDirectory()) {
            throw new SftpStatusException(4, "File already exists named " + s);
        }
    }
    
    public void mkdirs(final String s) throws SftpStatusException, SshException {
        final StringTokenizer stringTokenizer = new StringTokenizer(s, "/");
        String string = s.startsWith("/") ? "/" : "";
        while (stringTokenizer.hasMoreElements()) {
            final String string2 = string + (String)stringTokenizer.nextElement();
            try {
                this.stat(string2);
            }
            catch (final SftpStatusException ex) {
                try {
                    this.mkdir(string2);
                }
                catch (final SftpStatusException ex2) {
                    if (ex2.getStatus() == 3) {
                        throw ex2;
                    }
                }
            }
            string = string2 + "/";
        }
    }
    
    public boolean isDirectoryOrLinkedDirectory(final SftpFile sftpFile) throws SftpStatusException, SshException {
        return sftpFile.isDirectory() || (sftpFile.isLink() && this.stat(sftpFile.getAbsolutePath()).isDirectory());
    }
    
    public String pwd() {
        return this.ec;
    }
    
    public SftpFile[] ls() throws SftpStatusException, SshException {
        return this.ls(this.ec);
    }
    
    public SftpFile[] ls(final String s) throws SftpStatusException, SshException {
        final SftpFile openDirectory = this.ic.openDirectory(this.c(s));
        final Vector vector = new Vector();
        while (this.ic.listChildren(openDirectory, vector) > -1) {}
        openDirectory.close();
        final SftpFile[] array = new SftpFile[vector.size()];
        int n = 0;
        final Enumeration elements = vector.elements();
        while (elements.hasMoreElements()) {
            array[n++] = (SftpFile)elements.nextElement();
        }
        return array;
    }
    
    public void lcd(final String s) throws SftpStatusException {
        File file;
        if (!g(s)) {
            file = new File(this.fc, s);
        }
        else {
            file = new File(s);
        }
        if (!file.isDirectory()) {
            throw new SftpStatusException(4, s + " is not a directory");
        }
        try {
            this.fc = file.getCanonicalPath();
        }
        catch (final IOException ex) {
            throw new SftpStatusException(4, "Failed to canonicalize path " + s);
        }
    }
    
    private static boolean g(final String s) {
        return new File(s).isAbsolute();
    }
    
    public String lpwd() {
        return this.fc;
    }
    
    public SftpFileAttributes get(final String s, final FileTransferProgress fileTransferProgress) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        return this.get(s, fileTransferProgress, false);
    }
    
    public SftpFileAttributes get(final String s, final FileTransferProgress fileTransferProgress, final boolean b) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        String substring;
        if (s.lastIndexOf("/") > -1) {
            substring = s.substring(s.lastIndexOf("/") + 1);
        }
        else {
            substring = s;
        }
        return this.get(s, substring, fileTransferProgress, b);
    }
    
    public SftpFileAttributes get(final String s, final boolean b) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        return this.get(s, (FileTransferProgress)null, b);
    }
    
    public SftpFileAttributes get(final String s) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        return this.get(s, (FileTransferProgress)null);
    }
    
    public SftpFileAttributes get(final String s, final String s2, final FileTransferProgress fileTransferProgress) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        return this.get(s, s2, fileTransferProgress, false);
    }
    
    public SftpFileAttributes get(final String s, final String s2, final FileTransferProgress fileTransferProgress, final boolean b) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        OutputStream outputStream = null;
        SftpFileAttributes value = null;
        File e = this.e(s2);
        if (!e.exists()) {
            new File(e.getParent()).mkdirs();
        }
        if (e.isDirectory()) {
            final int lastIndex;
            if ((lastIndex = s.lastIndexOf(47)) > -1) {
                e = new File(e, s.substring(lastIndex));
            }
            else {
                e = new File(e, s);
            }
        }
        this.stat(s);
        long length = 0L;
        try {
            if (b && e.exists()) {
                length = e.length();
                final RandomAccessFile randomAccessFile = new RandomAccessFile(e, "rw");
                randomAccessFile.seek(length);
                outputStream = new _b(randomAccessFile);
            }
            else {
                outputStream = new FileOutputStream(e);
            }
            if (this.hc == 2) {
                int gc = this.gc;
                final int n = 0;
                byte[] array = null;
                if (this.ic.getVersion() <= 3 && this.ic.getExtension("newline@vandyke.com") != null) {
                    array = this.ic.getExtension("newline@vandyke.com").getBytes();
                }
                else if (this.ic.getVersion() > 3) {
                    array = this.ic.getCanonicalNewline().getBytes();
                }
                if (array != null) {
                    switch (array.length) {
                        case 1: {
                            if (array[0] == 13) {
                                gc = 3;
                                break;
                            }
                            if (array[0] == 10) {
                                gc = 2;
                                break;
                            }
                            throw new SftpStatusException(100, "Unsupported text mode: invalid newline character");
                        }
                        case 2: {
                            if (array[0] == 13 && array[1] == 10) {
                                gc = 1;
                                break;
                            }
                            throw new SftpStatusException(100, "Unsupported text mode: invalid newline characters");
                        }
                        default: {
                            throw new SftpStatusException(100, "Unsupported text mode: newline length > 2");
                        }
                    }
                }
                outputStream = EOLProcessor.createOutputStream(gc, n, outputStream);
            }
            value = this.get(s, outputStream, fileTransferProgress, length);
            return value;
        }
        catch (final IOException ex) {
            throw new SftpStatusException(4, "Failed to open outputstream to " + s2);
        }
        finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (value != null) {
                    e.getClass().getMethod("setLastModified", Long.TYPE).invoke(e, new Long(value.getModifiedTime().longValue() * 1000L));
                }
            }
            catch (final Throwable t) {}
        }
    }
    
    public SftpFileAttributes get(final String s, final String s2, final boolean b) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        return this.get(s, s2, null, b);
    }
    
    public SftpFileAttributes get(final String s, final String s2) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        return this.get(s, s2, false);
    }
    
    public SftpFileAttributes get(final String s, final OutputStream outputStream, final FileTransferProgress fileTransferProgress) throws SftpStatusException, SshException, TransferCancelledException {
        return this.get(s, outputStream, fileTransferProgress, 0L);
    }
    
    public void setRegularExpressionSyntax(final int kc) {
        this.kc = kc;
    }
    
    private SftpFile[] d(final String s) throws SftpStatusException, SshException {
        final int lastIndex;
        String s2;
        String s3;
        if ((lastIndex = s.lastIndexOf("/")) > -1) {
            s2 = s.substring(0, lastIndex);
            s3 = ((s.length() > lastIndex + 1) ? s.substring(lastIndex + 1) : "");
        }
        else {
            s2 = this.ec;
            s3 = s;
        }
        RegularExpressionMatching regularExpressionMatching = null;
        SftpFile[] array = null;
        switch (this.kc) {
            case 1: {
                regularExpressionMatching = new GlobRegExpMatching();
                array = this.ls(s2);
                break;
            }
            case 2: {
                regularExpressionMatching = new Perl5RegExpMatching();
                array = this.ls(s2);
                break;
            }
            default: {
                regularExpressionMatching = new NoRegExpMatching();
                array = new SftpFile[] { this.getSubsystemChannel().getFile(this.c(s)) };
                break;
            }
        }
        return regularExpressionMatching.matchFilesWithPattern(array, s3);
    }
    
    private SftpFile[] b(final String s, final String s2, final FileTransferProgress fileTransferProgress, final boolean b) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        final SftpFile[] d = this.d(s);
        final Vector vector = new Vector();
        for (int i = 0; i < d.length; ++i) {
            this.get(d[i].getAbsolutePath(), s2, fileTransferProgress, b);
            vector.addElement(d[i]);
        }
        final SftpFile[] array = new SftpFile[vector.size()];
        vector.copyInto(array);
        return array;
    }
    
    private String[] f(final String s) throws SftpStatusException, SshException {
        int n;
        String s2;
        String s3;
        if ((n = s.lastIndexOf(System.getProperty("file.separator"))) > -1 || (n = s.lastIndexOf(47)) > -1) {
            s2 = this.e(s.substring(0, n)).getAbsolutePath();
            s3 = ((n < s.length() - 1) ? s.substring(n + 1) : "");
        }
        else {
            s2 = this.fc;
            s3 = s;
        }
        RegularExpressionMatching regularExpressionMatching = null;
        File[] array = null;
        switch (this.kc) {
            case 1: {
                final File file = new File(s2);
                regularExpressionMatching = new GlobRegExpMatching();
                array = this.b(file);
                break;
            }
            case 2: {
                final File file2 = new File(s2);
                regularExpressionMatching = new Perl5RegExpMatching();
                array = this.b(file2);
                break;
            }
            default: {
                regularExpressionMatching = new NoRegExpMatching();
                array = new File[] { new File(s) };
                break;
            }
        }
        return regularExpressionMatching.matchFileNamesWithPattern(array, s3);
    }
    
    private File[] b(final File file) {
        final String absolutePath = file.getAbsolutePath();
        final String[] list = file.list();
        final File[] array = new File[list.length];
        for (int i = 0; i < list.length; ++i) {
            array[i] = new File(absolutePath, list[i]);
        }
        return array;
    }
    
    private void c(final String s, final String s2, final FileTransferProgress fileTransferProgress, final boolean b) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        final String c = this.c(s2);
        SftpFileAttributes stat;
        try {
            stat = this.stat(c);
        }
        catch (final SftpStatusException ex) {
            throw new SftpStatusException(ex.getStatus(), "Remote path '" + s2 + "' does not exist. It must be a valid directory and must already exist!");
        }
        if (!stat.isDirectory()) {
            throw new SftpStatusException(10, "Remote path '" + s2 + "' is not a directory!");
        }
        final String[] f = this.f(s);
        for (int i = 0; i < f.length; ++i) {
            try {
                this.put(f[i], c, fileTransferProgress, b);
            }
            catch (final SftpStatusException ex2) {
                throw new SftpStatusException(ex2.getStatus(), "Failed to put " + f[i] + " to " + s2 + " [" + ex2.getMessage() + "]");
            }
        }
    }
    
    public SftpFileAttributes get(final String s, final OutputStream outputStream, final FileTransferProgress fileTransferProgress, final long n) throws SftpStatusException, SshException, TransferCancelledException {
        final String c = this.c(s);
        final SftpFileAttributes attributes = this.ic.getAttributes(c);
        if (n > attributes.getSize().longValue()) {
            throw new SftpStatusException(101, "The local file size is greater than the remote file");
        }
        if (fileTransferProgress != null) {
            fileTransferProgress.started(attributes.getSize().longValue(), c);
        }
        SftpFile sftpFile;
        if (this.hc == 2 && this.ic.getVersion() > 3) {
            sftpFile = this.ic.openFile(c, 65);
        }
        else {
            sftpFile = this.ic.openFile(c, 1);
        }
        try {
            this.ic.performOptimizedRead(sftpFile.getHandle(), attributes.getSize().longValue(), this.dc, outputStream, this.jc, fileTransferProgress, n);
        }
        catch (final TransferCancelledException ex) {
            throw ex;
        }
        finally {
            try {
                outputStream.close();
            }
            catch (final Throwable t) {}
            try {
                this.ic.closeFile(sftpFile);
            }
            catch (final SftpStatusException ex2) {}
        }
        if (fileTransferProgress != null) {
            fileTransferProgress.completed();
        }
        return attributes;
    }
    
    public InputStream getInputStream(final String s, final long n) throws SftpStatusException, SshException {
        final String c = this.c(s);
        this.ic.getAttributes(c);
        return new SftpFileInputStream(this.ic.openFile(c, 1), n);
    }
    
    public InputStream getInputStream(final String s) throws SftpStatusException, SshException {
        return this.getInputStream(s, 0L);
    }
    
    public SftpFileAttributes get(final String s, final OutputStream outputStream, final long n) throws SftpStatusException, SshException, TransferCancelledException {
        return this.get(s, outputStream, null, n);
    }
    
    public SftpFileAttributes get(final String s, final OutputStream outputStream) throws SftpStatusException, SshException, TransferCancelledException {
        return this.get(s, outputStream, null, 0L);
    }
    
    public boolean isClosed() {
        return this.ic.isClosed();
    }
    
    public void put(final String s, final FileTransferProgress fileTransferProgress, final boolean b) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        this.put(s, new File(s).getName(), fileTransferProgress, b);
    }
    
    public void put(final String s, final FileTransferProgress fileTransferProgress) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        this.put(s, fileTransferProgress, false);
    }
    
    public void put(final String s) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        this.put(s, false);
    }
    
    public void put(final String s, final boolean b) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        this.put(s, (FileTransferProgress)null, b);
    }
    
    public void put(final String s, final String s2, final FileTransferProgress fileTransferProgress) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        this.put(s, s2, fileTransferProgress, false);
    }
    
    public void put(final String s, String string, final FileTransferProgress fileTransferProgress, final boolean b) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        final File e = this.e(s);
        final FileInputStream fileInputStream = new FileInputStream(e);
        long longValue = 0L;
        try {
            SftpFileAttributes sftpFileAttributes = this.stat(string);
            if (sftpFileAttributes.isDirectory()) {
                string = string + (string.endsWith("/") ? "" : "/") + e.getName();
                sftpFileAttributes = this.stat(string);
            }
            if (b) {
                if (e.length() <= sftpFileAttributes.getSize().longValue()) {
                    throw new SftpStatusException(101, "The remote file size is greater than the local file");
                }
                try {
                    longValue = sftpFileAttributes.getSize().longValue();
                    fileInputStream.skip(longValue);
                }
                catch (final IOException ex) {
                    throw new SftpStatusException(2, ex.getMessage());
                }
            }
        }
        catch (final SftpStatusException ex2) {}
        this.put(fileInputStream, string, fileTransferProgress, longValue);
    }
    
    public void put(final String s, final String s2, final boolean b) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        this.put(s, s2, null, b);
    }
    
    public void put(final String s, final String s2) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        this.put(s, s2, null, false);
    }
    
    public void put(final InputStream inputStream, final String s, final FileTransferProgress fileTransferProgress) throws SftpStatusException, SshException, TransferCancelledException {
        this.put(inputStream, s, fileTransferProgress, 0L);
    }
    
    public void put(InputStream inputStream, final String s, final FileTransferProgress fileTransferProgress, final long n) throws SftpStatusException, SshException, TransferCancelledException {
        final String c = this.c(s);
        final SftpFileAttributes sftpFileAttributes = null;
        if (this.hc == 2) {
            final int n2 = 0;
            int gc = this.gc;
            byte[] array = null;
            if (this.ic.getVersion() <= 3 && this.ic.getExtension("newline@vandyke.com") != null) {
                array = this.ic.getExtension("newline@vandyke.com").getBytes();
            }
            else if (this.ic.getVersion() > 3) {
                array = this.ic.getCanonicalNewline().getBytes();
            }
            if (array != null) {
                switch (array.length) {
                    case 1: {
                        if (array[0] == 13) {
                            gc = 3;
                            break;
                        }
                        if (array[0] == 10) {
                            gc = 2;
                            break;
                        }
                        throw new SftpStatusException(100, "Unsupported text mode: invalid newline character");
                    }
                    case 2: {
                        if (array[0] == 13 && array[1] == 10) {
                            gc = 1;
                            break;
                        }
                        throw new SftpStatusException(100, "Unsupported text mode: invalid newline characters");
                    }
                    default: {
                        throw new SftpStatusException(100, "Unsupported text mode: newline length > 2");
                    }
                }
            }
            try {
                inputStream = EOLProcessor.createInputStream(n2, gc, inputStream);
            }
            catch (final IOException ex) {
                throw new SshException("Failed to create EOL processing stream", 5);
            }
        }
        SftpFile sftpFile;
        if (n > 0L) {
            if (this.hc == 2 && this.ic.getVersion() > 3) {
                throw new SftpStatusException(8, "Resume on text mode files is not supported");
            }
            sftpFile = this.ic.openFile(c, 6, sftpFileAttributes);
        }
        else if (this.hc == 2 && this.ic.getVersion() > 3) {
            sftpFile = this.ic.openFile(c, 90, sftpFileAttributes);
        }
        else {
            sftpFile = this.ic.openFile(c, 26, sftpFileAttributes);
        }
        if (fileTransferProgress != null) {
            try {
                fileTransferProgress.started(inputStream.available(), c);
            }
            catch (final IOException ex2) {
                throw new SshException("Failed to determine local file size", 5);
            }
        }
        try {
            this.ic.performOptimizedWrite(sftpFile.getHandle(), this.dc, this.jc, inputStream, this.cc, fileTransferProgress, n);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (final Throwable t) {}
            this.ic.closeFile(sftpFile);
        }
        if (fileTransferProgress != null) {
            fileTransferProgress.completed();
        }
    }
    
    public OutputStream getOutputStream(final String s) throws SftpStatusException, SshException {
        return new SftpFileOutputStream(this.ic.openFile(this.c(s), 26));
    }
    
    public void put(final InputStream inputStream, final String s, final long n) throws SftpStatusException, SshException, TransferCancelledException {
        this.put(inputStream, s, null, n);
    }
    
    public void put(final InputStream inputStream, final String s) throws SftpStatusException, SshException, TransferCancelledException {
        this.put(inputStream, s, null, 0L);
    }
    
    public void chown(final String uid, final String s) throws SftpStatusException, SshException {
        final String c = this.c(s);
        final SftpFileAttributes attributes = this.ic.getAttributes(c);
        attributes.setUID(uid);
        this.ic.setAttributes(c, attributes);
    }
    
    public void chgrp(final String gid, final String s) throws SftpStatusException, SshException {
        final String c = this.c(s);
        final SftpFileAttributes attributes = this.ic.getAttributes(c);
        attributes.setGID(gid);
        this.ic.setAttributes(c, attributes);
    }
    
    public void chmod(final int n, final String s) throws SftpStatusException, SshException {
        this.ic.changePermissions(this.c(s), n);
    }
    
    public void umask(final String s) throws SshException {
        try {
            this.lc = Integer.parseInt(s, 8);
        }
        catch (final NumberFormatException ex) {
            throw new SshException("umask must be 4 digit octal number e.g. 0022", 4);
        }
    }
    
    public void rename(final String s, final String s2) throws SftpStatusException, SshException {
        final String c = this.c(s);
        final String c2 = this.c(s2);
        SftpFileAttributes attributes;
        try {
            attributes = this.ic.getAttributes(c2);
        }
        catch (final SftpStatusException ex) {
            this.ic.renameFile(c, c2);
            return;
        }
        if (attributes != null && attributes.isDirectory()) {
            this.ic.renameFile(c, c2);
            return;
        }
        throw new SftpStatusException(11, s2 + " already exists on the remote filesystem");
    }
    
    public void rm(final String s) throws SftpStatusException, SshException {
        final String c = this.c(s);
        if (this.ic.getAttributes(c).isDirectory()) {
            this.ic.removeDirectory(c);
        }
        else {
            this.ic.removeFile(c);
        }
    }
    
    public void rm(final String s, final boolean b, final boolean b2) throws SftpStatusException, SshException {
        final String c = this.c(s);
        if (this.ic.getAttributes(c).isDirectory()) {
            final SftpFile[] ls = this.ls(s);
            if (!b && ls.length > 0) {
                throw new SftpStatusException(4, "You cannot delete non-empty directory, use force=true to overide");
            }
            for (int i = 0; i < ls.length; ++i) {
                final SftpFile sftpFile = ls[i];
                if (sftpFile.isDirectory() && !sftpFile.getFilename().equals(".") && !sftpFile.getFilename().equals("..")) {
                    if (!b2) {
                        throw new SftpStatusException(4, "Directory has contents, cannot delete without recurse=true");
                    }
                    this.rm(sftpFile.getAbsolutePath(), b, b2);
                }
                else if (sftpFile.isFile()) {
                    this.ic.removeFile(sftpFile.getAbsolutePath());
                }
            }
            this.ic.removeDirectory(c);
        }
        else {
            this.ic.removeFile(c);
        }
    }
    
    public void symlink(final String s, final String s2) throws SftpStatusException, SshException {
        this.ic.createSymbolicLink(this.c(s), this.c(s2));
    }
    
    public SftpFileAttributes stat(final String s) throws SftpStatusException, SshException {
        return this.ic.getAttributes(this.c(s));
    }
    
    public String getAbsolutePath(final String s) throws SftpStatusException, SshException {
        return this.ic.getAbsolutePath(this.c(s));
    }
    
    public void quit() throws SshException {
        try {
            this.ic.close();
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2.getMessage(), 6);
        }
    }
    
    public void exit() throws SshException {
        try {
            this.ic.close();
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2.getMessage(), 6);
        }
    }
    
    public DirectoryOperation copyLocalDirectory(final String s, String s2, final boolean b, final boolean b2, final boolean b3, final FileTransferProgress fileTransferProgress) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        final DirectoryOperation directoryOperation = new DirectoryOperation();
        final File e = this.e(s);
        s2 = this.c(s2);
        s2 += (s2.endsWith("/") ? "" : "/");
        if (b3) {
            try {
                this.ic.getAttributes(s2);
            }
            catch (final SftpStatusException ex) {
                this.mkdirs(s2);
            }
        }
        final String[] list = e.list();
        if (list != null) {
            for (int i = 0; i < list.length; ++i) {
                final File file = new File(e, list[i]);
                if (file.isDirectory() && !file.getName().equals(".") && !file.getName().equals("..")) {
                    if (b) {
                        directoryOperation.addDirectoryOperation(this.copyLocalDirectory(file.getAbsolutePath(), s2, b, b2, b3, fileTransferProgress), file);
                    }
                }
                else if (file.isFile()) {
                    boolean b4 = false;
                    boolean b5 = false;
                    try {
                        final SftpFileAttributes attributes = this.ic.getAttributes(s2 + file.getName());
                        b5 = (file.length() == attributes.getSize().longValue() && file.lastModified() / 1000L == attributes.getModifiedTime().longValue());
                    }
                    catch (final SftpStatusException ex2) {
                        b4 = true;
                    }
                    try {
                        if (b3 && !b5) {
                            this.put(file.getAbsolutePath(), s2 + file.getName(), fileTransferProgress);
                            final SftpFileAttributes attributes2 = this.ic.getAttributes(s2 + file.getName());
                            attributes2.setTimes(new UnsignedInteger64(file.lastModified() / 1000L), new UnsignedInteger64(file.lastModified() / 1000L));
                            this.ic.setAttributes(s2 + file.getName(), attributes2);
                        }
                        if (b5) {
                            directoryOperation.c(file);
                        }
                        else if (!b4) {
                            directoryOperation.d(file);
                        }
                        else {
                            directoryOperation.b(file);
                        }
                    }
                    catch (final SftpStatusException ex3) {
                        directoryOperation.b(file, ex3);
                    }
                }
            }
        }
        if (b2) {
            try {
                final SftpFile[] ls = this.ls(s2);
                for (int j = 0; j < ls.length; ++j) {
                    final SftpFile sftpFile = ls[j];
                    if (!directoryOperation.containsFile(new File(e, sftpFile.getFilename())) && !sftpFile.getFilename().equals(".") && !sftpFile.getFilename().equals("..")) {
                        directoryOperation.c(sftpFile);
                        if (b3) {
                            if (sftpFile.isDirectory()) {
                                this.b(sftpFile, directoryOperation);
                                if (b3) {
                                    this.rm(sftpFile.getAbsolutePath(), true, true);
                                }
                            }
                            else if (sftpFile.isFile()) {
                                this.rm(sftpFile.getAbsolutePath());
                            }
                        }
                    }
                }
            }
            catch (final SftpStatusException ex4) {}
        }
        return directoryOperation;
    }
    
    private void b(SftpFile sftpFile, final DirectoryOperation directoryOperation) throws SftpStatusException, SshException {
        final SftpFile[] ls = this.ls(sftpFile.getAbsolutePath());
        directoryOperation.c(sftpFile);
        for (int i = 0; i < ls.length; ++i) {
            sftpFile = ls[i];
            if (sftpFile.isDirectory() && !sftpFile.getFilename().equals(".") && !sftpFile.getFilename().equals("..")) {
                this.b(sftpFile, directoryOperation);
            }
            else if (sftpFile.isFile()) {
                directoryOperation.c(sftpFile);
            }
        }
    }
    
    private void b(final File file, final DirectoryOperation directoryOperation) throws SftpStatusException, SshException {
        final String[] list = file.list();
        directoryOperation.e(file);
        if (list != null) {
            for (int i = 0; i < list.length; ++i) {
                final File file2 = new File(list[i]);
                if (file2.isDirectory() && !file2.getName().equals(".") && !file2.getName().equals("..")) {
                    this.b(file2, directoryOperation);
                }
                else if (file2.isFile()) {
                    directoryOperation.e(file2);
                }
            }
        }
    }
    
    public static String formatLongname(final SftpFile sftpFile) throws SftpStatusException, SshException {
        return formatLongname(sftpFile.getAttributes(), sftpFile.getFilename());
    }
    
    public static String formatLongname(final SftpFileAttributes sftpFileAttributes, final String s) {
        final StringBuffer sb = new StringBuffer();
        sb.append(b(10 - sftpFileAttributes.getPermissionsString().length()) + sftpFileAttributes.getPermissionsString());
        sb.append("    1 ");
        sb.append(sftpFileAttributes.getUID() + b(8 - sftpFileAttributes.getUID().length()));
        sb.append(" ");
        sb.append(sftpFileAttributes.getGID() + b(8 - sftpFileAttributes.getGID().length()));
        sb.append(" ");
        sb.append(b(8 - sftpFileAttributes.getSize().toString().length()) + sftpFileAttributes.getSize().toString());
        sb.append(" ");
        sb.append(b(12 - b(sftpFileAttributes.getModifiedTime()).length()) + b(sftpFileAttributes.getModifiedTime()));
        sb.append(" ");
        sb.append(s);
        return sb.toString();
    }
    
    private static String b(final UnsignedInteger64 unsignedInteger64) {
        if (unsignedInteger64 == null) {
            return "";
        }
        final long n = unsignedInteger64.longValue() * 1000L;
        SimpleDateFormat simpleDateFormat;
        if (System.currentTimeMillis() - n > 15552000000L) {
            simpleDateFormat = new SimpleDateFormat("MMM dd  yyyy");
        }
        else {
            simpleDateFormat = new SimpleDateFormat("MMM dd hh:mm");
        }
        return simpleDateFormat.format(new Date(n));
    }
    
    private static String b(final int n) {
        final StringBuffer sb = new StringBuffer("");
        if (n > 0) {
            for (int i = 0; i < n; ++i) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
    
    public DirectoryOperation copyRemoteDirectory(final String s, final String s2, final boolean b, final boolean b2, final boolean b3, final FileTransferProgress fileTransferProgress) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        final DirectoryOperation directoryOperation = new DirectoryOperation();
        final String pwd = this.pwd();
        this.cd(s);
        String s3 = s;
        if (s3.endsWith("/")) {
            s3 = s3.substring(0, s3.length() - 1);
        }
        final int lastIndex = s3.lastIndexOf(47);
        if (lastIndex != -1) {
            s3 = s3.substring(lastIndex + 1);
        }
        File file = new File(s2, s3);
        if (!file.isAbsolute()) {
            file = new File(this.lpwd(), s2);
        }
        if (!file.exists() && b3) {
            file.mkdir();
        }
        final SftpFile[] ls = this.ls();
        for (int i = 0; i < ls.length; ++i) {
            final SftpFile sftpFile = ls[i];
            if (sftpFile.isDirectory() && !sftpFile.getFilename().equals(".") && !sftpFile.getFilename().equals("..")) {
                if (b) {
                    directoryOperation.addDirectoryOperation(this.copyRemoteDirectory(sftpFile.getFilename(), file.getAbsolutePath(), b, b2, b3, fileTransferProgress), new File(file, sftpFile.getFilename()));
                }
            }
            else if (sftpFile.isFile()) {
                final File file2 = new File(file, sftpFile.getFilename());
                if (file2.exists() && file2.length() == sftpFile.getAttributes().getSize().longValue() && file2.lastModified() / 1000L == sftpFile.getAttributes().getModifiedTime().longValue()) {
                    if (b3) {
                        directoryOperation.c(file2);
                    }
                    else {
                        directoryOperation.d(sftpFile);
                    }
                }
                else {
                    try {
                        if (file2.exists()) {
                            if (b3) {
                                directoryOperation.d(file2);
                            }
                            else {
                                directoryOperation.e(sftpFile);
                            }
                        }
                        else if (b3) {
                            directoryOperation.b(file2);
                        }
                        else {
                            directoryOperation.b(sftpFile);
                        }
                        if (b3) {
                            this.get(sftpFile.getFilename(), file2.getAbsolutePath(), fileTransferProgress);
                        }
                    }
                    catch (final SftpStatusException ex) {
                        directoryOperation.b(file2, ex);
                    }
                }
            }
        }
        if (b2) {
            final String[] list = file.list();
            if (list != null) {
                for (int j = 0; j < list.length; ++j) {
                    final File file3 = new File(file, list[j]);
                    if (!directoryOperation.containsFile(file3)) {
                        directoryOperation.e(file3);
                        if (file3.isDirectory() && !file3.getName().equals(".") && !file3.getName().equals("..")) {
                            this.b(file3, directoryOperation);
                            if (b3) {
                                IOUtil.recurseDeleteDirectory(file3);
                            }
                        }
                        else if (b3) {
                            file3.delete();
                        }
                    }
                }
            }
        }
        this.cd(pwd);
        return directoryOperation;
    }
    
    public SftpFile[] getFiles(final String s) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        return this.getFiles(s, (FileTransferProgress)null);
    }
    
    public SftpFile[] getFiles(final String s, final boolean b) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        return this.getFiles(s, (FileTransferProgress)null, b);
    }
    
    public SftpFile[] getFiles(final String s, final FileTransferProgress fileTransferProgress) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        return this.getFiles(s, fileTransferProgress, false);
    }
    
    public SftpFile[] getFiles(final String s, final FileTransferProgress fileTransferProgress, final boolean b) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        return this.getFiles(s, this.fc, fileTransferProgress, b);
    }
    
    public SftpFile[] getFiles(final String s, final String s2) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        return this.getFiles(s, s2, false);
    }
    
    public SftpFile[] getFiles(final String s, final String s2, final boolean b) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        return this.getFiles(s, s2, null, b);
    }
    
    public SftpFile[] getFiles(final String s, final String s2, final FileTransferProgress fileTransferProgress, final boolean b) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        return this.b(s, s2, fileTransferProgress, b);
    }
    
    public void putFiles(final String s) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        this.putFiles(s, false);
    }
    
    public void putFiles(final String s, final boolean b) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        this.putFiles(s, (FileTransferProgress)null, b);
    }
    
    public void putFiles(final String s, final FileTransferProgress fileTransferProgress) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        this.putFiles(s, fileTransferProgress, false);
    }
    
    public void putFiles(final String s, final FileTransferProgress fileTransferProgress, final boolean b) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        this.putFiles(s, this.pwd(), fileTransferProgress, b);
    }
    
    public void putFiles(final String s, final String s2) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        this.putFiles(s, s2, null, false);
    }
    
    public void putFiles(final String s, final String s2, final boolean b) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        this.putFiles(s, s2, null, b);
    }
    
    public void putFiles(final String s, final String s2, final FileTransferProgress fileTransferProgress) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        this.putFiles(s, s2, fileTransferProgress, false);
    }
    
    public void putFiles(final String s, final String s2, final FileTransferProgress fileTransferProgress, final boolean b) throws FileNotFoundException, SftpStatusException, SshException, TransferCancelledException {
        this.c(s, s2, fileTransferProgress, b);
    }
    
    static class _b extends OutputStream
    {
        RandomAccessFile b;
        
        _b(final RandomAccessFile b) {
            this.b = b;
        }
        
        public void write(final int n) throws IOException {
            this.b.write(n);
        }
        
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            this.b.write(array, n, n2);
        }
        
        public void close() throws IOException {
            this.b.close();
        }
    }
}
