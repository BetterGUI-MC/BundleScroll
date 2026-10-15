package me.hsgamer.bettergui.bundlescroll.listener;

import me.hsgamer.bettergui.bundlescroll.BundleScrollEvent;
import me.hsgamer.hscore.bukkit.gui.BukkitGUIDisplay;
import me.hsgamer.hscore.minecraft.gui.button.DisplayButton;
import me.hsgamer.hscore.minecraft.gui.event.ViewerEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.function.Consumer;

public interface BundleSelectListener {
    void register();

    void unregister();

    default void handleSelection(Player player, int slot, int selectedIndex) {
        Inventory topInventory = player.getOpenInventory().getTopInventory();
        if (!(topInventory.getHolder() instanceof BukkitGUIDisplay)) {
            return;
        }

        DisplayButton displayButton = ((BukkitGUIDisplay) topInventory.getHolder()).getViewedButton(slot).orElse(null);
        if (displayButton == null) {
            return;
        }

        Consumer<ViewerEvent> action = displayButton.getAction();
        if (action == null) {
            return;
        }

        action.accept(new BundleScrollEvent(player.getUniqueId(), selectedIndex));
    }
}
