package com.miir;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.InputUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.resource.Resource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class TooltipsPlus {
//    unsure how i feel about this colors system. felt cute, might refactor later
    public static final MapColor[] COLORS = new MapColor[] {
            MapColor.GOLD,                      MapColor.RED,                 MapColor.MAGENTA,                MapColor.LIME,
            MapColor.TERRACOTTA_YELLOW,         MapColor.IRON_GRAY,           MapColor.WHITE,                  MapColor.DIAMOND_BLUE,
            MapColor.STONE_GRAY,                MapColor.GREEN,               MapColor.PALE_PURPLE,            MapColor.LAPIS_BLUE,
            MapColor.PALE_YELLOW,               MapColor.WHITE,               MapColor.TERRACOTTA_ORANGE,      MapColor.ORANGE,
            MapColor.TERRACOTTA_BLUE,           MapColor.CYAN,                MapColor.TERRACOTTA_GRAY,        MapColor.BROWN,
            MapColor.BRIGHT_RED,                MapColor.DARK_RED,            MapColor.RED,                    MapColor.TERRACOTTA_BLACK,
            MapColor.ORANGE,                    MapColor.OAK_TAN,             MapColor.WHITE_GRAY,             MapColor.BRIGHT_RED,
            MapColor.DULL_RED,                  MapColor.ORANGE,              MapColor.LIME,                   MapColor.OAK_TAN,
            MapColor.BRIGHT_RED,                MapColor.PALE_PURPLE,         MapColor.YELLOW,                 MapColor.LAPIS_BLUE,
            MapColor.DIAMOND_BLUE,              MapColor.BRIGHT_RED
    };
    private static final Enchantment[] ENCHANTMENTS = new Enchantment[] {
            Enchantments.MENDING,               Enchantments.VANISHING_CURSE, Enchantments.LURE,               Enchantments.LUCK_OF_THE_SEA,
            Enchantments.FORTUNE,               Enchantments.UNBREAKING,      Enchantments.SILK_TOUCH,         Enchantments.EFFICIENCY,
            Enchantments.PROTECTION,            Enchantments.THORNS,          Enchantments.AQUA_AFFINITY,      Enchantments.RESPIRATION,
            Enchantments.PROJECTILE_PROTECTION, Enchantments.FEATHER_FALLING, Enchantments.BLAST_PROTECTION,   Enchantments.FIRE_PROTECTION,
            Enchantments.DEPTH_STRIDER,         Enchantments.FROST_WALKER,    Enchantments.BINDING_CURSE,      Enchantments.SOUL_SPEED,
            Enchantments.SHARPNESS,             Enchantments.SMITE,           Enchantments.BANE_OF_ARTHROPODS, Enchantments.KNOCKBACK,
            Enchantments.FIRE_ASPECT,           Enchantments.LOOTING,         Enchantments.SWEEPING,           Enchantments.POWER,
            Enchantments.PUNCH,                 Enchantments.FLAME,           Enchantments.INFINITY,           Enchantments.LOYALTY,
            Enchantments.IMPALING,              Enchantments.RIPTIDE,         Enchantments.CHANNELING,         Enchantments.MULTISHOT,
            Enchantments.QUICK_CHARGE,          Enchantments.PIERCING
    };
    public static final Object2ObjectArrayMap<Enchantment, MapColor> ENCH_COLORS = new Object2ObjectArrayMap<>(ENCHANTMENTS, COLORS);
    private static final Object2ObjectArrayMap<Identifier, Integer> COLOR_CACHE = new Object2ObjectArrayMap<>();

    public static int getColor(ItemStack stack) {
        Item item = stack.getItem();
        int color = 0;
        if (stack.getRarity() != Rarity.COMMON) return stack.getRarity().formatting.getColorValue();
        if (item instanceof DyeableArmorItem) return ((DyeableItem) item).getColor(stack);
        if (item instanceof SpawnEggItem) return ((SpawnEggItem) item).getColor(0);
        if (item instanceof PotionItem || item instanceof TippedArrowItem) return getPotionColor(stack);
        if (item instanceof BlockItem && !(item instanceof BannerItem)) color = getMapColor(stack).color;
        if (color == 0) color = inferColor(stack);
        if (color == 0) color = getTextureColor(stack);
        if (color == 0) return Rarity.COMMON.formatting.getColorValue();
        return color;
    }

    private static int getPotionColor(ItemStack stack) {
        assert stack.getItem() instanceof PotionItem || stack.getItem() instanceof TippedArrowItem;
        return PotionUtil.getColor(stack);
    }

    private static int inferColor(ItemStack stack) {
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
        else if (name.contains("spruce")) return Blocks.SPRUCE_PLANKS.getDefaultMapColor().color;
        else if (name.contains(":warped_")) return Blocks.WARPED_PLANKS.getDefaultMapColor().color;
        else if (name.contains(":crimson_")) return Blocks.CRIMSON_PLANKS.getDefaultMapColor().color;
        else return 0;
    }

    private static int getTextureColor(ItemStack stack) {
//        this feels like a very heavy function but it's Good for Compat™
        Resource resource;
        if (MinecraftClient.getInstance().world != null) {
            Identifier stackId = Registry.ITEM.getId(stack.getItem());
//            that's right, we're caching babe
            if (COLOR_CACHE.get(stackId) == null) {
                Identifier texID = new Identifier(stackId.getNamespace(), "textures/item/"+stackId.getPath()+".png");
                try {
                resource = MinecraftClient.getInstance().getResourceManager().getResource(texID);
                } catch(Exception e) {
                    try {
                        texID = new Identifier(stackId.getNamespace(), "textures/block/"+stackId.getPath()+".png");
                        resource = MinecraftClient.getInstance().getResourceManager().getResource(texID);
                    } catch (Exception e1) {
                        return 0;
                    }
                }
                Resource textureResource = resource;
                NativeImage img;
                try {
                    img = NativeImage.read(textureResource.getInputStream());
                } catch (IOException e) {
                    COLOR_CACHE.put(stackId, 0);
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
                if (((color >> 16) & 0xFF) + ((color >> 8) & 0xFF) + (color & 0xFF) < 44+44+44) color = 0x444444;
                COLOR_CACHE.put(stackId, color);
                return color;
            } else {
                return COLOR_CACHE.get(stackId);
            }
        }
        return 0;
    }

    private static MapColor getMapColor(ItemStack stack) {
        MapColor color = ((BlockItem) stack.getItem()).getBlock().getDefaultMapColor();
        if (color.equals(MapColor.BLACK)) { // black is hard to see
            return MapColor.GRAY;
        }
        return color;
    }

    public static Text getClockTime() {
            if (MinecraftClient.getInstance().world != null) {
                long rawTime = MinecraftClient.getInstance().world.getTimeOfDay();
                int time = (int) (rawTime + 6000 > 23999 ? rawTime - 18000 : rawTime + 6000);
//                this is the one time that integer division has been helpful
                int hr = (time / 1000) % 24;
                float min = ((time % 1000) / 1000f) * 60;

                String paddingHr = hr < 10 ? "0" : "";
                String paddingMin = min < 10 ? "0" : "";
                if (hr < 0) hr += 23;
                if (min < 0) min += 60;
//                    mmm syntactic sugar
                MutableText text = (MutableText) Text.of(paddingHr + hr + ":" + paddingMin + (Math.round(min)));
                text.formatted(Formatting.GOLD);
                return text;
            }
            return Text.of("");
    }

    public static Text addBeehiveTooltip(ItemStack stack) {
        MutableText text = (MutableText) Text.of("Contains " + ((NbtList) ((NbtCompound) stack.getNbt().get("BlockEntityTag")).get("Bees")).size() + " bees");
        text.formatted(Formatting.GRAY);
        return text;
    }

    public static String romanNumeral(int num) {
        switch (num) {
            case 0:
                return "Nulla";
//            roman numerals don't have 0, but apparently they used that
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            case 50:
                return "L";
            case 100:
                return "C";
            case 500:
                return "D";
            case 1000:
                return "M";
            default:
                if (num > 3999) {
                    return ((Integer) num).toString();
                }
                return (
                          THOUS[num / 1000]
                        + HUNDS[num / 100 % 10]
                        + TENS[num / 10 % 10]
                        + ONES[num % 10]
                        );
        }
    }

    public static void addTooltip(List<Text> tooltip, NbtElement tag) {
        if (tag instanceof NbtList) {
            for (NbtElement element :
                    (NbtList) tag) {
                switch (element.getType()) {
                    case 10, 9 -> addTooltip(tooltip, element);
//                        wooooo recursion
                    default -> {
                        String str = "- " + element;
                        MutableText text = (MutableText) Text.of(str);
                        text.formatted(Formatting.GRAY);
                        tooltip.add(text);
                    }
                }
            }
        } else {
            for (String key :
                    ((NbtCompound) tag).getKeys()) {
                switch (((NbtCompound) tag).getType(key)) {
                    case 10:
                    case 9:
                        if (!key.equals("Enchantments") && !key.equals("display")) {
                            tooltip.add(Text.of(key + ":"));
                            addTooltip(tooltip, ((NbtCompound) tag).get(key));
                        }
                        break;
                    default:
                        String str = key + ": " + ((NbtCompound) tag).get(key).toString();
                        if (!key.equals("Damage")) {
                            MutableText text = (MutableText)Text.EMPTY;
                            text.append(str).formatted(Formatting.GRAY);
                            tooltip.add(text);
                        }
                        break;
                }

            }
        }
    }

    public static void addItemTooltip(List<Text> tooltip, NbtCompound tag) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(27, ItemStack.EMPTY);
        Inventories.readNbt(tag, defaultedList);
        int i = 0;
        int j = 0;
        int k = 5;
        boolean hasPots = false;
        boolean hasDiscs = false;
        Object2ObjectArrayMap<Item, Integer> condensedInv = new Object2ObjectArrayMap<>();
        Object2ObjectArrayMap<Potion, Integer> pots = new Object2ObjectArrayMap<>();
        Object2ObjectArrayMap<Potion, Integer> splashPots = new Object2ObjectArrayMap<>();
        Object2ObjectArrayMap<Potion, Integer> lingPots = new Object2ObjectArrayMap<>();
        Object2ObjectArrayMap<SoundEvent, Integer> discs = new Object2ObjectArrayMap<>();
        Formatting countFormatting = Formatting.GRAY;


        for (ItemStack stack1 :
                defaultedList) {
            Item item = stack1.getItem();
            if (item instanceof PotionItem) {
                hasPots = true;
                if (item instanceof SplashPotionItem) {
                    Potion potion = PotionUtil.getPotion(stack1);
                    if (splashPots.containsKey(potion)) {
                        splashPots.put(potion, (stack1.getCount() + splashPots.get(potion)));
                    } else {
                        splashPots.put(potion, stack1.getCount());
                    }
                } else if (item instanceof LingeringPotionItem) {
                    Potion potion = PotionUtil.getPotion(stack1);
                    if (lingPots.containsKey(potion)) {
                        lingPots.put(potion, (stack1.getCount() + lingPots.get(potion)));
                    } else {
                        lingPots.put(potion, stack1.getCount());
                    }
                } else {
                    Potion potion = PotionUtil.getPotion(stack1);
                    if (pots.containsKey(potion)) {
                        pots.put(potion, (stack1.getCount() + pots.get(potion)));
                    } else {
                        pots.put(potion, stack1.getCount());
                    }
                }

            }
            else if (item instanceof MusicDiscItem) {
                hasDiscs = true;
                SoundEvent music = ((MusicDiscItem) item).getSound();
                if (discs.containsKey(music)) {
                    int count = discs.get(music);
                    discs.put(music, count + stack1.getCount());
                }
                else {
                    discs.put(music, stack1.getCount());
                }
            }
            else {
                if (condensedInv.containsKey(stack1.getItem())) {
                    int count = condensedInv.get(stack1.getItem());
                    condensedInv.put(stack1.getItem(), count + stack1.getCount());
                } else {
                    condensedInv.put(stack1.getItem(), stack1.getCount());
                }
            }
        }

        if (hasPots) {
            for (Potion potion :
                    pots.keySet()) {
                Item pot = Items.POTION;
                ItemStack potStack = new ItemStack(pot);
                PotionUtil.setPotion(potStack, potion);
                j++;
                if (i <= k) {
                    ++i;
                    MutableText mutableText = potStack.getTooltip(null, TooltipContext.Default.NORMAL).get(0).copy();
                    mutableText.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(getColor(potStack))));
                    if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 346)) {
                        countFormatting = Formatting.DARK_GRAY;
                        String id = Registry.ITEM.getId(pot).toString();
                        mutableText = (MutableText) Text.of("[");
                        MutableText name = (MutableText) Text.of(id);
                        Formatting formatting = i % 2 == 0 ? Formatting.WHITE : Formatting.GRAY;
                        mutableText.append(name).append("]");
                        mutableText.formatted(formatting);
                    }
                    MutableText count = (MutableText) Text.of(" x" + (pots.get(potion)));
                    count.formatted(countFormatting);
                    mutableText.append(count);
                    tooltip.add(mutableText);
                }
            }
            for (Potion potion :
                    splashPots.keySet()) {
                Item pot = Items.SPLASH_POTION;
                ItemStack potStack = new ItemStack(pot);
                PotionUtil.setPotion(potStack, potion);
                j++;
                if (i <= k) {
                    ++i;
                    MutableText mutableText = potStack.getName().copy();
                    mutableText.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(getColor(potStack))));
                    if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 346)) {
                        countFormatting = Formatting.DARK_GRAY;
                        String id = Registry.ITEM.getId(pot).toString();
                        mutableText = (MutableText) Text.of("[");
                        MutableText name = (MutableText) Text.of(id);
                        Formatting formatting = i % 2 == 0 ? Formatting.WHITE : Formatting.GRAY;
                        mutableText.append(name).append("]");
                        mutableText.formatted(formatting);
                    }
                    MutableText count = (MutableText) Text.of(" x" + (splashPots.get(potion)));
                    count.formatted(countFormatting);
                    mutableText.append(count);
                    tooltip.add(mutableText);
                }
            }
            for (Potion potion :
                    lingPots.keySet()) {
                Item pot = Items.LINGERING_POTION;
                ItemStack potStack = new ItemStack(pot);
                PotionUtil.setPotion(potStack, potion);
                j++;
                if (i <= k) {
                    ++i;
                    MutableText mutableText = potStack.getName().copy();
                    mutableText.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(getColor(potStack))));
                    if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 346)) {
                        countFormatting = Formatting.DARK_GRAY;
                        String id = Registry.ITEM.getId(pot).toString();
                        mutableText = (MutableText) Text.of("[");
                        MutableText name = (MutableText) Text.of(id);
                        Formatting formatting = i % 2 == 0 ? Formatting.WHITE : Formatting.GRAY;
                        mutableText.append(name).append("]");
                        mutableText.formatted(formatting);
                    }
                    MutableText count = (MutableText) Text.of(" x" + (lingPots.get(potion)));
                    count.formatted(countFormatting);
                    mutableText.append(count);
                    tooltip.add(mutableText);
                }
            }
        }
        if (hasDiscs) {
            for (SoundEvent music:
                 discs.keySet()) {
                ++j;
                if (i <= k) {
                    ++i;
                    MusicDiscItem disc = MusicDiscItem.bySound(music);
                    MutableText mutableText = disc.getDescription().copy();
                    mutableText.formatted(disc.getRarity(disc.getDefaultStack()).formatting);
                    if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 346)) {
                        countFormatting = Formatting.DARK_GRAY;
                        String id = Registry.ITEM.getId(disc).toString();
                        mutableText = (MutableText) Text.of("[");
                        MutableText name = (MutableText) Text.of(id);
                        Formatting formatting = i % 2 == 0 ? Formatting.WHITE : Formatting.GRAY;
                        mutableText.append(name).append("]");
                        mutableText.formatted(formatting);
                    }
                    MutableText count = (MutableText) Text.of(" x" + (discs.get(music)));
                    count.formatted(countFormatting);
                    mutableText.append(count);
                    tooltip.add(mutableText);
                }
            }
        }
        for (Item item : condensedInv.keySet()) {
            if (!item.getDefaultStack().isEmpty()) {
                ++j;
                if (i <= k || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 342)) {
                    ++i;
                    MutableText mutableText = (MutableText) item.getDefaultStack().getTooltip(null, TooltipContext.Default.NORMAL).get(0).copy();
                    if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 346)) {
                        countFormatting = Formatting.DARK_GRAY;
                        String id = Registry.ITEM.getId(item).toString();
                        mutableText = (MutableText) Text.of("[");
                        MutableText name = (MutableText) Text.of(id);
                        Formatting formatting = i % 2 == 0 ? Formatting.WHITE : Formatting.GRAY;
                        mutableText.append(name).append("]");
                        mutableText.formatted(formatting);
                    }
                    else if (item instanceof BlockItem && item.getRarity(item.getDefaultStack()).equals(Rarity.COMMON)) {
                        mutableText.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(getColor(item.getDefaultStack()))));
                    }
                    MutableText count = (MutableText) Text.of(" x" + (condensedInv.get(item)));
                    count.formatted(countFormatting);
                    mutableText.append(count);
                    tooltip.add(mutableText);
                }
            }
        }
        if (j - i > 0) {
            tooltip.add(((MutableText) Text.of("LAlt to show " + (j - i) + " more...")).formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        }
    }

    private static final String[] THOUS = new String[] {
            "", "M", "MM", "MMM"
    };
    private static final String[] HUNDS = new String[] {
            "", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"
    };
    private static final String[] TENS = new String[] {
            "", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"
    };
    private static final String[] ONES = new String[] {
            "", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"
    };

}
