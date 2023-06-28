package com.miir.tooltipsplus.integration;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "tooltipsplus")
public class TooltipsPlusConfig implements ConfigData {
    public boolean colors = true;
    public boolean enchantColors = true;
    public boolean potionColors = true;
    public boolean showBees = true;
    public boolean clockTime = true;
    public boolean showRepairCost = true;
    public boolean numericalEnchants = false;
    public boolean maxEnchantPlus = false;
    public boolean skullTips = true;
    public boolean showNBT = true;
    public boolean shulkers = true;
}
