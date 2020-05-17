package uni.hamburg.inf.sssa.io;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public abstract class DependencyWriter {
	public BufferedWriter writer;
	
	
	//public static DependencyWriter createDependencyWriter( ) {
		
	//		return new ConllWriter();
	
	//}
	
	public abstract void writeInstance(uni.hamburg.inf.sssa.syntactic.DependencyInstanceConll0809 inst) throws IOException;
	
	public void startWriting(String file) throws IOException {
		writer = new BufferedWriter(new FileWriter(file));
	}
	
	public void close() throws IOException {
		if (writer != null) writer.close();
	}
	
}
