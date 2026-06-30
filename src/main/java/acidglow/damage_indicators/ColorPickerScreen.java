package acidglow.damage_indicators;

import java.util.function.IntConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ColorPickerScreen extends Screen {
    private static final int[] PALETTE = {
            0xFFFFFF, 0xFF5555, 0xFF9F1C, 0xFFFF55,
            0x55FF55, 0x4CFF72, 0x55FFFF, 0x40A9FF,
            0x5555FF, 0xAA55FF, 0xFF55FF, 0xAAAAAA,
            0x000000, 0xAA0000, 0x00AA00, 0x0000AA
    };

    private final Screen parent;
    private final Component label;
    private final IntConsumer onColorSelected;
    private int red;
    private int green;
    private int blue;
    private ChannelSlider redSlider;
    private ChannelSlider greenSlider;
    private ChannelSlider blueSlider;

    public ColorPickerScreen(Screen parent, Component label, int initialColor, IntConsumer onColorSelected) {
        super(Component.translatable("screen.acidglowsdamageindicators.color_picker"));
        this.parent = parent;
        this.label = label;
        this.onColorSelected = onColorSelected;
        this.red = (initialColor >> 16) & 0xFF;
        this.green = (initialColor >> 8) & 0xFF;
        this.blue = initialColor & 0xFF;
    }

    @Override
    protected void init() {
        int center = this.width / 2;
        int y = this.height / 5 + 42;
        this.redSlider = this.addRenderableWidget(new ChannelSlider(center - 120, y, 240, Component.translatable("screen.acidglowsdamageindicators.red"), this.red, value -> this.red = value));
        y += 28;
        this.greenSlider = this.addRenderableWidget(new ChannelSlider(center - 120, y, 240, Component.translatable("screen.acidglowsdamageindicators.green"), this.green, value -> this.green = value));
        y += 28;
        this.blueSlider = this.addRenderableWidget(new ChannelSlider(center - 120, y, 240, Component.translatable("screen.acidglowsdamageindicators.blue"), this.blue, value -> this.blue = value));
        y += 36;

        int startX = center - 110;
        for (int i = 0; i < PALETTE.length; i++) {
            int color = PALETTE[i];
            int x = startX + (i % 8) * 32;
            int swatchY = y + (i / 8) * 28;
            this.addRenderableWidget(new ColorSwatchButton(x, swatchY, 24, 20, Component.translatable("screen.acidglowsdamageindicators.palette_color"), color,
                    () -> this.setColor(color)));
        }

        this.addRenderableWidget(Button.builder(Component.translatable("screen.acidglowsdamageindicators.done"), button -> {
                    this.onColorSelected.accept(this.currentColor());
                    this.onClose();
                })
                .bounds(center - 125, this.height - 32, 120, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> this.onClose())
                .bounds(center + 5, this.height - 32, 120, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int center = this.width / 2;
        guiGraphics.drawCenteredString(this.font, this.title, center, 18, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, this.label, center, 38, 0xFFFFFF);

        int previewX = center - 40;
        int previewY = this.height / 5;
        guiGraphics.fill(previewX - 2, previewY - 2, previewX + 82, previewY + 26, 0xFFFFFFFF);
        guiGraphics.fill(previewX, previewY, previewX + 80, previewY + 24, 0xFF000000 | this.currentColor());
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    private int currentColor() {
        return (this.red << 16) | (this.green << 8) | this.blue;
    }

    private void setColor(int color) {
        this.red = (color >> 16) & 0xFF;
        this.green = (color >> 8) & 0xFF;
        this.blue = color & 0xFF;
        this.redSlider.setChannelValue(this.red);
        this.greenSlider.setChannelValue(this.green);
        this.blueSlider.setChannelValue(this.blue);
    }

    private static class ChannelSlider extends AbstractSliderButton {
        private final Component label;
        private final IntConsumer onChanged;

        ChannelSlider(int x, int y, int width, Component label, int initialValue, IntConsumer onChanged) {
            super(x, y, width, 20, Component.empty(), initialValue / 255.0D);
            this.label = label;
            this.onChanged = onChanged;
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal("")
                    .append(this.label)
                    .append(Component.literal(": " + this.channelValue())));
        }

        @Override
        protected void applyValue() {
            this.onChanged.accept(this.channelValue());
        }

        private int channelValue() {
            return (int) Math.round(this.value * 255.0D);
        }

        private void setChannelValue(int value) {
            this.setValue(value / 255.0D);
        }
    }
}
