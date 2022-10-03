package org.apache.poi.sl.usermodel;

import java.util.HashMap;
import java.util.Map;
import java.awt.Color;

public enum PresetColor
{
    ActiveBorder(Integer.valueOf(-4934476), 1, "activeBorder"), 
    ActiveCaption(Integer.valueOf(-6703919), 2, "activeCaption"), 
    ActiveCaptionText(Integer.valueOf(-16777216), 3, "captionText"), 
    AppWorkspace(Integer.valueOf(-5526613), 4, "appWorkspace"), 
    Control(Integer.valueOf(-986896), 5, "btnFace"), 
    ControlDark(Integer.valueOf(-9868951), 6, "btnShadow"), 
    ControlDarkDark(Integer.valueOf(-16777216), 7, "3dDkShadow"), 
    ControlLight(Integer.valueOf(-1842205), 8, "btnHighlight"), 
    ControlLightLight(Integer.valueOf(-1842205), 9, "3dLight"), 
    ControlText(Integer.valueOf(-16777216), 10, "btnText"), 
    Desktop(Integer.valueOf(-16777216), 11, "background"), 
    GrayText(Integer.valueOf(-9605779), 12, "grayText"), 
    Highlight(Integer.valueOf(-13395457), 13, "highlight"), 
    HighlightText(Integer.valueOf(-1), 14, "highlightText"), 
    HotTrack(Integer.valueOf(-16750900), 15, "hotLight"), 
    InactiveBorder(Integer.valueOf(-722948), 16, "inactiveBorder"), 
    InactiveCaption(Integer.valueOf(-4207141), 17, "inactiveCaption"), 
    InactiveCaptionText(Integer.valueOf(-16777216), 18, "inactiveCaptionText"), 
    Info(Integer.valueOf(-31), 19, "infoBk"), 
    InfoText(Integer.valueOf(-16777216), 20, "infoText"), 
    Menu(Integer.valueOf(-986896), 21, "menu"), 
    MenuText(Integer.valueOf(-16777216), 22, "menuText"), 
    ScrollBar(Integer.valueOf(-3618616), 23, "scrollBar"), 
    Window(Integer.valueOf(-1), 24, "window"), 
    WindowFrame(Integer.valueOf(-10197916), 25, "windowFrame"), 
    WindowText(Integer.valueOf(-16777216), 26, "windowText"), 
    Transparent(Integer.valueOf(16777215), 27, (String)null), 
    AliceBlue(Integer.valueOf(-984833), 28, "aliceBlue"), 
    AntiqueWhite(Integer.valueOf(-332841), 29, "antiqueWhite"), 
    Aqua(Integer.valueOf(-16711681), 30, "aqua"), 
    Aquamarine(Integer.valueOf(-8388652), 31, "aquamarine"), 
    Azure(Integer.valueOf(-983041), 32, "azure"), 
    Beige(Integer.valueOf(-657956), 33, "beige"), 
    Bisque(Integer.valueOf(-6972), 34, "bisque"), 
    Black(Integer.valueOf(-16777216), 35, "black"), 
    BlanchedAlmond(Integer.valueOf(-5171), 36, "blanchedAlmond"), 
    Blue(Integer.valueOf(-16776961), 37, "blue"), 
    BlueViolet(Integer.valueOf(-7722014), 38, "blueViolet"), 
    Brown(Integer.valueOf(-5952982), 39, "brown"), 
    BurlyWood(Integer.valueOf(-2180985), 40, "burlyWood"), 
    CadetBlue(Integer.valueOf(-10510688), 41, "cadetBlue"), 
    Chartreuse(Integer.valueOf(-8388864), 42, "chartreuse"), 
    Chocolate(Integer.valueOf(-2987746), 43, "chocolate"), 
    Coral(Integer.valueOf(-32944), 44, "coral"), 
    CornflowerBlue(Integer.valueOf(-10185235), 45, "cornflowerBlue"), 
    Cornsilk(Integer.valueOf(-1828), 46, "cornsilk"), 
    Crimson(Integer.valueOf(-2354116), 47, "crimson"), 
    Cyan(Integer.valueOf(-16711681), 48, "cyan"), 
    DarkBlue(Integer.valueOf(-16777077), 49, "dkBlue"), 
    DarkCyan(Integer.valueOf(-16741493), 50, "dkCyan"), 
    DarkGoldenrod(Integer.valueOf(-4684277), 51, "dkGoldenrod"), 
    DarkGray(Integer.valueOf(-5658199), 52, "dkGray"), 
    DarkGreen(Integer.valueOf(-16751616), 53, "dkGreen"), 
    DarkKhaki(Integer.valueOf(-4343957), 54, "dkKhaki"), 
    DarkMagenta(Integer.valueOf(-7667573), 55, "dkMagenta"), 
    DarkOliveGreen(Integer.valueOf(-11179217), 56, "dkOliveGreen"), 
    DarkOrange(Integer.valueOf(-29696), 57, "dkOrange"), 
    DarkOrchid(Integer.valueOf(-6737204), 58, "dkOrchid"), 
    DarkRed(Integer.valueOf(-7667712), 59, "dkRed"), 
    DarkSalmon(Integer.valueOf(-1468806), 60, "dkSalmon"), 
    DarkSeaGreen(Integer.valueOf(-7357301), 61, "dkSeaGreen"), 
    DarkSlateBlue(Integer.valueOf(-12042869), 62, "dkSlateBlue"), 
    DarkSlateGray(Integer.valueOf(-13676721), 63, "dkSlateGray"), 
    DarkTurquoise(Integer.valueOf(-16724271), 64, "dkTurquoise"), 
    DarkViolet(Integer.valueOf(-7077677), 65, "dkViolet"), 
    DeepPink(Integer.valueOf(-60269), 66, "deepPink"), 
    DeepSkyBlue(Integer.valueOf(-16728065), 67, "deepSkyBlue"), 
    DimGray(Integer.valueOf(-9868951), 68, "dimGray"), 
    DodgerBlue(Integer.valueOf(-14774017), 69, "dodgerBlue"), 
    Firebrick(Integer.valueOf(-5103070), 70, "firebrick"), 
    FloralWhite(Integer.valueOf(-1296), 71, "floralWhite"), 
    ForestGreen(Integer.valueOf(-14513374), 72, "forestGreen"), 
    Fuchsia(Integer.valueOf(-65281), 73, "fuchsia"), 
    Gainsboro(Integer.valueOf(-2302756), 74, "gainsboro"), 
    GhostWhite(Integer.valueOf(-460545), 75, "ghostWhite"), 
    Gold(Integer.valueOf(-10496), 76, "gold"), 
    Goldenrod(Integer.valueOf(-2448096), 77, "goldenrod"), 
    Gray(Integer.valueOf(-8355712), 78, "gray"), 
    Green(Integer.valueOf(-16744448), 79, "green"), 
    GreenYellow(Integer.valueOf(-5374161), 80, "greenYellow"), 
    Honeydew(Integer.valueOf(-983056), 81, "honeydew"), 
    HotPink(Integer.valueOf(-38476), 82, "hotPink"), 
    IndianRed(Integer.valueOf(-3318692), 83, "indianRed"), 
    Indigo(Integer.valueOf(-11861886), 84, "indigo"), 
    Ivory(Integer.valueOf(-16), 85, "ivory"), 
    Khaki(Integer.valueOf(-989556), 86, "khaki"), 
    Lavender(Integer.valueOf(-1644806), 87, "lavender"), 
    LavenderBlush(Integer.valueOf(-3851), 88, "lavenderBlush"), 
    LawnGreen(Integer.valueOf(-8586240), 89, "lawnGreen"), 
    LemonChiffon(Integer.valueOf(-1331), 90, "lemonChiffon"), 
    LightBlue(Integer.valueOf(-5383962), 91, "ltBlue"), 
    LightCoral(Integer.valueOf(-1015680), 92, "ltCoral"), 
    LightCyan(Integer.valueOf(-2031617), 93, "ltCyan"), 
    LightGoldenrodYellow(Integer.valueOf(-329096), 94, "ltGoldenrodYellow"), 
    LightGray(Integer.valueOf(-2894893), 95, "ltGray"), 
    LightGreen(Integer.valueOf(-7278960), 96, "ltGreen"), 
    LightPink(Integer.valueOf(-18751), 97, "ltPink"), 
    LightSalmon(Integer.valueOf(-24454), 98, "ltSalmon"), 
    LightSeaGreen(Integer.valueOf(-14634326), 99, "ltSeaGreen"), 
    LightSkyBlue(Integer.valueOf(-7876870), 100, "ltSkyBlue"), 
    LightSlateGray(Integer.valueOf(-8943463), 101, "ltSlateGray"), 
    LightSteelBlue(Integer.valueOf(-5192482), 102, "ltSteelBlue"), 
    LightYellow(Integer.valueOf(-32), 103, "ltYellow"), 
    Lime(Integer.valueOf(-16711936), 104, "lime"), 
    LimeGreen(Integer.valueOf(-13447886), 105, "limeGreen"), 
    Linen(Integer.valueOf(-331546), 106, "linen"), 
    Magenta(Integer.valueOf(-65281), 107, "magenta"), 
    Maroon(Integer.valueOf(-8388608), 108, "maroon"), 
    MediumAquamarine(Integer.valueOf(-10039894), 109, "medAquamarine"), 
    MediumBlue(Integer.valueOf(-16777011), 110, "medBlue"), 
    MediumOrchid(Integer.valueOf(-4565549), 111, "medOrchid"), 
    MediumPurple(Integer.valueOf(-7114533), 112, "medPurple"), 
    MediumSeaGreen(Integer.valueOf(-12799119), 113, "medSeaGreen"), 
    MediumSlateBlue(Integer.valueOf(-8689426), 114, "medSlateBlue"), 
    MediumSpringGreen(Integer.valueOf(-16713062), 115, "medSpringGreen"), 
    MediumTurquoise(Integer.valueOf(-12004916), 116, "medTurquoise"), 
    MediumVioletRed(Integer.valueOf(-3730043), 117, "medVioletRed"), 
    MidnightBlue(Integer.valueOf(-15132304), 118, "midnightBlue"), 
    MintCream(Integer.valueOf(-655366), 119, "mintCream"), 
    MistyRose(Integer.valueOf(-6943), 120, "mistyRose"), 
    Moccasin(Integer.valueOf(-6987), 121, "moccasin"), 
    NavajoWhite(Integer.valueOf(-8531), 122, "navajoWhite"), 
    Navy(Integer.valueOf(-16777088), 123, "navy"), 
    OldLace(Integer.valueOf(-133658), 124, "oldLace"), 
    Olive(Integer.valueOf(-8355840), 125, "olive"), 
    OliveDrab(Integer.valueOf(-9728477), 126, "oliveDrab"), 
    Orange(Integer.valueOf(-23296), 127, "orange"), 
    OrangeRed(Integer.valueOf(-47872), 128, "orangeRed"), 
    Orchid(Integer.valueOf(-2461482), 129, "orchid"), 
    PaleGoldenrod(Integer.valueOf(-1120086), 130, "paleGoldenrod"), 
    PaleGreen(Integer.valueOf(-6751336), 131, "paleGreen"), 
    PaleTurquoise(Integer.valueOf(-5247250), 132, "paleTurquoise"), 
    PaleVioletRed(Integer.valueOf(-2396013), 133, "paleVioletRed"), 
    PapayaWhip(Integer.valueOf(-4139), 134, "papayaWhip"), 
    PeachPuff(Integer.valueOf(-9543), 135, "peachPuff"), 
    Peru(Integer.valueOf(-3308225), 136, "peru"), 
    Pink(Integer.valueOf(-16181), 137, "pink"), 
    Plum(Integer.valueOf(-2252579), 138, "plum"), 
    PowderBlue(Integer.valueOf(-5185306), 139, "powderBlue"), 
    Purple(Integer.valueOf(-8388480), 140, "purple"), 
    Red(Integer.valueOf(-65536), 141, "red"), 
    RosyBrown(Integer.valueOf(-4419697), 142, "rosyBrown"), 
    RoyalBlue(Integer.valueOf(-12490271), 143, "royalBlue"), 
    SaddleBrown(Integer.valueOf(-7650029), 144, "saddleBrown"), 
    Salmon(Integer.valueOf(-360334), 145, "salmon"), 
    SandyBrown(Integer.valueOf(-744352), 146, "sandyBrown"), 
    SeaGreen(Integer.valueOf(-13726889), 147, "seaGreen"), 
    SeaShell(Integer.valueOf(-2578), 148, "seaShell"), 
    Sienna(Integer.valueOf(-6270419), 149, "sienna"), 
    Silver(Integer.valueOf(-4144960), 150, "silver"), 
    SkyBlue(Integer.valueOf(-7876885), 151, "skyBlue"), 
    SlateBlue(Integer.valueOf(-9807155), 152, "slateBlue"), 
    SlateGray(Integer.valueOf(-9404272), 153, "slateGray"), 
    Snow(Integer.valueOf(-1286), 154, "snow"), 
    SpringGreen(Integer.valueOf(-16711809), 155, "springGreen"), 
    SteelBlue(Integer.valueOf(-12156236), 156, "steelBlue"), 
    Tan(Integer.valueOf(-2968436), 157, "tan"), 
    Teal(Integer.valueOf(-16744320), 158, "teal"), 
    Thistle(Integer.valueOf(-2572328), 159, "thistle"), 
    Tomato(Integer.valueOf(-40121), 160, "tomato"), 
    Turquoise(Integer.valueOf(-12525360), 161, "turquoise"), 
    Violet(Integer.valueOf(-1146130), 162, "violet"), 
    Wheat(Integer.valueOf(-663885), 163, "wheat"), 
    White(Integer.valueOf(-1), 164, "white"), 
    WhiteSmoke(Integer.valueOf(-657931), 165, "whiteSmoke"), 
    Yellow(Integer.valueOf(-256), 166, "yellow"), 
    YellowGreen(Integer.valueOf(-6632142), 167, "yellowGreen"), 
    ButtonFace(Integer.valueOf(-986896), 168, (String)null), 
    ButtonHighlight(Integer.valueOf(-1), 169, (String)null), 
    ButtonShadow(Integer.valueOf(-6250336), 170, (String)null), 
    GradientActiveCaption(Integer.valueOf(-4599318), 171, "gradientActiveCaption"), 
    GradientInactiveCaption(Integer.valueOf(-2628366), 172, "gradientInactiveCaption"), 
    MenuBar(Integer.valueOf(-986896), 173, "menuBar"), 
    MenuHighlight(Integer.valueOf(-13395457), 174, "menuHighlight");
    
    public final Color color;
    public final int nativeId;
    public final String ooxmlId;
    private static final Map<String, PresetColor> lookupOoxmlId;
    
    private PresetColor(final Integer rgb, final int nativeId, final String ooxmlId) {
        this.color = ((rgb == null) ? null : new Color(rgb, true));
        this.nativeId = nativeId;
        this.ooxmlId = ooxmlId;
    }
    
    public static PresetColor valueOfOoxmlId(final String ooxmlId) {
        return PresetColor.lookupOoxmlId.get(ooxmlId);
    }
    
    public static PresetColor valueOfNativeId(final int nativeId) {
        final PresetColor[] vals = values();
        return (0 < nativeId && nativeId <= vals.length) ? vals[nativeId - 1] : null;
    }
    
    static {
        lookupOoxmlId = new HashMap<String, PresetColor>();
        for (final PresetColor pc : values()) {
            if (pc.ooxmlId != null) {
                PresetColor.lookupOoxmlId.put(pc.ooxmlId, pc);
            }
        }
    }
}