package eddxample.toybox;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.mumfrey.liteloader.client.gui.GuiCheckbox;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;

import eddxample.toybox.features.PistonHelper;
import eddxample.toybox.features.VillageMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

public class ToyBoxSettings extends Gui implements ConfigPanel
{	
	private final static int SPACING = 16;
	private Minecraft mc;
	private GuiButton activeButton;
	
	/* GUI */
	private List<GuiButton> buttons, buttons_r;
	private GuiCheckbox doors     , villageSphere;
	private GuiCheckbox golems    , doorSphere;
	private GuiCheckbox population;
	
	private GuiCheckbox showToMove,     showToBreak;
	private GuiCheckbox showBasicInfo;
	
	public ToyBoxSettings()
	{
		mc = Minecraft.getMinecraft();
	}


	private void actionPerformed(GuiButton button)
	{
		if (button == doors)
		{
			VillageMarker.lines = !VillageMarker.lines;
			doors.checked = VillageMarker.lines;
		}
		else if (button == golems)
		{
			VillageMarker.golem = !VillageMarker.golem;
			golems.checked = VillageMarker.golem;
		}
		else if (button == population)
		{
			VillageMarker.population = !VillageMarker.population;
			population.checked = VillageMarker.population;
		}
		else if (button == villageSphere)
		{
			VillageMarker.village_radius = (VillageMarker.village_radius + 1) % VillageMarker.modes.length;
			button.displayString = "Village Sphere: " + VillageMarker.modes[VillageMarker.village_radius];
			villageSphere.checked = VillageMarker.village_radius != 0;
		}
		else if (button == doorSphere)
		{
			VillageMarker.door_radius = (VillageMarker.door_radius + 1) % VillageMarker.modes.length;
			button.displayString = "Door Sphere: " + VillageMarker.modes[VillageMarker.door_radius];
			doorSphere.checked = VillageMarker.door_radius != 0;
		}
		else if (button == showToMove)
		{
			PistonHelper.showToMove = !PistonHelper.showToMove;
			showToMove.checked = PistonHelper.showToMove;
		}
		else if (button == showToBreak)
		{
			PistonHelper.showToBreak = !PistonHelper.showToBreak;
			showToBreak.checked = PistonHelper.showToBreak;
		}
		else if (button == showBasicInfo)
		{
			PistonHelper.showBasicInfo = !PistonHelper.showBasicInfo;
			showBasicInfo.checked = PistonHelper.showBasicInfo;
		}
		

	}

	@Override
	public void drawPanel(ConfigPanelHost host, int mouseX, int mouseY, float partialTicks)
	{
		drawString(mc.fontRenderer, "Village Marker:", 5, 4, 0x4185D1);
		drawString(mc.fontRenderer, "Piston Info:", 5, 70, 0x4185D1);
		
		for (GuiButton button : buttons)
		{
			button.drawButton(mc, mouseX, mouseY, partialTicks);
		}
		for (GuiButton button : buttons_r)
		{
			button.drawButton(mc, mouseX, mouseY, partialTicks);
		}
	}

	@Override
	public int getContentHeight()
	{
		return SPACING * buttons.size();
	}

	@Override
	public String getPanelTitle()
	{
		return "EDDxample's ToyBox Settings";
	}

	@Override
	public void onPanelShown(ConfigPanelHost host)
	{
		int id = 0;
		int line = 1;

		buttons = new ArrayList<GuiButton>();
		buttons.add(doors = new GuiCheckbox(id++, 10, SPACING * line++, "Doors"));
		doors.checked = VillageMarker.lines;
		buttons.add(golems = new GuiCheckbox(id++, 10, SPACING * line++, "Golem Cage"));
		golems.checked = VillageMarker.golem;
		buttons.add(population = new GuiCheckbox(id++, 10, SPACING * line++, "Population Cage"));
		population.checked = VillageMarker.population;
		line = 5;
		buttons.add(showToMove = new GuiCheckbox(id++, 10, SPACING * line++, "Blocks To Move"));
		showToMove.checked = PistonHelper.showToMove;
		buttons.add(showBasicInfo = new GuiCheckbox(id++, 10, SPACING * line++, "Count Blocks"));
		showBasicInfo.checked = PistonHelper.showBasicInfo;
		
		line = 1;
		buttons_r = new ArrayList<GuiButton>();
		buttons_r.add(villageSphere = new GuiCheckbox(id++, 110, SPACING * line++, "Village Sphere: " + VillageMarker.modes[VillageMarker.village_radius]));
		villageSphere.checked = VillageMarker.village_radius != 0;
		buttons_r.add(doorSphere = new GuiCheckbox(id++, 110, SPACING * line++, "Door Sphere: " + VillageMarker.modes[VillageMarker.door_radius]));
		doorSphere.checked = VillageMarker.door_radius != 0;
		line = 5;
		buttons.add(showToBreak = new GuiCheckbox(id++, 110, SPACING * line++, "Blocks To Break"));
		showToBreak.checked = PistonHelper.showToBreak;
	}
	
	@Override
	public void mousePressed(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton)
	{
		for (GuiButton button : buttons)
		{
			if (button.mousePressed(mc, mouseX, mouseY))
			{
				activeButton = button;
				button.playPressSound(mc.getSoundHandler());
				actionPerformed(button);
			}
		}
		
		for (GuiButton button : buttons_r)
		{
			if (button.mousePressed(mc, mouseX, mouseY))
			{
				activeButton = button;
				button.playPressSound(mc.getSoundHandler());
				actionPerformed(button);
			}
		}
	}
	
	
	/* ====== UNUSED / UNKNOWN METHODS ====== */
	
	
	@Override
	public void keyPressed(ConfigPanelHost host, char keyChar, int keyCode)
	{
		if (keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_RETURN)
		{
			host.close();
		}
	}
	@Override
	public void mouseReleased(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton)
	{
		if (activeButton != null)
		{
			activeButton.mouseReleased(mouseX, mouseY);
			activeButton = null;
		}
	}
	@Override
	public void mouseMoved(ConfigPanelHost host, int mouseX, int mouseY) {}
	@Override
  	public void onTick(ConfigPanelHost host) {}
	@Override
	public void onPanelHidden() {}
	@Override
	public void onPanelResize(ConfigPanelHost host) {}
}