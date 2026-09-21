package me.hsgamer.bettergui.bundlescroll;

import me.hsgamer.hscore.minecraft.gui.event.ViewerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BundleScrollEvent implements ViewerEvent {
    private final UUID viewerID;
    private final int selectedIndex;

    public BundleScrollEvent(UUID viewerID, int selectedIndex) {
        this.viewerID = viewerID;
        this.selectedIndex = selectedIndex;
    }

    @Override
    public @NotNull UUID getViewerID() {
        return viewerID;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
}
