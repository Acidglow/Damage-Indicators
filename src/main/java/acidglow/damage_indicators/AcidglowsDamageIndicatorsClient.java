package acidglow.damage_indicators;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = AcidglowsDamageIndicators.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = AcidglowsDamageIndicators.MODID, value = Dist.CLIENT)
public class AcidglowsDamageIndicatorsClient {
    static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(AcidglowsDamageIndicators.MODID, "damage_indicators"));
    static final KeyMapping OPEN_CONFIG_KEY = new KeyMapping(
            "key.acidglowsdamageindicators.open_config",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_P,
            KEY_CATEGORY);

    public AcidglowsDamageIndicatorsClient(IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(this::registerKeyMappings);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        NeoForge.EVENT_BUS.register(ClientEvents.class);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        AcidglowsDamageIndicators.LOGGER.info("Loaded {} client", AcidglowsDamageIndicators.MODID);
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(KEY_CATEGORY);
        event.register(OPEN_CONFIG_KEY);
    }
}
