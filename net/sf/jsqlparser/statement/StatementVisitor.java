package net.sf.jsqlparser.statement;

import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.delete.Delete;

public interface StatementVisitor
{
    void visit(final Commit p0);
    
    void visit(final Delete p0);
    
    void visit(final Update p0);
    
    void visit(final Insert p0);
    
    void visit(final Replace p0);
    
    void visit(final Drop p0);
    
    void visit(final Truncate p0);
    
    void visit(final CreateIndex p0);
    
    void visit(final CreateTable p0);
    
    void visit(final CreateView p0);
    
    void visit(final AlterView p0);
    
    void visit(final Alter p0);
    
    void visit(final Statements p0);
    
    void visit(final Execute p0);
    
    void visit(final SetStatement p0);
    
    void visit(final Merge p0);
    
    void visit(final Select p0);
    
    void visit(final Upsert p0);
}
