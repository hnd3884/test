package javax.sound.sampled;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

public class AudioFileFormat
{
    private Type type;
    private int byteLength;
    private AudioFormat format;
    private int frameLength;
    private HashMap<String, Object> properties;
    
    protected AudioFileFormat(final Type type, final int byteLength, final AudioFormat format, final int frameLength) {
        this.type = type;
        this.byteLength = byteLength;
        this.format = format;
        this.frameLength = frameLength;
        this.properties = null;
    }
    
    public AudioFileFormat(final Type type, final AudioFormat audioFormat, final int n) {
        this(type, -1, audioFormat, n);
    }
    
    public AudioFileFormat(final Type type, final AudioFormat audioFormat, final int n, final Map<String, Object> map) {
        this(type, -1, audioFormat, n);
        this.properties = new HashMap<String, Object>(map);
    }
    
    public Type getType() {
        return this.type;
    }
    
    public int getByteLength() {
        return this.byteLength;
    }
    
    public AudioFormat getFormat() {
        return this.format;
    }
    
    public int getFrameLength() {
        return this.frameLength;
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
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.type != null) {
            sb.append(this.type.toString() + " (." + this.type.getExtension() + ") file");
        }
        else {
            sb.append("unknown file format");
        }
        if (this.byteLength != -1) {
            sb.append(", byte length: " + this.byteLength);
        }
        sb.append(", data format: " + this.format);
        if (this.frameLength != -1) {
            sb.append(", frame length: " + this.frameLength);
        }
        return new String(sb);
    }
    
    public static class Type
    {
        public static final Type WAVE;
        public static final Type AU;
        public static final Type AIFF;
        public static final Type AIFC;
        public static final Type SND;
        private final String name;
        private final String extension;
        
        public Type(final String name, final String extension) {
            this.name = name;
            this.extension = extension;
        }
        
        @Override
        public final boolean equals(final Object o) {
            if (this.toString() == null) {
                return o != null && o.toString() == null;
            }
            return o instanceof Type && this.toString().equals(o.toString());
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
        
        public String getExtension() {
            return this.extension;
        }
        
        static {
            WAVE = new Type("WAVE", "wav");
            AU = new Type("AU", "au");
            AIFF = new Type("AIFF", "aif");
            AIFC = new Type("AIFF-C", "aifc");
            SND = new Type("SND", "snd");
        }
    }
}
