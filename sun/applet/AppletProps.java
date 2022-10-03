package sun.applet;

import java.util.Hashtable;
import java.awt.Event;
import java.security.PrivilegedActionException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.security.PrivilegedExceptionAction;
import java.util.Properties;
import sun.security.action.GetBooleanAction;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.awt.Button;
import java.awt.Component;
import java.awt.Label;
import java.awt.LayoutManager;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Choice;
import java.awt.TextField;
import java.awt.Frame;

class AppletProps extends Frame
{
    TextField proxyHost;
    TextField proxyPort;
    Choice accessMode;
    private static AppletMessageHandler amh;
    
    AppletProps() {
        this.setTitle(AppletProps.amh.getMessage("title"));
        final Panel panel = new Panel();
        panel.setLayout(new GridLayout(0, 2));
        panel.add(new Label(AppletProps.amh.getMessage("label.http.server", "Http proxy server:")));
        panel.add(this.proxyHost = new TextField());
        panel.add(new Label(AppletProps.amh.getMessage("label.http.proxy")));
        panel.add(this.proxyPort = new TextField());
        panel.add(new Label(AppletProps.amh.getMessage("label.class")));
        panel.add(this.accessMode = new Choice());
        this.accessMode.addItem(AppletProps.amh.getMessage("choice.class.item.restricted"));
        this.accessMode.addItem(AppletProps.amh.getMessage("choice.class.item.unrestricted"));
        this.add("Center", panel);
        final Panel panel2 = new Panel();
        panel2.add(new Button(AppletProps.amh.getMessage("button.apply")));
        panel2.add(new Button(AppletProps.amh.getMessage("button.reset")));
        panel2.add(new Button(AppletProps.amh.getMessage("button.cancel")));
        this.add("South", panel2);
        this.move(200, 150);
        this.pack();
        this.reset();
    }
    
    void reset() {
        final AppletSecurity appletSecurity = (AppletSecurity)System.getSecurityManager();
        if (appletSecurity != null) {
            appletSecurity.reset();
        }
        final String text = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("http.proxyHost"));
        final String text2 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("http.proxyPort"));
        if (AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("package.restrict.access.sun"))) {
            this.accessMode.select(AppletProps.amh.getMessage("choice.class.item.restricted"));
        }
        else {
            this.accessMode.select(AppletProps.amh.getMessage("choice.class.item.unrestricted"));
        }
        if (text != null) {
            this.proxyHost.setText(text);
            this.proxyPort.setText(text2);
        }
        else {
            this.proxyHost.setText("");
            this.proxyPort.setText("");
        }
    }
    
    void apply() {
        final String trim = this.proxyHost.getText().trim();
        final String trim2 = this.proxyPort.getText().trim();
        final Properties properties = AccessController.doPrivileged((PrivilegedAction<Properties>)new PrivilegedAction() {
            @Override
            public Object run() {
                return System.getProperties();
            }
        });
        if (trim.length() != 0) {
            int int1 = 0;
            try {
                int1 = Integer.parseInt(trim2);
            }
            catch (final NumberFormatException ex) {}
            if (int1 <= 0) {
                this.proxyPort.selectAll();
                this.proxyPort.requestFocus();
                new AppletPropsErrorDialog(this, AppletProps.amh.getMessage("title.invalidproxy"), AppletProps.amh.getMessage("label.invalidproxy"), AppletProps.amh.getMessage("button.ok")).show();
                return;
            }
            ((Hashtable<String, String>)properties).put("http.proxyHost", trim);
            ((Hashtable<String, String>)properties).put("http.proxyPort", trim2);
        }
        else {
            ((Hashtable<String, String>)properties).put("http.proxyHost", "");
        }
        if (AppletProps.amh.getMessage("choice.class.item.restricted").equals(this.accessMode.getSelectedItem())) {
            ((Hashtable<String, String>)properties).put("package.restrict.access.sun", "true");
        }
        else {
            ((Hashtable<String, String>)properties).put("package.restrict.access.sun", "false");
        }
        try {
            this.reset();
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                @Override
                public Object run() throws IOException {
                    final FileOutputStream fileOutputStream = new FileOutputStream(Main.theUserPropertiesFile);
                    final Properties properties = new Properties();
                    for (int i = 0; i < Main.avDefaultUserProps.length; ++i) {
                        final String s = Main.avDefaultUserProps[i][0];
                        properties.setProperty(s, properties.getProperty(s));
                    }
                    properties.store(fileOutputStream, AppletProps.amh.getMessage("prop.store"));
                    fileOutputStream.close();
                    return null;
                }
            });
            this.hide();
        }
        catch (final PrivilegedActionException ex2) {
            System.out.println(AppletProps.amh.getMessage("apply.exception", ex2.getException()));
            ex2.printStackTrace();
            this.reset();
        }
    }
    
    @Override
    public boolean action(final Event event, final Object o) {
        if (AppletProps.amh.getMessage("button.apply").equals(o)) {
            this.apply();
            return true;
        }
        if (AppletProps.amh.getMessage("button.reset").equals(o)) {
            this.reset();
            return true;
        }
        if (AppletProps.amh.getMessage("button.cancel").equals(o)) {
            this.reset();
            this.hide();
            return true;
        }
        return false;
    }
    
    static {
        AppletProps.amh = new AppletMessageHandler("appletprops");
    }
}
