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

public final class WaveFileWriter extends SunFileWriter
{
    static final int RIFF_MAGIC = 1380533830;
    static final int WAVE_MAGIC = 1463899717;
    static final int FMT_MAGIC = 1718449184;
    static final int DATA_MAGIC = 1684108385;
    static final int WAVE_FORMAT_UNKNOWN = 0;
    static final int WAVE_FORMAT_PCM = 1;
    static final int WAVE_FORMAT_ADPCM = 2;
    static final int WAVE_FORMAT_ALAW = 6;
    static final int WAVE_FORMAT_MULAW = 7;
    static final int WAVE_FORMAT_OKI_ADPCM = 16;
    static final int WAVE_FORMAT_DIGISTD = 21;
    static final int WAVE_FORMAT_DIGIFIX = 22;
    static final int WAVE_IBM_FORMAT_MULAW = 257;
    static final int WAVE_IBM_FORMAT_ALAW = 258;
    static final int WAVE_IBM_FORMAT_ADPCM = 259;
    static final int WAVE_FORMAT_DVI_ADPCM = 17;
    static final int WAVE_FORMAT_SX7383 = 7175;
    
    public WaveFileWriter() {
        super(new AudioFileFormat.Type[] { AudioFileFormat.Type.WAVE });
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
        final WaveFileFormat waveFileFormat = (WaveFileFormat)this.getAudioFileFormat(type, audioInputStream);
        if (audioInputStream.getFrameLength() == -1L) {
            throw new IOException("stream length not specified");
        }
        return this.writeWaveFile(audioInputStream, waveFileFormat, outputStream);
    }
    
    @Override
    public int write(final AudioInputStream audioInputStream, final AudioFileFormat.Type type, final File file) throws IOException {
        final WaveFileFormat waveFileFormat = (WaveFileFormat)this.getAudioFileFormat(type, audioInputStream);
        final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file), 4096);
        final int writeWaveFile = this.writeWaveFile(audioInputStream, waveFileFormat, bufferedOutputStream);
        bufferedOutputStream.close();
        if (waveFileFormat.getByteLength() == -1) {
            final int n = writeWaveFile - waveFileFormat.getHeaderSize();
            final int n2 = n + waveFileFormat.getHeaderSize() - 8;
            final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.skipBytes(4);
            randomAccessFile.writeInt(this.big2little(n2));
            randomAccessFile.skipBytes(12 + WaveFileFormat.getFmtChunkSize(waveFileFormat.getWaveType()) + 4);
            randomAccessFile.writeInt(this.big2little(n));
            randomAccessFile.close();
        }
        return writeWaveFile;
    }
    
    private AudioFileFormat getAudioFileFormat(final AudioFileFormat.Type type, final AudioInputStream audioInputStream) {
        final AudioFormat.Encoding pcm_SIGNED = AudioFormat.Encoding.PCM_SIGNED;
        final AudioFormat format = audioInputStream.getFormat();
        final AudioFormat.Encoding encoding = format.getEncoding();
        if (!this.types[0].equals(type)) {
            throw new IllegalArgumentException("File type " + type + " not supported.");
        }
        int n = 1;
        AudioFormat.Encoding encoding2;
        int n2;
        if (AudioFormat.Encoding.ALAW.equals(encoding) || AudioFormat.Encoding.ULAW.equals(encoding)) {
            encoding2 = encoding;
            n2 = format.getSampleSizeInBits();
            if (encoding.equals(AudioFormat.Encoding.ALAW)) {
                n = 6;
            }
            else {
                n = 7;
            }
        }
        else if (format.getSampleSizeInBits() == 8) {
            encoding2 = AudioFormat.Encoding.PCM_UNSIGNED;
            n2 = 8;
        }
        else {
            encoding2 = AudioFormat.Encoding.PCM_SIGNED;
            n2 = format.getSampleSizeInBits();
        }
        final AudioFormat audioFormat = new AudioFormat(encoding2, format.getSampleRate(), n2, format.getChannels(), format.getFrameSize(), format.getFrameRate(), false);
        int n3;
        if (audioInputStream.getFrameLength() != -1L) {
            n3 = (int)audioInputStream.getFrameLength() * format.getFrameSize() + WaveFileFormat.getHeaderSize(n);
        }
        else {
            n3 = -1;
        }
        return new WaveFileFormat(AudioFileFormat.Type.WAVE, n3, audioFormat, (int)audioInputStream.getFrameLength());
    }
    
    private int writeWaveFile(final InputStream inputStream, final WaveFileFormat waveFileFormat, final OutputStream outputStream) throws IOException {
        int n = 0;
        final InputStream fileStream = this.getFileStream(waveFileFormat, inputStream);
        final byte[] array = new byte[4096];
        int byteLength = waveFileFormat.getByteLength();
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
    
    private InputStream getFileStream(final WaveFileFormat waveFileFormat, final InputStream inputStream) throws IOException {
        final AudioFormat format = waveFileFormat.getFormat();
        final int headerSize = waveFileFormat.getHeaderSize();
        final int n = 1380533830;
        final int n2 = 1463899717;
        final int n3 = 1718449184;
        final int fmtChunkSize = WaveFileFormat.getFmtChunkSize(waveFileFormat.getWaveType());
        short n4 = (short)waveFileFormat.getWaveType();
        final short n5 = (short)format.getChannels();
        final short n6 = (short)format.getSampleSizeInBits();
        final int n7 = (int)format.getSampleRate();
        final int frameSize = format.getFrameSize();
        final int n8 = (int)format.getFrameRate();
        final int n9 = n5 * n6 * n7 / 8;
        final short n10 = (short)(n6 / 8 * n5);
        final int n11 = 1684108385;
        final int n12 = waveFileFormat.getFrameLength() * frameSize;
        waveFileFormat.getByteLength();
        final int n13 = n12 + headerSize - 8;
        InputStream inputStream2 = inputStream;
        if (inputStream instanceof AudioInputStream) {
            final AudioFormat format2 = ((AudioInputStream)inputStream).getFormat();
            final AudioFormat.Encoding encoding = format2.getEncoding();
            if (AudioFormat.Encoding.PCM_SIGNED.equals(encoding) && n6 == 8) {
                n4 = 1;
                inputStream2 = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, format2.getSampleRate(), format2.getSampleSizeInBits(), format2.getChannels(), format2.getFrameSize(), format2.getFrameRate(), false), (AudioInputStream)inputStream);
            }
            if (((AudioFormat.Encoding.PCM_SIGNED.equals(encoding) && format2.isBigEndian()) || (AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding) && !format2.isBigEndian()) || (AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding) && format2.isBigEndian())) && n6 != 8) {
                n4 = 1;
                inputStream2 = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format2.getSampleRate(), format2.getSampleSizeInBits(), format2.getChannels(), format2.getFrameSize(), format2.getFrameRate(), false), (AudioInputStream)inputStream);
            }
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeInt(n);
        dataOutputStream.writeInt(this.big2little(n13));
        dataOutputStream.writeInt(n2);
        dataOutputStream.writeInt(n3);
        dataOutputStream.writeInt(this.big2little(fmtChunkSize));
        dataOutputStream.writeShort(this.big2littleShort(n4));
        dataOutputStream.writeShort(this.big2littleShort(n5));
        dataOutputStream.writeInt(this.big2little(n7));
        dataOutputStream.writeInt(this.big2little(n9));
        dataOutputStream.writeShort(this.big2littleShort(n10));
        dataOutputStream.writeShort(this.big2littleShort(n6));
        if (n4 != 1) {
            dataOutputStream.writeShort(0);
        }
        dataOutputStream.writeInt(n11);
        dataOutputStream.writeInt(this.big2little(n12));
        dataOutputStream.close();
        return new SequenceInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), new NoCloseInputStream(inputStream2));
    }
}
