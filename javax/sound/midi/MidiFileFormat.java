package javax.sound.midi;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

public class MidiFileFormat
{
    public static final int UNKNOWN_LENGTH = -1;
    protected int type;
    protected float divisionType;
    protected int resolution;
    protected int byteLength;
    protected long microsecondLength;
    private HashMap<String, Object> properties;
    
    public MidiFileFormat(final int type, final float divisionType, final int resolution, final int byteLength, final long microsecondLength) {
        this.type = type;
        this.divisionType = divisionType;
        this.resolution = resolution;
        this.byteLength = byteLength;
        this.microsecondLength = microsecondLength;
        this.properties = null;
    }
    
    public MidiFileFormat(final int n, final float n2, final int n3, final int n4, final long n5, final Map<String, Object> map) {
        this(n, n2, n3, n4, n5);
        this.properties = new HashMap<String, Object>(map);
    }
    
    public int getType() {
        return this.type;
    }
    
    public float getDivisionType() {
        return this.divisionType;
    }
    
    public int getResolution() {
        return this.resolution;
    }
    
    public int getByteLength() {
        return this.byteLength;
    }
    
    public long getMicrosecondLength() {
        return this.microsecondLength;
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
}
