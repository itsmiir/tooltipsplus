package com.miir.tooltipsplus.mixin;

import com.miir.TooltipsPlus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
        if ((!stack.getItem().equals(Items.ENCHANTED_BOOK) && stack.hasTag() || stack.getItem().equals(Items.CLOCK)) && MinecraftClient.getInstance().options.advancedItemTooltips) {
            if (stack.getItem().equals(Items.BEE_NEST) || stack.getItem().equals(Items.BEEHIVE)) {
                TooltipsPlus.addBeehiveTooltip(stack, tooltip);
            }
            else if (stack.getItem().equals(Items.CLOCK)) {
                    TooltipsPlus.getClockTime(tooltip);
            }
            else if (stack.getItem() instanceof BlockItem && stack.getTag().contains("BlockEntityTag")) {
                NbtCompound tag = stack.getSubTag("BlockEntityTag");
                if (tag != null) {
                    if (tag.contains("LootTable", 8)) {
                        tooltip.add(new LiteralText("???????"));
                    }

                    if (tag.contains("Items", 9)) {
                        TooltipsPlus.addItemTooltip(tooltip, tag);
                    }
                }
            }
            else if (stack.getItem().equals(Items.WRITABLE_BOOK) || stack.getItem().equals(Items.WRITTEN_BOOK)) {
                    try {
                        MutableText text = (MutableText) Text.of("Book of " + ((NbtList) stack.getTag().get("pages")).size() + " pages");
                        text.formatted(Formatting.GRAY);
                        tooltip.add(text);
                    } catch (NullPointerException ignored) {
                    }
                }
            else if ((!(stack.getItem() instanceof BlockItem) || !((BlockItem) stack.getItem()).getBlock().isIn(BlockTags.SHULKER_BOXES)) && !stack.getItem().equals(Items.PLAYER_HEAD)) {
                    NbtCompound tag = stack.getTag();
                    TooltipsPlus.addTooltip(tooltip, tag);
                }
            }
        }

}