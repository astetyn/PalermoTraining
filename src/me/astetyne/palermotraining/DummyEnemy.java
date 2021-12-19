package me.astetyne.palermotraining;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class DummyEnemy extends PTEnemy {

    public DummyEnemy(PTGame game) {
        super(game, Color.GREEN);
    }

    @Override
    int getPoints() {
        return 1;
    }

    @Override
    HitType getHitType() {
        return HitType.EASY;
    }

}
