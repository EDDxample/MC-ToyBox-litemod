package eddxample.toybox.features;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChunkView extends JPanel implements ActionListener, MouseListener, MouseMotionListener, MouseWheelListener
{
	static JMenuItem itemGoto00, itemPlayerPos, itemPlayerAtNether, itemNether, itemOverWorld, itemEnd;
	
	static JMenuItem  toggleChunkType, toggleSlimeChunks, toggleUnloadPriority;
	static String mode = "ChunkType";
	
	static int origenX, origenY, clickX, clickY, chunkSize = 40, centerChunkX, centerChunkY;
	static boolean shouldDrag = false;
	
	static final Minecraft mc = Minecraft.getMinecraft();
	static long worldSeed;
	static int maxPriority = 0;
	
	public static World clientWorld, serverWorld = null;
	public static int actualDim = 0;
	public static JFrame globalFrame;
	public static ChunkView globalInstance;
	static String[] dims = {"(Nether)","(OverWorld)","(End)"};
	
	//FRAME STUFF
	public ChunkView()
	{
		clientWorld = mc.player.world;
		worldSeed = serverWorld.getSeed();
		actualDim = mc.player.dimension;
		globalFrame = new JFrame("Chunk Minimap "+ dims[actualDim + 1]);
		globalFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		globalFrame.setSize(new Dimension(600, 600));
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		setMenu(globalFrame);
		globalFrame.add(this);
		globalFrame.setVisible(true);
		
		origenX = (globalFrame.getWidth()/2) - 8;
		origenY = (globalFrame.getHeight()/2) - 20;
	}
	public void setMenu(JFrame frame)
    {
        JMenuBar mb = new JMenuBar();
        frame.setJMenuBar(mb);
        
        JMenu menu0 = new JMenu("Chunk Mode");
        mb.add(menu0);
        toggleChunkType = new JMenuItem("Show Chunk Type");
        toggleChunkType.addActionListener(this);
        menu0.add(toggleChunkType);
        toggleSlimeChunks = new JMenuItem("Show SlimeChunks");
        toggleSlimeChunks.addActionListener(this);
        menu0.add(toggleSlimeChunks);
        toggleUnloadPriority = new JMenuItem("Show Unload Priority");
        toggleUnloadPriority.addActionListener(this);
        menu0.add(toggleUnloadPriority);
        
        JMenu menu1 = new JMenu("Dimension");
        mb.add(menu1);
        itemNether = new JMenuItem("Nether");
        itemNether.addActionListener(this);
        menu1.add(itemNether);
        itemOverWorld = new JMenuItem("OverWorld");
        itemOverWorld.addActionListener(this);
        menu1.add(itemOverWorld);
        itemEnd = new JMenuItem("End");
        itemEnd.addActionListener(this);
        menu1.add(itemEnd);
        
        JMenu menu2 = new JMenu("Go to");
        mb.add(menu2);
        itemGoto00 = new JMenuItem("0 , 0");
        itemGoto00.addActionListener(this);
        menu2.add(itemGoto00);
        itemPlayerPos = new JMenuItem("PlayerPos");
        itemPlayerPos.addActionListener(this);
        menu2.add(itemPlayerPos);
        itemPlayerAtNether = new JMenuItem("PlayerAtNether");
        itemPlayerAtNether.addActionListener(this);
        menu2.add(itemPlayerAtNether);
    }
	static void changeDim(int dim)
	{
    	actualDim = dim;
    	serverWorld = Minecraft.getMinecraft().getIntegratedServer().getWorld(dim);
    	globalFrame.setTitle("Chunk Minimap "+ dims[dim + 1]);
	}
	public static void  update()
	{
		if (serverWorld == null) serverWorld = mc.getIntegratedServer().getWorld(actualDim);
		else if (globalInstance != null && globalInstance.isVisible())
		{
    		clientWorld = mc.player.world;
			globalInstance.repaint();
		}
	}
	
	//DRAW STUFF
	public void paintComponent(Graphics g1)
	{
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D) g1;
		Rectangle bounds = g.getClipBounds();
		
		//radius {radiusX, radiusY}
		int[] radius = getRadius(bounds);
		maxPriority = 0;
		
		EntityPlayer player = mc.player;
		
		//DRAW CHUNKS
		if (serverWorld != null)
		{
			for (int j = centerChunkY - radius[1]; j < centerChunkY + radius[1]; j += chunkSize)
			{
				for (int i = centerChunkX - radius[0]; i < centerChunkX + radius[0]; i += chunkSize)
				{
					//ChunkPos
					int x = (i - origenX) / chunkSize;
					int y = (j - origenY) / chunkSize;
					Color[] colors = getChunkColor(x, y, player);
				
					g.setColor(colors[0]);
					g.fillRect(i, j, chunkSize, chunkSize);
				
					g.setColor(colors[1]);
				
					String text = x+", "+y;
					if (mode.equals("UnloadPriority")) text =""+unloadPriority(x, y);
					
				
					int textWidth = g.getFontMetrics().stringWidth(text);
				
					if (textWidth < chunkSize && chunkSize > 10) g.drawString(text, i + chunkSize/2 - textWidth/2, j + 5 + chunkSize/2);

				}
			}
		}
		
		//0,0 LINES
		g.setColor(new Color(0xFF0000));
		g.drawLine(0, origenY, bounds.width, origenY);
		g.drawLine(0, origenY+1, bounds.width, origenY+1);
		g.setColor(new Color(0x0000FF));
		g.drawLine(origenX, 0, origenX, bounds.height);
		g.drawLine(origenX+1, 0, origenX+1, bounds.height);
	}
	
	//MATH
	static int[] getRadius(Rectangle bounds)
	{
		int midX = bounds.width/2,
		midY = bounds.height/2;	
		centerChunkX = midX - ((midX - origenX) % chunkSize);		
		centerChunkY = midY - ((midY - origenY) % chunkSize);
		int radiusX = (int) (Math.floor((double)(centerChunkX + (chunkSize*3))/chunkSize))*chunkSize;
		int radiusY = (int) (Math.floor((double)(centerChunkY + (chunkSize*4))/chunkSize))*chunkSize;
		return new int[] {radiusX, radiusY};
	}
	static float map(float value, float min1, float max1, float min2, float max2)
	{
		return min2 + (max2 - min2) * ((value - min1) / (max1 - min1));
	}
	
	//CHUNK STUFF
	public static Color[] getChunkColor(int chunkX, int chunkZ, EntityPlayer player)
	{
		Color chunkColor = new Color(0xc1c1c1);
		Color textColor = new Color(0xFFFFFF); 
		int x = (int) player.posX >> 4;
		int z = (int) player.posZ >> 4;
		boolean playerFlag = chunkX == x && chunkZ == z && player.dimension == actualDim;
		
		
		if (mode.equals("UnloadPriority"))
		{
			chunkColor = new Color(hashKeyColor(chunkX, chunkZ));
			
			if (playerFlag)//PLAYER CHUNK
			{
				textColor = new Color(0xFF00);//MAGENTA
			}
			else
			{
				textColor = Color.RED;
			}
		}
		else
		{
			textColor = new Color(1);
			
			if (mode.equals("ChunkType"))
			{
				
				if (playerFlag)//PLAYER CHUNK
				{
					chunkColor = new Color(0xFF00FF);//MAGENTA
				}
				else if (player.dimension == actualDim && clientWorld.getChunkFromChunkCoords(chunkX, chunkZ).wasTicked())//PLAYER LOADED CHUNKS
				{
					chunkColor = new Color(0x00FF00);//GREEN
				}
				else if (actualDim == 0 && serverWorld.isSpawnChunk(chunkX, chunkZ) && serverWorld.isBlockLoaded(new BlockPos(chunkX*16 + 8, 0, chunkZ*16 + 8)))//SPAWN CHUNKS
				{
					chunkColor = new Color(0x00FFFF);//CYAN
				}
				else if (serverWorld.isBlockLoaded(new BlockPos(chunkX*16 + 8, 0, chunkZ*16 + 8)))//LAZY CHUNKS
				{
					chunkColor = new Color(0xFF0000);//RED
				}
			}
			else if(mode.equals("SlimeChunk"))
				
			if (playerFlag)//PLAYER CHUNK
			{
				chunkColor = new Color(0xFF00FF);//MAGENTA
			}
			else if(isSlime(chunkX, chunkZ))
			{
				chunkColor = new Color(0x00CC00);
			}
		}
		if ((chunkX % 2 + chunkZ % 2) % 2 == 0 && !mode.equals("UnloadPriority"))
		{
			chunkColor = getDarker(chunkColor);
		}
		
		return new Color[] {chunkColor,textColor};
	}
	static Color getDarker(Color c)
	{
		int scale = 30;
	    int r = Math.max(0, (int) (c.getRed() -scale));
	    int g = Math.max(0, (int) (c.getGreen() -scale));
	    int b = Math.max(0, (int) (c.getBlue() -scale));
	    return new Color(r,g,b);
	}
	static int hashKeyColor(int chunkX, int chunkZ)
	{
		int priority = unloadPriority(chunkX, chunkZ);
		maxPriority = priority > maxPriority ? priority : maxPriority;
		int mappedP = (int) map(priority, 0, maxPriority, 0, 0xFF);
		return mappedP << 16 | mappedP << 8 | mappedP;
	}
	static int unloadPriority(int x, int z)
	{
		return (short)((x ^ z) & 0xFFFF) ^ ((x ^ z) >> 16);
	}
	static boolean isSlime(int chunkX, int chunkZ)
	{
        Random rnd = new Random(worldSeed + (long) (chunkX * chunkX * 0x4c1906) + (long) (chunkX * 0x5ac0db) + (long) (chunkZ * chunkZ) * 0x4307a7L + (long) (chunkZ * 0x5f24f) ^ 0x3ad8025f);
        return rnd.nextInt(10) == 0;
    }

    //EVENTS
    public void actionPerformed(ActionEvent e)
    {
        //MODE
        if (e.getSource() == toggleChunkType) 	   {mode = "ChunkType";}
        if (e.getSource() == toggleSlimeChunks)    {mode = "SlimeChunk"; changeDim(0);}
        if (e.getSource() == toggleUnloadPriority) {mode = "UnloadPriority";}
        
        //DIMENSION
    	if (e.getSource() == itemNether && actualDim != -1)   {changeDim(-1);}
    	if (e.getSource() == itemOverWorld && actualDim != 0) {changeDim(0);}
    	if (e.getSource() == itemEnd && actualDim != 1) {origenX = centerChunkX; origenY = centerChunkY; changeDim(1);}
    	
    	//GO TO
        if (e.getSource() == itemGoto00) {origenX = centerChunkX; origenY = centerChunkY;}
        if (e.getSource() == itemPlayerPos)
        {
        	BlockPos player = mc.player.getPosition();
        	origenX = centerChunkX - (player.getX() >> 4)*chunkSize;
        	origenY = centerChunkY - (player.getZ() >> 4)*chunkSize;
        }
        if (e.getSource() == itemPlayerAtNether && actualDim != -1)
        {
        	BlockPos player = mc.player.getPosition();
        	origenX = centerChunkX - (player.getX() >> 7)*chunkSize;
        	origenY = centerChunkY - (player.getZ() >> 7)*chunkSize;
        	changeDim(-1);
        }
        repaint();
    }
	public void mousePressed(MouseEvent e) 
	{
		shouldDrag = false;
		if (e.getButton() == 1)
		{
			shouldDrag = true;
			clickX = e.getX();
			clickY = e.getY();
		}
		else if (e.getButton() == 3)
		{
			int x = e.getX() - (e.getX() - origenX)%chunkSize;
			int y = e.getY() - (e.getY() - origenY)%chunkSize;
			System.out.println(x+" , "+y);
		}
	}
	public void mouseDragged(MouseEvent e)
	{
		if (shouldDrag)
		{
			origenX += e.getX() - clickX;
			origenY += e.getY() - clickY;
			clickX = e.getX();
			clickY = e.getY();
			repaint();
		}		
	}
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		double i = -e.getPreciseWheelRotation();
		int save = chunkSize;
		if (i < 0) chunkSize += chunkSize > 6 ? i : 0;
		if (i > 0) chunkSize += chunkSize < 60 ? i : 0;
		
		if (chunkSize != save)
		{
			origenX += (i > 0 ? -1 : 1)*((e.getX() - origenX)/chunkSize + 1);
			origenY += (i > 0 ? -1 : 1)*((e.getY() - origenY)/chunkSize + 1);
			repaint();
        }
	}
	
	
	public void mouseClicked(MouseEvent e) {}
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e)  {}
	public void mouseMoved(MouseEvent e)   {}
	private static final long serialVersionUID = 1L;
}