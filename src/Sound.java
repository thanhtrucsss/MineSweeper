package src;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Sound {
    private static final int NUM_SOUNDS = 5;
    public static Object isMuted;
    private static Clip[] clips;
    private static final String FILE_PATH_PREFIX = "";
    private static boolean soundEnabled = true;
    private static float volume = 1.0f; // 1.0 = 100%
    private static boolean muted = false;

    static {
        clips = new Clip[NUM_SOUNDS];
        for (int i = 0; i < NUM_SOUNDS; i++) {
            loadSound(i + 1, "sound" + (i + 1) + ".wav");
        }
    }

    private static void loadSound(int index, String fileName) {
        try {
            String filePath = FILE_PATH_PREFIX + fileName;
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
            clips[index - 1] = AudioSystem.getClip();
            clips[index - 1].open(audioInputStream);
            setClipVolume(clips[index - 1], volume); // Set initial volume
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private static void setClipVolume(Clip clip, float vol) {
        if (clip == null) return;
        try {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            // dB conversion
            float min = gain.getMinimum();
            float max = gain.getMaximum();
            // Linear to dB
            float db = (vol == 0f) ? min : (float)(Math.log10(vol) * 20.0);
            if (db < min) db = min;
            if (db > max) db = max;
            gain.setValue(db);
        } catch (Exception ignored) { }
    }

    public static void playSound(int index) {
        if (soundEnabled && !muted && index >= 1 && index <= NUM_SOUNDS && clips[index - 1] != null) {
            if (clips[index - 1].isRunning()) {
                clips[index - 1].stop();
            }
            clips[index - 1].setFramePosition(0);
            setClipVolume(clips[index - 1], volume);
            clips[index - 1].start();
        }
    }

    public static void toggleSound() {
        setSoundEnabled(!getSoundEnabled());
        System.out.println("Mute/Unmute");
    }

    public static void stopSound(int index) {
        if (index >= 1 && index <= NUM_SOUNDS && clips[index - 1] != null) {
            if (clips[index - 1].isRunning()) {
                clips[index - 1].stop();
            }
        }
    }

    public static void startSound(int index) {
        if (index >= 1 && index <= NUM_SOUNDS && clips[index - 1] != null) {
            clips[index - 1].setFramePosition(0);
            setClipVolume(clips[index - 1], volume);
            clips[index - 1].start();
        }
    }

    public static boolean getSoundEnabled() {
        return soundEnabled;
    }
    public static void setSoundEnabled(boolean n) {
        soundEnabled = n;
    }

    // Volume control API
    public static void setVolumePercent(int percent) {
        if (percent < 0) percent = 0;
        if (percent > 100) percent = 100;
        volume = percent / 100.0f;
        if (volume == 0) muted = true;
        else muted = false;
        // Áp dụng cho tất cả clip đã load
        for (Clip clip : clips) setClipVolume(clip, volume);
    }
    public static int getVolumePercent() {
        return (int) (volume * 100);
    }

    public static void setMuted(boolean m) {
        muted = m;
        if (muted) {
            // Volume về 0, nhưng nhớ lại volume trước đó nếu muốn restore
            for (Clip clip : clips) setClipVolume(clip, 0f);
        } else {
            for (Clip clip : clips) setClipVolume(clip, volume);
        }
    }
    public static boolean isMuted() {
        return muted;
    }

}
