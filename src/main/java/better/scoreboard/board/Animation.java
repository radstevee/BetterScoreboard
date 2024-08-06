package better.scoreboard.board;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;

/**
 * This represents a single line on the scoreboard. It can cycle through multiple lines of text and randomly select
 * a line if set to.
 *
 * @Author: am noah
 * @Since: 1.0.0
 * @Updated: 1.0.0
 */
public class Animation {

    private final List<String> animation;
    private final int animationSpeed;
    private final boolean random;

    private int currentIndex, currentTick;
    private boolean updateTick;

    /**
     * Initialize the Animation, reading required data from the configuration.
     */
    public Animation(ConfigurationSection config) {
        currentIndex = currentTick = 0;

        if (config == null) {
            animationSpeed = 1;
            random = false;
            animation = Collections.singletonList("Invalid config!");
            return;
        }

        random = config.getBoolean("random", false);
        animationSpeed = config.getInt("animation-speed", 1);
        animation = config.getStringList("animation");

        if (random) currentIndex = (int) (animation.size() * Math.random());
        if (animationSpeed < 0) updateTick = true;

    }

    /*
     * Getters.
     */

    /**
     * Returns the current line of text that should be displayed by this animation.
     */
    public String getLine() {
        return animation.get(currentIndex);
    }

    /**
     * Return whether the current tick is an animation update tick.
     */
    public boolean isUpdateTick() {
        return updateTick;
    }

    /*
     * Functions.
     */

    /**
     * Progresses the animation forward by 1 tick.
     */
    public void tick() {
        // Should not proceed if it's a static board.
        if (animationSpeed < 0) return;

        // Only progress further once the animation's refresh rate has elapsed.
        currentTick++;
        updateTick = false;
        if (currentTick < animationSpeed) return;
        currentTick = 0;
        updateTick = true;

        // Randomly select an index if that's chosen in the config.
        if (random) {
            currentIndex = (int) (Math.random() * animation.size());
            return;
        }

        // Otherwise progress the current index.
        currentIndex++;
        if (currentIndex == animation.size()) currentIndex = 0;
    }
}
