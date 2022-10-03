package sun.java2d.cmm.lcms;

import java.util.Arrays;
import java.util.HashMap;
import sun.java2d.cmm.Profile;

final class LCMSProfile extends Profile
{
    private final TagCache tagCache;
    private final Object disposerReferent;
    
    LCMSProfile(final long n, final Object disposerReferent) {
        super(n);
        this.disposerReferent = disposerReferent;
        this.tagCache = new TagCache(this);
    }
    
    final long getLcmsPtr() {
        return this.getNativePtr();
    }
    
    TagData getTag(final int n) {
        return this.tagCache.getTag(n);
    }
    
    void clearTagCache() {
        this.tagCache.clear();
    }
    
    static class TagCache
    {
        final LCMSProfile profile;
        private HashMap<Integer, TagData> tags;
        
        TagCache(final LCMSProfile profile) {
            this.profile = profile;
            this.tags = new HashMap<Integer, TagData>();
        }
        
        TagData getTag(final int n) {
            TagData tagData = this.tags.get(n);
            if (tagData == null) {
                final byte[] tagNative = LCMS.getTagNative(this.profile.getNativePtr(), n);
                if (tagNative != null) {
                    tagData = new TagData(n, tagNative);
                    this.tags.put(n, tagData);
                }
            }
            return tagData;
        }
        
        void clear() {
            this.tags.clear();
        }
    }
    
    static class TagData
    {
        private int signature;
        private byte[] data;
        
        TagData(final int signature, final byte[] data) {
            this.signature = signature;
            this.data = data;
        }
        
        int getSize() {
            return this.data.length;
        }
        
        byte[] getData() {
            return Arrays.copyOf(this.data, this.data.length);
        }
        
        void copyDataTo(final byte[] array) {
            System.arraycopy(this.data, 0, array, 0, this.data.length);
        }
        
        int getSignature() {
            return this.signature;
        }
    }
}
