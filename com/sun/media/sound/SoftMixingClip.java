package com.sun.media.sound;

import javax.sound.sampled.LineUnavailableException;
import java.io.ByteArrayOutputStream;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import java.util.Arrays;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import javax.sound.sampled.DataLine;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Clip;

public final class SoftMixingClip extends SoftMixingDataLine implements Clip
{
    private AudioFormat format;
    private int framesize;
    private byte[] data;
    private final InputStream datastream;
    private int offset;
    private int bufferSize;
    private float[] readbuffer;
    private boolean open;
    private AudioFormat outputformat;
    private int out_nrofchannels;
    private int in_nrofchannels;
    private int frameposition;
    private boolean frameposition_sg;
    private boolean active_sg;
    private int loopstart;
    private int loopend;
    private boolean active;
    private int loopcount;
    private boolean _active;
    private int _frameposition;
    private boolean loop_sg;
    private int _loopcount;
    private int _loopstart;
    private int _loopend;
    private float _rightgain;
    private float _leftgain;
    private float _eff1gain;
    private float _eff2gain;
    private AudioFloatInputStream afis;
    
    SoftMixingClip(final SoftMixingMixer softMixingMixer, final DataLine.Info info) {
        super(softMixingMixer, info);
        this.datastream = new InputStream() {
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
            public int read(final byte[] array, int i, int n) throws IOException {
                if (SoftMixingClip.this._loopcount != 0) {
                    final int n2 = SoftMixingClip.this._loopend * SoftMixingClip.this.framesize;
                    final int n3 = SoftMixingClip.this._loopstart * SoftMixingClip.this.framesize;
                    int n4 = SoftMixingClip.this._frameposition * SoftMixingClip.this.framesize;
                    if (n4 + n >= n2 && n4 < n2) {
                        final int n5 = i + n;
                        final int n6 = i;
                        while (i != n5) {
                            if (n4 == n2) {
                                if (SoftMixingClip.this._loopcount == 0) {
                                    break;
                                }
                                n4 = n3;
                                if (SoftMixingClip.this._loopcount != -1) {
                                    SoftMixingClip.this._loopcount--;
                                }
                            }
                            n = n5 - i;
                            final int n7 = n2 - n4;
                            if (n > n7) {
                                n = n7;
                            }
                            System.arraycopy(SoftMixingClip.this.data, n4, array, i, n);
                            i += n;
                        }
                        if (SoftMixingClip.this._loopcount == 0) {
                            n = n5 - i;
                            final int n8 = n2 - n4;
                            if (n > n8) {
                                n = n8;
                            }
                            System.arraycopy(SoftMixingClip.this.data, n4, array, i, n);
                            i += n;
                        }
                        SoftMixingClip.this._frameposition = n4 / SoftMixingClip.this.framesize;
                        return n6 - i;
                    }
                }
                final int n9 = SoftMixingClip.this._frameposition * SoftMixingClip.this.framesize;
                final int n10 = SoftMixingClip.this.bufferSize - n9;
                if (n10 == 0) {
                    return -1;
                }
                if (n > n10) {
                    n = n10;
                }
                System.arraycopy(SoftMixingClip.this.data, n9, array, i, n);
                SoftMixingClip.this._frameposition += n / SoftMixingClip.this.framesize;
                return n;
            }
        };
        this.open = false;
        this.frameposition = 0;
        this.frameposition_sg = false;
        this.active_sg = false;
        this.loopstart = 0;
        this.loopend = -1;
        this.active = false;
        this.loopcount = 0;
        this._active = false;
        this._frameposition = 0;
        this.loop_sg = false;
        this._loopcount = 0;
        this._loopstart = 0;
        this._loopend = -1;
    }
    
    @Override
    protected void processControlLogic() {
        this._rightgain = this.rightgain;
        this._leftgain = this.leftgain;
        this._eff1gain = this.eff1gain;
        this._eff2gain = this.eff2gain;
        if (this.active_sg) {
            this._active = this.active;
            this.active_sg = false;
        }
        else {
            this.active = this._active;
        }
        if (this.frameposition_sg) {
            this._frameposition = this.frameposition;
            this.frameposition_sg = false;
            this.afis = null;
        }
        else {
            this.frameposition = this._frameposition;
        }
        if (this.loop_sg) {
            this._loopcount = this.loopcount;
            this._loopstart = this.loopstart;
            this._loopend = this.loopend;
        }
        if (this.afis == null) {
            this.afis = AudioFloatInputStream.getInputStream(new AudioInputStream(this.datastream, this.format, -1L));
            if (Math.abs(this.format.getSampleRate() - this.outputformat.getSampleRate()) > 1.0E-6) {
                this.afis = new AudioFloatInputStreamResampler(this.afis, this.outputformat);
            }
        }
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
                if (read == -1) {
                    this._active = false;
                    return;
                }
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
            if (this._eff1gain > 2.0E-4) {
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
            if (this._eff2gain > 2.0E-4) {
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
    public int getFrameLength() {
        return this.bufferSize / this.format.getFrameSize();
    }
    
    @Override
    public long getMicrosecondLength() {
        return (long)(this.getFrameLength() * (1000000.0 / this.getFormat().getSampleRate()));
    }
    
    @Override
    public void loop(final int loopcount) {
        LineEvent lineEvent = null;
        synchronized (this.control_mutex) {
            if (this.isOpen()) {
                if (this.active) {
                    return;
                }
                this.active = true;
                this.active_sg = true;
                this.loopcount = loopcount;
                lineEvent = new LineEvent(this, LineEvent.Type.START, this.getLongFramePosition());
            }
        }
        if (lineEvent != null) {
            this.sendEvent(lineEvent);
        }
    }
    
    @Override
    public void open(final AudioInputStream audioInputStream) throws LineUnavailableException, IOException {
        if (this.isOpen()) {
            throw new IllegalStateException("Clip is already open with format " + this.getFormat() + " and frame lengh of " + this.getFrameLength());
        }
        if (AudioFloatConverter.getConverter(audioInputStream.getFormat()) == null) {
            throw new IllegalArgumentException("Invalid format : " + audioInputStream.getFormat().toString());
        }
        if (audioInputStream.getFrameLength() != -1L) {
            final byte[] array = new byte[(int)audioInputStream.getFrameLength() * audioInputStream.getFormat().getFrameSize()];
            int n = 512 * audioInputStream.getFormat().getFrameSize();
            int i;
            int read;
            for (i = 0; i != array.length; i += read) {
                if (n > array.length - i) {
                    n = array.length - i;
                }
                read = audioInputStream.read(array, i, n);
                if (read == -1) {
                    break;
                }
                if (read == 0) {
                    Thread.yield();
                }
            }
            this.open(audioInputStream.getFormat(), array, 0, i);
        }
        else {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final byte[] array2 = new byte[512 * audioInputStream.getFormat().getFrameSize()];
            int read2;
            while ((read2 = audioInputStream.read(array2)) != -1) {
                if (read2 == 0) {
                    Thread.yield();
                }
                byteArrayOutputStream.write(array2, 0, read2);
            }
            this.open(audioInputStream.getFormat(), byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size());
        }
    }
    
    @Override
    public void open(final AudioFormat format, final byte[] array, final int offset, final int bufferSize) throws LineUnavailableException {
        synchronized (this.control_mutex) {
            if (this.isOpen()) {
                throw new IllegalStateException("Clip is already open with format " + this.getFormat() + " and frame lengh of " + this.getFrameLength());
            }
            if (AudioFloatConverter.getConverter(format) == null) {
                throw new IllegalArgumentException("Invalid format : " + format.toString());
            }
            if (bufferSize % format.getFrameSize() != 0) {
                throw new IllegalArgumentException("Buffer size does not represent an integral number of sample frames!");
            }
            if (array != null) {
                this.data = Arrays.copyOf(array, array.length);
            }
            this.offset = offset;
            this.bufferSize = bufferSize;
            this.format = format;
            this.framesize = format.getFrameSize();
            this.loopstart = 0;
            this.loopend = -1;
            this.loop_sg = true;
            if (!this.mixer.isOpen()) {
                this.mixer.open();
                this.mixer.implicitOpen = true;
            }
            this.outputformat = this.mixer.getFormat();
            this.out_nrofchannels = this.outputformat.getChannels();
            this.in_nrofchannels = format.getChannels();
            this.open = true;
            this.mixer.getMainMixer().openLine(this);
        }
    }
    
    @Override
    public void setFramePosition(final int frameposition) {
        synchronized (this.control_mutex) {
            this.frameposition_sg = true;
            this.frameposition = frameposition;
        }
    }
    
    @Override
    public void setLoopPoints(final int loopstart, final int loopend) {
        synchronized (this.control_mutex) {
            if (loopend != -1) {
                if (loopend < loopstart) {
                    throw new IllegalArgumentException("Invalid loop points : " + loopstart + " - " + loopend);
                }
                if (loopend * this.framesize > this.bufferSize) {
                    throw new IllegalArgumentException("Invalid loop points : " + loopstart + " - " + loopend);
                }
            }
            if (loopstart * this.framesize > this.bufferSize) {
                throw new IllegalArgumentException("Invalid loop points : " + loopstart + " - " + loopend);
            }
            if (0 < loopstart) {
                throw new IllegalArgumentException("Invalid loop points : " + loopstart + " - " + loopend);
            }
            this.loopstart = loopstart;
            this.loopend = loopend;
            this.loop_sg = true;
        }
    }
    
    @Override
    public void setMicrosecondPosition(final long n) {
        this.setFramePosition((int)(n * (this.getFormat().getSampleRate() / 1000000.0)));
    }
    
    @Override
    public int available() {
        return 0;
    }
    
    @Override
    public void drain() {
    }
    
    @Override
    public void flush() {
    }
    
    @Override
    public int getBufferSize() {
        return this.bufferSize;
    }
    
    @Override
    public AudioFormat getFormat() {
        return this.format;
    }
    
    @Override
    public int getFramePosition() {
        synchronized (this.control_mutex) {
            return this.frameposition;
        }
    }
    
    @Override
    public float getLevel() {
        return -1.0f;
    }
    
    @Override
    public long getLongFramePosition() {
        return this.getFramePosition();
    }
    
    @Override
    public long getMicrosecondPosition() {
        return (long)(this.getFramePosition() * (1000000.0 / this.getFormat().getSampleRate()));
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
                this.active_sg = true;
                this.loopcount = 0;
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
                this.active_sg = true;
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
        return this.open;
    }
    
    @Override
    public void open() throws LineUnavailableException {
        if (this.data == null) {
            throw new IllegalArgumentException("Illegal call to open() in interface Clip");
        }
        this.open(this.format, this.data, this.offset, this.bufferSize);
    }
}
