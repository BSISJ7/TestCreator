package TestCreator.options;

public class Options {
    private String theme;
    private boolean isAudioEnabled;

    private float playbackSpeed;

    public Options(){
        this.theme = OptionsMenu.CSS_THEMES.PRIMER_DARK.getTheme();
        this.isAudioEnabled = true;
        this.playbackSpeed = 1.0f;
    }

    public Options(String theme, boolean isAudioEnabled, float playbackSpeed) {
        this.theme = theme;
        this.isAudioEnabled = isAudioEnabled;
        this.playbackSpeed = playbackSpeed;
    }

    public Options(String theme, boolean isAudioEnabled) {
        this.theme = theme;
        this.isAudioEnabled = isAudioEnabled;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public boolean isAudioEnabled() {
        return isAudioEnabled;
    }

    public void setAudioEnabled(boolean audioEnabled) {
        isAudioEnabled = audioEnabled;
    }

    public float getPlaybackSpeed() {
        return playbackSpeed;
    }

    public void setPlaybackSpeed(float playbackSpeed) {
        this.playbackSpeed = playbackSpeed;
    }
}
