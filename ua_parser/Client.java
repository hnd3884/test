package ua_parser;

public class Client
{
    public final UserAgent userAgent;
    public final OS os;
    public final Device device;
    
    public Client(final UserAgent userAgent, final OS os, final Device device) {
        this.userAgent = userAgent;
        this.os = os;
        this.device = device;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Client)) {
            return false;
        }
        final Client o = (Client)other;
        return ((this.userAgent != null && this.userAgent.equals(o.userAgent)) || this.userAgent == o.userAgent) && ((this.os != null && this.os.equals(o.os)) || this.os == o.os) && ((this.device != null && this.device.equals(o.device)) || this.device == o.device);
    }
    
    @Override
    public int hashCode() {
        int h = (this.userAgent == null) ? 0 : this.userAgent.hashCode();
        h += ((this.os == null) ? 0 : this.os.hashCode());
        h += ((this.device == null) ? 0 : this.device.hashCode());
        return h;
    }
    
    @Override
    public String toString() {
        return String.format("{\"user_agent\": %s, \"os\": %s, \"device\": %s}", this.userAgent, this.os, this.device);
    }
}
