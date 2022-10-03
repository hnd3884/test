package com.lowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;

public class PdfDashPattern extends PdfArray
{
    private float dash;
    private float gap;
    private float phase;
    
    public PdfDashPattern() {
        this.dash = -1.0f;
        this.gap = -1.0f;
        this.phase = -1.0f;
    }
    
    public PdfDashPattern(final float dash) {
        super(new PdfNumber(dash));
        this.dash = -1.0f;
        this.gap = -1.0f;
        this.phase = -1.0f;
        this.dash = dash;
    }
    
    public PdfDashPattern(final float dash, final float gap) {
        super(new PdfNumber(dash));
        this.dash = -1.0f;
        this.gap = -1.0f;
        this.phase = -1.0f;
        this.add(new PdfNumber(gap));
        this.dash = dash;
        this.gap = gap;
    }
    
    public PdfDashPattern(final float dash, final float gap, final float phase) {
        super(new PdfNumber(dash));
        this.dash = -1.0f;
        this.gap = -1.0f;
        this.phase = -1.0f;
        this.add(new PdfNumber(gap));
        this.dash = dash;
        this.gap = gap;
        this.phase = phase;
    }
    
    public void add(final float n) {
        this.add(new PdfNumber(n));
    }
    
    @Override
    public void toPdf(final PdfWriter writer, final OutputStream os) throws IOException {
        os.write(91);
        if (this.dash >= 0.0f) {
            new PdfNumber(this.dash).toPdf(writer, os);
            if (this.gap >= 0.0f) {
                os.write(32);
                new PdfNumber(this.gap).toPdf(writer, os);
            }
        }
        os.write(93);
        if (this.phase >= 0.0f) {
            os.write(32);
            new PdfNumber(this.phase).toPdf(writer, os);
        }
    }
}
