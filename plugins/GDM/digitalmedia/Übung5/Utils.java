package digitalmedia.Übung5;

public class Utils {

	public static void printGreyValues( int [] image, int width, int height) {
    	for ( int yi = 0; yi< height; yi++ ) {
        	for (int xk = 0; xk< width; xk++) {
        		int col = (image[ yi* width + xk ] >> 16) & 0xff;
        		System.out.print( col+" ");
        	}
        	System.out.println();
        }
    }
    
    public static void makeRGB(int [] img) {
    	for (int i =0; i< img.length; i++)
    		img[i] = 0xFF000000| (0xFF & img[i]) <<16 | (0xFF & img[i]) <<8 |(0xFF & img[i]);
    }
    
	public static void reset(int[] image) {
		for ( int i =0; i < image.length; i++)
        	image[i] = 0xFF000000;
	}

}
