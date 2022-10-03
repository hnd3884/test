package com.sun.media.sound;

public final class DLSModulator
{
    public static final int CONN_DST_NONE = 0;
    public static final int CONN_DST_GAIN = 1;
    public static final int CONN_DST_PITCH = 3;
    public static final int CONN_DST_PAN = 4;
    public static final int CONN_DST_LFO_FREQUENCY = 260;
    public static final int CONN_DST_LFO_STARTDELAY = 261;
    public static final int CONN_DST_EG1_ATTACKTIME = 518;
    public static final int CONN_DST_EG1_DECAYTIME = 519;
    public static final int CONN_DST_EG1_RELEASETIME = 521;
    public static final int CONN_DST_EG1_SUSTAINLEVEL = 522;
    public static final int CONN_DST_EG2_ATTACKTIME = 778;
    public static final int CONN_DST_EG2_DECAYTIME = 779;
    public static final int CONN_DST_EG2_RELEASETIME = 781;
    public static final int CONN_DST_EG2_SUSTAINLEVEL = 782;
    public static final int CONN_DST_KEYNUMBER = 5;
    public static final int CONN_DST_LEFT = 16;
    public static final int CONN_DST_RIGHT = 17;
    public static final int CONN_DST_CENTER = 18;
    public static final int CONN_DST_LEFTREAR = 19;
    public static final int CONN_DST_RIGHTREAR = 20;
    public static final int CONN_DST_LFE_CHANNEL = 21;
    public static final int CONN_DST_CHORUS = 128;
    public static final int CONN_DST_REVERB = 129;
    public static final int CONN_DST_VIB_FREQUENCY = 276;
    public static final int CONN_DST_VIB_STARTDELAY = 277;
    public static final int CONN_DST_EG1_DELAYTIME = 523;
    public static final int CONN_DST_EG1_HOLDTIME = 524;
    public static final int CONN_DST_EG1_SHUTDOWNTIME = 525;
    public static final int CONN_DST_EG2_DELAYTIME = 783;
    public static final int CONN_DST_EG2_HOLDTIME = 784;
    public static final int CONN_DST_FILTER_CUTOFF = 1280;
    public static final int CONN_DST_FILTER_Q = 1281;
    public static final int CONN_SRC_NONE = 0;
    public static final int CONN_SRC_LFO = 1;
    public static final int CONN_SRC_KEYONVELOCITY = 2;
    public static final int CONN_SRC_KEYNUMBER = 3;
    public static final int CONN_SRC_EG1 = 4;
    public static final int CONN_SRC_EG2 = 5;
    public static final int CONN_SRC_PITCHWHEEL = 6;
    public static final int CONN_SRC_CC1 = 129;
    public static final int CONN_SRC_CC7 = 135;
    public static final int CONN_SRC_CC10 = 138;
    public static final int CONN_SRC_CC11 = 139;
    public static final int CONN_SRC_RPN0 = 256;
    public static final int CONN_SRC_RPN1 = 257;
    public static final int CONN_SRC_RPN2 = 258;
    public static final int CONN_SRC_POLYPRESSURE = 7;
    public static final int CONN_SRC_CHANNELPRESSURE = 8;
    public static final int CONN_SRC_VIBRATO = 9;
    public static final int CONN_SRC_MONOPRESSURE = 10;
    public static final int CONN_SRC_CC91 = 219;
    public static final int CONN_SRC_CC93 = 221;
    public static final int CONN_TRN_NONE = 0;
    public static final int CONN_TRN_CONCAVE = 1;
    public static final int CONN_TRN_CONVEX = 2;
    public static final int CONN_TRN_SWITCH = 3;
    public static final int DST_FORMAT_CB = 1;
    public static final int DST_FORMAT_CENT = 1;
    public static final int DST_FORMAT_TIMECENT = 2;
    public static final int DST_FORMAT_PERCENT = 3;
    int source;
    int control;
    int destination;
    int transform;
    int scale;
    int version;
    
    public DLSModulator() {
        this.version = 1;
    }
    
    public int getControl() {
        return this.control;
    }
    
    public void setControl(final int control) {
        this.control = control;
    }
    
    public static int getDestinationFormat(final int n) {
        if (n == 1) {
            return 1;
        }
        if (n == 3) {
            return 1;
        }
        if (n == 4) {
            return 3;
        }
        if (n == 260) {
            return 1;
        }
        if (n == 261) {
            return 2;
        }
        if (n == 518) {
            return 2;
        }
        if (n == 519) {
            return 2;
        }
        if (n == 521) {
            return 2;
        }
        if (n == 522) {
            return 3;
        }
        if (n == 778) {
            return 2;
        }
        if (n == 779) {
            return 2;
        }
        if (n == 781) {
            return 2;
        }
        if (n == 782) {
            return 3;
        }
        if (n == 5) {
            return 1;
        }
        if (n == 16) {
            return 1;
        }
        if (n == 17) {
            return 1;
        }
        if (n == 18) {
            return 1;
        }
        if (n == 19) {
            return 1;
        }
        if (n == 20) {
            return 1;
        }
        if (n == 21) {
            return 1;
        }
        if (n == 128) {
            return 3;
        }
        if (n == 129) {
            return 3;
        }
        if (n == 276) {
            return 1;
        }
        if (n == 277) {
            return 2;
        }
        if (n == 523) {
            return 2;
        }
        if (n == 524) {
            return 2;
        }
        if (n == 525) {
            return 2;
        }
        if (n == 783) {
            return 2;
        }
        if (n == 784) {
            return 2;
        }
        if (n == 1280) {
            return 1;
        }
        if (n == 1281) {
            return 1;
        }
        return -1;
    }
    
    public static String getDestinationName(final int n) {
        if (n == 1) {
            return "gain";
        }
        if (n == 3) {
            return "pitch";
        }
        if (n == 4) {
            return "pan";
        }
        if (n == 260) {
            return "lfo1.freq";
        }
        if (n == 261) {
            return "lfo1.delay";
        }
        if (n == 518) {
            return "eg1.attack";
        }
        if (n == 519) {
            return "eg1.decay";
        }
        if (n == 521) {
            return "eg1.release";
        }
        if (n == 522) {
            return "eg1.sustain";
        }
        if (n == 778) {
            return "eg2.attack";
        }
        if (n == 779) {
            return "eg2.decay";
        }
        if (n == 781) {
            return "eg2.release";
        }
        if (n == 782) {
            return "eg2.sustain";
        }
        if (n == 5) {
            return "keynumber";
        }
        if (n == 16) {
            return "left";
        }
        if (n == 17) {
            return "right";
        }
        if (n == 18) {
            return "center";
        }
        if (n == 19) {
            return "leftrear";
        }
        if (n == 20) {
            return "rightrear";
        }
        if (n == 21) {
            return "lfe_channel";
        }
        if (n == 128) {
            return "chorus";
        }
        if (n == 129) {
            return "reverb";
        }
        if (n == 276) {
            return "vib.freq";
        }
        if (n == 277) {
            return "vib.delay";
        }
        if (n == 523) {
            return "eg1.delay";
        }
        if (n == 524) {
            return "eg1.hold";
        }
        if (n == 525) {
            return "eg1.shutdown";
        }
        if (n == 783) {
            return "eg2.delay";
        }
        if (n == 784) {
            return "eg.2hold";
        }
        if (n == 1280) {
            return "filter.cutoff";
        }
        if (n == 1281) {
            return "filter.q";
        }
        return null;
    }
    
    public static String getSourceName(final int n) {
        if (n == 0) {
            return "none";
        }
        if (n == 1) {
            return "lfo";
        }
        if (n == 2) {
            return "keyonvelocity";
        }
        if (n == 3) {
            return "keynumber";
        }
        if (n == 4) {
            return "eg1";
        }
        if (n == 5) {
            return "eg2";
        }
        if (n == 6) {
            return "pitchweel";
        }
        if (n == 129) {
            return "cc1";
        }
        if (n == 135) {
            return "cc7";
        }
        if (n == 138) {
            return "c10";
        }
        if (n == 139) {
            return "cc11";
        }
        if (n == 7) {
            return "polypressure";
        }
        if (n == 8) {
            return "channelpressure";
        }
        if (n == 9) {
            return "vibrato";
        }
        if (n == 10) {
            return "monopressure";
        }
        if (n == 219) {
            return "cc91";
        }
        if (n == 221) {
            return "cc93";
        }
        return null;
    }
    
    public int getDestination() {
        return this.destination;
    }
    
    public void setDestination(final int destination) {
        this.destination = destination;
    }
    
    public int getScale() {
        return this.scale;
    }
    
    public void setScale(final int scale) {
        this.scale = scale;
    }
    
    public int getSource() {
        return this.source;
    }
    
    public void setSource(final int source) {
        this.source = source;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public void setVersion(final int version) {
        this.version = version;
    }
    
    public int getTransform() {
        return this.transform;
    }
    
    public void setTransform(final int transform) {
        this.transform = transform;
    }
}
