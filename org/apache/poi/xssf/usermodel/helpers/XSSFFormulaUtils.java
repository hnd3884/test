package org.apache.poi.xssf.usermodel.helpers;

import org.w3c.dom.NodeList;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.formula.ptg.Pxg3D;
import org.apache.poi.ss.formula.ptg.Pxg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellFormula;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaType;
import org.w3c.dom.Node;
import java.util.List;
import java.util.Iterator;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class XSSFFormulaUtils
{
    private final XSSFWorkbook _wb;
    private final XSSFEvaluationWorkbook _fpwb;
    
    public XSSFFormulaUtils(final XSSFWorkbook wb) {
        this._wb = wb;
        this._fpwb = XSSFEvaluationWorkbook.create(this._wb);
    }
    
    public void updateSheetName(final int sheetIndex, final String oldName, final String newName) {
        for (final XSSFName nm : this._wb.getAllNames()) {
            if (nm.getSheetIndex() == -1 || nm.getSheetIndex() == sheetIndex) {
                this.updateName(nm, oldName, newName);
            }
        }
        for (final Sheet sh : this._wb) {
            for (final Row row : sh) {
                for (final Cell cell : row) {
                    if (cell.getCellType() == CellType.FORMULA) {
                        this.updateFormula((XSSFCell)cell, oldName, newName);
                    }
                }
            }
        }
        final List<POIXMLDocumentPart> rels = this._wb.getSheetAt(sheetIndex).getRelations();
        for (final POIXMLDocumentPart r : rels) {
            if (r instanceof XSSFDrawing) {
                final XSSFDrawing dg = (XSSFDrawing)r;
                for (final XSSFChart chart : dg.getCharts()) {
                    final Node dom = chart.getCTChartSpace().getDomNode();
                    this.updateDomSheetReference(dom, oldName, newName);
                }
            }
        }
    }
    
    private void updateFormula(final XSSFCell cell, final String oldName, final String newName) {
        final CTCellFormula f = cell.getCTCell().getF();
        if (f != null) {
            final String formula = f.getStringValue();
            if (formula != null && formula.length() > 0) {
                final int sheetIndex = this._wb.getSheetIndex((Sheet)cell.getSheet());
                final Ptg[] parse;
                final Ptg[] ptgs = parse = FormulaParser.parse(formula, (FormulaParsingWorkbook)this._fpwb, FormulaType.CELL, sheetIndex, cell.getRowIndex());
                for (final Ptg ptg : parse) {
                    this.updatePtg(ptg, oldName, newName);
                }
                final String updatedFormula = FormulaRenderer.toFormulaString((FormulaRenderingWorkbook)this._fpwb, ptgs);
                if (!formula.equals(updatedFormula)) {
                    f.setStringValue(updatedFormula);
                }
            }
        }
    }
    
    private void updateName(final XSSFName name, final String oldName, final String newName) {
        final String formula = name.getRefersToFormula();
        if (formula != null) {
            final int sheetIndex = name.getSheetIndex();
            final int rowIndex = -1;
            final Ptg[] parse;
            final Ptg[] ptgs = parse = FormulaParser.parse(formula, (FormulaParsingWorkbook)this._fpwb, FormulaType.NAMEDRANGE, sheetIndex, rowIndex);
            for (final Ptg ptg : parse) {
                this.updatePtg(ptg, oldName, newName);
            }
            final String updatedFormula = FormulaRenderer.toFormulaString((FormulaRenderingWorkbook)this._fpwb, ptgs);
            if (!formula.equals(updatedFormula)) {
                name.setRefersToFormula(updatedFormula);
            }
        }
    }
    
    private void updatePtg(final Ptg ptg, final String oldName, final String newName) {
        if (ptg instanceof Pxg) {
            final Pxg pxg = (Pxg)ptg;
            if (pxg.getExternalWorkbookNumber() < 1) {
                if (pxg.getSheetName() != null && pxg.getSheetName().equals(oldName)) {
                    pxg.setSheetName(newName);
                }
                if (pxg instanceof Pxg3D) {
                    final Pxg3D pxg3D = (Pxg3D)pxg;
                    if (pxg3D.getLastSheetName() != null && pxg3D.getLastSheetName().equals(oldName)) {
                        pxg3D.setLastSheetName(newName);
                    }
                }
            }
        }
    }
    
    private void updateDomSheetReference(final Node dom, final String oldName, final String newName) {
        final String value = dom.getNodeValue();
        if (value != null && (value.contains(oldName + "!") || value.contains(oldName + "'!"))) {
            final XSSFName temporary = this._wb.createName();
            temporary.setRefersToFormula(value);
            this.updateName(temporary, oldName, newName);
            dom.setNodeValue(temporary.getRefersToFormula());
            this._wb.removeName((Name)temporary);
        }
        final NodeList nl = dom.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            this.updateDomSheetReference(nl.item(i), oldName, newName);
        }
    }
}
