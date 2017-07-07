package digitalmedia.‹bung4;

import ij.*;
import ij.io.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.filter.*;


public class DM_U4 implements PlugInFilter {

	protected ImagePlus imp;
	final static String[] choices = { "Wischen", "Weiche Blende", "AB Overlay","BA Overlay", "Schieb-Blende", "Chroma-Keying", "Eigene ‹berblendung", "Eigene ‹berblendung2"};

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
		if (s.equals("Wischen"))
			methode = 1;
		if (s.equals("Weiche Blende"))
			methode = 2;
		if (s.equals("AB Overlay"))
			methode = 3;
		if (s.equals("BA Overlay"))
			methode = 4;
		if (s.equals("Schieb-Blende"))
			methode = 5;
		if (s.equals("Chroma-Keying"))
			methode = 6;
		if (s.equals("Eigene ‹berblendung"))
			methode = 7;
		if (s.equals("Eigene ‹berblendung2"))
			methode = 8;

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

					//TODO WISCHEN ********************************************************
					if (methode == 1) {
						if (y + 1 > (z - 1) * (double) width / (length - 1)) {
							pixels_Erg[pos] = pixels_B[pos];
						} else {
							pixels_Erg[pos] = pixels_A[pos];
						}
					}

					//TODO WEICHE BLENDE **************************************************
					if (methode == 2) {
						/*
						 * cartoon style should be correct according to my
						 * calculations- dont know why it looks like ths
						 */

						int r = (rA / length * (z - 1) + rB / length * (length - (z - 1)));
						int g = (gA / length * (z - 1) + gB / length * (length - (z - 1)));
						int b = (bA / length * (z - 1) + bB / length * (length - (z - 1)));
						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + (b & 0xff);
					}

					//TODO OVERLAY ********************************************************
					if (methode == 3) {
						//Overlay AB, A Vordergrund
						int r, g, b;

						if (rA <= 128)  r = rA * rB / 128;
						else r = 255 - (255 - rA) * (255 - rB) / 128;

						if (gA <= 128) g = gA * gB / 128;
						else g = 255 - (255 - gA) * (255 - gB) / 128;

						if (bA <= 128)b = bA * bB / 128;
						else b = 255 - (255 - bA) * (255 - bB) / 128;


						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}
					
					if (methode == 4) {
						///Overlay BA, B Vordergrund
						int r, g, b;

						if (rB <= 128)  r = rB * rA / 128;
						else r = 255 - (255 - rB) * (255 - rA) / 128;

						if (gB <= 128) g = gB * gA / 128;
						else g = 255 - (255 - gB) * (255 - gA) / 128;

						if (bB <= 128)b = bB * bA / 128;
						else b = 255 - (255 - bB) * (255 - bA) / 128;
						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}
					
					//TODO SCHIEB-BLENDE **************************************************
					if (methode == 5) {
						int limit =  (width-1) * (z-1) / (length-1);
						int r; 
						int g;
						int b;
						int deltaX = x - limit;
						
						if (deltaX >= 0) {
							int newPos = y * width + deltaX;
							r = (pixels_B[newPos] >> 16) & 0xff;
							g = (pixels_B[newPos] >>  8) & 0xff;
							b = (pixels_B[newPos] >>  0) & 0xff;	
						} else {
							int newPos = y * width + ((width) + deltaX);
							r = (pixels_A[newPos] >> 16) & 0xff;
							g = (pixels_A[newPos] >>  8) & 0xff;
							b = (pixels_A[newPos] >>  0) & 0xff;
						}
						
						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}
					
					//TODO CHROMA-KEYING **************************************************
					if (methode == 6) {
						//TrenngraphGraph = y= x +70  -> dr¸ber ist transparent, drunter ist sichtbar . im CbCr Farbraum
						
						//alle Pixel sind erstmal wie im ersten Bild
						int r = rB;
						int g = gB;
						int b = bB;
						
						//dann nehme ich mir die Pixel vom 2 Bild und wandle sie in YCbCr um
						int YCbCr[] =  RGB2YCbCr(rA,gA,bA);
						
						//if(Cb+10 > Cr) - wenn dies y,x Werte unter dem Trenngraphen liegen
						if(YCbCr[1]+10 > YCbCr[2]){
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
					
					//TODO EIGENE ‹BERBLENDUNG *********************************************
					if (methode == 7) {
						// Extra kreisblende ( vielleicht mehrere Kreisblenden hintereinander- Wasserwellen nach einem Tropfen)
						int r1b = (int) Math.sqrt(Math.pow(width, 2)+Math.pow(height, 2))/2; //radius - so groﬂ wie die diagonale/2
						//Mittelpunkt
						int xMitte = width/2;
						int yMitte = height/2;
						int r,g,b;
						
						int deltaX = x - xMitte;
						int deltaY = y - yMitte;
						//‰ndere den Radius
						//System.out.println("length: "+ length);
						//System.out.println("z = " + z);
						//System.out.println("r1b " + r1b);
						double cu = r1b/length;
						int r1 = (int) (cu*z*6);
						int r2 = (int) (cu*z*0.75*6);
						int r3 = (int) (cu*z*0.5*6);
						int r4 = (int) (cu*z*0.25*6);
						//150/95*95 =95?!!
						//System.out.println("r1 " +r1);
						if (Math.pow(deltaX, 2) + Math.pow(deltaY, 2) < Math.pow(r4, 2)){
							r = rB;
							g = gB;
							b = bB;
						}else if (Math.pow(deltaX, 2) + Math.pow(deltaY, 2) < Math.pow(r3, 2)){
							r=(int) ((0.75)*rB+(0.25)*rA);
							g=(int) ((0.75)*gB+(0.25)*gA);
							b=(int) ((0.75)*bB+(0.25)*bA);
						}else if (Math.pow(deltaX, 2) + Math.pow(deltaY, 2) < Math.pow(r2, 2)){
							r=(int) ((0.5)*rB+(0.5)*rA);
							g=(int) ((0.5)*gB+(0.5)*gA);
							b=(int) ((0.5)*bB+(0.5)*bA);
						}else if (Math.pow(deltaX, 2) + Math.pow(deltaY, 2) < Math.pow(r1, 2)){
							r=(int) ((0.25)*rB+(0.75)*rA);
							g=(int) ((0.25)*gB+(0.75)*gA);
							b=(int) ((0.25)*bB+(0.75)*bA);
							
						}else{
							r=rA;
							g=gA;
							b=bA;
						}

						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}
					
					if (methode == 8) {
						// Extra kreisblende ( vielleicht mehrere Kreisblenden hintereinander- Wasserwellen nach einem Tropfen)
						int r1b = (int) Math.sqrt(Math.pow(width, 2)+Math.pow(height, 2))/2; //radius - so groﬂ wie die diagonale/2
						//Mittelpunkt
						int xMitte = width/2;
						int yMitte = height/2;
						int r,g,b;
						
						int deltaX = x - xMitte;
						int deltaY = y - yMitte;
						//‰ndere den Radius
						//System.out.println("length: "+ length);
						//System.out.println("z = " + z);
						//System.out.println("r1b " + r1b);
						double cu = r1b/length;
						int r1 = (int) (cu*z*6);
						int r2 = (int) (cu*z*0.75*6);
						int r3 = (int) (cu*z*0.5*6);
						int r4 = (int) (cu*z*0.25*6);
						//150/95*95 =95?!!
						//System.out.println("r1 " +r1);
						if (Math.pow(deltaX, 2) + Math.pow(deltaY, 2) < Math.pow(r4, 2)){
							r = rB;
							g = gB;
							b = bB;
						}else if (Math.pow(deltaX, 2) + Math.pow(deltaY, 2) < Math.pow(r3, 2)){
							r=(int) ((0.75)*rB+(0.25)*rA);
							g=(int) ((0.75)*gB+(0.25)*gA);
							b=(int) ((0.75)*bB+(0.25)*bA);
						}else if (Math.pow(deltaX, 2) + Math.pow(deltaY, 2) < Math.pow(r2, 2)){
							r=(int) ((0.5)*rB+(0.5)*rA);
							g=(int) ((0.5)*gB+(0.5)*gA);
							b=(int) ((0.5)*bB+(0.5)*bA);
						}else if (Math.pow(deltaX, 2) + Math.pow(deltaY, 2) < Math.pow(r1, 2)){
							r=(int) ((0.25)*rB+(0.75)*rA);
							g=(int) ((0.25)*gB+(0.75)*gA);
							b=(int) ((0.25)*bB+(0.75)*bA);
							
						}else{
							r=rA;
							g=gA;
							b=bA;
						}

						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}

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

	private int overlay(int colA, int colB) {
		int r;
		if(colB <= 128) {
			 r = (colA * colB) / 128;
		} else {
			 r = 255 - ((255 - colA) * (255 - colB) / 128);
		}
		return r;
	}
}
