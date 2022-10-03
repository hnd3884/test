package javax.swing.plaf.basic;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.text.PasswordView;
import javax.swing.text.View;
import javax.swing.text.Element;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class BasicPasswordFieldUI extends BasicTextFieldUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new BasicPasswordFieldUI();
    }
    
    @Override
    protected String getPropertyPrefix() {
        return "PasswordField";
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        final Character c = (Character)UIManager.getDefaults().get(this.getPropertyPrefix() + ".echoChar");
        if (c != null) {
            LookAndFeel.installProperty(this.getComponent(), "echoChar", c);
        }
    }
    
    @Override
    public View create(final Element element) {
        return new PasswordView(element);
    }
    
    @Override
    ActionMap createActionMap() {
        final ActionMap actionMap = super.createActionMap();
        if (actionMap.get("select-word") != null) {
            final Action value = actionMap.get("select-line");
            if (value != null) {
                actionMap.remove("select-word");
                actionMap.put("select-word", value);
            }
        }
        return actionMap;
    }
}
