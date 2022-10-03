package redis.clients.jedis;

import redis.clients.jedis.exceptions.JedisRedirectionException;
import redis.clients.jedis.exceptions.JedisMovedDataException;
import redis.clients.jedis.exceptions.JedisAskDataException;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.JedisClusterCRC16;
import redis.clients.jedis.exceptions.JedisClusterMaxRedirectionsException;
import redis.clients.jedis.exceptions.JedisClusterException;

public abstract class JedisClusterCommand<T>
{
    private JedisClusterConnectionHandler connectionHandler;
    private int redirections;
    private ThreadLocal<Jedis> askConnection;
    
    public JedisClusterCommand(final JedisClusterConnectionHandler connectionHandler, final int maxRedirections) {
        this.askConnection = new ThreadLocal<Jedis>();
        this.connectionHandler = connectionHandler;
        this.redirections = maxRedirections;
    }
    
    public abstract T execute(final Jedis p0);
    
    public T run(final String key) {
        if (key == null) {
            throw new JedisClusterException("No way to dispatch this command to Redis Cluster.");
        }
        return this.runWithRetries(key, this.redirections, false, false);
    }
    
    private T runWithRetries(final String key, final int redirections, final boolean tryRandomNode, boolean asking) {
        if (redirections <= 0) {
            throw new JedisClusterMaxRedirectionsException("Too many Cluster redirections?");
        }
        Jedis connection = null;
        try {
            if (asking) {
                connection = this.askConnection.get();
                connection.asking();
                asking = false;
            }
            else if (tryRandomNode) {
                connection = this.connectionHandler.getConnection();
            }
            else {
                connection = this.connectionHandler.getConnectionFromSlot(JedisClusterCRC16.getSlot(key));
            }
            return this.execute(connection);
        }
        catch (final JedisConnectionException jce) {
            if (tryRandomNode) {
                throw jce;
            }
            this.releaseConnection(connection);
            connection = null;
            return this.runWithRetries(key, redirections - 1, true, asking);
        }
        catch (final JedisRedirectionException jre) {
            this.releaseConnection(connection);
            connection = null;
            if (jre instanceof JedisAskDataException) {
                asking = true;
                this.askConnection.set(this.connectionHandler.getConnectionFromNode(jre.getTargetNode()));
            }
            else {
                if (!(jre instanceof JedisMovedDataException)) {
                    throw new JedisClusterException(jre);
                }
                this.connectionHandler.renewSlotCache();
            }
            return this.runWithRetries(key, redirections - 1, false, asking);
        }
        finally {
            this.releaseConnection(connection);
        }
    }
    
    private void releaseConnection(final Jedis connection) {
        if (connection != null) {
            connection.close();
        }
    }
}
