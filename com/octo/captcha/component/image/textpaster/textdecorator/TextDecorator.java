package com.octo.captcha.component.image.textpaster.textdecorator;

import com.octo.captcha.component.image.textpaster.MutableAttributedString;
import java.awt.Graphics2D;

public interface TextDecorator
{
    void decorateAttributedString(final Graphics2D p0, final MutableAttributedString p1);
}
