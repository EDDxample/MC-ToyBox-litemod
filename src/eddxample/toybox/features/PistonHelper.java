package eddxample.toybox.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.BlockPos;

public class PistonHelper
{
	/* Settings */
	public static boolean showToMove, showToBreak, showBasicInfo;
	
	public static final String nope = "\u00a71\u00a74NOPE", gold = "\u00a76";
	
	public static boolean validState, activated, extending;
	public static BlockPos pistonPos;
	public static BlockPos[] tobreak, tomove;
	
	public static boolean isNecessary()
	{
		return showToMove || showToBreak || showBasicInfo;
	}
	
	public static void set(BlockPos posIn, BlockPos[] btm, BlockPos[] btb, boolean isValid, boolean _extending)
	{
		pistonPos = posIn;
		tomove = btm;
		tobreak = btb;
		validState = isValid;
		extending = _extending;
	}
	
	public static void draw(float partialTicks)
	{
		if (!isNecessary()) return;
		
		if (activated)
		{
			final EntityPlayerSP player = Minecraft.getMinecraft().player;
	        final double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
	        final double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
	        final double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
	        final RenderManager rm = Minecraft.getMinecraft().getRenderManager();
	        BlockPos pos;
	        
	        if (validState)
			{
	        	int count = 0;
				if (showToBreak)
				{
					for (int i = 0; i < 12; i++)
					{
						pos = tobreak[11 - i];
						if (pos != null)
						{
							count++;
							EntityRenderer.drawNameplate(Minecraft.getMinecraft().fontRenderer, "\u00a7c"+count, (float)(pos.getX() + 0.5f - d0), (float)(pos.getY() + 0.5f - d1), (float)(pos.getZ() + 0.5f - d2), 0, rm.playerViewY, rm.playerViewX, rm.options.thirdPersonView == 2, false);
						}
			
					}
				}
				int moved = - count;
				if (showToMove || showBasicInfo)
				{
					for (int i = 0; i < 12; i++)
					{
						pos = tomove[11 - i];
						if (pos != null)
						{
							count++;
							if (showToMove) EntityRenderer.drawNameplate(Minecraft.getMinecraft().fontRenderer, ""+count, (float)(pos.getX() + 0.5f - d0), (float)(pos.getY() + 0.5f - d1), (float)(pos.getZ() + 0.5f - d2), 0, rm.playerViewY, rm.playerViewX, rm.options.thirdPersonView == 2, false);
						}
					}
				}
	        	if (showBasicInfo)
	        	{
	        		moved += count;
	        		pos = pistonPos;
	        		EntityRenderer.drawNameplate(Minecraft.getMinecraft().fontRenderer, gold + (extending ? "Pushes" : "Pulls"), (float)(pos.getX() + 0.5f - d0), (float)(pos.getY() + 0.8f - d1), (float)(pos.getZ() + 0.5f - d2), 0, rm.playerViewY, rm.playerViewX, rm.options.thirdPersonView == 2, false);
	        		EntityRenderer.drawNameplate(Minecraft.getMinecraft().fontRenderer, gold + (moved < 0 ? 0 : moved)         , (float)(pos.getX() + 0.5f - d0), (float)(pos.getY() + 0.5f - d1), (float)(pos.getZ() + 0.5f - d2), 0, rm.playerViewY, rm.playerViewX, rm.options.thirdPersonView == 2, false);
	        		EntityRenderer.drawNameplate(Minecraft.getMinecraft().fontRenderer, gold + "Blocks"                        , (float)(pos.getX() + 0.5f - d0), (float)(pos.getY() + 0.2f - d1), (float)(pos.getZ() + 0.5f - d2), 0, rm.playerViewY, rm.playerViewX, rm.options.thirdPersonView == 2, false);
	        	}
			}
			else
			{
				EntityRenderer.drawNameplate(Minecraft.getMinecraft().fontRenderer, nope, (float)(pistonPos.getX() + 0.5f - d0), (float)(pistonPos.getY() + 0.5f - d1), (float)(pistonPos.getZ() + 0.5f - d2), 0, rm.playerViewY, rm.playerViewX, rm.options.thirdPersonView == 2, false);
			}
		}
	}
}
