package javax.swing.plaf.nimbus;

import java.awt.Shape;
import java.awt.Paint;
import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Path2D;

final class FileChooserPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_ENABLED = 1;
    static final int FILEICON_ENABLED = 2;
    static final int DIRECTORYICON_ENABLED = 3;
    static final int UPFOLDERICON_ENABLED = 4;
    static final int NEWFOLDERICON_ENABLED = 5;
    static final int COMPUTERICON_ENABLED = 6;
    static final int HARDDRIVEICON_ENABLED = 7;
    static final int FLOPPYDRIVEICON_ENABLED = 8;
    static final int HOMEFOLDERICON_ENABLED = 9;
    static final int DETAILSVIEWICON_ENABLED = 10;
    static final int LISTVIEWICON_ENABLED = 11;
    private int state;
    private PaintContext ctx;
    private Path2D path;
    private Rectangle2D rect;
    private RoundRectangle2D roundRect;
    private Ellipse2D ellipse;
    private Color color1;
    private Color color2;
    private Color color3;
    private Color color4;
    private Color color5;
    private Color color6;
    private Color color7;
    private Color color8;
    private Color color9;
    private Color color10;
    private Color color11;
    private Color color12;
    private Color color13;
    private Color color14;
    private Color color15;
    private Color color16;
    private Color color17;
    private Color color18;
    private Color color19;
    private Color color20;
    private Color color21;
    private Color color22;
    private Color color23;
    private Color color24;
    private Color color25;
    private Color color26;
    private Color color27;
    private Color color28;
    private Color color29;
    private Color color30;
    private Color color31;
    private Color color32;
    private Color color33;
    private Color color34;
    private Color color35;
    private Color color36;
    private Color color37;
    private Color color38;
    private Color color39;
    private Color color40;
    private Color color41;
    private Color color42;
    private Color color43;
    private Color color44;
    private Color color45;
    private Color color46;
    private Color color47;
    private Color color48;
    private Color color49;
    private Color color50;
    private Color color51;
    private Color color52;
    private Color color53;
    private Color color54;
    private Color color55;
    private Color color56;
    private Color color57;
    private Color color58;
    private Color color59;
    private Color color60;
    private Color color61;
    private Color color62;
    private Color color63;
    private Color color64;
    private Color color65;
    private Color color66;
    private Color color67;
    private Color color68;
    private Color color69;
    private Color color70;
    private Color color71;
    private Color color72;
    private Color color73;
    private Color color74;
    private Color color75;
    private Color color76;
    private Color color77;
    private Color color78;
    private Color color79;
    private Color color80;
    private Color color81;
    private Color color82;
    private Color color83;
    private Color color84;
    private Color color85;
    private Color color86;
    private Color color87;
    private Color color88;
    private Color color89;
    private Color color90;
    private Color color91;
    private Color color92;
    private Color color93;
    private Color color94;
    private Color color95;
    private Color color96;
    private Color color97;
    private Color color98;
    private Color color99;
    private Color color100;
    private Color color101;
    private Color color102;
    private Color color103;
    private Color color104;
    private Color color105;
    private Color color106;
    private Color color107;
    private Color color108;
    private Color color109;
    private Color color110;
    private Color color111;
    private Color color112;
    private Color color113;
    private Color color114;
    private Color color115;
    private Color color116;
    private Color color117;
    private Color color118;
    private Color color119;
    private Color color120;
    private Color color121;
    private Color color122;
    private Object[] componentColors;
    
    public FileChooserPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("control", 0.0f, 0.0f, 0.0f, 0);
        this.color2 = this.decodeColor("nimbusBlueGrey", 0.007936537f, -0.065654516f, -0.13333333f, 0);
        this.color3 = new Color(97, 98, 102, 255);
        this.color4 = this.decodeColor("nimbusBlueGrey", -0.032679737f, -0.043332636f, 0.24705881f, 0);
        this.color5 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, 0);
        this.color6 = this.decodeColor("nimbusBase", 0.0077680945f, -0.51781034f, 0.3490196f, 0);
        this.color7 = this.decodeColor("nimbusBase", 0.013940871f, -0.599277f, 0.41960782f, 0);
        this.color8 = this.decodeColor("nimbusBase", 0.004681647f, -0.4198052f, 0.14117646f, 0);
        this.color9 = this.decodeColor("nimbusBase", 0.0f, -0.6357143f, 0.45098037f, -127);
        this.color10 = this.decodeColor("nimbusBlueGrey", 0.0f, 0.0f, -0.21f, -99);
        this.color11 = this.decodeColor("nimbusBase", 2.9569864E-4f, -0.45978838f, 0.2980392f, 0);
        this.color12 = this.decodeColor("nimbusBase", 0.0015952587f, -0.34848025f, 0.18823528f, 0);
        this.color13 = this.decodeColor("nimbusBase", 0.0015952587f, -0.30844158f, 0.09803921f, 0);
        this.color14 = this.decodeColor("nimbusBase", 0.0015952587f, -0.27329817f, 0.035294116f, 0);
        this.color15 = this.decodeColor("nimbusBase", 0.004681647f, -0.6198413f, 0.43921566f, 0);
        this.color16 = this.decodeColor("nimbusBase", 0.0f, -0.6357143f, 0.45098037f, -125);
        this.color17 = this.decodeColor("nimbusBase", 0.0f, -0.6357143f, 0.45098037f, -50);
        this.color18 = this.decodeColor("nimbusBase", 0.0f, -0.6357143f, 0.45098037f, -100);
        this.color19 = this.decodeColor("nimbusBase", 0.0012094378f, -0.23571429f, -0.0784314f, 0);
        this.color20 = this.decodeColor("nimbusBase", 2.9569864E-4f, -0.115166366f, -0.2627451f, 0);
        this.color21 = this.decodeColor("nimbusBase", 0.0027436614f, -0.335015f, 0.011764705f, 0);
        this.color22 = this.decodeColor("nimbusBase", 0.0024294257f, -0.3857143f, 0.031372547f, 0);
        this.color23 = this.decodeColor("nimbusBase", 0.0018081069f, -0.3595238f, -0.13725492f, 0);
        this.color24 = new Color(255, 200, 0, 255);
        this.color25 = this.decodeColor("nimbusBase", 0.004681647f, -0.44904763f, 0.039215684f, 0);
        this.color26 = this.decodeColor("nimbusBase", 0.0015952587f, -0.43718487f, -0.015686274f, 0);
        this.color27 = this.decodeColor("nimbusBase", 2.9569864E-4f, -0.39212453f, -0.24313727f, 0);
        this.color28 = this.decodeColor("nimbusBase", 0.004681647f, -0.6117143f, 0.43137252f, 0);
        this.color29 = this.decodeColor("nimbusBase", 0.0012094378f, -0.28015873f, -0.019607842f, 0);
        this.color30 = this.decodeColor("nimbusBase", 0.00254488f, -0.07049692f, -0.2784314f, 0);
        this.color31 = this.decodeColor("nimbusBase", 0.0015952587f, -0.28045115f, 0.04705882f, 0);
        this.color32 = this.decodeColor("nimbusBlueGrey", 0.0f, 5.847961E-4f, -0.21568626f, 0);
        this.color33 = this.decodeColor("nimbusBase", -0.0061469674f, 0.3642857f, 0.14509803f, 0);
        this.color34 = this.decodeColor("nimbusBase", 0.0053939223f, 0.3642857f, -0.0901961f, 0);
        this.color35 = this.decodeColor("nimbusBase", 0.0f, -0.6357143f, 0.45098037f, 0);
        this.color36 = this.decodeColor("nimbusBase", -0.006044388f, -0.23963585f, 0.45098037f, 0);
        this.color37 = this.decodeColor("nimbusBase", -0.0063245893f, 0.01592505f, 0.4078431f, 0);
        this.color38 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, -170);
        this.color39 = this.decodeColor("nimbusOrange", -0.032758567f, -0.018273294f, 0.25098038f, 0);
        this.color40 = new Color(255, 255, 255, 255);
        this.color41 = new Color(252, 255, 92, 255);
        this.color42 = new Color(253, 191, 4, 255);
        this.color43 = new Color(160, 161, 163, 255);
        this.color44 = new Color(0, 0, 0, 255);
        this.color45 = new Color(239, 241, 243, 255);
        this.color46 = new Color(197, 201, 205, 255);
        this.color47 = new Color(105, 110, 118, 255);
        this.color48 = new Color(63, 67, 72, 255);
        this.color49 = new Color(56, 51, 25, 255);
        this.color50 = new Color(144, 255, 0, 255);
        this.color51 = new Color(243, 245, 246, 255);
        this.color52 = new Color(208, 212, 216, 255);
        this.color53 = new Color(191, 193, 194, 255);
        this.color54 = new Color(170, 172, 175, 255);
        this.color55 = new Color(152, 155, 158, 255);
        this.color56 = new Color(59, 62, 66, 255);
        this.color57 = new Color(46, 46, 46, 255);
        this.color58 = new Color(64, 64, 64, 255);
        this.color59 = new Color(43, 43, 43, 255);
        this.color60 = new Color(164, 179, 206, 255);
        this.color61 = new Color(97, 123, 170, 255);
        this.color62 = new Color(53, 86, 146, 255);
        this.color63 = new Color(48, 82, 144, 255);
        this.color64 = new Color(71, 99, 150, 255);
        this.color65 = new Color(224, 224, 224, 255);
        this.color66 = new Color(232, 232, 232, 255);
        this.color67 = new Color(231, 234, 237, 255);
        this.color68 = new Color(205, 211, 215, 255);
        this.color69 = new Color(149, 153, 156, 54);
        this.color70 = new Color(255, 122, 101, 255);
        this.color71 = new Color(54, 78, 122, 255);
        this.color72 = new Color(51, 60, 70, 255);
        this.color73 = new Color(228, 232, 237, 255);
        this.color74 = new Color(27, 57, 87, 255);
        this.color75 = new Color(75, 109, 137, 255);
        this.color76 = new Color(77, 133, 185, 255);
        this.color77 = new Color(81, 59, 7, 255);
        this.color78 = new Color(97, 74, 18, 255);
        this.color79 = new Color(137, 115, 60, 255);
        this.color80 = new Color(174, 151, 91, 255);
        this.color81 = new Color(114, 92, 13, 255);
        this.color82 = new Color(64, 48, 0, 255);
        this.color83 = new Color(244, 222, 143, 255);
        this.color84 = new Color(160, 161, 162, 255);
        this.color85 = new Color(226, 230, 233, 255);
        this.color86 = new Color(221, 225, 230, 255);
        this.color87 = this.decodeColor("nimbusBase", 0.004681647f, -0.48756614f, 0.19215685f, 0);
        this.color88 = this.decodeColor("nimbusBase", 0.004681647f, -0.48399013f, 0.019607842f, 0);
        this.color89 = this.decodeColor("nimbusBase", -0.0028941035f, -0.5906323f, 0.4078431f, 0);
        this.color90 = this.decodeColor("nimbusBase", 0.004681647f, -0.51290727f, 0.34509802f, 0);
        this.color91 = this.decodeColor("nimbusBase", 0.009583652f, -0.5642857f, 0.3843137f, 0);
        this.color92 = this.decodeColor("nimbusBase", -0.0072231293f, -0.6074885f, 0.4235294f, 0);
        this.color93 = this.decodeColor("nimbusBase", 7.13408E-4f, -0.52158386f, 0.17254901f, 0);
        this.color94 = this.decodeColor("nimbusBase", 0.012257397f, -0.5775132f, 0.19215685f, 0);
        this.color95 = this.decodeColor("nimbusBase", 0.08801502f, -0.6164835f, -0.14117649f, 0);
        this.color96 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.5019608f, 0);
        this.color97 = this.decodeColor("nimbusBase", -0.0036516786f, -0.555393f, 0.42745095f, 0);
        this.color98 = this.decodeColor("nimbusBase", -0.0010654926f, -0.3634138f, 0.2862745f, 0);
        this.color99 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.29803923f, 0);
        this.color100 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, 0.12156862f, 0);
        this.color101 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.54901963f, 0);
        this.color102 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.48627454f, 0);
        this.color103 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.007843137f, 0);
        this.color104 = this.decodeColor("nimbusBase", -0.0028941035f, -0.5408867f, -0.09411767f, 0);
        this.color105 = this.decodeColor("nimbusBase", -0.011985004f, -0.54721874f, -0.10588238f, 0);
        this.color106 = this.decodeColor("nimbusBase", -0.0022627711f, -0.4305861f, -0.0901961f, 0);
        this.color107 = this.decodeColor("nimbusBase", -0.00573498f, -0.447479f, -0.21568629f, 0);
        this.color108 = this.decodeColor("nimbusBase", 0.004681647f, -0.53271f, 0.36470586f, 0);
        this.color109 = this.decodeColor("nimbusBase", 0.004681647f, -0.5276062f, -0.11372551f, 0);
        this.color110 = this.decodeColor("nimbusBase", -8.738637E-4f, -0.5278006f, -0.0039215684f, 0);
        this.color111 = this.decodeColor("nimbusBase", -0.0028941035f, -0.5338625f, -0.12549022f, 0);
        this.color112 = this.decodeColor("nimbusBlueGrey", -0.03535354f, -0.008674465f, -0.32156864f, 0);
        this.color113 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.010526314f, -0.3529412f, 0);
        this.color114 = this.decodeColor("nimbusBase", -0.0028941035f, -0.5234694f, -0.1647059f, 0);
        this.color115 = this.decodeColor("nimbusBase", 0.004681647f, -0.53401935f, -0.086274534f, 0);
        this.color116 = this.decodeColor("nimbusBase", 0.004681647f, -0.52077174f, -0.20784315f, 0);
        this.color117 = new Color(108, 114, 120, 255);
        this.color118 = new Color(77, 82, 87, 255);
        this.color119 = this.decodeColor("nimbusBase", -0.004577577f, -0.52179027f, -0.2392157f, 0);
        this.color120 = this.decodeColor("nimbusBase", -0.004577577f, -0.547479f, -0.14901963f, 0);
        this.color121 = new Color(186, 186, 186, 50);
        this.color122 = new Color(186, 186, 186, 40);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 1: {
                this.paintBackgroundEnabled(graphics2D);
                break;
            }
            case 2: {
                this.paintfileIconEnabled(graphics2D);
                break;
            }
            case 3: {
                this.paintdirectoryIconEnabled(graphics2D);
                break;
            }
            case 4: {
                this.paintupFolderIconEnabled(graphics2D);
                break;
            }
            case 5: {
                this.paintnewFolderIconEnabled(graphics2D);
                break;
            }
            case 7: {
                this.painthardDriveIconEnabled(graphics2D);
                break;
            }
            case 8: {
                this.paintfloppyDriveIconEnabled(graphics2D);
                break;
            }
            case 9: {
                this.painthomeFolderIconEnabled(graphics2D);
                break;
            }
            case 10: {
                this.paintdetailsViewIconEnabled(graphics2D);
                break;
            }
            case 11: {
                this.paintlistViewIconEnabled(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
    }
    
    private void paintfileIconEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color2);
        graphics2D.fill(this.path);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color3);
        graphics2D.fill(this.rect);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient1(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient2(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color8);
        graphics2D.fill(this.path);
        this.path = this.decodePath5();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.path);
    }
    
    private void paintdirectoryIconEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath6();
        graphics2D.setPaint(this.color10);
        graphics2D.fill(this.path);
        this.path = this.decodePath7();
        graphics2D.setPaint(this.decodeGradient3(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath8();
        graphics2D.setPaint(this.decodeGradient4(this.path));
        graphics2D.fill(this.path);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color17);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.color18);
        graphics2D.fill(this.rect);
        this.path = this.decodePath9();
        graphics2D.setPaint(this.decodeGradient5(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath10();
        graphics2D.setPaint(this.decodeGradient6(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath11();
        graphics2D.setPaint(this.color24);
        graphics2D.fill(this.path);
    }
    
    private void paintupFolderIconEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath12();
        graphics2D.setPaint(this.decodeGradient7(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath13();
        graphics2D.setPaint(this.decodeGradient8(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath14();
        graphics2D.setPaint(this.decodeGradient9(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath15();
        graphics2D.setPaint(this.decodeGradient10(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath16();
        graphics2D.setPaint(this.color32);
        graphics2D.fill(this.path);
        this.path = this.decodePath17();
        graphics2D.setPaint(this.decodeGradient11(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath18();
        graphics2D.setPaint(this.color35);
        graphics2D.fill(this.path);
        this.path = this.decodePath19();
        graphics2D.setPaint(this.decodeGradient12(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintnewFolderIconEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath6();
        graphics2D.setPaint(this.color10);
        graphics2D.fill(this.path);
        this.path = this.decodePath7();
        graphics2D.setPaint(this.decodeGradient3(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath8();
        graphics2D.setPaint(this.decodeGradient4(this.path));
        graphics2D.fill(this.path);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color17);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.color18);
        graphics2D.fill(this.rect);
        this.path = this.decodePath9();
        graphics2D.setPaint(this.decodeGradient5(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath10();
        graphics2D.setPaint(this.decodeGradient6(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath11();
        graphics2D.setPaint(this.color24);
        graphics2D.fill(this.path);
        this.path = this.decodePath20();
        graphics2D.setPaint(this.color38);
        graphics2D.fill(this.path);
        this.path = this.decodePath21();
        graphics2D.setPaint(this.color39);
        graphics2D.fill(this.path);
        this.path = this.decodePath22();
        graphics2D.setPaint(this.decodeRadial1(this.path));
        graphics2D.fill(this.path);
    }
    
    private void painthardDriveIconEnabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect6();
        graphics2D.setPaint(this.color43);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect7();
        graphics2D.setPaint(this.color44);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect8();
        graphics2D.setPaint(this.decodeGradient13(this.rect));
        graphics2D.fill(this.rect);
        this.path = this.decodePath23();
        graphics2D.setPaint(this.decodeGradient14(this.path));
        graphics2D.fill(this.path);
        this.rect = this.decodeRect9();
        graphics2D.setPaint(this.color49);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect10();
        graphics2D.setPaint(this.color49);
        graphics2D.fill(this.rect);
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.color50);
        graphics2D.fill(this.ellipse);
        this.path = this.decodePath24();
        graphics2D.setPaint(this.decodeGradient15(this.path));
        graphics2D.fill(this.path);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.color53);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse3();
        graphics2D.setPaint(this.color53);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse4();
        graphics2D.setPaint(this.color54);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse5();
        graphics2D.setPaint(this.color55);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse6();
        graphics2D.setPaint(this.color55);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse7();
        graphics2D.setPaint(this.color55);
        graphics2D.fill(this.ellipse);
        this.rect = this.decodeRect11();
        graphics2D.setPaint(this.color56);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect12();
        graphics2D.setPaint(this.color56);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect13();
        graphics2D.setPaint(this.color56);
        graphics2D.fill(this.rect);
    }
    
    private void paintfloppyDriveIconEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath25();
        graphics2D.setPaint(this.decodeGradient16(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath26();
        graphics2D.setPaint(this.decodeGradient17(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath27();
        graphics2D.setPaint(this.decodeGradient18(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath28();
        graphics2D.setPaint(this.decodeGradient19(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath29();
        graphics2D.setPaint(this.color69);
        graphics2D.fill(this.path);
        this.rect = this.decodeRect14();
        graphics2D.setPaint(this.color70);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect15();
        graphics2D.setPaint(this.color40);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect16();
        graphics2D.setPaint(this.color67);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect17();
        graphics2D.setPaint(this.color71);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect18();
        graphics2D.setPaint(this.color44);
        graphics2D.fill(this.rect);
    }
    
    private void painthomeFolderIconEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath30();
        graphics2D.setPaint(this.color72);
        graphics2D.fill(this.path);
        this.path = this.decodePath31();
        graphics2D.setPaint(this.color73);
        graphics2D.fill(this.path);
        this.rect = this.decodeRect19();
        graphics2D.setPaint(this.decodeGradient20(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect20();
        graphics2D.setPaint(this.color76);
        graphics2D.fill(this.rect);
        this.path = this.decodePath32();
        graphics2D.setPaint(this.decodeGradient21(this.path));
        graphics2D.fill(this.path);
        this.rect = this.decodeRect21();
        graphics2D.setPaint(this.decodeGradient22(this.rect));
        graphics2D.fill(this.rect);
        this.path = this.decodePath33();
        graphics2D.setPaint(this.decodeGradient23(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath34();
        graphics2D.setPaint(this.color83);
        graphics2D.fill(this.path);
        this.path = this.decodePath35();
        graphics2D.setPaint(this.decodeGradient24(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath36();
        graphics2D.setPaint(this.decodeGradient25(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintdetailsViewIconEnabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect22();
        graphics2D.setPaint(this.decodeGradient26(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect23();
        graphics2D.setPaint(this.decodeGradient27(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect24();
        graphics2D.setPaint(this.color93);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.color93);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect25();
        graphics2D.setPaint(this.color93);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect26();
        graphics2D.setPaint(this.color94);
        graphics2D.fill(this.rect);
        this.ellipse = this.decodeEllipse8();
        graphics2D.setPaint(this.decodeGradient28(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse9();
        graphics2D.setPaint(this.decodeRadial2(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.path = this.decodePath37();
        graphics2D.setPaint(this.decodeGradient29(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath38();
        graphics2D.setPaint(this.decodeGradient30(this.path));
        graphics2D.fill(this.path);
        this.rect = this.decodeRect27();
        graphics2D.setPaint(this.color104);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect28();
        graphics2D.setPaint(this.color105);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect29();
        graphics2D.setPaint(this.color106);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect30();
        graphics2D.setPaint(this.color107);
        graphics2D.fill(this.rect);
    }
    
    private void paintlistViewIconEnabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect31();
        graphics2D.setPaint(this.decodeGradient26(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect32();
        graphics2D.setPaint(this.decodeGradient31(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect33();
        graphics2D.setPaint(this.color109);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect34();
        graphics2D.setPaint(this.decodeGradient32(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect35();
        graphics2D.setPaint(this.color111);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect36();
        graphics2D.setPaint(this.color112);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect37();
        graphics2D.setPaint(this.color113);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect38();
        graphics2D.setPaint(this.decodeGradient33(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect39();
        graphics2D.setPaint(this.color116);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect40();
        graphics2D.setPaint(this.decodeGradient34(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect41();
        graphics2D.setPaint(this.decodeGradient35(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect42();
        graphics2D.setPaint(this.color119);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect43();
        graphics2D.setPaint(this.color121);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect44();
        graphics2D.setPaint(this.color121);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect45();
        graphics2D.setPaint(this.color121);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect46();
        graphics2D.setPaint(this.color122);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect47();
        graphics2D.setPaint(this.color121);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect48();
        graphics2D.setPaint(this.color122);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect49();
        graphics2D.setPaint(this.color122);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect50();
        graphics2D.setPaint(this.color121);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect51();
        graphics2D.setPaint(this.color122);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect52();
        graphics2D.setPaint(this.color122);
        graphics2D.fill(this.rect);
    }
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(1.0f), this.decodeX(2.0f) - this.decodeX(1.0f), this.decodeY(2.0f) - this.decodeY(1.0f));
        return this.rect;
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.2f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(1.9197531f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(0.9f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(0.88888896f));
        this.path.lineTo(this.decodeX(1.9537036f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(0.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Rectangle2D decodeRect2() {
        this.rect.setRect(this.decodeX(0.4f), this.decodeY(2.8f), this.decodeX(2.6f) - this.decodeX(0.4f), this.decodeY(3.0f) - this.decodeY(2.8f));
        return this.rect;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.8333333f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(0.2f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath3() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.8333333f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(0.2f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath4() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.8333333f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(1.6234567f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(1.6296296f), this.decodeY(1.2037038f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(1.2006173f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(0.2f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath5() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.8333333f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(0.4f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath6() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(2.4f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(2.4f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.4f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath7() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.6f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.6037037f), this.decodeY(1.8425925f));
        this.path.lineTo(this.decodeX(0.8f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(2.6f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath8() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.2f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.40833336f), this.decodeY(1.8645833f));
        this.path.lineTo(this.decodeX(0.79583335f), this.decodeY(0.8f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(0.8f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(2.6f));
        this.path.closePath();
        return this.path;
    }
    
    private Rectangle2D decodeRect3() {
        this.rect.setRect(this.decodeX(0.2f), this.decodeY(0.6f), this.decodeX(0.4f) - this.decodeX(0.2f), this.decodeY(0.8f) - this.decodeY(0.6f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect4() {
        this.rect.setRect(this.decodeX(0.6f), this.decodeY(0.2f), this.decodeX(1.3333334f) - this.decodeX(0.6f), this.decodeY(0.4f) - this.decodeY(0.2f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect5() {
        this.rect.setRect(this.decodeX(1.5f), this.decodeY(0.6f), this.decodeX(2.4f) - this.decodeX(1.5f), this.decodeY(0.8f) - this.decodeY(0.6f));
        return this.rect;
    }
    
    private Path2D decodePath9() {
        this.path.reset();
        this.path.moveTo(this.decodeX(3.0f), this.decodeY(0.8f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.5888889f), this.decodeY(0.20370372f));
        this.path.lineTo(this.decodeX(0.5962963f), this.decodeY(0.34814817f));
        this.path.lineTo(this.decodeX(0.34814817f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(2.774074f), this.decodeY(1.1604939f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.8925927f), this.decodeY(1.1882716f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.65185183f));
        this.path.lineTo(this.decodeX(0.63703704f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(1.5925925f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(0.8f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(0.8f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath10() {
        this.path.reset();
        this.path.moveTo(this.decodeX(2.4f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(0.8f));
        this.path.lineTo(this.decodeX(0.74814814f), this.decodeY(0.8f));
        this.path.lineTo(this.decodeX(0.4037037f), this.decodeY(1.8425925f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.5925926f), this.decodeY(2.225926f));
        this.path.lineTo(this.decodeX(0.916f), this.decodeY(0.996f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(1.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath11() {
        this.path.reset();
        this.path.moveTo(this.decodeX(2.2f), this.decodeY(2.2f));
        this.path.lineTo(this.decodeX(2.2f), this.decodeY(2.2f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath12() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(1.8333333f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.8f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.8f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath13() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.2f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(0.8f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(0.8f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(2.8f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath14() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.4f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(0.8f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(2.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath15() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.6f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(0.8f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(2.8f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath16() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.1702899f), this.decodeY(1.2536231f));
        this.path.lineTo(this.decodeX(1.1666666f), this.decodeY(1.0615941f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(1.0978261f));
        this.path.lineTo(this.decodeX(2.7782607f), this.decodeY(1.25f));
        this.path.lineTo(this.decodeX(2.3913045f), this.decodeY(1.3188406f));
        this.path.lineTo(this.decodeX(2.3826087f), this.decodeY(1.7246377f));
        this.path.lineTo(this.decodeX(2.173913f), this.decodeY(1.9347827f));
        this.path.lineTo(this.decodeX(1.8695652f), this.decodeY(1.923913f));
        this.path.lineTo(this.decodeX(1.710145f), this.decodeY(1.7246377f));
        this.path.lineTo(this.decodeX(1.710145f), this.decodeY(1.3115941f));
        this.path.lineTo(this.decodeX(1.1702899f), this.decodeY(1.2536231f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath17() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.1666666f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(1.1666666f), this.decodeY(0.9130435f));
        this.path.lineTo(this.decodeX(1.9456522f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(2.0608697f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(2.9956522f), this.decodeY(0.9130435f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(2.2f), this.decodeY(1.8333333f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(1.8333333f));
        this.path.lineTo(this.decodeX(1.6666667f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(1.6666667f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(1.1666666f), this.decodeY(1.1666666f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath18() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.2717391f), this.decodeY(0.9956522f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(2.2f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(2.2f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.8652174f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(0.13043478f));
        this.path.lineTo(this.decodeX(1.2717391f), this.decodeY(0.9956522f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath19() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.8333333f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.3913044f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.9963768f), this.decodeY(0.25652176f));
        this.path.lineTo(this.decodeX(2.6608696f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.2f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.2f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(1.6666667f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath20() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.22692308f), this.decodeY(0.061538465f));
        this.path.lineTo(this.decodeX(0.75384617f), this.decodeY(0.37692308f));
        this.path.lineTo(this.decodeX(0.91923076f), this.decodeY(0.01923077f));
        this.path.lineTo(this.decodeX(1.2532052f), this.decodeY(0.40769228f));
        this.path.lineTo(this.decodeX(1.7115386f), this.decodeY(0.13846155f));
        this.path.lineTo(this.decodeX(1.6923077f), this.decodeY(0.85f));
        this.path.lineTo(this.decodeX(2.169231f), this.decodeY(0.9115385f));
        this.path.lineTo(this.decodeX(1.7852564f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(1.9166667f), this.decodeY(1.9679487f));
        this.path.lineTo(this.decodeX(1.3685898f), this.decodeY(1.8301282f));
        this.path.lineTo(this.decodeX(1.1314102f), this.decodeY(2.2115386f));
        this.path.lineTo(this.decodeX(0.63076925f), this.decodeY(1.8205128f));
        this.path.lineTo(this.decodeX(0.22692308f), this.decodeY(1.9262822f));
        this.path.lineTo(this.decodeX(0.31153846f), this.decodeY(1.4871795f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(1.1538461f));
        this.path.lineTo(this.decodeX(0.38461536f), this.decodeY(0.68076926f));
        this.path.lineTo(this.decodeX(0.22692308f), this.decodeY(0.061538465f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath21() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.23461537f), this.decodeY(0.33076924f));
        this.path.lineTo(this.decodeX(0.32692307f), this.decodeY(0.21538463f));
        this.path.lineTo(this.decodeX(0.9653846f), this.decodeY(0.74615383f));
        this.path.lineTo(this.decodeX(1.0160257f), this.decodeY(0.01923077f));
        this.path.lineTo(this.decodeX(1.1506411f), this.decodeY(0.01923077f));
        this.path.lineTo(this.decodeX(1.2275641f), this.decodeY(0.72307694f));
        this.path.lineTo(this.decodeX(1.6987178f), this.decodeY(0.20769231f));
        this.path.lineTo(this.decodeX(1.8237178f), this.decodeY(0.37692308f));
        this.path.lineTo(this.decodeX(1.3878205f), this.decodeY(0.94230765f));
        this.path.lineTo(this.decodeX(1.9775641f), this.decodeY(1.0256411f));
        this.path.lineTo(this.decodeX(1.9839742f), this.decodeY(1.1474359f));
        this.path.lineTo(this.decodeX(1.4070512f), this.decodeY(1.2083334f));
        this.path.lineTo(this.decodeX(1.7980769f), this.decodeY(1.7307692f));
        this.path.lineTo(this.decodeX(1.7532051f), this.decodeY(1.8269231f));
        this.path.lineTo(this.decodeX(1.2211539f), this.decodeY(1.3365384f));
        this.path.lineTo(this.decodeX(1.1506411f), this.decodeY(1.9839742f));
        this.path.lineTo(this.decodeX(1.0288461f), this.decodeY(1.9775641f));
        this.path.lineTo(this.decodeX(0.95384616f), this.decodeY(1.3429488f));
        this.path.lineTo(this.decodeX(0.28846154f), this.decodeY(1.8012822f));
        this.path.lineTo(this.decodeX(0.20769231f), this.decodeY(1.7371795f));
        this.path.lineTo(this.decodeX(0.75f), this.decodeY(1.173077f));
        this.path.lineTo(this.decodeX(0.011538462f), this.decodeY(1.1634616f));
        this.path.lineTo(this.decodeX(0.015384616f), this.decodeY(1.0224359f));
        this.path.lineTo(this.decodeX(0.79615384f), this.decodeY(0.94230765f));
        this.path.lineTo(this.decodeX(0.23461537f), this.decodeY(0.33076924f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath22() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.58461535f), this.decodeY(0.6615385f));
        this.path.lineTo(this.decodeX(0.68846154f), this.decodeY(0.56923074f));
        this.path.lineTo(this.decodeX(0.9884615f), this.decodeY(0.80769235f));
        this.path.lineTo(this.decodeX(1.0352564f), this.decodeY(0.43076926f));
        this.path.lineTo(this.decodeX(1.1282052f), this.decodeY(0.43846154f));
        this.path.lineTo(this.decodeX(1.1891025f), this.decodeY(0.80769235f));
        this.path.lineTo(this.decodeX(1.4006411f), this.decodeY(0.59615386f));
        this.path.lineTo(this.decodeX(1.4967948f), this.decodeY(0.70384616f));
        this.path.lineTo(this.decodeX(1.3173077f), this.decodeY(0.9384615f));
        this.path.lineTo(this.decodeX(1.625f), this.decodeY(1.0256411f));
        this.path.lineTo(this.decodeX(1.6282051f), this.decodeY(1.1346154f));
        this.path.lineTo(this.decodeX(1.2564102f), this.decodeY(1.176282f));
        this.path.lineTo(this.decodeX(1.4711539f), this.decodeY(1.3910257f));
        this.path.lineTo(this.decodeX(1.4070512f), this.decodeY(1.4807693f));
        this.path.lineTo(this.decodeX(1.1858975f), this.decodeY(1.2724359f));
        this.path.lineTo(this.decodeX(1.1474359f), this.decodeY(1.6602564f));
        this.path.lineTo(this.decodeX(1.0416666f), this.decodeY(1.6602564f));
        this.path.lineTo(this.decodeX(0.9769231f), this.decodeY(1.2884616f));
        this.path.lineTo(this.decodeX(0.6923077f), this.decodeY(1.5f));
        this.path.lineTo(this.decodeX(0.6423077f), this.decodeY(1.3782052f));
        this.path.lineTo(this.decodeX(0.83076924f), this.decodeY(1.176282f));
        this.path.lineTo(this.decodeX(0.46923074f), this.decodeY(1.1474359f));
        this.path.lineTo(this.decodeX(0.48076925f), this.decodeY(1.0064102f));
        this.path.lineTo(this.decodeX(0.8230769f), this.decodeY(0.98461545f));
        this.path.lineTo(this.decodeX(0.58461535f), this.decodeY(0.6615385f));
        this.path.closePath();
        return this.path;
    }
    
    private Rectangle2D decodeRect6() {
        this.rect.setRect(this.decodeX(0.2f), this.decodeY(0.0f), this.decodeX(2.8f) - this.decodeX(0.2f), this.decodeY(2.2f) - this.decodeY(0.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect7() {
        this.rect.setRect(this.decodeX(0.2f), this.decodeY(2.2f), this.decodeX(2.8f) - this.decodeX(0.2f), this.decodeY(3.0f) - this.decodeY(2.2f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect8() {
        this.rect.setRect(this.decodeX(0.4f), this.decodeY(0.2f), this.decodeX(2.6f) - this.decodeX(0.4f), this.decodeY(2.2f) - this.decodeY(0.2f));
        return this.rect;
    }
    
    private Path2D decodePath23() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.4f), this.decodeY(2.2f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.2f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(2.2f));
        this.path.closePath();
        return this.path;
    }
    
    private Rectangle2D decodeRect9() {
        this.rect.setRect(this.decodeX(0.6f), this.decodeY(2.8f), this.decodeX(1.6666667f) - this.decodeX(0.6f), this.decodeY(3.0f) - this.decodeY(2.8f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect10() {
        this.rect.setRect(this.decodeX(1.8333333f), this.decodeY(2.8f), this.decodeX(2.4f) - this.decodeX(1.8333333f), this.decodeY(3.0f) - this.decodeY(2.8f));
        return this.rect;
    }
    
    private Ellipse2D decodeEllipse1() {
        this.ellipse.setFrame(this.decodeX(0.6f), this.decodeY(2.4f), this.decodeX(0.8f) - this.decodeX(0.6f), this.decodeY(2.6f) - this.decodeY(2.4f));
        return this.ellipse;
    }
    
    private Path2D decodePath24() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.0f), this.decodeY(0.4f));
        this.path.curveTo(this.decodeAnchorX(1.0f, 1.0f), this.decodeAnchorY(0.4f, -1.0f), this.decodeAnchorX(2.0f, -1.0f), this.decodeAnchorY(0.4f, -1.0f), this.decodeX(2.0f), this.decodeY(0.4f));
        this.path.curveTo(this.decodeAnchorX(2.0f, 1.0f), this.decodeAnchorY(0.4f, 1.0f), this.decodeAnchorX(2.2f, 0.0f), this.decodeAnchorY(1.0f, -1.0f), this.decodeX(2.2f), this.decodeY(1.0f));
        this.path.curveTo(this.decodeAnchorX(2.2f, 0.0f), this.decodeAnchorY(1.0f, 1.0f), this.decodeAnchorX(2.2f, 0.0f), this.decodeAnchorY(1.5f, -2.0f), this.decodeX(2.2f), this.decodeY(1.5f));
        this.path.curveTo(this.decodeAnchorX(2.2f, 0.0f), this.decodeAnchorY(1.5f, 2.0f), this.decodeAnchorX(1.6666667f, 1.0f), this.decodeAnchorY(1.8333333f, 0.0f), this.decodeX(1.6666667f), this.decodeY(1.8333333f));
        this.path.curveTo(this.decodeAnchorX(1.6666667f, -1.0f), this.decodeAnchorY(1.8333333f, 0.0f), this.decodeAnchorX(1.3333334f, 1.0f), this.decodeAnchorY(1.8333333f, 0.0f), this.decodeX(1.3333334f), this.decodeY(1.8333333f));
        this.path.curveTo(this.decodeAnchorX(1.3333334f, -1.0f), this.decodeAnchorY(1.8333333f, 0.0f), this.decodeAnchorX(0.8f, 0.0f), this.decodeAnchorY(1.5f, 2.0f), this.decodeX(0.8f), this.decodeY(1.5f));
        this.path.curveTo(this.decodeAnchorX(0.8f, 0.0f), this.decodeAnchorY(1.5f, -2.0f), this.decodeAnchorX(0.8f, 0.0f), this.decodeAnchorY(1.0f, 1.0f), this.decodeX(0.8f), this.decodeY(1.0f));
        this.path.curveTo(this.decodeAnchorX(0.8f, 0.0f), this.decodeAnchorY(1.0f, -1.0f), this.decodeAnchorX(1.0f, -1.0f), this.decodeAnchorY(0.4f, 1.0f), this.decodeX(1.0f), this.decodeY(0.4f));
        this.path.closePath();
        return this.path;
    }
    
    private Ellipse2D decodeEllipse2() {
        this.ellipse.setFrame(this.decodeX(0.6f), this.decodeY(0.2f), this.decodeX(0.8f) - this.decodeX(0.6f), this.decodeY(0.4f) - this.decodeY(0.2f));
        return this.ellipse;
    }
    
    private Ellipse2D decodeEllipse3() {
        this.ellipse.setFrame(this.decodeX(2.2f), this.decodeY(0.2f), this.decodeX(2.4f) - this.decodeX(2.2f), this.decodeY(0.4f) - this.decodeY(0.2f));
        return this.ellipse;
    }
    
    private Ellipse2D decodeEllipse4() {
        this.ellipse.setFrame(this.decodeX(2.2f), this.decodeY(1.0f), this.decodeX(2.4f) - this.decodeX(2.2f), this.decodeY(1.1666666f) - this.decodeY(1.0f));
        return this.ellipse;
    }
    
    private Ellipse2D decodeEllipse5() {
        this.ellipse.setFrame(this.decodeX(2.2f), this.decodeY(1.6666667f), this.decodeX(2.4f) - this.decodeX(2.2f), this.decodeY(1.8333333f) - this.decodeY(1.6666667f));
        return this.ellipse;
    }
    
    private Ellipse2D decodeEllipse6() {
        this.ellipse.setFrame(this.decodeX(0.6f), this.decodeY(1.6666667f), this.decodeX(0.8f) - this.decodeX(0.6f), this.decodeY(1.8333333f) - this.decodeY(1.6666667f));
        return this.ellipse;
    }
    
    private Ellipse2D decodeEllipse7() {
        this.ellipse.setFrame(this.decodeX(0.6f), this.decodeY(1.0f), this.decodeX(0.8f) - this.decodeX(0.6f), this.decodeY(1.1666666f) - this.decodeY(1.0f));
        return this.ellipse;
    }
    
    private Rectangle2D decodeRect11() {
        this.rect.setRect(this.decodeX(0.8f), this.decodeY(2.2f), this.decodeX(1.0f) - this.decodeX(0.8f), this.decodeY(2.6f) - this.decodeY(2.2f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect12() {
        this.rect.setRect(this.decodeX(1.1666666f), this.decodeY(2.2f), this.decodeX(1.3333334f) - this.decodeX(1.1666666f), this.decodeY(2.6f) - this.decodeY(2.2f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect13() {
        this.rect.setRect(this.decodeX(1.5f), this.decodeY(2.2f), this.decodeX(1.6666667f) - this.decodeX(1.5f), this.decodeY(2.6f) - this.decodeY(2.2f));
        return this.rect;
    }
    
    private Path2D decodePath25() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.2f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath26() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.2f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(0.4f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath27() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.8f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(1.5f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(1.5f));
        this.path.lineTo(this.decodeX(2.2f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(2.2f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.8f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.8f), this.decodeY(1.6666667f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath28() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.1666666f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(1.1666666f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(2.2f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(2.2f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.6666667f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.6666667f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(2.2f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(2.2f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(1.1666666f), this.decodeY(0.2f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath29() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.8f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.6666667f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.6666667f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(0.8f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(0.8f), this.decodeY(0.2f));
        this.path.closePath();
        return this.path;
    }
    
    private Rectangle2D decodeRect14() {
        this.rect.setRect(this.decodeX(0.8f), this.decodeY(2.6f), this.decodeX(2.2f) - this.decodeX(0.8f), this.decodeY(2.8f) - this.decodeY(2.6f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect15() {
        this.rect.setRect(this.decodeX(0.36153847f), this.decodeY(2.3576922f), this.decodeX(0.63461536f) - this.decodeX(0.36153847f), this.decodeY(2.6807692f) - this.decodeY(2.3576922f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect16() {
        this.rect.setRect(this.decodeX(2.376923f), this.decodeY(2.3807693f), this.decodeX(2.6384616f) - this.decodeX(2.376923f), this.decodeY(2.6846154f) - this.decodeY(2.3807693f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect17() {
        this.rect.setRect(this.decodeX(0.4f), this.decodeY(2.4f), this.decodeX(0.6f) - this.decodeX(0.4f), this.decodeY(2.6f) - this.decodeY(2.4f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect18() {
        this.rect.setRect(this.decodeX(2.4f), this.decodeY(2.4f), this.decodeX(2.6f) - this.decodeX(2.4f), this.decodeY(2.6f) - this.decodeY(2.4f));
        return this.rect;
    }
    
    private Path2D decodePath30() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.4f), this.decodeY(1.5f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(1.5f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(1.5f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath31() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.6f), this.decodeY(1.5f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(1.5f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(0.8f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(1.5f));
        this.path.closePath();
        return this.path;
    }
    
    private Rectangle2D decodeRect19() {
        this.rect.setRect(this.decodeX(1.6666667f), this.decodeY(1.6666667f), this.decodeX(2.2f) - this.decodeX(1.6666667f), this.decodeY(2.2f) - this.decodeY(1.6666667f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect20() {
        this.rect.setRect(this.decodeX(1.8333333f), this.decodeY(1.8333333f), this.decodeX(2.0f) - this.decodeX(1.8333333f), this.decodeY(2.0f) - this.decodeY(1.8333333f));
        return this.rect;
    }
    
    private Path2D decodePath32() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.0f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(1.8333333f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(1.1666666f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(1.8333333f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(2.8f));
        this.path.closePath();
        return this.path;
    }
    
    private Rectangle2D decodeRect21() {
        this.rect.setRect(this.decodeX(1.1666666f), this.decodeY(1.8333333f), this.decodeX(1.3333334f) - this.decodeX(1.1666666f), this.decodeY(2.6f) - this.decodeY(1.8333333f));
        return this.rect;
    }
    
    private Path2D decodePath33() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(1.3974359f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.596154f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(1.6666667f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(1.3333334f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath34() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.2576923f), this.decodeY(1.3717948f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(1.5f));
        this.path.lineTo(this.decodeX(0.3230769f), this.decodeY(1.4711539f));
        this.path.lineTo(this.decodeX(1.4006411f), this.decodeY(0.40384617f));
        this.path.lineTo(this.decodeX(1.5929487f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(2.6615386f), this.decodeY(1.4615384f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(1.5f));
        this.path.lineTo(this.decodeX(2.7461538f), this.decodeY(1.3653846f));
        this.path.lineTo(this.decodeX(1.6089742f), this.decodeY(0.19615385f));
        this.path.lineTo(this.decodeX(1.4070512f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.2576923f), this.decodeY(1.3717948f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath35() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.6f), this.decodeY(1.5f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(1.5f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath36() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.6666667f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(1.6666667f), this.decodeY(0.6f));
        this.path.closePath();
        return this.path;
    }
    
    private Rectangle2D decodeRect22() {
        this.rect.setRect(this.decodeX(0.2f), this.decodeY(0.0f), this.decodeX(3.0f) - this.decodeX(0.2f), this.decodeY(2.8f) - this.decodeY(0.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect23() {
        this.rect.setRect(this.decodeX(0.4f), this.decodeY(0.2f), this.decodeX(2.8f) - this.decodeX(0.4f), this.decodeY(2.6f) - this.decodeY(0.2f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect24() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(0.6f), this.decodeX(1.3333334f) - this.decodeX(1.0f), this.decodeY(0.8f) - this.decodeY(0.6f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect25() {
        this.rect.setRect(this.decodeX(1.5f), this.decodeY(1.3333334f), this.decodeX(2.4f) - this.decodeX(1.5f), this.decodeY(1.5f) - this.decodeY(1.3333334f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect26() {
        this.rect.setRect(this.decodeX(1.5f), this.decodeY(2.0f), this.decodeX(2.4f) - this.decodeX(1.5f), this.decodeY(2.2f) - this.decodeY(2.0f));
        return this.rect;
    }
    
    private Ellipse2D decodeEllipse8() {
        this.ellipse.setFrame(this.decodeX(0.6f), this.decodeY(0.8f), this.decodeX(2.2f) - this.decodeX(0.6f), this.decodeY(2.4f) - this.decodeY(0.8f));
        return this.ellipse;
    }
    
    private Ellipse2D decodeEllipse9() {
        this.ellipse.setFrame(this.decodeX(0.8f), this.decodeY(1.0f), this.decodeX(2.0f) - this.decodeX(0.8f), this.decodeY(2.2f) - this.decodeY(1.0f));
        return this.ellipse;
    }
    
    private Path2D decodePath37() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(2.2f));
        this.path.lineTo(this.decodeX(0.8f), this.decodeY(1.8333333f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.8f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath38() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.1826087f), this.decodeY(2.7217393f));
        this.path.lineTo(this.decodeX(0.2826087f), this.decodeY(2.8217392f));
        this.path.lineTo(this.decodeX(1.0181159f), this.decodeY(2.095652f));
        this.path.lineTo(this.decodeX(0.9130435f), this.decodeY(1.9891305f));
        this.path.lineTo(this.decodeX(0.1826087f), this.decodeY(2.7217393f));
        this.path.closePath();
        return this.path;
    }
    
    private Rectangle2D decodeRect27() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(1.3333334f), this.decodeX(1.3333334f) - this.decodeX(1.0f), this.decodeY(1.5f) - this.decodeY(1.3333334f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect28() {
        this.rect.setRect(this.decodeX(1.5f), this.decodeY(1.3333334f), this.decodeX(1.8333333f) - this.decodeX(1.5f), this.decodeY(1.5f) - this.decodeY(1.3333334f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect29() {
        this.rect.setRect(this.decodeX(1.5f), this.decodeY(1.6666667f), this.decodeX(1.8333333f) - this.decodeX(1.5f), this.decodeY(1.8333333f) - this.decodeY(1.6666667f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect30() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(1.6666667f), this.decodeX(1.3333334f) - this.decodeX(1.0f), this.decodeY(1.8333333f) - this.decodeY(1.6666667f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect31() {
        this.rect.setRect(this.decodeX(0.0f), this.decodeY(0.0f), this.decodeX(3.0f) - this.decodeX(0.0f), this.decodeY(2.8f) - this.decodeY(0.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect32() {
        this.rect.setRect(this.decodeX(0.2f), this.decodeY(0.2f), this.decodeX(2.8f) - this.decodeX(0.2f), this.decodeY(2.6f) - this.decodeY(0.2f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect33() {
        this.rect.setRect(this.decodeX(0.8f), this.decodeY(0.6f), this.decodeX(1.1666666f) - this.decodeX(0.8f), this.decodeY(0.8f) - this.decodeY(0.6f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect34() {
        this.rect.setRect(this.decodeX(1.3333334f), this.decodeY(0.6f), this.decodeX(2.2f) - this.decodeX(1.3333334f), this.decodeY(0.8f) - this.decodeY(0.6f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect35() {
        this.rect.setRect(this.decodeX(1.3333334f), this.decodeY(1.0f), this.decodeX(2.0f) - this.decodeX(1.3333334f), this.decodeY(1.1666666f) - this.decodeY(1.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect36() {
        this.rect.setRect(this.decodeX(0.8f), this.decodeY(1.0f), this.decodeX(1.1666666f) - this.decodeX(0.8f), this.decodeY(1.1666666f) - this.decodeY(1.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect37() {
        this.rect.setRect(this.decodeX(0.8f), this.decodeY(1.3333334f), this.decodeX(1.1666666f) - this.decodeX(0.8f), this.decodeY(1.5f) - this.decodeY(1.3333334f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect38() {
        this.rect.setRect(this.decodeX(1.3333334f), this.decodeY(1.3333334f), this.decodeX(2.2f) - this.decodeX(1.3333334f), this.decodeY(1.5f) - this.decodeY(1.3333334f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect39() {
        this.rect.setRect(this.decodeX(0.8f), this.decodeY(1.6666667f), this.decodeX(1.1666666f) - this.decodeX(0.8f), this.decodeY(1.8333333f) - this.decodeY(1.6666667f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect40() {
        this.rect.setRect(this.decodeX(1.3333334f), this.decodeY(1.6666667f), this.decodeX(2.0f) - this.decodeX(1.3333334f), this.decodeY(1.8333333f) - this.decodeY(1.6666667f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect41() {
        this.rect.setRect(this.decodeX(1.3333334f), this.decodeY(2.0f), this.decodeX(2.2f) - this.decodeX(1.3333334f), this.decodeY(2.2f) - this.decodeY(2.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect42() {
        this.rect.setRect(this.decodeX(0.8f), this.decodeY(2.0f), this.decodeX(1.1666666f) - this.decodeX(0.8f), this.decodeY(2.2f) - this.decodeY(2.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect43() {
        this.rect.setRect(this.decodeX(0.8f), this.decodeY(0.8f), this.decodeX(1.1666666f) - this.decodeX(0.8f), this.decodeY(1.0f) - this.decodeY(0.8f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect44() {
        this.rect.setRect(this.decodeX(1.3333334f), this.decodeY(0.8f), this.decodeX(2.2f) - this.decodeX(1.3333334f), this.decodeY(1.0f) - this.decodeY(0.8f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect45() {
        this.rect.setRect(this.decodeX(0.8f), this.decodeY(1.1666666f), this.decodeX(1.1666666f) - this.decodeX(0.8f), this.decodeY(1.3333334f) - this.decodeY(1.1666666f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect46() {
        this.rect.setRect(this.decodeX(1.3333334f), this.decodeY(1.1666666f), this.decodeX(2.0f) - this.decodeX(1.3333334f), this.decodeY(1.3333334f) - this.decodeY(1.1666666f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect47() {
        this.rect.setRect(this.decodeX(0.8f), this.decodeY(1.5f), this.decodeX(1.1666666f) - this.decodeX(0.8f), this.decodeY(1.6666667f) - this.decodeY(1.5f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect48() {
        this.rect.setRect(this.decodeX(1.3333334f), this.decodeY(1.5f), this.decodeX(2.2f) - this.decodeX(1.3333334f), this.decodeY(1.6666667f) - this.decodeY(1.5f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect49() {
        this.rect.setRect(this.decodeX(1.3333334f), this.decodeY(1.8333333f), this.decodeX(2.0f) - this.decodeX(1.3333334f), this.decodeY(2.0f) - this.decodeY(1.8333333f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect50() {
        this.rect.setRect(this.decodeX(0.8f), this.decodeY(1.8333333f), this.decodeX(1.1666666f) - this.decodeX(0.8f), this.decodeY(2.0f) - this.decodeY(1.8333333f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect51() {
        this.rect.setRect(this.decodeX(0.8f), this.decodeY(2.2f), this.decodeX(1.1666666f) - this.decodeX(0.8f), this.decodeY(2.4f) - this.decodeY(2.2f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect52() {
        this.rect.setRect(this.decodeX(1.3333334f), this.decodeY(2.2f), this.decodeX(2.2f) - this.decodeX(1.3333334f), this.decodeY(2.4f) - this.decodeY(2.2f));
        return this.rect;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.046296295f * n3 + n, 0.9675926f * n4 + n2, 0.4861111f * n3 + n, 0.5324074f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color4, this.decodeColor(this.color4, this.color5, 0.5f), this.color5 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color6, this.decodeColor(this.color6, this.color7, 0.5f), this.color7 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.04191617f, 0.10329342f, 0.16467066f, 0.24550897f, 0.3263473f, 0.6631737f, 1.0f }, new Color[] { this.color11, this.decodeColor(this.color11, this.color12, 0.5f), this.color12, this.decodeColor(this.color12, this.color13, 0.5f), this.color13, this.decodeColor(this.color13, this.color14, 0.5f), this.color14 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color6, this.decodeColor(this.color6, this.color15, 0.5f), this.color15 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color19, this.decodeColor(this.color19, this.color20, 0.5f), this.color20 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.12724552f, 0.25449103f, 0.62724555f, 1.0f }, new Color[] { this.color21, this.decodeColor(this.color21, this.color22, 0.5f), this.color22, this.decodeColor(this.color22, this.color23, 0.5f), this.color23 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.06392045f, 0.1278409f, 0.5213069f, 0.91477275f }, new Color[] { this.color25, this.decodeColor(this.color25, this.color26, 0.5f), this.color26, this.decodeColor(this.color26, this.color27, 0.5f), this.color27 });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.048295453f, 0.09659091f, 0.5482955f, 1.0f }, new Color[] { this.color28, this.decodeColor(this.color28, this.color6, 0.5f), this.color6, this.decodeColor(this.color6, this.color15, 0.5f), this.color15 });
    }
    
    private Paint decodeGradient9(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color29, this.decodeColor(this.color29, this.color30, 0.5f), this.color30 });
    }
    
    private Paint decodeGradient10(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.06534091f, 0.13068181f, 0.3096591f, 0.48863637f, 0.7443182f, 1.0f }, new Color[] { this.color11, this.decodeColor(this.color11, this.color12, 0.5f), this.color12, this.decodeColor(this.color12, this.color31, 0.5f), this.color31, this.decodeColor(this.color31, this.color14, 0.5f), this.color14 });
    }
    
    private Paint decodeGradient11(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color33, this.decodeColor(this.color33, this.color34, 0.5f), this.color34 });
    }
    
    private Paint decodeGradient12(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color36, this.decodeColor(this.color36, this.color37, 0.5f), this.color37 });
    }
    
    private Paint decodeRadial1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        return this.decodeRadialGradient(0.5f * (float)bounds2D.getWidth() + (float)bounds2D.getX(), 1.0f * (float)bounds2D.getHeight() + (float)bounds2D.getY(), 0.53913116f, new float[] { 0.11290322f, 0.17419355f, 0.23548387f, 0.31129032f, 0.38709676f, 0.47903225f, 0.57096773f }, new Color[] { this.color40, this.decodeColor(this.color40, this.color41, 0.5f), this.color41, this.decodeColor(this.color41, this.color41, 0.5f), this.color41, this.decodeColor(this.color41, this.color42, 0.5f), this.color42 });
    }
    
    private Paint decodeGradient13(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color45, this.decodeColor(this.color45, this.color46, 0.5f), this.color46 });
    }
    
    private Paint decodeGradient14(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color47, this.decodeColor(this.color47, this.color48, 0.5f), this.color48 });
    }
    
    private Paint decodeGradient15(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.3983871f, 0.7967742f, 0.8983871f, 1.0f }, new Color[] { this.color51, this.decodeColor(this.color51, this.color52, 0.5f), this.color52, this.decodeColor(this.color52, this.color51, 0.5f), this.color51 });
    }
    
    private Paint decodeGradient16(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.061290324f, 0.12258065f, 0.5016129f, 0.88064516f, 0.9403226f, 1.0f }, new Color[] { this.color57, this.decodeColor(this.color57, this.color58, 0.5f), this.color58, this.decodeColor(this.color58, this.color59, 0.5f), this.color59, this.decodeColor(this.color59, this.color44, 0.5f), this.color44 });
    }
    
    private Paint decodeGradient17(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.05f, 0.1f, 0.19193548f, 0.28387097f, 0.5209677f, 0.7580645f, 0.87903225f, 1.0f }, new Color[] { this.color60, this.decodeColor(this.color60, this.color61, 0.5f), this.color61, this.decodeColor(this.color61, this.color62, 0.5f), this.color62, this.decodeColor(this.color62, this.color63, 0.5f), this.color63, this.decodeColor(this.color63, this.color64, 0.5f), this.color64 });
    }
    
    private Paint decodeGradient18(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.058064517f, 0.090322584f, 0.12258065f, 0.15645161f, 0.19032258f, 0.22741935f, 0.26451612f, 0.31290323f, 0.36129034f, 0.38225806f, 0.4032258f, 0.4596774f, 0.516129f, 0.54193544f, 0.56774193f, 0.61451614f, 0.66129035f, 0.70645165f, 0.7516129f }, new Color[] { this.color65, this.decodeColor(this.color65, this.color40, 0.5f), this.color40, this.decodeColor(this.color40, this.color40, 0.5f), this.color40, this.decodeColor(this.color40, this.color65, 0.5f), this.color65, this.decodeColor(this.color65, this.color65, 0.5f), this.color65, this.decodeColor(this.color65, this.color40, 0.5f), this.color40, this.decodeColor(this.color40, this.color40, 0.5f), this.color40, this.decodeColor(this.color40, this.color66, 0.5f), this.color66, this.decodeColor(this.color66, this.color66, 0.5f), this.color66, this.decodeColor(this.color66, this.color40, 0.5f), this.color40 });
    }
    
    private Paint decodeGradient19(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color67, this.decodeColor(this.color67, this.color67, 0.5f), this.color67 });
    }
    
    private Paint decodeGradient20(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color74, this.decodeColor(this.color74, this.color75, 0.5f), this.color75 });
    }
    
    private Paint decodeGradient21(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color77, this.decodeColor(this.color77, this.color78, 0.5f), this.color78 });
    }
    
    private Paint decodeGradient22(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color79, this.decodeColor(this.color79, this.color80, 0.5f), this.color80 });
    }
    
    private Paint decodeGradient23(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color81, this.decodeColor(this.color81, this.color82, 0.5f), this.color82 });
    }
    
    private Paint decodeGradient24(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.43076923f * n3 + n, 0.37820512f * n4 + n2, 0.7076923f * n3 + n, 0.6730769f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color84, this.decodeColor(this.color84, this.color85, 0.5f), this.color85 });
    }
    
    private Paint decodeGradient25(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.63076925f * n3 + n, 0.3621795f * n4 + n2, 0.28846154f * n3 + n, 0.73397434f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color84, this.decodeColor(this.color84, this.color86, 0.5f), this.color86 });
    }
    
    private Paint decodeGradient26(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color87, this.decodeColor(this.color87, this.color88, 0.5f), this.color88 });
    }
    
    private Paint decodeGradient27(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.056818184f, 0.11363637f, 0.34232956f, 0.57102275f, 0.7855114f, 1.0f }, new Color[] { this.color89, this.decodeColor(this.color89, this.color90, 0.5f), this.color90, this.decodeColor(this.color90, this.color91, 0.5f), this.color91, this.decodeColor(this.color91, this.color92, 0.5f), this.color92 });
    }
    
    private Paint decodeGradient28(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.75f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color95, this.decodeColor(this.color95, this.color96, 0.5f), this.color96 });
    }
    
    private Paint decodeRadial2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        return this.decodeRadialGradient(0.49223602f * (float)bounds2D.getWidth() + (float)bounds2D.getX(), 0.9751553f * (float)bounds2D.getHeight() + (float)bounds2D.getY(), 0.73615754f, new float[] { 0.0f, 0.40625f, 1.0f }, new Color[] { this.color97, this.decodeColor(this.color97, this.color98, 0.5f), this.color98 });
    }
    
    private Paint decodeGradient29(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.0f * n3 + n, 0.0f * n4 + n2, 1.0f * n3 + n, 1.0f * n4 + n2, new float[] { 0.38352272f, 0.4190341f, 0.45454547f, 0.484375f, 0.51420456f }, new Color[] { this.color99, this.decodeColor(this.color99, this.color100, 0.5f), this.color100, this.decodeColor(this.color100, this.color101, 0.5f), this.color101 });
    }
    
    private Paint decodeGradient30(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(1.0f * n3 + n, 0.0f * n4 + n2, 0.0f * n3 + n, 1.0f * n4 + n2, new float[] { 0.12215909f, 0.16051137f, 0.19886364f, 0.2627841f, 0.32670453f, 0.43039775f, 0.53409094f }, new Color[] { this.color102, this.decodeColor(this.color102, this.color35, 0.5f), this.color35, this.decodeColor(this.color35, this.color35, 0.5f), this.color35, this.decodeColor(this.color35, this.color103, 0.5f), this.color103 });
    }
    
    private Paint decodeGradient31(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.038352273f, 0.07670455f, 0.24289773f, 0.4090909f, 0.7045455f, 1.0f }, new Color[] { this.color89, this.decodeColor(this.color89, this.color90, 0.5f), this.color90, this.decodeColor(this.color90, this.color108, 0.5f), this.color108, this.decodeColor(this.color108, this.color92, 0.5f), this.color92 });
    }
    
    private Paint decodeGradient32(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.0f * n3 + n, 0.0f * n4 + n2, 1.0f * n3 + n, 1.0f * n4 + n2, new float[] { 0.25f, 0.33522725f, 0.42045453f, 0.50142044f, 0.5823864f }, new Color[] { this.color109, this.decodeColor(this.color109, this.color110, 0.5f), this.color110, this.decodeColor(this.color110, this.color109, 0.5f), this.color109 });
    }
    
    private Paint decodeGradient33(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.75f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.24147727f, 0.48295453f, 0.74147725f, 1.0f }, new Color[] { this.color114, this.decodeColor(this.color114, this.color115, 0.5f), this.color115, this.decodeColor(this.color115, this.color114, 0.5f), this.color114 });
    }
    
    private Paint decodeGradient34(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.0f * n3 + n, 0.0f * n4 + n2, 1.0f * n3 + n, 0.0f * n4 + n2, new float[] { 0.0f, 0.21732955f, 0.4346591f }, new Color[] { this.color117, this.decodeColor(this.color117, this.color118, 0.5f), this.color118 });
    }
    
    private Paint decodeGradient35(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.0f * n3 + n, 0.0f * n4 + n2, 1.0f * n3 + n, 0.0f * n4 + n2, new float[] { 0.0f, 0.21448864f, 0.42897728f, 0.7144886f, 1.0f }, new Color[] { this.color119, this.decodeColor(this.color119, this.color120, 0.5f), this.color120, this.decodeColor(this.color120, this.color119, 0.5f), this.color119 });
    }
}
