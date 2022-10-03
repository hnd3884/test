package javax.swing.plaf.nimbus;

import java.lang.ref.SoftReference;
import sun.awt.AppContext;
import java.awt.image.BufferedImage;

abstract class Effect
{
    abstract EffectType getEffectType();
    
    abstract float getOpacity();
    
    abstract BufferedImage applyEffect(final BufferedImage p0, final BufferedImage p1, final int p2, final int p3);
    
    protected static ArrayCache getArrayCache() {
        ArrayCache arrayCache = (ArrayCache)AppContext.getAppContext().get(ArrayCache.class);
        if (arrayCache == null) {
            arrayCache = new ArrayCache();
            AppContext.getAppContext().put(ArrayCache.class, arrayCache);
        }
        return arrayCache;
    }
    
    enum EffectType
    {
        UNDER, 
        BLENDED, 
        OVER;
    }
    
    protected static class ArrayCache
    {
        private SoftReference<int[]> tmpIntArray;
        private SoftReference<byte[]> tmpByteArray1;
        private SoftReference<byte[]> tmpByteArray2;
        private SoftReference<byte[]> tmpByteArray3;
        
        protected ArrayCache() {
            this.tmpIntArray = null;
            this.tmpByteArray1 = null;
            this.tmpByteArray2 = null;
            this.tmpByteArray3 = null;
        }
        
        protected int[] getTmpIntArray(final int n) {
            int[] array;
            if (this.tmpIntArray == null || (array = this.tmpIntArray.get()) == null || array.length < n) {
                array = new int[n];
                this.tmpIntArray = new SoftReference<int[]>(array);
            }
            return array;
        }
        
        protected byte[] getTmpByteArray1(final int n) {
            byte[] array;
            if (this.tmpByteArray1 == null || (array = this.tmpByteArray1.get()) == null || array.length < n) {
                array = new byte[n];
                this.tmpByteArray1 = new SoftReference<byte[]>(array);
            }
            return array;
        }
        
        protected byte[] getTmpByteArray2(final int n) {
            byte[] array;
            if (this.tmpByteArray2 == null || (array = this.tmpByteArray2.get()) == null || array.length < n) {
                array = new byte[n];
                this.tmpByteArray2 = new SoftReference<byte[]>(array);
            }
            return array;
        }
        
        protected byte[] getTmpByteArray3(final int n) {
            byte[] array;
            if (this.tmpByteArray3 == null || (array = this.tmpByteArray3.get()) == null || array.length < n) {
                array = new byte[n];
                this.tmpByteArray3 = new SoftReference<byte[]>(array);
            }
            return array;
        }
    }
}
