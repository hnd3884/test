package HTTPClient;

import java.util.Vector;
import java.io.IOException;

class VerifyDigest implements HashVerifier, GlobalConstants
{
    private String HA1;
    private String nonce;
    private String method;
    private String uri;
    private String hdr;
    private RoResponse resp;
    
    public VerifyDigest(final String HA1, final String nonce, final String method, final String uri, final String hdr, final RoResponse resp) {
        this.HA1 = HA1;
        this.nonce = nonce;
        this.method = method;
        this.uri = uri;
        this.hdr = hdr;
        this.resp = resp;
    }
    
    public void verifyHash(byte[] hash, final long len) throws IOException {
        String auth_info = this.resp.getHeader(this.hdr);
        if (auth_info == null) {
            auth_info = this.resp.getTrailer(this.hdr);
        }
        if (auth_info == null) {
            return;
        }
        Vector pai;
        try {
            pai = Util.parseHeader(auth_info);
        }
        catch (final ParseException pe) {
            throw new IOException(pe.toString());
        }
        final HttpHeaderElement elem = Util.getElement(pai, "digest");
        if (elem == null || elem.getValue() == null) {
            return;
        }
        final byte[] digest = DefaultAuthHandler.unHex(elem.getValue());
        final String entity_info = MD5.hexDigest(String.valueOf(this.uri) + ":" + header_val("Content-Type", this.resp) + ":" + header_val("Content-Length", this.resp) + ":" + header_val("Content-Encoding", this.resp) + ":" + header_val("Last-Modified", this.resp) + ":" + header_val("Expires", this.resp));
        hash = MD5.digest(String.valueOf(this.HA1) + ":" + this.nonce + ":" + this.method + ":" + header_val("Date", this.resp) + ":" + entity_info + ":" + MD5.toHex(hash));
        for (int idx = 0; idx < hash.length; ++idx) {
            if (hash[idx] != digest[idx]) {
                throw new IOException("MD5-Digest mismatch: expected " + DefaultAuthHandler.hex(digest) + " but calculated " + DefaultAuthHandler.hex(hash));
            }
        }
        Log.write(8, "Auth:  digest from " + this.hdr + " successfully verified");
    }
    
    private static final String header_val(final String hdr_name, final RoResponse resp) throws IOException {
        final String hdr = resp.getHeader(hdr_name);
        final String tlr = resp.getTrailer(hdr_name);
        return (hdr != null) ? hdr : ((tlr != null) ? tlr : "");
    }
}
