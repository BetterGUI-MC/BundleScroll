package me.hsgamer.bettergui.bundlescroll;

import me.hsgamer.bettergui.api.addon.GetPlugin;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.bundlescroll.listener.BundleSelectListener;
import me.hsgamer.bettergui.bundlescroll.listener.PacketBundleSelectListener;
import me.hsgamer.bettergui.bundlescroll.listener.PaperBundleSelectListener;
import me.hsgamer.hscore.expansion.common.Expansion;
import org.bukkit.Bukkit;

public final class BundleScroll implements Expansion, GetPlugin {
    private BundleSelectListener bundleSelectListener;

    @Override
    public boolean onLoad() {
        if (PaperBundleSelectListener.isAvailable()) {
            bundleSelectListener = new PaperBundleSelectListener(getPlugin());
        } else if (Bukkit.getPluginManager().getPlugin("packetevents") != null) {
            bundleSelectListener = new PacketBundleSelectListener();
        }
        return bundleSelectListener != null;
    }

    @Override
    public void onEnable() {
        ButtonBuilder.INSTANCE.register(BundleButton::new, "bundle", "scroll");
        bundleSelectListener.register();
    }

    @Override
    public void onDisable() {
        bundleSelectListener.unregister();
    }
}
