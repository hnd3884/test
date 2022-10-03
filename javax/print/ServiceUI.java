package javax.print;

import javax.print.attribute.Attribute;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.standard.Fidelity;
import sun.print.SunAlternateMedia;
import javax.print.attribute.standard.Destination;
import java.awt.Component;
import java.awt.Dialog;
import sun.print.ServiceDialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.GraphicsEnvironment;
import javax.print.attribute.PrintRequestAttributeSet;
import java.awt.GraphicsConfiguration;

public class ServiceUI
{
    public static PrintService printDialog(final GraphicsConfiguration graphicsConfiguration, final int n, final int n2, final PrintService[] array, final PrintService printService, final DocFlavor docFlavor, final PrintRequestAttributeSet set) throws HeadlessException {
        int n3 = -1;
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("services must be non-null and non-empty");
        }
        if (set == null) {
            throw new IllegalArgumentException("attributes must be non-null");
        }
        if (printService != null) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i].equals(printService)) {
                    n3 = i;
                    break;
                }
            }
            if (n3 < 0) {
                throw new IllegalArgumentException("services must contain defaultService");
            }
        }
        else {
            n3 = 0;
        }
        final Dialog locationRelativeTo = null;
        Rectangle union = (graphicsConfiguration == null) ? GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds() : graphicsConfiguration.getBounds();
        ServiceDialog serviceDialog;
        if (locationRelativeTo instanceof Frame) {
            serviceDialog = new ServiceDialog(graphicsConfiguration, n + union.x, n2 + union.y, array, n3, docFlavor, set, (Frame)locationRelativeTo);
        }
        else {
            serviceDialog = new ServiceDialog(graphicsConfiguration, n + union.x, n2 + union.y, array, n3, docFlavor, set, locationRelativeTo);
        }
        final Rectangle bounds = serviceDialog.getBounds();
        final GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        for (int j = 0; j < screenDevices.length; ++j) {
            union = union.union(screenDevices[j].getDefaultConfiguration().getBounds());
        }
        if (!union.contains(bounds)) {
            serviceDialog.setLocationRelativeTo(locationRelativeTo);
        }
        serviceDialog.show();
        if (serviceDialog.getStatus() == 1) {
            final PrintRequestAttributeSet attributes = serviceDialog.getAttributes();
            final Class<Destination> clazz = Destination.class;
            final Class<SunAlternateMedia> clazz2 = SunAlternateMedia.class;
            final Class<Fidelity> clazz3 = Fidelity.class;
            if (set.containsKey(clazz) && !attributes.containsKey(clazz)) {
                set.remove(clazz);
            }
            if (set.containsKey(clazz2) && !attributes.containsKey(clazz2)) {
                set.remove(clazz2);
            }
            set.addAll(attributes);
            final Fidelity fidelity = (Fidelity)set.get(clazz3);
            if (fidelity != null && fidelity == Fidelity.FIDELITY_TRUE) {
                removeUnsupportedAttributes(serviceDialog.getPrintService(), docFlavor, set);
            }
        }
        return serviceDialog.getPrintService();
    }
    
    private static void removeUnsupportedAttributes(final PrintService printService, final DocFlavor docFlavor, final AttributeSet set) {
        final AttributeSet unsupportedAttributes = printService.getUnsupportedAttributes(docFlavor, set);
        if (unsupportedAttributes != null) {
            final Attribute[] array = unsupportedAttributes.toArray();
            for (int i = 0; i < array.length; ++i) {
                final Class<? extends Attribute> category = array[i].getCategory();
                if (printService.isAttributeCategorySupported(category)) {
                    final Attribute attribute = (Attribute)printService.getDefaultAttributeValue(category);
                    if (attribute != null) {
                        set.add(attribute);
                    }
                    else {
                        set.remove(category);
                    }
                }
                else {
                    set.remove(category);
                }
            }
        }
    }
}
