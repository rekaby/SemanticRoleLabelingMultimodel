/**
 * 
 */

package uni.hamburg.inf.sssa.io;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import uni.hamburg.inf.sssa.logging.LogCode;
import uni.hamburg.inf.sssa.logging.SssaWrapperLogging;
import uni.hamburg.inf.sssa.properties.SssaProperties;
import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.semantic.ContextRelationType;
import uni.hamburg.inf.sssa.semantic.SemanticManager;
import uni.hamburg.inf.sssa.semantic.VerbNetContextRelationType;
import uni.hamburg.inf.sssa.util.architecture.ClassicSingleton;


/**
 * @author Rekaby
 *
 */
public class PropBankReader {

	private static SssaWrapperLogging log = SssaWrapperLogging.getInstance(PropBankReader.class);
	protected String repositoryPath;//=ISssaPropertiesKeys.probBankFolderPath;

	//This map has lemma as keys, per lemma there is a map with Lemma+sense as keys, and list of roles as values 
	protected HashMap<String, Map<String,Set<String>>> bankMap =new HashMap<String,  Map<String,Set<String>>>();
	private static PropBankReader instance = null;
	
	public static PropBankReader getInstance() {
		if(instance == null) {
	         instance = new PropBankReader();
	         instance.repositoryPath=ISssaPropertiesKeys.probBankFolderPath;
	      }
	      return instance;
	   }
	
	public  void loadBank() {
		// TODO Auto-generated method stub
		File pbFolderPath = new File(SssaProperties.getInstance().getStringProperty(repositoryPath));
		File[] listOfFiles = pbFolderPath.listFiles();
		
		for (int i = 0;listOfFiles!=null&& i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".xml")) {
		        //System.out.println("File " + listOfFiles[i].getAbsolutePath());
		        readXML(listOfFiles[i].getAbsolutePath());
		        //System.out.println(verbRolesMap);
				//System.out.println(verbRolesMap.size());
				//System.out.println("---------------------------------------------------");
				
		      } 
		}
		log.logInfo(LogCode.GENERAL_CODE.getCode(),"ProbBank is loaded Succ with "+bankMap.size()+ " Predicates");
		
		//System.out.println("VerbNet is loaded Succ with "+verbRolesMap.size()+ " Predicates");
		//System.out.println(verbRolesMap);
		
	}
	public boolean readXML(String xmlFilePath)  {
        
		File inputFile = new File(xmlFilePath);
        SAXReader reader = new SAXReader();
        
        try {
        	Document document = reader.read( inputFile );
        	Element classElement = document.getRootElement();
        	handleBankClassNode(document,classElement,new ArrayList<String>());
            return true;

        } catch (DocumentException ex) {
            System.out.println(ex.getMessage()+ " in File:"+xmlFilePath);
        } 

        return false;
    }
	private void handleBankClassNode(Document document,Node headNode,List<String> parentRoles)
	{
		String predicate="";
		
		List<Node> nodes = document.selectNodes(headNode.getUniquePath()+"/predicate" );
		for (int i = 0; i < nodes.size(); i++) {
			predicate=nodes.get(i).valueOf("@lemma");
			List<Node> rolesetsNodes = document.selectNodes(nodes.get(i).getUniquePath()+"/roleset" );
			Map<String,Set<String>> rolesetsmap=new HashMap<String, Set<String>>();
			for (Iterator iterator  = rolesetsNodes.iterator(); iterator.hasNext();) {
				
				String sense="";
				Node rolesetnode = (Node) iterator.next();
				sense=rolesetnode.valueOf("@id");
				
				List<Node> rolesNodes = document.selectNodes(rolesetnode.getUniquePath()+"/roles/role");
				Set<String> roleSet = new HashSet<String>();
				for (Iterator iterator2  = rolesNodes.iterator(); iterator2.hasNext();) {
					Node rolenode = (Node) iterator2.next();
					try {
						int id=Integer.parseInt(rolenode.valueOf("@n"));
						roleSet.add("A"+id);
					} catch (Exception e) {
						//System.out.println(e.getMessage());
						//if (rolenode.valueOf("@n")!=null && !rolenode.valueOf("@n").trim().equals("")
						//		&& !rolenode.valueOf("@n").trim().equalsIgnoreCase("M")&& !rolenode.valueOf("@n").trim().equalsIgnoreCase("A")) {
						//	roleSet.add(rolenode.valueOf("@n"));	
						//}
					}
					
					
				}
				rolesetsmap.put(sense, roleSet);
			}
			bankMap.put(predicate, rolesetsmap);
		}
	}
	
	public HashMap<String, Map<String,Set<String>>> getBankRolesMap() {
		return bankMap;
	}
	
}
