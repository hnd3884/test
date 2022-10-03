package javax.swing;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import sun.awt.SunToolkit;
import java.awt.Container;
import java.awt.Component;
import java.util.Comparator;
import java.io.Serializable;

public class LayoutFocusTraversalPolicy extends SortingFocusTraversalPolicy implements Serializable
{
    private static final SwingDefaultFocusTraversalPolicy fitnessTestPolicy;
    
    public LayoutFocusTraversalPolicy() {
        super(new LayoutComparator());
    }
    
    LayoutFocusTraversalPolicy(final Comparator<? super Component> comparator) {
        super(comparator);
    }
    
    @Override
    public Component getComponentAfter(final Container container, final Component component) {
        if (container == null || component == null) {
            throw new IllegalArgumentException("aContainer and aComponent cannot be null");
        }
        final Comparator<? super Component> comparator = this.getComparator();
        if (comparator instanceof LayoutComparator) {
            ((LayoutComparator)comparator).setComponentOrientation(container.getComponentOrientation());
        }
        return super.getComponentAfter(container, component);
    }
    
    @Override
    public Component getComponentBefore(final Container container, final Component component) {
        if (container == null || component == null) {
            throw new IllegalArgumentException("aContainer and aComponent cannot be null");
        }
        final Comparator<? super Component> comparator = this.getComparator();
        if (comparator instanceof LayoutComparator) {
            ((LayoutComparator)comparator).setComponentOrientation(container.getComponentOrientation());
        }
        return super.getComponentBefore(container, component);
    }
    
    @Override
    public Component getFirstComponent(final Container container) {
        if (container == null) {
            throw new IllegalArgumentException("aContainer cannot be null");
        }
        final Comparator<? super Component> comparator = this.getComparator();
        if (comparator instanceof LayoutComparator) {
            ((LayoutComparator)comparator).setComponentOrientation(container.getComponentOrientation());
        }
        return super.getFirstComponent(container);
    }
    
    @Override
    public Component getLastComponent(final Container container) {
        if (container == null) {
            throw new IllegalArgumentException("aContainer cannot be null");
        }
        final Comparator<? super Component> comparator = this.getComparator();
        if (comparator instanceof LayoutComparator) {
            ((LayoutComparator)comparator).setComponentOrientation(container.getComponentOrientation());
        }
        return super.getLastComponent(container);
    }
    
    @Override
    protected boolean accept(final Component component) {
        if (!super.accept(component)) {
            return false;
        }
        if (SunToolkit.isInstanceOf(component, "javax.swing.JTable")) {
            return true;
        }
        if (SunToolkit.isInstanceOf(component, "javax.swing.JComboBox")) {
            final JComboBox comboBox = (JComboBox)component;
            return comboBox.getUI().isFocusTraversable(comboBox);
        }
        if (component instanceof JComponent) {
            InputMap inputMap;
            for (inputMap = ((JComponent)component).getInputMap(0, false); inputMap != null && inputMap.size() == 0; inputMap = inputMap.getParent()) {}
            if (inputMap != null) {
                return true;
            }
        }
        return LayoutFocusTraversalPolicy.fitnessTestPolicy.accept(component);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(this.getComparator());
        objectOutputStream.writeBoolean(this.getImplicitDownCycleTraversal());
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.setComparator((Comparator<? super Component>)objectInputStream.readObject());
        this.setImplicitDownCycleTraversal(objectInputStream.readBoolean());
    }
    
    static {
        fitnessTestPolicy = new SwingDefaultFocusTraversalPolicy();
    }
}
