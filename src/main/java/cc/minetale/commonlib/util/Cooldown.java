package cc.minetale.commonlib.util;

import lombok.Getter;

import java.time.Duration;

@Getter
public class Cooldown {

    private final Duration duration;
    private long lastUpdate;

    public Cooldown(Duration duration) {
        this.duration = duration;
    }

    public void refresh() {
        lastUpdate = System.currentTimeMillis();
    }

    public boolean isReady() {
        return !hasCooldown();
    }

    public String getSecondsRemaining() {
        return TimeUtil.millisToSeconds(getMillisRemaining());
    }

    public long getMillisRemaining() {
        return (lastUpdate + duration.toMillis()) - System.currentTimeMillis();
    }

    public boolean hasCooldown() {
        return getMillisRemaining() > 0;
    }

}
