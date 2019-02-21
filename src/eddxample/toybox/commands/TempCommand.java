package eddxample.toybox.commands;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class TempCommand extends CommandBase
{
	static String village_color_scheme;
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length != 1) throw new CommandException(getUsage(sender), new Object[0]);
		else
		{
		}
		
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"village_colors"}) : (args.length == 2 ? getListOfStringsMatchingLastWord(args, new String[] {"_v", "_xy1", "_yz1", "_xz1", "_yz0"}) : Collections.emptyList());
	}

	@Override
	public String getName()
	{
		return "temp";
	}

	@Override
	public String getUsage(ICommandSender arg0)
	{
		return "/temp <stuff>";
	}
}
