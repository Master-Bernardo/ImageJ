package digitalmedia.�bung2;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
     Opens an image window and adds a panel below the image
*/
public class DM_U2_4b implements PlugIn {

    ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;
	//default
	private static double brightness = 1;
	private static double contrast = 10;  
	private static double saturation = 10;
	private static double hue = 180;
	
	
    public static void main(String args[]) {
		//new ImageJ();
    	conversionTest();
    	IJ.open("C:/Users/Windows 10/Desktop/ImageJ/plugins/GDM/digitalmedia/�bung2/orchid.jpg");
    	//IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");
		
		DM_U2_4b pw = new DM_U2_4b();
		pw.imp = IJ.getImage();
		pw.run("");
	}
    

	
    private static void conversionTest() {
    	System.out.println("Color CONVERSIONTEST");
		for (int r =0; r< 256; r++) {
			for (int g = 0; g<256; g++) {
				for  (int b = 0; b<256; b++) {
					//TODO implement Methods: RGB2YCbCr and YCbCr2RGB
					double[] detailedYCbCrResult = RGB2YCbCr(r,g,b);
					
					double[]detailedBackConverted = YCbCr2RGB(detailedYCbCrResult[0], detailedYCbCrResult[1], detailedYCbCrResult[2]);
					int backConverted[] = new int[3];
					//zu int casten
					for(int t=0; t<3; t++){
						backConverted[t] = (int)(detailedBackConverted[t]+0.5);
					}
					//TODO use previous line instead of the following line:
					//int backConverted[] = {r,g,b};
					if ( r != backConverted[0] || g != backConverted[1] ||
							b !=  backConverted[2])
					{
						System.out.println( "Your Conversion failed: r: "+ r + " g: " +g + " b: "+b + 
								" != " + backConverted[0] + ", " + backConverted[1]+", "+ backConverted[2]);
					}
				}
			}
		}
		System.out.println("done");
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
	
	/*
	 * Konvertiert RGB in CbCr (Rechnet mit doubles da ints in der Rechnung an genauingkeit verlieren
	 */
	public static double[] RGB2YCbCr(double r, double g, double b){
		double[] result = new double[3];
		//Y
		result[0]= 0.299*r+0.587*g+0.114*b;
		//Cb
		result[1]= -0.168736*r-0.331264*g+0.5*b;
		//Cr
		result[2]= 0.5*r-0.418688*g-0.081312*b;
		return result;
	}
	
	public static double[] YCbCr2RGB(double y, double cb, double cr){
		double[] result = new double[3];
		//r
		result[0]=y+1.402*cr;
		//g
		result[1]=y-0.3441*cb-0.7141*cr;
		//b
		result[2]=y+1.772*cb;
		return result;
	}
	//TODO Bildmanipulation hier hinzuf�gen
	/*
	 * Konvertiert YCbCr in RGB mit Helligkeits�nderung
	 */
	public static double[] YCbCr2RGB(double y, double cbPre, double crPre, double brightness, double contrast, double saturation, double hue){
		double[] result = new double[3];
		
		//saturation
		double cbSat=cbPre*(saturation/10);
		double crSat=crPre*(saturation/10);
		
		//hue
		double phi = hue/60+3;  //deltacb /deltacr      phis sollte MAth.pi/2   irgendsowas  - Winkel von grad in rad umwandeln
		//double phi = hue/(2*Math.PI)+128;
		//drehung
		double cb= (Math.cos(phi)*cbSat+Math.sin(phi)*crSat);
		double cr= (-(Math.sin(phi))*cbSat+Math.cos(phi)*crSat);
		
		
		
		//r
		double rPre = y+1.402*cr;
		double r = (contrast/10)*(rPre-127.5)+127.5+brightness;
		//avoid overflow
		if(r>255)r=255;
		if(r<0)r=0;
		result[0]=r;
		//g
		double gPre =y-0.3441*cb-0.7141*cr;
		double g = (contrast/10)*(gPre-127.5)+127.5+brightness;
		if(g>255)g=255;
		if(g<0)g=0;
		result[1]=g;
		//b
		double bPre = y+1.772*cb;
		double b = (contrast/10)*(bPre-127.5)+127.5+brightness;
		if(b>255)b=255;
		if(b<0)b=0;
		result[2]=b;
		
		return result;
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
    
    
    class CustomWindow extends ImageWindow implements ChangeListener {
         
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		//TODO hier neue Slider und Variablen initializieren
		private JSlider jSliderBrightness;
		private JSlider jSliderContrast;
		private JSlider jSliderSaturation;
		private JSlider jSliderHue;
		private JButton defaultButton;
		
		

		CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
        }
    
        void addPanel() {
        	//JPanel panel = new JPanel();
        	Panel panel = new Panel();
        	//TODO hier werden neue Panels hinzugef�gt
            panel.setLayout(new GridLayout(5, 1));
            jSliderBrightness = makeTitledSlider("Helligkeit", 0, 200, 100);
            jSliderContrast = makeTitledSlider("Kontrast", 0, 100, 10);
            jSliderSaturation = makeTitledSlider("S�ttigung", 0, 50, 10);
            jSliderHue = makeTitledSlider("Farbe", 0, 360, 180);
          //defaultButton
    		defaultButton = new JButton("Default");
    		 defaultButton.addActionListener(new ButtonClass());
            panel.add(jSliderBrightness);
            panel.add(jSliderContrast);
            panel.add(jSliderSaturation);
            panel.add(jSliderHue);
            panel.add(defaultButton);
           
            
            add(panel);
            
            pack();
         }
      
        private JSlider makeTitledSlider(String string, int minVal, int maxVal, int val) {
		
        	JSlider slider = new JSlider(JSlider.HORIZONTAL, minVal, maxVal, val );
        	Dimension preferredSize = new Dimension(width, 50);
        	slider.setPreferredSize(preferredSize);
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(), 
					string, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
			slider.setMajorTickSpacing((maxVal - minVal)/10 );
			slider.setPaintTicks(true);
			slider.addChangeListener(this);
			
			return slider;
		}
        
        private void setSliderTitle(JSlider slider, String str) {
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
				str, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
		}

		public void stateChanged( ChangeEvent e ){
			JSlider slider = (JSlider)e.getSource();
			//TODO hier kommt auch was zu den Slidern
			if (slider == jSliderBrightness) {
				brightness = slider.getValue()-100;
				String str = "Helligkeit " + brightness; 
				setSliderTitle(jSliderBrightness, str); 
			}
			
			if (slider == jSliderContrast) {
				contrast = slider.getValue();
				String str = "Kontrast " + contrast; 
				setSliderTitle(jSliderContrast, str); 
			}
			
			if (slider == jSliderSaturation) {
				saturation = slider.getValue();
				String str = "S�ttigung " + saturation; 
				setSliderTitle(jSliderSaturation, str); 
			}
			
			if (slider == jSliderHue) {
				hue = slider.getValue();
				String str = "Farbe " + hue; 
				setSliderTitle(jSliderHue, str); 
			}
			
			changePixelValues(imp.getProcessor());
			
			imp.updateAndDraw();
		}


		private void changePixelValues(ImageProcessor ip) {
			
			// Array fuer den Zugriff auf die Pixelwerte
			int[] pixels = (int[])ip.getPixels();
			
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
					
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					
					
					// anstelle dieser drei Zeilen später hier die Farbtransformation durchführen,
					// die Y Cb Cr -Werte verändern und dann wieder zurücktransformieren
					//int rn = (int) (r + brightness);
					//int gn = (int) (g + brightness);
					//int bn = (int) (b + brightness);
					
					double[]ycbcr= RGB2YCbCr(r,g,b);
					int Y =(int) (ycbcr[0]+0.5);
					int cb=(int) (ycbcr[1]+0.5);
					int cr=(int) (ycbcr[2]+0.5);
					
					//TODO
					//hier wird das Bild manipuliert
					double[]rgb = YCbCr2RGB(Y,cb,cr,brightness, contrast, saturation, hue);
					int rn= (int) (rgb[0]+0.5);
					int gn= (int) (rgb[1]+0.5);
					int bn= (int) (rgb[2]+0.5);
					
					// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
					
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}
		
    } // CustomWindow inner class
    
    //ButtonCLass
    public class ButtonClass implements ActionListener{
    	public void actionPerformed(ActionEvent e){
    		brightness = 1;
    		contrast = 10;  
    		saturation = 10;
    		hue = 180;
    		imp.updateAndDraw();
    	}
    }
} 


