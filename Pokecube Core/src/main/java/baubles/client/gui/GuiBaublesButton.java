package baubles.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;

public class GuiBaublesButton extends GuiButton {

	public GuiBaublesButton(int buttonId, int x, int y, int width, int height, String buttonText) {
		super(buttonId, x, y, width, height, buttonText);
	}
	
	public void drawButton(Minecraft mc, int xx, int yy)
    {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRendererObj;
            mc.getTextureManager().bindTexture(GuiPlayerExpanded.background);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = xx >= this.xPosition && yy >= this.yPosition && xx < this.xPosition + this.width && yy < this.yPosition + this.height;
            int k = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            
            
            if (k==1) {
            	this.drawTexturedModalRect(this.xPosition, this.yPosition, 200, 48, 10, 10);	
            } else {
            	this.drawTexturedModalRect(this.xPosition, this.yPosition, 210, 48, 10, 10);
            	this.drawCenteredString(fontrenderer, this.displayString, 
            			this.xPosition + 5, this.yPosition + this.height, 0xffffff);
            }
            
            this.mouseDragged(mc, xx, yy);
            
            
        }
    }

}
