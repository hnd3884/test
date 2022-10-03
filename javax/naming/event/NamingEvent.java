package javax.naming.event;

import javax.naming.Binding;
import java.util.EventObject;

public class NamingEvent extends EventObject
{
    public static final int OBJECT_ADDED = 0;
    public static final int OBJECT_REMOVED = 1;
    public static final int OBJECT_RENAMED = 2;
    public static final int OBJECT_CHANGED = 3;
    protected Object changeInfo;
    protected int type;
    protected Binding oldBinding;
    protected Binding newBinding;
    private static final long serialVersionUID = -7126752885365133499L;
    
    public NamingEvent(final EventContext eventContext, final int type, final Binding newBinding, final Binding oldBinding, final Object changeInfo) {
        super(eventContext);
        this.type = type;
        this.oldBinding = oldBinding;
        this.newBinding = newBinding;
        this.changeInfo = changeInfo;
    }
    
    public int getType() {
        return this.type;
    }
    
    public EventContext getEventContext() {
        return (EventContext)this.getSource();
    }
    
    public Binding getOldBinding() {
        return this.oldBinding;
    }
    
    public Binding getNewBinding() {
        return this.newBinding;
    }
    
    public Object getChangeInfo() {
        return this.changeInfo;
    }
    
    public void dispatch(final NamingListener namingListener) {
        switch (this.type) {
            case 0: {
                ((NamespaceChangeListener)namingListener).objectAdded(this);
                break;
            }
            case 1: {
                ((NamespaceChangeListener)namingListener).objectRemoved(this);
                break;
            }
            case 2: {
                ((NamespaceChangeListener)namingListener).objectRenamed(this);
                break;
            }
            case 3: {
                ((ObjectChangeListener)namingListener).objectChanged(this);
                break;
            }
        }
    }
}
