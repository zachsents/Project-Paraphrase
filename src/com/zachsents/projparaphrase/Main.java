package com.zachsents.projparaphrase;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class Main
{	
	static JTextArea inputTA, outputTA;
	static JSlider coverageSlider;
	
	public static void main(String[] args) throws IOException
    {
		JFrame frame = new JFrame("Project Paraphrase");
		WordOmitter.loadBlacklist();
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {e.printStackTrace();}
		
		
    	// INPUT TEXT AREA
		inputTA = new JTextArea(5, 20);
    	inputTA.setMargin(new Insets(5, 5, 5, 5));
    	inputTA.setLineWrap(true);
    	
    	JScrollPane input = new JScrollPane(inputTA);
    	
    	
    	// OUTPUT TEXT AREA
    	outputTA = new JTextArea(5, 20);
    	outputTA.setMargin(new Insets(5, 5, 5, 5));
    	outputTA.setLineWrap(true);
    	outputTA.setEditable(false);
    	
    	JScrollPane output = new JScrollPane(outputTA);
    	
    	
    	// COVERAGE ADJUSTMENT
    	coverageSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
    	coverageSlider.setMajorTickSpacing(10);
    	coverageSlider.setMinorTickSpacing(2);
    	coverageSlider.setPaintTicks(true);
    	coverageSlider.setPaintLabels(true);
    	coverageSlider.setPaintTrack(true);
    	coverageSlider.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 10));
    	
    	JLabel coverageLabel = new JLabel("Coverage");
    	coverageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    	coverageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 10));
    	
    	JPanel coverage = new JPanel();
    	coverage.setLayout(new BoxLayout(coverage, BoxLayout.PAGE_AXIS));
    	coverage.add(coverageLabel);
    	coverage.add(coverageSlider);
    	
    	
    	// PARAPHRASE BUTTON
    	JButton paraphraseButton = new JButton("Paraphrase!");
    	paraphraseButton.addActionListener(new ParaphraseButtonListener());
    	paraphraseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    	
    	
    	// BLACKLIST REFRESH BUTTON
    	JButton blacklistRefreshButton = new JButton("Refresh Blacklist");
    	blacklistRefreshButton.addActionListener(new RefreshBlacklistButtonListener());
    	blacklistRefreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    	
    	
    	
    	// BUTTONS PANEL
    	JPanel buttons = new JPanel();
    	buttons.setLayout(new BoxLayout(buttons, BoxLayout.PAGE_AXIS));
    	buttons.add(paraphraseButton);
    	buttons.add(Box.createRigidArea(new Dimension(5, 10)));
    	buttons.add(blacklistRefreshButton);
    	
    	
    	// TOP BAR
    	JPanel topBar = new JPanel();
    	topBar.setLayout(new BoxLayout(topBar, BoxLayout.LINE_AXIS));
    	topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    	topBar.add(coverage);
    	topBar.add(buttons);
    	
    	
    	// FRAME COMPOSITION
    	frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
    	
    	frame.add(topBar);
    	frame.add(input);
    	frame.add(output);
    	
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setPreferredSize(new Dimension(800, 600));
    	
    	frame.pack();
    	frame.setVisible(true);
    	frame.setResizable(true);
    	frame.setLocationRelativeTo(null);
    }
    
    public static String reword(String text)
    {
    	String[] array = text.split(" "), processed = new String[array.length];
    	boolean[] periods = new boolean[array.length], commas = new boolean[array.length], capitalized = new boolean[array.length];
    	for(int i = 0; i < array.length; i++)
    	{	
    		if(array[i].isEmpty()){
    			continue;
    		}
    		
    		String word = array[i];
    		
    		if(word.contains(","))
    		{
    			word = word.replace(",", "");
    			commas[i] = true;
    		}
    		
    		if(word.contains("."))
    		{
    			word = word.replace(".", "");
    			periods[i] = true;
    		}
    		
    		if(Character.isUpperCase(word.charAt(0)))
    		{
    			capitalized[i] = true;
    		}
    		
    		System.out.println(word);
    		
    		if(!WordOmitter.blacklist.contains(word) && Math.random() * 100 <= coverageSlider.getValue())
    		{
    			String replacement = "";
    			 
    			// THESAURUS REQUEST
    	    	try
    	    	{
    	    		URL url = new URL("http://www.thesaurus.com/browse/" + word + "?s=t");

        	        InputStream is = url.openConnection().getInputStream();
        	        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        	        
        	        boolean search = true;

        	        while (search)
        	        {
        	        	String line = br.readLine();
        	        	if(line != null)
        	        	{
        	        		if(line.startsWith("<span class=" + '"' + "text" + '"' + ">"))
            	        	{
            	        		replacement = line.replace("<span class=" + '"' + "text" + '"' + ">", "");
            	        		replacement = replacement.replace("</span>", "");
            	        		search = false;
            	        	}
        	        	} else search = false;
        	        }
    	    	} catch(Exception e) {
    	    		e.printStackTrace();
    	    		System.exit(0);
    	    	}
    	    	processed[i] = replacement.isEmpty() ? (capitalized[i] ? Character.toUpperCase(word.charAt(0)) + word.substring(1) : word) + (commas[i] ? "," : "") + (periods[i] ? "." : "") :
    	    										   (capitalized[i] ? Character.toUpperCase(replacement.charAt(0)) + replacement.substring(1) : replacement) + (commas[i] ? "," : "") + (periods[i] ? "." : "");
    		} else {
    			processed[i] = (capitalized[i] ? Character.toUpperCase(word.charAt(0)) + word.substring(1) : word) + (commas[i] ? "," : "") + (periods[i] ? "." : "");
    		}
    	}
    	
    	String result = "";
    	for(String s : processed)
    	{
    		result = result + s + " ";
    	}
    	
    	return result.replace("null", "");
    }
}