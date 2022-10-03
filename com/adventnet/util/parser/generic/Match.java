package com.adventnet.util.parser.generic;

import java.util.Hashtable;

class Match
{
    Hashtable patternList;
    
    Match() {
        this.patternList = null;
    }
    
    void setPatternList(final Hashtable patternList) {
        this.patternList = patternList;
    }
    
    Hashtable getPatternList() {
        return this.patternList;
    }
}
