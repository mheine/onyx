package voice;
/*
 * Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.SwingUtilities;

import onyx.MusicPlayerGUI;


/**
 * A simple voice-controlled demo showing a simple speech application 
 * built using Sphinx-4. This application uses the Sphinx-4 endpointer,
 * which automatically segments incoming audio into utterances and silences.
 */
public class VoiceControlledGUI {
	
	static MusicPlayerGUI MP;

    /**
     * Main method for running the voice-controlled application.
     */
    public static void main(String[] args) {
        try {
            URL url;
            if (args.length > 0) {
                url = new File(args[0]).toURI().toURL();
            } else {
                url = VoiceControlledGUI.class.getResource("voice.config.xml");
            }

            System.out.println("Loading...");
            
           // MusicPlayerGUI MP = null;
            
            SwingUtilities.invokeLater(new Runnable() {
    			public void run() {
    				try {
    					MP = new MusicPlayerGUI();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			}
    		});

            ConfigurationManager cm = new ConfigurationManager(url);

	    Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
	    Microphone microphone = (Microphone) cm.lookup("microphone");


            /* allocate the resource necessary for the recognizer */
            recognizer.allocate();

            /* the microphone will keep recording until the program exits */
	    if (microphone.startRecording()) {

		System.out.println
		    ("The following commands are available:\n( play | pause | next | previous | shuffle | repeat )");

		while (true) {
		    System.out.println
			("Start speaking. Press Ctrl-C to quit.\n");

                    /*
                     * This method will return when the end of speech
                     * is reached. Note that the endpointer will determine
                     * the end of speech.
                     */ 
		    Result result = recognizer.recognize();
		    
		    if (result != null) {
			String resultText = result.getBestFinalResultNoFiller();
			System.out.println("You said: " + resultText + "\n");
			
			if(resultText.trim().equals("play")) {
				MP.pauseOrPlay();
			}
			
			else if(resultText.trim().equals("pause")) {
				MP.pauseOrPlay();
			}
			
			else if(resultText.trim().equals("next")) {
				MP.playNextTrack();
			}
			
			else if(resultText.trim().equals("previous")) {
				MP.playPreviousTrack();
			}
			
			else if(resultText.trim().equals("shuffle")) {
				MP.simulateShuffle();
			}
			
			
			
			
		    } else {
			System.out.println("Unable to hear voice.\n");
		    }
		}
	    } else {
		System.out.println("Cannot start microphone.");
		recognizer.deallocate();
		System.exit(1);
	    }
        } catch (IOException e) {
            System.err.println("Problem when loading application: " + e);
            e.printStackTrace();
        } catch (PropertyException e) {
            System.err.println("Problem configuring application: " + e);
            e.printStackTrace();
        } catch (InstantiationException e) {
            System.err.println("Problem creating application: " + e);
            e.printStackTrace();
        }
    }
}
