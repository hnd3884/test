package org.htmlparser.tags;

import org.htmlparser.util.NodeList;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.Node;
import org.htmlparser.filters.IsEqualFilter;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.NodeClassFilter;

public class TableRow extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEnders;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return TableRow.mIds;
    }
    
    public String[] getEnders() {
        return TableRow.mEnders;
    }
    
    public String[] getEndTagEnders() {
        return TableRow.mEndTagEnders;
    }
    
    public TableColumn[] getColumns() {
        NodeList kids = this.getChildren();
        TableColumn[] ret;
        if (null != kids) {
            final NodeClassFilter cls = new NodeClassFilter(TableRow.class);
            final HasParentFilter recursion = new HasParentFilter(null);
            final NodeFilter filter = new OrFilter(new AndFilter(cls, new IsEqualFilter(this)), new AndFilter(new NotFilter(cls), recursion));
            recursion.setParentFilter(filter);
            kids = kids.extractAllNodesThatMatch(new AndFilter(new NodeClassFilter(TableColumn.class), filter), true);
            ret = new TableColumn[kids.size()];
            kids.copyToNodeArray(ret);
        }
        else {
            ret = new TableColumn[0];
        }
        return ret;
    }
    
    public int getColumnCount() {
        return this.getColumns().length;
    }
    
    public TableHeader[] getHeaders() {
        NodeList kids = this.getChildren();
        TableHeader[] ret;
        if (null != kids) {
            final NodeClassFilter cls = new NodeClassFilter(TableRow.class);
            final HasParentFilter recursion = new HasParentFilter(null);
            final NodeFilter filter = new OrFilter(new AndFilter(cls, new IsEqualFilter(this)), new AndFilter(new NotFilter(cls), recursion));
            recursion.setParentFilter(filter);
            kids = kids.extractAllNodesThatMatch(new AndFilter(new NodeClassFilter(TableHeader.class), filter), true);
            ret = new TableHeader[kids.size()];
            kids.copyToNodeArray(ret);
        }
        else {
            ret = new TableHeader[0];
        }
        return ret;
    }
    
    public int getHeaderCount() {
        return this.getHeaders().length;
    }
    
    public boolean hasHeader() {
        return 0 != this.getHeaderCount();
    }
    
    static {
        mIds = new String[] { "TR" };
        mEnders = new String[] { "TBODY", "TFOOT", "THEAD" };
        mEndTagEnders = new String[] { "TBODY", "TFOOT", "THEAD", "TABLE" };
    }
}
