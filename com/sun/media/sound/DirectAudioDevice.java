package com.sun.media.sound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat;
import java.util.Vector;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.Control;
import javax.sound.sampled.Mixer;

final class DirectAudioDevice extends AbstractMixer
{
    private static final int CLIP_BUFFER_TIME = 1000;
    private static final int DEFAULT_LINE_BUFFER_TIME = 500;
    private int deviceCountOpened;
    private int deviceCountStarted;
    
    DirectAudioDevice(final DirectAudioDeviceProvider.DirectAudioDeviceInfo directAudioDeviceInfo) {
        super(directAudioDeviceInfo, null, null, null);
        this.deviceCountOpened = 0;
        this.deviceCountStarted = 0;
        final DirectDLI dataLineInfo = this.createDataLineInfo(true);
        if (dataLineInfo != null) {
            (this.sourceLineInfo = new Line.Info[2])[0] = dataLineInfo;
            this.sourceLineInfo[1] = new DirectDLI((Class)Clip.class, dataLineInfo.getFormats(), dataLineInfo.getHardwareFormats(), 32, -1);
        }
        else {
            this.sourceLineInfo = new Line.Info[0];
        }
        final DirectDLI dataLineInfo2 = this.createDataLineInfo(false);
        if (dataLineInfo2 != null) {
            (this.targetLineInfo = new Line.Info[1])[0] = dataLineInfo2;
        }
        else {
            this.targetLineInfo = new Line.Info[0];
        }
    }
    
    private DirectDLI createDataLineInfo(final boolean b) {
        final Vector vector = new Vector();
        AudioFormat[] array = null;
        AudioFormat[] array2 = null;
        synchronized (vector) {
            nGetFormats(this.getMixerIndex(), this.getDeviceID(), b, vector);
            if (vector.size() > 0) {
                int size;
                final int n = size = vector.size();
                array = new AudioFormat[n];
                for (int i = 0; i < n; ++i) {
                    final AudioFormat audioFormat = vector.elementAt(i);
                    (array[i] = audioFormat).getSampleSizeInBits();
                    final boolean equals = audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
                    final boolean equals2 = audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED);
                    if (equals || equals2) {
                        ++size;
                    }
                }
                array2 = new AudioFormat[size];
                int n2 = 0;
                for (final AudioFormat audioFormat2 : array) {
                    array2[n2++] = audioFormat2;
                    final int sampleSizeInBits = audioFormat2.getSampleSizeInBits();
                    final boolean equals3 = audioFormat2.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
                    final boolean equals4 = audioFormat2.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED);
                    if (sampleSizeInBits == 8) {
                        if (equals3) {
                            array2[n2++] = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, audioFormat2.getSampleRate(), sampleSizeInBits, audioFormat2.getChannels(), audioFormat2.getFrameSize(), audioFormat2.getSampleRate(), audioFormat2.isBigEndian());
                        }
                        else if (equals4) {
                            array2[n2++] = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat2.getSampleRate(), sampleSizeInBits, audioFormat2.getChannels(), audioFormat2.getFrameSize(), audioFormat2.getSampleRate(), audioFormat2.isBigEndian());
                        }
                    }
                    else if (sampleSizeInBits > 8 && (equals3 || equals4)) {
                        array2[n2++] = new AudioFormat(audioFormat2.getEncoding(), audioFormat2.getSampleRate(), sampleSizeInBits, audioFormat2.getChannels(), audioFormat2.getFrameSize(), audioFormat2.getSampleRate(), !audioFormat2.isBigEndian());
                    }
                }
            }
        }
        if (array2 != null) {
            return new DirectDLI((Class)(b ? SourceDataLine.class : TargetDataLine.class), array2, array, 32, -1);
        }
        return null;
    }
    
    @Override
    public Line getLine(final Line.Info info) throws LineUnavailableException {
        final Line.Info lineInfo = this.getLineInfo(info);
        if (lineInfo == null) {
            throw new IllegalArgumentException("Line unsupported: " + info);
        }
        if (lineInfo instanceof DataLine.Info) {
            final DataLine.Info info2 = (DataLine.Info)lineInfo;
            int maxBufferSize = -1;
            AudioFormat[] formats = null;
            if (info instanceof DataLine.Info) {
                formats = ((DataLine.Info)info).getFormats();
                maxBufferSize = ((DataLine.Info)info).getMaxBufferSize();
            }
            AudioFormat audioFormat;
            if (formats == null || formats.length == 0) {
                audioFormat = null;
            }
            else {
                audioFormat = formats[formats.length - 1];
                if (!Toolkit.isFullySpecifiedPCMFormat(audioFormat)) {
                    audioFormat = null;
                }
            }
            if (info2.getLineClass().isAssignableFrom(DirectSDL.class)) {
                return new DirectSDL(info2, audioFormat, maxBufferSize, this);
            }
            if (info2.getLineClass().isAssignableFrom(DirectClip.class)) {
                return new DirectClip(info2, audioFormat, maxBufferSize, this);
            }
            if (info2.getLineClass().isAssignableFrom(DirectTDL.class)) {
                return new DirectTDL(info2, audioFormat, maxBufferSize, this);
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
        if (lineInfo instanceof DataLine.Info) {
            return this.getMaxSimulLines();
        }
        return 0;
    }
    
    @Override
    protected void implOpen() throws LineUnavailableException {
    }
    
    @Override
    protected void implClose() {
    }
    
    @Override
    protected void implStart() {
    }
    
    @Override
    protected void implStop() {
    }
    
    int getMixerIndex() {
        return ((DirectAudioDeviceProvider.DirectAudioDeviceInfo)this.getMixerInfo()).getIndex();
    }
    
    int getDeviceID() {
        return ((DirectAudioDeviceProvider.DirectAudioDeviceInfo)this.getMixerInfo()).getDeviceID();
    }
    
    int getMaxSimulLines() {
        return ((DirectAudioDeviceProvider.DirectAudioDeviceInfo)this.getMixerInfo()).getMaxSimulLines();
    }
    
    private static void addFormat(final Vector vector, int n, int n2, final int n3, final float n4, final int n5, final boolean b, final boolean b2) {
        AudioFormat.Encoding encoding = null;
        switch (n5) {
            case 0: {
                encoding = (b ? AudioFormat.Encoding.PCM_SIGNED : AudioFormat.Encoding.PCM_UNSIGNED);
                break;
            }
            case 1: {
                encoding = AudioFormat.Encoding.ULAW;
                if (n != 8) {
                    n = 8;
                    n2 = n3;
                    break;
                }
                break;
            }
            case 2: {
                encoding = AudioFormat.Encoding.ALAW;
                if (n != 8) {
                    n = 8;
                    n2 = n3;
                    break;
                }
                break;
            }
        }
        if (encoding == null) {
            return;
        }
        if (n2 <= 0) {
            if (n3 > 0) {
                n2 = (n + 7) / 8 * n3;
            }
            else {
                n2 = -1;
            }
        }
        vector.add(new AudioFormat(encoding, n4, n, n3, n2, n4, b2));
    }
    
    protected static AudioFormat getSignOrEndianChangedFormat(final AudioFormat audioFormat) {
        final boolean equals = audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
        final boolean equals2 = audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED);
        if (audioFormat.getSampleSizeInBits() > 8 && equals) {
            return new AudioFormat(audioFormat.getEncoding(), audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getFrameRate(), !audioFormat.isBigEndian());
        }
        if (audioFormat.getSampleSizeInBits() == 8 && (equals || equals2)) {
            return new AudioFormat(equals ? AudioFormat.Encoding.PCM_UNSIGNED : AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getFrameRate(), audioFormat.isBigEndian());
        }
        return null;
    }
    
    private static native void nGetFormats(final int p0, final int p1, final boolean p2, final Vector p3);
    
    private static native long nOpen(final int p0, final int p1, final boolean p2, final int p3, final float p4, final int p5, final int p6, final int p7, final boolean p8, final boolean p9, final int p10) throws LineUnavailableException;
    
    private static native void nStart(final long p0, final boolean p1);
    
    private static native void nStop(final long p0, final boolean p1);
    
    private static native void nClose(final long p0, final boolean p1);
    
    private static native int nWrite(final long p0, final byte[] p1, final int p2, final int p3, final int p4, final float p5, final float p6);
    
    private static native int nRead(final long p0, final byte[] p1, final int p2, final int p3, final int p4);
    
    private static native int nGetBufferSize(final long p0, final boolean p1);
    
    private static native boolean nIsStillDraining(final long p0, final boolean p1);
    
    private static native void nFlush(final long p0, final boolean p1);
    
    private static native int nAvailable(final long p0, final boolean p1);
    
    private static native long nGetBytePosition(final long p0, final boolean p1, final long p2);
    
    private static native void nSetBytePosition(final long p0, final boolean p1, final long p2);
    
    private static native boolean nRequiresServicing(final long p0, final boolean p1);
    
    private static native void nService(final long p0, final boolean p1);
    
    private static final class DirectDLI extends DataLine.Info
    {
        final AudioFormat[] hardwareFormats;
        
        private DirectDLI(final Class clazz, final AudioFormat[] array, final AudioFormat[] hardwareFormats, final int n, final int n2) {
            super(clazz, array, n, n2);
            this.hardwareFormats = hardwareFormats;
        }
        
        public boolean isFormatSupportedInHardware(final AudioFormat audioFormat) {
            if (audioFormat == null) {
                return false;
            }
            for (int i = 0; i < this.hardwareFormats.length; ++i) {
                if (audioFormat.matches(this.hardwareFormats[i])) {
                    return true;
                }
            }
            return false;
        }
        
        private AudioFormat[] getHardwareFormats() {
            return this.hardwareFormats;
        }
    }
    
    private static class DirectDL extends AbstractDataLine implements EventDispatcher.LineMonitor
    {
        protected final int mixerIndex;
        protected final int deviceID;
        protected long id;
        protected int waitTime;
        protected volatile boolean flushing;
        protected final boolean isSource;
        protected volatile long bytePosition;
        protected volatile boolean doIO;
        protected volatile boolean stoppedWritten;
        protected volatile boolean drained;
        protected boolean monitoring;
        protected int softwareConversionSize;
        protected AudioFormat hardwareFormat;
        private final Gain gainControl;
        private final Mute muteControl;
        private final Balance balanceControl;
        private final Pan panControl;
        private float leftGain;
        private float rightGain;
        protected volatile boolean noService;
        protected final Object lockNative;
        
        protected DirectDL(final DataLine.Info info, final DirectAudioDevice directAudioDevice, final AudioFormat audioFormat, final int n, final int mixerIndex, final int deviceID, final boolean isSource) {
            super(info, directAudioDevice, null, audioFormat, n);
            this.flushing = false;
            this.doIO = false;
            this.stoppedWritten = false;
            this.drained = false;
            this.monitoring = false;
            this.softwareConversionSize = 0;
            this.gainControl = new Gain();
            this.muteControl = new Mute();
            this.balanceControl = new Balance();
            this.panControl = new Pan();
            this.noService = false;
            this.lockNative = new Object();
            this.mixerIndex = mixerIndex;
            this.deviceID = deviceID;
            this.waitTime = 10;
            this.isSource = isSource;
        }
        
        @Override
        void implOpen(final AudioFormat audioFormat, int bufferSize) throws LineUnavailableException {
            Toolkit.isFullySpecifiedAudioFormat(audioFormat);
            if (!this.isSource) {
                JSSecurityManager.checkRecordPermission();
            }
            int n = 0;
            if (audioFormat.getEncoding().equals(AudioFormat.Encoding.ULAW)) {
                n = 1;
            }
            else if (audioFormat.getEncoding().equals(AudioFormat.Encoding.ALAW)) {
                n = 2;
            }
            if (bufferSize <= -1) {
                bufferSize = (int)Toolkit.millis2bytes(audioFormat, 500L);
            }
            DirectDLI directDLI = null;
            if (this.info instanceof DirectDLI) {
                directDLI = (DirectDLI)this.info;
            }
            if (this.isSource) {
                if (!audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) && !audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
                    this.controls = new Control[0];
                }
                else if (audioFormat.getChannels() > 2 || audioFormat.getSampleSizeInBits() > 16) {
                    this.controls = new Control[0];
                }
                else {
                    if (audioFormat.getChannels() == 1) {
                        this.controls = new Control[2];
                    }
                    else {
                        (this.controls = new Control[4])[2] = this.balanceControl;
                        this.controls[3] = this.panControl;
                    }
                    this.controls[0] = this.gainControl;
                    this.controls[1] = this.muteControl;
                }
            }
            this.hardwareFormat = audioFormat;
            this.softwareConversionSize = 0;
            if (directDLI != null && !directDLI.isFormatSupportedInHardware(audioFormat)) {
                final AudioFormat signOrEndianChangedFormat = DirectAudioDevice.getSignOrEndianChangedFormat(audioFormat);
                if (directDLI.isFormatSupportedInHardware(signOrEndianChangedFormat)) {
                    this.hardwareFormat = signOrEndianChangedFormat;
                    this.softwareConversionSize = audioFormat.getFrameSize() / audioFormat.getChannels();
                }
            }
            bufferSize = bufferSize / audioFormat.getFrameSize() * audioFormat.getFrameSize();
            this.id = nOpen(this.mixerIndex, this.deviceID, this.isSource, n, this.hardwareFormat.getSampleRate(), this.hardwareFormat.getSampleSizeInBits(), this.hardwareFormat.getFrameSize(), this.hardwareFormat.getChannels(), this.hardwareFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED), this.hardwareFormat.isBigEndian(), bufferSize);
            if (this.id == 0L) {
                throw new LineUnavailableException("line with format " + audioFormat + " not supported.");
            }
            this.bufferSize = nGetBufferSize(this.id, this.isSource);
            if (this.bufferSize < 1) {
                this.bufferSize = bufferSize;
            }
            this.format = audioFormat;
            this.waitTime = (int)Toolkit.bytes2millis(audioFormat, this.bufferSize) / 4;
            if (this.waitTime < 10) {
                this.waitTime = 1;
            }
            else if (this.waitTime > 1000) {
                this.waitTime = 1000;
            }
            this.bytePosition = 0L;
            this.stoppedWritten = false;
            this.doIO = false;
            this.calcVolume();
        }
        
        @Override
        void implStart() {
            if (!this.isSource) {
                JSSecurityManager.checkRecordPermission();
            }
            synchronized (this.lockNative) {
                nStart(this.id, this.isSource);
            }
            this.monitoring = this.requiresServicing();
            if (this.monitoring) {
                this.getEventDispatcher().addLineMonitor(this);
            }
            synchronized (this.lock) {
                this.doIO = true;
                if (this.isSource && this.stoppedWritten) {
                    this.setStarted(true);
                    this.setActive(true);
                }
            }
        }
        
        @Override
        void implStop() {
            if (!this.isSource) {
                JSSecurityManager.checkRecordPermission();
            }
            if (this.monitoring) {
                this.getEventDispatcher().removeLineMonitor(this);
                this.monitoring = false;
            }
            synchronized (this.lockNative) {
                nStop(this.id, this.isSource);
            }
            synchronized (this.lock) {
                this.setActive(this.doIO = false);
                this.setStarted(false);
                this.lock.notifyAll();
            }
            this.stoppedWritten = false;
        }
        
        @Override
        void implClose() {
            if (!this.isSource) {
                JSSecurityManager.checkRecordPermission();
            }
            if (this.monitoring) {
                this.getEventDispatcher().removeLineMonitor(this);
                this.monitoring = false;
            }
            this.doIO = false;
            final long id = this.id;
            this.id = 0L;
            synchronized (this.lockNative) {
                nClose(id, this.isSource);
            }
            this.bytePosition = 0L;
            this.softwareConversionSize = 0;
        }
        
        @Override
        public int available() {
            if (this.id == 0L) {
                return 0;
            }
            final int access$1400;
            synchronized (this.lockNative) {
                access$1400 = nAvailable(this.id, this.isSource);
            }
            return access$1400;
        }
        
        @Override
        public void drain() {
            this.noService = true;
            int n = 0;
            long longFramePosition = this.getLongFramePosition();
            boolean b = false;
            while (!this.drained) {
                synchronized (this.lockNative) {
                    if (this.id == 0L || !this.doIO || !nIsStillDraining(this.id, this.isSource)) {
                        break;
                    }
                }
                if (n % 5 == 4) {
                    final long longFramePosition2 = this.getLongFramePosition();
                    b |= (longFramePosition2 != longFramePosition);
                    if (n % 50 > 45) {
                        if (!b) {
                            break;
                        }
                        b = false;
                        longFramePosition = longFramePosition2;
                    }
                }
                ++n;
                synchronized (this.lock) {
                    try {
                        this.lock.wait(10L);
                    }
                    catch (final InterruptedException ex) {}
                }
            }
            if (this.doIO && this.id != 0L) {
                this.drained = true;
            }
            this.noService = false;
        }
        
        @Override
        public void flush() {
            if (this.id != 0L) {
                this.flushing = true;
                synchronized (this.lock) {
                    this.lock.notifyAll();
                }
                synchronized (this.lockNative) {
                    if (this.id != 0L) {
                        nFlush(this.id, this.isSource);
                    }
                }
                this.drained = true;
            }
        }
        
        @Override
        public long getLongFramePosition() {
            long access$1700;
            synchronized (this.lockNative) {
                access$1700 = nGetBytePosition(this.id, this.isSource, this.bytePosition);
            }
            if (access$1700 < 0L) {
                access$1700 = 0L;
            }
            return access$1700 / this.getFormat().getFrameSize();
        }
        
        public int write(final byte[] array, int n, int n2) {
            this.flushing = false;
            if (n2 == 0) {
                return 0;
            }
            if (n2 < 0) {
                throw new IllegalArgumentException("illegal len: " + n2);
            }
            if (n2 % this.getFormat().getFrameSize() != 0) {
                throw new IllegalArgumentException("illegal request to write non-integral number of frames (" + n2 + " bytes, frameSize = " + this.getFormat().getFrameSize() + " bytes)");
            }
            if (n < 0) {
                throw new ArrayIndexOutOfBoundsException(n);
            }
            if (n + (long)n2 > array.length) {
                throw new ArrayIndexOutOfBoundsException(array.length);
            }
            synchronized (this.lock) {
                if (!this.isActive() && this.doIO) {
                    this.setActive(true);
                    this.setStarted(true);
                }
            }
            int n3 = 0;
            while (!this.flushing) {
                final int access$1800;
                synchronized (this.lockNative) {
                    access$1800 = nWrite(this.id, array, n, n2, this.softwareConversionSize, this.leftGain, this.rightGain);
                    if (access$1800 < 0) {
                        break;
                    }
                    this.bytePosition += access$1800;
                    if (access$1800 > 0) {
                        this.drained = false;
                    }
                }
                n2 -= access$1800;
                n3 += access$1800;
                if (!this.doIO || n2 <= 0) {
                    break;
                }
                n += access$1800;
                synchronized (this.lock) {
                    try {
                        this.lock.wait(this.waitTime);
                    }
                    catch (final InterruptedException ex) {}
                }
            }
            if (n3 > 0 && !this.doIO) {
                this.stoppedWritten = true;
            }
            return n3;
        }
        
        protected boolean requiresServicing() {
            return nRequiresServicing(this.id, this.isSource);
        }
        
        @Override
        public void checkLine() {
            synchronized (this.lockNative) {
                if (this.monitoring && this.doIO && this.id != 0L && !this.flushing && !this.noService) {
                    nService(this.id, this.isSource);
                }
            }
        }
        
        private void calcVolume() {
            if (this.getFormat() == null) {
                return;
            }
            if (this.muteControl.getValue()) {
                this.leftGain = 0.0f;
                this.rightGain = 0.0f;
                return;
            }
            final float linearGain = this.gainControl.getLinearGain();
            if (this.getFormat().getChannels() == 1) {
                this.leftGain = linearGain;
                this.rightGain = linearGain;
            }
            else {
                final float value = this.balanceControl.getValue();
                if (value < 0.0f) {
                    this.leftGain = linearGain;
                    this.rightGain = linearGain * (value + 1.0f);
                }
                else {
                    this.leftGain = linearGain * (1.0f - value);
                    this.rightGain = linearGain;
                }
            }
        }
        
        protected final class Gain extends FloatControl
        {
            private float linearGain;
            
            private Gain() {
                super(Type.MASTER_GAIN, Toolkit.linearToDB(0.0f), Toolkit.linearToDB(2.0f), Math.abs(Toolkit.linearToDB(1.0f) - Toolkit.linearToDB(0.0f)) / 128.0f, -1, 0.0f, "dB", "Minimum", "", "Maximum");
                this.linearGain = 1.0f;
            }
            
            @Override
            public void setValue(final float n) {
                final float dbToLinear = Toolkit.dBToLinear(n);
                super.setValue(Toolkit.linearToDB(dbToLinear));
                this.linearGain = dbToLinear;
                DirectDL.this.calcVolume();
            }
            
            float getLinearGain() {
                return this.linearGain;
            }
        }
        
        private final class Mute extends BooleanControl
        {
            private Mute() {
                super(Type.MUTE, false, "True", "False");
            }
            
            @Override
            public void setValue(final boolean value) {
                super.setValue(value);
                DirectDL.this.calcVolume();
            }
        }
        
        private final class Balance extends FloatControl
        {
            private Balance() {
                super(Type.BALANCE, -1.0f, 1.0f, 0.0078125f, -1, 0.0f, "", "Left", "Center", "Right");
            }
            
            @Override
            public void setValue(final float n) {
                this.setValueImpl(n);
                DirectDL.this.panControl.setValueImpl(n);
                DirectDL.this.calcVolume();
            }
            
            void setValueImpl(final float value) {
                super.setValue(value);
            }
        }
        
        private final class Pan extends FloatControl
        {
            private Pan() {
                super(Type.PAN, -1.0f, 1.0f, 0.0078125f, -1, 0.0f, "", "Left", "Center", "Right");
            }
            
            @Override
            public void setValue(final float n) {
                this.setValueImpl(n);
                DirectDL.this.balanceControl.setValueImpl(n);
                DirectDL.this.calcVolume();
            }
            
            void setValueImpl(final float value) {
                super.setValue(value);
            }
        }
    }
    
    private static final class DirectSDL extends DirectDL implements SourceDataLine
    {
        private DirectSDL(final DataLine.Info info, final AudioFormat audioFormat, final int n, final DirectAudioDevice directAudioDevice) {
            super(info, directAudioDevice, audioFormat, n, directAudioDevice.getMixerIndex(), directAudioDevice.getDeviceID(), true);
        }
    }
    
    private static final class DirectTDL extends DirectDL implements TargetDataLine
    {
        private DirectTDL(final DataLine.Info info, final AudioFormat audioFormat, final int n, final DirectAudioDevice directAudioDevice) {
            super(info, directAudioDevice, audioFormat, n, directAudioDevice.getMixerIndex(), directAudioDevice.getDeviceID(), false);
        }
        
        @Override
        public int read(final byte[] array, int n, int n2) {
            this.flushing = false;
            if (n2 == 0) {
                return 0;
            }
            if (n2 < 0) {
                throw new IllegalArgumentException("illegal len: " + n2);
            }
            if (n2 % this.getFormat().getFrameSize() != 0) {
                throw new IllegalArgumentException("illegal request to read non-integral number of frames (" + n2 + " bytes, frameSize = " + this.getFormat().getFrameSize() + " bytes)");
            }
            if (n < 0) {
                throw new ArrayIndexOutOfBoundsException(n);
            }
            if (n + (long)n2 > array.length) {
                throw new ArrayIndexOutOfBoundsException(array.length);
            }
            synchronized (this.lock) {
                if (!this.isActive() && this.doIO) {
                    this.setActive(true);
                    this.setStarted(true);
                }
            }
            int n3 = 0;
            while (this.doIO && !this.flushing) {
                final int access$2400;
                synchronized (this.lockNative) {
                    access$2400 = nRead(this.id, array, n, n2, this.softwareConversionSize);
                    if (access$2400 < 0) {
                        break;
                    }
                    this.bytePosition += access$2400;
                    if (access$2400 > 0) {
                        this.drained = false;
                    }
                }
                n2 -= access$2400;
                n3 += access$2400;
                if (n2 <= 0) {
                    break;
                }
                n += access$2400;
                synchronized (this.lock) {
                    try {
                        this.lock.wait(this.waitTime);
                    }
                    catch (final InterruptedException ex) {}
                }
            }
            if (this.flushing) {
                n3 = 0;
            }
            return n3;
        }
    }
    
    private static final class DirectClip extends DirectDL implements Clip, Runnable, AutoClosingClip
    {
        private volatile Thread thread;
        private volatile byte[] audioData;
        private volatile int frameSize;
        private volatile int m_lengthInFrames;
        private volatile int loopCount;
        private volatile int clipBytePosition;
        private volatile int newFramePosition;
        private volatile int loopStartFrame;
        private volatile int loopEndFrame;
        private boolean autoclosing;
        
        private DirectClip(final DataLine.Info info, final AudioFormat audioFormat, final int n, final DirectAudioDevice directAudioDevice) {
            super(info, directAudioDevice, audioFormat, n, directAudioDevice.getMixerIndex(), directAudioDevice.getDeviceID(), true);
            this.audioData = null;
            this.autoclosing = false;
        }
        
        @Override
        public void open(final AudioFormat audioFormat, final byte[] array, final int n, final int n2) throws LineUnavailableException {
            Toolkit.isFullySpecifiedAudioFormat(audioFormat);
            final byte[] array2 = new byte[n2];
            System.arraycopy(array, n, array2, 0, n2);
            this.open(audioFormat, array2, n2 / audioFormat.getFrameSize());
        }
        
        private void open(final AudioFormat audioFormat, final byte[] audioData, final int lengthInFrames) throws LineUnavailableException {
            Toolkit.isFullySpecifiedAudioFormat(audioFormat);
            synchronized (this.mixer) {
                if (this.isOpen()) {
                    throw new IllegalStateException("Clip is already open with format " + this.getFormat() + " and frame lengh of " + this.getFrameLength());
                }
                this.audioData = audioData;
                this.frameSize = audioFormat.getFrameSize();
                this.m_lengthInFrames = lengthInFrames;
                this.bytePosition = 0L;
                this.clipBytePosition = 0;
                this.newFramePosition = -1;
                this.loopStartFrame = 0;
                this.loopEndFrame = lengthInFrames - 1;
                this.loopCount = 0;
                try {
                    this.open(audioFormat, (int)Toolkit.millis2bytes(audioFormat, 1000L));
                }
                catch (final LineUnavailableException ex) {
                    this.audioData = null;
                    throw ex;
                }
                catch (final IllegalArgumentException ex2) {
                    this.audioData = null;
                    throw ex2;
                }
                (this.thread = JSSecurityManager.createThread(this, "Direct Clip", true, 6, false)).start();
            }
            if (this.isAutoClosing()) {
                this.getEventDispatcher().autoClosingClipOpened(this);
            }
        }
        
        @Override
        public void open(final AudioInputStream audioInputStream) throws LineUnavailableException, IOException {
            Toolkit.isFullySpecifiedAudioFormat(audioInputStream.getFormat());
            synchronized (this.mixer) {
                if (this.isOpen()) {
                    throw new IllegalStateException("Clip is already open with format " + this.getFormat() + " and frame lengh of " + this.getFrameLength());
                }
                final int n = (int)audioInputStream.getFrameLength();
                int n2 = 0;
                final int frameSize = audioInputStream.getFormat().getFrameSize();
                byte[] internalBuffer;
                if (n != -1) {
                    final int n3 = n * frameSize;
                    if (n3 < 0) {
                        throw new IllegalArgumentException("Audio data < 0");
                    }
                    try {
                        internalBuffer = new byte[n3];
                    }
                    catch (final OutOfMemoryError outOfMemoryError) {
                        throw new IOException("Audio data is too big");
                    }
                    int n4 = n3;
                    int read = 0;
                    while (n4 > 0 && read >= 0) {
                        read = audioInputStream.read(internalBuffer, n2, n4);
                        if (read > 0) {
                            n2 += read;
                            n4 -= read;
                        }
                        else {
                            if (read != 0) {
                                continue;
                            }
                            Thread.yield();
                        }
                    }
                }
                else {
                    final int max = Math.max(16384, frameSize);
                    final DirectBAOS directBAOS = new DirectBAOS();
                    byte[] array;
                    try {
                        array = new byte[max];
                    }
                    catch (final OutOfMemoryError outOfMemoryError2) {
                        throw new IOException("Audio data is too big");
                    }
                    int i = 0;
                    while (i >= 0) {
                        i = audioInputStream.read(array, 0, array.length);
                        if (i > 0) {
                            directBAOS.write(array, 0, i);
                            n2 += i;
                        }
                        else {
                            if (i != 0) {
                                continue;
                            }
                            Thread.yield();
                        }
                    }
                    internalBuffer = directBAOS.getInternalBuffer();
                }
                this.open(audioInputStream.getFormat(), internalBuffer, n2 / frameSize);
            }
        }
        
        @Override
        public int getFrameLength() {
            return this.m_lengthInFrames;
        }
        
        @Override
        public long getMicrosecondLength() {
            return Toolkit.frames2micros(this.getFormat(), this.getFrameLength());
        }
        
        @Override
        public void setFramePosition(int frameLength) {
            if (frameLength < 0) {
                frameLength = 0;
            }
            else if (frameLength >= this.getFrameLength()) {
                frameLength = this.getFrameLength();
            }
            if (this.doIO) {
                this.newFramePosition = frameLength;
            }
            else {
                this.clipBytePosition = frameLength * this.frameSize;
                this.newFramePosition = -1;
            }
            this.bytePosition = frameLength * this.frameSize;
            this.flush();
            synchronized (this.lockNative) {
                nSetBytePosition(this.id, this.isSource, frameLength * this.frameSize);
            }
        }
        
        @Override
        public long getLongFramePosition() {
            return super.getLongFramePosition();
        }
        
        @Override
        public synchronized void setMicrosecondPosition(final long n) {
            this.setFramePosition((int)Toolkit.micros2frames(this.getFormat(), n));
        }
        
        @Override
        public void setLoopPoints(final int loopStartFrame, int loopEndFrame) {
            if (loopStartFrame < 0 || loopStartFrame >= this.getFrameLength()) {
                throw new IllegalArgumentException("illegal value for start: " + loopStartFrame);
            }
            if (loopEndFrame >= this.getFrameLength()) {
                throw new IllegalArgumentException("illegal value for end: " + loopEndFrame);
            }
            if (loopEndFrame == -1) {
                loopEndFrame = this.getFrameLength() - 1;
                if (loopEndFrame < 0) {
                    loopEndFrame = 0;
                }
            }
            if (loopEndFrame < loopStartFrame) {
                throw new IllegalArgumentException("End position " + loopEndFrame + "  preceeds start position " + loopStartFrame);
            }
            this.loopStartFrame = loopStartFrame;
            this.loopEndFrame = loopEndFrame;
        }
        
        @Override
        public void loop(final int loopCount) {
            this.loopCount = loopCount;
            this.start();
        }
        
        @Override
        void implOpen(final AudioFormat audioFormat, final int n) throws LineUnavailableException {
            if (this.audioData == null) {
                throw new IllegalArgumentException("illegal call to open() in interface Clip");
            }
            super.implOpen(audioFormat, n);
        }
        
        @Override
        void implClose() {
            final Thread thread = this.thread;
            this.thread = null;
            this.doIO = false;
            if (thread != null) {
                synchronized (this.lock) {
                    this.lock.notifyAll();
                }
                try {
                    thread.join(2000L);
                }
                catch (final InterruptedException ex) {}
            }
            super.implClose();
            this.audioData = null;
            this.newFramePosition = -1;
            this.getEventDispatcher().autoClosingClipClosed(this);
        }
        
        @Override
        void implStart() {
            super.implStart();
        }
        
        @Override
        void implStop() {
            super.implStop();
            this.loopCount = 0;
        }
        
        @Override
        public void run() {
            final Thread currentThread = Thread.currentThread();
            while (this.thread == currentThread) {
                synchronized (this.lock) {
                    if (!this.doIO) {
                        try {
                            this.lock.wait();
                        }
                        catch (final InterruptedException ex) {}
                        finally {
                            if (this.thread != currentThread) {
                                break;
                            }
                        }
                    }
                }
                while (this.doIO) {
                    if (this.newFramePosition >= 0) {
                        this.clipBytePosition = this.newFramePosition * this.frameSize;
                        this.newFramePosition = -1;
                    }
                    int loopEndFrame = this.getFrameLength() - 1;
                    if (this.loopCount > 0 || this.loopCount == -1) {
                        loopEndFrame = this.loopEndFrame;
                    }
                    int align = (int)(loopEndFrame - (long)(this.clipBytePosition / this.frameSize) + 1L) * this.frameSize;
                    if (align > this.getBufferSize()) {
                        align = Toolkit.align(this.getBufferSize(), this.frameSize);
                    }
                    final int write = this.write(this.audioData, this.clipBytePosition, align);
                    this.clipBytePosition += write;
                    if (this.doIO && this.newFramePosition < 0 && write >= 0 && this.clipBytePosition / this.frameSize > (long)loopEndFrame) {
                        if (this.loopCount > 0 || this.loopCount == -1) {
                            if (this.loopCount != -1) {
                                --this.loopCount;
                            }
                            this.newFramePosition = this.loopStartFrame;
                        }
                        else {
                            this.drain();
                            this.stop();
                        }
                    }
                }
            }
        }
        
        @Override
        public boolean isAutoClosing() {
            return this.autoclosing;
        }
        
        @Override
        public void setAutoClosing(final boolean autoclosing) {
            if (autoclosing != this.autoclosing) {
                if (this.isOpen()) {
                    if (autoclosing) {
                        this.getEventDispatcher().autoClosingClipOpened(this);
                    }
                    else {
                        this.getEventDispatcher().autoClosingClipClosed(this);
                    }
                }
                this.autoclosing = autoclosing;
            }
        }
        
        @Override
        protected boolean requiresServicing() {
            return false;
        }
    }
    
    private static class DirectBAOS extends ByteArrayOutputStream
    {
        DirectBAOS() {
        }
        
        public byte[] getInternalBuffer() {
            return this.buf;
        }
    }
}
