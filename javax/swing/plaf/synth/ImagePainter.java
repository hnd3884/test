package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import sun.awt.AppContext;
import java.lang.ref.WeakReference;
import sun.swing.plaf.synth.Paint9Painter;
import java.net.URL;
import java.awt.Insets;
import java.awt.Image;

class ImagePainter extends SynthPainter
{
    private static final StringBuffer CACHE_KEY;
    private Image image;
    private Insets sInsets;
    private Insets dInsets;
    private URL path;
    private boolean tiles;
    private boolean paintCenter;
    private Paint9Painter imageCache;
    private boolean center;
    
    private static Paint9Painter getPaint9Painter() {
        synchronized (ImagePainter.CACHE_KEY) {
            final WeakReference weakReference = (WeakReference)AppContext.getAppContext().get(ImagePainter.CACHE_KEY);
            Paint9Painter paint9Painter;
            if (weakReference == null || (paint9Painter = (Paint9Painter)weakReference.get()) == null) {
                paint9Painter = new Paint9Painter(30);
                AppContext.getAppContext().put(ImagePainter.CACHE_KEY, new WeakReference(paint9Painter));
            }
            return paint9Painter;
        }
    }
    
    ImagePainter(final boolean tiles, final boolean paintCenter, final Insets insets, final Insets insets2, final URL path, final boolean center) {
        if (insets != null) {
            this.sInsets = (Insets)insets.clone();
        }
        if (insets2 == null) {
            this.dInsets = this.sInsets;
        }
        else {
            this.dInsets = (Insets)insets2.clone();
        }
        this.tiles = tiles;
        this.paintCenter = paintCenter;
        this.imageCache = getPaint9Painter();
        this.path = path;
        this.center = center;
    }
    
    public boolean getTiles() {
        return this.tiles;
    }
    
    public boolean getPaintsCenter() {
        return this.paintCenter;
    }
    
    public boolean getCenter() {
        return this.center;
    }
    
    public Insets getInsets(final Insets insets) {
        if (insets == null) {
            return (Insets)this.dInsets.clone();
        }
        insets.left = this.dInsets.left;
        insets.right = this.dInsets.right;
        insets.top = this.dInsets.top;
        insets.bottom = this.dInsets.bottom;
        return insets;
    }
    
    public Image getImage() {
        if (this.image == null) {
            this.image = new ImageIcon(this.path, null).getImage();
        }
        return this.image;
    }
    
    private void paint(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        final Image image = this.getImage();
        if (Paint9Painter.validImage(image)) {
            Paint9Painter.PaintType paintType;
            if (this.getCenter()) {
                paintType = Paint9Painter.PaintType.CENTER;
            }
            else if (!this.getTiles()) {
                paintType = Paint9Painter.PaintType.PAINT9_STRETCH;
            }
            else {
                paintType = Paint9Painter.PaintType.PAINT9_TILE;
            }
            int n5 = 512;
            if (!this.getCenter() && !this.getPaintsCenter()) {
                n5 |= 0x10;
            }
            this.imageCache.paint(synthContext.getComponent(), graphics, n, n2, n3, n4, image, this.sInsets, this.dInsets, paintType, n5);
        }
    }
    
    @Override
    public void paintArrowButtonBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintArrowButtonBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintArrowButtonForeground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintButtonBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintButtonBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintCheckBoxMenuItemBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintCheckBoxMenuItemBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintCheckBoxBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintCheckBoxBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintColorChooserBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintColorChooserBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintComboBoxBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintComboBoxBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintDesktopIconBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintDesktopIconBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintDesktopPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintDesktopPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintEditorPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintEditorPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintFileChooserBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintFileChooserBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintFormattedTextFieldBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintFormattedTextFieldBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintInternalFrameTitlePaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintInternalFrameTitlePaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintInternalFrameBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintInternalFrameBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintLabelBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintLabelBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintListBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintListBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintMenuBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintMenuBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintMenuItemBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintMenuItemBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintMenuBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintMenuBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintOptionPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintOptionPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintPanelBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintPanelBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintPasswordFieldBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintPasswordFieldBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintPopupMenuBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintPopupMenuBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintProgressBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintProgressBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintProgressBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintProgressBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintProgressBarForeground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintRadioButtonMenuItemBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintRadioButtonMenuItemBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintRadioButtonBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintRadioButtonBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintRootPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintRootPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintScrollBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintScrollBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintScrollBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintScrollBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintScrollBarThumbBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintScrollBarThumbBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintScrollBarTrackBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintScrollBarTrackBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintScrollBarTrackBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintScrollBarTrackBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintScrollPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintScrollPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSeparatorBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSeparatorBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSeparatorBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSeparatorBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSeparatorForeground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSliderBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSliderBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSliderBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSliderBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSliderThumbBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSliderThumbBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSliderTrackBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSliderTrackBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSliderTrackBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSliderTrackBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSpinnerBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSpinnerBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSplitPaneDividerBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSplitPaneDividerBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSplitPaneDividerForeground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSplitPaneDragDivider(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSplitPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintSplitPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTabbedPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTabbedPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTabbedPaneTabAreaBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTabbedPaneTabAreaBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTabbedPaneTabAreaBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTabbedPaneTabAreaBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTabbedPaneTabBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTabbedPaneTabBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTabbedPaneTabBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTabbedPaneTabBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTabbedPaneContentBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTabbedPaneContentBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTableHeaderBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTableHeaderBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTableBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTableBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTextAreaBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTextAreaBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTextPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTextPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTextFieldBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTextFieldBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintToggleButtonBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintToggleButtonBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintToolBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintToolBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintToolBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintToolBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintToolBarContentBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintToolBarContentBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintToolBarContentBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintToolBarContentBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintToolBarDragWindowBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintToolBarDragWindowBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintToolBarDragWindowBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintToolBarDragWindowBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintToolTipBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintToolTipBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTreeBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTreeBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTreeCellBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTreeCellBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintTreeCellFocus(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintViewportBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintViewportBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paint(synthContext, graphics, n, n2, n3, n4);
    }
    
    static {
        CACHE_KEY = new StringBuffer("SynthCacheKey");
    }
}
