package com.miir.tooltipsplus;

import com.miir.TooltipsPlus;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Objects;

public abstract class NBTLogic {
    public static void addTooltip(List<Text> tooltip, NbtElement tag) {
        if (tag instanceof NbtList && (TooltipsPlus.CONFIG.showNBT || TooltipsPlus.CONFIG.showRepairCost)) {
            for (NbtElement element :
                    (NbtList) tag) {
                switch (element.getType()) {
                    case 10, 9 -> addTooltip(tooltip, element);
//                        woo recursion
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
                        if (!key.equals("Enchantments") && !key.equals("display") && !key.equals("StoredEnchantments") && TooltipsPlus.CONFIG.showNBT) {
                            tooltip.add(Text.of(key + ":"));
                            addTooltip(tooltip, ((NbtCompound) tag).get(key));
                        }
                        break;
                    default:
                        String str = key + ": " + ((NbtCompound) tag).get(key);
                        if (!key.equals("Damage") && TooltipsPlus.CONFIG.showNBT || (key.equals("RepairCost") && TooltipsPlus.CONFIG.showRepairCost)) {
                            MutableText text = MutableText.of(TextContent.EMPTY);
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

            } else if (item instanceof MusicDiscItem) {
                hasDiscs = true;
                SoundEvent music = ((MusicDiscItem) item).getSound();
                if (discs.containsKey(music)) {
                    int count = discs.get(music);
                    discs.put(music, count + stack1.getCount());
                } else {
                    discs.put(music, stack1.getCount());
                }
            } else {
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
                    mutableText.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(ColorFinder.getColor(potStack))));
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
                    mutableText.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(ColorFinder.getColor(potStack))));
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
                    mutableText.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(ColorFinder.getColor(potStack))));
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
            for (SoundEvent music :
                    discs.keySet()) {
                ++j;
                if (i <= k) {
                    ++i;
                    MusicDiscItem disc = MusicDiscItem.bySound(music);
                    if (disc != null) {
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
        }
        for (Item item : condensedInv.keySet()) {
            if (!item.getDefaultStack().isEmpty()) {
                ++j;
                if (i <= k || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 342)) {
                    ++i;
                    MutableText mutableText = item.getDefaultStack().getTooltip(null, TooltipContext.Default.NORMAL).get(0).copy();
                    if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 346)) {
                        countFormatting = Formatting.DARK_GRAY;
                        String id = Registry.ITEM.getId(item).toString();
                        mutableText = (MutableText) Text.of("[");
                        MutableText name = (MutableText) Text.of(id);
                        Formatting formatting = i % 2 == 0 ? Formatting.WHITE : Formatting.GRAY;
                        mutableText.append(name).append("]");
                        mutableText.formatted(formatting);
                    } else if (item instanceof BlockItem && item.getRarity(item.getDefaultStack()).equals(Rarity.COMMON)) {
                        mutableText.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(ColorFinder.getColor(item.getDefaultStack()))));
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

    public static boolean hasBees(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem bi) {
            if (bi.getBlock() instanceof BeehiveBlock) {
                if (stack.getNbt() != null) {
                    try {
                        return !((NbtList) Objects.requireNonNull(((NbtCompound) Objects.requireNonNull(stack.getNbt().get("BlockEntityTag"))).get("Bees"))).isEmpty();
                    } catch (NullPointerException ignored) {
                    }
                }
            }
        }
        return false;
    }

    public static boolean hasSkowner(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem bi && stack.getNbt() != null) {
            if (bi.getBlock() instanceof SkullBlock) {
                return (stack.getNbt().contains("SkullOwner", NbtElement.COMPOUND_TYPE));
            }
        }
        return false;
    }
}
