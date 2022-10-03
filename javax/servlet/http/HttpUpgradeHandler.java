package javax.servlet.http;

public interface HttpUpgradeHandler
{
    void init(final WebConnection p0);
    
    void destroy();
}
