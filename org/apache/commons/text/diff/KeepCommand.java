package org.apache.commons.text.diff;

public class KeepCommand<T> extends EditCommand<T>
{
    public KeepCommand(final T object) {
        super(object);
    }
    
    @Override
    public void accept(final CommandVisitor<T> visitor) {
        visitor.visitKeepCommand(this.getObject());
    }
}
