package com.adventnet.tools.update.installer;

import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.jar.JarFile;

public class JarChecker
{
    public String getTheMainAttributesValue(final String jarName, final String key) {
        String value = null;
        try {
            final JarFile jar = new JarFile(jarName);
            final Manifest mani = jar.getManifest();
            final Attributes mainAttr = mani.getMainAttributes();
            final Object[] obj = this.getTheAttributesKeys(mainAttr);
            for (int j = 0; j < obj.length; ++j) {
                if (obj[j].toString().equalsIgnoreCase(key)) {
                    value = (String)mainAttr.get(obj[j]);
                    return value;
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    
    private Object[] getTheAttributesValues(final Attributes attr) {
        final Object[] key = attr.keySet().toArray();
        final int length = key.length;
        final Object[] values = new Object[length];
        for (int j = 0; j < length; ++j) {
            values[j] = attr.get(key[j]);
        }
        return values;
    }
    
    private Object[] getTheAttributesKeys(final Attributes attr) {
        final Object[] key = attr.keySet().toArray();
        return key;
    }
    
    public String getTheSubAttributesValue(final String jarName, final String key) {
        String value = null;
        try {
            final JarFile jar = new JarFile(jarName);
            final Manifest mani = jar.getManifest();
            final Map map = mani.getEntries();
            final Object[] array = map.keySet().toArray();
            for (int i = 0; i < array.length; ++i) {
                final Attributes attr = map.get(array[i]);
                final Object[] obj = this.getTheAttributesKeys(attr);
                for (int j = 0; j < obj.length; ++j) {
                    if (obj[j].toString().equalsIgnoreCase(key)) {
                        value = (String)attr.get(obj[j]);
                        return value;
                    }
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
