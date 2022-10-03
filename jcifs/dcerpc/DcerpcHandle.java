package jcifs.dcerpc;

import java.io.IOException;
import jcifs.dcerpc.ndr.NdrBuffer;
import jcifs.smb.BufferCache;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import jcifs.smb.NtlmPasswordAuthentication;

public abstract class DcerpcHandle implements DcerpcConstants
{
    protected DcerpcBinding binding;
    protected int max_xmit;
    protected int max_recv;
    protected int state;
    private static int call_id;
    
    public DcerpcHandle() {
        this.max_xmit = 4280;
        this.max_recv = this.max_xmit;
        this.state = 0;
    }
    
    protected static DcerpcBinding parseBinding(final String str) throws DcerpcException {
        final char[] arr = str.toCharArray();
        String proto = null;
        String key = null;
        DcerpcBinding binding = null;
        int si;
        int state;
        int mark = state = (si = 0);
        do {
            final char ch = arr[si];
            switch (state) {
                case 0: {
                    if (ch == ':') {
                        proto = str.substring(mark, si);
                        mark = si + 1;
                        state = 1;
                        continue;
                    }
                    continue;
                }
                case 1: {
                    if (ch == '\\') {
                        mark = si + 1;
                        continue;
                    }
                    state = 2;
                }
                case 2: {
                    if (ch == '[') {
                        String server = str.substring(mark, si).trim();
                        if (server.length() == 0) {
                            server = "127.0.0.1";
                        }
                        binding = new DcerpcBinding(proto, str.substring(mark, si));
                        mark = si + 1;
                        state = 5;
                        continue;
                    }
                    continue;
                }
                case 5: {
                    if (ch == '=') {
                        key = str.substring(mark, si).trim();
                        mark = si + 1;
                        continue;
                    }
                    if (ch == ',' || ch == ']') {
                        final String val = str.substring(mark, si).trim();
                        if (key == null) {
                            key = "endpoint";
                        }
                        binding.setOption(key, val);
                        key = null;
                        continue;
                    }
                    continue;
                }
                default: {
                    si = arr.length;
                    continue;
                }
            }
        } while (++si < arr.length);
        if (binding == null || binding.endpoint == null) {
            throw new DcerpcException("Invalid binding URL: " + str);
        }
        return binding;
    }
    
    public static DcerpcHandle getHandle(final String url, final NtlmPasswordAuthentication auth) throws UnknownHostException, MalformedURLException, DcerpcException {
        if (url.startsWith("ncacn_np:")) {
            return new DcerpcPipeHandle(url, auth);
        }
        throw new DcerpcException("DCERPC transport not supported: " + url);
    }
    
    public void sendrecv(final DcerpcMessage msg) throws DcerpcException, IOException {
        if (this.state == 0) {
            this.state = 1;
            final DcerpcMessage bind = new DcerpcBind(this.binding, this);
            this.sendrecv(bind);
        }
        byte[] stub = BufferCache.getBuffer();
        try {
            final NdrBuffer buf = new NdrBuffer(stub, 0);
            msg.flags = 3;
            msg.call_id = DcerpcHandle.call_id;
            msg.encode(buf);
            int n;
            for (int tot = buf.getLength(), off = 0; off < tot; off += n) {
                msg.call_id = DcerpcHandle.call_id++;
                if (tot - off > this.max_xmit) {
                    throw new DcerpcException("Fragmented request PDUs currently not supported");
                }
                n = tot - off;
                this.doSendFragment(stub, off, n);
            }
            this.doReceiveFragment(stub);
            buf.reset();
            msg.decode_header(buf);
            int off = 24;
            if (msg.ptype == 2 && !msg.isFlagSet(2)) {
                off = msg.length;
            }
            byte[] frag = null;
            NdrBuffer fbuf = null;
            while (!msg.isFlagSet(2)) {
                if (frag == null) {
                    frag = new byte[this.max_recv];
                    fbuf = new NdrBuffer(frag, 0);
                }
                this.doReceiveFragment(frag);
                fbuf.reset();
                msg.decode_header(fbuf);
                final int stub_frag_len = msg.length - 24;
                if (off + stub_frag_len > stub.length) {
                    final byte[] tmp = new byte[off + stub_frag_len];
                    System.arraycopy(stub, 0, tmp, 0, off);
                    stub = tmp;
                }
                System.arraycopy(frag, 24, stub, off, stub_frag_len);
                off += stub_frag_len;
            }
            buf.reset();
            msg.decode(buf);
        }
        finally {
            BufferCache.releaseBuffer(stub);
        }
        final DcerpcException de;
        if ((de = msg.getResult()) != null) {
            throw de;
        }
    }
    
    public String toString() {
        return this.binding.toString();
    }
    
    protected abstract void doSendFragment(final byte[] p0, final int p1, final int p2) throws IOException;
    
    protected abstract void doReceiveFragment(final byte[] p0) throws IOException;
    
    public abstract void close() throws IOException;
    
    static {
        DcerpcHandle.call_id = 0;
    }
}
