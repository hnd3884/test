package org.cyberneko.html;

public interface HTMLEventInfo
{
    int getBeginLineNumber();
    
    int getBeginColumnNumber();
    
    int getBeginCharacterOffset();
    
    int getEndLineNumber();
    
    int getEndColumnNumber();
    
    int getEndCharacterOffset();
    
    boolean isSynthesized();
    
    public static class SynthesizedItem implements HTMLEventInfo
    {
        public int getBeginLineNumber() {
            return -1;
        }
        
        public int getBeginColumnNumber() {
            return -1;
        }
        
        public int getBeginCharacterOffset() {
            return -1;
        }
        
        public int getEndLineNumber() {
            return -1;
        }
        
        public int getEndColumnNumber() {
            return -1;
        }
        
        public int getEndCharacterOffset() {
            return -1;
        }
        
        public boolean isSynthesized() {
            return true;
        }
        
        public String toString() {
            return "synthesized";
        }
    }
}
