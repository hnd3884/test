package net.sf.jsqlparser.statement.select;

import java.util.List;

public class WithItem implements SelectBody
{
    private String name;
    private List<SelectItem> withItemList;
    private SelectBody selectBody;
    private boolean recursive;
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public boolean isRecursive() {
        return this.recursive;
    }
    
    public void setRecursive(final boolean recursive) {
        this.recursive = recursive;
    }
    
    public SelectBody getSelectBody() {
        return this.selectBody;
    }
    
    public void setSelectBody(final SelectBody selectBody) {
        this.selectBody = selectBody;
    }
    
    public List<SelectItem> getWithItemList() {
        return this.withItemList;
    }
    
    public void setWithItemList(final List<SelectItem> withItemList) {
        this.withItemList = withItemList;
    }
    
    @Override
    public String toString() {
        return (this.recursive ? "RECURSIVE " : "") + this.name + ((this.withItemList != null) ? (" " + PlainSelect.getStringList(this.withItemList, true, true)) : "") + " AS (" + this.selectBody + ")";
    }
    
    @Override
    public void accept(final SelectVisitor visitor) {
        visitor.visit(this);
    }
}
