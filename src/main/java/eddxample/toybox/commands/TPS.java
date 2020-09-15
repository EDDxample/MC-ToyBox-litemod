package eddxample.toybox.commands;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class TPS extends CommandBase
{
    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    /**
     * Callback for when the command is executed
     */
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
    	if (args.length > 2)
    	{
    		throw new CommandException(this.getUsage(sender), new Object[0]);
    	}
    	else if (args.length == 1)
    	{
    		if (!args[0].equalsIgnoreCase("warp") && !args[0].equalsIgnoreCase("server") && !args[0].equalsIgnoreCase("client"))
    		{
    			float f = parseFloat(args[1]);
    			serverTPS = f;
    			clientTPS = f;
    			notifyCommandListener(sender, this, "[TPS] Server = " + serverTPS);
                notifyCommandListener(sender, this, "[TPS] Client = " + clientTPS);
    			
    		}
    		else throw new CommandException(this.getUsage(sender));
    	}
    	else if (args.length == 2)
    	{
    		float f = parseFloat(args[1]);
    		if (f <= 0)
    		{
    			notifyCommandListener(sender, this, "[TPS]" + TextFormatting.RED + " Invalid number");
    			return;
    		}
    		if (args[0].equalsIgnoreCase("warp"))
    		{
    			i += (int)f * ms_per_tick;
    			notifyCommandListener(sender, this, "[TPS] Warping " + (int)f + " ticks...");
    			isWarping = true;
    		}
    		else if (args[0].equalsIgnoreCase("server"))
    		{
    			serverTPS = f;
    			notifyCommandListener(sender, this, "[TPS] Server = " + serverTPS);
    		}
    		else if (args[0].equalsIgnoreCase("client"))
    		{
    			clientTPS = f;
    			notifyCommandListener(sender, this, "[TPS] Client = " + clientTPS);
    		}
    		
    		else throw new CommandException(this.getUsage(sender));
    	}
    	else
    	{
            notifyCommandListener(sender, this, "[TPS] Server = " + serverTPS);
            notifyCommandListener(sender, this, "[TPS] Client = " + clientTPS);
    	}
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"warp", "client", "server"}) : Collections.<String>emptyList();
    }
    
	public String getName()
	{
		return "tps";
	}
	public String getUsage(ICommandSender sender)
	{
		return "/tps client/server/warp <ticks per second / ticks to warp>";
	}
    static float parseFloat(String s)
    {
        try { return (float)Double.parseDouble(s); }
        catch (NumberFormatException e){ return -1.0F; }
    }
	
	
    /* TPS STUFF */
    public static boolean isWarping;
	public static float serverTPS = 20.0F, clientTPS = 20.0F;
	public static long ms_per_tick = (long)(1000 / serverTPS),
					   warn_time = (long)(serverTPS * 200),
					   last_sleep = 0,
					   elapsed_ticks = 0,
					   i = 0;
	
	public static void updateServerTPS(long time)
	{
		elapsed_ticks = (int)((time - last_sleep) / ms_per_tick);
		last_sleep = time;
		ms_per_tick = (long)(1000 / serverTPS);
		warn_time = (long)(serverTPS * 200);
	}
}
