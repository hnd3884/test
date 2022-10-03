package javax.sound.midi;

import java.util.EventListener;

public interface MetaEventListener extends EventListener
{
    void meta(final MetaMessage p0);
}
