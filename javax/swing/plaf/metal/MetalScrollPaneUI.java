package javax.swing.plaf.metal;

import java.beans.PropertyChangeEvent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicScrollPaneUI;

public class MetalScrollPaneUI extends BasicScrollPaneUI
{
    private PropertyChangeListener scrollBarSwapListener;
    
    public static ComponentUI createUI(final JComponent component) {
        return new MetalScrollPaneUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
        final JScrollPane scrollPane = (JScrollPane)component;
        this.updateScrollbarsFreeStanding();
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        super.uninstallUI(component);
        final JScrollPane scrollPane = (JScrollPane)component;
        final JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        final JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        if (horizontalScrollBar != null) {
            horizontalScrollBar.putClientProperty("JScrollBar.isFreeStanding", null);
        }
        if (verticalScrollBar != null) {
            verticalScrollBar.putClientProperty("JScrollBar.isFreeStanding", null);
        }
    }
    
    public void installListeners(final JScrollPane scrollPane) {
        super.installListeners(scrollPane);
        scrollPane.addPropertyChangeListener(this.scrollBarSwapListener = this.createScrollBarSwapListener());
    }
    
    @Override
    protected void uninstallListeners(final JComponent component) {
        super.uninstallListeners(component);
        component.removePropertyChangeListener(this.scrollBarSwapListener);
    }
    
    @Deprecated
    public void uninstallListeners(final JScrollPane scrollPane) {
        super.uninstallListeners(scrollPane);
        scrollPane.removePropertyChangeListener(this.scrollBarSwapListener);
    }
    
    private void updateScrollbarsFreeStanding() {
        if (this.scrollpane == null) {
            return;
        }
        Boolean b;
        if (this.scrollpane.getBorder() instanceof MetalBorders.ScrollPaneBorder) {
            b = Boolean.FALSE;
        }
        else {
            b = Boolean.TRUE;
        }
        final JScrollBar horizontalScrollBar = this.scrollpane.getHorizontalScrollBar();
        if (horizontalScrollBar != null) {
            horizontalScrollBar.putClientProperty("JScrollBar.isFreeStanding", b);
        }
        final JScrollBar verticalScrollBar = this.scrollpane.getVerticalScrollBar();
        if (verticalScrollBar != null) {
            verticalScrollBar.putClientProperty("JScrollBar.isFreeStanding", b);
        }
    }
    
    protected PropertyChangeListener createScrollBarSwapListener() {
        return new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                final String propertyName = propertyChangeEvent.getPropertyName();
                if (propertyName.equals("verticalScrollBar") || propertyName.equals("horizontalScrollBar")) {
                    final JScrollBar scrollBar = (JScrollBar)propertyChangeEvent.getOldValue();
                    if (scrollBar != null) {
                        scrollBar.putClientProperty("JScrollBar.isFreeStanding", null);
                    }
                    final JScrollBar scrollBar2 = (JScrollBar)propertyChangeEvent.getNewValue();
                    if (scrollBar2 != null) {
                        scrollBar2.putClientProperty("JScrollBar.isFreeStanding", Boolean.FALSE);
                    }
                }
                else if ("border".equals(propertyName)) {
                    MetalScrollPaneUI.this.updateScrollbarsFreeStanding();
                }
            }
        };
    }
}
