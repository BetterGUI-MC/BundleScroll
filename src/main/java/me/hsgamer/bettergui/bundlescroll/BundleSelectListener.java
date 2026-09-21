package me.hsgamer.bettergui.bundlescroll;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSelectBundleItem;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.hscore.bukkit.gui.BukkitGUIDisplay;
import me.hsgamer.hscore.minecraft.gui.button.DisplayButton;
import me.hsgamer.hscore.minecraft.gui.event.ViewerEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.function.Consumer;

public class BundleSelectListener extends PacketListenerAbstract {
    public BundleSelectListener() {
        super(PacketListenerPriority.NORMAL);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.SELECT_BUNDLE_ITEM) {
            return;
        }

        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        WrapperPlayClientSelectBundleItem packet = new WrapperPlayClientSelectBundleItem(event);
        int slot = packet.getSlotId();
        int selectedIndex = packet.getSelectedItemIndex();

        SchedulerUtil.entity(player).run(() -> handleSelection(player, slot, selectedIndex));
    }

    private void handleSelection(Player player, int slot, int selectedIndex) {
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
