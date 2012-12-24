import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class i3A_platek_4 extends PApplet {

/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/54745*@* */
/* !do not delete the line above, required for linking your tweak if you re-upload */
float dRam = 140;  //dlugosc ramienia
float bRam = 7;    //szerokosc ramienia
int ilR = 7;       //ilo\u015b\u0107 rekurencji

int ilRam = 6;

public void setup() {
  size(700, 700);
  background(0);
  noStroke();
  smooth();
  fill(255);
  platekS(width/2, height/2, dRam, bRam, ilRam, ilR); 
  frameRate(29);
}

public void draw() {
}
public void mousePressed()
{
  background(0);
  ilRam = PApplet.parseInt(random(6)+3);

  platekS(width/2, height/2, dRam, bRam, ilRam, ilR);
}

public void platekS(float X1, float Y1, float H, float B, int ilRam, int ilRek)
{
  int[] tabilRam = new int[ilRek];
  float[] tabH = new float[ilRek];
  float[] tabB = new float[ilRek];
  float[] tabalfaR = new float[ilRek];

  tabilRam[ilRek-1] = ilRam;       
  tabH[ilRek-1] = H;
  tabB[ilRek-1] = B;
  tabalfaR[ilRek-1] = 0;

  for (int i = ilRek-2; i >= 0; i--) {
    tabilRam[i] = PApplet.parseInt(random(6)+2);
    tabH[i] = tabH[i+1]*random(0.5f, 0.7f);
    tabB[i] = tabB[i+1]*random(0.4f, 0.5f);
    tabalfaR[i] = random(HALF_PI, PI-HALF_PI/2);
    print(i+": "+tabilRam[i]+" "+tabH[i]+" "+tabB[i]+" "+tabalfaR[i]+"\n");
  }
  platekR(X1, Y1, tabH, tabB, tabalfaR, tabilRam, ilRek);
}

public void platekR(float X1, float Y1, float H[], float B[], float alfaR[], int ilRam[], int ilRek)
{
  ilRek--;
  if (ilRek+1 > 0 && ilRam[ilRek] != 0)
  {
    float alfa;
    if(alfaR[ilRek] == 0)
    alfa = (TWO_PI-alfaR[ilRek])/(ilRam[ilRek]);
    else
    alfa = (TWO_PI-alfaR[ilRek])/(ilRam[ilRek]-1);
    
    pushMatrix();
    translate(X1, Y1);
    rotate(-(TWO_PI-alfaR[ilRek])/2);
    for (int i = 0; i < ilRam[ilRek]; i++)
    {
     
      rect(0, -B[ilRek]/2, H[ilRek], B[ilRek]); 
      platekR(H[ilRek], 0, H, B, alfaR, ilRam, ilRek);
      rotate(alfa);
    }      
    popMatrix();
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "i3A_platek_4" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
