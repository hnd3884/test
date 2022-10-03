package sun.print;

import java.awt.Graphics;
import java.awt.PrintJob;
import java.awt.PrintGraphics;

public class ProxyPrintGraphics extends ProxyGraphics implements PrintGraphics
{
    private PrintJob printJob;
    
    public ProxyPrintGraphics(final Graphics graphics, final PrintJob printJob) {
        super(graphics);
        this.printJob = printJob;
    }
    
    @Override
    public PrintJob getPrintJob() {
        return this.printJob;
    }
    
    @Override
    public Graphics create() {
        return new ProxyPrintGraphics(this.getGraphics().create(), this.printJob);
    }
    
    @Override
    public Graphics create(final int n, final int n2, final int n3, final int n4) {
        return new ProxyPrintGraphics(this.getGraphics().create(n, n2, n3, n4), this.printJob);
    }
    
    public Graphics getGraphics() {
        return super.getGraphics();
    }
    
    @Override
    public void dispose() {
        super.dispose();
    }
}
