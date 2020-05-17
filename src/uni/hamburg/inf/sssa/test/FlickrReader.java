package uni.hamburg.inf.sssa.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.naming.PartialResultException;

import uni.hamburg.inf.sssa.semantic.ContextInstance;
import uni.hamburg.inf.sssa.semantic.ContextRelationType;
import uni.hamburg.inf.sssa.semantic.SemanticRelation;
import uni.hamburg.inf.sssa.util.architecture.ClassicSingleton;



public class FlickrReader  {

	static BufferedReader reader;
	public static void startReading(String file) throws IOException {
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
	}
	public static void readfile() throws IOException {
		Pattern p ;//= Pattern.compile("\\w*<td>\\w*</td>\\w*");
	   String line = reader.readLine();
	    while (line != null ) {
	    	if(Pattern.matches("<td>(.*)</td>(.*)",line))
	    	{
	    		
	    		line=line.replaceAll("<td>(.*)</td>(.*)", "");
	    		//continue;
	    		//throw new IOException("FOUND");
	    	}
	    	if(line.trim().equals("")){
	    		line = reader.readLine();
	    		continue;
	    	}
	    	//matcj
	    	
	    	if(!(line.endsWith(".")||line.endsWith(".")))
	    	{
	    		line+=".";
	    	}
	    	System.out.println(line);
	    	line = reader.readLine();
	    }
	    
	}
	public static void main (String[] arg)
	{
		try {
			startReading("C:\\Users\\Rekaby\\Desktop\\prepare.txt");
			readfile();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
