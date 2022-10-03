package sun.audio;

import javax.sound.midi.MetaMessage;
import javax.sound.sampled.AudioFormat;
import java.io.BufferedInputStream;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.MetaEventListener;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.midi.Sequencer;
import com.sun.media.sound.DataPusher;
import javax.sound.sampled.Line;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import com.sun.media.sound.Toolkit;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Mixer;
import java.util.Vector;
import java.util.Hashtable;

public final class AudioDevice
{
    private boolean DEBUG;
    private Hashtable clipStreams;
    private Vector infos;
    private boolean playing;
    private Mixer mixer;
    public static final AudioDevice device;
    
    private AudioDevice() {
        this.DEBUG = false;
        this.playing = false;
        this.mixer = null;
        this.clipStreams = new Hashtable();
        this.infos = new Vector();
    }
    
    private synchronized void startSampled(AudioInputStream pcmConvertedAudioInputStream, final InputStream inputStream) throws UnsupportedAudioFileException, LineUnavailableException {
        pcmConvertedAudioInputStream = Toolkit.getPCMConvertedAudioInputStream(pcmConvertedAudioInputStream);
        if (pcmConvertedAudioInputStream == null) {
            return;
        }
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class, pcmConvertedAudioInputStream.getFormat());
        if (!AudioSystem.isLineSupported(info)) {
            return;
        }
        final DataPusher dataPusher = new DataPusher((SourceDataLine)AudioSystem.getLine(info), pcmConvertedAudioInputStream);
        this.infos.addElement(new Info(null, inputStream, dataPusher));
        dataPusher.start();
    }
    
    private synchronized void startMidi(final InputStream sequence, final InputStream inputStream) throws InvalidMidiDataException, MidiUnavailableException {
        final Sequencer sequencer = MidiSystem.getSequencer();
        sequencer.open();
        try {
            sequencer.setSequence(sequence);
        }
        catch (final IOException ex) {
            throw new InvalidMidiDataException(ex.getMessage());
        }
        final Info info = new Info(sequencer, inputStream, null);
        this.infos.addElement(info);
        sequencer.addMetaEventListener(info);
        sequencer.start();
    }
    
    public synchronized void openChannel(final InputStream inputStream) {
        if (this.DEBUG) {
            System.out.println("AudioDevice: openChannel");
            System.out.println("input stream =" + inputStream);
        }
        for (int i = 0; i < this.infos.size(); ++i) {
            if (((Info)this.infos.elementAt(i)).in == inputStream) {
                return;
            }
        }
        Label_0368: {
            if (inputStream instanceof AudioStream) {
                if (((AudioStream)inputStream).midiformat != null) {
                    try {
                        this.startMidi(((AudioStream)inputStream).stream, inputStream);
                        break Label_0368;
                    }
                    catch (final Exception ex) {
                        return;
                    }
                }
                if (((AudioStream)inputStream).ais == null) {
                    break Label_0368;
                }
                try {
                    this.startSampled(((AudioStream)inputStream).ais, inputStream);
                    break Label_0368;
                }
                catch (final Exception ex2) {
                    return;
                }
            }
            if (inputStream instanceof AudioDataStream) {
                if (inputStream instanceof ContinuousAudioDataStream) {
                    try {
                        this.startSampled(new AudioInputStream(inputStream, ((AudioDataStream)inputStream).getAudioData().format, -1L), inputStream);
                        break Label_0368;
                    }
                    catch (final Exception ex3) {
                        return;
                    }
                }
                try {
                    this.startSampled(new AudioInputStream(inputStream, ((AudioDataStream)inputStream).getAudioData().format, ((AudioDataStream)inputStream).getAudioData().buffer.length), inputStream);
                    break Label_0368;
                }
                catch (final Exception ex4) {
                    return;
                }
            }
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 1024);
            try {
                AudioInputStream audioInputStream;
                try {
                    audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
                }
                catch (final IOException ex5) {
                    return;
                }
                this.startSampled(audioInputStream, inputStream);
            }
            catch (final UnsupportedAudioFileException ex6) {
                try {
                    try {
                        MidiSystem.getMidiFileFormat(bufferedInputStream);
                    }
                    catch (final IOException ex7) {
                        return;
                    }
                    this.startMidi(bufferedInputStream, inputStream);
                }
                catch (final InvalidMidiDataException ex8) {
                    final AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.ULAW, 8000.0f, 8, 1, 1, 8000.0f, true);
                    try {
                        this.startSampled(new AudioInputStream(bufferedInputStream, audioFormat, -1L), inputStream);
                    }
                    catch (final UnsupportedAudioFileException ex9) {
                        return;
                    }
                    catch (final LineUnavailableException ex10) {
                        return;
                    }
                }
                catch (final MidiUnavailableException ex11) {
                    return;
                }
            }
            catch (final LineUnavailableException ex12) {
                return;
            }
        }
        this.notify();
    }
    
    public synchronized void closeChannel(final InputStream inputStream) {
        if (this.DEBUG) {
            System.out.println("AudioDevice.closeChannel");
        }
        if (inputStream == null) {
            return;
        }
        for (int i = 0; i < this.infos.size(); ++i) {
            final Info info = this.infos.elementAt(i);
            if (info.in == inputStream) {
                if (info.sequencer != null) {
                    info.sequencer.stop();
                    this.infos.removeElement(info);
                }
                else if (info.datapusher != null) {
                    info.datapusher.stop();
                    this.infos.removeElement(info);
                }
            }
        }
        this.notify();
    }
    
    public synchronized void open() {
    }
    
    public synchronized void close() {
    }
    
    public void play() {
        if (this.DEBUG) {
            System.out.println("exiting play()");
        }
    }
    
    public synchronized void closeStreams() {
        for (int i = 0; i < this.infos.size(); ++i) {
            final Info info = this.infos.elementAt(i);
            if (info.sequencer != null) {
                info.sequencer.stop();
                info.sequencer.close();
                this.infos.removeElement(info);
            }
            else if (info.datapusher != null) {
                info.datapusher.stop();
                this.infos.removeElement(info);
            }
        }
        if (this.DEBUG) {
            System.err.println("Audio Device: Streams all closed.");
        }
        this.clipStreams = new Hashtable();
        this.infos = new Vector();
    }
    
    public int openChannels() {
        return this.infos.size();
    }
    
    void setVerbose(final boolean debug) {
        this.DEBUG = debug;
    }
    
    static {
        device = new AudioDevice();
    }
    
    final class Info implements MetaEventListener
    {
        final Sequencer sequencer;
        final InputStream in;
        final DataPusher datapusher;
        
        Info(final Sequencer sequencer, final InputStream in, final DataPusher datapusher) {
            this.sequencer = sequencer;
            this.in = in;
            this.datapusher = datapusher;
        }
        
        @Override
        public void meta(final MetaMessage metaMessage) {
            if (metaMessage.getType() == 47 && this.sequencer != null) {
                this.sequencer.close();
            }
        }
    }
}
