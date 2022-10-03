package javax.naming.ldap;

import com.sun.jndi.ldap.BerEncoder;
import java.io.IOException;

public final class SortControl extends BasicControl
{
    public static final String OID = "1.2.840.113556.1.4.473";
    private static final long serialVersionUID = -1965961680233330744L;
    
    public SortControl(final String s, final boolean b) throws IOException {
        super("1.2.840.113556.1.4.473", b, null);
        super.value = this.setEncodedValue(new SortKey[] { new SortKey(s) });
    }
    
    public SortControl(final String[] array, final boolean b) throws IOException {
        super("1.2.840.113556.1.4.473", b, null);
        final SortKey[] encodedValue = new SortKey[array.length];
        for (int i = 0; i < array.length; ++i) {
            encodedValue[i] = new SortKey(array[i]);
        }
        super.value = this.setEncodedValue(encodedValue);
    }
    
    public SortControl(final SortKey[] encodedValue, final boolean b) throws IOException {
        super("1.2.840.113556.1.4.473", b, null);
        super.value = this.setEncodedValue(encodedValue);
    }
    
    private byte[] setEncodedValue(final SortKey[] array) throws IOException {
        final BerEncoder berEncoder = new BerEncoder(30 * array.length + 10);
        berEncoder.beginSeq(48);
        for (int i = 0; i < array.length; ++i) {
            berEncoder.beginSeq(48);
            berEncoder.encodeString(array[i].getAttributeID(), true);
            final String matchingRuleID;
            if ((matchingRuleID = array[i].getMatchingRuleID()) != null) {
                berEncoder.encodeString(matchingRuleID, 128, true);
            }
            if (!array[i].isAscending()) {
                berEncoder.encodeBoolean(true, 129);
            }
            berEncoder.endSeq();
        }
        berEncoder.endSeq();
        return berEncoder.getTrimmedBuf();
    }
}
