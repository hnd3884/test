package com.adventnet.sym.server.mdm.util;

import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import java.io.InputStream;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader
{
    public List getSheetDataCollection(final String fileName) {
        final List cellVectorHolder = new ArrayList();
        InputStream myInput = null;
        try {
            myInput = ApiFactoryProvider.getFileAccessAPI().readFile(fileName);
            final POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            final HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            final HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            final Iterator rowIter = mySheet.rowIterator();
            while (rowIter.hasNext()) {
                final HSSFRow myRow = rowIter.next();
                final Iterator cellIter = myRow.cellIterator();
                final List cellStoreVector = new ArrayList();
                while (cellIter.hasNext()) {
                    final HSSFCell myCell = cellIter.next();
                    if (myCell.getCellTypeEnum().equals((Object)CellType.NUMERIC)) {
                        final Double cellValue = myCell.getNumericCellValue();
                        cellStoreVector.add(cellValue.intValue());
                    }
                    else {
                        cellStoreVector.add(myCell.getStringCellValue());
                    }
                }
                cellVectorHolder.add(cellStoreVector);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            if (myInput != null) {
                try {
                    myInput.close();
                }
                catch (final Exception exp) {
                    exp.printStackTrace();
                }
            }
        }
        finally {
            if (myInput != null) {
                try {
                    myInput.close();
                }
                catch (final Exception exp2) {
                    exp2.printStackTrace();
                }
            }
        }
        return cellVectorHolder;
    }
}
