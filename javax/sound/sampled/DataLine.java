package javax.sound.sampled;

import java.util.Arrays;

public interface DataLine extends Line
{
    void drain();
    
    void flush();
    
    void start();
    
    void stop();
    
    boolean isRunning();
    
    boolean isActive();
    
    AudioFormat getFormat();
    
    int getBufferSize();
    
    int available();
    
    int getFramePosition();
    
    long getLongFramePosition();
    
    long getMicrosecondPosition();
    
    float getLevel();
    
    public static class Info extends Line.Info
    {
        private final AudioFormat[] formats;
        private final int minBufferSize;
        private final int maxBufferSize;
        
        public Info(final Class<?> clazz, final AudioFormat[] array, final int minBufferSize, final int maxBufferSize) {
            super(clazz);
            if (array == null) {
                this.formats = new AudioFormat[0];
            }
            else {
                this.formats = Arrays.copyOf(array, array.length);
            }
            this.minBufferSize = minBufferSize;
            this.maxBufferSize = maxBufferSize;
        }
        
        public Info(final Class<?> clazz, final AudioFormat audioFormat, final int n) {
            super(clazz);
            if (audioFormat == null) {
                this.formats = new AudioFormat[0];
            }
            else {
                this.formats = new AudioFormat[] { audioFormat };
            }
            this.minBufferSize = n;
            this.maxBufferSize = n;
        }
        
        public Info(final Class<?> clazz, final AudioFormat audioFormat) {
            this(clazz, audioFormat, -1);
        }
        
        public AudioFormat[] getFormats() {
            return Arrays.copyOf(this.formats, this.formats.length);
        }
        
        public boolean isFormatSupported(final AudioFormat audioFormat) {
            for (int i = 0; i < this.formats.length; ++i) {
                if (audioFormat.matches(this.formats[i])) {
                    return true;
                }
            }
            return false;
        }
        
        public int getMinBufferSize() {
            return this.minBufferSize;
        }
        
        public int getMaxBufferSize() {
            return this.maxBufferSize;
        }
        
        @Override
        public boolean matches(final Line.Info info) {
            if (!super.matches(info)) {
                return false;
            }
            final Info info2 = (Info)info;
            if (this.getMaxBufferSize() >= 0 && info2.getMaxBufferSize() >= 0 && this.getMaxBufferSize() > info2.getMaxBufferSize()) {
                return false;
            }
            if (this.getMinBufferSize() >= 0 && info2.getMinBufferSize() >= 0 && this.getMinBufferSize() < info2.getMinBufferSize()) {
                return false;
            }
            final AudioFormat[] formats = this.getFormats();
            if (formats != null) {
                for (int i = 0; i < formats.length; ++i) {
                    if (formats[i] != null && !info2.isFormatSupported(formats[i])) {
                        return false;
                    }
                }
            }
            return true;
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            if (this.formats.length == 1 && this.formats[0] != null) {
                sb.append(" supporting format " + this.formats[0]);
            }
            else if (this.getFormats().length > 1) {
                sb.append(" supporting " + this.getFormats().length + " audio formats");
            }
            if (this.minBufferSize != -1 && this.maxBufferSize != -1) {
                sb.append(", and buffers of " + this.minBufferSize + " to " + this.maxBufferSize + " bytes");
            }
            else if (this.minBufferSize != -1 && this.minBufferSize > 0) {
                sb.append(", and buffers of at least " + this.minBufferSize + " bytes");
            }
            else if (this.maxBufferSize != -1) {
                sb.append(", and buffers of up to " + this.minBufferSize + " bytes");
            }
            return new String(super.toString() + (Object)sb);
        }
    }
}
