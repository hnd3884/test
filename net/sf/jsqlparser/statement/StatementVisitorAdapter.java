package net.sf.jsqlparser.statement;

import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.execute.Execute;
import java.util.Iterator;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.Select;

public class StatementVisitorAdapter implements StatementVisitor
{
    @Override
    public void visit(final Commit commit) {
    }
    
    @Override
    public void visit(final Select select) {
    }
    
    @Override
    public void visit(final Delete delete) {
    }
    
    @Override
    public void visit(final Update update) {
    }
    
    @Override
    public void visit(final Insert insert) {
    }
    
    @Override
    public void visit(final Replace replace) {
    }
    
    @Override
    public void visit(final Drop drop) {
    }
    
    @Override
    public void visit(final Truncate truncate) {
    }
    
    @Override
    public void visit(final CreateIndex createIndex) {
    }
    
    @Override
    public void visit(final CreateTable createTable) {
    }
    
    @Override
    public void visit(final CreateView createView) {
    }
    
    @Override
    public void visit(final Alter alter) {
    }
    
    @Override
    public void visit(final Statements stmts) {
        for (final Statement statement : stmts.getStatements()) {
            statement.accept(this);
        }
    }
    
    @Override
    public void visit(final Execute execute) {
    }
    
    @Override
    public void visit(final SetStatement set) {
    }
    
    @Override
    public void visit(final Merge merge) {
    }
    
    @Override
    public void visit(final AlterView alterView) {
    }
    
    @Override
    public void visit(final Upsert upsert) {
    }
}
