package com.sun.media.sound;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import java.io.IOException;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import java.util.ArrayList;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineListener;
import java.util.List;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Mixer;

public final class SoftMixingMixer implements Mixer
{
    static final String INFO_NAME = "Gervill Sound Mixer";
    static final String INFO_VENDOR = "OpenJDK Proposal";
    static final String INFO_DESCRIPTION = "Software Sound Mixer";
    static final String INFO_VERSION = "1.0";
    static final Mixer.Info info;
    final Object control_mutex;
    boolean implicitOpen;
    private boolean open;
    private SoftMixingMainMixer mainmixer;
    private AudioFormat format;
    private SourceDataLine sourceDataLine;
    private SoftAudioPusher pusher;
    private AudioInputStream pusher_stream;
    private final float controlrate = 147.0f;
    private final long latency = 100000L;
    private final boolean jitter_correction = false;
    private final List<LineListener> listeners;
    private final Line.Info[] sourceLineInfo;
    
    public SoftMixingMixer() {
        this.control_mutex = this;
        this.implicitOpen = false;
        this.open = false;
        this.mainmixer = null;
        this.format = new AudioFormat(44100.0f, 16, 2, true, false);
        this.sourceDataLine = null;
        this.pusher = null;
        this.pusher_stream = null;
        this.listeners = new ArrayList<LineListener>();
        this.sourceLineInfo = new Line.Info[2];
        final ArrayList list = new ArrayList();
        for (int i = 1; i <= 2; ++i) {
            list.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0f, 8, i, i, -1.0f, false));
            list.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0f, 8, i, i, -1.0f, false));
            for (int j = 16; j < 32; j += 8) {
                list.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0f, j, i, i * j / 8, -1.0f, false));
                list.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0f, j, i, i * j / 8, -1.0f, false));
                list.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0f, j, i, i * j / 8, -1.0f, true));
                list.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0f, j, i, i * j / 8, -1.0f, true));
            }
            list.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0f, 32, i, i * 4, -1.0f, false));
            list.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0f, 32, i, i * 4, -1.0f, true));
            list.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0f, 64, i, i * 8, -1.0f, false));
            list.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0f, 64, i, i * 8, -1.0f, true));
        }
        final AudioFormat[] array = list.toArray(new AudioFormat[list.size()]);
        this.sourceLineInfo[0] = new DataLine.Info(SourceDataLine.class, array, -1, -1);
        this.sourceLineInfo[1] = new DataLine.Info(Clip.class, array, -1, -1);
    }
    
    @Override
    public Line getLine(final Line.Info info) throws LineUnavailableException {
        if (!this.isLineSupported(info)) {
            throw new IllegalArgumentException("Line unsupported: " + info);
        }
        if (info.getLineClass() == SourceDataLine.class) {
            return new SoftMixingSourceDataLine(this, (DataLine.Info)info);
        }
        if (info.getLineClass() == Clip.class) {
            return new SoftMixingClip(this, (DataLine.Info)info);
        }
        throw new IllegalArgumentException("Line unsupported: " + info);
    }
    
    @Override
    public int getMaxLines(final Line.Info info) {
        if (info.getLineClass() == SourceDataLine.class) {
            return -1;
        }
        if (info.getLineClass() == Clip.class) {
            return -1;
        }
        return 0;
    }
    
    @Override
    public Mixer.Info getMixerInfo() {
        return SoftMixingMixer.info;
    }
    
    @Override
    public Line.Info[] getSourceLineInfo() {
        final Line.Info[] array = new Line.Info[this.sourceLineInfo.length];
        System.arraycopy(this.sourceLineInfo, 0, array, 0, this.sourceLineInfo.length);
        return array;
    }
    
    @Override
    public Line.Info[] getSourceLineInfo(final Line.Info info) {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < this.sourceLineInfo.length; ++i) {
            if (info.matches(this.sourceLineInfo[i])) {
                list.add(this.sourceLineInfo[i]);
            }
        }
        return list.toArray(new Line.Info[list.size()]);
    }
    
    @Override
    public Line[] getSourceLines() {
        final Line[] array;
        synchronized (this.control_mutex) {
            if (this.mainmixer == null) {
                return new Line[0];
            }
            final SoftMixingDataLine[] openLines = this.mainmixer.getOpenLines();
            array = new Line[openLines.length];
            for (int i = 0; i < array.length; ++i) {
                array[i] = openLines[i];
            }
        }
        return array;
    }
    
    @Override
    public Line.Info[] getTargetLineInfo() {
        return new Line.Info[0];
    }
    
    @Override
    public Line.Info[] getTargetLineInfo(final Line.Info info) {
        return new Line.Info[0];
    }
    
    @Override
    public Line[] getTargetLines() {
        return new Line[0];
    }
    
    @Override
    public boolean isLineSupported(final Line.Info info) {
        if (info != null) {
            for (int i = 0; i < this.sourceLineInfo.length; ++i) {
                if (info.matches(this.sourceLineInfo[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean isSynchronizationSupported(final Line[] array, final boolean b) {
        return false;
    }
    
    @Override
    public void synchronize(final Line[] array, final boolean b) {
        throw new IllegalArgumentException("Synchronization not supported by this mixer.");
    }
    
    @Override
    public void unsynchronize(final Line[] array) {
        throw new IllegalArgumentException("Synchronization not supported by this mixer.");
    }
    
    @Override
    public void addLineListener(final LineListener lineListener) {
        synchronized (this.control_mutex) {
            this.listeners.add(lineListener);
        }
    }
    
    private void sendEvent(final LineEvent lineEvent) {
        if (this.listeners.size() == 0) {
            return;
        }
        final LineListener[] array = this.listeners.toArray(new LineListener[this.listeners.size()]);
        for (int length = array.length, i = 0; i < length; ++i) {
            array[i].update(lineEvent);
        }
    }
    
    @Override
    public void close() {
        if (!this.isOpen()) {
            return;
        }
        this.sendEvent(new LineEvent(this, LineEvent.Type.CLOSE, -1L));
        SoftAudioPusher pusher = null;
        AudioInputStream pusher_stream = null;
        synchronized (this.control_mutex) {
            if (this.pusher != null) {
                pusher = this.pusher;
                pusher_stream = this.pusher_stream;
                this.pusher = null;
                this.pusher_stream = null;
            }
        }
        if (pusher != null) {
            pusher.stop();
            try {
                pusher_stream.close();
            }
            catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
        synchronized (this.control_mutex) {
            if (this.mainmixer != null) {
                this.mainmixer.close();
            }
            this.open = false;
            if (this.sourceDataLine != null) {
                this.sourceDataLine.drain();
                this.sourceDataLine.close();
                this.sourceDataLine = null;
            }
        }
    }
    
    @Override
    public Control getControl(final Control.Type type) {
        throw new IllegalArgumentException("Unsupported control type : " + type);
    }
    
    @Override
    public Control[] getControls() {
        return new Control[0];
    }
    
    @Override
    public Line.Info getLineInfo() {
        return new Line.Info(Mixer.class);
    }
    
    @Override
    public boolean isControlSupported(final Control.Type type) {
        return false;
    }
    
    @Override
    public boolean isOpen() {
        synchronized (this.control_mutex) {
            return this.open;
        }
    }
    
    @Override
    public void open() throws LineUnavailableException {
        if (this.isOpen()) {
            this.implicitOpen = false;
            return;
        }
        this.open(null);
    }
    
    public void open(SourceDataLine sourceDataLine) throws LineUnavailableException {
        if (this.isOpen()) {
            this.implicitOpen = false;
            return;
        }
        synchronized (this.control_mutex) {
            try {
                if (sourceDataLine != null) {
                    this.format = sourceDataLine.getFormat();
                }
                final AudioInputStream openStream = this.openStream(this.getFormat());
                if (sourceDataLine == null) {
                    synchronized (SoftMixingMixerProvider.mutex) {
                        SoftMixingMixerProvider.lockthread = Thread.currentThread();
                    }
                    try {
                        final Mixer mixer = AudioSystem.getMixer(null);
                        if (mixer != null) {
                            Line.Info info = null;
                            AudioFormat format = null;
                            final Line.Info[] sourceLineInfo = mixer.getSourceLineInfo();
                        Label_0358:
                            for (int i = 0; i < sourceLineInfo.length; ++i) {
                                if (sourceLineInfo[i].getLineClass() == SourceDataLine.class) {
                                    final DataLine.Info info2 = (DataLine.Info)sourceLineInfo[i];
                                    final AudioFormat[] formats = info2.getFormats();
                                    for (int j = 0; j < formats.length; ++j) {
                                        final AudioFormat audioFormat = formats[j];
                                        if ((audioFormat.getChannels() == 2 || audioFormat.getChannels() == -1) && (audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) || audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) && (audioFormat.getSampleRate() == -1.0f || audioFormat.getSampleRate() == 48000.0) && (audioFormat.getSampleSizeInBits() == -1 || audioFormat.getSampleSizeInBits() == 16)) {
                                            info = info2;
                                            int channels = audioFormat.getChannels();
                                            final boolean equals = audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
                                            float sampleRate = audioFormat.getSampleRate();
                                            final boolean bigEndian = audioFormat.isBigEndian();
                                            int sampleSizeInBits = audioFormat.getSampleSizeInBits();
                                            if (sampleSizeInBits == -1) {
                                                sampleSizeInBits = 16;
                                            }
                                            if (channels == -1) {
                                                channels = 2;
                                            }
                                            if (sampleRate == -1.0f) {
                                                sampleRate = 48000.0f;
                                            }
                                            format = new AudioFormat(sampleRate, sampleSizeInBits, channels, equals, bigEndian);
                                            break Label_0358;
                                        }
                                    }
                                }
                            }
                            if (format != null) {
                                this.format = format;
                                sourceDataLine = (SourceDataLine)mixer.getLine(info);
                            }
                        }
                        if (sourceDataLine == null) {
                            sourceDataLine = AudioSystem.getSourceDataLine(this.format);
                        }
                    }
                    finally {
                        synchronized (SoftMixingMixerProvider.mutex) {
                            SoftMixingMixerProvider.lockthread = null;
                        }
                    }
                    if (sourceDataLine == null) {
                        throw new IllegalArgumentException("No line matching " + SoftMixingMixer.info.toString() + " is supported.");
                    }
                }
                this.getClass();
                final double n = 100000.0;
                if (!sourceDataLine.isOpen()) {
                    sourceDataLine.open(this.getFormat(), this.getFormat().getFrameSize() * (int)(this.getFormat().getFrameRate() * (n / 1000000.0)));
                    this.sourceDataLine = sourceDataLine;
                }
                if (!sourceDataLine.isActive()) {
                    sourceDataLine.start();
                }
                int available = 512;
                try {
                    available = openStream.available();
                }
                catch (final IOException ex) {}
                final int bufferSize = sourceDataLine.getBufferSize();
                if (bufferSize - bufferSize % available < 3 * available) {}
                this.pusher = new SoftAudioPusher(sourceDataLine, openStream, available);
                this.pusher_stream = openStream;
                this.pusher.start();
            }
            catch (final LineUnavailableException ex2) {
                if (this.isOpen()) {
                    this.close();
                }
                throw new LineUnavailableException(ex2.toString());
            }
        }
    }
    
    public AudioInputStream openStream(final AudioFormat format) throws LineUnavailableException {
        if (this.isOpen()) {
            throw new LineUnavailableException("Mixer is already open");
        }
        synchronized (this.control_mutex) {
            this.open = true;
            this.implicitOpen = false;
            if (format != null) {
                this.format = format;
            }
            this.mainmixer = new SoftMixingMainMixer(this);
            this.sendEvent(new LineEvent(this, LineEvent.Type.OPEN, -1L));
            return this.mainmixer.getInputStream();
        }
    }
    
    @Override
    public void removeLineListener(final LineListener lineListener) {
        synchronized (this.control_mutex) {
            this.listeners.remove(lineListener);
        }
    }
    
    public long getLatency() {
        synchronized (this.control_mutex) {
            return 100000L;
        }
    }
    
    public AudioFormat getFormat() {
        synchronized (this.control_mutex) {
            return this.format;
        }
    }
    
    float getControlRate() {
        return 147.0f;
    }
    
    SoftMixingMainMixer getMainMixer() {
        if (!this.isOpen()) {
            return null;
        }
        return this.mainmixer;
    }
    
    static {
        info = new Info();
    }
    
    private static class Info extends Mixer.Info
    {
        Info() {
            super("Gervill Sound Mixer", "OpenJDK Proposal", "Software Sound Mixer", "1.0");
        }
    }
}
