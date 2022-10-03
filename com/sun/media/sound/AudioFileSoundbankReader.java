package com.sun.media.sound;

import java.io.File;
import javax.sound.midi.Instrument;
import java.io.DataInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioSystem;
import javax.sound.midi.Soundbank;
import java.net.URL;
import javax.sound.midi.spi.SoundbankReader;

public final class AudioFileSoundbankReader extends SoundbankReader
{
    @Override
    public Soundbank getSoundbank(final URL url) throws InvalidMidiDataException, IOException {
        try {
            final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            final Soundbank soundbank = this.getSoundbank(audioInputStream);
            audioInputStream.close();
            return soundbank;
        }
        catch (final UnsupportedAudioFileException ex) {
            return null;
        }
        catch (final IOException ex2) {
            return null;
        }
    }
    
    @Override
    public Soundbank getSoundbank(final InputStream inputStream) throws InvalidMidiDataException, IOException {
        inputStream.mark(512);
        try {
            final Soundbank soundbank = this.getSoundbank(AudioSystem.getAudioInputStream(inputStream));
            if (soundbank != null) {
                return soundbank;
            }
        }
        catch (final UnsupportedAudioFileException ex) {}
        catch (final IOException ex2) {}
        inputStream.reset();
        return null;
    }
    
    public Soundbank getSoundbank(final AudioInputStream audioInputStream) throws InvalidMidiDataException, IOException {
        try {
            byte[] byteArray;
            if (audioInputStream.getFrameLength() == -1L) {
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                final byte[] array = new byte[1024 - 1024 % audioInputStream.getFormat().getFrameSize()];
                int read;
                while ((read = audioInputStream.read(array)) != -1) {
                    byteArrayOutputStream.write(array, 0, read);
                }
                audioInputStream.close();
                byteArray = byteArrayOutputStream.toByteArray();
            }
            else {
                byteArray = new byte[(int)(audioInputStream.getFrameLength() * audioInputStream.getFormat().getFrameSize())];
                new DataInputStream(audioInputStream).readFully(byteArray);
            }
            final ModelByteBufferWavetable modelByteBufferWavetable = new ModelByteBufferWavetable(new ModelByteBuffer(byteArray), audioInputStream.getFormat(), -4800.0f);
            final ModelPerformer modelPerformer = new ModelPerformer();
            modelPerformer.getOscillators().add(modelByteBufferWavetable);
            final SimpleSoundbank simpleSoundbank = new SimpleSoundbank();
            final SimpleInstrument simpleInstrument = new SimpleInstrument();
            simpleInstrument.add(modelPerformer);
            simpleSoundbank.addInstrument(simpleInstrument);
            return simpleSoundbank;
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    @Override
    public Soundbank getSoundbank(final File file) throws InvalidMidiDataException, IOException {
        try {
            AudioSystem.getAudioInputStream(file).close();
            final ModelByteBufferWavetable modelByteBufferWavetable = new ModelByteBufferWavetable(new ModelByteBuffer(file, 0L, file.length()), -4800.0f);
            final ModelPerformer modelPerformer = new ModelPerformer();
            modelPerformer.getOscillators().add(modelByteBufferWavetable);
            final SimpleSoundbank simpleSoundbank = new SimpleSoundbank();
            final SimpleInstrument simpleInstrument = new SimpleInstrument();
            simpleInstrument.add(modelPerformer);
            simpleSoundbank.addInstrument(simpleInstrument);
            return simpleSoundbank;
        }
        catch (final UnsupportedAudioFileException ex) {
            return null;
        }
        catch (final IOException ex2) {
            return null;
        }
    }
}
