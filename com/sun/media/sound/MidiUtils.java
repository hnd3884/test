package com.sun.media.sound;

import javax.sound.midi.MidiEvent;
import java.util.ArrayList;
import javax.sound.midi.Track;
import javax.sound.midi.Sequence;
import javax.sound.midi.MidiMessage;

public final class MidiUtils
{
    public static final int DEFAULT_TEMPO_MPQ = 500000;
    public static final int META_END_OF_TRACK_TYPE = 47;
    public static final int META_TEMPO_TYPE = 81;
    
    private MidiUtils() {
    }
    
    public static boolean isMetaEndOfTrack(final MidiMessage midiMessage) {
        if (midiMessage.getLength() != 3 || midiMessage.getStatus() != 255) {
            return false;
        }
        final byte[] message = midiMessage.getMessage();
        return (message[1] & 0xFF) == 0x2F && message[2] == 0;
    }
    
    public static boolean isMetaTempo(final MidiMessage midiMessage) {
        if (midiMessage.getLength() != 6 || midiMessage.getStatus() != 255) {
            return false;
        }
        final byte[] message = midiMessage.getMessage();
        return (message[1] & 0xFF) == 0x51 && message[2] == 3;
    }
    
    public static int getTempoMPQ(final MidiMessage midiMessage) {
        if (midiMessage.getLength() != 6 || midiMessage.getStatus() != 255) {
            return -1;
        }
        final byte[] message = midiMessage.getMessage();
        if ((message[1] & 0xFF) != 0x51 || message[2] != 3) {
            return -1;
        }
        return (message[5] & 0xFF) | (message[4] & 0xFF) << 8 | (message[3] & 0xFF) << 16;
    }
    
    public static double convertTempo(double n) {
        if (n <= 0.0) {
            n = 1.0;
        }
        return 6.0E7 / n;
    }
    
    public static long ticks2microsec(final long n, final double n2, final int n3) {
        return (long)(n * n2 / n3);
    }
    
    public static long microsec2ticks(final long n, final double n2, final int n3) {
        return (long)(n * (double)n3 / n2);
    }
    
    public static long tick2microsecond(final Sequence sequence, final long n, TempoCache tempoCache) {
        if (sequence.getDivisionType() != 0.0f) {
            return (long)(1000000.0 * (n / (double)(sequence.getDivisionType() * sequence.getResolution())));
        }
        if (tempoCache == null) {
            tempoCache = new TempoCache(sequence);
        }
        final int resolution = sequence.getResolution();
        final long[] ticks = tempoCache.ticks;
        final int[] tempos = tempoCache.tempos;
        final int length = tempos.length;
        int snapshotIndex = tempoCache.snapshotIndex;
        int snapshotMicro = tempoCache.snapshotMicro;
        long n2 = 0L;
        if (snapshotIndex <= 0 || snapshotIndex >= length || ticks[snapshotIndex] > n) {
            snapshotMicro = 0;
            snapshotIndex = 0;
        }
        if (length > 0) {
            for (int n3 = snapshotIndex + 1; n3 < length && ticks[n3] <= n; ++n3) {
                snapshotMicro += (int)ticks2microsec(ticks[n3] - ticks[n3 - 1], tempos[n3 - 1], resolution);
                snapshotIndex = n3;
            }
            n2 = snapshotMicro + ticks2microsec(n - ticks[snapshotIndex], tempos[snapshotIndex], resolution);
        }
        tempoCache.snapshotIndex = snapshotIndex;
        tempoCache.snapshotMicro = snapshotMicro;
        return n2;
    }
    
    public static long microsecond2tick(final Sequence sequence, final long n, TempoCache tempoCache) {
        if (sequence.getDivisionType() != 0.0f) {
            final long n2 = (long)(n * (double)sequence.getDivisionType() * sequence.getResolution() / 1000000.0);
            if (tempoCache != null) {
                tempoCache.currTempo = (int)tempoCache.getTempoMPQAt(n2);
            }
            return n2;
        }
        if (tempoCache == null) {
            tempoCache = new TempoCache(sequence);
        }
        final long[] ticks = tempoCache.ticks;
        final int[] tempos = tempoCache.tempos;
        final int length = tempos.length;
        final int resolution = sequence.getResolution();
        long n3 = 0L;
        long n4 = 0L;
        int i = 1;
        if (n > 0L && length > 0) {
            while (i < length) {
                final long n5 = n3 + ticks2microsec(ticks[i] - ticks[i - 1], tempos[i - 1], resolution);
                if (n5 > n) {
                    break;
                }
                n3 = n5;
                ++i;
            }
            n4 = ticks[i - 1] + microsec2ticks(n - n3, tempos[i - 1], resolution);
        }
        tempoCache.currTempo = tempos[i - 1];
        return n4;
    }
    
    public static int tick2index(final Track track, final long n) {
        int n2 = 0;
        if (n > 0L) {
            int i = 0;
            int n3 = track.size() - 1;
            while (i < n3) {
                n2 = i + n3 >> 1;
                final long tick = track.get(n2).getTick();
                if (tick == n) {
                    break;
                }
                if (tick < n) {
                    if (i == n3 - 1) {
                        ++n2;
                        break;
                    }
                    i = n2;
                }
                else {
                    n3 = n2;
                }
            }
        }
        return n2;
    }
    
    public static final class TempoCache
    {
        long[] ticks;
        int[] tempos;
        int snapshotIndex;
        int snapshotMicro;
        int currTempo;
        private boolean firstTempoIsFake;
        
        public TempoCache() {
            this.snapshotIndex = 0;
            this.snapshotMicro = 0;
            this.firstTempoIsFake = false;
            this.ticks = new long[1];
            (this.tempos = new int[1])[0] = 500000;
            this.snapshotIndex = 0;
            this.snapshotMicro = 0;
        }
        
        public TempoCache(final Sequence sequence) {
            this();
            this.refresh(sequence);
        }
        
        public synchronized void refresh(final Sequence sequence) {
            final ArrayList list = new ArrayList();
            final Track[] tracks = sequence.getTracks();
            if (tracks.length > 0) {
                final Track track = tracks[0];
                for (int size = track.size(), i = 0; i < size; ++i) {
                    final MidiEvent value = track.get(i);
                    if (MidiUtils.isMetaTempo(value.getMessage())) {
                        list.add(value);
                    }
                }
            }
            int n = list.size() + 1;
            this.firstTempoIsFake = true;
            if (n > 1 && list.get(0).getTick() == 0L) {
                --n;
                this.firstTempoIsFake = false;
            }
            this.ticks = new long[n];
            this.tempos = new int[n];
            int n2 = 0;
            if (this.firstTempoIsFake) {
                this.ticks[0] = 0L;
                this.tempos[0] = 500000;
                ++n2;
            }
            for (int j = 0; j < list.size(); ++j, ++n2) {
                final MidiEvent midiEvent = list.get(j);
                this.ticks[n2] = midiEvent.getTick();
                this.tempos[n2] = MidiUtils.getTempoMPQ(midiEvent.getMessage());
            }
            this.snapshotIndex = 0;
            this.snapshotMicro = 0;
        }
        
        public int getCurrTempoMPQ() {
            return this.currTempo;
        }
        
        float getTempoMPQAt(final long n) {
            return this.getTempoMPQAt(n, -1.0f);
        }
        
        synchronized float getTempoMPQAt(final long n, final float n2) {
            int i = 0;
            while (i < this.ticks.length) {
                if (this.ticks[i] > n) {
                    if (i > 0) {
                        --i;
                    }
                    if (n2 > 0.0f && i == 0 && this.firstTempoIsFake) {
                        return n2;
                    }
                    return (float)this.tempos[i];
                }
                else {
                    ++i;
                }
            }
            return (float)this.tempos[this.tempos.length - 1];
        }
    }
}
