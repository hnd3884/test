package com.sun.media.sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.spi.FormatConversionProvider;

abstract class SunCodec extends FormatConversionProvider
{
    private final AudioFormat.Encoding[] inputEncodings;
    private final AudioFormat.Encoding[] outputEncodings;
    
    SunCodec(final AudioFormat.Encoding[] inputEncodings, final AudioFormat.Encoding[] outputEncodings) {
        this.inputEncodings = inputEncodings;
        this.outputEncodings = outputEncodings;
    }
    
    @Override
    public final AudioFormat.Encoding[] getSourceEncodings() {
        final AudioFormat.Encoding[] array = new AudioFormat.Encoding[this.inputEncodings.length];
        System.arraycopy(this.inputEncodings, 0, array, 0, this.inputEncodings.length);
        return array;
    }
    
    @Override
    public final AudioFormat.Encoding[] getTargetEncodings() {
        final AudioFormat.Encoding[] array = new AudioFormat.Encoding[this.outputEncodings.length];
        System.arraycopy(this.outputEncodings, 0, array, 0, this.outputEncodings.length);
        return array;
    }
    
    @Override
    public abstract AudioFormat.Encoding[] getTargetEncodings(final AudioFormat p0);
    
    @Override
    public abstract AudioFormat[] getTargetFormats(final AudioFormat.Encoding p0, final AudioFormat p1);
    
    @Override
    public abstract AudioInputStream getAudioInputStream(final AudioFormat.Encoding p0, final AudioInputStream p1);
    
    @Override
    public abstract AudioInputStream getAudioInputStream(final AudioFormat p0, final AudioInputStream p1);
}
