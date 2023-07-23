package com.miir.tooltipsplus;

import com.miir.TooltipsPlus;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.PlayerSkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public abstract class ExtraTooltips {
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
            return Text.empty();
    }

    public static Text getHeadTooltip(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem) {
            if (((BlockItem) stack.getItem()).getBlock() instanceof PlayerSkullBlock) {
                NbtCompound nbtCompound = stack.getNbt();
                GameProfile gameProfile;
                if (nbtCompound != null) {
                    if (nbtCompound.contains("SkullOwner", NbtElement.COMPOUND_TYPE)) {
                        gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
                        if (gameProfile != null) {
                            return Text.literal("Skull Owner: " + gameProfile.getName());
                        }
                    } else if (nbtCompound.contains("SkullOwner", NbtElement.STRING_TYPE) && !StringUtils.isBlank(nbtCompound.getString("SkullOwner"))) {
                        gameProfile = new GameProfile(null, nbtCompound.getString("SkullOwner"));
                        return Text.literal("Skull Owner: " + gameProfile.getName());
                    }
                }
            }
        }
        return Text.empty();
    }

    public static Text addBeehiveTooltip(ItemStack stack) {
        try {
            int bees = ((NbtList) Objects.requireNonNull(((NbtCompound) Objects.requireNonNull(stack.getNbt().get("BlockEntityTag"))).get("Bees"))).size();
            MutableText text = (MutableText) Text.of("Contains " + bees + (bees == 1 ? " bee" : " bees"));
            text.formatted(Formatting.GRAY);
            return text;
        } catch (Exception e) {
            return Text.empty();
        }
    }

    public static String romanNumeral(int num) {
        if (TooltipsPlus.CONFIG.numericalEnchants) {
            return Integer.toString(num);
        }
        switch (num) {
            case 0 -> {
                return "Nulla";
            }
//            roman numerals don't have 0, but apparently they used that
            case 1 -> {
                return "I";
            }
            case 2 -> {
                return "II";
            }
            case 3 -> {
                return "III";
            }
            case 4 -> {
                return "IV";
            }
            case 5 -> {
                return "V";
            }
            case 6 -> {
                return "VI";
            }
            case 7 -> {
                return "VII";
            }
            case 8 -> {
                return "VIII";
            }
            case 9 -> {
                return "IX";
            }
            case 10 -> {
                return "X";
            }
            case 50 -> {
                return "L";
            }
            case 100 -> {
                return "C";
            }
            case 500 -> {
                return "D";
            }
            case 1000 -> {
                return "M";
            }
            default -> {
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
