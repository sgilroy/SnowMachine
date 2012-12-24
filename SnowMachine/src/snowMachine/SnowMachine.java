package snowMachine;

import processing.core.PApplet;

public class SnowMachine extends PApplet
{

	/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/54745*@* */
/* !do not delete the line above, required for linking your tweak if you re-upload */
	float armLength = 140;  // arm length
	float shoulderLength = 7;    // shoulder length
	int recursions = 7;       //ilo\u015b\u0107 recursion

	int symmetricalDivisions = 6;
	private long seed;
	private int currentRecursions = 1;
	private int velocity = 1;

	public void setup()
	{
		size(700, 700);
		background(0);
		noStroke();
		smooth();
		fill(255);
		frameRate(29);
		drawPlates(width / 2, height / 2, armLength, shoulderLength, symmetricalDivisions, recursions);
	}

	public void draw()
	{
		randomSeed(seed);
		background(0);
		symmetricalDivisions = 6;

		drawPlates(width / 2, height / 2, armLength, shoulderLength, symmetricalDivisions, currentRecursions);

		currentRecursions += velocity;
		if (currentRecursions > recursions)
		{
			currentRecursions = recursions;
			velocity = -1;
		}
		if (currentRecursions < 1)
		{
			currentRecursions = 1;
			velocity = 1;
		}
	}

	public void mousePressed()
	{
		currentRecursions = 1;
		seed = (long) random(0, 1024);
	}

	public void drawPlates(float X1, float Y1, float H, float B, int symmetricalDivisions, int inRecursions)
	{
		int[] plateSymmetricalDivisions = new int[inRecursions];
		float[] plateWidths = new float[inRecursions];
		float[] plateHeights = new float[inRecursions];
		float[] plateAlphas = new float[inRecursions];

		plateSymmetricalDivisions[inRecursions - 1] = symmetricalDivisions;
		plateWidths[inRecursions - 1] = H;
		plateHeights[inRecursions - 1] = B;
		plateAlphas[inRecursions - 1] = 0;

		for (int i = inRecursions - 2; i >= 0; i--)
		{
			plateSymmetricalDivisions[i] = PApplet.parseInt(random(6) + 2);
			plateWidths[i] = plateWidths[i + 1] * random(0.5f, 0.7f);
			plateHeights[i] = plateHeights[i + 1] * random(0.4f, 0.5f);
			plateAlphas[i] = random(HALF_PI, PI - HALF_PI / 2);
			print(i + ": " + plateSymmetricalDivisions[i] + " " + plateWidths[i] + " " + plateHeights[i] + " " + plateAlphas[i] + "\n");
		}
		drawPlatesRecursive(X1, Y1, plateWidths, plateHeights, plateAlphas, plateSymmetricalDivisions, inRecursions);
	}

	public void drawPlatesRecursive(float X1, float Y1, float plateWidths[], float plateHeights[], float plateAlphas[],
									int plateSymmetricalDivisions[], int inRecursions)
	{
		inRecursions--;
		if (inRecursions + 1 > 0 && plateSymmetricalDivisions[inRecursions] != 0)
		{
			float alpha;
			if (plateAlphas[inRecursions] == 0)
				alpha = (TWO_PI - plateAlphas[inRecursions]) / (plateSymmetricalDivisions[inRecursions]);
			else
				alpha = (TWO_PI - plateAlphas[inRecursions]) / (plateSymmetricalDivisions[inRecursions] - 1);

			pushMatrix();
			translate(X1, Y1);
			rotate(-(TWO_PI - plateAlphas[inRecursions]) / 2);
			for (int i = 0; i < plateSymmetricalDivisions[inRecursions]; i++)
			{

				rect(0, -plateHeights[inRecursions] / 2, plateWidths[inRecursions], plateHeights[inRecursions]);
				drawPlatesRecursive(plateWidths[inRecursions], 0, plateWidths, plateHeights, plateAlphas,
									plateSymmetricalDivisions, inRecursions);
				rotate(alpha);
			}
			popMatrix();
		}
	}

	static public void main(String[] passedArgs)
	{
		String[] appletArgs = new String[]{"SnowMachine"};
		if (passedArgs != null)
		{
			PApplet.main(concat(appletArgs, passedArgs));
		} else
		{
			PApplet.main(appletArgs);
		}
	}
}
