package digitalmedia;

import gdmvalidation.Ueb1Validation;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

//erste Uebung (elementare Bilderzeugung)

public class Ubung1_4b implements PlugIn {

	final static String[] choices = { "Schwarzes Bild", "Gelbes Bild", "Schwarz/Weiss Verlauf",
			"Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf", "Italienische Fahne", "Kegelschnitt", "Japanische Fahne",
			"Japanische Fahne mit weichen Kanten", "Streifenmuster", "advanced Stripes" };

	private String choice;

	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen
		ij.exitWhenQuitting(true);

		Ubung1_4b imageGeneration = new Ubung1_4b();
		imageGeneration.run("");
	}

	public void run(String arg) {

		int width = 566; // Breite
		int height = 400; // Hoehe

		// RGB-Bild erzeugen
		ImagePlus imagePlus = NewImage.createRGBImage("DM_U1", width, height, 1, NewImage.FILL_BLACK);
		ImageProcessor ip = imagePlus.getProcessor();

		// Arrays fuer den Zugriff auf die Pixelwerte
		int[] pixels = (int[]) ip.getPixels();

		dialog();

		// //////////////////////////////////////////////////////////////
		if (choice.equals("Schwarzes Bild")) {
			int r, g, b;
			r = g = b = 0;
			setImageToColor(pixels, width, height, r, g, b);
		} else if (choice.equals("Gelbes Bild")) {
			int r, g, b;
			// TODO set the color to yellow
			r = 255;
			g = 255;
			b = 0;
			setImageToColor(pixels, width, height, r, g, b);
		} else if (choice.equals("Schwarz/Weiss Verlauf")) {
			blackToWhite(pixels, width, height);
			Ueb1Validation.validateBlackToWhite(pixels, width, height);
		} else if (choice.equals("Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf")) {
			black2RedAndBlack2Blue(pixels, width, height);
			Ueb1Validation.validateBlack2RedAndBlack2Blue(pixels, width, height);
		} else if (choice.equals("Italienische Fahne")) {
			flagItalian(pixels, width, height);
			Ueb1Validation.validateFlagItalian(pixels, width, height);
		} else if (choice.equals("Kegelschnitt")) {
			conicSection(pixels, width, height);
		} else if (choice.equals("Japanische Fahne")) {
			flagOfJapan(pixels, width, height);
		} else if (choice.equals("Japanische Fahne mit weichen Kanten")) {
			flagOfJapanSmooth(pixels, width, height);
		} else if (choice.equals("Streifenmuster")) {
			stripes(pixels, width, height);
		} else if (choice.equals("advanced Stripes")) {
			advancedStripes(pixels, width, height);
		}

		// //////////////////////////////////////////////////////////////////

		// neues Bild anzeigen
		imagePlus.show();
		imagePlus.updateAndDraw();
	}

	private void blackToWhite(int[] pixels, int width, int height) {
		// TODO set some values here
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g, b;
				// TODO code for the image.
				r = 255 * x / (width - 1);
				g = b = r;
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void black2RedAndBlack2Blue(int[] pixels, int width, int height) {
		// TODO set some values here
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// dasselbe wie vorher, nur statt width wird die height genommen und
			// nur blau-> blau-schwarz
			for (int x = 0; x < width; x++) {
				// Schleife ueber die x-Werte
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g;
				// TODO code for the image.
				// dasselbe wie vorher, nur dass g=0 zu rot-schwarz f¸hrt
				int b = 255 * y / (height - 1);
				r = 255 * x / (width - 1);
				g = 0;
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void flagItalian(int[] pixels, int width, int height) {
		// TODO set some values here
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g, b;
				r = b = g = 0;
				// TODO code for the flag.
				// width wird durch 3 geteilt, darauf basierend mit einer
				// if-Schleife
				if (x < width / 3) {
					g = 255;
				} else if (x >= (width / 3) * 2) {
					r = 255;
					b = g = 0;
				} else {
					r = b = g = 255;
				}
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void conicSection(int[] pixels, int width, int height) {
		// TODO set some values here
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g, b;
				// TODO code for the flag.
				r = g = b = 0;
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void flagOfJapan(int[] pixels, int width, int height) {
		// TODO set some values here
		int xMitte = width / 2;
		int yMitte = height / 2;
		int radius = width / 4;
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g, b;
				// TODO code for the flag.
				int deltaX = x - xMitte;
				int deltaY = y - yMitte;
				// Wenn Abstand kleiner als radius zum Mittelpunkt, dann rot
				// f‰rben
				if (Math.pow(deltaX, 2) + Math.pow(deltaY, 2) < Math.pow(radius, 2)) {
					// besser deltaX * deltaX statt pow in diesem Fall
					r = 255;
					b = g = 0;
				} else {
					r = g = b = 255;
				}
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	// Aufgabe g - japanische Flagge mit weichen √úberg√§ngen - FERTIG!
	// again -> xMitte, yMitte, radius
	// unser Bereich in dem es smooth wird ist zwischen zwei Kreisen um den
	// Mittelpunkt
	// wir unterscheiden die Bereiche:
	// Abstand Mittelpunkt bis innerer radius
	// Abstand innerer Radius und √§u√üerer Radius
	// smoothness wird durch g = b = 255*radiussq/(radius-radius2) erreicht
	private void flagOfJapanSmooth(int[] pixels, int width, int height) {
		// TODO set some values here
		final int xMitte = width / 2;
		final int yMitte = height / 2;
		final int radius = width / 4;
		final int radius2 = width / 6;
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g, b;
				// TODO code for the flag.
				// Berechnen des Abstandes genannt radiussq
				int radiussq = (int) Math.sqrt((((y - yMitte) * (y - yMitte)) + ((x - xMitte) * (x - xMitte))));

				if (radius > (radiussq) && radiussq > radius2) {
					g = b = 255 * radiussq / (radius - radius2);
					r = 255;
				} else if (radius > radiussq) {
					g = b = 0;
					r = 255;
				} else {
					r = g = b = 255;
				}
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void TrippyflagOfJapanSmooth(int[] pixels, int width, int height) {
		// TODO set some values here

		int xMitte = width / 2;
		int yMitte = height / 2;
		int radius = width / 5;
		int softenerBorder = width / 4;

		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g, b;
				// TODO code for the flag.
				int deltaX = Math.abs(x - xMitte);
				int deltaY = Math.abs(y - yMitte);
				if (Math.pow(deltaX, 2) + Math.pow(deltaY, 2) < Math.pow(radius, 2)) {
					r = 255;
					b = g = 0;
				} else if (Math.pow(deltaX, 2) + Math.pow(deltaY, 2) < Math.pow(softenerBorder, 2)) {
					// softness code
					// der Abstand zwischen 0 und 255 = softenerBorder - radius
					// Diesen Abstand nochmal in 255 unterteilen
					int sonftMin = radius;
					int softMax = softenerBorder;
					// wurzel
					double distance = Math.floor((Math.pow(deltaX, 2) + Math.pow(deltaY, 2)));
					int softArea = radius - softenerBorder;
					r = (int) (distance * x / softArea);

					g = b = 0;
				} else {
					r = g = b = 255;
				}
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	// Aufgabe h - vertikale Streifen, abwechselnd schwarz wei√ü
	private void stripes(int[] pixels, int width, int height) {
		// TODO set some values here
		final int stripeNum = 12;
		final double stripe = (double) width / stripeNum;
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g, b;
				// TODO code for the flag.
				if ((int) (x / stripe) % 2 == 0) {
					r = g = b = 255;
				} else {
					r = g = b = 0;
				}
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}


	// Aufgabe h - vertikale Streifen, abwechselnd schwarz wei√ü
		private void advancedStripes(int[] pixels, int width, int height) {
			// TODO set some values here
			final int columnNum = 12;
			final int lineNum = 12;
			int verticalIndex, horizontalIndex;
			final double column = (double) width / columnNum;
			final double line = (double) height / lineNum;
			// Schleife ueber die y-Werte
			for (int y = 0; y < height; y++) {
				verticalIndex = (int) (y / line);
				// Schleife ueber die x-Werte
				for (int x = 0; x < width; x++) {
					int pos = y * width + x; // Arrayposition bestimmen
					int r, g, b;
					// TODO code for the flag.
					horizontalIndex = (int) (x / column);
					if (horizontalIndex % 2 == 0) {
						r = g = b = 255;
					} else {
						r = g = b = 0;
					}
					if (horizontalIndex == verticalIndex) {
						r = 255;
						g = b = 0;
					}
					pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
				}
			}
		}
	private void setImageToColor(int[] pixels, int width, int height, int r, int g, int b) {
		int color = 0xFF000000 | (r << 16) | (g << 8) | b;
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				// Werte zurueckschreiben
				pixels[pos] = color;
			}
		}
	}

	private void dialog() {
		// Dialog fuer Auswahl der Bilderzeugung
		GenericDialog gd = new GenericDialog("Bildart");
		gd.addChoice("Bildtyp", choices, choices[0]);
		gd.showDialog(); // generiere Eingabefenster
		choice = gd.getNextChoice(); // Auswahl uebernehmen

		if (gd.wasCanceled())
			System.exit(0);
	}

	private void flagOfJapanSmoothTHEEYE(int[] pixels, int width, int height) {
		// TODO set some values here

		int xMitte = width / 2;
		int yMitte = height / 2;
		int radius = width / 5;
		int softenerBorder = width / 4;

		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g, b;
				// TODO code for the flag.
				int deltaX = Math.abs(x - xMitte);
				int deltaY = Math.abs(y - yMitte);
				// derselbe code f¸r den mittleren kreis
				if (Math.pow(deltaX, 2) + Math.pow(deltaY, 2) < Math.pow(radius, 2)) {
					r = 255;
					b = g = 0;
					// Der fall f¸r den zwischenbereich - hier muss ein Verlauf
					// zustandekomen
				} else if (Math.pow(deltaX, 2) + Math.pow(deltaY, 2) < Math.pow(softenerBorder, 2)) {
					// softness code
					// der Abstand zwischen 0 und 255 = softenerBorder - radius
					// Diesen Abstand nochmal in 255 unterteilen
					int distance = (int) (Math.floor((Math.pow(deltaX, 2) + Math.pow(deltaY, 2))) - radius);
					int sonftMin = radius;
					int softMax = softenerBorder;
					int softenerArea = softenerBorder - radius;
					r = distance;

					g = b = 0;
				} else {
					r = g = b = 255;
				}
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}
}