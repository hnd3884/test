package com.sun.corba.se.impl.ior;

import java.io.ObjectOutputStream;
import java.io.IOException;
import sun.corba.SharedSecrets;
import java.io.ObjectInputStream;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import org.omg.CORBA.Object;

public class StubIORImpl
{
    private int hashCode;
    private byte[] typeData;
    private int[] profileTags;
    private byte[][] profileData;
    
    public StubIORImpl() {
        this.hashCode = 0;
        this.typeData = null;
        this.profileTags = null;
        this.profileData = null;
    }
    
    public String getRepositoryId() {
        if (this.typeData == null) {
            return null;
        }
        return new String(this.typeData);
    }
    
    public StubIORImpl(final org.omg.CORBA.Object object) {
        final OutputStream create_output_stream = StubAdapter.getORB(object).create_output_stream();
        create_output_stream.write_Object(object);
        final InputStream create_input_stream = create_output_stream.create_input_stream();
        final int read_long = create_input_stream.read_long();
        create_input_stream.read_octet_array(this.typeData = new byte[read_long], 0, read_long);
        final int read_long2 = create_input_stream.read_long();
        this.profileTags = new int[read_long2];
        this.profileData = new byte[read_long2][];
        for (int i = 0; i < read_long2; ++i) {
            this.profileTags[i] = create_input_stream.read_long();
            create_input_stream.read_octet_array(this.profileData[i] = new byte[create_input_stream.read_long()], 0, this.profileData[i].length);
        }
    }
    
    public Delegate getDelegate(final ORB orb) {
        final OutputStream create_output_stream = orb.create_output_stream();
        create_output_stream.write_long(this.typeData.length);
        create_output_stream.write_octet_array(this.typeData, 0, this.typeData.length);
        create_output_stream.write_long(this.profileTags.length);
        for (int i = 0; i < this.profileTags.length; ++i) {
            create_output_stream.write_long(this.profileTags[i]);
            create_output_stream.write_long(this.profileData[i].length);
            create_output_stream.write_octet_array(this.profileData[i], 0, this.profileData[i].length);
        }
        return StubAdapter.getDelegate(create_output_stream.create_input_stream().read_Object());
    }
    
    public void doRead(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final int int1 = objectInputStream.readInt();
        SharedSecrets.getJavaOISAccess().checkArray(objectInputStream, byte[].class, int1);
        objectInputStream.readFully(this.typeData = new byte[int1]);
        final int int2 = objectInputStream.readInt();
        SharedSecrets.getJavaOISAccess().checkArray(objectInputStream, int[].class, int2);
        SharedSecrets.getJavaOISAccess().checkArray(objectInputStream, byte[].class, int2);
        this.profileTags = new int[int2];
        this.profileData = new byte[int2][];
        for (int i = 0; i < int2; ++i) {
            this.profileTags[i] = objectInputStream.readInt();
            final int int3 = objectInputStream.readInt();
            SharedSecrets.getJavaOISAccess().checkArray(objectInputStream, byte[].class, int3);
            objectInputStream.readFully(this.profileData[i] = new byte[int3]);
        }
    }
    
    public void doWrite(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(this.typeData.length);
        objectOutputStream.write(this.typeData);
        objectOutputStream.writeInt(this.profileTags.length);
        for (int i = 0; i < this.profileTags.length; ++i) {
            objectOutputStream.writeInt(this.profileTags[i]);
            objectOutputStream.writeInt(this.profileData[i].length);
            objectOutputStream.write(this.profileData[i]);
        }
    }
    
    @Override
    public synchronized int hashCode() {
        if (this.hashCode == 0) {
            for (int i = 0; i < this.typeData.length; ++i) {
                this.hashCode = this.hashCode * 37 + this.typeData[i];
            }
            for (int j = 0; j < this.profileTags.length; ++j) {
                this.hashCode = this.hashCode * 37 + this.profileTags[j];
                for (int k = 0; k < this.profileData[j].length; ++k) {
                    this.hashCode = this.hashCode * 37 + this.profileData[j][k];
                }
            }
        }
        return this.hashCode;
    }
    
    private boolean equalArrays(final int[] array, final int[] array2) {
        if (array.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
    
    private boolean equalArrays(final byte[] array, final byte[] array2) {
        if (array.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
    
    private boolean equalArrays(final byte[][] array, final byte[][] array2) {
        if (array.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (!this.equalArrays(array[i], array2[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StubIORImpl)) {
            return false;
        }
        final StubIORImpl stubIORImpl = (StubIORImpl)o;
        return stubIORImpl.hashCode() == this.hashCode() && this.equalArrays(this.typeData, stubIORImpl.typeData) && this.equalArrays(this.profileTags, stubIORImpl.profileTags) && this.equalArrays(this.profileData, stubIORImpl.profileData);
    }
    
    private void appendByteArray(final StringBuffer sb, final byte[] array) {
        for (int i = 0; i < array.length; ++i) {
            sb.append(Integer.toHexString(array[i]));
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("SimpleIORImpl[");
        sb.append(new String(this.typeData));
        for (int i = 0; i < this.profileTags.length; ++i) {
            sb.append(",(");
            sb.append(this.profileTags[i]);
            sb.append(")");
            this.appendByteArray(sb, this.profileData[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}
