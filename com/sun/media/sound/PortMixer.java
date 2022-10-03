package com.sun.media.sound;

import javax.sound.sampled.FloatControl;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.CompoundControl;
import java.util.Vector;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Line;
import javax.sound.sampled.Control;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;

final class PortMixer extends AbstractMixer
{
    private static final int SRC_UNKNOWN = 1;
    private static final int SRC_MICROPHONE = 2;
    private static final int SRC_LINE_IN = 3;
    private static final int SRC_COMPACT_DISC = 4;
    private static final int SRC_MASK = 255;
    private static final int DST_UNKNOWN = 256;
    private static final int DST_SPEAKER = 512;
    private static final int DST_HEADPHONE = 768;
    private static final int DST_LINE_OUT = 1024;
    private static final int DST_MASK = 65280;
    private Port.Info[] portInfos;
    private PortMixerPort[] ports;
    private long id;
    
    PortMixer(final PortMixerProvider.PortMixerInfo portMixerInfo) {
        super(portMixerInfo, null, null, null);
        this.id = 0L;
        int nGetPortCount = 0;
        int n = 0;
        int n2 = 0;
        try {
            try {
                this.id = nOpen(this.getMixerIndex());
                if (this.id != 0L) {
                    nGetPortCount = nGetPortCount(this.id);
                    if (nGetPortCount < 0) {
                        nGetPortCount = 0;
                    }
                }
            }
            catch (final Exception ex) {}
            this.portInfos = new Port.Info[nGetPortCount];
            for (int i = 0; i < nGetPortCount; ++i) {
                final int nGetPortType = nGetPortType(this.id, i);
                n += (((nGetPortType & 0xFF) != 0x0) ? 1 : 0);
                n2 += (((nGetPortType & 0xFF00) != 0x0) ? 1 : 0);
                this.portInfos[i] = this.getPortInfo(i, nGetPortType);
            }
        }
        finally {
            if (this.id != 0L) {
                nClose(this.id);
            }
            this.id = 0L;
        }
        this.sourceLineInfo = new Port.Info[n];
        this.targetLineInfo = new Port.Info[n2];
        int n3 = 0;
        int n4 = 0;
        for (int j = 0; j < nGetPortCount; ++j) {
            if (this.portInfos[j].isSource()) {
                this.sourceLineInfo[n3++] = this.portInfos[j];
            }
            else {
                this.targetLineInfo[n4++] = this.portInfos[j];
            }
        }
    }
    
    @Override
    public Line getLine(final Line.Info info) throws LineUnavailableException {
        final Line.Info lineInfo = this.getLineInfo(info);
        if (lineInfo != null && lineInfo instanceof Port.Info) {
            for (int i = 0; i < this.portInfos.length; ++i) {
                if (lineInfo.equals(this.portInfos[i])) {
                    return this.getPort(i);
                }
            }
        }
        throw new IllegalArgumentException("Line unsupported: " + info);
    }
    
    @Override
    public int getMaxLines(final Line.Info info) {
        final Line.Info lineInfo = this.getLineInfo(info);
        if (lineInfo == null) {
            return 0;
        }
        if (lineInfo instanceof Port.Info) {
            return 1;
        }
        return 0;
    }
    
    @Override
    protected void implOpen() throws LineUnavailableException {
        this.id = nOpen(this.getMixerIndex());
    }
    
    @Override
    protected void implClose() {
        final long id = this.id;
        this.id = 0L;
        nClose(id);
        if (this.ports != null) {
            for (int i = 0; i < this.ports.length; ++i) {
                if (this.ports[i] != null) {
                    this.ports[i].disposeControls();
                }
            }
        }
    }
    
    @Override
    protected void implStart() {
    }
    
    @Override
    protected void implStop() {
    }
    
    private Port.Info getPortInfo(final int n, final int n2) {
        switch (n2) {
            case 1: {
                return new PortInfo(nGetPortName(this.getID(), n), true);
            }
            case 2: {
                return Port.Info.MICROPHONE;
            }
            case 3: {
                return Port.Info.LINE_IN;
            }
            case 4: {
                return Port.Info.COMPACT_DISC;
            }
            case 256: {
                return new PortInfo(nGetPortName(this.getID(), n), false);
            }
            case 512: {
                return Port.Info.SPEAKER;
            }
            case 768: {
                return Port.Info.HEADPHONE;
            }
            case 1024: {
                return Port.Info.LINE_OUT;
            }
            default: {
                return null;
            }
        }
    }
    
    int getMixerIndex() {
        return ((PortMixerProvider.PortMixerInfo)this.getMixerInfo()).getIndex();
    }
    
    Port getPort(final int n) {
        if (this.ports == null) {
            this.ports = new PortMixerPort[this.portInfos.length];
        }
        if (this.ports[n] == null) {
            return this.ports[n] = new PortMixerPort(this.portInfos[n], this, n);
        }
        return this.ports[n];
    }
    
    long getID() {
        return this.id;
    }
    
    private static native long nOpen(final int p0) throws LineUnavailableException;
    
    private static native void nClose(final long p0);
    
    private static native int nGetPortCount(final long p0);
    
    private static native int nGetPortType(final long p0, final int p1);
    
    private static native String nGetPortName(final long p0, final int p1);
    
    private static native void nGetControls(final long p0, final int p1, final Vector p2);
    
    private static native void nControlSetIntValue(final long p0, final int p1);
    
    private static native int nControlGetIntValue(final long p0);
    
    private static native void nControlSetFloatValue(final long p0, final float p1);
    
    private static native float nControlGetFloatValue(final long p0);
    
    private static final class PortMixerPort extends AbstractLine implements Port
    {
        private final int portIndex;
        private long id;
        
        private PortMixerPort(final Port.Info info, final PortMixer portMixer, final int portIndex) {
            super(info, portMixer, null);
            this.portIndex = portIndex;
        }
        
        void implOpen() throws LineUnavailableException {
            final long id = ((PortMixer)this.mixer).getID();
            if (this.id == 0L || id != this.id || this.controls.length == 0) {
                this.id = id;
                final Vector vector = new Vector();
                synchronized (vector) {
                    nGetControls(this.id, this.portIndex, vector);
                    this.controls = new Control[vector.size()];
                    for (int i = 0; i < this.controls.length; ++i) {
                        this.controls[i] = (Control)vector.elementAt(i);
                    }
                }
            }
            else {
                this.enableControls(this.controls, true);
            }
        }
        
        private void enableControls(final Control[] array, final boolean b) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i] instanceof BoolCtrl) {
                    ((BoolCtrl)array[i]).closed = !b;
                }
                else if (array[i] instanceof FloatCtrl) {
                    ((FloatCtrl)array[i]).closed = !b;
                }
                else if (array[i] instanceof CompoundControl) {
                    this.enableControls(((CompoundControl)array[i]).getMemberControls(), b);
                }
            }
        }
        
        private void disposeControls() {
            this.enableControls(this.controls, false);
            this.controls = new Control[0];
        }
        
        void implClose() {
            this.enableControls(this.controls, false);
        }
        
        @Override
        public void open() throws LineUnavailableException {
            synchronized (this.mixer) {
                if (!this.isOpen()) {
                    this.mixer.open(this);
                    try {
                        this.implOpen();
                        this.setOpen(true);
                    }
                    catch (final LineUnavailableException ex) {
                        this.mixer.close(this);
                        throw ex;
                    }
                }
            }
        }
        
        @Override
        public void close() {
            synchronized (this.mixer) {
                if (this.isOpen()) {
                    this.setOpen(false);
                    this.implClose();
                    this.mixer.close(this);
                }
            }
        }
    }
    
    private static final class BoolCtrl extends BooleanControl
    {
        private final long controlID;
        private boolean closed;
        
        private static Type createType(final String s) {
            if (s.equals("Mute")) {
                return Type.MUTE;
            }
            if (s.equals("Select")) {}
            return new BCT(s);
        }
        
        private BoolCtrl(final long n, final String s) {
            this(n, createType(s));
        }
        
        private BoolCtrl(final long controlID, final Type type) {
            super(type, false);
            this.closed = false;
            this.controlID = controlID;
        }
        
        @Override
        public void setValue(final boolean b) {
            if (!this.closed) {
                nControlSetIntValue(this.controlID, b ? 1 : 0);
            }
        }
        
        @Override
        public boolean getValue() {
            return !this.closed && nControlGetIntValue(this.controlID) != 0;
        }
        
        private static final class BCT extends Type
        {
            private BCT(final String s) {
                super(s);
            }
        }
    }
    
    private static final class CompCtrl extends CompoundControl
    {
        private CompCtrl(final String s, final Control[] array) {
            super(new CCT(s), array);
        }
        
        private static final class CCT extends Type
        {
            private CCT(final String s) {
                super(s);
            }
        }
    }
    
    private static final class FloatCtrl extends FloatControl
    {
        private final long controlID;
        private boolean closed;
        private static final Type[] FLOAT_CONTROL_TYPES;
        
        private FloatCtrl(final long n, final String s, final float n2, final float n3, final float n4, final String s2) {
            this(n, new FCT(s), n2, n3, n4, s2);
        }
        
        private FloatCtrl(final long n, final int n2, final float n3, final float n4, final float n5, final String s) {
            this(n, FloatCtrl.FLOAT_CONTROL_TYPES[n2], n3, n4, n5, s);
        }
        
        private FloatCtrl(final long controlID, final Type type, final float n, final float n2, final float n3, final String s) {
            super(type, n, n2, n3, 1000, n, s);
            this.closed = false;
            this.controlID = controlID;
        }
        
        @Override
        public void setValue(final float n) {
            if (!this.closed) {
                nControlSetFloatValue(this.controlID, n);
            }
        }
        
        @Override
        public float getValue() {
            if (!this.closed) {
                return nControlGetFloatValue(this.controlID);
            }
            return this.getMinimum();
        }
        
        static {
            FLOAT_CONTROL_TYPES = new Type[] { null, Type.BALANCE, Type.MASTER_GAIN, Type.PAN, Type.VOLUME };
        }
        
        private static final class FCT extends Type
        {
            private FCT(final String s) {
                super(s);
            }
        }
    }
    
    private static final class PortInfo extends Port.Info
    {
        private PortInfo(final String s, final boolean b) {
            super(Port.class, s, b);
        }
    }
}
