package com.sun.media.sound;

import javax.sound.sampled.AudioInputStream;
import java.io.InputStream;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.Arrays;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;

public final class SoftMixingSourceDataLine extends SoftMixingDataLine implements SourceDataLine
{
    private boolean open;
    private AudioFormat format;
    private int framesize;
    private int bufferSize;
    private float[] readbuffer;
    private boolean active;
    private byte[] cycling_buffer;
    private int cycling_read_pos;
    private int cycling_write_pos;
    private int cycling_avail;
    private long cycling_framepos;
    private AudioFloatInputStream afis;
    private boolean _active;
    private AudioFormat outputformat;
    private int out_nrofchannels;
    private int in_nrofchannels;
    private float _rightgain;
    private float _leftgain;
    private float _eff1gain;
    private float _eff2gain;
    
    SoftMixingSourceDataLine(final SoftMixingMixer softMixingMixer, final DataLine.Info info) {
        super(softMixingMixer, info);
        this.open = false;
        this.format = new AudioFormat(44100.0f, 16, 2, true, false);
        this.bufferSize = -1;
        this.active = false;
        this.cycling_read_pos = 0;
        this.cycling_write_pos = 0;
        this.cycling_avail = 0;
        this.cycling_framepos = 0L;
        this._active = false;
    }
    
    @Override
    public int write(final byte[] array, int n, final int n2) {
        if (!this.isOpen()) {
            return 0;
        }
        if (n2 % this.framesize != 0) {
            throw new IllegalArgumentException("Number of bytes does not represent an integral number of sample frames.");
        }
        if (n < 0) {
            throw new ArrayIndexOutOfBoundsException(n);
        }
        if (n + (long)n2 > array.length) {
            throw new ArrayIndexOutOfBoundsException(array.length);
        }
        final byte[] cycling_buffer = this.cycling_buffer;
        final int length = this.cycling_buffer.length;
        int i = 0;
        while (i != n2) {
            int cycling_avail;
            synchronized (this.cycling_buffer) {
                int cycling_write_pos = this.cycling_write_pos;
                cycling_avail = this.cycling_avail;
                while (i != n2 && cycling_avail != length) {
                    cycling_buffer[cycling_write_pos++] = array[n++];
                    ++i;
                    ++cycling_avail;
                    if (cycling_write_pos == length) {
                        cycling_write_pos = 0;
                    }
                }
                this.cycling_avail = cycling_avail;
                this.cycling_write_pos = cycling_write_pos;
                if (i == n2) {
                    return i;
                }
            }
            if (cycling_avail == length) {
                try {
                    Thread.sleep(1L);
                }
                catch (final InterruptedException ex) {
                    return i;
                }
                if (!this.isRunning()) {
                    return i;
                }
                continue;
            }
        }
        return i;
    }
    
    @Override
    protected void processControlLogic() {
        this._active = this.active;
        this._rightgain = this.rightgain;
        this._leftgain = this.leftgain;
        this._eff1gain = this.eff1gain;
        this._eff2gain = this.eff2gain;
    }
    
    @Override
    protected void processAudioLogic(final SoftAudioBuffer[] array) {
        if (this._active) {
            final float[] array2 = array[0].array();
            final float[] array3 = array[1].array();
            final int size = array[0].getSize();
            final int n = size * this.in_nrofchannels;
            if (this.readbuffer == null || this.readbuffer.length < n) {
                this.readbuffer = new float[n];
            }
            try {
                final int read = this.afis.read(this.readbuffer);
                if (read != this.in_nrofchannels) {
                    Arrays.fill(this.readbuffer, read, n, 0.0f);
                }
            }
            catch (final IOException ex) {}
            final int in_nrofchannels = this.in_nrofchannels;
            for (int i = 0, n2 = 0; i < size; ++i, n2 += in_nrofchannels) {
                final float[] array4 = array2;
                final int n3 = i;
                array4[n3] += this.readbuffer[n2] * this._leftgain;
            }
            if (this.out_nrofchannels != 1) {
                if (this.in_nrofchannels == 1) {
                    for (int j = 0, n4 = 0; j < size; ++j, n4 += in_nrofchannels) {
                        final float[] array5 = array3;
                        final int n5 = j;
                        array5[n5] += this.readbuffer[n4] * this._rightgain;
                    }
                }
                else {
                    for (int k = 0, n6 = 1; k < size; ++k, n6 += in_nrofchannels) {
                        final float[] array6 = array3;
                        final int n7 = k;
                        array6[n7] += this.readbuffer[n6] * this._rightgain;
                    }
                }
            }
            if (this._eff1gain > 1.0E-4) {
                final float[] array7 = array[2].array();
                for (int l = 0, n8 = 0; l < size; ++l, n8 += in_nrofchannels) {
                    final float[] array8 = array7;
                    final int n9 = l;
                    array8[n9] += this.readbuffer[n8] * this._eff1gain;
                }
                if (this.in_nrofchannels == 2) {
                    for (int n10 = 0, n11 = 1; n10 < size; ++n10, n11 += in_nrofchannels) {
                        final float[] array9 = array7;
                        final int n12 = n10;
                        array9[n12] += this.readbuffer[n11] * this._eff1gain;
                    }
                }
            }
            if (this._eff2gain > 1.0E-4) {
                final float[] array10 = array[3].array();
                for (int n13 = 0, n14 = 0; n13 < size; ++n13, n14 += in_nrofchannels) {
                    final float[] array11 = array10;
                    final int n15 = n13;
                    array11[n15] += this.readbuffer[n14] * this._eff2gain;
                }
                if (this.in_nrofchannels == 2) {
                    for (int n16 = 0, n17 = 1; n16 < size; ++n16, n17 += in_nrofchannels) {
                        final float[] array12 = array10;
                        final int n18 = n16;
                        array12[n18] += this.readbuffer[n17] * this._eff2gain;
                    }
                }
            }
        }
    }
    
    @Override
    public void open() throws LineUnavailableException {
        this.open(this.format);
    }
    
    @Override
    public void open(final AudioFormat audioFormat) throws LineUnavailableException {
        if (this.bufferSize == -1) {
            this.bufferSize = (int)(audioFormat.getFrameRate() / 2.0f) * audioFormat.getFrameSize();
        }
        this.open(audioFormat, this.bufferSize);
    }
    
    @Override
    public void open(final AudioFormat format, int n) throws LineUnavailableException {
        LineEvent lineEvent = null;
        if (n < format.getFrameSize() * 32) {
            n = format.getFrameSize() * 32;
        }
        synchronized (this.control_mutex) {
            if (!this.isOpen()) {
                if (!this.mixer.isOpen()) {
                    this.mixer.open();
                    this.mixer.implicitOpen = true;
                }
                lineEvent = new LineEvent(this, LineEvent.Type.OPEN, 0L);
                this.bufferSize = n - n % format.getFrameSize();
                this.format = format;
                this.framesize = format.getFrameSize();
                this.outputformat = this.mixer.getFormat();
                this.out_nrofchannels = this.outputformat.getChannels();
                this.in_nrofchannels = format.getChannels();
                this.open = true;
                this.mixer.getMainMixer().openLine(this);
                this.cycling_buffer = new byte[this.framesize * n];
                this.cycling_read_pos = 0;
                this.cycling_write_pos = 0;
                this.cycling_avail = 0;
                this.cycling_framepos = 0L;
                this.afis = AudioFloatInputStream.getInputStream(new AudioInputStream(new InputStream() {
                    @Override
                    public int read() throws IOException {
                        final byte[] array = { 0 };
                        final int read = this.read(array);
                        if (read < 0) {
                            return read;
                        }
                        return array[0] & 0xFF;
                    }
                    
                    @Override
                    public int available() throws IOException {
                        synchronized (SoftMixingSourceDataLine.this.cycling_buffer) {
                            return SoftMixingSourceDataLine.this.cycling_avail;
                        }
                    }
                    
                    @Override
                    public int read(final byte[] array, int n, int access$100) throws IOException {
                        synchronized (SoftMixingSourceDataLine.this.cycling_buffer) {
                            if (access$100 > SoftMixingSourceDataLine.this.cycling_avail) {
                                access$100 = SoftMixingSourceDataLine.this.cycling_avail;
                            }
                            int access$101 = SoftMixingSourceDataLine.this.cycling_read_pos;
                            final byte[] access$102 = SoftMixingSourceDataLine.this.cycling_buffer;
                            final int length = access$102.length;
                            for (int i = 0; i < access$100; ++i) {
                                array[n++] = access$102[access$101];
                                if (++access$101 == length) {
                                    access$101 = 0;
                                }
                            }
                            SoftMixingSourceDataLine.this.cycling_read_pos = access$101;
                            SoftMixingSourceDataLine.this.cycling_avail -= access$100;
                            SoftMixingSourceDataLine.this.cycling_framepos += access$100 / SoftMixingSourceDataLine.this.framesize;
                        }
                        return access$100;
                    }
                }, format, -1L));
                this.afis = new NonBlockingFloatInputStream(this.afis);
                if (Math.abs(format.getSampleRate() - this.outputformat.getSampleRate()) > 1.0E-6) {
                    this.afis = new AudioFloatInputStreamResampler(this.afis, this.outputformat);
                }
            }
            else if (!format.matches(this.getFormat())) {
                throw new IllegalStateException("Line is already open with format " + this.getFormat() + " and bufferSize " + this.getBufferSize());
            }
        }
        if (lineEvent != null) {
            this.sendEvent(lineEvent);
        }
    }
    
    @Override
    public int available() {
        synchronized (this.cycling_buffer) {
            return this.cycling_buffer.length - this.cycling_avail;
        }
    }
    
    @Override
    public void drain() {
        while (true) {
            final int cycling_avail;
            synchronized (this.cycling_buffer) {
                cycling_avail = this.cycling_avail;
            }
            if (cycling_avail != 0) {
                break;
            }
            try {
                Thread.sleep(1L);
            }
            catch (final InterruptedException ex) {}
        }
    }
    
    @Override
    public void flush() {
        synchronized (this.cycling_buffer) {
            this.cycling_read_pos = 0;
            this.cycling_write_pos = 0;
            this.cycling_avail = 0;
        }
    }
    
    @Override
    public int getBufferSize() {
        synchronized (this.control_mutex) {
            return this.bufferSize;
        }
    }
    
    @Override
    public AudioFormat getFormat() {
        synchronized (this.control_mutex) {
            return this.format;
        }
    }
    
    @Override
    public int getFramePosition() {
        return (int)this.getLongFramePosition();
    }
    
    @Override
    public float getLevel() {
        return -1.0f;
    }
    
    @Override
    public long getLongFramePosition() {
        synchronized (this.cycling_buffer) {
            return this.cycling_framepos;
        }
    }
    
    @Override
    public long getMicrosecondPosition() {
        return (long)(this.getLongFramePosition() * (1000000.0 / this.getFormat().getSampleRate()));
    }
    
    @Override
    public boolean isActive() {
        synchronized (this.control_mutex) {
            return this.active;
        }
    }
    
    @Override
    public boolean isRunning() {
        synchronized (this.control_mutex) {
            return this.active;
        }
    }
    
    @Override
    public void start() {
        LineEvent lineEvent = null;
        synchronized (this.control_mutex) {
            if (this.isOpen()) {
                if (this.active) {
                    return;
                }
                this.active = true;
                lineEvent = new LineEvent(this, LineEvent.Type.START, this.getLongFramePosition());
            }
        }
        if (lineEvent != null) {
            this.sendEvent(lineEvent);
        }
    }
    
    @Override
    public void stop() {
        LineEvent lineEvent = null;
        synchronized (this.control_mutex) {
            if (this.isOpen()) {
                if (!this.active) {
                    return;
                }
                this.active = false;
                lineEvent = new LineEvent(this, LineEvent.Type.STOP, this.getLongFramePosition());
            }
        }
        if (lineEvent != null) {
            this.sendEvent(lineEvent);
        }
    }
    
    @Override
    public void close() {
        LineEvent lineEvent = null;
        synchronized (this.control_mutex) {
            if (!this.isOpen()) {
                return;
            }
            this.stop();
            lineEvent = new LineEvent(this, LineEvent.Type.CLOSE, this.getLongFramePosition());
            this.open = false;
            this.mixer.getMainMixer().closeLine(this);
        }
        if (lineEvent != null) {
            this.sendEvent(lineEvent);
        }
    }
    
    @Override
    public boolean isOpen() {
        synchronized (this.control_mutex) {
            return this.open;
        }
    }
    
    private static class NonBlockingFloatInputStream extends AudioFloatInputStream
    {
        AudioFloatInputStream ais;
        
        NonBlockingFloatInputStream(final AudioFloatInputStream ais) {
            this.ais = ais;
        }
        
        @Override
        public int available() throws IOException {
            return this.ais.available();
        }
        
        @Override
        public void close() throws IOException {
            this.ais.close();
        }
        
        @Override
        public AudioFormat getFormat() {
            return this.ais.getFormat();
        }
        
        @Override
        public long getFrameLength() {
            return this.ais.getFrameLength();
        }
        
        @Override
        public void mark(final int n) {
            this.ais.mark(n);
        }
        
        @Override
        public boolean markSupported() {
            return this.ais.markSupported();
        }
        
        @Override
        public int read(final float[] array, final int n, final int n2) throws IOException {
            final int available = this.available();
            if (n2 > available) {
                Arrays.fill(array, n + this.ais.read(array, n, available), n + n2, 0.0f);
                return n2;
            }
            return this.ais.read(array, n, n2);
        }
        
        @Override
        public void reset() throws IOException {
            this.ais.reset();
        }
        
        @Override
        public long skip(final long n) throws IOException {
            return this.ais.skip(n);
        }
    }
}
