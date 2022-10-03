package com.adventnet.persistence.internal;

import java.util.Hashtable;
import com.adventnet.db.persistence.metadata.UniqueValueGeneration;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.logging.Level;
import com.adventnet.persistence.template.TemplateUtil;
import java.util.List;
import com.adventnet.db.persistence.metadata.DataDictionary;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.persistence.PersistenceException;
import java.util.logging.Logger;
import com.adventnet.db.persistence.SequenceGenerator;
import java.util.HashMap;
import java.util.Properties;

public class SequenceGeneratorRepository
{
    private static Properties typeVsGen;
    private static HashMap<String, SequenceGenerator> repository;
    private static final Logger OUT;
    
    private SequenceGeneratorRepository() {
    }
    
    public static void put(final String dataType, final String genClassName) {
        ((Hashtable<String, String>)SequenceGeneratorRepository.typeVsGen).put(dataType, genClassName);
    }
    
    public static synchronized void add(final String name, final SequenceGenerator gen) {
        if (!SequenceGeneratorRepository.repository.containsKey(name)) {
            SequenceGeneratorRepository.repository.put(name, gen);
        }
    }
    
    public static void rename(final String oldName, final String newName, final String dataType, final String genClassName) throws PersistenceException {
        synchronized (SequenceGeneratorRepository.class) {
            SequenceGenerator gen = SequenceGeneratorRepository.repository.remove(oldName);
            if (gen == null) {
                try {
                    gen = createSequenceGenerator(oldName, dataType, genClassName);
                }
                catch (final Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }
            gen.renameTo(newName);
            SequenceGeneratorRepository.repository.put(newName, gen);
        }
    }
    
    public static SequenceGenerator remove(final String name) {
        return SequenceGeneratorRepository.repository.remove(name);
    }
    
    public static SequenceGenerator get(final String name) {
        final SequenceGenerator gen = SequenceGeneratorRepository.repository.get(name);
        return gen;
    }
    
    public static SequenceGenerator getOrCreate(final String name, final String dataType) {
        SequenceGenerator gen = get(name);
        if (gen != null) {
            return gen;
        }
        try {
            gen = createSequenceGenerator(name, dataType, null);
            add(name, gen);
        }
        catch (final Exception exp) {
            throw new IllegalArgumentException("Incorrect Sequence Generator Name {" + name + "}", exp);
        }
        return gen;
    }
    
    public static void initGeneratorValues(final String moduleName) throws PersistenceException {
        DataDictionary dd;
        try {
            dd = MetaDataUtil.getDataDictionary(moduleName);
        }
        catch (final MetaDataException mde) {
            throw new PersistenceException("MetaDataException during initGeneratorValues", mde);
        }
        final List<TableDefinition> tableDefs = dd.getTableDefinitions();
        for (int len = tableDefs.size(), i = 0; i < len; ++i) {
            final TableDefinition td = tableDefs.get(i);
            initGeneratorValues(td);
        }
    }
    
    public static void initGeneratorValues(final TableDefinition td) throws PersistenceException {
        initGeneratorValues(td, null);
    }
    
    public static void initGeneratorValues(final TableDefinition td, final String templateInstanceID) throws PersistenceException {
        final List<String> colNames = td.getColumnNames();
        for (int noOfCols = colNames.size(), j = 0; j < noOfCols; ++j) {
            final String colName = colNames.get(j);
            final ColumnDefinition cd = td.getColumnDefinitionByName(colName);
            final String dataType = cd.getDataType();
            final UniqueValueGeneration uvg = cd.getUniqueValueGeneration();
            if (uvg != null) {
                String generatorName;
                if (!uvg.isInstanceSpecificSequenceGeneratorEnabled()) {
                    generatorName = uvg.getGeneratorName();
                }
                else {
                    if (td.isTemplate() && templateInstanceID == null) {
                        throw new IllegalArgumentException("InstanceID can not be null, while creating sequence generator for template instance");
                    }
                    generatorName = uvg.getGeneratorNameForTemplateInstance(TemplateUtil.getTableName(td.getTableName(), templateInstanceID), cd.getColumnName());
                }
                synchronized (generatorName) {
                    SequenceGenerator gen = get(generatorName);
                    Label_0226: {
                        if (gen == null) {
                            try {
                                gen = createSequenceGenerator(generatorName, dataType, uvg.getGeneratorClass());
                                add(generatorName, gen);
                                SequenceGeneratorRepository.OUT.log(Level.FINEST, " CREATED generator {0} for {1}", new Object[] { gen, generatorName });
                                break Label_0226;
                            }
                            catch (final Exception ie) {
                                throw new PersistenceException("Exception when creating SequenceGenerator", ie);
                            }
                        }
                        SequenceGeneratorRepository.OUT.log(Level.FINEST, "Generator for {0} already found : {1}", new Object[] { generatorName, gen });
                    }
                }
            }
        }
    }
    
    private static SequenceGenerator createSequenceGenerator(final String generatorName, final String dataType, String genClassName) throws PersistenceException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        SequenceGeneratorRepository.OUT.log(Level.FINEST, " Creating generator for {0}.....", generatorName);
        if (genClassName == null) {
            genClassName = SequenceGeneratorRepository.typeVsGen.getProperty(dataType);
        }
        SequenceGeneratorRepository.OUT.log(Level.FINEST, " Generator className {0}.....", genClassName);
        final SequenceGenerator gen = (SequenceGenerator)Class.forName(genClassName).newInstance();
        gen.init(generatorName);
        return gen;
    }
    
    public static void removeGeneratorValues(final String moduleName) throws PersistenceException {
        DataDictionary dd;
        try {
            dd = MetaDataUtil.getDataDictionary(moduleName);
        }
        catch (final MetaDataException mde) {
            throw new PersistenceException("Exception ", mde);
        }
        final List<TableDefinition> tableDefns = dd.getTableDefinitions();
        final int size = tableDefns.size();
        for (int i = size - 1; i >= 0; --i) {
            final TableDefinition td = tableDefns.get(i);
            SequenceGeneratorRepository.OUT.log(Level.FINEST, "****************************  TableDefinition ************ {0}", td.getTableName());
            removeGeneratorValues(td);
        }
    }
    
    public static void removeGeneratorValues(final TableDefinition td, final String templateInstanceID) throws PersistenceException {
        final List<String> colNames = td.getColumnNames();
        for (int noOfCols = colNames.size(), j = 0; j < noOfCols; ++j) {
            final String colName = colNames.get(j);
            final ColumnDefinition cd = td.getColumnDefinitionByName(colName);
            final String dataType = cd.getDataType();
            final UniqueValueGeneration uvg = cd.getUniqueValueGeneration();
            if (uvg != null) {
                String generatorName;
                if (!uvg.isInstanceSpecificSequenceGeneratorEnabled() && templateInstanceID == null) {
                    generatorName = uvg.getGeneratorName();
                }
                else {
                    generatorName = uvg.getGeneratorNameForTemplateInstance(TemplateUtil.getTableName(td.getTableName(), templateInstanceID), cd.getColumnName());
                }
                final SequenceGenerator gen = remove(generatorName);
                if (gen != null) {
                    gen.cleanup();
                    gen.remove();
                    SequenceGeneratorRepository.OUT.log(Level.FINEST, "Removed instance {0} for generatorName : {1}", new Object[] { gen, generatorName });
                }
                else {
                    SequenceGeneratorRepository.OUT.log(Level.FINEST, " Cannot removed SequenceGenerator for generatorName : {0} - sequencegenerator is null", generatorName);
                }
            }
        }
    }
    
    public static void removeGeneratorValues(final TableDefinition td) throws PersistenceException {
        removeGeneratorValues(td, null);
    }
    
    static {
        SequenceGeneratorRepository.typeVsGen = new Properties();
        SequenceGeneratorRepository.repository = new HashMap<String, SequenceGenerator>();
        OUT = Logger.getLogger(SequenceGeneratorRepository.class.getName());
        ((Hashtable<String, String>)SequenceGeneratorRepository.typeVsGen).put("INTEGER", "com.adventnet.db.persistence.IntegerSequenceGenerator");
        ((Hashtable<String, String>)SequenceGeneratorRepository.typeVsGen).put("BIGINT", "com.adventnet.db.persistence.LongSequenceGenerator");
    }
}
