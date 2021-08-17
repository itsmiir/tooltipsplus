package com.miir.tooltipsplus.mixin;

import com.google.gson.JsonParseException;
import com.miir.TooltipsPlus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public abstract class NameDisplayOverride {

    @Overwrite
    public Text getName() {
        NbtCompound nbtCompound = ((ItemStack) (Object) this).getSubTag("display");
        if (nbtCompound != null && nbtCompound.contains("Name", 8)) {
            try {
                MutableText text = Text.Serializer.fromJson(nbtCompound.getString("Name"));
                if (text != null) {
                    return text.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(TooltipsPlus.getColor((((ItemStack) (Object) this))))));
                }

                nbtCompound.remove("Name");
            } catch (JsonParseException var3) {
                nbtCompound.remove("Name");
            }
        }

        return ((MutableText) ((ItemStack) ((Object) this)).getItem().getName(((ItemStack) (Object) this)))
                .setStyle(
                        Style.EMPTY.withColor(
                                TextColor.fromRgb(
                                        TooltipsPlus.getColor((((ItemStack) (Object) this))))));
    }
}
