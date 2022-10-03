package javax.swing;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Collection;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DesktopPaneUI;
import java.awt.FocusTraversalPolicy;
import java.awt.Component;
import java.awt.Container;
import java.util.List;
import javax.accessibility.Accessible;

public class JDesktopPane extends JLayeredPane implements Accessible
{
    private static final String uiClassID = "DesktopPaneUI";
    transient DesktopManager desktopManager;
    private transient JInternalFrame selectedFrame;
    public static final int LIVE_DRAG_MODE = 0;
    public static final int OUTLINE_DRAG_MODE = 1;
    private int dragMode;
    private boolean dragModeSet;
    private transient List<JInternalFrame> framesCache;
    private boolean componentOrderCheckingEnabled;
    private boolean componentOrderChanged;
    
    public JDesktopPane() {
        this.selectedFrame = null;
        this.dragMode = 0;
        this.dragModeSet = false;
        this.componentOrderCheckingEnabled = true;
        this.componentOrderChanged = false;
        this.setUIProperty("opaque", Boolean.TRUE);
        this.setFocusCycleRoot(true);
        this.setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
            @Override
            public Component getDefaultComponent(final Container container) {
                final JInternalFrame[] allFrames = JDesktopPane.this.getAllFrames();
                Component defaultComponent = null;
                for (final JInternalFrame internalFrame : allFrames) {
                    defaultComponent = internalFrame.getFocusTraversalPolicy().getDefaultComponent(internalFrame);
                    if (defaultComponent != null) {
                        break;
                    }
                }
                return defaultComponent;
            }
        });
        this.updateUI();
    }
    
    public DesktopPaneUI getUI() {
        return (DesktopPaneUI)this.ui;
    }
    
    public void setUI(final DesktopPaneUI ui) {
        super.setUI(ui);
    }
    
    public void setDragMode(final int dragMode) {
        this.firePropertyChange("dragMode", this.dragMode, this.dragMode = dragMode);
        this.dragModeSet = true;
    }
    
    public int getDragMode() {
        return this.dragMode;
    }
    
    public DesktopManager getDesktopManager() {
        return this.desktopManager;
    }
    
    public void setDesktopManager(final DesktopManager desktopManager) {
        this.firePropertyChange("desktopManager", this.desktopManager, this.desktopManager = desktopManager);
    }
    
    @Override
    public void updateUI() {
        this.setUI((DesktopPaneUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "DesktopPaneUI";
    }
    
    public JInternalFrame[] getAllFrames() {
        return getAllFrames(this).toArray(new JInternalFrame[0]);
    }
    
    private static Collection<JInternalFrame> getAllFrames(final Container container) {
        final LinkedHashSet set = new LinkedHashSet();
        for (int componentCount = container.getComponentCount(), i = 0; i < componentCount; ++i) {
            final Component component = container.getComponent(i);
            if (component instanceof JInternalFrame) {
                set.add(component);
            }
            else if (component instanceof JInternalFrame.JDesktopIcon) {
                final JInternalFrame internalFrame = ((JInternalFrame.JDesktopIcon)component).getInternalFrame();
                if (internalFrame != null) {
                    set.add(internalFrame);
                }
            }
            else if (component instanceof Container) {
                set.addAll(getAllFrames((Container)component));
            }
        }
        return set;
    }
    
    public JInternalFrame getSelectedFrame() {
        return this.selectedFrame;
    }
    
    public void setSelectedFrame(final JInternalFrame selectedFrame) {
        this.selectedFrame = selectedFrame;
    }
    
    public JInternalFrame[] getAllFramesInLayer(final int n) {
        final Collection<JInternalFrame> allFrames = getAllFrames(this);
        final Iterator<JInternalFrame> iterator = allFrames.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getLayer() != n) {
                iterator.remove();
            }
        }
        return allFrames.toArray(new JInternalFrame[0]);
    }
    
    private List<JInternalFrame> getFrames() {
        final TreeSet set = new TreeSet();
        for (int i = 0; i < this.getComponentCount(); ++i) {
            final Component component = this.getComponent(i);
            if (component instanceof JInternalFrame) {
                set.add(new ComponentPosition((JInternalFrame)component, this.getLayer(component), i));
            }
            else if (component instanceof JInternalFrame.JDesktopIcon) {
                final JInternalFrame internalFrame = ((JInternalFrame.JDesktopIcon)component).getInternalFrame();
                set.add(new ComponentPosition(internalFrame, this.getLayer((Component)internalFrame), i));
            }
        }
        final ArrayList list = new ArrayList(set.size());
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            list.add((Object)((ComponentPosition)iterator.next()).component);
        }
        return (List<JInternalFrame>)list;
    }
    
    private JInternalFrame getNextFrame(final JInternalFrame internalFrame, final boolean b) {
        this.verifyFramesCache();
        if (internalFrame == null) {
            return this.getTopInternalFrame();
        }
        int index = this.framesCache.indexOf(internalFrame);
        if (index == -1 || this.framesCache.size() == 1) {
            return null;
        }
        if (b) {
            if (++index == this.framesCache.size()) {
                index = 0;
            }
        }
        else if (--index == -1) {
            index = this.framesCache.size() - 1;
        }
        return this.framesCache.get(index);
    }
    
    JInternalFrame getNextFrame(final JInternalFrame internalFrame) {
        return this.getNextFrame(internalFrame, true);
    }
    
    private JInternalFrame getTopInternalFrame() {
        if (this.framesCache.size() == 0) {
            return null;
        }
        return this.framesCache.get(0);
    }
    
    private void updateFramesCache() {
        this.framesCache = this.getFrames();
    }
    
    private void verifyFramesCache() {
        if (this.componentOrderChanged) {
            this.componentOrderChanged = false;
            this.updateFramesCache();
        }
    }
    
    @Override
    public void remove(final Component component) {
        super.remove(component);
        this.updateFramesCache();
    }
    
    public JInternalFrame selectFrame(final boolean b) {
        final JInternalFrame selectedFrame = this.getSelectedFrame();
        final JInternalFrame nextFrame = this.getNextFrame(selectedFrame, b);
        if (nextFrame == null) {
            return null;
        }
        this.setComponentOrderCheckingEnabled(false);
        if (b && selectedFrame != null) {
            selectedFrame.moveToBack();
        }
        try {
            nextFrame.setSelected(true);
        }
        catch (final PropertyVetoException ex) {}
        this.setComponentOrderCheckingEnabled(true);
        return nextFrame;
    }
    
    void setComponentOrderCheckingEnabled(final boolean componentOrderCheckingEnabled) {
        this.componentOrderCheckingEnabled = componentOrderCheckingEnabled;
    }
    
    @Override
    protected void addImpl(final Component component, final Object o, final int n) {
        super.addImpl(component, o, n);
        if (this.componentOrderCheckingEnabled && (component instanceof JInternalFrame || component instanceof JInternalFrame.JDesktopIcon)) {
            this.componentOrderChanged = true;
        }
    }
    
    @Override
    public void remove(final int n) {
        if (this.componentOrderCheckingEnabled) {
            final Component component = this.getComponent(n);
            if (component instanceof JInternalFrame || component instanceof JInternalFrame.JDesktopIcon) {
                this.componentOrderChanged = true;
            }
        }
        super.remove(n);
    }
    
    @Override
    public void removeAll() {
        if (this.componentOrderCheckingEnabled) {
            for (int componentCount = this.getComponentCount(), i = 0; i < componentCount; ++i) {
                final Component component = this.getComponent(i);
                if (component instanceof JInternalFrame || component instanceof JInternalFrame.JDesktopIcon) {
                    this.componentOrderChanged = true;
                    break;
                }
            }
        }
        super.removeAll();
    }
    
    @Override
    public void setComponentZOrder(final Component component, final int n) {
        super.setComponentZOrder(component, n);
        if (this.componentOrderCheckingEnabled && (component instanceof JInternalFrame || component instanceof JInternalFrame.JDesktopIcon)) {
            this.componentOrderChanged = true;
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("DesktopPaneUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    void setUIProperty(final String s, final Object o) {
        if (s == "dragMode") {
            if (!this.dragModeSet) {
                this.setDragMode((int)o);
                this.dragModeSet = false;
            }
        }
        else {
            super.setUIProperty(s, o);
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",desktopManager=" + ((this.desktopManager != null) ? this.desktopManager.toString() : "");
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJDesktopPane();
        }
        return this.accessibleContext;
    }
    
    private static class ComponentPosition implements Comparable<ComponentPosition>
    {
        private final JInternalFrame component;
        private final int layer;
        private final int zOrder;
        
        ComponentPosition(final JInternalFrame component, final int layer, final int zOrder) {
            this.component = component;
            this.layer = layer;
            this.zOrder = zOrder;
        }
        
        @Override
        public int compareTo(final ComponentPosition componentPosition) {
            final int n = componentPosition.layer - this.layer;
            if (n == 0) {
                return this.zOrder - componentPosition.zOrder;
            }
            return n;
        }
    }
    
    protected class AccessibleJDesktopPane extends AccessibleJComponent
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.DESKTOP_PANE;
        }
    }
}
