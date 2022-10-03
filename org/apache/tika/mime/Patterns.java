package org.apache.tika.mime;

import java.util.Iterator;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.Map;
import java.io.Serializable;

class Patterns implements Serializable
{
    private static final long serialVersionUID = -5778015347278111140L;
    private final MediaTypeRegistry registry;
    private final Map<String, MimeType> names;
    private final Map<String, MimeType> extensions;
    private final SortedMap<String, MimeType> globs;
    private int minExtensionLength;
    private int maxExtensionLength;
    
    public Patterns(final MediaTypeRegistry registry) {
        this.names = new HashMap<String, MimeType>();
        this.extensions = new HashMap<String, MimeType>();
        this.globs = new TreeMap<String, MimeType>(new LengthComparator());
        this.minExtensionLength = Integer.MAX_VALUE;
        this.maxExtensionLength = 0;
        this.registry = registry;
    }
    
    public void add(final String pattern, final MimeType type) throws MimeTypeException {
        this.add(pattern, false, type);
    }
    
    public void add(final String pattern, final boolean isJavaRegex, final MimeType type) throws MimeTypeException {
        if (pattern == null || type == null) {
            throw new IllegalArgumentException("Pattern and/or mime type is missing");
        }
        if (isJavaRegex) {
            this.addGlob(pattern, type);
        }
        else if (pattern.indexOf(42) == -1 && pattern.indexOf(63) == -1 && pattern.indexOf(91) == -1) {
            this.addName(pattern, type);
        }
        else if (pattern.startsWith("*") && pattern.indexOf(42, 1) == -1 && pattern.indexOf(63) == -1 && pattern.indexOf(91) == -1) {
            final String extension = pattern.substring(1);
            this.addExtension(extension, type);
            type.addExtension(extension);
        }
        else {
            this.addGlob(this.compile(pattern), type);
        }
    }
    
    private void addName(final String name, final MimeType type) throws MimeTypeException {
        final MimeType previous = this.names.get(name);
        if (previous == null || this.registry.isSpecializationOf(previous.getType(), type.getType())) {
            this.names.put(name, type);
        }
        else if (previous != type) {
            if (!this.registry.isSpecializationOf(type.getType(), previous.getType())) {
                throw new MimeTypeException("Conflicting name pattern: " + name);
            }
        }
    }
    
    private void addExtension(final String extension, final MimeType type) throws MimeTypeException {
        final MimeType previous = this.extensions.get(extension);
        if (previous == null || this.registry.isSpecializationOf(previous.getType(), type.getType())) {
            this.extensions.put(extension, type);
            final int length = extension.length();
            this.minExtensionLength = Math.min(this.minExtensionLength, length);
            this.maxExtensionLength = Math.max(this.maxExtensionLength, length);
        }
        else if (previous != type) {
            if (!this.registry.isSpecializationOf(type.getType(), previous.getType())) {
                throw new MimeTypeException("Conflicting extension pattern: " + extension);
            }
        }
    }
    
    private void addGlob(final String glob, final MimeType type) throws MimeTypeException {
        final MimeType previous = this.globs.get(glob);
        if (previous == null || this.registry.isSpecializationOf(previous.getType(), type.getType())) {
            this.globs.put(glob, type);
        }
        else if (previous != type) {
            if (!this.registry.isSpecializationOf(type.getType(), previous.getType())) {
                throw new MimeTypeException("Conflicting glob pattern: " + glob);
            }
        }
    }
    
    public MimeType matches(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name is missing");
        }
        if (this.names.containsKey(name)) {
            return this.names.get(name);
        }
        int n;
        for (int maxLength = n = Math.min(this.maxExtensionLength, name.length()); n >= this.minExtensionLength; --n) {
            final String extension = name.substring(name.length() - n);
            if (this.extensions.containsKey(extension)) {
                return this.extensions.get(extension);
            }
        }
        for (final Map.Entry<String, MimeType> entry : this.globs.entrySet()) {
            if (name.matches(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    private String compile(final String glob) {
        final StringBuilder pattern = new StringBuilder();
        pattern.append("\\A");
        for (int i = 0; i < glob.length(); ++i) {
            final char ch = glob.charAt(i);
            if (ch == '?') {
                pattern.append('.');
            }
            else if (ch == '*') {
                pattern.append(".*");
            }
            else if ("\\[]^.-$+(){}|".indexOf(ch) != -1) {
                pattern.append('\\');
                pattern.append(ch);
            }
            else {
                pattern.append(ch);
            }
        }
        pattern.append("\\z");
        return pattern.toString();
    }
    
    private static final class LengthComparator implements Comparator<String>, Serializable
    {
        private static final long serialVersionUID = 8468289702915532359L;
        
        @Override
        public int compare(final String a, final String b) {
            int diff = b.length() - a.length();
            if (diff == 0) {
                diff = a.compareTo(b);
            }
            return diff;
        }
    }
}
