package com.miir.tooltipsplus.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.TextSearchProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class FixSearchingMixin {
    @Shadow @Final private SearchManager searchManager;

    @Inject(method = "initializeSearchProviders", at = @At("TAIL"))
    private void mixin(CallbackInfo ci) {
        this.searchManager.put(
                SearchManager.ITEM_TOOLTIP,
                stacks -> new TextSearchProvider<>(
                        stack -> Stream.of(stack.getName())
                                .map(tooltip -> Formatting.strip(tooltip.getString()).trim()).filter(string -> !string.isEmpty()), stack -> Stream.of(Registry.ITEM.getId(stack.getItem())), (List<ItemStack>) stacks));
    }
}
