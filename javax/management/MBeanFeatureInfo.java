package javax.management;

import java.io.StreamCorruptedException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Objects;
import java.io.Serializable;

public class MBeanFeatureInfo implements Serializable, DescriptorRead
{
    static final long serialVersionUID = 3952882688968447265L;
    protected String name;
    protected String description;
    private transient Descriptor descriptor;
    
    public MBeanFeatureInfo(final String s, final String s2) {
        this(s, s2, null);
    }
    
    public MBeanFeatureInfo(final String name, final String description, final Descriptor descriptor) {
        this.name = name;
        this.description = description;
        this.descriptor = descriptor;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public Descriptor getDescriptor() {
        return (Descriptor)ImmutableDescriptor.nonNullDescriptor(this.descriptor).clone();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MBeanFeatureInfo)) {
            return false;
        }
        final MBeanFeatureInfo mBeanFeatureInfo = (MBeanFeatureInfo)o;
        return Objects.equals(mBeanFeatureInfo.getName(), this.getName()) && Objects.equals(mBeanFeatureInfo.getDescription(), this.getDescription()) && Objects.equals(mBeanFeatureInfo.getDescriptor(), this.getDescriptor());
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode() ^ this.getDescription().hashCode() ^ this.getDescriptor().hashCode();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.descriptor != null && this.descriptor.getClass() == ImmutableDescriptor.class) {
            objectOutputStream.write(1);
            final String[] fieldNames = this.descriptor.getFieldNames();
            objectOutputStream.writeObject(fieldNames);
            objectOutputStream.writeObject(this.descriptor.getFieldValues(fieldNames));
        }
        else {
            objectOutputStream.write(0);
            objectOutputStream.writeObject(this.descriptor);
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        switch (objectInputStream.read()) {
            case 1: {
                final String[] array = (String[])objectInputStream.readObject();
                final Object[] array2 = (Object[])objectInputStream.readObject();
                this.descriptor = ((array.length == 0) ? ImmutableDescriptor.EMPTY_DESCRIPTOR : new ImmutableDescriptor(array, array2));
                break;
            }
            case 0: {
                this.descriptor = (Descriptor)objectInputStream.readObject();
                if (this.descriptor == null) {
                    this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
                    break;
                }
                break;
            }
            case -1: {
                this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
                break;
            }
            default: {
                throw new StreamCorruptedException("Got unexpected byte.");
            }
        }
    }
}
