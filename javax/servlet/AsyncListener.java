package javax.servlet;

import java.io.IOException;
import java.util.EventListener;

public interface AsyncListener extends EventListener
{
    void onComplete(final AsyncEvent p0) throws IOException;
    
    void onTimeout(final AsyncEvent p0) throws IOException;
    
    void onError(final AsyncEvent p0) throws IOException;
    
    void onStartAsync(final AsyncEvent p0) throws IOException;
}
