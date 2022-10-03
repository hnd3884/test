package java.awt;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleContext;
import javax.accessibility.Accessible;

public class Panel extends Container implements Accessible
{
    private static final String base = "panel";
    private static int nameCounter;
    private static final long serialVersionUID = -2728009084054400034L;
    
    public Panel() {
        this(new FlowLayout());
    }
    
    public Panel(final LayoutManager layout) {
        this.setLayout(layout);
    }
    
    @Override
    String constructComponentName() {
        synchronized (Panel.class) {
            return "panel" + Panel.nameCounter++;
        }
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.peer == null) {
                this.peer = this.getToolkit().createPanel(this);
            }
            super.addNotify();
        }
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleAWTPanel();
        }
        return this.accessibleContext;
    }
    
    static {
        Panel.nameCounter = 0;
    }
    
    protected class AccessibleAWTPanel extends AccessibleAWTContainer
    {
        private static final long serialVersionUID = -6409552226660031050L;
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PANEL;
        }
    }
}
