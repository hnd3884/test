package java.beans.beancontext;

import java.util.EventListener;

public interface BeanContextMembershipListener extends EventListener
{
    void childrenAdded(final BeanContextMembershipEvent p0);
    
    void childrenRemoved(final BeanContextMembershipEvent p0);
}
