package javax.sound.midi;

import com.sun.media.sound.MidiUtils;
import java.util.Vector;

public class Sequence
{
    public static final float PPQ = 0.0f;
    public static final float SMPTE_24 = 24.0f;
    public static final float SMPTE_25 = 25.0f;
    public static final float SMPTE_30DROP = 29.97f;
    public static final float SMPTE_30 = 30.0f;
    protected float divisionType;
    protected int resolution;
    protected Vector<Track> tracks;
    
    public Sequence(final float n, final int resolution) throws InvalidMidiDataException {
        this.tracks = new Vector<Track>();
        if (n == 0.0f) {
            this.divisionType = 0.0f;
        }
        else if (n == 24.0f) {
            this.divisionType = 24.0f;
        }
        else if (n == 25.0f) {
            this.divisionType = 25.0f;
        }
        else if (n == 29.97f) {
            this.divisionType = 29.97f;
        }
        else {
            if (n != 30.0f) {
                throw new InvalidMidiDataException("Unsupported division type: " + n);
            }
            this.divisionType = 30.0f;
        }
        this.resolution = resolution;
    }
    
    public Sequence(final float n, final int resolution, final int n2) throws InvalidMidiDataException {
        this.tracks = new Vector<Track>();
        if (n == 0.0f) {
            this.divisionType = 0.0f;
        }
        else if (n == 24.0f) {
            this.divisionType = 24.0f;
        }
        else if (n == 25.0f) {
            this.divisionType = 25.0f;
        }
        else if (n == 29.97f) {
            this.divisionType = 29.97f;
        }
        else {
            if (n != 30.0f) {
                throw new InvalidMidiDataException("Unsupported division type: " + n);
            }
            this.divisionType = 30.0f;
        }
        this.resolution = resolution;
        for (int i = 0; i < n2; ++i) {
            this.tracks.addElement(new Track());
        }
    }
    
    public float getDivisionType() {
        return this.divisionType;
    }
    
    public int getResolution() {
        return this.resolution;
    }
    
    public Track createTrack() {
        final Track track = new Track();
        this.tracks.addElement(track);
        return track;
    }
    
    public boolean deleteTrack(final Track track) {
        return this.tracks.removeElement(track);
    }
    
    public Track[] getTracks() {
        return this.tracks.toArray(new Track[0]);
    }
    
    public long getMicrosecondLength() {
        return MidiUtils.tick2microsecond(this, this.getTickLength(), null);
    }
    
    public long getTickLength() {
        long n = 0L;
        synchronized (this.tracks) {
            for (int i = 0; i < this.tracks.size(); ++i) {
                final long ticks = this.tracks.elementAt(i).ticks();
                if (ticks > n) {
                    n = ticks;
                }
            }
            return n;
        }
    }
    
    public Patch[] getPatchList() {
        return new Patch[0];
    }
}
