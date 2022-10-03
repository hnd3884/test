package javax.sound.sampled;

import java.util.EventListener;

public interface LineListener extends EventListener
{
    void update(final LineEvent p0);
}
