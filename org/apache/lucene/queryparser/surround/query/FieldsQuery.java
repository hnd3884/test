package org.apache.lucene.queryparser.surround.query;

import java.util.Iterator;
import org.apache.lucene.search.Query;
import java.util.ArrayList;
import java.util.List;

public class FieldsQuery extends SrndQuery
{
    private SrndQuery q;
    private List<String> fieldNames;
    private final char fieldOp;
    private final String OrOperatorName = "OR";
    
    public FieldsQuery(final SrndQuery q, final List<String> fieldNames, final char fieldOp) {
        this.q = q;
        this.fieldNames = fieldNames;
        this.fieldOp = fieldOp;
    }
    
    public FieldsQuery(final SrndQuery q, final String fieldName, final char fieldOp) {
        this.q = q;
        (this.fieldNames = new ArrayList<String>()).add(fieldName);
        this.fieldOp = fieldOp;
    }
    
    @Override
    public boolean isFieldsSubQueryAcceptable() {
        return false;
    }
    
    public Query makeLuceneQueryNoBoost(final BasicQueryFactory qf) {
        if (this.fieldNames.size() == 1) {
            return this.q.makeLuceneQueryFieldNoBoost(this.fieldNames.get(0), qf);
        }
        final List<SrndQuery> queries = new ArrayList<SrndQuery>();
        final Iterator<String> fni = this.getFieldNames().listIterator();
        while (fni.hasNext()) {
            final SrndQuery qc = this.q.clone();
            queries.add(new FieldsQuery(qc, fni.next(), this.fieldOp));
        }
        final OrQuery oq = new OrQuery(queries, true, "OR");
        return oq.makeLuceneQueryField(null, qf);
    }
    
    @Override
    public Query makeLuceneQueryFieldNoBoost(final String fieldName, final BasicQueryFactory qf) {
        return this.makeLuceneQueryNoBoost(qf);
    }
    
    public List<String> getFieldNames() {
        return this.fieldNames;
    }
    
    public char getFieldOperator() {
        return this.fieldOp;
    }
    
    @Override
    public String toString() {
        final StringBuilder r = new StringBuilder();
        r.append("(");
        this.fieldNamesToString(r);
        r.append(this.q.toString());
        r.append(")");
        return r.toString();
    }
    
    protected void fieldNamesToString(final StringBuilder r) {
        final Iterator<String> fni = this.getFieldNames().listIterator();
        while (fni.hasNext()) {
            r.append(fni.next());
            r.append(this.getFieldOperator());
        }
    }
}
