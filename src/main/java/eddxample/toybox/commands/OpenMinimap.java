package eddxample.toybox.commands;

import eddxample.toybox.features.ChunkView;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class OpenMinimap extends CommandBase
{
    /**
     * Callback for when the command is executed
     */
    public void execute(MinecraftServer var1, ICommandSender sender, String[] args)
    {
    	if (ChunkView.globalInstance == null || !ChunkView.globalFrame.isVisible())
    	{
    		notifyCommandListener(sender, this, "[Minimap] Launching... ", new Object[0]);
    		ChunkView.globalInstance = new ChunkView();
    	}
    	else
    		notifyCommandListener(sender, this, "[Minimap] Can't open 2 minimaps :P", new Object[0]);
    }

	public String getName()                       {return "minimap";}
	public String getUsage(ICommandSender sender) {return "/minimap";}
    public int getRequiredPermissionLevel()       {return 2;}
}
