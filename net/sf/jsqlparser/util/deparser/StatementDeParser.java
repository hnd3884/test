package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.Commit;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.SetStatement;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.truncate.Truncate;
import java.util.Iterator;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.StatementVisitor;

public class StatementDeParser implements StatementVisitor
{
    private ExpressionDeParser expressionDeParser;
    private SelectDeParser selectDeParser;
    private StringBuilder buffer;
    
    public StatementDeParser(final StringBuilder buffer) {
        this(new ExpressionDeParser(), new SelectDeParser(), buffer);
    }
    
    public StatementDeParser(final ExpressionDeParser expressionDeParser, final SelectDeParser selectDeParser, final StringBuilder buffer) {
        this.expressionDeParser = expressionDeParser;
        this.selectDeParser = selectDeParser;
        this.buffer = buffer;
    }
    
    @Override
    public void visit(final CreateIndex createIndex) {
        final CreateIndexDeParser createIndexDeParser = new CreateIndexDeParser(this.buffer);
        createIndexDeParser.deParse(createIndex);
    }
    
    @Override
    public void visit(final CreateTable createTable) {
        final CreateTableDeParser createTableDeParser = new CreateTableDeParser(this.buffer);
        createTableDeParser.deParse(createTable);
    }
    
    @Override
    public void visit(final CreateView createView) {
        final CreateViewDeParser createViewDeParser = new CreateViewDeParser(this.buffer);
        createViewDeParser.deParse(createView);
    }
    
    @Override
    public void visit(final AlterView alterView) {
        final AlterViewDeParser alterViewDeParser = new AlterViewDeParser(this.buffer);
        alterViewDeParser.deParse(alterView);
    }
    
    @Override
    public void visit(final Delete delete) {
        this.selectDeParser.setBuffer(this.buffer);
        this.expressionDeParser.setSelectVisitor(this.selectDeParser);
        this.expressionDeParser.setBuffer(this.buffer);
        this.selectDeParser.setExpressionVisitor(this.expressionDeParser);
        final DeleteDeParser deleteDeParser = new DeleteDeParser(this.expressionDeParser, this.buffer);
        deleteDeParser.deParse(delete);
    }
    
    @Override
    public void visit(final Drop drop) {
        final DropDeParser dropDeParser = new DropDeParser(this.buffer);
        dropDeParser.deParse(drop);
    }
    
    @Override
    public void visit(final Insert insert) {
        this.selectDeParser.setBuffer(this.buffer);
        this.expressionDeParser.setSelectVisitor(this.selectDeParser);
        this.expressionDeParser.setBuffer(this.buffer);
        this.selectDeParser.setExpressionVisitor(this.expressionDeParser);
        final InsertDeParser insertDeParser = new InsertDeParser(this.expressionDeParser, this.selectDeParser, this.buffer);
        insertDeParser.deParse(insert);
    }
    
    @Override
    public void visit(final Replace replace) {
        this.selectDeParser.setBuffer(this.buffer);
        this.expressionDeParser.setSelectVisitor(this.selectDeParser);
        this.expressionDeParser.setBuffer(this.buffer);
        this.selectDeParser.setExpressionVisitor(this.expressionDeParser);
        final ReplaceDeParser replaceDeParser = new ReplaceDeParser(this.expressionDeParser, this.selectDeParser, this.buffer);
        replaceDeParser.deParse(replace);
    }
    
    @Override
    public void visit(final Select select) {
        this.selectDeParser.setBuffer(this.buffer);
        this.expressionDeParser.setSelectVisitor(this.selectDeParser);
        this.expressionDeParser.setBuffer(this.buffer);
        this.selectDeParser.setExpressionVisitor(this.expressionDeParser);
        if (select.getWithItemsList() != null && !select.getWithItemsList().isEmpty()) {
            this.buffer.append("WITH ");
            final Iterator<WithItem> iter = select.getWithItemsList().iterator();
            while (iter.hasNext()) {
                final WithItem withItem = iter.next();
                withItem.accept(this.selectDeParser);
                if (iter.hasNext()) {
                    this.buffer.append(",");
                }
                this.buffer.append(" ");
            }
        }
        select.getSelectBody().accept(this.selectDeParser);
    }
    
    @Override
    public void visit(final Truncate truncate) {
    }
    
    @Override
    public void visit(final Update update) {
        this.selectDeParser.setBuffer(this.buffer);
        this.expressionDeParser.setSelectVisitor(this.selectDeParser);
        this.expressionDeParser.setBuffer(this.buffer);
        final UpdateDeParser updateDeParser = new UpdateDeParser(this.expressionDeParser, this.selectDeParser, this.buffer);
        this.selectDeParser.setExpressionVisitor(this.expressionDeParser);
        updateDeParser.deParse(update);
    }
    
    public StringBuilder getBuffer() {
        return this.buffer;
    }
    
    public void setBuffer(final StringBuilder buffer) {
        this.buffer = buffer;
    }
    
    @Override
    public void visit(final Alter alter) {
        final AlterDeParser alterDeParser = new AlterDeParser(this.buffer);
        alterDeParser.deParse(alter);
    }
    
    @Override
    public void visit(final Statements stmts) {
        stmts.accept(this);
    }
    
    @Override
    public void visit(final Execute execute) {
        this.selectDeParser.setBuffer(this.buffer);
        this.expressionDeParser.setSelectVisitor(this.selectDeParser);
        this.expressionDeParser.setBuffer(this.buffer);
        final ExecuteDeParser executeDeParser = new ExecuteDeParser(this.expressionDeParser, this.buffer);
        this.selectDeParser.setExpressionVisitor(this.expressionDeParser);
        executeDeParser.deParse(execute);
    }
    
    @Override
    public void visit(final SetStatement set) {
        this.selectDeParser.setBuffer(this.buffer);
        this.expressionDeParser.setSelectVisitor(this.selectDeParser);
        this.expressionDeParser.setBuffer(this.buffer);
        final SetStatementDeParser setStatementDeparser = new SetStatementDeParser(this.expressionDeParser, this.buffer);
        this.selectDeParser.setExpressionVisitor(this.expressionDeParser);
        setStatementDeparser.deParse(set);
    }
    
    @Override
    public void visit(final Merge merge) {
        this.buffer.append(merge.toString());
    }
    
    @Override
    public void visit(final Commit commit) {
        this.buffer.append(commit.toString());
    }
    
    @Override
    public void visit(final Upsert upsert) {
        this.selectDeParser.setBuffer(this.buffer);
        this.expressionDeParser.setSelectVisitor(this.selectDeParser);
        this.expressionDeParser.setBuffer(this.buffer);
        this.selectDeParser.setExpressionVisitor(this.expressionDeParser);
        final UpsertDeParser upsertDeParser = new UpsertDeParser(this.expressionDeParser, this.selectDeParser, this.buffer);
        upsertDeParser.deParse(upsert);
    }
}
