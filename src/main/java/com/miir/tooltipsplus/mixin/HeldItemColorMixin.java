package com.miir.tooltipsplus.mixin;

import com.miir.TooltipsPlus;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(InGameHud.class)
public class HeldItemColorMixin {
    @Shadow private ItemStack currentStack;

    @ModifyVariable(
            method = "renderHeldItemTooltip",
            index = 2,
            at = @At(
                    target = "Lnet/minecraft/item/ItemStack;hasCustomName()Z",
                    shift = At.Shift.BEFORE,
                    value = "INVOKE"
            )
    )
    private MutableText mixin(MutableText original) {
        return original.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(TooltipsPlus.getColor(this.currentStack))));
    }
}
