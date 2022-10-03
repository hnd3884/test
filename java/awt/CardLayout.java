package java.awt;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.ObjectInputStream;
import java.io.ObjectStreamField;
import java.util.Vector;
import java.io.Serializable;

public class CardLayout implements LayoutManager2, Serializable
{
    private static final long serialVersionUID = -4328196481005934313L;
    Vector<Card> vector;
    int currentCard;
    int hgap;
    int vgap;
    private static final ObjectStreamField[] serialPersistentFields;
    
    public CardLayout() {
        this(0, 0);
    }
    
    public CardLayout(final int hgap, final int vgap) {
        this.vector = new Vector<Card>();
        this.currentCard = 0;
        this.hgap = hgap;
        this.vgap = vgap;
    }
    
    public int getHgap() {
        return this.hgap;
    }
    
    public void setHgap(final int hgap) {
        this.hgap = hgap;
    }
    
    public int getVgap() {
        return this.vgap;
    }
    
    public void setVgap(final int vgap) {
        this.vgap = vgap;
    }
    
    @Override
    public void addLayoutComponent(final Component component, Object o) {
        synchronized (component.getTreeLock()) {
            if (o == null) {
                o = "";
            }
            if (!(o instanceof String)) {
                throw new IllegalArgumentException("cannot add to layout: constraint must be a string");
            }
            this.addLayoutComponent((String)o, component);
        }
    }
    
    @Deprecated
    @Override
    public void addLayoutComponent(final String s, final Component comp) {
        synchronized (comp.getTreeLock()) {
            if (!this.vector.isEmpty()) {
                comp.setVisible(false);
            }
            for (int i = 0; i < this.vector.size(); ++i) {
                if (this.vector.get(i).name.equals(s)) {
                    this.vector.get(i).comp = comp;
                    return;
                }
            }
            this.vector.add(new Card(s, comp));
        }
    }
    
    @Override
    public void removeLayoutComponent(final Component component) {
        synchronized (component.getTreeLock()) {
            int i = 0;
            while (i < this.vector.size()) {
                if (this.vector.get(i).comp == component) {
                    if (component.isVisible() && component.getParent() != null) {
                        this.next(component.getParent());
                    }
                    this.vector.remove(i);
                    if (this.currentCard > i) {
                        --this.currentCard;
                        break;
                    }
                    break;
                }
                else {
                    ++i;
                }
            }
        }
    }
    
    @Override
    public Dimension preferredLayoutSize(final Container container) {
        synchronized (container.getTreeLock()) {
            final Insets insets = container.getInsets();
            final int componentCount = container.getComponentCount();
            int width = 0;
            int height = 0;
            for (int i = 0; i < componentCount; ++i) {
                final Dimension preferredSize = container.getComponent(i).getPreferredSize();
                if (preferredSize.width > width) {
                    width = preferredSize.width;
                }
                if (preferredSize.height > height) {
                    height = preferredSize.height;
                }
            }
            return new Dimension(insets.left + insets.right + width + this.hgap * 2, insets.top + insets.bottom + height + this.vgap * 2);
        }
    }
    
    @Override
    public Dimension minimumLayoutSize(final Container container) {
        synchronized (container.getTreeLock()) {
            final Insets insets = container.getInsets();
            final int componentCount = container.getComponentCount();
            int width = 0;
            int height = 0;
            for (int i = 0; i < componentCount; ++i) {
                final Dimension minimumSize = container.getComponent(i).getMinimumSize();
                if (minimumSize.width > width) {
                    width = minimumSize.width;
                }
                if (minimumSize.height > height) {
                    height = minimumSize.height;
                }
            }
            return new Dimension(insets.left + insets.right + width + this.hgap * 2, insets.top + insets.bottom + height + this.vgap * 2);
        }
    }
    
    @Override
    public Dimension maximumLayoutSize(final Container container) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    @Override
    public float getLayoutAlignmentX(final Container container) {
        return 0.5f;
    }
    
    @Override
    public float getLayoutAlignmentY(final Container container) {
        return 0.5f;
    }
    
    @Override
    public void invalidateLayout(final Container container) {
    }
    
    @Override
    public void layoutContainer(final Container container) {
        synchronized (container.getTreeLock()) {
            final Insets insets = container.getInsets();
            final int componentCount = container.getComponentCount();
            boolean b = false;
            for (int i = 0; i < componentCount; ++i) {
                final Component component = container.getComponent(i);
                component.setBounds(this.hgap + insets.left, this.vgap + insets.top, container.width - (this.hgap * 2 + insets.left + insets.right), container.height - (this.vgap * 2 + insets.top + insets.bottom));
                if (component.isVisible()) {
                    b = true;
                }
            }
            if (!b && componentCount > 0) {
                container.getComponent(0).setVisible(true);
            }
        }
    }
    
    void checkLayout(final Container container) {
        if (container.getLayout() != this) {
            throw new IllegalArgumentException("wrong parent for CardLayout");
        }
    }
    
    public void first(final Container container) {
        synchronized (container.getTreeLock()) {
            this.checkLayout(container);
            final int componentCount = container.getComponentCount();
            for (int i = 0; i < componentCount; ++i) {
                final Component component = container.getComponent(i);
                if (component.isVisible()) {
                    component.setVisible(false);
                    break;
                }
            }
            if (componentCount > 0) {
                this.currentCard = 0;
                container.getComponent(0).setVisible(true);
                container.validate();
            }
        }
    }
    
    public void next(final Container container) {
        synchronized (container.getTreeLock()) {
            this.checkLayout(container);
            for (int componentCount = container.getComponentCount(), i = 0; i < componentCount; ++i) {
                final Component component = container.getComponent(i);
                if (component.isVisible()) {
                    component.setVisible(false);
                    this.currentCard = (i + 1) % componentCount;
                    container.getComponent(this.currentCard).setVisible(true);
                    container.validate();
                    return;
                }
            }
            this.showDefaultComponent(container);
        }
    }
    
    public void previous(final Container container) {
        synchronized (container.getTreeLock()) {
            this.checkLayout(container);
            for (int componentCount = container.getComponentCount(), i = 0; i < componentCount; ++i) {
                final Component component = container.getComponent(i);
                if (component.isVisible()) {
                    component.setVisible(false);
                    this.currentCard = ((i > 0) ? (i - 1) : (componentCount - 1));
                    container.getComponent(this.currentCard).setVisible(true);
                    container.validate();
                    return;
                }
            }
            this.showDefaultComponent(container);
        }
    }
    
    void showDefaultComponent(final Container container) {
        if (container.getComponentCount() > 0) {
            this.currentCard = 0;
            container.getComponent(0).setVisible(true);
            container.validate();
        }
    }
    
    public void last(final Container container) {
        synchronized (container.getTreeLock()) {
            this.checkLayout(container);
            final int componentCount = container.getComponentCount();
            for (int i = 0; i < componentCount; ++i) {
                final Component component = container.getComponent(i);
                if (component.isVisible()) {
                    component.setVisible(false);
                    break;
                }
            }
            if (componentCount > 0) {
                this.currentCard = componentCount - 1;
                container.getComponent(this.currentCard).setVisible(true);
                container.validate();
            }
        }
    }
    
    public void show(final Container container, final String s) {
        synchronized (container.getTreeLock()) {
            this.checkLayout(container);
            Component comp = null;
            for (int size = this.vector.size(), i = 0; i < size; ++i) {
                final Card card = this.vector.get(i);
                if (card.name.equals(s)) {
                    comp = card.comp;
                    this.currentCard = i;
                    break;
                }
            }
            if (comp != null && !comp.isVisible()) {
                for (int componentCount = container.getComponentCount(), j = 0; j < componentCount; ++j) {
                    final Component component = container.getComponent(j);
                    if (component.isVisible()) {
                        component.setVisible(false);
                        break;
                    }
                }
                comp.setVisible(true);
                container.validate();
            }
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[hgap=" + this.hgap + ",vgap=" + this.vgap + "]";
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        this.hgap = fields.get("hgap", 0);
        this.vgap = fields.get("vgap", 0);
        if (fields.defaulted("vector")) {
            final Hashtable hashtable = (Hashtable)fields.get("tab", null);
            this.vector = new Vector<Card>();
            if (hashtable != null && !hashtable.isEmpty()) {
                final Enumeration keys = hashtable.keys();
                while (keys.hasMoreElements()) {
                    final String s = (String)keys.nextElement();
                    final Component component = hashtable.get(s);
                    this.vector.add(new Card(s, component));
                    if (component.isVisible()) {
                        this.currentCard = this.vector.size() - 1;
                    }
                }
            }
        }
        else {
            this.vector = (Vector)fields.get("vector", null);
            this.currentCard = fields.get("currentCard", 0);
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final Hashtable hashtable = new Hashtable();
        for (int size = this.vector.size(), i = 0; i < size; ++i) {
            final Card card = this.vector.get(i);
            hashtable.put(card.name, card.comp);
        }
        final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
        putFields.put("hgap", this.hgap);
        putFields.put("vgap", this.vgap);
        putFields.put("vector", this.vector);
        putFields.put("currentCard", this.currentCard);
        putFields.put("tab", hashtable);
        objectOutputStream.writeFields();
    }
    
    static {
        serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("tab", Hashtable.class), new ObjectStreamField("hgap", Integer.TYPE), new ObjectStreamField("vgap", Integer.TYPE), new ObjectStreamField("vector", Vector.class), new ObjectStreamField("currentCard", Integer.TYPE) };
    }
    
    class Card implements Serializable
    {
        static final long serialVersionUID = 6640330810709497518L;
        public String name;
        public Component comp;
        
        public Card(final String name, final Component comp) {
            this.name = name;
            this.comp = comp;
        }
    }
}
