package javax.swing;

public class ComponentInputMap extends InputMap
{
    private JComponent component;
    
    public ComponentInputMap(final JComponent component) {
        this.component = component;
        if (component == null) {
            throw new IllegalArgumentException("ComponentInputMaps must be associated with a non-null JComponent");
        }
    }
    
    @Override
    public void setParent(final InputMap parent) {
        if (this.getParent() == parent) {
            return;
        }
        if (parent != null && (!(parent instanceof ComponentInputMap) || ((ComponentInputMap)parent).getComponent() != this.getComponent())) {
            throw new IllegalArgumentException("ComponentInputMaps must have a parent ComponentInputMap associated with the same component");
        }
        super.setParent(parent);
        this.getComponent().componentInputMapChanged(this);
    }
    
    public JComponent getComponent() {
        return this.component;
    }
    
    @Override
    public void put(final KeyStroke keyStroke, final Object o) {
        super.put(keyStroke, o);
        if (this.getComponent() != null) {
            this.getComponent().componentInputMapChanged(this);
        }
    }
    
    @Override
    public void remove(final KeyStroke keyStroke) {
        super.remove(keyStroke);
        if (this.getComponent() != null) {
            this.getComponent().componentInputMapChanged(this);
        }
    }
    
    @Override
    public void clear() {
        final int size = this.size();
        super.clear();
        if (size > 0 && this.getComponent() != null) {
            this.getComponent().componentInputMapChanged(this);
        }
    }
}
