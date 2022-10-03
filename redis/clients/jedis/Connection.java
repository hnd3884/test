package redis.clients.jedis;

import redis.clients.jedis.exceptions.JedisDataException;
import java.util.ArrayList;
import java.util.List;
import redis.clients.util.IOUtils;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import redis.clients.util.SafeEncoder;
import java.net.SocketException;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.RedisInputStream;
import redis.clients.util.RedisOutputStream;
import java.net.Socket;
import java.io.Closeable;

public class Connection implements Closeable
{
    private static final byte[][] EMPTY_ARGS;
    private String host;
    private int port;
    private Socket socket;
    private RedisOutputStream outputStream;
    private RedisInputStream inputStream;
    private int pipelinedCommands;
    private int connectionTimeout;
    private int soTimeout;
    private boolean broken;
    
    public Connection() {
        this.host = "localhost";
        this.port = 6379;
        this.pipelinedCommands = 0;
        this.connectionTimeout = 2000;
        this.soTimeout = 2000;
        this.broken = false;
    }
    
    public Connection(final String host) {
        this.host = "localhost";
        this.port = 6379;
        this.pipelinedCommands = 0;
        this.connectionTimeout = 2000;
        this.soTimeout = 2000;
        this.broken = false;
        this.host = host;
    }
    
    public Connection(final String host, final int port) {
        this.host = "localhost";
        this.port = 6379;
        this.pipelinedCommands = 0;
        this.connectionTimeout = 2000;
        this.soTimeout = 2000;
        this.broken = false;
        this.host = host;
        this.port = port;
    }
    
    public Socket getSocket() {
        return this.socket;
    }
    
    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }
    
    public int getSoTimeout() {
        return this.soTimeout;
    }
    
    public void setConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    public void setSoTimeout(final int soTimeout) {
        this.soTimeout = soTimeout;
    }
    
    public void setTimeoutInfinite() {
        try {
            if (!this.isConnected()) {
                this.connect();
            }
            this.socket.setSoTimeout(0);
        }
        catch (final SocketException ex) {
            this.broken = true;
            throw new JedisConnectionException(ex);
        }
    }
    
    public void rollbackTimeout() {
        try {
            this.socket.setSoTimeout(this.soTimeout);
        }
        catch (final SocketException ex) {
            this.broken = true;
            throw new JedisConnectionException(ex);
        }
    }
    
    protected Connection sendCommand(final Protocol.Command cmd, final String... args) {
        final byte[][] bargs = new byte[args.length][];
        for (int i = 0; i < args.length; ++i) {
            bargs[i] = SafeEncoder.encode(args[i]);
        }
        return this.sendCommand(cmd, bargs);
    }
    
    protected Connection sendCommand(final Protocol.Command cmd) {
        return this.sendCommand(cmd, Connection.EMPTY_ARGS);
    }
    
    protected Connection sendCommand(final Protocol.Command cmd, final byte[]... args) {
        try {
            this.connect();
            Protocol.sendCommand(this.outputStream, cmd, args);
            ++this.pipelinedCommands;
            return this;
        }
        catch (final JedisConnectionException ex) {
            try {
                final String errorMessage = Protocol.readErrorLineIfPossible(this.inputStream);
                if (errorMessage != null && errorMessage.length() > 0) {
                    ex = new JedisConnectionException(errorMessage, ex.getCause());
                }
            }
            catch (final Exception ex2) {}
            this.broken = true;
            throw ex;
        }
    }
    
    public String getHost() {
        return this.host;
    }
    
    public void setHost(final String host) {
        this.host = host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public void connect() {
        if (!this.isConnected()) {
            try {
                (this.socket = new Socket()).setReuseAddress(true);
                this.socket.setKeepAlive(true);
                this.socket.setTcpNoDelay(true);
                this.socket.setSoLinger(true, 0);
                this.socket.connect(new InetSocketAddress(this.host, this.port), this.connectionTimeout);
                this.socket.setSoTimeout(this.soTimeout);
                this.outputStream = new RedisOutputStream(this.socket.getOutputStream());
                this.inputStream = new RedisInputStream(this.socket.getInputStream());
            }
            catch (final IOException ex) {
                this.broken = true;
                throw new JedisConnectionException(ex);
            }
        }
    }
    
    @Override
    public void close() {
        this.disconnect();
    }
    
    public void disconnect() {
        if (this.isConnected()) {
            try {
                this.outputStream.flush();
                this.socket.close();
            }
            catch (final IOException ex) {
                this.broken = true;
                throw new JedisConnectionException(ex);
            }
            finally {
                IOUtils.closeQuietly(this.socket);
            }
        }
    }
    
    public boolean isConnected() {
        return this.socket != null && this.socket.isBound() && !this.socket.isClosed() && this.socket.isConnected() && !this.socket.isInputShutdown() && !this.socket.isOutputShutdown();
    }
    
    public String getStatusCodeReply() {
        this.flush();
        --this.pipelinedCommands;
        final byte[] resp = (byte[])this.readProtocolWithCheckingBroken();
        if (null == resp) {
            return null;
        }
        return SafeEncoder.encode(resp);
    }
    
    public String getBulkReply() {
        final byte[] result = this.getBinaryBulkReply();
        if (null != result) {
            return SafeEncoder.encode(result);
        }
        return null;
    }
    
    public byte[] getBinaryBulkReply() {
        this.flush();
        --this.pipelinedCommands;
        return (byte[])this.readProtocolWithCheckingBroken();
    }
    
    public Long getIntegerReply() {
        this.flush();
        --this.pipelinedCommands;
        return (Long)this.readProtocolWithCheckingBroken();
    }
    
    public List<String> getMultiBulkReply() {
        return BuilderFactory.STRING_LIST.build(this.getBinaryMultiBulkReply());
    }
    
    public List<byte[]> getBinaryMultiBulkReply() {
        this.flush();
        --this.pipelinedCommands;
        return (List)this.readProtocolWithCheckingBroken();
    }
    
    public void resetPipelinedCount() {
        this.pipelinedCommands = 0;
    }
    
    public List<Object> getRawObjectMultiBulkReply() {
        return (List)this.readProtocolWithCheckingBroken();
    }
    
    public List<Object> getObjectMultiBulkReply() {
        this.flush();
        --this.pipelinedCommands;
        return this.getRawObjectMultiBulkReply();
    }
    
    public List<Long> getIntegerMultiBulkReply() {
        this.flush();
        --this.pipelinedCommands;
        return (List)this.readProtocolWithCheckingBroken();
    }
    
    public List<Object> getAll() {
        return this.getAll(0);
    }
    
    public List<Object> getAll(final int except) {
        final List<Object> all = new ArrayList<Object>();
        this.flush();
        while (this.pipelinedCommands > except) {
            try {
                all.add(this.readProtocolWithCheckingBroken());
            }
            catch (final JedisDataException e) {
                all.add(e);
            }
            --this.pipelinedCommands;
        }
        return all;
    }
    
    public Object getOne() {
        this.flush();
        --this.pipelinedCommands;
        return this.readProtocolWithCheckingBroken();
    }
    
    public boolean isBroken() {
        return this.broken;
    }
    
    protected void flush() {
        try {
            this.outputStream.flush();
        }
        catch (final IOException ex) {
            this.broken = true;
            throw new JedisConnectionException(ex);
        }
    }
    
    protected Object readProtocolWithCheckingBroken() {
        try {
            return Protocol.read(this.inputStream);
        }
        catch (final JedisConnectionException exc) {
            this.broken = true;
            throw exc;
        }
    }
    
    static {
        EMPTY_ARGS = new byte[0][];
    }
}
