package javax.imageio.event;

import javax.imageio.ImageReader;
import java.util.EventListener;

public interface IIOReadWarningListener extends EventListener
{
    void warningOccurred(final ImageReader p0, final String p1);
}
