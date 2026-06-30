package acidglow.damage_indicators;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue SHOW_DAMAGE_INDICATORS = BUILDER
            .comment("Show floating damage indicators above damaged entities.")
            .define("showDamageIndicators", true);

    public static final ModConfigSpec.IntValue LOW_DAMAGE_COLOR = BUILDER
            .comment("RGB color for low damage indicators.")
            .defineInRange("lowDamageColor", 0xFFFFFF, 0x000000, 0xFFFFFF);

    public static final ModConfigSpec.IntValue HIGH_DAMAGE_COLOR = BUILDER
            .comment("RGB color for high damage indicators.")
            .defineInRange("highDamageColor", 0x4CFF72, 0x000000, 0xFFFFFF);

    public static final ModConfigSpec.IntValue ENTITY_DAMAGE_COLOR = BUILDER
            .comment("RGB color for damage caused by non-player entities.")
            .defineInRange("entityDamageColor", 0xFF5555, 0x000000, 0xFFFFFF);

    public static final ModConfigSpec.IntValue CRIT_DAMAGE_COLOR = BUILDER
            .comment("RGB color for critical damage indicators.")
            .defineInRange("criticalDamageColor", 0xFF9F1C, 0x000000, 0xFFFFFF);

    public static final ModConfigSpec.DoubleValue TEXT_SIZE = BUILDER
            .comment("Floating damage text size multiplier.")
            .defineInRange("textSize", 1.0D, 0.5D, 4.0D);

    static final ModConfigSpec SPEC = BUILDER.build();
}
