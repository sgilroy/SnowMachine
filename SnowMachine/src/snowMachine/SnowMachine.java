package snowMachine;

import controlP5.*;
import processing.core.PApplet;
import processing.core.PFont;
import processing.pdf.*;

import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SnowMachine extends PApplet
{

	/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/54745*@* */
/* !do not delete the line above, required for linking your tweak if you re-upload */
	float branchLengthBase = 195;
	float branchLengthVariation = 14;
	float branchWidthBase = 2;
	float branchWidthVariation = 18;
	float branchLengthScaleBase = 0.42f;
	float branchLengthScaleVariation = -0.22f;
	float branchWidthScaleBase = 5.7f;
	float branchWidthScaleVariation = -1.03f;

	int maxRecursions = 3;       // recursion

	int primaryBranches = 6;
	private int seed;
	private float currentRecursions = 3;
	private int velocity = 1;
	private boolean isAnimated = true;
	private boolean isPolygonDrawingEnabled = true;

	ControlP5 cp5;

	Accordion accordion;
	private Slider currentRecursionsSlider;
	private Slider seedSlider;
	private int primaryBranchesBase = 6;
	private int primaryBranchesVariation = 0;
	private int subBranchesBase = 0;
	private int subBranchesVariation = 0;
	private int midBranchesBase = 2;
	private int midBranchesVariation = 6;

	private boolean guiInitialized = false;
	private float angleBase = PI * 2 / 3;
	private float angleVariation = 0; // old value: HALF_PI;
	private float fillAlpha = 0.3f;
	private float strokeAlpha = 0;
	private boolean autoSeed = true;
	private boolean autoSave = true;
	private boolean autoPdf = false;
	private float recursionsPerMillisecond = 1f / 400;
	private int animationTime = 0;
	private int previousTime = 0;
	private static boolean fullScreen = false;
	private float fractionPolygons = 0.3f;
	private boolean linearAnimation = false;

	private boolean useGrid = false;
	private int gridRows = 3;
	private int gridColumns = 3;

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
		;

		cp5.addToggle("Auto Save")
				.setValue(isAutoSave())
				.plugTo(this, "setAutoSave")
		;

		cp5.addToggle("Auto PDF")
				.setValue(isAutoPdf())
				.plugTo(this, "setAutoPdf")
				.linebreak()
		;

		final int maxRecursionsLimit = 7;
		cp5.addSlider("maxRecursions").setRange(1, maxRecursionsLimit).setCaptionLabel("Recursions Max");
		cp5.addSlider("recursionsPerMillisecond").setRange(1f / 10000, 1f / 50).setDecimalPrecision(4).setCaptionLabel("  Speed");

		currentRecursionsSlider = cp5.addSlider("currentRecursions")
				.setRange(0, maxRecursionsLimit).setCaptionLabel("  Current");

		Group appearanceGroup = cp5.addGroup("Appearance")
				.setBackgroundColor(color(0, 64))
				;

		cp5.begin(appearanceGroup, 10, 10);

		cp5.addButton("resetSeed")
				.setCaptionLabel("Reset Seed")
		;

		cp5.addButton("decrementSeed")
				.setCaptionLabel("<")
		;

		cp5.addButton("incrementSeed")
				.setCaptionLabel(">").linebreak()
		;

		seedSlider = cp5.addSlider("seed")
				.setRange(0, 1000000)
		;

		cp5.addSlider("primaryBranchesBase")
				.setRange(1, 12).setCaptionLabel("Primary Branches")
		;

		cp5.addSlider("primaryBranchesVariation")
				.setRange(0, 12).setCaptionLabel("  Variation")
		;

		cp5.addSlider("subBranchesBase")
				.setRange(0, 12).setCaptionLabel("Sub Branches")
		;

		cp5.addSlider("subBranchesVariation").setRange(0, 12).setCaptionLabel("  Variation");

		cp5.addSlider("midBranchesBase")
				.setRange(0, 12).setCaptionLabel("Mid Branches")
		;

		cp5.addSlider("midBranchesVariation").setRange(0, 12).setCaptionLabel("  Variation");

		cp5.addSlider("branchLengthBase").setRange(0, 400).setCaptionLabel("Length");
		cp5.addSlider("branchLengthVariation").setRange(0, 400).setCaptionLabel("  Variation");
		cp5.addSlider("branchWidthBase").setRange(0, 100).setCaptionLabel("Width");
		cp5.addSlider("branchWidthVariation").setRange(0, 100).setCaptionLabel("  Variation");

		cp5.addSlider("branchLengthScaleBase").setRange(-2, 2).setCaptionLabel("Length Scaling");
		cp5.addSlider("branchLengthScaleVariation").setRange(-2, 2).setCaptionLabel("  Variation");
		cp5.addSlider("branchWidthScaleBase").setRange(-2, 8).setCaptionLabel("Width Scaling");
		cp5.addSlider("branchWidthScaleVariation").setRange(-2, 2).setCaptionLabel("  Variation");

		cp5.addSlider("fractionPolygons").setRange(0, 1).setCaptionLabel("% Polygons");
		Controller lastController = cp5.addToggle("Polygons").plugTo(this, "isPolygonDrawingEnabled").linebreak();

		appearanceGroup.setBackgroundHeight((int) (lastController.getPosition().y + lastController.getHeight()) + 20);

		Group fileGroup = cp5.addGroup("File")
				.setBackgroundColor(color(0, 64));

		cp5.begin(fileGroup, 10, 10);

		cp5.addButton("save")
				.setSize(80, 20)
		;

		cp5.addButton("pdf")
				.setSize(80, 20)
		;

		Group renderGroup = cp5.addGroup("render")
				.setBackgroundColor(color(0, 64));

		cp5.begin(renderGroup, 10, 10);

		cp5.addSlider("fillAlpha").setRange(0, 1).setCaptionLabel("Fill");
		cp5.addSlider("strokeAlpha").setRange(0, 1).setCaptionLabel("Stroke");
		cp5.addToggle("toggleGrid").setCaptionLabel("Grid")
				.setValue(isUseGrid())
				.plugTo(this, "setUseGrid");
		cp5.addSlider(grid, "0", "rows").setRange(1, 20).setCaptionLabel("Rows").plugTo(grid, "update");
		cp5.addSlider(grid, "0", "columns").setRange(1, 20).setCaptionLabel("Columns").plugTo(grid, "update");

		accordion = cp5.addAccordion("acc")
				.setPosition(10, 10)
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
				setAnimated(!isAnimated());
			}
		}, ' ');
		cp5.mapKeyFor(new ControlKey()
		{
			public void keyEvent()
			{
				decrementSeed();
			}
		}, KeyEvent.VK_LEFT);
		cp5.mapKeyFor(new ControlKey()
		{
			public void keyEvent()
			{
				incrementSeed();
			}
		}, KeyEvent.VK_RIGHT);
		cp5.mapKeyFor(new ControlKey()
		{
			public void keyEvent()
			{
				setCurrentRecursions(max((float) (Math.floor(currentRecursions) - 1), 0));
			}
		}, KeyEvent.VK_DOWN);
		cp5.mapKeyFor(new ControlKey()
		{
			public void keyEvent()
			{
				setCurrentRecursions(min((float) (Math.floor(currentRecursions) + 1), maxRecursionsLimit));
			}
		}, KeyEvent.VK_UP);
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
				setCurrentRecursions(maxRecursions);
			}
		}, 'm');
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
				if (cp5.isVisible())
				{
					cp5.hide();
				}
				else
				{
					cp5.show();
				}
			}
		}, '0');

		accordion.open();

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
		if (guiInitialized && isAutoSave())
		{
			cp5.saveProperties("settings.ser", getValuePropertySetName());
		}
	}

	public void save()
	{
		cp5.saveProperties("snowflake-" + (new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss")).format(new Date()) + ".ser",
						   getValuePropertySetName());
	}

	public void pdf()
	{
		String name = "snowflake-" + getSeed() + (useGrid ? "-grid-" + grid.columns + "x" + grid.rows : "");
		PGraphicsPDF pdf = (PGraphicsPDF) createGraphics(width, height, PDF, name + ".pdf");
		pdf.beginDraw();
		beginRecord(pdf);
		drawSnowflake();
		endRecord();
		pdf.endDraw();
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
		drawSnowflake();

		if (isAnimated())
		{
			int currentTime = millis();
			int timeDelta = currentTime - previousTime;
			int maximumTimeIncrement = (int)(1 / recursionsPerMillisecond);
			if (timeDelta > maximumTimeIncrement) timeDelta = maximumTimeIncrement;
			previousTime = currentTime;
			animationTime += timeDelta;
			float newCurrentRecursions;
			if (linearAnimation) {
				newCurrentRecursions = getCurrentRecursions() + velocity * timeDelta * recursionsPerMillisecond;
			} else {
				newCurrentRecursions = map((float) Math.cos(animationTime * recursionsPerMillisecond), -1, 1, -maxRecursions, maxRecursions) * velocity;
			}
			if (newCurrentRecursions >= maxRecursions)
			{
				newCurrentRecursions = maxRecursions;
				if (linearAnimation) {
					velocity = -1;
				}
			}
			if (newCurrentRecursions <= 0)
			{
				newCurrentRecursions = -newCurrentRecursions;
				velocity = -velocity;
				if (autoSeed)
				{
					incrementSeed();
					setCurrentRecursions(maxRecursions);
					if (autoPdf) {
						pdf();
					}
				}
			}
			setCurrentRecursions(newCurrentRecursions);
		}
	}

	private void drawSnowflake() {
		background(0);
		int count = 0;
		pushMatrix();
		int accordionWidthPadded = cp5.isVisible() ? accordion.getWidth() + 30 : 0;
		translate(accordionWidthPadded, 0);
		float availableWidth = width - accordionWidthPadded;
		float diameter = min(availableWidth, height);
		scale(1f / max(grid.rows, grid.columns));
		for (int r = 0; r < grid.rows; r++) {
			for (int c = 0; c < grid.columns; c++) {
				randomSeed(getSeed() + count);
				fill(255, 255f * fillAlpha);
				stroke(255, 255f * strokeAlpha);
				primaryBranches = primaryBranchesBase + Math.round(random(primaryBranchesVariation));

				drawBranches(
						diameter * (c + 0.5f),
						diameter * (r + 0.5f),
						branchLengthBase + random(branchLengthVariation),
						branchWidthBase + random(branchWidthVariation),
						primaryBranches, getCurrentRecursions());
				count++;
			}
		}
		popMatrix();
	}

	public void resetSeed()
	{
		setSeed(millis());
	}

	public void decrementSeed()
	{
		setSeed(getSeed() - grid.getTotal());
	}

	public void incrementSeed()
	{
		setSeed(getSeed() + grid.getTotal());
	}

	public void drawBranches(float x1, float y1, float primaryBranchLength, float primaryBranchWidth, int primaryBranches,
							 float inRecursions)
	{
		float levelFraction;
		levelFraction = getLevelFraction(inRecursions, 0);

		float spreadAngle = 0;
		ArrayList<SnowflakeNodeInstance> rootInstances = new ArrayList<SnowflakeNodeInstance>();
		rootInstances.add(new SnowflakeNodeInstance(primaryBranches, 0, 1, 1, spreadAngle));
		SnowflakeNode rootNode = new SnowflakeNode(
				getSnowflakeNodeType(0), primaryBranches,
				0, primaryBranchLength * levelFraction, primaryBranchWidth * levelFraction, spreadAngle, rootInstances);
		ArrayList<SnowflakeNode> currentLevelNodes = new ArrayList<SnowflakeNode>();
		currentLevelNodes.add(rootNode);
		for (int i = 0; i < (int)Math.ceil(inRecursions) - 1; i++)
		{
			levelFraction = getLevelFraction(inRecursions, i + 1);

			ArrayList<SnowflakeNode> nextLevelNodes = new ArrayList<SnowflakeNode>();
			for (SnowflakeNode currentNode : currentLevelNodes)
			{
				boolean uniform = true;
				ArrayList<SnowflakeNodeInstance> instances = new ArrayList<SnowflakeNodeInstance>();
				float tipWidth = 0;
				int divisions = Math.min(primaryBranches,
											 (int) Math.floor(random(subBranchesVariation) + subBranchesBase));
				float parentLength = currentNode.getBranchLength();
				if (divisions > 0) {
					boolean evenDivisions = divisions % 2 == 0;
					float branchOffset = evenDivisions ? parentLength * random(1f) : parentLength;

					float branchWidth = currentNode.getBranchWidth() * (branchWidthScaleBase + random(
							branchWidthScaleVariation)) * levelFraction;

					if (!evenDivisions)
					{
						tipWidth = Math.max(tipWidth, branchWidth);
					}

					float branchSpreadAngle = divisions == 1 ? 0 : random(angleBase, angleBase + angleVariation);
					if (uniform) {
						instances.add(new SnowflakeNodeInstance(
							divisions,
							branchOffset,
							branchLengthScaleBase + random(branchLengthScaleVariation),
							branchWidthScaleBase + random(branchWidthScaleVariation),
							branchSpreadAngle
						));
					} else {
						nextLevelNodes.add(currentNode.addNode(new SnowflakeNode(
								getSnowflakeNodeType(i + 1), divisions,
								branchOffset, parentLength * (branchLengthScaleBase + random(branchLengthScaleVariation)) * levelFraction,
								branchWidth,
								branchSpreadAngle
						)));
					}
				}

				int midBranches = (int) Math.floor(random(midBranchesVariation + 1) + midBranchesBase);
				float previousOffset = 0;
				for (int m = 0; m < midBranches; m++) {
					// add mid branches along the length of the current node
					float branchOffset = constrain(
							map(randomGaussian(),
								-5,
								5,
								m * parentLength / midBranches,
								(m + 1) * parentLength / midBranches),
								0,
							parentLength);
					float branchWidth = currentNode.getBranchWidth() * (branchWidthScaleBase + random(
							branchWidthScaleVariation)) * levelFraction;
					float branchLength = parentLength / midBranches * 3 * (branchLengthScaleBase + random(branchLengthScaleVariation)) * levelFraction;

					// don't let the branch be any longer than the remaining length of the parent branch
					branchLength = min(branchLength, parentLength - branchOffset);
					if (uniform) {
						// don't let the instance branch be any longer than the remaining length of the parent branch
						// solve for: effectiveLength = parentLength * levelFraction * branchLengthScale
						float lengthLimit = parentLength - branchOffset;
						// solve for: lengthLimit = parentLength * lengthScaleLimit
						float lengthScaleLimit = lengthLimit / parentLength;
						instances.add(new SnowflakeNodeInstance(
							2,
							branchOffset,
							min(lengthScaleLimit, branchLengthScaleBase + random(branchLengthScaleVariation)),
							branchWidthScaleBase + random(branchWidthScaleVariation),
							TWO_PI / 3
						));
					} else {
						nextLevelNodes.add(currentNode.addNode(new SnowflakeNode(
								getSnowflakeNodeType(i + 1), 2,
								branchOffset, branchLength,
								branchWidth,
								TWO_PI / 3
						)));
					}
				}

				boolean midTrunkBranches = true;
				if (uniform && midTrunkBranches && midBranches > 0) {
					for (int m = 0; m < midBranches + 1; m++) {
						float branchOffset = m == 0 ? 0 : instances.get(m - 1).getBranchOffset();
						float nextBranchOffset = m < midBranches ? instances.get(m).getBranchOffset() : parentLength;
						instances.add(new SnowflakeNodeInstance(
							1,
								branchOffset,
								max((nextBranchOffset - branchOffset) / parentLength, m == 0 ? 0 : instances.get(m - 1).getBranchLengthScale()),
								max(1, m == 0 ? 0 : instances.get(m - 1).getBranchWidthScale()),
								0
						));
					}
				}

				if (uniform) {
					nextLevelNodes.add(currentNode.addNode(new SnowflakeNode(
							getSnowflakeNodeType(i + 1), 2,
							0, parentLength * levelFraction,
							currentNode.getBranchWidth(),
							TWO_PI / 3,
							instances
					)));
				}

				currentNode.setTipWidth(tipWidth);
			}
			currentLevelNodes = nextLevelNodes;
		}

		drawNodeBranchesRecursive(x1, y1, rootNode, (int)Math.ceil(inRecursions));
	}

	private float randomGaussianRange() {
		return randomGaussianRange(0, 1);
	}

	private float randomGaussianRange(float stop) {
		return randomGaussianRange(stop, 0);
	}

	private float randomGaussianRange(float stop, float start) {
		return constrain(map(randomGaussian(), -5, 5, start, stop), start, stop);
	}

	private float getLevelFraction(float inRecursions, int i)
	{
		float levelFraction = inRecursions - i;
		if (levelFraction > 1) levelFraction = 1;
		return levelFraction;
	}

	private SnowflakeNodeType getSnowflakeNodeType(int level)
	{
		int i = Math.round(fractionPolygons * 0.5f + random(0.5f));
		if (level > 1 && isPolygonDrawingEnabled)
		{
			return SnowflakeNodeType.values()[i];
		}
		else
			return SnowflakeNodeType.RECTANGLES;
	}

	private void drawNodeBranchesRecursive(float branchX, float branchY, SnowflakeNode node, int inRecursions)
	{
		inRecursions--;
		for (SnowflakeNodeInstance instance : node.getInstances())
		{
			if (instance.getSymmetricalDivisions() != 0)
			{
				float alpha;
				if (instance.getSpreadAngle() == 0)
					alpha = (TWO_PI) / (instance.getSymmetricalDivisions());
				else
					alpha = (instance.getSpreadAngle()) / (instance.getSymmetricalDivisions() - 1);

				pushMatrix();
				translate(branchX + instance.getBranchOffset(), branchY);
				scale(instance.getBranchLengthScale());
				rotate(-(instance.getSpreadAngle()) / 2);
				float branchLength = node.getBranchLength();
				if (node.getType() == SnowflakeNodeType.POLYGON && branchLength > 1)
				{
					polygon(getPolygonSides(node), 0, 0, branchLength);
				}
				for (int d = 0; d < instance.getSymmetricalDivisions(); d++)
				{
					float branchWidth = node.getBranchWidth() * instance.getBranchWidthScale();
					if (node.getType() == SnowflakeNodeType.RECTANGLES)
					{
						float pointLength = (float) ((branchWidth / 2) / Math.tan(TWO_PI / primaryBranches));
						beginShape();
						vertex(0, -branchWidth / 2);
						vertex(branchLength, -branchWidth / 2);
						vertex(branchLength + pointLength, 0);
						vertex(branchLength, +branchWidth / 2);
						vertex(0, +branchWidth / 2);
						endShape(CLOSE);
					} else if (node.getType() == SnowflakeNodeType.LINES) {
						strokeWeight(branchWidth);
						strokeCap(ROUND);
						beginShape(LINES);
						vertex(0, 0);
						vertex(branchLength, 0);
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

	public boolean isAutoSave()
	{
		return autoSave;
	}

	public boolean isAutoPdf()
	{
		return autoPdf;
	}

	public boolean isUseGrid()
	{
		return useGrid;
	}

	public void setAutoSeed(boolean autoSeed)
	{
		this.autoSeed = autoSeed;
	}
	public void setAutoSave(boolean autoSave)
	{
		this.autoSave = autoSave;
	}
	public void setAutoPdf(boolean autoPdf)
	{
		this.autoPdf = autoPdf;
	}
	public void setUseGrid(boolean value)
	{
		this.useGrid = value;
		updateGrid();
	}

	private Grid grid = new Grid();
	private void updateGrid() {
		grid.rows = useGrid ? gridRows : 1;
		grid.columns = useGrid ? gridColumns : 1;
		grid.update();
	}
}
