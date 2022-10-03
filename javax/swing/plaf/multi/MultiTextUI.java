package javax.swing.plaf.multi;

import javax.accessibility.Accessible;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.text.View;
import javax.swing.text.EditorKit;
import javax.swing.text.Position;
import javax.swing.text.BadLocationException;
import java.awt.Rectangle;
import java.awt.Point;
import javax.swing.text.JTextComponent;
import javax.swing.plaf.ComponentUI;
import java.util.Vector;
import javax.swing.plaf.TextUI;

public class MultiTextUI extends TextUI
{
    protected Vector uis;
    
    public MultiTextUI() {
        this.uis = new Vector();
    }
    
    public ComponentUI[] getUIs() {
        return MultiLookAndFeel.uisToArray(this.uis);
    }
    
    @Override
    public String getToolTipText(final JTextComponent textComponent, final Point point) {
        final String toolTipText = this.uis.elementAt(0).getToolTipText(textComponent, point);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((TextUI)this.uis.elementAt(i)).getToolTipText(textComponent, point);
        }
        return toolTipText;
    }
    
    @Override
    public Rectangle modelToView(final JTextComponent textComponent, final int n) throws BadLocationException {
        final Rectangle modelToView = this.uis.elementAt(0).modelToView(textComponent, n);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((TextUI)this.uis.elementAt(i)).modelToView(textComponent, n);
        }
        return modelToView;
    }
    
    @Override
    public Rectangle modelToView(final JTextComponent textComponent, final int n, final Position.Bias bias) throws BadLocationException {
        final Rectangle modelToView = this.uis.elementAt(0).modelToView(textComponent, n, bias);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((TextUI)this.uis.elementAt(i)).modelToView(textComponent, n, bias);
        }
        return modelToView;
    }
    
    @Override
    public int viewToModel(final JTextComponent textComponent, final Point point) {
        final int viewToModel = this.uis.elementAt(0).viewToModel(textComponent, point);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((TextUI)this.uis.elementAt(i)).viewToModel(textComponent, point);
        }
        return viewToModel;
    }
    
    @Override
    public int viewToModel(final JTextComponent textComponent, final Point point, final Position.Bias[] array) {
        final int viewToModel = this.uis.elementAt(0).viewToModel(textComponent, point, array);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((TextUI)this.uis.elementAt(i)).viewToModel(textComponent, point, array);
        }
        return viewToModel;
    }
    
    @Override
    public int getNextVisualPositionFrom(final JTextComponent textComponent, final int n, final Position.Bias bias, final int n2, final Position.Bias[] array) throws BadLocationException {
        final int nextVisualPosition = this.uis.elementAt(0).getNextVisualPositionFrom(textComponent, n, bias, n2, array);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((TextUI)this.uis.elementAt(i)).getNextVisualPositionFrom(textComponent, n, bias, n2, array);
        }
        return nextVisualPosition;
    }
    
    @Override
    public void damageRange(final JTextComponent textComponent, final int n, final int n2) {
        for (int i = 0; i < this.uis.size(); ++i) {
            ((TextUI)this.uis.elementAt(i)).damageRange(textComponent, n, n2);
        }
    }
    
    @Override
    public void damageRange(final JTextComponent textComponent, final int n, final int n2, final Position.Bias bias, final Position.Bias bias2) {
        for (int i = 0; i < this.uis.size(); ++i) {
            ((TextUI)this.uis.elementAt(i)).damageRange(textComponent, n, n2, bias, bias2);
        }
    }
    
    @Override
    public EditorKit getEditorKit(final JTextComponent textComponent) {
        final EditorKit editorKit = this.uis.elementAt(0).getEditorKit(textComponent);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((TextUI)this.uis.elementAt(i)).getEditorKit(textComponent);
        }
        return editorKit;
    }
    
    @Override
    public View getRootView(final JTextComponent textComponent) {
        final View rootView = this.uis.elementAt(0).getRootView(textComponent);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((TextUI)this.uis.elementAt(i)).getRootView(textComponent);
        }
        return rootView;
    }
    
    @Override
    public boolean contains(final JComponent component, final int n, final int n2) {
        final boolean contains = this.uis.elementAt(0).contains(component, n, n2);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).contains(component, n, n2);
        }
        return contains;
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        for (int i = 0; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).update(graphics, component);
        }
    }
    
    public static ComponentUI createUI(final JComponent component) {
        final MultiTextUI multiTextUI = new MultiTextUI();
        return MultiLookAndFeel.createUIs(multiTextUI, multiTextUI.uis, component);
    }
    
    @Override
    public void installUI(final JComponent component) {
        for (int i = 0; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).installUI(component);
        }
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        for (int i = 0; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).uninstallUI(component);
        }
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        for (int i = 0; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).paint(graphics, component);
        }
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final Dimension preferredSize = this.uis.elementAt(0).getPreferredSize(component);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).getPreferredSize(component);
        }
        return preferredSize;
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        final Dimension minimumSize = this.uis.elementAt(0).getMinimumSize(component);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).getMinimumSize(component);
        }
        return minimumSize;
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        final Dimension maximumSize = this.uis.elementAt(0).getMaximumSize(component);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).getMaximumSize(component);
        }
        return maximumSize;
    }
    
    @Override
    public int getAccessibleChildrenCount(final JComponent component) {
        final int accessibleChildrenCount = this.uis.elementAt(0).getAccessibleChildrenCount(component);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).getAccessibleChildrenCount(component);
        }
        return accessibleChildrenCount;
    }
    
    @Override
    public Accessible getAccessibleChild(final JComponent component, final int n) {
        final Accessible accessibleChild = this.uis.elementAt(0).getAccessibleChild(component, n);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).getAccessibleChild(component, n);
        }
        return accessibleChild;
    }
}
