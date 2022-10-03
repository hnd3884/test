package com.zoho.dddiff;

import java.util.logging.Level;
import org.w3c.dom.Document;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Text;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import org.w3c.dom.Element;
import java.util.List;
import java.util.logging.Logger;

class DataDictionaryDiffGenerator
{
    private static final Logger LOGGER;
    private List<Element> oldDocuments;
    private List<Element> newDocuments;
    private List changes;
    
    DataDictionaryDiffGenerator() {
        this.oldDocuments = new ArrayList<Element>();
        this.newDocuments = new ArrayList<Element>();
        this.changes = new ArrayList();
    }
    
    private Element getElement(final NodeList nodes, final String elementName) {
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            if (node.getNodeType() == 1) {
                final Element element = (Element)node;
                if (element.getTagName().equals(elementName)) {
                    return element;
                }
            }
        }
        return null;
    }
    
    private Element getElementByName(final NodeList nodes, final String elementName, final String name) {
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            if (node.getNodeType() == 1) {
                final Element element = (Element)node;
                final String tagName = element.getTagName();
                if (tagName.equals(elementName)) {
                    final NamedNodeMap attris = element.getAttributes();
                    final Attr nameattr = (Attr)attris.getNamedItem("name");
                    final String Name = nameattr.getValue();
                    if (Name.equals(name)) {
                        return element;
                    }
                }
            }
        }
        return null;
    }
    
    private Element getElementByValue(final NodeList nodes, final String elementName, final String value) {
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element element = (Element)node;
                    if (element.getTagName().equals(elementName)) {
                        final NodeList innernodes = element.getChildNodes();
                        for (int j = 0; j < innernodes.getLength(); ++j) {
                            final Node innernode = innernodes.item(j);
                            if (innernode.getNodeType() == 3 && ((Text)innernode).getData().equals(value)) {
                                return element;
                            }
                        }
                        break;
                    }
                    break;
                }
            }
        }
        return null;
    }
    
    private String getTagValue(final NodeList nodes, final String tagName) {
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            if (node.getNodeType() == 1) {
                final Element element = (Element)node;
                if (element.getTagName().equals(tagName)) {
                    final NodeList innerNodes = element.getChildNodes();
                    for (int j = 0; j < innerNodes.getLength(); ++j) {
                        final Node innerNode = innerNodes.item(j);
                        if (innerNode.getNodeType() == 3) {
                            return ((Text)innerNode).getData();
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private List<String> getValues(final NodeList nodes, final String tagName) {
        final List<String> Columns = new ArrayList<String>();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element element = (Element)node;
                    if (element.getTagName().equals(tagName)) {
                        final NodeList innernodes = element.getChildNodes();
                        for (int j = 0; j < innernodes.getLength(); ++j) {
                            final Node innernode = innernodes.item(j);
                            if (innernode.getNodeType() == 3) {
                                Columns.add(((Text)innernode).getData());
                            }
                        }
                        break;
                    }
                    break;
                }
            }
        }
        return Columns;
    }
    
    private List<String> getNames(final NodeList nodes, final String tagName) {
        final List<String> Names = new ArrayList<String>();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            if (node.getNodeType() == 1) {
                final Element nodeElement = (Element)node;
                if (nodeElement.getTagName().equals(tagName)) {
                    final NamedNodeMap attris = nodeElement.getAttributes();
                    final Attr nameattr = (Attr)attris.getNamedItem("name");
                    final String columnname = nameattr.getValue();
                    Names.add(columnname);
                }
            }
        }
        return Names;
    }
    
    private Map getFKColumns(final NodeList nodes) {
        final Map fkColumns = new HashMap();
        for (int j = 0; j < nodes.getLength(); ++j) {
            final Node node = nodes.item(j);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    final String tagName = nodeElement.getTagName();
                    if (tagName.equals("fk-column")) {
                        final NodeList nodelist = nodeElement.getChildNodes();
                        String localcol = null;
                        String refcol = null;
                        for (int i = 0; i < nodelist.getLength(); ++i) {
                            final Node nod = nodelist.item(i);
                            if (nod.getNodeType() == 1) {
                                final Element element = (Element)nod;
                                if (element.getTagName().equals("fk-local-column")) {
                                    final NodeList innernodes = element.getChildNodes();
                                    for (int k = 0; k < innernodes.getLength(); ++k) {
                                        final Node innernode = innernodes.item(k);
                                        if (innernode.getNodeType() == 3) {
                                            localcol = ((Text)innernode).getData();
                                        }
                                    }
                                }
                                else if (element.getTagName().equals("fk-reference-column")) {
                                    final NodeList innernodes = element.getChildNodes();
                                    for (int k = 0; k < innernodes.getLength(); ++k) {
                                        final Node innernode = innernodes.item(k);
                                        if (innernode.getNodeType() == 3) {
                                            refcol = ((Text)innernode).getData();
                                        }
                                    }
                                }
                            }
                        }
                        fkColumns.put(localcol, refcol);
                        break;
                    }
                    break;
                }
            }
        }
        return fkColumns;
    }
    
    private Element getTableElement(final List<Element> elements, final String tableName) {
        for (final Element ele : elements) {
            final NodeList nodes = ele.getChildNodes();
            for (int i = 0; i < nodes.getLength(); ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == 1) {
                    final Element element = (Element)node;
                    final String tagName = element.getTagName();
                    if (tagName.equals("table")) {
                        final NamedNodeMap attris = element.getAttributes();
                        final Attr nameattr = (Attr)attris.getNamedItem("name");
                        final String Name = nameattr.getValue();
                        if (Name.equals(tableName)) {
                            return element;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private Element getDDElement(final List<Element> elements, final String ddName) {
        for (final Element ele : elements) {
            if (ele.getAttribute("name").equals(ddName)) {
                return ele;
            }
        }
        return null;
    }
    
    private void diffDDs() {
        final Map<String, List<Element>> oldDDName_Vs_Elements = new HashMap<String, List<Element>>();
        final Map<String, List<Element>> newDDName_Vs_Elements = new HashMap<String, List<Element>>();
        final List<String> oldTableNames = new ArrayList<String>();
        final List<String> newTableNames = new ArrayList<String>();
        for (final Element ele : this.oldDocuments) {
            final String ddName = ele.getAttribute("name");
            List<Element> ddElements;
            if (oldDDName_Vs_Elements.containsKey(ddName)) {
                ddElements = oldDDName_Vs_Elements.get(ddName);
            }
            else {
                ddElements = new ArrayList<Element>();
                oldDDName_Vs_Elements.put(ddName, ddElements);
            }
            ddElements.add(ele);
            final List<String> names = this.getNames(ele.getChildNodes(), "table");
            oldTableNames.addAll(names);
        }
        for (final Element ele : this.newDocuments) {
            final String ddName = ele.getAttribute("name");
            List<Element> ddElements;
            if (newDDName_Vs_Elements.containsKey(ddName)) {
                ddElements = newDDName_Vs_Elements.get(ddName);
            }
            else {
                ddElements = new ArrayList<Element>();
                newDDName_Vs_Elements.put(ddName, ddElements);
            }
            ddElements.add(ele);
            final List<String> names = this.getNames(ele.getChildNodes(), "table");
            newTableNames.addAll(names);
        }
        for (final String name : newDDName_Vs_Elements.keySet()) {
            if (oldDDName_Vs_Elements.keySet().contains(name)) {
                final List<Element> oldDDElements = oldDDName_Vs_Elements.get(name);
                final List<Element> newDDElements = newDDName_Vs_Elements.get(name);
                final List<String> changedAttributes = new ArrayList<String>();
                final List<Element> changedOldElements = new ArrayList<Element>();
                final List<Element> changedNewElements = new ArrayList<Element>();
                String oldDcType = null;
                String newDCType = null;
                Element oldDcTypeDefinedElement = null;
                Element newDcTypeDefinedElement = null;
                final Iterator<Element> iterator4 = oldDDElements.iterator();
                while (iterator4.hasNext()) {
                    final Element oldDDElement = oldDcTypeDefinedElement = iterator4.next();
                    final String dcType = oldDDElement.getAttribute("dc-type");
                    if (!dcType.isEmpty()) {
                        oldDcType = dcType;
                        break;
                    }
                }
                final Iterator<Element> iterator5 = newDDElements.iterator();
                while (iterator5.hasNext()) {
                    final Element newDDElement = newDcTypeDefinedElement = iterator5.next();
                    final String dcType = newDDElement.getAttribute("dc-type");
                    if (!dcType.isEmpty()) {
                        newDCType = dcType;
                        break;
                    }
                }
                Label_0587: {
                    if (oldDcType == null) {
                        if (newDCType == null) {
                            break Label_0587;
                        }
                    }
                    else if (oldDcType.equals(newDCType)) {
                        break Label_0587;
                    }
                    changedAttributes.add("dc-type");
                    changedOldElements.add(oldDcTypeDefinedElement);
                    changedNewElements.add(newDcTypeDefinedElement);
                }
                String oldTemplateHandler = null;
                String newTemplateHandler = null;
                Element oldTemplateHandlerDefinedElement = null;
                Element newTemplateHandlerDefinedElement = null;
                final Iterator<Element> iterator6 = oldDDElements.iterator();
                while (iterator6.hasNext()) {
                    final Element oldDDElement2 = oldTemplateHandlerDefinedElement = iterator6.next();
                    final String templateHandler = oldDDElement2.getAttribute("template-meta-handler");
                    if (!templateHandler.isEmpty()) {
                        oldTemplateHandler = templateHandler;
                        break;
                    }
                }
                final Iterator<Element> iterator7 = newDDElements.iterator();
                while (iterator7.hasNext()) {
                    final Element newDDElement2 = newTemplateHandlerDefinedElement = iterator7.next();
                    final String templateHandler = newDDElement2.getAttribute("template-meta-handler");
                    if (!templateHandler.isEmpty()) {
                        newTemplateHandler = templateHandler;
                        break;
                    }
                }
                Label_0780: {
                    if (oldTemplateHandler == null) {
                        if (newTemplateHandler == null) {
                            break Label_0780;
                        }
                    }
                    else if (oldTemplateHandler.equals(newTemplateHandler)) {
                        break Label_0780;
                    }
                    changedAttributes.add("template-meta-handler");
                    changedOldElements.add(oldTemplateHandlerDefinedElement);
                    changedNewElements.add(newTemplateHandlerDefinedElement);
                }
                String oldDescription = null;
                String newDescription = null;
                Element oldDescriptionDefinedElement = null;
                Element newDescriptionDefinedElement = null;
                final Iterator<Element> iterator8 = oldDDElements.iterator();
                while (iterator8.hasNext()) {
                    final Element oldDDElement3 = oldDescriptionDefinedElement = iterator8.next();
                    final String description = this.getTagValue(oldDDElement3.getChildNodes(), "description");
                    if (description != null) {
                        oldDescription = description;
                        break;
                    }
                }
                final Iterator<Element> iterator9 = newDDElements.iterator();
                while (iterator9.hasNext()) {
                    final Element newDDElement3 = newDescriptionDefinedElement = iterator9.next();
                    final String description = this.getTagValue(newDDElement3.getChildNodes(), "description");
                    if (description != null) {
                        newDescription = description;
                        break;
                    }
                }
                Label_0975: {
                    if (oldDescription == null) {
                        if (newDescription == null) {
                            break Label_0975;
                        }
                    }
                    else if (oldDescription.equals(newDescription)) {
                        break Label_0975;
                    }
                    changedAttributes.add("description");
                    changedOldElements.add(oldDescriptionDefinedElement);
                    changedNewElements.add(newDescriptionDefinedElement);
                }
                if (changedAttributes.isEmpty()) {
                    continue;
                }
                final ModifiedElement modify = new ModifiedElement(name, changedAttributes, changedOldElements, changedNewElements);
                this.changes.add(modify);
            }
            else {
                final AddedElement add = new AddedElement(this.getDDElement(this.newDocuments, name), null, name, DataDictionaryDiff.ElementType.DD);
                this.changes.add(add);
            }
        }
        for (final String name : oldDDName_Vs_Elements.keySet()) {
            if (!newDDName_Vs_Elements.keySet().contains(name)) {
                final DeletedElement drop = new DeletedElement(this.getDDElement(this.oldDocuments, name), null, name, DataDictionaryDiff.ElementType.DD);
                this.changes.add(drop);
            }
        }
        for (final String tableName : newTableNames) {
            if (!oldTableNames.contains(tableName)) {
                final Element tableElement = this.getTableElement(this.newDocuments, tableName);
                final String ddName2 = ((Element)tableElement.getParentNode()).getAttribute("name");
                final AddedElement add2 = new AddedElement(tableElement, tableName, ddName2, DataDictionaryDiff.ElementType.TABLE);
                this.changes.add(add2);
            }
            else {
                final Element oldTable = this.getTableElement(this.oldDocuments, tableName);
                final Element newTable = this.getTableElement(this.newDocuments, tableName);
                this.diffTable(oldTable, newTable, tableName);
            }
        }
        for (final String tableName : oldTableNames) {
            if (!newTableNames.contains(tableName)) {
                final Element tableElement = this.getTableElement(this.oldDocuments, tableName);
                final String ddName2 = ((Element)tableElement.getParentNode()).getAttribute("name");
                final DeletedElement drop2 = new DeletedElement(tableElement, tableName, ddName2, DataDictionaryDiff.ElementType.TABLE);
                this.changes.add(drop2);
            }
        }
    }
    
    private void diffTable(final Element oldtableElement, final Element newtableElement, final String tableName) {
        final String ddName = ((Element)newtableElement.getParentNode()).getAttribute("name");
        final List<String> tableChanges = new ArrayList<String>();
        final String oldTableDisplayName = oldtableElement.getAttribute("display-name");
        final String newTableDisplayName = newtableElement.getAttribute("display-name");
        Label_0080: {
            if (oldTableDisplayName == null) {
                if (newTableDisplayName == null) {
                    break Label_0080;
                }
            }
            else if (oldTableDisplayName.equals(newTableDisplayName)) {
                break Label_0080;
            }
            tableChanges.add("display-name");
        }
        final String oldTableSystem = oldtableElement.getAttribute("system");
        final String newTableSystem = newtableElement.getAttribute("system");
        Label_0133: {
            if (oldTableSystem == null) {
                if (newTableSystem == null) {
                    break Label_0133;
                }
            }
            else if (oldTableSystem.equals(newTableSystem)) {
                break Label_0133;
            }
            tableChanges.add("system");
        }
        final String oldTableCreatetable = oldtableElement.getAttribute("createtable");
        final String newTableCreatetable = newtableElement.getAttribute("createtable");
        Label_0186: {
            if (oldTableCreatetable == null) {
                if (newTableCreatetable == null) {
                    break Label_0186;
                }
            }
            else if (oldTableCreatetable.equals(newTableCreatetable)) {
                break Label_0186;
            }
            tableChanges.add("createtable");
        }
        final String oldTableTemplate = oldtableElement.getAttribute("template");
        final String newTableTemplate = newtableElement.getAttribute("template");
        Label_0239: {
            if (oldTableTemplate == null) {
                if (newTableTemplate == null) {
                    break Label_0239;
                }
            }
            else if (oldTableTemplate.equals(newTableTemplate)) {
                break Label_0239;
            }
            tableChanges.add("template");
        }
        final String oldDcType = oldtableElement.getAttribute("dc-type");
        final String newDcType = newtableElement.getAttribute("dc-type");
        Label_0292: {
            if (oldDcType == null) {
                if (newDcType == null) {
                    break Label_0292;
                }
            }
            else if (oldDcType.equals(newDcType)) {
                break Label_0292;
            }
            tableChanges.add("dc-type");
        }
        final NodeList oldTablenodes = oldtableElement.getChildNodes();
        final NodeList newTablenodes = newtableElement.getChildNodes();
        final String oldTableDesc = this.getTagValue(oldTablenodes, "description");
        final String newTableDesc = this.getTagValue(newTablenodes, "description");
        Label_0361: {
            if (oldTableDesc == null) {
                if (newTableDesc == null) {
                    break Label_0361;
                }
            }
            else if (oldTableDesc.equals(newTableDesc)) {
                break Label_0361;
            }
            tableChanges.add("description");
        }
        final String oldDDName = ((Element)oldtableElement.getParentNode()).getAttribute("name");
        final String newDDName = ((Element)newtableElement.getParentNode()).getAttribute("name");
        Label_0430: {
            if (oldDDName == null) {
                if (newDDName == null) {
                    break Label_0430;
                }
            }
            else if (oldDDName.equals(newDDName)) {
                break Label_0430;
            }
            tableChanges.add("ddName");
        }
        if (!tableChanges.isEmpty()) {
            final ModifiedElement tableModify = new ModifiedElement(oldtableElement, newtableElement, tableName, ddName, DataDictionaryDiff.ElementType.TABLE, tableChanges);
            this.changes.add(tableModify);
        }
        final Element oldColumnsElement = this.getElement(oldTablenodes, "columns");
        final Element newColumnsElement = this.getElement(newTablenodes, "columns");
        final List<String> oldColumnNames = this.getNames(oldColumnsElement.getChildNodes(), "column");
        final List<String> newColumnNames = this.getNames(newColumnsElement.getChildNodes(), "column");
        for (final String columnName : newColumnNames) {
            if (oldColumnNames.contains(columnName)) {
                final Element oldColumnElement = this.getElementByName(oldColumnsElement.getChildNodes(), "column", columnName);
                final Element newColumnElement = this.getElementByName(newColumnsElement.getChildNodes(), "column", columnName);
                final List<String> changedAttributes = this.diffColumn(oldColumnElement, newColumnElement);
                if (changedAttributes.isEmpty()) {
                    continue;
                }
                final ModifiedElement modify = new ModifiedElement(oldColumnElement, newColumnElement, tableName, ddName, DataDictionaryDiff.ElementType.COLUMN, changedAttributes);
                this.changes.add(modify);
            }
            else {
                final AddedElement add = new AddedElement(this.getElementByName(newColumnsElement.getChildNodes(), "column", columnName), tableName, ddName, DataDictionaryDiff.ElementType.COLUMN);
                this.changes.add(add);
            }
        }
        for (final String columnName : oldColumnNames) {
            if (!newColumnNames.contains(columnName)) {
                final DeletedElement drop = new DeletedElement(this.getElementByName(oldColumnsElement.getChildNodes(), "column", columnName), tableName, ddName, DataDictionaryDiff.ElementType.COLUMN);
                this.changes.add(drop);
            }
        }
        final Element oldPK = this.getElement(oldTablenodes, "primary-key");
        final Element newPK = this.getElement(newTablenodes, "primary-key");
        if (oldPK != null && newPK != null) {
            final List<String> changedAttributes2 = this.diffPK(oldPK, newPK);
            if (!changedAttributes2.isEmpty()) {
                final ModifiedElement modify2 = new ModifiedElement(oldPK, newPK, tableName, ddName, DataDictionaryDiff.ElementType.PK, changedAttributes2);
                this.changes.add(modify2);
            }
        }
        final Element oldFKs = this.getElement(oldTablenodes, "foreign-keys");
        final Element newFKs = this.getElement(newTablenodes, "foreign-keys");
        this.diffFKS(oldFKs, newFKs, tableName, ddName);
        final Element oldUniquekeys = this.getElement(oldTablenodes, "unique-keys");
        final Element newUniquekeys = this.getElement(newTablenodes, "unique-keys");
        this.diffUKS(oldUniquekeys, newUniquekeys, tableName, ddName);
        final Element oldIndexes = this.getElement(oldTablenodes, "indexes");
        final Element newIndexes = this.getElement(newTablenodes, "indexes");
        this.diffIDXES(oldIndexes, newIndexes, tableName, ddName);
    }
    
    private List<String> diffColumn(final Element oldColumnElement, final Element newColumnElement) {
        final List<String> attributes = new ArrayList<String>();
        final String oldColumnDisplayName = oldColumnElement.getAttribute("display-name");
        final String newColumnDisplayName = newColumnElement.getAttribute("display-name");
        Label_0060: {
            if (oldColumnDisplayName == null) {
                if (newColumnDisplayName == null) {
                    break Label_0060;
                }
            }
            else if (oldColumnDisplayName.equals(newColumnDisplayName)) {
                break Label_0060;
            }
            attributes.add("display-name");
        }
        final String oldColumnDesc = this.getTagValue(oldColumnElement.getChildNodes(), "description");
        final String newColumnDesc = this.getTagValue(newColumnElement.getChildNodes(), "description");
        Label_0120: {
            if (oldColumnDesc == null) {
                if (newColumnDesc == null) {
                    break Label_0120;
                }
            }
            else if (oldColumnDesc.equals(newColumnDesc)) {
                break Label_0120;
            }
            attributes.add("description");
        }
        final String oldColumnDataType = this.getTagValue(oldColumnElement.getChildNodes(), "data-type");
        final String newColumnDataType = this.getTagValue(newColumnElement.getChildNodes(), "data-type");
        if (!oldColumnDataType.equals(newColumnDataType)) {
            attributes.add("data-type");
        }
        final String oldColumnMaxSize = this.getTagValue(oldColumnElement.getChildNodes(), "max-size");
        final String newColumnMaxSize = this.getTagValue(newColumnElement.getChildNodes(), "max-size");
        Label_0227: {
            if (oldColumnMaxSize == null) {
                if (newColumnMaxSize == null) {
                    break Label_0227;
                }
            }
            else if (oldColumnMaxSize.equals(newColumnMaxSize)) {
                break Label_0227;
            }
            attributes.add("max-size");
        }
        final String oldColumnPrecision = this.getTagValue(oldColumnElement.getChildNodes(), "precision");
        final String newColumnPrecision = this.getTagValue(newColumnElement.getChildNodes(), "precision");
        Label_0287: {
            if (oldColumnPrecision == null) {
                if (newColumnPrecision == null) {
                    break Label_0287;
                }
            }
            else if (oldColumnPrecision.equals(newColumnPrecision)) {
                break Label_0287;
            }
            attributes.add("precision");
        }
        final String oldColumnDefaultValue = this.getTagValue(oldColumnElement.getChildNodes(), "default-value");
        final String newColumnDefaultValue = this.getTagValue(newColumnElement.getChildNodes(), "default-value");
        Label_0347: {
            if (oldColumnDefaultValue == null) {
                if (newColumnDefaultValue == null) {
                    break Label_0347;
                }
            }
            else if (oldColumnDefaultValue.equals(newColumnDefaultValue)) {
                break Label_0347;
            }
            attributes.add("default-value");
        }
        final String oldColumnNullableValue = this.getTagValue(oldColumnElement.getChildNodes(), "nullable");
        final String newColumnNullableValue = this.getTagValue(newColumnElement.getChildNodes(), "nullable");
        Label_0407: {
            if (oldColumnNullableValue == null) {
                if (newColumnNullableValue == null) {
                    break Label_0407;
                }
            }
            else if (oldColumnNullableValue.equals(newColumnNullableValue)) {
                break Label_0407;
            }
            attributes.add("nullable");
        }
        final String oldColumnUniqueValue = this.getTagValue(oldColumnElement.getChildNodes(), "unique");
        final String newColumnUniqueValue = this.getTagValue(newColumnElement.getChildNodes(), "unique");
        Label_0467: {
            if (oldColumnUniqueValue == null) {
                if (newColumnUniqueValue == null) {
                    break Label_0467;
                }
            }
            else if (oldColumnUniqueValue.equals(newColumnUniqueValue)) {
                break Label_0467;
            }
            attributes.add("unique");
        }
        final Element oldColumnAllowedValues = this.getElement(oldColumnElement.getChildNodes(), "allowed-values");
        final Element newColumnAllowedValues = this.getElement(newColumnElement.getChildNodes(), "allowed-values");
        if (oldColumnAllowedValues == null && newColumnAllowedValues != null) {
            attributes.add("allowed-values");
        }
        else if (oldColumnAllowedValues != null && newColumnAllowedValues == null) {
            attributes.add("allowed-values");
        }
        else if (oldColumnAllowedValues != null && newColumnAllowedValues != null) {
            final boolean isChanged = this.diffAllowedValues(oldColumnAllowedValues, newColumnAllowedValues);
            if (isChanged) {
                attributes.add("allowed-values");
            }
        }
        final Element oldColumnUniquevalueGeneration = this.getElement(oldColumnElement.getChildNodes(), "uniquevalue-generation");
        final Element newColumnUniquevalueGeneration = this.getElement(newColumnElement.getChildNodes(), "uniquevalue-generation");
        if (oldColumnUniquevalueGeneration == null && newColumnUniquevalueGeneration != null) {
            attributes.add("uniquevalue-generation");
        }
        else if (oldColumnUniquevalueGeneration != null && newColumnUniquevalueGeneration == null) {
            attributes.add("uniquevalue-generation");
        }
        else if (oldColumnUniquevalueGeneration != null && newColumnUniquevalueGeneration != null) {
            final boolean isChanged2 = this.diffUniqueValueGeneration(oldColumnUniquevalueGeneration, newColumnUniquevalueGeneration);
            if (isChanged2) {
                attributes.add("uniquevalue-generation");
            }
        }
        return attributes;
    }
    
    private boolean diffAllowedValues(final Element oldAllowedValues, final Element newAllowedValues) {
        final String oldFromValue = this.getTagValue(oldAllowedValues.getChildNodes(), "from");
        final String newFromValue = this.getTagValue(newAllowedValues.getChildNodes(), "from");
        Label_0050: {
            if (oldFromValue == null) {
                if (newFromValue == null) {
                    break Label_0050;
                }
            }
            else if (oldFromValue.equals(newFromValue)) {
                break Label_0050;
            }
            return true;
        }
        final String oldToValue = this.getTagValue(oldAllowedValues.getChildNodes(), "to");
        final String newToValue = this.getTagValue(newAllowedValues.getChildNodes(), "to");
        Label_0103: {
            if (oldToValue == null) {
                if (newToValue == null) {
                    break Label_0103;
                }
            }
            else if (oldToValue.equals(newToValue)) {
                break Label_0103;
            }
            return true;
        }
        final String oldPattern = this.getTagValue(oldAllowedValues.getChildNodes(), "pattern");
        final String newPattern = this.getTagValue(newAllowedValues.getChildNodes(), "pattern");
        Label_0156: {
            if (oldPattern == null) {
                if (newPattern == null) {
                    break Label_0156;
                }
            }
            else if (oldPattern.equals(newPattern)) {
                break Label_0156;
            }
            return true;
        }
        final List<String> oldValues = this.getValues(oldAllowedValues.getChildNodes(), "value");
        final List<String> newValues = this.getValues(newAllowedValues.getChildNodes(), "value");
        return !oldValues.equals(newValues);
    }
    
    private boolean diffUniqueValueGeneration(final Element oldgenerator, final Element newgenerator) {
        final String oldName = this.getTagValue(oldgenerator.getChildNodes(), "generator-name");
        final String newName = this.getTagValue(newgenerator.getChildNodes(), "generator-name");
        if (!oldName.equals(newName)) {
            return true;
        }
        final String oldNameColumn = this.getTagValue(oldgenerator.getChildNodes(), "name-column");
        final String newNameColumn = this.getTagValue(newgenerator.getChildNodes(), "name-column");
        Label_0091: {
            if (oldNameColumn == null) {
                if (newNameColumn == null) {
                    break Label_0091;
                }
            }
            else if (oldNameColumn.equals(newNameColumn)) {
                break Label_0091;
            }
            return true;
        }
        final String oldGenClass = this.getTagValue(oldgenerator.getChildNodes(), "generator-class");
        final String newGenClass = this.getTagValue(newgenerator.getChildNodes(), "generator-class");
        Label_0144: {
            if (oldGenClass == null) {
                if (newGenClass == null) {
                    break Label_0144;
                }
            }
            else if (oldGenClass.equals(newGenClass)) {
                break Label_0144;
            }
            return true;
        }
        final String oldInstanceSpecificSeqGen = this.getTagValue(oldgenerator.getChildNodes(), "instancespecific-seqgen");
        final String newInstanceSpecificSeqGen = this.getTagValue(newgenerator.getChildNodes(), "instancespecific-seqgen");
        if (oldInstanceSpecificSeqGen == null) {
            if (newInstanceSpecificSeqGen == null) {
                return false;
            }
        }
        else if (oldInstanceSpecificSeqGen.equals(newInstanceSpecificSeqGen)) {
            return false;
        }
        return true;
    }
    
    private List<String> diffPK(final Element oldPK, final Element newPK) {
        final List<String> attributes = new ArrayList<String>();
        final String oldPKName = ((Attr)oldPK.getAttributes().getNamedItem("name")).getValue();
        final String newPKName = ((Attr)newPK.getAttributes().getNamedItem("name")).getValue();
        Label_0086: {
            if (oldPKName == null) {
                if (newPKName == null) {
                    break Label_0086;
                }
            }
            else if (oldPKName.equals(newPKName)) {
                break Label_0086;
            }
            attributes.add("name");
        }
        final List<String> oldPKcolumns = this.getValues(oldPK.getChildNodes(), "primary-key-column");
        final List<String> newPKcolumns = this.getValues(newPK.getChildNodes(), "primary-key-column");
        if (!oldPKcolumns.equals(newPKcolumns)) {
            attributes.add("primary-key-column");
        }
        return attributes;
    }
    
    private void diffUKS(final Element oldUniquekeys, final Element newUniquekeys, final String tableName, final String ddName) {
        if (oldUniquekeys == null && newUniquekeys != null) {
            final List<String> UKNames = this.getNames(newUniquekeys.getChildNodes(), "unique-key");
            for (final String ukName : UKNames) {
                final AddedElement add = new AddedElement(this.getElementByName(newUniquekeys.getChildNodes(), "unique-key", ukName), tableName, ddName, DataDictionaryDiff.ElementType.UK);
                this.changes.add(add);
            }
        }
        else if (oldUniquekeys != null && newUniquekeys == null) {
            final List<String> UKNames = this.getNames(oldUniquekeys.getChildNodes(), "unique-key");
            for (final String ukName : UKNames) {
                final DeletedElement drop = new DeletedElement(this.getElementByName(oldUniquekeys.getChildNodes(), "unique-key", ukName), tableName, ddName, DataDictionaryDiff.ElementType.UK);
                this.changes.add(drop);
            }
        }
        else if (oldUniquekeys != null && newUniquekeys != null) {
            final List<String> oldUKNames = this.getNames(oldUniquekeys.getChildNodes(), "unique-key");
            final List<String> newUKNames = this.getNames(newUniquekeys.getChildNodes(), "unique-key");
            for (final String name : newUKNames) {
                if (!oldUKNames.contains(name)) {
                    final AddedElement add2 = new AddedElement(this.getElementByName(newUniquekeys.getChildNodes(), "unique-key", name), tableName, ddName, DataDictionaryDiff.ElementType.UK);
                    this.changes.add(add2);
                }
                else {
                    final Element oldUK = this.getElementByName(oldUniquekeys.getChildNodes(), "unique-key", name);
                    final Element newUK = this.getElementByName(newUniquekeys.getChildNodes(), "unique-key", name);
                    final List<String> oldUKColumns = this.getValues(oldUK.getChildNodes(), "unique-key-column");
                    final List<String> newUKColumns = this.getValues(newUK.getChildNodes(), "unique-key-column");
                    if (oldUKColumns.equals(newUKColumns)) {
                        continue;
                    }
                    final List<String> attributes = new ArrayList<String>();
                    attributes.add("unique-key-column");
                    final ModifiedElement modify = new ModifiedElement(oldUK, newUK, tableName, ddName, DataDictionaryDiff.ElementType.UK, attributes);
                    this.changes.add(modify);
                }
            }
            for (final String name : oldUKNames) {
                if (!newUKNames.contains(name)) {
                    final DeletedElement drop2 = new DeletedElement(this.getElementByName(oldUniquekeys.getChildNodes(), "unique-key", name), tableName, ddName, DataDictionaryDiff.ElementType.UK);
                    this.changes.add(drop2);
                }
            }
        }
    }
    
    private void diffIDXES(final Element oldIndexes, final Element newIndexes, final String tableName, final String ddName) {
        if (oldIndexes == null && newIndexes != null) {
            final List<String> IDXNames = this.getNames(newIndexes.getChildNodes(), "index");
            for (final String IDXName : IDXNames) {
                final AddedElement add = new AddedElement(this.getElementByName(newIndexes.getChildNodes(), "index", IDXName), tableName, ddName, DataDictionaryDiff.ElementType.IDX);
                this.changes.add(add);
            }
        }
        else if (oldIndexes != null && newIndexes == null) {
            final List<String> IDXNames = this.getNames(oldIndexes.getChildNodes(), "index");
            for (final String IDXName : IDXNames) {
                final DeletedElement drop = new DeletedElement(this.getElementByName(oldIndexes.getChildNodes(), "index", IDXName), tableName, ddName, DataDictionaryDiff.ElementType.IDX);
                this.changes.add(drop);
            }
        }
        else if (oldIndexes != null && newIndexes != null) {
            final List<String> oldIDXNames = this.getNames(oldIndexes.getChildNodes(), "index");
            final List<String> newIDXNames = this.getNames(newIndexes.getChildNodes(), "index");
            for (final String name : newIDXNames) {
                if (!oldIDXNames.contains(name)) {
                    final AddedElement add2 = new AddedElement(this.getElementByName(newIndexes.getChildNodes(), "index", name), tableName, ddName, DataDictionaryDiff.ElementType.IDX);
                    this.changes.add(add2);
                }
                else {
                    final Element oldIDX = this.getElementByName(oldIndexes.getChildNodes(), "index", name);
                    final Element newIDX = this.getElementByName(newIndexes.getChildNodes(), "index", name);
                    final List<String> oldIDXColumns = this.getValues(oldIDX.getChildNodes(), "index-column");
                    final List<String> newIDXColumns = this.getValues(newIDX.getChildNodes(), "index-column");
                    if (!oldIDXColumns.equals(newIDXColumns)) {
                        final List<String> attributes = new ArrayList<String>();
                        attributes.add("index-column");
                        final ModifiedElement modify = new ModifiedElement(oldIDX, newIDX, tableName, ddName, DataDictionaryDiff.ElementType.IDX, attributes);
                        this.changes.add(modify);
                    }
                    else {
                        final List<String> attributes = new ArrayList<String>();
                        for (final String columnName : oldIDXColumns) {
                            final Element oldIDXColumnElement = this.getElementByValue(oldIDX.getChildNodes(), "index-column", columnName);
                            final Element newIDXColumnElement = this.getElementByValue(newIDX.getChildNodes(), "index-column", columnName);
                            final String oldIDXColumnSize = oldIDXColumnElement.getAttribute("size");
                            final String newIDXColumnSize = newIDXColumnElement.getAttribute("size");
                            Label_0599: {
                                if (oldIDXColumnSize == null) {
                                    if (newIDXColumnSize == null) {
                                        break Label_0599;
                                    }
                                }
                                else if (oldIDXColumnSize.equals(newIDXColumnSize)) {
                                    break Label_0599;
                                }
                                attributes.add("size of " + columnName);
                            }
                            String oldIDXIsAscending = oldIDXColumnElement.getAttribute("isAscending");
                            String newIDXIsAscending = newIDXColumnElement.getAttribute("isAscending");
                            oldIDXIsAscending = ((oldIDXIsAscending != null && !oldIDXIsAscending.isEmpty()) ? oldIDXIsAscending : "true");
                            newIDXIsAscending = ((newIDXIsAscending != null && !newIDXIsAscending.isEmpty()) ? newIDXIsAscending : "true");
                            if (!oldIDXIsAscending.equals(newIDXIsAscending)) {
                                attributes.add("isAscending of " + columnName);
                            }
                            final String oldIDXIsNullsFirst = oldIDXColumnElement.getAttribute("isNullsFirst");
                            final String newIDXIsNullsFirst = newIDXColumnElement.getAttribute("isNullsFirst");
                            if (oldIDXIsNullsFirst == null) {
                                if (newIDXIsNullsFirst == null) {
                                    continue;
                                }
                            }
                            else if (oldIDXIsNullsFirst.equals(newIDXIsNullsFirst)) {
                                continue;
                            }
                            attributes.add("isNullsFirst of " + columnName);
                        }
                        if (attributes.isEmpty()) {
                            continue;
                        }
                        final ModifiedElement modify = new ModifiedElement(oldIDX, newIDX, tableName, ddName, DataDictionaryDiff.ElementType.IDX, attributes);
                        this.changes.add(modify);
                    }
                }
            }
            for (final String name : oldIDXNames) {
                if (!newIDXNames.contains(name)) {
                    final DeletedElement drop2 = new DeletedElement(this.getElementByName(oldIndexes.getChildNodes(), "index", name), tableName, ddName, DataDictionaryDiff.ElementType.IDX);
                    this.changes.add(drop2);
                }
            }
        }
    }
    
    private void diffFKS(final Element oldFKS, final Element newFKS, final String tableName, final String ddName) {
        if (oldFKS == null && newFKS != null) {
            final List<String> fkNames = this.getNames(newFKS.getChildNodes(), "foreign-key");
            for (final String name : fkNames) {
                final AddedElement add = new AddedElement(this.getElementByName(newFKS.getChildNodes(), "foreign-key", name), tableName, ddName, DataDictionaryDiff.ElementType.FK);
                this.changes.add(add);
            }
        }
        else if (oldFKS != null && newFKS == null) {
            final List<String> fkNames = this.getNames(oldFKS.getChildNodes(), "foreign-key");
            for (final String name : fkNames) {
                final DeletedElement drop = new DeletedElement(this.getElementByName(oldFKS.getChildNodes(), "foreign-key", name), tableName, ddName, DataDictionaryDiff.ElementType.FK);
                this.changes.add(drop);
            }
        }
        else if (oldFKS != null && newFKS != null) {
            final List<String> oldFKNames = this.getNames(oldFKS.getChildNodes(), "foreign-key");
            final List<String> newFKNames = this.getNames(newFKS.getChildNodes(), "foreign-key");
            for (final String name2 : newFKNames) {
                if (!oldFKNames.contains(name2)) {
                    final AddedElement add2 = new AddedElement(this.getElementByName(newFKS.getChildNodes(), "foreign-key", name2), tableName, ddName, DataDictionaryDiff.ElementType.FK);
                    this.changes.add(add2);
                }
                else {
                    final Element oldFKElement = this.getElementByName(oldFKS.getChildNodes(), "foreign-key", name2);
                    final Element newFKElement = this.getElementByName(newFKS.getChildNodes(), "foreign-key", name2);
                    final List<String> attributes = this.diffFK(oldFKElement, newFKElement);
                    if (attributes.isEmpty()) {
                        continue;
                    }
                    final ModifiedElement modify = new ModifiedElement(oldFKElement, newFKElement, tableName, ddName, DataDictionaryDiff.ElementType.FK, attributes);
                    this.changes.add(modify);
                }
            }
            for (final String fkName : oldFKNames) {
                if (!newFKNames.contains(fkName)) {
                    final DeletedElement drop2 = new DeletedElement(this.getElementByName(oldFKS.getChildNodes(), "foreign-key", fkName), tableName, ddName, DataDictionaryDiff.ElementType.FK);
                    this.changes.add(drop2);
                }
            }
        }
    }
    
    private List<String> diffFK(final Element oldFK, final Element newFK) {
        final List attributes = new ArrayList();
        final String oldFKreferenceTabName = oldFK.getAttribute("reference-table-name");
        final String newFKreferenceTabName = newFK.getAttribute("reference-table-name");
        if (!oldFKreferenceTabName.equals(newFKreferenceTabName)) {
            attributes.add("reference-table-name");
        }
        final String oldFKbidirectionalValue = oldFK.getAttribute("isbidirectional");
        final String newFKbidirectionalValue = newFK.getAttribute("isbidirectional");
        Label_0099: {
            if (oldFKbidirectionalValue == null) {
                if (newFKbidirectionalValue == null) {
                    break Label_0099;
                }
            }
            else if (oldFKbidirectionalValue.equals(newFKbidirectionalValue)) {
                break Label_0099;
            }
            attributes.add("isbidirectional");
        }
        final String oldFKDesc = this.getTagValue(oldFK.getChildNodes(), "description");
        final String newFKDesc = this.getTagValue(newFK.getChildNodes(), "description");
        Label_0159: {
            if (oldFKDesc == null) {
                if (newFKDesc == null) {
                    break Label_0159;
                }
            }
            else if (oldFKDesc.equals(newFKDesc)) {
                break Label_0159;
            }
            attributes.add("description");
        }
        final String oldFKConstraint = this.getTagValue(oldFK.getChildNodes(), "fk-constraints");
        final String newFKConstraint = this.getTagValue(newFK.getChildNodes(), "fk-constraints");
        Label_0219: {
            if (oldFKConstraint == null) {
                if (newFKConstraint == null) {
                    break Label_0219;
                }
            }
            else if (oldFKConstraint.equals(newFKConstraint)) {
                break Label_0219;
            }
            attributes.add("fk-constraints");
        }
        final Element oldfkCOlumns = this.getElement(oldFK.getChildNodes(), "fk-columns");
        final Element newfkColumns = this.getElement(newFK.getChildNodes(), "fk-columns");
        final Map oldFKs = this.getFKColumns(oldfkCOlumns.getChildNodes());
        final Map newFKs = this.getFKColumns(newfkColumns.getChildNodes());
        if (!oldFKs.equals(newFKs)) {
            attributes.add("fk-local-column");
            attributes.add("fk-reference-column");
        }
        return attributes;
    }
    
    private static Element getDocElement(final URL url) throws Exception {
        final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        builderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        builderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        final DocumentBuilder builder = builderFactory.newDocumentBuilder();
        InputStream urlStream = null;
        try {
            urlStream = url.openStream();
            final Document document = builder.parse(urlStream);
            return document.getDocumentElement();
        }
        finally {
            if (urlStream != null) {
                urlStream.close();
            }
        }
    }
    
    static List diff(final URL[] oldFiles, final URL[] newFiles) throws Exception {
        final DataDictionaryDiffGenerator ddDiffGen = new DataDictionaryDiffGenerator();
        for (int i = 0; i < oldFiles.length; ++i) {
            DataDictionaryDiffGenerator.LOGGER.log(Level.INFO, "diff :: oldFiles[{0}] is [{1}]", new Object[] { i, oldFiles[i] });
            final Element element = getDocElement(oldFiles[i]);
            ddDiffGen.oldDocuments.add(element);
        }
        for (int j = 0; j < newFiles.length; ++j) {
            DataDictionaryDiffGenerator.LOGGER.log(Level.INFO, "diff :: newFiles[{0}] is [{1}]", new Object[] { j, newFiles[j] });
            final Element element = getDocElement(newFiles[j]);
            ddDiffGen.newDocuments.add(element);
        }
        ddDiffGen.diffDDs();
        return ddDiffGen.changes;
    }
    
    static {
        LOGGER = Logger.getLogger(DataDictionaryDiffGenerator.class.getName());
    }
}
