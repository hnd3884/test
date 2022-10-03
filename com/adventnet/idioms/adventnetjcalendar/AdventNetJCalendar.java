package com.adventnet.idioms.adventnetjcalendar;

import javax.swing.JFrame;
import java.awt.Container;
import java.awt.event.FocusEvent;
import javax.swing.event.AncestorEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.awt.Toolkit;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;
import java.util.Date;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.text.DateFormat;
import java.util.Calendar;
import com.toedter.calendar.JCalendar;
import javax.swing.JWindow;
import java.awt.event.FocusListener;
import javax.swing.event.AncestorListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

public class AdventNetJCalendar extends JPanel implements ActionListener, PropertyChangeListener, MouseListener, AncestorListener, FocusListener
{
    private JWindow calendarWindow;
    private JCalendar jCalendar;
    private Calendar calendar;
    private DateFormat df;
    private JButton calendarViewBtn;
    private JTextField dateField;
    private JPanel mainPanel;
    
    public AdventNetJCalendar() {
        this.initComponents();
        this.dateField.addAncestorListener(this);
        this.dateField.addFocusListener(this);
        this.df = DateFormat.getDateInstance(1);
        final Date time = new Date();
        (this.calendar = Calendar.getInstance()).setTime(time);
        (this.jCalendar = new JCalendar()).setBorder((Border)new SoftBevelBorder(0));
        this.jCalendar.setCalendar(this.calendar);
        this.calendarViewBtn.addActionListener(this);
        this.jCalendar.addPropertyChangeListener((PropertyChangeListener)this);
        this.jCalendar.getDayChooser().addPropertyChangeListener((PropertyChangeListener)this);
        this.dateField.setText(this.df.format(time));
    }
    
    private void initComponents() {
        this.mainPanel = new JPanel();
        this.calendarViewBtn = new JButton();
        this.dateField = new JTextField();
        this.setLayout(new BorderLayout());
        this.setName("");
        this.mainPanel.setLayout(new GridBagLayout());
        this.calendarViewBtn.setText("v");
        this.calendarViewBtn.setActionCommand("show");
        this.calendarViewBtn.setHorizontalTextPosition(2);
        this.calendarViewBtn.setMargin(new Insets(0, 0, 0, 0));
        this.calendarViewBtn.setMinimumSize(new Dimension(20, 20));
        this.calendarViewBtn.setPreferredSize(new Dimension(20, 20));
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = 3;
        gridBagConstraints.anchor = 13;
        this.mainPanel.add(this.calendarViewBtn, gridBagConstraints);
        this.dateField.setEditable(false);
        this.dateField.setMinimumSize(new Dimension(100, 20));
        this.dateField.setPreferredSize(new Dimension(120, 20));
        this.dateField.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                AdventNetJCalendar.this.dateFieldActionPerformed(actionEvent);
            }
        });
        final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.fill = 1;
        gridBagConstraints2.weightx = 0.1;
        gridBagConstraints2.weighty = 0.1;
        this.mainPanel.add(this.dateField, gridBagConstraints2);
        this.add(this.mainPanel, "Center");
    }
    
    private void dateFieldActionPerformed(final ActionEvent actionEvent) {
    }
    
    private void initCalendarWindow(final Window window) {
        if (window != null) {
            window.addMouseListener(this);
        }
        (this.calendarWindow = new JWindow(window)).addMouseListener(this);
        this.calendarWindow.getContentPane().add((Component)this.jCalendar, "Center");
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals("show")) {
            if (this.calendarWindow.isVisible()) {
                this.setCalendarVisible(false);
            }
            else {
                this.setCalendarVisible(true);
            }
        }
    }
    
    private Point getDisplayLocation(final Component component, final Component component2) {
        final Point locationOnScreen = component.getLocationOnScreen();
        final Dimension size = component2.getSize();
        int n = (int)locationOnScreen.getX();
        int n2 = (int)locationOnScreen.getY() + component.getHeight();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (n + size.getWidth() > screenSize.getWidth()) {
            n -= (int)size.getWidth();
        }
        if (n2 + size.getHeight() > screenSize.getHeight()) {
            n2 = n2 - (int)size.getHeight() - (int)component.getSize().getHeight();
        }
        return new Point(n, n2);
    }
    
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        final String propertyName = propertyChangeEvent.getPropertyName();
        if (propertyName.equals("calendar")) {
            this.calendar = (Calendar)propertyChangeEvent.getNewValue();
            this.dateField.setText(this.df.format(this.calendar.getTime()));
        }
        else if (propertyName.equals("day")) {
            this.setCalendarVisible(false);
        }
    }
    
    public void mouseClicked(final MouseEvent mouseEvent) {
    }
    
    public void mouseEntered(final MouseEvent mouseEvent) {
    }
    
    public void mouseExited(final MouseEvent mouseEvent) {
    }
    
    public void mousePressed(final MouseEvent mouseEvent) {
        this.setCalendarVisible(false);
    }
    
    public void mouseReleased(final MouseEvent mouseEvent) {
    }
    
    public void ancestorAdded(final AncestorEvent ancestorEvent) {
        this.initCalendarWindow(this.getParentWindow(ancestorEvent.getComponent()));
    }
    
    public void ancestorMoved(final AncestorEvent ancestorEvent) {
    }
    
    public void ancestorRemoved(final AncestorEvent ancestorEvent) {
    }
    
    public void focusGained(final FocusEvent focusEvent) {
        this.setCalendarVisible(false);
    }
    
    public void focusLost(final FocusEvent focusEvent) {
    }
    
    private Window getParentWindow(final Component component) {
        final Container parent = component.getParent();
        if (parent == null) {
            return null;
        }
        if (parent instanceof Window) {
            return (Window)parent;
        }
        return this.getParentWindow(parent);
    }
    
    private void setCalendarVisible(final boolean b) {
        if (b) {
            this.calendarWindow.pack();
            this.calendarWindow.setLocation(this.getDisplayLocation(this.calendarViewBtn, this.calendarWindow));
            this.calendarWindow.setVisible(true);
        }
        else {
            this.calendarWindow.setVisible(false);
        }
    }
    
    public static void main(final String[] array) {
        final JFrame frame = new JFrame();
        final AdventNetJCalendar adventNetJCalendar = new AdventNetJCalendar();
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(adventNetJCalendar, "North");
        frame.getContentPane().add(panel, "North");
        frame.pack();
        frame.setVisible(true);
    }
    
    public Calendar getCalendar() {
        return this.calendar;
    }
    
    public void setCalendar(final Calendar calendar) {
        this.calendar = calendar;
        if (this.jCalendar != null) {
            this.jCalendar.setCalendar(this.calendar);
            this.dateField.setText(this.df.format(this.calendar.getTime()));
        }
    }
    
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        this.dateField.setEnabled(enabled);
        this.calendarViewBtn.setEnabled(enabled);
    }
}
