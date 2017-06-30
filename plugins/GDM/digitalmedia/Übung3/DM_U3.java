package digitalmedia.�bung3;


import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Random;

import javax.swing.JComboBox;

/**
     Opens an image window and adds a panel below the image
 */
public class DM_U3 implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;
	Random randomer = new Random();

	String[] items = { "Original", "Rot-Kanal", "Negativ", "Graustufen",
			"Binärbild", "5 Graustufen", "10 Graustufen", "32 Graustufen",
			"Random Dithering", "Fehlerdiffusion", "Sepia", "6 Farben",
			"Blau-Rot-Verlauf","Floyd-Steinberg" };

	public static void main(String args[]) {

		ImageJ ij = new ImageJ();
		//TODO 
		IJ.open("");//bear.jpg

		DM_U3 pw = new DM_U3();
		pw.imp = IJ.getImage();
		pw.run("");
	}

	@Override
	public void run(String arg) {
		if (imp==null) 
			imp = WindowManager.getCurrentImage();
		if (imp==null) {
			return;
		}
		CustomCanvas cc = new CustomCanvas(imp);
		storePixelValues(imp.getProcessor());
		new CustomWindow(imp, cc);
	}


	private void storePixelValues(ImageProcessor ip) {
		width = ip.getWidth();
		height = ip.getHeight();

		origPixels = ((int []) ip.getPixels()).clone();
	}
	class CustomCanvas extends ImageCanvas {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		CustomCanvas(ImagePlus imp) {
			super(imp);
		}
	} // CustomCanvas inner class

	class CustomWindow extends ImageWindow implements ItemListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String method;
		
		CustomWindow(ImagePlus imp, ImageCanvas ic) {
			super(imp, ic);
			addPanel();
		}

		void addPanel() {
			//JPanel panel = new JPanel();
			Panel panel = new Panel();
			JComboBox cb = new JComboBox(items);
			panel.add(cb);
			cb.addItemListener(this);
			add(panel);
			pack();
		}

		
		@Override
		public void itemStateChanged(ItemEvent evt) {
			// Get the affected item
			Object item = evt.getItem();

			if (evt.getStateChange() == ItemEvent.SELECTED) {
				System.out.println("Selected: " + item.toString());
				method = item.toString();
				changePixelValues(imp.getProcessor());
				imp.updateAndDraw();
			} 
		}

		private void changePixelValues(ImageProcessor ip) {
			// Array zum Zurückschreiben der Pixelwerte
			int[] pixels = (int[])ip.getPixels();
			if (method.equals("Original")) {
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						pixels[pos] = origPixels[pos];
					}
				}
			}
			
			if (method.equals("Rot-Kanal")) {
				redChanel(origPixels, pixels, width, height);
			}
			if (method.equals("Negativ")) {
				negativeImage(origPixels, pixels, width, height);
				//gdmvalidation.Ueb3Validation.validateNegativeImage(origPixels, pixels, width, height);
			}
			if (method.equals("Graustufen")) {
				greyValueImage(origPixels, pixels, width, height);
			}
			if (method.equals("Binärbild")) {
				binaryImage(origPixels, pixels, width, height);
			}
			if (method.equals("5 Graustufen")) {
				greyValueImage5Values(origPixels, pixels, width, height);
			}
			if (method.equals("10 Graustufen")) {
				greyValueImage10Values(origPixels, pixels, width, height);
			}
			if (method.equals("32 Graustufen")) {
				greyValueImage32Values(origPixels, pixels, width, height);
			}
			if (method.equals("Random Dithering")) {
				randomDithering(origPixels, pixels, width, height);
			}
			if (method.equals("Fehlerdiffusion")) {
				errorDiffusion(origPixels, pixels, width, height);
			}
			if (method.equals("Sepia")) {
				sepiaImage(origPixels, pixels, width, height);
			}
			if (method.equals("6 Farben")) {
				mapImageTo6Colors(origPixels, pixels, width, height);
			}
			if (method.equals("Blau-Rot-Verlauf")) {
				blueToRed( pixels, width, height);
			}
			if (method.equals("Floyd-Steinberg")) {
				//floydSteinberg(origPixels, pixels, width, height);
			}
			
		}

		private void redChanel(int[] origPixels, int[] pixels, int width, int height) {
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
		
					int r = (argb >> 16) & 0xff;
					//int g = (argb >>  8) & 0xff;
					//int b =  argb        & 0xff;
		
					int rn = r;
					int gn = 0;
					int bn = 0;
		
					// Hier muessen ggfs. die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
		
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}

		private void negativeImage(int[] origPixels, int[] pixels, int width, int height) 
		{
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
		
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					// TODO b)
					int rn = 255-r;
					int gn = 255-g;
					int bn = 255-b;
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}

		private void greyValueImage(int[] origPixels, int[] pixels, int width,
				int height) 
		{

			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 

					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					// TODO c)
					//Greyercode
					int greyvalue = (int) (r*0.299+g*0.587+b*0.114);
					int rn,gn,bn;
					rn = gn = bn =greyvalue;
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}

		private void binaryImage(int[] origPixels, int[] pixels, int width, int height) 
		{
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
		
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					// TODO d1)
					//Greyercode
					int greyvalue = (int) (r*0.299+g*0.587+b*0.114);
					if(greyvalue>=128)greyvalue=255;
					else greyvalue = 0;
					int rn,gn,bn;
					rn = gn = bn =greyvalue;
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}	
		}

		private void greyValueImage5Values(int[] origPixels, int[] pixels, int width, int height) 
		{
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
		
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					// TODO d2)
					//Greyercode
					int greyvalue = (int) (r*0.299+g*0.587+b*0.114);
					if(greyvalue<255/5)greyvalue=0;
					else if(greyvalue<255/5*2&&greyvalue>=255/5)greyvalue=255/4;
					else if(greyvalue<255/5*3&&greyvalue>=255/5*2)greyvalue=255/4*2;
					else if(greyvalue<255/5*4&&greyvalue>=255/5*3)greyvalue=255/4*3;
					else greyvalue=255;
					int rn,gn,bn;
					rn = gn = bn =greyvalue;
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}		
		}

		private void greyValueImage10Values(int[] origPixels, int[] pixels, int width, int height) 
		{
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
		
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					// TODO d3)
					//Greyercode
					int greyvalue = (int) (r*0.299+g*0.587+b*0.114);
					if(greyvalue<255/5)greyvalue=0;
					else if(greyvalue<255/10*2&&greyvalue>=255/10)greyvalue=255/8;
					else if(greyvalue<255/10*3&&greyvalue>=255/10*2)greyvalue=255/8*2;
					else if(greyvalue<255/10*4&&greyvalue>=255/10*3)greyvalue=255/8*3;
					else if(greyvalue<255/10*5&&greyvalue>=255/10*4)greyvalue=255/8*4;
					else if(greyvalue<255/10*6&&greyvalue>=255/10*5)greyvalue=255/8*5;
					else if(greyvalue<255/10*7&&greyvalue>=255/10*6)greyvalue=255/8*6;
					else if(greyvalue<255/10*8&&greyvalue>=255/10*7)greyvalue=255/8*7;
					else if(greyvalue<255/10*9&&greyvalue>=255/10*8)greyvalue=255/8*8;
					else greyvalue=255;
					int rn,gn,bn;
					rn = gn = bn =greyvalue;
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}		
		}

		private void greyValueImage32Values(int[] origPixels2, int[] pixels,
				int width2, int height2) {
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
		
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					// TODO d4)
					//Greyercode
					int greyvalue = (int) (r*0.299+g*0.587+b*0.114);
					greyvalue = greyvalue-greyvalue%8;
					if(greyvalue<0)greyvalue=0;
					int rn,gn,bn;
					rn = gn = bn =greyvalue;
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}		
		}

		private void randomDithering(int[] origPixels, int[] pixels, int width,
				int height) {
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
		
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					// TODO e)
					//Greyercode
					int greyvalue = (int) (r*0.299+g*0.587+b*0.114);
					greyvalue+=randomer.nextInt(101)-50;
					if(greyvalue<128)greyvalue=0;
					else greyvalue = 255;
					int rn,gn,bn;
					rn = gn = bn =greyvalue;
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}		
		}

		private void errorDiffusion(int[] origPixels, int[] pixels, int width, int height) 
		{
			int error=0;
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
		
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					// TODO f)
					//Greyercode
					int greyvalue = (int) (r*0.299+g*0.587+b*0.114);
					greyvalue = greyvalue-error; //kann auch draufaddiert werden
					if(greyvalue>=128){
						error = 255-greyvalue;
						greyvalue=255;
					}
					else{
						error = 0-greyvalue;
						greyvalue=0;
					}
					
					int rn,gn,bn;
					rn = gn = bn =greyvalue;
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}

		private void sepiaImage(int[] origPixels, int[] pixels, int width, int height)
		{
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
		
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					// TODO g)
					int rn = (int) (0.393*r+0.769*g+0.189*b);
					int gn = (int) (0.349*r+0.686*g+0.168*b);
					int bn = (int) (0.272*r+0.534*g+0.131*b);
					if(rn>255)rn=255;
					if(gn>255)gn=255;
					if(bn>255)bn=255;
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}		
		}

		private void mapImageTo6Colors(int[] origPixels, int[] pixels, int width, int height) 
		{
			//balck
			int color1R = 0;
			int color1G = 0;
			int color1B = 0;
			//wei�
			int color2R = 255;
			int color2G = 255;
			int color2B = 255;
			//hellbraun
			int color3R = 108;
			int color3G = 97;
			int color3B = 88;
			//dunkelbraun
			int color4R = 43;
			int color4G = 46;
			int color4B = 45;
			//blau
			int color5R = 55;
			int color5G = 106;
			int color5B = 141;
			//hellgrau
			int color6R = 198;
			int color6G = 196;
			int color6B = 197;
			
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 

					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					// TODO h)
					//Abst�nde zwischen den 6 anderen Farben vergleichen
					int distance1 = (int) Math.sqrt(Math.pow((color1R-r), 2)+Math.pow((color1G-g),2)+Math.pow((color1B-b),2));
					int distance2 = (int) Math.sqrt(Math.pow((color2R-r), 2)+Math.pow((color2G-g),2)+Math.pow((color2B-b),2));
					int distance3 = (int) Math.sqrt(Math.pow((color3R-r), 2)+Math.pow((color3G-g),2)+Math.pow((color3B-b),2));
					int distance4 = (int) Math.sqrt(Math.pow((color4R-r), 2)+Math.pow((color4G-g),2)+Math.pow((color4B-b),2));
					int distance5 = (int) Math.sqrt(Math.pow((color5R-r), 2)+Math.pow((color5G-g),2)+Math.pow((color5B-b),2));
					int distance6 = (int) Math.sqrt(Math.pow((color6R-r), 2)+Math.pow((color6G-g),2)+Math.pow((color6B-b),2));
					
					//sorting algorythm
					int[] distances = {distance1,distance2,distance3,distance4,distance5,distance6};
					int smallestDistance = distances[0];
					int smallestDistanceColor = 1;
					for(int i=1;i<6;i++){
						if (distances[i]<smallestDistance){
							smallestDistance=distances[i];
							smallestDistanceColor = i+1;
						}
					}
					int rn = color1R;
					int gn = color1G;
					int bn = color1B;
					switch(smallestDistanceColor){
					
					case 2:
						rn = color2R;
						gn = color2G;
						bn = color2B;
						break;
					case 3:
						rn = color3R;
						gn = color3G;
						bn = color3B;
						break;
					case 4:
						rn = color4R;
						gn = color4G;
						bn = color4B;
						break;
					case 5:
						rn = color5R;
						gn = color5G;
						bn = color5B;
						break;
					case 6:
						rn = color6R;
						gn = color6G;
						bn = color6B;
						break;
					default:
						rn = color1R;
						gn = color1G;
						bn = color1B;
						break;
					}
					
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}

		private void blueToRed( int[] pixels, int width, int height) 
		{
			for (int y=0; y<height; y++) {
				int error = 0;
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					// TODO i)
					int rn = (int) ((255.0/width)*x);
					int gn = 0;
					int bn = 255-rn;		//((255/width)*-1)*x+255;
					
					
					
					if(y<height/2){
						//rn = (int) (rn*0.299+gn*0.587+bn*0.114);
						rn+=randomer.nextInt(101)-50;
						if(rn<128)rn=0;
						else rn = 255;
					}
					
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}
	} // CustomWindow inner class
} 
