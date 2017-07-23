package digitalmedia.Ubung6;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import gdmvalidation.ScaleValidate;

public class DM_U6 implements PlugInFilter {
    protected ImagePlus imp;

    public static void main(String args[]) {
        ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen
        ij.exitWhenQuitting(true);

        //TODO open your image here
        //IJ.open("/home/...../component.jpg");
        IJ.open("C:\\Users\\Windows 10\\Desktop\\ImageJ\\plugins\\GDM\\digitalmedia\\Ubung6\\component.jpg");

        DM_U6 sd = new DM_U6();
        sd.imp = IJ.getImage();
        ImageProcessor ip = sd.imp.getProcessor();
        sd.run(ip);
    }

    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about"))
        {showAbout(); return DONE;}
        return DOES_RGB+NO_CHANGES;
        // kann RGB-Bilder und veraendert das Original nicht
    }
    
	
	
    void test() {
    	System.out.println("If you see \"you solved it\", this only means that your solution works correct\n" +
    			"only for this scaling factor!\n" +
    			"Therefore, please try with many different values for width and height, to check if it will work for \"all\" possible values" 
    			);
    	
    	
        //pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
        int testImWidth = 4;
        int testImHeight= 4;
        int[] testIm = {  0,   0,  0, 0,
        		          0, 100,  3, 0,
        		          0,   0,  0, 0,
        		         30,  30, 30, 0
        };
        
        Utils.makeRGB(testIm);

        int outTestImWidth = 7;
        int outTestImHeight= 9;
        int [] outTestIm = new int[outTestImWidth*outTestImHeight];
        Utils.reset(outTestIm);
        
        

        
  // blank new image      
        System.out.println();
        System.out.println("orig image:");
        Utils.printGreyValues(testIm, testImWidth, testImHeight);
        
        System.out.println();
        System.out.println("new image:");
        Utils.printGreyValues(outTestIm, outTestImWidth, outTestImHeight);
   // copy image
        System.out.println();
        copyImage(testIm, testImWidth, testImHeight, outTestIm, outTestImWidth, outTestImHeight);
 		ScaleValidate.validateCopy(testIm, testImWidth, testImHeight, outTestIm, outTestImWidth, outTestImHeight);
		System.out.println("Copy Image:");
		Utils.printGreyValues(outTestIm, outTestImWidth, outTestImHeight);
		Utils.reset(outTestIm);
   // nearest neighbor
        System.out.println();
        Pixelwiederholung(testIm, testImWidth, testImHeight, outTestIm, outTestImWidth, outTestImHeight);
        ScaleValidate.validateNearestNeighbor(testIm, testImWidth, testImHeight, outTestIm, outTestImWidth, outTestImHeight);
        System.out.println("nearest neighbour:");
        Utils.printGreyValues(outTestIm, outTestImWidth, outTestImHeight);
        Utils.reset(outTestIm);
    // bilinear    
        System.out.println();
        bilinearInterpolation(testIm, testImWidth, testImHeight, outTestIm, outTestImWidth, outTestImHeight);
   		ScaleValidate.validateBilinear(testIm,testImWidth,testImHeight,outTestIm, outTestImWidth, outTestImHeight);
   	 
		System.out.println("bilinear image:");
		Utils.printGreyValues(outTestIm, outTestImWidth, outTestImHeight);
      	System.out.println("test finished\n");
      	Utils.reset(outTestIm);

    }


    public void run(ImageProcessor ip) {

        String[] dropdownmenue = {"Kopie", "Pixelwiederholung", "Bilinear", "Test"};
        GenericDialog gd = new GenericDialog("scale");
        gd.addChoice("Methode",dropdownmenue,dropdownmenue[0]);
        gd.addNumericField("Breite:",800,0);
        gd.addNumericField("Hoehe:",700,0);

        gd.showDialog();

        int newWidth =  (int)gd.getNextNumber();
        int newHight = (int)gd.getNextNumber(); // _n fuer das neue skalierte Bild
        String choice = gd.getNextChoice();

        int width  = ip.getWidth();  // Breite bestimmen
        int height = ip.getHeight(); // Hoehe bestimmen
        
        
        ImagePlus scaledImage = NewImage.createRGBImage("Skaliertes Bild",
                newWidth, newHight, 1, NewImage.FILL_BLACK);

        ImageProcessor ip_n = scaledImage.getProcessor();

        int[] pix = (int[])ip.getPixels();
        int[] pix_n = (int[])ip_n.getPixels();
        boolean test = false;
        if (choice == "Test") {
        	test();
        	test = true;
        } else if (choice == "Kopie") {
            copyImage(pix, width, height, pix_n, newWidth, newHight);
    		ScaleValidate.validateCopy(pix, width, height, pix_n, newWidth, newHight);
        } else if (choice == "Pixelwiederholung") {
            Pixelwiederholung(pix, width, height, pix_n, newWidth, newHight);
    		ScaleValidate.validateNearestNeighbor(pix, width, height, pix_n, newWidth, newHight);
        } else if (choice == "Bilinear") {
        	bilinearInterpolation(pix, width, height, pix_n, newWidth, newHight);
    		ScaleValidate.validateBilinear(pix,width,height,pix_n, newWidth, newHight);
        }
        // neues Bild anzeigen
        if (!test) {
        	scaledImage.show();
        	scaledImage.updateAndDraw();
        }
    }

	private void bilinearInterpolation(int[] origPix, int origWidth,
			int origHeight, int[] newPix, int newWidth, int newHeight) {
		//TODO set your ratio here:
		
		//die VergrößerungsRatios
		double ratioX = (double)origWidth/newWidth;
		double ratioY = (double)origHeight/newHeight;

		// Schleife ueber das neue Bild
		for (int yNew=0; yNew<newHeight; yNew++) {
			for (int xNew=0; xNew<newWidth; xNew++) {
				//TODO add your code here
				
				//doubles benutzen um genua den Punkt zu orten
				double pointX = xNew*ratioX; 
		    	double pointY = yNew*ratioY;
				
		    	//genauer Pixel
		    	int pixX = (int) Math.floor(xNew*ratioX); 
		    	int pixY = (int) Math.floor(yNew*ratioY);
				
		    	//Abstände
		    	double h = pointX - pixX;
				double v = pointY - pixY;
				
				
				//positionen der 4 Umliegenden Pixel, darauf achten, dass sie 
				//nicht aus dem bild reichen( die if Abfragen beugen dies vor
				int posA = (int) (pixY*origWidth + pixX);
				int posB=posA,posC=posA,posD=posA;
				
				if (pixX != origWidth-1) posB = (int) (pixY*origWidth + (pixX + 1));
				if (pixY != origHeight-1) posC = (int) ((pixY+1)*origWidth + pixX);
				if (pixX != origWidth-1&&pixY != origHeight-1) posD = (int) ((pixY+1)*origWidth + (pixX + 1));
				
				
				//sich die RGB Werte der 4 umliegenden Pixel holen
				int[] Argb = getRGB (origPix, posA);
				int[] Brgb = getRGB(origPix, posB);
				int[] Crgb = getRGB (origPix, posC);
				int[] Drgb = getRGB (origPix, posD);
				
				//alles in die Formel aus der 6 Folie eingeben
				// P = A*(1-h)*(1-v)+B*h*(1-v)+C*(1-h)*v+D*h*v
				
				int r = (int) Math.round(Argb[0]*(1 - h)*(1 - v)  + Brgb[0]*h*(1 - v) + Crgb[0]*(1 - h)*v  + Drgb[0]*h*v);
				int g = (int) Math.round(Argb[1]*(1 - h)*(1 - v)  + Brgb[1]*h*(1 - v) + Crgb[1]*(1 - h)*v  + Drgb[1]*h*v);
				int b = (int) Math.round(Argb[2]*(1 - h)*(1 - v)  + Brgb[2]*h*(1 - v) + Crgb[2]*(1 - h)*v  + Drgb[2]*h*v);
			
				int posNew = yNew*newWidth + xNew;
				newPix[posNew] = (0xFF<<24) | (r<<16) | (g << 8) | b;
			}
		}
	}
	
	private int[] getRGB (int origPix[], int posOrig) {
		int argb = origPix[posOrig];
		
		int r = (argb >> 16) & 0xff;
		int g = (argb >>  8) & 0xff;
		int b =  argb        & 0xff;
		
		int[] rgb = {r,g,b};
		
		return rgb;
}

	private void Pixelwiederholung(int[] origPix, int origWidth, int origHeight,
			int[] newPix, int newWidth, int newHeight) {
		//TODO set the values of ratioX and ratioY
		
		//die VergrößerungsRatios
		double ratioX = (double)origWidth/newWidth;
		double ratioY = (double)origHeight/newHeight;
		
		// Schleife ueber das neue Bild
		for (int yNew=0; yNew<newHeight; yNew++) {
		    for (int xNew=0; xNew<newWidth; xNew++) {
		    	//TODO  put your code here
		    	
		    	// Lesen der Originalwerte
		    	//durch das Teilen hier unten holen wir für mehrere neue Pixel 
		    	//informationen aus dem gleichen Originalpixel
		    	int origX = (int) (xNew*ratioX);
		    	int origY = (int) (yNew*ratioY);
		    	
		    	int posOrig = origY*origWidth + origX;
		    	int argb = origPix[posOrig];  
				int r = (argb >> 16) & 0xff;
				int g = (argb >>  8) & 0xff;
				int b =  argb        & 0xff;
				
				//Die Werte der originalPixel werden unverändert 
				//für die neuen übernommen
		    	int posNew = yNew*newWidth + xNew;
				newPix[posNew] = (0xFF<<24) | (r<<16) | (g << 8) | b;
		    }
		}

	}

	private void copyImage(int[] origPix, int origWidth, int origHeight,
			int[] newPix, int newWidth, int newHeight) {
		for (int yNew=0; yNew<newHeight; yNew++) {
		    for (int xNew=0; xNew<newWidth; xNew++) {
		        int y = yNew;
		        int x = xNew;

		        if (y < origHeight && x < origWidth) {
		            int posNew = yNew*newWidth + xNew;
		            int pos  =  y  *origWidth   + x;

		            newPix[posNew] = origPix[pos];
		        }
		     }
	        }
	}

    void showAbout() {
        IJ.showMessage("");
    }
}

