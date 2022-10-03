package com.sun.jndi.dns;

import javax.naming.Name;
import java.util.Date;
import java.lang.ref.SoftReference;

class ZoneNode extends NameNode
{
    private SoftReference<NameNode> contentsRef;
    private long serialNumber;
    private Date expiration;
    
    ZoneNode(final String s) {
        super(s);
        this.contentsRef = null;
        this.serialNumber = -1L;
        this.expiration = null;
    }
    
    @Override
    protected NameNode newNameNode(final String s) {
        return new ZoneNode(s);
    }
    
    synchronized void depopulate() {
        this.contentsRef = null;
        this.serialNumber = -1L;
    }
    
    synchronized boolean isPopulated() {
        return this.getContents() != null;
    }
    
    synchronized NameNode getContents() {
        return (this.contentsRef != null) ? this.contentsRef.get() : null;
    }
    
    synchronized boolean isExpired() {
        return this.expiration != null && this.expiration.before(new Date());
    }
    
    ZoneNode getDeepestPopulated(final DnsName dnsName) {
        ZoneNode zoneNode = this;
        ZoneNode zoneNode2 = this.isPopulated() ? this : null;
        for (int i = 1; i < dnsName.size(); ++i) {
            zoneNode = (ZoneNode)zoneNode.get(dnsName.getKey(i));
            if (zoneNode == null) {
                break;
            }
            if (zoneNode.isPopulated()) {
                zoneNode2 = zoneNode;
            }
        }
        return zoneNode2;
    }
    
    NameNode populate(final DnsName dnsName, final ResourceRecords resourceRecords) {
        final NameNode nameNode = new NameNode(null);
        for (int i = 0; i < resourceRecords.answer.size(); ++i) {
            final ResourceRecord resourceRecord = resourceRecords.answer.elementAt(i);
            final DnsName name = resourceRecord.getName();
            if (name.size() > dnsName.size() && name.startsWith(dnsName)) {
                final NameNode add = nameNode.add(name, dnsName.size());
                if (resourceRecord.getType() == 2) {
                    add.setZoneCut(true);
                }
            }
        }
        final ResourceRecord resourceRecord2 = resourceRecords.answer.firstElement();
        synchronized (this) {
            this.contentsRef = new SoftReference<NameNode>(nameNode);
            this.serialNumber = getSerialNumber(resourceRecord2);
            this.setExpiration(getMinimumTtl(resourceRecord2));
            return nameNode;
        }
    }
    
    private void setExpiration(final long n) {
        this.expiration = new Date(System.currentTimeMillis() + 1000L * n);
    }
    
    private static long getMinimumTtl(final ResourceRecord resourceRecord) {
        final String s = (String)resourceRecord.getRdata();
        return Long.parseLong(s.substring(s.lastIndexOf(32) + 1));
    }
    
    int compareSerialNumberTo(final ResourceRecord resourceRecord) {
        return ResourceRecord.compareSerialNumbers(this.serialNumber, getSerialNumber(resourceRecord));
    }
    
    private static long getSerialNumber(final ResourceRecord resourceRecord) {
        final String s = (String)resourceRecord.getRdata();
        int n = s.length();
        int n2 = -1;
        for (int i = 0; i < 5; ++i) {
            n2 = n;
            n = s.lastIndexOf(32, n2 - 1);
        }
        return Long.parseLong(s.substring(n + 1, n2));
    }
}
