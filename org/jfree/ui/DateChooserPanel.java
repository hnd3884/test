package org.jfree.ui;

import java.awt.Insets;
import javax.swing.JLabel;
import java.text.DateFormatSymbols;
import java.awt.GridLayout;
import org.jfree.date.SerialDate;
import javax.swing.BorderFactory;
import java.util.Date;
import java.awt.event.ActionEvent;
import java.awt.Component;
import javax.swing.UIManager;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.Font;
import java.awt.Color;
import java.util.Calendar;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

public class DateChooserPanel extends JPanel implements ActionListener
{
    private Calendar chosenDate;
    private Color chosenDateButtonColor;
    private Color chosenMonthButtonColor;
    private Color chosenOtherButtonColor;
    private int firstDayOfWeek;
    private int yearSelectionRange;
    private Font dateFont;
    private JComboBox monthSelector;
    private JComboBox yearSelector;
    private JButton todayButton;
    private JButton[] buttons;
    private boolean refreshing;
    private int[] WEEK_DAYS;
    
    public DateChooserPanel() {
        this(Calendar.getInstance(), false);
    }
    
    public DateChooserPanel(final Calendar calendar, final boolean controlPanel) {
        super(new BorderLayout());
        this.yearSelectionRange = 20;
        this.dateFont = new Font("SansSerif", 0, 10);
        this.refreshing = false;
        this.chosenDateButtonColor = UIManager.getColor("textHighlight");
        this.chosenMonthButtonColor = UIManager.getColor("control");
        this.chosenOtherButtonColor = UIManager.getColor("controlShadow");
        this.chosenDate = calendar;
        this.firstDayOfWeek = calendar.getFirstDayOfWeek();
        this.WEEK_DAYS = new int[7];
        for (int i = 0; i < 7; ++i) {
            this.WEEK_DAYS[i] = (this.firstDayOfWeek + i - 1) % 7 + 1;
        }
        this.add(this.constructSelectionPanel(), "North");
        this.add(this.getCalendarPanel(), "Center");
        if (controlPanel) {
            this.add(this.constructControlPanel(), "South");
        }
        this.setDate(calendar.getTime());
    }
    
    public void actionPerformed(final ActionEvent e) {
        if (e.getActionCommand().equals("monthSelectionChanged")) {
            final JComboBox c = (JComboBox)e.getSource();
            final int dayOfMonth = this.chosenDate.get(5);
            this.chosenDate.set(5, 1);
            this.chosenDate.set(2, c.getSelectedIndex());
            final int maxDayOfMonth = this.chosenDate.getActualMaximum(5);
            this.chosenDate.set(5, Math.min(dayOfMonth, maxDayOfMonth));
            this.refreshButtons();
        }
        else if (e.getActionCommand().equals("yearSelectionChanged")) {
            if (!this.refreshing) {
                final JComboBox c = (JComboBox)e.getSource();
                final Integer y = (Integer)c.getSelectedItem();
                final int dayOfMonth2 = this.chosenDate.get(5);
                this.chosenDate.set(5, 1);
                this.chosenDate.set(1, y);
                final int maxDayOfMonth2 = this.chosenDate.getActualMaximum(5);
                this.chosenDate.set(5, Math.min(dayOfMonth2, maxDayOfMonth2));
                this.refreshYearSelector();
                this.refreshButtons();
            }
        }
        else if (e.getActionCommand().equals("todayButtonClicked")) {
            this.setDate(new Date());
        }
        else if (e.getActionCommand().equals("dateButtonClicked")) {
            final JButton b = (JButton)e.getSource();
            final int i = Integer.parseInt(b.getName());
            final Calendar cal = this.getFirstVisibleDate();
            cal.add(5, i);
            this.setDate(cal.getTime());
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
        final int minMonth = this.chosenDate.getMinimum(2);
        final int maxMonth = this.chosenDate.getMaximum(2);
        final String[] months = new String[maxMonth - minMonth + 1];
        System.arraycopy(SerialDate.getMonths(), minMonth, months, 0, months.length);
        (this.monthSelector = new JComboBox((E[])months)).addActionListener(this);
        this.monthSelector.setActionCommand("monthSelectionChanged");
        p.add(this.monthSelector);
        (this.yearSelector = new JComboBox((E[])this.getYears(0))).addActionListener(this);
        this.yearSelector.setActionCommand("yearSelectionChanged");
        p.add(this.yearSelector);
        return p;
    }
    
    private boolean equalDates(final Calendar c1, final Calendar c2) {
        return c1.get(5) == c2.get(5) && c1.get(2) == c2.get(2) && c1.get(1) == c2.get(1);
    }
    
    private Color getButtonColor(final Calendar theDate) {
        if (this.equalDates(theDate, this.chosenDate)) {
            return this.chosenDateButtonColor;
        }
        if (theDate.get(2) == this.chosenDate.get(2)) {
            return this.chosenMonthButtonColor;
        }
        return this.chosenOtherButtonColor;
    }
    
    private JPanel getCalendarPanel() {
        final JPanel p = new JPanel(new GridLayout(7, 7));
        final DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        final String[] weekDays = dateFormatSymbols.getShortWeekdays();
        for (int i = 0; i < this.WEEK_DAYS.length; ++i) {
            p.add(new JLabel(weekDays[this.WEEK_DAYS[i]], 0));
        }
        this.buttons = new JButton[42];
        for (int j = 0; j < 42; ++j) {
            final JButton b = new JButton("");
            b.setMargin(new Insets(1, 1, 1, 1));
            b.setName(Integer.toString(j));
            b.setFont(this.dateFont);
            b.setFocusPainted(false);
            b.setActionCommand("dateButtonClicked");
            b.addActionListener(this);
            p.add(this.buttons[j] = b);
        }
        return p;
    }
    
    public Color getChosenDateButtonColor() {
        return this.chosenDateButtonColor;
    }
    
    public Color getChosenMonthButtonColor() {
        return this.chosenMonthButtonColor;
    }
    
    public Color getChosenOtherButtonColor() {
        return this.chosenOtherButtonColor;
    }
    
    public Date getDate() {
        return this.chosenDate.getTime();
    }
    
    private int getFirstDayOfWeek() {
        return this.firstDayOfWeek;
    }
    
    private Calendar getFirstVisibleDate() {
        final Calendar c = Calendar.getInstance();
        c.set(this.chosenDate.get(1), this.chosenDate.get(2), 1);
        c.add(5, -1);
        while (c.get(7) != this.getFirstDayOfWeek()) {
            c.add(5, -1);
        }
        return c;
    }
    
    public int getYearSelectionRange() {
        return this.yearSelectionRange;
    }
    
    private Integer[] getYears(final int chosenYear) {
        final int size = this.yearSelectionRange * 2 + 1;
        final int start = chosenYear - this.yearSelectionRange;
        final Integer[] years = new Integer[size];
        for (int i = 0; i < size; ++i) {
            years[i] = new Integer(i + start);
        }
        return years;
    }
    
    private void refreshButtons() {
        final Calendar c = this.getFirstVisibleDate();
        for (int i = 0; i < 42; ++i) {
            final JButton b = this.buttons[i];
            b.setText(Integer.toString(c.get(5)));
            b.setBackground(this.getButtonColor(c));
            c.add(5, 1);
        }
    }
    
    private void refreshYearSelector() {
        if (!this.refreshing) {
            this.refreshing = true;
            this.yearSelector.removeAllItems();
            final Integer[] years = this.getYears(this.chosenDate.get(1));
            for (int i = 0; i < years.length; ++i) {
                this.yearSelector.addItem(years[i]);
            }
            this.yearSelector.setSelectedItem(new Integer(this.chosenDate.get(1)));
            this.refreshing = false;
        }
    }
    
    public void setChosenDateButtonColor(final Color chosenDateButtonColor) {
        if (chosenDateButtonColor == null) {
            throw new NullPointerException("UIColor must not be null.");
        }
        final Color oldValue = this.chosenDateButtonColor;
        this.chosenDateButtonColor = chosenDateButtonColor;
        this.refreshButtons();
        this.firePropertyChange("chosenDateButtonColor", oldValue, chosenDateButtonColor);
    }
    
    public void setChosenMonthButtonColor(final Color chosenMonthButtonColor) {
        if (chosenMonthButtonColor == null) {
            throw new NullPointerException("UIColor must not be null.");
        }
        final Color oldValue = this.chosenMonthButtonColor;
        this.chosenMonthButtonColor = chosenMonthButtonColor;
        this.refreshButtons();
        this.firePropertyChange("chosenMonthButtonColor", oldValue, chosenMonthButtonColor);
    }
    
    public void setChosenOtherButtonColor(final Color chosenOtherButtonColor) {
        if (chosenOtherButtonColor == null) {
            throw new NullPointerException("UIColor must not be null.");
        }
        final Color oldValue = this.chosenOtherButtonColor;
        this.chosenOtherButtonColor = chosenOtherButtonColor;
        this.refreshButtons();
        this.firePropertyChange("chosenOtherButtonColor", oldValue, chosenOtherButtonColor);
    }
    
    public void setDate(final Date theDate) {
        this.chosenDate.setTime(theDate);
        this.monthSelector.setSelectedIndex(this.chosenDate.get(2));
        this.refreshYearSelector();
        this.refreshButtons();
    }
    
    public void setYearSelectionRange(final int yearSelectionRange) {
        final int oldYearSelectionRange = this.yearSelectionRange;
        this.yearSelectionRange = yearSelectionRange;
        this.refreshYearSelector();
        this.firePropertyChange("yearSelectionRange", oldYearSelectionRange, yearSelectionRange);
    }
}
