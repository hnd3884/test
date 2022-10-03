package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.Workbook;
import java.io.InputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackageAccess;
import java.io.File;
import org.apache.poi.openxml4j.opc.ZipPackage;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class XSSFWorkbookFactory extends WorkbookFactory
{
    public static XSSFWorkbook createWorkbook() {
        return new XSSFWorkbook();
    }
    
    public static XSSFWorkbook create(final OPCPackage pkg) throws IOException {
        return createWorkbook(pkg);
    }
    
    public static XSSFWorkbook createWorkbook(final ZipPackage pkg) throws IOException {
        return createWorkbook((OPCPackage)pkg);
    }
    
    public static XSSFWorkbook createWorkbook(final OPCPackage pkg) throws IOException {
        try {
            return new XSSFWorkbook(pkg);
        }
        catch (final RuntimeException ioe) {
            pkg.revert();
            throw ioe;
        }
    }
    
    public static XSSFWorkbook createWorkbook(final File file, final boolean readOnly) throws IOException {
        try {
            final OPCPackage pkg = OPCPackage.open(file, readOnly ? PackageAccess.READ : PackageAccess.READ_WRITE);
            return createWorkbook(pkg);
        }
        catch (final InvalidFormatException e) {
            throw new IOException(e);
        }
    }
    
    public static XSSFWorkbook createWorkbook(final InputStream stream) throws IOException {
        try {
            final OPCPackage pkg = OPCPackage.open(stream);
            return createWorkbook(pkg);
        }
        catch (final InvalidFormatException e) {
            throw new IOException(e);
        }
    }
    
    static {
        WorkbookFactory.createXssfFromScratch = XSSFWorkbookFactory::createWorkbook;
        WorkbookFactory.createXssfByStream = XSSFWorkbookFactory::createWorkbook;
        WorkbookFactory.createXssfByPackage = (o -> createWorkbook((OPCPackage)o));
        WorkbookFactory.createXssfByFile = XSSFWorkbookFactory::createWorkbook;
    }
}
