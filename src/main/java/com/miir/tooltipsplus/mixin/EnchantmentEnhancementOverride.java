package com.miir.tooltipsplus.mixin;

import com.miir.TooltipsPlus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Environment(EnvType.CLIENT)
@Mixin(Enchantment.class)
public class EnchantmentEnhancementOverride {

//    i know this is bad, fuck you ~m
/**
 * @author miir
 */
@Overwrite
    public Text getName(int level) {
        MutableText mutableText = Text.translatable(((Enchantment) ((Object) this)).getTranslationKey());
        mutableText.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(getEnchantmentColor(((Enchantment)(Object)this)))));

        if (level != 1 || ((Enchantment) ((Object) this)).getMaxLevel() != 1) {
            MutableText levelNumeral = (MutableText) Text.of(TooltipsPlus.romanNumeral(level));
            switch (level) {
                case 1:
                    levelNumeral.formatted(Formatting.WHITE);
                    break;
                case 2:
                    levelNumeral.formatted(Formatting.YELLOW);
                    break;
                case 3:
                    levelNumeral.formatted(Formatting.AQUA);
                    break;
                case 4:
                    levelNumeral.formatted(Formatting.DARK_PURPLE);
                    break;
                case 5:
                    levelNumeral.formatted(Formatting.DARK_RED);
                    break;
                default:
                    levelNumeral.formatted(Formatting.DARK_GREEN);
            }
            mutableText.append(" ").append(levelNumeral);
        }

        return mutableText;
    }
    private int getEnchantmentColor(Enchantment enchantment) {
        return TooltipsPlus.ENCH_COLORS.get(enchantment).color;
    }
}
