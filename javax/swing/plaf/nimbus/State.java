package javax.swing.plaf.nimbus;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;

public abstract class State<T extends JComponent>
{
    static final Map<String, StandardState> standardStates;
    static final State Enabled;
    static final State MouseOver;
    static final State Pressed;
    static final State Disabled;
    static final State Focused;
    static final State Selected;
    static final State Default;
    private String name;
    
    protected State(final String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    boolean isInState(final T t, final int n) {
        return this.isInState(t);
    }
    
    protected abstract boolean isInState(final T p0);
    
    String getName() {
        return this.name;
    }
    
    static boolean isStandardStateName(final String s) {
        return State.standardStates.containsKey(s);
    }
    
    static StandardState getStandardState(final String s) {
        return State.standardStates.get(s);
    }
    
    static {
        standardStates = new HashMap<String, StandardState>(7);
        Enabled = new StandardState(1);
        MouseOver = new StandardState(2);
        Pressed = new StandardState(4);
        Disabled = new StandardState(8);
        Focused = new StandardState(256);
        Selected = new StandardState(512);
        Default = new StandardState(1024);
    }
    
    static final class StandardState extends State<JComponent>
    {
        private int state;
        
        private StandardState(final int state) {
            super(toString(state));
            this.state = state;
            StandardState.standardStates.put(this.getName(), this);
        }
        
        public int getState() {
            return this.state;
        }
        
        @Override
        boolean isInState(final JComponent component, final int n) {
            return (n & this.state) == this.state;
        }
        
        @Override
        protected boolean isInState(final JComponent component) {
            throw new AssertionError((Object)"This method should never be called");
        }
        
        private static String toString(final int n) {
            final StringBuffer sb = new StringBuffer();
            if ((n & 0x400) == 0x400) {
                sb.append("Default");
            }
            if ((n & 0x8) == 0x8) {
                if (sb.length() > 0) {
                    sb.append("+");
                }
                sb.append("Disabled");
            }
            if ((n & 0x1) == 0x1) {
                if (sb.length() > 0) {
                    sb.append("+");
                }
                sb.append("Enabled");
            }
            if ((n & 0x100) == 0x100) {
                if (sb.length() > 0) {
                    sb.append("+");
                }
                sb.append("Focused");
            }
            if ((n & 0x2) == 0x2) {
                if (sb.length() > 0) {
                    sb.append("+");
                }
                sb.append("MouseOver");
            }
            if ((n & 0x4) == 0x4) {
                if (sb.length() > 0) {
                    sb.append("+");
                }
                sb.append("Pressed");
            }
            if ((n & 0x200) == 0x200) {
                if (sb.length() > 0) {
                    sb.append("+");
                }
                sb.append("Selected");
            }
            return sb.toString();
        }
    }
}
