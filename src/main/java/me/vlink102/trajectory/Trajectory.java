package me.vlink102.trajectory;

import me.vlink102.trajectory.mojang.MojangTrajectory;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Trajectory extends JavaPlugin implements Listener {
    private final MojangTrajectory mojangTrajectory = new MojangTrajectory();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Enabled trajectory calculations.");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getMaterial() == Material.BOW) {
                mojangTrajectory.find(event.getPlayer(), Objects.requireNonNull(event.getItem()));
            }
        }
    }
}
