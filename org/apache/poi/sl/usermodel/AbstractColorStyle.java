package org.apache.poi.sl.usermodel;

import java.util.Objects;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.util.Internal;

@Internal
public abstract class AbstractColorStyle implements ColorStyle
{
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof ColorStyle && Objects.equals(DrawPaint.applyColorTransform(this), DrawPaint.applyColorTransform((ColorStyle)o)));
    }
    
    @Override
    public int hashCode() {
        return DrawPaint.applyColorTransform(this).hashCode();
    }
}
