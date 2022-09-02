package com.miir;

import com.miir.tooltipsplus.integration.TooltipsPlusConfig;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.MapColor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TooltipsPlus implements ClientModInitializer {
    //    unsure how i feel about this color system. felt cute, might refactor later
    public static final Object2ObjectArrayMap<Enchantment, MapColor> ENCH_COLORS = new Object2ObjectArrayMap<>(
            new Enchantment[]{
                    Enchantments.MENDING,               Enchantments.VANISHING_CURSE, Enchantments.LURE,               Enchantments.LUCK_OF_THE_SEA,
                    Enchantments.FORTUNE,               Enchantments.UNBREAKING,      Enchantments.SILK_TOUCH,         Enchantments.EFFICIENCY,
                    Enchantments.PROTECTION,            Enchantments.THORNS,          Enchantments.AQUA_AFFINITY,      Enchantments.RESPIRATION,
                    Enchantments.PROJECTILE_PROTECTION, Enchantments.FEATHER_FALLING, Enchantments.BLAST_PROTECTION,   Enchantments.FIRE_PROTECTION,
                    Enchantments.DEPTH_STRIDER,         Enchantments.FROST_WALKER,    Enchantments.BINDING_CURSE,      Enchantments.SOUL_SPEED,
                    Enchantments.SHARPNESS,             Enchantments.SMITE,           Enchantments.BANE_OF_ARTHROPODS, Enchantments.KNOCKBACK,
                    Enchantments.FIRE_ASPECT,           Enchantments.LOOTING,         Enchantments.SWEEPING,           Enchantments.POWER,
                    Enchantments.PUNCH,                 Enchantments.FLAME,           Enchantments.INFINITY,           Enchantments.LOYALTY,
                    Enchantments.IMPALING,              Enchantments.RIPTIDE,         Enchantments.CHANNELING,         Enchantments.MULTISHOT,
                    Enchantments.QUICK_CHARGE,          Enchantments.PIERCING,        Enchantments.SWIFT_SNEAK
            },
            new MapColor[] {
                    MapColor.GOLD,                      MapColor.RED,                 MapColor.MAGENTA,                MapColor.LIME,
                    MapColor.TERRACOTTA_YELLOW,         MapColor.IRON_GRAY,           MapColor.WHITE,                  MapColor.DIAMOND_BLUE,
                    MapColor.STONE_GRAY,                MapColor.GREEN,               MapColor.PALE_PURPLE,            MapColor.LAPIS_BLUE,
                    MapColor.PALE_YELLOW,               MapColor.WHITE,               MapColor.TERRACOTTA_ORANGE,      MapColor.ORANGE,
                    MapColor.TERRACOTTA_BLUE,           MapColor.CYAN,                MapColor.TERRACOTTA_GRAY,        MapColor.BROWN,
                    MapColor.BRIGHT_RED,                MapColor.DARK_RED,            MapColor.RED,                    MapColor.TERRACOTTA_BLACK,
                    MapColor.ORANGE,                    MapColor.OAK_TAN,             MapColor.WHITE_GRAY,             MapColor.BRIGHT_RED,
                    MapColor.DULL_RED,                  MapColor.ORANGE,              MapColor.LIME,                   MapColor.OAK_TAN,
                    MapColor.BRIGHT_RED,                MapColor.PALE_PURPLE,         MapColor.YELLOW,                 MapColor.LAPIS_BLUE,
                    MapColor.DIAMOND_BLUE,              MapColor.BRIGHT_RED,          MapColor.CYAN
    });
    public static TooltipsPlusConfig CONFIG;

    public static final Object2ObjectArrayMap<Identifier, Integer> COLOR_CACHE = new Object2ObjectArrayMap<>();

    @Override
    public void onInitializeClient() {
        AutoConfig.register(TooltipsPlusConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(TooltipsPlusConfig.class).getConfig();
    }
}
