package org.apache.poi.hssf.usermodel;

import org.apache.poi.poifs.filesystem.DirectoryNode;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.Internal;
import org.apache.poi.ss.usermodel.WorkbookFactory;

@Internal
public class HSSFWorkbookFactory extends WorkbookFactory
{
    public static HSSFWorkbook createWorkbook() {
        return new HSSFWorkbook();
    }
    
    public static HSSFWorkbook createWorkbook(final POIFSFileSystem fs) throws IOException {
        return new HSSFWorkbook(fs);
    }
    
    public static HSSFWorkbook createWorkbook(final DirectoryNode root) throws IOException {
        return new HSSFWorkbook(root, true);
    }
    
    static {
        WorkbookFactory.createHssfFromScratch = HSSFWorkbookFactory::createWorkbook;
        WorkbookFactory.createHssfByNode = HSSFWorkbookFactory::createWorkbook;
    }
}
