package com.adventnet.ds.query;

import com.adventnet.ds.query.util.QueryUtil;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class IndexHintClause implements Cloneable
{
    private IndexHint idxHint;
    private List<String> indexes;
    private SelectQuery.Clause idxHintFor;
    private boolean isKey;
    
    public IndexHintClause(final IndexHint idxHint, final List<String> idxList) {
        this(idxHint, idxList, null);
    }
    
    public IndexHintClause(final IndexHint idxHint, final List<String> idxList, final SelectQuery.Clause idxHintFor) {
        this.idxHint = null;
        this.indexes = new ArrayList<String>();
        this.idxHintFor = null;
        this.isKey = false;
        this.idxHint = idxHint;
        this.indexes = idxList;
        this.idxHintFor = idxHintFor;
    }
    
    public IndexHintClause(final IndexHint idxHint, final List<String> idxList, final boolean isKey) {
        this(idxHint, idxList, null, isKey);
    }
    
    public IndexHintClause(final IndexHint idxHint, final List<String> idxList, final SelectQuery.Clause idxHintFor, final boolean isKey) {
        this(idxHint, idxList, idxHintFor);
        this.isKey = isKey;
    }
    
    public boolean isKey() {
        return this.isKey;
    }
    
    public IndexHint getIndexHint() {
        return this.idxHint;
    }
    
    public SelectQuery.Clause getIndexHintFor() {
        return this.idxHintFor;
    }
    
    public List<String> getIndexes() {
        return this.indexes;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final IndexHintClause idxHint = (IndexHintClause)super.clone();
        idxHint.indexes = new ArrayList<String>(this.indexes);
        return idxHint;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("\n\t\t<IndexHintConstants>" + this.idxHint + "</IndexHintConstants>");
        buf.append("\n\t\t<IndexHintFor>" + this.idxHintFor + "</IndexHintFor>");
        buf.append("\n\t\t<IndexNameList>" + this.indexes + "</IndexNameList>");
        return buf.toString();
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (!(object instanceof IndexHintClause)) {
            return false;
        }
        final IndexHintClause hintClause = (IndexHintClause)object;
        return hintClause.getIndexHint().equals(this.idxHint) && hintClause.isKey() == this.isKey && QueryUtil.compareList(hintClause.getIndexes(), this.indexes, true) && (hintClause.getIndexHintFor() == null || this.idxHintFor != null) && (hintClause.getIndexHintFor() != null || this.idxHintFor == null) && (hintClause.getIndexHintFor() == null || this.idxHintFor == null || hintClause.getIndexHintFor().equals(this.idxHintFor));
    }
    
    @Override
    public int hashCode() {
        return this.idxHint.hashCode() + ((this.idxHintFor != null) ? (2 * this.idxHintFor.hashCode()) : 0) + ((this.indexes != null) ? (3 * this.indexes.hashCode()) : 0);
    }
    
    public enum IndexHint
    {
        USE("use"), 
        IGNORE("ignore"), 
        FORCE("force");
        
        String indexHint;
        
        private IndexHint(final String hint) {
            this.indexHint = null;
            this.indexHint = hint;
        }
        
        public boolean equals(final String hint) {
            return this.indexHint.equalsIgnoreCase(hint);
        }
    }
}
