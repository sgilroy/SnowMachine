package snowMachine;

import controlP5.*;
import processing.core.PApplet;
import processing.core.PFont;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SnowMachine extends PApplet
{

	/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/54745*@* */
/* !do not delete the line above, required for linking your tweak if you re-upload */
	float branchLengthBase = 110;
	float branchLengthVariation = 50;
	float branchWidthBase = 7;
	float branchWidthVariation = 3;
	float branchLengthScaleBase = 0.5f;
	float branchLengthScaleVariation = 0.2f;
	float branchWidthScaleBase = 0.4f;
	float branchWidthScaleVariation = 0.1f;

	int maxRecursions = 5;       //ilo\u015b\u0107 recursion

	int primaryBranches = 6;
	private int seed;
	private float currentRecursions = 1;
	private int velocity = 1;
	private boolean isAnimated = true;
	private boolean isPolygonDrawingEnabled = true;

	ControlP5 cp5;

	Accordion accordion;
	private Slider currentRecursionsSlider;
	private Slider seedSlider;
	private int primaryBranchesBase = 6;
	private int primaryBranchesVariation = 0;
	private int subBranchesBase = 3;
	private int subBranchesVariation = 0;
	private boolean guiInitialized = false;
	private float angleBase = PI * 2 / 3;
	private float angleVariation = 0; // old value: HALF_PI;
	private float fillAlpha = 0.5f;
	private float strokeAlpha = 0.5f;
	private boolean autoSeed = true;
	private float recursionsPerMillisecond = 1f / 400;
	private int previousTime = 0;
	private static boolean fullScreen = false;
	private float fractionPolygons = 0.5f;

	public void setup()
	{
		if (fullScreen)
		{
			size(displayWidth, displayHeight);
		}
		else
		{
			size(900, 700);
		}

		background(0);
		smooth();
		frameRate(29);
		gui();
		createValuePropertySet();
	}

	void gui()
	{
		cp5 = new ControlP5(this);

		// change the default font to Verdana
		PFont p = createFont("Verdana", 10);
//		cp5.setFont(p);
		cp5.setAutoAddDirection(ControlP5Constants.VERTICAL);

		Group complexityGroup = cp5.addGroup("Complexity")
				.setBackgroundColor(color(0, 64));

		cp5.begin(complexityGroup, 10, 10);

		cp5.addToggle("Animate")
				.setValue(isAnimated())
				.plugTo(this, "setAnimated")
		;

		cp5.addToggle("Auto Seed")
				.setValue(isAutoSeed())
				.plugTo(this, "setAutoSeed")
				.linebreak()
		;

		cp5.addSlider("maxRecursions").setRange(1, 7);
		cp5.addSlider("recursionsPerMillisecond").setRange(1f / 10000, 1f / 50).setDecimalPrecision(4);

		currentRecursionsSlider = cp5.addSlider("currentRecursions")
				.setRange(0, maxRecursions);

		Group appearanceGroup = cp5.addGroup("Appearance")
				.setBackgroundColor(color(0, 64))
				;

		cp5.begin(appearanceGroup, 10, 10);

		cp5.addButton("resetSeed")
				.linebreak()
		;

		seedSlider = cp5.addSlider("seed")
				.setRange(0, 1000000)
		;

		cp5.addSlider("primaryBranchesBase")
				.setRange(1, 12)
		;

		cp5.addSlider("primaryBranchesVariation")
				.setRange(0, 12)
		;

		cp5.addSlider("subBranchesBase")
				.setRange(1, 12)
		;

		cp5.addSlider("subBranchesVariation").setRange(0, 12);

		cp5.addSlider("branchLengthBase").setRange(0, 200);
		cp5.addSlider("branchLengthVariation").setRange(0, 200);
		cp5.addSlider("branchWidthBase").setRange(0, 100);
		cp5.addSlider("branchWidthVariation").setRange(0, 100);

		cp5.addSlider("branchLengthScaleBase").setRange(-2, 2);
		cp5.addSlider("branchLengthScaleVariation").setRange(-2, 2);
		cp5.addSlider("branchWidthScaleBase").setRange(-2, 2);
		cp5.addSlider("branchWidthScaleVariation").setRange(-2, 2);

		cp5.addSlider("fractionPolygons").setRange(0, 1);
		Controller lastController = cp5.addToggle("Polygons").plugTo(this, "isPolygonDrawingEnabled").linebreak();

		appearanceGroup.setBackgroundHeight((int) (lastController.getPosition().y + lastController.getHeight()) + 20);

		Group fileGroup = cp5.addGroup("File")
				.setBackgroundColor(color(0, 64));

		cp5.begin(fileGroup, 10, 10);

		cp5.addButton("save")
				.setSize(80, 20)
		;

		Group renderGroup = cp5.addGroup("render")
				.setBackgroundColor(color(0, 64));

		cp5.begin(renderGroup, 10, 10);

		cp5.addSlider("fillAlpha").setRange(0, 1);
		cp5.addSlider("strokeAlpha").setRange(0, 1);

		accordion = cp5.addAccordion("acc")
				.setPosition(40, 40)
				.setWidth(250)
				.addItem(complexityGroup)
				.addItem(appearanceGroup)
				.addItem(renderGroup)
				.addItem(fileGroup)
		;

		cp5.mapKeyFor(new ControlKey()
		{
			public void keyEvent()
			{
				accordion.open(0, 1, 2);
			}
		}, 'o');
		cp5.mapKeyFor(new ControlKey()
		{
			public void keyEvent()
			{
				accordion.close(0, 1, 2);
			}
		}, 'c');
		cp5.mapKeyFor(new ControlKey()
		{
			public void keyEvent()
			{
				accordion.setWidth(300);
			}
		}, '1');
		cp5.mapKeyFor(new ControlKey()
		{
			public void keyEvent()
			{
				accordion.setPosition(0, 0);
				accordion.setItemHeight(190);
			}
		}, '2');
		cp5.mapKeyFor(new ControlKey()
		{
			public void keyEvent()
			{
				accordion.setCollapseMode(ControlP5.MULTI);
			}
		}, '3');
		cp5.mapKeyFor(new ControlKey()
		{
			public void keyEvent()
			{
				accordion.setCollapseMode(ControlP5.SINGLE);
			}
		}, '4');
		cp5.mapKeyFor(new ControlKey()
		{
			public void keyEvent()
			{
				cp5.remove("myGroup1");
			}
		}, '0');

		accordion.open(0, 1, 2);

		// use Accordion.MULTI to allow multiple group
		// to be open at a time.
		accordion.setCollapseMode(Accordion.MULTI);

		// when in SINGLE mode, only 1 accordion
		// group can be open at a time.
		// accordion.setCollapseMode(Accordion.SINGLE);

		cp5.loadProperties("settings.ser");
		guiInitialized = true;
	}

	public void controlEvent(ControlEvent event)
	{
//		println("got a control event from controller with id " + event.getController().getId());
		if (guiInitialized)
		{
			cp5.saveProperties("settings.ser", getValuePropertySetName());
		}
	}

	public void save()
	{
		cp5.saveProperties("snowflake-" + (new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss")).format(new Date()) + ".ser",
						   getValuePropertySetName());
	}

	private String getValuePropertySetName()
	{
		return "value";
	}

	private void createValuePropertySet()
	{
		ControllerProperties properties = cp5.getProperties();
		properties.addSet("value");
		for (ControllerProperty controllerProperty : properties.get().keySet())
		{
			if (controllerProperty.toString().endsWith("Value"))
			{
				properties.copy(controllerProperty, "value");
			}
		}
	}

	public void draw()
	{
		randomSeed(getSeed());
		background(0);
		fill(255, 255f * fillAlpha);
		stroke(255, 255f * strokeAlpha);
		primaryBranches = primaryBranchesBase + Math.round(random(primaryBranchesVariation));

		drawBranches(width - height / 2, height / 2, branchLengthBase + random(branchLengthVariation),
					 branchWidthBase + random(branchWidthVariation), primaryBranches, getCurrentRecursions());

		if (isAnimated())
		{
			int currentTime = millis();
			int timeDelta = currentTime - previousTime;
			int maximumTimeIncrement = (int)(1 / recursionsPerMillisecond);
			if (timeDelta > maximumTimeIncrement) timeDelta = maximumTimeIncrement;
			previousTime = currentTime;
			float newCurrentRecursions = getCurrentRecursions() + velocity * timeDelta * recursionsPerMillisecond;
			if (newCurrentRecursions > maxRecursions)
			{
				newCurrentRecursions = maxRecursions;
				velocity = -1;
			}
			if (newCurrentRecursions < 0)
			{
				newCurrentRecursions = 0;
				velocity = 1;
				if (autoSeed)
				{
					resetSeed();
				}
			}
			setCurrentRecursions(newCurrentRecursions);
		}
	}

	public void resetSeed()
	{
		setSeed(millis());
	}

	public void drawBranches(float x1, float y1, float primaryBranchLength, float primaryBranchWidth, int primaryBranches,
							 float inRecursions)
	{
		float levelFraction;
		levelFraction = getLevelFraction(inRecursions, 0);

		float spreadAngle = 0;
		SnowflakeNode rootNode = new SnowflakeNode(
				getSnowflakeNodeType(), primaryBranches,
				0, primaryBranchLength * levelFraction, primaryBranchWidth * levelFraction, spreadAngle);
		ArrayList<SnowflakeNode> currentLevelNodes = new ArrayList<SnowflakeNode>();
		currentLevelNodes.add(rootNode);
		for (int i = 0; i < (int)inRecursions; i++)
		{
			levelFraction = getLevelFraction(inRecursions, i + 1);

			ArrayList<SnowflakeNode> nextLevelNodes = new ArrayList<SnowflakeNode>();
			for (SnowflakeNode currentNode : currentLevelNodes)
			{
				float tipWidth = 0;
				int remainingBranches = primaryBranches;
				while (remainingBranches > 0)
				{
					int divisions = Math.min(remainingBranches,
											 (int) Math.floor(random(subBranchesVariation + 1) + subBranchesBase));
					remainingBranches -= divisions;

					boolean evenDivisions = divisions % 2 == 0;
					float branchOffset = evenDivisions ? currentNode.getBranchLength() * random(1f) : currentNode.getBranchLength();

					float branchWidth = currentNode.getBranchWidth() * (branchWidthScaleBase + random(
							branchWidthScaleVariation)) * levelFraction;

					if (!evenDivisions)
					{
						tipWidth = Math.max(tipWidth, branchWidth);
					}

					nextLevelNodes.add(currentNode.addNode(new SnowflakeNode(
							getSnowflakeNodeType(), divisions,
							branchOffset, currentNode.getBranchLength() * (branchLengthScaleBase + random(branchLengthScaleVariation)) * levelFraction,
							branchWidth,
							divisions == 1 ? 0 : random(angleBase, angleBase + angleVariation)
					)));
				}
				currentNode.setTipWidth(tipWidth);
			}
			currentLevelNodes = nextLevelNodes;
		}

		drawNodeBranchesRecursive(x1, y1, rootNode, (int)Math.ceil(inRecursions));
	}

	private float getLevelFraction(float inRecursions, int i)
	{
		float levelFraction = inRecursions - i;
		if (levelFraction > 1) levelFraction = 1;
		return levelFraction;
	}

	private SnowflakeNodeType getSnowflakeNodeType()
	{
		int i = Math.round(fractionPolygons * 0.5f + random(0.5f));
		if (isPolygonDrawingEnabled)
		{
			return SnowflakeNodeType.values()[i];
		}
		else
			return SnowflakeNodeType.RECTANGLES;
	}

	private void drawNodeBranchesRecursive(float branchX, float branchY, SnowflakeNode node, int inRecursions)
	{
		inRecursions--;
		if (node.getSymmetricalDivisions() != 0)
		{
			float alpha;
			if (node.getSpreadAngle() == 0)
				alpha = (TWO_PI) / (node.getSymmetricalDivisions());
			else
				alpha = (node.getSpreadAngle()) / (node.getSymmetricalDivisions() - 1);

			pushMatrix();
			translate(branchX, branchY);
			if (node.getType() == SnowflakeNodeType.POLYGON)
			{
				polygon(getPolygonSides(node), 0, 0, node.getBranchLength());
			}
			rotate(-(node.getSpreadAngle()) / 2);
			for (int i = 0; i < node.getSymmetricalDivisions(); i++)
			{
				if (node.getType() == SnowflakeNodeType.RECTANGLES)
				{
//					rect(0, -node.getBranchWidth() / 2, node.getBranchLength(), node.getBranchWidth());

					beginShape();
					vertex(0, -node.getBranchWidth() / 2);
					vertex(node.getBranchLength(), -node.getTipWidth() / 2);
					vertex(node.getBranchLength(), +node.getTipWidth() / 2);
					vertex(0, +node.getBranchWidth() / 2);
					endShape(CLOSE);
				}

				for (SnowflakeNode childNode : node.getChildNodes())
				{
					drawNodeBranchesRecursive(childNode.getBranchOffset(), 0, childNode, inRecursions);
				}
				rotate(alpha);
			}
			popMatrix();
		}
	}

	private int getPolygonSides(SnowflakeNode node)
	{
//		return node.getSymmetricalDivisions();
		return primaryBranches;
	}

	void polygon(int n, float cx, float cy, float r)
	{
		polygon(n, cx, cy, r * 2.0f, r * 2.0f, 0.0f);
	}

	void polygon(int n, float cx, float cy, float w, float h, float startAngle)
	{
		if (n > 2)
		{
			float angle = TWO_PI / n;

			/* The horizontal "radius" is one half the width;
					 the vertical "radius" is one half the height */
			w = w / 2.0f;
			h = h / 2.0f;

			beginShape();
			for (int i = 0; i < n; i++)
			{
				vertex(cx + w * cos(startAngle + angle * i),
					   cy + h * sin(startAngle + angle * i));
			}
			endShape(CLOSE);
		}
	}

	static public void main(String[] args)
	{
		if (args.length > 0 && args[0].equals("--present"))
		{
			fullScreen = true;
			PApplet.main(new String[]{"--present", "snowMachine.SnowMachine"});
		}
		else
		{
			PApplet.main(new String[]{"snowMachine.SnowMachine"});
		}

	}

	public boolean isAnimated()
	{
		return isAnimated;
	}

	public void setAnimated(boolean animated)
	{
		isAnimated = animated;
		previousTime = millis();
	}

	public float getCurrentRecursions()
	{
		return currentRecursions;
	}

	public void setCurrentRecursions(float currentRecursions)
	{
		this.currentRecursions = currentRecursions;
		if (currentRecursionsSlider != null)
		{
			currentRecursionsSlider.setValue(currentRecursions);
		}
	}

	public int getSeed()
	{
		return seed;
	}

	public void setSeed(int seed)
	{
		this.seed = seed;
		if (seedSlider != null)
		{
			seedSlider.setValue(seed);
		}
	}

	public boolean isAutoSeed()
	{
		return autoSeed;
	}

	public void setAutoSeed(boolean autoSeed)
	{
		this.autoSeed = autoSeed;
	}
}
