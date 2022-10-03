package javax.sound.midi;

public class VoiceStatus
{
    public boolean active;
    public int channel;
    public int bank;
    public int program;
    public int note;
    public int volume;
    
    public VoiceStatus() {
        this.active = false;
        this.channel = 0;
        this.bank = 0;
        this.program = 0;
        this.note = 0;
        this.volume = 0;
    }
}
