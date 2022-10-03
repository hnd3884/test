package sun.security.action;

import java.util.Map;
import java.security.Provider;
import java.security.PrivilegedAction;

public class PutAllAction implements PrivilegedAction<Void>
{
    private final Provider provider;
    private final Map<?, ?> map;
    
    public PutAllAction(final Provider provider, final Map<?, ?> map) {
        this.provider = provider;
        this.map = map;
    }
    
    @Override
    public Void run() {
        this.provider.putAll(this.map);
        return null;
    }
}
