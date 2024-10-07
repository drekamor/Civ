package net.civmc.kitpvp;

import net.civmc.kitpvp.data.Kit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class KitApplier {

    public static void applyKit(Kit kit, Player player) {
        player.sendMessage(Component.text("Applied kit " + kit.name(), NamedTextColor.GREEN));

        player.clearActivePotionEffects();

        player.setFoodLevel(20);
        player.setSaturation(0);
        player.setExhaustion(0);

        player.setHealth(20);

        player.setFireTicks(0);

        player.getInventory().clear();
        player.getInventory().setContents(kit.items());
    }
}