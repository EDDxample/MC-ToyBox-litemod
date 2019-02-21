package eddxample.toybox.commands;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class ChangeBiome extends CommandBase
{

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length != 7) throw new CommandException(getUsage(sender), new Object[0]);
    	
    	else
    	{
    		BlockPos pos = parseBlockPos(sender, args, 0, false);
            BlockPos pos1 = parseBlockPos(sender, args, 3, false);
            World world = server.getEntityWorld();
            
            if (!world.isBlockLoaded(pos) || !world.isBlockLoaded(pos1)) throw new CommandException("commands.fill.outOfWorld", new Object[0]);
            byte biome = (byte) Integer.parseInt(args[6]);
            
            if (Biome.getBiome(biome) == null) throw new CommandException("There's no such biome with this ID", new Object[0]);
            
            byte[] list = null;
            
            for (int i = Math.min(pos.getX(), pos1.getX()); i <= Math.max(pos.getX(), pos1.getX()); i++)
            {
            	for (int j = Math.min(pos.getZ(), pos1.getZ()); j <= Math.max(pos.getZ(), pos1.getZ()); j++)
            	{
            		list = world.getChunkFromChunkCoords(i >> 4, j >> 4).getBiomeArray();
            		list[(j & 15) << 4 | (i & 15)] = biome;
       			 	world.getChunkFromChunkCoords(i >> 4, j >> 4).setBiomeArray(list);
       			 	Minecraft.getMinecraft().world.getChunkFromChunkCoords(i >> 4, j >> 4).setBiomeArray(list);
            	}
            }
            notifyCommandListener(sender, this, "Changed to "+Biome.getBiome(biome).getBiomeName(), new Object[] {});
    	}
	}
	
	@Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        return (args.length > 0 && args.length <= 3) ? getTabCompletionCoordinate(args, 0, pos) : ((args.length > 3 && args.length <= 6) ? getTabCompletionCoordinate(args, 3, pos) : Collections.emptyList());
    }

	@Override
	public String getName()
	{
		return "changeBiome";
	}

	@Override
	public String getUsage(ICommandSender arg0)
	{
		return "/changeBiome <x1, y1, z1> <x2, y2, z2> <BiomeID>";
	}

}
