package com.miir;

import com.sun.imageio.plugins.png.PNGImageReader;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.PngFile;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class TooltipsPlus implements ModInitializer {
//    unsure how i feel about this colors system. felt cute, might refactor later ~em
    public static final Object2ObjectArrayMap<Enchantment, MapColor> ENCH_COLORS = new Object2ObjectArrayMap<>(
            new Enchantment[] {
                    Enchantments.MENDING, Enchantments.VANISHING_CURSE, Enchantments.LURE, Enchantments.LUCK_OF_THE_SEA,
                    Enchantments.FORTUNE, Enchantments.UNBREAKING, Enchantments.SILK_TOUCH, Enchantments.EFFICIENCY,
                    Enchantments.PROTECTION, Enchantments.THORNS, Enchantments.AQUA_AFFINITY, Enchantments.RESPIRATION,
                    Enchantments.PROJECTILE_PROTECTION, Enchantments.FEATHER_FALLING, Enchantments.BLAST_PROTECTION, Enchantments.FIRE_PROTECTION,
                    Enchantments.DEPTH_STRIDER, Enchantments.FROST_WALKER, Enchantments.BINDING_CURSE, Enchantments.SOUL_SPEED,
                    Enchantments.SHARPNESS, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS, Enchantments.KNOCKBACK,
                    Enchantments.FIRE_ASPECT, Enchantments.LOOTING, Enchantments.SWEEPING, Enchantments.POWER,
                    Enchantments.PUNCH, Enchantments.FLAME, Enchantments.INFINITY, Enchantments.LOYALTY,
                    Enchantments.IMPALING, Enchantments.RIPTIDE, Enchantments.CHANNELING, Enchantments.MULTISHOT,
                    Enchantments.QUICK_CHARGE, Enchantments.PIERCING

            },
            new MapColor[] {
                    MapColor.GOLD, MapColor.GRAY, MapColor.MAGENTA, MapColor.LIME,
                    MapColor.TERRACOTTA_YELLOW, MapColor.IRON_GRAY, MapColor.WHITE, MapColor.DIAMOND_BLUE,
                    MapColor.STONE_GRAY, MapColor.GREEN, MapColor.PALE_PURPLE, MapColor.LAPIS_BLUE,
                    MapColor.PALE_YELLOW, MapColor.WHITE, MapColor.TERRACOTTA_ORANGE, MapColor.field_25707,
                    MapColor.field_25706, MapColor.CYAN, MapColor.TERRACOTTA_GRAY, MapColor.BROWN,
                    MapColor.BRIGHT_RED, MapColor.DARK_RED, MapColor.RED, MapColor.field_25703,
                    MapColor.ORANGE, MapColor.OAK_TAN, MapColor.WHITE_GRAY, MapColor.BRIGHT_RED,
                    MapColor.field_25703, MapColor.ORANGE, MapColor.BLACK, MapColor.OAK_TAN,
                    MapColor.BRIGHT_RED, MapColor.PALE_PURPLE, MapColor.YELLOW, MapColor.LAPIS_BLUE,
                    MapColor.DIAMOND_BLUE, MapColor.BRIGHT_RED
            }
    );

    public static int getColor(ItemStack stack) {
        int color = 0;
        if (stack.getRarity() != Rarity.COMMON) {
            return stack.getRarity().formatting.getColorValue();
        }
        if (stack.getItem() instanceof PotionItem || stack.getItem() instanceof TippedArrowItem) {
            return getPotionColor(stack);
        }
        if (stack.getItem() instanceof BlockItem && !(stack.getItem() instanceof BannerItem)) {
            color = getMapColor(stack).color;
        }
        if (color == 0) {
            color = inferColorFromID(stack, color);
        }
        if (color == 0) {
            return Rarity.COMMON.formatting.getColorValue();
        }
        return color;
    }

    private static int getPotionColor(ItemStack stack) {
        assert stack.getItem() instanceof PotionItem || stack.getItem() instanceof TippedArrowItem;
        return PotionUtil.getColor(stack);
    }

    private static int inferColorFromID(ItemStack stack, int original) {
        String name = Registry.ITEM.getId(stack.getItem()).toString();
        if (name.contains("white_")) return MapColor.WHITE.color;
        else if (name.contains("light_gray_")) return MapColor.LIGHT_GRAY.color;
        else if (name.contains("gray_") || name.contains("black_")) return MapColor.GRAY.color;
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
        else if (name.contains("brown_")) return MapColor.BROWN.color;
        else if (name.contains("magenta")) return MapColor.MAGENTA.color;
        else if (name.contains("netherite")) return MapColor.GRAY.color;
        else if (name.contains("diamond")) return Blocks.DIAMOND_BLOCK.getDefaultMapColor().color;
        else if (name.contains("iron")) return Blocks.IRON_BLOCK.getDefaultMapColor().color;
        else if (name.contains("gold_") || name.contains("golden_")) return Blocks.GOLD_BLOCK.getDefaultMapColor().color;
        else if (name.contains("emerald")) return Blocks.EMERALD_BLOCK.getDefaultMapColor().color;
        else if (name.contains("leather")) return MapColor.BROWN.color;
        else if (name.contains("redstone")) return Blocks.REDSTONE_BLOCK.getDefaultMapColor().color;
        else if (name.contains("glowstone")) return Blocks.GLOWSTONE.getDefaultMapColor().color;
        else if (name.contains("stone")) return Blocks.COBBLESTONE.getDefaultMapColor().color;
        else if (name.contains("wood")) return Blocks.OAK_PLANKS.getDefaultMapColor().color;
        else if (name.contains("prismarine")) return Blocks.PRISMARINE.getDefaultMapColor().color;
        else return original;
    }

    private static MapColor getMapColor(ItemStack stack) {
        MapColor color = ((BlockItem) stack.getItem()).getBlock().getDefaultMapColor();
        if (color.equals(MapColor.CLEAR)) {
            return MapColor.CLEAR;
        } else if (color.equals(MapColor.BLACK)) {
            return MapColor.GRAY;
        }
        return color;
    }

    public static void getClockTime(List<Text> tooltip) {
        try {
            long rawTime = MinecraftClient.getInstance().world.getTimeOfDay();
            int time = (int) (rawTime + 6000 > 23999 ? rawTime - 18000 : rawTime + 6000);
            int hr = time / 1000;
            float min = ((time % 1000) / 1000f) * 60;

            String paddingHr = hr < 10 ? "0" : "";
            String paddingMin = min < 10 ? "0" : "";
//                    i am proud to say that the above is one of the most confusing pieces of code i've written ~em
            MutableText text = (MutableText) Text.of(paddingHr + hr + ":" + paddingMin + (Math.round(min)));
            text.formatted(Formatting.GOLD);
            tooltip.add(text);
        } catch (NullPointerException ignored) {
        }
    }

    public static void addBeehiveTooltip(ItemStack stack, List<Text> tooltip) {
        MutableText text = (MutableText) Text.of("Contains " + ((NbtList) ((NbtCompound) stack.getTag().get("BlockEntityTag")).get("Bees")).size() + " bees");
        text.formatted(Formatting.GRAY);
        tooltip.add(text);
    }

    public static String romanNumeral(int num) {
        switch (num) {
            case 0: return "Nulla";
//            roman numerals don't have 0 but apparently they used that
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            case 6: return "VI";
            case 7: return "VII";
            case 8: return "VIII";
            case 9: return "IX";
            case 10: return "X";
            case 50: return "L";
            case 100: return "C";
            case 500: return "D";
            case 1000: return "M";
            default:
                String digits = String.valueOf(num);
                if (num > 3999) {
                    return digits;
                }
                StringBuilder numerals = new StringBuilder();
                char[] digitchars = digits.toCharArray();
                for (int i = digitchars.length; i > 0; i--) {
                    switch (i) {
                        case 1:
                            switch (digitchars[i - 1]) {
                                case '1':
                                    numerals.append("I");
                                    break;
                                case '2':
                                    numerals.append("II");
                                    break;
                                case '3':
                                    numerals.append("III");
                                    break;
                                case '4':
                                    numerals.append("IV");
                                    break;
                                case '5':
                                    numerals.append("V");
                                    break;
                                case '6':
                                    numerals.append("VI");
                                    break;
                                case '7':
                                    numerals.append("VII");
                                    break;
                                case '8':
                                    numerals.append("VIII");
                                    break;
                                case '9':
                                    numerals.append("IX");
                                    break;
                                case '0':
                                default:
                                    break;
                            }
                        case 2:
                            switch (digitchars[i - 1]) {
                                case '1':
                                    numerals.append("X");
                                    break;
                                case '2':
                                    numerals.append("XX");
                                    break;
                                case '3':
                                    numerals.append("XXX");
                                    break;
                                case '4':
                                    numerals.append("XL");
                                    break;
                                case '5':
                                    numerals.append("L");
                                    break;
                                case '6':
                                    numerals.append("LX");
                                    break;
                                case '7':
                                    numerals.append("LXX");
                                    break;
                                case '8':
                                    numerals.append("LXXX");
                                    break;
                                case '9':
                                    numerals.append("XC");
                                    break;
                                case '0':
                                default:
                                    break;
                            }
                        case 3:
                            switch (digitchars[i - 1]) {
                                case '1':
                                    numerals.append("C");
                                    break;
                                case '2':
                                    numerals.append("CC");
                                    break;
                                case '3':
                                    numerals.append("CCC");
                                    break;
                                case '4':
                                    numerals.append("CD");
                                    break;
                                case '5':
                                    numerals.append("D");
                                    break;
                                case '6':
                                    numerals.append("DC");
                                    break;
                                case '7':
                                    numerals.append("DCC");
                                    break;
                                case '8':
                                    numerals.append("DCCC");
                                    break;
                                case '9':
                                    numerals.append("CM");
                                    break;
                                case '0':
                                default:
                                    break;
                            }
                        case 4:
                            switch (digitchars[i - 1]) {
                                case '1':
                                    numerals.append("M");
                                    break;
                                case '2':
                                    numerals.append("MM");
                                    break;
                                case '3':
                                    numerals.append("MMM");
                                    break;
                                case '4':
                                case '5':
                                case '6':
                                case '7':
                                case '8':
                                case '9':
                                case '0':
                                default:
                                    return digits;
                            }
                        default:
                    }
                }
                return numerals.toString();
        }
    }

    public static void addTooltip(List<Text> tooltip, NbtElement tag) {
        if (tag instanceof NbtList) {
            for (NbtElement element :
                    (NbtList) tag) {
                switch (element.getType()) {
                    case 10:
                    case 9:
                        addTooltip(tooltip, element);
//                        wooooo recursion babey
                        break;
                    default:
                        String str = "- " + element.toString();
                        MutableText text = (MutableText) Text.of(str);
                        text.formatted(Formatting.GRAY);
                        tooltip.add(text);
                        break;
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
                            tooltip.add(Text.of(str));
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
                    MutableText mutableText = potStack.getName().shallowCopy();
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
                    MutableText mutableText = potStack.getName().shallowCopy();
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
                    MutableText mutableText = potStack.getName().shallowCopy();
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
                    MutableText mutableText = disc.getDescription().shallowCopy();
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
                    MutableText mutableText = item.getDefaultStack().getName().shallowCopy();
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

    public static void getColorFromTexture() {

    }

    @Override
    public void onInitialize() {
    }
}
