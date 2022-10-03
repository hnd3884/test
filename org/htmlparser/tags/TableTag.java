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

public class TableTag extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return TableTag.mIds;
    }
    
    public String[] getEndTagEnders() {
        return TableTag.mEndTagEnders;
    }
    
    public TableRow[] getRows() {
        NodeList kids = this.getChildren();
        TableRow[] ret;
        if (null != kids) {
            final NodeClassFilter cls = new NodeClassFilter(TableTag.class);
            final HasParentFilter recursion = new HasParentFilter(null);
            final NodeFilter filter = new OrFilter(new AndFilter(cls, new IsEqualFilter(this)), new AndFilter(new NotFilter(cls), recursion));
            recursion.setParentFilter(filter);
            kids = kids.extractAllNodesThatMatch(new AndFilter(new NodeClassFilter(TableRow.class), filter), true);
            ret = new TableRow[kids.size()];
            kids.copyToNodeArray(ret);
        }
        else {
            ret = new TableRow[0];
        }
        return ret;
    }
    
    public int getRowCount() {
        return this.getRows().length;
    }
    
    public TableRow getRow(final int index) {
        final TableRow[] rows = this.getRows();
        TableRow ret;
        if (index < rows.length) {
            ret = rows[index];
        }
        else {
            ret = null;
        }
        return ret;
    }
    
    public String toString() {
        return "TableTag\n********\n" + this.toHtml();
    }
    
    static {
        mIds = new String[] { "TABLE" };
        mEndTagEnders = new String[] { "BODY", "HTML" };
    }
}
