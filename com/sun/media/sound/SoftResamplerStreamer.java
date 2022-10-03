package com.sun.media.sound;

import java.io.IOException;

public interface SoftResamplerStreamer extends ModelOscillatorStream
{
    void open(final ModelWavetable p0, final float p1) throws IOException;
}
