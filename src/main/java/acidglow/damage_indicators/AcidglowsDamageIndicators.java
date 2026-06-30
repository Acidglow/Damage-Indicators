package acidglow.damage_indicators;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(AcidglowsDamageIndicators.MODID)
public class AcidglowsDamageIndicators {
    public static final String MODID = "acidglowsdamageindicators";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AcidglowsDamageIndicators(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayloads);
        NeoForge.EVENT_BUS.register(new DamageEventHandler());
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Loaded {}", MODID);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(DamageIndicatorPayload.TYPE, DamageIndicatorPayload.STREAM_CODEC, DamageIndicatorPayload::handleClient);
    }
}
