package net.sf.jsqlparser.statement.select;

import java.util.List;

public class Distinct
{
    private List<SelectItem> onSelectItems;
    private boolean useUnique;
    
    public Distinct() {
        this.useUnique = false;
    }
    
    public Distinct(final boolean useUnique) {
        this.useUnique = false;
        this.useUnique = useUnique;
    }
    
    public List<SelectItem> getOnSelectItems() {
        return this.onSelectItems;
    }
    
    public void setOnSelectItems(final List<SelectItem> list) {
        this.onSelectItems = list;
    }
    
    public boolean isUseUnique() {
        return this.useUnique;
    }
    
    public void setUseUnique(final boolean useUnique) {
        this.useUnique = useUnique;
    }
    
    @Override
    public String toString() {
        String sql = this.useUnique ? "UNIQUE" : "DISTINCT";
        if (this.onSelectItems != null && !this.onSelectItems.isEmpty()) {
            sql = sql + " ON (" + PlainSelect.getStringList(this.onSelectItems) + ")";
        }
        return sql;
    }
}
