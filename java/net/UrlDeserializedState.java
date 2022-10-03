package java.net;

final class UrlDeserializedState
{
    private final String protocol;
    private final String host;
    private final int port;
    private final String authority;
    private final String file;
    private final String ref;
    private final int hashCode;
    
    public UrlDeserializedState(final String protocol, final String host, final int port, final String authority, final String file, final String ref, final int hashCode) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.authority = authority;
        this.file = file;
        this.ref = ref;
        this.hashCode = hashCode;
    }
    
    String getProtocol() {
        return this.protocol;
    }
    
    String getHost() {
        return this.host;
    }
    
    String getAuthority() {
        return this.authority;
    }
    
    int getPort() {
        return this.port;
    }
    
    String getFile() {
        return this.file;
    }
    
    String getRef() {
        return this.ref;
    }
    
    int getHashCode() {
        return this.hashCode;
    }
    
    String reconstituteUrlString() {
        int n = this.protocol.length() + 1;
        if (this.authority != null && this.authority.length() > 0) {
            n += 2 + this.authority.length();
        }
        if (this.file != null) {
            n += this.file.length();
        }
        if (this.ref != null) {
            n += 1 + this.ref.length();
        }
        final StringBuilder sb = new StringBuilder(n);
        sb.append(this.protocol);
        sb.append(":");
        if (this.authority != null && this.authority.length() > 0) {
            sb.append("//");
            sb.append(this.authority);
        }
        if (this.file != null) {
            sb.append(this.file);
        }
        if (this.ref != null) {
            sb.append("#");
            sb.append(this.ref);
        }
        return sb.toString();
    }
}
