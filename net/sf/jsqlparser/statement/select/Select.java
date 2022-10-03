package net.sf.jsqlparser.statement.select;

import java.util.Iterator;
import net.sf.jsqlparser.statement.StatementVisitor;
import java.util.List;
import net.sf.jsqlparser.statement.Statement;

public class Select implements Statement
{
    private SelectBody selectBody;
    private List<WithItem> withItemsList;
    
    @Override
    public void accept(final StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }
    
    public SelectBody getSelectBody() {
        return this.selectBody;
    }
    
    public void setSelectBody(final SelectBody body) {
        this.selectBody = body;
    }
    
    @Override
    public String toString() {
        final StringBuilder retval = new StringBuilder();
        if (this.withItemsList != null && !this.withItemsList.isEmpty()) {
            retval.append("WITH ");
            final Iterator<WithItem> iter = this.withItemsList.iterator();
            while (iter.hasNext()) {
                final WithItem withItem = iter.next();
                retval.append(withItem);
                if (iter.hasNext()) {
                    retval.append(",");
                }
                retval.append(" ");
            }
        }
        retval.append(this.selectBody);
        return retval.toString();
    }
    
    public List<WithItem> getWithItemsList() {
        return this.withItemsList;
    }
    
    public void setWithItemsList(final List<WithItem> withItemsList) {
        this.withItemsList = withItemsList;
    }
}
