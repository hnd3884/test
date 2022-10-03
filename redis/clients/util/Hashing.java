package redis.clients.util;

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

public interface Hashing
{
    public static final Hashing MURMUR_HASH = new MurmurHash();
    public static final ThreadLocal<MessageDigest> md5Holder = new ThreadLocal<MessageDigest>();
    public static final Hashing MD5 = new Hashing() {
        @Override
        public long hash(final String key) {
            return this.hash(SafeEncoder.encode(key));
        }
        
        @Override
        public long hash(final byte[] key) {
            try {
                if (Hashing$1.md5Holder.get() == null) {
                    Hashing$1.md5Holder.set(MessageDigest.getInstance("MD5"));
                }
            }
            catch (final NoSuchAlgorithmException e) {
                throw new IllegalStateException("++++ no md5 algorythm found");
            }
            final MessageDigest md5 = Hashing$1.md5Holder.get();
            md5.reset();
            md5.update(key);
            final byte[] bKey = md5.digest();
            final long res = (long)(bKey[3] & 0xFF) << 24 | (long)(bKey[2] & 0xFF) << 16 | (long)(bKey[1] & 0xFF) << 8 | (long)(bKey[0] & 0xFF);
            return res;
        }
    };
    
    long hash(final String p0);
    
    long hash(final byte[] p0);
}
