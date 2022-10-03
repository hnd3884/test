package javax.swing.plaf.synth;

import java.util.Iterator;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;
import sun.swing.plaf.synth.DefaultSynthStyle;

class ParsedSynthStyle extends DefaultSynthStyle
{
    private static SynthPainter DELEGATING_PAINTER_INSTANCE;
    private PainterInfo[] _painters;
    
    private static PainterInfo[] mergePainterInfo(final PainterInfo[] array, final PainterInfo[] array2) {
        if (array == null) {
            return array2;
        }
        if (array2 == null) {
            return array;
        }
        final int length = array.length;
        final int length2 = array2.length;
        int n = 0;
        PainterInfo[] array3 = new PainterInfo[length + length2];
        System.arraycopy(array, 0, array3, 0, length);
        for (int i = 0; i < length2; ++i) {
            boolean b = false;
            for (int j = 0; j < length - n; ++j) {
                if (array2[i].equalsPainter(array[j])) {
                    array3[j] = array2[i];
                    ++n;
                    b = true;
                    break;
                }
            }
            if (!b) {
                array3[length + i - n] = array2[i];
            }
        }
        if (n > 0) {
            final PainterInfo[] array4 = array3;
            array3 = new PainterInfo[array3.length - n];
            System.arraycopy(array4, 0, array3, 0, array3.length);
        }
        return array3;
    }
    
    public ParsedSynthStyle() {
    }
    
    public ParsedSynthStyle(final DefaultSynthStyle defaultSynthStyle) {
        super(defaultSynthStyle);
        if (defaultSynthStyle instanceof ParsedSynthStyle) {
            final ParsedSynthStyle parsedSynthStyle = (ParsedSynthStyle)defaultSynthStyle;
            if (parsedSynthStyle._painters != null) {
                this._painters = parsedSynthStyle._painters;
            }
        }
    }
    
    @Override
    public SynthPainter getPainter(final SynthContext synthContext) {
        return ParsedSynthStyle.DELEGATING_PAINTER_INSTANCE;
    }
    
    public void setPainters(final PainterInfo[] painters) {
        this._painters = painters;
    }
    
    @Override
    public DefaultSynthStyle addTo(DefaultSynthStyle defaultSynthStyle) {
        if (!(defaultSynthStyle instanceof ParsedSynthStyle)) {
            defaultSynthStyle = new ParsedSynthStyle(defaultSynthStyle);
        }
        final ParsedSynthStyle parsedSynthStyle = (ParsedSynthStyle)super.addTo(defaultSynthStyle);
        parsedSynthStyle._painters = mergePainterInfo(parsedSynthStyle._painters, this._painters);
        return parsedSynthStyle;
    }
    
    private SynthPainter getBestPainter(final SynthContext synthContext, final String s, final int n) {
        final StateInfo stateInfo = (StateInfo)this.getStateInfo(synthContext.getComponentState());
        final SynthPainter bestPainter;
        if (stateInfo != null && (bestPainter = this.getBestPainter(stateInfo.getPainters(), s, n)) != null) {
            return bestPainter;
        }
        final SynthPainter bestPainter2;
        if ((bestPainter2 = this.getBestPainter(this._painters, s, n)) != null) {
            return bestPainter2;
        }
        return SynthPainter.NULL_PAINTER;
    }
    
    private SynthPainter getBestPainter(final PainterInfo[] array, final String s, final int n) {
        if (array == null) {
            return null;
        }
        SynthPainter painter = null;
        SynthPainter painter2 = null;
        for (int i = array.length - 1; i >= 0; --i) {
            final PainterInfo painterInfo = array[i];
            if (painterInfo.getMethod() == s) {
                if (painterInfo.getDirection() == n) {
                    return painterInfo.getPainter();
                }
                if (painter2 == null && painterInfo.getDirection() == -1) {
                    painter2 = painterInfo.getPainter();
                }
            }
            else if (painter == null && painterInfo.getMethod() == null) {
                painter = painterInfo.getPainter();
            }
        }
        if (painter2 != null) {
            return painter2;
        }
        return painter;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(super.toString());
        if (this._painters != null) {
            sb.append(",painters=[");
            for (int i = 0; i < this._painters.length; ++i) {
                sb.append(this._painters[i].toString());
            }
            sb.append("]");
        }
        return sb.toString();
    }
    
    static {
        ParsedSynthStyle.DELEGATING_PAINTER_INSTANCE = new DelegatingPainter();
    }
    
    static class StateInfo extends DefaultSynthStyle.StateInfo
    {
        private PainterInfo[] _painterInfo;
        
        public StateInfo() {
        }
        
        public StateInfo(final DefaultSynthStyle.StateInfo stateInfo) {
            super(stateInfo);
            if (stateInfo instanceof StateInfo) {
                this._painterInfo = ((StateInfo)stateInfo)._painterInfo;
            }
        }
        
        public void setPainters(final PainterInfo[] painterInfo) {
            this._painterInfo = painterInfo;
        }
        
        public PainterInfo[] getPainters() {
            return this._painterInfo;
        }
        
        @Override
        public Object clone() {
            return new StateInfo(this);
        }
        
        @Override
        public DefaultSynthStyle.StateInfo addTo(DefaultSynthStyle.StateInfo addTo) {
            if (!(addTo instanceof StateInfo)) {
                addTo = new StateInfo(addTo);
            }
            else {
                addTo = super.addTo(addTo);
                final StateInfo stateInfo = (StateInfo)addTo;
                stateInfo._painterInfo = mergePainterInfo(stateInfo._painterInfo, this._painterInfo);
            }
            return addTo;
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer(super.toString());
            sb.append(",painters=[");
            if (this._painterInfo != null) {
                for (int i = 0; i < this._painterInfo.length; ++i) {
                    sb.append("    ").append(this._painterInfo[i].toString());
                }
            }
            sb.append("]");
            return sb.toString();
        }
    }
    
    static class PainterInfo
    {
        private String _method;
        private SynthPainter _painter;
        private int _direction;
        
        PainterInfo(final String s, final SynthPainter painter, final int direction) {
            if (s != null) {
                this._method = s.intern();
            }
            this._painter = painter;
            this._direction = direction;
        }
        
        void addPainter(final SynthPainter synthPainter) {
            if (!(this._painter instanceof AggregatePainter)) {
                this._painter = new AggregatePainter(this._painter);
            }
            ((AggregatePainter)this._painter).addPainter(synthPainter);
        }
        
        String getMethod() {
            return this._method;
        }
        
        SynthPainter getPainter() {
            return this._painter;
        }
        
        int getDirection() {
            return this._direction;
        }
        
        boolean equalsPainter(final PainterInfo painterInfo) {
            return this._method == painterInfo._method && this._direction == painterInfo._direction;
        }
        
        @Override
        public String toString() {
            return "PainterInfo {method=" + this._method + ",direction=" + this._direction + ",painter=" + this._painter + "}";
        }
    }
    
    private static class AggregatePainter extends SynthPainter
    {
        private List<SynthPainter> painters;
        
        AggregatePainter(final SynthPainter synthPainter) {
            (this.painters = new LinkedList<SynthPainter>()).add(synthPainter);
        }
        
        void addPainter(final SynthPainter synthPainter) {
            if (synthPainter != null) {
                this.painters.add(synthPainter);
            }
        }
        
        @Override
        public void paintArrowButtonBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintArrowButtonBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintArrowButtonBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintArrowButtonBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintArrowButtonForeground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintArrowButtonForeground(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintButtonBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintButtonBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintButtonBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintButtonBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintCheckBoxMenuItemBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintCheckBoxMenuItemBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintCheckBoxMenuItemBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintCheckBoxMenuItemBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintCheckBoxBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintCheckBoxBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintCheckBoxBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintCheckBoxBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintColorChooserBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintColorChooserBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintColorChooserBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintColorChooserBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintComboBoxBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintComboBoxBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintComboBoxBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintComboBoxBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintDesktopIconBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintDesktopIconBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintDesktopIconBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintDesktopIconBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintDesktopPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintDesktopPaneBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintDesktopPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintDesktopPaneBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintEditorPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintEditorPaneBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintEditorPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintEditorPaneBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintFileChooserBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintFileChooserBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintFileChooserBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintFileChooserBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintFormattedTextFieldBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintFormattedTextFieldBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintFormattedTextFieldBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintFormattedTextFieldBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintInternalFrameTitlePaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintInternalFrameTitlePaneBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintInternalFrameTitlePaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintInternalFrameTitlePaneBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintInternalFrameBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintInternalFrameBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintInternalFrameBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintInternalFrameBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintLabelBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintLabelBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintLabelBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintLabelBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintListBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintListBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintListBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintListBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintMenuBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintMenuBarBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintMenuBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintMenuBarBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintMenuItemBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintMenuItemBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintMenuItemBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintMenuItemBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintMenuBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintMenuBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintMenuBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintMenuBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintOptionPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintOptionPaneBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintOptionPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintOptionPaneBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintPanelBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintPanelBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintPanelBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintPanelBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintPasswordFieldBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintPasswordFieldBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintPasswordFieldBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintPasswordFieldBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintPopupMenuBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintPopupMenuBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintPopupMenuBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintPopupMenuBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintProgressBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintProgressBarBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintProgressBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintProgressBarBackground(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintProgressBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintProgressBarBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintProgressBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintProgressBarBorder(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintProgressBarForeground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintProgressBarForeground(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintRadioButtonMenuItemBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintRadioButtonMenuItemBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintRadioButtonMenuItemBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintRadioButtonMenuItemBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintRadioButtonBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintRadioButtonBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintRadioButtonBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintRadioButtonBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintRootPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintRootPaneBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintRootPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintRootPaneBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintScrollBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintScrollBarBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintScrollBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintScrollBarBackground(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintScrollBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintScrollBarBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintScrollBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintScrollBarBorder(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintScrollBarThumbBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintScrollBarThumbBackground(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintScrollBarThumbBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintScrollBarThumbBorder(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintScrollBarTrackBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintScrollBarTrackBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintScrollBarTrackBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintScrollBarTrackBackground(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintScrollBarTrackBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintScrollBarTrackBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintScrollBarTrackBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintScrollBarTrackBorder(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintScrollPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintScrollPaneBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintScrollPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintScrollPaneBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintSeparatorBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSeparatorBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintSeparatorBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSeparatorBackground(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintSeparatorBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSeparatorBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintSeparatorBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSeparatorBorder(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintSeparatorForeground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSeparatorForeground(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintSliderBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSliderBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintSliderBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSliderBackground(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintSliderBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSliderBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintSliderBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSliderBorder(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintSliderThumbBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSliderThumbBackground(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintSliderThumbBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSliderThumbBorder(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintSliderTrackBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSliderTrackBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintSliderTrackBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSliderTrackBackground(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintSliderTrackBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSliderTrackBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintSliderTrackBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSliderTrackBorder(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintSpinnerBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSpinnerBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintSpinnerBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSpinnerBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintSplitPaneDividerBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSplitPaneDividerBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintSplitPaneDividerBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSplitPaneDividerBackground(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintSplitPaneDividerForeground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSplitPaneDividerForeground(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintSplitPaneDragDivider(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSplitPaneDragDivider(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintSplitPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSplitPaneBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintSplitPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintSplitPaneBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTabbedPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTabbedPaneBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTabbedPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTabbedPaneBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTabbedPaneTabAreaBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTabbedPaneTabAreaBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTabbedPaneTabAreaBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTabbedPaneTabAreaBackground(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintTabbedPaneTabAreaBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTabbedPaneTabAreaBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTabbedPaneTabAreaBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTabbedPaneTabAreaBorder(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintTabbedPaneTabBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTabbedPaneTabBackground(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintTabbedPaneTabBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTabbedPaneTabBackground(synthContext, graphics, n, n2, n3, n4, n5, n6);
            }
        }
        
        @Override
        public void paintTabbedPaneTabBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTabbedPaneTabBorder(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintTabbedPaneTabBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTabbedPaneTabBorder(synthContext, graphics, n, n2, n3, n4, n5, n6);
            }
        }
        
        @Override
        public void paintTabbedPaneContentBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTabbedPaneContentBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTabbedPaneContentBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTabbedPaneContentBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTableHeaderBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTableHeaderBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTableHeaderBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTableHeaderBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTableBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTableBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTableBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTableBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTextAreaBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTextAreaBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTextAreaBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTextAreaBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTextPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTextPaneBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTextPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTextPaneBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTextFieldBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTextFieldBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTextFieldBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTextFieldBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintToggleButtonBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintToggleButtonBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintToggleButtonBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintToggleButtonBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintToolBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintToolBarBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintToolBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintToolBarBackground(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintToolBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintToolBarBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintToolBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintToolBarBorder(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintToolBarContentBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintToolBarContentBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintToolBarContentBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintToolBarContentBackground(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintToolBarContentBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintToolBarContentBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintToolBarContentBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintToolBarContentBorder(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintToolBarDragWindowBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintToolBarDragWindowBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintToolBarDragWindowBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintToolBarDragWindowBackground(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintToolBarDragWindowBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintToolBarDragWindowBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintToolBarDragWindowBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintToolBarDragWindowBorder(synthContext, graphics, n, n2, n3, n4, n5);
            }
        }
        
        @Override
        public void paintToolTipBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintToolTipBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintToolTipBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintToolTipBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTreeBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTreeBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTreeBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTreeBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTreeCellBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTreeCellBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTreeCellBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTreeCellBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintTreeCellFocus(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintTreeCellFocus(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintViewportBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintViewportBackground(synthContext, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public void paintViewportBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Iterator<SynthPainter> iterator = this.painters.iterator();
            while (iterator.hasNext()) {
                iterator.next().paintViewportBorder(synthContext, graphics, n, n2, n3, n4);
            }
        }
    }
    
    private static class DelegatingPainter extends SynthPainter
    {
        private static SynthPainter getPainter(final SynthContext synthContext, final String s, final int n) {
            return ((ParsedSynthStyle)synthContext.getStyle()).getBestPainter(synthContext, s, n);
        }
        
        @Override
        public void paintArrowButtonBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "arrowbuttonbackground", -1).paintArrowButtonBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintArrowButtonBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "arrowbuttonborder", -1).paintArrowButtonBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintArrowButtonForeground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "arrowbuttonforeground", n5).paintArrowButtonForeground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintButtonBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "buttonbackground", -1).paintButtonBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintButtonBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "buttonborder", -1).paintButtonBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintCheckBoxMenuItemBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "checkboxmenuitembackground", -1).paintCheckBoxMenuItemBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintCheckBoxMenuItemBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "checkboxmenuitemborder", -1).paintCheckBoxMenuItemBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintCheckBoxBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "checkboxbackground", -1).paintCheckBoxBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintCheckBoxBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "checkboxborder", -1).paintCheckBoxBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintColorChooserBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "colorchooserbackground", -1).paintColorChooserBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintColorChooserBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "colorchooserborder", -1).paintColorChooserBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintComboBoxBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "comboboxbackground", -1).paintComboBoxBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintComboBoxBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "comboboxborder", -1).paintComboBoxBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintDesktopIconBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "desktopiconbackground", -1).paintDesktopIconBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintDesktopIconBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "desktopiconborder", -1).paintDesktopIconBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintDesktopPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "desktoppanebackground", -1).paintDesktopPaneBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintDesktopPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "desktoppaneborder", -1).paintDesktopPaneBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintEditorPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "editorpanebackground", -1).paintEditorPaneBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintEditorPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "editorpaneborder", -1).paintEditorPaneBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintFileChooserBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "filechooserbackground", -1).paintFileChooserBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintFileChooserBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "filechooserborder", -1).paintFileChooserBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintFormattedTextFieldBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "formattedtextfieldbackground", -1).paintFormattedTextFieldBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintFormattedTextFieldBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "formattedtextfieldborder", -1).paintFormattedTextFieldBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintInternalFrameTitlePaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "internalframetitlepanebackground", -1).paintInternalFrameTitlePaneBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintInternalFrameTitlePaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "internalframetitlepaneborder", -1).paintInternalFrameTitlePaneBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintInternalFrameBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "internalframebackground", -1).paintInternalFrameBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintInternalFrameBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "internalframeborder", -1).paintInternalFrameBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintLabelBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "labelbackground", -1).paintLabelBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintLabelBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "labelborder", -1).paintLabelBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintListBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "listbackground", -1).paintListBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintListBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "listborder", -1).paintListBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintMenuBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "menubarbackground", -1).paintMenuBarBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintMenuBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "menubarborder", -1).paintMenuBarBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintMenuItemBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "menuitembackground", -1).paintMenuItemBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintMenuItemBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "menuitemborder", -1).paintMenuItemBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintMenuBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "menubackground", -1).paintMenuBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintMenuBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "menuborder", -1).paintMenuBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintOptionPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "optionpanebackground", -1).paintOptionPaneBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintOptionPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "optionpaneborder", -1).paintOptionPaneBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintPanelBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "panelbackground", -1).paintPanelBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintPanelBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "panelborder", -1).paintPanelBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintPasswordFieldBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "passwordfieldbackground", -1).paintPasswordFieldBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintPasswordFieldBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "passwordfieldborder", -1).paintPasswordFieldBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintPopupMenuBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "popupmenubackground", -1).paintPopupMenuBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintPopupMenuBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "popupmenuborder", -1).paintPopupMenuBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintProgressBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "progressbarbackground", -1).paintProgressBarBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintProgressBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "progressbarbackground", n5).paintProgressBarBackground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintProgressBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "progressbarborder", -1).paintProgressBarBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintProgressBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "progressbarborder", n5).paintProgressBarBorder(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintProgressBarForeground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "progressbarforeground", n5).paintProgressBarForeground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintRadioButtonMenuItemBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "radiobuttonmenuitembackground", -1).paintRadioButtonMenuItemBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintRadioButtonMenuItemBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "radiobuttonmenuitemborder", -1).paintRadioButtonMenuItemBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintRadioButtonBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "radiobuttonbackground", -1).paintRadioButtonBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintRadioButtonBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "radiobuttonborder", -1).paintRadioButtonBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintRootPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "rootpanebackground", -1).paintRootPaneBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintRootPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "rootpaneborder", -1).paintRootPaneBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintScrollBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "scrollbarbackground", -1).paintScrollBarBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintScrollBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "scrollbarbackground", n5).paintScrollBarBackground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintScrollBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "scrollbarborder", -1).paintScrollBarBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintScrollBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "scrollbarborder", n5).paintScrollBarBorder(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintScrollBarThumbBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "scrollbarthumbbackground", n5).paintScrollBarThumbBackground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintScrollBarThumbBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "scrollbarthumbborder", n5).paintScrollBarThumbBorder(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintScrollBarTrackBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "scrollbartrackbackground", -1).paintScrollBarTrackBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintScrollBarTrackBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "scrollbartrackbackground", n5).paintScrollBarTrackBackground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintScrollBarTrackBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "scrollbartrackborder", -1).paintScrollBarTrackBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintScrollBarTrackBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "scrollbartrackborder", n5).paintScrollBarTrackBorder(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintScrollPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "scrollpanebackground", -1).paintScrollPaneBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintScrollPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "scrollpaneborder", -1).paintScrollPaneBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintSeparatorBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "separatorbackground", -1).paintSeparatorBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintSeparatorBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "separatorbackground", n5).paintSeparatorBackground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintSeparatorBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "separatorborder", -1).paintSeparatorBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintSeparatorBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "separatorborder", n5).paintSeparatorBorder(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintSeparatorForeground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "separatorforeground", n5).paintSeparatorForeground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintSliderBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "sliderbackground", -1).paintSliderBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintSliderBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "sliderbackground", n5).paintSliderBackground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintSliderBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "sliderborder", -1).paintSliderBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintSliderBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "sliderborder", n5).paintSliderBorder(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintSliderThumbBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "sliderthumbbackground", n5).paintSliderThumbBackground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintSliderThumbBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "sliderthumbborder", n5).paintSliderThumbBorder(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintSliderTrackBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "slidertrackbackground", -1).paintSliderTrackBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintSliderTrackBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "slidertrackbackground", n5).paintSliderTrackBackground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintSliderTrackBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "slidertrackborder", -1).paintSliderTrackBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintSliderTrackBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "slidertrackborder", n5).paintSliderTrackBorder(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintSpinnerBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "spinnerbackground", -1).paintSpinnerBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintSpinnerBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "spinnerborder", -1).paintSpinnerBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintSplitPaneDividerBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "splitpanedividerbackground", -1).paintSplitPaneDividerBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintSplitPaneDividerBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "splitpanedividerbackground", n5).paintSplitPaneDividerBackground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintSplitPaneDividerForeground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "splitpanedividerforeground", n5).paintSplitPaneDividerForeground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintSplitPaneDragDivider(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "splitpanedragdivider", n5).paintSplitPaneDragDivider(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintSplitPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "splitpanebackground", -1).paintSplitPaneBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintSplitPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "splitpaneborder", -1).paintSplitPaneBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTabbedPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "tabbedpanebackground", -1).paintTabbedPaneBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTabbedPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "tabbedpaneborder", -1).paintTabbedPaneBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTabbedPaneTabAreaBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "tabbedpanetabareabackground", -1).paintTabbedPaneTabAreaBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTabbedPaneTabAreaBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "tabbedpanetabareabackground", n5).paintTabbedPaneTabAreaBackground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintTabbedPaneTabAreaBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "tabbedpanetabareaborder", -1).paintTabbedPaneTabAreaBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTabbedPaneTabAreaBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "tabbedpanetabareaborder", n5).paintTabbedPaneTabAreaBorder(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintTabbedPaneTabBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "tabbedpanetabbackground", -1).paintTabbedPaneTabBackground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintTabbedPaneTabBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
            getPainter(synthContext, "tabbedpanetabbackground", n6).paintTabbedPaneTabBackground(synthContext, graphics, n, n2, n3, n4, n5, n6);
        }
        
        @Override
        public void paintTabbedPaneTabBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "tabbedpanetabborder", -1).paintTabbedPaneTabBorder(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintTabbedPaneTabBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
            getPainter(synthContext, "tabbedpanetabborder", n6).paintTabbedPaneTabBorder(synthContext, graphics, n, n2, n3, n4, n5, n6);
        }
        
        @Override
        public void paintTabbedPaneContentBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "tabbedpanecontentbackground", -1).paintTabbedPaneContentBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTabbedPaneContentBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "tabbedpanecontentborder", -1).paintTabbedPaneContentBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTableHeaderBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "tableheaderbackground", -1).paintTableHeaderBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTableHeaderBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "tableheaderborder", -1).paintTableHeaderBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTableBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "tablebackground", -1).paintTableBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTableBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "tableborder", -1).paintTableBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTextAreaBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "textareabackground", -1).paintTextAreaBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTextAreaBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "textareaborder", -1).paintTextAreaBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTextPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "textpanebackground", -1).paintTextPaneBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTextPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "textpaneborder", -1).paintTextPaneBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTextFieldBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "textfieldbackground", -1).paintTextFieldBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTextFieldBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "textfieldborder", -1).paintTextFieldBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintToggleButtonBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "togglebuttonbackground", -1).paintToggleButtonBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintToggleButtonBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "togglebuttonborder", -1).paintToggleButtonBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintToolBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "toolbarbackground", -1).paintToolBarBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintToolBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "toolbarbackground", n5).paintToolBarBackground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintToolBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "toolbarborder", -1).paintToolBarBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintToolBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "toolbarborder", n5).paintToolBarBorder(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintToolBarContentBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "toolbarcontentbackground", -1).paintToolBarContentBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintToolBarContentBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "toolbarcontentbackground", n5).paintToolBarContentBackground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintToolBarContentBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "toolbarcontentborder", -1).paintToolBarContentBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintToolBarContentBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "toolbarcontentborder", n5).paintToolBarContentBorder(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintToolBarDragWindowBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "toolbardragwindowbackground", -1).paintToolBarDragWindowBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintToolBarDragWindowBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "toolbardragwindowbackground", n5).paintToolBarDragWindowBackground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintToolBarDragWindowBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "toolbardragwindowborder", -1).paintToolBarDragWindowBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintToolBarDragWindowBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
            getPainter(synthContext, "toolbardragwindowborder", n5).paintToolBarDragWindowBorder(synthContext, graphics, n, n2, n3, n4, n5);
        }
        
        @Override
        public void paintToolTipBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "tooltipbackground", -1).paintToolTipBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintToolTipBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "tooltipborder", -1).paintToolTipBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTreeBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "treebackground", -1).paintTreeBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTreeBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "treeborder", -1).paintTreeBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTreeCellBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "treecellbackground", -1).paintTreeCellBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTreeCellBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "treecellborder", -1).paintTreeCellBorder(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintTreeCellFocus(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "treecellfocus", -1).paintTreeCellFocus(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintViewportBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "viewportbackground", -1).paintViewportBackground(synthContext, graphics, n, n2, n3, n4);
        }
        
        @Override
        public void paintViewportBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            getPainter(synthContext, "viewportborder", -1).paintViewportBorder(synthContext, graphics, n, n2, n3, n4);
        }
    }
}
