package com.miir.tooltipsplus.mixin;

import com.miir.TooltipsPlus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.PlayerSkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(Item.class)
public class RenderNBTMixin {
    @Inject(
            at = @At("HEAD"),
            method = "appendTooltip"
    )
    private void mixin(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if ((!stack.getItem().equals(Items.ENCHANTED_BOOK) && stack.hasNbt() || stack.getItem().equals(Items.CLOCK)) && MinecraftClient.getInstance().options.advancedItemTooltips) {
            if (stack.getItem().equals(Items.CLOCK)) {
                tooltip.add(TooltipsPlus.getClockTime());
            } else if (stack.getItem() instanceof BlockItem) {
                if (((BlockItem) stack.getItem()).getBlock() instanceof BeehiveBlock) {
                    tooltip.add(TooltipsPlus.addBeehiveTooltip(stack));
                }
                if (stack.getNbt().contains("BlockEntityTag")) {
                    NbtCompound tag = stack.getSubNbt("BlockEntityTag");
                    if (tag != null) {
                        if (tag.contains("LootTable", 8)) {
                            tooltip.add(Text.literal("???????"));
                        }

                        if (tag.contains("Items", 9)) {
                            TooltipsPlus.addItemTooltip(tooltip, tag);
                        }
                    }
                } else {
                    NbtCompound tag = stack.getNbt();
                    if (tag != null) TooltipsPlus.addTooltip(tooltip, tag);
                }
            }
        } else if (stack.getItem().equals(Items.WRITABLE_BOOK) || stack.getItem().equals(Items.WRITTEN_BOOK)) {
            try {
                MutableText text = (MutableText) Text.of("Book of " + ((NbtList) stack.getNbt().get("pages")).size() + " pages");
                text.formatted(Formatting.GRAY);
                tooltip.add(text);
            } catch (NullPointerException ignored) {
            }
        } else {
            NbtCompound tag = stack.getNbt();
            if (tag != null) TooltipsPlus.addTooltip(tooltip, tag);
        }
    }
}