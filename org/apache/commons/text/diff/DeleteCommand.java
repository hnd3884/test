package org.apache.commons.text.diff;

public class DeleteCommand<T> extends EditCommand<T>
{
    public DeleteCommand(final T object) {
        super(object);
    }
    
    @Override
    public void accept(final CommandVisitor<T> visitor) {
        visitor.visitDeleteCommand(this.getObject());
    }
}
