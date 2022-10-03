package sun.instrument;

import java.security.ProtectionDomain;
import java.lang.instrument.ClassFileTransformer;

public class TransformerManager
{
    private TransformerInfo[] mTransformerList;
    private boolean mIsRetransformable;
    
    TransformerManager(final boolean mIsRetransformable) {
        this.mTransformerList = new TransformerInfo[0];
        this.mIsRetransformable = mIsRetransformable;
    }
    
    boolean isRetransformable() {
        return this.mIsRetransformable;
    }
    
    public synchronized void addTransformer(final ClassFileTransformer classFileTransformer) {
        final TransformerInfo[] mTransformerList = this.mTransformerList;
        final TransformerInfo[] mTransformerList2 = new TransformerInfo[mTransformerList.length + 1];
        System.arraycopy(mTransformerList, 0, mTransformerList2, 0, mTransformerList.length);
        mTransformerList2[mTransformerList.length] = new TransformerInfo(classFileTransformer);
        this.mTransformerList = mTransformerList2;
    }
    
    public synchronized boolean removeTransformer(final ClassFileTransformer classFileTransformer) {
        boolean b = false;
        final TransformerInfo[] mTransformerList = this.mTransformerList;
        final int length = mTransformerList.length;
        final int n = length - 1;
        int n2 = 0;
        for (int i = length - 1; i >= 0; --i) {
            if (mTransformerList[i].transformer() == classFileTransformer) {
                b = true;
                n2 = i;
                break;
            }
        }
        if (b) {
            final TransformerInfo[] mTransformerList2 = new TransformerInfo[n];
            if (n2 > 0) {
                System.arraycopy(mTransformerList, 0, mTransformerList2, 0, n2);
            }
            if (n2 < n) {
                System.arraycopy(mTransformerList, n2 + 1, mTransformerList2, n2, n - n2);
            }
            this.mTransformerList = mTransformerList2;
        }
        return b;
    }
    
    synchronized boolean includesTransformer(final ClassFileTransformer classFileTransformer) {
        final TransformerInfo[] mTransformerList = this.mTransformerList;
        for (int length = mTransformerList.length, i = 0; i < length; ++i) {
            if (mTransformerList[i].transformer() == classFileTransformer) {
                return true;
            }
        }
        return false;
    }
    
    private TransformerInfo[] getSnapshotTransformerList() {
        return this.mTransformerList;
    }
    
    public byte[] transform(final ClassLoader classLoader, final String s, final Class<?> clazz, final ProtectionDomain protectionDomain, final byte[] array) {
        boolean b = false;
        final TransformerInfo[] snapshotTransformerList = this.getSnapshotTransformerList();
        byte[] array2 = array;
        for (int i = 0; i < snapshotTransformerList.length; ++i) {
            final ClassFileTransformer transformer = snapshotTransformerList[i].transformer();
            byte[] transform = null;
            try {
                transform = transformer.transform(classLoader, s, clazz, protectionDomain, array2);
            }
            catch (final Throwable t) {}
            if (transform != null) {
                b = true;
                array2 = transform;
            }
        }
        byte[] array3;
        if (b) {
            array3 = array2;
        }
        else {
            array3 = null;
        }
        return array3;
    }
    
    int getTransformerCount() {
        return this.getSnapshotTransformerList().length;
    }
    
    boolean setNativeMethodPrefix(final ClassFileTransformer classFileTransformer, final String prefix) {
        final TransformerInfo[] snapshotTransformerList = this.getSnapshotTransformerList();
        for (int i = 0; i < snapshotTransformerList.length; ++i) {
            final TransformerInfo transformerInfo = snapshotTransformerList[i];
            if (transformerInfo.transformer() == classFileTransformer) {
                transformerInfo.setPrefix(prefix);
                return true;
            }
        }
        return false;
    }
    
    String[] getNativeMethodPrefixes() {
        final TransformerInfo[] snapshotTransformerList = this.getSnapshotTransformerList();
        final String[] array = new String[snapshotTransformerList.length];
        for (int i = 0; i < snapshotTransformerList.length; ++i) {
            array[i] = snapshotTransformerList[i].getPrefix();
        }
        return array;
    }
    
    private class TransformerInfo
    {
        final ClassFileTransformer mTransformer;
        String mPrefix;
        
        TransformerInfo(final ClassFileTransformer mTransformer) {
            this.mTransformer = mTransformer;
            this.mPrefix = null;
        }
        
        ClassFileTransformer transformer() {
            return this.mTransformer;
        }
        
        String getPrefix() {
            return this.mPrefix;
        }
        
        void setPrefix(final String mPrefix) {
            this.mPrefix = mPrefix;
        }
    }
}
