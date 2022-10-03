package com.lowagie.text.html;

import java.util.Locale;
import java.util.StringTokenizer;
import com.lowagie.text.error_messages.MessageLocalization;
import java.awt.Color;
import java.util.HashMap;

public class WebColors extends HashMap
{
    private static final long serialVersionUID = 3542523100813372896L;
    public static final WebColors NAMES;
    
    public static Color getRGBColor(String name) throws IllegalArgumentException {
        int[] c = { 0, 0, 0, 0 };
        if (name.startsWith("#")) {
            if (name.length() == 4) {
                c[0] = Integer.parseInt(name.substring(1, 2), 16) * 16;
                c[1] = Integer.parseInt(name.substring(2, 3), 16) * 16;
                c[2] = Integer.parseInt(name.substring(3), 16) * 16;
                return new Color(c[0], c[1], c[2], c[3]);
            }
            if (name.length() == 7) {
                c[0] = Integer.parseInt(name.substring(1, 3), 16);
                c[1] = Integer.parseInt(name.substring(3, 5), 16);
                c[2] = Integer.parseInt(name.substring(5), 16);
                return new Color(c[0], c[1], c[2], c[3]);
            }
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("unknown.color.format.must.be.rgb.or.rrggbb"));
        }
        else {
            if (name.startsWith("rgb(")) {
                final StringTokenizer tok = new StringTokenizer(name, "rgb(), \t\r\n\f");
                for (int k = 0; k < 3; ++k) {
                    final String v = tok.nextToken();
                    if (v.endsWith("%")) {
                        c[k] = Integer.parseInt(v.substring(0, v.length() - 1)) * 255 / 100;
                    }
                    else {
                        c[k] = Integer.parseInt(v);
                    }
                    if (c[k] < 0) {
                        c[k] = 0;
                    }
                    else if (c[k] > 255) {
                        c[k] = 255;
                    }
                }
                return new Color(c[0], c[1], c[2], c[3]);
            }
            name = name.toLowerCase(Locale.ROOT);
            if (!WebColors.NAMES.containsKey(name)) {
                throw new IllegalArgumentException("Color '" + name + "' not found.");
            }
            c = WebColors.NAMES.get(name);
            return new Color(c[0], c[1], c[2], c[3]);
        }
    }
    
    static {
        (NAMES = new WebColors()).put("aliceblue", new int[] { 240, 248, 255, 0 });
        WebColors.NAMES.put("antiquewhite", new int[] { 250, 235, 215, 0 });
        WebColors.NAMES.put("aqua", new int[] { 0, 255, 255, 0 });
        WebColors.NAMES.put("aquamarine", new int[] { 127, 255, 212, 0 });
        WebColors.NAMES.put("azure", new int[] { 240, 255, 255, 0 });
        WebColors.NAMES.put("beige", new int[] { 245, 245, 220, 0 });
        WebColors.NAMES.put("bisque", new int[] { 255, 228, 196, 0 });
        WebColors.NAMES.put("black", new int[] { 0, 0, 0, 0 });
        WebColors.NAMES.put("blanchedalmond", new int[] { 255, 235, 205, 0 });
        WebColors.NAMES.put("blue", new int[] { 0, 0, 255, 0 });
        WebColors.NAMES.put("blueviolet", new int[] { 138, 43, 226, 0 });
        WebColors.NAMES.put("brown", new int[] { 165, 42, 42, 0 });
        WebColors.NAMES.put("burlywood", new int[] { 222, 184, 135, 0 });
        WebColors.NAMES.put("cadetblue", new int[] { 95, 158, 160, 0 });
        WebColors.NAMES.put("chartreuse", new int[] { 127, 255, 0, 0 });
        WebColors.NAMES.put("chocolate", new int[] { 210, 105, 30, 0 });
        WebColors.NAMES.put("coral", new int[] { 255, 127, 80, 0 });
        WebColors.NAMES.put("cornflowerblue", new int[] { 100, 149, 237, 0 });
        WebColors.NAMES.put("cornsilk", new int[] { 255, 248, 220, 0 });
        WebColors.NAMES.put("crimson", new int[] { 220, 20, 60, 0 });
        WebColors.NAMES.put("cyan", new int[] { 0, 255, 255, 0 });
        WebColors.NAMES.put("darkblue", new int[] { 0, 0, 139, 0 });
        WebColors.NAMES.put("darkcyan", new int[] { 0, 139, 139, 0 });
        WebColors.NAMES.put("darkgoldenrod", new int[] { 184, 134, 11, 0 });
        WebColors.NAMES.put("darkgray", new int[] { 169, 169, 169, 0 });
        WebColors.NAMES.put("darkgreen", new int[] { 0, 100, 0, 0 });
        WebColors.NAMES.put("darkkhaki", new int[] { 189, 183, 107, 0 });
        WebColors.NAMES.put("darkmagenta", new int[] { 139, 0, 139, 0 });
        WebColors.NAMES.put("darkolivegreen", new int[] { 85, 107, 47, 0 });
        WebColors.NAMES.put("darkorange", new int[] { 255, 140, 0, 0 });
        WebColors.NAMES.put("darkorchid", new int[] { 153, 50, 204, 0 });
        WebColors.NAMES.put("darkred", new int[] { 139, 0, 0, 0 });
        WebColors.NAMES.put("darksalmon", new int[] { 233, 150, 122, 0 });
        WebColors.NAMES.put("darkseagreen", new int[] { 143, 188, 143, 0 });
        WebColors.NAMES.put("darkslateblue", new int[] { 72, 61, 139, 0 });
        WebColors.NAMES.put("darkslategray", new int[] { 47, 79, 79, 0 });
        WebColors.NAMES.put("darkturquoise", new int[] { 0, 206, 209, 0 });
        WebColors.NAMES.put("darkviolet", new int[] { 148, 0, 211, 0 });
        WebColors.NAMES.put("deeppink", new int[] { 255, 20, 147, 0 });
        WebColors.NAMES.put("deepskyblue", new int[] { 0, 191, 255, 0 });
        WebColors.NAMES.put("dimgray", new int[] { 105, 105, 105, 0 });
        WebColors.NAMES.put("dodgerblue", new int[] { 30, 144, 255, 0 });
        WebColors.NAMES.put("firebrick", new int[] { 178, 34, 34, 0 });
        WebColors.NAMES.put("floralwhite", new int[] { 255, 250, 240, 0 });
        WebColors.NAMES.put("forestgreen", new int[] { 34, 139, 34, 0 });
        WebColors.NAMES.put("fuchsia", new int[] { 255, 0, 255, 0 });
        WebColors.NAMES.put("gainsboro", new int[] { 220, 220, 220, 0 });
        WebColors.NAMES.put("ghostwhite", new int[] { 248, 248, 255, 0 });
        WebColors.NAMES.put("gold", new int[] { 255, 215, 0, 0 });
        WebColors.NAMES.put("goldenrod", new int[] { 218, 165, 32, 0 });
        WebColors.NAMES.put("gray", new int[] { 128, 128, 128, 0 });
        WebColors.NAMES.put("green", new int[] { 0, 128, 0, 0 });
        WebColors.NAMES.put("greenyellow", new int[] { 173, 255, 47, 0 });
        WebColors.NAMES.put("honeydew", new int[] { 240, 255, 240, 0 });
        WebColors.NAMES.put("hotpink", new int[] { 255, 105, 180, 0 });
        WebColors.NAMES.put("indianred", new int[] { 205, 92, 92, 0 });
        WebColors.NAMES.put("indigo", new int[] { 75, 0, 130, 0 });
        WebColors.NAMES.put("ivory", new int[] { 255, 255, 240, 0 });
        WebColors.NAMES.put("khaki", new int[] { 240, 230, 140, 0 });
        WebColors.NAMES.put("lavender", new int[] { 230, 230, 250, 0 });
        WebColors.NAMES.put("lavenderblush", new int[] { 255, 240, 245, 0 });
        WebColors.NAMES.put("lawngreen", new int[] { 124, 252, 0, 0 });
        WebColors.NAMES.put("lemonchiffon", new int[] { 255, 250, 205, 0 });
        WebColors.NAMES.put("lightblue", new int[] { 173, 216, 230, 0 });
        WebColors.NAMES.put("lightcoral", new int[] { 240, 128, 128, 0 });
        WebColors.NAMES.put("lightcyan", new int[] { 224, 255, 255, 0 });
        WebColors.NAMES.put("lightgoldenrodyellow", new int[] { 250, 250, 210, 0 });
        WebColors.NAMES.put("lightgreen", new int[] { 144, 238, 144, 0 });
        WebColors.NAMES.put("lightgrey", new int[] { 211, 211, 211, 0 });
        WebColors.NAMES.put("lightpink", new int[] { 255, 182, 193, 0 });
        WebColors.NAMES.put("lightsalmon", new int[] { 255, 160, 122, 0 });
        WebColors.NAMES.put("lightseagreen", new int[] { 32, 178, 170, 0 });
        WebColors.NAMES.put("lightskyblue", new int[] { 135, 206, 250, 0 });
        WebColors.NAMES.put("lightslategray", new int[] { 119, 136, 153, 0 });
        WebColors.NAMES.put("lightsteelblue", new int[] { 176, 196, 222, 0 });
        WebColors.NAMES.put("lightyellow", new int[] { 255, 255, 224, 0 });
        WebColors.NAMES.put("lime", new int[] { 0, 255, 0, 0 });
        WebColors.NAMES.put("limegreen", new int[] { 50, 205, 50, 0 });
        WebColors.NAMES.put("linen", new int[] { 250, 240, 230, 0 });
        WebColors.NAMES.put("magenta", new int[] { 255, 0, 255, 0 });
        WebColors.NAMES.put("maroon", new int[] { 128, 0, 0, 0 });
        WebColors.NAMES.put("mediumaquamarine", new int[] { 102, 205, 170, 0 });
        WebColors.NAMES.put("mediumblue", new int[] { 0, 0, 205, 0 });
        WebColors.NAMES.put("mediumorchid", new int[] { 186, 85, 211, 0 });
        WebColors.NAMES.put("mediumpurple", new int[] { 147, 112, 219, 0 });
        WebColors.NAMES.put("mediumseagreen", new int[] { 60, 179, 113, 0 });
        WebColors.NAMES.put("mediumslateblue", new int[] { 123, 104, 238, 0 });
        WebColors.NAMES.put("mediumspringgreen", new int[] { 0, 250, 154, 0 });
        WebColors.NAMES.put("mediumturquoise", new int[] { 72, 209, 204, 0 });
        WebColors.NAMES.put("mediumvioletred", new int[] { 199, 21, 133, 0 });
        WebColors.NAMES.put("midnightblue", new int[] { 25, 25, 112, 0 });
        WebColors.NAMES.put("mintcream", new int[] { 245, 255, 250, 0 });
        WebColors.NAMES.put("mistyrose", new int[] { 255, 228, 225, 0 });
        WebColors.NAMES.put("moccasin", new int[] { 255, 228, 181, 0 });
        WebColors.NAMES.put("navajowhite", new int[] { 255, 222, 173, 0 });
        WebColors.NAMES.put("navy", new int[] { 0, 0, 128, 0 });
        WebColors.NAMES.put("oldlace", new int[] { 253, 245, 230, 0 });
        WebColors.NAMES.put("olive", new int[] { 128, 128, 0, 0 });
        WebColors.NAMES.put("olivedrab", new int[] { 107, 142, 35, 0 });
        WebColors.NAMES.put("orange", new int[] { 255, 165, 0, 0 });
        WebColors.NAMES.put("orangered", new int[] { 255, 69, 0, 0 });
        WebColors.NAMES.put("orchid", new int[] { 218, 112, 214, 0 });
        WebColors.NAMES.put("palegoldenrod", new int[] { 238, 232, 170, 0 });
        WebColors.NAMES.put("palegreen", new int[] { 152, 251, 152, 0 });
        WebColors.NAMES.put("paleturquoise", new int[] { 175, 238, 238, 0 });
        WebColors.NAMES.put("palevioletred", new int[] { 219, 112, 147, 0 });
        WebColors.NAMES.put("papayawhip", new int[] { 255, 239, 213, 0 });
        WebColors.NAMES.put("peachpuff", new int[] { 255, 218, 185, 0 });
        WebColors.NAMES.put("peru", new int[] { 205, 133, 63, 0 });
        WebColors.NAMES.put("pink", new int[] { 255, 192, 203, 0 });
        WebColors.NAMES.put("plum", new int[] { 221, 160, 221, 0 });
        WebColors.NAMES.put("powderblue", new int[] { 176, 224, 230, 0 });
        WebColors.NAMES.put("purple", new int[] { 128, 0, 128, 0 });
        WebColors.NAMES.put("red", new int[] { 255, 0, 0, 0 });
        WebColors.NAMES.put("rosybrown", new int[] { 188, 143, 143, 0 });
        WebColors.NAMES.put("royalblue", new int[] { 65, 105, 225, 0 });
        WebColors.NAMES.put("saddlebrown", new int[] { 139, 69, 19, 0 });
        WebColors.NAMES.put("salmon", new int[] { 250, 128, 114, 0 });
        WebColors.NAMES.put("sandybrown", new int[] { 244, 164, 96, 0 });
        WebColors.NAMES.put("seagreen", new int[] { 46, 139, 87, 0 });
        WebColors.NAMES.put("seashell", new int[] { 255, 245, 238, 0 });
        WebColors.NAMES.put("sienna", new int[] { 160, 82, 45, 0 });
        WebColors.NAMES.put("silver", new int[] { 192, 192, 192, 0 });
        WebColors.NAMES.put("skyblue", new int[] { 135, 206, 235, 0 });
        WebColors.NAMES.put("slateblue", new int[] { 106, 90, 205, 0 });
        WebColors.NAMES.put("slategray", new int[] { 112, 128, 144, 0 });
        WebColors.NAMES.put("snow", new int[] { 255, 250, 250, 0 });
        WebColors.NAMES.put("springgreen", new int[] { 0, 255, 127, 0 });
        WebColors.NAMES.put("steelblue", new int[] { 70, 130, 180, 0 });
        WebColors.NAMES.put("tan", new int[] { 210, 180, 140, 0 });
        WebColors.NAMES.put("transparent", new int[] { 0, 0, 0, 255 });
        WebColors.NAMES.put("teal", new int[] { 0, 128, 128, 0 });
        WebColors.NAMES.put("thistle", new int[] { 216, 191, 216, 0 });
        WebColors.NAMES.put("tomato", new int[] { 255, 99, 71, 0 });
        WebColors.NAMES.put("turquoise", new int[] { 64, 224, 208, 0 });
        WebColors.NAMES.put("violet", new int[] { 238, 130, 238, 0 });
        WebColors.NAMES.put("wheat", new int[] { 245, 222, 179, 0 });
        WebColors.NAMES.put("white", new int[] { 255, 255, 255, 0 });
        WebColors.NAMES.put("whitesmoke", new int[] { 245, 245, 245, 0 });
        WebColors.NAMES.put("yellow", new int[] { 255, 255, 0, 0 });
        WebColors.NAMES.put("yellowgreen", new int[] { 9, 2765, 50, 0 });
    }
}
