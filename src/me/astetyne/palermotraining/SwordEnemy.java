package me.astetyne.palermotraining;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;

public class SwordEnemy extends PTEnemy {

    public SwordEnemy(PTGame game) {
        super(game, Color.AQUA);

        Vector newVec = game.middle.clone().subtract(body.getLocation()).toVector();
        moveVec = newVec.normalize().multiply(0.1 * game.difficulty);

        EntityEquipment ee = body.getEquipment();
        ee.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
    }

    @Override
    int getPoints() {
        return 3;
    }

    @Override
    HitType getHitType() {
        return HitType.SWORD;
    }

}
