package org.apache.poi.xddf.usermodel.text;

import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import java.util.Optional;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import java.util.function.Function;

public interface TextContainer
{
     <R> Optional<R> findDefinedParagraphProperty(final Function<CTTextParagraphProperties, Boolean> p0, final Function<CTTextParagraphProperties, R> p1);
    
     <R> Optional<R> findDefinedRunProperty(final Function<CTTextCharacterProperties, Boolean> p0, final Function<CTTextCharacterProperties, R> p1);
}
