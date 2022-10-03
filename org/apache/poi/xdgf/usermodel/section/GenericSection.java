package org.apache.poi.xdgf.usermodel.section;

import org.apache.poi.xdgf.usermodel.XDGFSheet;
import com.microsoft.schemas.office.visio.x2012.main.SectionType;

public class GenericSection extends XDGFSection
{
    public GenericSection(final SectionType section, final XDGFSheet containingSheet) {
        super(section, containingSheet);
    }
    
    @Override
    public void setupMaster(final XDGFSection section) {
    }
}
