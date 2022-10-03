package net.sf.jsqlparser.util.deparser;

import java.util.List;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;

public class AlterViewDeParser
{
    private StringBuilder buffer;
    private SelectVisitor selectVisitor;
    
    public AlterViewDeParser(final StringBuilder buffer) {
        final SelectDeParser selectDeParser = new SelectDeParser();
        selectDeParser.setBuffer(buffer);
        final ExpressionDeParser expressionDeParser = new ExpressionDeParser(selectDeParser, buffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        this.selectVisitor = selectDeParser;
        this.buffer = buffer;
    }
    
    public AlterViewDeParser(final StringBuilder buffer, final SelectVisitor selectVisitor) {
        this.buffer = buffer;
        this.selectVisitor = selectVisitor;
    }
    
    public void deParse(final AlterView alterView) {
        this.buffer.append("ALTER ");
        this.buffer.append("VIEW ").append(alterView.getView().getFullyQualifiedName());
        if (alterView.getColumnNames() != null) {
            this.buffer.append(PlainSelect.getStringList(alterView.getColumnNames(), true, true));
        }
        this.buffer.append(" AS ");
        alterView.getSelectBody().accept(this.selectVisitor);
    }
    
    public StringBuilder getBuffer() {
        return this.buffer;
    }
    
    public void setBuffer(final StringBuilder buffer) {
        this.buffer = buffer;
    }
}
