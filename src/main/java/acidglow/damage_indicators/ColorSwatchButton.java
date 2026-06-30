package acidglow.damage_indicators;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ColorSwatchButton extends AbstractWidget {
    private final Runnable onPress;
    private int color;

    public ColorSwatchButton(int x, int y, int width, int height, Component message, int color, Runnable onPress) {
        super(x, y, width, height, message);
        this.color = color & 0xFFFFFF;
        this.onPress = onPress;
    }

    public void setColor(int color) {
        this.color = color & 0xFFFFFF;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int border = this.isHoveredOrFocused() ? 0xFFFFFFFF : 0xFFAAAAAA;
        guiGraphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), border);
        guiGraphics.fill(this.getX() + 2, this.getY() + 2, this.getRight() - 2, this.getBottom() - 2, 0xFF000000 | this.color);
        if (this.isHovered()) {
            guiGraphics.requestCursor(CursorTypes.POINTING_HAND);
        }
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean isDoubleClick) {
        this.onPress.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
