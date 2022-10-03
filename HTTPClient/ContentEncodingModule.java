package HTTPClient;

import java.io.IOException;
import java.util.zip.InflaterInputStream;
import java.util.zip.Inflater;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.ByteArrayInputStream;
import java.util.zip.GZIPInputStream;
import java.util.Vector;

class ContentEncodingModule implements HTTPClientModule
{
    private static final byte[] dummy;
    
    @Override
    public int requestHandler(final Request req, final Response[] resp) throws ModuleException {
        NVPair[] hdrs;
        int idx;
        for (hdrs = req.getHeaders(), idx = 0; idx < hdrs.length && !hdrs[idx].getName().equalsIgnoreCase("Accept-Encoding"); ++idx) {}
        Vector pae;
        if (idx == hdrs.length) {
            hdrs = Util.resizeArray(hdrs, idx + 1);
            req.setHeaders(hdrs);
            pae = new Vector();
        }
        else {
            try {
                pae = Util.parseHeader(hdrs[idx].getValue());
            }
            catch (final ParseException pe) {
                throw new ModuleException(pe.toString());
            }
        }
        final HttpHeaderElement all = Util.getElement(pae, "*");
        if (all != null) {
            NVPair[] params;
            for (params = all.getParams(), idx = 0; idx < params.length && !params[idx].getName().equalsIgnoreCase("q"); ++idx) {}
            if (idx == params.length) {
                return 0;
            }
            if (params[idx].getValue() == null || params[idx].getValue().length() == 0) {
                throw new ModuleException("Invalid q value for \"*\" in Accept-Encoding header: ");
            }
            try {
                if (Float.valueOf(params[idx].getValue()) > 0.0) {
                    return 0;
                }
            }
            catch (final NumberFormatException nfe) {
                throw new ModuleException("Invalid q value for \"*\" in Accept-Encoding header: " + nfe.getMessage());
            }
        }
        if (!pae.contains(new HttpHeaderElement("deflate"))) {
            pae.addElement(new HttpHeaderElement("deflate"));
        }
        if (!pae.contains(new HttpHeaderElement("gzip"))) {
            pae.addElement(new HttpHeaderElement("gzip"));
        }
        if (!pae.contains(new HttpHeaderElement("x-gzip"))) {
            pae.addElement(new HttpHeaderElement("x-gzip"));
        }
        if (!pae.contains(new HttpHeaderElement("compress"))) {
            pae.addElement(new HttpHeaderElement("compress"));
        }
        if (!pae.contains(new HttpHeaderElement("x-compress"))) {
            pae.addElement(new HttpHeaderElement("x-compress"));
        }
        hdrs[idx] = new NVPair("Accept-Encoding", Util.assembleHeader(pae));
        return 0;
    }
    
    @Override
    public void responsePhase1Handler(final Response resp, final RoRequest req) {
    }
    
    @Override
    public int responsePhase2Handler(final Response resp, final Request req) {
        return 10;
    }
    
    @Override
    public void responsePhase3Handler(final Response resp, final RoRequest req) throws IOException, ModuleException {
        final String ce = resp.getHeader("Content-Encoding");
        if (ce == null || req.getMethod().equals("HEAD") || resp.getStatusCode() == 206) {
            return;
        }
        Vector pce;
        try {
            pce = Util.parseHeader(ce);
        }
        catch (final ParseException pe) {
            throw new ModuleException(pe.toString());
        }
        if (pce.size() == 0) {
            return;
        }
        final String encoding = pce.firstElement().getName();
        if (encoding.equalsIgnoreCase("gzip") || encoding.equalsIgnoreCase("x-gzip")) {
            Log.write(32, "CEM:   pushing gzip-input-stream");
            resp.inp_stream = new GZIPInputStream(resp.inp_stream);
            pce.removeElementAt(pce.size() - 1);
            resp.deleteHeader("Content-length");
        }
        else if (encoding.equalsIgnoreCase("deflate")) {
            Log.write(32, "CEM:   pushing inflater-input-stream");
            final SequenceInputStream is = new SequenceInputStream(resp.inp_stream, new ByteArrayInputStream(ContentEncodingModule.dummy));
            resp.inp_stream = new InflaterInputStream(is, new Inflater(true));
            pce.removeElementAt(pce.size() - 1);
            resp.deleteHeader("Content-length");
        }
        else if (encoding.equalsIgnoreCase("compress") || encoding.equalsIgnoreCase("x-compress")) {
            Log.write(32, "CEM:   pushing uncompress-input-stream");
            resp.inp_stream = new UncompressInputStream(resp.inp_stream);
            pce.removeElementAt(pce.size() - 1);
            resp.deleteHeader("Content-length");
        }
        else if (encoding.equalsIgnoreCase("identity")) {
            Log.write(32, "CEM:   ignoring 'identity' token");
            pce.removeElementAt(pce.size() - 1);
        }
        else {
            Log.write(32, "CEM:   Unknown content encoding '" + encoding + "'");
        }
        if (pce.size() > 0) {
            resp.setHeader("Content-Encoding", Util.assembleHeader(pce));
        }
        else {
            resp.deleteHeader("Content-Encoding");
        }
    }
    
    @Override
    public void trailerHandler(final Response resp, final RoRequest req) {
    }
    
    static {
        dummy = new byte[1];
    }
}
