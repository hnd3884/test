package com.sun.activation.viewers;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.Container;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import javax.activation.DataHandler;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;
import java.awt.Button;
import java.awt.GridBagLayout;
import java.awt.TextArea;
import java.awt.event.ActionListener;
import javax.activation.CommandObject;
import java.awt.Panel;

public class TextEditor extends Panel implements CommandObject, ActionListener
{
    private TextArea text_area;
    private GridBagLayout panel_gb;
    private Panel button_panel;
    private Button save_button;
    private File text_file;
    private String text_buffer;
    private InputStream data_ins;
    private FileInputStream fis;
    private DataHandler _dh;
    private boolean DEBUG;
    
    public TextEditor() {
        this.text_area = null;
        this.panel_gb = null;
        this.button_panel = null;
        this.save_button = null;
        this.text_file = null;
        this.text_buffer = null;
        this.data_ins = null;
        this.fis = null;
        this._dh = null;
        this.DEBUG = false;
        this.setLayout(this.panel_gb = new GridBagLayout());
        (this.button_panel = new Panel()).setLayout(new FlowLayout());
        this.save_button = new Button("SAVE");
        this.button_panel.add(this.save_button);
        this.addGridComponent(this, this.button_panel, this.panel_gb, 0, 0, 1, 1, 1, 0);
        (this.text_area = new TextArea("This is text", 24, 80, 1)).setEditable(true);
        this.addGridComponent(this, this.text_area, this.panel_gb, 0, 1, 1, 2, 1, 1);
        this.save_button.addActionListener(this);
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.save_button) {
            this.performSaveOperation();
        }
    }
    
    private void addGridComponent(final Container container, final Component component, final GridBagLayout gridBagLayout, final int gridx, final int gridy, final int gridwidth, final int gridheight, final int n, final int n2) {
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = gridx;
        gridBagConstraints.gridy = gridy;
        gridBagConstraints.gridwidth = gridwidth;
        gridBagConstraints.gridheight = gridheight;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weighty = n2;
        gridBagConstraints.weightx = n;
        gridBagConstraints.anchor = 10;
        gridBagLayout.setConstraints(component, gridBagConstraints);
        container.add(component);
    }
    
    public void addNotify() {
        super.addNotify();
        this.invalidate();
    }
    
    public Dimension getPreferredSize() {
        return this.text_area.getMinimumSize(24, 80);
    }
    
    private void performSaveOperation() {
        OutputStream outputStream = null;
        try {
            outputStream = this._dh.getOutputStream();
        }
        catch (final Exception ex) {}
        final String text = this.text_area.getText();
        if (outputStream == null) {
            System.out.println("Invalid outputstream in TextEditor!");
            System.out.println("not saving!");
        }
        try {
            outputStream.write(text.getBytes());
            outputStream.flush();
            outputStream.close();
        }
        catch (final IOException ex2) {
            System.out.println("TextEditor Save Operation failed with: " + ex2);
        }
    }
    
    public void setCommandContext(final String s, final DataHandler dh) throws IOException {
        this._dh = dh;
        this.setInputStream(this._dh.getInputStream());
    }
    
    public void setInputStream(final InputStream inputStream) throws IOException {
        final byte[] array = new byte[1024];
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int read;
        while ((read = inputStream.read(array)) > 0) {
            byteArrayOutputStream.write(array, 0, read);
        }
        inputStream.close();
        this.text_buffer = byteArrayOutputStream.toString();
        this.text_area.setText(this.text_buffer);
    }
}
