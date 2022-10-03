package org.apache.commons.compress.archivers.sevenz;

public class SevenZFileOptions
{
    private static final int DEFAUL_MEMORY_LIMIT_IN_KB = Integer.MAX_VALUE;
    private static final boolean DEFAULT_USE_DEFAULTNAME_FOR_UNNAMED_ENTRIES = false;
    private static final boolean DEFAULT_TRY_TO_RECOVER_BROKEN_ARCHIVES = false;
    private final int maxMemoryLimitInKb;
    private final boolean useDefaultNameForUnnamedEntries;
    private final boolean tryToRecoverBrokenArchives;
    public static final SevenZFileOptions DEFAULT;
    
    private SevenZFileOptions(final int maxMemoryLimitInKb, final boolean useDefaultNameForUnnamedEntries, final boolean tryToRecoverBrokenArchives) {
        this.maxMemoryLimitInKb = maxMemoryLimitInKb;
        this.useDefaultNameForUnnamedEntries = useDefaultNameForUnnamedEntries;
        this.tryToRecoverBrokenArchives = tryToRecoverBrokenArchives;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public int getMaxMemoryLimitInKb() {
        return this.maxMemoryLimitInKb;
    }
    
    public boolean getUseDefaultNameForUnnamedEntries() {
        return this.useDefaultNameForUnnamedEntries;
    }
    
    public boolean getTryToRecoverBrokenArchives() {
        return this.tryToRecoverBrokenArchives;
    }
    
    static {
        DEFAULT = new SevenZFileOptions(Integer.MAX_VALUE, false, false);
    }
    
    public static class Builder
    {
        private int maxMemoryLimitInKb;
        private boolean useDefaultNameForUnnamedEntries;
        private boolean tryToRecoverBrokenArchives;
        
        public Builder() {
            this.maxMemoryLimitInKb = Integer.MAX_VALUE;
            this.useDefaultNameForUnnamedEntries = false;
            this.tryToRecoverBrokenArchives = false;
        }
        
        public Builder withMaxMemoryLimitInKb(final int maxMemoryLimitInKb) {
            this.maxMemoryLimitInKb = maxMemoryLimitInKb;
            return this;
        }
        
        public Builder withUseDefaultNameForUnnamedEntries(final boolean useDefaultNameForUnnamedEntries) {
            this.useDefaultNameForUnnamedEntries = useDefaultNameForUnnamedEntries;
            return this;
        }
        
        public Builder withTryToRecoverBrokenArchives(final boolean tryToRecoverBrokenArchives) {
            this.tryToRecoverBrokenArchives = tryToRecoverBrokenArchives;
            return this;
        }
        
        public SevenZFileOptions build() {
            return new SevenZFileOptions(this.maxMemoryLimitInKb, this.useDefaultNameForUnnamedEntries, this.tryToRecoverBrokenArchives, null);
        }
    }
}
