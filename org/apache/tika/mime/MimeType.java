package org.apache.tika.mime;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.net.URI;
import java.util.List;
import java.io.Serializable;

public final class MimeType implements Comparable<MimeType>, Serializable
{
    private static final long serialVersionUID = 4357830439860729201L;
    private final MediaType type;
    private final int minLength = 0;
    private String acronym;
    private String uti;
    private List<URI> links;
    private String description;
    private List<Magic> magics;
    private List<RootXML> rootXML;
    private List<String> extensions;
    private boolean isInterpreted;
    
    MimeType(final MediaType type) {
        this.acronym = "";
        this.uti = "";
        this.links = Collections.emptyList();
        this.description = "";
        this.magics = null;
        this.rootXML = null;
        this.extensions = null;
        this.isInterpreted = false;
        if (type == null) {
            throw new IllegalArgumentException("Media type name is missing");
        }
        this.type = type;
    }
    
    public static boolean isValid(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name is missing");
        }
        boolean slash = false;
        for (int i = 0; i < name.length(); ++i) {
            final char ch = name.charAt(i);
            if (ch <= ' ' || ch >= '\u007f' || ch == '(' || ch == ')' || ch == '<' || ch == '>' || ch == '@' || ch == ',' || ch == ';' || ch == ':' || ch == '\\' || ch == '\"' || ch == '[' || ch == ']' || ch == '?' || ch == '=') {
                return false;
            }
            if (ch == '/') {
                if (slash || i == 0 || i + 1 == name.length()) {
                    return false;
                }
                slash = true;
            }
        }
        return slash;
    }
    
    public MediaType getType() {
        return this.type;
    }
    
    public String getName() {
        return this.type.toString();
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        if (description == null) {
            throw new IllegalArgumentException("Description is missing");
        }
        this.description = description;
    }
    
    public String getAcronym() {
        return this.acronym;
    }
    
    void setAcronym(final String v) {
        if (v == null) {
            throw new IllegalArgumentException("Acronym is missing");
        }
        this.acronym = v;
    }
    
    public String getUniformTypeIdentifier() {
        return this.uti;
    }
    
    void setUniformTypeIdentifier(final String v) {
        if (v == null) {
            throw new IllegalArgumentException("Uniform Type Identifier is missing");
        }
        this.uti = v;
    }
    
    public List<URI> getLinks() {
        return this.links;
    }
    
    void addLink(final URI link) {
        if (link == null) {
            throw new IllegalArgumentException("Missing Link");
        }
        final List<URI> copy = new ArrayList<URI>(this.links.size() + 1);
        copy.addAll(this.links);
        copy.add(link);
        this.links = Collections.unmodifiableList((List<? extends URI>)copy);
    }
    
    void addRootXML(final String namespaceURI, final String localName) {
        if (this.rootXML == null) {
            this.rootXML = new ArrayList<RootXML>();
        }
        this.rootXML.add(new RootXML(this, namespaceURI, localName));
    }
    
    boolean matchesXML(final String namespaceURI, final String localName) {
        if (this.rootXML != null) {
            for (final RootXML xml : this.rootXML) {
                if (xml.matches(namespaceURI, localName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean hasRootXML() {
        return this.rootXML != null;
    }
    
    List<Magic> getMagics() {
        if (this.magics != null) {
            return this.magics;
        }
        return Collections.emptyList();
    }
    
    void addMagic(final Magic magic) {
        if (magic == null) {
            return;
        }
        if (this.magics == null) {
            this.magics = new ArrayList<Magic>();
        }
        this.magics.add(magic);
    }
    
    int getMinLength() {
        return 0;
    }
    
    public boolean hasMagic() {
        return this.magics != null;
    }
    
    public boolean matchesMagic(final byte[] data) {
        for (int i = 0; this.magics != null && i < this.magics.size(); ++i) {
            final Magic magic = this.magics.get(i);
            if (magic.eval(data)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean matches(final byte[] data) {
        return this.matchesMagic(data);
    }
    
    boolean isInterpreted() {
        return this.isInterpreted;
    }
    
    void setInterpreted(final boolean interpreted) {
        this.isInterpreted = interpreted;
    }
    
    @Override
    public int compareTo(final MimeType mime) {
        return this.type.compareTo(mime.type);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof MimeType) {
            final MimeType that = (MimeType)o;
            return this.type.equals(that.type);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.type.hashCode();
    }
    
    @Override
    public String toString() {
        return this.type.toString();
    }
    
    public String getExtension() {
        if (this.extensions == null) {
            return "";
        }
        return this.extensions.get(0);
    }
    
    public boolean hasExtension() {
        return this.extensions != null;
    }
    
    public List<String> getExtensions() {
        if (this.extensions != null) {
            return Collections.unmodifiableList((List<? extends String>)this.extensions);
        }
        return Collections.emptyList();
    }
    
    void addExtension(final String extension) {
        if (this.extensions == null) {
            this.extensions = Collections.singletonList(extension);
        }
        else if (this.extensions.size() == 1) {
            this.extensions = new ArrayList<String>(this.extensions);
        }
        if (!this.extensions.contains(extension)) {
            this.extensions.add(extension);
        }
    }
    
    static class RootXML implements Serializable
    {
        private static final long serialVersionUID = 5140496601491000730L;
        private MimeType type;
        private String namespaceURI;
        private String localName;
        
        RootXML(final MimeType type, final String namespaceURI, final String localName) {
            this.type = null;
            this.namespaceURI = null;
            this.localName = null;
            if (this.isEmpty(namespaceURI) && this.isEmpty(localName)) {
                throw new IllegalArgumentException("Both namespaceURI and localName cannot be empty");
            }
            this.type = type;
            this.namespaceURI = namespaceURI;
            this.localName = localName;
        }
        
        boolean matches(final String namespaceURI, final String localName) {
            if (!this.isEmpty(this.namespaceURI)) {
                if (!this.namespaceURI.equals(namespaceURI)) {
                    return false;
                }
            }
            else if (!this.isEmpty(namespaceURI)) {
                return false;
            }
            if (!this.isEmpty(this.localName)) {
                return this.localName.equals(localName);
            }
            return this.isEmpty(localName);
        }
        
        private boolean isEmpty(final String str) {
            return str == null || str.equals("");
        }
        
        MimeType getType() {
            return this.type;
        }
        
        String getNameSpaceURI() {
            return this.namespaceURI;
        }
        
        String getLocalName() {
            return this.localName;
        }
        
        @Override
        public String toString() {
            return this.type + ", " + this.namespaceURI + ", " + this.localName;
        }
    }
}
