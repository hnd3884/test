package org.apache.poi.ss.formula.eval;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class RefListEval implements ValueEval
{
    private final List<ValueEval> list;
    
    public RefListEval(final ValueEval v1, final ValueEval v2) {
        this.list = new ArrayList<ValueEval>();
        this.add(v1);
        this.add(v2);
    }
    
    private void add(final ValueEval v) {
        if (v instanceof RefListEval) {
            this.list.addAll(((RefListEval)v).list);
        }
        else {
            this.list.add(v);
        }
    }
    
    public List<ValueEval> getList() {
        return this.list;
    }
}
