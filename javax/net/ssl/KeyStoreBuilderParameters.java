package javax.net.ssl;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.security.KeyStore;
import java.util.List;

public class KeyStoreBuilderParameters implements ManagerFactoryParameters
{
    private final List<KeyStore.Builder> parameters;
    
    public KeyStoreBuilderParameters(final KeyStore.Builder builder) {
        this.parameters = Collections.singletonList((KeyStore.Builder)Objects.requireNonNull((T)builder));
    }
    
    public KeyStoreBuilderParameters(final List<KeyStore.Builder> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.parameters = Collections.unmodifiableList((List<? extends KeyStore.Builder>)new ArrayList<KeyStore.Builder>(list));
    }
    
    public List<KeyStore.Builder> getParameters() {
        return this.parameters;
    }
}
