package org.jfree.ui.tabbedui;

import org.jfree.util.Log;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.awt.Window;
import javax.swing.SwingUtilities;
import java.beans.PropertyChangeListener;
import javax.swing.JMenu;
import java.awt.Component;
import javax.swing.event.ChangeListener;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JMenuBar;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.util.ArrayList;
import javax.swing.JComponent;

public abstract class AbstractTabbedUI extends JComponent
{
    public static final String JMENUBAR_PROPERTY = "jMenuBar";
    public static final String GLOBAL_MENU_PROPERTY = "globalMenu";
    private ArrayList rootEditors;
    private JTabbedPane tabbedPane;
    private int selectedRootEditor;
    private JComponent currentToolbar;
    private JPanel toolbarContainer;
    private Action closeAction;
    private JMenuBar jMenuBar;
    private boolean globalMenu;
    
    public AbstractTabbedUI() {
        this.selectedRootEditor = -1;
        (this.toolbarContainer = new JPanel()).setLayout(new BorderLayout());
        (this.tabbedPane = new JTabbedPane(3)).addChangeListener(new TabChangeHandler(this.tabbedPane));
        this.rootEditors = new ArrayList();
        this.setLayout(new BorderLayout());
        this.add(this.toolbarContainer, "North");
        this.add(this.tabbedPane, "Center");
        this.closeAction = this.createCloseAction();
    }
    
    private void addMenus(final JMenuBar menuBar, final JMenu[] customMenus) {
        for (int i = 0; i < customMenus.length; ++i) {
            menuBar.add(customMenus[i]);
        }
    }
    
    public void addRootEditor(final RootEditor rootPanel) {
        this.rootEditors.add(rootPanel);
        this.tabbedPane.add(rootPanel.getEditorName(), rootPanel.getMainPanel());
        rootPanel.addPropertyChangeListener("enabled", new TabEnableChangeListener());
        this.updateRootEditorEnabled(rootPanel);
        if (this.getRootEditorCount() == 1) {
            this.setSelectedEditor(0);
        }
        else if (this.isGlobalMenu()) {
            this.setJMenuBar(this.updateGlobalMenubar());
        }
    }
    
    protected abstract void attempExit();
    
    private void closeToolbar() {
        if (this.currentToolbar != null) {
            if (this.currentToolbar.getParent() != this.toolbarContainer) {
                final Window w = SwingUtilities.windowForComponent(this.currentToolbar);
                if (w != null) {
                    w.setVisible(false);
                    w.dispose();
                }
            }
            this.currentToolbar.setVisible(false);
        }
    }
    
    protected Action createCloseAction() {
        return new ExitAction();
    }
    
    private JMenuBar createEditorMenubar(final RootEditor root) {
        JMenuBar menuBar = this.getJMenuBar();
        if (menuBar == null) {
            menuBar = new JMenuBar();
        }
        else {
            menuBar.removeAll();
        }
        this.addMenus(menuBar, this.getPrefixMenus());
        if (this.isGlobalMenu()) {
            for (int i = 0; i < this.rootEditors.size(); ++i) {
                final RootEditor editor = this.rootEditors.get(i);
                this.addMenus(menuBar, editor.getMenus());
            }
        }
        else {
            this.addMenus(menuBar, root.getMenus());
        }
        this.addMenus(menuBar, this.getPostfixMenus());
        return menuBar;
    }
    
    public Action getCloseAction() {
        return this.closeAction;
    }
    
    public JMenuBar getJMenuBar() {
        return this.jMenuBar;
    }
    
    protected abstract JMenu[] getPostfixMenus();
    
    protected abstract JMenu[] getPrefixMenus();
    
    public RootEditor getRootEditor(final int pos) {
        return this.rootEditors.get(pos);
    }
    
    public int getRootEditorCount() {
        return this.rootEditors.size();
    }
    
    public int getSelectedEditor() {
        return this.selectedRootEditor;
    }
    
    protected JTabbedPane getTabbedPane() {
        return this.tabbedPane;
    }
    
    public boolean isGlobalMenu() {
        return this.globalMenu;
    }
    
    public void setGlobalMenu(final boolean globalMenu) {
        this.globalMenu = globalMenu;
        if (this.isGlobalMenu()) {
            this.setJMenuBar(this.updateGlobalMenubar());
        }
        else if (this.getRootEditorCount() > 0) {
            this.setJMenuBar(this.createEditorMenubar(this.getRootEditor(this.getSelectedEditor())));
        }
    }
    
    protected void setJMenuBar(final JMenuBar menuBar) {
        final JMenuBar oldMenuBar = this.jMenuBar;
        this.firePropertyChange("jMenuBar", oldMenuBar, this.jMenuBar = menuBar);
    }
    
    public void setSelectedEditor(final int selectedEditor) {
        final int oldEditor = this.selectedRootEditor;
        if (oldEditor == selectedEditor) {
            return;
        }
        this.selectedRootEditor = selectedEditor;
        for (int i = 0; i < this.rootEditors.size(); ++i) {
            final boolean shouldBeActive = i == selectedEditor;
            final RootEditor container = this.rootEditors.get(i);
            if (container.isActive() && !shouldBeActive) {
                container.setActive(false);
            }
        }
        if (this.currentToolbar != null) {
            this.closeToolbar();
            this.toolbarContainer.removeAll();
            this.currentToolbar = null;
        }
        for (int j = 0; j < this.rootEditors.size(); ++j) {
            final boolean shouldBeActive2 = j == selectedEditor;
            final RootEditor container2 = this.rootEditors.get(j);
            if (!container2.isActive() && shouldBeActive2) {
                container2.setActive(true);
                this.setJMenuBar(this.createEditorMenubar(container2));
                this.currentToolbar = container2.getToolbar();
                if (this.currentToolbar != null) {
                    this.toolbarContainer.add(this.currentToolbar, "Center");
                    this.toolbarContainer.setVisible(true);
                    this.currentToolbar.setVisible(true);
                }
                else {
                    this.toolbarContainer.setVisible(false);
                }
                this.getJMenuBar().repaint();
            }
        }
    }
    
    private JMenuBar updateGlobalMenubar() {
        JMenuBar menuBar = this.getJMenuBar();
        if (menuBar == null) {
            menuBar = new JMenuBar();
        }
        else {
            menuBar.removeAll();
        }
        this.addMenus(menuBar, this.getPrefixMenus());
        for (int i = 0; i < this.rootEditors.size(); ++i) {
            final RootEditor editor = this.rootEditors.get(i);
            this.addMenus(menuBar, editor.getMenus());
        }
        this.addMenus(menuBar, this.getPostfixMenus());
        return menuBar;
    }
    
    protected void updateRootEditorEnabled(final RootEditor editor) {
        final boolean enabled = editor.isEnabled();
        for (int i = 0; i < this.tabbedPane.getTabCount(); ++i) {
            final Component tab = this.tabbedPane.getComponentAt(i);
            if (tab == editor.getMainPanel()) {
                this.tabbedPane.setEnabledAt(i, enabled);
                return;
            }
        }
    }
    
    protected class ExitAction extends AbstractAction
    {
        public ExitAction() {
            this.putValue("Name", "Exit");
        }
        
        public void actionPerformed(final ActionEvent e) {
            AbstractTabbedUI.this.attempExit();
        }
    }
    
    private class TabChangeHandler implements ChangeListener
    {
        private final JTabbedPane pane;
        
        public TabChangeHandler(final JTabbedPane pane) {
            this.pane = pane;
        }
        
        public void stateChanged(final ChangeEvent e) {
            AbstractTabbedUI.this.setSelectedEditor(this.pane.getSelectedIndex());
        }
    }
    
    private class TabEnableChangeListener implements PropertyChangeListener
    {
        public TabEnableChangeListener() {
        }
        
        public void propertyChange(final PropertyChangeEvent evt) {
            if (!evt.getPropertyName().equals("enabled")) {
                Log.debug("PropertyName");
                return;
            }
            if (!(evt.getSource() instanceof RootEditor)) {
                Log.debug("Source");
                return;
            }
            final RootEditor editor = (RootEditor)evt.getSource();
            AbstractTabbedUI.this.updateRootEditorEnabled(editor);
        }
    }
}
