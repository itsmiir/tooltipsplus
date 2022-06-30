package com.miir.tooltipsplus.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ShulkerBoxBlock.class)
public class ShulkerRenderOverride {

    @Environment(EnvType.CLIENT)
    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options, CallbackInfo ci) {
        if (!MinecraftClient.getInstance().options.advancedItemTooltips) {
            NbtCompound nbtCompound = stack.getSubNbt("BlockEntityTag");
            if (nbtCompound != null) {
                if (nbtCompound.contains("LootTable", 8)) {
                    tooltip.add(Text.literal("???????"));
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
                                MutableText mutableText = itemStack.getName().copy();
                                MutableText count = (MutableText) Text.of(" x" + itemStack.getCount());
                                count.formatted(Formatting.GRAY);
                                mutableText.append(count);
                                tooltip.add(mutableText);
                            }
                        }
                    }

                    if (j - i > 0) {
                        tooltip.add((Text.translatable("container.shulkerBox.more", j - i)).formatted(Formatting.ITALIC));
                    }
                }
            }
        } else {
            ci.cancel();
        }
    }
}
