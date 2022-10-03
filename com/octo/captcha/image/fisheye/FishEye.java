package com.octo.captcha.image.fisheye;

import java.awt.geom.Point2D;
import java.util.StringTokenizer;
import java.awt.image.BufferedImage;
import java.awt.Point;
import com.octo.captcha.image.ImageCaptcha;

public class FishEye extends ImageCaptcha
{
    private Point deformationCenter;
    private Integer tolerance;
    
    protected FishEye(final String s, final BufferedImage bufferedImage, final Point deformationCenter, final Integer tolerance) {
        super(s, bufferedImage);
        this.deformationCenter = deformationCenter;
        this.tolerance = tolerance;
    }
    
    public Boolean validateResponse(final Object o) {
        if (o instanceof Point) {
            return this.validateResponse((Point)o);
        }
        if (o instanceof String) {
            final String s = (String)o;
            try {
                final StringTokenizer stringTokenizer = new StringTokenizer(s, ",");
                return this.validateResponse(new Point(Integer.parseInt(stringTokenizer.nextToken()), Integer.parseInt(stringTokenizer.nextToken())));
            }
            catch (final Throwable t) {
                return Boolean.FALSE;
            }
        }
        return Boolean.FALSE;
    }
    
    private Boolean validateResponse(final Point point) {
        if (point.distance(this.deformationCenter) <= this.tolerance) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
