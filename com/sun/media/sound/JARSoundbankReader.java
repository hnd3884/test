package com.sun.media.sound;

import java.io.File;
import javax.sound.midi.InvalidMidiDataException;
import java.util.Iterator;
import sun.reflect.misc.ReflectUtil;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLClassLoader;
import java.util.ArrayList;
import javax.sound.midi.Soundbank;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import javax.sound.midi.spi.SoundbankReader;

public final class JARSoundbankReader extends SoundbankReader
{
    private static boolean isZIP(final URL url) {
        boolean b = false;
        try {
            final InputStream openStream = url.openStream();
            try {
                final byte[] array = new byte[4];
                b = (openStream.read(array) == 4);
                if (b) {
                    b = (array[0] == 80 && array[1] == 75 && array[2] == 3 && array[3] == 4);
                }
            }
            finally {
                openStream.close();
            }
        }
        catch (final IOException ex) {}
        return b;
    }
    
    @Override
    public Soundbank getSoundbank(final URL url) throws InvalidMidiDataException, IOException {
        if (!isZIP(url)) {
            return null;
        }
        final ArrayList list = new ArrayList();
        final URLClassLoader instance = URLClassLoader.newInstance(new URL[] { url });
        final InputStream resourceAsStream = instance.getResourceAsStream("META-INF/services/javax.sound.midi.Soundbank");
        if (resourceAsStream == null) {
            return null;
        }
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
            for (String s = bufferedReader.readLine(); s != null; s = bufferedReader.readLine()) {
                if (!s.startsWith("#")) {
                    try {
                        final Class<?> forName = Class.forName(s.trim(), false, instance);
                        if (Soundbank.class.isAssignableFrom(forName)) {
                            list.add(ReflectUtil.newInstance(forName));
                        }
                    }
                    catch (final ClassNotFoundException ex) {}
                    catch (final InstantiationException ex2) {}
                    catch (final IllegalAccessException ex3) {}
                }
            }
        }
        finally {
            resourceAsStream.close();
        }
        if (list.size() == 0) {
            return null;
        }
        if (list.size() == 1) {
            return (Soundbank)list.get(0);
        }
        final SimpleSoundbank simpleSoundbank = new SimpleSoundbank();
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            simpleSoundbank.addAllInstruments((Soundbank)iterator.next());
        }
        return simpleSoundbank;
    }
    
    @Override
    public Soundbank getSoundbank(final InputStream inputStream) throws InvalidMidiDataException, IOException {
        return null;
    }
    
    @Override
    public Soundbank getSoundbank(final File file) throws InvalidMidiDataException, IOException {
        return this.getSoundbank(file.toURI().toURL());
    }
}
