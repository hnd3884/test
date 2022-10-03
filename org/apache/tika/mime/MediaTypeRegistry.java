package org.apache.tika.mime;

import java.util.Iterator;
import java.util.Collection;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.io.Serializable;

public class MediaTypeRegistry implements Serializable
{
    private static final long serialVersionUID = 4710974869988895410L;
    private final Map<MediaType, MediaType> registry;
    private final Map<MediaType, MediaType> inheritance;
    
    public MediaTypeRegistry() {
        this.registry = new ConcurrentHashMap<MediaType, MediaType>();
        this.inheritance = new HashMap<MediaType, MediaType>();
    }
    
    public static MediaTypeRegistry getDefaultRegistry() {
        return MimeTypes.getDefaultMimeTypes().getMediaTypeRegistry();
    }
    
    public SortedSet<MediaType> getTypes() {
        return new TreeSet<MediaType>(this.registry.values());
    }
    
    public SortedSet<MediaType> getAliases(final MediaType type) {
        final SortedSet<MediaType> aliases = new TreeSet<MediaType>();
        for (final Map.Entry<MediaType, MediaType> entry : this.registry.entrySet()) {
            if (entry.getValue().equals(type) && !entry.getKey().equals(type)) {
                aliases.add(entry.getKey());
            }
        }
        return aliases;
    }
    
    public SortedSet<MediaType> getChildTypes(final MediaType type) {
        final SortedSet<MediaType> children = new TreeSet<MediaType>();
        for (final Map.Entry<MediaType, MediaType> entry : this.inheritance.entrySet()) {
            if (entry.getValue().equals(type)) {
                children.add(entry.getKey());
            }
        }
        return children;
    }
    
    public void addType(final MediaType type) {
        this.registry.put(type, type);
    }
    
    public void addAlias(final MediaType type, final MediaType alias) {
        this.registry.put(alias, type);
    }
    
    public void addSuperType(final MediaType type, final MediaType supertype) {
        this.inheritance.put(type, supertype);
    }
    
    public MediaType normalize(final MediaType type) {
        if (type == null) {
            return null;
        }
        final MediaType canonical = this.registry.get(type.getBaseType());
        if (canonical == null) {
            return type;
        }
        if (type.hasParameters()) {
            return new MediaType(canonical, type.getParameters());
        }
        return canonical;
    }
    
    protected boolean isSpecializationOf(final MimeType extension, final MimeType magicBits, final Map<MediaType, MimeType> types) {
        return this.isSpecializationOf(extension, magicBits, types, false);
    }
    
    private boolean isSpecializationOf(final MimeType extension, final MimeType magicBits, final Map<MediaType, MimeType> types, final boolean isRecursiveCall) {
        return extension != null && (extension == magicBits || this.isSpecializationOf(types.get(this.getSupertype(extension.getType())), magicBits, types, true));
    }
    
    public boolean isSpecializationOf(final MediaType a, final MediaType b) {
        return this.isInstanceOf(this.getSupertype(a), b);
    }
    
    public boolean isInstanceOf(final MediaType a, final MediaType b) {
        return a != null && (a.equals(b) || this.isSpecializationOf(a, b));
    }
    
    public boolean isInstanceOf(final String a, final MediaType b) {
        return this.isInstanceOf(this.normalize(MediaType.parse(a)), b);
    }
    
    public MediaType getSupertype(final MediaType type) {
        if (type == null) {
            return null;
        }
        if (this.inheritance.containsKey(type)) {
            return this.inheritance.get(type);
        }
        if (type.hasParameters()) {
            return type.getBaseType();
        }
        if (type.getSubtype().endsWith("+xml")) {
            return MediaType.APPLICATION_XML;
        }
        if (type.getSubtype().endsWith("+zip")) {
            return MediaType.APPLICATION_ZIP;
        }
        if ("text".equals(type.getType()) && !MediaType.TEXT_PLAIN.equals(type)) {
            return MediaType.TEXT_PLAIN;
        }
        if (type.getType().contains("empty") && !MediaType.EMPTY.equals(type)) {
            return MediaType.EMPTY;
        }
        if (!MediaType.OCTET_STREAM.equals(type)) {
            return MediaType.OCTET_STREAM;
        }
        return null;
    }
}
