package java.awt.font;

import java.io.ObjectStreamException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

public final class TransformAttribute implements Serializable
{
    private AffineTransform transform;
    public static final TransformAttribute IDENTITY;
    static final long serialVersionUID = 3356247357827709530L;
    
    public TransformAttribute(final AffineTransform affineTransform) {
        if (affineTransform != null && !affineTransform.isIdentity()) {
            this.transform = new AffineTransform(affineTransform);
        }
    }
    
    public AffineTransform getTransform() {
        final AffineTransform transform = this.transform;
        return (transform == null) ? new AffineTransform() : new AffineTransform(transform);
    }
    
    public boolean isIdentity() {
        return this.transform == null;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws ClassNotFoundException, IOException {
        if (this.transform == null) {
            this.transform = new AffineTransform();
        }
        objectOutputStream.defaultWriteObject();
    }
    
    private Object readResolve() throws ObjectStreamException {
        if (this.transform == null || this.transform.isIdentity()) {
            return TransformAttribute.IDENTITY;
        }
        return this;
    }
    
    @Override
    public int hashCode() {
        return (this.transform == null) ? 0 : this.transform.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o != null) {
            try {
                final TransformAttribute transformAttribute = (TransformAttribute)o;
                if (this.transform == null) {
                    return transformAttribute.transform == null;
                }
                return this.transform.equals(transformAttribute.transform);
            }
            catch (final ClassCastException ex) {}
        }
        return false;
    }
    
    static {
        IDENTITY = new TransformAttribute(null);
    }
}
