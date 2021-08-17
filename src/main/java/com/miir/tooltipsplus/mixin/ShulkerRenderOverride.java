package com.miir.tooltipsplus.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ShulkerBoxBlock.class)
public class ShulkerRenderOverride {

    @Environment(EnvType.CLIENT)
    @Overwrite
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        if (!MinecraftClient.getInstance().options.advancedItemTooltips) {
            NbtCompound nbtCompound = stack.getSubTag("BlockEntityTag");
            if (nbtCompound != null) {
                if (nbtCompound.contains("LootTable", 8)) {
                    tooltip.add(new LiteralText("???????"));
                }

                if (nbtCompound.contains("Items", 9)) {
                    DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(27, ItemStack.EMPTY);
                    Inventories.readNbt(nbtCompound, defaultedList);
                    int i = 0;
                    int j = 0;

                    for (ItemStack itemStack : defaultedList) {
                        if (!itemStack.isEmpty()) {
                            ++j;
                            if (i <= 4) {
                                ++i;
                                MutableText mutableText = itemStack.getName().shallowCopy();
                                MutableText count = (MutableText) Text.of(" x" + itemStack.getCount());
                                count.formatted(Formatting.GRAY);
                                mutableText.append(count);
                                tooltip.add(mutableText);
                            }
                        }
                    }

                    if (j - i > 0) {
                        tooltip.add((new TranslatableText("container.shulkerBox.more", j - i)).formatted(Formatting.ITALIC));
                    }
                }
            }

        }
    }
}
