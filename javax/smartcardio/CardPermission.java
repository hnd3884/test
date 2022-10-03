package javax.smartcardio;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.Permission;

public class CardPermission extends Permission
{
    private static final long serialVersionUID = 7146787880530705613L;
    private static final int A_CONNECT = 1;
    private static final int A_EXCLUSIVE = 2;
    private static final int A_GET_BASIC_CHANNEL = 4;
    private static final int A_OPEN_LOGICAL_CHANNEL = 8;
    private static final int A_RESET = 16;
    private static final int A_TRANSMIT_CONTROL = 32;
    private static final int A_ALL = 63;
    private static final int[] ARRAY_MASKS;
    private static final String S_CONNECT = "connect";
    private static final String S_EXCLUSIVE = "exclusive";
    private static final String S_GET_BASIC_CHANNEL = "getBasicChannel";
    private static final String S_OPEN_LOGICAL_CHANNEL = "openLogicalChannel";
    private static final String S_RESET = "reset";
    private static final String S_TRANSMIT_CONTROL = "transmitControl";
    private static final String S_ALL = "*";
    private static final String[] ARRAY_STRINGS;
    private transient int mask;
    private volatile String actions;
    
    public CardPermission(final String s, final String s2) {
        super(s);
        if (s == null) {
            throw new NullPointerException();
        }
        this.mask = getMask(s2);
    }
    
    private static int getMask(final String s) {
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("actions must not be empty");
        }
        for (int i = 0; i < CardPermission.ARRAY_STRINGS.length; ++i) {
            if (s == CardPermission.ARRAY_STRINGS[i]) {
                return CardPermission.ARRAY_MASKS[i];
            }
        }
        if (s.endsWith(",")) {
            throw new IllegalArgumentException("Invalid actions: '" + s + "'");
        }
        int n = 0;
        final String[] split = s.split(",");
        final int length = split.length;
        int j = 0;
    Label_0111:
        while (j < length) {
            final String s2 = split[j];
            for (int k = 0; k < CardPermission.ARRAY_STRINGS.length; ++k) {
                if (CardPermission.ARRAY_STRINGS[k].equalsIgnoreCase(s2)) {
                    n |= CardPermission.ARRAY_MASKS[k];
                    ++j;
                    continue Label_0111;
                }
            }
            throw new IllegalArgumentException("Invalid action: '" + s2 + "'");
        }
        return n;
    }
    
    private static String getActions(final int n) {
        if (n == 63) {
            return "*";
        }
        int n2 = 1;
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CardPermission.ARRAY_MASKS.length; ++i) {
            final int n3 = CardPermission.ARRAY_MASKS[i];
            if ((n & n3) == n3) {
                if (n2 == 0) {
                    sb.append(",");
                }
                else {
                    n2 = 0;
                }
                sb.append(CardPermission.ARRAY_STRINGS[i]);
            }
        }
        return sb.toString();
    }
    
    @Override
    public String getActions() {
        if (this.actions == null) {
            this.actions = getActions(this.mask);
        }
        return this.actions;
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (!(permission instanceof CardPermission)) {
            return false;
        }
        final CardPermission cardPermission = (CardPermission)permission;
        if ((this.mask & cardPermission.mask) != cardPermission.mask) {
            return false;
        }
        final String name = this.getName();
        return name.equals("*") || name.equals(cardPermission.getName());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CardPermission)) {
            return false;
        }
        final CardPermission cardPermission = (CardPermission)o;
        return this.getName().equals(cardPermission.getName()) && this.mask == cardPermission.mask;
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode() + 31 * this.mask;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (this.actions == null) {
            this.getActions();
        }
        objectOutputStream.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.mask = getMask(this.actions);
    }
    
    static {
        ARRAY_MASKS = new int[] { 63, 1, 2, 4, 8, 16, 32 };
        ARRAY_STRINGS = new String[] { "*", "connect", "exclusive", "getBasicChannel", "openLogicalChannel", "reset", "transmitControl" };
    }
}
