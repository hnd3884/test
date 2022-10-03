package com.sun.media.sound;

import java.io.ByteArrayOutputStream;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.midi.MetaMessage;
import javax.sound.sampled.LineEvent;
import javax.sound.midi.MidiUnavailableException;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineListener;
import javax.sound.midi.MetaEventListener;
import java.applet.AudioClip;

public final class JavaSoundAudioClip implements AudioClip, MetaEventListener, LineListener
{
    private static final boolean DEBUG = false;
    private static final int BUFFER_SIZE = 16384;
    private long lastPlayCall;
    private static final int MINIMUM_PLAY_DELAY = 30;
    private byte[] loadedAudio;
    private int loadedAudioByteLength;
    private AudioFormat loadedAudioFormat;
    private AutoClosingClip clip;
    private boolean clipLooping;
    private DataPusher datapusher;
    private Sequencer sequencer;
    private Sequence sequence;
    private boolean sequencerloop;
    private static final long CLIP_THRESHOLD = 1048576L;
    private static final int STREAM_BUFFER_SIZE = 1024;
    
    public JavaSoundAudioClip(final InputStream inputStream) throws IOException {
        this.lastPlayCall = 0L;
        this.loadedAudio = null;
        this.loadedAudioByteLength = 0;
        this.loadedAudioFormat = null;
        this.clip = null;
        this.clipLooping = false;
        this.datapusher = null;
        this.sequencer = null;
        this.sequence = null;
        this.sequencerloop = false;
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 1024);
        bufferedInputStream.mark(1024);
        int n;
        try {
            n = (this.loadAudioData(AudioSystem.getAudioInputStream(bufferedInputStream)) ? 1 : 0);
            if (n != 0) {
                n = 0;
                if (this.loadedAudioByteLength < 1048576L) {
                    n = (this.createClip() ? 1 : 0);
                }
                if (n == 0) {
                    n = (this.createSourceDataLine() ? 1 : 0);
                }
            }
        }
        catch (final UnsupportedAudioFileException ex) {
            try {
                MidiSystem.getMidiFileFormat(bufferedInputStream);
                n = (this.createSequencer(bufferedInputStream) ? 1 : 0);
            }
            catch (final InvalidMidiDataException ex2) {
                n = 0;
            }
        }
        if (n == 0) {
            throw new IOException("Unable to create AudioClip from input stream");
        }
    }
    
    @Override
    public synchronized void play() {
        this.startImpl(false);
    }
    
    @Override
    public synchronized void loop() {
        this.startImpl(true);
    }
    
    private synchronized void startImpl(final boolean b) {
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lastPlayCall < 30L) {
            return;
        }
        this.lastPlayCall = currentTimeMillis;
        try {
            if (this.clip != null) {
                this.clip.setAutoClosing(false);
                try {
                    if (!this.clip.isOpen()) {
                        this.clip.open(this.loadedAudioFormat, this.loadedAudio, 0, this.loadedAudioByteLength);
                    }
                    else {
                        this.clip.flush();
                        if (b != this.clipLooping) {
                            this.clip.stop();
                        }
                    }
                    this.clip.setFramePosition(0);
                    if (b) {
                        this.clip.loop(-1);
                    }
                    else {
                        this.clip.start();
                    }
                    this.clipLooping = b;
                }
                finally {
                    this.clip.setAutoClosing(true);
                }
            }
            else if (this.datapusher != null) {
                this.datapusher.start(b);
            }
            else if (this.sequencer != null) {
                this.sequencerloop = b;
                if (this.sequencer.isRunning()) {
                    this.sequencer.setMicrosecondPosition(0L);
                }
                if (!this.sequencer.isOpen()) {
                    try {
                        this.sequencer.open();
                        this.sequencer.setSequence(this.sequence);
                    }
                    catch (final InvalidMidiDataException ex) {}
                    catch (final MidiUnavailableException ex2) {}
                }
                this.sequencer.addMetaEventListener(this);
                try {
                    this.sequencer.start();
                }
                catch (final Exception ex3) {}
            }
        }
        catch (final Exception ex4) {}
    }
    
    @Override
    public synchronized void stop() {
        this.lastPlayCall = 0L;
        if (this.clip != null) {
            try {
                this.clip.flush();
            }
            catch (final Exception ex) {}
            try {
                this.clip.stop();
            }
            catch (final Exception ex2) {}
        }
        else if (this.datapusher != null) {
            this.datapusher.stop();
        }
        else if (this.sequencer != null) {
            try {
                this.sequencerloop = false;
                this.sequencer.addMetaEventListener(this);
                this.sequencer.stop();
            }
            catch (final Exception ex3) {}
            try {
                this.sequencer.close();
            }
            catch (final Exception ex4) {}
        }
    }
    
    @Override
    public synchronized void update(final LineEvent lineEvent) {
    }
    
    @Override
    public synchronized void meta(final MetaMessage metaMessage) {
        if (metaMessage.getType() == 47) {
            if (this.sequencerloop) {
                this.sequencer.setMicrosecondPosition(0L);
                this.loop();
            }
            else {
                this.stop();
            }
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().toString();
    }
    
    @Override
    protected void finalize() {
        if (this.clip != null) {
            this.clip.close();
        }
        if (this.datapusher != null) {
            this.datapusher.close();
        }
        if (this.sequencer != null) {
            this.sequencer.close();
        }
    }
    
    private boolean loadAudioData(AudioInputStream pcmConvertedAudioInputStream) throws IOException, UnsupportedAudioFileException {
        pcmConvertedAudioInputStream = Toolkit.getPCMConvertedAudioInputStream(pcmConvertedAudioInputStream);
        if (pcmConvertedAudioInputStream == null) {
            return false;
        }
        this.loadedAudioFormat = pcmConvertedAudioInputStream.getFormat();
        final long frameLength = pcmConvertedAudioInputStream.getFrameLength();
        final int frameSize = this.loadedAudioFormat.getFrameSize();
        long n = -1L;
        if (frameLength != -1L && frameLength > 0L && frameSize != -1 && frameSize > 0) {
            n = frameLength * frameSize;
        }
        if (n != -1L) {
            this.readStream(pcmConvertedAudioInputStream, n);
        }
        else {
            this.readStream(pcmConvertedAudioInputStream);
        }
        return true;
    }
    
    private void readStream(final AudioInputStream audioInputStream, final long n) throws IOException {
        int n2;
        if (n > 2147483647L) {
            n2 = Integer.MAX_VALUE;
        }
        else {
            n2 = (int)n;
        }
        this.loadedAudio = new byte[n2];
        this.loadedAudioByteLength = 0;
        while (true) {
            final int read = audioInputStream.read(this.loadedAudio, this.loadedAudioByteLength, n2 - this.loadedAudioByteLength);
            if (read <= 0) {
                break;
            }
            this.loadedAudioByteLength += read;
        }
        audioInputStream.close();
    }
    
    private void readStream(final AudioInputStream audioInputStream) throws IOException {
        final DirectBAOS directBAOS = new DirectBAOS();
        final byte[] array = new byte[16384];
        int loadedAudioByteLength = 0;
        while (true) {
            final int read = audioInputStream.read(array, 0, array.length);
            if (read <= 0) {
                break;
            }
            loadedAudioByteLength += read;
            directBAOS.write(array, 0, read);
        }
        audioInputStream.close();
        this.loadedAudio = directBAOS.getInternalBuffer();
        this.loadedAudioByteLength = loadedAudioByteLength;
    }
    
    private boolean createClip() {
        try {
            final DataLine.Info info = new DataLine.Info(Clip.class, this.loadedAudioFormat);
            if (!AudioSystem.isLineSupported(info)) {
                return false;
            }
            final Line line = AudioSystem.getLine(info);
            if (!(line instanceof AutoClosingClip)) {
                return false;
            }
            (this.clip = (AutoClosingClip)line).setAutoClosing(true);
        }
        catch (final Exception ex) {
            return false;
        }
        return this.clip != null;
    }
    
    private boolean createSourceDataLine() {
        try {
            final DataLine.Info info = new DataLine.Info(SourceDataLine.class, this.loadedAudioFormat);
            if (!AudioSystem.isLineSupported(info)) {
                return false;
            }
            this.datapusher = new DataPusher((SourceDataLine)AudioSystem.getLine(info), this.loadedAudioFormat, this.loadedAudio, this.loadedAudioByteLength);
        }
        catch (final Exception ex) {
            return false;
        }
        return this.datapusher != null;
    }
    
    private boolean createSequencer(final BufferedInputStream bufferedInputStream) throws IOException {
        try {
            this.sequencer = MidiSystem.getSequencer();
        }
        catch (final MidiUnavailableException ex) {
            return false;
        }
        if (this.sequencer == null) {
            return false;
        }
        try {
            this.sequence = MidiSystem.getSequence(bufferedInputStream);
            if (this.sequence == null) {
                return false;
            }
        }
        catch (final InvalidMidiDataException ex2) {
            return false;
        }
        return true;
    }
    
    private static class DirectBAOS extends ByteArrayOutputStream
    {
        DirectBAOS() {
        }
        
        public byte[] getInternalBuffer() {
            return this.buf;
        }
    }
}
