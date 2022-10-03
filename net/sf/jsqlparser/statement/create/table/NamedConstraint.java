package net.sf.jsqlparser.statement.create.table;

import java.util.List;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class NamedConstraint extends Index
{
    @Override
    public String toString() {
        final String idxSpecText = PlainSelect.getStringList(this.getIndexSpec(), false, false);
        return ((this.getName() != null) ? ("CONSTRAINT " + this.getName() + " ") : "") + this.getType() + " " + PlainSelect.getStringList(this.getColumnsNames(), true, true) + ("".equals(idxSpecText) ? "" : (" " + idxSpecText));
    }
}
