package org.apache.commons.compress.archivers.zip;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.zip.ZipException;
import java.util.Map;

public class ExtraFieldUtils
{
    private static final int WORD = 4;
    private static final Map<ZipShort, Class<?>> implementations;
    static final ZipExtraField[] EMPTY_ZIP_EXTRA_FIELD_ARRAY;
    
    public static void register(final Class<?> c) {
        try {
            final ZipExtraField ze = (ZipExtraField)c.newInstance();
            ExtraFieldUtils.implementations.put(ze.getHeaderId(), c);
        }
        catch (final ClassCastException cc) {
            throw new RuntimeException(c + " doesn't implement ZipExtraField");
        }
        catch (final InstantiationException ie) {
            throw new RuntimeException(c + " is not a concrete class");
        }
        catch (final IllegalAccessException ie2) {
            throw new RuntimeException(c + "'s no-arg constructor is not public");
        }
    }
    
    public static ZipExtraField createExtraField(final ZipShort headerId) throws InstantiationException, IllegalAccessException {
        final ZipExtraField field = createExtraFieldNoDefault(headerId);
        if (field != null) {
            return field;
        }
        final UnrecognizedExtraField u = new UnrecognizedExtraField();
        u.setHeaderId(headerId);
        return u;
    }
    
    public static ZipExtraField createExtraFieldNoDefault(final ZipShort headerId) throws InstantiationException, IllegalAccessException {
        final Class<?> c = ExtraFieldUtils.implementations.get(headerId);
        if (c != null) {
            return (ZipExtraField)c.newInstance();
        }
        return null;
    }
    
    public static ZipExtraField[] parse(final byte[] data) throws ZipException {
        return parse(data, true, UnparseableExtraField.THROW);
    }
    
    public static ZipExtraField[] parse(final byte[] data, final boolean local) throws ZipException {
        return parse(data, local, UnparseableExtraField.THROW);
    }
    
    public static ZipExtraField[] parse(final byte[] data, final boolean local, final UnparseableExtraField onUnparseableData) throws ZipException {
        return parse(data, local, new ExtraFieldParsingBehavior() {
            @Override
            public ZipExtraField onUnparseableExtraField(final byte[] data, final int off, final int len, final boolean local, final int claimedLength) throws ZipException {
                return onUnparseableData.onUnparseableExtraField(data, off, len, local, claimedLength);
            }
            
            @Override
            public ZipExtraField createExtraField(final ZipShort headerId) throws ZipException, InstantiationException, IllegalAccessException {
                return ExtraFieldUtils.createExtraField(headerId);
            }
            
            @Override
            public ZipExtraField fill(final ZipExtraField field, final byte[] data, final int off, final int len, final boolean local) throws ZipException {
                return ExtraFieldUtils.fillExtraField(field, data, off, len, local);
            }
        });
    }
    
    public static ZipExtraField[] parse(final byte[] data, final boolean local, final ExtraFieldParsingBehavior parsingBehavior) throws ZipException {
        final List<ZipExtraField> v = new ArrayList<ZipExtraField>();
        int start = 0;
        final int dataLength = data.length;
        while (start <= dataLength - 4) {
            final ZipShort headerId = new ZipShort(data, start);
            final int length = new ZipShort(data, start + 2).getValue();
            if (start + 4 + length > dataLength) {
                final ZipExtraField field = parsingBehavior.onUnparseableExtraField(data, start, dataLength - start, local, length);
                if (field != null) {
                    v.add(field);
                    break;
                }
                break;
            }
            else {
                try {
                    final ZipExtraField ze = Objects.requireNonNull(parsingBehavior.createExtraField(headerId), "createExtraField must not return null");
                    v.add(Objects.requireNonNull(parsingBehavior.fill(ze, data, start + 4, length, local), "fill must not return null"));
                    start += length + 4;
                }
                catch (final InstantiationException | IllegalAccessException ie) {
                    throw (ZipException)new ZipException(ie.getMessage()).initCause(ie);
                }
            }
        }
        return v.toArray(ExtraFieldUtils.EMPTY_ZIP_EXTRA_FIELD_ARRAY);
    }
    
    public static byte[] mergeLocalFileDataData(final ZipExtraField[] data) {
        final int dataLength = data.length;
        final boolean lastIsUnparseableHolder = dataLength > 0 && data[dataLength - 1] instanceof UnparseableExtraFieldData;
        final int regularExtraFieldCount = lastIsUnparseableHolder ? (dataLength - 1) : dataLength;
        int sum = 4 * regularExtraFieldCount;
        for (final ZipExtraField element : data) {
            sum += element.getLocalFileDataLength().getValue();
        }
        final byte[] result = new byte[sum];
        int start = 0;
        for (int i = 0; i < regularExtraFieldCount; ++i) {
            System.arraycopy(data[i].getHeaderId().getBytes(), 0, result, start, 2);
            System.arraycopy(data[i].getLocalFileDataLength().getBytes(), 0, result, start + 2, 2);
            start += 4;
            final byte[] local = data[i].getLocalFileDataData();
            if (local != null) {
                System.arraycopy(local, 0, result, start, local.length);
                start += local.length;
            }
        }
        if (lastIsUnparseableHolder) {
            final byte[] local2 = data[dataLength - 1].getLocalFileDataData();
            if (local2 != null) {
                System.arraycopy(local2, 0, result, start, local2.length);
            }
        }
        return result;
    }
    
    public static byte[] mergeCentralDirectoryData(final ZipExtraField[] data) {
        final int dataLength = data.length;
        final boolean lastIsUnparseableHolder = dataLength > 0 && data[dataLength - 1] instanceof UnparseableExtraFieldData;
        final int regularExtraFieldCount = lastIsUnparseableHolder ? (dataLength - 1) : dataLength;
        int sum = 4 * regularExtraFieldCount;
        for (final ZipExtraField element : data) {
            sum += element.getCentralDirectoryLength().getValue();
        }
        final byte[] result = new byte[sum];
        int start = 0;
        for (int i = 0; i < regularExtraFieldCount; ++i) {
            System.arraycopy(data[i].getHeaderId().getBytes(), 0, result, start, 2);
            System.arraycopy(data[i].getCentralDirectoryLength().getBytes(), 0, result, start + 2, 2);
            start += 4;
            final byte[] central = data[i].getCentralDirectoryData();
            if (central != null) {
                System.arraycopy(central, 0, result, start, central.length);
                start += central.length;
            }
        }
        if (lastIsUnparseableHolder) {
            final byte[] central2 = data[dataLength - 1].getCentralDirectoryData();
            if (central2 != null) {
                System.arraycopy(central2, 0, result, start, central2.length);
            }
        }
        return result;
    }
    
    public static ZipExtraField fillExtraField(final ZipExtraField ze, final byte[] data, final int off, final int len, final boolean local) throws ZipException {
        try {
            if (local) {
                ze.parseFromLocalFileData(data, off, len);
            }
            else {
                ze.parseFromCentralDirectoryData(data, off, len);
            }
            return ze;
        }
        catch (final ArrayIndexOutOfBoundsException aiobe) {
            throw (ZipException)new ZipException("Failed to parse corrupt ZIP extra field of type " + Integer.toHexString(ze.getHeaderId().getValue())).initCause(aiobe);
        }
    }
    
    static {
        implementations = new ConcurrentHashMap<ZipShort, Class<?>>();
        register(AsiExtraField.class);
        register(X5455_ExtendedTimestamp.class);
        register(X7875_NewUnix.class);
        register(JarMarker.class);
        register(UnicodePathExtraField.class);
        register(UnicodeCommentExtraField.class);
        register(Zip64ExtendedInformationExtraField.class);
        register(X000A_NTFS.class);
        register(X0014_X509Certificates.class);
        register(X0015_CertificateIdForFile.class);
        register(X0016_CertificateIdForCentralDirectory.class);
        register(X0017_StrongEncryptionHeader.class);
        register(X0019_EncryptionRecipientCertificateList.class);
        register(ResourceAlignmentExtraField.class);
        EMPTY_ZIP_EXTRA_FIELD_ARRAY = new ZipExtraField[0];
    }
    
    public static final class UnparseableExtraField implements UnparseableExtraFieldBehavior
    {
        public static final int THROW_KEY = 0;
        public static final int SKIP_KEY = 1;
        public static final int READ_KEY = 2;
        public static final UnparseableExtraField THROW;
        public static final UnparseableExtraField SKIP;
        public static final UnparseableExtraField READ;
        private final int key;
        
        private UnparseableExtraField(final int k) {
            this.key = k;
        }
        
        public int getKey() {
            return this.key;
        }
        
        @Override
        public ZipExtraField onUnparseableExtraField(final byte[] data, final int off, final int len, final boolean local, final int claimedLength) throws ZipException {
            switch (this.key) {
                case 0: {
                    throw new ZipException("Bad extra field starting at " + off + ".  Block length of " + claimedLength + " bytes exceeds remaining data of " + (len - 4) + " bytes.");
                }
                case 2: {
                    final UnparseableExtraFieldData field = new UnparseableExtraFieldData();
                    if (local) {
                        field.parseFromLocalFileData(data, off, len);
                    }
                    else {
                        field.parseFromCentralDirectoryData(data, off, len);
                    }
                    return field;
                }
                case 1: {
                    return null;
                }
                default: {
                    throw new ZipException("Unknown UnparseableExtraField key: " + this.key);
                }
            }
        }
        
        static {
            THROW = new UnparseableExtraField(0);
            SKIP = new UnparseableExtraField(1);
            READ = new UnparseableExtraField(2);
        }
    }
}
