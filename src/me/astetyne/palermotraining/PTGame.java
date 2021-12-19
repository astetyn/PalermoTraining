package me.astetyne.palermotraining;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class PTGame {

    final static int ARENA_RADIUS = 40;
    final static int DEFAULT_DELAY_BASE = 500;  // delay base in (ms)
    final static int DEFAULT_DELAY = 1000;      // delay between new entity spawn in (ms)
    final static int GAME_DURATION = 120;        // in (s)
    final static int START_COOLDOWN = 5;     // in (s)

    PalermoTrainingPlugin plugin;
    Location middle;
    int difficulty;
    boolean running;
    int secsPassed;

    PTPlayer p1, p2;

    List<PTEnemy> enemies;

    long gameStart;
    long lastSpawn = 0;
    long newSpawnDelay = 0;

    public PTGame(PalermoTrainingPlugin plugin, Player player1, Player player2, int difficulty) {

        plugin.getServer().getPluginManager().registerEvents(plugin, plugin);

        gameStart = System.currentTimeMillis();
        middle = player1.getLocation();
        this.difficulty = difficulty;
        this.plugin = plugin;
        running = true;
        secsPassed = 0;

        p1 = new PTPlayer(player1);
        p2 = new PTPlayer(player2);

        enemies = new ArrayList<>();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            tickEnemies();
            spawnNewEnemies();

            if(gameStart + GAME_DURATION * 1000 < System.currentTimeMillis()) {
                endGame();
            }

        }, START_COOLDOWN * 20, 0);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

            if(secsPassed % 30 == 0) {
                int remaining = GAME_DURATION - secsPassed;
                sendToBothPlayers("&e&l" + remaining + " &6sekund do konca.");
            }

            if(secsPassed >= GAME_DURATION) {
                endGame();
            }
            secsPassed++;

        }, START_COOLDOWN, 20);

        for(int i = 0; i < 10; i++) {
            enemies.add(new FriendlyEnemy(this));
        }

    }

    public void endGame() {
        onDisable();
        sendPointsMsg(p1, p2);
        sendPointsMsg(p2, p1);
    }

    void sendPointsMsg(PTPlayer me, PTPlayer friend) {
        me.sendMsg("&6&l---- Hra skoncila! ----");
        me.sendMsg("\n&e  " + me.player.getName() + ": &r&l" + me.getPoints());
        me.sendMsg(me.generatePointsMsg());
        me.sendMsg("\n&e  " + friend.player.getName() + ": &r&l" + friend.getPoints());
        me.sendMsg(friend.generatePointsMsg());
        me.sendMsg("\n&6&l---------------------");

        if(me.getPoints() > friend.getPoints()) {
            me.player.playSound(me.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            me.sendMsgBig("&aVyhral/a si!", "");
            spawnFireworks(me.player.getLocation(), 1);
        }else {
            me.player.playSound(me.player.getLocation(), Sound.ENTITY_CAT_PURREOW, 1, 1);
            me.sendMsgBig("&cPrehral/a si.", "&rMozno nabuduce.");
        }

    }

    void sendToBothPlayers(String s) {
        p1.sendMsg(s);
        p2.sendMsg(s);
    }

    public void onArrowHit(ProjectileHitEvent e) {
        PTEnemy target = getEnemy((ArmorStand) e.getHitEntity());
        PTPlayer shooter = getPTPlayer((Player) e.getEntity().getShooter());

        if(target == null) {
            return;
        }

        e.getEntity().remove();

        if(target instanceof SwordEnemy) {
            //e.setCancelled(true); -- not working with 1.15.2
            return;
        }

        if(shooter == null) {
            return;
        }

        target.body.remove();
        enemies.remove(target);

        Location loc = target.body.getLocation();
        loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc.add(0, 1, 0), 1);

        shooter.addPoints(target.getHitType(), target.getPoints());
    }

    public void onASHit(EntityDamageByEntityEvent e) {
        PTEnemy target = getEnemy((ArmorStand) e.getEntity());
        PTPlayer shooter = getPTPlayer((Player) e.getDamager());

        if(target != null && !(target instanceof SwordEnemy)) {
            e.setCancelled(true);
            return;
        }

        if(shooter == null) {
            return;
        }

        if(shooter.player.getInventory().getItemInMainHand().getType() != Material.IRON_SWORD) {
            e.setCancelled(true);
            return;
        }

        assert target != null;
        target.body.remove();
        enemies.remove(target);

        Location loc = target.body.getLocation();
        loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc.add(0, 1, 0), 1);
        shooter.addPoints(target.getHitType(), target.getPoints());
    }

    PTEnemy getEnemy(ArmorStand as) {
        for(PTEnemy enemy : enemies) {
            if(enemy.body == as) {
                return enemy;
            }
        }
        return null;
    }

    PTPlayer getPTPlayer(Player p) {
        if(p1.player == p) {
            return p1;
        }else if(p2.player == p){
            return p2;
        }
        return null;
    }

    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(plugin);
        for(PTEnemy e : enemies) {
            e.body.remove();
        }
        running = false;
        HandlerList.unregisterAll((Plugin) plugin);
    }

    void tickEnemies() {
        List<PTEnemy> copy = new ArrayList<>(enemies);
        for(PTEnemy e : copy) {
            e.tick();
            if(e.body.isDead()) {
                enemies.remove(e);
            }
        }
    }

    void spawnNewEnemies() {
        if(lastSpawn + newSpawnDelay < System.currentTimeMillis()) {
            lastSpawn = System.currentTimeMillis();
            newSpawnDelay = (long) (Math.random() * DEFAULT_DELAY) + DEFAULT_DELAY_BASE;

            if(Math.random() < 0.1) {
                enemies.add(new SwordEnemy(this));
            }

            if(Math.random() < 0.3) {
                enemies.add(new DummyEnemy(this));
            }

            if(Math.random() < 0.2) {
                enemies.add(new ChaosEnemy(this));
            }

            if(Math.random() < 0.1) {
                enemies.add(new FriendlyEnemy(this));
            }
        }
    }

    public static void spawnFireworks(Location location, int amount) {
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.RED).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();

        for(int i = 0;i < amount; i++){
            Firework fw2 = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }
}
