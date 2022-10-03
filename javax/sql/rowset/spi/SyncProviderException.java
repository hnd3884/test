package javax.sql.rowset.spi;

import com.sun.rowset.internal.SyncResolverImpl;
import java.sql.SQLException;

public class SyncProviderException extends SQLException
{
    private SyncResolver syncResolver;
    static final long serialVersionUID = -939908523620640692L;
    
    public SyncProviderException() {
        this.syncResolver = null;
    }
    
    public SyncProviderException(final String s) {
        super(s);
        this.syncResolver = null;
    }
    
    public SyncProviderException(final SyncResolver syncResolver) {
        this.syncResolver = null;
        if (syncResolver == null) {
            throw new IllegalArgumentException("Cannot instantiate a SyncProviderException with a null SyncResolver object");
        }
        this.syncResolver = syncResolver;
    }
    
    public SyncResolver getSyncResolver() {
        if (this.syncResolver != null) {
            return this.syncResolver;
        }
        try {
            this.syncResolver = new SyncResolverImpl();
        }
        catch (final SQLException ex) {}
        return this.syncResolver;
    }
    
    public void setSyncResolver(final SyncResolver syncResolver) {
        if (syncResolver == null) {
            throw new IllegalArgumentException("Cannot set a null SyncResolver object");
        }
        this.syncResolver = syncResolver;
    }
}
