package sun.tracing;

import com.sun.tracing.Provider;
import java.io.PrintStream;
import com.sun.tracing.ProviderFactory;

public class PrintStreamProviderFactory extends ProviderFactory
{
    private PrintStream stream;
    
    public PrintStreamProviderFactory(final PrintStream stream) {
        this.stream = stream;
    }
    
    @Override
    public <T extends Provider> T createProvider(final Class<T> clazz) {
        final PrintStreamProvider printStreamProvider = new PrintStreamProvider(clazz, this.stream);
        printStreamProvider.init();
        return (T)printStreamProvider.newProxyInstance();
    }
}
