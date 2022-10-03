package sun.applet;

import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionListener;
import java.awt.Button;
import java.awt.Panel;
import java.awt.Component;
import java.awt.TextArea;
import java.awt.Frame;

final class TextFrame extends Frame
{
    private static AppletMessageHandler amh;
    
    TextFrame(final int n, final int n2, final String title, final String text) {
        this.setTitle(title);
        final TextArea textArea = new TextArea(20, 60);
        textArea.setText(text);
        textArea.setEditable(false);
        this.add("Center", textArea);
        final Panel panel = new Panel();
        this.add("South", panel);
        final Button button = new Button(TextFrame.amh.getMessage("button.dismiss", "Dismiss"));
        panel.add(button);
        button.addActionListener(new ActionEventListener());
        this.pack();
        this.move(n, n2);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                TextFrame.this.dispose();
            }
        });
    }
    
    static {
        TextFrame.amh = new AppletMessageHandler("textframe");
    }
}
