package com.miir.tooltipsplus.mixin;

import com.miir.TooltipsPlus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
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
    @Inject(at = @At("HEAD"), method = "appendTooltip")
    private void mixin(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        Item item = stack.getItem();
        if (stack.hasNbt()) {
            if (item instanceof BlockItem) {
                if (((BlockItem) stack.getItem()).getBlock() instanceof BeehiveBlock) {
                    tooltip.add(TooltipsPlus.addBeehiveTooltip(stack));
                }
                if (stack.getNbt().contains("BlockEntityTag")) {
                    NbtCompound tag = stack.getSubNbt("BlockEntityTag");
                    if (tag != null) {
                        if (tag.contains("LootTable", NbtElement.STRING_TYPE)) {
                            tooltip.add(Text.literal("???????"));
                        }

                        if (tag.contains("Items", NbtElement.LIST_TYPE)) {
                            TooltipsPlus.addItemTooltip(tooltip, tag);
                        }
                    }
                } else {
                    NbtCompound tag = stack.getNbt();
                    if (tag != null && MinecraftClient.getInstance().options.advancedItemTooltips)
                        TooltipsPlus.addTooltip(tooltip, tag);
                }
            } else if (item.equals(Items.WRITABLE_BOOK) || item.equals(Items.WRITTEN_BOOK)) {
                try {
                    int pages = ((NbtList) stack.getNbt().get("pages")).size();
                    MutableText text = (MutableText) Text.of("Book of " + pages + (pages == 1 ? " page" : " pages"));
                    text.formatted(Formatting.GRAY);
                    tooltip.add(text);
                } catch (NullPointerException ignored){}
            } else {
                NbtCompound tag = stack.getNbt();
                if (tag != null && MinecraftClient.getInstance().options.advancedItemTooltips)
                    TooltipsPlus.addTooltip(tooltip, tag);
            }
        }
        if (item.equals(Items.CLOCK)) {
            tooltip.add(TooltipsPlus.getClockTime());
        }
    }
}