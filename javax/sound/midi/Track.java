package javax.sound.midi;

import com.sun.media.sound.MidiUtils;
import java.util.HashSet;
import java.util.ArrayList;

public class Track
{
    private ArrayList eventsList;
    private HashSet set;
    private MidiEvent eotEvent;
    
    Track() {
        this.eventsList = new ArrayList();
        this.set = new HashSet();
        this.eotEvent = new MidiEvent(new ImmutableEndOfTrack(), 0L);
        this.eventsList.add(this.eotEvent);
        this.set.add(this.eotEvent);
    }
    
    public boolean add(final MidiEvent midiEvent) {
        if (midiEvent == null) {
            return false;
        }
        synchronized (this.eventsList) {
            if (!this.set.contains(midiEvent)) {
                int n = this.eventsList.size();
                MidiEvent midiEvent2 = null;
                if (n > 0) {
                    midiEvent2 = this.eventsList.get(n - 1);
                }
                if (midiEvent2 != this.eotEvent) {
                    if (midiEvent2 != null) {
                        this.eotEvent.setTick(midiEvent2.getTick());
                    }
                    else {
                        this.eotEvent.setTick(0L);
                    }
                    this.eventsList.add(this.eotEvent);
                    this.set.add(this.eotEvent);
                    n = this.eventsList.size();
                }
                if (MidiUtils.isMetaEndOfTrack(midiEvent.getMessage())) {
                    if (midiEvent.getTick() > this.eotEvent.getTick()) {
                        this.eotEvent.setTick(midiEvent.getTick());
                    }
                    return true;
                }
                this.set.add(midiEvent);
                int n2;
                for (n2 = n; n2 > 0 && midiEvent.getTick() < ((MidiEvent)this.eventsList.get(n2 - 1)).getTick(); --n2) {}
                if (n2 == n) {
                    this.eventsList.set(n - 1, midiEvent);
                    if (this.eotEvent.getTick() < midiEvent.getTick()) {
                        this.eotEvent.setTick(midiEvent.getTick());
                    }
                    this.eventsList.add(this.eotEvent);
                }
                else {
                    this.eventsList.add(n2, midiEvent);
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean remove(final MidiEvent midiEvent) {
        synchronized (this.eventsList) {
            if (this.set.remove(midiEvent)) {
                final int index = this.eventsList.indexOf(midiEvent);
                if (index >= 0) {
                    this.eventsList.remove(index);
                    return true;
                }
            }
        }
        return false;
    }
    
    public MidiEvent get(final int n) throws ArrayIndexOutOfBoundsException {
        try {
            synchronized (this.eventsList) {
                return this.eventsList.get(n);
            }
        }
        catch (final IndexOutOfBoundsException ex) {
            throw new ArrayIndexOutOfBoundsException(ex.getMessage());
        }
    }
    
    public int size() {
        synchronized (this.eventsList) {
            return this.eventsList.size();
        }
    }
    
    public long ticks() {
        long tick = 0L;
        synchronized (this.eventsList) {
            if (this.eventsList.size() > 0) {
                tick = this.eventsList.get(this.eventsList.size() - 1).getTick();
            }
        }
        return tick;
    }
    
    private static class ImmutableEndOfTrack extends MetaMessage
    {
        private ImmutableEndOfTrack() {
            super(new byte[3]);
            this.data[0] = -1;
            this.data[1] = 47;
            this.data[2] = 0;
        }
        
        @Override
        public void setMessage(final int n, final byte[] array, final int n2) throws InvalidMidiDataException {
            throw new InvalidMidiDataException("cannot modify end of track message");
        }
    }
}
