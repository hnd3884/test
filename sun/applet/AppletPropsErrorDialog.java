package sun.applet;

import java.awt.Event;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Button;
import java.awt.Component;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Frame;
import java.awt.Dialog;

class AppletPropsErrorDialog extends Dialog
{
    public AppletPropsErrorDialog(final Frame frame, final String s, final String s2, final String s3) {
        super(frame, s, true);
        final Panel panel = new Panel();
        this.add("Center", new Label(s2));
        panel.add(new Button(s3));
        this.add("South", panel);
        this.pack();
        final Dimension size = this.size();
        final Rectangle bounds = frame.bounds();
        this.move(bounds.x + (bounds.width - size.width) / 2, bounds.y + (bounds.height - size.height) / 2);
    }
    
    @Override
    public boolean action(final Event event, final Object o) {
        this.hide();
        this.dispose();
        return true;
    }
}
