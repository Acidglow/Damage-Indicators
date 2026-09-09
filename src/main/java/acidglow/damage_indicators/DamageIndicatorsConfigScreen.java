package acidglow.damage_indicators;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class DamageIndicatorsConfigScreen extends Screen {
    private static final int CONTROL_WIDTH = 85;
    private static final int NORMAL_ROW_HEIGHT = 20;
    private static final int COMPACT_ROW_HEIGHT = 16;
    private static final int CONTROL_COUNT = 8;

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
        boolean compact = this.height < 280;
        int rowHeight = compact ? COMPACT_ROW_HEIGHT : NORMAL_ROW_HEIGHT;
        int rowGap = rowHeight + (compact ? 2 : 5);
        int contentHeight = CONTROL_COUNT * rowHeight + (CONTROL_COUNT - 1) * (rowGap - rowHeight);
        int y = Math.max(40, Math.min(this.height / 6 + 10, this.height - 52 - contentHeight));

        this.addRenderableWidget(CycleButton.onOffBuilder(Config.SHOW_DAMAGE_INDICATORS.get())
                .create(center - 120, y, 240, rowHeight, Component.translatable("screen.acidglowsdamageindicators.show"),
                        (button, value) -> Config.SHOW_DAMAGE_INDICATORS.set(value)));
        y += rowGap;

        this.addRenderableWidget(CycleButton.onOffBuilder(Config.USE_CUSTOM_FONT.get())
                .create(center - 120, y, 240, rowHeight, Component.translatable("screen.acidglowsdamageindicators.custom_font"),
                        (button, value) -> Config.USE_CUSTOM_FONT.set(value)));
        y += rowGap;

        this.addRenderableWidget(CycleButton.onOffBuilder(Config.REQUIRE_LINE_OF_SIGHT.get())
                .create(center - 120, y, 240, rowHeight, Component.translatable("screen.acidglowsdamageindicators.line_of_sight"),
                        (button, value) -> Config.REQUIRE_LINE_OF_SIGHT.set(value)));
        y += rowGap;

        this.addLabel(labelX, y, labelWidth, rowHeight, "screen.acidglowsdamageindicators.low_color");
        this.lowColorButton = this.addColorSwatch(controlX, y, rowHeight, "screen.acidglowsdamageindicators.low_color", this.lowColor, color -> {
            this.lowColor = color;
            this.lowColorButton.setColor(color);
        });
        y += rowGap;
        this.addLabel(labelX, y, labelWidth, rowHeight, "screen.acidglowsdamageindicators.high_color");
        this.highColorButton = this.addColorSwatch(controlX, y, rowHeight, "screen.acidglowsdamageindicators.high_color", this.highColor, color -> {
            this.highColor = color;
            this.highColorButton.setColor(color);
        });
        y += rowGap;
        this.addLabel(labelX, y, labelWidth, rowHeight, "screen.acidglowsdamageindicators.crit_color");
        this.critColorButton = this.addColorSwatch(controlX, y, rowHeight, "screen.acidglowsdamageindicators.crit_color", this.critColor, color -> {
            this.critColor = color;
            this.critColorButton.setColor(color);
        });
        y += rowGap;
        this.addLabel(labelX, y, labelWidth, rowHeight, "screen.acidglowsdamageindicators.entity_color");
        this.entityColorButton = this.addColorSwatch(controlX, y, rowHeight, "screen.acidglowsdamageindicators.entity_color", this.entityColor, color -> {
            this.entityColor = color;
            this.entityColorButton.setColor(color);
        });
        y += rowGap;

        this.addLabel(labelX, y, labelWidth, rowHeight, "screen.acidglowsdamageindicators.text_size");
        this.textSize = new EditBox(this.font, controlX, y, CONTROL_WIDTH, rowHeight, Component.translatable("screen.acidglowsdamageindicators.text_size"));
        this.textSize.setValue(String.format(java.util.Locale.ROOT, "%.2f", Config.TEXT_SIZE.get()));
        this.addRenderableWidget(this.textSize);

        this.addRenderableWidget(Button.builder(Component.translatable("screen.acidglowsdamageindicators.done"), button -> this.onClose())
                .bounds(center - 60, this.height - 32, 120, 20)
                .build());
    }

    private void addLabel(int x, int y, int width, int height, String labelKey) {
        StringWidget label = new StringWidget(x, y, width, height, Component.translatable(labelKey), this.font);
        label.setFGColor(0xFFFFFF);
        this.addRenderableWidget(label);
    }

    private ColorSwatchButton addColorSwatch(int x, int y, int height, String labelKey, int value, java.util.function.IntConsumer onChanged) {
        Component label = Component.translatable(labelKey);
        ColorSwatchButton button = new ColorSwatchButton(x, y, CONTROL_WIDTH, height, label, value,
                () -> {
                    if (this.minecraft != null) {
                        this.minecraft.gui.setScreen(new ColorPickerScreen(this, label, valueFor(labelKey), onChanged));
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
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        int center = this.width / 2;
        guiGraphics.centeredText(this.font, this.title, center, 18, 0xFFFFFF);
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
            this.minecraft.gui.setScreen(this.parent);
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
