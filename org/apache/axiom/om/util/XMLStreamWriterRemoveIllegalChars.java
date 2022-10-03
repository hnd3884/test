package org.apache.axiom.om.util;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class XMLStreamWriterRemoveIllegalChars extends XMLStreamWriterFilterBase
{
    private static final Log log;
    private static byte[] REMOVE;
    private final int FFFE = 65534;
    private final char FFFF = '\uffff';
    private final char SURROGATE_START = '\ud800';
    private final char SURROGATE_END = '\udfff';
    
    public XMLStreamWriterRemoveIllegalChars() {
        if (XMLStreamWriterRemoveIllegalChars.log.isDebugEnabled()) {
            XMLStreamWriterRemoveIllegalChars.log.debug((Object)("Creating XMLStreamWriterRemoveIllegalChars object " + this));
        }
    }
    
    @Override
    protected String xmlData(final String value) {
        char[] buffer = null;
        final int len = value.length();
        int srcI = 0;
        int tgtI = 0;
        int copyLength = 0;
        int i = 0;
        while (i < len) {
            final int cp = value.codePointAt(i);
            if (cp > 65535) {
                i += 2;
                copyLength += 2;
            }
            else {
                if ((cp < 32 && XMLStreamWriterRemoveIllegalChars.REMOVE[cp] > 0) || (cp >= 55296 && cp <= 57343) || cp == 65535 || cp == 65534) {
                    if (buffer == null) {
                        if (XMLStreamWriterRemoveIllegalChars.log.isDebugEnabled()) {
                            XMLStreamWriterRemoveIllegalChars.log.debug((Object)("One or more illegal characterss found.  Codepoint=" + cp));
                        }
                        buffer = value.toCharArray();
                    }
                    System.arraycopy(buffer, srcI, buffer, tgtI, copyLength);
                    tgtI += copyLength;
                    srcI = i + 1;
                    copyLength = 0;
                }
                else {
                    ++copyLength;
                }
                ++i;
            }
        }
        if (buffer == null) {
            return value;
        }
        System.arraycopy(buffer, srcI, buffer, tgtI, copyLength);
        final String newValue = new String(buffer, 0, tgtI + copyLength);
        return newValue;
    }
    
    static {
        log = LogFactory.getLog((Class)XMLStreamWriterRemoveIllegalChars.class);
        (XMLStreamWriterRemoveIllegalChars.REMOVE = new byte[32])[0] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[1] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[2] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[3] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[4] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[5] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[6] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[7] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[8] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[11] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[12] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[14] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[15] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[16] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[17] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[18] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[19] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[20] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[21] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[22] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[23] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[24] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[25] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[26] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[27] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[28] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[29] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[30] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[31] = 1;
    }
}
