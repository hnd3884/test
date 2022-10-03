package org.apache.poi.hpsf.wellknown;

import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.util.Removal;
import org.apache.poi.util.Internal;

@Internal
@Deprecated
@Removal(version = "4.2.0")
public class SectionIDMap
{
    @Deprecated
    public static final ClassID SUMMARY_INFORMATION_ID;
    @Deprecated
    public static final ClassID[] DOCUMENT_SUMMARY_INFORMATION_ID;
    @Deprecated
    public static final String UNDEFINED = "[undefined]";
    
    static {
        SUMMARY_INFORMATION_ID = SummaryInformation.FORMAT_ID;
        DOCUMENT_SUMMARY_INFORMATION_ID = DocumentSummaryInformation.FORMAT_ID;
    }
}
