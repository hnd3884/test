package com.sun.media.sound;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;

public final class Toolkit
{
    private Toolkit() {
    }
    
    static void getUnsigned8(final byte[] array, final int n, final int n2) {
        for (int i = n; i < n + n2; ++i) {
            final int n3 = i;
            array[n3] += 128;
        }
    }
    
    static void getByteSwapped(final byte[] array, final int n, final int n2) {
        for (int i = n; i < n + n2; i += 2) {
            final byte b = array[i];
            array[i] = array[i + 1];
            array[i + 1] = b;
        }
    }
    
    static float linearToDB(final float n) {
        return (float)(Math.log((n == 0.0) ? 1.0E-4 : ((double)n)) / Math.log(10.0) * 20.0);
    }
    
    static float dBToLinear(final float n) {
        return (float)Math.pow(10.0, n / 20.0);
    }
    
    static long align(final long n, final int n2) {
        if (n2 <= 1) {
            return n;
        }
        return n - n % n2;
    }
    
    static int align(final int n, final int n2) {
        if (n2 <= 1) {
            return n;
        }
        return n - n % n2;
    }
    
    static long millis2bytes(final AudioFormat audioFormat, final long n) {
        return align((long)(n * audioFormat.getFrameRate() / 1000.0f * audioFormat.getFrameSize()), audioFormat.getFrameSize());
    }
    
    static long bytes2millis(final AudioFormat audioFormat, final long n) {
        return (long)(n / audioFormat.getFrameRate() * 1000.0f / audioFormat.getFrameSize());
    }
    
    static long micros2bytes(final AudioFormat audioFormat, final long n) {
        return align((long)(n * audioFormat.getFrameRate() / 1000000.0f * audioFormat.getFrameSize()), audioFormat.getFrameSize());
    }
    
    static long bytes2micros(final AudioFormat audioFormat, final long n) {
        return (long)(n / audioFormat.getFrameRate() * 1000000.0f / audioFormat.getFrameSize());
    }
    
    static long micros2frames(final AudioFormat audioFormat, final long n) {
        return (long)(n * audioFormat.getFrameRate() / 1000000.0f);
    }
    
    static long frames2micros(final AudioFormat audioFormat, final long n) {
        return (long)(n / (double)audioFormat.getFrameRate() * 1000000.0);
    }
    
    static void isFullySpecifiedAudioFormat(final AudioFormat audioFormat) {
        if (audioFormat.getFrameSize() <= 0) {
            throw new IllegalArgumentException("invalid frame size: " + ((audioFormat.getFrameSize() == -1) ? "NOT_SPECIFIED" : String.valueOf(audioFormat.getFrameSize())));
        }
        if (!audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) && !audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED) && !audioFormat.getEncoding().equals(AudioFormat.Encoding.ULAW) && !audioFormat.getEncoding().equals(AudioFormat.Encoding.ALAW)) {
            return;
        }
        if (audioFormat.getFrameRate() <= 0.0f) {
            throw new IllegalArgumentException("invalid frame rate: " + ((audioFormat.getFrameRate() == -1.0f) ? "NOT_SPECIFIED" : String.valueOf(audioFormat.getFrameRate())));
        }
        if (audioFormat.getSampleRate() <= 0.0f) {
            throw new IllegalArgumentException("invalid sample rate: " + ((audioFormat.getSampleRate() == -1.0f) ? "NOT_SPECIFIED" : String.valueOf(audioFormat.getSampleRate())));
        }
        if (audioFormat.getSampleSizeInBits() <= 0) {
            throw new IllegalArgumentException("invalid sample size in bits: " + ((audioFormat.getSampleSizeInBits() == -1) ? "NOT_SPECIFIED" : String.valueOf(audioFormat.getSampleSizeInBits())));
        }
        if (audioFormat.getChannels() <= 0) {
            throw new IllegalArgumentException("invalid number of channels: " + ((audioFormat.getChannels() == -1) ? "NOT_SPECIFIED" : String.valueOf(audioFormat.getChannels())));
        }
    }
    
    static boolean isFullySpecifiedPCMFormat(final AudioFormat audioFormat) {
        return (audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) || audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) && audioFormat.getFrameRate() > 0.0f && audioFormat.getSampleRate() > 0.0f && audioFormat.getSampleSizeInBits() > 0 && audioFormat.getFrameSize() > 0 && audioFormat.getChannels() > 0;
    }
    
    public static AudioInputStream getPCMConvertedAudioInputStream(AudioInputStream audioInputStream) {
        final AudioFormat format = audioInputStream.getFormat();
        if (!format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) && !format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
            try {
                audioInputStream = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), 16, format.getChannels(), format.getChannels() * 2, format.getSampleRate(), Platform.isBigEndian()), audioInputStream);
            }
            catch (final Exception ex) {
                audioInputStream = null;
            }
        }
        return audioInputStream;
    }
}
