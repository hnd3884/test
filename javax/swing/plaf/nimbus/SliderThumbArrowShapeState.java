package javax.swing.plaf.nimbus;

import javax.swing.JComponent;

class SliderThumbArrowShapeState extends State
{
    SliderThumbArrowShapeState() {
        super("ArrowShape");
    }
    
    @Override
    protected boolean isInState(final JComponent component) {
        return component.getClientProperty("Slider.paintThumbArrowShape") == Boolean.TRUE;
    }
}
