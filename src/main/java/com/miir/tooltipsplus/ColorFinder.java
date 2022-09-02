package com.miir.tooltipsplus;

import com.miir.TooltipsPlus;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.util.Optional;
// don't know why i put abstract first. i'm evil like that i guess
abstract public class ColorFinder {

    public static MapColor getMapColor(ItemStack stack) {
        MapColor color = ((BlockItem) stack.getItem()).getBlock().getDefaultMapColor();
        if (color.equals(MapColor.BLACK)) { // black is hard to see
            return MapColor.GRAY;
        }
        return color;
    }
    public static int getTextureColor(ItemStack stack) {
//        this feels like a very heavy function, but it's Good for Compat™
        if (MinecraftClient.getInstance().world != null) {
            Identifier stackId = Registry.ITEM.getId(stack.getItem());
//            that's right, we're caching babe
            if (TooltipsPlus.COLOR_CACHE.get(stackId) == null) {
                Identifier texID = new Identifier(stackId.getNamespace(), "textures/item/"+stackId.getPath()+".png");
                Optional<Resource> optionalResource = MinecraftClient.getInstance().getResourceManager().getResource(texID);
                if (optionalResource.isEmpty()) {
                    texID = new Identifier(stackId.getNamespace(), "textures/block/"+stackId.getPath()+".png");
                    optionalResource = MinecraftClient.getInstance().getResourceManager().getResource(texID);
                }
                if (optionalResource.isPresent()) {
                    Resource textureResource = optionalResource.get();
                    NativeImage img;
                    try {
                        img = NativeImage.read(textureResource.getInputStream());
                    } catch (IOException e) {
                        TooltipsPlus.COLOR_CACHE.put(stackId, 0);
                        return 0;
                    }
                    int h = img.getHeight();
                    int w = img.getWidth();
                    int r = 0;
                    int g = 0;
                    int b = 0;
                    int n = 0;
                    for (int x = 0; x < w; x++) {
                        for (int y = 0; y < h; y++) {
                            int c = img.getColor(x, y);
                            if (c >> 24 == 0) continue;

                            r += c & 0xFF;
                            g += (c & 0xFF00) >> 8;
                            b += (c & 0xFF0000) >> 16;
                            n++;
                        }
                    }
                    int color = Math.round(b / ((float) n)) | (Math.round(g / ((float) n)) << 8) | (Math.round(r / ((float) n)) << 16);
                    color = ensureReadability(color);
                    TooltipsPlus.COLOR_CACHE.put(stackId, color);
                    return color;
                } else {
                    return 0;
                }
            } else {
                return TooltipsPlus.COLOR_CACHE.get(stackId);
            }
        }
        return 0;
    }

    public static int getPotionColor(ItemStack stack) {
        assert stack.getItem() instanceof PotionItem || stack.getItem() instanceof TippedArrowItem;
        return TooltipsPlus.CONFIG.potionColors ? PotionUtil.getColor(stack) : MapColor.WHITE.color;
    }

    public static int inferColor(ItemStack stack) {
//        this is a bit suspicious and pretty bad for compat tbh
//        also wtf is regex
        String name = Registry.ITEM.getId(stack.getItem()).toString();
        if      (name.contains("black_") || name.contains(":gray_")) return MapColor.GRAY.color;
        else if (name.contains("light_gray_")) return MapColor.LIGHT_GRAY.color;
        else if (name.contains("white_")) return MapColor.WHITE.color;
        else if (name.contains("pink_")) return MapColor.PINK.color;
        else if (name.contains(":red_")) return MapColor.RED.color;
        else if (name.contains("orange")) return MapColor.ORANGE.color;
        else if (name.contains("yellow")) return MapColor.YELLOW.color;
        else if (name.contains(":lime")) return MapColor.LIME.color;
        else if (name.contains(":green")) return MapColor.GREEN.color;
        else if (name.contains(":cyan")) return MapColor.CYAN.color;
        else if (name.contains("light_blue")) return MapColor.LIGHT_BLUE.color;
        else if (name.contains(":blue")) return MapColor.BLUE.color;
        else if (name.contains("purple")) return MapColor.PURPLE.color;
        else if (name.contains("magenta")) return MapColor.MAGENTA.color;
        else if (name.contains(":brown")) return MapColor.BROWN.color;
        else if (name.contains("emerald")) return Blocks.EMERALD_BLOCK.getDefaultMapColor().color;
        else if (name.contains("redstone")) return Blocks.REDSTONE_BLOCK.getDefaultMapColor().color;
        else if (name.contains(":oak")) return Blocks.OAK_PLANKS.getDefaultMapColor().color;
        else if (name.contains("birch")) return Blocks.BIRCH_PLANKS.getDefaultMapColor().color;
        else if (name.contains("dark_oak")) return Blocks.DARK_OAK_PLANKS.getDefaultMapColor().color;
        else if (name.contains("jungle")) return Blocks.JUNGLE_PLANKS.getDefaultMapColor().color;
        else if (name.contains("acacia")) return Blocks.ACACIA_PLANKS.getDefaultMapColor().color;
        else if (name.contains("mangrove")) return Blocks.MANGROVE_PLANKS.getDefaultMapColor().color;
        else if (name.contains("spruce")) return Blocks.SPRUCE_PLANKS.getDefaultMapColor().color;
        else if (name.contains(":warped_")) return Blocks.WARPED_PLANKS.getDefaultMapColor().color;
        else if (name.contains(":crimson_")) return Blocks.CRIMSON_PLANKS.getDefaultMapColor().color;
        else return 0;
    }

    public static int getColor(ItemStack stack) {
        int color = 0;
        if (stack.getRarity() != Rarity.COMMON) return stack.getRarity().formatting.getColorValue();
        if (TooltipsPlus.CONFIG.colors) {
            Item item = stack.getItem();
            if ((item instanceof PotionItem || item instanceof TippedArrowItem) && TooltipsPlus.CONFIG.potionColors) return getPotionColor(stack);
            if (item instanceof DyeableArmorItem) return ((DyeableItem) item).getColor(stack);
            if (item instanceof SpawnEggItem) return ((SpawnEggItem) item).getColor(0);
            if (item instanceof BlockItem && !(item instanceof BannerItem)) color = getMapColor(stack).color;
            if (color == 0) color = inferColor(stack);
            if (color == 0) color = getTextureColor(stack);
        }
        if (color == 0) return Rarity.COMMON.formatting.getColorValue();
        return ensureReadability(color);
    }

    public static int ensureReadability(int i) {
        int r = (i & 0xFF0000) >> 16;
        int g = (i & 0x00FF00) >>  8;
        int b = (i & 0x0000FF)/*:)*/;
        while (r + b + g < 44+44+44) {
            r *= 1.1;
            b *= 1.1;
            g *= 1.1;
        }
        return (r << 16 | g << 8 | b);
    }
}
