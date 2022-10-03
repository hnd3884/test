package jcifs.dcerpc;

import jcifs.dcerpc.msrpc.lsarpc;
import jcifs.dcerpc.msrpc.srvsvc;
import java.util.Iterator;
import java.util.HashMap;

class DcerpcBinding
{
    private static HashMap INTERFACES;
    String proto;
    String server;
    String endpoint;
    HashMap options;
    UUID uuid;
    int major;
    int minor;
    
    DcerpcBinding(final String proto, final String server) {
        this.endpoint = null;
        this.options = null;
        this.uuid = null;
        this.proto = proto;
        this.server = server;
    }
    
    void setOption(final String key, final Object val) throws DcerpcException {
        if (key.equals("endpoint")) {
            this.endpoint = val.toString().toLowerCase();
            if (this.endpoint.startsWith("\\pipe\\")) {
                final String iface = DcerpcBinding.INTERFACES.get(this.endpoint.substring(6));
                if (iface != null) {
                    final int c = iface.indexOf(58);
                    final int p = iface.indexOf(46, c + 1);
                    this.uuid = new UUID(iface.substring(0, c));
                    this.major = Integer.parseInt(iface.substring(c + 1, p));
                    this.minor = Integer.parseInt(iface.substring(p + 1));
                    return;
                }
            }
            throw new DcerpcException("Bad endpoint: " + this.endpoint);
        }
        if (this.options == null) {
            this.options = new HashMap();
        }
        this.options.put(key, val);
    }
    
    Object getOption(final String key) {
        if (key.equals("endpoint")) {
            return this.endpoint;
        }
        return this.options.get(key);
    }
    
    public String toString() {
        String ret = this.proto + ":" + this.server + "[" + this.endpoint;
        if (this.options != null) {
            final Iterator iter = this.options.keySet().iterator();
            while (iter.hasNext()) {
                final Object key = iter.next();
                final Object val = this.options.get(key);
                ret = ret + "," + key + "=" + val;
            }
        }
        ret += "]";
        return ret;
    }
    
    static {
        (DcerpcBinding.INTERFACES = new HashMap()).put("srvsvc", srvsvc.getSyntax());
        DcerpcBinding.INTERFACES.put("lsarpc", lsarpc.getSyntax());
    }
}
