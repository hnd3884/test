package com.adventnet.beans.smartsearchcomponent;

import java.util.MissingResourceException;
import java.awt.Font;
import com.adventnet.beans.smartsearchcomponent.events.SearchListener;
import java.util.EventListener;
import com.adventnet.beans.smartsearchcomponent.events.SearchEvent;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Component;
import com.adventnet.beans.morefewercomponent.MoreFewerComponent;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JButton;
import java.awt.Cursor;
import java.util.ResourceBundle;
import javax.swing.event.EventListenerList;
import javax.swing.JPanel;

public class SmartSearchComponent extends JPanel
{
    protected EventListenerList listenerList;
    protected ResourceBundle bundle;
    private Cursor busyCursor;
    private Cursor defaultCursor;
    private JPanel topPanel;
    private JPanel buttonsPanel;
    private JButton help;
    private JSplitPane jSplitPane1;
    private JLabel searchTargetText;
    private JPanel searchTargetPanel;
    private JButton search;
    private JButton stopSearch;
    private JComboBox searchTargetCombo;
    private MoreFewerComponent moreFewerComponent1;
    
    public SmartSearchComponent() {
        this.busyCursor = new Cursor(3);
        this.defaultCursor = new Cursor(0);
        this.initComponents();
        this.init();
    }
    
    private void init() {
        this.jSplitPane1.setBottomComponent(new JPanel());
        this.listenerList = new EventListenerList();
        this.setUpTexts();
        this.search.setEnabled(true);
        this.stopSearch.setEnabled(false);
    }
    
    private void initComponents() {
        this.topPanel = new JPanel();
        this.searchTargetPanel = new JPanel();
        this.searchTargetText = new JLabel();
        this.searchTargetCombo = new JComboBox();
        this.buttonsPanel = new JPanel();
        this.search = new JButton();
        this.stopSearch = new JButton();
        this.help = new JButton();
        this.jSplitPane1 = new JSplitPane();
        this.moreFewerComponent1 = new MoreFewerComponent();
        this.setLayout(new BorderLayout());
        this.topPanel.setLayout(new BorderLayout());
        this.searchTargetPanel.setLayout(new FlowLayout(0));
        this.searchTargetText.setText("Search Items in:");
        this.searchTargetPanel.add(this.searchTargetText);
        this.searchTargetPanel.add(this.searchTargetCombo);
        this.topPanel.add(this.searchTargetPanel, "Center");
        this.buttonsPanel.setLayout(new GridBagLayout());
        this.search.setText("Search");
        this.search.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                SmartSearchComponent.this.searchActionPerformed(actionEvent);
            }
        });
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.anchor = 11;
        this.buttonsPanel.add(this.search, gridBagConstraints);
        this.stopSearch.setText("Stop Search");
        this.stopSearch.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                SmartSearchComponent.this.stopSearchActionPerformed(actionEvent);
            }
        });
        final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.fill = 2;
        gridBagConstraints2.anchor = 11;
        gridBagConstraints2.insets = new Insets(5, 0, 5, 0);
        this.buttonsPanel.add(this.stopSearch, gridBagConstraints2);
        this.help.setText("Help");
        final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.gridy = 2;
        gridBagConstraints3.fill = 2;
        gridBagConstraints3.anchor = 11;
        gridBagConstraints3.insets = new Insets(0, 0, 5, 0);
        this.buttonsPanel.add(this.help, gridBagConstraints3);
        this.topPanel.add(this.buttonsPanel, "East");
        this.add(this.topPanel, "North");
        this.jSplitPane1.setOrientation(0);
        this.jSplitPane1.setNextFocusableComponent(this.searchTargetText);
        this.jSplitPane1.setOneTouchExpandable(true);
        this.moreFewerComponent1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        this.jSplitPane1.setLeftComponent(this.moreFewerComponent1);
        this.add(this.jSplitPane1, "Center");
    }
    
    private void stopSearchActionPerformed(final ActionEvent actionEvent) {
        this.fireStopSearchEvent(new SearchEvent(this, this.moreFewerComponent1.getInputCriteria(), this.searchTargetCombo.getSelectedItem()));
    }
    
    private void searchActionPerformed(final ActionEvent actionEvent) {
        this.fireStartSearchEvent(new SearchEvent(this, this.moreFewerComponent1.getInputCriteria(), this.searchTargetCombo.getSelectedItem()));
    }
    
    protected void fireStartSearchEvent(final SearchEvent searchEvent) {
        final FireEventThread fireEventThread = new FireEventThread(this.listenerList.getListeners((Class<EventListener>)SearchListener.class), searchEvent, true);
        this.setCursor(this.busyCursor);
        this.search.setEnabled(false);
        this.stopSearch.setEnabled(true);
        fireEventThread.start();
    }
    
    protected void fireStopSearchEvent(final SearchEvent searchEvent) {
        final FireEventThread fireEventThread = new FireEventThread(this.listenerList.getListeners((Class<EventListener>)SearchListener.class), searchEvent, false);
        this.stopSearch.setEnabled(false);
        this.search.setEnabled(true);
        this.setCursor(this.defaultCursor);
        fireEventThread.start();
    }
    
    public void addSearchListener(final SearchListener searchListener) {
        this.listenerList.add(SearchListener.class, searchListener);
    }
    
    public void removeSearchListener(final SearchListener searchListener) {
        this.listenerList.remove(SearchListener.class, searchListener);
    }
    
    public void setButtonsPanelVisible(final boolean visible) {
        this.buttonsPanel.setVisible(visible);
    }
    
    public boolean isButtonsPanelVisible() {
        return this.buttonsPanel.isValid();
    }
    
    public void setHelpButtonVisible(final boolean visible) {
        this.help.setVisible(visible);
    }
    
    public boolean isHelpButtonVisible() {
        return this.help.isVisible();
    }
    
    public void setSearchTargetPanelVisible(final boolean visible) {
        this.searchTargetPanel.setVisible(visible);
    }
    
    public boolean isSearchTargetPanelVisible() {
        return this.searchTargetPanel.isVisible();
    }
    
    public void setSearchTargetLabelVisible(final boolean visible) {
        this.searchTargetText.setVisible(visible);
    }
    
    public boolean isSearchTargetLabelVisible() {
        return this.searchTargetText.isVisible();
    }
    
    public void setSearchTargetComboVisible(final boolean visible) {
        this.searchTargetCombo.setVisible(visible);
    }
    
    public boolean isSearchTargetComboVisible() {
        return this.searchTargetCombo.isVisible();
    }
    
    public void addSearchTargetComboItems(final Object[] array) {
        for (int i = 0; i < array.length; ++i) {
            this.searchTargetCombo.addItem(array[i]);
        }
    }
    
    public void addSearchTargetComboItem(final Object o) {
        this.searchTargetCombo.addItem(o);
    }
    
    public Object[] getSearchTargetComboItems() {
        final Object[] array = new Object[this.searchTargetCombo.getItemCount()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.searchTargetCombo.getItemAt(i);
        }
        return array;
    }
    
    public void setSearchTargetComboItems(final Object[] array) {
        this.searchTargetCombo.removeAllItems();
        this.addSearchTargetComboItems(array);
    }
    
    public void setSearchTargetText(final String text) {
        this.searchTargetText.setText(text);
    }
    
    public String getSearchTargetText() {
        return this.searchTargetText.getText();
    }
    
    public void setSearchButtonText(final String text) {
        this.search.setText(text);
    }
    
    public String getSearchButtonText() {
        return this.search.getText();
    }
    
    public void setStopSearchButtonText(final String text) {
        this.stopSearch.setText(text);
    }
    
    public String getStopSearchButtonText() {
        return this.stopSearch.getText();
    }
    
    public void setHelpButtonText(final String text) {
        this.help.setText(text);
    }
    
    public String getHelpButtonText() {
        return this.help.getText();
    }
    
    public void startSearch() {
        this.fireStartSearchEvent(new SearchEvent(this, this.moreFewerComponent1.getInputCriteria(), this.searchTargetCombo.getSelectedItem()));
    }
    
    public void stopSearch() {
        this.fireStopSearchEvent(new SearchEvent(this, this.moreFewerComponent1.getInputCriteria(), this.searchTargetCombo.getSelectedItem()));
    }
    
    private void setUpTexts() {
        final Font font = this.search.getFont();
        this.search.setFont(new Font(font.getName(), 0, font.getSize()));
        this.search.setText(this.getString("Search"));
        final Font font2 = this.stopSearch.getFont();
        this.stopSearch.setFont(new Font(font2.getName(), 0, font2.getSize()));
        this.stopSearch.setText(this.getString("Stop Search"));
        final Font font3 = this.help.getFont();
        this.help.setFont(new Font(font3.getName(), 0, font3.getSize()));
        this.help.setText(this.getString("Help"));
        final Font font4 = this.searchTargetText.getFont();
        this.searchTargetText.setFont(new Font(font4.getName(), 0, font4.getSize()));
        this.searchTargetText.setText(this.getString("Search items in"));
        final Font font5 = this.searchTargetCombo.getFont();
        this.searchTargetCombo.setFont(new Font(font5.getName(), 0, font5.getSize()));
    }
    
    public void setResultsComponent(final Component bottomComponent) {
        this.search.setEnabled(true);
        this.stopSearch.setEnabled(false);
        this.setCursor(this.defaultCursor);
        this.jSplitPane1.setBottomComponent(bottomComponent);
    }
    
    public Component getResultsComponent() {
        return this.jSplitPane1.getBottomComponent();
    }
    
    public MoreFewerComponent getMoreFewerComponent() {
        return this.moreFewerComponent1;
    }
    
    public JComboBox getSearchTargetCombo() {
        return this.searchTargetCombo;
    }
    
    public void setResourceBundle(final ResourceBundle bundle) {
        this.bundle = bundle;
        this.setUpTexts();
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
    
    class FireEventThread extends Thread
    {
        private EventListener[] arr;
        private SearchEvent eventObj;
        private boolean start;
        
        public FireEventThread(final EventListener[] arr, final SearchEvent eventObj, final boolean start) {
            this.arr = arr;
            this.eventObj = eventObj;
            this.start = start;
        }
        
        public void run() {
            for (int i = 0; i < this.arr.length; ++i) {
                if (this.start) {
                    ((SearchListener)this.arr[i]).startSearch(this.eventObj);
                }
                else {
                    ((SearchListener)this.arr[i]).stopSearch(this.eventObj);
                }
            }
        }
    }
}
