package javax.sound.sampled;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

public class AudioFormat
{
    protected Encoding encoding;
    protected float sampleRate;
    protected int sampleSizeInBits;
    protected int channels;
    protected int frameSize;
    protected float frameRate;
    protected boolean bigEndian;
    private HashMap<String, Object> properties;
    
    public AudioFormat(final Encoding encoding, final float sampleRate, final int sampleSizeInBits, final int channels, final int frameSize, final float frameRate, final boolean bigEndian) {
        this.encoding = encoding;
        this.sampleRate = sampleRate;
        this.sampleSizeInBits = sampleSizeInBits;
        this.channels = channels;
        this.frameSize = frameSize;
        this.frameRate = frameRate;
        this.bigEndian = bigEndian;
        this.properties = null;
    }
    
    public AudioFormat(final Encoding encoding, final float n, final int n2, final int n3, final int n4, final float n5, final boolean b, final Map<String, Object> map) {
        this(encoding, n, n2, n3, n4, n5, b);
        this.properties = new HashMap<String, Object>(map);
    }
    
    public AudioFormat(final float n, final int n2, final int n3, final boolean b, final boolean b2) {
        this(b ? Encoding.PCM_SIGNED : Encoding.PCM_UNSIGNED, n, n2, n3, (n3 == -1 || n2 == -1) ? -1 : ((n2 + 7) / 8 * n3), n, b2);
    }
    
    public Encoding getEncoding() {
        return this.encoding;
    }
    
    public float getSampleRate() {
        return this.sampleRate;
    }
    
    public int getSampleSizeInBits() {
        return this.sampleSizeInBits;
    }
    
    public int getChannels() {
        return this.channels;
    }
    
    public int getFrameSize() {
        return this.frameSize;
    }
    
    public float getFrameRate() {
        return this.frameRate;
    }
    
    public boolean isBigEndian() {
        return this.bigEndian;
    }
    
    public Map<String, Object> properties() {
        Map map;
        if (this.properties == null) {
            map = new HashMap(0);
        }
        else {
            map = (Map)this.properties.clone();
        }
        return (Map<String, Object>)Collections.unmodifiableMap((Map<?, ?>)map);
    }
    
    public Object getProperty(final String s) {
        if (this.properties == null) {
            return null;
        }
        return this.properties.get(s);
    }
    
    public boolean matches(final AudioFormat audioFormat) {
        return audioFormat.getEncoding().equals(this.getEncoding()) && (audioFormat.getChannels() == -1 || audioFormat.getChannels() == this.getChannels()) && (audioFormat.getSampleRate() == -1.0f || audioFormat.getSampleRate() == this.getSampleRate()) && (audioFormat.getSampleSizeInBits() == -1 || audioFormat.getSampleSizeInBits() == this.getSampleSizeInBits()) && (audioFormat.getFrameRate() == -1.0f || audioFormat.getFrameRate() == this.getFrameRate()) && (audioFormat.getFrameSize() == -1 || audioFormat.getFrameSize() == this.getFrameSize()) && (this.getSampleSizeInBits() <= 8 || audioFormat.isBigEndian() == this.isBigEndian());
    }
    
    @Override
    public String toString() {
        String string = "";
        if (this.getEncoding() != null) {
            string = this.getEncoding().toString() + " ";
        }
        String string2;
        if (this.getSampleRate() == -1.0f) {
            string2 = "unknown sample rate, ";
        }
        else {
            string2 = "" + this.getSampleRate() + " Hz, ";
        }
        String string3;
        if (this.getSampleSizeInBits() == -1.0f) {
            string3 = "unknown bits per sample, ";
        }
        else {
            string3 = "" + this.getSampleSizeInBits() + " bit, ";
        }
        String string4;
        if (this.getChannels() == 1) {
            string4 = "mono, ";
        }
        else if (this.getChannels() == 2) {
            string4 = "stereo, ";
        }
        else if (this.getChannels() == -1) {
            string4 = " unknown number of channels, ";
        }
        else {
            string4 = "" + this.getChannels() + " channels, ";
        }
        String string5;
        if (this.getFrameSize() == -1.0f) {
            string5 = "unknown frame size, ";
        }
        else {
            string5 = "" + this.getFrameSize() + " bytes/frame, ";
        }
        String string6 = "";
        if (Math.abs(this.getSampleRate() - this.getFrameRate()) > 1.0E-5) {
            if (this.getFrameRate() == -1.0f) {
                string6 = "unknown frame rate, ";
            }
            else {
                string6 = this.getFrameRate() + " frames/second, ";
            }
        }
        String s = "";
        if ((this.getEncoding().equals(Encoding.PCM_SIGNED) || this.getEncoding().equals(Encoding.PCM_UNSIGNED)) && (this.getSampleSizeInBits() > 8 || this.getSampleSizeInBits() == -1)) {
            if (this.isBigEndian()) {
                s = "big-endian";
            }
            else {
                s = "little-endian";
            }
        }
        return string + string2 + string3 + string4 + string5 + string6 + s;
    }
    
    public static class Encoding
    {
        public static final Encoding PCM_SIGNED;
        public static final Encoding PCM_UNSIGNED;
        public static final Encoding PCM_FLOAT;
        public static final Encoding ULAW;
        public static final Encoding ALAW;
        private String name;
        
        public Encoding(final String name) {
            this.name = name;
        }
        
        @Override
        public final boolean equals(final Object o) {
            if (this.toString() == null) {
                return o != null && o.toString() == null;
            }
            return o instanceof Encoding && this.toString().equals(o.toString());
        }
        
        @Override
        public final int hashCode() {
            if (this.toString() == null) {
                return 0;
            }
            return this.toString().hashCode();
        }
        
        @Override
        public final String toString() {
            return this.name;
        }
        
        static {
            PCM_SIGNED = new Encoding("PCM_SIGNED");
            PCM_UNSIGNED = new Encoding("PCM_UNSIGNED");
            PCM_FLOAT = new Encoding("PCM_FLOAT");
            ULAW = new Encoding("ULAW");
            ALAW = new Encoding("ALAW");
        }
    }
}
