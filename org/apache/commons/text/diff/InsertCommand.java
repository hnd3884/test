package org.apache.commons.text.diff;

public class InsertCommand<T> extends EditCommand<T>
{
    public InsertCommand(final T object) {
        super(object);
    }
    
    @Override
    public void accept(final CommandVisitor<T> visitor) {
        visitor.visitInsertCommand(this.getObject());
    }
}
