package org.apache.commons.text.lookup;

import java.nio.file.Files;
import java.nio.file.Paths;

final class FileStringLookup extends AbstractStringLookup
{
    static final AbstractStringLookup INSTANCE;
    
    private FileStringLookup() {
    }
    
    @Override
    public String lookup(final String key) {
        if (key == null) {
            return null;
        }
        final String[] keys = key.split(String.valueOf(':'));
        final int keyLen = keys.length;
        if (keyLen < 2) {
            throw IllegalArgumentExceptions.format("Bad file key format [%s], expected format is CharsetName:DocumentPath.", key);
        }
        final String charsetName = keys[0];
        final String fileName = this.substringAfter(key, ':');
        try {
            return new String(Files.readAllBytes(Paths.get(fileName, new String[0])), charsetName);
        }
        catch (final Exception e) {
            throw IllegalArgumentExceptions.format(e, "Error looking up file [%s] with charset [%s].", fileName, charsetName);
        }
    }
    
    static {
        INSTANCE = new FileStringLookup();
    }
}
