package com.sun.media.sound;

import java.security.AccessController;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;
import javax.sound.midi.Receiver;
import javax.sound.sampled.Port;
import javax.sound.sampled.Clip;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.SourceDataLine;
import java.util.Collections;
import java.util.ArrayList;
import javax.sound.midi.spi.MidiFileReader;
import javax.sound.midi.spi.MidiFileWriter;
import javax.sound.midi.spi.SoundbankReader;
import javax.sound.midi.spi.MidiDeviceProvider;
import javax.sound.sampled.spi.AudioFileWriter;
import javax.sound.sampled.spi.AudioFileReader;
import javax.sound.sampled.spi.FormatConversionProvider;
import javax.sound.sampled.spi.MixerProvider;
import java.util.List;
import java.util.Properties;

public final class JDK13Services
{
    private static final String PROPERTIES_FILENAME = "sound.properties";
    private static Properties properties;
    
    private JDK13Services() {
    }
    
    public static List<?> getProviders(final Class<?> clazz) {
        List<Object> providers;
        if (!MixerProvider.class.equals(clazz) && !FormatConversionProvider.class.equals(clazz) && !AudioFileReader.class.equals(clazz) && !AudioFileWriter.class.equals(clazz) && !MidiDeviceProvider.class.equals(clazz) && !SoundbankReader.class.equals(clazz) && !MidiFileWriter.class.equals(clazz) && !MidiFileReader.class.equals(clazz)) {
            providers = new ArrayList<Object>(0);
        }
        else {
            providers = JSSecurityManager.getProviders(clazz);
        }
        return Collections.unmodifiableList((List<?>)providers);
    }
    
    public static synchronized String getDefaultProviderClassName(final Class clazz) {
        String substring = null;
        final String defaultProvider = getDefaultProvider(clazz);
        if (defaultProvider != null) {
            final int index = defaultProvider.indexOf(35);
            if (index != 0) {
                if (index > 0) {
                    substring = defaultProvider.substring(0, index);
                }
                else {
                    substring = defaultProvider;
                }
            }
        }
        return substring;
    }
    
    public static synchronized String getDefaultInstanceName(final Class clazz) {
        String substring = null;
        final String defaultProvider = getDefaultProvider(clazz);
        if (defaultProvider != null) {
            final int index = defaultProvider.indexOf(35);
            if (index >= 0 && index < defaultProvider.length() - 1) {
                substring = defaultProvider.substring(index + 1);
            }
        }
        return substring;
    }
    
    private static synchronized String getDefaultProvider(final Class clazz) {
        if (!SourceDataLine.class.equals(clazz) && !TargetDataLine.class.equals(clazz) && !Clip.class.equals(clazz) && !Port.class.equals(clazz) && !Receiver.class.equals(clazz) && !Transmitter.class.equals(clazz) && !Synthesizer.class.equals(clazz) && !Sequencer.class.equals(clazz)) {
            return null;
        }
        final String name = clazz.getName();
        Object property = AccessController.doPrivileged(() -> System.getProperty(s));
        if (property == null) {
            property = getProperties().getProperty(name);
        }
        if ("".equals(property)) {
            property = null;
        }
        return (String)property;
    }
    
    private static synchronized Properties getProperties() {
        if (JDK13Services.properties == null) {
            JSSecurityManager.loadProperties(JDK13Services.properties = new Properties(), "sound.properties");
        }
        return JDK13Services.properties;
    }
}
