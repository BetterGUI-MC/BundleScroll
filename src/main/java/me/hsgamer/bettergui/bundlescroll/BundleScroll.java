package me.hsgamer.bettergui.bundlescroll;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.expansion.common.Expansion;

public final class BundleScroll implements Expansion {
    private PacketListenerCommon bundleSelectListener;

    @Override
    public void onEnable() {
        ButtonBuilder.INSTANCE.register(BundleButton::new, "bundle", "scroll");
        bundleSelectListener = PacketEvents.getAPI().getEventManager().registerListener(new BundleSelectListener());
    }

    @Override
    public void onDisable() {
        if (bundleSelectListener != null) {
            PacketEvents.getAPI().getEventManager().unregisterListener(bundleSelectListener);
            bundleSelectListener = null;
        }
    }
}
