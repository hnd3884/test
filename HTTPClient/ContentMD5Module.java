package HTTPClient;

import java.io.IOException;

class ContentMD5Module implements HTTPClientModule
{
    public int requestHandler(final Request req, final Response[] resp) {
        return 0;
    }
    
    public void responsePhase1Handler(final Response resp, final RoRequest req) {
    }
    
    public int responsePhase2Handler(final Response resp, final Request req) {
        return 10;
    }
    
    public void responsePhase3Handler(final Response resp, final RoRequest req) throws IOException, ModuleException {
        if (req.getMethod().equals("HEAD")) {
            return;
        }
        final String md5_digest = resp.getHeader("Content-MD5");
        final String trailer = resp.getHeader("Trailer");
        boolean md5_tok = false;
        try {
            if (trailer != null) {
                md5_tok = Util.hasToken(trailer, "Content-MD5");
            }
        }
        catch (final ParseException pe) {
            throw new ModuleException(pe.toString());
        }
        if ((md5_digest == null && !md5_tok) || resp.getHeader("Transfer-Encoding") != null) {
            return;
        }
        if (md5_digest != null) {
            Log.write(32, "CMD5M: Received digest: " + md5_digest + " - pushing md5-check-stream");
        }
        else {
            Log.write(32, "CMD5M: Expecting digest in trailer  - pushing md5-check-stream");
        }
        resp.inp_stream = new MD5InputStream(resp.inp_stream, new VerifyMD5(resp));
    }
    
    public void trailerHandler(final Response resp, final RoRequest req) {
    }
}
