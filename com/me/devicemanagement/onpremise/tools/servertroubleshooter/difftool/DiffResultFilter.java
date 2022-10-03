package com.me.devicemanagement.onpremise.tools.servertroubleshooter.difftool;

import java.util.Properties;
import java.util.Arrays;
import com.me.devicemanagement.onpremise.tools.servertroubleshooter.difftool.util.DiffToolUtil;
import org.json.JSONException;
import java.io.IOException;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.HashMap;
import java.util.logging.Level;
import org.json.JSONArray;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;

public class DiffResultFilter
{
    private static final Logger LOGGER;
    private List<String> ignoreDiffTypes;
    private List<String> ignoreModuleNames;
    private List<String> ignoreTableNames;
    private List<String> ignoreColumnNames;
    private Map<String, JSONArray> filteredDiff;
    
    public DiffResultFilter() {
        this.ignoreDiffTypes = null;
        this.ignoreModuleNames = null;
        this.ignoreTableNames = null;
        this.ignoreColumnNames = null;
        this.filteredDiff = null;
    }
    
    public void filter(final Map<String, JSONArray> diffMap) throws IOException, JSONException {
        DiffResultFilter.LOGGER.log(Level.INFO, "Filtering DiffTool output");
        DiffResultFilter.LOGGER.log(Level.INFO, "Diff size before filter : {0}", diffMap.size());
        this.filteredDiff = new HashMap<String, JSONArray>();
        for (final Map.Entry<String, JSONArray> diffEntry : diffMap.entrySet()) {
            final String key = diffEntry.getKey();
            final JSONArray diffValue = diffEntry.getValue();
            final JSONArray filteredValue = new JSONArray();
            for (int index = 0; index < diffValue.length(); ++index) {
                final JSONObject diff = diffValue.getJSONObject(index);
                final String columnName = String.valueOf(diff.get("name"));
                final String moduleName = String.valueOf(diff.get("modulename"));
                final String diffType = String.valueOf(diff.get("difftype"));
                final String tableName = String.valueOf(diff.get("tablename"));
                if ((moduleName.equals("") || !this.containsIgnoreCase(this.ignoreModuleNames, moduleName)) && (tableName.equals("") || !this.containsIgnoreCase(this.ignoreTableNames, tableName)) && (columnName.equals("") || !this.containsIgnoreCase(this.ignoreColumnNames, columnName)) && (diffType.equals("") || !this.containsIgnoreCase(this.ignoreDiffTypes, diffType))) {
                    filteredValue.put((Object)diff);
                }
            }
            if (filteredValue.length() > 0) {
                this.filteredDiff.put(key, filteredValue);
                DiffResultFilter.LOGGER.log(Level.FINE, "Adding table : {0} diffs : {1}", new Object[] { key, filteredValue.toString() });
            }
        }
        DiffResultFilter.LOGGER.log(Level.INFO, "Diff size before filter : {0}", this.filteredDiff.size());
    }
    
    private boolean containsIgnoreCase(final List<String> list, final String value) {
        for (final String element : list) {
            if (value.equalsIgnoreCase(element)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
    
    private void setFilterValues() throws IOException {
        DiffResultFilter.LOGGER.log(Level.INFO, "Setting filter from conf file");
        final Properties filterProps = DiffToolUtil.getDiffToolProps();
        final String ignoreDifftype = filterProps.getProperty("difftool.filter.ignore.difftype");
        this.trimList(this.ignoreDiffTypes = Arrays.asList(ignoreDifftype.split(",")));
        final String ignoreModulename = filterProps.getProperty("difftool.filter.ignore.modulename");
        this.trimList(this.ignoreModuleNames = Arrays.asList(ignoreModulename.split(",")));
        final String ignoeTablename = filterProps.getProperty("difftool.filter.ignore.tablename");
        this.trimList(this.ignoreTableNames = Arrays.asList(ignoeTablename.split(",")));
        final String ignoreColumnName = filterProps.getProperty("difftool.filter.ignore.name");
        this.trimList(this.ignoreColumnNames = Arrays.asList(ignoreColumnName.split(",")));
        DiffResultFilter.LOGGER.log(Level.INFO, "Filters are set");
        DiffResultFilter.LOGGER.log(Level.FINE, "Filters list");
        DiffResultFilter.LOGGER.log(Level.FINE, "Difftypes : {0}", ignoreDifftype);
        DiffResultFilter.LOGGER.log(Level.FINE, "Modulenames : {0}", ignoreModulename);
        DiffResultFilter.LOGGER.log(Level.FINE, "Tablenames : {0}", ignoeTablename);
        DiffResultFilter.LOGGER.log(Level.FINE, "Columnnames : {0}", ignoreColumnName);
    }
    
    private void trimList(final List<String> array) {
        for (int index = 0; index < array.size(); ++index) {
            array.set(index, array.get(index).trim());
        }
    }
    
    public Map<String, JSONArray> filterDiffOutput(final Map<String, JSONArray> diffMap) {
        try {
            this.setFilterValues();
            this.filter(diffMap);
        }
        catch (final Exception e) {
            DiffResultFilter.LOGGER.log(Level.WARNING, "Caught exception in filtering diff result: ", e);
        }
        return this.filteredDiff;
    }
    
    static {
        LOGGER = Logger.getLogger("DiffToolLogger");
    }
}
