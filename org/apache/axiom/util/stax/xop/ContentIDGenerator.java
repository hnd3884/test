package org.apache.axiom.util.stax.xop;

import org.apache.axiom.util.UIDGenerator;

public interface ContentIDGenerator
{
    public static final ContentIDGenerator DEFAULT = new ContentIDGenerator() {
        public String generateContentID(final String existingContentID) {
            if (existingContentID == null) {
                return UIDGenerator.generateContentId();
            }
            return existingContentID;
        }
    };
    
    String generateContentID(final String p0);
}
