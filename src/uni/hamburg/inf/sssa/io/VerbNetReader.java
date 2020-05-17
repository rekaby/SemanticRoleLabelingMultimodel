/**
 * 
 */

package uni.hamburg.inf.sssa.io;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
public class VerbNetReader {

	private static SssaWrapperLogging log = SssaWrapperLogging.getInstance(VerbNetReader.class);

	private HashMap<String, Set<String>> verbRolesMap =new HashMap<String,  Set<String>>();
	private static VerbNetReader instance = null;
	public static VerbNetReader getInstance() {
	      if(instance == null) {
	         instance = new VerbNetReader();
	      }
	      return instance;
	   }
	
	public  void loadVerbNet() {
		// TODO Auto-generated method stub
		System.out.println(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.verbnetFolderPath));
		File vbFolderPath = new File(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.verbnetFolderPath));
		File[] listOfFiles = vbFolderPath.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".xml")) {
		        //System.out.println("File " + listOfFiles[i].getAbsolutePath());
		        readXML(listOfFiles[i].getAbsolutePath());
		        //System.out.println(verbRolesMap);
				//System.out.println(verbRolesMap.size());
				//System.out.println("---------------------------------------------------");
				
		      } 
		}
		log.logInfo(LogCode.GENERAL_CODE.getCode(),"VerbNet is loaded Succ with "+verbRolesMap.size()+ " Predicates");
		
		//System.out.println("VerbNet is loaded Succ with "+verbRolesMap.size()+ " Predicates");
		//System.out.println(verbRolesMap);
		
	}
	public boolean readXML(String xmlFilePath)  {
        
		File inputFile = new File(xmlFilePath);
        SAXReader reader = new SAXReader();
        
        try {
        	Document document = reader.read( inputFile );
        	Element classElement = document.getRootElement();
        	handleVerbNetClassNode(document,classElement,new ArrayList<String>());
            
            
            
            return true;

        } catch (DocumentException ex) {
            System.out.println(ex.getMessage());
        } 

        return false;
    }
	private void handleVerbNetClassNode(Document document,Node headNode,List<String> parentRoles)
	{
		List<String> tempRoles=new ArrayList<String>();
		List<Node> nodes = document.selectNodes(headNode.getUniquePath()+"/THEMROLES/THEMROLE" );
		for (Node node : nodes) {
			try {
				String role=node.valueOf("@type");
				role=role.replace("-", "_");
				role=role.replace("?", "");
				VerbNetContextRelationType verify=VerbNetContextRelationType.valueOf(role);
				tempRoles.add(SemanticManager.getInstance().getCorelatedSemanticRelationType(verify).toString());
			} catch (Exception e) {
				//System.out.println(document);
				//e.printStackTrace();
			}
			
		}
		//System.out.println(tempRoles);
		tempRoles.addAll(parentRoles);//to consider parents roles and dont change in parent comming list
		nodes = document.selectNodes(headNode.getUniquePath()+"/MEMBERS/MEMBER" );
		for (Node node : nodes) {
			if(verbRolesMap.containsKey(node.valueOf("@name")))
			{
				verbRolesMap.get(node.valueOf("@name")).addAll(tempRoles);
			}
			else
			{
				verbRolesMap.put(node.valueOf("@name"), new HashSet<String>(tempRoles));
			}
			
		}
		//System.out.println(verbRolesMap);
		//System.out.println(verbRolesMap.size());
		nodes = document.selectNodes(headNode.getUniquePath()+"/SUBCLASSES/VNSUBCLASS" );
		for (Node node : nodes) {
			handleVerbNetClassNode(document,node,tempRoles);
		}
		
	}
	
	public HashMap<String, Set<String>> getVerbRolesMap() {
		return verbRolesMap;
	}
	
	
	
}
