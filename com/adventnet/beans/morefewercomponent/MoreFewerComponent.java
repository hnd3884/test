package com.adventnet.beans.morefewercomponent;

import com.adventnet.beans.criteriatable.events.AttributeModelEvent;
import java.util.MissingResourceException;
import com.adventnet.beans.criteriatable.ComparatorsTable;
import com.adventnet.beans.criteriatable.AttributeValueEditorComponent;
import com.adventnet.beans.criteriatable.AttributeValueEditorComponentsTable;
import javax.swing.table.DefaultTableModel;
import com.adventnet.beans.criteriatable.Criterion;
import com.adventnet.beans.criteriatable.Criteria;
import com.adventnet.beans.criteriatable.events.CriteriaChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.AbstractButton;
import java.awt.Component;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import com.adventnet.beans.criteriatable.AttributeModel;
import javax.swing.JComponent;
import javax.swing.table.JTableHeader;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import com.adventnet.beans.criteriatable.CriteriaTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.util.HashMap;
import java.util.ResourceBundle;
import com.adventnet.beans.criteriatable.events.AttributeModelListener;
import com.adventnet.beans.criteriatable.events.CriteriaChangeListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.JPanel;

public class MoreFewerComponent extends JPanel implements ListSelectionListener, CriteriaChangeListener, AttributeModelListener
{
    public static final int MATCH_ALL = 0;
    public static final int MATCH_ANY = 1;
    private int defaultOption;
    private boolean advancedMode;
    private ResourceBundle bundle;
    private int maximumCriterionLimit;
    private int minimumCriterionLimit;
    private String operatorForMatchAnyOption;
    private String operatorForMatchAllOption;
    private HashMap stateMap;
    private JCheckBox advancedOption;
    private JPanel buttonPanel;
    private JButton clear;
    private JScrollPane criteriaPane;
    private CriteriaTable criteriaTable1;
    private JButton fewer;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JSeparator jSeparator1;
    private JRadioButton matchAll;
    private JRadioButton matchAny;
    private ButtonGroup matchOptions;
    private JPanel matchOptionsPanel;
    private JButton more;
    private JButton moveDown;
    private JButton moveUp;
    private JPanel optionsPanel;
    private JButton[] buttons;
    private JRadioButton[] radButtons;
    
    public MoreFewerComponent() {
        this(1);
    }
    
    public MoreFewerComponent(final int minimumCriterionLimit) {
        this.defaultOption = 1;
        this.maximumCriterionLimit = -1;
        this.minimumCriterionLimit = 1;
        this.minimumCriterionLimit = minimumCriterionLimit;
        this.initComponents();
        this.init();
    }
    
    private void init() {
        this.stateMap = new HashMap();
        this.buttons = new JButton[] { this.moveDown, this.moveUp, this.clear, this.fewer, this.more };
        this.radButtons = new JRadioButton[] { this.matchAll, this.matchAny };
        this.setAdvancedMode(false);
        this.criteriaTable1.setTableHeader(null);
        this.criteriaTable1.setSelectionBackground(this.criteriaTable1.getBackground());
        this.criteriaPane.getViewport().setBackground(this.criteriaTable1.getBackground());
        this.setButtonsInitialState();
        this.addListeners();
        if (this.getAttributeModel().getOperatorsCount() > 1) {
            this.setOperatorForMatchAllOption(this.getAttributeModel().getOperator(0));
            this.setOperatorForMatchAnyOption(this.getAttributeModel().getOperator(1));
        }
        this.selectDefaultMatchOption();
        this.setResourceBundle(ResourceBundle.getBundle("com/adventnet/beans/morefewercomponent/morefewercomponent"));
    }
    
    public void setDefaultMatchOption(final int defaultOption) {
        this.defaultOption = defaultOption;
        this.selectDefaultMatchOption();
    }
    
    public int getDefaultMatchOption() {
        return this.defaultOption;
    }
    
    private void selectDefaultMatchOption() {
        if (this.defaultOption == 1) {
            this.matchAny.doClick();
        }
        else if (this.defaultOption == 0) {
            this.matchAll.doClick();
        }
    }
    
    public void setEnabled(final boolean focusable) {
        if (this.isEnabled() == focusable) {
            return;
        }
        super.setEnabled(focusable);
        this.criteriaTable1.cancelEditing();
        this.criteriaTable1.setEnabled(focusable);
        this.criteriaTable1.setFocusable(focusable);
        if (!focusable) {
            this.saveState();
            for (int i = 0; i < this.buttons.length; ++i) {
                this.buttons[i].setEnabled(false);
            }
            for (int j = 0; j < this.radButtons.length; ++j) {
                this.radButtons[j].setEnabled(false);
            }
            this.advancedOption.setEnabled(false);
        }
        else {
            this.revertState();
        }
    }
    
    private void saveState() {
        for (int i = 0; i < this.buttons.length; ++i) {
            this.stateMap.put(this.buttons[i], new Boolean(this.buttons[i].isEnabled()));
        }
        for (int j = 0; j < this.radButtons.length; ++j) {
            this.stateMap.put(this.radButtons[j], new Boolean(this.radButtons[j].isEnabled()));
        }
        this.stateMap.put(this.advancedOption, new Boolean(this.advancedOption.isEnabled()));
    }
    
    private void revertState() {
        for (int i = 0; i < this.buttons.length; ++i) {
            this.buttons[i].setEnabled(this.getState(this.buttons[i]));
        }
        for (int j = 0; j < this.radButtons.length; ++j) {
            this.radButtons[j].setEnabled(this.getState(this.radButtons[j]));
        }
        this.advancedOption.setEnabled(this.getState(this.advancedOption));
    }
    
    private boolean getState(final Object o) {
        final Boolean b = this.stateMap.get(o);
        if (b == null) {
            return ((JComponent)o).isEnabled();
        }
        return b;
    }
    
    private void setUpTexts() {
        this.matchAll.setText(this.getString("Match All"));
        this.matchAny.setText(this.getString("Match Any"));
        this.advancedOption.setText(this.getString("Advanced"));
        this.more.setToolTipText(this.getString("More"));
        this.fewer.setToolTipText(this.getString("Fewer"));
        this.clear.setToolTipText(this.getString("Clear"));
        this.moveUp.setToolTipText(this.getString("Move Up"));
        this.moveDown.setToolTipText(this.getString("Move Down"));
    }
    
    private void addListeners() {
        this.criteriaTable1.getSelectionModel().addListSelectionListener(this);
        this.criteriaTable1.addCriteriaChangeListener(this);
        this.getAttributeModel().addAttributeModelListener(this);
        this.setEnabled(this.getAttributeModel().getAttributeCount() > 0);
    }
    
    public void setAttributeModel(final AttributeModel attributeModel) {
        if (this.criteriaTable1.getAttributeModel() != attributeModel) {
            this.criteriaTable1.setAttributeModel(attributeModel);
            this.clearAllCriterions();
            this.getAttributeModel().addAttributeModelListener(this);
            this.setEnabled(this.getAttributeModel().getAttributeCount() > 0);
        }
    }
    
    public AttributeModel getAttributeModel() {
        return this.criteriaTable1.getAttributeModel();
    }
    
    public int getMinimumCriterionLimit() {
        return this.getCriteriaTable().getMinimumCriterionLimit();
    }
    
    public void setMaximumCriterionLimit(final int maximumCriterionLimit) {
        if (maximumCriterionLimit > 0 && maximumCriterionLimit >= this.getCriteriaTable().getCriterionCount() && maximumCriterionLimit > this.getMinimumCriterionLimit()) {
            this.maximumCriterionLimit = maximumCriterionLimit;
        }
    }
    
    public int getMaximumCriterionLimit() {
        return this.maximumCriterionLimit;
    }
    
    public CriteriaTable getCriteriaTable() {
        return this.criteriaTable1;
    }
    
    public void setAdvancedMode(final boolean advancedMode) {
        this.advancedMode = advancedMode;
        this.setMode();
    }
    
    public boolean isAdvancedMode() {
        return this.advancedMode;
    }
    
    private void setMode() {
        if (this.isAdvancedMode()) {
            this.enableDisbleMoveButtons();
        }
        else {
            this.moveUp.setEnabled(false);
            this.moveDown.setEnabled(false);
        }
        this.matchAll.setEnabled(!this.isAdvancedMode());
        this.matchAny.setEnabled(!this.isAdvancedMode());
        this.advancedOption.setSelected(this.isAdvancedMode());
        this.criteriaTable1.setAdvancedMode(this.isAdvancedMode());
    }
    
    private void initComponents() {
        this.matchOptions = new ButtonGroup();
        this.optionsPanel = new JPanel();
        this.jPanel2 = new JPanel();
        this.advancedOption = new JCheckBox();
        this.matchOptionsPanel = new JPanel();
        this.matchAny = new JRadioButton();
        this.matchAll = new JRadioButton();
        this.jPanel1 = new JPanel();
        this.criteriaPane = new JScrollPane();
        this.criteriaTable1 = new CriteriaTable(this.minimumCriterionLimit);
        this.buttonPanel = new JPanel();
        this.moveUp = new JButton();
        this.moveDown = new JButton();
        this.fewer = new JButton();
        this.more = new JButton();
        this.clear = new JButton();
        this.jSeparator1 = new JSeparator();
        this.setLayout(new BorderLayout(0, 5));
        this.setMaximumSize(new Dimension(300, 200));
        this.optionsPanel.setLayout(new BorderLayout());
        this.jPanel2.setLayout(new GridBagLayout());
        this.advancedOption.setFont(new Font("Dialog", 0, 12));
        this.advancedOption.setMnemonic('d');
        this.advancedOption.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                MoreFewerComponent.this.advancedOptionActionPerformed(actionEvent);
            }
        });
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 3, 0, 0);
        gridBagConstraints.anchor = 17;
        gridBagConstraints.weightx = 0.1;
        this.jPanel2.add(this.advancedOption, gridBagConstraints);
        this.matchOptionsPanel.setLayout(new GridBagLayout());
        this.matchAny.setFont(new Font("Dialog", 0, 12));
        this.matchAny.setMnemonic('n');
        this.matchAny.setSelected(true);
        this.matchOptions.add(this.matchAny);
        this.matchAny.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                MoreFewerComponent.this.matchAnyActionPerformed(actionEvent);
            }
        });
        final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        this.matchOptionsPanel.add(this.matchAny, gridBagConstraints2);
        this.matchAll.setFont(new Font("Dialog", 0, 12));
        this.matchAll.setMnemonic('a');
        this.matchOptions.add(this.matchAll);
        this.matchAll.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                MoreFewerComponent.this.matchAllActionPerformed(actionEvent);
            }
        });
        final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 1;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.insets = new Insets(0, 10, 0, 0);
        this.matchOptionsPanel.add(this.matchAll, gridBagConstraints3);
        final GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.insets = new Insets(0, 0, 0, 3);
        gridBagConstraints4.anchor = 13;
        gridBagConstraints4.weightx = 0.1;
        this.jPanel2.add(this.matchOptionsPanel, gridBagConstraints4);
        this.optionsPanel.add(this.jPanel2, "Center");
        this.add(this.optionsPanel, "North");
        this.jPanel1.setLayout(new BorderLayout());
        this.criteriaPane.setViewportView(this.criteriaTable1);
        this.jPanel1.add(this.criteriaPane, "Center");
        this.buttonPanel.setLayout(new GridBagLayout());
        this.moveUp.setFont(new Font("Dialog", 0, 12));
        this.moveUp.setIcon(new ImageIcon(this.getClass().getResource("/com/adventnet/beans/morefewercomponent/up.gif")));
        this.moveUp.setMnemonic('u');
        this.moveUp.setToolTipText("Move Up");
        this.moveUp.setMinimumSize(new Dimension(20, 20));
        this.moveUp.setPreferredSize(new Dimension(20, 20));
        this.moveUp.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                MoreFewerComponent.this.moveUpActionPerformed(actionEvent);
            }
        });
        final GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.gridx = 3;
        gridBagConstraints5.gridy = 1;
        gridBagConstraints5.insets = new Insets(3, 5, 2, 0);
        gridBagConstraints5.anchor = 13;
        gridBagConstraints5.weightx = 0.1;
        this.buttonPanel.add(this.moveUp, gridBagConstraints5);
        this.moveDown.setFont(new Font("Dialog", 0, 12));
        this.moveDown.setIcon(new ImageIcon(this.getClass().getResource("/com/adventnet/beans/morefewercomponent/down.gif")));
        this.moveDown.setMnemonic('w');
        this.moveDown.setToolTipText("Move Down");
        this.moveDown.setMinimumSize(new Dimension(20, 20));
        this.moveDown.setPreferredSize(new Dimension(20, 20));
        this.moveDown.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                MoreFewerComponent.this.moveDownActionPerformed(actionEvent);
            }
        });
        final GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        gridBagConstraints6.gridx = 4;
        gridBagConstraints6.gridy = 1;
        gridBagConstraints6.insets = new Insets(3, 5, 2, 5);
        gridBagConstraints6.anchor = 13;
        this.buttonPanel.add(this.moveDown, gridBagConstraints6);
        this.fewer.setFont(new Font("Dialog", 0, 12));
        this.fewer.setIcon(new ImageIcon(this.getClass().getResource("/com/adventnet/beans/morefewercomponent/fewer.gif")));
        this.fewer.setMnemonic('f');
        this.fewer.setToolTipText("Fewer");
        this.fewer.setMinimumSize(new Dimension(20, 20));
        this.fewer.setPreferredSize(new Dimension(20, 20));
        this.fewer.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                MoreFewerComponent.this.fewerActionPerformed(actionEvent);
            }
        });
        final GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
        gridBagConstraints7.gridx = 1;
        gridBagConstraints7.gridy = 1;
        gridBagConstraints7.insets = new Insets(3, 0, 2, 5);
        gridBagConstraints7.anchor = 17;
        this.buttonPanel.add(this.fewer, gridBagConstraints7);
        this.more.setFont(new Font("Dialog", 0, 12));
        this.more.setIcon(new ImageIcon(this.getClass().getResource("/com/adventnet/beans/morefewercomponent/more.gif")));
        this.more.setMnemonic('m');
        this.more.setToolTipText("More");
        this.more.setMinimumSize(new Dimension(20, 20));
        this.more.setPreferredSize(new Dimension(20, 20));
        this.more.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                MoreFewerComponent.this.moreActionPerformed(actionEvent);
            }
        });
        final GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
        gridBagConstraints8.gridx = 0;
        gridBagConstraints8.gridy = 1;
        gridBagConstraints8.insets = new Insets(3, 5, 2, 5);
        gridBagConstraints8.anchor = 17;
        this.buttonPanel.add(this.more, gridBagConstraints8);
        this.clear.setFont(new Font("Dialog", 0, 12));
        this.clear.setIcon(new ImageIcon(this.getClass().getResource("/com/adventnet/beans/morefewercomponent/clear.gif")));
        this.clear.setMnemonic('c');
        this.clear.setToolTipText("Clear");
        this.clear.setMinimumSize(new Dimension(20, 20));
        this.clear.setPreferredSize(new Dimension(20, 20));
        this.clear.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                MoreFewerComponent.this.clearActionPerformed(actionEvent);
            }
        });
        final GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
        gridBagConstraints9.gridx = 2;
        gridBagConstraints9.gridy = 1;
        gridBagConstraints9.insets = new Insets(3, 10, 2, 5);
        gridBagConstraints9.anchor = 17;
        gridBagConstraints9.weightx = 0.1;
        this.buttonPanel.add(this.clear, gridBagConstraints9);
        final GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
        gridBagConstraints10.gridwidth = 0;
        gridBagConstraints10.fill = 2;
        gridBagConstraints10.insets = new Insets(0, 3, 0, 3);
        this.buttonPanel.add(this.jSeparator1, gridBagConstraints10);
        this.jPanel1.add(this.buttonPanel, "North");
        this.add(this.jPanel1, "Center");
    }
    
    private void matchAllActionPerformed(final ActionEvent actionEvent) {
        this.criteriaTable1.useOperatorForAllCriterions(this.getOperatorForMatchAllOption());
    }
    
    private void matchAnyActionPerformed(final ActionEvent actionEvent) {
        this.criteriaTable1.useOperatorForAllCriterions(this.getOperatorForMatchAnyOption());
    }
    
    private void moveDownActionPerformed(final ActionEvent actionEvent) {
        this.criteriaTable1.moveCriterionDown(this.criteriaTable1.getSelectedRows()[0], this.criteriaTable1.getSelectedRows()[this.criteriaTable1.getSelectedRowCount() - 1]);
    }
    
    private void moveUpActionPerformed(final ActionEvent actionEvent) {
        this.criteriaTable1.moveCriterionUp(this.criteriaTable1.getSelectedRows()[0], this.criteriaTable1.getSelectedRows()[this.criteriaTable1.getSelectedRowCount() - 1]);
    }
    
    private void advancedOptionActionPerformed(final ActionEvent actionEvent) {
        this.getCriteriaTable().setAdvancedMode(this.advancedOption.isSelected());
        this.setAdvancedMode(this.advancedOption.isSelected());
        this.valueChanged(null);
    }
    
    private void setButtonsInitialState() {
        this.moveUp.setEnabled(false);
        this.moveDown.setEnabled(false);
        if (this.criteriaTable1.getCriterionCount() <= this.getMinimumCriterionLimit()) {
            this.clear.setEnabled(false);
            this.fewer.setEnabled(false);
        }
        else {
            this.clear.setEnabled(true);
            this.fewer.setEnabled(true);
        }
    }
    
    private void clearActionPerformed(final ActionEvent actionEvent) {
        this.criteriaTable1.clearAllCriterions();
    }
    
    private void fewerActionPerformed(final ActionEvent actionEvent) {
        this.criteriaTable1.removeLastCriterion();
    }
    
    private void moreActionPerformed(final ActionEvent actionEvent) {
        this.criteriaTable1.addNewCriterion();
    }
    
    private void enableDisbleMoveButtons() {
        if (this.criteriaTable1.getSelectedRowCount() > 0 && this.criteriaTable1.getSelectedRow() != -1) {
            final int n = this.criteriaTable1.getSelectedRows()[0];
            final int n2 = this.criteriaTable1.getSelectedRows()[this.criteriaTable1.getSelectedRowCount() - 1];
            if (this.criteriaTable1.isValidToGroup(n, n2)) {
                if (n - 1 >= 0) {
                    if (this.isAdvancedMode()) {
                        this.moveUp.setEnabled(true);
                    }
                }
                else {
                    this.moveUp.setEnabled(false);
                }
                if (n2 + 1 < this.criteriaTable1.getRowCount()) {
                    if (this.isAdvancedMode()) {
                        this.moveDown.setEnabled(true);
                    }
                }
                else {
                    this.moveDown.setEnabled(false);
                }
            }
            else {
                this.moveUp.setEnabled(false);
                this.moveDown.setEnabled(false);
            }
        }
        else {
            this.moveUp.setEnabled(false);
            this.moveDown.setEnabled(false);
        }
    }
    
    public void valueChanged(final ListSelectionEvent listSelectionEvent) {
        if (this.isAdvancedMode()) {
            this.enableDisbleMoveButtons();
        }
    }
    
    public void criteriaChanged(final CriteriaChangeEvent criteriaChangeEvent) {
        if (this.criteriaTable1.getCriterionCount() <= this.getMinimumCriterionLimit()) {
            this.clear.setEnabled(false);
            this.fewer.setEnabled(false);
        }
        else {
            this.clear.setEnabled(true);
            this.fewer.setEnabled(true);
        }
        if (this.maximumCriterionLimit > 0 && this.criteriaTable1.getCriterionCount() >= this.maximumCriterionLimit) {
            this.more.setEnabled(false);
        }
        else {
            this.more.setEnabled(true);
        }
        this.enableDisbleMoveButtons();
    }
    
    public void setButtonsPanelVisible(final boolean visible) {
        this.buttonPanel.setVisible(visible);
    }
    
    public boolean isButtonsPanelVisible() {
        return this.buttonPanel.isVisible();
    }
    
    public void setMoreButtonVisible(final boolean visible) {
        this.more.setVisible(visible);
    }
    
    public boolean isMoreButtonVisible() {
        return this.more.isVisible();
    }
    
    public void setFewerButtonVisible(final boolean visible) {
        this.fewer.setVisible(visible);
    }
    
    public boolean isFewerButtonVisible() {
        return this.fewer.isVisible();
    }
    
    public void setClearButtonVisible(final boolean visible) {
        this.clear.setVisible(visible);
    }
    
    public boolean isClearButtonVisible() {
        return this.clear.isVisible();
    }
    
    public void setMoveUpButtonVisible(final boolean visible) {
        this.moveUp.setVisible(visible);
    }
    
    public boolean isMoveUpButtonVisible() {
        return this.moveUp.isVisible();
    }
    
    public void setMoveDownButtonVisible(final boolean visible) {
        this.moveDown.setVisible(visible);
    }
    
    public boolean isMoveDownButtonVisible() {
        return this.moveDown.isVisible();
    }
    
    public void setOptionsPanelVisible(final boolean visible) {
        this.optionsPanel.setVisible(visible);
    }
    
    public boolean isOptionsPanelVisible() {
        return this.optionsPanel.isVisible();
    }
    
    public void setAdvancedOptionVisible(final boolean visible) {
        this.advancedOption.setVisible(visible);
    }
    
    public boolean isAdvancedOptionVisible() {
        return this.advancedOption.isVisible();
    }
    
    public void setMatchOptionsVisible(final boolean visible) {
        this.matchOptionsPanel.setVisible(visible);
    }
    
    public boolean isMatchOptionsVisible() {
        return this.matchOptionsPanel.isVisible();
    }
    
    public void setMoreButtonIcon(final Icon icon) {
        this.more.setIcon(icon);
    }
    
    public Icon getMoreButtonIcon() {
        return this.more.getIcon();
    }
    
    public void setFewerButtonIcon(final Icon icon) {
        this.fewer.setIcon(icon);
    }
    
    public Icon getFewerButtonIcon() {
        return this.fewer.getIcon();
    }
    
    public void setClearButtonIcon(final Icon icon) {
        this.clear.setIcon(icon);
    }
    
    public Icon getClearButtonIcon() {
        return this.clear.getIcon();
    }
    
    public void setMoveUpButtonIcon(final Icon icon) {
        this.moveUp.setIcon(icon);
    }
    
    public Icon getMoveUpButtonIcon() {
        return this.moveUp.getIcon();
    }
    
    public void setMoveDownButtonIcon(final Icon icon) {
        this.moveDown.setIcon(icon);
    }
    
    public Icon getMoveDownButtonIcon() {
        return this.moveDown.getIcon();
    }
    
    public void setAdvancedOptionText(final String text) {
        this.advancedOption.setText(text);
    }
    
    public String getAdvancedOptionText() {
        return this.advancedOption.getText();
    }
    
    public void setMatchAllOptionText(final String text) {
        this.matchAll.setText(text);
    }
    
    public String getMatchAllOptionText() {
        return this.matchAll.getText();
    }
    
    public void setMatchAnyOptionText(final String text) {
        this.matchAny.setText(text);
    }
    
    public String getMatchAnyOptionText() {
        return this.matchAny.getText();
    }
    
    public void setOperatorForMatchAnyOption(final String operatorForMatchAnyOption) {
        this.operatorForMatchAnyOption = operatorForMatchAnyOption;
    }
    
    public String getOperatorForMatchAnyOption() {
        return this.operatorForMatchAnyOption;
    }
    
    public void setOperatorForMatchAllOption(final String operatorForMatchAllOption) {
        this.operatorForMatchAllOption = operatorForMatchAllOption;
    }
    
    public String getOperatorForMatchAllOption() {
        return this.operatorForMatchAllOption;
    }
    
    public Criteria getInputCriteria() {
        return this.criteriaTable1.getCriteria();
    }
    
    public void addOperandToDisplay(final String s) {
        this.getCriteriaTable().addOperandToDisplay(s);
        this.changeMode();
        this.changeMatchOption();
    }
    
    public void addCriteriaToDisplay(final Criteria criteria) {
        this.getCriteriaTable().addCriteriaToDisplay(criteria);
        this.changeMode();
        this.changeMatchOption();
    }
    
    public void addCriterionToDisplay(final Criterion criterion) {
        this.getCriteriaTable().addCriterionToDisplay(criterion);
        this.changeMode();
        this.changeMatchOption();
    }
    
    private void changeMode() {
        final DefaultTableModel defaultTableModel = (DefaultTableModel)this.getCriteriaTable().getModel();
        for (int i = 0; i < defaultTableModel.getRowCount(); ++i) {
            final Object value = defaultTableModel.getValueAt(i, 0);
            if (value != null && (value.equals("GROUP_START_INDEX") || value.equals("GROUP_END_INDEX"))) {
                this.setAdvancedMode(true);
                this.criteriaChanged(null);
                return;
            }
        }
        this.setAdvancedMode(false);
        this.criteriaChanged(null);
    }
    
    private void changeMatchOption() {
        if (!this.isAdvancedMode()) {
            final DefaultTableModel defaultTableModel = (DefaultTableModel)this.getCriteriaTable().getModel();
            final Object value = defaultTableModel.getValueAt(0, 4);
            if (value == null) {
                return;
            }
            for (int i = 1; i < defaultTableModel.getRowCount() - 1; ++i) {
                final Object value2 = defaultTableModel.getValueAt(i, 4);
                if (value2 != null && !value2.equals(value)) {
                    this.setAdvancedMode(true);
                    return;
                }
            }
            if (this.getOperatorForMatchAllOption().equals(value)) {
                this.matchAll.setSelected(true);
            }
            else {
                this.matchAny.setSelected(true);
            }
        }
    }
    
    public void removeLastCriterion() {
        this.criteriaTable1.removeLastCriterion();
    }
    
    public void addNewCriterion() {
        this.criteriaTable1.addNewCriterion();
    }
    
    public void clearAllCriterions() {
        this.criteriaTable1.clearAllCriterions();
    }
    
    public void setResourceBundle(final ResourceBundle resourceBundle) {
        this.bundle = resourceBundle;
        this.getCriteriaTable().setResourceBundle(resourceBundle);
        this.setUpTexts();
    }
    
    public AttributeValueEditorComponentsTable getDefaultAttributeValueEditorComponents() {
        return this.criteriaTable1.getDefaultAttributeValueEditorComponents();
    }
    
    public void setDefaultAttributeValueEditorComponent(final Class clazz, final AttributeValueEditorComponent attributeValueEditorComponent) {
        this.criteriaTable1.setDefaultAttributeValueEditorComponent(clazz, attributeValueEditorComponent);
    }
    
    public ComparatorsTable getDefaultComparators() {
        return this.criteriaTable1.getDefaultComparators();
    }
    
    public void setDefaultComparators(final Class clazz, final String[] array) {
        this.criteriaTable1.setDefaultComparators(clazz, array);
    }
    
    public ResourceBundle getResourceBundle() {
        return this.bundle;
    }
    
    public String getString(final String s) {
        String string = null;
        if (this.bundle != null) {
            try {
                string = this.bundle.getString(s);
            }
            catch (final MissingResourceException ex) {}
        }
        if (string == null || string.equals("")) {
            return s.trim();
        }
        return string;
    }
    
    public void attributeModelChanged(final AttributeModelEvent attributeModelEvent) {
        this.setEnabled(this.getAttributeModel().getAttributeCount() > 0);
    }
}
