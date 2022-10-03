package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.spi.ior.ObjectId;
import sun.corba.EncapsInputStreamFactory;
import com.sun.corba.se.spi.ior.ObjectKey;
import java.io.IOException;
import org.omg.CORBA.MARSHAL;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;

public class ObjectKeyFactoryImpl implements ObjectKeyFactory
{
    public static final int MAGIC_BASE = -1347695874;
    public static final int JAVAMAGIC_OLD = -1347695874;
    public static final int JAVAMAGIC_NEW = -1347695873;
    public static final int JAVAMAGIC_NEWER = -1347695872;
    public static final int MAX_MAGIC = -1347695872;
    public static final byte JDK1_3_1_01_PATCH_LEVEL = 1;
    private final ORB orb;
    private IORSystemException wrapper;
    private Handler fullKey;
    private Handler oktempOnly;
    
    public ObjectKeyFactoryImpl(final ORB orb) {
        this.fullKey = new Handler() {
            @Override
            public ObjectKeyTemplate handle(final int n, final int n2, final InputStream inputStream, final OctetSeqHolder octetSeqHolder) {
                ObjectKeyTemplate objectKeyTemplate = null;
                if (n2 >= 32 && n2 <= 63) {
                    if (n >= -1347695872) {
                        objectKeyTemplate = new POAObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, n, n2, inputStream, octetSeqHolder);
                    }
                    else {
                        objectKeyTemplate = new OldPOAObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, n, n2, inputStream, octetSeqHolder);
                    }
                }
                else if (n2 >= 0 && n2 < 32) {
                    if (n >= -1347695872) {
                        objectKeyTemplate = new JIDLObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, n, n2, inputStream, octetSeqHolder);
                    }
                    else {
                        objectKeyTemplate = new OldJIDLObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, n, n2, inputStream, octetSeqHolder);
                    }
                }
                return objectKeyTemplate;
            }
        };
        this.oktempOnly = new Handler() {
            @Override
            public ObjectKeyTemplate handle(final int n, final int n2, final InputStream inputStream, final OctetSeqHolder octetSeqHolder) {
                ObjectKeyTemplate objectKeyTemplate = null;
                if (n2 >= 32 && n2 <= 63) {
                    if (n >= -1347695872) {
                        objectKeyTemplate = new POAObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, n, n2, inputStream);
                    }
                    else {
                        objectKeyTemplate = new OldPOAObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, n, n2, inputStream);
                    }
                }
                else if (n2 >= 0 && n2 < 32) {
                    if (n >= -1347695872) {
                        objectKeyTemplate = new JIDLObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, n, n2, inputStream);
                    }
                    else {
                        objectKeyTemplate = new OldJIDLObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, n, n2, inputStream);
                    }
                }
                return objectKeyTemplate;
            }
        };
        this.orb = orb;
        this.wrapper = IORSystemException.get(orb, "oa.ior");
    }
    
    private boolean validMagic(final int n) {
        return n >= -1347695874 && n <= -1347695872;
    }
    
    private ObjectKeyTemplate create(final InputStream inputStream, final Handler handler, final OctetSeqHolder octetSeqHolder) {
        ObjectKeyTemplate handle = null;
        try {
            inputStream.mark(0);
            final int read_long = inputStream.read_long();
            if (this.validMagic(read_long)) {
                handle = handler.handle(read_long, inputStream.read_long(), inputStream, octetSeqHolder);
            }
        }
        catch (final MARSHAL marshal) {}
        if (handle == null) {
            try {
                inputStream.reset();
            }
            catch (final IOException ex) {}
        }
        return handle;
    }
    
    @Override
    public ObjectKey create(final byte[] array) {
        final OctetSeqHolder octetSeqHolder = new OctetSeqHolder();
        final EncapsInputStream encapsInputStream = EncapsInputStreamFactory.newEncapsInputStream(this.orb, array, array.length);
        ObjectKeyTemplate create = this.create(encapsInputStream, this.fullKey, octetSeqHolder);
        if (create == null) {
            create = new WireObjectKeyTemplate(encapsInputStream, octetSeqHolder);
        }
        return new ObjectKeyImpl(create, new ObjectIdImpl(octetSeqHolder.value));
    }
    
    @Override
    public ObjectKeyTemplate createTemplate(final InputStream inputStream) {
        ObjectKeyTemplate create = this.create(inputStream, this.oktempOnly, null);
        if (create == null) {
            create = new WireObjectKeyTemplate(this.orb);
        }
        return create;
    }
}
