package com.sun.media.sound;

import java.io.File;
import java.io.OutputStream;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.spi.AudioFileWriter;

public final class WaveFloatFileWriter extends AudioFileWriter
{
    @Override
    public AudioFileFormat.Type[] getAudioFileTypes() {
        return new AudioFileFormat.Type[] { AudioFileFormat.Type.WAVE };
    }
    
    @Override
    public AudioFileFormat.Type[] getAudioFileTypes(final AudioInputStream audioInputStream) {
        if (!audioInputStream.getFormat().getEncoding().equals(AudioFormat.Encoding.PCM_FLOAT)) {
            return new AudioFileFormat.Type[0];
        }
        return new AudioFileFormat.Type[] { AudioFileFormat.Type.WAVE };
    }
    
    private void checkFormat(final AudioFileFormat.Type type, final AudioInputStream audioInputStream) {
        if (!AudioFileFormat.Type.WAVE.equals(type)) {
            throw new IllegalArgumentException("File type " + type + " not supported.");
        }
        if (!audioInputStream.getFormat().getEncoding().equals(AudioFormat.Encoding.PCM_FLOAT)) {
            throw new IllegalArgumentException("File format " + audioInputStream.getFormat() + " not supported.");
        }
    }
    
    public void write(final AudioInputStream audioInputStream, final RIFFWriter riffWriter) throws IOException {
        final RIFFWriter writeChunk = riffWriter.writeChunk("fmt ");
        final AudioFormat format = audioInputStream.getFormat();
        writeChunk.writeUnsignedShort(3);
        writeChunk.writeUnsignedShort(format.getChannels());
        writeChunk.writeUnsignedInt((int)format.getSampleRate());
        writeChunk.writeUnsignedInt((int)format.getFrameRate() * format.getFrameSize());
        writeChunk.writeUnsignedShort(format.getFrameSize());
        writeChunk.writeUnsignedShort(format.getSampleSizeInBits());
        writeChunk.close();
        final RIFFWriter writeChunk2 = riffWriter.writeChunk("data");
        final byte[] array = new byte[1024];
        int read;
        while ((read = audioInputStream.read(array, 0, array.length)) != -1) {
            writeChunk2.write(array, 0, read);
        }
        writeChunk2.close();
    }
    
    private AudioInputStream toLittleEndian(final AudioInputStream audioInputStream) {
        final AudioFormat format = audioInputStream.getFormat();
        return AudioSystem.getAudioInputStream(new AudioFormat(format.getEncoding(), format.getSampleRate(), format.getSampleSizeInBits(), format.getChannels(), format.getFrameSize(), format.getFrameRate(), false), audioInputStream);
    }
    
    @Override
    public int write(AudioInputStream littleEndian, final AudioFileFormat.Type type, final OutputStream outputStream) throws IOException {
        this.checkFormat(type, littleEndian);
        if (littleEndian.getFormat().isBigEndian()) {
            littleEndian = this.toLittleEndian(littleEndian);
        }
        final RIFFWriter riffWriter = new RIFFWriter(new NoCloseOutputStream(outputStream), "WAVE");
        this.write(littleEndian, riffWriter);
        final int n = (int)riffWriter.getFilePointer();
        riffWriter.close();
        return n;
    }
    
    @Override
    public int write(AudioInputStream littleEndian, final AudioFileFormat.Type type, final File file) throws IOException {
        this.checkFormat(type, littleEndian);
        if (littleEndian.getFormat().isBigEndian()) {
            littleEndian = this.toLittleEndian(littleEndian);
        }
        final RIFFWriter riffWriter = new RIFFWriter(file, "WAVE");
        this.write(littleEndian, riffWriter);
        final int n = (int)riffWriter.getFilePointer();
        riffWriter.close();
        return n;
    }
    
    private static class NoCloseOutputStream extends OutputStream
    {
        final OutputStream out;
        
        NoCloseOutputStream(final OutputStream out) {
            this.out = out;
        }
        
        @Override
        public void write(final int n) throws IOException {
            this.out.write(n);
        }
        
        @Override
        public void flush() throws IOException {
            this.out.flush();
        }
        
        @Override
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            this.out.write(array, n, n2);
        }
        
        @Override
        public void write(final byte[] array) throws IOException {
            this.out.write(array);
        }
    }
}
