package java.awt.peer;

import java.awt.CheckboxGroup;

public interface CheckboxPeer extends ComponentPeer
{
    void setState(final boolean p0);
    
    void setCheckboxGroup(final CheckboxGroup p0);
    
    void setLabel(final String p0);
}
