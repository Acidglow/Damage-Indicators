package acidglow.damage_indicators;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(AcidglowsDamageIndicators.MODID)
public class AcidglowsDamageIndicators {
    public static final String MODID = "acidglowsdamageindicators";

    public AcidglowsDamageIndicators(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::registerPayloads);
        NeoForge.EVENT_BUS.register(new DamageEventHandler());
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(DamageIndicatorPayload.TYPE, DamageIndicatorPayload.STREAM_CODEC, DamageIndicatorPayload::handleClient);
    }
}
