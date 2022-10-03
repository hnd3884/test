package HTTPClient;

import java.io.IOException;
import java.util.zip.InflaterInputStream;
import java.util.zip.GZIPInputStream;
import java.util.Vector;

class TransferEncodingModule implements HTTPClientModule
{
    public int requestHandler(final Request req, final Response[] resp) throws ModuleException {
        NVPair[] hdrs;
        int idx;
        for (hdrs = req.getHeaders(), idx = 0; idx < hdrs.length && !hdrs[idx].getName().equalsIgnoreCase("TE"); ++idx) {}
        Vector pte;
        if (idx == hdrs.length) {
            hdrs = Util.resizeArray(hdrs, idx + 1);
            req.setHeaders(hdrs);
            pte = new Vector();
        }
        else {
            try {
                pte = Util.parseHeader(hdrs[idx].getValue());
            }
            catch (final ParseException pe) {
                throw new ModuleException(pe.toString());
            }
        }
        final HttpHeaderElement all = Util.getElement(pte, "*");
        if (all != null) {
            NVPair[] params;
            for (params = all.getParams(), idx = 0; idx < params.length && !params[idx].getName().equalsIgnoreCase("q"); ++idx) {}
            if (idx == params.length) {
                return 0;
            }
            if (params[idx].getValue() == null || params[idx].getValue().length() == 0) {
                throw new ModuleException("Invalid q value for \"*\" in TE header: ");
            }
            try {
                if (Float.valueOf(params[idx].getValue()) > 0.0) {
                    return 0;
                }
            }
            catch (final NumberFormatException nfe) {
                throw new ModuleException("Invalid q value for \"*\" in TE header: " + nfe.getMessage());
            }
        }
        if (!pte.contains(new HttpHeaderElement("deflate"))) {
            pte.addElement(new HttpHeaderElement("deflate"));
        }
        if (!pte.contains(new HttpHeaderElement("gzip"))) {
            pte.addElement(new HttpHeaderElement("gzip"));
        }
        if (!pte.contains(new HttpHeaderElement("compress"))) {
            pte.addElement(new HttpHeaderElement("compress"));
        }
        hdrs[idx] = new NVPair("TE", Util.assembleHeader(pte));
        return 0;
    }
    
    public void responsePhase1Handler(final Response resp, final RoRequest req) {
    }
    
    public int responsePhase2Handler(final Response resp, final Request req) {
        return 10;
    }
    
    public void responsePhase3Handler(final Response resp, final RoRequest req) throws IOException, ModuleException {
        final String te = resp.getHeader("Transfer-Encoding");
        if (te == null || req.getMethod().equals("HEAD")) {
            return;
        }
        Vector pte = null;
    Label_0273:
        while (true) {
            Label_0265: {
                try {
                    pte = Util.parseHeader(te);
                    break Label_0265;
                }
                catch (final ParseException pe) {
                    throw new ModuleException(pe.toString());
                }
                final String encoding = pte.lastElement().getName();
                if (encoding.equalsIgnoreCase("gzip")) {
                    Log.write(32, "TEM:   pushing gzip-input-stream");
                    resp.inp_stream = new GZIPInputStream(resp.inp_stream);
                }
                else if (encoding.equalsIgnoreCase("deflate")) {
                    Log.write(32, "TEM:   pushing inflater-input-stream");
                    resp.inp_stream = new InflaterInputStream(resp.inp_stream);
                }
                else if (encoding.equalsIgnoreCase("compress")) {
                    Log.write(32, "TEM:   pushing uncompress-input-stream");
                    resp.inp_stream = new UncompressInputStream(resp.inp_stream);
                }
                else if (encoding.equalsIgnoreCase("chunked")) {
                    Log.write(32, "TEM:   pushing chunked-input-stream");
                    resp.inp_stream = new ChunkedInputStream(resp.inp_stream);
                }
                else {
                    if (!encoding.equalsIgnoreCase("identity")) {
                        Log.write(32, "TEM:   Unknown transfer encoding '" + encoding + "'");
                        break Label_0273;
                    }
                    Log.write(32, "TEM:   ignoring 'identity' token");
                }
                pte.removeElementAt(pte.size() - 1);
            }
            if (pte.size() > 0) {
                continue;
            }
            break;
        }
        if (pte.size() > 0) {
            resp.setHeader("Transfer-Encoding", Util.assembleHeader(pte));
        }
        else {
            resp.deleteHeader("Transfer-Encoding");
        }
    }
    
    public void trailerHandler(final Response resp, final RoRequest req) {
    }
}
