package me.hsgamer.bettergui.bundlescroll;

import io.github.projectunified.minelib.scheduler.common.util.Platform;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public final class BundleItems {
    public static final int DUMMY_ITEM_COUNT = 12;

    private static final Material BUNDLE_MATERIAL = Material.BUNDLE;
    private static final Material DUMMY_MATERIAL = Material.BARRIER;
    private static final NamespacedKey INVISIBLE_MODEL = NamespacedKey.minecraft("air");
    private static final String HIDE_BUNDLE_CONTENTS_FLAG = "HIDE_BUNDLE_CONTENTS";

    private static ItemStack dummyItem;

    private BundleItems() {
    }

    public static boolean isBundle(@Nullable Material material) {
        return material != null && material.name().contains("BUNDLE");
    }

    public static @NotNull ItemStack decorate(@NotNull ItemStack item) {
        ItemStack bundle;
        if (isBundle(item.getType())) {
            bundle = item;
        } else {
            bundle = new ItemStack(BUNDLE_MATERIAL, Math.max(1, item.getAmount()));
            bundle.setItemMeta(item.getItemMeta());
        }

        ItemMeta itemMeta = bundle.getItemMeta();
        if (!(itemMeta instanceof BundleMeta)) {
            return bundle;
        }

        BundleMeta bundleMeta = (BundleMeta) itemMeta;
        List<ItemStack> items = new ArrayList<>(DUMMY_ITEM_COUNT);
        for (int i = 0; i < DUMMY_ITEM_COUNT; i++) {
            items.add(getDummyItem());
        }
        bundleMeta.setItems(items);
        bundle.setItemMeta(bundleMeta);

        hideBundleContents(bundle);
        return bundle;
    }

    @SuppressWarnings("deprecation")
    public static @NotNull ItemStack getDummyItem() {
        if (dummyItem == null) {
            ItemStack item = new ItemStack(DUMMY_MATERIAL);
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(" ");
                itemMeta.setHideTooltip(true);
                itemMeta.setItemModel(INVISIBLE_MODEL);
                item.setItemMeta(itemMeta);
            }
            dummyItem = item;
        }
        return dummyItem.clone();
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void hideBundleContents(ItemStack item) {
        if (Platform.PAPER.isPlatform()) {
            TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay();
            TooltipDisplay current = item.getData(DataComponentTypes.TOOLTIP_DISPLAY);
            if (current != null) {
                builder.hideTooltip(current.hideTooltip());
                builder.hiddenComponents(new LinkedHashSet<>(current.hiddenComponents()));
            }
            builder.addHiddenComponents(DataComponentTypes.BUNDLE_CONTENTS);
            item.setData(DataComponentTypes.TOOLTIP_DISPLAY, builder.build());
            return;
        }

        try {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                itemMeta.addItemFlags(ItemFlag.valueOf(HIDE_BUNDLE_CONTENTS_FLAG));
                item.setItemMeta(itemMeta);
            }
        } catch (Throwable ignored) {
        }
    }
}
