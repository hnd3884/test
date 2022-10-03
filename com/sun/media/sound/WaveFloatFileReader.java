package com.sun.media.sound;

import java.io.FileInputStream;
import java.io.File;
import java.io.BufferedInputStream;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFileFormat;
import java.io.InputStream;
import javax.sound.sampled.spi.AudioFileReader;

public final class WaveFloatFileReader extends AudioFileReader
{
    @Override
    public AudioFileFormat getAudioFileFormat(final InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        inputStream.mark(200);
        AudioFileFormat internal_getAudioFileFormat;
        try {
            internal_getAudioFileFormat = this.internal_getAudioFileFormat(inputStream);
        }
        finally {
            inputStream.reset();
        }
        return internal_getAudioFileFormat;
    }
    
    private AudioFileFormat internal_getAudioFileFormat(final InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        final RIFFReader riffReader = new RIFFReader(inputStream);
        if (!riffReader.getFormat().equals("RIFF")) {
            throw new UnsupportedAudioFileException();
        }
        if (!riffReader.getType().equals("WAVE")) {
            throw new UnsupportedAudioFileException();
        }
        boolean b = false;
        boolean b2 = false;
        int unsignedShort = 1;
        long unsignedInt = 1L;
        int unsignedShort2 = 1;
        int unsignedShort3 = 1;
        while (riffReader.hasNextChunk()) {
            final RIFFReader nextChunk = riffReader.nextChunk();
            if (nextChunk.getFormat().equals("fmt ")) {
                b = true;
                if (nextChunk.readUnsignedShort() != 3) {
                    throw new UnsupportedAudioFileException();
                }
                unsignedShort = nextChunk.readUnsignedShort();
                unsignedInt = nextChunk.readUnsignedInt();
                nextChunk.readUnsignedInt();
                unsignedShort2 = nextChunk.readUnsignedShort();
                unsignedShort3 = nextChunk.readUnsignedShort();
            }
            if (nextChunk.getFormat().equals("data")) {
                b2 = true;
                break;
            }
        }
        if (!b) {
            throw new UnsupportedAudioFileException();
        }
        if (!b2) {
            throw new UnsupportedAudioFileException();
        }
        return new AudioFileFormat(AudioFileFormat.Type.WAVE, new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, (float)unsignedInt, unsignedShort3, unsignedShort, unsignedShort2, (float)unsignedInt, false), -1);
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        final AudioFileFormat audioFileFormat = this.getAudioFileFormat(inputStream);
        final RIFFReader riffReader = new RIFFReader(inputStream);
        if (!riffReader.getFormat().equals("RIFF")) {
            throw new UnsupportedAudioFileException();
        }
        if (!riffReader.getType().equals("WAVE")) {
            throw new UnsupportedAudioFileException();
        }
        while (riffReader.hasNextChunk()) {
            final RIFFReader nextChunk = riffReader.nextChunk();
            if (nextChunk.getFormat().equals("data")) {
                return new AudioInputStream(nextChunk, audioFileFormat.getFormat(), nextChunk.getSize());
            }
        }
        throw new UnsupportedAudioFileException();
    }
    
    @Override
    public AudioFileFormat getAudioFileFormat(final URL url) throws UnsupportedAudioFileException, IOException {
        final InputStream openStream = url.openStream();
        AudioFileFormat audioFileFormat;
        try {
            audioFileFormat = this.getAudioFileFormat(new BufferedInputStream(openStream));
        }
        finally {
            openStream.close();
        }
        return audioFileFormat;
    }
    
    @Override
    public AudioFileFormat getAudioFileFormat(final File file) throws UnsupportedAudioFileException, IOException {
        final FileInputStream fileInputStream = new FileInputStream(file);
        AudioFileFormat audioFileFormat;
        try {
            audioFileFormat = this.getAudioFileFormat(new BufferedInputStream(fileInputStream));
        }
        finally {
            fileInputStream.close();
        }
        return audioFileFormat;
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final URL url) throws UnsupportedAudioFileException, IOException {
        return this.getAudioInputStream(new BufferedInputStream(url.openStream()));
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final File file) throws UnsupportedAudioFileException, IOException {
        return this.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
    }
}
