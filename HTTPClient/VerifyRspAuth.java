package HTTPClient;

import java.util.Vector;
import java.io.IOException;

class VerifyRspAuth implements HashVerifier, GlobalConstants
{
    private String uri;
    private String HA1;
    private String alg;
    private String nonce;
    private String cnonce;
    private String nc;
    private String hdr;
    private RoResponse resp;
    
    public VerifyRspAuth(final String uri, final String HA1, final String alg, final String nonce, final String cnonce, final String nc, final String hdr, final RoResponse resp) {
        this.uri = uri;
        this.HA1 = HA1;
        this.alg = alg;
        this.nonce = nonce;
        this.cnonce = cnonce;
        this.nc = nc;
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
        HttpHeaderElement elem = Util.getElement(pai, "qop");
        final String qop;
        if (elem == null || (qop = elem.getValue()) == null || (!qop.equalsIgnoreCase("auth") && !qop.equalsIgnoreCase("auth-int"))) {
            return;
        }
        elem = Util.getElement(pai, "rspauth");
        if (elem == null || elem.getValue() == null) {
            return;
        }
        final byte[] digest = DefaultAuthHandler.unHex(elem.getValue());
        elem = Util.getElement(pai, "cnonce");
        if (elem != null && elem.getValue() != null && !elem.getValue().equals(this.cnonce)) {
            throw new IOException("Digest auth scheme: received wrong client-nonce '" + elem.getValue() + "' - expected '" + this.cnonce + "'");
        }
        elem = Util.getElement(pai, "nc");
        if (elem != null && elem.getValue() != null && !elem.getValue().equals(this.nc)) {
            throw new IOException("Digest auth scheme: received wrong nonce-count '" + elem.getValue() + "' - expected '" + this.nc + "'");
        }
        String A1;
        if (this.alg != null && this.alg.equalsIgnoreCase("MD5-sess")) {
            A1 = MD5.hexDigest(String.valueOf(this.HA1) + ":" + this.nonce + ":" + this.cnonce);
        }
        else {
            A1 = this.HA1;
        }
        String A2 = ":" + this.uri;
        if (qop.equalsIgnoreCase("auth-int")) {
            A2 = String.valueOf(A2) + ":" + MD5.toHex(hash);
        }
        A2 = MD5.hexDigest(A2);
        hash = MD5.digest(String.valueOf(A1) + ":" + this.nonce + ":" + this.nc + ":" + this.cnonce + ":" + qop + ":" + A2);
        for (int idx = 0; idx < hash.length; ++idx) {
            if (hash[idx] != digest[idx]) {
                throw new IOException("MD5-Digest mismatch: expected " + DefaultAuthHandler.hex(digest) + " but calculated " + DefaultAuthHandler.hex(hash));
            }
        }
        Log.write(8, "Auth:  rspauth from " + this.hdr + " successfully verified");
    }
}
