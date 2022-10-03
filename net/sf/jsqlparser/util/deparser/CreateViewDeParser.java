package net.sf.jsqlparser.util.deparser;

import java.util.List;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;

public class CreateViewDeParser
{
    private StringBuilder buffer;
    private SelectVisitor selectVisitor;
    
    public CreateViewDeParser(final StringBuilder buffer) {
        final SelectDeParser selectDeParser = new SelectDeParser();
        selectDeParser.setBuffer(buffer);
        final ExpressionDeParser expressionDeParser = new ExpressionDeParser(selectDeParser, buffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        this.selectVisitor = selectDeParser;
        this.buffer = buffer;
    }
    
    public CreateViewDeParser(final StringBuilder buffer, final SelectVisitor selectVisitor) {
        this.buffer = buffer;
        this.selectVisitor = selectVisitor;
    }
    
    public void deParse(final CreateView createView) {
        this.buffer.append("CREATE ");
        if (createView.isOrReplace()) {
            this.buffer.append("OR REPLACE ");
        }
        if (createView.isMaterialized()) {
            this.buffer.append("MATERIALIZED ");
        }
        this.buffer.append("VIEW ").append(createView.getView().getFullyQualifiedName());
        if (createView.getColumnNames() != null) {
            this.buffer.append(PlainSelect.getStringList(createView.getColumnNames(), true, true));
        }
        this.buffer.append(" AS ");
        createView.getSelectBody().accept(this.selectVisitor);
    }
    
    public StringBuilder getBuffer() {
        return this.buffer;
    }
    
    public void setBuffer(final StringBuilder buffer) {
        this.buffer = buffer;
    }
}
