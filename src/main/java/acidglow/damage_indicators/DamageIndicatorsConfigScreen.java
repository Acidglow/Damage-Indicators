package acidglow.damage_indicators;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class DamageIndicatorsConfigScreen extends Screen {
    private static final int CONTROL_WIDTH = 85;
    private static final int ROW_HEIGHT = 20;
    private static final int ROW_GAP = 30;

    private final @Nullable Screen parent;
    private int lowColor = Config.LOW_DAMAGE_COLOR.get();
    private int highColor = Config.HIGH_DAMAGE_COLOR.get();
    private int entityColor = Config.ENTITY_DAMAGE_COLOR.get();
    private int critColor = Config.CRIT_DAMAGE_COLOR.get();
    private ColorSwatchButton lowColorButton;
    private ColorSwatchButton highColorButton;
    private ColorSwatchButton entityColorButton;
    private ColorSwatchButton critColorButton;
    private EditBox textSize;

    public DamageIndicatorsConfigScreen(@Nullable Screen parent) {
        super(Component.translatable("screen.acidglowsdamageindicators.config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int center = this.width / 2;
        int labelX = Math.max(16, center - 150);
        int controlX = Math.min(center + 70, this.width - 16 - CONTROL_WIDTH);
        int labelWidth = Math.max(90, controlX - labelX - 12);
        int y = this.height / 6 + 10;

        this.addRenderableWidget(CycleButton.onOffBuilder(Config.SHOW_DAMAGE_INDICATORS.get())
                .create(center - 120, y, 240, 20, Component.translatable("screen.acidglowsdamageindicators.show"),
                        (button, value) -> Config.SHOW_DAMAGE_INDICATORS.set(value)));
        y += ROW_GAP;

        this.addLabel(labelX, y, labelWidth, "screen.acidglowsdamageindicators.low_color");
        this.lowColorButton = this.addColorSwatch(controlX, y, "screen.acidglowsdamageindicators.low_color", this.lowColor, color -> {
            this.lowColor = color;
            this.lowColorButton.setColor(color);
        });
        y += ROW_GAP;
        this.addLabel(labelX, y, labelWidth, "screen.acidglowsdamageindicators.high_color");
        this.highColorButton = this.addColorSwatch(controlX, y, "screen.acidglowsdamageindicators.high_color", this.highColor, color -> {
            this.highColor = color;
            this.highColorButton.setColor(color);
        });
        y += ROW_GAP;
        this.addLabel(labelX, y, labelWidth, "screen.acidglowsdamageindicators.crit_color");
        this.critColorButton = this.addColorSwatch(controlX, y, "screen.acidglowsdamageindicators.crit_color", this.critColor, color -> {
            this.critColor = color;
            this.critColorButton.setColor(color);
        });
        y += ROW_GAP;
        this.addLabel(labelX, y, labelWidth, "screen.acidglowsdamageindicators.entity_color");
        this.entityColorButton = this.addColorSwatch(controlX, y, "screen.acidglowsdamageindicators.entity_color", this.entityColor, color -> {
            this.entityColor = color;
            this.entityColorButton.setColor(color);
        });
        y += ROW_GAP;

        this.addLabel(labelX, y, labelWidth, "screen.acidglowsdamageindicators.text_size");
        this.textSize = new EditBox(this.font, controlX, y, CONTROL_WIDTH, ROW_HEIGHT, Component.translatable("screen.acidglowsdamageindicators.text_size"));
        this.textSize.setValue(String.format(java.util.Locale.ROOT, "%.2f", Config.TEXT_SIZE.get()));
        this.addRenderableWidget(this.textSize);

        this.addRenderableWidget(Button.builder(Component.translatable("screen.acidglowsdamageindicators.done"), button -> this.onClose())
                .bounds(center - 60, this.height - 32, 120, 20)
                .build());
    }

    private void addLabel(int x, int y, int width, String labelKey) {
        StringWidget label = new StringWidget(x, y, width, ROW_HEIGHT, Component.translatable(labelKey), this.font);
        label.setFGColor(0xFFFFFF);
        this.addRenderableWidget(label);
    }

    private ColorSwatchButton addColorSwatch(int x, int y, String labelKey, int value, java.util.function.IntConsumer onChanged) {
        Component label = Component.translatable(labelKey);
        ColorSwatchButton button = new ColorSwatchButton(x, y, CONTROL_WIDTH, ROW_HEIGHT, label, value,
                () -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(new ColorPickerScreen(this, label, valueFor(labelKey), onChanged));
                    }
                });
        this.addRenderableWidget(button);
        return button;
    }

    private int valueFor(String labelKey) {
        return switch (labelKey) {
            case "screen.acidglowsdamageindicators.low_color" -> this.lowColor;
            case "screen.acidglowsdamageindicators.high_color" -> this.highColor;
            case "screen.acidglowsdamageindicators.entity_color" -> this.entityColor;
            case "screen.acidglowsdamageindicators.crit_color" -> this.critColor;
            default -> 0xFFFFFF;
        };
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int center = this.width / 2;
        guiGraphics.drawCenteredString(this.font, this.title, center, 18, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        Config.LOW_DAMAGE_COLOR.set(this.lowColor);
        Config.HIGH_DAMAGE_COLOR.set(this.highColor);
        Config.ENTITY_DAMAGE_COLOR.set(this.entityColor);
        Config.CRIT_DAMAGE_COLOR.set(this.critColor);
        Config.TEXT_SIZE.set(parseDouble(this.textSize.getValue(), Config.TEXT_SIZE.get(), 0.5D, 4.0D));
        Config.SPEC.save();
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    private static double parseDouble(String value, double fallback, double min, double max) {
        try {
            return Math.max(min, Math.min(max, Double.parseDouble(value)));
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
