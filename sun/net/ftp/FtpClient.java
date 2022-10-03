package sun.net.ftp;

import java.util.Iterator;
import java.util.Date;
import java.util.List;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.io.Closeable;

public abstract class FtpClient implements Closeable
{
    private static final int FTP_PORT = 21;
    
    public static final int defaultPort() {
        return 21;
    }
    
    protected FtpClient() {
    }
    
    public static FtpClient create() {
        return FtpClientProvider.provider().createFtpClient();
    }
    
    public static FtpClient create(final InetSocketAddress inetSocketAddress) throws FtpProtocolException, IOException {
        final FtpClient create = create();
        if (inetSocketAddress != null) {
            create.connect(inetSocketAddress);
        }
        return create;
    }
    
    public static FtpClient create(final String s) throws FtpProtocolException, IOException {
        return create(new InetSocketAddress(s, 21));
    }
    
    public abstract FtpClient enablePassiveMode(final boolean p0);
    
    public abstract boolean isPassiveModeEnabled();
    
    public abstract FtpClient setConnectTimeout(final int p0);
    
    public abstract int getConnectTimeout();
    
    public abstract FtpClient setReadTimeout(final int p0);
    
    public abstract int getReadTimeout();
    
    public abstract FtpClient setProxy(final Proxy p0);
    
    public abstract Proxy getProxy();
    
    public abstract boolean isConnected();
    
    public abstract FtpClient connect(final SocketAddress p0) throws FtpProtocolException, IOException;
    
    public abstract FtpClient connect(final SocketAddress p0, final int p1) throws FtpProtocolException, IOException;
    
    public abstract SocketAddress getServerAddress();
    
    public abstract FtpClient login(final String p0, final char[] p1) throws FtpProtocolException, IOException;
    
    public abstract FtpClient login(final String p0, final char[] p1, final String p2) throws FtpProtocolException, IOException;
    
    @Override
    public abstract void close() throws IOException;
    
    public abstract boolean isLoggedIn();
    
    public abstract FtpClient changeDirectory(final String p0) throws FtpProtocolException, IOException;
    
    public abstract FtpClient changeToParentDirectory() throws FtpProtocolException, IOException;
    
    public abstract String getWorkingDirectory() throws FtpProtocolException, IOException;
    
    public abstract FtpClient setRestartOffset(final long p0);
    
    public abstract FtpClient getFile(final String p0, final OutputStream p1) throws FtpProtocolException, IOException;
    
    public abstract InputStream getFileStream(final String p0) throws FtpProtocolException, IOException;
    
    public OutputStream putFileStream(final String s) throws FtpProtocolException, IOException {
        return this.putFileStream(s, false);
    }
    
    public abstract OutputStream putFileStream(final String p0, final boolean p1) throws FtpProtocolException, IOException;
    
    public FtpClient putFile(final String s, final InputStream inputStream) throws FtpProtocolException, IOException {
        return this.putFile(s, inputStream, false);
    }
    
    public abstract FtpClient putFile(final String p0, final InputStream p1, final boolean p2) throws FtpProtocolException, IOException;
    
    public abstract FtpClient appendFile(final String p0, final InputStream p1) throws FtpProtocolException, IOException;
    
    public abstract FtpClient rename(final String p0, final String p1) throws FtpProtocolException, IOException;
    
    public abstract FtpClient deleteFile(final String p0) throws FtpProtocolException, IOException;
    
    public abstract FtpClient makeDirectory(final String p0) throws FtpProtocolException, IOException;
    
    public abstract FtpClient removeDirectory(final String p0) throws FtpProtocolException, IOException;
    
    public abstract FtpClient noop() throws FtpProtocolException, IOException;
    
    public abstract String getStatus(final String p0) throws FtpProtocolException, IOException;
    
    public abstract List<String> getFeatures() throws FtpProtocolException, IOException;
    
    public abstract FtpClient abort() throws FtpProtocolException, IOException;
    
    public abstract FtpClient completePending() throws FtpProtocolException, IOException;
    
    public abstract FtpClient reInit() throws FtpProtocolException, IOException;
    
    public abstract FtpClient setType(final TransferType p0) throws FtpProtocolException, IOException;
    
    public FtpClient setBinaryType() throws FtpProtocolException, IOException {
        this.setType(TransferType.BINARY);
        return this;
    }
    
    public FtpClient setAsciiType() throws FtpProtocolException, IOException {
        this.setType(TransferType.ASCII);
        return this;
    }
    
    public abstract InputStream list(final String p0) throws FtpProtocolException, IOException;
    
    public abstract InputStream nameList(final String p0) throws FtpProtocolException, IOException;
    
    public abstract long getSize(final String p0) throws FtpProtocolException, IOException;
    
    public abstract Date getLastModified(final String p0) throws FtpProtocolException, IOException;
    
    public abstract FtpClient setDirParser(final FtpDirParser p0);
    
    public abstract Iterator<FtpDirEntry> listFiles(final String p0) throws FtpProtocolException, IOException;
    
    public abstract FtpClient useKerberos() throws FtpProtocolException, IOException;
    
    public abstract String getWelcomeMsg();
    
    public abstract FtpReplyCode getLastReplyCode();
    
    public abstract String getLastResponseString();
    
    public abstract long getLastTransferSize();
    
    public abstract String getLastFileName();
    
    public abstract FtpClient startSecureSession() throws FtpProtocolException, IOException;
    
    public abstract FtpClient endSecureSession() throws FtpProtocolException, IOException;
    
    public abstract FtpClient allocate(final long p0) throws FtpProtocolException, IOException;
    
    public abstract FtpClient structureMount(final String p0) throws FtpProtocolException, IOException;
    
    public abstract String getSystem() throws FtpProtocolException, IOException;
    
    public abstract String getHelp(final String p0) throws FtpProtocolException, IOException;
    
    public abstract FtpClient siteCmd(final String p0) throws FtpProtocolException, IOException;
    
    public enum TransferType
    {
        ASCII, 
        BINARY, 
        EBCDIC;
    }
}
