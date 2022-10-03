package java.net;

import java.io.IOException;
import sun.net.sdp.SdpSupport;

class SdpSocketImpl extends PlainSocketImpl
{
    @Override
    protected void create(final boolean b) throws IOException {
        if (!b) {
            throw new UnsupportedOperationException("Must be a stream socket");
        }
        this.fd = SdpSupport.createSocket();
        if (this.socket != null) {
            this.socket.setCreated();
        }
        if (this.serverSocket != null) {
            this.serverSocket.setCreated();
        }
    }
}
