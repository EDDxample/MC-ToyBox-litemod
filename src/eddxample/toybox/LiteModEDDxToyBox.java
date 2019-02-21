package eddxample.toybox;

import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.PostRenderListener;
import com.mumfrey.liteloader.ServerCommandProvider;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.modconfig.ConfigPanel;

import eddxample.toybox.commands.ChangeBiome;
import eddxample.toybox.commands.OpenMinimap;
import eddxample.toybox.commands.TPS;
import eddxample.toybox.commands.TempCommand;
import eddxample.toybox.features.ChunkView;
import eddxample.toybox.features.PistonHelper;
import eddxample.toybox.features.VillageMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ServerCommandManager;

public class LiteModEDDxToyBox implements ServerCommandProvider, PostRenderListener, Tickable, Configurable
{
	private Lock mutex = new ReentrantLock();
    
    
	@Override
	public void provideCommands(ServerCommandManager commandManager)
	{
		commandManager.registerCommand(new ChangeBiome());
		commandManager.registerCommand(new TempCommand());
		commandManager.registerCommand(new TPS());
		commandManager.registerCommand(new OpenMinimap());
	}
	
	@Override
	public void onPostRenderEntities(float partialTicks)
	{
        if (!Minecraft.getMinecraft().isIntegratedServerRunning())
        {
            return;
        }
        
        mutex.lock();
        
        try
        {
        	VillageMarker.RenderVillages(partialTicks);
        }
        
        finally
        {
        	mutex.unlock();
        }
	}
	
	@Override
	public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock)
	{
		if(clock && inGame)
		{
			mutex.lock();
        
			try
			{
				VillageMarker.genLists();
				ChunkView.update();
			}
        
			finally
			{
				mutex.unlock();
			}
		}
	}
	
	@Override
	public void onPostRender(float partialTicks)
	{
		if (!Minecraft.getMinecraft().isIntegratedServerRunning())
        {
            return;
        }
        
        mutex.lock();
        
        try
        {
        	PistonHelper.draw(partialTicks);
        }
        
        finally
        {
        	mutex.unlock();
        }
	}
	
	@Override
	public Class<? extends ConfigPanel> getConfigPanelClass()
	{
		return ToyBoxSettings.class;
	}
	
    @Override
    public String getName()
    {
        return "EDDxToyBox";
    }
    
    @Override
    public String getVersion()
    {
        return "0.1";
    }

	
	/* ====== UNUSED METHODS ====== */
	
	public LiteModEDDxToyBox() {}
    public void init(File configPath) {}
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {}
    
}
