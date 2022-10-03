package jcifs.smb;

import jcifs.Config;
import jcifs.dcerpc.msrpc.MsrpcShareGetInfo;
import java.io.OutputStream;
import java.io.InputStream;
import jcifs.dcerpc.DcerpcMessage;
import jcifs.dcerpc.DcerpcHandle;
import jcifs.dcerpc.msrpc.MsrpcShareEnum;
import java.io.PrintStream;
import java.util.ArrayList;
import java.security.Principal;
import java.io.IOException;
import jcifs.netbios.NbtAddress;
import jcifs.UniAddress;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import java.net.URL;
import jcifs.util.LogStream;
import java.net.URLConnection;

public class SmbFile extends URLConnection implements SmbConstants
{
    static final int O_RDONLY = 1;
    static final int O_WRONLY = 2;
    static final int O_RDWR = 3;
    static final int O_APPEND = 4;
    static final int O_CREAT = 16;
    static final int O_EXCL = 32;
    static final int O_TRUNC = 64;
    public static final int FILE_NO_SHARE = 0;
    public static final int FILE_SHARE_READ = 1;
    public static final int FILE_SHARE_WRITE = 2;
    public static final int FILE_SHARE_DELETE = 4;
    public static final int ATTR_READONLY = 1;
    public static final int ATTR_HIDDEN = 2;
    public static final int ATTR_SYSTEM = 4;
    public static final int ATTR_VOLUME = 8;
    public static final int ATTR_DIRECTORY = 16;
    public static final int ATTR_ARCHIVE = 32;
    static final int ATTR_COMPRESSED = 2048;
    static final int ATTR_NORMAL = 128;
    static final int ATTR_TEMPORARY = 256;
    static final int ATTR_GET_MASK = 32767;
    static final int ATTR_SET_MASK = 12455;
    static final int DEFAULT_ATTR_EXPIRATION_PERIOD = 5000;
    static final int HASH_DOT;
    static final int HASH_DOT_DOT;
    static LogStream log;
    static long attrExpirationPeriod;
    public static final int TYPE_FILESYSTEM = 1;
    public static final int TYPE_WORKGROUP = 2;
    public static final int TYPE_SERVER = 4;
    public static final int TYPE_SHARE = 8;
    public static final int TYPE_NAMED_PIPE = 16;
    public static final int TYPE_PRINTER = 32;
    public static final int TYPE_COMM = 64;
    private String canon;
    private String share;
    private long createTime;
    private long lastModified;
    private int attributes;
    private long attrExpiration;
    private long size;
    private long sizeExpiration;
    private boolean isExists;
    private int shareAccess;
    private SmbComBlankResponse blank_resp;
    private DfsReferral dfsReferral;
    NtlmPasswordAuthentication auth;
    SmbTree tree;
    String unc;
    int fid;
    int type;
    boolean opened;
    int tree_num;
    private SmbExtendedAuthenticator authenticator;
    
    public SmbFile(final String url) throws MalformedURLException {
        this(new URL(null, url, Handler.SMB_HANDLER));
    }
    
    public SmbFile(final SmbFile context, final String name) throws MalformedURLException, UnknownHostException {
        this(context.isWorkgroup0() ? new URL(null, "smb://" + name, Handler.SMB_HANDLER) : new URL(context.url, name, Handler.SMB_HANDLER), context.auth);
    }
    
    public SmbFile(final String context, final String name) throws MalformedURLException {
        this(new URL(new URL(null, context, Handler.SMB_HANDLER), name, Handler.SMB_HANDLER));
    }
    
    public SmbFile(final String url, final NtlmPasswordAuthentication auth) throws MalformedURLException {
        this(new URL(null, url, Handler.SMB_HANDLER), auth);
    }
    
    public SmbFile(final String url, final NtlmPasswordAuthentication auth, final int shareAccess) throws MalformedURLException {
        this(new URL(null, url, Handler.SMB_HANDLER), auth);
        if ((shareAccess & 0xFFFFFFF8) != 0x0) {
            throw new RuntimeException("Illegal shareAccess parameter");
        }
        this.shareAccess = shareAccess;
    }
    
    public SmbFile(final String context, final String name, final NtlmPasswordAuthentication auth) throws MalformedURLException {
        this(new URL(new URL(null, context, Handler.SMB_HANDLER), name, Handler.SMB_HANDLER), auth);
    }
    
    public SmbFile(final String context, final String name, final NtlmPasswordAuthentication auth, final int shareAccess) throws MalformedURLException {
        this(new URL(new URL(null, context, Handler.SMB_HANDLER), name, Handler.SMB_HANDLER), auth);
        if ((shareAccess & 0xFFFFFFF8) != 0x0) {
            throw new RuntimeException("Illegal shareAccess parameter");
        }
        this.shareAccess = shareAccess;
    }
    
    public SmbFile(final SmbFile context, final String name, final int shareAccess) throws MalformedURLException, UnknownHostException {
        this(context.isWorkgroup0() ? new URL(null, "smb://" + name, Handler.SMB_HANDLER) : new URL(context.url, name, Handler.SMB_HANDLER), context.auth);
        if ((shareAccess & 0xFFFFFFF8) != 0x0) {
            throw new RuntimeException("Illegal shareAccess parameter");
        }
        this.shareAccess = shareAccess;
    }
    
    public SmbFile(final URL url) {
        this(url, new NtlmPasswordAuthentication(url.getUserInfo()));
    }
    
    public SmbFile(final URL url, final NtlmPasswordAuthentication auth) {
        super(url);
        this.shareAccess = 7;
        this.blank_resp = null;
        this.dfsReferral = null;
        this.tree = null;
        this.authenticator = null;
        this.auth = ((auth == null) ? new NtlmPasswordAuthentication(url.getUserInfo()) : auth);
        this.getUncPath0();
    }
    
    SmbFile(final SmbFile context, String name, final int type, final int attributes, final long createTime, final long lastModified, final long size) throws MalformedURLException, UnknownHostException {
        this(context.isWorkgroup0() ? new URL(null, "smb://" + name + "/", Handler.SMB_HANDLER) : new URL(context.url, name + (((attributes & 0x10) > 0) ? "/" : "")));
        this.auth = context.auth;
        if (context.share != null) {
            this.tree = context.tree;
            this.dfsReferral = context.dfsReferral;
        }
        final int last = name.length() - 1;
        if (name.charAt(last) == '/') {
            name = name.substring(0, last);
        }
        if (context.share == null) {
            this.unc = "\\";
        }
        else if (context.unc.equals("\\")) {
            this.unc = '\\' + name;
        }
        else {
            this.unc = context.unc + '\\' + name;
        }
        this.type = type;
        this.attributes = attributes;
        this.createTime = createTime;
        this.lastModified = lastModified;
        this.size = size;
        this.isExists = true;
        final long n = System.currentTimeMillis() + SmbFile.attrExpirationPeriod;
        this.sizeExpiration = n;
        this.attrExpiration = n;
    }
    
    private SmbComBlankResponse blank_resp() {
        if (this.blank_resp == null) {
            this.blank_resp = new SmbComBlankResponse();
        }
        return this.blank_resp;
    }
    
    void send(final ServerMessageBlock request, final ServerMessageBlock response) throws SmbException {
        while (true) {
            this.connect0();
            DfsReferral dr = null;
            if (this.tree.inDfs) {
                dr = this.tree.session.transport.lookupReferral(this.unc);
                if (dr != null) {
                    UniAddress addr;
                    try {
                        addr = UniAddress.getByName(dr.server);
                    }
                    catch (final UnknownHostException uhe) {
                        throw new SmbException(dr.server, uhe);
                    }
                    final SmbTransport trans = SmbTransport.getSmbTransport(addr, this.url.getPort());
                    this.tree = trans.getSmbSession(this.authenticator, this.auth).getSmbTree(dr.share, null);
                    final LogStream log = SmbFile.log;
                    if (LogStream.level >= 3) {
                        SmbFile.log.println(dr);
                    }
                    this.dfsReferral = dr;
                    String dunc = this.dfsReferral.nodepath + this.unc.substring(this.dfsReferral.path.length());
                    this.unc = dunc;
                    if (request.path.endsWith("\\") && !dunc.endsWith("\\")) {
                        dunc += "\\";
                    }
                    request.path = dunc;
                }
                request.flags2 |= 0x1000;
            }
            else {
                request.flags2 &= 0xFFFFEFFF;
            }
            try {
                this.tree.send(request, response);
            }
            catch (final DfsReferral dr) {
                if (dr.resolveHashes) {
                    throw dr;
                }
                request.reset();
                continue;
            }
            break;
        }
    }
    
    static String queryLookup(final String query, final String param) {
        final char[] in = query.toCharArray();
        int st;
        int eq = st = 0;
        for (int i = 0; i < in.length; ++i) {
            final int ch = in[i];
            if (ch == 38) {
                if (eq > st) {
                    final String p = new String(in, st, eq - st);
                    if (p.equalsIgnoreCase(param)) {
                        ++eq;
                        return new String(in, eq, i - eq);
                    }
                }
                st = i + 1;
            }
            else if (ch == 61) {
                eq = i;
            }
        }
        if (eq > st) {
            final String p = new String(in, st, eq - st);
            if (p.equalsIgnoreCase(param)) {
                ++eq;
                return new String(in, eq, in.length - eq);
            }
        }
        return null;
    }
    
    UniAddress getAddress() throws UnknownHostException {
        final String host = this.url.getHost();
        final String path = this.url.getPath();
        final String query = this.url.getQuery();
        if (query != null) {
            final String server = queryLookup(query, "server");
            if (server != null && server.length() > 0) {
                return UniAddress.getByName(server);
            }
        }
        if (host.length() == 0) {
            try {
                final NbtAddress addr = NbtAddress.getByName("\u0001\u0002__MSBROWSE__\u0002", 1, null);
                return UniAddress.getByName(addr.getHostAddress());
            }
            catch (final UnknownHostException uhe) {
                NtlmPasswordAuthentication.initDefaults();
                if (NtlmPasswordAuthentication.DEFAULT_DOMAIN.equals("?")) {
                    throw uhe;
                }
                return UniAddress.getByName(NtlmPasswordAuthentication.DEFAULT_DOMAIN, true);
            }
        }
        if (path.length() == 0 || path.equals("/")) {
            return UniAddress.getByName(host, true);
        }
        return UniAddress.getByName(host);
    }
    
    void connect0() throws SmbException {
        try {
            this.connect();
        }
        catch (final UnknownHostException uhe) {
            throw new SmbException("Failed to connect to server", uhe);
        }
        catch (final SmbException se) {
            throw se;
        }
        catch (final IOException ioe) {
            throw new SmbException("Failed to connect to server", ioe);
        }
    }
    
    public void connect() throws IOException {
        if (this.isConnected()) {
            return;
        }
        this.getUncPath0();
        final UniAddress addr = this.getAddress();
        final SmbTransport trans = SmbTransport.getSmbTransport(addr, this.url.getPort());
        SmbSession ssn = trans.getSmbSession(this.authenticator, this.auth);
        this.tree = ssn.getSmbTree(this.share, null);
        try {
            this.tree.treeConnect(null, null);
        }
        catch (final SmbAuthException sae) {
            if (this.share == null) {
                ssn = trans.getSmbSession(this.authenticator, NtlmPasswordAuthentication.NULL);
                (this.tree = ssn.getSmbTree(null, null)).treeConnect(null, null);
            }
            else {
                final NtlmPasswordAuthentication a;
                if ((a = NtlmAuthenticator.requestNtlmPasswordAuthentication(this.url.toString(), sae)) == null) {
                    throw sae;
                }
                this.auth = a;
                ssn = trans.getSmbSession(this.authenticator, this.auth);
                (this.tree = ssn.getSmbTree(this.share, null)).treeConnect(null, null);
            }
        }
    }
    
    boolean isConnected() {
        return this.connected = (this.tree != null && this.tree.treeConnected);
    }
    
    int open0(final int flags, final int access, final int attrs, final int options) throws SmbException {
        this.connect0();
        final LogStream log = SmbFile.log;
        if (LogStream.level >= 3) {
            SmbFile.log.println("open0: " + this.unc);
        }
        int f;
        if (this.tree.session.transport.hasCapability(16)) {
            final SmbComNTCreateAndXResponse response = new SmbComNTCreateAndXResponse();
            this.send(new SmbComNTCreateAndX(this.unc, flags, access, this.shareAccess, attrs, options, null), response);
            f = response.fid;
            this.attributes = (response.extFileAttributes & 0x7FFF);
            this.attrExpiration = System.currentTimeMillis() + SmbFile.attrExpirationPeriod;
            this.isExists = true;
        }
        else {
            final SmbComOpenAndXResponse response2 = new SmbComOpenAndXResponse();
            this.send(new SmbComOpenAndX(this.unc, flags, access, null), response2);
            f = response2.fid;
        }
        return f;
    }
    
    void open(final int flags, final int access, final int attrs, final int options) throws SmbException {
        if (this.isOpen()) {
            return;
        }
        this.fid = this.open0(flags, access, attrs, options);
        this.opened = true;
        this.tree_num = this.tree.tree_num;
    }
    
    boolean isOpen() {
        return this.opened && this.isConnected() && this.tree_num == this.tree.tree_num;
    }
    
    void close(final int f, final long lastWriteTime) throws SmbException {
        final LogStream log = SmbFile.log;
        if (LogStream.level >= 3) {
            SmbFile.log.println("close: " + f);
        }
        this.send(new SmbComClose(f, lastWriteTime), this.blank_resp());
    }
    
    void close(final long lastWriteTime) throws SmbException {
        if (!this.isOpen()) {
            return;
        }
        this.close(this.fid, lastWriteTime);
        this.opened = false;
    }
    
    void close() throws SmbException {
        this.close(0L);
    }
    
    public Principal getPrincipal() {
        return this.auth;
    }
    
    public String getName() {
        this.getUncPath0();
        if (this.canon.length() > 1) {
            int i;
            for (i = this.canon.length() - 2; this.canon.charAt(i) != '/'; --i) {}
            return this.canon.substring(i + 1);
        }
        if (this.share != null) {
            return this.share + '/';
        }
        if (this.url.getHost().length() > 0) {
            return this.url.getHost() + '/';
        }
        return "smb://";
    }
    
    public String getParent() {
        String str = this.url.getAuthority();
        if (str.length() > 0) {
            final StringBuffer sb = new StringBuffer("smb://");
            sb.append(str);
            this.getUncPath0();
            if (this.canon.length() > 1) {
                sb.append(this.canon);
            }
            else {
                sb.append('/');
            }
            int i;
            for (str = sb.toString(), i = str.length() - 2; str.charAt(i) != '/'; --i) {}
            return str.substring(0, i + 1);
        }
        return "smb://";
    }
    
    public String getPath() {
        return this.url.toString();
    }
    
    String getUncPath0() {
        if (this.unc == null) {
            final char[] in = this.url.getPath().toCharArray();
            final char[] out = new char[in.length];
            final int length = in.length;
            int state = 0;
            int o = 0;
            for (int i = 0; i < length; ++i) {
                switch (state) {
                    case 0: {
                        if (in[i] != '/') {
                            return null;
                        }
                        out[o++] = in[i];
                        state = 1;
                        break;
                    }
                    case 1: {
                        if (in[i] == '/') {
                            break;
                        }
                        if (in[i] == '.' && (i + 1 >= length || in[i + 1] == '/')) {
                            ++i;
                            break;
                        }
                        if (i + 1 >= length || in[i] != '.' || in[i + 1] != '.' || (i + 2 < length && in[i + 2] != '/')) {
                            state = 2;
                        }
                        i += 2;
                        if (o == 1) {
                            break;
                        }
                        while (--o > 1) {
                            if (out[o - 1] == '/') {
                                break;
                            }
                        }
                        break;
                    }
                    case 2: {
                        if (in[i] == '/') {
                            state = 1;
                        }
                        out[o++] = in[i];
                        break;
                    }
                }
            }
            this.canon = new String(out, 0, o);
            if (o > 1) {
                --o;
                final int i = this.canon.indexOf(47, 1);
                if (i < 0) {
                    this.share = this.canon.substring(1);
                    this.unc = "\\";
                }
                else if (i == o) {
                    this.share = this.canon.substring(1, i);
                    this.unc = "\\";
                }
                else {
                    this.share = this.canon.substring(1, i);
                    this.unc = this.canon.substring(i, (out[o] == '/') ? o : (o + 1));
                    this.unc = this.unc.replace('/', '\\');
                }
            }
            else {
                this.share = null;
                this.unc = "\\";
            }
        }
        return this.unc;
    }
    
    public String getUncPath() {
        this.getUncPath0();
        if (this.share == null) {
            return "\\\\" + this.url.getHost();
        }
        return "\\\\" + this.url.getHost() + this.canon.replace('/', '\\');
    }
    
    public String getCanonicalPath() {
        final String str = this.url.getAuthority();
        this.getUncPath0();
        if (str.length() > 0) {
            return "smb://" + this.url.getAuthority() + this.canon;
        }
        return "smb://";
    }
    
    public String getShare() {
        return this.share;
    }
    
    String getServerWithDfs() {
        if (this.dfsReferral != null) {
            char[] server;
            int start;
            for (server = this.dfsReferral.node.toCharArray(), start = 0; start < server.length && server[start] == '\\'; ++start) {}
            int end;
            for (end = start; end < server.length && server[end] != '\\'; ++end) {}
            return new String(server, start, end - start);
        }
        return this.getServer();
    }
    
    public String getServer() {
        final String str = this.url.getHost();
        if (str.length() == 0) {
            return null;
        }
        return str;
    }
    
    public int getType() throws SmbException {
        if (this.type == 0) {
            if (this.getUncPath0().length() > 1) {
                this.type = 1;
            }
            else if (this.share != null) {
                this.connect0();
                if (this.share.equals("IPC$")) {
                    this.type = 16;
                }
                else if (this.tree.service.equals("LPT1:")) {
                    this.type = 32;
                }
                else if (this.tree.service.equals("COMM")) {
                    this.type = 64;
                }
                else {
                    this.type = 8;
                }
            }
            else if (this.url.getAuthority().length() == 0) {
                this.type = 2;
            }
            else {
                UniAddress addr;
                try {
                    addr = this.getAddress();
                }
                catch (final UnknownHostException uhe) {
                    throw new SmbException(this.url.toString(), uhe);
                }
                if (addr.getAddress() instanceof NbtAddress) {
                    final int code = ((NbtAddress)addr.getAddress()).getNameType();
                    if (code == 29 || code == 27) {
                        return this.type = 2;
                    }
                }
                this.type = 4;
            }
        }
        return this.type;
    }
    
    boolean isWorkgroup0() throws UnknownHostException {
        if (this.type == 2 || this.url.getHost().length() == 0) {
            this.type = 2;
            return true;
        }
        this.getUncPath0();
        if (this.share == null) {
            final UniAddress addr = this.getAddress();
            if (addr.getAddress() instanceof NbtAddress) {
                final int code = ((NbtAddress)addr.getAddress()).getNameType();
                if (code == 29 || code == 27) {
                    this.type = 2;
                    return true;
                }
            }
            this.type = 4;
        }
        return false;
    }
    
    Info queryPath(final String path, final int infoLevel) throws SmbException {
        this.connect0();
        final LogStream log = SmbFile.log;
        if (LogStream.level >= 3) {
            SmbFile.log.println("queryPath: " + path);
        }
        if (this.tree.session.transport.hasCapability(16)) {
            final Trans2QueryPathInformationResponse response = new Trans2QueryPathInformationResponse(infoLevel);
            this.send(new Trans2QueryPathInformation(path, infoLevel), response);
            return response.info;
        }
        final SmbComQueryInformationResponse response2 = new SmbComQueryInformationResponse(this.tree.session.transport.server.serverTimeZone * 1000 * 60L);
        this.send(new SmbComQueryInformation(path), response2);
        return response2;
    }
    
    public boolean exists() throws SmbException {
        if (this.attrExpiration > System.currentTimeMillis()) {
            return this.isExists;
        }
        this.attributes = 17;
        this.createTime = 0L;
        this.lastModified = 0L;
        this.isExists = false;
        try {
            if (this.url.getHost().length() != 0) {
                if (this.share == null) {
                    if (this.getType() == 2) {
                        UniAddress.getByName(this.url.getHost(), true);
                    }
                    else {
                        UniAddress.getByName(this.url.getHost()).getHostName();
                    }
                }
                else if (this.getUncPath0().length() == 1 || this.share.equalsIgnoreCase("IPC$")) {
                    this.connect0();
                }
                else {
                    final Info info = this.queryPath(this.getUncPath0(), 257);
                    this.attributes = info.getAttributes();
                    this.createTime = info.getCreateTime();
                    this.lastModified = info.getLastWriteTime();
                }
            }
            this.isExists = true;
        }
        catch (final UnknownHostException uhe) {}
        catch (final SmbException se) {
            switch (se.getNtStatus()) {
                case -1073741809:
                case -1073741773:
                case -1073741772:
                case -1073741766: {
                    break;
                }
                default: {
                    throw se;
                }
            }
        }
        this.attrExpiration = System.currentTimeMillis() + SmbFile.attrExpirationPeriod;
        return this.isExists;
    }
    
    public boolean canRead() throws SmbException {
        return this.getType() == 16 || this.exists();
    }
    
    public boolean canWrite() throws SmbException {
        return this.getType() == 16 || (this.exists() && (this.attributes & 0x1) == 0x0);
    }
    
    public boolean isDirectory() throws SmbException {
        return this.getUncPath0().length() == 1 || (this.exists() && (this.attributes & 0x10) == 0x10);
    }
    
    public boolean isFile() throws SmbException {
        if (this.getUncPath0().length() == 1) {
            return false;
        }
        this.exists();
        return (this.attributes & 0x10) == 0x0;
    }
    
    public boolean isHidden() throws SmbException {
        if (this.share == null) {
            return false;
        }
        if (this.getUncPath0().length() == 1) {
            return this.share.endsWith("$");
        }
        this.exists();
        return (this.attributes & 0x2) == 0x2;
    }
    
    public String getDfsPath() throws SmbException {
        this.connect0();
        if (this.tree.inDfs) {
            this.exists();
        }
        if (this.dfsReferral == null) {
            return null;
        }
        String path = "smb:/" + this.dfsReferral.node + this.unc;
        path = path.replace('\\', '/');
        if (this.isDirectory()) {
            path += '/';
        }
        return path;
    }
    
    public long createTime() throws SmbException {
        if (this.getUncPath0().length() > 1) {
            this.exists();
            return this.createTime;
        }
        return 0L;
    }
    
    public long lastModified() throws SmbException {
        if (this.getUncPath0().length() > 1) {
            this.exists();
            return this.lastModified;
        }
        return 0L;
    }
    
    public String[] list() throws SmbException {
        return this.list("*", 22, null, null);
    }
    
    public String[] list(final SmbFilenameFilter filter) throws SmbException {
        return this.list("*", 22, filter, null);
    }
    
    public SmbFile[] listFiles() throws SmbException {
        return this.listFiles("*", 22, null, null);
    }
    
    public SmbFile[] listFiles(final String wildcard) throws SmbException {
        return this.listFiles(wildcard, 22, null, null);
    }
    
    public SmbFile[] listFiles(final SmbFilenameFilter filter) throws SmbException {
        return this.listFiles("*", 22, filter, null);
    }
    
    public SmbFile[] listFiles(final SmbFileFilter filter) throws SmbException {
        return this.listFiles("*", 22, null, filter);
    }
    
    String[] list(final String wildcard, final int searchAttributes, final SmbFilenameFilter fnf, final SmbFileFilter ff) throws SmbException {
        final ArrayList list = new ArrayList();
        try {
            final int hostlen = this.url.getHost().length();
            if (hostlen == 0 || this.share == null) {
                boolean done = false;
                if (hostlen != 0 && this.getType() == 4) {
                    try {
                        this.doMsrpcEnum(list, false, wildcard, searchAttributes, fnf, ff);
                        done = true;
                    }
                    catch (final IOException ioe) {
                        final LogStream log = SmbFile.log;
                        if (LogStream.level >= 3) {
                            ioe.printStackTrace(SmbFile.log);
                        }
                    }
                }
                if (!done) {
                    this.doNetEnum(list, false, wildcard, searchAttributes, fnf, ff);
                }
            }
            else {
                this.doFindFirstNext(list, false, wildcard, searchAttributes, fnf, ff);
            }
        }
        catch (final UnknownHostException uhe) {
            throw new SmbException(this.url.toString(), uhe);
        }
        catch (final MalformedURLException mue) {
            throw new SmbException(this.url.toString(), mue);
        }
        return list.toArray(new String[list.size()]);
    }
    
    SmbFile[] listFiles(String wildcard, int searchAttributes, final SmbFilenameFilter fnf, final SmbFileFilter ff) throws SmbException {
        final ArrayList list = new ArrayList();
        if (ff != null && ff instanceof DosFileFilter) {
            final DosFileFilter dff = (DosFileFilter)ff;
            if (dff.wildcard != null) {
                wildcard = dff.wildcard;
            }
            searchAttributes = dff.attributes;
        }
        try {
            final int hostlen = this.url.getHost().length();
            if (hostlen == 0 || this.share == null) {
                boolean done = false;
                if (hostlen != 0 && this.getType() == 4) {
                    try {
                        this.doMsrpcEnum(list, true, wildcard, searchAttributes, fnf, ff);
                        done = true;
                    }
                    catch (final IOException ioe) {
                        final LogStream log = SmbFile.log;
                        if (LogStream.level >= 3) {
                            ioe.printStackTrace(SmbFile.log);
                        }
                    }
                }
                if (!done) {
                    this.doNetEnum(list, true, wildcard, searchAttributes, fnf, ff);
                }
            }
            else {
                this.doFindFirstNext(list, true, wildcard, searchAttributes, fnf, ff);
            }
        }
        catch (final UnknownHostException uhe) {
            throw new SmbException(this.url.toString(), uhe);
        }
        catch (final MalformedURLException mue) {
            throw new SmbException(this.url.toString(), mue);
        }
        return list.toArray(new SmbFile[list.size()]);
    }
    
    void doMsrpcEnum(final ArrayList list, final boolean files, final String wildcard, final int searchAttributes, final SmbFilenameFilter fnf, final SmbFileFilter ff) throws IOException, UnknownHostException, MalformedURLException {
        final String p = this.url.getPath();
        if (p.lastIndexOf(47) != p.length() - 1) {
            throw new SmbException(this.url.toString() + " directory must end with '/'");
        }
        if (this.getType() != 4) {
            throw new SmbException("The requested list operations is invalid: " + this.url.toString());
        }
        final MsrpcShareEnum rpc = new MsrpcShareEnum(this.url.getHost());
        final DcerpcHandle handle = DcerpcHandle.getHandle("ncacn_np:" + this.url.getHost() + "[\\PIPE\\srvsvc]", this.auth);
        try {
            handle.sendrecv(rpc);
            if (rpc.retval != 0) {
                throw new SmbException(rpc.retval, true);
            }
            final FileEntry[] entries = rpc.getEntries();
            for (int i = 0; i < entries.length; ++i) {
                final FileEntry e = entries[i];
                final String name = e.getName();
                if (fnf == null || fnf.accept(this, name)) {
                    if (name.length() > 0) {
                        final SmbFile f = new SmbFile(this, name, e.getType(), 17, 0L, 0L, 0L);
                        if (ff == null || ff.accept(f)) {
                            if (files) {
                                list.add(f);
                            }
                            else {
                                list.add(name);
                            }
                        }
                    }
                }
            }
        }
        finally {
            try {
                handle.close();
            }
            catch (final IOException ioe) {
                final LogStream log = SmbFile.log;
                if (LogStream.level >= 4) {
                    ioe.printStackTrace(SmbFile.log);
                }
            }
        }
    }
    
    void doNetEnum(final ArrayList list, final boolean files, final String wildcard, final int searchAttributes, final SmbFilenameFilter fnf, final SmbFileFilter ff) throws SmbException, UnknownHostException, MalformedURLException {
        final int listType = (this.url.getHost().length() == 0) ? 0 : this.getType();
        final String p = this.url.getPath();
        if (p.lastIndexOf(47) != p.length() - 1) {
            throw new SmbException(this.url.toString() + " directory must end with '/'");
        }
        switch (listType) {
            case 0: {
                this.connect0();
                final SmbComTransaction req = new NetServerEnum2(this.tree.session.transport.server.oemDomainName, Integer.MIN_VALUE);
                final SmbComTransactionResponse resp = new NetServerEnum2Response();
                break;
            }
            case 2: {
                final SmbComTransaction req = new NetServerEnum2(this.url.getHost(), -1);
                final SmbComTransactionResponse resp = new NetServerEnum2Response();
                break;
            }
            case 4: {
                final SmbComTransaction req = new NetShareEnum();
                final SmbComTransactionResponse resp = new NetShareEnumResponse();
                break;
            }
            default: {
                throw new SmbException("The requested list operations is invalid: " + this.url.toString());
            }
        }
        boolean more;
        do {
            final SmbComTransaction request;
            final NetServerEnum2Response response;
            this.send(request, response);
            more = (response.status == 234);
            if (response.status != 0 && response.status != 234) {
                throw new SmbException(response.status, true);
            }
            for (int n = more ? (response.numEntries - 1) : response.numEntries, i = 0; i < n; ++i) {
                final FileEntry e = response.results[i];
                final String name = e.getName();
                if (fnf == null || fnf.accept(this, name)) {
                    if (name.length() > 0) {
                        final SmbFile f = new SmbFile(this, name, e.getType(), 17, 0L, 0L, 0L);
                        if (ff == null || ff.accept(f)) {
                            if (files) {
                                list.add(f);
                            }
                            else {
                                list.add(name);
                            }
                        }
                    }
                }
            }
            if (listType != 0 && listType != 2) {
                break;
            }
            request.subCommand = -41;
            request.reset(0, response.lastName);
            response.reset();
        } while (more);
    }
    
    void doFindFirstNext(final ArrayList list, final boolean files, final String wildcard, final int searchAttributes, final SmbFilenameFilter fnf, final SmbFileFilter ff) throws SmbException, UnknownHostException, MalformedURLException {
        final String path = this.getUncPath0();
        final String p = this.url.getPath();
        if (p.lastIndexOf(47) != p.length() - 1) {
            throw new SmbException(this.url.toString() + " directory must end with '/'");
        }
        SmbComTransaction req = new Trans2FindFirst2(path, wildcard, searchAttributes);
        final Trans2FindFirst2Response resp = new Trans2FindFirst2Response();
        final LogStream log = SmbFile.log;
        if (LogStream.level >= 3) {
            SmbFile.log.println("doFindFirstNext: " + req.path);
        }
        this.send(req, resp);
        final int sid = resp.sid;
        req = new Trans2FindNext2(sid, resp.resumeKey, resp.lastName);
        resp.subCommand = 2;
        while (true) {
            for (int i = 0; i < resp.numEntries; ++i) {
                final FileEntry e = resp.results[i];
                final String name = e.getName();
                if (name.length() < 3) {
                    final int h = name.hashCode();
                    if (h == SmbFile.HASH_DOT) {
                        continue;
                    }
                    if (h == SmbFile.HASH_DOT_DOT) {
                        continue;
                    }
                }
                if (fnf == null || fnf.accept(this, name)) {
                    if (name.length() > 0) {
                        final SmbFile f = new SmbFile(this, name, 1, e.getAttributes(), e.createTime(), e.lastModified(), e.length());
                        if (ff == null || ff.accept(f)) {
                            if (files) {
                                list.add(f);
                            }
                            else {
                                list.add(name);
                            }
                        }
                    }
                }
            }
            if (resp.isEndOfSearch || resp.numEntries == 0) {
                break;
            }
            req.reset(resp.resumeKey, resp.lastName);
            resp.reset();
            this.send(req, resp);
        }
        this.send(new SmbComFindClose2(sid), this.blank_resp());
    }
    
    public void renameTo(final SmbFile dest) throws SmbException {
        if (this.getUncPath0().length() == 1 || dest.getUncPath0().length() == 1) {
            throw new SmbException("Invalid operation for workgroups, servers, or shares");
        }
        this.connect0();
        dest.connect0();
        if (this.tree.inDfs) {
            this.exists();
            dest.exists();
        }
        if (!this.tree.equals(dest.tree)) {
            throw new SmbException("Invalid operation for workgroups, servers, or shares");
        }
        final LogStream log = SmbFile.log;
        if (LogStream.level >= 3) {
            SmbFile.log.println("renameTo: " + this.unc + " -> " + dest.unc);
        }
        final long n = 0L;
        this.sizeExpiration = n;
        this.attrExpiration = n;
        dest.attrExpiration = 0L;
        this.send(new SmbComRename(this.unc, dest.unc), this.blank_resp());
    }
    
    void copyTo0(final SmbFile dest, final byte[][] b, final int bsize, final WriterThread w, final SmbComReadAndX req, final SmbComReadAndXResponse resp) throws SmbException {
        if (this.attrExpiration < System.currentTimeMillis()) {
            this.attributes = 17;
            this.createTime = 0L;
            this.lastModified = 0L;
            this.isExists = false;
            final Info info = this.queryPath(this.getUncPath0(), 257);
            this.attributes = info.getAttributes();
            this.createTime = info.getCreateTime();
            this.lastModified = info.getLastWriteTime();
            this.isExists = true;
            this.attrExpiration = System.currentTimeMillis() + SmbFile.attrExpirationPeriod;
        }
        if (this.isDirectory()) {
            final String path = dest.getUncPath0();
            if (path.length() > 1) {
                try {
                    dest.mkdir();
                    dest.setPathInformation(this.attributes, this.createTime, this.lastModified);
                }
                catch (final SmbException se) {
                    if (se.getNtStatus() != -1073741790 && se.getNtStatus() != -1073741771) {
                        throw se;
                    }
                }
            }
            final SmbFile[] files = this.listFiles("*", 22, null, null);
            try {
                for (int i = 0; i < files.length; ++i) {
                    final SmbFile ndest = new SmbFile(dest, files[i].getName(), files[i].type, files[i].attributes, files[i].createTime, files[i].lastModified, files[i].size);
                    files[i].copyTo0(ndest, b, bsize, w, req, resp);
                }
            }
            catch (final UnknownHostException uhe) {
                throw new SmbException(this.url.toString(), uhe);
            }
            catch (final MalformedURLException mue) {
                throw new SmbException(this.url.toString(), mue);
            }
        }
        else {
            try {
                this.open(1, 0, 128, 0);
                try {
                    dest.open(82, 258, this.attributes, 0);
                }
                catch (final SmbAuthException sae) {
                    if ((dest.attributes & 0x1) == 0x0) {
                        throw sae;
                    }
                    dest.setPathInformation(dest.attributes & 0xFFFFFFFE, 0L, 0L);
                    dest.open(82, 258, this.attributes, 0);
                }
                int i;
                int off = i = 0;
                while (true) {
                    req.setParam(this.fid, off, bsize);
                    resp.setParam(b[i], 0);
                    this.send(req, resp);
                    synchronized (w) {
                        while (!w.ready) {
                            try {
                                w.wait();
                                continue;
                            }
                            catch (final InterruptedException ie) {
                                throw new SmbException(dest.url.toString(), ie);
                            }
                            break;
                        }
                        if (w.e != null) {
                            throw w.e;
                        }
                        if (resp.dataLength <= 0) {
                            break;
                        }
                        w.write(b[i], resp.dataLength, dest, off);
                    }
                    i = ((i != 1) ? 1 : 0);
                    off += resp.dataLength;
                }
                dest.send(new Trans2SetFileInformation(dest.fid, this.attributes, this.createTime, this.lastModified), new Trans2SetFileInformationResponse());
                dest.close(0L);
            }
            catch (final Exception ex) {
                final LogStream log = SmbFile.log;
                if (LogStream.level > 1) {
                    ex.printStackTrace(SmbFile.log);
                }
            }
            finally {
                this.close();
            }
        }
    }
    
    public void copyTo(final SmbFile dest) throws SmbException {
        if (this.share == null || dest.share == null) {
            throw new SmbException("Invalid operation for workgroups or servers");
        }
        final SmbComReadAndX req = new SmbComReadAndX();
        final SmbComReadAndXResponse resp = new SmbComReadAndXResponse();
        this.connect0();
        dest.connect0();
        if (this.tree.inDfs) {
            this.exists();
            dest.exists();
        }
        try {
            if (this.getAddress().equals(dest.getAddress()) && this.canon.regionMatches(true, 0, dest.canon, 0, Math.min(this.canon.length(), dest.canon.length()))) {
                throw new SmbException("Source and destination paths overlap.");
            }
        }
        catch (final UnknownHostException ex) {}
        final WriterThread w = new WriterThread();
        w.setDaemon(true);
        w.start();
        final SmbTransport t1 = this.tree.session.transport;
        final SmbTransport t2 = dest.tree.session.transport;
        if (t1.snd_buf_size < t2.snd_buf_size) {
            t2.snd_buf_size = t1.snd_buf_size;
        }
        else {
            t1.snd_buf_size = t2.snd_buf_size;
        }
        final int bsize = Math.min(t1.rcv_buf_size - 70, t1.snd_buf_size - 70);
        final byte[][] b = new byte[2][bsize];
        try {
            this.copyTo0(dest, b, bsize, w, req, resp);
        }
        finally {
            w.write(null, -1, null, 0);
        }
    }
    
    public void delete() throws SmbException {
        if (this.tree == null || this.tree.inDfs) {
            this.exists();
        }
        this.getUncPath0();
        this.delete(this.unc);
    }
    
    void delete(final String fileName) throws SmbException {
        if (this.getUncPath0().length() == 1) {
            throw new SmbException("Invalid operation for workgroups, servers, or shares");
        }
        if (System.currentTimeMillis() > this.attrExpiration) {
            this.attributes = 17;
            this.createTime = 0L;
            this.lastModified = 0L;
            this.isExists = false;
            final Info info = this.queryPath(this.getUncPath0(), 257);
            this.attributes = info.getAttributes();
            this.createTime = info.getCreateTime();
            this.lastModified = info.getLastWriteTime();
            this.attrExpiration = System.currentTimeMillis() + SmbFile.attrExpirationPeriod;
            this.isExists = true;
        }
        if ((this.attributes & 0x1) != 0x0) {
            this.setReadWrite();
        }
        final LogStream log = SmbFile.log;
        if (LogStream.level >= 3) {
            SmbFile.log.println("delete: " + fileName);
        }
        if ((this.attributes & 0x10) != 0x0) {
            try {
                final SmbFile[] l = this.listFiles("*", 22, null, null);
                for (int i = 0; i < l.length; ++i) {
                    l[i].delete();
                }
            }
            catch (final SmbException se) {
                if (se.getNtStatus() != -1073741809) {
                    throw se;
                }
            }
            this.send(new SmbComDeleteDirectory(fileName), this.blank_resp());
        }
        else {
            this.send(new SmbComDelete(fileName), this.blank_resp());
        }
        final long n = 0L;
        this.sizeExpiration = n;
        this.attrExpiration = n;
    }
    
    public long length() throws SmbException {
        if (this.sizeExpiration > System.currentTimeMillis()) {
            return this.size;
        }
        if (this.getType() == 8) {
            final int level = 1;
            final Trans2QueryFSInformationResponse response = new Trans2QueryFSInformationResponse(level);
            this.send(new Trans2QueryFSInformation(level), response);
            this.size = response.info.getCapacity();
        }
        else if (this.getUncPath0().length() > 1 && this.type != 16) {
            final Info info = this.queryPath(this.getUncPath0(), 258);
            this.size = info.getSize();
        }
        else {
            this.size = 0L;
        }
        this.sizeExpiration = System.currentTimeMillis() + SmbFile.attrExpirationPeriod;
        return this.size;
    }
    
    public long getDiskFreeSpace() throws SmbException {
        if (this.getType() == 8 || this.type == 1) {
            int level = 1007;
            try {
                return this.queryFSInformation(level);
            }
            catch (final SmbException ex) {
                switch (ex.getNtStatus()) {
                    case -1073741823:
                    case -1073741821: {
                        level = 1;
                        return this.queryFSInformation(level);
                    }
                    default: {
                        throw ex;
                    }
                }
            }
        }
        return 0L;
    }
    
    private long queryFSInformation(final int level) throws SmbException {
        final Trans2QueryFSInformationResponse response = new Trans2QueryFSInformationResponse(level);
        this.send(new Trans2QueryFSInformation(level), response);
        if (this.type == 8) {
            this.size = response.info.getCapacity();
            this.sizeExpiration = System.currentTimeMillis() + SmbFile.attrExpirationPeriod;
        }
        return response.info.getFree();
    }
    
    public void mkdir() throws SmbException {
        final String path = this.getUncPath0();
        if (path.length() == 1) {
            throw new SmbException("Invalid operation for workgroups, servers, or shares");
        }
        final LogStream log = SmbFile.log;
        if (LogStream.level >= 3) {
            SmbFile.log.println("mkdir: " + path);
        }
        this.send(new SmbComCreateDirectory(path), this.blank_resp());
        final long n = 0L;
        this.sizeExpiration = n;
        this.attrExpiration = n;
    }
    
    public void mkdirs() throws SmbException {
        SmbFile parent;
        try {
            parent = new SmbFile(this.getParent(), this.auth);
        }
        catch (final IOException ioe) {
            return;
        }
        if (!parent.exists()) {
            parent.mkdirs();
        }
        this.mkdir();
    }
    
    public void createNewFile() throws SmbException {
        if (this.getUncPath0().length() == 1) {
            throw new SmbException("Invalid operation for workgroups, servers, or shares");
        }
        this.close(this.open0(51, 0, 128, 0), 0L);
    }
    
    void setPathInformation(final int attrs, final long ctime, final long mtime) throws SmbException {
        this.exists();
        final int dir = this.attributes & 0x10;
        final int f = this.open0(1, 256, dir, (dir != 0) ? 1 : 64);
        this.send(new Trans2SetFileInformation(f, attrs | dir, ctime, mtime), new Trans2SetFileInformationResponse());
        this.close(f, 0L);
        this.attrExpiration = 0L;
    }
    
    public void setCreateTime(final long time) throws SmbException {
        if (this.getUncPath0().length() == 1) {
            throw new SmbException("Invalid operation for workgroups, servers, or shares");
        }
        this.setPathInformation(0, time, 0L);
    }
    
    public void setLastModified(final long time) throws SmbException {
        if (this.getUncPath0().length() == 1) {
            throw new SmbException("Invalid operation for workgroups, servers, or shares");
        }
        this.setPathInformation(0, 0L, time);
    }
    
    public int getAttributes() throws SmbException {
        if (this.getUncPath0().length() == 1) {
            return 0;
        }
        this.exists();
        return this.attributes & 0x7FFF;
    }
    
    public void setAttributes(final int attrs) throws SmbException {
        if (this.getUncPath0().length() == 1) {
            throw new SmbException("Invalid operation for workgroups, servers, or shares");
        }
        this.setPathInformation(attrs & 0x30A7, 0L, 0L);
    }
    
    public void setReadOnly() throws SmbException {
        this.setAttributes(this.getAttributes() | 0x1);
    }
    
    public void setReadWrite() throws SmbException {
        this.setAttributes(this.getAttributes() & 0xFFFFFFFE);
    }
    
    public URL toURL() throws MalformedURLException {
        return this.url;
    }
    
    public int hashCode() {
        int hash;
        try {
            hash = this.getAddress().hashCode();
        }
        catch (final UnknownHostException uhe) {
            hash = this.getServer().toUpperCase().hashCode();
        }
        this.getUncPath0();
        return hash + this.canon.toUpperCase().hashCode();
    }
    
    public boolean equals(final Object obj) {
        return obj instanceof SmbFile && obj.hashCode() == this.hashCode();
    }
    
    public String toString() {
        return this.url.toString();
    }
    
    public int getContentLength() {
        try {
            return (int)(this.length() & 0xFFFFFFFFL);
        }
        catch (final SmbException se) {
            return 0;
        }
    }
    
    public long getDate() {
        try {
            return this.lastModified();
        }
        catch (final SmbException se) {
            return 0L;
        }
    }
    
    public long getLastModified() {
        try {
            return this.lastModified();
        }
        catch (final SmbException se) {
            return 0L;
        }
    }
    
    public InputStream getInputStream() throws IOException {
        return new SmbFileInputStream(this);
    }
    
    public OutputStream getOutputStream() throws IOException {
        return new SmbFileOutputStream(this);
    }
    
    private void processAces(final ACE[] aces, final boolean resolveSids) throws IOException {
        final String server = this.getServerWithDfs();
        if (resolveSids) {
            final SID[] sids = new SID[aces.length];
            final String[] names = null;
            for (int ai = 0; ai < aces.length; ++ai) {
                sids[ai] = aces[ai].sid;
            }
            SID.resolveSids(server, this.auth, sids);
        }
        else {
            for (int ai = 0; ai < aces.length; ++ai) {
                aces[ai].sid.origin_server = server;
                aces[ai].sid.origin_auth = this.auth;
            }
        }
    }
    
    public ACE[] getSecurity(final boolean resolveSids) throws IOException {
        final int f = this.open0(1, 131072, 0, this.isDirectory() ? 1 : 0);
        final NtTransQuerySecurityDesc request = new NtTransQuerySecurityDesc(f, 4);
        final NtTransQuerySecurityDescResponse response = new NtTransQuerySecurityDescResponse();
        try {
            this.send(request, response);
        }
        finally {
            this.close(f, 0L);
        }
        final ACE[] aces = response.securityDescriptor.aces;
        this.processAces(aces, resolveSids);
        return aces;
    }
    
    public ACE[] getShareSecurity(final boolean resolveSids) throws IOException {
        final String p = this.url.getPath();
        final MsrpcShareGetInfo rpc = new MsrpcShareGetInfo(this.url.getHost(), this.getShare());
        final DcerpcHandle handle = DcerpcHandle.getHandle("ncacn_np:" + this.url.getHost() + "[\\PIPE\\srvsvc]", this.auth);
        ACE[] aces;
        try {
            handle.sendrecv(rpc);
            if (rpc.retval != 0) {
                throw new SmbException(rpc.retval, true);
            }
            aces = rpc.getSecurity();
            this.processAces(aces, resolveSids);
        }
        finally {
            try {
                handle.close();
            }
            catch (final IOException ioe) {
                final LogStream log = SmbFile.log;
                if (LogStream.level >= 1) {
                    ioe.printStackTrace(SmbFile.log);
                }
            }
        }
        return aces;
    }
    
    public ACE[] getSecurity() throws IOException {
        return this.getSecurity(false);
    }
    
    public SmbFile(final String url, final SmbExtendedAuthenticator authenticator, final int shareAccess) throws MalformedURLException {
        this(url, (NtlmPasswordAuthentication)null, shareAccess);
        this.authenticator = authenticator;
    }
    
    public SmbFile(final String url, final SmbExtendedAuthenticator authenticator) throws MalformedURLException {
        this(url, (NtlmPasswordAuthentication)null);
        this.authenticator = authenticator;
    }
    
    public SmbFile(final URL url, final SmbExtendedAuthenticator authenticator) {
        this(url, (NtlmPasswordAuthentication)null);
        this.authenticator = authenticator;
    }
    
    static {
        HASH_DOT = ".".hashCode();
        HASH_DOT_DOT = "..".hashCode();
        SmbFile.log = LogStream.getInstance();
        try {
            Class.forName("jcifs.Config");
        }
        catch (final ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        SmbFile.attrExpirationPeriod = Config.getLong("jcifs.smb.client.attrExpirationPeriod", 5000L);
    }
    
    class WriterThread extends Thread
    {
        byte[] b;
        int n;
        int off;
        boolean ready;
        SmbFile dest;
        SmbException e;
        boolean useNTSmbs;
        SmbComWriteAndX reqx;
        SmbComWrite req;
        ServerMessageBlock resp;
        
        WriterThread() throws SmbException {
            super("JCIFS-WriterThread");
            this.e = null;
            this.useNTSmbs = SmbFile.this.tree.session.transport.hasCapability(16);
            if (this.useNTSmbs) {
                this.reqx = new SmbComWriteAndX();
                this.resp = new SmbComWriteAndXResponse();
            }
            else {
                this.req = new SmbComWrite();
                this.resp = new SmbComWriteResponse();
            }
            this.ready = false;
        }
        
        synchronized void write(final byte[] b, final int n, final SmbFile dest, final int off) {
            this.b = b;
            this.n = n;
            this.dest = dest;
            this.off = off;
            this.ready = false;
            this.notify();
        }
        
        public void run() {
            synchronized (this) {
                try {
                    while (true) {
                        this.notify();
                        this.ready = true;
                        while (this.ready) {
                            this.wait();
                        }
                        if (this.n == -1) {
                            break;
                        }
                        if (this.useNTSmbs) {
                            this.reqx.setParam(this.dest.fid, this.off, this.n, this.b, 0, this.n);
                            this.dest.send(this.reqx, this.resp);
                        }
                        else {
                            this.req.setParam(this.dest.fid, this.off, this.n, this.b, 0, this.n);
                            this.dest.send(this.req, this.resp);
                        }
                    }
                    return;
                }
                catch (final SmbException e) {
                    this.e = e;
                }
                catch (final Exception x) {
                    this.e = new SmbException("WriterThread", x);
                }
                this.notify();
            }
        }
    }
}
