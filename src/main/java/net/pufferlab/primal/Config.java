package net.pufferlab.primal;

import java.io.File;
import java.util.*;

import net.minecraftforge.common.config.Configuration;
import net.pufferlab.primal.recipes.AnvilAction;
import net.pufferlab.primal.utils.IOUtils;
import net.pufferlab.primal.utils.IPrimalType;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public enum Config {

    // Vanilla Tweaks
    noTreeFistPunching(Module.early_game$tweaks, true,
        "Whether to enable no tree punching mechanics. Meaning that you cannot break logs with your fist."),
    fallingTree(Module.early_game$tweaks, true, "Whether trees fall, so no more floating logs"),
    harderSoil(Module.early_game$tweaks, true,
        "Whether to make soil (dirt/sand) slightly harder to break, giving shovels more use"),
    harderTrees(Module.early_game$tweaks, true,
        "Whether to make tree logs slightly harder to break, depending on the size of the tree"),
    destructiveFallingBlocks(Module.early_game$tweaks, true,
        "Whether to make it so instead of the falling block getting destroyed on replaceable blocks, the replaceable block get destroyed instead."),
    sidewayFallingBlocks(Module.early_game$tweaks, true,
        "Whether you want to enable sideway gravity for falling blocks, put to false if you dont want the gravity changes"),
    vanillaToolsRemovalMode(Module.early_game$tweaks, 1, 0, 2,
        "0: Don't remove vanilla tools. 1: Remove the recipes. 2: Keep the recipes but make tools unusable."),
    leatherDropReplacement(Module.early_game$tweaks, true,
        "Whether to replace leather drops with raw hides to balance the leather recipes."),
    stickDropChance(Module.early_game$tweaks, 0.166F,
        "The chance from 0 (0%) to 1 (100%) for a stick to drop from leaves. Putting this to 0 will stop dropping."),

    // Mod Content
    fireStarterSuccessChance(Module.early_game, 0.2F,
        "The chance from 0 (0%) to 1 (100%) for the fire starter to succeed making a fire. Putting this to 0 will stop the fire starter from working."),
    ceramicBucketLiquids(Module.early_game, new String[] { "fluiddeath", "fluidpure" },
        "The extra liquids that the ceramic bucket will be able to hold"),
    ceramicBucketLiquidsHotCap(Module.early_game, 1000,
        "The temperature for a liquid to be considered hot and break the ceramic bucket."),
    ropeLadderExtension(Module.early_game, 10,
        "The amount of blocks the rope ladder can be extended from a supported block"),

    // Campfire
    campfireBurnTime(Module.early_game, 20 * 120,
        "The time in ticks that it takes for the campfire to consume one of its fuel."),
    campfireSmeltTime(Module.early_game, 20 * 15,
        "The time in ticks that it will take the Campfire to smelt one of its slot."),
    ovenSmeltTime(Module.early_game, 20 * 7, "The time in ticks that it will take the Oven to smelt one of its slot."),

    // Forge
    forgeBurnTime(Module.early_game, 20 * 120,
        "The time in ticks that it takes for the forge to consume one of its fuel."),

    // Pit Kiln
    pitKilnSmeltTime(Module.early_game, 20 * 120,
        "The time in ticks that it will take the Pit Kiln to smelt it's content."),

    // Log Pile
    logPileSmeltTime(Module.early_game, 20 * 120,
        "The time in ticks that it will take the LogPile to smelt into charcoal."),

    // Torch
    litTorches(Module.early_game$lighting, true, "Put to false if you don't want to enable the torches with burn time"),
    torchBurnTime(Module.early_game$lighting, 20 * 60 * 20,
        "The time in ticks that lit torches will burn before going out."),
    torchRebalance(Module.early_game$lighting, true,
        "Whether to make vanilla torches require glowstone to balance the lit torches"),

    // Food
    foodValues(Module.farming, new ValueIntFloat(Constants.foodTypesAll, Value.Type.food),
        "All of the corresponding food values and how much food they give when eating, the first argument argument is hunger, and second is saturation"),
    foodBaseGrowth(Module.farming, 1500,
        "The base amount of ticks it takes to grow one stage (depends on how much stages a plant has"),
    farmlandReplenishment(Module.farming, 2000,
        "Numbers of ticks that the farmlands takes to replenishment its nutrient content a small amount"),
    farmlandReplenishmentRate(Module.farming, 0.01F,
        "The percentage of the nutrients that get restored every times it replenishes"),
    bonemealInstantGrowth(Module.farming, false,
        "Keeps the function that makes bone meal instantly grow crops one stage (kind of overpowered)"),

    // Metal
    temperatureCap(Module.metalworking, 1,
        "The minimum temperature will be displayed, anything lower will not show in tooltips"),
    metalHeatRendering(Module.metalworking, true,
        "Put to false if you don't want heat rendering on items. Will globally disable all rendering even on modded ingots."),
    modMetalHeatRendering(Module.metalworking, true,
        "Put to false if you are getting some rendering issue with other mod ingots that get registered Primal Heat Rendering overlay."),
    metalPriority(Module.metalworking, new String[] { "minecraft", "primal", "etfuturum" },
        "The ingot priority list, higher means the mod items will be taken in priority."),
    metalPriorityOverride(Module.metalworking,
        new String[] { "ingotIron=minecraft:iron_ingot", "ingotGold=minecraft:gold_ingot" },
        "Override so certain ore dictionary give specific materials"),
    metalMelting(Module.metalworking, new ValueInt(Constants.metalTypesAll, Value.Type.melting),
        "The melting temperature for the correspond metals."),
    metalForging(Module.metalworking, new ValueInt(Constants.metalTypesAll, Value.Type.forging),
        "The forging temperature for the correspond metals."),
    metalWelding(Module.metalworking, new ValueInt(Constants.metalTypesAll, Value.Type.welding),
        "The welding temperature for the correspond metals."),
    metalLiquids(Module.metalworking, new ValueString(Constants.metalTypesAll, Value.Type.fluid),
        "The liquids that will be used for the corresponding metals"),
    metalHighTierCasting(Module.metalworking, false,
        "Whether to enable high tier metals being able to be casted in a mold."),
    metalOreValue(Module.metalworking, 36, "The value that one ore should give."),
    metalSmallOreValue(Module.metalworking, 16, "The value that one small ore should give."),
    metalIngotValue(Module.metalworking, 144, "The value that one ingot of metal should give."),
    metalVanillaToolValue(Module.metalworking, false,
        "Put to true if you want the casting to be the actual values of ingots in vanilla tools instead of 1 ingot"),
    metalNuggetValue(Module.metalworking, 16, "The value that one nugget of metal should give."),

    // Forging
    anvilActionStep(Module.metalworking$forging, new ValueInt(AnvilAction.values()),
        "The step value that the anvil action will take when clicked."),
    anvilLineRange(Module.metalworking$forging, 1,
        "The range that you need to be to the recipe line to complete an anvil recipe."),

    // Mechanical Power General
    extendMechanicalPowerRendering(Module.mechanical_power, true,
        "Prevent mechanical power TESR to stop rendering at the same distance as normal entities, since they are large blocks"),

    // Waterwheel
    waterwheelDefaultSpeed(Module.mechanical_power, 5F, "The default speed that the waterwheel will have."),
    waterwheelRestrictBiome(Module.mechanical_power, false,
        "Whether waterwheel should be limited to River/Oceans biomes."),

    // Windmill
    windmillDefaultSpeed(Module.mechanical_power, 5F, "The default speed that the windmill will have."),
    windmillIdealHeight(Module.mechanical_power, 100, 0, 256, "The height which the windmill will spin the fastest."),
    windmillRange(Module.mechanical_power, 60, 0, 256,
        "The range around the ideal height in which the windmill will operate."),

    // General
    minWorldHeight(Module.world, 0,
        "The minimum Y value that the mod features will spawn. (Do not put lower than 0 without Cubic Chunks)"),
    maxWorldHeight(Module.world, 255,
        "The maximum Y value that the mod features will spawn. (Do not put higher than 255 without Cubic Chunks)"),
    strataStoneTypes(Module.world, true, "Put to false if you want to disable all of the stone types of the mod."),
    soilTypes(Module.world, true, "Put to false if you want to disable all of the soil types of the mod."),
    oreVeins(Module.world, true, "Whether to enable large ore veins"),

    // WorldGen
    worldLayerExtending(Module.world$default, false,
        "[EXPERIMENTAL] Extends the height of the world, making the surface generate higher"),
    strataBiomeSpecific(Module.world$default, true,
        "Put to false if you don't want biome-specific stones (ex. Desert will have sandstone, BOP Volcanos have basalt) etc.."),
    strataWorldGen(Module.world$default, true, "Whether to enable the generation of the strata stone types"),
    strataStoneHeightRange(Module.world$default, new Range(Constants.stoneTypes, Value.Type.height),
        "The corresponding min/max Y Value that the strata stones will be able to spawn."),
    soilWorldGen(Module.world$default, true, "Whether to enable the generation of the soil types"),
    enableVanillaOres(Module.world$default, false, "Put to true if you want the vanilla ores back"),
    oreVeinsWorldGen(Module.world$default, true, "Whether to enable the generation of the ore types"),
    oreVeinsHeightRange(Module.world$default, new Range(Constants.veinTypesAll, Value.Type.height),
        "The corresponding min/max Y Value that the ores will be able to spawn."),
    oreVeinsSizeRange(Module.world$default, new Range(Constants.veinTypesAll, Value.Type.size),
        "The corresponding min/max size values that the ore veins will randomly generate between these values."),
    oreVeinsRarity(Module.world$default, new ValueFloat(Constants.veinTypesAll, Value.Type.rarity),
        "The correspond rarity that the vein will spawn every chunk"),
    rockWorldGen(Module.world$default, true, "Whether to enable loose rocks generating in the world."),
    shellWorldGen(Module.world$default, true, "Whether to enable loose shell generating bear beaches."),

    // TerraFirma
    useAllThreadsTF(Module.world$terrafirma, false, "Uses all threads possible for worldgen."),
    seaLevelTF(Module.world$terrafirma, 100,
        "The sea level of the world, below this Y value, the world will be filled with water"),
    strataStoneHeightRangeTF(Module.world$terrafirma, new Range(Constants.stoneTypes, Value.Type.heightTF),
        "The corresponding min/max Y Value that the strata stones will be able to spawn."),
    soilHumidityRangeTF(Module.world$terrafirma, new ValueFloat(Constants.soilTypes, Value.Type.humidityTF),
        "The humidity value of the different soil types, it will determine where they spawn."),
    oreVeinsHeightRangeTF(Module.world$terrafirma, new Range(Constants.veinTypesAll, Value.Type.heightTF),
        "The corresponding min/max Y Value that the ores will be able to spawn."),

    // Mixins
    simplifyDebugMenu(Module.fixes, true, "Remove useless info from the debug (F3) menu"),
    wearableRenderer(Module.fixes, true,
        "Enable the mixins for the wearable renderer (used for clothes), disable if you use a mod that causes shit to break"),
    hpBatcherDisabler(Module.fixes, true, "Prevents hodgepodge to slow down packet for the mod tile entities"),
    particleFix(Module.fixes, true,
        "Fixes particles to match the block it's being broken, required for some blocks to have correct looking particles"),
    plantFix(Module.fixes, true, "Fixes some vanilla logic not applying to the mod grass"),
    waterloggingFix(Module.fixes, true,
        "Fixes some water details to make some waterlogged blocks render and function properly"),
    dragonAPIPlantFix(Module.fixes, true,
        "This enables the mixins to DragonAPI to make it recognize the mod dirt, and avoid plants popping off."),
    bopPlantFix(Module.fixes, true,
        "This enables the mixins to Biomes O' Plenty to make it recognize the mod dirt, and avoid plants popping off."),
    exblPlantFix(Module.fixes, true,
        "This enables the mixins to Extra Biomes XL to make it recognize the mod dirt, and avoid plants popping off.");

    public boolean isBoolean;
    public boolean isInt;
    public boolean isFloat;
    public boolean isStringList;
    public boolean isVarValue;
    public final String name;
    public final String category;
    public final String comment;

    boolean bValue;
    boolean bDefault;
    int iValue;
    int iDefault;
    int iMinValue;
    int iMaxValue;
    float fValue;
    float fDefault;
    float fMinValue;
    float fMaxValue;
    String[] slValue;
    String[] slDefault;
    Value value;

    Config(Module category, boolean defaultValue, String comment) {
        this.isBoolean = true;
        this.name = this.name();
        this.category = category.name;
        this.comment = comment;
        this.bDefault = defaultValue;
        this.bValue = bDefault;
    }

    Config(Module category, int defaultValue, String comment) {
        this.isInt = true;
        this.name = this.name();
        this.iMinValue = 0;
        this.iMaxValue = 50000;
        this.category = category.name;
        this.comment = comment;
        this.iDefault = defaultValue;
        this.iValue = iDefault;
    }

    Config(Module category, int defaultValue, int minValue, int maxValue, String comment) {
        this.isInt = true;
        this.name = this.name();
        this.iMinValue = minValue;
        this.iMaxValue = maxValue;
        this.category = category.name;
        this.comment = comment;
        this.iDefault = defaultValue;
        this.iValue = iDefault;
    }

    Config(Module category, float defaultValue, String comment) {
        this.isFloat = true;
        this.name = this.name();
        this.category = category.name;
        this.comment = comment;
        this.fDefault = defaultValue;
        if (defaultValue > 1.0F) {
            this.fMinValue = 0.0F;
            this.fMaxValue = 256.0F;
        } else {
            this.fMinValue = 0.0F;
            this.fMaxValue = 1.0F;
        }
        this.fValue = fDefault;
    }

    Config(Module category, String[] defaultList, String comment) {
        this.isStringList = true;
        this.name = this.name();
        this.category = category.name;
        this.comment = comment;
        this.slDefault = defaultList;
        this.slValue = slDefault;
    }

    Config(Module category, Value configValue, String comment) {
        this.isStringList = true;
        this.isVarValue = true;
        this.name = this.name();
        this.category = category.name;
        setConfigValue(configValue);
        this.comment = comment;
    }

    public void setConfigValue(Value value) {
        this.value = value;
        this.value.setConfig(this);
        String[] str = value.getDefault();
        this.slDefault = str;
        this.slValue = str;
    }

    public boolean getBoolean() {
        if (!this.isBoolean) {
            throw new IllegalStateException();
        }
        return bValue;
    }

    public int getInt() {
        if (!this.isInt) {
            throw new IllegalStateException();
        }
        return iValue;
    }

    public int getDefaultInt() {
        if (!this.isInt) {
            throw new IllegalStateException();
        }
        return iDefault;
    }

    public float getFloat() {
        if (!this.isFloat) {
            throw new IllegalStateException();
        }
        return fValue;
    }

    public float getDefaultFloat() {
        if (!this.isFloat) {
            throw new IllegalStateException();
        }
        return fDefault;
    }

    public boolean roll(Random random) {
        if (!this.isFloat) {
            throw new IllegalStateException();
        }
        return random.nextFloat() < getFloat();
    }

    public String[] getStringList() {
        if (!this.isStringList) {
            throw new IllegalStateException();
        }
        return slValue;
    }

    public static Configuration configuration;

    public static void setupEarlyConfig() {
        if (configuration == null) {
            Config.synchronizeConfiguration(IOUtils.createConfigFile(Primal.MODID));
        }
    }

    public static void synchronizeConfiguration(File configFile) {
        configuration = new Configuration(configFile);
        for (Config config : Config.values()) {
            if (config.isBoolean) {
                config.bValue = configuration.getBoolean(config.name, config.category, config.bDefault, config.comment);
            }
            if (config.isInt) {
                config.iValue = configuration.getInt(
                    config.name,
                    config.category,
                    config.iDefault,
                    config.iMinValue,
                    config.iMaxValue,
                    config.comment);
            }
            if (config.isFloat) {
                config.fValue = configuration.getFloat(
                    config.name,
                    config.category,
                    config.fDefault,
                    config.fMinValue,
                    config.fMaxValue,
                    config.comment);
            }
            if (config.isStringList) {
                config.slValue = configuration
                    .getStringList(config.name, config.category, config.slDefault, config.comment);
            }
        }
        for (Module module : Module.values()) {
            configuration.setCategoryComment(module.name, module.comment);
        }
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static void updateConfiguration() {
        for (Config config : Config.values()) {
            if (config.isVarValue) {
                config.value.genMap();
                config.value.updateValues();
            }
        }
    }

    public static enum Module {

        early_game(true,
            "Includes all the early game stuff such as campfire, pitkiln and everything you will use early on."),
        early_game$tweaks(true, "Multiple vanilla tweaks to spice up the early game."),
        early_game$lighting(true, "Includes all of the changes related to lighting."),
        farming(true, "Includes all of the food stuff."),
        metalworking(true, "Includes all of the metalworking aspect of the mod, such as the forge, crucible and such."),
        metalworking$forging(true, "Includes all options for the Anvil Forging minigame"),
        mechanical_power(true,
            "Includes all of the mechanical power machinery, such as windmill, waterwheel and anything that moves."),
        world(true, "Includes all of the world stuff from the mod."),
        world$default(true, "All options for default worldgen types (BOP/Vanilla)"),
        world$terrafirma(true, "All options for the custom worldgen type Terrafirma"),
        fixes(true, "Includes all vanilla and mod fixes");

        public String name;
        public String comment;
        public boolean isMainModule;
        boolean enabled;
        boolean enabledDefault;

        Module(boolean enabled, String comment) {
            this.name = this.name()
                .replace("$", ".");
            this.isMainModule = this.name.indexOf('.') == -1;
            this.enabledDefault = enabled;
            this.comment = comment;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

    public static class Value {

        private Config config;
        private Type index;
        private final IPrimalType[] types;

        public Value(IPrimalType[] types) {
            this(types, Type.none);
        }

        public Value(IPrimalType[] types, Type index) {
            this.types = types;
            this.index = index;
        }

        public Type getIndex() {
            return index;
        }

        public void setConfig(Config config) {
            this.config = config;
        }

        public Config getConfig() {
            return config;
        }

        public IPrimalType[] getTypes() {
            return types;
        }

        public void genMap() {}

        public void updateValues() {}

        public String[] getDefault() {
            return null;
        }

        public enum Type {
            none,
            height,
            heightTF,
            size,
            rarity,
            fluid,
            food,
            melting,
            forging,
            welding,
            humidity,
            humidityTF
        }
    }

    public static class Range extends Value {

        private final TObjectIntMap<String> minMap = new TObjectIntHashMap<>();
        private final TObjectIntMap<String> maxMap = new TObjectIntHashMap<>();

        public Range(IPrimalType[] types) {
            super(types);
        }

        public Range(IPrimalType[] types, Type index) {
            super(types, index);
        }

        public String[] getDefault() {
            IPrimalType[] types = getTypes();
            Type index = getIndex();
            String[] list = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                list[i] = types[i].getName() + "=" + types[i].getMinInt(index) + "-" + types[i].getMaxInt(index);
            }
            return list;
        }

        public void updateValues() {
            Type index = getIndex();
            for (IPrimalType type : getTypes()) {
                String name = type.getName();
                if (hasValidRange(name)) {
                    type.setMinInt(index, minMap.get(name));
                    type.setMaxInt(index, maxMap.get(name));
                }
            }
        }

        public void genMap() {
            Config config = getConfig();
            String[] priorityOverride = config.getStringList();
            try {
                for (String s : priorityOverride) {
                    String[] spl = s.split("=");
                    if (spl.length == 2) {
                        String ore = spl[0];
                        String[] hs = spl[1].split("-");
                        int min = Integer.parseInt(hs[0]);
                        int max = Integer.parseInt(hs[1]);
                        minMap.put(ore, min);
                        maxMap.put(ore, max);
                    }
                }
            } catch (Exception e) {
                throwInvalidConfig(config);
            }
        }

        public boolean hasValidRange(String string) {
            return minMap.containsKey(string) && maxMap.containsKey(string);
        }
    }

    public static class ValueInt extends Value {

        private final TObjectIntMap<String> valueMap = new TObjectIntHashMap<>();

        public ValueInt(IPrimalType[] types, Type type) {
            super(types, type);
        }

        public ValueInt(IPrimalType[] types) {
            super(types);
        }

        public String[] getDefault() {
            IPrimalType[] types = getTypes();
            Type index = getIndex();
            String[] list = new String[types.length];
            for (int i = 0; i < list.length; i++) {
                list[i] = types[i].getName() + "=" + types[i].getInt(index);
            }
            return list;
        }

        public void updateValues() {
            Type index = getIndex();
            for (IPrimalType type : getTypes()) {
                String name = type.getName();
                if (hasValidValue(name)) {
                    type.setInt(index, valueMap.get(name));
                }
            }
        }

        public void genMap() {
            Config config = getConfig();
            String[] priorityOverride = config.getStringList();
            try {
                for (String s : priorityOverride) {
                    String[] spl = s.split("=");
                    if (spl.length == 2) {
                        String ore = spl[0];

                        int temp = Integer.parseInt(spl[1]);
                        valueMap.put(ore, temp);
                    }
                }
            } catch (Exception e) {
                throwInvalidConfig(config);
            }
        }

        public boolean hasValidValue(String string) {
            return valueMap.containsKey(string);
        }
    }

    public static class ValueFloat extends Value {

        private final TObjectFloatMap<String> valueMap = new TObjectFloatHashMap<>();

        public ValueFloat(IPrimalType[] types, Type index) {
            super(types, index);
        }

        public ValueFloat(IPrimalType[] types) {
            super(types);
        }

        public String[] getDefault() {
            IPrimalType[] types = getTypes();
            Type index = getIndex();
            String[] list = new String[types.length];
            for (int i = 0; i < list.length; i++) {
                list[i] = types[i].getName() + "=" + types[i].getFloat(index);
            }
            return list;
        }

        public void updateValues() {
            Type index = getIndex();
            for (IPrimalType type : getTypes()) {
                String name = type.getName();
                if (hasValidValue(name)) {
                    type.setFloat(index, valueMap.get(name));
                }
            }
        }

        public void genMap() {
            Config config = getConfig();
            String[] priorityOverride = config.getStringList();
            try {
                for (String s : priorityOverride) {
                    String[] spl = s.split("=");
                    if (spl.length == 2) {
                        String ore = spl[0];

                        float temp = Float.parseFloat(spl[1]);
                        valueMap.put(ore, temp);
                    }
                }
            } catch (Exception e) {
                throwInvalidConfig(config);
            }
        }

        public boolean hasValidValue(String string) {
            return valueMap.containsKey(string);
        }
    }

    public static class ValueString extends Value {

        private final Map<String, String> valueMap = new HashMap<>();

        public ValueString(IPrimalType[] types, Type type) {
            super(types, type);
        }

        public ValueString(IPrimalType[] types) {
            super(types);
        }

        public String[] getDefault() {
            IPrimalType[] types = getTypes();
            Type index = getIndex();
            String[] list = new String[types.length];
            for (int i = 0; i < list.length; i++) {
                list[i] = types[i].getName() + "=" + types[i].getString(index);
            }
            return list;
        }

        public void updateValues() {
            Type index = getIndex();
            for (IPrimalType type : getTypes()) {
                String name = type.getName();
                if (hasValidValue(name)) {
                    type.setString(index, valueMap.get(name));
                }
            }
        }

        public void genMap() {
            Config config = getConfig();
            String[] priorityOverride = config.getStringList();
            try {
                for (String s : priorityOverride) {
                    String[] spl = s.split("=");
                    if (spl.length == 2) {
                        String ore = spl[0];

                        String temp = spl[1];
                        valueMap.put(ore, temp);
                    }
                }
            } catch (Exception e) {
                throwInvalidConfig(config);
            }
        }

        public boolean hasValidValue(String string) {
            return valueMap.containsKey(string);
        }
    }

    public static class ValueIntFloat extends Value {

        private final TObjectIntMap<String> valueMap = new TObjectIntHashMap<>();
        private final TObjectFloatMap<String> value2Map = new TObjectFloatHashMap<>();

        public ValueIntFloat(IPrimalType[] types, Type type) {
            super(types, type);
        }

        public ValueIntFloat(IPrimalType[] types) {
            super(types);
        }

        public String[] getDefault() {
            Type index = getIndex();
            IPrimalType[] types = getTypes();
            List<String> list = new ArrayList<>();
            for (int i = 0; i < types.length; i++) {
                if (types[i].getInt(index) > 0 && types[i].getFloat(index) > 0.0F) {
                    list.add(types[i].getName() + "=" + types[i].getInt(index) + "," + types[i].getFloat(index));
                }
            }
            return list.toArray(new String[0]);
        }

        public void updateValues() {
            Type index = getIndex();
            for (IPrimalType type : getTypes()) {
                String name = type.getName();
                if (hasValidValue(name)) {
                    type.setInt(index, valueMap.get(name));
                    type.setFloat(index, value2Map.get(name));
                }
            }
        }

        public void genMap() {
            Config config = getConfig();
            String[] priorityOverride = config.getStringList();
            try {
                for (String s : priorityOverride) {
                    String[] spl = s.split("=");
                    if (spl.length == 2) {
                        String ore = spl[0];
                        String[] hs = spl[1].split(",");

                        int primary = Integer.parseInt(hs[0]);
                        float secondary = Float.parseFloat(hs[1]);
                        valueMap.put(ore, primary);
                        value2Map.put(ore, secondary);
                    }
                }
            } catch (Exception e) {
                throwInvalidConfig(config);
            }
        }

        public boolean hasValidValue(String string) {
            return valueMap.containsKey(string) && value2Map.containsKey(string);
        }
    }

    public static void throwInvalidConfig(Config config) {
        throw new IllegalStateException(
            "Config [" + config.name
                + "] in "
                + Primal.MODID
                + ".cfg is malformed."
                + "\n Delete the entry or fix it so it correctly apply in-game.");
    }
}
