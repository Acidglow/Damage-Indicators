package acidglow.damage_indicators;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class ClientDamageIndicators {
    private static final DecimalFormat DAMAGE_FORMAT = new DecimalFormat("0.#");
    private static final int LIFETIME_TICKS = 32;
    private static final List<Indicator> INDICATORS = new ArrayList<>();

    private ClientDamageIndicators() {
    }

    public static void add(DamageIndicatorPayload payload) {
        if (!Config.SHOW_DAMAGE_INDICATORS.get()) {
            return;
        }

        float sideOffset = ((payload.entityId() * 31) % 100 - 50) / 100.0F;
        INDICATORS.add(new Indicator(
                Component.literal(DAMAGE_FORMAT.format(payload.amount())),
                payload.category(),
                payload.position(),
                sideOffset));
    }

    public static void tick() {
        Iterator<Indicator> iterator = INDICATORS.iterator();
        while (iterator.hasNext()) {
            Indicator indicator = iterator.next();
            indicator.age++;
            if (indicator.age >= LIFETIME_TICKS) {
                iterator.remove();
            }
        }
    }

    public static void clear() {
        INDICATORS.clear();
    }

    public static void render(GuiGraphics guiGraphics, float partialTick) {
        if (!Config.SHOW_DAMAGE_INDICATORS.get() || INDICATORS.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        Camera camera = minecraft.gameRenderer.getMainCamera();
        for (Indicator indicator : INDICATORS) {
            renderIndicator(guiGraphics, minecraft, camera, indicator, partialTick);
        }
    }

    private static void renderIndicator(GuiGraphics guiGraphics, Minecraft minecraft, Camera camera, Indicator indicator, float partialTick) {
        Font font = minecraft.font;
        float progress = Mth.clamp((indicator.age + partialTick) / (float) LIFETIME_TICKS, 0.0F, 1.0F);
        float alpha = Mth.clamp(1.0F - progress, 0.0F, 1.0F);
        Vec3 worldPosition = indicator.origin.add(indicator.sideOffset * progress, progress * 1.15D, 0.0D);
        ScreenPoint screenPoint = project(worldPosition, camera, guiGraphics.guiWidth(), guiGraphics.guiHeight(), minecraft.options.fov().get());
        if (screenPoint == null) {
            return;
        }

        int rgb = colorFor(indicator.category);
        int color = ARGB.color((int) (alpha * 255.0F), (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
        float scale = Config.TEXT_SIZE.get().floatValue();
        int textWidth = font.width(indicator.text);

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(screenPoint.x, screenPoint.y);
        guiGraphics.pose().scale(scale, scale);
        guiGraphics.drawString(font, indicator.text, Math.round(-textWidth / 2.0F), 0, color, true);
        guiGraphics.pose().popMatrix();
    }

    private static ScreenPoint project(Vec3 worldPosition, Camera camera, int guiWidth, int guiHeight, int fovDegrees) {
        Vec3 delta = worldPosition.subtract(camera.position());
        Vector3f cameraSpace = new Vector3f((float) delta.x, (float) delta.y, (float) delta.z);
        new Quaternionf(camera.rotation()).conjugate().transform(cameraSpace);

        float depth = -cameraSpace.z();
        if (depth <= 0.05F) {
            return null;
        }

        float focalLength = (float) (guiHeight / (2.0D * Math.tan(Math.toRadians(fovDegrees) / 2.0D)));
        float x = guiWidth / 2.0F + cameraSpace.x() * focalLength / depth;
        float y = guiHeight / 2.0F - cameraSpace.y() * focalLength / depth;

        if (x < -100.0F || x > guiWidth + 100.0F || y < -100.0F || y > guiHeight + 100.0F) {
            return null;
        }

        return new ScreenPoint(x, y);
    }

    private static int colorFor(DamageCategory category) {
        return switch (category) {
            case LOW -> Config.LOW_DAMAGE_COLOR.get();
            case HIGH -> Config.HIGH_DAMAGE_COLOR.get();
            case ENTITY -> Config.ENTITY_DAMAGE_COLOR.get();
            case CRITICAL -> Config.CRIT_DAMAGE_COLOR.get();
        };
    }

    private static final class Indicator {
        private final Component text;
        private final DamageCategory category;
        private final Vec3 origin;
        private final float sideOffset;
        private int age;

        private Indicator(Component text, DamageCategory category, Vec3 origin, float sideOffset) {
            this.text = text;
            this.category = category;
            this.origin = origin;
            this.sideOffset = sideOffset;
        }
    }

    private record ScreenPoint(float x, float y) {
    }
}
