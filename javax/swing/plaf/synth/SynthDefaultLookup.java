package javax.swing.plaf.synth;

import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import sun.swing.DefaultLookup;

class SynthDefaultLookup extends DefaultLookup
{
    @Override
    public Object getDefault(final JComponent component, final ComponentUI componentUI, final String s) {
        if (!(componentUI instanceof SynthUI)) {
            return super.getDefault(component, componentUI, s);
        }
        final SynthContext context = ((SynthUI)componentUI).getContext(component);
        final Object value = context.getStyle().get(context, s);
        context.dispose();
        return value;
    }
}
