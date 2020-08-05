package com.BSISJ7.TestCreator.utilities;
/*
 * Taken from The Java Developers Almanac 1.4

 * Modifications by Crysomere
 */

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;


public class playMusic {

    private File music;
    private Clip clip;
    private boolean loop = false;
    private boolean playing = false;
    private boolean pause = false;
    private boolean change = false;
    private int loopNum = -1;
    private int playAt = -1;
    private String musicName;
    private String changeMusicName;
    private BooleanControl muteControl;
    private FloatControl gainControl;
    private Boolean firstRun = true;
    private boolean changed = false;

    public playMusic(File music, boolean loop, int playAt) {
        this.playAt = playAt * 100000;
        this.loop = loop;
        this.music = music;
        musicName = music.getName();
    }

    public playMusic(File music, int loopNum, int playAt) {
        this.playAt = playAt;
        this.loopNum = loopNum;
        this.music = music;
        musicName = music.getName();
    }

    public playMusic(File music, boolean loop) {
        this.loop = loop;
        this.music = music;
        musicName = music.getName();
    }

    public playMusic(File music, int loopNum) {
        this.loopNum = loopNum;
        this.music = music;
        musicName = music.getName();
    }


    public void setMusic(File music, boolean loop, int playAt) {
        this.playAt = playAt * 100000;
        this.loop = loop;
        this.music = music;
        musicName = music.getName();
    }

    public void setMusic(File music, int loopNum, int playAt) {
        this.playAt = playAt;
        this.loopNum = loopNum;
        this.music = music;
        musicName = music.getName();
    }

    public void setMusic(File music, boolean loop) {
        this.loop = loop;
        this.music = music;
        musicName = music.getName();
    }

    public void setMusic(File music, int loopNum) {
        this.loopNum = loopNum;
        this.music = music;
        musicName = music.getName();
    }

    public String getMusicName() {
        return musicName;
    }

    public void changeMusic(String fileName, boolean loop) {
        music = new File(fileName);
        this.loop = loop;
        change = true;
        musicName = music.getName();
    }

    public void changeMusic(String fileName, int loop) {
        music = new File(fileName);
        this.loopNum = loop;
        change = true;
        musicName = music.getName();
    }

    public void startMusic() {
        try {
            changed = true;
            if (!changeMusicName.equals(musicName)) {
                musicName = changeMusicName;
                music = new File(musicName);
                clip.stop();
                clip.close();
                playMusic();
            } else if (playing == false && pause == false) {
                playing = true;
                try {
                    clip.start();
                } catch (NullPointerException NPE) {
                    playMusic();
                }
            } else if (playing == false && pause == true) {
                clip.start();
                playing = true;
                pause = false;
            } else if (playing == true && pause == false) {
                clip.stop();
                clip.close();
                playMusic();
            }
        } catch (NullPointerException NPE) {
            playMusic();
        }
        changed = false;
    }

    public void pauseMusic() {
        playing = false;
        pause = true;
        clip.stop();
    }

    public void stopMusic() {
        if (playing == true) {
            playing = false;
            pause = false;
            clip.stop();
            clip.close();
        }
    }

    public boolean getPaused() {
        return pause;
    }

    public boolean getPlaying() {
        return playing;
    }

    public void setNextSong(String songName) {
        changeMusicName = songName;
    }

    public double getDuration() {
        return clip.getBufferSize() / (clip.getFormat().getFrameSize() * clip.getFormat().getFrameRate());
    }

    public double getPosition() {
        return clip.getMicrosecondPosition() / 1000000.0d;
    }

    public void setPosition(long position) {
        clip.setMicrosecondPosition(position);
    }

    public int getPositionInt() {
        return (int) clip.getMicrosecondPosition();
    }

    public double getVolume() {
        return gainControl.getValue();
    }

    public void setVolume(float val) {
        //To create a Clip object, see Loading and Playing Sampled Audio // Set Volume
        double gain = val; // number between 0 and 1 (loudest)
        float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
        System.out.println("Gain Control: " + dB);
        try {
            gainControl.setValue(dB);
        } catch (NullPointerException NPE) {
        }
    }

    public double getMaxPosition() {
        return clip.getMicrosecondLength();
    }

    public void mute() {
        try {
            if (muteControl.getValue() == false) {
                muteControl.setValue(true);
            } else {
                muteControl.setValue(false);
            }
        } catch (NullPointerException NPE) {
        }
    }

    public boolean getChangedSong() {
        return changed;
    }

    public void setChangedSong(boolean change) {
        changed = change;
    }

    public void playMusic() {
        playing = true;
        pause = false;
        try {
            // From file
            AudioInputStream stream = AudioSystem.getAudioInputStream(music);

            // From URL
            //stream = AudioSystem.getAudioInputStream(new URL("http://hostname/audiofile"));

            // At present, ALAW and ULAW encodings must be converted
            // to PCM_SIGNED before it can be played
            AudioFormat format = stream.getFormat();
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                format = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        format.getSampleRate(),
                        format.getSampleSizeInBits() * 2,
                        format.getChannels(),
                        format.getFrameSize() * 2,
                        format.getFrameRate(),
                        true);        // big endian
                stream = AudioSystem.getAudioInputStream(format, stream);
            }

            // Create the clip
            DataLine.Info info = new DataLine.Info(
                    Clip.class, stream.getFormat(), ((int) stream.getFrameLength() * format.getFrameSize()));
            Clip clip = (Clip) AudioSystem.getLine(info);
            this.clip = clip;
            // This method does not return until the audio file is completely loaded
            clip.open(stream);

            // Start playing
            if (playAt != -1)
                clip.setMicrosecondPosition(playAt);
            clip.start();
            if (loop) {
                clip.loop(-1);
            }
            if (loopNum != -1) {
                clip.loop(loopNum);
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } catch (LineUnavailableException e) {
        } catch (UnsupportedAudioFileException e) {
        }
        muteControl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
        gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        if (firstRun) {
            setVolume((float) 11 / 20);
            firstRun = false;
        }
    }
}
