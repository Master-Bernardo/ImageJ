//package digitalmedia.Uebung5;


//import gdmvalidation.Ueb5Validation;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;


/**
     Opens an image window and adds a panel below the image
 */
public class DM_U5 implements PlugIn 
{
	ImagePlus imp; // ImagePlus object
	private int[] origImagePixels;

	String[] items = {"Original", "Filter 1", "Weichzeichnen", "Hochpass", "Unsharp Mask","test"};


	public static void main(String args[]) {
		ImageJ ij = new ImageJ();
		ij.exitWhenQuitting(true);
		//TODO open your image here
		IJ.open("C:\\Users\\Windows 10\\Desktop\\ImageJ\\plugins\\GDM\\digitalmedia\\�bung5\\sail.jpg");
		DM_U5 pw = new DM_U5();
		pw.imp = IJ.getImage();
		pw.run("");
	}

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
		origImagePixels = ((int []) ip.getPixels()).clone();
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

		public void itemStateChanged(ItemEvent evt) {
			// Get the affected item
			Object item = evt.getItem();

			if (evt.getStateChange() == ItemEvent.SELECTED) {
				System.out.println("Selected: " + item.toString());
				changePixelValues(imp.getProcessor(), item.toString());
				imp.updateAndDraw();
			} 
		}

		private void changePixelValues(ImageProcessor ip, String method) {

			// Array zum Zurückschreiben der Pixelwerte
			int[] changedPixels = (int[])ip.getPixels();
			int width = ip.getWidth();
			int height = ip.getHeight();
			if (method.equals("Original")) {
				copyImage(origImagePixels, changedPixels, width, height);
			}
			if (method.equals("Filter 1")) {
				filter1(origImagePixels, changedPixels, width, height );
			}
			if (method.equals("Weichzeichnen")) {
				lowPass(origImagePixels, changedPixels, width, height);
			}
			if (method.equals("Hochpass")) {
				highPass(origImagePixels, changedPixels, width, height);
			}
			if (method.equals("Unsharp Mask")) {
				unsharpMask(origImagePixels, changedPixels, width, height);
			}
			if (method.equals("test")) {
				test();
			}
		}
	}

	private void copyImage(int[] origPixels, int[] newpixels, int imwidth, int imheight) 
	{
		for (int y=0; y<imheight; y++) {
			for (int x=0; x<imwidth; x++) {
				int pos = y*imwidth + x;
				newpixels[pos] = origPixels[pos];
			}
		}
	}

	/*
	 * Ein Beispiel:
	 */
	private void filter1(int[] origPixels, int[] newpixels, int imwidth, int imheight) 
	{
		for (int y=0; y<imheight; y++) {
			for (int x=0; x<imwidth; x++) {
				int pos = y*imwidth + x;
				int argb = origPixels[pos];  // Lesen der Originalwerte 
	
				int r = (argb >> 16) & 0xff;
				int g = (argb >>  8) & 0xff;
				int b =  argb        & 0xff;
	
				int rn = r/2;
				int gn = g/2;
				int bn = b/2;
	
				newpixels[pos] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;
			}
		}
	}
	
	int rk = 0;
	int gk = 0;
	int bk = 0;
	
	/*
	 * Weichzeichnen (lowPass)
	 */
	private void lowPass(int[] origPixels, int[] newpixels, int imwidth, int imheight) {
		// TODO Auto-generated method stub
		for (int y=0; y<imheight; y++) {
			for (int x=0; x<imwidth; x++) {
				int pos = y*imwidth + x;
				int argb = origPixels[pos];  // Lesen der Originalwerte 
				
				
				//Kernel
				for(int i=-1;i<2;i++){
					for(int j=-1;j<2;j++){
						int posNew = Math.min(Math.max(0,(y+i)),imheight-1)*imwidth+Math.min(Math.max(0,(x+j)), imwidth-1);
						argb = origPixels[posNew];
						this.rk += (argb >> 16) & 0xff;
						this.gk += (argb >>  8) & 0xff;
						this.bk +=  argb        & 0xff;
					}
				}
				
				rk = this.rk/9;
				gk = this.gk/9;
				bk = this.bk/9;
				
				
				
				
				int rn = rk;
				int gn = gk;
				int bn = bk;
	
				newpixels[pos] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;
			}
		}
	}

	/*
	 * Hochpass
	 */
	private void highPass(int[] origPixels, int[] newpixels, int imwidth, int imheight) {
		// TODO Auto-generated method stub
		for (int y=0; y<imheight; y++) {
			for (int x=0; x<imwidth; x++) {
				int pos = y*imwidth + x;
				int argb = origPixels[pos];  // Lesen der Originalwerte 
	
				int r = (argb >> 16) & 0xff;
				int g = (argb >>  8) & 0xff;
				int b =  argb        & 0xff;
	
				
				
				//Hochpass = Original - Tiefpass
				int rn = r-rk;
				int gn = g-gk;
				int bn = b-bk;
	
				newpixels[pos] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;
			}
		}
	}

	/*
	 * Unscharf maskieren
	 */
	private void unsharpMask(int[] origPixels, int[] newpixels, int imwidth, int imheight) {
		// TODO Auto-generated method stub
	}

	/*
	 * Test-Methode. DO NOT CHANGE!
	 */
	void test() {
		//pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
		int testImWidth = 5;
		int testImHeight= 5;
		int[] testIm = {  0,   0,  0, 0,  0, 
				0, 100,  0, 0,  0,
				0, 100,  0, 0,  0,
				0,   0,  0, 0,  0,
				0,   0,  0, 0,  0
		};
	
		Utils.makeRGB(testIm);
	
		int outTestImWidth = testImWidth;
		int outTestImHeight= testImHeight;
		int [] outTestIm = new int[outTestImWidth*outTestImHeight];
		Utils.reset(outTestIm);
	
	
		// blank new image      
		System.out.println();
		System.out.println("orig image:");
		Utils.printGreyValues(testIm, testImWidth, testImHeight);
		System.out.println();
		System.out.println("new image:");
		Utils.printGreyValues(outTestIm, outTestImWidth, outTestImHeight);
	
		// low pass image
		System.out.println();
		lowPass(testIm,outTestIm, testImWidth, testImHeight);
		System.out.println("low pass image:");
		Utils.printGreyValues(outTestIm, outTestImWidth, outTestImHeight);
		Utils.reset(outTestIm);
		// high pass
		System.out.println();
		highPass(testIm, outTestIm,testImWidth, testImHeight);
		System.out.println("high pass");
		Utils.printGreyValues(outTestIm, outTestImWidth, outTestImHeight);
		Utils.reset(outTestIm);
		// unsharp mask   
		System.out.println();
		unsharpMask(testIm, outTestIm,testImWidth, testImHeight);
		System.out.println("unsharp mask");
		Utils.printGreyValues(outTestIm, outTestImWidth, outTestImHeight);
		System.out.println("test finished\n");
		Utils.reset(outTestIm);
	}
}
// CustomWindow inner class

