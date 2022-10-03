package org.apache.lucene.queryparser.surround.query;

import org.apache.lucene.search.Query;

abstract class RewriteQuery<SQ extends SrndQuery> extends Query
{
    protected final SQ srndQuery;
    protected final String fieldName;
    protected final BasicQueryFactory qf;
    
    RewriteQuery(final SQ srndQuery, final String fieldName, final BasicQueryFactory qf) {
        this.srndQuery = srndQuery;
        this.fieldName = fieldName;
        this.qf = qf;
    }
    
    public String toString(final String field) {
        return this.getClass().getName() + (field.isEmpty() ? "" : ("(unused: " + field + ")")) + "(" + this.fieldName + ", " + this.srndQuery.toString() + ", " + this.qf.toString() + ")";
    }
    
    public int hashCode() {
        return super.hashCode() ^ this.fieldName.hashCode() ^ this.qf.hashCode() ^ this.srndQuery.hashCode();
    }
    
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }
        final RewriteQuery<SQ> other = (RewriteQuery<SQ>)obj;
        return super.equals(obj) && this.fieldName.equals(other.fieldName) && this.qf.equals(other.qf) && this.srndQuery.equals(other.srndQuery);
    }
}
