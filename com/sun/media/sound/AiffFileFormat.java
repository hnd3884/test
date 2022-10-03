package com.sun.media.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat;

final class AiffFileFormat extends AudioFileFormat
{
    static final int AIFF_MAGIC = 1179603533;
    static final int AIFC_MAGIC = 1095321155;
    static final int AIFF_MAGIC2 = 1095321158;
    static final int FVER_MAGIC = 1180058962;
    static final int FVER_TIMESTAMP = -1568648896;
    static final int COMM_MAGIC = 1129270605;
    static final int SSND_MAGIC = 1397968452;
    static final int AIFC_PCM = 1313820229;
    static final int AIFC_ACE2 = 1094927666;
    static final int AIFC_ACE8 = 1094927672;
    static final int AIFC_MAC3 = 1296122675;
    static final int AIFC_MAC6 = 1296122678;
    static final int AIFC_ULAW = 1970037111;
    static final int AIFC_IMA4 = 1768775988;
    static final int AIFF_HEADERSIZE = 54;
    private final int headerSize = 54;
    private final int commChunkSize = 26;
    private final int fverChunkSize = 0;
    
    AiffFileFormat(final AudioFileFormat audioFileFormat) {
        this(audioFileFormat.getType(), audioFileFormat.getByteLength(), audioFileFormat.getFormat(), audioFileFormat.getFrameLength());
    }
    
    AiffFileFormat(final Type type, final int n, final AudioFormat audioFormat, final int n2) {
        super(type, n, audioFormat, n2);
    }
    
    int getHeaderSize() {
        return 54;
    }
    
    int getCommChunkSize() {
        return 26;
    }
    
    int getFverChunkSize() {
        return 0;
    }
    
    int getSsndChunkOffset() {
        return this.getHeaderSize() - 16;
    }
}
