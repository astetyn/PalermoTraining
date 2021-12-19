package me.astetyne.palermotraining;

import org.bukkit.Color;

public class FriendlyEnemy extends PTEnemy {

    final int AVG_DELAY = 2000;

    long lastDirChange;

    public FriendlyEnemy(PTGame game) {
        super(game, Color.RED);
        lastDirChange = System.currentTimeMillis();
    }

    @Override
    public void tick() {
        if(lastDirChange + AVG_DELAY / 2.0 + Math.random() * AVG_DELAY < System.currentTimeMillis()) {
            lastDirChange = System.currentTimeMillis();
            moveVec = getRandVec(game.difficulty).multiply(0.5);
        }
        super.tick();
    }

    @Override
    int getPoints() {
        return -2;
    }

    @Override
    HitType getHitType() {
        return HitType.FRIENDLY;
    }
}
