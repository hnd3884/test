package jcifs.smb;

import java.util.Collections;
import java.util.HashMap;
import jcifs.util.Hexdump;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.IOException;
import jcifs.dcerpc.UnicodeString;
import jcifs.dcerpc.DcerpcMessage;
import jcifs.dcerpc.msrpc.MsrpcLookupSids;
import jcifs.dcerpc.msrpc.LsaPolicyHandle;
import jcifs.dcerpc.DcerpcHandle;
import java.util.Map;
import jcifs.dcerpc.rpc;

public class SID extends rpc.sid_t
{
    public static final int SID_TYPE_USE_NONE = 0;
    public static final int SID_TYPE_USER = 1;
    public static final int SID_TYPE_DOM_GRP = 2;
    public static final int SID_TYPE_DOMAIN = 3;
    public static final int SID_TYPE_ALIAS = 4;
    public static final int SID_TYPE_WKN_GRP = 5;
    public static final int SID_TYPE_DELETED = 6;
    public static final int SID_TYPE_INVALID = 7;
    public static final int SID_TYPE_UNKNOWN = 8;
    static final String[] SID_TYPE_NAMES;
    static Map sid_cache;
    int type;
    String domainName;
    String acctName;
    String origin_server;
    NtlmPasswordAuthentication origin_auth;
    
    static void resolveSids(final DcerpcHandle handle, final LsaPolicyHandle policyHandle, final SID[] sids) throws IOException {
        final MsrpcLookupSids rpc = new MsrpcLookupSids(policyHandle, sids);
        handle.sendrecv(rpc);
        switch (rpc.retval) {
            case -1073741709:
            case 0:
            case 263: {
                for (int si = 0; si < sids.length; ++si) {
                    sids[si].type = rpc.names.names[si].sid_type;
                    sids[si].domainName = null;
                    switch (sids[si].type) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5: {
                            final int sid_index = rpc.names.names[si].sid_index;
                            final rpc.unicode_string ustr = rpc.domains.domains[sid_index].name;
                            sids[si].domainName = new UnicodeString(ustr, false).toString();
                            break;
                        }
                    }
                    sids[si].acctName = new UnicodeString(rpc.names.names[si].name, false).toString();
                }
                return;
            }
            default: {
                throw new SmbException(rpc.retval, false);
            }
        }
    }
    
    static void resolveSids0(final String authorityServerName, final NtlmPasswordAuthentication auth, final SID[] sids) throws IOException {
        DcerpcHandle handle = null;
        LsaPolicyHandle policyHandle = null;
        try {
            handle = DcerpcHandle.getHandle("ncacn_np:" + authorityServerName + "[\\PIPE\\lsarpc]", auth);
            policyHandle = new LsaPolicyHandle(handle, null, 2048);
            resolveSids(handle, policyHandle, sids);
        }
        finally {
            if (handle != null) {
                if (policyHandle != null) {
                    policyHandle.close();
                }
                handle.close();
            }
        }
    }
    
    public static void resolveSids(final String authorityServerName, final NtlmPasswordAuthentication auth, SID[] sids) throws IOException {
        final ArrayList list = new ArrayList(sids.length);
        for (int si = 0; si < sids.length; ++si) {
            final SID sid = SID.sid_cache.get(sids[si]);
            if (sid != null) {
                sids[si].type = sid.type;
                sids[si].domainName = sid.domainName;
                sids[si].acctName = sid.acctName;
            }
            else {
                list.add(sids[si]);
            }
        }
        if (list.size() > 0) {
            sids = list.toArray(new SID[0]);
            resolveSids0(authorityServerName, auth, sids);
            for (int si = 0; si < sids.length; ++si) {
                SID.sid_cache.put(sids[si], sids[si]);
            }
        }
    }
    
    public SID(final byte[] src, int si) {
        this.domainName = null;
        this.acctName = null;
        this.origin_server = null;
        this.origin_auth = null;
        this.revision = src[si++];
        this.sub_authority_count = src[si++];
        System.arraycopy(src, si, this.identifier_authority = new byte[6], 0, 6);
        si += 6;
        if (this.sub_authority_count > 100) {
            throw new RuntimeException("Invalid SID");
        }
        this.sub_authority = new int[this.sub_authority_count];
        for (int i = 0; i < this.sub_authority_count; ++i) {
            this.sub_authority[i] = ServerMessageBlock.readInt4(src, si);
            si += 4;
        }
    }
    
    public SID(final String textual) throws SmbException {
        this.domainName = null;
        this.acctName = null;
        this.origin_server = null;
        this.origin_auth = null;
        final StringTokenizer st = new StringTokenizer(textual, "-");
        if (st.countTokens() < 3 || !st.nextToken().equals("S")) {
            throw new SmbException("Bad textual SID format: " + textual);
        }
        this.revision = Byte.parseByte(st.nextToken());
        final String tmp = st.nextToken();
        long id = 0L;
        if (tmp.startsWith("0x")) {
            id = Long.parseLong(tmp.substring(2), 16);
        }
        else {
            id = Long.parseLong(tmp);
        }
        this.identifier_authority = new byte[6];
        for (int i = 5; id > 0L; id >>= 8, --i) {
            this.identifier_authority[i] = (byte)(id % 256L);
        }
        this.sub_authority_count = (byte)st.countTokens();
        if (this.sub_authority_count > 0) {
            this.sub_authority = new int[this.sub_authority_count];
            for (int i = 0; i < this.sub_authority_count; ++i) {
                this.sub_authority[i] = (int)(Long.parseLong(st.nextToken()) & 0xFFFFFFFFL);
            }
        }
    }
    
    public SID(final SID domsid, final int rid) {
        this.domainName = null;
        this.acctName = null;
        this.origin_server = null;
        this.origin_auth = null;
        this.revision = domsid.revision;
        this.identifier_authority = domsid.identifier_authority;
        this.sub_authority_count = (byte)(domsid.sub_authority_count + 1);
        this.sub_authority = new int[this.sub_authority_count];
        int i;
        for (i = 0; i < domsid.sub_authority_count; ++i) {
            this.sub_authority[i] = domsid.sub_authority[i];
        }
        this.sub_authority[i] = rid;
    }
    
    public int getType() {
        if (this.origin_server != null) {
            this.resolveWeak();
        }
        return this.type;
    }
    
    public String getTypeText() {
        if (this.origin_server != null) {
            this.resolveWeak();
        }
        return SID.SID_TYPE_NAMES[this.type];
    }
    
    public String getDomainName() {
        if (this.origin_server != null) {
            this.resolveWeak();
        }
        if (this.type == 8) {
            final String full = this.toString();
            return full.substring(0, full.length() - this.getAccountName().length() - 1);
        }
        return this.domainName;
    }
    
    public String getAccountName() {
        if (this.origin_server != null) {
            this.resolveWeak();
        }
        if (this.type == 8) {
            return "" + this.sub_authority[this.sub_authority_count - 1];
        }
        if (this.type == 3) {
            return "";
        }
        return this.acctName;
    }
    
    public int hashCode() {
        int hcode = this.identifier_authority[5];
        for (int i = 0; i < this.sub_authority_count; ++i) {
            hcode += 65599 * this.sub_authority[i];
        }
        return hcode;
    }
    
    public boolean equals(final Object obj) {
        if (obj instanceof SID) {
            final SID sid = (SID)obj;
            if (sid == this) {
                return true;
            }
            if (sid.sub_authority_count == this.sub_authority_count) {
                int i = this.sub_authority_count;
                while (i-- > 0) {
                    if (sid.sub_authority[i] != this.sub_authority[i]) {
                        return false;
                    }
                }
                for (i = 0; i < 6; ++i) {
                    if (sid.identifier_authority[i] != this.identifier_authority[i]) {
                        return false;
                    }
                }
                return sid.revision == this.revision;
            }
        }
        return false;
    }
    
    public String toString() {
        String ret = "S-" + (this.revision & 0xFF) + "-";
        if (this.identifier_authority[0] != 0 || this.identifier_authority[1] != 0) {
            ret += "0x";
            ret += Hexdump.toHexString(this.identifier_authority, 0, 6);
        }
        else {
            long shift = 0L;
            long id = 0L;
            for (int i = 5; i > 1; --i) {
                id += ((long)this.identifier_authority[i] & 0xFFL) << (int)shift;
                shift += 8L;
            }
            ret += id;
        }
        for (int j = 0; j < this.sub_authority_count; ++j) {
            ret = ret + "-" + ((long)this.sub_authority[j] & 0xFFFFFFFFL);
        }
        return ret;
    }
    
    public String toDisplayString() {
        if (this.origin_server != null) {
            this.resolveWeak();
        }
        if (this.domainName != null) {
            String str;
            if (this.type == 3) {
                str = this.domainName;
            }
            else if (this.domainName == null || this.type == 5 || this.domainName.equals("BUILTIN")) {
                if (this.type == 8) {
                    str = this.toString();
                }
                else {
                    str = this.acctName;
                }
            }
            else {
                str = this.domainName + "\\" + this.acctName;
            }
            return str;
        }
        return this.toString();
    }
    
    void resolve(final String authorityServerName, final NtlmPasswordAuthentication auth) throws IOException {
        final SID[] sids = { this };
        resolveSids(authorityServerName, auth, sids);
    }
    
    void resolveWeak() {
        if (this.origin_server != null) {
            try {
                this.resolve(this.origin_server, this.origin_auth);
            }
            catch (final IOException ioe) {}
            finally {
                this.origin_server = null;
                this.origin_auth = null;
            }
        }
    }
    
    static {
        SID_TYPE_NAMES = new String[] { "0", "User", "Domain group", "Domain", "Local group", "Builtin group", "Deleted", "Invalid", "Unknown" };
        SID.sid_cache = Collections.synchronizedMap(new HashMap<Object, Object>());
    }
}
