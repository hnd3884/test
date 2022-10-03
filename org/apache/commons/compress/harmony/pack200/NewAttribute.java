package org.apache.commons.compress.harmony.pack200;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.Attribute;

public class NewAttribute extends Attribute
{
    private boolean contextClass;
    private boolean contextMethod;
    private boolean contextField;
    private boolean contextCode;
    private final String layout;
    private byte[] contents;
    private int codeOff;
    private Label[] labels;
    private ClassReader classReader;
    private char[] buf;
    
    public NewAttribute(final String type, final String layout, final int context) {
        super(type);
        this.contextClass = false;
        this.contextMethod = false;
        this.contextField = false;
        this.contextCode = false;
        this.layout = layout;
        this.addContext(context);
    }
    
    public NewAttribute(final ClassReader classReader, final String type, final String layout, final byte[] contents, final char[] buf, final int codeOff, final Label[] labels) {
        super(type);
        this.contextClass = false;
        this.contextMethod = false;
        this.contextField = false;
        this.contextCode = false;
        this.classReader = classReader;
        this.contents = contents;
        this.layout = layout;
        this.codeOff = codeOff;
        this.labels = labels;
        this.buf = buf;
    }
    
    public void addContext(final int context) {
        switch (context) {
            case 0: {
                this.contextClass = true;
                break;
            }
            case 2: {
                this.contextMethod = true;
                break;
            }
            case 1: {
                this.contextField = true;
                break;
            }
            case 3: {
                this.contextCode = true;
                break;
            }
        }
    }
    
    public boolean isContextClass() {
        return this.contextClass;
    }
    
    public boolean isContextMethod() {
        return this.contextMethod;
    }
    
    public boolean isContextField() {
        return this.contextField;
    }
    
    public boolean isContextCode() {
        return this.contextCode;
    }
    
    public String getLayout() {
        return this.layout;
    }
    
    public boolean isUnknown() {
        return false;
    }
    
    public boolean isCodeAttribute() {
        return this.codeOff != -1;
    }
    
    protected Attribute read(final ClassReader cr, final int off, final int len, final char[] buf, final int codeOff, final Label[] labels) {
        final byte[] attributeContents = new byte[len];
        System.arraycopy(cr.b, off, attributeContents, 0, len);
        return new NewAttribute(cr, this.type, this.layout, attributeContents, buf, codeOff, labels);
    }
    
    public boolean isUnknown(final int context) {
        switch (context) {
            case 0: {
                return !this.contextClass;
            }
            case 2: {
                return !this.contextMethod;
            }
            case 1: {
                return !this.contextField;
            }
            case 3: {
                return !this.contextCode;
            }
            default: {
                return false;
            }
        }
    }
    
    public String readUTF8(final int index) {
        return this.classReader.readUTF8(index, this.buf);
    }
    
    public String readClass(final int index) {
        return this.classReader.readClass(index, this.buf);
    }
    
    public Object readConst(final int index) {
        return this.classReader.readConst(index, this.buf);
    }
    
    public byte[] getBytes() {
        return this.contents;
    }
    
    public Label getLabel(final int index) {
        return this.labels[index];
    }
    
    public static class ErrorAttribute extends NewAttribute
    {
        public ErrorAttribute(final String type, final int context) {
            super(type, "", context);
        }
        
        @Override
        protected Attribute read(final ClassReader cr, final int off, final int len, final char[] buf, final int codeOff, final Label[] labels) {
            throw new Error("Attribute " + this.type + " was found");
        }
    }
    
    public static class StripAttribute extends NewAttribute
    {
        public StripAttribute(final String type, final int context) {
            super(type, "", context);
        }
        
        @Override
        protected Attribute read(final ClassReader cr, final int off, final int len, final char[] buf, final int codeOff, final Label[] labels) {
            return null;
        }
    }
    
    public static class PassAttribute extends NewAttribute
    {
        public PassAttribute(final String type, final int context) {
            super(type, "", context);
        }
        
        @Override
        protected Attribute read(final ClassReader cr, final int off, final int len, final char[] buf, final int codeOff, final Label[] labels) {
            throw new Segment.PassException();
        }
    }
}
