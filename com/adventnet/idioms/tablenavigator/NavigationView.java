package com.adventnet.idioms.tablenavigator;

import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JLabel;
import com.adventnet.beans.rangenavigator.ActionComboPanel;
import com.adventnet.beans.rangenavigator.NavigationButtonsPanel;
import com.adventnet.beans.rangenavigator.PageLengthPanel;
import com.adventnet.beans.rangenavigator.RangePanel;
import com.adventnet.beans.rangenavigator.LabeledNumericTextField;
import com.adventnet.beans.rangenavigator.events.ActionComboListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import com.adventnet.beans.rangenavigator.RangeNavigator;
import javax.swing.JScrollPane;
import java.awt.Component;
import javax.swing.JPanel;

public class NavigationView extends JPanel
{
    private Component viewRangeComp;
    private JPanel customComponentPanel;
    private JPanel jPanel1;
    private JScrollPane jsp;
    protected RangeNavigator rangeNavigator;
    
    public NavigationView() {
        this.initComponents();
        this.init();
    }
    
    private void init() {
        this.setCustomComponentsPanelVisible(false);
    }
    
    public Component add(final Component component) {
        return this.addViewRangeComponent(component);
    }
    
    public void setCustomComponentsPanelVisible(final boolean visible) {
        final GridBagLayout gridBagLayout = (GridBagLayout)this.jPanel1.getLayout();
        final GridBagConstraints constraints = gridBagLayout.getConstraints((Component)this.getRangeNavigator());
        final GridBagConstraints constraints2 = gridBagLayout.getConstraints(this.getCustomComponentsPanel());
        if (visible) {
            constraints.weightx = 0.0;
            constraints2.weightx = 0.1;
        }
        else {
            constraints.weightx = 0.1;
            constraints2.weightx = 0.0;
        }
        gridBagLayout.setConstraints((Component)this.getRangeNavigator(), constraints);
        gridBagLayout.setConstraints(this.getCustomComponentsPanel(), constraints2);
        this.customComponentPanel.setVisible(visible);
    }
    
    public JPanel getCustomComponentsPanel() {
        return this.customComponentPanel;
    }
    
    private Component addViewRangeComponent(final Component viewRangeComp) {
        if (this.viewRangeComp != null) {
            this.remove(this.viewRangeComp);
        }
        this.viewRangeComp = viewRangeComp;
        this.jsp.getViewport().add(viewRangeComp);
        return viewRangeComp;
    }
    
    public RangeNavigator getRangeNavigator() {
        return this.rangeNavigator;
    }
    
    public void addActionComboListener(final ActionComboListener actionComboListener) {
        this.rangeNavigator.addActionComboListener(actionComboListener);
    }
    
    public void removeActionComboListener(final ActionComboListener actionComboListener) {
        this.rangeNavigator.removeActionComboListener(actionComboListener);
    }
    
    public LabeledNumericTextField getTotalPanel() {
        return this.rangeNavigator.getTotalPanel();
    }
    
    public RangePanel getFromToPanel() {
        return this.rangeNavigator.getFromToPanel();
    }
    
    public RangePanel getPagePanel() {
        return this.rangeNavigator.getPagePanel();
    }
    
    public PageLengthPanel getPageLengthPanel() {
        return this.rangeNavigator.getPageLengthPanel();
    }
    
    public NavigationButtonsPanel getButtonPanel() {
        return this.rangeNavigator.getButtonPanel();
    }
    
    public ActionComboPanel getActionPanel() {
        return this.rangeNavigator.getActionPanel();
    }
    
    public long getCurrentPage() {
        return this.rangeNavigator.getCurrentPage();
    }
    
    public long getTotalPages() {
        return this.rangeNavigator.getTotalPages();
    }
    
    public void setPageLengths(final long[] pageLengths) {
        this.rangeNavigator.setPageLengths(pageLengths);
    }
    
    public long[] getPageLengths() {
        return this.rangeNavigator.getPageLengths();
    }
    
    public long getPageLength() {
        return this.rangeNavigator.getPageLength();
    }
    
    public void setTotalLabel(final String totalLabel) {
        this.rangeNavigator.setTotalLabel(totalLabel);
    }
    
    public void setFromLabel(final String fromLabel) {
        this.rangeNavigator.setFromLabel(fromLabel);
    }
    
    public void setToLabel(final String toLabel) {
        this.rangeNavigator.setToLabel(toLabel);
    }
    
    public void setPageLabel(final String pageLabel) {
        this.rangeNavigator.setPageLabel(pageLabel);
    }
    
    public void setShowLabel(final String showLabel) {
        this.rangeNavigator.setShowLabel(showLabel);
    }
    
    public JLabel getTotalLabel() {
        return this.rangeNavigator.getTotalLabel();
    }
    
    public JLabel getFromLabel() {
        return this.rangeNavigator.getFromLabel();
    }
    
    public JLabel getToLabel() {
        return this.rangeNavigator.getToLabel();
    }
    
    public JLabel getPageLabel() {
        return this.rangeNavigator.getPageLabel();
    }
    
    public JLabel getShowLabel() {
        return this.rangeNavigator.getShowLabel();
    }
    
    public JScrollPane getScrollPane() {
        return this.jsp;
    }
    
    private void initComponents() {
        this.jsp = new JScrollPane();
        this.jPanel1 = new JPanel();
        this.rangeNavigator = new RangeNavigator();
        this.customComponentPanel = new JPanel();
        this.setLayout(new GridBagLayout());
        this.jsp.setHorizontalScrollBarPolicy(31);
        this.jsp.setAutoscrolls(true);
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = 1;
        gridBagConstraints.anchor = 11;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        this.add(this.jsp, gridBagConstraints);
        this.jPanel1.setLayout(new GridBagLayout());
        final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.anchor = 13;
        gridBagConstraints2.insets = new Insets(0, 5, 0, 0);
        this.jPanel1.add((Component)this.rangeNavigator, gridBagConstraints2);
        final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.fill = 1;
        gridBagConstraints3.anchor = 17;
        gridBagConstraints3.weightx = 0.1;
        this.jPanel1.add(this.customComponentPanel, gridBagConstraints3);
        final GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = 1;
        gridBagConstraints4.anchor = 11;
        this.add(this.jPanel1, gridBagConstraints4);
    }
}
