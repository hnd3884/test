package org.jfree.ui;

import java.util.Enumeration;
import java.awt.Insets;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.util.Vector;
import javax.swing.BorderFactory;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.Font;
import org.jfree.date.SerialDate;
import java.awt.Color;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

public class SerialDateChooserPanel extends JPanel implements ActionListener
{
    public static final Color DEFAULT_DATE_BUTTON_COLOR;
    public static final Color DEFAULT_MONTH_BUTTON_COLOR;
    private SerialDate date;
    private Color dateButtonColor;
    private Color monthButtonColor;
    private Color chosenOtherButtonColor;
    private int firstDayOfWeek;
    private int yearSelectionRange;
    private Font dateFont;
    private JComboBox monthSelector;
    private JComboBox yearSelector;
    private JButton todayButton;
    private JButton[] buttons;
    private boolean refreshing;
    
    static {
        DEFAULT_DATE_BUTTON_COLOR = Color.red;
        DEFAULT_MONTH_BUTTON_COLOR = Color.lightGray;
    }
    
    public SerialDateChooserPanel() {
        this(SerialDate.createInstance(new Date()), false, SerialDateChooserPanel.DEFAULT_DATE_BUTTON_COLOR, SerialDateChooserPanel.DEFAULT_MONTH_BUTTON_COLOR);
    }
    
    public SerialDateChooserPanel(final SerialDate date, final boolean controlPanel) {
        this(date, controlPanel, SerialDateChooserPanel.DEFAULT_DATE_BUTTON_COLOR, SerialDateChooserPanel.DEFAULT_MONTH_BUTTON_COLOR);
    }
    
    public SerialDateChooserPanel(final SerialDate date, final boolean controlPanel, final Color dateButtonColor, final Color monthButtonColor) {
        super(new BorderLayout());
        this.chosenOtherButtonColor = Color.darkGray;
        this.firstDayOfWeek = 1;
        this.yearSelectionRange = 20;
        this.dateFont = new Font("SansSerif", 0, 10);
        this.monthSelector = null;
        this.yearSelector = null;
        this.todayButton = null;
        this.buttons = null;
        this.refreshing = false;
        this.date = date;
        this.dateButtonColor = dateButtonColor;
        this.monthButtonColor = monthButtonColor;
        this.add(this.constructSelectionPanel(), "North");
        this.add(this.getCalendarPanel(), "Center");
        if (controlPanel) {
            this.add(this.constructControlPanel(), "South");
        }
    }
    
    public void actionPerformed(final ActionEvent e) {
        if (e.getActionCommand().equals("monthSelectionChanged")) {
            final JComboBox c = (JComboBox)e.getSource();
            this.date = SerialDate.createInstance(this.date.getDayOfMonth(), c.getSelectedIndex() + 1, this.date.getYYYY());
            this.refreshButtons();
        }
        else if (e.getActionCommand().equals("yearSelectionChanged")) {
            if (!this.refreshing) {
                final JComboBox c = (JComboBox)e.getSource();
                final Integer y = (Integer)c.getSelectedItem();
                this.date = SerialDate.createInstance(this.date.getDayOfMonth(), this.date.getMonth(), y);
                this.refreshYearSelector();
                this.refreshButtons();
            }
        }
        else if (e.getActionCommand().equals("todayButtonClicked")) {
            this.setDate(SerialDate.createInstance(new Date()));
        }
        else if (e.getActionCommand().equals("dateButtonClicked")) {
            final JButton b = (JButton)e.getSource();
            final int i = Integer.parseInt(b.getName());
            final SerialDate first = this.getFirstVisibleDate();
            final SerialDate selected = SerialDate.addDays(i, first);
            this.setDate(selected);
        }
    }
    
    private JPanel constructControlPanel() {
        final JPanel p = new JPanel();
        p.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        (this.todayButton = new JButton("Today")).addActionListener(this);
        this.todayButton.setActionCommand("todayButtonClicked");
        p.add(this.todayButton);
        return p;
    }
    
    private JPanel constructSelectionPanel() {
        final JPanel p = new JPanel();
        (this.monthSelector = new JComboBox((E[])SerialDate.getMonths())).addActionListener(this);
        this.monthSelector.setActionCommand("monthSelectionChanged");
        p.add(this.monthSelector);
        (this.yearSelector = new JComboBox(this.getYears(0))).addActionListener(this);
        this.yearSelector.setActionCommand("yearSelectionChanged");
        p.add(this.yearSelector);
        return p;
    }
    
    protected Color getButtonColor(final SerialDate targetDate) {
        if (this.date.equals(this.date)) {
            return this.dateButtonColor;
        }
        if (targetDate.getMonth() == this.date.getMonth()) {
            return this.monthButtonColor;
        }
        return this.chosenOtherButtonColor;
    }
    
    private JPanel getCalendarPanel() {
        final JPanel panel = new JPanel(new GridLayout(7, 7));
        panel.add(new JLabel("Sun", 0));
        panel.add(new JLabel("Mon", 0));
        panel.add(new JLabel("Tue", 0));
        panel.add(new JLabel("Wed", 0));
        panel.add(new JLabel("Thu", 0));
        panel.add(new JLabel("Fri", 0));
        panel.add(new JLabel("Sat", 0));
        this.buttons = new JButton[42];
        for (int i = 0; i < 42; ++i) {
            final JButton button = new JButton("");
            button.setMargin(new Insets(1, 1, 1, 1));
            button.setName(Integer.toString(i));
            button.setFont(this.dateFont);
            button.setFocusPainted(false);
            button.setActionCommand("dateButtonClicked");
            button.addActionListener(this);
            panel.add(this.buttons[i] = button);
        }
        return panel;
    }
    
    public SerialDate getDate() {
        return this.date;
    }
    
    private int getFirstDayOfWeek() {
        return this.firstDayOfWeek;
    }
    
    protected SerialDate getFirstVisibleDate() {
        SerialDate result;
        for (result = SerialDate.createInstance(1, this.date.getMonth(), this.date.getYYYY()), result = SerialDate.addDays(-1, result); result.getDayOfWeek() != this.getFirstDayOfWeek(); result = SerialDate.addDays(-1, result)) {}
        return result;
    }
    
    private Vector getYears(final int chosenYear) {
        final Vector v = new Vector();
        for (int i = chosenYear - this.yearSelectionRange; i <= chosenYear + this.yearSelectionRange; ++i) {
            v.addElement(new Integer(i));
        }
        return v;
    }
    
    protected void refreshButtons() {
        SerialDate current = this.getFirstVisibleDate();
        for (int i = 0; i < 42; ++i) {
            final JButton button = this.buttons[i];
            button.setText(String.valueOf(current.getDayOfWeek()));
            button.setBackground(this.getButtonColor(current));
            current = SerialDate.addDays(1, current);
        }
    }
    
    private void refreshYearSelector() {
        if (!this.refreshing) {
            this.refreshing = true;
            this.yearSelector.removeAllItems();
            final Vector v = this.getYears(this.date.getYYYY());
            final Enumeration e = v.elements();
            while (e.hasMoreElements()) {
                this.yearSelector.addItem(e.nextElement());
            }
            this.yearSelector.setSelectedItem(new Integer(this.date.getYYYY()));
            this.refreshing = false;
        }
    }
    
    public void setDate(final SerialDate date) {
        this.date = date;
        this.monthSelector.setSelectedIndex(date.getMonth() - 1);
        this.refreshYearSelector();
        this.refreshButtons();
    }
}
