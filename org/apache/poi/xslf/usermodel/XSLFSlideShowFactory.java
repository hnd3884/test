package org.apache.poi.xslf.usermodel;

import java.io.InputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackageAccess;
import java.io.File;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.Internal;
import org.apache.poi.sl.usermodel.SlideShowFactory;

@Internal
public class XSLFSlideShowFactory extends SlideShowFactory
{
    public static XMLSlideShow create(final OPCPackage pkg) throws IOException {
        try {
            return new XMLSlideShow(pkg);
        }
        catch (final IllegalArgumentException ioe) {
            pkg.revert();
            throw ioe;
        }
    }
    
    public static XMLSlideShow createSlideShow(final OPCPackage pkg) throws IOException {
        try {
            return new XMLSlideShow(pkg);
        }
        catch (final IllegalArgumentException ioe) {
            pkg.revert();
            throw ioe;
        }
    }
    
    public static XMLSlideShow createSlideShow(final File file, final boolean readOnly) throws IOException {
        try {
            final OPCPackage pkg = OPCPackage.open(file, readOnly ? PackageAccess.READ : PackageAccess.READ_WRITE);
            return createSlideShow(pkg);
        }
        catch (final InvalidFormatException e) {
            throw new IOException(e);
        }
    }
    
    public static XMLSlideShow createSlideShow(final InputStream stream) throws IOException {
        try {
            final OPCPackage pkg = OPCPackage.open(stream);
            return createSlideShow(pkg);
        }
        catch (final InvalidFormatException e) {
            throw new IOException(e);
        }
    }
    
    static {
        SlideShowFactory.createXslfByFile = XSLFSlideShowFactory::createSlideShow;
        SlideShowFactory.createXslfByStream = XSLFSlideShowFactory::createSlideShow;
    }
}
