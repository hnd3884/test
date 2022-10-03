package com.adventnet.ds.query;

import java.util.Vector;
import java.util.List;
import java.util.Comparator;

class RowComparator implements Comparator
{
    int sortSize;
    private List sortColumns;
    private Vector orderVect;
    int[] order;
    
    RowComparator(final List sortColumns, final Vector orderVect, final int[] order) {
        this.sortSize = 0;
        this.sortColumns = null;
        this.orderVect = new Vector();
        this.order = null;
        this.sortColumns = sortColumns;
        this.orderVect = orderVect;
        this.order = order;
        if (sortColumns != null) {
            this.sortSize = sortColumns.size();
        }
    }
    
    @Override
    public int compare(final Object vectObj1, final Object vectObj2) {
        int retVal = 0;
        final Vector vect1 = (Vector)vectObj1;
        final Vector vect2 = (Vector)vectObj2;
        for (int i = 0; i < this.sortSize; ++i) {
            final Object obj1 = vect1.get(this.order[i] - 1);
            final Object obj2 = vect2.get(this.order[i] - 1);
            final boolean isAscending = this.sortColumns.get(i).isAscending();
            retVal = this.getComparedValue(obj1, obj2, this.orderVect.get(i), isAscending);
            if (retVal != 0) {
                return retVal;
            }
        }
        return retVal;
    }
    
    private int getComparedValue(final Object obj1, final Object obj2, final Vector vect, final boolean isAscending) {
        if (vect == null) {
            if (obj1 instanceof String) {
                return this.getValue(DataSet.coll.compare(obj1, obj2), isAscending);
            }
            if (obj1 instanceof Boolean) {
                return this.getComparedValue(String.valueOf(obj1), String.valueOf(obj2), isAscending);
            }
            return this.getComparedValue(obj1, obj2, isAscending);
        }
        else {
            final int firstInd = vect.indexOf(obj1);
            final int secInd = vect.indexOf(obj2);
            if (firstInd == secInd) {
                return 0;
            }
            if (firstInd > secInd) {
                return 1;
            }
            return -1;
        }
    }
    
    private int getComparedValue(final Object obj1, final Object obj2, final boolean isAscending) {
        if (obj1 == obj2) {
            return 0;
        }
        if (obj1 == null) {
            return this.getValue(-1, isAscending);
        }
        if (obj2 == null) {
            return this.getValue(1, isAscending);
        }
        return this.getValue(((Comparable)obj1).compareTo(obj2), isAscending);
    }
    
    private int getValue(final int ret, final boolean isAscending) {
        if (isAscending) {
            return ret;
        }
        return -ret;
    }
}
