package org.apache.poi.ss.usermodel;

import java.util.Locale;

public abstract class ExtendedColor implements Color
{
    protected void setColor(final java.awt.Color clr) {
        this.setRGB(new byte[] { (byte)clr.getRed(), (byte)clr.getGreen(), (byte)clr.getBlue() });
    }
    
    public abstract boolean isAuto();
    
    public abstract boolean isIndexed();
    
    public abstract boolean isRGB();
    
    public abstract boolean isThemed();
    
    public abstract short getIndex();
    
    public abstract int getTheme();
    
    public abstract byte[] getRGB();
    
    public abstract byte[] getARGB();
    
    protected abstract byte[] getStoredRBG();
    
    public abstract void setRGB(final byte[] p0);
    
    protected byte[] getRGBOrARGB() {
        if (this.isIndexed() && this.getIndex() > 0) {
            final byte[] rgb = this.getIndexedRGB();
            if (rgb != null) {
                return rgb;
            }
        }
        return this.getStoredRBG();
    }
    
    protected abstract byte[] getIndexedRGB();
    
    public byte[] getRGBWithTint() {
        byte[] rgb = this.getStoredRBG();
        if (rgb != null) {
            if (rgb.length == 4) {
                final byte[] tmp = new byte[3];
                System.arraycopy(rgb, 1, tmp, 0, 3);
                rgb = tmp;
            }
            final double tint = this.getTint();
            for (int i = 0; i < rgb.length; ++i) {
                rgb[i] = applyTint(rgb[i] & 0xFF, tint);
            }
        }
        return rgb;
    }
    
    public String getARGBHex() {
        final byte[] rgb = this.getARGB();
        if (rgb == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (final byte c : rgb) {
            final int i = c & 0xFF;
            final String cs = Integer.toHexString(i);
            if (cs.length() == 1) {
                sb.append('0');
            }
            sb.append(cs);
        }
        return sb.toString().toUpperCase(Locale.ROOT);
    }
    
    public void setARGBHex(final String argb) {
        if (argb.length() == 6 || argb.length() == 8) {
            final byte[] rgb = new byte[argb.length() / 2];
            for (int i = 0; i < rgb.length; ++i) {
                final String part = argb.substring(i * 2, (i + 1) * 2);
                rgb[i] = (byte)Integer.parseInt(part, 16);
            }
            this.setRGB(rgb);
            return;
        }
        throw new IllegalArgumentException("Must be of the form 112233 or FFEEDDCC");
    }
    
    private static byte applyTint(final int lum, final double tint) {
        if (tint > 0.0) {
            return (byte)(lum * (1.0 - tint) + (255.0 - 255.0 * (1.0 - tint)));
        }
        if (tint < 0.0) {
            return (byte)(lum * (1.0 + tint));
        }
        return (byte)lum;
    }
    
    public abstract double getTint();
    
    public abstract void setTint(final double p0);
}
