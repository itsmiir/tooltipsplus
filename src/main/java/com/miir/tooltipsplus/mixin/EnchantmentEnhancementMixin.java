package com.miir.tooltipsplus.mixin;

import com.miir.TooltipsPlus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.MapColor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Environment(EnvType.CLIENT)
@Mixin(Enchantment.class)
public class EnchantmentEnhancementMixin {
@Inject(at = @At("HEAD"), method = "getName", cancellable = true)
    public void mixin(int level, CallbackInfoReturnable<Text> cir) {
        MutableText mutableText = new TranslatableText(((Enchantment) ((Object) this)).getTranslationKey());
        mutableText.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(getEnchantmentColor(((Enchantment)(Object)this)))));
        if (level != 1 || ((Enchantment) ((Object) this)).getMaxLevel() != 1) {
            MutableText levelNumeral = (MutableText) Text.of(TooltipsPlus.romanNumeral(level));
            switch (level) {
                case 1 -> levelNumeral.formatted(Formatting.WHITE);
                case 2 -> levelNumeral.formatted(Formatting.YELLOW);
                case 3 -> levelNumeral.formatted(Formatting.AQUA);
                case 4 -> levelNumeral.formatted(Formatting.DARK_PURPLE);
                case 5 -> levelNumeral.formatted(Formatting.DARK_RED);
                default -> levelNumeral.formatted(Formatting.DARK_GREEN);
            }
            mutableText.append(" ").append(levelNumeral);
        }
    cir.setReturnValue(mutableText);
    }
    private int getEnchantmentColor(Enchantment enchantment) {
        MapColor color = TooltipsPlus.ENCH_COLORS.get(enchantment);
        return color != null ? color.color : TooltipsPlus.COLORS[new Random(enchantment.hashCode()).nextInt(TooltipsPlus.COLORS.length)].color;
    }
}
