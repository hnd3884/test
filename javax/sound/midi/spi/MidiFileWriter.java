package javax.sound.midi.spi;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.midi.Sequence;

public abstract class MidiFileWriter
{
    public abstract int[] getMidiFileTypes();
    
    public abstract int[] getMidiFileTypes(final Sequence p0);
    
    public boolean isFileTypeSupported(final int n) {
        final int[] midiFileTypes = this.getMidiFileTypes();
        for (int i = 0; i < midiFileTypes.length; ++i) {
            if (n == midiFileTypes[i]) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isFileTypeSupported(final int n, final Sequence sequence) {
        final int[] midiFileTypes = this.getMidiFileTypes(sequence);
        for (int i = 0; i < midiFileTypes.length; ++i) {
            if (n == midiFileTypes[i]) {
                return true;
            }
        }
        return false;
    }
    
    public abstract int write(final Sequence p0, final int p1, final OutputStream p2) throws IOException;
    
    public abstract int write(final Sequence p0, final int p1, final File p2) throws IOException;
}
