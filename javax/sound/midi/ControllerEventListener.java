package javax.sound.midi;

import java.util.EventListener;

public interface ControllerEventListener extends EventListener
{
    void controlChange(final ShortMessage p0);
}
