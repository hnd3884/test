package sun.nio.ch;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Iterator;
import java.nio.channels.MembershipKey;
import java.net.NetworkInterface;
import java.util.List;
import java.net.InetAddress;
import java.util.Map;

class MembershipRegistry
{
    private Map<InetAddress, List<MembershipKeyImpl>> groups;
    
    MembershipRegistry() {
        this.groups = null;
    }
    
    MembershipKey checkMembership(final InetAddress inetAddress, final NetworkInterface networkInterface, final InetAddress inetAddress2) {
        if (this.groups != null) {
            final List list = this.groups.get(inetAddress);
            if (list != null) {
                for (final MembershipKeyImpl membershipKeyImpl : list) {
                    if (membershipKeyImpl.networkInterface().equals(networkInterface)) {
                        if (inetAddress2 == null) {
                            if (membershipKeyImpl.sourceAddress() == null) {
                                return membershipKeyImpl;
                            }
                            throw new IllegalStateException("Already a member to receive all packets");
                        }
                        else {
                            if (membershipKeyImpl.sourceAddress() == null) {
                                throw new IllegalStateException("Already have source-specific membership");
                            }
                            if (inetAddress2.equals(membershipKeyImpl.sourceAddress())) {
                                return membershipKeyImpl;
                            }
                            continue;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    void add(final MembershipKeyImpl membershipKeyImpl) {
        final InetAddress group = membershipKeyImpl.group();
        List<MembershipKeyImpl> list;
        if (this.groups == null) {
            this.groups = new HashMap<InetAddress, List<MembershipKeyImpl>>();
            list = null;
        }
        else {
            list = this.groups.get(group);
        }
        if (list == null) {
            list = new LinkedList<MembershipKeyImpl>();
            this.groups.put(group, list);
        }
        list.add(membershipKeyImpl);
    }
    
    void remove(final MembershipKeyImpl membershipKeyImpl) {
        final InetAddress group = membershipKeyImpl.group();
        final List list = this.groups.get(group);
        if (list != null) {
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                if (iterator.next() == membershipKeyImpl) {
                    iterator.remove();
                    break;
                }
            }
            if (list.isEmpty()) {
                this.groups.remove(group);
            }
        }
    }
    
    void invalidateAll() {
        if (this.groups != null) {
            final Iterator<InetAddress> iterator = this.groups.keySet().iterator();
            while (iterator.hasNext()) {
                final Iterator iterator2 = this.groups.get(iterator.next()).iterator();
                while (iterator2.hasNext()) {
                    ((MembershipKeyImpl)iterator2.next()).invalidate();
                }
            }
        }
    }
}
