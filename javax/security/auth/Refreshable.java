package javax.security.auth;

public interface Refreshable
{
    boolean isCurrent();
    
    void refresh() throws RefreshFailedException;
}
