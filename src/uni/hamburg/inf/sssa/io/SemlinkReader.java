/**
 * 
 */

package uni.hamburg.inf.sssa.io;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
public class SemlinkReader {

	private static SssaWrapperLogging log = SssaWrapperLogging.getInstance(SemlinkReader.class);
//VerbLemma->Probbank role->set of verbnet roles
	private HashMap<String, HashMap<String, Set<String>>> verbnetSemlinkRolesMap =new HashMap<String, HashMap<String, Set<String>>>();
	private static SemlinkReader instance = null;
	public static SemlinkReader getInstance() {
	      if(instance == null) {
	         instance = new SemlinkReader();
	      }
	      return instance;
	   }
	
	public  void loadSemlink() {
		// TODO Auto-generated method stub
		System.out.println(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.semLinkFilePath));
		File semlinkvbFilePath = new File(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.semLinkFilePath));
		
		readXML(semlinkvbFilePath.getAbsolutePath());
		
		log.logInfo(LogCode.GENERAL_CODE.getCode(),"SemLink is loaded Succ with "+verbnetSemlinkRolesMap.size()+ " Predicates");
		
		//System.out.println("VerbNet is loaded Succ with "+verbRolesMap.size()+ " Predicates");
		//System.out.println(verbRolesMap);
		
	}
	public boolean readXML(String xmlFilePath)  {
        
		File inputFile = new File(xmlFilePath);
        SAXReader reader = new SAXReader();
        
        try {
        	Document document = reader.read( inputFile );
        	Element classElement = document.getRootElement();
        	List<Node> nodes = document.selectNodes(classElement.getUniquePath()+"/predicate" );
        	for (Node node:nodes)
        	{
        		HashMap<String, Set<String>> map=	handleVerbNetClassNode(document,node,new ArrayList<String>());
        		verbnetSemlinkRolesMap.put(node.valueOf("@lemma"), map);
        	}
        	
            
            
            
            return true;

        } catch (DocumentException ex) {
            System.out.println(ex.getMessage());
        } 

        return false;
    }
	private HashMap<String, Set<String>> handleVerbNetClassNode(Document document,Node headNode,List<String> parentRoles)
	{
		HashMap<String, Set<String>> results=new HashMap<String, Set<String>>();
		
		List<Node> nodes = document.selectNodes(headNode.getUniquePath()+"/argmap" );
		for (Node node : nodes) {
			try {
				List<Node> rolenodes = document.selectNodes(node.getUniquePath()+"/role" );
				for (Node rolenode : rolenodes) {
					String pbrole="A"+rolenode.valueOf("@pb-arg	");
					String vnrole=rolenode.valueOf("@vn-theta");
					vnrole=vnrole.replace("-", "_");
					vnrole=vnrole.replace("?", "");
					if (results.keySet().contains(pbrole))
					{
						results.get(pbrole).add(vnrole);
					}
					else
					{
						Set<String> tempVNroles=new HashSet<String>();
						tempVNroles.add(vnrole);
						results.put(pbrole, tempVNroles);
					}
					
				}
				
			} catch (Exception e) {
				System.out.println(document);
				e.printStackTrace();
			}
			
		}
		return results;
				
	}
	
	public HashMap<String, HashMap<String, Set<String>>> getVerbnetSemlinkRolesMap() {
		return verbnetSemlinkRolesMap;
	}
	
	public  Set<String> getRelatedVerbnetSemlinkRoles(String lemma, Set<String> pbRoles) {
		Set<String> results=new HashSet<String>();
		HashMap<String, Set<String>> map=verbnetSemlinkRolesMap.get(lemma);
		for (String pbRole:pbRoles)
		{
			if (map!=null && map.keySet().contains(pbRole))
			{
				results.addAll(map.get(pbRole));	
			}
		}
		return results;
	}
	
	
}
