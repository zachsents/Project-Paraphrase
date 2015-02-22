package com.zachsents.projparaphrase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class WordOmitter
{
	public static List<String> blacklist = new ArrayList<String>();
	
	public static void loadBlacklist()
	{
		File file = new File("blacklist.txt");
		try {
			blacklist = Files.readAllLines(file.toPath());
		} catch(IOException e) {}
	}
}