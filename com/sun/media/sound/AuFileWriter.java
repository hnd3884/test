package com.sun.media.sound;

import java.io.SequenceInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import javax.sound.sampled.AudioSystem;
import java.io.RandomAccessFile;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;

public final class AuFileWriter extends SunFileWriter
{
    public static final int UNKNOWN_SIZE = -1;
    
    public AuFileWriter() {
        super(new AudioFileFormat.Type[] { AudioFileFormat.Type.AU });
    }
    
    @Override
    public AudioFileFormat.Type[] getAudioFileTypes(final AudioInputStream audioInputStream) {
        final AudioFileFormat.Type[] array = new AudioFileFormat.Type[this.types.length];
        System.arraycopy(this.types, 0, array, 0, this.types.length);
        final AudioFormat.Encoding encoding = audioInputStream.getFormat().getEncoding();
        if (AudioFormat.Encoding.ALAW.equals(encoding) || AudioFormat.Encoding.ULAW.equals(encoding) || AudioFormat.Encoding.PCM_SIGNED.equals(encoding) || AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding)) {
            return array;
        }
        return new AudioFileFormat.Type[0];
    }
    
    @Override
    public int write(final AudioInputStream audioInputStream, final AudioFileFormat.Type type, final OutputStream outputStream) throws IOException {
        return this.writeAuFile(audioInputStream, (AuFileFormat)this.getAudioFileFormat(type, audioInputStream), outputStream);
    }
    
    @Override
    public int write(final AudioInputStream audioInputStream, final AudioFileFormat.Type type, final File file) throws IOException {
        final AuFileFormat auFileFormat = (AuFileFormat)this.getAudioFileFormat(type, audioInputStream);
        final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file), 4096);
        final int writeAuFile = this.writeAuFile(audioInputStream, auFileFormat, bufferedOutputStream);
        bufferedOutputStream.close();
        if (auFileFormat.getByteLength() == -1) {
            final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() <= 2147483647L) {
                randomAccessFile.skipBytes(8);
                randomAccessFile.writeInt(writeAuFile - 24);
            }
            randomAccessFile.close();
        }
        return writeAuFile;
    }
    
    private AudioFileFormat getAudioFileFormat(final AudioFileFormat.Type type, final AudioInputStream audioInputStream) {
        final AudioFormat.Encoding pcm_SIGNED = AudioFormat.Encoding.PCM_SIGNED;
        final AudioFormat format = audioInputStream.getFormat();
        final AudioFormat.Encoding encoding = format.getEncoding();
        if (!this.types[0].equals(type)) {
            throw new IllegalArgumentException("File type " + type + " not supported.");
        }
        AudioFormat.Encoding encoding2;
        int n;
        if (AudioFormat.Encoding.ALAW.equals(encoding) || AudioFormat.Encoding.ULAW.equals(encoding)) {
            encoding2 = encoding;
            n = format.getSampleSizeInBits();
        }
        else if (format.getSampleSizeInBits() == 8) {
            encoding2 = AudioFormat.Encoding.PCM_SIGNED;
            n = 8;
        }
        else {
            encoding2 = AudioFormat.Encoding.PCM_SIGNED;
            n = format.getSampleSizeInBits();
        }
        final AudioFormat audioFormat = new AudioFormat(encoding2, format.getSampleRate(), n, format.getChannels(), format.getFrameSize(), format.getFrameRate(), true);
        int n2;
        if (audioInputStream.getFrameLength() != -1L) {
            n2 = (int)audioInputStream.getFrameLength() * format.getFrameSize() + 24;
        }
        else {
            n2 = -1;
        }
        return new AuFileFormat(AudioFileFormat.Type.AU, n2, audioFormat, (int)audioInputStream.getFrameLength());
    }
    
    private InputStream getFileStream(final AuFileFormat auFileFormat, final InputStream inputStream) throws IOException {
        final AudioFormat format = auFileFormat.getFormat();
        final int n = 24;
        final long n2 = auFileFormat.getFrameLength();
        long n3 = (n2 == -1L) ? -1L : (n2 * format.getFrameSize());
        if (n3 > 2147483647L) {
            n3 = -1L;
        }
        final int auType = auFileFormat.getAuType();
        final int n4 = (int)format.getSampleRate();
        final int channels = format.getChannels();
        final boolean b = true;
        InputStream audioInputStream = inputStream;
        if (inputStream instanceof AudioInputStream) {
            final AudioFormat format2 = ((AudioInputStream)inputStream).getFormat();
            final AudioFormat.Encoding encoding = format2.getEncoding();
            if (AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding) || (AudioFormat.Encoding.PCM_SIGNED.equals(encoding) && b != format2.isBigEndian())) {
                audioInputStream = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format2.getSampleRate(), format2.getSampleSizeInBits(), format2.getChannels(), format2.getFrameSize(), format2.getFrameRate(), b), (AudioInputStream)inputStream);
            }
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        if (b) {
            dataOutputStream.writeInt(779316836);
            dataOutputStream.writeInt(n);
            dataOutputStream.writeInt((int)n3);
            dataOutputStream.writeInt(auType);
            dataOutputStream.writeInt(n4);
            dataOutputStream.writeInt(channels);
        }
        else {
            dataOutputStream.writeInt(1684960046);
            dataOutputStream.writeInt(this.big2little(n));
            dataOutputStream.writeInt(this.big2little((int)n3));
            dataOutputStream.writeInt(this.big2little(auType));
            dataOutputStream.writeInt(this.big2little(n4));
            dataOutputStream.writeInt(this.big2little(channels));
        }
        dataOutputStream.close();
        return new SequenceInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), new NoCloseInputStream(audioInputStream));
    }
    
    private int writeAuFile(final InputStream inputStream, final AuFileFormat auFileFormat, final OutputStream outputStream) throws IOException {
        int n = 0;
        final InputStream fileStream = this.getFileStream(auFileFormat, inputStream);
        final byte[] array = new byte[4096];
        int byteLength = auFileFormat.getByteLength();
        int read;
        while ((read = fileStream.read(array)) >= 0) {
            if (byteLength > 0) {
                if (read >= byteLength) {
                    outputStream.write(array, 0, byteLength);
                    n += byteLength;
                    break;
                }
                outputStream.write(array, 0, read);
                n += read;
                byteLength -= read;
            }
            else {
                outputStream.write(array, 0, read);
                n += read;
            }
        }
        return n;
    }
}
