package me.astetyne.palermotraining;

import org.bukkit.Color;
import org.bukkit.Location;

public class ChaosEnemy extends PTEnemy {

    final int DELAY = 3000;

    long lastDirChange;

    public ChaosEnemy(PTGame game) {
        super(game, Color.YELLOW);
        lastDirChange = System.currentTimeMillis();
    }

    @Override
    public void tick() {
        if(lastDirChange + DELAY / game.difficulty < System.currentTimeMillis()) {
            lastDirChange = System.currentTimeMillis();
            moveVec = getRandVec(game.difficulty).multiply(2);
        }
        super.tick();
    }

    @Override
    int getPoints() {
        return 2;
    }

    @Override
    HitType getHitType() {
        return HitType.HARD;
    }
}
