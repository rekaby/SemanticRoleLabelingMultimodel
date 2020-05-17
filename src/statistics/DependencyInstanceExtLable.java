package statistics;

import uni.hamburg.inf.sssa.syntactic.DependencyInstanceConll0809;

public class DependencyInstanceExtLable extends DependencyInstanceConll0809{

	public String[] extendedLable;

	

	public DependencyInstanceExtLable(DependencyInstanceConll0809 a) {
		super(a);
		this.gpostags=a.gpostags;
		this.ppostags=a.ppostags;
		this.splitForms08=a.splitForms08;
	//	for (int i = 0; i < a.length; i++) {
			//this.splitForms08[i].replaceAll("\\", "\\");
		//}
		
		this.splitPpos08=a.splitPpos08;
		
		//this.cpostags=a.cpostags;
		// TODO Auto-generated constructor stub
	}
	
	
}
