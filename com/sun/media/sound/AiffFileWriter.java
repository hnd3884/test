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
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;

public final class AiffFileWriter extends SunFileWriter
{
    private static final int DOUBLE_MANTISSA_LENGTH = 52;
    private static final int DOUBLE_EXPONENT_LENGTH = 11;
    private static final long DOUBLE_SIGN_MASK = Long.MIN_VALUE;
    private static final long DOUBLE_EXPONENT_MASK = 9218868437227405312L;
    private static final long DOUBLE_MANTISSA_MASK = 4503599627370495L;
    private static final int DOUBLE_EXPONENT_OFFSET = 1023;
    private static final int EXTENDED_EXPONENT_OFFSET = 16383;
    private static final int EXTENDED_MANTISSA_LENGTH = 63;
    private static final int EXTENDED_EXPONENT_LENGTH = 15;
    private static final long EXTENDED_INTEGER_MASK = Long.MIN_VALUE;
    
    public AiffFileWriter() {
        super(new AudioFileFormat.Type[] { AudioFileFormat.Type.AIFF });
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
        final AiffFileFormat aiffFileFormat = (AiffFileFormat)this.getAudioFileFormat(type, audioInputStream);
        if (audioInputStream.getFrameLength() == -1L) {
            throw new IOException("stream length not specified");
        }
        return this.writeAiffFile(audioInputStream, aiffFileFormat, outputStream);
    }
    
    @Override
    public int write(final AudioInputStream audioInputStream, final AudioFileFormat.Type type, final File file) throws IOException {
        final AiffFileFormat aiffFileFormat = (AiffFileFormat)this.getAudioFileFormat(type, audioInputStream);
        final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file), 4096);
        final int writeAiffFile = this.writeAiffFile(audioInputStream, aiffFileFormat, bufferedOutputStream);
        bufferedOutputStream.close();
        if (aiffFileFormat.getByteLength() == -1) {
            final int n = aiffFileFormat.getFormat().getChannels() * aiffFileFormat.getFormat().getSampleSizeInBits();
            final int n2 = writeAiffFile;
            final int n3 = n2 - aiffFileFormat.getHeaderSize() + 16;
            final int n4 = (int)((n3 - 16) * 8L / n);
            final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.skipBytes(4);
            randomAccessFile.writeInt(n2 - 8);
            randomAccessFile.skipBytes(4 + aiffFileFormat.getFverChunkSize() + 4 + 4 + 2);
            randomAccessFile.writeInt(n4);
            randomAccessFile.skipBytes(16);
            randomAccessFile.writeInt(n3 - 8);
            randomAccessFile.close();
        }
        return writeAiffFile;
    }
    
    private AudioFileFormat getAudioFileFormat(final AudioFileFormat.Type type, final AudioInputStream audioInputStream) {
        final AudioFormat.Encoding pcm_SIGNED = AudioFormat.Encoding.PCM_SIGNED;
        final AudioFormat format = audioInputStream.getFormat();
        final AudioFormat.Encoding encoding = format.getEncoding();
        boolean b = false;
        if (!this.types[0].equals(type)) {
            throw new IllegalArgumentException("File type " + type + " not supported.");
        }
        AudioFormat.Encoding encoding2;
        int sampleSizeInBits;
        if (AudioFormat.Encoding.ALAW.equals(encoding) || AudioFormat.Encoding.ULAW.equals(encoding)) {
            if (format.getSampleSizeInBits() != 8) {
                throw new IllegalArgumentException("Encoding " + encoding + " supported only for 8-bit data.");
            }
            encoding2 = AudioFormat.Encoding.PCM_SIGNED;
            sampleSizeInBits = 16;
            b = true;
        }
        else if (format.getSampleSizeInBits() == 8) {
            encoding2 = AudioFormat.Encoding.PCM_UNSIGNED;
            sampleSizeInBits = 8;
        }
        else {
            encoding2 = AudioFormat.Encoding.PCM_SIGNED;
            sampleSizeInBits = format.getSampleSizeInBits();
        }
        final AudioFormat audioFormat = new AudioFormat(encoding2, format.getSampleRate(), sampleSizeInBits, format.getChannels(), format.getFrameSize(), format.getFrameRate(), true);
        int n;
        if (audioInputStream.getFrameLength() != -1L) {
            if (b) {
                n = (int)audioInputStream.getFrameLength() * format.getFrameSize() * 2 + 54;
            }
            else {
                n = (int)audioInputStream.getFrameLength() * format.getFrameSize() + 54;
            }
        }
        else {
            n = -1;
        }
        return new AiffFileFormat(AudioFileFormat.Type.AIFF, n, audioFormat, (int)audioInputStream.getFrameLength());
    }
    
    private int writeAiffFile(final InputStream inputStream, final AiffFileFormat aiffFileFormat, final OutputStream outputStream) throws IOException {
        int n = 0;
        final InputStream fileStream = this.getFileStream(aiffFileFormat, inputStream);
        final byte[] array = new byte[4096];
        int byteLength = aiffFileFormat.getByteLength();
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
    
    private InputStream getFileStream(final AiffFileFormat aiffFileFormat, final InputStream inputStream) throws IOException {
        final AudioFormat format = aiffFileFormat.getFormat();
        final int headerSize = aiffFileFormat.getHeaderSize();
        aiffFileFormat.getFverChunkSize();
        final int commChunkSize = aiffFileFormat.getCommChunkSize();
        int n = -1;
        int n2 = -1;
        aiffFileFormat.getSsndChunkOffset();
        final short n3 = (short)format.getChannels();
        final short n4 = (short)format.getSampleSizeInBits();
        final int n5 = n3 * n4;
        final int frameLength = aiffFileFormat.getFrameLength();
        if (frameLength != -1) {
            final long n6 = frameLength * (long)n5 / 8L;
            n2 = (int)n6 + 16;
            n = (int)n6 + headerSize;
        }
        final float sampleRate = format.getSampleRate();
        InputStream inputStream2 = inputStream;
        if (inputStream instanceof AudioInputStream) {
            final AudioFormat format2 = ((AudioInputStream)inputStream).getFormat();
            final AudioFormat.Encoding encoding = format2.getEncoding();
            if (AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding) || (AudioFormat.Encoding.PCM_SIGNED.equals(encoding) && !format2.isBigEndian())) {
                inputStream2 = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format2.getSampleRate(), format2.getSampleSizeInBits(), format2.getChannels(), format2.getFrameSize(), format2.getFrameRate(), true), (AudioInputStream)inputStream);
            }
            else if (AudioFormat.Encoding.ULAW.equals(encoding) || AudioFormat.Encoding.ALAW.equals(encoding)) {
                if (format2.getSampleSizeInBits() != 8) {
                    throw new IllegalArgumentException("unsupported encoding");
                }
                inputStream2 = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format2.getSampleRate(), format2.getSampleSizeInBits() * 2, format2.getChannels(), format2.getFrameSize() * 2, format2.getFrameRate(), true), (AudioInputStream)inputStream);
            }
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeInt(1179603533);
        dataOutputStream.writeInt(n - 8);
        dataOutputStream.writeInt(1095321158);
        dataOutputStream.writeInt(1129270605);
        dataOutputStream.writeInt(commChunkSize - 8);
        dataOutputStream.writeShort(n3);
        dataOutputStream.writeInt(frameLength);
        dataOutputStream.writeShort(n4);
        this.write_ieee_extended(dataOutputStream, sampleRate);
        dataOutputStream.writeInt(1397968452);
        dataOutputStream.writeInt(n2 - 8);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.close();
        return new SequenceInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), new NoCloseInputStream(inputStream2));
    }
    
    private void write_ieee_extended(final DataOutputStream dataOutputStream, final float n) throws IOException {
        final long doubleToLongBits = Double.doubleToLongBits(n);
        final long n2 = (doubleToLongBits & Long.MIN_VALUE) >> 63;
        final long n3 = (doubleToLongBits & 0x7FF0000000000000L) >> 52;
        final long n4 = doubleToLongBits & 0xFFFFFFFFFFFFFL;
        final long n5 = n3 - 1023L + 16383L;
        final long n6 = n4 << 11;
        final short n7 = (short)(n2 << 15 | n5);
        final long n8 = Long.MIN_VALUE | n6;
        dataOutputStream.writeShort(n7);
        dataOutputStream.writeLong(n8);
    }
}
