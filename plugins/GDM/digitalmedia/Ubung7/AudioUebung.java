/*
 * File:		AudioUebung.java
 * Author:		Klaus Jung
 * Copyright:	(c) 2012 by Klaus Jung, all rights reserved
 */
package digitalmedia.Ubung7;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.awt.*;
import java.io.File;

public class AudioUebung extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static final int borderWidth = 5;
	
	private static final String initialFileName = "test-stereo.wav";
	
	private static final int bufferSize = 4096;	// size of audio processing buffer in bytes
	
	// GUI components
	private static JFrame frame;
	private JButton load;			// file open button
	private JButton play;			// play button
	private JButton stop;			// stop button
	private JCheckBox mono;			// mono checkbox
	private JLabel statusLine;		// to print some status text
	
	
	private File file;						// current audio file
	private int channels = 1;				// number auf audio channels
	private SourceDataLine sourceLine;		// used for audio playback
	private boolean cancel = false;			// true will cancel current playback
	
			
	
	public AudioUebung() {
        super(new BorderLayout(borderWidth, borderWidth));

        setBorder(BorderFactory.createEmptyBorder(borderWidth,borderWidth,borderWidth,borderWidth));
         
        // load the default image
        file = new File(initialFileName);
        
        if(!file.canRead()) file = openFile(); // file not found, choose another audio file
        
         // control panel
        JPanel controls = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0,borderWidth,0,0);

		// load image button
        load = new JButton("Open Audio File");
        load.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		File newFile = openFile();
        		if(newFile != null) {
        	        play.setEnabled(true);
        			file = newFile;
        			processFile();
        		}
        	}        	
        });
        controls.add(load, c);
        
        // play button
        play = new JButton("Play");
        play.setEnabled(file != null);
        play.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		processFile();
        	}        	
        });
        controls.add(play, c);
        
        // stop button
        stop = new JButton("Stop");
        stop.setEnabled(false);
        stop.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
				stopPlayback();
        	}        	
        });
        controls.add(stop, c);
        
        // mono checkbox
        mono = new JCheckBox("Mono");
        controls.add(mono, c);
        
        // status panel
        
        JPanel status = new JPanel(new GridBagLayout());
 
         // some status text
        statusLine = new JLabel(" ");
        
        status.add(statusLine);
        
        
        JPanel component = new JPanel(new GridBagLayout());
        
        status.add(component);
        
        add(controls, BorderLayout.NORTH);
        add(status, BorderLayout.SOUTH);

        processFile();                
	}
	
	private File openFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Audio (*.wav, *.aiff, *.au)", "wav", "aiff", "au");
        chooser.setFileFilter(filter);
        int ret = chooser.showOpenDialog(this);
        if(ret == JFileChooser.APPROVE_OPTION) return chooser.getSelectedFile();
        return null;		
	}
	
	
	private static void createAndShowGUI() {
		// create and setup the window
		frame = new JFrame("Audio Excercise");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        final AudioUebung contentPane = new AudioUebung();
        contentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(contentPane);

        frame.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		contentPane.stopPlayback();
        	}
        });

        // display the window.
        frame.pack();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        frame.setLocation((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2);
        frame.setVisible(true);
	}

	public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
	
	public void stopPlayback() {
		cancel = true;
		stop.setEnabled(false);
		if(sourceLine != null) {
			sourceLine.flush(); 
			sourceLine.stop(); 
			sourceLine.close();
		}
	}
		
	private void processFile() {
		if(file == null)
			return;
		
		new Thread() {
			public void run() {
				cancel = false;
				play.setEnabled(false);
				stop.setEnabled(true);
				load.setEnabled(false);
				statusLine.setText("Playback");

				try {
					AudioInputStream stream = AudioSystem.getAudioInputStream(file);
					AudioFormat inputFormat = stream.getFormat();
					channels = inputFormat.getChannels();
					mono.setEnabled(channels > 1);
					//System.out.println(inputFormat.getFrameSize());
					AudioFormat outputFormat = new AudioFormat( AudioFormat.Encoding.PCM_SIGNED, 
																inputFormat.getSampleRate(),
																16,	// 16 bits PCM
																channels,
																channels * 2, // 2 bytes for 16 bits
																inputFormat.getSampleRate(),
																false); // always use little endian
					DataLine.Info info = new DataLine.Info(SourceDataLine.class, outputFormat); 
					sourceLine = (SourceDataLine)AudioSystem.getLine(info);
					sourceLine.open();
					sourceLine.start();
					
					if(inputFormat.getSampleSizeInBits() != 16) {
						throw new Exception("Only 16 bit samples supported");
					}

					byte[] buffer = new byte[bufferSize];
					short[] samples = new short[bufferSize / 2];
					int numReadBytes = 0;

					long startTime = System.currentTimeMillis();

					while(!cancel){
						numReadBytes = stream.read(buffer,0,bufferSize);
						if(numReadBytes == -1) {
							// reached end of audio input file
							break;
						}
						convertToShort(buffer, numReadBytes, samples, inputFormat.isBigEndian());
						processAudioBuffer(samples, numReadBytes / 2);
					}

					sourceLine.drain(); 
					sourceLine.stop(); 
					sourceLine.close();
					
					long time = System.currentTimeMillis() - startTime;
					statusLine.setText((cancel ? "Stopped at " : "Playback Time = ") + time/1000.0 + " s");
					
				} catch (Exception e) {
					statusLine.setText(e.toString());
					e.printStackTrace();
				}

				play.setEnabled(true);
				stop.setEnabled(false);
				load.setEnabled(true);
			}
		}.start();
	}
	
	
	private void convertToShort(byte[] buffer, int length, short[] samples, boolean isBigEndian) {
		if(samples.length < length/2)
			return;	// sample buffer too small
		
		int i0 = isBigEndian ? 1 : 0;
		int i1 = isBigEndian ? 0 : 1;
		
		for(int i = 0; i < length; i += 2) {
			samples[i>>1] = (short)(((int)buffer[i+i0] & 0xff) | (((int)buffer[i+i1] & 0xff) << 8));
		}
	}
	
	private byte[] convertToByte(short[] samples, int length) {
		length = Math.min(samples.length, length);
		byte[] buffer = new byte[length * 2];
		
		for(int i = 0, j = 0; i < length; i++, j+= 2) {
			buffer[j  ] = (byte) (samples[i] & 0x00ff);
			buffer[j+1] = (byte) ((samples[i] & 0xff00) >> 8);
		}
		return buffer;
	}

	private void playBackBuffer(short[] samples, int length) {
		byte[] buffer = convertToByte(samples, length);
		sourceLine.write(buffer, 0, buffer.length);
	}	
	
	
	private void processAudioBuffer(short[] samples, int length) {
		
		// do some processing with sample data
		
		if(channels == 2 && mono.isSelected()) {
			// convert to mono
			for(int i = 0; i < length; i += 2) {
				int s = ((int)samples[i] + (int)samples[i+1]) / 2;
				// write back in place
				samples[i] = (short)s;
				samples[i+1] = (short)s;
			}
		}
		
		// change volume
		for(int i = 0; i < length; i++) {
			int s = samples[i];
			s *= 2;
			// clip into valid range
			if(s > 0x7fff) s = 0x7fff;
			if(s < - 0x8000) s = - 0x8000;
			// write back in place
			samples[i] = (short)s;
		}
		
		playBackBuffer(samples, length);
	}
	
	
}

