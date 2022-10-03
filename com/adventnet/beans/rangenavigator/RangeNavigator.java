package com.adventnet.beans.rangenavigator;

import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import java.awt.event.FocusEvent;
import com.adventnet.beans.rangenavigator.events.ActionComboEvent;
import java.awt.event.ActionEvent;
import java.util.EventListener;
import java.util.ArrayList;
import java.util.MissingResourceException;
import com.adventnet.beans.rangenavigator.events.ActionComboListener;
import javax.swing.JToggleButton;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.text.Document;
import javax.swing.JTextField;
import java.awt.Component;
import com.adventnet.beans.rangenavigator.events.NavigationEvent;
import java.beans.PropertyChangeSupport;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JPanel;
import java.util.ResourceBundle;
import java.io.Serializable;
import java.awt.event.KeyListener;
import java.awt.event.FocusListener;
import java.awt.event.ActionListener;
import com.adventnet.beans.rangenavigator.events.NavigationListener;
import javax.swing.JToolBar;

public class RangeNavigator extends JToolBar implements NavigationListener, ActionListener, FocusListener, KeyListener, Serializable
{
    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;
    private int align;
    private LabeledNumericTextField totalPanel;
    private RangePanel fromtoPanel;
    private RangePanel pagePanel;
    private PageLengthPanel pageLengthPanel;
    private NavigationButtonsPanel buttonPanel;
    private ActionComboPanel actionPanel;
    private static ResourceBundle bundle;
    private JPanel[] panelArray;
    private NumericTextField numericTextField;
    private NavigationModel dataModel;
    private Vector actionVector;
    private Hashtable actionComboItems;
    private PropertyChangeSupport propertySupport;
    private long[] comboArr;
    private long lastEnteredPageLen;
    private boolean acceptAllPageLengths;
    
    public RangeNavigator() {
        this.align = 0;
        this.numericTextField = null;
        this.dataModel = null;
        this.actionVector = null;
        this.actionComboItems = null;
        this.comboArr = new long[] { 10L, 25L, 50L, 100L };
        this.acceptAllPageLengths = false;
        this.init();
    }
    
    private void init() {
        this.totalPanel = new LabeledNumericTextField("Total", 0L, 5);
        this.fromtoPanel = new RangePanel("From", 0L, "To", 0L, 5, 5);
        this.pagePanel = new RangePanel("Page", 0L, "of", 0L, 5, 5);
        this.pageLengthPanel = new PageLengthPanel();
        this.buttonPanel = new NavigationButtonsPanel();
        this.actionPanel = new ActionComboPanel();
        this.panelArray = new JPanel[6];
        this.actionVector = new Vector();
        this.setTotalFieldEditable(false);
        this.setPageLengthComboEditable(true);
        this.setTotalPagesEditable(false);
        this.setRangeNavigatorUI();
        this.setPageLengths(this.comboArr);
        this.setModel(this.createDefaultDataModel());
        this.getFromField().addActionListener(this);
        this.getFromField().addFocusListener(this);
        this.getFromField().addKeyListener(this);
        this.getToField().addActionListener(this);
        this.getToField().addFocusListener(this);
        this.getToField().addKeyListener(this);
        this.getPageField().addFocusListener(this);
        this.getPageField().addActionListener(this);
        this.getPageField().addKeyListener(this);
        this.getActionCombo().addActionListener(this);
        this.getPageLengthTextField().addFocusListener(this);
        this.getPageLengthTextField().addKeyListener(this);
        this.getFirstButton().addActionListener(this);
        this.getPreviousButton().addActionListener(this);
        this.getNextButton().addActionListener(this);
        this.getLastButton().addActionListener(this);
        this.getPageLengthCombo().addActionListener(this);
        this.getActionPanel().setVisible(false);
        this.setResourceBundle(ResourceBundle.getBundle("com/adventnet/beans/rangenavigator/RangeNavigator"));
        super.setFloatable(false);
    }
    
    public void setModel(final NavigationModel dataModel) {
        if (dataModel == null) {
            new IllegalArgumentException("Cannot set null model to RangeNavigator");
        }
        if (this.dataModel != dataModel) {
            (this.dataModel = dataModel).addNavigationListener(this);
            this.navigationChanged(new NavigationEvent(dataModel));
        }
    }
    
    protected NavigationModel createDefaultDataModel() {
        return new DefaultNavigationModel(100L);
    }
    
    public NavigationModel getModel() {
        return this.dataModel;
    }
    
    public LabeledNumericTextField getTotalPanel() {
        return this.totalPanel;
    }
    
    public RangePanel getFromToPanel() {
        return this.fromtoPanel;
    }
    
    public RangePanel getPagePanel() {
        return this.pagePanel;
    }
    
    public PageLengthPanel getPageLengthPanel() {
        return this.pageLengthPanel;
    }
    
    public NavigationButtonsPanel getButtonPanel() {
        return this.buttonPanel;
    }
    
    public ActionComboPanel getActionPanel() {
        return this.actionPanel;
    }
    
    private void intializeRNPanels() {
        (this.panelArray[0] = this.totalPanel).setMinimumSize(this.panelArray[0].getPreferredSize());
        (this.panelArray[1] = this.fromtoPanel).setMinimumSize(this.panelArray[1].getPreferredSize());
        (this.panelArray[2] = this.pagePanel).setMinimumSize(this.panelArray[2].getPreferredSize());
        (this.panelArray[3] = this.pageLengthPanel).setMinimumSize(this.panelArray[3].getPreferredSize());
        (this.panelArray[4] = this.buttonPanel).setMinimumSize(this.panelArray[4].getPreferredSize());
        (this.panelArray[5] = this.actionPanel).setMinimumSize(this.panelArray[5].getPreferredSize());
    }
    
    protected void setRangeNavigatorUI() {
        this.intializeRNPanels();
        for (int i = 0; i < 6; ++i) {
            this.add(this.panelArray[i]);
        }
        this.updateUI();
    }
    
    public JTextField getFromField() {
        return this.fromtoPanel.getFirstComponent().getField();
    }
    
    public JTextField getToField() {
        return this.fromtoPanel.getSecondComponent().getField();
    }
    
    public JTextField getPageField() {
        final JTextField field = this.pagePanel.getFirstComponent().getField();
        field.setDocument(new NumericTextField("0123456789-"));
        field.setCaretPosition(0);
        return field;
    }
    
    private void setTotalFieldEditable(final boolean editable) {
        this.totalPanel.getField().setEditable(editable);
    }
    
    public void setPageLengthComboEditable(final boolean editable) {
        this.pageLengthPanel.getPageLengthCombo().setEditable(editable);
    }
    
    private void setTotalPagesEditable(final boolean editable) {
        this.pagePanel.getSecondComponent().getField().setEditable(editable);
    }
    
    public void setPageLengths(final long[] comboArr) {
        this.comboArr = comboArr;
        this.getPageLengthCombo().removeAllItems();
        for (int i = 0; i < comboArr.length; ++i) {
            this.getPageLengthCombo().addItem(new Long(comboArr[i]));
        }
    }
    
    public long[] getPageLengths() {
        return this.comboArr;
    }
    
    public long[] getPageLengthComboValues() {
        final long[] array = new long[this.getPageLengthCombo().getItemCount()];
        for (int i = 0; i < this.getPageLengthCombo().getItemCount(); ++i) {
            array[i] = (long)this.getPageLengthCombo().getItemAt(i);
        }
        return array;
    }
    
    private JTextField getPageLengthTextField() {
        return (JTextField)this.pageLengthPanel.getPageLengthCombo().getEditor().getEditorComponent();
    }
    
    public JComboBox getPageLengthCombo() {
        return this.pageLengthPanel.getPageLengthCombo();
    }
    
    private void setFirstButtonToolTip(final String toolTipText) {
        if (toolTipText != null) {
            if (!"".equals(toolTipText)) {
                this.buttonPanel.getFirstButton().setToolTipText(toolTipText);
            }
            else {
                this.buttonPanel.getFirstButton().setToolTipText("First");
            }
        }
        else {
            this.buttonPanel.getFirstButton().setToolTipText("First");
        }
    }
    
    private void setPreviousButtonToolTip(final String toolTipText) {
        if (toolTipText != null) {
            if (!"".equals(toolTipText)) {
                this.buttonPanel.getPreviousButton().setToolTipText(toolTipText);
            }
            else {
                this.buttonPanel.getPreviousButton().setToolTipText("Previous");
            }
        }
        else {
            this.buttonPanel.getPreviousButton().setToolTipText("Previous");
        }
    }
    
    private void setNextButtonToolTip(final String toolTipText) {
        if (toolTipText != null) {
            if (!"".equals(toolTipText)) {
                this.buttonPanel.getNextButton().setToolTipText(toolTipText);
            }
            else {
                this.buttonPanel.getNextButton().setToolTipText("Next");
            }
        }
        else {
            this.buttonPanel.getNextButton().setToolTipText("Next");
        }
    }
    
    private void setLastButtonToolTip(final String toolTipText) {
        if (toolTipText != null) {
            if (!"".equals(toolTipText)) {
                this.buttonPanel.getLastButton().setToolTipText(toolTipText);
            }
            else {
                this.buttonPanel.getLastButton().setToolTipText("Last");
            }
        }
        else {
            this.buttonPanel.getLastButton().setToolTipText("Last");
        }
    }
    
    public JButton getFirstButton() {
        return this.buttonPanel.getFirstButton();
    }
    
    public JButton getPreviousButton() {
        return this.buttonPanel.getPreviousButton();
    }
    
    public JButton getNextButton() {
        return this.buttonPanel.getNextButton();
    }
    
    public JButton getLastButton() {
        return this.buttonPanel.getLastButton();
    }
    
    public JLabel getTotalLabel() {
        return this.totalPanel.getLabelComponent();
    }
    
    public JLabel getFromLabel() {
        return this.fromtoPanel.getFirstComponent().getLabelComponent();
    }
    
    public JLabel getToLabel() {
        return this.fromtoPanel.getSecondComponent().getLabelComponent();
    }
    
    public JLabel getPageLabel() {
        return this.pagePanel.getFirstComponent().getLabelComponent();
    }
    
    public JLabel getShowLabel() {
        return this.pageLengthPanel.getShowLabel();
    }
    
    public String getPerPageLabel() {
        return this.pageLengthPanel.getPerPageText();
    }
    
    public String getOfLabel() {
        return this.pagePanel.getSecondText();
    }
    
    public JLabel getActionLabel() {
        return this.actionPanel.getActionLabelComponent();
    }
    
    public void setTotalLabel(final String label) {
        if (label == null || label.trim().equals("")) {
            this.totalPanel.setLabel(getString("Total"));
        }
        else {
            this.totalPanel.setLabel(label);
        }
    }
    
    public void setFromLabel(final String firstText) {
        if (firstText == null || firstText.trim().equals("")) {
            this.fromtoPanel.setFirstText(getString("From"));
        }
        else {
            this.fromtoPanel.setFirstText(firstText);
        }
    }
    
    public void setFromLabel(final String firstText, final char displayedMnemonic) {
        if (firstText == null || firstText.trim().equals("")) {
            this.fromtoPanel.setFirstText(getString("From"));
        }
        else {
            this.fromtoPanel.setFirstText(firstText);
        }
        this.getFromLabel().setDisplayedMnemonic(displayedMnemonic);
        this.getFromLabel().setLabelFor(this.getFromField());
    }
    
    public void setToLabel(final String secondText) {
        if (secondText == null || secondText.trim().equals("")) {
            this.fromtoPanel.setSecondText(getString("To"));
        }
        else {
            this.fromtoPanel.setSecondText(secondText);
        }
    }
    
    public void setToLabel(final String secondText, final char displayedMnemonic) {
        if (secondText == null || secondText.trim().equals("")) {
            this.fromtoPanel.setSecondText(getString("To"));
        }
        else {
            this.fromtoPanel.setSecondText(secondText);
        }
        this.getToLabel().setDisplayedMnemonic(displayedMnemonic);
        this.getToLabel().setLabelFor(this.getToField());
    }
    
    public void setPageLabel(final String firstText) {
        if (firstText == null || firstText.trim().equals("")) {
            this.pagePanel.setFirstText(getString("Page"));
        }
        else {
            this.pagePanel.setFirstText(firstText);
        }
    }
    
    public void setPageLabel(final String firstText, final char displayedMnemonic) {
        if (firstText == null || firstText.trim().equals("")) {
            this.pagePanel.setFirstText(getString("Page"));
        }
        else {
            this.pagePanel.setFirstText(firstText);
        }
        this.getPageLabel().setDisplayedMnemonic(displayedMnemonic);
        this.getPageLabel().setLabelFor(this.getPageField());
    }
    
    public void setShowLabel(final String pageLengthText) {
        if (pageLengthText == null || pageLengthText.trim().equals("")) {
            this.pageLengthPanel.setPageLengthText(getString("Show"));
        }
        else {
            this.pageLengthPanel.setPageLengthText(pageLengthText);
        }
    }
    
    public void setShowLabel(final String pageLengthText, final char displayedMnemonic) {
        if (pageLengthText == null || pageLengthText.trim().equals("")) {
            this.pageLengthPanel.setPageLengthText(getString("Show"));
        }
        else {
            this.pageLengthPanel.setPageLengthText(pageLengthText);
        }
        this.getShowLabel().setDisplayedMnemonic(displayedMnemonic);
        this.getShowLabel().setLabelFor(this.getPageLengthCombo());
    }
    
    public void setPerPageLabel(final String perPageText) {
        if (perPageText == null || perPageText.trim().equals("")) {
            this.pageLengthPanel.setPerPageText(getString("per page"));
        }
        else {
            this.pageLengthPanel.setPerPageText(perPageText);
        }
    }
    
    public void setOfLabel(final String secondText) {
        if (secondText == null || secondText.trim().equals("")) {
            this.pagePanel.setSecondText(getString("of"));
        }
        else {
            this.pagePanel.setSecondText(secondText);
        }
    }
    
    public void setActionLabel(final String actionLabel) {
        if (actionLabel == null || actionLabel.trim().equals("")) {
            this.actionPanel.setActionLabel(getString("Action"));
        }
        else {
            this.actionPanel.setActionLabel(actionLabel);
        }
    }
    
    public void setActionLabel(final String actionLabel, final char displayedMnemonic) {
        if (actionLabel == null || actionLabel.trim().equals("")) {
            this.actionPanel.setActionLabel(getString("Action"));
        }
        else {
            this.actionPanel.setActionLabel(actionLabel);
        }
        this.getActionLabel().setDisplayedMnemonic(displayedMnemonic);
        this.getActionLabel().setLabelFor(this.getActionCombo());
    }
    
    public void addActionComboStringItems(final String[] array) {
        this.actionPanel.addActionComboStringItems(array);
    }
    
    public void addActionComboButtonItem(final JToggleButton toggleButton) {
        this.actionPanel.addActionComboButtonItems(toggleButton);
    }
    
    public JComboBox getActionCombo() {
        return this.actionPanel.getActionCombo();
    }
    
    public void addActionComboListener(final ActionComboListener actionComboListener) {
        this.actionVector.add(actionComboListener);
    }
    
    public void removeActionComboListener(final ActionComboListener actionComboListener) {
        this.actionVector.remove(actionComboListener);
    }
    
    public void setResourceBundle(final ResourceBundle bundle) {
        RangeNavigator.bundle = bundle;
        this.setUpProperties();
    }
    
    public ResourceBundle getResourceBundle() {
        return RangeNavigator.bundle;
    }
    
    public static String getString(final String s) {
        String string = null;
        if (RangeNavigator.bundle != null) {
            try {
                string = RangeNavigator.bundle.getString(s);
            }
            catch (final MissingResourceException ex) {}
        }
        if (string == null || string.equals("")) {
            return s.trim();
        }
        return string;
    }
    
    private void setUpProperties() {
        this.setTotalLabel(getString("Total"));
        this.setFromLabel(getString("From"), 'm');
        this.setToLabel(getString("To"), 't');
        this.setPageLabel(getString("Page"), 'g');
        this.setShowLabel(getString("Show"), 's');
        this.setPerPageLabel(getString("per page"));
        this.setOfLabel(getString("of"));
        this.setActionLabel(getString("Action"), 'a');
        this.setFirstButtonToolTip(getString("First"));
        this.getFirstButton().setMnemonic('f');
        this.setPreviousButtonToolTip(getString("Previous"));
        this.getNextButton().setMnemonic('n');
        this.setNextButtonToolTip(getString("Next"));
        this.getPreviousButton().setMnemonic('p');
        this.setLastButtonToolTip(getString("Last"));
        this.getLastButton().setMnemonic('l');
    }
    
    private void calculateTotalPagesAndCurrentPage() {
        final long startIndex = this.getModel().getStartIndex();
        final long endIndex = this.getModel().getEndIndex();
        final long pageLength = this.getModel().getPageLength();
        final long totalRecordsCount = this.getModel().getTotalRecordsCount();
        long totalPages = totalRecordsCount / pageLength;
        if (totalRecordsCount % pageLength != 0L) {
            ++totalPages;
        }
        this.setTotalPages(totalPages);
        if (totalPages == 0L) {
            this.setCurrentPage(0L);
            return;
        }
        if (pageLength == 1L) {
            this.setCurrentPage(startIndex);
            return;
        }
        final long n = startIndex / pageLength;
        final long n2 = endIndex / pageLength;
        final long n3 = n * pageLength + 1L + pageLength - 1L;
        if (Math.abs(n3 - endIndex) > Math.abs(startIndex - n3)) {
            this.setCurrentPage(n2 + 1L);
        }
        else {
            this.setCurrentPage(n + 1L);
        }
    }
    
    private void setCurrentPage(final long n) {
        this.getPagePanel().getFirstComponent().getField().setText(String.valueOf(n));
    }
    
    private void setTotalPages(final long n) {
        this.getPagePanel().getSecondComponent().getField().setText(String.valueOf(n));
    }
    
    public long getTotalPages() {
        return Long.parseLong(this.getPagePanel().getFirstComponent().getField().getText());
    }
    
    private void showPage(final long n) {
        final long pageLength = this.getModel().getPageLength();
        final long n2 = (n - 1L) * pageLength + 1L;
        this.getModel().showRange(n2, n2 + pageLength - 1L);
    }
    
    public long getCurrentPage() {
        return Long.parseLong(this.getPagePanel().getFirstComponent().getField().getText());
    }
    
    public void setAcceptAllPageLengths(final boolean acceptAllPageLengths) {
        this.acceptAllPageLengths = acceptAllPageLengths;
    }
    
    public boolean getAcceptAllPageLengths() {
        return this.acceptAllPageLengths;
    }
    
    private void displayPageLength(final long lastEnteredPageLen) {
        final EventListener[] listeners = this.getPageLengthCombo().getListeners(ActionListener.class);
        for (int i = 0; i < listeners.length; ++i) {
            this.getPageLengthCombo().removeActionListener((ActionListener)listeners[i]);
        }
        final Long n = new Long(lastEnteredPageLen);
        final ArrayList list = new ArrayList();
        if (!this.acceptAllPageLengths) {
            this.getPageLengthCombo().removeAllItems();
            for (int j = 0; j < this.getPageLengths().length; ++j) {
                final Long n2 = new Long(this.getPageLengths()[j]);
                list.add(n2);
                this.getPageLengthCombo().addItem(n2);
            }
            boolean b = false;
            if (!list.contains(n)) {
                b = true;
                list.add(n);
                this.lastEnteredPageLen = lastEnteredPageLen;
                this.getPageLengthCombo().addItem(n);
            }
            if (!b && this.lastEnteredPageLen > 0L) {
                this.getPageLengthCombo().addItem(new Long(this.lastEnteredPageLen));
            }
            this.getPageLengthCombo().setSelectedItem(n);
        }
        else {
            for (int k = 0; k < this.getPageLengthCombo().getItemCount(); ++k) {
                list.add(this.getPageLengthCombo().getItemAt(k));
            }
            if (!list.contains(n)) {
                this.getPageLengthCombo().addItem(n);
            }
            this.getPageLengthCombo().setSelectedItem(n);
        }
        for (int l = 0; l < listeners.length; ++l) {
            this.getPageLengthCombo().addActionListener((ActionListener)listeners[l]);
        }
    }
    
    public long getPageLength() {
        return Long.parseLong(this.getPageLengthCombo().getSelectedItem().toString());
    }
    
    public void navigationChanged(final NavigationEvent navigationEvent) {
        final NavigationModel model = this.getModel();
        this.getTotalPanel().getField().setText(String.valueOf(model.getTotalRecordsCount()));
        this.getFromToPanel().getFirstComponent().getField().setText(String.valueOf(model.getStartIndex()));
        this.getFromToPanel().getSecondComponent().getField().setText(String.valueOf(model.getEndIndex()));
        this.displayPageLength(model.getPageLength());
        this.calculateTotalPagesAndCurrentPage();
        this.setButtonStates();
    }
    
    private void setButtonStates() {
        final long startIndex = this.getModel().getStartIndex();
        final long endIndex = this.getModel().getEndIndex();
        this.getModel().getPageLength();
        final long totalRecordsCount = this.getModel().getTotalRecordsCount();
        this.getFirstButton().setEnabled(startIndex != 1L && startIndex != 0L);
        this.getLastButton().setEnabled(endIndex != totalRecordsCount);
        this.getPreviousButton().setEnabled(startIndex != 1L && startIndex != 0L);
        this.getNextButton().setEnabled(endIndex != totalRecordsCount);
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
        this.getModel().getStartIndex();
        this.getModel().getEndIndex();
        final long pageLength = this.getModel().getPageLength();
        this.getModel().getTotalRecordsCount();
        if (actionEvent.getSource() == this.getFirstButton()) {
            this.getModel().showRange(1L, 1L + pageLength - 1L);
            return;
        }
        if (actionEvent.getSource() == this.getLastButton()) {
            final long totalRecordsCount = this.getModel().getTotalRecordsCount();
            this.getModel().showRange((totalRecordsCount - pageLength + 1L < 0L) ? 1L : (totalRecordsCount - pageLength + 1L), totalRecordsCount);
            return;
        }
        if (actionEvent.getSource() == this.getNextButton()) {
            final long n = this.getModel().getEndIndex() + 1L;
            long totalRecordsCount2 = n + this.getModel().getPageLength() - 1L;
            if (totalRecordsCount2 > this.getModel().getTotalRecordsCount()) {
                totalRecordsCount2 = this.getModel().getTotalRecordsCount();
            }
            this.getModel().showRange(n, totalRecordsCount2);
            return;
        }
        if (actionEvent.getSource() == this.getPreviousButton()) {
            long n2 = this.getModel().getStartIndex() - 1L;
            final long pageLength2 = this.getModel().getPageLength();
            long n3 = n2 - pageLength2 + 1L;
            if (n3 <= 0L) {
                n3 = 1L;
            }
            if (n2 - n3 + 1L < pageLength2) {
                n2 = pageLength2;
            }
            this.getModel().showRange(n3, n2);
            return;
        }
        if (actionEvent.getSource() == this.getPageLengthCombo()) {
            long long1;
            try {
                long1 = Long.parseLong(this.getPageLengthCombo().getSelectedItem().toString());
            }
            catch (final NumberFormatException ex) {
                this.navigationChanged(new NavigationEvent(this.dataModel));
                return;
            }
            catch (final NullPointerException ex2) {
                return;
            }
            if (long1 != 0L) {
                this.getModel().setPageLength(long1);
            }
            else {
                this.navigationChanged(new NavigationEvent(this.dataModel));
            }
            return;
        }
        if (actionEvent.getSource() != this.getFromToPanel().getFirstComponent().getField()) {
            if (actionEvent.getSource() != this.getFromToPanel().getSecondComponent().getField()) {
                if (actionEvent.getSource() == this.getPagePanel().getFirstComponent().getField()) {
                    try {
                        this.showPage(Long.parseLong(this.getPagePanel().getFirstComponent().getField().getText()));
                    }
                    catch (final NumberFormatException ex3) {
                        this.navigationChanged(new NavigationEvent(this.dataModel));
                    }
                }
                if (actionEvent.getSource() == this.getActionCombo() && this.getActionCombo().getSelectedItem() != null) {
                    this.fireActionComboEvent();
                }
                return;
            }
        }
        long long2;
        long long3;
        try {
            long2 = Long.parseLong(this.getFromToPanel().getFirstComponent().getField().getText());
            long3 = Long.parseLong(this.getFromToPanel().getSecondComponent().getField().getText());
        }
        catch (final NumberFormatException ex4) {
            this.navigationChanged(new NavigationEvent(this.dataModel));
            return;
        }
        final long n4 = long3 - long2 + 1L;
        if (n4 != 0L && long2 > 0L && long3 > 0L) {
            this.getModel().setPageLength((n4 > this.getModel().getTotalRecordsCount()) ? this.getModel().getTotalRecordsCount() : n4);
        }
        this.getModel().showRange(long2, long3);
    }
    
    private void fireActionComboEvent() {
        final ActionComboEvent actionEventObject = this.createActionEventObject();
        for (int i = 0; i < this.actionVector.size(); ++i) {
            ((ActionComboListener)this.actionVector.get(i)).actionChangedEvent(actionEventObject);
        }
    }
    
    private ActionComboEvent createActionEventObject() {
        final ActionComboEvent actionComboEvent = new ActionComboEvent(this);
        actionComboEvent.totalValue = this.getModel().getTotalRecordsCount();
        actionComboEvent.fromValue = this.getModel().getStartIndex();
        actionComboEvent.toValue = this.getModel().getEndIndex();
        actionComboEvent.pageNo = this.getCurrentPage();
        actionComboEvent.totalPages = this.getTotalPages();
        actionComboEvent.actionItem = this.actionPanel.getActionCombo().getSelectedItem();
        actionComboEvent.pageLength = this.getModel().getPageLength();
        return actionComboEvent;
    }
    
    public void focusLost(final FocusEvent focusEvent) {
        if (focusEvent.getSource() == this.getFromToPanel().getFirstComponent().getField()) {
            return;
        }
        if (focusEvent.getSource() == this.getFromToPanel().getSecondComponent().getField()) {
            this.navigationChanged(new NavigationEvent(this.dataModel));
            return;
        }
        this.actionPerformed(new ActionEvent(focusEvent.getSource(), focusEvent.getID(), ""));
    }
    
    public void focusGained(final FocusEvent focusEvent) {
        final Object source = focusEvent.getSource();
        if (source != null && source instanceof JTextComponent) {
            ((JTextComponent)source).selectAll();
        }
    }
    
    public void keyPressed(final KeyEvent keyEvent) {
    }
    
    public void keyReleased(final KeyEvent keyEvent) {
    }
    
    public void keyTyped(final KeyEvent keyEvent) {
        final Object source = keyEvent.getSource();
        if (source instanceof JTextComponent) {
            final JTextComponent textComponent = (JTextComponent)source;
            final int caretPosition = textComponent.getCaretPosition();
            if ((caretPosition == 0 || (textComponent.getSelectedText() != null && textComponent.getSelectedText().length() == caretPosition)) && keyEvent.getKeyChar() == '0') {
                keyEvent.consume();
            }
        }
    }
}
