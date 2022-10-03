package com.adventnet.beans.ipaddressfield;

import javax.swing.text.BadLocationException;
import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.util.StringTokenizer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.text.Document;
import javax.swing.JTextField;
import java.awt.LayoutManager;
import java.awt.GridBagLayout;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import javax.swing.JPanel;

public class IpAddressComponent extends JPanel implements FocusListener, ActionListener, KeyListener
{
    private boolean tab;
    private int y;
    public String validValues;
    private int length;
    private boolean inst;
    private GridBagConstraints cons;
    private Insets inset;
    private int maxValue;
    private EventListenerList eL;
    private EventListenerList kL;
    private Vector textVec;
    private Vector labelVec;
    private String separator;
    private boolean ipV6;
    
    public IpAddressComponent() {
        this.tab = false;
        this.y = 0;
        this.validValues = "0123456789*.";
        this.length = 0;
        this.inst = false;
        this.cons = new GridBagConstraints();
        this.maxValue = 255;
        this.eL = new EventListenerList();
        this.kL = new EventListenerList();
        this.textVec = new Vector();
        this.labelVec = new Vector();
        this.setSeparator(".");
        this.setLayout(new GridBagLayout());
        this.inset = new Insets(0, 0, 0, 0);
        this.setIpV6(false);
        this.setSize(225, 52);
        this.setVisible(true);
    }
    
    private void clearFields() {
        this.removeAll();
    }
    
    public boolean isIpV6() {
        return this.ipV6;
    }
    
    public void setIpV6(final boolean ipV6) {
        this.ipV6 = ipV6;
        if (this.ipV6) {
            this.clearFields();
            this.setNumberOfFields(6);
        }
        else {
            this.clearFields();
            this.setNumberOfFields(4);
        }
    }
    
    private void setNumberOfFields(final int n) {
        int n2 = 0;
        if (this.textVec.size() > 0) {
            this.textVec.clear();
            this.labelVec.clear();
        }
        for (int i = 0; i < n - 1; ++i) {
            this.textVec.add(new JTextField(new IpTextDocument(), "0", 3));
            this.textVec.lastElement().addFocusListener(this);
            this.textVec.lastElement().addActionListener(this);
            this.textVec.lastElement().addKeyListener(this);
            final int n3 = n2;
            final int n4 = 0;
            final int n5 = 1;
            final int n6 = 1;
            final double n7 = 1.0;
            final double n8 = 1.0;
            final GridBagConstraints cons = this.cons;
            final int n9 = 10;
            final GridBagConstraints cons2 = this.cons;
            this.setConstraints(n3, n4, n5, n6, n7, n8, n9, 1, this.inset, 0, 0);
            ++n2;
            this.add((Component)this.textVec.elementAt(i), this.cons);
            final int n10 = n2;
            final int n11 = 0;
            final int n12 = 1;
            final int n13 = 1;
            final double n14 = 0.0;
            final double n15 = 0.0;
            final GridBagConstraints cons3 = this.cons;
            final int n16 = 10;
            final GridBagConstraints cons4 = this.cons;
            this.setConstraints(n10, n11, n12, n13, n14, n15, n16, 0, this.inset, 0, 0);
            ++n2;
            this.labelVec.add(new JLabel("."));
            this.add((Component)this.labelVec.elementAt(i), this.cons);
            ((JLabel)this.labelVec.elementAt(i)).setText(this.getSeparator());
        }
        this.textVec.add(new JTextField(new IpTextDocument(), "0", 3));
        final int n17 = n2;
        final int n18 = 0;
        final int n19 = 1;
        final int n20 = 1;
        final double n21 = 1.0;
        final double n22 = 1.0;
        final GridBagConstraints cons5 = this.cons;
        final int n23 = 10;
        final GridBagConstraints cons6 = this.cons;
        this.setConstraints(n17, n18, n19, n20, n21, n22, n23, 1, this.inset, 0, 0);
        this.add(this.textVec.elementAt(this.textVec.size() - 1), this.cons);
        this.textVec.lastElement().addFocusListener(this);
        this.textVec.lastElement().addActionListener(this);
        this.textVec.lastElement().addKeyListener(this);
        this.textVec.firstElement().selectAll();
        this.textVec.firstElement().requestFocus();
        this.setOpaque(true);
        this.setBackground(new Color(-1));
        this.setBorderToAllComponents(new LineBorder(new Color(-1), 0));
        this.setTextHorizontalAlignment(0);
        this.updateUI();
    }
    
    public void setConstraints(final int gridx, final int gridy, final int gridwidth, final int gridheight, final double weightx, final double weighty, final int anchor, final int fill, final Insets insets, final int ipadx, final int ipady) {
        this.cons.gridx = gridx;
        this.cons.gridy = gridy;
        this.cons.gridwidth = gridwidth;
        this.cons.gridheight = gridheight;
        this.cons.weightx = weightx;
        this.cons.weighty = weighty;
        this.cons.anchor = anchor;
        this.cons.fill = fill;
        this.cons.insets = insets;
        this.cons.ipadx = ipadx;
        this.cons.ipady = ipady;
    }
    
    public void setOpaque(final boolean b) {
        super.setOpaque(b);
        if (this.textVec != null && this.textVec.firstElement() != null) {
            for (int i = 0; i < this.textVec.size(); ++i) {
                ((JTextField)this.textVec.elementAt(i)).setOpaque(b);
            }
        }
    }
    
    public boolean isOpaque() {
        return super.isOpaque();
    }
    
    public void setForeground(final Color foreground) {
        super.setForeground(foreground);
        if (this.textVec != null && this.textVec.firstElement() != null) {
            for (int i = 0; i < this.textVec.size(); ++i) {
                ((JTextField)this.textVec.elementAt(i)).setForeground(foreground);
            }
        }
        if (this.labelVec != null && this.labelVec.firstElement() != null) {
            for (int j = 0; j < this.labelVec.size(); ++j) {
                ((JLabel)this.labelVec.elementAt(j)).setForeground(foreground);
            }
        }
    }
    
    public Color getForeground() {
        return super.getForeground();
    }
    
    public void setBackground(final Color background) {
        super.setBackground(background);
        if (this.textVec != null && this.textVec.firstElement() != null) {
            for (int i = 0; i < this.textVec.size(); ++i) {
                ((JTextField)this.textVec.elementAt(i)).setBackground(background);
            }
        }
        if (this.labelVec != null && this.labelVec.firstElement() != null) {
            for (int j = 0; j < this.labelVec.size(); ++j) {
                ((JLabel)this.labelVec.elementAt(j)).setBackground(background);
            }
        }
    }
    
    public Color getBackground() {
        return super.getBackground();
    }
    
    private void setBorderToAllComponents(final Border border) {
        this.setBorder(border);
        if (this.textVec.firstElement() != null) {
            for (int i = 0; i < this.textVec.size(); ++i) {
                ((JTextField)this.textVec.elementAt(i)).setBorder(border);
            }
        }
        if (this.labelVec != null && this.labelVec.firstElement() != null) {
            for (int j = 0; j < this.labelVec.size(); ++j) {
                ((JLabel)this.labelVec.elementAt(j)).setBorder(border);
            }
        }
    }
    
    public void setTextHorizontalAlignment(final int horizontalAlignment) {
        if (this.textVec.firstElement() != null) {
            for (int i = 0; i < this.textVec.size(); ++i) {
                ((JTextField)this.textVec.elementAt(i)).setHorizontalAlignment(horizontalAlignment);
            }
        }
    }
    
    public int getTextHorizontalAlignment() {
        return this.textVec.firstElement().getHorizontalAlignment();
    }
    
    private void setSeparator(final String s) {
        this.separator = s;
        for (int i = 0; i < this.labelVec.size(); ++i) {
            ((JLabel)this.labelVec.elementAt(i)).setText(s);
        }
    }
    
    private String getSeparator() {
        return this.separator;
    }
    
    private JTextField getIpAddressField(final int n) {
        return this.textVec.elementAt(n);
    }
    
    public void setEditable(final boolean editable) {
        for (int i = 0; i < this.textVec.size(); ++i) {
            ((JTextField)this.textVec.elementAt(i)).setEditable(editable);
        }
    }
    
    public void setEnabled(final boolean enabled) {
        for (int i = 0; i < this.textVec.size(); ++i) {
            ((JTextField)this.textVec.elementAt(i)).setEnabled(enabled);
        }
    }
    
    public boolean isEnabled() {
        return this.textVec.firstElement().isEnabled();
    }
    
    public boolean isEditable() {
        return this.textVec.firstElement().isEditable();
    }
    
    public String getText() {
        if (this.noEmptyString()) {
            String string = "";
            for (int i = 0; i < this.textVec.size() - 1; ++i) {
                string = string + ((JTextField)this.textVec.elementAt(i)).getText() + this.getSeparator();
            }
            return string + this.textVec.lastElement().getText();
        }
        return "";
    }
    
    public void setText(final String s) {
        if (s == null || s.equals("")) {
            return;
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(s, this.getSeparator());
        final int[] value = new int[this.textVec.size()];
        if (stringTokenizer.countTokens() == this.textVec.size()) {
            try {
                for (int i = 0; i < this.textVec.size(); ++i) {
                    value[i] = Integer.parseInt(this.checkSpecialCharacter((String)stringTokenizer.nextElement()));
                }
                this.setValue(value);
            }
            catch (final NumberFormatException ex) {
                System.err.println("Unable to parse the specified string " + s);
            }
            this.updateUI();
            return;
        }
        System.out.println(" The Number of Fields is not the same value as Number of given Values ");
        System.err.println("Specified string " + s + " is not valid.");
    }
    
    public int[] getValue() {
        final int[] array = new int[this.textVec.size()];
        if (this.noEmptyString()) {
            for (int i = 0; i < this.textVec.size(); ++i) {
                array[i] = new Integer(this.checkSpecialCharacter(((JTextField)this.textVec.get(i)).getText()));
            }
        }
        return array;
    }
    
    private String checkSpecialCharacter(final String s) {
        if (s.indexOf("*") > 0) {
            return "-1";
        }
        if ("*".equals(s)) {
            return "-1";
        }
        return s;
    }
    
    public void setValue(final int[] array) {
        if (array == null) {
            System.err.println("Specified value is not valid.");
            return;
        }
        if (array.length != this.textVec.size()) {
            return;
        }
        for (int i = 0; i < this.textVec.size(); ++i) {
            this.setValueFor((JTextField)this.textVec.get(i), array[i]);
        }
    }
    
    private void setValueFor(final JTextField textField, final int n) {
        if (n == -1) {
            textField.setText("*");
        }
        else {
            textField.setText("" + n);
        }
    }
    
    private boolean noEmptyString() {
        for (int i = 0; i < this.textVec.size(); ++i) {
            if (((JTextField)this.textVec.get(i)).getText().trim().equals("")) {
                return false;
            }
        }
        return true;
    }
    
    public void focusGained(final FocusEvent focusEvent) {
        ((JTextField)focusEvent.getSource()).selectAll();
    }
    
    public void focusLost(final FocusEvent focusEvent) {
    }
    
    private int getMaxValue() {
        return this.maxValue;
    }
    
    private void setMaxValue(final int maxValue) {
        this.maxValue = maxValue;
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
        actionEvent.getSource();
        this.fireActionEvents(new ActionEvent(this, 0, null));
    }
    
    public void keyTyped(final KeyEvent keyEvent) {
        keyEvent.getSource();
        this.fireTypedKeyEvent(new KeyEvent(this, 0, 0L, keyEvent.getKeyCode(), keyEvent.getKeyChar()));
    }
    
    public void keyPressed(final KeyEvent keyEvent) {
        keyEvent.getSource();
        this.fireTypedKeyEvent(new KeyEvent(this, 0, 0L, keyEvent.getKeyCode(), keyEvent.getKeyChar()));
    }
    
    public void keyReleased(final KeyEvent keyEvent) {
        keyEvent.getSource();
        this.fireTypedKeyEvent(new KeyEvent(this, 0, 0L, keyEvent.getKeyCode(), keyEvent.getKeyChar()));
    }
    
    private void fireActionEvents(final ActionEvent actionEvent) {
        final Object[] listenerList = this.eL.getListenerList();
        if (listenerList != null) {
            for (int i = 0; i < listenerList.length; i += 2) {
                ((ActionListener)listenerList[i + 1]).actionPerformed(actionEvent);
            }
        }
    }
    
    public void fireTypedKeyEvent(final KeyEvent keyEvent) {
        final Object[] listenerList = this.kL.getListenerList();
        if (listenerList != null) {
            for (int i = 0; i < listenerList.length; i += 2) {
                ((KeyListener)listenerList[i + 1]).keyTyped(keyEvent);
            }
        }
    }
    
    public void addActionListener(final ActionListener actionListener) {
        this.eL.add(ActionListener.class, actionListener);
    }
    
    public void removeActionListener(final ActionListener actionListener) {
        this.eL.remove(ActionListener.class, actionListener);
    }
    
    public void addKeyListener(final KeyListener keyListener) {
        this.kL.add(KeyListener.class, keyListener);
    }
    
    public void removeKeyListener(final KeyListener keyListener) {
        this.kL.remove(KeyListener.class, keyListener);
    }
    
    class IpTextDocument extends PlainDocument
    {
        public IpTextDocument() {
        }
        
        public void insertString(final int n, final String s, final AttributeSet set) throws BadLocationException {
            if (s == null) {
                return;
            }
            for (int i = 0; i < s.length(); ++i) {
                if (IpAddressComponent.this.validValues.indexOf(String.valueOf(s.charAt(i))) == -1) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }
            IpAddressComponent.this.length = this.getLength();
            if (IpAddressComponent.this.length > String.valueOf(IpAddressComponent.this.getMaxValue()).length() - 1) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            this.getText(0, this.getLength());
            String string;
            if (IpAddressComponent.this.length == 0) {
                string = s;
            }
            else {
                final StringBuffer sb = new StringBuffer(this.getText(0, this.getLength()));
                sb.insert(n, s);
                string = sb.toString();
            }
            for (int j = 0; j < string.length(); ++j) {
                if (string.charAt(j) == '*') {
                    super.remove(0, this.getLength());
                    super.insertString(0, "*", set);
                    this.setNextFocus(this);
                    return;
                }
                if (string.charAt(j) == '.') {
                    this.setNextFocus(this);
                    return;
                }
            }
            if (Integer.parseInt(string) > IpAddressComponent.this.getMaxValue()) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            if (IpAddressComponent.this.length == String.valueOf(IpAddressComponent.this.getMaxValue()).length() - 1) {
                IpAddressComponent.this.tab = true;
            }
            if (IpAddressComponent.this.tab) {
                this.setNextFocus(this);
                IpAddressComponent.this.tab = false;
            }
            super.insertString(n, s, set);
        }
        
        private void setNextFocus(final IpTextDocument ipTextDocument) {
            for (int i = 0; i < IpAddressComponent.this.textVec.size() - 1; ++i) {
                if (((JTextField)IpAddressComponent.this.textVec.elementAt(i)).getDocument() == this) {
                    ((JTextField)IpAddressComponent.this.textVec.elementAt(i + 1)).requestFocus();
                    return;
                }
            }
        }
        
        public void remove(final int n, final int n2) throws BadLocationException {
            --IpAddressComponent.this.length;
            super.remove(n, n2);
        }
    }
}
