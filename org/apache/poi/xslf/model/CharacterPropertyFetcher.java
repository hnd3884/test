package org.apache.poi.xslf.model;

import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;

public abstract class CharacterPropertyFetcher<T> extends ParagraphPropertyFetcher<T>
{
    public CharacterPropertyFetcher(final int level) {
        super(level);
    }
    
    @Override
    public boolean fetch(final CTTextParagraphProperties props) {
        return props != null && props.isSetDefRPr() && this.fetch(props.getDefRPr());
    }
    
    public abstract boolean fetch(final CTTextCharacterProperties p0);
}
