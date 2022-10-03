package org.apache.poi.poifs.filesystem;

import org.apache.poi.util.POILogFactory;
import java.io.File;
import org.apache.poi.util.POILogger;

public class POIFSDocumentPath
{
    private static final POILogger log;
    private final String[] components;
    private int hashcode;
    
    public POIFSDocumentPath(final String[] components) throws IllegalArgumentException {
        if (components == null) {
            this.components = new String[0];
        }
        else {
            this.components = new String[components.length];
            for (int j = 0; j < components.length; ++j) {
                if (components[j] == null || components[j].length() == 0) {
                    throw new IllegalArgumentException("components cannot contain null or empty strings");
                }
                this.components[j] = components[j];
            }
        }
    }
    
    public POIFSDocumentPath() {
        this.components = new String[0];
    }
    
    public POIFSDocumentPath(final POIFSDocumentPath path, final String[] components) throws IllegalArgumentException {
        if (components == null) {
            this.components = new String[path.components.length];
        }
        else {
            this.components = new String[path.components.length + components.length];
        }
        System.arraycopy(path.components, 0, this.components, 0, path.components.length);
        if (components != null) {
            for (int j = 0; j < components.length; ++j) {
                if (components[j] == null) {
                    throw new IllegalArgumentException("components cannot contain null");
                }
                if (components[j].length() == 0) {
                    POIFSDocumentPath.log.log(5, "Directory under " + path + " has an empty name, not all OLE2 readers will handle this file correctly!");
                }
                this.components[j + path.components.length] = components[j];
            }
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean rval = false;
        if (o != null && o.getClass() == this.getClass()) {
            if (this == o) {
                rval = true;
            }
            else {
                final POIFSDocumentPath path = (POIFSDocumentPath)o;
                if (path.components.length == this.components.length) {
                    rval = true;
                    for (int j = 0; j < this.components.length; ++j) {
                        if (!path.components[j].equals(this.components[j])) {
                            rval = false;
                            break;
                        }
                    }
                }
            }
        }
        return rval;
    }
    
    @Override
    public int hashCode() {
        if (this.hashcode == 0) {
            this.hashcode = this.computeHashCode();
        }
        return this.hashcode;
    }
    
    private int computeHashCode() {
        int code = 0;
        for (int j = 0; j < this.components.length; ++j) {
            code += this.components[j].hashCode();
        }
        return code;
    }
    
    public int length() {
        return this.components.length;
    }
    
    public String getComponent(final int n) throws ArrayIndexOutOfBoundsException {
        return this.components[n];
    }
    
    public POIFSDocumentPath getParent() {
        final int length = this.components.length - 1;
        if (length < 0) {
            return null;
        }
        final String[] parentComponents = new String[length];
        System.arraycopy(this.components, 0, parentComponents, 0, length);
        return new POIFSDocumentPath(parentComponents);
    }
    
    public String getName() {
        if (this.components.length == 0) {
            return "";
        }
        return this.components[this.components.length - 1];
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        final int l = this.length();
        b.append(File.separatorChar);
        for (int i = 0; i < l; ++i) {
            b.append(this.getComponent(i));
            if (i < l - 1) {
                b.append(File.separatorChar);
            }
        }
        return b.toString();
    }
    
    static {
        log = POILogFactory.getLogger(POIFSDocumentPath.class);
    }
}
