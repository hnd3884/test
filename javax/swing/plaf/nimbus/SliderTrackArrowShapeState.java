package javax.swing.plaf.nimbus;

import javax.swing.JComponent;

class SliderTrackArrowShapeState extends State
{
    SliderTrackArrowShapeState() {
        super("ArrowShape");
    }
    
    @Override
    protected boolean isInState(final JComponent component) {
        return component.getClientProperty("Slider.paintThumbArrowShape") == Boolean.TRUE;
    }
}
