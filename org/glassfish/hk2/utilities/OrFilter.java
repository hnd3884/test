package org.glassfish.hk2.utilities;

import java.util.Iterator;
import org.glassfish.hk2.utilities.general.GeneralUtilities;
import org.glassfish.hk2.api.IndexedFilter;
import org.glassfish.hk2.api.Descriptor;
import java.util.ArrayList;
import org.glassfish.hk2.api.Filter;

public class OrFilter implements Filter
{
    private final ArrayList<Filter> allFilters;
    
    public OrFilter(final Filter... filters) {
        this.allFilters = new ArrayList<Filter>(filters.length);
        for (final Filter f : filters) {
            if (f != null) {
                this.allFilters.add(f);
            }
        }
    }
    
    @Override
    public boolean matches(final Descriptor d) {
        for (final Filter filter : this.allFilters) {
            if (filter instanceof IndexedFilter) {
                final IndexedFilter iFilter = (IndexedFilter)filter;
                final String name = iFilter.getName();
                if (name != null && !GeneralUtilities.safeEquals((Object)name, (Object)d.getName())) {
                    continue;
                }
                final String contract = iFilter.getAdvertisedContract();
                if (contract != null && !d.getAdvertisedContracts().contains(contract)) {
                    continue;
                }
            }
            if (filter.matches(d)) {
                return true;
            }
        }
        return false;
    }
}
