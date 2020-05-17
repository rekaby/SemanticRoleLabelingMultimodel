package uni.hamburg.inf.sssa.io;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public  class PlanFileWriter {
	public BufferedWriter writer;
	
	private static PlanFileWriter instance = null;
	public static PlanFileWriter getInstance() {
	      if(instance == null) {
	         instance = new PlanFileWriter();
	      }
	      return instance;
	   }
	
	public  void writeInstance(String str)
	{
		try {
			writer.write(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startWriting(String file) throws IOException {
		writer = new BufferedWriter(new FileWriter(file));
	}
	public void appendWriting(String file) throws IOException {
		writer = new BufferedWriter(new FileWriter(file,true));
	}
	public void close() throws IOException {
		if (writer != null) writer.close();
	}
	
}
