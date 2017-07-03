package digitalmedia.‹bung4;

import ij.*;
import ij.io.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.filter.*;


public class DM_U4 implements PlugInFilter {

	protected ImagePlus imp;
	final static String[] choices = {"Wischen", "Weiche Blende", "Chroma Key", "Extra"};

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB+STACK_REQUIRED;
	}
	
	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen 
		ij.exitWhenQuitting(true);
		
		IJ.open("C:\\Users\\Windows 10\\Desktop\\ImageJ\\plugins\\GDM\\digitalmedia\\‹bung4\\StackB.zip");
		
		DM_U4 sd = new DM_U4();
		sd.imp = IJ.getImage();
		ImageProcessor B_ip = sd.imp.getProcessor();
		sd.run(B_ip);
	}

	public void run(ImageProcessor B_ip) {
		// Film B wird uebergeben
		ImageStack stack_B = imp.getStack();
		
		int length = stack_B.getSize();
		int width  = B_ip.getWidth();
		int height = B_ip.getHeight();
		
		// ermoeglicht das Laden eines Bildes / Films
		Opener o = new Opener();
		OpenDialog od_A = new OpenDialog("Ausw√§hlen des 2. Filmes ...",  "");
				
		// Film A wird dazugeladen
		String dateiA = od_A.getFileName();
		if (dateiA == null) return; // Abbruch
		String pfadA = od_A.getDirectory();
		ImagePlus A = o.openImage(pfadA,dateiA);
		if (A == null) return; // Abbruch

		ImageProcessor A_ip = A.getProcessor();
		ImageStack stack_A  = A.getStack();

		if (A_ip.getWidth() != width || A_ip.getHeight() != height)
		{
			IJ.showMessage("Fehler", "Bildgr√∂√üen passen nicht zusammen");
			return;
		}
		
		// Neuen Film (Stack) "Erg" mit der kleineren Laenge von beiden erzeugen
		length = Math.min(length,stack_A.getSize());

		ImagePlus Erg = NewImage.createRGBImage("Ergebnis", width, height, length, NewImage.FILL_BLACK);
		ImageStack stack_Erg  = Erg.getStack();

		// Dialog fuer Auswahl des Ueberlagerungsmodus
		GenericDialog gd = new GenericDialog("√úberlagerung");
		gd.addChoice("Methode",choices,"");
		gd.showDialog();

		int methode = 0;		
		String s = gd.getNextChoice();
		if (s.equals("Wischen")) methode = 1;
		if (s.equals("Weiche Blende")) methode = 2;
		if (s.equals("Chroma Key")) methode = 3;
		if (s.equals("Extra")) methode = 4;

		// Arrays fuer die einzelnen Bilder
		int[] pixels_B;
		int[] pixels_A;
		int[] pixels_Erg;

		// Schleife ueber alle Bilder
		for (int z=1; z<=length; z++)
		{
			pixels_B   = (int[]) stack_B.getPixels(z);
			pixels_A   = (int[]) stack_A.getPixels(z);
			pixels_Erg = (int[]) stack_Erg.getPixels(z);

			int pos = 0;
			for (int y=0; y<height; y++)
				for (int x=0; x<width; x++, pos++)
				{
					int cA = pixels_A[pos];
					int rA = (cA & 0xff0000) >> 16;
					int gA = (cA & 0x00ff00) >> 8;
					int bA = (cA & 0x0000ff);

					int cB = pixels_B[pos];
					int rB = (cB & 0xff0000) >> 16;
					int gB = (cB & 0x00ff00) >> 8;
					int bB = (cB & 0x0000ff);

					//wischen
					if (methode == 1) {
						if (y+1 > (z-1)*(double)height/(length-1)) {
							pixels_Erg[pos] = pixels_B[pos];
						}
						else {
							pixels_Erg[pos] = pixels_A[pos];
						}
					}
					
					if (methode == 2) {
						/* cartoon style
						should be correct according to my calculations- dont know why it looks like ths
						*/
						
						int r = (rA/length*(z-1)+rB/length*(length-(z-1)) );
						int g = (gA/length*(z-1)+gB/length*(length-(z-1)) );
						int b = (bA/length*(z-1)+bB/length*(length-(z-1)) );
						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}
					
					//Chroma Key
					if (methode == 3) {
						//TrenngraphGraph = y= x +70  -> dr¸ber ist transparent, drunter ist sichtbar . im CbCr Farbraum
						
						
						int r = rB;
						int g = gB;
						int b = bB;
						
						int YCbCr[] =  RGB2YCbCr(rA,gA,bA);
						//System.out.println(YCbCr[0]);
						//System.out.println(YCbCr[1]);
						//System.out.println(YCbCr[2]);
						
						//if(Cb+70 > Cr) - wenn dies y,x Werte ¸ber dem Trenngraphen liegen
						if(YCbCr[1]+40 > YCbCr[2]){
							r=rA;
							g=gA;
							b=bA;
							//‹bergangszone, zweiter Graph
						}else if(YCbCr[1]+10> YCbCr[2]&&YCbCr[1]+20 < YCbCr[2]){
							r=rA/2+rB/2;
							g=gA/2+gB/2;
							b=bA/2+bB/2;
						
						}
						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}

					/* copy this!
					if (methode == 2) {
						// ...
					
						int r = ...
						int g = ...
						int b = ...

						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}
					*/
				}
		}

		// neues Bild anzeigen
		Erg.show();
		Erg.updateAndDraw();
		
		

	}

	public static int[] RGB2YCbCr(double r, double g, double b){
		int[] result = new int[3];
		//Y
		result[0]= (int) (0.299*r+0.587*g+0.114*b);
		//Cb
		result[1]= (int) (-0.168736*r-0.331264*g+0.5*b);
		//Cr
		result[2]= (int) (0.5*r-0.418688*g-0.081312*b);
		return result;
	}
	
	public static int[] YCbCr2RGB(double y, double cb, double cr){
		int[] result = new int[3];
		//r
		result[0]=(int) (y+1.402*cr);
		//g
		result[1]=(int) (y-0.3441*cb-0.7141*cr);
		//b
		result[2]=(int) (y+1.772*cb);
		return result;
}
}
