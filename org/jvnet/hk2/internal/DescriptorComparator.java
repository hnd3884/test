package org.jvnet.hk2.internal;

import java.io.Serializable;
import org.glassfish.hk2.api.Descriptor;
import java.util.Comparator;

public class DescriptorComparator implements Comparator<Descriptor>, Serializable
{
    private static final long serialVersionUID = 4454509124508404602L;
    
    @Override
    public int compare(final Descriptor o1, final Descriptor o2) {
        final int o1Ranking = o1.getRanking();
        final int o2Ranking = o2.getRanking();
        if (o1Ranking < o2Ranking) {
            return 1;
        }
        if (o1Ranking > o2Ranking) {
            return -1;
        }
        final long o1LocatorId = o1.getLocatorId();
        final long o2LocatorId = o2.getLocatorId();
        if (o1LocatorId < o2LocatorId) {
            return 1;
        }
        if (o1LocatorId > o2LocatorId) {
            return -1;
        }
        final long o1ServiceId = o1.getServiceId();
        final long o2ServiceId = o2.getServiceId();
        if (o1ServiceId > o2ServiceId) {
            return 1;
        }
        if (o1ServiceId < o2ServiceId) {
            return -1;
        }
        return 0;
    }
}
