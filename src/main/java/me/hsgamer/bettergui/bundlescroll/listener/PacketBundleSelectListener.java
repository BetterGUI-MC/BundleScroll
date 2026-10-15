package me.hsgamer.bettergui.bundlescroll.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSelectBundleItem;
import me.hsgamer.bettergui.util.SchedulerUtil;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class PacketBundleSelectListener implements BundleSelectListener {
    private PacketListenerCommon packetListener;

    @Override
    public void register() {
        packetListener = PacketEvents.getAPI().getEventManager().registerListener(new PacketListenerAbstract(PacketListenerPriority.NORMAL) {
            @Override
            public void onPacketReceive(@NonNull PacketReceiveEvent event) {
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
        });
    }

    @Override
    public void unregister() {
        if (packetListener != null) {
            PacketEvents.getAPI().getEventManager().unregisterListener(packetListener);
        }
    }
}
