package eddxample.toybox.mixins;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import eddxample.toybox.commands.TPS;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.crash.CrashReport;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ReportedException;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements ICommandSender, Runnable, IThreadListener, ISnooperInfo
{
	@Shadow private long currentTime;
	@Shadow private final ServerStatusResponse statusResponse = new ServerStatusResponse();
	@Shadow private boolean serverRunning;
	@Shadow private long timeOfLastWarning;
	@Shadow private String motd;
	@Shadow private static final Logger LOGGER = LogManager.getLogger();
	@Shadow public WorldServer[] worlds;
	@Shadow private boolean serverStopped;
	@Shadow private boolean serverIsRunning;
	
	@Shadow public static long getCurrentTimeMillis() {return 0;}
	@Shadow public abstract void applyServerIconToResponse(ServerStatusResponse response);
	@Shadow public abstract void tick();
	@Shadow public abstract void finalTick(CrashReport report);
	@Shadow public abstract CrashReport addServerInfoToCrashReport(CrashReport report);
	@Shadow public abstract void systemExitNow();
	@Shadow public abstract void stopServer();
	@Shadow public abstract File getDataDirectory();
	@Shadow public abstract boolean init() throws IOException;
	
	
	@Override
    public void run()
    {
        try
        {
            if (init())
            {
                currentTime = getCurrentTimeMillis();
                statusResponse.setServerDescription(new TextComponentString(motd));
                statusResponse.setVersion(new ServerStatusResponse.Version("1.12", 335));
                applyServerIconToResponse(statusResponse);

                while (serverRunning)
                {
                	TPS.updateServerTPS(currentTime);
                    long k = getCurrentTimeMillis();
                    long j = k - currentTime;

                    if (j > TPS.warn_time && currentTime - timeOfLastWarning >= 15000L)
                    {
                        LOGGER.warn("Can't keep up! Did the system time change, or is the server overloaded? Running {}ms behind, skipping {} tick(s)", Long.valueOf(j), Long.valueOf(j / 50L));
                        j = TPS.warn_time;
                        timeOfLastWarning = currentTime;
                    }

                    if (j < 0L)
                    {
                        LOGGER.warn("Time ran backwards! Did the system time change?");
                        j = 0L;
                    }

                    TPS.i += j;
                    currentTime = k;

                    if (worlds[0].areAllPlayersAsleep())
                    {
                        tick();
                        TPS.i = 0L;
                    }
                    else
                    {
                        while (TPS.i > 50L)
                        {
                        	TPS.i -= TPS.ms_per_tick;
                            tick();
                        }
                        if (TPS.isWarping)
                        {
                        	
                        	Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentTranslation("[TPS] Done!", new Object[0]), false);
                        	TPS.isWarping = false;
                        }
                    }

                    Thread.sleep(TPS.ms_per_tick);
                    serverIsRunning = true;
                }
            }
            else
            {
                finalTick((CrashReport)null);
            }
        }
        catch (Throwable throwable1)
        {
            LOGGER.error("Encountered an unexpected exception", throwable1);
            CrashReport crashreport = null;

            if (throwable1 instanceof ReportedException)
            {
                crashreport = addServerInfoToCrashReport(((ReportedException)throwable1).getCrashReport());
            }
            else
            {
                crashreport = addServerInfoToCrashReport(new CrashReport("Exception in server tick loop", throwable1));
            }

            File file1 = new File(new File(getDataDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");

            if (crashreport.saveToFile(file1))
            {
                LOGGER.error("This crash report has been saved to: {}", (Object)file1.getAbsolutePath());
            }
            else
            {
                LOGGER.error("We were unable to save this crash report to disk.");
            }

            finalTick(crashreport);
        }
        finally
        {
            try
            {
                serverStopped = true;
                stopServer();
            }
            catch (Throwable throwable)
            {
                LOGGER.error("Exception stopping the server", throwable);
            }
            finally
            {
                systemExitNow();
            }
        }
    }

}
