package javax.imageio.event;

import javax.imageio.ImageWriter;
import java.util.EventListener;

public interface IIOWriteWarningListener extends EventListener
{
    void warningOccurred(final ImageWriter p0, final int p1, final String p2);
}
