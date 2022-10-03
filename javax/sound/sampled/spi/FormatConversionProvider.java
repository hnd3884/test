package javax.sound.sampled.spi;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;

public abstract class FormatConversionProvider
{
    public abstract AudioFormat.Encoding[] getSourceEncodings();
    
    public abstract AudioFormat.Encoding[] getTargetEncodings();
    
    public boolean isSourceEncodingSupported(final AudioFormat.Encoding encoding) {
        final AudioFormat.Encoding[] sourceEncodings = this.getSourceEncodings();
        for (int i = 0; i < sourceEncodings.length; ++i) {
            if (encoding.equals(sourceEncodings[i])) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isTargetEncodingSupported(final AudioFormat.Encoding encoding) {
        final AudioFormat.Encoding[] targetEncodings = this.getTargetEncodings();
        for (int i = 0; i < targetEncodings.length; ++i) {
            if (encoding.equals(targetEncodings[i])) {
                return true;
            }
        }
        return false;
    }
    
    public abstract AudioFormat.Encoding[] getTargetEncodings(final AudioFormat p0);
    
    public boolean isConversionSupported(final AudioFormat.Encoding encoding, final AudioFormat audioFormat) {
        final AudioFormat.Encoding[] targetEncodings = this.getTargetEncodings(audioFormat);
        for (int i = 0; i < targetEncodings.length; ++i) {
            if (encoding.equals(targetEncodings[i])) {
                return true;
            }
        }
        return false;
    }
    
    public abstract AudioFormat[] getTargetFormats(final AudioFormat.Encoding p0, final AudioFormat p1);
    
    public boolean isConversionSupported(final AudioFormat audioFormat, final AudioFormat audioFormat2) {
        final AudioFormat[] targetFormats = this.getTargetFormats(audioFormat.getEncoding(), audioFormat2);
        for (int i = 0; i < targetFormats.length; ++i) {
            if (audioFormat.matches(targetFormats[i])) {
                return true;
            }
        }
        return false;
    }
    
    public abstract AudioInputStream getAudioInputStream(final AudioFormat.Encoding p0, final AudioInputStream p1);
    
    public abstract AudioInputStream getAudioInputStream(final AudioFormat p0, final AudioInputStream p1);
}
