package com.zoho.security.policy;

public class IPRange
{
    private long fromIP;
    private long toIP;
    
    public IPRange(final String ip) {
        this.fromIP = -1L;
        this.toIP = -1L;
        if (ip.contains("-")) {
            final String[] ips = ip.split("-");
            if (ips.length != 2) {
                throw new SecurityPolicyException("INVALID_IP_FORMAT");
            }
            final String fromIP = ips[0];
            IPUtil.checkIP(fromIP);
            this.fromIP = IPUtil.ipV42Long(fromIP);
            final String toIP = ips[1];
            IPUtil.checkIP(toIP);
            this.toIP = IPUtil.ipV42Long(toIP);
        }
    }
    
    public IPRange(final String fromIP, final String toIP) {
        this.fromIP = -1L;
        this.toIP = -1L;
        this.fromIP = IPUtil.ipV42Long(fromIP);
        this.toIP = IPUtil.ipV42Long(toIP);
    }
    
    public long getFromIP() {
        return this.fromIP;
    }
    
    public long getToIP() {
        return this.toIP;
    }
    
    public boolean isIPInRange(final String ip) {
        return this.isIPInRange(IPUtil.ipV42Long(ip));
    }
    
    public boolean isIPInRange(final long ip) {
        return ip <= this.toIP && ip >= this.fromIP;
    }
}
