package me.astetyne.palermotraining;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import static me.astetyne.palermotraining.PTGame.ARENA_RADIUS;

public abstract class PTEnemy {

    final double speedFactor = 0.1;

    PTGame game;
    ArmorStand body;
    Vector moveVec;

    public PTEnemy(PTGame game, Color chestplateColor) {
        this.game = game;
        Location startLoc = game.middle.clone();
        startLoc.add(getRandArenaOffset(), 0, getRandArenaOffset());
        body = (ArmorStand) game.middle.getWorld().spawnEntity(startLoc, EntityType.ARMOR_STAND);
        moveVec = getRandVec(game.difficulty);

        body.setBasePlate(false);
        body.setArms(true);
        body.setMarker(false);

        EntityEquipment ee = body.getEquipment();

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
        meta.setColor(chestplateColor);
        chestplate.setItemMeta(meta);
        ee.setChestplate(chestplate);

        ee.setHelmet(new ItemStack(Material.PLAYER_HEAD));
        ee.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        ee.setBoots(new ItemStack(Material.LEATHER_BOOTS));
        ee.setItemInMainHand(new ItemStack(Material.BOW));
    }

    abstract int getPoints();

    abstract HitType getHitType();

    public void tick() {
        // TODO: make this more efficient
        Location newLoc = body.getLocation().add(moveVec);
        newLoc.setPitch(0);
        newLoc.setYaw(getLookAtYaw(moveVec));
        body.teleport(newLoc);
        body.setVelocity(moveVec);
        if(body.getLocation().distance(game.middle) > ARENA_RADIUS) {
            body.remove();
        }
    }

    double getRandArenaOffset() {
        return (Math.random() - 0.5) * ARENA_RADIUS / 2;
    }

    Vector getRandVec(int difficulty) {
        Vector v = new Vector(Math.random() - 0.5, 0, Math.random() - 0.5);
        v.multiply(speedFactor);
        v.multiply(difficulty);
        return v;
    }

    // copied and modified from https://bukkit.org/threads/how-do-i-get-yaw-and-pitch-from-a-vector.50317/
    public float getLookAtYaw(Vector motion) {
        double dx = motion.getX();
        double dz = motion.getZ();
        double yaw = 0;
        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                yaw = 1.5 * Math.PI;
            } else {
                yaw = 0.5 * Math.PI;
            }
            yaw -= Math.atan(dz / dx);
        } else if (dz < 0) {
            yaw = Math.PI;
        }
        return (float) (-yaw * 180 / Math.PI);
    }

}
