package engine;

import game.World;

import java.util.ArrayList;
import java.util.Collections;

import settings.Config;

public class PathFinder
{	
	private final static int MAX_STEPS = Config.MAX_STEPS;
	private final static int MAX_DEPTH = Config.MAX_DEPTH;
	private final static boolean DIAGONAL_MOVEMENT = Config.DIAGONAL_MOVEMENT;
	
	private World world;
	
	private ArrayList<Node> closedNodes = new ArrayList <Node> ();
	private SortedList openNodes = new SortedList ();
	private Node[][] nodes;
	
	public PathFinder ()
	{
	}
	public void setWorld (World world)
	{
		this.world = world;
	}
	public Path find (int startX, int startY, int targetX, int targetY, boolean log)
	{		
		try
		{
			if (targetX >= world.getMap().getGridWidth () || targetY >= world.getMap().getGridHeight ())
			{
				if (log) Log.out ("Targeted path point is outside of the map.");
				return null;
			}
			if (world.getMap().tileBlocked  (targetX, targetY))
			{
				if (log) Log.out ("Targeted path point is blocked for movement.");
				return null;
			}
			if (startX == targetX && startY == targetY)
			{
				if (log) Log.out ("You are already on the targeted point.");
				return null;
			}
			
			resetPathfinder (startX, startY, targetX, targetY);
			int depth = 0;
			
			while (openNodes.size () != 0 && depth < MAX_DEPTH)
			{
				Node currentNode = getFirstInopenNodes ();
				
				if (currentNode == nodes[targetX][targetY]) break;
				
				openNodes.remove (currentNode);
				closedNodes.add (currentNode);
				
				for (int x = -1; x < 2; x++)
				{
					for (int y = -1; y < 2; y++)
					{
						if (x == 0 && y == 0) continue;
						
						if (!DIAGONAL_MOVEMENT) 
						{
							if (x != 0 && y != 0) continue;
						}
						
						int neighborX = x + currentNode.x;
						int neighborY = y + currentNode.y;
						
						if (isValidLocation (startX, startY, neighborX, neighborY))
						{
							
							int diagonalCost = 0;
							if (x == -1 && y == -1 || x == 1 && y == -1 || x == -1 && y == 1 || x == 1 && y == 1)
							{
								if (world.getMap().tileBlocked(currentNode.x, currentNode.y-1)) continue;
								if (world.getMap().tileBlocked(currentNode.x-1, currentNode.y)) continue;
								if (world.getMap().tileBlocked(currentNode.x, currentNode.y+1)) continue;
								if (world.getMap().tileBlocked(currentNode.x+1, currentNode.y)) continue;
								
								diagonalCost = 1;
							}
							
							float nextStepCost = currentNode.cost + getTileCost (neighborX, neighborY);
							Node neighbourNode = nodes[neighborX][neighborY];
							
							if (nextStepCost < neighbourNode.cost)
							{
								if (openNodes.contains (neighbourNode))
								{
									openNodes.remove (neighbourNode);
								}
								if (closedNodes.contains (neighbourNode))
								{
									closedNodes.remove (neighbourNode);
								}
							}
							
							if (!openNodes.contains (neighbourNode) && !closedNodes.contains (neighbourNode))
							{
								neighbourNode.cost = nextStepCost + diagonalCost;
								depth = Math.max (depth, neighbourNode.setParent (currentNode));
								neighbourNode.heuristic = getGlobalCost (neighborX, neighborY, targetX, targetY) + getTileCost (neighborX, neighborY);
								openNodes.add (neighbourNode);
							}
						}
					}
				}
			}
		
			if(nodes[targetX][targetY].parent == null)
			{
				if (log) Log.out ("Reached maximum search depth (" + MAX_DEPTH + ").");
				return null;
			}
			else
			{
				Path path = createPath (new Path (), startX, startY, targetX, targetY);
				
				if (path.getLength () > MAX_STEPS)
				{
					if (log) Log.out ("Reached the maximum amount of steps (" + MAX_STEPS + ").");
					return null;
				}
				
				return path;
			}
		}
		catch (Exception e)
		{
			Failure.add("Failed to create path to target.", e);
			return null;
		}
	}
	
	public void fillNodes ()
	{
		nodes = new Node[world.getMap().getGridWidth ()][world.getMap().getGridHeight ()];
		
		for (int x = 0; x < world.getMap().getGridWidth (); x ++)
		{
			for (int y = 0; y < world.getMap().getGridHeight (); y ++)
			{
				nodes[x][y] = new Node (x, y);
			}
		}
	}
	private void resetPathfinder (int sx, int sy, int tx, int ty)
	{
		fillNodes ();
		nodes[sx][sy].cost = 0;
		nodes[tx][ty].depth = 0;
		nodes[tx][ty].parent = null;
		openNodes.clear ();
		closedNodes.clear ();
		
		openNodes.add (nodes[sx][sy]);
	}
	private Path createPath (Path path, int sx, int sy, int tx, int ty)
	{
		Node targetNode = nodes[tx][ty];
		while (targetNode != nodes[sx][sy])
		{
			path.prependStep (targetNode.x, targetNode.y);
			targetNode = targetNode.parent;
		}
		path.prependStep (sx, sy);
		
		return path;
	}
	private float getGlobalCost (int x, int y, int tx, int ty)
	{
		float dx = tx - x;
		float dy = ty - y;
		
		return (float) Math.sqrt((dx*dx)+(dy*dy));
	}
	private int getTileCost (int x, int y)
	{
		return world.getMap().getTileCost (x, y);
	}
	private Node getFirstInopenNodes ()
	{
		return openNodes.getFirst ();
	}
	private boolean isValidLocation (int sx, int sy, int x, int y)
	{
		boolean invalid = (x < 0 || y < 0 || x >= world.getMap().getGridWidth () || y >= world.getMap().getGridHeight ());
		
		if (!invalid && (sx != x || sy != y))
		{
			invalid = world.getMap().tileBlocked (x, y);
		}
		
		return !invalid;
	}	
	public boolean isBlocked (int x, int y)
	{
		return world.getMap().tileBlocked(x, y);
	}
	private class SortedList
	{
		private ArrayList<Node> list = new ArrayList<Node> ();
		
		public void add (Node node)
		{
			list.add (node);
			Collections.sort (list);
		}
		public int size ()
		{
			return list.size ();
		}
		public Node getFirst ()
		{
			return list.get (0);
		}
		public boolean contains (Node node)
		{
			return list.contains (node);
		}
		public void clear ()
		{
			list.clear ();
		}
		public void remove (Node node)
		{
			list.remove (node);
		}
	}
	private class Node implements Comparable<Object>
	{
		private int x;
		private int y;
		private float cost;
		private Node parent;
		private float heuristic;
		private int depth;
		
		private Node (int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		
		public int setParent (Node parent)
		{
			depth = parent.depth+1;
			this.parent = parent;
			return depth;
		}
		@Override
		public int compareTo (Object object)
		{
			Node node = (Node) object;
			
			float f = heuristic + cost;
			float of = node.heuristic + cost;
			
			if (f < of)
			{
				return -1;
			}
			else if (f > of)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
	}
}
