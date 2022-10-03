package com.sun.media.sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SF2Region
{
    public static final int GENERATOR_STARTADDRSOFFSET = 0;
    public static final int GENERATOR_ENDADDRSOFFSET = 1;
    public static final int GENERATOR_STARTLOOPADDRSOFFSET = 2;
    public static final int GENERATOR_ENDLOOPADDRSOFFSET = 3;
    public static final int GENERATOR_STARTADDRSCOARSEOFFSET = 4;
    public static final int GENERATOR_MODLFOTOPITCH = 5;
    public static final int GENERATOR_VIBLFOTOPITCH = 6;
    public static final int GENERATOR_MODENVTOPITCH = 7;
    public static final int GENERATOR_INITIALFILTERFC = 8;
    public static final int GENERATOR_INITIALFILTERQ = 9;
    public static final int GENERATOR_MODLFOTOFILTERFC = 10;
    public static final int GENERATOR_MODENVTOFILTERFC = 11;
    public static final int GENERATOR_ENDADDRSCOARSEOFFSET = 12;
    public static final int GENERATOR_MODLFOTOVOLUME = 13;
    public static final int GENERATOR_UNUSED1 = 14;
    public static final int GENERATOR_CHORUSEFFECTSSEND = 15;
    public static final int GENERATOR_REVERBEFFECTSSEND = 16;
    public static final int GENERATOR_PAN = 17;
    public static final int GENERATOR_UNUSED2 = 18;
    public static final int GENERATOR_UNUSED3 = 19;
    public static final int GENERATOR_UNUSED4 = 20;
    public static final int GENERATOR_DELAYMODLFO = 21;
    public static final int GENERATOR_FREQMODLFO = 22;
    public static final int GENERATOR_DELAYVIBLFO = 23;
    public static final int GENERATOR_FREQVIBLFO = 24;
    public static final int GENERATOR_DELAYMODENV = 25;
    public static final int GENERATOR_ATTACKMODENV = 26;
    public static final int GENERATOR_HOLDMODENV = 27;
    public static final int GENERATOR_DECAYMODENV = 28;
    public static final int GENERATOR_SUSTAINMODENV = 29;
    public static final int GENERATOR_RELEASEMODENV = 30;
    public static final int GENERATOR_KEYNUMTOMODENVHOLD = 31;
    public static final int GENERATOR_KEYNUMTOMODENVDECAY = 32;
    public static final int GENERATOR_DELAYVOLENV = 33;
    public static final int GENERATOR_ATTACKVOLENV = 34;
    public static final int GENERATOR_HOLDVOLENV = 35;
    public static final int GENERATOR_DECAYVOLENV = 36;
    public static final int GENERATOR_SUSTAINVOLENV = 37;
    public static final int GENERATOR_RELEASEVOLENV = 38;
    public static final int GENERATOR_KEYNUMTOVOLENVHOLD = 39;
    public static final int GENERATOR_KEYNUMTOVOLENVDECAY = 40;
    public static final int GENERATOR_INSTRUMENT = 41;
    public static final int GENERATOR_RESERVED1 = 42;
    public static final int GENERATOR_KEYRANGE = 43;
    public static final int GENERATOR_VELRANGE = 44;
    public static final int GENERATOR_STARTLOOPADDRSCOARSEOFFSET = 45;
    public static final int GENERATOR_KEYNUM = 46;
    public static final int GENERATOR_VELOCITY = 47;
    public static final int GENERATOR_INITIALATTENUATION = 48;
    public static final int GENERATOR_RESERVED2 = 49;
    public static final int GENERATOR_ENDLOOPADDRSCOARSEOFFSET = 50;
    public static final int GENERATOR_COARSETUNE = 51;
    public static final int GENERATOR_FINETUNE = 52;
    public static final int GENERATOR_SAMPLEID = 53;
    public static final int GENERATOR_SAMPLEMODES = 54;
    public static final int GENERATOR_RESERVED3 = 55;
    public static final int GENERATOR_SCALETUNING = 56;
    public static final int GENERATOR_EXCLUSIVECLASS = 57;
    public static final int GENERATOR_OVERRIDINGROOTKEY = 58;
    public static final int GENERATOR_UNUSED5 = 59;
    public static final int GENERATOR_ENDOPR = 60;
    protected Map<Integer, Short> generators;
    protected List<SF2Modulator> modulators;
    
    public SF2Region() {
        this.generators = new HashMap<Integer, Short>();
        this.modulators = new ArrayList<SF2Modulator>();
    }
    
    public Map<Integer, Short> getGenerators() {
        return this.generators;
    }
    
    public boolean contains(final int n) {
        return this.generators.containsKey(n);
    }
    
    public static short getDefaultValue(final int n) {
        if (n == 8) {
            return 13500;
        }
        if (n == 21) {
            return -12000;
        }
        if (n == 23) {
            return -12000;
        }
        if (n == 25) {
            return -12000;
        }
        if (n == 26) {
            return -12000;
        }
        if (n == 27) {
            return -12000;
        }
        if (n == 28) {
            return -12000;
        }
        if (n == 30) {
            return -12000;
        }
        if (n == 33) {
            return -12000;
        }
        if (n == 34) {
            return -12000;
        }
        if (n == 35) {
            return -12000;
        }
        if (n == 36) {
            return -12000;
        }
        if (n == 38) {
            return -12000;
        }
        if (n == 43) {
            return 32512;
        }
        if (n == 44) {
            return 32512;
        }
        if (n == 46) {
            return -1;
        }
        if (n == 47) {
            return -1;
        }
        if (n == 56) {
            return 100;
        }
        if (n == 58) {
            return -1;
        }
        return 0;
    }
    
    public short getShort(final int n) {
        if (!this.contains(n)) {
            return getDefaultValue(n);
        }
        return this.generators.get(n);
    }
    
    public void putShort(final int n, final short n2) {
        this.generators.put(n, n2);
    }
    
    public byte[] getBytes(final int n) {
        final int integer = this.getInteger(n);
        return new byte[] { (byte)(0xFF & integer), (byte)((0xFF00 & integer) >> 8) };
    }
    
    public void putBytes(final int n, final byte[] array) {
        this.generators.put(n, (short)(array[0] + (array[1] << 8)));
    }
    
    public int getInteger(final int n) {
        return 0xFFFF & this.getShort(n);
    }
    
    public void putInteger(final int n, final int n2) {
        this.generators.put(n, (short)n2);
    }
    
    public List<SF2Modulator> getModulators() {
        return this.modulators;
    }
}
