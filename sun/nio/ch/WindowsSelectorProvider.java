package sun.nio.ch;

import java.io.IOException;
import java.nio.channels.spi.SelectorProvider;
import java.nio.channels.spi.AbstractSelector;

public class WindowsSelectorProvider extends SelectorProviderImpl
{
    @Override
    public AbstractSelector openSelector() throws IOException {
        return new WindowsSelectorImpl(this);
    }
}
