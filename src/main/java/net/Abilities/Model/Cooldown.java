package net.Abilities.Model;

public class Cooldown {
    private final long lastUsed = System.currentTimeMillis();
    private final long cooldownTimeNumber;

    public Cooldown(long cooldownTimeNumber) {
        this.cooldownTimeNumber = cooldownTimeNumber;
    }

    public long getTimeLeft() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - lastUsed) / 1000;
        return cooldownTimeNumber - elapsedTime;
    }

    public boolean hasFinished() {
        return (System.currentTimeMillis() - lastUsed) > (cooldownTimeNumber * 1000);
    }
}
