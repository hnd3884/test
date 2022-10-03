package com.sun.media.sound;

import java.nio.FloatBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;

public abstract class AudioFloatConverter
{
    private AudioFormat format;
    
    public static AudioFloatConverter getConverter(final AudioFormat format) {
        AudioFloatConverter audioFloatConverter = null;
        if (format.getFrameSize() == 0) {
            return null;
        }
        if (format.getFrameSize() != (format.getSampleSizeInBits() + 7) / 8 * format.getChannels()) {
            return null;
        }
        if (format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
            if (format.isBigEndian()) {
                if (format.getSampleSizeInBits() <= 8) {
                    audioFloatConverter = new AudioFloatConversion8S();
                }
                else if (format.getSampleSizeInBits() > 8 && format.getSampleSizeInBits() <= 16) {
                    audioFloatConverter = new AudioFloatConversion16SB();
                }
                else if (format.getSampleSizeInBits() > 16 && format.getSampleSizeInBits() <= 24) {
                    audioFloatConverter = new AudioFloatConversion24SB();
                }
                else if (format.getSampleSizeInBits() > 24 && format.getSampleSizeInBits() <= 32) {
                    audioFloatConverter = new AudioFloatConversion32SB();
                }
                else if (format.getSampleSizeInBits() > 32) {
                    audioFloatConverter = new AudioFloatConversion32xSB((format.getSampleSizeInBits() + 7) / 8 - 4);
                }
            }
            else if (format.getSampleSizeInBits() <= 8) {
                audioFloatConverter = new AudioFloatConversion8S();
            }
            else if (format.getSampleSizeInBits() > 8 && format.getSampleSizeInBits() <= 16) {
                audioFloatConverter = new AudioFloatConversion16SL();
            }
            else if (format.getSampleSizeInBits() > 16 && format.getSampleSizeInBits() <= 24) {
                audioFloatConverter = new AudioFloatConversion24SL();
            }
            else if (format.getSampleSizeInBits() > 24 && format.getSampleSizeInBits() <= 32) {
                audioFloatConverter = new AudioFloatConversion32SL();
            }
            else if (format.getSampleSizeInBits() > 32) {
                audioFloatConverter = new AudioFloatConversion32xSL((format.getSampleSizeInBits() + 7) / 8 - 4);
            }
        }
        else if (format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
            if (format.isBigEndian()) {
                if (format.getSampleSizeInBits() <= 8) {
                    audioFloatConverter = new AudioFloatConversion8U();
                }
                else if (format.getSampleSizeInBits() > 8 && format.getSampleSizeInBits() <= 16) {
                    audioFloatConverter = new AudioFloatConversion16UB();
                }
                else if (format.getSampleSizeInBits() > 16 && format.getSampleSizeInBits() <= 24) {
                    audioFloatConverter = new AudioFloatConversion24UB();
                }
                else if (format.getSampleSizeInBits() > 24 && format.getSampleSizeInBits() <= 32) {
                    audioFloatConverter = new AudioFloatConversion32UB();
                }
                else if (format.getSampleSizeInBits() > 32) {
                    audioFloatConverter = new AudioFloatConversion32xUB((format.getSampleSizeInBits() + 7) / 8 - 4);
                }
            }
            else if (format.getSampleSizeInBits() <= 8) {
                audioFloatConverter = new AudioFloatConversion8U();
            }
            else if (format.getSampleSizeInBits() > 8 && format.getSampleSizeInBits() <= 16) {
                audioFloatConverter = new AudioFloatConversion16UL();
            }
            else if (format.getSampleSizeInBits() > 16 && format.getSampleSizeInBits() <= 24) {
                audioFloatConverter = new AudioFloatConversion24UL();
            }
            else if (format.getSampleSizeInBits() > 24 && format.getSampleSizeInBits() <= 32) {
                audioFloatConverter = new AudioFloatConversion32UL();
            }
            else if (format.getSampleSizeInBits() > 32) {
                audioFloatConverter = new AudioFloatConversion32xUL((format.getSampleSizeInBits() + 7) / 8 - 4);
            }
        }
        else if (format.getEncoding().equals(AudioFormat.Encoding.PCM_FLOAT)) {
            if (format.getSampleSizeInBits() == 32) {
                if (format.isBigEndian()) {
                    audioFloatConverter = new AudioFloatConversion32B();
                }
                else {
                    audioFloatConverter = new AudioFloatConversion32L();
                }
            }
            else if (format.getSampleSizeInBits() == 64) {
                if (format.isBigEndian()) {
                    audioFloatConverter = new AudioFloatConversion64B();
                }
                else {
                    audioFloatConverter = new AudioFloatConversion64L();
                }
            }
        }
        if ((format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) || format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) && format.getSampleSizeInBits() % 8 != 0) {
            audioFloatConverter = new AudioFloatLSBFilter(audioFloatConverter, format);
        }
        if (audioFloatConverter != null) {
            audioFloatConverter.format = format;
        }
        return audioFloatConverter;
    }
    
    public final AudioFormat getFormat() {
        return this.format;
    }
    
    public abstract float[] toFloatArray(final byte[] p0, final int p1, final float[] p2, final int p3, final int p4);
    
    public final float[] toFloatArray(final byte[] array, final float[] array2, final int n, final int n2) {
        return this.toFloatArray(array, 0, array2, n, n2);
    }
    
    public final float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2) {
        return this.toFloatArray(array, n, array2, 0, n2);
    }
    
    public final float[] toFloatArray(final byte[] array, final float[] array2, final int n) {
        return this.toFloatArray(array, 0, array2, 0, n);
    }
    
    public final float[] toFloatArray(final byte[] array, final float[] array2) {
        return this.toFloatArray(array, 0, array2, 0, array2.length);
    }
    
    public abstract byte[] toByteArray(final float[] p0, final int p1, final int p2, final byte[] p3, final int p4);
    
    public final byte[] toByteArray(final float[] array, final int n, final byte[] array2, final int n2) {
        return this.toByteArray(array, 0, n, array2, n2);
    }
    
    public final byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2) {
        return this.toByteArray(array, n, n2, array2, 0);
    }
    
    public final byte[] toByteArray(final float[] array, final int n, final byte[] array2) {
        return this.toByteArray(array, 0, n, array2, 0);
    }
    
    public final byte[] toByteArray(final float[] array, final byte[] array2) {
        return this.toByteArray(array, 0, array.length, array2, 0);
    }
    
    private static class AudioFloatLSBFilter extends AudioFloatConverter
    {
        private final AudioFloatConverter converter;
        private final int offset;
        private final int stepsize;
        private final byte mask;
        private byte[] mask_buffer;
        
        AudioFloatLSBFilter(final AudioFloatConverter converter, final AudioFormat audioFormat) {
            final int sampleSizeInBits = audioFormat.getSampleSizeInBits();
            final boolean bigEndian = audioFormat.isBigEndian();
            this.converter = converter;
            this.stepsize = (sampleSizeInBits + 7) / 8;
            this.offset = (bigEndian ? (this.stepsize - 1) : 0);
            final int n = sampleSizeInBits % 8;
            if (n == 0) {
                this.mask = 0;
            }
            else if (n == 1) {
                this.mask = -128;
            }
            else if (n == 2) {
                this.mask = -64;
            }
            else if (n == 3) {
                this.mask = -32;
            }
            else if (n == 4) {
                this.mask = -16;
            }
            else if (n == 5) {
                this.mask = -8;
            }
            else if (n == 6) {
                this.mask = -4;
            }
            else if (n == 7) {
                this.mask = -2;
            }
            else {
                this.mask = -1;
            }
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            final byte[] byteArray = this.converter.toByteArray(array, n, n2, array2, n3);
            for (int n4 = n2 * this.stepsize, i = n3 + this.offset; i < n4; i += this.stepsize) {
                array2[i] &= this.mask;
            }
            return byteArray;
        }
        
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            if (this.mask_buffer == null || this.mask_buffer.length < array.length) {
                this.mask_buffer = new byte[array.length];
            }
            System.arraycopy(array, 0, this.mask_buffer, 0, array.length);
            for (int n4 = n3 * this.stepsize, i = n + this.offset; i < n4; i += this.stepsize) {
                this.mask_buffer[i] &= this.mask;
            }
            return this.converter.toFloatArray(this.mask_buffer, n, array2, n2, n3);
        }
    }
    
    private static class AudioFloatConversion64L extends AudioFloatConverter
    {
        ByteBuffer bytebuffer;
        DoubleBuffer floatbuffer;
        double[] double_buff;
        
        private AudioFloatConversion64L() {
            this.bytebuffer = null;
            this.floatbuffer = null;
            this.double_buff = null;
        }
        
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            final int n4 = n3 * 8;
            if (this.bytebuffer == null || this.bytebuffer.capacity() < n4) {
                this.bytebuffer = ByteBuffer.allocate(n4).order(ByteOrder.LITTLE_ENDIAN);
                this.floatbuffer = this.bytebuffer.asDoubleBuffer();
            }
            this.bytebuffer.position(0);
            this.floatbuffer.position(0);
            this.bytebuffer.put(array, n, n4);
            if (this.double_buff == null || this.double_buff.length < n3 + n2) {
                this.double_buff = new double[n3 + n2];
            }
            this.floatbuffer.get(this.double_buff, n2, n3);
            for (int n5 = n2 + n3, i = n2; i < n5; ++i) {
                array2[i] = (float)this.double_buff[i];
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            final int n4 = n2 * 8;
            if (this.bytebuffer == null || this.bytebuffer.capacity() < n4) {
                this.bytebuffer = ByteBuffer.allocate(n4).order(ByteOrder.LITTLE_ENDIAN);
                this.floatbuffer = this.bytebuffer.asDoubleBuffer();
            }
            this.floatbuffer.position(0);
            this.bytebuffer.position(0);
            if (this.double_buff == null || this.double_buff.length < n + n2) {
                this.double_buff = new double[n + n2];
            }
            for (int n5 = n + n2, i = n; i < n5; ++i) {
                this.double_buff[i] = array[i];
            }
            this.floatbuffer.put(this.double_buff, n, n2);
            this.bytebuffer.get(array2, n3, n4);
            return array2;
        }
    }
    
    private static class AudioFloatConversion64B extends AudioFloatConverter
    {
        ByteBuffer bytebuffer;
        DoubleBuffer floatbuffer;
        double[] double_buff;
        
        private AudioFloatConversion64B() {
            this.bytebuffer = null;
            this.floatbuffer = null;
            this.double_buff = null;
        }
        
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            final int n4 = n3 * 8;
            if (this.bytebuffer == null || this.bytebuffer.capacity() < n4) {
                this.bytebuffer = ByteBuffer.allocate(n4).order(ByteOrder.BIG_ENDIAN);
                this.floatbuffer = this.bytebuffer.asDoubleBuffer();
            }
            this.bytebuffer.position(0);
            this.floatbuffer.position(0);
            this.bytebuffer.put(array, n, n4);
            if (this.double_buff == null || this.double_buff.length < n3 + n2) {
                this.double_buff = new double[n3 + n2];
            }
            this.floatbuffer.get(this.double_buff, n2, n3);
            for (int n5 = n2 + n3, i = n2; i < n5; ++i) {
                array2[i] = (float)this.double_buff[i];
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            final int n4 = n2 * 8;
            if (this.bytebuffer == null || this.bytebuffer.capacity() < n4) {
                this.bytebuffer = ByteBuffer.allocate(n4).order(ByteOrder.BIG_ENDIAN);
                this.floatbuffer = this.bytebuffer.asDoubleBuffer();
            }
            this.floatbuffer.position(0);
            this.bytebuffer.position(0);
            if (this.double_buff == null || this.double_buff.length < n + n2) {
                this.double_buff = new double[n + n2];
            }
            for (int n5 = n + n2, i = n; i < n5; ++i) {
                this.double_buff[i] = array[i];
            }
            this.floatbuffer.put(this.double_buff, n, n2);
            this.bytebuffer.get(array2, n3, n4);
            return array2;
        }
    }
    
    private static class AudioFloatConversion32L extends AudioFloatConverter
    {
        ByteBuffer bytebuffer;
        FloatBuffer floatbuffer;
        
        private AudioFloatConversion32L() {
            this.bytebuffer = null;
            this.floatbuffer = null;
        }
        
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            final int n4 = n3 * 4;
            if (this.bytebuffer == null || this.bytebuffer.capacity() < n4) {
                this.bytebuffer = ByteBuffer.allocate(n4).order(ByteOrder.LITTLE_ENDIAN);
                this.floatbuffer = this.bytebuffer.asFloatBuffer();
            }
            this.bytebuffer.position(0);
            this.floatbuffer.position(0);
            this.bytebuffer.put(array, n, n4);
            this.floatbuffer.get(array2, n2, n3);
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            final int n4 = n2 * 4;
            if (this.bytebuffer == null || this.bytebuffer.capacity() < n4) {
                this.bytebuffer = ByteBuffer.allocate(n4).order(ByteOrder.LITTLE_ENDIAN);
                this.floatbuffer = this.bytebuffer.asFloatBuffer();
            }
            this.floatbuffer.position(0);
            this.bytebuffer.position(0);
            this.floatbuffer.put(array, n, n2);
            this.bytebuffer.get(array2, n3, n4);
            return array2;
        }
    }
    
    private static class AudioFloatConversion32B extends AudioFloatConverter
    {
        ByteBuffer bytebuffer;
        FloatBuffer floatbuffer;
        
        private AudioFloatConversion32B() {
            this.bytebuffer = null;
            this.floatbuffer = null;
        }
        
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            final int n4 = n3 * 4;
            if (this.bytebuffer == null || this.bytebuffer.capacity() < n4) {
                this.bytebuffer = ByteBuffer.allocate(n4).order(ByteOrder.BIG_ENDIAN);
                this.floatbuffer = this.bytebuffer.asFloatBuffer();
            }
            this.bytebuffer.position(0);
            this.floatbuffer.position(0);
            this.bytebuffer.put(array, n, n4);
            this.floatbuffer.get(array2, n2, n3);
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            final int n4 = n2 * 4;
            if (this.bytebuffer == null || this.bytebuffer.capacity() < n4) {
                this.bytebuffer = ByteBuffer.allocate(n4).order(ByteOrder.BIG_ENDIAN);
                this.floatbuffer = this.bytebuffer.asFloatBuffer();
            }
            this.floatbuffer.position(0);
            this.bytebuffer.position(0);
            this.floatbuffer.put(array, n, n2);
            this.bytebuffer.get(array2, n3, n4);
            return array2;
        }
    }
    
    private static class AudioFloatConversion8S extends AudioFloatConverter
    {
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            int n4 = n;
            int n5 = n2;
            for (int i = 0; i < n3; ++i) {
                array2[n5++] = array[n4++] * 0.007874016f;
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            int n4 = n;
            int n5 = n3;
            for (int i = 0; i < n2; ++i) {
                array2[n5++] = (byte)(array[n4++] * 127.0f);
            }
            return array2;
        }
    }
    
    private static class AudioFloatConversion8U extends AudioFloatConverter
    {
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            int n4 = n;
            int n5 = n2;
            for (int i = 0; i < n3; ++i) {
                array2[n5++] = ((array[n4++] & 0xFF) - 127) * 0.007874016f;
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            int n4 = n;
            int n5 = n3;
            for (int i = 0; i < n2; ++i) {
                array2[n5++] = (byte)(127.0f + array[n4++] * 127.0f);
            }
            return array2;
        }
    }
    
    private static class AudioFloatConversion16SL extends AudioFloatConverter
    {
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            int n4 = n;
            for (int n5 = n2 + n3, i = n2; i < n5; ++i) {
                array2[i] = (short)((array[n4++] & 0xFF) | array[n4++] << 8) * 3.051851E-5f;
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            int n4 = n3;
            for (int n5 = n + n2, i = n; i < n5; ++i) {
                final int n6 = (int)(array[i] * 32767.0);
                array2[n4++] = (byte)n6;
                array2[n4++] = (byte)(n6 >>> 8);
            }
            return array2;
        }
    }
    
    private static class AudioFloatConversion16SB extends AudioFloatConverter
    {
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            int n4 = n;
            int n5 = n2;
            for (int i = 0; i < n3; ++i) {
                array2[n5++] = (short)(array[n4++] << 8 | (array[n4++] & 0xFF)) * 3.051851E-5f;
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            int n4 = n;
            int n5 = n3;
            for (int i = 0; i < n2; ++i) {
                final int n6 = (int)(array[n4++] * 32767.0);
                array2[n5++] = (byte)(n6 >>> 8);
                array2[n5++] = (byte)n6;
            }
            return array2;
        }
    }
    
    private static class AudioFloatConversion16UL extends AudioFloatConverter
    {
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            int n4 = n;
            int n5 = n2;
            for (int i = 0; i < n3; ++i) {
                array2[n5++] = (((array[n4++] & 0xFF) | (array[n4++] & 0xFF) << 8) - 32767) * 3.051851E-5f;
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            int n4 = n;
            int n5 = n3;
            for (int i = 0; i < n2; ++i) {
                final int n6 = 32767 + (int)(array[n4++] * 32767.0);
                array2[n5++] = (byte)n6;
                array2[n5++] = (byte)(n6 >>> 8);
            }
            return array2;
        }
    }
    
    private static class AudioFloatConversion16UB extends AudioFloatConverter
    {
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            int n4 = n;
            int n5 = n2;
            for (int i = 0; i < n3; ++i) {
                array2[n5++] = (((array[n4++] & 0xFF) << 8 | (array[n4++] & 0xFF)) - 32767) * 3.051851E-5f;
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            int n4 = n;
            int n5 = n3;
            for (int i = 0; i < n2; ++i) {
                final int n6 = 32767 + (int)(array[n4++] * 32767.0);
                array2[n5++] = (byte)(n6 >>> 8);
                array2[n5++] = (byte)n6;
            }
            return array2;
        }
    }
    
    private static class AudioFloatConversion24SL extends AudioFloatConverter
    {
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            int n4 = n;
            int n5 = n2;
            for (int i = 0; i < n3; ++i) {
                int n6 = (array[n4++] & 0xFF) | (array[n4++] & 0xFF) << 8 | (array[n4++] & 0xFF) << 16;
                if (n6 > 8388607) {
                    n6 -= 16777216;
                }
                array2[n5++] = n6 * 1.192093E-7f;
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            int n4 = n;
            int n5 = n3;
            for (int i = 0; i < n2; ++i) {
                int n6 = (int)(array[n4++] * 8388607.0f);
                if (n6 < 0) {
                    n6 += 16777216;
                }
                array2[n5++] = (byte)n6;
                array2[n5++] = (byte)(n6 >>> 8);
                array2[n5++] = (byte)(n6 >>> 16);
            }
            return array2;
        }
    }
    
    private static class AudioFloatConversion24SB extends AudioFloatConverter
    {
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            int n4 = n;
            int n5 = n2;
            for (int i = 0; i < n3; ++i) {
                int n6 = (array[n4++] & 0xFF) << 16 | (array[n4++] & 0xFF) << 8 | (array[n4++] & 0xFF);
                if (n6 > 8388607) {
                    n6 -= 16777216;
                }
                array2[n5++] = n6 * 1.192093E-7f;
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            int n4 = n;
            int n5 = n3;
            for (int i = 0; i < n2; ++i) {
                int n6 = (int)(array[n4++] * 8388607.0f);
                if (n6 < 0) {
                    n6 += 16777216;
                }
                array2[n5++] = (byte)(n6 >>> 16);
                array2[n5++] = (byte)(n6 >>> 8);
                array2[n5++] = (byte)n6;
            }
            return array2;
        }
    }
    
    private static class AudioFloatConversion24UL extends AudioFloatConverter
    {
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            int n4 = n;
            int n5 = n2;
            for (int i = 0; i < n3; ++i) {
                array2[n5++] = (((array[n4++] & 0xFF) | (array[n4++] & 0xFF) << 8 | (array[n4++] & 0xFF) << 16) - 8388607) * 1.192093E-7f;
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            int n4 = n;
            int n5 = n3;
            for (int i = 0; i < n2; ++i) {
                final int n6 = (int)(array[n4++] * 8388607.0f) + 8388607;
                array2[n5++] = (byte)n6;
                array2[n5++] = (byte)(n6 >>> 8);
                array2[n5++] = (byte)(n6 >>> 16);
            }
            return array2;
        }
    }
    
    private static class AudioFloatConversion24UB extends AudioFloatConverter
    {
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            int n4 = n;
            int n5 = n2;
            for (int i = 0; i < n3; ++i) {
                array2[n5++] = (((array[n4++] & 0xFF) << 16 | (array[n4++] & 0xFF) << 8 | (array[n4++] & 0xFF)) - 8388607) * 1.192093E-7f;
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            int n4 = n;
            int n5 = n3;
            for (int i = 0; i < n2; ++i) {
                final int n6 = (int)(array[n4++] * 8388607.0f) + 8388607;
                array2[n5++] = (byte)(n6 >>> 16);
                array2[n5++] = (byte)(n6 >>> 8);
                array2[n5++] = (byte)n6;
            }
            return array2;
        }
    }
    
    private static class AudioFloatConversion32SL extends AudioFloatConverter
    {
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            int n4 = n;
            int n5 = n2;
            for (int i = 0; i < n3; ++i) {
                array2[n5++] = ((array[n4++] & 0xFF) | (array[n4++] & 0xFF) << 8 | (array[n4++] & 0xFF) << 16 | (array[n4++] & 0xFF) << 24) * 4.656613E-10f;
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            int n4 = n;
            int n5 = n3;
            for (int i = 0; i < n2; ++i) {
                final int n6 = (int)(array[n4++] * 2.14748365E9f);
                array2[n5++] = (byte)n6;
                array2[n5++] = (byte)(n6 >>> 8);
                array2[n5++] = (byte)(n6 >>> 16);
                array2[n5++] = (byte)(n6 >>> 24);
            }
            return array2;
        }
    }
    
    private static class AudioFloatConversion32SB extends AudioFloatConverter
    {
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            int n4 = n;
            int n5 = n2;
            for (int i = 0; i < n3; ++i) {
                array2[n5++] = ((array[n4++] & 0xFF) << 24 | (array[n4++] & 0xFF) << 16 | (array[n4++] & 0xFF) << 8 | (array[n4++] & 0xFF)) * 4.656613E-10f;
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            int n4 = n;
            int n5 = n3;
            for (int i = 0; i < n2; ++i) {
                final int n6 = (int)(array[n4++] * 2.14748365E9f);
                array2[n5++] = (byte)(n6 >>> 24);
                array2[n5++] = (byte)(n6 >>> 16);
                array2[n5++] = (byte)(n6 >>> 8);
                array2[n5++] = (byte)n6;
            }
            return array2;
        }
    }
    
    private static class AudioFloatConversion32UL extends AudioFloatConverter
    {
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            int n4 = n;
            int n5 = n2;
            for (int i = 0; i < n3; ++i) {
                array2[n5++] = (((array[n4++] & 0xFF) | (array[n4++] & 0xFF) << 8 | (array[n4++] & 0xFF) << 16 | (array[n4++] & 0xFF) << 24) - Integer.MAX_VALUE) * 4.656613E-10f;
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            int n4 = n;
            int n5 = n3;
            for (int i = 0; i < n2; ++i) {
                final int n6 = (int)(array[n4++] * 2.14748365E9f) + Integer.MAX_VALUE;
                array2[n5++] = (byte)n6;
                array2[n5++] = (byte)(n6 >>> 8);
                array2[n5++] = (byte)(n6 >>> 16);
                array2[n5++] = (byte)(n6 >>> 24);
            }
            return array2;
        }
    }
    
    private static class AudioFloatConversion32UB extends AudioFloatConverter
    {
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            int n4 = n;
            int n5 = n2;
            for (int i = 0; i < n3; ++i) {
                array2[n5++] = (((array[n4++] & 0xFF) << 24 | (array[n4++] & 0xFF) << 16 | (array[n4++] & 0xFF) << 8 | (array[n4++] & 0xFF)) - Integer.MAX_VALUE) * 4.656613E-10f;
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            int n4 = n;
            int n5 = n3;
            for (int i = 0; i < n2; ++i) {
                final int n6 = (int)(array[n4++] * 2.14748365E9f) + Integer.MAX_VALUE;
                array2[n5++] = (byte)(n6 >>> 24);
                array2[n5++] = (byte)(n6 >>> 16);
                array2[n5++] = (byte)(n6 >>> 8);
                array2[n5++] = (byte)n6;
            }
            return array2;
        }
    }
    
    private static class AudioFloatConversion32xSL extends AudioFloatConverter
    {
        final int xbytes;
        
        AudioFloatConversion32xSL(final int xbytes) {
            this.xbytes = xbytes;
        }
        
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            int n4 = n;
            int n5 = n2;
            for (int i = 0; i < n3; ++i) {
                n4 += this.xbytes;
                array2[n5++] = ((array[n4++] & 0xFF) | (array[n4++] & 0xFF) << 8 | (array[n4++] & 0xFF) << 16 | (array[n4++] & 0xFF) << 24) * 4.656613E-10f;
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            int n4 = n;
            int n5 = n3;
            for (int i = 0; i < n2; ++i) {
                final int n6 = (int)(array[n4++] * 2.14748365E9f);
                for (int j = 0; j < this.xbytes; ++j) {
                    array2[n5++] = 0;
                }
                array2[n5++] = (byte)n6;
                array2[n5++] = (byte)(n6 >>> 8);
                array2[n5++] = (byte)(n6 >>> 16);
                array2[n5++] = (byte)(n6 >>> 24);
            }
            return array2;
        }
    }
    
    private static class AudioFloatConversion32xSB extends AudioFloatConverter
    {
        final int xbytes;
        
        AudioFloatConversion32xSB(final int xbytes) {
            this.xbytes = xbytes;
        }
        
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            int n4 = n;
            int n5 = n2;
            for (int i = 0; i < n3; ++i) {
                final int n6 = (array[n4++] & 0xFF) << 24 | (array[n4++] & 0xFF) << 16 | (array[n4++] & 0xFF) << 8 | (array[n4++] & 0xFF);
                n4 += this.xbytes;
                array2[n5++] = n6 * 4.656613E-10f;
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            int n4 = n;
            int n5 = n3;
            for (int i = 0; i < n2; ++i) {
                final int n6 = (int)(array[n4++] * 2.14748365E9f);
                array2[n5++] = (byte)(n6 >>> 24);
                array2[n5++] = (byte)(n6 >>> 16);
                array2[n5++] = (byte)(n6 >>> 8);
                array2[n5++] = (byte)n6;
                for (int j = 0; j < this.xbytes; ++j) {
                    array2[n5++] = 0;
                }
            }
            return array2;
        }
    }
    
    private static class AudioFloatConversion32xUL extends AudioFloatConverter
    {
        final int xbytes;
        
        AudioFloatConversion32xUL(final int xbytes) {
            this.xbytes = xbytes;
        }
        
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            int n4 = n;
            int n5 = n2;
            for (int i = 0; i < n3; ++i) {
                n4 += this.xbytes;
                array2[n5++] = (((array[n4++] & 0xFF) | (array[n4++] & 0xFF) << 8 | (array[n4++] & 0xFF) << 16 | (array[n4++] & 0xFF) << 24) - Integer.MAX_VALUE) * 4.656613E-10f;
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            int n4 = n;
            int n5 = n3;
            for (int i = 0; i < n2; ++i) {
                final int n6 = (int)(array[n4++] * 2.14748365E9f) + Integer.MAX_VALUE;
                for (int j = 0; j < this.xbytes; ++j) {
                    array2[n5++] = 0;
                }
                array2[n5++] = (byte)n6;
                array2[n5++] = (byte)(n6 >>> 8);
                array2[n5++] = (byte)(n6 >>> 16);
                array2[n5++] = (byte)(n6 >>> 24);
            }
            return array2;
        }
    }
    
    private static class AudioFloatConversion32xUB extends AudioFloatConverter
    {
        final int xbytes;
        
        AudioFloatConversion32xUB(final int xbytes) {
            this.xbytes = xbytes;
        }
        
        @Override
        public float[] toFloatArray(final byte[] array, final int n, final float[] array2, final int n2, final int n3) {
            int n4 = n;
            int n5 = n2;
            for (int i = 0; i < n3; ++i) {
                final int n6 = (array[n4++] & 0xFF) << 24 | (array[n4++] & 0xFF) << 16 | (array[n4++] & 0xFF) << 8 | (array[n4++] & 0xFF);
                n4 += this.xbytes;
                array2[n5++] = (n6 - Integer.MAX_VALUE) * 4.656613E-10f;
            }
            return array2;
        }
        
        @Override
        public byte[] toByteArray(final float[] array, final int n, final int n2, final byte[] array2, final int n3) {
            int n4 = n;
            int n5 = n3;
            for (int i = 0; i < n2; ++i) {
                final int n6 = (int)(array[n4++] * 2.147483647E9) + Integer.MAX_VALUE;
                array2[n5++] = (byte)(n6 >>> 24);
                array2[n5++] = (byte)(n6 >>> 16);
                array2[n5++] = (byte)(n6 >>> 8);
                array2[n5++] = (byte)n6;
                for (int j = 0; j < this.xbytes; ++j) {
                    array2[n5++] = 0;
                }
            }
            return array2;
        }
    }
}
