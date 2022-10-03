package com.sun.java.swing.plaf.windows;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Color;
import sun.awt.windows.ThemeReader;
import java.util.EnumMap;
import javax.swing.JComponent;
import java.awt.Component;

class TMSchema
{
    public enum Control
    {
        BUTTON, 
        COMBOBOX, 
        EDIT, 
        HEADER, 
        LISTBOX, 
        LISTVIEW, 
        MENU, 
        PROGRESS, 
        REBAR, 
        SCROLLBAR, 
        SPIN, 
        TAB, 
        TOOLBAR, 
        TRACKBAR, 
        TREEVIEW, 
        WINDOW;
    }
    
    public enum Part
    {
        MENU(Control.MENU, 0), 
        MP_BARBACKGROUND(Control.MENU, 7), 
        MP_BARITEM(Control.MENU, 8), 
        MP_POPUPBACKGROUND(Control.MENU, 9), 
        MP_POPUPBORDERS(Control.MENU, 10), 
        MP_POPUPCHECK(Control.MENU, 11), 
        MP_POPUPCHECKBACKGROUND(Control.MENU, 12), 
        MP_POPUPGUTTER(Control.MENU, 13), 
        MP_POPUPITEM(Control.MENU, 14), 
        MP_POPUPSEPARATOR(Control.MENU, 15), 
        MP_POPUPSUBMENU(Control.MENU, 16), 
        BP_PUSHBUTTON(Control.BUTTON, 1), 
        BP_RADIOBUTTON(Control.BUTTON, 2), 
        BP_CHECKBOX(Control.BUTTON, 3), 
        BP_GROUPBOX(Control.BUTTON, 4), 
        CP_COMBOBOX(Control.COMBOBOX, 0), 
        CP_DROPDOWNBUTTON(Control.COMBOBOX, 1), 
        CP_BACKGROUND(Control.COMBOBOX, 2), 
        CP_TRANSPARENTBACKGROUND(Control.COMBOBOX, 3), 
        CP_BORDER(Control.COMBOBOX, 4), 
        CP_READONLY(Control.COMBOBOX, 5), 
        CP_DROPDOWNBUTTONRIGHT(Control.COMBOBOX, 6), 
        CP_DROPDOWNBUTTONLEFT(Control.COMBOBOX, 7), 
        CP_CUEBANNER(Control.COMBOBOX, 8), 
        EP_EDIT(Control.EDIT, 0), 
        EP_EDITTEXT(Control.EDIT, 1), 
        HP_HEADERITEM(Control.HEADER, 1), 
        HP_HEADERSORTARROW(Control.HEADER, 4), 
        LBP_LISTBOX(Control.LISTBOX, 0), 
        LVP_LISTVIEW(Control.LISTVIEW, 0), 
        PP_PROGRESS(Control.PROGRESS, 0), 
        PP_BAR(Control.PROGRESS, 1), 
        PP_BARVERT(Control.PROGRESS, 2), 
        PP_CHUNK(Control.PROGRESS, 3), 
        PP_CHUNKVERT(Control.PROGRESS, 4), 
        RP_GRIPPER(Control.REBAR, 1), 
        RP_GRIPPERVERT(Control.REBAR, 2), 
        SBP_SCROLLBAR(Control.SCROLLBAR, 0), 
        SBP_ARROWBTN(Control.SCROLLBAR, 1), 
        SBP_THUMBBTNHORZ(Control.SCROLLBAR, 2), 
        SBP_THUMBBTNVERT(Control.SCROLLBAR, 3), 
        SBP_LOWERTRACKHORZ(Control.SCROLLBAR, 4), 
        SBP_UPPERTRACKHORZ(Control.SCROLLBAR, 5), 
        SBP_LOWERTRACKVERT(Control.SCROLLBAR, 6), 
        SBP_UPPERTRACKVERT(Control.SCROLLBAR, 7), 
        SBP_GRIPPERHORZ(Control.SCROLLBAR, 8), 
        SBP_GRIPPERVERT(Control.SCROLLBAR, 9), 
        SBP_SIZEBOX(Control.SCROLLBAR, 10), 
        SPNP_UP(Control.SPIN, 1), 
        SPNP_DOWN(Control.SPIN, 2), 
        TABP_TABITEM(Control.TAB, 1), 
        TABP_TABITEMLEFTEDGE(Control.TAB, 2), 
        TABP_TABITEMRIGHTEDGE(Control.TAB, 3), 
        TABP_PANE(Control.TAB, 9), 
        TP_TOOLBAR(Control.TOOLBAR, 0), 
        TP_BUTTON(Control.TOOLBAR, 1), 
        TP_SEPARATOR(Control.TOOLBAR, 5), 
        TP_SEPARATORVERT(Control.TOOLBAR, 6), 
        TKP_TRACK(Control.TRACKBAR, 1), 
        TKP_TRACKVERT(Control.TRACKBAR, 2), 
        TKP_THUMB(Control.TRACKBAR, 3), 
        TKP_THUMBBOTTOM(Control.TRACKBAR, 4), 
        TKP_THUMBTOP(Control.TRACKBAR, 5), 
        TKP_THUMBVERT(Control.TRACKBAR, 6), 
        TKP_THUMBLEFT(Control.TRACKBAR, 7), 
        TKP_THUMBRIGHT(Control.TRACKBAR, 8), 
        TKP_TICS(Control.TRACKBAR, 9), 
        TKP_TICSVERT(Control.TRACKBAR, 10), 
        TVP_TREEVIEW(Control.TREEVIEW, 0), 
        TVP_GLYPH(Control.TREEVIEW, 2), 
        WP_WINDOW(Control.WINDOW, 0), 
        WP_CAPTION(Control.WINDOW, 1), 
        WP_MINCAPTION(Control.WINDOW, 3), 
        WP_MAXCAPTION(Control.WINDOW, 5), 
        WP_FRAMELEFT(Control.WINDOW, 7), 
        WP_FRAMERIGHT(Control.WINDOW, 8), 
        WP_FRAMEBOTTOM(Control.WINDOW, 9), 
        WP_SYSBUTTON(Control.WINDOW, 13), 
        WP_MDISYSBUTTON(Control.WINDOW, 14), 
        WP_MINBUTTON(Control.WINDOW, 15), 
        WP_MDIMINBUTTON(Control.WINDOW, 16), 
        WP_MAXBUTTON(Control.WINDOW, 17), 
        WP_CLOSEBUTTON(Control.WINDOW, 18), 
        WP_MDICLOSEBUTTON(Control.WINDOW, 20), 
        WP_RESTOREBUTTON(Control.WINDOW, 21), 
        WP_MDIRESTOREBUTTON(Control.WINDOW, 22);
        
        private final Control control;
        private final int value;
        
        private Part(final Control control, final int value) {
            this.control = control;
            this.value = value;
        }
        
        public int getValue() {
            return this.value;
        }
        
        public String getControlName(final Component component) {
            String string = "";
            if (component instanceof JComponent) {
                final String s = (String)((JComponent)component).getClientProperty("XPStyle.subAppName");
                if (s != null) {
                    string = s + "::";
                }
            }
            return string + this.control.toString();
        }
        
        @Override
        public String toString() {
            return this.control.toString() + "." + this.name();
        }
    }
    
    public enum State
    {
        ACTIVE, 
        ASSIST, 
        BITMAP, 
        CHECKED, 
        CHECKEDDISABLED, 
        CHECKEDHOT, 
        CHECKEDNORMAL, 
        CHECKEDPRESSED, 
        CHECKMARKNORMAL, 
        CHECKMARKDISABLED, 
        BULLETNORMAL, 
        BULLETDISABLED, 
        CLOSED, 
        DEFAULTED, 
        DISABLED, 
        DISABLEDHOT, 
        DISABLEDPUSHED, 
        DOWNDISABLED, 
        DOWNHOT, 
        DOWNNORMAL, 
        DOWNPRESSED, 
        FOCUSED, 
        HOT, 
        HOTCHECKED, 
        ICONHOT, 
        ICONNORMAL, 
        ICONPRESSED, 
        ICONSORTEDHOT, 
        ICONSORTEDNORMAL, 
        ICONSORTEDPRESSED, 
        INACTIVE, 
        INACTIVENORMAL, 
        INACTIVEHOT, 
        INACTIVEPUSHED, 
        INACTIVEDISABLED, 
        LEFTDISABLED, 
        LEFTHOT, 
        LEFTNORMAL, 
        LEFTPRESSED, 
        MIXEDDISABLED, 
        MIXEDHOT, 
        MIXEDNORMAL, 
        MIXEDPRESSED, 
        NORMAL, 
        PRESSED, 
        OPENED, 
        PUSHED, 
        READONLY, 
        RIGHTDISABLED, 
        RIGHTHOT, 
        RIGHTNORMAL, 
        RIGHTPRESSED, 
        SELECTED, 
        UNCHECKEDDISABLED, 
        UNCHECKEDHOT, 
        UNCHECKEDNORMAL, 
        UNCHECKEDPRESSED, 
        UPDISABLED, 
        UPHOT, 
        UPNORMAL, 
        UPPRESSED, 
        HOVER, 
        UPHOVER, 
        DOWNHOVER, 
        LEFTHOVER, 
        RIGHTHOVER, 
        SORTEDDOWN, 
        SORTEDHOT, 
        SORTEDNORMAL, 
        SORTEDPRESSED, 
        SORTEDUP;
        
        private static EnumMap<Part, State[]> stateMap;
        
        private static synchronized void initStates() {
            (State.stateMap = new EnumMap<Part, State[]>(Part.class)).put(Part.EP_EDITTEXT, new State[] { State.NORMAL, State.HOT, State.SELECTED, State.DISABLED, State.FOCUSED, State.READONLY, State.ASSIST });
            State.stateMap.put(Part.BP_PUSHBUTTON, new State[] { State.NORMAL, State.HOT, State.PRESSED, State.DISABLED, State.DEFAULTED });
            State.stateMap.put(Part.BP_RADIOBUTTON, new State[] { State.UNCHECKEDNORMAL, State.UNCHECKEDHOT, State.UNCHECKEDPRESSED, State.UNCHECKEDDISABLED, State.CHECKEDNORMAL, State.CHECKEDHOT, State.CHECKEDPRESSED, State.CHECKEDDISABLED });
            State.stateMap.put(Part.BP_CHECKBOX, new State[] { State.UNCHECKEDNORMAL, State.UNCHECKEDHOT, State.UNCHECKEDPRESSED, State.UNCHECKEDDISABLED, State.CHECKEDNORMAL, State.CHECKEDHOT, State.CHECKEDPRESSED, State.CHECKEDDISABLED, State.MIXEDNORMAL, State.MIXEDHOT, State.MIXEDPRESSED, State.MIXEDDISABLED });
            final State[] array = { State.NORMAL, State.HOT, State.PRESSED, State.DISABLED };
            State.stateMap.put(Part.CP_COMBOBOX, array);
            State.stateMap.put(Part.CP_DROPDOWNBUTTON, array);
            State.stateMap.put(Part.CP_BACKGROUND, array);
            State.stateMap.put(Part.CP_TRANSPARENTBACKGROUND, array);
            State.stateMap.put(Part.CP_BORDER, array);
            State.stateMap.put(Part.CP_READONLY, array);
            State.stateMap.put(Part.CP_DROPDOWNBUTTONRIGHT, array);
            State.stateMap.put(Part.CP_DROPDOWNBUTTONLEFT, array);
            State.stateMap.put(Part.CP_CUEBANNER, array);
            State.stateMap.put(Part.HP_HEADERITEM, new State[] { State.NORMAL, State.HOT, State.PRESSED, State.SORTEDNORMAL, State.SORTEDHOT, State.SORTEDPRESSED, State.ICONNORMAL, State.ICONHOT, State.ICONPRESSED, State.ICONSORTEDNORMAL, State.ICONSORTEDHOT, State.ICONSORTEDPRESSED });
            State.stateMap.put(Part.HP_HEADERSORTARROW, new State[] { State.SORTEDDOWN, State.SORTEDUP });
            final State[] array2 = { State.NORMAL, State.HOT, State.PRESSED, State.DISABLED, State.HOVER };
            State.stateMap.put(Part.SBP_SCROLLBAR, array2);
            State.stateMap.put(Part.SBP_THUMBBTNVERT, array2);
            State.stateMap.put(Part.SBP_THUMBBTNHORZ, array2);
            State.stateMap.put(Part.SBP_GRIPPERVERT, array2);
            State.stateMap.put(Part.SBP_GRIPPERHORZ, array2);
            State.stateMap.put(Part.SBP_ARROWBTN, new State[] { State.UPNORMAL, State.UPHOT, State.UPPRESSED, State.UPDISABLED, State.DOWNNORMAL, State.DOWNHOT, State.DOWNPRESSED, State.DOWNDISABLED, State.LEFTNORMAL, State.LEFTHOT, State.LEFTPRESSED, State.LEFTDISABLED, State.RIGHTNORMAL, State.RIGHTHOT, State.RIGHTPRESSED, State.RIGHTDISABLED, State.UPHOVER, State.DOWNHOVER, State.LEFTHOVER, State.RIGHTHOVER });
            final State[] array3 = { State.NORMAL, State.HOT, State.PRESSED, State.DISABLED };
            State.stateMap.put(Part.SPNP_UP, array3);
            State.stateMap.put(Part.SPNP_DOWN, array3);
            State.stateMap.put(Part.TVP_GLYPH, new State[] { State.CLOSED, State.OPENED });
            State[] array4 = { State.NORMAL, State.HOT, State.PUSHED, State.DISABLED, State.INACTIVENORMAL, State.INACTIVEHOT, State.INACTIVEPUSHED, State.INACTIVEDISABLED };
            if (ThemeReader.getInt(Control.WINDOW.toString(), Part.WP_CLOSEBUTTON.getValue(), 1, Prop.IMAGECOUNT.getValue()) == 10) {
                array4 = new State[] { State.NORMAL, State.HOT, State.PUSHED, State.DISABLED, null, State.INACTIVENORMAL, State.INACTIVEHOT, State.INACTIVEPUSHED, State.INACTIVEDISABLED, null };
            }
            State.stateMap.put(Part.WP_MINBUTTON, array4);
            State.stateMap.put(Part.WP_MAXBUTTON, array4);
            State.stateMap.put(Part.WP_RESTOREBUTTON, array4);
            State.stateMap.put(Part.WP_CLOSEBUTTON, array4);
            State.stateMap.put(Part.TKP_TRACK, new State[] { State.NORMAL });
            State.stateMap.put(Part.TKP_TRACKVERT, new State[] { State.NORMAL });
            final State[] array5 = { State.NORMAL, State.HOT, State.PRESSED, State.FOCUSED, State.DISABLED };
            State.stateMap.put(Part.TKP_THUMB, array5);
            State.stateMap.put(Part.TKP_THUMBBOTTOM, array5);
            State.stateMap.put(Part.TKP_THUMBTOP, array5);
            State.stateMap.put(Part.TKP_THUMBVERT, array5);
            State.stateMap.put(Part.TKP_THUMBRIGHT, array5);
            final State[] array6 = { State.NORMAL, State.HOT, State.SELECTED, State.DISABLED, State.FOCUSED };
            State.stateMap.put(Part.TABP_TABITEM, array6);
            State.stateMap.put(Part.TABP_TABITEMLEFTEDGE, array6);
            State.stateMap.put(Part.TABP_TABITEMRIGHTEDGE, array6);
            State.stateMap.put(Part.TP_BUTTON, new State[] { State.NORMAL, State.HOT, State.PRESSED, State.DISABLED, State.CHECKED, State.HOTCHECKED });
            final State[] array7 = { State.ACTIVE, State.INACTIVE };
            State.stateMap.put(Part.WP_WINDOW, array7);
            State.stateMap.put(Part.WP_FRAMELEFT, array7);
            State.stateMap.put(Part.WP_FRAMERIGHT, array7);
            State.stateMap.put(Part.WP_FRAMEBOTTOM, array7);
            final State[] array8 = { State.ACTIVE, State.INACTIVE, State.DISABLED };
            State.stateMap.put(Part.WP_CAPTION, array8);
            State.stateMap.put(Part.WP_MINCAPTION, array8);
            State.stateMap.put(Part.WP_MAXCAPTION, array8);
            State.stateMap.put(Part.MP_BARBACKGROUND, new State[] { State.ACTIVE, State.INACTIVE });
            State.stateMap.put(Part.MP_BARITEM, new State[] { State.NORMAL, State.HOT, State.PUSHED, State.DISABLED, State.DISABLEDHOT, State.DISABLEDPUSHED });
            State.stateMap.put(Part.MP_POPUPCHECK, new State[] { State.CHECKMARKNORMAL, State.CHECKMARKDISABLED, State.BULLETNORMAL, State.BULLETDISABLED });
            State.stateMap.put(Part.MP_POPUPCHECKBACKGROUND, new State[] { State.DISABLEDPUSHED, State.NORMAL, State.BITMAP });
            State.stateMap.put(Part.MP_POPUPITEM, new State[] { State.NORMAL, State.HOT, State.DISABLED, State.DISABLEDHOT });
            State.stateMap.put(Part.MP_POPUPSUBMENU, new State[] { State.NORMAL, State.DISABLED });
        }
        
        public static synchronized int getValue(final Part part, final State state) {
            if (State.stateMap == null) {
                initStates();
            }
            final Enum[] array = State.stateMap.get(part);
            if (array != null) {
                for (int i = 0; i < array.length; ++i) {
                    if (state == array[i]) {
                        return i + 1;
                    }
                }
            }
            if (state == null || state == State.NORMAL) {
                return 1;
            }
            return 0;
        }
    }
    
    public enum Prop
    {
        COLOR((Class)Color.class, 204), 
        SIZE((Class)Dimension.class, 207), 
        FLATMENUS((Class)Boolean.class, 1001), 
        BORDERONLY((Class)Boolean.class, 2203), 
        IMAGECOUNT((Class)Integer.class, 2401), 
        BORDERSIZE((Class)Integer.class, 2403), 
        PROGRESSCHUNKSIZE((Class)Integer.class, 2411), 
        PROGRESSSPACESIZE((Class)Integer.class, 2412), 
        TEXTSHADOWOFFSET((Class)Point.class, 3402), 
        NORMALSIZE((Class)Dimension.class, 3409), 
        SIZINGMARGINS((Class)Insets.class, 3601), 
        CONTENTMARGINS((Class)Insets.class, 3602), 
        CAPTIONMARGINS((Class)Insets.class, 3603), 
        BORDERCOLOR((Class)Color.class, 3801), 
        FILLCOLOR((Class)Color.class, 3802), 
        TEXTCOLOR((Class)Color.class, 3803), 
        TEXTSHADOWCOLOR((Class)Color.class, 3818), 
        BGTYPE((Class)Integer.class, 4001), 
        TEXTSHADOWTYPE((Class)Integer.class, 4010), 
        TRANSITIONDURATIONS((Class)Integer.class, 6000);
        
        private final Class type;
        private final int value;
        
        private Prop(final Class type, final int value) {
            this.type = type;
            this.value = value;
        }
        
        public int getValue() {
            return this.value;
        }
        
        @Override
        public String toString() {
            return this.name() + "[" + this.type.getName() + "] = " + this.value;
        }
    }
    
    public enum TypeEnum
    {
        BT_IMAGEFILE(Prop.BGTYPE, "imagefile", 0), 
        BT_BORDERFILL(Prop.BGTYPE, "borderfill", 1), 
        TST_NONE(Prop.TEXTSHADOWTYPE, "none", 0), 
        TST_SINGLE(Prop.TEXTSHADOWTYPE, "single", 1), 
        TST_CONTINUOUS(Prop.TEXTSHADOWTYPE, "continuous", 2);
        
        private final Prop prop;
        private final String enumName;
        private final int value;
        
        private TypeEnum(final Prop prop, final String enumName, final int value) {
            this.prop = prop;
            this.enumName = enumName;
            this.value = value;
        }
        
        @Override
        public String toString() {
            return this.prop + "=" + this.enumName + "=" + this.value;
        }
        
        String getName() {
            return this.enumName;
        }
        
        static TypeEnum getTypeEnum(final Prop prop, final int n) {
            for (final TypeEnum typeEnum : values()) {
                if (typeEnum.prop == prop && typeEnum.value == n) {
                    return typeEnum;
                }
            }
            return null;
        }
    }
}
