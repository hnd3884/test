package com.adventnet.client.components.table.template;

import org.htmlparser.nodes.TextNode;
import com.adventnet.client.components.table.web.TableDatasetModel;
import org.htmlparser.util.ParserException;
import com.adventnet.client.tpl.TemplateAPI;
import java.util.List;
import com.adventnet.i18n.I18N;
import com.adventnet.client.util.web.WebClientUtil;
import javax.swing.table.TableModel;
import com.adventnet.client.components.rangenavigator.web.NavigationConfig;
import org.htmlparser.Tag;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;
import com.adventnet.iam.xss.IAMEncoder;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpServletRequest;
import org.htmlparser.Node;
import java.util.Map;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.components.table.web.TableIterator;
import com.adventnet.persistence.Row;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.table.web.TableTransformerContext;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.visitors.NodeVisitor;
import com.adventnet.client.action.web.MenuVariablesGenerator;
import com.adventnet.client.components.table.web.TableViewModel;
import java.util.logging.Level;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.util.NodeList;
import java.util.HashMap;
import java.util.logging.Logger;

public class FillTable
{
    private static final Logger OUT;
    public static HashMap tablehtmlmap;
    public static HashMap tabhtmlmap;
    
    public static NodeList getNodeList(final String s) {
        final Lexer viewLexer = new Lexer(s);
        final Parser parser = new Parser(viewLexer);
        NodeList nl = new NodeList();
        try {
            nl = parser.parse((NodeFilter)null);
        }
        catch (final Exception e) {
            FillTable.OUT.log(Level.WARNING, "Exception when trying to create NodeList from the string:  " + s, e);
        }
        return nl;
    }
    
    public static String getFilledRowHtml(final TableViewModel viewModel, final String templateViewName, final boolean returnFilterAlone) throws Exception {
        final TableTransformerContext transContext = viewModel.getTableTransformerContext();
        final ViewContext viewContext = transContext.getViewContext();
        final String viewName = viewContext.getUniqueId();
        final int columnCount = viewModel.getViewColumns().length;
        final ColumnProperties[] columnProperties = new ColumnProperties[columnCount];
        final String[] headerColumns = new String[columnCount];
        initColumnProperties(columnProperties, viewModel, headerColumns);
        NodeList template = new NodeList();
        if (!templateViewName.equals("")) {
            template = getTemplate(templateViewName, viewContext);
        }
        else {
            template = getTemplate(viewName, viewContext);
        }
        try {
            template = parseNavigation(viewName, template, viewModel);
        }
        catch (final Exception e) {
            FillTable.OUT.log(Level.WARNING, "Enable Navigation or parsing error in navigation part", e);
        }
        final Long chooserId = (Long)viewModel.getTableViewConfigRow().get("COLUMNCHOOSERMENUITEM");
        String columnChooserType = null;
        if (chooserId != null) {
            columnChooserType = MenuVariablesGenerator.getMenuItemID(chooserId);
        }
        final ExtractTemplateHeaderCols visitor = new ExtractTemplateHeaderCols(viewContext, columnChooserType);
        template.visitAllNodesWith((NodeVisitor)visitor);
        final CheckAndPopulateFilterComboVisitor filterVisitor = new CheckAndPopulateFilterComboVisitor(viewContext);
        template.visitAllNodesWith((NodeVisitor)filterVisitor);
        String tillRef = template.toHtml();
        final int viewnameindex = tillRef.indexOf("${viewName}");
        try {
            if (viewnameindex != -1) {
                tillRef = tillRef.substring(0, viewnameindex) + viewName + tillRef.substring(tillRef.indexOf("}", viewnameindex) + 1, tillRef.length());
            }
        }
        catch (final Exception e2) {
            FillTable.OUT.log(Level.WARNING, "Exception while filling ${viewname} ", e2);
        }
        if (returnFilterAlone) {
            NodeList dupList = getNodeList(tillRef);
            final HasAttributeFilter filterForFilterRow = new HasAttributeFilter();
            filterForFilterRow.setAttributeName("mc:row");
            filterForFilterRow.setAttributeValue("filter");
            dupList = dupList.extractAllNodesThatMatch((NodeFilter)filterForFilterRow, true);
            return dupList.toHtml();
        }
        template = getNodeList(tillRef);
        final StringBuilder data = new StringBuilder(tillRef);
        getDefaultMenu(viewModel, headerColumns, data, template, columnProperties);
        replaceHeaderRow(data, transContext, viewModel, headerColumns, columnProperties, visitor, template);
        getReplacedSearchRow(viewModel, transContext, visitor, headerColumns, template, columnProperties, data);
        getReplacedRowData(viewContext, viewModel, columnProperties, visitor, template, data);
        getReplacedSumRowHtml(template, visitor, viewContext, viewModel, data);
        return data.toString();
    }
    
    private static void initColumnProperties(final ColumnProperties[] columnProperties, final TableViewModel viewModel, final String[] headerColumns) throws Exception {
        final TableTransformerContext transContext = viewModel.getTableTransformerContext();
        final TableIterator iter = viewModel.getNewTableIterator();
        final ViewContext viewContext = transContext.getViewContext();
        iter.reset();
        int columnNo = 0;
        while (iter.nextColumn()) {
            final StringBuilder sb = new StringBuilder();
            iter.initTransCtxForCurrentCell("HEADER");
            if (transContext.getRenderedAttributes().get("VALUE") != null && !transContext.getRenderedAttributes().get("VALUE").equals("&nbsp;")) {
                final Object obj = transContext.getRenderedAttributes().get("VALUE");
                if (((String)obj).indexOf(",") == -1) {
                    sb.append((String)obj);
                }
                else {
                    sb.append("\"" + (String)obj + "\"");
                }
            }
            else {
                sb.append("");
            }
            final String temp = sb.toString();
            headerColumns[columnNo] = temp;
            ++columnNo;
        }
        final DataObject viewConfigDO = viewContext.getModel().getViewConfiguration();
        final Long columnConfigNameNo = (Long)viewConfigDO.getFirstValue("ACTableViewConfig", 4);
        final DataObject dataObject = TableViewModel.getColumnConfigDO(columnConfigNameNo, viewConfigDO);
        final Iterator it = dataObject.getRows("ACColumnConfiguration");
        columnNo = 0;
        final Map columnProps = new HashMap();
        while (it.hasNext()) {
            final Row row = it.next();
            final Boolean isSearchEnabled = (Boolean)row.get("SEARCHENABLED");
            final Boolean isSortEnabled = (Boolean)row.get("SORTENABLED");
            String cssClass = "";
            if (row.get("CSSCLASS") != null) {
                cssClass = (String)row.get("CSSCLASS");
            }
            String headerCss = "";
            if (row.get("HEADERCSS") != null) {
                headerCss = (String)row.get("HEADERCSS");
            }
            final ColumnProperties column = new ColumnProperties((String)row.get("COLUMNALIAS"));
            column.setIsSearchEnabled(isSearchEnabled);
            column.setIsSortEnabled(isSortEnabled);
            column.setCssClassName(cssClass);
            column.setHeaderCss(headerCss);
            columnProps.put(row.get("COLUMNALIAS"), column);
        }
        final Object[] columns = viewModel.getViewColumns();
        for (int i = 0; i < columns.length; ++i) {
            final String columnName = (String)((Object[])columns[i])[0];
            columnProperties[i] = columnProps.get(columnName);
        }
    }
    
    private static void getDefaultMenu(final TableViewModel viewModel, final String[] headerColumns, final StringBuilder data, final NodeList template, final ColumnProperties[] columnProperties) throws Exception {
        final TableTransformerContext transContext = viewModel.getTableTransformerContext();
        final ViewContext viewContext = transContext.getViewContext();
        final String viewName = viewContext.getUniqueId();
        final HasAttributeFilter menuFilter = new HasAttributeFilter();
        menuFilter.setAttributeName("mc:column");
        menuFilter.setAttributeValue("DefaultMenu");
        final NodeList menuNode = template.extractAllNodesThatMatch((NodeFilter)menuFilter, true);
        int ind = 0;
        String remove = null;
        if (menuNode.size() != 0) {
            final Node menu_column = menuNode.elementAt(0);
            remove = menu_column.toHtml();
            ind = data.indexOf(remove);
            final String menuString = TemplateMenuGenerator.getMenu(viewName, viewContext);
            final HeaderFillingVisitor headerFillingVisitor = new HeaderFillingVisitor(viewName, null, viewModel.getSortedColumn(), null, transContext.getSortButtonClass(), menuString);
            menuNode.visitAllNodesWith((NodeVisitor)headerFillingVisitor);
            if (ind != -1) {
                data.delete(ind, ind + remove.length());
                data.insert(ind, menu_column.toHtml());
            }
        }
    }
    
    private static void replaceHeaderRow(final StringBuilder data, final TableTransformerContext transContext, final TableViewModel viewModel, final String[] headerColumns, final ColumnProperties[] columnProperties, final ExtractTemplateHeaderCols visitor, final NodeList template) throws Exception {
        final HasAttributeFilter headerFilter = new HasAttributeFilter();
        headerFilter.setAttributeName("mc:row");
        headerFilter.setAttributeValue("header");
        final NodeList headerNode = template.extractAllNodesThatMatch((NodeFilter)headerFilter, true);
        final String remove1 = headerNode.toHtml();
        final int ind = (remove1 == null) ? -1 : data.indexOf(remove1);
        final String headerRowString = getHeaderHtmldata(headerColumns, columnProperties, visitor, transContext, viewModel, headerNode);
        if (ind != -1) {
            data.delete(ind, ind + remove1.length());
            data.insert(ind, headerRowString);
        }
    }
    
    private static void getReplacedSearchRow(final TableViewModel viewModel, final TableTransformerContext transContext, final ExtractTemplateHeaderCols visitor, final String[] headerColumns, final NodeList template, final ColumnProperties[] columnProperties, final StringBuilder data) throws Exception {
        final HasAttributeFilter searchRowFilter = new HasAttributeFilter();
        searchRowFilter.setAttributeName("mc:row");
        searchRowFilter.setAttributeValue("searchRow");
        final NodeList headerNode1 = template.extractAllNodesThatMatch((NodeFilter)searchRowFilter, true);
        final Node searchRow = headerNode1.elementAt(0);
        String remove1 = null;
        if (searchRow != null) {
            remove1 = searchRow.toHtml();
        }
        final int ind = (remove1 == null) ? -1 : data.indexOf(remove1);
        final String headerPart = getSearchRowHtml(viewModel, transContext, visitor, headerColumns, headerNode1, columnProperties);
        if (ind != -1) {
            data.delete(ind, ind + remove1.length());
            data.insert(ind, headerPart);
        }
    }
    
    private static void getReplacedRowData(final ViewContext viewContext, final TableViewModel viewModel, final ColumnProperties[] columnProperties, final ExtractTemplateHeaderCols visitor, final NodeList template, StringBuilder data) throws Exception {
        final HasAttributeFilter filter = new HasAttributeFilter();
        filter.setAttributeName("mc:row");
        filter.setAttributeValue("reference");
        final NodeList nl = template.extractAllNodesThatMatch((NodeFilter)filter, true);
        final String remove = nl.toHtml();
        final int ind = data.indexOf(remove);
        final String output = getRowHtmlData(viewContext, viewModel, columnProperties, visitor, nl);
        data.delete(ind, ind + remove.length());
        data = data.insert(ind, output);
    }
    
    private static void getReplacedSumRowHtml(NodeList template, final ExtractTemplateHeaderCols visitor, final ViewContext viewContext, final TableViewModel viewModel, final StringBuilder data) throws Exception {
        final String viewName = viewContext.getUniqueId();
        final StringBuilder sumHtml = new StringBuilder();
        final boolean isSumAndDefaultHtmlPresent = checkSumHtmlAndInitSumHtml(template, sumHtml);
        String tillRef = null;
        if (isSumAndDefaultHtmlPresent) {
            tillRef = removeSumHtmlIfPresent(template, false);
            template = getNodeList(tillRef);
        }
        if (isSumAndDefaultHtmlPresent) {
            int count = 0;
            final int newCount = 1;
            final StringBuilder sumRow = new StringBuilder();
            final String[] splitedSumHtml = getSumHtmlArray(sumHtml.toString());
            final String sumColHtml = splitedSumHtml[1];
            final String sumHtmlPrefix = splitedSumHtml[0];
            final String sumHtmlSuffix = splitedSumHtml[2];
            final Object[] view_columns = viewModel.getViewColumns();
            for (int noOfPreColumnsInTemplate = visitor.getNoOfPreColumnsInTemplate(), i = 0; i < noOfPreColumnsInTemplate + view_columns.length; ++i) {
                if (i > noOfPreColumnsInTemplate) {
                    final String columnName = (String)((Object[])view_columns[count])[0];
                    updateSumRow(sumRow, sumColHtml, columnName, viewContext);
                    ++count;
                }
                else {
                    updateSumRow(sumRow, sumColHtml, null, viewContext);
                }
            }
            final int index = data.indexOf("$${sumrowhtml}$$");
            if (index != -1) {
                data.delete(index, index + "$${sumrowhtml}$$".length());
                String sum_row = sumHtmlPrefix + sumRow.toString() + sumHtmlSuffix;
                sum_row = parseSumEntries(sum_row, viewContext);
                sum_row = checkDollarRequestParams(checkDollarProperties(sum_row), viewContext);
                data.insert(index, sum_row);
            }
        }
        String sum_html = "$${SUMHTML}$$";
        final int index2 = data.indexOf(sum_html);
        if (index2 != -1) {
            data.delete(index2, index2 + sum_html.length());
            sum_html = getCustomReplacedSumString(viewContext);
            data.insert(index2, sum_html);
        }
    }
    
    private static String getHeaderHtmldata(final String[] headerColumns, final ColumnProperties[] columnProperties, final ExtractTemplateHeaderCols visitor, final TableTransformerContext transContext, final TableViewModel viewModel, final NodeList headerNode) throws Exception {
        final ViewContext viewContext = transContext.getViewContext();
        final PageContext pageContext = transContext.getPageContext();
        final String viewName = viewContext.getUniqueId();
        final String menuString = TemplateMenuGenerator.getMenu(viewName, viewContext);
        final String columnchooser = visitor.getColumnChooser();
        final String columnChooserType = visitor.getColumnChooserType();
        final int noOfColumnsInTemplate = visitor.getNoOfColumnsInTemplate();
        final int noOfPreColumnsInTemplate = visitor.getNoOfPreColumnsInTemplate();
        final int noOfPostColumnsInTemplate = visitor.getNoOfPostColumnsInTempate();
        final Map templateHtmlHeaderCols = visitor.getTemplateHtmlHeaderCols();
        final Map columnOrderInTemplate = visitor.getColumnOrderInTemplate();
        final Map preColumnsInTemplate = visitor.getPreColumnsInTemplate();
        final Map postColumnsInTemplate = visitor.getPostColumnsInTemplate();
        StringBuilder header = new StringBuilder();
        final boolean isSearchPresent = transContext.isSearchEnabled() && transContext.getSearchValue() != null && transContext.getSearchValue() != "";
        final SearchButtonClassSettingVisitor visit = new SearchButtonClassSettingVisitor(isSearchPresent);
        for (int i = 1; i <= noOfPreColumnsInTemplate; ++i) {
            String value = preColumnsInTemplate.get(i);
            value = checkDollarRequestParams(checkDollarProperties(value), viewContext);
            final NodeList preCols = getNodeList(value);
            final HeaderFillingVisitor headerFillingVisitor = new HeaderFillingVisitor(viewName, null, viewModel.getSortedColumn(), null, transContext.getSortButtonClass(), menuString);
            preCols.visitAllNodesWith((NodeVisitor)headerFillingVisitor);
            preCols.visitAllNodesWith((NodeVisitor)visit);
            header.append(preCols.toHtml());
        }
        final Object[] viewColumns = viewModel.getViewColumns();
        for (int j = 0; j < viewColumns.length; ++j) {
            final String colName = (String)((Object[])viewColumns[j])[0];
            String value2 = "";
            if (colName != null) {
                if (templateHtmlHeaderCols.get(viewName + "_" + colName) != null) {
                    value2 = templateHtmlHeaderCols.get(viewName + "_" + colName);
                }
                else {
                    if (templateHtmlHeaderCols.get(viewName + "_DefaultColumn") == null) {
                        throw new Exception("Column name(=" + colName + ") not found in template as well as 'DefaultColumn' is not specified");
                    }
                    value2 = templateHtmlHeaderCols.get(viewName + "_DefaultColumn");
                }
            }
            if (value2 != "") {
                final boolean isSortEnabled = columnProperties[j].isSortEnabled;
                final SortSettingVisitor sortVisitor = new SortSettingVisitor(colName, isSortEnabled);
                NodeList val = getNodeList(value2);
                val.visitAllNodesWith((NodeVisitor)sortVisitor);
                value2 = val.toHtml();
                final int indexReplace = value2.indexOf("DefaultColumn");
                if (indexReplace != -1) {
                    value2 = value2.substring(0, indexReplace) + colName + value2.substring(indexReplace + 13);
                }
                val = getNodeList(value2);
                final HeaderFillingVisitor headerFillingVisitor2 = new HeaderFillingVisitor(viewName, columnProperties[j], viewModel.getSortedColumn(), headerColumns[j], transContext.getSortButtonClass(), menuString);
                val.visitAllNodesWith((NodeVisitor)headerFillingVisitor2);
                val.visitAllNodesWith((NodeVisitor)visit);
                value2 = val.toHtml();
                value2 = checkDollarRequestParams(checkDollarProperties(value2), viewContext);
                header.append(value2);
            }
        }
        for (int j = 1; j <= noOfPostColumnsInTemplate; ++j) {
            String value3 = postColumnsInTemplate.get(j);
            value3 = checkDollarRequestParams(checkDollarProperties(value3), viewContext);
            final NodeList postCols = getNodeList(value3);
            final HeaderFillingVisitor headerFillingVisitor3 = new HeaderFillingVisitor(viewName, null, viewModel.getSortedColumn(), null, transContext.getSortButtonClass(), menuString);
            postCols.visitAllNodesWith((NodeVisitor)headerFillingVisitor3);
            postCols.visitAllNodesWith((NodeVisitor)visit);
            header.append(postCols.toHtml());
        }
        if (columnChooserType != null && columnChooserType.trim().length() > 0 && header.indexOf(columnchooser) != -1) {
            final int headerPart1 = header.indexOf(columnchooser);
            Long ccMenuItem;
            try {
                ccMenuItem = MenuVariablesGenerator.getMenuItemNo(columnChooserType);
            }
            catch (final Exception e) {
                e.printStackTrace();
                throw new Exception("no columnchooser in name " + columnChooserType);
            }
            final String scriptInclusion = MenuVariablesGenerator.getScriptInclusion((Object)ccMenuItem, (HttpServletRequest)pageContext.getRequest());
            final String menuVariableScript = MenuVariablesGenerator.generateMenuVariableScript((Object)ccMenuItem, (HttpServletRequest)pageContext.getRequest());
            final String script = scriptInclusion + menuVariableScript;
            header = header.insert(headerPart1, script);
        }
        final Node head_row = headerNode.elementAt(0);
        head_row.setChildren(getNodeList(header.toString()));
        return head_row.toHtml();
    }
    
    private static String getSearchRowHtml(final TableViewModel viewModel, final TableTransformerContext transContext, final ExtractTemplateHeaderCols visitor, final String[] headerColumns, final NodeList searchColumn, final ColumnProperties[] columnProperties) throws Exception {
        final String viewName = transContext.getViewContext().getUniqueId();
        final int noOfColumnsInTemplate = visitor.getNoOfColumnsInTemplate();
        final int noOfPreColumnsInTemplate = visitor.getNoOfPreColumnsInTemplate();
        final int noOfPostColumnsInTemplate = visitor.getNoOfPostColumnsInTempate();
        final Map columnOrderInTemplate = visitor.getColumnOrderInTemplate();
        final boolean isSearchPresent = transContext.isSearchEnabled() && transContext.getSearchValue() != null && transContext.getSearchValue() != "";
        final ExtractTemplateSearchRowCols visitor2 = new ExtractTemplateSearchRowCols(viewName, isSearchPresent, noOfColumnsInTemplate, noOfPreColumnsInTemplate, noOfPostColumnsInTemplate, columnOrderInTemplate);
        searchColumn.visitAllNodesWith((NodeVisitor)visitor2);
        final StringBuilder searchRow = new StringBuilder();
        final Map templateHtmlSearchRowCols = visitor2.getTemplateHtmlSearchRowCols();
        final Map preColumnsInTemplate = visitor2.getPreColumnsInTemplate();
        final Map postColumnsInTemplate = visitor2.getPostColumnsInTemplate();
        final Object[] viewColumns = viewModel.getViewColumns();
        for (int i = 1; i <= noOfPreColumnsInTemplate; ++i) {
            final String value = preColumnsInTemplate.get(i);
            searchRow.append(value);
        }
        for (int i = 0; i < viewColumns.length; ++i) {
            final String columnName = (String)((Object[])viewColumns[i])[0];
            String replaceBy = templateHtmlSearchRowCols.get(columnName);
            final boolean searchEnabled = columnProperties[i].isSearchEnabled;
            if (columnName != null && replaceBy == null) {
                if (searchEnabled) {
                    replaceBy = "<td class=\"TableHeader\"><input  class=\"tableSpotSearch\" type=\"text\"  onkeypress=\"return TableDOMModel.searchEnterKeyLis(this,event);\" name=\"" + IAMEncoder.encodeHTMLAttribute(columnName) + "\"></input></td> ";
                }
                else {
                    replaceBy = "<td class=\"TableHeader\"></td> ";
                }
            }
            if (replaceBy != null && isSearchPresent) {
                final String display = headerColumns[i];
                if (display.equals(transContext.getDisplayName()) && viewName.equals(transContext.getViewContext().getUniqueId())) {
                    final String searchVal = transContext.getSearchValue();
                    if (searchVal != null) {
                        final int startindex = replaceBy.indexOf("type=\"text\"");
                        replaceBy = replaceBy.substring(0, startindex - 1) + " value=\"" + searchVal + "\" " + replaceBy.substring(startindex, replaceBy.length());
                    }
                }
            }
            if (replaceBy == null) {
                replaceBy = "";
            }
            searchRow.append(replaceBy);
        }
        for (int i = 1; i <= noOfPostColumnsInTemplate; ++i) {
            searchRow.append(postColumnsInTemplate.get(i));
        }
        final Node search_row = searchColumn.elementAt(0);
        if (searchRow != null && searchRow.length() > 0 && search_row != null) {
            search_row.setChildren(getNodeList(searchRow.toString()));
            return search_row.toHtml();
        }
        return "";
    }
    
    private static String getRowHtmlData(final ViewContext viewContext, final TableViewModel viewModel, final ColumnProperties[] columnProperties, final ExtractTemplateHeaderCols visitor, final NodeList nl) throws Exception {
        final String viewName = viewContext.getUniqueId();
        final int noOfColumnsInTemplate = visitor.getNoOfColumnsInTemplate();
        final int noOfPreColumnsInTemplate = visitor.getNoOfPreColumnsInTemplate();
        final int noOfPostColumnsInTemplate = visitor.getNoOfPostColumnsInTempate();
        final Map columnOrderInTemplate = visitor.getColumnOrderInTemplate();
        final Node rowList = nl.elementAt(0);
        FillTable.OUT.log(Level.FINER, "Parsing the data for row {0}", rowList.toHtml());
        final ExtractTemplateRowCols rowVisitor = new ExtractTemplateRowCols(viewName, noOfColumnsInTemplate, noOfPreColumnsInTemplate, noOfPostColumnsInTemplate, columnOrderInTemplate);
        nl.visitAllNodesWith((NodeVisitor)rowVisitor);
        final Map templateHtmlRowCols = rowVisitor.getTemplateHtmlRowCols();
        final Map preColumnsInTemplate = rowVisitor.getPreColumnsInTemplate();
        final Object[] columns = viewModel.getViewColumns();
        String tempValue2 = "";
        int k = 1;
        final StringBuilder output = new StringBuilder();
        final TableTransformerContext transContext = viewModel.getTableTransformerContext();
        final TableNavigatorModel tableModel = (TableNavigatorModel)viewModel.getTableModel();
        final TableIterator iterator = viewModel.getNewTableIterator();
        while (iterator.nextRow()) {
            boolean isLastColumn = false;
            final StringBuilder rowlist = new StringBuilder();
            for (int j = 1; j <= noOfPreColumnsInTemplate; ++j) {
                String value = preColumnsInTemplate.get(j);
                if (value != null) {
                    value = checkRequestTemplates(value, viewContext.getRequest());
                    value = checkDollarRequestParams(checkDollarProperties(value), viewContext);
                    rowlist.append(value);
                }
            }
            int j = 1;
            String temp = "";
            iterator.setCurrentColumn(-1);
            while (iterator.nextColumn()) {
                iterator.initTransCtxForCurrentCell("Cell");
                final int columnindex = transContext.getColumnIndex();
                Object obj = null;
                if (columnindex != -1) {
                    obj = tableModel.getValueAt(transContext.getRowIndex(), columnindex);
                }
                final HashMap props = transContext.getRenderedAttributes();
                if (props.size() > 0) {
                    temp = CellRenderer.getHTML(props, viewModel);
                }
                String columnName = columnName = (String)((Object[])columns[j - 1])[0];
                if (j >= columns.length) {
                    isLastColumn = true;
                }
                FillTable.OUT.log(Level.FINER, "column to get data for {0} isLastColumn {1}", new Object[] { columnName, isLastColumn });
                String htmlrow = templateHtmlRowCols.get(columnName);
                if (htmlrow == null) {
                    htmlrow = templateHtmlRowCols.get("DefaultColumn");
                }
                String rowValue = temp;
                String filledHtml = "";
                if (rowValue == null) {
                    rowValue = "";
                }
                else {
                    final NodeList temp2 = getNodeList(htmlrow);
                    if (isLastColumn && ((Tag)temp2.elementAt(0)).getAttribute("colspan") == null) {
                        ((Tag)temp2.elementAt(0)).setAttribute("colspan", "" + (noOfPostColumnsInTemplate + 1));
                    }
                    final RowFillingVisitor visitor2 = new RowFillingVisitor(rowValue);
                    temp2.visitAllNodesWith((NodeVisitor)visitor2);
                    filledHtml = temp2.toHtml();
                }
                tempValue2 = columnName;
                final NodeList tempList = getNodeList(filledHtml);
                String cssClassName = "";
                cssClassName = columnProperties[j - 1].cssClassName;
                final DefaultClassSettingVisitor visitor3 = new DefaultClassSettingVisitor(cssClassName);
                tempList.visitAllNodesWith((NodeVisitor)visitor3);
                filledHtml = tempList.toHtml();
                filledHtml = checkRequestTemplates(filledHtml, viewContext.getRequest());
                filledHtml = checkDollarRequestParams(checkDollarProperties(filledHtml), viewContext);
                rowlist.append(filledHtml);
                ++j;
            }
            final NodeList row_html = getNodeList(rowlist.toString());
            final Node row_value = nl.elementAt(0);
            row_value.setChildren(row_html);
            final String row = row_value.toHtml();
            final boolean isEvenRow = k % 2 == 0;
            final ClassSettingRowVisitor visitor4 = new ClassSettingRowVisitor(k);
            final NodeList row_htm = getNodeList(row);
            row_htm.visitAllNodesWith((NodeVisitor)visitor4);
            output.append(row_htm.toHtml());
            ++k;
        }
        return output.toString();
    }
    
    public static NodeList getTemplate(final String viewName, final ViewContext viewContext) throws Exception {
        String templateString = FillTable.tablehtmlmap.get(viewName);
        templateString = parseSumEntries(templateString, viewContext);
        final NodeList viewlist = getNodeList(templateString);
        return viewlist;
    }
    
    public static NodeList parseNavigation(final String viewName, NodeList template, final TableViewModel viewModel) throws Exception {
        String cuttedTemplate = template.toHtml();
        final ViewContext viewContext = viewModel.getTableTransformerContext().getViewContext();
        final NavigationConfig navigationConfig = viewModel.getNavigationConfig();
        final boolean isNoCount = NavigationConfig.getNocount(viewContext).equals("true");
        final HasAttributeFilter navigatorFilter = new HasAttributeFilter();
        navigatorFilter.setAttributeName("mc:row");
        navigatorFilter.setAttributeValue("mc:navigator");
        final NodeList navigatorNodeList = template.extractAllNodesThatMatch((NodeFilter)navigatorFilter, true);
        if (navigatorNodeList.size() != 0) {
            viewContext.setStateParameter("NAVIGATIONPRESENT", (Object)"true");
            final Node NavigatorNode = navigatorNodeList.elementAt(0);
            final int firstNavig = Integer.parseInt(navigationConfig.getRangeList().get(0).toString());
            if ("ifRequired".equalsIgnoreCase(viewContext.getModel().getFeatureValue("showNavigation")) && firstNavig >= navigationConfig.getTotalRecords()) {
                final String navig = NavigatorNode.toHtml();
                final int index = cuttedTemplate.indexOf(navig);
                if (index == -1) {
                    return getNodeList(cuttedTemplate);
                }
                cuttedTemplate = cuttedTemplate.substring(0, index) + cuttedTemplate.substring(index + navig.length(), cuttedTemplate.length());
                return getNodeList(checkDollarRequestParams(checkDollarProperties(cuttedTemplate), viewContext));
            }
            else {
                int in = cuttedTemplate.indexOf("${startRow}");
                long no = 0L;
                try {
                    no = navigationConfig.getFromIndex();
                }
                catch (final Exception e) {
                    throw new Exception("Enable navigation in ACTable view config" + viewName);
                }
                if (in != -1) {
                    cuttedTemplate = cuttedTemplate.substring(0, in) + no + cuttedTemplate.substring(in + 11, cuttedTemplate.length());
                }
                in = cuttedTemplate.indexOf("${lastRow}");
                no = navigationConfig.getToIndex();
                if (in != -1) {
                    cuttedTemplate = cuttedTemplate.substring(0, in) + no + cuttedTemplate.substring(in + 10, cuttedTemplate.length());
                }
                in = cuttedTemplate.indexOf("${totalRows}");
                no = navigationConfig.getTotalRecords();
                if (in != -1) {
                    cuttedTemplate = cuttedTemplate.substring(0, in) + no + cuttedTemplate.substring(in + 12, cuttedTemplate.length());
                }
                final HasAttributeFilter imagefilter = new HasAttributeFilter();
                imagefilter.setAttributeName("mc:firstLink");
                imagefilter.setAttributeValue("true");
                final NodeList firstLinkImage = navigatorNodeList.extractAllNodesThatMatch((NodeFilter)imagefilter, true);
                String toBeReplaced = "";
                if (firstLinkImage.elementAt(0) != null) {
                    toBeReplaced = firstLinkImage.elementAt(0).toHtml();
                }
                Node node = firstLinkImage.elementAt(0);
                node.accept((NodeVisitor)new NavigationClassSettingVisitor("mc:firstLink", "true", "disable"));
                String replace = node.toHtml();
                if (navigationConfig.getPageNumber() > 1 && !isNoCount) {
                    replace = "<a href=\"javascript:showRange('" + IAMEncoder.encodeJavaScript(viewContext.getUniqueId()) + "','" + navigationConfig.getFirstPageIndex() + "','1')\">" + toBeReplaced + "</a>";
                }
                if (!toBeReplaced.equals("")) {
                    in = cuttedTemplate.indexOf(toBeReplaced);
                    if (in != -1) {
                        cuttedTemplate = cuttedTemplate.substring(0, in) + replace + cuttedTemplate.substring(in + toBeReplaced.length(), cuttedTemplate.length());
                    }
                }
                imagefilter.setAttributeName("mc:previousLink");
                imagefilter.setAttributeValue("true");
                final NodeList previousLinkImage = navigatorNodeList.extractAllNodesThatMatch((NodeFilter)imagefilter, true);
                toBeReplaced = "";
                if (previousLinkImage.elementAt(0) != null) {
                    toBeReplaced = previousLinkImage.elementAt(0).toHtml();
                }
                node = previousLinkImage.elementAt(0);
                node.accept((NodeVisitor)new NavigationClassSettingVisitor("mc:previousLink", "true", "disable"));
                replace = node.toHtml();
                if (navigationConfig.getPageNumber() > 1) {
                    replace = "<a href=\"javascript:showRange('" + IAMEncoder.encodeJavaScript(viewContext.getUniqueId()) + "','" + navigationConfig.getPreviousPageIndex() + "','" + (navigationConfig.getPageNumber() - 1) + "')\">" + toBeReplaced + "</a>";
                }
                if (isNoCount && navigationConfig.getPageNumber() != 1) {
                    replace = "<a href=\"javascript:showRange('" + IAMEncoder.encodeJavaScript(viewName) + "','" + navigationConfig.getPreviousPageIndex() + "','" + (navigationConfig.getPageNumber() - 1) + "')\">" + toBeReplaced + "</a>";
                }
                else if (isNoCount) {
                    replace = toBeReplaced;
                }
                if (!toBeReplaced.equals("")) {
                    in = cuttedTemplate.indexOf(toBeReplaced);
                    if (in != -1) {
                        cuttedTemplate = cuttedTemplate.substring(0, in) + replace + cuttedTemplate.substring(in + toBeReplaced.length(), cuttedTemplate.length());
                    }
                }
                imagefilter.setAttributeName("mc:nextLink");
                imagefilter.setAttributeValue("true");
                final NodeList nextLinkImage = navigatorNodeList.extractAllNodesThatMatch((NodeFilter)imagefilter, true);
                toBeReplaced = "";
                if (nextLinkImage.elementAt(0) != null) {
                    toBeReplaced = nextLinkImage.elementAt(0).toHtml();
                }
                node = nextLinkImage.elementAt(0);
                node.accept((NodeVisitor)new NavigationClassSettingVisitor("mc:nextLink", "true", "disable"));
                replace = node.toHtml();
                if (navigationConfig.getPageNumber() < navigationConfig.getTotalPages()) {
                    replace = "<a href=\"javascript:showRange('" + IAMEncoder.encodeJavaScript(viewContext.getUniqueId()) + "','" + navigationConfig.getNextPageIndex() + "','" + (navigationConfig.getPageNumber() + 1) + "')\">" + toBeReplaced + "</a>";
                }
                if (isNoCount && navigationConfig.getPageLength() < ((TableModel)viewModel.getTableModel()).getRowCount()) {
                    final Long sum = navigationConfig.getPageLength() + navigationConfig.getFromIndex();
                    replace = "<a href=\"javascript:showRange('" + IAMEncoder.encodeJavaScript(viewName) + "','" + sum + "')\">" + toBeReplaced + "</a>";
                }
                else if (isNoCount) {
                    replace = toBeReplaced;
                }
                if (!toBeReplaced.equals("")) {
                    in = cuttedTemplate.indexOf(toBeReplaced);
                    if (in != -1) {
                        cuttedTemplate = cuttedTemplate.substring(0, in) + replace + cuttedTemplate.substring(in + toBeReplaced.length(), cuttedTemplate.length());
                    }
                }
                imagefilter.setAttributeName("mc:lastLink");
                imagefilter.setAttributeValue("true");
                final NodeList lastLinkImage = navigatorNodeList.extractAllNodesThatMatch((NodeFilter)imagefilter, true);
                if (lastLinkImage.elementAt(0) != null) {
                    toBeReplaced = lastLinkImage.elementAt(0).toHtml();
                }
                node = lastLinkImage.elementAt(0);
                node.accept((NodeVisitor)new NavigationClassSettingVisitor("mc:lastLink", "true", "disable"));
                replace = node.toHtml();
                if (navigationConfig.getPageNumber() < navigationConfig.getTotalPages()) {
                    replace = "<a href=\"javascript:showRange('" + IAMEncoder.encodeJavaScript(viewContext.getUniqueId()) + "','" + navigationConfig.getLastPageIndex() + "','" + navigationConfig.getTotalPages() + "')\">" + toBeReplaced + "</a>";
                }
                if (!toBeReplaced.equals("")) {
                    in = cuttedTemplate.indexOf(toBeReplaced);
                    if (in != -1) {
                        cuttedTemplate = cuttedTemplate.substring(0, in) + replace + cuttedTemplate.substring(in + toBeReplaced.length(), cuttedTemplate.length());
                    }
                }
                if (cuttedTemplate != null && cuttedTemplate != "") {
                    template = getNodeList(checkDollarRequestParams(checkDollarProperties(cuttedTemplate), viewContext));
                }
                final NavigationVisitor navigVisitor = new NavigationVisitor(viewContext, navigationConfig);
                template.visitAllNodesWith((NodeVisitor)navigVisitor);
                template = handleDivSelectNavig(template, navigationConfig);
            }
        }
        return template;
    }
    
    public static String getCustomReplacedSumString(final ViewContext vc) throws Exception {
        final String sumRow = WebClientUtil.getAuthImpl().getListViewTotalHtmlString(vc);
        return checkDollarRequestParams(checkDollarProperties(parseSumEntries(sumRow, vc)), vc);
    }
    
    public static String checkDollarProperties(String string) throws Exception {
        final StringBuilder buff = new StringBuilder();
        while (string.indexOf("${I18N||") != -1) {
            final int index = string.indexOf("${I18N||");
            final int index2 = string.indexOf("}", index);
            buff.append(string.substring(0, index));
            final String dollarstuff = string.substring(index, index2 + 1);
            string = string.substring(index2 + 1);
            buff.append(getDollarI18N(dollarstuff));
        }
        buff.append(string);
        return buff.toString();
    }
    
    private static String getDollarI18N(final String str) throws Exception {
        final String data = str.substring(2, str.length() - 1);
        final String[] datas = data.split("\\|\\|", 2);
        return I18N.getMsg(datas[1], new Object[0]);
    }
    
    private static NodeList handleDivSelectNavig(final NodeList template, final NavigationConfig config) throws Exception {
        String tillRefer = template.toHtml();
        final String currentlySelectedPageLength = String.valueOf(config.getPageLength());
        final List rangeList = config.getRangeList();
        final int size = rangeList.size();
        final HashMap map = new HashMap();
        for (int i = 0; i < size; ++i) {
            final String replace = rangeList.get(i).toString();
            final String toreplace = "pageLength" + (i + 1);
            map.put(toreplace, replace);
        }
        map.put("currentlySelectedPageLength", currentlySelectedPageLength);
        tillRefer = getReplacedString(tillRefer, map);
        return getNodeList(tillRefer);
    }
    
    public static String checkDollarRequestParams(String string, final ViewContext vc) throws Exception {
        final StringBuilder buff = new StringBuilder();
        while (string.indexOf("${REQ||") != -1) {
            final int index = string.indexOf("${REQ||");
            final int index2 = string.indexOf("}", index);
            buff.append(string.substring(0, index));
            final String dollarstuff = string.substring(index, index2 + 1);
            string = string.substring(index2 + 1);
            buff.append(getDollarReqParams(dollarstuff, vc));
        }
        buff.append(string);
        return buff.toString();
    }
    
    private static String getDollarReqParams(final String str, final ViewContext vc) {
        try {
            final String data = str.substring(2, str.length() - 1);
            final String[] datas = data.split("\\|\\|", 2);
            Object ret = "";
            ret = vc.getRequest().getParameter(datas[1]);
            if (ret == null) {
                ret = vc.getRequest().getAttribute(datas[1]);
            }
            if (ret == null) {
                final TemplateAPI.VariableHandler varhandler = TemplateAPI.getVariableHandler("TABLESUMHANDLER");
                ret = varhandler.getVariableValue(datas[1], 0, (Object)vc);
            }
            return ret.toString();
        }
        catch (final Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    private static String getReplacedString(String temp, final HashMap map) {
        for (final Object toreplace : map.keySet()) {
            final String toReplace = "${" + toreplace.toString() + "}";
            final String replace = map.get(toreplace).toString();
            final int index = temp.indexOf(toReplace);
            if (index != -1) {
                temp = temp.substring(0, index) + replace + temp.substring(index + toReplace.length(), temp.length());
            }
        }
        return temp;
    }
    
    public static String checkRequestTemplates(final String str, final HttpServletRequest req) throws Exception {
        String scheme = req.getScheme();
        if ("http".equals(scheme) && req.getServerPort() == 443) {
            scheme = "https";
        }
        final Object[] compiledInfo = TemplateAPI.getCompiledInfo(str, TemplateAPI.simplePattern);
        final String servername = req.getServerName();
        final int port = req.getServerPort();
        final String serverpath = servername + ":" + port;
        final String temp = TemplateAPI.getFilledString(compiledInfo, (TemplateAPI.VariableHandler)null, (Object)new Object[][] { { "REQ||SCHEME", scheme }, { "REQ||LOCAL", serverpath } });
        return temp;
    }
    
    public static String parseSumEntries(final String template, final ViewContext viewContext) throws ParserException {
        final NodeList nl = getNodeList(template);
        nl.visitAllNodesWith((NodeVisitor)new SumSettingVisitor(viewContext));
        return nl.toHtml();
    }
    
    private static boolean checkSumHtmlAndInitSumHtml(final NodeList nl, final StringBuilder sumhtml) {
        HasAttributeFilter filter = new HasAttributeFilter();
        filter.setAttributeName("mc:row");
        filter.setAttributeValue("sumRow");
        NodeList result = nl.extractAllNodesThatMatch((NodeFilter)filter, true);
        if (result.size() == 0) {
            FillTable.OUT.log(Level.FINER, "No default sum row given");
            return false;
        }
        if (result.size() > 1) {
            FillTable.OUT.log(Level.FINER, "has more than one rows with mc:row=sumrow");
        }
        final Node sumNode = result.elementAt(0);
        final NodeList newlist = sumNode.getChildren();
        final NodeList list = new NodeList(sumNode);
        filter = new HasAttributeFilter("mc:column", "DefaultSumColumn");
        result = newlist.extractAllNodesThatMatch((NodeFilter)filter, true);
        for (int i = 0; i < newlist.size(); ++i) {
            list.remove(result.elementAt(i));
        }
        sumhtml.append(list.toHtml());
        if (result.size() > 1) {
            FillTable.OUT.log(Level.FINER, "more than one defaultsumcolumn specified");
        }
        if (result.size() == 0) {
            FillTable.OUT.log(Level.FINER, "No default sum column given");
            return false;
        }
        final Node defaultColumn = result.elementAt(0);
        return true;
    }
    
    public static String removeSumHtmlIfPresent(final NodeList nl, final boolean justRemove) {
        final HasAttributeFilter filter = new HasAttributeFilter();
        filter.setAttributeName("mc:row");
        filter.setAttributeValue("sumRow");
        final NodeList result = nl.extractAllNodesThatMatch((NodeFilter)filter, true);
        if (result.size() == 0) {
            FillTable.OUT.log(Level.FINER, "No default sum row given");
            return nl.toHtml();
        }
        if (result.size() > 1) {
            FillTable.OUT.log(Level.FINER, "has more than one rows with mc:row=sumrow");
        }
        final Node sumNode = result.elementAt(0);
        final String sumNodeString = sumNode.toHtml();
        String nodeString = nl.toHtml();
        final int index = nodeString.indexOf(sumNodeString);
        String dollar = "";
        if (!justRemove) {
            dollar = "$${sumrowhtml}$$";
        }
        nodeString = nodeString.substring(0, index) + dollar + nodeString.substring(index + sumNodeString.length(), nodeString.length());
        return nodeString;
    }
    
    private static String[] getSumHtmlArray(final String tr) {
        final NodeList sum = getNodeList(tr);
        final HasAttributeFilter filter = new HasAttributeFilter("mc:column", "DefaultSumColumn");
        final String col = sum.extractAllNodesThatMatch((NodeFilter)filter, true).toHtml();
        final int index = tr.indexOf(col);
        return new String[] { tr.substring(0, index), col, tr.substring(index + col.length(), tr.length()) };
    }
    
    private static void updateSumRow(final StringBuilder sumrow, final String col, final String colName, final ViewContext vc) throws Exception {
        final TableViewModel viewmodel = (TableViewModel)vc.getViewModel();
        if (!(viewmodel.getTableModel() instanceof TableDatasetModel)) {
            return;
        }
        boolean flag = false;
        final TableDatasetModel tdsm = (TableDatasetModel)viewmodel.getTableModel();
        if (!tdsm.getTotalSumMap().containsKey(colName) && !tdsm.getViewSumMap().containsKey(colName)) {
            flag = true;
        }
        if (colName == null || flag) {
            final Node ls = getNodeList(col).elementAt(0);
            ls.setChildren(new NodeList((Node)new TextNode("&nbsp;")));
            final String emptyTd = ls.toHtml();
            sumrow.append(emptyTd);
            return;
        }
        final NodeList ls2 = getNodeList(col);
        ls2.visitAllNodesWith((NodeVisitor)new SumVisitor(colName, vc));
        sumrow.append(ls2.toHtml());
    }
    
    static {
        OUT = Logger.getLogger(FillTable.class.getName());
        FillTable.tablehtmlmap = new HashMap(149, 0.75f);
        FillTable.tabhtmlmap = new HashMap(149, 0.75f);
    }
    
    static class SumVisitor extends NodeVisitor
    {
        String colName;
        ViewContext viewContext;
        
        public SumVisitor(final String col, final ViewContext vc) {
            this.colName = "";
            this.viewContext = null;
            this.colName = col;
            this.viewContext = vc;
        }
        
        public void visitTag(final Tag tag) {
            if (tag.getAttribute("mc:Tsum") != null) {
                tag.setAttribute("mc:Tsum", this.colName);
            }
            if (tag.getAttribute("mc:Vsum") != null) {
                tag.setAttribute("mc:Vsum", this.colName);
            }
        }
    }
    
    static class ColumnProperties
    {
        public String columnAlias;
        public boolean isSortEnabled;
        public boolean isSearchEnabled;
        public String headerCss;
        public String cssClassName;
        
        public ColumnProperties(final String columnAlias) {
            this.isSortEnabled = false;
            this.isSearchEnabled = false;
            this.headerCss = null;
            this.cssClassName = null;
            this.columnAlias = columnAlias;
        }
        
        public void setIsSortEnabled(final boolean isSortEnabled) {
            this.isSortEnabled = isSortEnabled;
        }
        
        public void setIsSearchEnabled(final boolean isSearchEnabled) {
            this.isSearchEnabled = isSearchEnabled;
        }
        
        public void setHeaderCss(final String headerCss) {
            this.headerCss = headerCss;
        }
        
        public void setCssClassName(final String cssClassName) {
            this.cssClassName = cssClassName;
        }
    }
}
