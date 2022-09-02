package com.miir.tooltipsplus.mixin;

import com.miir.TooltipsPlus;
import com.miir.tooltipsplus.ColorFinder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public abstract class NameDisplayMixin {
    @ModifyVariable(
            method = "getTooltip",
            index = 4,
            at = @At(value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/item/ItemStack;hasCustomName()Z",
            shift = At.Shift.BEFORE)
    )
    public MutableText mixin(MutableText original) {
        if (TooltipsPlus.CONFIG.colors) {
            return original.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(ColorFinder.getColor((((ItemStack) (Object) this))))));
        }
        return original;
    }
}
