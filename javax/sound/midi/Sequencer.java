package javax.sound.midi;

import java.io.IOException;
import java.io.InputStream;

public interface Sequencer extends MidiDevice
{
    public static final int LOOP_CONTINUOUSLY = -1;
    
    void setSequence(final Sequence p0) throws InvalidMidiDataException;
    
    void setSequence(final InputStream p0) throws IOException, InvalidMidiDataException;
    
    Sequence getSequence();
    
    void start();
    
    void stop();
    
    boolean isRunning();
    
    void startRecording();
    
    void stopRecording();
    
    boolean isRecording();
    
    void recordEnable(final Track p0, final int p1);
    
    void recordDisable(final Track p0);
    
    float getTempoInBPM();
    
    void setTempoInBPM(final float p0);
    
    float getTempoInMPQ();
    
    void setTempoInMPQ(final float p0);
    
    void setTempoFactor(final float p0);
    
    float getTempoFactor();
    
    long getTickLength();
    
    long getTickPosition();
    
    void setTickPosition(final long p0);
    
    long getMicrosecondLength();
    
    long getMicrosecondPosition();
    
    void setMicrosecondPosition(final long p0);
    
    void setMasterSyncMode(final SyncMode p0);
    
    SyncMode getMasterSyncMode();
    
    SyncMode[] getMasterSyncModes();
    
    void setSlaveSyncMode(final SyncMode p0);
    
    SyncMode getSlaveSyncMode();
    
    SyncMode[] getSlaveSyncModes();
    
    void setTrackMute(final int p0, final boolean p1);
    
    boolean getTrackMute(final int p0);
    
    void setTrackSolo(final int p0, final boolean p1);
    
    boolean getTrackSolo(final int p0);
    
    boolean addMetaEventListener(final MetaEventListener p0);
    
    void removeMetaEventListener(final MetaEventListener p0);
    
    int[] addControllerEventListener(final ControllerEventListener p0, final int[] p1);
    
    int[] removeControllerEventListener(final ControllerEventListener p0, final int[] p1);
    
    void setLoopStartPoint(final long p0);
    
    long getLoopStartPoint();
    
    void setLoopEndPoint(final long p0);
    
    long getLoopEndPoint();
    
    void setLoopCount(final int p0);
    
    int getLoopCount();
    
    public static class SyncMode
    {
        private String name;
        public static final SyncMode INTERNAL_CLOCK;
        public static final SyncMode MIDI_SYNC;
        public static final SyncMode MIDI_TIME_CODE;
        public static final SyncMode NO_SYNC;
        
        protected SyncMode(final String name) {
            this.name = name;
        }
        
        @Override
        public final boolean equals(final Object o) {
            return super.equals(o);
        }
        
        @Override
        public final int hashCode() {
            return super.hashCode();
        }
        
        @Override
        public final String toString() {
            return this.name;
        }
        
        static {
            INTERNAL_CLOCK = new SyncMode("Internal Clock");
            MIDI_SYNC = new SyncMode("MIDI Sync");
            MIDI_TIME_CODE = new SyncMode("MIDI Time Code");
            NO_SYNC = new SyncMode("No Timing");
        }
    }
}
