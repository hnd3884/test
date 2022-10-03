package HTTPClient;

import java.io.IOException;

class VerifyMD5 implements HashVerifier
{
    RoResponse resp;
    
    public VerifyMD5(final RoResponse resp) {
        this.resp = resp;
    }
    
    public void verifyHash(final byte[] hash, final long len) throws IOException {
        String hdr;
        try {
            if ((hdr = this.resp.getHeader("Content-MD5")) == null) {
                hdr = this.resp.getTrailer("Content-MD5");
            }
        }
        catch (final IOException ex) {
            return;
        }
        if (hdr == null) {
            return;
        }
        final byte[] ContMD5 = Codecs.base64Decode(hdr.trim().getBytes("8859_1"));
        for (int idx = 0; idx < hash.length; ++idx) {
            if (hash[idx] != ContMD5[idx]) {
                throw new IOException("MD5-Digest mismatch: expected " + hex(ContMD5) + " but calculated " + hex(hash));
            }
        }
        Log.write(32, "CMD5M: hash successfully verified");
    }
    
    private static String hex(final byte[] buf) {
        final StringBuffer str = new StringBuffer(buf.length * 3);
        for (int idx = 0; idx < buf.length; ++idx) {
            str.append(Character.forDigit(buf[idx] >>> 4 & 0xF, 16));
            str.append(Character.forDigit(buf[idx] & 0xF, 16));
            str.append(':');
        }
        str.setLength(str.length() - 1);
        return str.toString();
    }
}
