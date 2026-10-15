package me.hsgamer.bettergui.bundlescroll.listener;

import io.papermc.paper.event.inventory.PlayerBundleItemSelectEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperBundleSelectListener implements Listener, BundleSelectListener {
    private final JavaPlugin plugin;

    public PaperBundleSelectListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static boolean isAvailable() {
        try {
            Class.forName("io.papermc.paper.event.inventory.PlayerBundleItemSelectEvent");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onBundleSelect(PlayerBundleItemSelectEvent event) {
        handleSelection(event.getPlayer(), event.getSlot(), event.getSelectedIndex());
    }
}
