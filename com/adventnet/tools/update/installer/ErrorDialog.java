package com.adventnet.tools.update.installer;

import javax.swing.JFrame;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.Component;
import java.awt.Frame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JDialog;

public class ErrorDialog extends JDialog
{
    private JScrollPane jScrollPane1;
    private JTextArea jTextArea1;
    
    public ErrorDialog(final Frame parent, final boolean modal) {
        super(parent, modal);
        this.initComponents();
    }
    
    private void initComponents() {
        this.jScrollPane1 = new JScrollPane();
        this.jTextArea1 = new JTextArea();
        this.setDefaultCloseOperation(2);
        this.setLocationRelativeTo(this);
        this.setResizable(false);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(final KeyEvent evt) {
                ErrorDialog.this.formKeyTyped(evt);
            }
            
            @Override
            public void keyPressed(final KeyEvent evt) {
                ErrorDialog.this.formKeyPressed(evt);
            }
            
            @Override
            public void keyReleased(final KeyEvent evt) {
                ErrorDialog.this.formKeyReleased(evt);
            }
        });
        this.jTextArea1.setEditable(false);
        this.jScrollPane1.setViewportView(this.jTextArea1);
        this.setTitle("Update Manager Error Dialog");
        this.setSize(550, 400);
        this.getContentPane().add(this.jScrollPane1, "Center");
    }
    
    private void formKeyReleased(final KeyEvent evt) {
        System.out.println("formKeyReleased");
        if (evt.getKeyCode() == 27) {
            this.dispose();
        }
    }
    
    private void formKeyPressed(final KeyEvent evt) {
        System.out.println("formKeyPressed");
        if (evt.getKeyCode() == 27) {
            this.dispose();
        }
    }
    
    private void formKeyTyped(final KeyEvent evt) {
        System.out.println("formKeyTyped");
        if (evt.getKeyCode() == 27) {
            this.dispose();
        }
    }
    
    public static void main(final String[] args) {
        new ErrorDialog(new JFrame(), true).show();
    }
    
    public void add(final String s) {
        this.jTextArea1.setEditable(false);
        this.jTextArea1.append(s + "\n");
    }
}
