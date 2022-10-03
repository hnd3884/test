package HTTPClient;

import java.net.ProtocolException;
import java.io.IOException;

class DefaultModule implements HTTPClientModule
{
    private int req_timeout_retries;
    
    DefaultModule() {
        this.req_timeout_retries = 3;
    }
    
    public int requestHandler(final Request req, final Response[] resp) {
        return 0;
    }
    
    public void responsePhase1Handler(final Response resp, final RoRequest req) {
    }
    
    public int responsePhase2Handler(final Response resp, final Request req) throws IOException {
        final int sts = resp.getStatusCode();
        switch (sts) {
            case 408: {
                if (this.req_timeout_retries-- == 0 || req.getStream() != null) {
                    Log.write(32, "DefM:  Status " + sts + " " + resp.getReasonLine() + " not handled - " + "maximum number of retries exceeded");
                    return 10;
                }
                Log.write(32, "DefM:  Handling " + sts + " " + resp.getReasonLine() + " - " + "resending request");
                return 13;
            }
            case 411: {
                if (req.getStream() != null && req.getStream().getLength() == -1) {
                    return 10;
                }
                try {
                    resp.getInputStream().close();
                }
                catch (final IOException ex) {}
                if (req.getData() != null) {
                    throw new ProtocolException("Received status code 411 even though Content-Length was sent");
                }
                Log.write(32, "DefM:  Handling " + sts + " " + resp.getReasonLine() + " - resending " + "request with 'Content-length: 0'");
                req.setData(new byte[0]);
                return 13;
            }
            case 505: {
                return 10;
            }
            default: {
                return 10;
            }
        }
    }
    
    public void responsePhase3Handler(final Response resp, final RoRequest req) {
    }
    
    public void trailerHandler(final Response resp, final RoRequest req) {
    }
}
