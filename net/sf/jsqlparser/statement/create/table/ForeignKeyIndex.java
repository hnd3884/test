package net.sf.jsqlparser.statement.create.table;

import net.sf.jsqlparser.statement.select.PlainSelect;
import java.util.List;
import net.sf.jsqlparser.schema.Table;

public class ForeignKeyIndex extends NamedConstraint
{
    private Table table;
    private List<String> referencedColumnNames;
    private String onDeleteReferenceOption;
    private String onUpdateReferenceOption;
    
    public Table getTable() {
        return this.table;
    }
    
    public void setTable(final Table table) {
        this.table = table;
    }
    
    public List<String> getReferencedColumnNames() {
        return this.referencedColumnNames;
    }
    
    public void setReferencedColumnNames(final List<String> referencedColumnNames) {
        this.referencedColumnNames = referencedColumnNames;
    }
    
    public String getOnDeleteReferenceOption() {
        return this.onDeleteReferenceOption;
    }
    
    public void setOnDeleteReferenceOption(final String onDeleteReferenceOption) {
        this.onDeleteReferenceOption = onDeleteReferenceOption;
    }
    
    public String getOnUpdateReferenceOption() {
        return this.onUpdateReferenceOption;
    }
    
    public void setOnUpdateReferenceOption(final String onUpdateReferenceOption) {
        this.onUpdateReferenceOption = onUpdateReferenceOption;
    }
    
    @Override
    public String toString() {
        String referenceOptions = "";
        if (this.onDeleteReferenceOption != null) {
            referenceOptions = referenceOptions + " ON DELETE " + this.onDeleteReferenceOption;
        }
        if (this.onUpdateReferenceOption != null) {
            referenceOptions = referenceOptions + " ON UPDATE " + this.onUpdateReferenceOption;
        }
        return super.toString() + " REFERENCES " + this.table + PlainSelect.getStringList(this.getReferencedColumnNames(), true, true) + referenceOptions;
    }
}
