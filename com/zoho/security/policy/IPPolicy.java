package com.zoho.security.policy;

import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class IPPolicy extends SecurityPolicyHandler
{
    private List<String> ipList;
    private List<IPRange> ipRanges;
    
    public IPPolicy(final String ip) {
        super(POLICY.IP.name());
        this.ipList = null;
        this.ipRanges = null;
        this.setIPs(ip);
    }
    
    private void setIPs(final String ipInput) {
        if (ipInput.contains(",")) {
            final String[] split;
            final String[] ips = split = ipInput.split(",");
            for (final String ip : split) {
                this.addIP(ip);
            }
        }
        else {
            this.addIP(ipInput);
        }
    }
    
    private void addIP(final String ip) {
        if (ip.contains("-")) {
            if (this.ipRanges == null) {
                this.ipRanges = new ArrayList<IPRange>();
            }
            this.ipRanges.add(new IPRange(ip));
        }
        else {
            if (this.ipList == null) {
                this.ipList = new ArrayList<String>();
            }
            IPUtil.checkIP(ip);
            this.ipList.add(ip);
        }
    }
    
    @Override
    public boolean isAccessAllowed(final HttpServletRequest request) {
        final String remoteIP = request.getRemoteAddr();
        if (remoteIP == null) {
            return false;
        }
        if (this.ipList != null && this.ipList.contains(remoteIP)) {
            return true;
        }
        final long ip = IPUtil.ipV42Long(remoteIP);
        if (this.ipRanges != null) {
            for (final IPRange range : this.ipRanges) {
                if (range.isIPInRange(ip)) {
                    return true;
                }
            }
        }
        return false;
    }
}
