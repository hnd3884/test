package HTTPClient;

import java.io.IOException;

final class ResponseHandler
{
    RespInputStream stream;
    Response resp;
    Request request;
    boolean eof;
    IOException exception;
    private byte[] endbndry;
    private int[] end_cmp;
    
    ResponseHandler(final Response resp, final Request request, final StreamDemultiplexor demux) {
        this.eof = false;
        this.resp = resp;
        this.request = request;
        this.stream = new RespInputStream(demux, this);
        Log.write(4, "Demux: Opening stream " + this.stream.hashCode() + " for demux (" + demux.hashCode() + ")");
    }
    
    byte[] getEndBoundary(final BufferedInputStream MasterStream) throws IOException, ParseException {
        if (this.endbndry == null) {
            this.setupBoundary(MasterStream);
        }
        return this.endbndry;
    }
    
    int[] getEndCompiled(final BufferedInputStream MasterStream) throws IOException, ParseException {
        if (this.end_cmp == null) {
            this.setupBoundary(MasterStream);
        }
        return this.end_cmp;
    }
    
    void setupBoundary(final BufferedInputStream MasterStream) throws IOException, ParseException {
        final String endstr = "--" + Util.getParameter("boundary", this.resp.getHeader("Content-Type")) + "--\r\n";
        this.endbndry = endstr.getBytes("8859_1");
        this.end_cmp = Util.compile_search(this.endbndry);
        MasterStream.markForSearch();
    }
}
