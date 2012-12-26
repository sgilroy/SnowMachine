package snowMachine;

import controlP5.*;
import processing.core.PApplet;
import processing.core.PFont;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

public class SnowMachine extends PApplet
{

	/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/54745*@* */
/* !do not delete the line above, required for linking your tweak if you re-upload */
	float armLength = 140;  // arm length
	float shoulderLength = 7;    // shoulder length
	int recursions = 7;       //ilo\u015b\u0107 recursion

	int primaryBranches = 6;
	private int seed;
	private int currentRecursions = 1;
	private int velocity = 1;
	private boolean isAnimated = true;

	ControlP5 cp5;

	Accordion accordion;
	private Slider currentRecursionsSlider;
	private Slider seedSlider;
	private int primaryBranchesBase = 6;
	private int primaryBranchesVariation = 0;
	private int subBranchesBase = 2;
	private int subBranchesVariation = 6;
	private boolean guiInitialized = false;

	public void setup()
	{
		size(700, 700);
		background(0);
		noStroke();
		smooth();
		fill(255);
		frameRate(29);
		drawBranches(width / 2, height / 2, armLength, shoulderLength, primaryBranches, recursions);
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
				.setBackgroundColor(color(0, 64))
				;

		cp5.begin(complexityGroup, 10, 10);

		cp5.addToggle("Animate")
				.setValue(isAnimated())
				.plugTo(this, "setAnimated")
				.linebreak()
		;

		currentRecursionsSlider = cp5.addSlider("currentRecursions")
				.setRange(1, recursions);

		Group appearanceGroup = cp5.addGroup("Appearance")
				.setBackgroundColor(color(0, 64))
				.setBackgroundHeight(150);

		cp5.begin(appearanceGroup, 10, 10);

		cp5.addButton("resetSeed")
				.linebreak()
		;

		Controller previousControl;
		previousControl = seedSlider = cp5.addSlider("seed")
				.setRange(0, 100000)
		;

		previousControl = cp5.addSlider("primaryBranchesBase")
				.setRange(1, 12)
		;

		previousControl = cp5.addSlider("primaryBranchesVariation")
				.setRange(0, 12)
		;

		previousControl = cp5.addSlider("subBranchesBase")
				.setRange(1, 12)
		;

		previousControl = cp5.addSlider("subBranchesVariation")
				.setRange(0, 12)
		;

		Group fileGroup = cp5.addGroup("File")
				.setBackgroundColor(color(0, 64))
				;

		cp5.begin(fileGroup, 10, 10);

		cp5.addButton("save")
				.setSize(80, 20)
		;

		accordion = cp5.addAccordion("acc")
				.setPosition(40, 40)
				.setWidth(250)
				.addItem(complexityGroup)
				.addItem(appearanceGroup)
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
				accordion.setCollapseMode(ControlP5.ALL);
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
		primaryBranches = primaryBranchesBase + Math.round(random(primaryBranchesVariation));

		drawBranches(width / 2, height / 2, armLength, shoulderLength, primaryBranches, getCurrentRecursions());

		if (isAnimated())
		{
			int newCurrentRecursions = getCurrentRecursions() + velocity;
			if (newCurrentRecursions > recursions)
			{
				newCurrentRecursions = recursions;
				velocity = -1;
			}
			if (newCurrentRecursions < 1)
			{
				newCurrentRecursions = 1;
				velocity = 1;
			}
			setCurrentRecursions(newCurrentRecursions);
		}
	}

	public void resetSeed()
	{
//		setCurrentRecursions(isAnimated() ? 1 : recursions);
		setSeed(millis());
	}

	public void drawBranches(float x1, float y1, float H, float B, int symmetricalDivisions, int inRecursions)
	{
		int[] branchSymmetricalDivisions = new int[inRecursions];
		float[] branchWidths = new float[inRecursions];
		float[] branchHeights = new float[inRecursions];
		float[] branchAlphas = new float[inRecursions];

		branchSymmetricalDivisions[inRecursions - 1] = symmetricalDivisions;
		branchWidths[inRecursions - 1] = H;
		branchHeights[inRecursions - 1] = B;
		branchAlphas[inRecursions - 1] = 0;

		for (int i = inRecursions - 2; i >= 0; i--)
		{
			branchSymmetricalDivisions[i] = PApplet.parseInt(random(subBranchesVariation) + subBranchesBase);
			branchWidths[i] = branchWidths[i + 1] * random(0.5f, 0.7f);
			branchHeights[i] = branchHeights[i + 1] * random(0.4f, 0.5f);
			branchAlphas[i] = random(HALF_PI, PI - HALF_PI / 2);
//			print(i + ": " + branchSymmetricalDivisions[i] + " " + branchWidths[i] + " " + branchHeights[i] + " " + branchAlphas[i] + "\n");
		}
		drawBranchesRecursive(x1, y1, branchWidths, branchHeights, branchAlphas, branchSymmetricalDivisions,
							  inRecursions);
	}

	public void drawBranchesRecursive(float branchX, float branchY, float branchWidths[], float branchHeights[],
									  float branchAlphas[],
									  int branchSymmetricalDivisions[], int inRecursions)
	{
		inRecursions--;
		if (inRecursions + 1 > 0 && branchSymmetricalDivisions[inRecursions] != 0)
		{
			float alpha;
			if (branchAlphas[inRecursions] == 0)
				alpha = (TWO_PI - branchAlphas[inRecursions]) / (branchSymmetricalDivisions[inRecursions]);
			else
				alpha = (TWO_PI - branchAlphas[inRecursions]) / (branchSymmetricalDivisions[inRecursions] - 1);

			pushMatrix();
			translate(branchX, branchY);
			rotate(-(TWO_PI - branchAlphas[inRecursions]) / 2);
			for (int i = 0; i < branchSymmetricalDivisions[inRecursions]; i++)
			{
				rect(0, -branchHeights[inRecursions] / 2, branchWidths[inRecursions], branchHeights[inRecursions]);
				drawBranchesRecursive(branchWidths[inRecursions], 0, branchWidths, branchHeights, branchAlphas,
									  branchSymmetricalDivisions, inRecursions);
				rotate(alpha);
			}
			popMatrix();
		}
	}

	static public void main(String[] passedArgs)
	{
		String[] appletArgs = new String[]{"snowMachine.SnowMachine"};
		if (passedArgs != null)
		{
			PApplet.main(concat(appletArgs, passedArgs));
		} else
		{
			PApplet.main(appletArgs);
		}
	}

	public boolean isAnimated()
	{
		return isAnimated;
	}

	public void setAnimated(boolean animated)
	{
		isAnimated = animated;
		if (!isAnimated())
		{
			setCurrentRecursions(recursions);
		}
	}

	public int getCurrentRecursions()
	{
		return currentRecursions;
	}

	public void setCurrentRecursions(int currentRecursions)
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
}
