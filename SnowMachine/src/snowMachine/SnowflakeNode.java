package snowMachine;

import java.util.ArrayList;

public class SnowflakeNode
{
	private final ArrayList<SnowflakeNodeInstance> instances;
	private final int symmetricalDivisions;
	private final float branchOffset;
	private final float branchLength;
	private final float branchWidth;
	private final float spreadAngle;
	private ArrayList<SnowflakeNode> childNodes = new ArrayList<SnowflakeNode>();
	private SnowflakeNodeType type;
	private float tipWidth;

	/**
	 * Constructor for a snowflake node
	 *
	 * @param type
	 * @param symmetricalDivisions Number of symmetrical divisions this node represents
	 * @param branchOffset
	 * @param branchLength
	 * @param branchWidth
	 * @param spreadAngle
	 */
	public SnowflakeNode(SnowflakeNodeType type, int symmetricalDivisions, float branchOffset, float branchLength,
                         float branchWidth,
                         float spreadAngle) {
		this(type, symmetricalDivisions, branchOffset, branchLength, branchWidth, spreadAngle, new ArrayList<SnowflakeNodeInstance>());
	}

	/**
	 * Constructor for a snowflake node
	 * @param type
	 * @param symmetricalDivisions Number of symmetrical divisions this node represents
	 * @param branchOffset
	 * @param branchLength
	 * @param branchWidth
	 * @param spreadAngle
	 * @param instances
	 */
	public SnowflakeNode(SnowflakeNodeType type, int symmetricalDivisions, float branchOffset, float branchLength,
						 float branchWidth,
						 float spreadAngle,
						 ArrayList<SnowflakeNodeInstance> instances)
	{
		this.type = type;
		this.symmetricalDivisions = symmetricalDivisions;
		this.branchOffset = branchOffset;
		this.branchLength = branchLength;
		this.branchWidth = Math.max(0, branchWidth);
		this.spreadAngle = spreadAngle;
		this.instances = instances;
	}

	public int getSymmetricalDivisions()
	{
		return symmetricalDivisions;
	}

	public float getBranchLength()
	{
		return branchLength;
	}

	public float getBranchWidth()
	{
		return branchWidth;
	}

	public float getSpreadAngle()
	{
		return spreadAngle;
	}

	public SnowflakeNode addNode(SnowflakeNode snowflakeNode)
	{
		childNodes.add(snowflakeNode);
		return snowflakeNode;
	}

	public ArrayList<SnowflakeNode> getChildNodes()
	{
		return childNodes;
	}

	public SnowflakeNodeType getType()
	{
		return type;
	}

	public float getBranchOffset()
	{
		return branchOffset;
	}

	public void setTipWidth(float tipWidth)
	{
		this.tipWidth = tipWidth;
	}

	public float getTipWidth()
	{
		return tipWidth;
	}

	public ArrayList<SnowflakeNodeInstance> getInstances() {
		return instances;
	}

}
