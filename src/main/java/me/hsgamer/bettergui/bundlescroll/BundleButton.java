package me.hsgamer.bettergui.bundlescroll;

import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.builder.ItemModifierBuilder;
import me.hsgamer.bettergui.button.ActionButton;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.gui.object.BukkitItem;
import me.hsgamer.hscore.bukkit.item.BukkitItemBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.minecraft.gui.button.Button;
import me.hsgamer.hscore.minecraft.gui.button.DisplayButton;
import me.hsgamer.hscore.minecraft.gui.button.impl.SimpleButton;
import me.hsgamer.hscore.minecraft.gui.event.ClickEvent;
import me.hsgamer.hscore.minecraft.gui.event.ViewerEvent;
import me.hsgamer.hscore.minecraft.item.ItemBuilder;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class BundleButton extends ActionButton<Button> {
    private final ActionApplier nextItemAction;
    private final ActionApplier previousItemAction;
    private final Map<UUID, Integer> lastSelectedIndices = new HashMap<>();

    public BundleButton(ButtonBuilder.Input input) {
        super(input);
        Map<String, Object> keys = new CaseInsensitiveStringMap<>(input.options);
        this.nextItemAction = MapUtils.getOptional(keys, "next-action", "on-next-action", "next-command", "on-next-command", "down-action", "on-down-action", "down-command", "on-down-command")
                .map(value -> new ActionApplier(menu, value))
                .orElse(ActionApplier.EMPTY);
        this.previousItemAction = MapUtils.getOptional(keys, "previous-action", "on-previous-action", "previous-command", "on-previous-command", "up-action", "on-up-action", "up-command", "on-up-command")
                .map(value -> new ActionApplier(menu, value))
                .orElse(ActionApplier.EMPTY);
    }

    static @Nullable Direction decodeStep(int selectedIndex, @Nullable Integer previousIndex) {
        if (previousIndex == null) {
            if (selectedIndex == 0) {
                return Direction.DOWN;
            }
            if (selectedIndex == BundleItems.DUMMY_ITEM_COUNT - 1) {
                return Direction.UP;
            }
            return null;
        }

        int delta = Math.floorMod(selectedIndex - previousIndex, BundleItems.DUMMY_ITEM_COUNT);
        if (delta == 1) {
            return Direction.DOWN;
        }
        if (delta == BundleItems.DUMMY_ITEM_COUNT - 1) {
            return Direction.UP;
        }
        return null;
    }

    @Override
    protected Function<Consumer<ClickEvent>, Button> getButtonFunction(Map<String, Object> section) {
        ItemBuilder<ItemStack> itemBuilder = StringReplacerApplier.apply(new BukkitItemBuilder(), this);
        ItemModifierBuilder.INSTANCE.build(section).forEach(itemBuilder::addItemModifier);
        return clickConsumer -> new SimpleButton(uuid -> new BukkitItem(BundleItems.decorate(itemBuilder.build(uuid))), clickConsumer);
    }

    @Override
    public DisplayButton display(@NonNull UUID uuid) {
        DisplayButton displayButton = super.display(uuid);
        if (displayButton == null) {
            return null;
        }

        Consumer<ViewerEvent> clickAction = displayButton.getAction();
        displayButton.setAction(event -> {
            if (event instanceof BundleScrollEvent) {
                handleBundleScroll((BundleScrollEvent) event);
            } else if (clickAction != null) {
                clickAction.accept(event);
            }
        });
        return displayButton;
    }

    @Override
    public void refresh(UUID uuid) {
        lastSelectedIndices.remove(uuid);
    }

    @Override
    public void stop() {
        lastSelectedIndices.clear();
        super.stop();
    }

    private void handleBundleScroll(BundleScrollEvent event) {
        Direction direction = decode(event);
        if (direction == null) {
            return;
        }

        ActionApplier actionApplier = direction == Direction.DOWN ? nextItemAction : previousItemAction;
        if (actionApplier.isEmpty()) {
            return;
        }

        BatchRunnable batchRunnable = new BatchRunnable();
        batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> actionApplier.accept(event.getViewerID(), process));
        SchedulerUtil.async().run(batchRunnable);
    }

    private @Nullable Direction decode(BundleScrollEvent event) {
        int selectedIndex = event.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= BundleItems.DUMMY_ITEM_COUNT) {
            lastSelectedIndices.remove(event.getViewerID());
            return null;
        }

        Integer previousIndex = lastSelectedIndices.put(event.getViewerID(), selectedIndex);
        return decodeStep(selectedIndex, previousIndex);
    }

    public enum Direction {
        UP,
        DOWN
    }
}
