package com.zoho.dddiff;

import org.w3c.dom.Element;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Iterator;
import java.util.ArrayList;
import java.net.URL;
import java.util.List;

public class DataDictionaryDiff
{
    private List<AddedElement> newDDs;
    private List<AddedElement> newTables;
    private List<AddedElement> newColumns;
    private List<AddedElement> newFKs;
    private List<AddedElement> newUKs;
    private List<AddedElement> newIDXs;
    private List<DeletedElement> droppedDDs;
    private List<DeletedElement> droppedTables;
    private List<DeletedElement> droppedColumns;
    private List<DeletedElement> droppedFKs;
    private List<DeletedElement> droppedUKs;
    private List<DeletedElement> droppedIDXs;
    private List<ModifiedElement> modifiedDDs;
    private List<ModifiedElement> modifiedTables;
    private List<ModifiedElement> modifiedColumns;
    private List<ModifiedElement> modifiedPKs;
    private List<ModifiedElement> modifiedUKs;
    private List<ModifiedElement> modifiedFKs;
    private List<ModifiedElement> modifiedIDXs;
    private List<String> modifiedTableNames;
    private List<Object> allChanges;
    
    DataDictionaryDiff(final URL[] oldFiles, final URL[] newFiles) throws Exception {
        this.newDDs = new ArrayList<AddedElement>();
        this.newTables = new ArrayList<AddedElement>();
        this.newColumns = new ArrayList<AddedElement>();
        this.newFKs = new ArrayList<AddedElement>();
        this.newUKs = new ArrayList<AddedElement>();
        this.newIDXs = new ArrayList<AddedElement>();
        this.droppedDDs = new ArrayList<DeletedElement>();
        this.droppedTables = new ArrayList<DeletedElement>();
        this.droppedColumns = new ArrayList<DeletedElement>();
        this.droppedFKs = new ArrayList<DeletedElement>();
        this.droppedUKs = new ArrayList<DeletedElement>();
        this.droppedIDXs = new ArrayList<DeletedElement>();
        this.modifiedDDs = new ArrayList<ModifiedElement>();
        this.modifiedTables = new ArrayList<ModifiedElement>();
        this.modifiedColumns = new ArrayList<ModifiedElement>();
        this.modifiedPKs = new ArrayList<ModifiedElement>();
        this.modifiedUKs = new ArrayList<ModifiedElement>();
        this.modifiedFKs = new ArrayList<ModifiedElement>();
        this.modifiedIDXs = new ArrayList<ModifiedElement>();
        this.modifiedTableNames = new ArrayList<String>();
        this.allChanges = DataDictionaryDiffGenerator.diff(oldFiles, newFiles);
        for (final Object obj : this.allChanges) {
            if (obj instanceof AddedElement) {
                final AddedElement add = (AddedElement)obj;
                switch (add.getType()) {
                    case DD: {
                        this.newDDs.add(add);
                        continue;
                    }
                    case TABLE: {
                        this.newTables.add(add);
                        continue;
                    }
                    case COLUMN: {
                        this.newColumns.add(add);
                        if (!this.modifiedTableNames.contains(add.getTableName())) {
                            this.modifiedTableNames.add(add.getTableName());
                            continue;
                        }
                        continue;
                    }
                    case IDX: {
                        this.newIDXs.add(add);
                        if (!this.modifiedTableNames.contains(add.getTableName())) {
                            this.modifiedTableNames.add(add.getTableName());
                            continue;
                        }
                        continue;
                    }
                    case FK: {
                        this.newFKs.add(add);
                        if (!this.modifiedTableNames.contains(add.getTableName())) {
                            this.modifiedTableNames.add(add.getTableName());
                            continue;
                        }
                        continue;
                    }
                    case UK: {
                        this.newUKs.add(add);
                        if (!this.modifiedTableNames.contains(add.getTableName())) {
                            this.modifiedTableNames.add(add.getTableName());
                            continue;
                        }
                        continue;
                    }
                }
            }
            else if (obj instanceof DeletedElement) {
                final DeletedElement drop = (DeletedElement)obj;
                switch (drop.getType()) {
                    case DD: {
                        this.droppedDDs.add(drop);
                        continue;
                    }
                    case TABLE: {
                        this.droppedTables.add(drop);
                        continue;
                    }
                    case COLUMN: {
                        this.droppedColumns.add(drop);
                        if (!this.modifiedTableNames.contains(drop.getTableName())) {
                            this.modifiedTableNames.add(drop.getTableName());
                            continue;
                        }
                        continue;
                    }
                    case FK: {
                        this.droppedFKs.add(drop);
                        if (!this.modifiedTableNames.contains(drop.getTableName())) {
                            this.modifiedTableNames.add(drop.getTableName());
                            continue;
                        }
                        continue;
                    }
                    case IDX: {
                        this.droppedIDXs.add(drop);
                        if (!this.modifiedTableNames.contains(drop.getTableName())) {
                            this.modifiedTableNames.add(drop.getTableName());
                            continue;
                        }
                        continue;
                    }
                    case UK: {
                        this.droppedUKs.add(drop);
                        if (!this.modifiedTableNames.contains(drop.getTableName())) {
                            this.modifiedTableNames.add(drop.getTableName());
                            continue;
                        }
                        continue;
                    }
                }
            }
            else {
                if (!(obj instanceof ModifiedElement)) {
                    continue;
                }
                final ModifiedElement modify = (ModifiedElement)obj;
                switch (modify.getType()) {
                    case DD: {
                        this.modifiedDDs.add(modify);
                        continue;
                    }
                    case TABLE: {
                        this.modifiedTables.add(modify);
                        if (!this.modifiedTableNames.contains(modify.getTableName())) {
                            this.modifiedTableNames.add(modify.getTableName());
                            continue;
                        }
                        continue;
                    }
                    case COLUMN: {
                        this.modifiedColumns.add(modify);
                        if (!this.modifiedTableNames.contains(modify.getTableName())) {
                            this.modifiedTableNames.add(modify.getTableName());
                            continue;
                        }
                        continue;
                    }
                    case FK: {
                        this.modifiedFKs.add(modify);
                        if (!this.modifiedTableNames.contains(modify.getTableName())) {
                            this.modifiedTableNames.add(modify.getTableName());
                            continue;
                        }
                        continue;
                    }
                    case PK: {
                        this.modifiedPKs.add(modify);
                        if (!this.modifiedTableNames.contains(modify.getTableName())) {
                            this.modifiedTableNames.add(modify.getTableName());
                            continue;
                        }
                        continue;
                    }
                    case UK: {
                        this.modifiedUKs.add(modify);
                        if (!this.modifiedTableNames.contains(modify.getTableName())) {
                            this.modifiedTableNames.add(modify.getTableName());
                            continue;
                        }
                        continue;
                    }
                    case IDX: {
                        this.modifiedIDXs.add(modify);
                        if (!this.modifiedTableNames.contains(modify.getTableName())) {
                            this.modifiedTableNames.add(modify.getTableName());
                            continue;
                        }
                        continue;
                    }
                }
            }
        }
    }
    
    public List<String> getModifiedTableNames() {
        return this.modifiedTableNames;
    }
    
    public List getAllChanges() {
        return this.allChanges;
    }
    
    public List<AddedElement> getNewDDs() {
        return this.newDDs;
    }
    
    public List<AddedElement> getNewTables() {
        return this.newTables;
    }
    
    public List<AddedElement> getNewColumns() {
        return this.newColumns;
    }
    
    public List<AddedElement> getNewForeignKeys() {
        return this.newFKs;
    }
    
    public List<AddedElement> getNewUniqueKeys() {
        return this.newUKs;
    }
    
    public List<AddedElement> getNewIndexes() {
        return this.newIDXs;
    }
    
    public List<DeletedElement> getDeletedDDs() {
        return this.droppedDDs;
    }
    
    public List<DeletedElement> getDroppedTables() {
        return this.droppedTables;
    }
    
    public List<DeletedElement> getDroppedColumns() {
        return this.droppedColumns;
    }
    
    public List<DeletedElement> getDroppedUniquesKeys() {
        return this.droppedUKs;
    }
    
    public List<DeletedElement> getDroppedForeignKeys() {
        return this.droppedFKs;
    }
    
    public List<DeletedElement> getDroppedIndexes() {
        return this.droppedIDXs;
    }
    
    public List<ModifiedElement> getModifiedDDs() {
        return this.modifiedDDs;
    }
    
    public List<ModifiedElement> getModifiedTables() {
        return this.modifiedTables;
    }
    
    public List<ModifiedElement> getModifiedColumns() {
        return this.modifiedColumns;
    }
    
    public List<ModifiedElement> getModifiedPrimaryKeys() {
        return this.modifiedPKs;
    }
    
    public List<ModifiedElement> getModifiedUniqueKeys() {
        return this.modifiedUKs;
    }
    
    public List<ModifiedElement> getModifiedForeignKeys() {
        return this.modifiedFKs;
    }
    
    public List<ModifiedElement> getModifiedIndexes() {
        return this.modifiedIDXs;
    }
    
    public void writeToFile(final File file) {
        final StringBuilder builder = new StringBuilder();
        builder.append("\n<ppm-changes>");
        if (!this.newDDs.isEmpty()) {
            builder.append("\n\t<new-dds>");
            for (final AddedElement add : this.newDDs) {
                builder.append("\n\t\t<new-dd name=\"");
                builder.append(add.getDDName());
                builder.append("\" />");
            }
            builder.append("\n\t</new-dds>");
        }
        if (!this.droppedDDs.isEmpty()) {
            builder.append("\n\t<deleted-dds>");
            for (final DeletedElement drop : this.droppedDDs) {
                builder.append("\n\t\t<deleted-dd name=\"");
                builder.append(drop.getDDName());
                builder.append("\" />");
            }
            builder.append("\n\t<deleted-dds>");
        }
        if (!this.modifiedDDs.isEmpty()) {
            builder.append("\n\t<modified-dds>");
            for (final ModifiedElement modifiedElement : this.modifiedDDs) {
                builder.append("\n\t\t<modified-dd name=\"");
                builder.append(modifiedElement.getDDName());
                builder.append("\">\n\t\t\t<modified-attributes>");
                builder.append(modifiedElement.getChangedAttributes().toString());
                builder.append("</modified-attributes>\n\t\t</modified-dd>");
            }
            builder.append("\n\t</modified-dds>");
        }
        if (!this.newTables.isEmpty()) {
            builder.append("\n\t<new-tables>");
            for (final AddedElement add : this.newTables) {
                builder.append("\n\t\t<new-table name=\"");
                builder.append(add.getTableName());
                builder.append("\" dd-name=\"");
                builder.append(add.getDDName());
                builder.append("\" />");
            }
            builder.append("\n\t</new-tables>");
        }
        if (!this.droppedTables.isEmpty()) {
            builder.append("\n\t<deleted-tables>");
            for (final DeletedElement drop : this.droppedTables) {
                builder.append("\n\t\t<deleted-table name=\"");
                builder.append(drop.getTableName());
                builder.append("\" />");
            }
            builder.append("\n\t</deleted-tables>");
        }
        if (!this.modifiedTables.isEmpty()) {
            builder.append("\n\t<modified-tables>");
            for (final ModifiedElement modifiedElement : this.modifiedTables) {
                builder.append("\n\t\t<modified-table name=\"");
                builder.append(modifiedElement.getTableName());
                builder.append("\" dd-name=\"");
                builder.append(modifiedElement.getDDName());
                builder.append("\">");
                builder.append("\n\t\t\t<modified-attributes>");
                builder.append(modifiedElement.getChangedAttributes());
                builder.append("</modified-attributes>\n\t\t</modified-table>");
            }
            builder.append("\n\t</modified-tables>");
        }
        if (!this.newColumns.isEmpty()) {
            builder.append("\n\t<new-columns>");
            for (final AddedElement add : this.newColumns) {
                builder.append("\n\t\t<new-column name=\"");
                builder.append(add.getElement().getAttribute("name"));
                builder.append("\" table-name=\"");
                builder.append(add.getTableName());
                builder.append("\" dd-name=\"");
                builder.append(add.getDDName());
                builder.append("\" />");
            }
            builder.append("\n\t</new-columns>");
        }
        if (!this.droppedColumns.isEmpty()) {
            builder.append("\n\t<deleted-columns>");
            for (final DeletedElement drop : this.droppedColumns) {
                builder.append("\n\t\t<deleted-column name=\"");
                builder.append(drop.getElement().getAttribute("name"));
                builder.append("\" table-name=\"");
                builder.append(drop.getTableName());
                builder.append("\" />");
            }
            builder.append("\n\t</deleted-columns>");
        }
        if (!this.modifiedColumns.isEmpty()) {
            builder.append("\n\t<modified-columns>");
            for (final ModifiedElement modifiedElement : this.modifiedColumns) {
                builder.append("\n\t\t<modified-column name=\"");
                builder.append(modifiedElement.getNewElement().getAttribute("name"));
                builder.append("\" table-name=\"");
                builder.append(modifiedElement.getTableName());
                builder.append("\" dd-name=\"");
                builder.append(modifiedElement.getDDName());
                builder.append("\">\n\t\t\t<modified-attributes>");
                builder.append(modifiedElement.getChangedAttributes());
                builder.append("</modified-attributes>\n\t\t</modified-column>");
            }
            builder.append("\n\t</modified-columns>");
        }
        if (!this.modifiedPKs.isEmpty()) {
            builder.append("\n\t<modified-pks>");
            for (final ModifiedElement modifiedElement : this.modifiedPKs) {
                builder.append("\n\t\t<modified-pk name=\"");
                builder.append(modifiedElement.getNewElement().getAttribute("name"));
                builder.append("\" table-name=\"");
                builder.append(modifiedElement.getTableName());
                builder.append("\" dd-name=\"");
                builder.append(modifiedElement.getDDName());
                builder.append("\">\n\t\t\t<modified-attributes>");
                builder.append(modifiedElement.getChangedAttributes());
                builder.append("</modified-attributes>\n\t\t</modified-pk>");
            }
            builder.append("\n\t</modified-pks>");
        }
        if (!this.newFKs.isEmpty()) {
            builder.append("\n\t<new-fks>");
            for (final AddedElement add : this.newFKs) {
                builder.append("\n\t\t<new-fk name=\"");
                builder.append(add.getElement().getAttribute("name"));
                builder.append("\" table-name=\"");
                builder.append(add.getTableName());
                builder.append("\" dd-name=\"");
                builder.append(add.getDDName());
                builder.append("\" />");
            }
            builder.append("\n\t</new-fks>");
        }
        if (!this.droppedFKs.isEmpty()) {
            builder.append("\n\t<deleted-fks>");
            for (final DeletedElement drop : this.droppedFKs) {
                builder.append("\n\t\t<deleted-fk name=\"");
                builder.append(drop.getElement().getAttribute("name"));
                builder.append("\" table-name=\"");
                builder.append(drop.getTableName());
                builder.append("\" />");
            }
            builder.append("\n\t</deleted-fks>");
        }
        if (!this.modifiedFKs.isEmpty()) {
            builder.append("\n\t<modified-fks>");
            for (final ModifiedElement modifiedElement : this.modifiedFKs) {
                builder.append("\n\t\t<modified-fk name=\"");
                builder.append(modifiedElement.getNewElement().getAttribute("name"));
                builder.append("\" table-name=\"");
                builder.append(modifiedElement.getTableName());
                builder.append("\" dd-name=\"");
                builder.append(modifiedElement.getDDName());
                builder.append("\">\n\t\t\t<modified-attributes>");
                builder.append(modifiedElement.getChangedAttributes());
                builder.append("</modified-attributes>\n\t\t</modified-fk>");
            }
            builder.append("\n\t</modified-fks>");
        }
        if (!this.newUKs.isEmpty()) {
            builder.append("\n\t<new-uks>");
            for (final AddedElement add : this.newUKs) {
                builder.append("\n\t\t<new-uk name=\"");
                builder.append(add.getElement().getAttribute("name"));
                builder.append("\" table-name=\"");
                builder.append(add.getTableName());
                builder.append("\" dd-name=\"");
                builder.append(add.getDDName());
                builder.append("\" />");
            }
            builder.append("\n\t</new-uks>");
        }
        if (!this.droppedUKs.isEmpty()) {
            builder.append("\n\t<deleted-uks>");
            for (final DeletedElement drop : this.droppedUKs) {
                builder.append("\n\t\t<deleted-uk name=\"");
                builder.append(drop.getElement().getAttribute("name"));
                builder.append("\" table-name=\"");
                builder.append(drop.getTableName());
                builder.append("\" />");
            }
            builder.append("\n\t</deleted-uks>");
        }
        if (!this.modifiedUKs.isEmpty()) {
            builder.append("\n\t<modified-uks>");
            for (final ModifiedElement modifiedElement : this.modifiedUKs) {
                builder.append("\n\t\t<modified-uk name=\"");
                builder.append(modifiedElement.getNewElement().getAttribute("name"));
                builder.append("\" table-name=\"");
                builder.append(modifiedElement.getTableName());
                builder.append("\" dd-name=\"");
                builder.append(modifiedElement.getDDName());
                builder.append("\">\n\t\t\t<modified-attributes>");
                builder.append(modifiedElement.getChangedAttributes());
                builder.append("</modified-attributes>\n\t\t</modified-uk>");
            }
            builder.append("\n\t</modified-uks>");
        }
        if (!this.newIDXs.isEmpty()) {
            builder.append("\n\t<new-idxs>");
            for (final AddedElement add : this.newIDXs) {
                builder.append("\n\t\t<new-idx name=\"");
                builder.append(add.getElement().getAttribute("name"));
                builder.append("\" table-name=\"");
                builder.append(add.getTableName());
                builder.append("\" dd-name=\"");
                builder.append(add.getDDName());
                builder.append("\" />");
            }
            builder.append("\n\t</new-idxs>");
        }
        if (!this.droppedIDXs.isEmpty()) {
            builder.append("\n\t<deleted-idxs>");
            for (final DeletedElement drop : this.droppedIDXs) {
                builder.append("\n\t\t<deleted-idx name=\"");
                builder.append(drop.getElement().getAttribute("name"));
                builder.append("\" table-name=\"");
                builder.append(drop.getTableName());
                builder.append("\" />");
            }
            builder.append("\n\t</deleted-idxs>");
        }
        if (!this.modifiedIDXs.isEmpty()) {
            builder.append("\n\t<modified-idxs>");
            for (final ModifiedElement modifiedElement : this.modifiedIDXs) {
                builder.append("\n\t\t<modified-idx name=\"");
                builder.append(modifiedElement.getNewElement().getAttribute("name"));
                builder.append("\" table-name=\"");
                builder.append(modifiedElement.getTableName());
                builder.append("\" dd-name=\"");
                builder.append(modifiedElement.getDDName());
                builder.append("\">\n\t\t\t<modified-attributes>");
                builder.append(modifiedElement.getChangedAttributes());
                builder.append("</modified-attributes>\n\t\t</modified-idx>");
            }
            builder.append("\n\t</modified-idxs>");
        }
        builder.append("\n</ppm-changes>");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
            fos.write(builder.toString().getBytes());
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
        finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final IOException e2) {
                throw new IllegalArgumentException(e2);
            }
        }
    }
    
    public String toHTMLString() {
        final StringBuilder sb = new StringBuilder();
        if (!this.newDDs.isEmpty()) {
            sb.append("<b>New data-dictionaries </b><br/>");
            for (final AddedElement add : this.newDDs) {
                sb.append(add.getDDName() + "<br/>");
            }
        }
        if (!this.droppedDDs.isEmpty()) {
            sb.append("<br/><br/><b>Deleted data-dictionaries</b><br/>");
            for (final DeletedElement drop : this.droppedDDs) {
                sb.append(drop.getDDName() + "<br/>");
            }
        }
        if (!this.modifiedDDs.isEmpty()) {
            sb.append("<br/><br/><b>Modified data-dictionaries</b><br/>");
            sb.append("<table border='1' cellspacing='0'><tr><td><b>DD Name</b></td><td><b>Modified Attributes</b></td></tr>");
            for (final ModifiedElement mod : this.modifiedDDs) {
                sb.append("<tr>");
                sb.append("<td>" + mod.getDDName() + "</td><td>" + mod.getChangedAttributes().toString() + "</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
        }
        if (!this.newTables.isEmpty()) {
            sb.append("<br/><br/><b>New Tables</b><br/>");
            for (final AddedElement add : this.newTables) {
                sb.append(add.getTableName() + "<br/>");
            }
        }
        if (!this.droppedTables.isEmpty()) {
            sb.append("<br/><b>Deleted Tables</b><br/>");
            for (final DeletedElement drop : this.droppedTables) {
                sb.append(drop.getTableName() + "<br/>");
            }
        }
        if (!this.modifiedTables.isEmpty()) {
            sb.append("<br/><br/><b>Tables Modified in its attribute</b><br/>");
            sb.append("<table border='1' cellspacing='0'><tr><td><b>TableName</b></td><td><b>Modified Attributes</b></td></tr>");
            for (final ModifiedElement mod : this.modifiedTables) {
                sb.append("<tr>");
                sb.append("<td>" + mod.getTableName() + "</td><td>" + mod.getChangedAttributes().toString() + "</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
        }
        if (!this.newColumns.isEmpty()) {
            sb.append("<br/><b>New Columns</b><br/>");
            for (final AddedElement add : this.newColumns) {
                final Element columnElement = add.getElement();
                sb.append(add.getTableName() + ".<u>" + columnElement.getAttribute("name") + "</u><br/>");
            }
        }
        if (!this.droppedColumns.isEmpty()) {
            sb.append("<br/><b>Dropped Columns</b><br/>");
            for (final DeletedElement drop : this.droppedColumns) {
                final Element columnElement = drop.getElement();
                sb.append(drop.getTableName() + ".<u>" + columnElement.getAttribute("name") + "</u><br/>");
            }
        }
        if (!this.modifiedColumns.isEmpty()) {
            sb.append("<br/><b>Modified Columns</b><br/>");
            sb.append("<table border='1' cellspacing='0'><tr><td><b>TableName</b></td><td><b>ColumnName</b></td><td><b>Modified Attributes</b></td></tr>");
            for (final ModifiedElement mod : this.modifiedColumns) {
                final Element columnElement = mod.getOldElement();
                sb.append("<tr>");
                sb.append("<td>" + mod.getTableName() + "</td><td>" + columnElement.getAttribute("name") + "</td><td>" + mod.getChangedAttributes().toString() + "</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
        }
        if (!this.modifiedPKs.isEmpty()) {
            sb.append("<br/><b>Modified PrimaryKeys</b><br/>");
            sb.append("<table border='1' cellspacing='0'><tr><td><b>TableName</b></td><td><b>New primary-key Name</b></td><td><b>Modified Attributes</b></td></tr>");
            for (final ModifiedElement mod : this.modifiedPKs) {
                final Element pkElement = mod.getNewElement();
                sb.append("<tr>");
                sb.append("<td>" + mod.getTableName() + "</td><td>" + pkElement.getAttribute("name") + "</td><td>" + mod.getChangedAttributes().toString() + "</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
        }
        if (!this.newFKs.isEmpty()) {
            sb.append("<br/><b>New Foreign-Keys</b><br/>");
            for (final AddedElement add : this.newFKs) {
                final Element fkElement = add.getElement();
                sb.append(add.getTableName() + ".<u>" + fkElement.getAttribute("name") + "</u><br/>");
            }
        }
        if (!this.droppedFKs.isEmpty()) {
            sb.append("<br/><b>Dropped Foreign-keys</b><br/>");
            for (final DeletedElement drop : this.droppedFKs) {
                final Element fkElement = drop.getElement();
                sb.append(drop.getTableName() + ".<u>" + fkElement.getAttribute("name") + "</u><br/>");
            }
        }
        if (!this.modifiedFKs.isEmpty()) {
            sb.append("<br/><b>Modified Foreign-keys</b><br/>");
            sb.append("<table border='1' cellspacing='0'><tr><td><b>TableName</b></td><td><b>Foreign-key Name</b></td><td><b>Modified Attributes</b></td></tr>");
            for (final ModifiedElement mod : this.modifiedFKs) {
                final Element fkElement = mod.getNewElement();
                sb.append("<tr>");
                sb.append("<td>" + mod.getTableName() + "</td><td>" + fkElement.getAttribute("name") + "</td><td>" + mod.getChangedAttributes().toString() + "</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
        }
        if (!this.newUKs.isEmpty()) {
            sb.append("<br/><b>New Unique-keys</b><br/>");
            for (final AddedElement add : this.newUKs) {
                final Element ukElement = add.getElement();
                sb.append(add.getTableName() + ".<u>" + ukElement.getAttribute("name") + "</u><br/>");
            }
        }
        if (!this.droppedUKs.isEmpty()) {
            sb.append("<br/><b>Dropped Unique-keys</b><br/>");
            for (final DeletedElement drop : this.droppedUKs) {
                final Element ukElement = drop.getElement();
                sb.append(drop.getTableName() + ".<u>" + ukElement.getAttribute("name") + "</u><br/>");
            }
        }
        if (!this.modifiedUKs.isEmpty()) {
            sb.append("<br/><b>Modified Unique-keys</b><br/>");
            sb.append("<table border='1' cellspacing='0'><tr><td><b>TableName</b></td><td><b>Unique-key Name</b></td><td><b>Modified Attributes</b></td></tr>");
            for (final ModifiedElement mod : this.modifiedUKs) {
                final Element ukElement = mod.getNewElement();
                sb.append("<tr>");
                sb.append("<td>" + mod.getTableName() + "</td><td>" + ukElement.getAttribute("name") + "</td><td>" + mod.getChangedAttributes().toString() + "</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
        }
        if (!this.newIDXs.isEmpty()) {
            sb.append("<br/><b>New Indexes</b><br/>");
            for (final AddedElement add : this.newIDXs) {
                final Element idxElement = add.getElement();
                sb.append(add.getTableName() + ".<u>" + idxElement.getAttribute("name") + "</u><br/>");
            }
        }
        if (!this.droppedIDXs.isEmpty()) {
            sb.append("<br/><b>Dropped Indexes</b><br/>");
            for (final DeletedElement drop : this.droppedIDXs) {
                final Element idxElement = drop.getElement();
                sb.append(drop.getTableName() + ".<u>" + idxElement.getAttribute("name") + "</u><br/>");
            }
        }
        if (!this.modifiedIDXs.isEmpty()) {
            sb.append("<br/><b>Modified Indexes</b><br/>");
            sb.append("<table border='1' cellspacing='0'><tr><td><b>TableName</b></td><td><b>Index Name</b></td><td><b>Modified Attributes</b></td></tr>");
            for (final ModifiedElement mod : this.modifiedIDXs) {
                final Element idxElement = mod.getNewElement();
                sb.append("<tr>");
                sb.append("<td>" + mod.getTableName() + "</td><td>" + idxElement.getAttribute("name") + "</td><td>" + mod.getChangedAttributes().toString() + "</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
        }
        return sb.toString();
    }
    
    public enum ElementType
    {
        DD, 
        TABLE, 
        COLUMN, 
        PK, 
        FK, 
        UK, 
        IDX;
    }
}
