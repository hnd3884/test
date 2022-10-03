package com.octo.captcha.engine.sound.utils;

import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat;
import java.io.File;
import javax.sound.sampled.AudioInputStream;

public class SoundToFile
{
    public static void serialize(final AudioInputStream audioInputStream, final File file) throws IOException {
        file.createNewFile();
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
    }
}
