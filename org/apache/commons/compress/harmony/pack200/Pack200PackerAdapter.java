package org.apache.commons.compress.harmony.pack200;

import java.util.jar.JarInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarFile;
import org.apache.commons.compress.java.util.jar.Pack200;

public class Pack200PackerAdapter extends Pack200Adapter implements Pack200.Packer
{
    private final PackingOptions options;
    
    public Pack200PackerAdapter() {
        this.options = new PackingOptions();
    }
    
    @Override
    public void pack(final JarFile file, final OutputStream out) throws IOException {
        if (file == null || out == null) {
            throw new IllegalArgumentException("Must specify both input and output streams");
        }
        this.completed(0.0);
        try {
            new Archive(file, out, this.options).pack();
        }
        catch (final Pack200Exception e) {
            throw new IOException("Failed to pack Jar:" + String.valueOf(e));
        }
        this.completed(1.0);
    }
    
    @Override
    public void pack(final JarInputStream in, final OutputStream out) throws IOException {
        if (in == null || out == null) {
            throw new IllegalArgumentException("Must specify both input and output streams");
        }
        this.completed(0.0);
        final PackingOptions options = new PackingOptions();
        try {
            new Archive(in, out, options).pack();
        }
        catch (final Pack200Exception e) {
            throw new IOException("Failed to pack Jar:" + String.valueOf(e));
        }
        this.completed(1.0);
        in.close();
    }
    
    @Override
    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
        super.firePropertyChange(propertyName, oldValue, newValue);
        if (newValue != null && !newValue.equals(oldValue)) {
            if (propertyName.startsWith("pack.class.attribute.")) {
                final String attributeName = propertyName.substring("pack.class.attribute.".length());
                this.options.addClassAttributeAction(attributeName, (String)newValue);
            }
            else if (propertyName.startsWith("pack.code.attribute.")) {
                final String attributeName = propertyName.substring("pack.code.attribute.".length());
                this.options.addCodeAttributeAction(attributeName, (String)newValue);
            }
            else if (propertyName.equals("pack.deflate.hint")) {
                this.options.setDeflateHint((String)newValue);
            }
            else if (propertyName.equals("pack.effort")) {
                this.options.setEffort(Integer.parseInt((String)newValue));
            }
            else if (propertyName.startsWith("pack.field.attribute.")) {
                final String attributeName = propertyName.substring("pack.field.attribute.".length());
                this.options.addFieldAttributeAction(attributeName, (String)newValue);
            }
            else if (propertyName.equals("pack.keep.file.order")) {
                this.options.setKeepFileOrder(Boolean.parseBoolean((String)newValue));
            }
            else if (propertyName.startsWith("pack.method.attribute.")) {
                final String attributeName = propertyName.substring("pack.method.attribute.".length());
                this.options.addMethodAttributeAction(attributeName, (String)newValue);
            }
            else if (propertyName.equals("pack.modification.time")) {
                this.options.setModificationTime((String)newValue);
            }
            else if (propertyName.startsWith("pack.pass.file.")) {
                if (oldValue != null && !oldValue.equals("")) {
                    this.options.removePassFile((String)oldValue);
                }
                this.options.addPassFile((String)newValue);
            }
            else if (propertyName.equals("pack.segment.limit")) {
                this.options.setSegmentLimit(Long.parseLong((String)newValue));
            }
            else if (propertyName.equals("pack.unknown.attribute")) {
                this.options.setUnknownAttributeAction((String)newValue);
            }
        }
    }
}
