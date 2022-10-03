package redis.clients.jedis.exceptions;

public class InvalidURIException extends JedisException
{
    private static final long serialVersionUID = -781691993326357802L;
    
    public InvalidURIException(final String message) {
        super(message);
    }
    
    public InvalidURIException(final Throwable cause) {
        super(cause);
    }
    
    public InvalidURIException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
