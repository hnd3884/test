package com.octo.captcha.component.image.textpaster.glyphsdecorator;

import java.awt.image.BufferedImage;
import com.octo.captcha.component.image.textpaster.Glyphs;
import java.awt.Graphics2D;

public interface GlyphsDecorator
{
    void decorate(final Graphics2D p0, final Glyphs p1, final BufferedImage p2);
}
