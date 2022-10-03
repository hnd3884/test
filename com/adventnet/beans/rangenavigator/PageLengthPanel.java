package com.adventnet.beans.rangenavigator;

import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.EventListener;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.text.Document;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

public class PageLengthPanel extends JPanel implements ActionListener
{
    private JPanel pageLengthPanel;
    private JLabel showLabel;
    private JComboBox pageLength;
    private JTextField pageLengthTextfield;
    private JLabel perPageLabel;
    private GridBagConstraints cons;
    private Insets inset;
    private GridBagLayout gl;
    
    public PageLengthPanel() {
        this.pageLengthPanel = null;
        this.showLabel = null;
        this.pageLength = null;
        this.pageLengthTextfield = null;
        this.perPageLabel = null;
        this.cons = null;
        this.inset = null;
        this.showLabel = new JLabel("Show");
        (this.pageLength = new JComboBox()).setMaximumRowCount(5);
        this.pageLength.setPreferredSize(new Dimension(60, this.pageLength.getPreferredSize().height));
        this.pageLength.setMinimumSize(this.pageLength.getPreferredSize());
        (this.pageLengthTextfield = (JTextField)this.pageLength.getEditor().getEditorComponent()).setDocument(new NumericTextField("0123456789"));
        this.pageLengthTextfield.setHorizontalAlignment(4);
        this.perPageLabel = new JLabel("per page");
        this.setPageLengthUI();
    }
    
    public void setPageLengthUI() {
        this.setLayout(this.gl = new GridBagLayout());
        this.cons = new GridBagConstraints();
        this.inset = new Insets(0, 5, 0, 0);
        this.pageLength.addActionListener(this);
        final int n = 0;
        final int n2 = 0;
        final int n3 = 1;
        final int n4 = 1;
        final double n5 = 0.0;
        final double n6 = 1.0;
        final GridBagConstraints cons = this.cons;
        final int n7 = 10;
        final GridBagConstraints cons2 = this.cons;
        this.setConstraints(n, n2, n3, n4, n5, n6, n7, 0, this.inset, 0, 0);
        this.gl.setConstraints(this.showLabel, this.cons);
        this.add(this.showLabel);
        final int n8 = 1;
        final int n9 = 0;
        final int n10 = 1;
        final int n11 = 1;
        final double n12 = 0.0;
        final double n13 = 0.0;
        final GridBagConstraints cons3 = this.cons;
        final int n14 = 10;
        final GridBagConstraints cons4 = this.cons;
        this.setConstraints(n8, n9, n10, n11, n12, n13, n14, 0, this.inset, 0, 0);
        this.gl.setConstraints(this.pageLength, this.cons);
        this.add(this.pageLength);
        final int n15 = 2;
        final int n16 = 0;
        final int n17 = 1;
        final int n18 = 1;
        final double n19 = 0.1;
        final double n20 = 0.0;
        final GridBagConstraints cons5 = this.cons;
        final int n21 = 17;
        final GridBagConstraints cons6 = this.cons;
        this.setConstraints(n15, n16, n17, n18, n19, n20, n21, 0, this.inset, 0, 0);
        this.gl.setConstraints(this.perPageLabel, this.cons);
        this.add(this.perPageLabel);
    }
    
    private void setConstraints(final int gridx, final int gridy, final int gridwidth, final int gridheight, final double weightx, final double weighty, final int anchor, final int fill, final Insets insets, final int ipadx, final int ipady) {
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
    
    public void setPageLengthText(final String text) {
        this.showLabel.setText(text);
    }
    
    public String getPageLengthText() {
        return this.showLabel.getText();
    }
    
    public void setPerPageText(final String text) {
        this.perPageLabel.setText(text);
    }
    
    public String getPerPageText() {
        return this.perPageLabel.getText();
    }
    
    public void setNumber(final int n) {
        this.pageLengthTextfield.setText(n + "");
    }
    
    public String getNumber() {
        String s;
        if (this.getPageLengthCombo().isEditable()) {
            s = this.pageLengthTextfield.getText().trim();
        }
        else {
            s = this.getPageLengthCombo().getSelectedItem().toString();
        }
        return s;
    }
    
    JComboBox getPageLengthCombo() {
        return this.pageLength;
    }
    
    public void setPageLength(int[] array) {
        array = this.putDefaultValues(array);
        array = this.SelectionSort(array);
        if (array.length != 0) {
            final EventListener[] listeners = this.pageLength.getListeners(ActionListener.class);
            for (int i = 0; i < listeners.length; ++i) {
                this.pageLength.removeActionListener((ActionListener)listeners[i]);
            }
            this.pageLength.removeAllItems();
            for (int j = 0; j < array.length; ++j) {
                if (array[j] != 0) {
                    this.pageLength.addItem(new Integer(array[j]) + "");
                }
            }
            for (int k = 0; k < listeners.length; ++k) {
                this.pageLength.addActionListener((ActionListener)listeners[k]);
            }
        }
    }
    
    public int[] getPageLengthComboItems() {
        final int itemCount = this.getPageLengthCombo().getItemCount();
        final int[] array = new int[itemCount];
        for (int i = 0; i < itemCount; ++i) {
            array[i] = Integer.parseInt(this.getPageLengthCombo().getItemAt(i).toString().trim());
        }
        return array;
    }
    
    private int[] putDefaultValues(int[] array) {
        final int[] array2 = { 10, 15, 25, 50, 100 };
        if (array.length == 0) {
            array = array2;
        }
        else {
            int n = 0;
            for (int i = 0; i < array.length; ++i) {
                if (array[i] != 0) {
                    ++n;
                }
            }
            if (n == 0) {
                array = array2;
            }
        }
        return array;
    }
    
    private int[] removeDuplicates(final int[] array) {
        final Vector vector = new Vector();
        for (int i = 0; i < array.length; ++i) {
            if (!vector.contains(new Integer(array[i]))) {
                vector.add(new Integer(array[i]));
            }
        }
        final Object[] array2 = vector.toArray();
        final int[] array3 = new int[array2.length];
        for (int j = 0; j < array2.length; ++j) {
            array3[j] = (int)array2[j];
        }
        return array3;
    }
    
    public int[] SelectionSort(final int[] array) {
        for (int i = 0; i < array.length; ++i) {
            int n = i;
            for (int j = i + 1; j < array.length; ++j) {
                if (array[j] < array[n]) {
                    n = j;
                }
            }
            final int n2 = array[i];
            array[i] = array[n];
            array[n] = n2;
        }
        return this.removeDuplicates(array);
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
    }
    
    public JLabel getShowLabel() {
        return this.showLabel;
    }
    
    public JLabel getPerpageLabel() {
        return this.perPageLabel;
    }
}
