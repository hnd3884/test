package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.common.usermodel.HyperlinkType;

public interface CreationHelper
{
    RichTextString createRichTextString(final String p0);
    
    DataFormat createDataFormat();
    
    Hyperlink createHyperlink(final HyperlinkType p0);
    
    FormulaEvaluator createFormulaEvaluator();
    
    ExtendedColor createExtendedColor();
    
    ClientAnchor createClientAnchor();
    
    AreaReference createAreaReference(final String p0);
    
    AreaReference createAreaReference(final CellReference p0, final CellReference p1);
}
