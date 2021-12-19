package me.astetyne.palermotraining;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PalermoTrainingPlugin extends JavaPlugin implements Listener {

    PTGame game;

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        if(game != null) {
            game.onDisable();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage("Only player can start new game.");
            return false;
        }

        if(args.length != 2) {
            return false;
        }

        int difficulty;
        try{
            difficulty = Integer.parseInt(args[0]);
        }catch(NumberFormatException e) {
            sender.sendMessage("Obtiaznost musi byt medzi 1-3.");
            return true;
        }
        difficulty = Math.max(difficulty, 1);
        difficulty = Math.min(difficulty, 3);

        Player secondPlayer = Bukkit.getPlayer(args[1]);
        if(secondPlayer == null || !secondPlayer.isOnline()) {
            sender.sendMessage("Neplatne meno druheho hraca.");
            return true;
        }

        if(game != null && game.running) {
            sender.sendMessage("Momentalne prebieha hra, skus to neskor.");
            return true;
        }

        game = new PTGame(this, (Player)sender, secondPlayer, difficulty);

        return true;
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent e) {
        if(!(e.getHitEntity() instanceof ArmorStand)
                || !(e.getEntity() instanceof Arrow)
                || !(e.getEntity().getShooter() instanceof Player)) {
            return;
        }
        game.onArrowHit(e);
    }

    @EventHandler
    public void onEnemySlay(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player && game.getPTPlayer((Player) e.getEntity()) != null) {
            e.setCancelled(true);
            return;
        }
        if(!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof ArmorStand)) {
            return;
        }
        game.onASHit(e);
    }

    @EventHandler
    public void onASManipulate(PlayerArmorStandManipulateEvent e) {
        for(PTEnemy enemy : game.enemies) {
            if(enemy.body == e.getRightClicked()) {
                e.setCancelled(true);
                return;
            }
        }
    }
}
