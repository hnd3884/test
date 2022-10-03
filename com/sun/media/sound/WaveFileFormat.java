package com.sun.media.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat;

final class WaveFileFormat extends AudioFileFormat
{
    private final int waveType;
    private static final int STANDARD_HEADER_SIZE = 28;
    private static final int STANDARD_FMT_CHUNK_SIZE = 16;
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
    
    WaveFileFormat(final AudioFileFormat audioFileFormat) {
        this(audioFileFormat.getType(), audioFileFormat.getByteLength(), audioFileFormat.getFormat(), audioFileFormat.getFrameLength());
    }
    
    WaveFileFormat(final Type type, final int n, final AudioFormat audioFormat, final int n2) {
        super(type, n, audioFormat, n2);
        final AudioFormat.Encoding encoding = audioFormat.getEncoding();
        if (encoding.equals(AudioFormat.Encoding.ALAW)) {
            this.waveType = 6;
        }
        else if (encoding.equals(AudioFormat.Encoding.ULAW)) {
            this.waveType = 7;
        }
        else if (encoding.equals(AudioFormat.Encoding.PCM_SIGNED) || encoding.equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
            this.waveType = 1;
        }
        else {
            this.waveType = 0;
        }
    }
    
    int getWaveType() {
        return this.waveType;
    }
    
    int getHeaderSize() {
        return getHeaderSize(this.getWaveType());
    }
    
    static int getHeaderSize(final int n) {
        return 28 + getFmtChunkSize(n);
    }
    
    static int getFmtChunkSize(final int n) {
        int n2 = 16;
        if (n != 1) {
            n2 += 2;
        }
        return n2;
    }
}
