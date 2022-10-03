package com.adventnet.tools.update.util;

import java.util.Collection;
import java.util.Vector;
import java.util.Hashtable;
import java.util.List;

public class GroupingFunction
{
    private List groupNames;
    private List groupingFilters;
    
    public GroupingFunction() {
        this.groupNames = null;
        this.groupingFilters = null;
    }
    
    public void setFilters(final List groupNames, final List groupingFilters) {
        if (groupNames.size() != groupingFilters.size()) {
            throw new IllegalArgumentException("The Group Names and Grouping Filters are not of equal size");
        }
        this.groupNames = groupNames;
        this.groupingFilters = groupingFilters;
    }
    
    public Hashtable groupElements(final List elements) {
        final Hashtable table = new Hashtable();
        if (this.groupNames != null) {
            final EnhancedFileFilter[] filters = this.groupingFilters.toArray(new EnhancedFileFilter[0]);
            final int noOfElements = elements.size();
            final int noOfGroups = this.groupNames.size();
            for (int i = 0; i < noOfElements; ++i) {
                final String fileName = elements.get(i);
                for (int j = 0; j < noOfGroups; ++j) {
                    final String groupName = this.groupNames.get(j);
                    if (filters[j].accept(fileName)) {
                        Vector vect = table.get(groupName);
                        if (vect == null) {
                            vect = new Vector();
                            table.put(groupName, vect);
                        }
                        vect.add(fileName);
                        break;
                    }
                }
            }
        }
        else {
            final Vector vect2 = new Vector();
            vect2.addAll(elements);
            table.put("File List", vect2);
        }
        return table;
    }
}
