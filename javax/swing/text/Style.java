package javax.swing.text;

import javax.swing.event.ChangeListener;

public interface Style extends MutableAttributeSet
{
    String getName();
    
    void addChangeListener(final ChangeListener p0);
    
    void removeChangeListener(final ChangeListener p0);
}
