package com.miir.tooltipsplus.mixin;

import com.miir.TooltipsPlus;
import com.miir.tooltipsplus.ColorFinder;
import com.miir.tooltipsplus.ExtraTooltips;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.MapColor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Environment(EnvType.CLIENT)
@Mixin(Enchantment.class)
public abstract class EnchantmentEnhancementMixin {
    @Shadow public abstract int getMaxLevel();

    @Inject(at = @At("HEAD"), method = "getName", cancellable = true)
    public void mixin(int level, CallbackInfoReturnable<Text> cir) {
        if (TooltipsPlus.CONFIG == null) return; // in case this method is accessed in a mod that gets loaded earlier's onInitialize method
        MutableText mutableText = Text.translatable(((Enchantment) ((Object) this)).getTranslationKey());
        if (TooltipsPlus.CONFIG.enchantColors) mutableText.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(getEnchantmentColor(((Enchantment)(Object)this)))));
        if ((level != 1 || ((Enchantment) ((Object) this)).getMaxLevel() != 1)) {
            MutableText levelNumeral = (MutableText) Text.of(ExtraTooltips.romanNumeral(level));
            if (TooltipsPlus.CONFIG.enchantColors) {
                switch (level) {
                    case 1 -> levelNumeral.formatted(Formatting.WHITE);
                    case 2 -> levelNumeral.formatted(Formatting.YELLOW);
                    case 3 -> levelNumeral.formatted(Formatting.AQUA);
                    case 4 -> levelNumeral.formatted(Formatting.DARK_PURPLE);
                    case 5 -> levelNumeral.formatted(Formatting.DARK_RED);
                    default -> levelNumeral.formatted(Formatting.DARK_GREEN);
                }
            }
            if (level == this.getMaxLevel() && TooltipsPlus.CONFIG.maxEnchantPlus) levelNumeral = Text.literal("+").formatted(Formatting.DARK_RED);
            mutableText.append(" ").append(levelNumeral);
        }
    cir.setReturnValue(mutableText);
    }
    private int getEnchantmentColor(Enchantment enchantment) {
        MapColor color = TooltipsPlus.CONFIG.enchantColors ? TooltipsPlus.ENCH_COLORS.get(enchantment) : MapColor.WHITE;
        return ColorFinder.ensureReadability(color == null ? enchantment.hashCode() : color.color);
    }
}
