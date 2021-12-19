package me.astetyne.palermotraining;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PTPlayer {

    Player player;
    int easyHits, swordHits, hardHits, friendlyHits;

    public PTPlayer(Player player) {
        this.player = player;
        easyHits = 0;
        hardHits = 0;
        swordHits = 0;
        friendlyHits = 0;

        sendMsgBig("&6- Hra zacina -", "&epalermo trening");
        sendMsg("&6&l---- Palermo Trening ----");
        sendMsg("&azelena &r(luk) &l-> &a&l+1");
        sendMsg("&ezlta &r(luk) &l-> &e&l+2");
        sendMsg("&bmodra &r(mec) &l-> &b&l+3");
        sendMsg("&ccervena &r(luk) &l-> &c&l-2");
    }

    public void addPoints(HitType hitType, int points) {
        ChatColor color = null;

        switch(hitType) {
            case EASY: {
                easyHits++;
                color = ChatColor.GREEN;
                break;
            }
            case HARD: {
                hardHits++;
                color = ChatColor.YELLOW;
                break;
            }
            case SWORD: {
                swordHits++;
                color = ChatColor.AQUA;
                break;
            }
            case FRIENDLY: {
                friendlyHits++;
                color = ChatColor.RED;
                break;
            }
        }

        if(points >= 0) {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        }else {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
        }

        String sign = points >= 0 ? "+" : "";
        sendMsg("&6Zasah "+color+"&l" + sign + points);
    }

    public int getPoints() {
        return easyHits + hardHits * 2 + swordHits * 3 - friendlyHits * 2;
    }

    public String generatePointsMsg() {
        return "&7(&a" + easyHits + "&7/&e" + hardHits + "&7/&b" + swordHits + "&7/&c" + friendlyHits + "&7)";
    }

    public void sendMsg(String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        player.sendMessage(msg);
    }

    public void sendMsgBig(String msg, String smallMsg) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        smallMsg = ChatColor.translateAlternateColorCodes('&', smallMsg);
        player.sendTitle(msg, smallMsg, 20, 60, 40); // fade in - stale - fade out
    }

}
