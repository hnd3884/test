package javax.sound.midi;

public class Patch
{
    private final int bank;
    private final int program;
    
    public Patch(final int bank, final int program) {
        this.bank = bank;
        this.program = program;
    }
    
    public int getBank() {
        return this.bank;
    }
    
    public int getProgram() {
        return this.program;
    }
}
