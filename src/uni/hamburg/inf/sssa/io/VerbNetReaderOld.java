/**
 * 
 */

package uni.hamburg.inf.sssa.io;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;




import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



/**
 * @author Rekaby
 *
 */
public class VerbNetReaderOld {

	private HashMap<String, Set<String>> verbRolesMap =new HashMap<String,  Set<String>>();
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new VerbNetReaderOld().loadVerbNet();
		
		
	}
	
	public  void loadVerbNet() {
		// TODO Auto-generated method stub
		readXML("C:\\Users\\Rekaby\\Desktop\\verbnet-3.2\\new_vn\\admire-31.2.xml");
		System.out.println(verbRolesMap);
		System.out.println("Besm Allah");
	}
	public boolean readXML(String xml) {
        
        Document dom;
       
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use the factory to take an instance of the document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // parse using the builder to get the DOM mapping of the    
            // XML file
            dom = db.parse(xml);

            org.w3c.dom.Element doc = dom.getDocumentElement();
            
            handleVerbNetClassNode(dom,doc);
           /* System.out.println(doc.getNodeName());
            getTextValue(doc, doc.getNodeName());
            getTextValue(doc, "MEMBERS");
            getTextValue(doc, "MEMBER");
            getTextValue(doc, "VNSUBCLASS");
           */
            /*role2 = getTextValue(role2, doc, "MEMBERS");
            if (role2 != null) {
                if (!role2.isEmpty())
                    rolev.add(role2);
            }
            role3 = getTextValue(role3, doc, "role3");
            if (role3 != null) {
                if (!role3.isEmpty())
                    rolev.add(role3);
            }
            role4 = getTextValue(role4, doc, "role4");
            if ( role4 != null) {
                if (!role4.isEmpty())
                    rolev.add(role4);
            }*/
            return true;

        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
        } catch (SAXException se) {
            System.out.println(se.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

        return false;
    }
	private void handleVerbNetClassNode(Document dom,org.w3c.dom.Element node)
	{
		List<String> tempRoles=new ArrayList<String>();
		System.out.println(node.getChildNodes().getLength());
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			System.out.println(node.getChildNodes().item(i).getNodeType() + node.getChildNodes().item(i).getNodeName());
		}
		
		
		Node themRolesNode=node.getChildNodes().item(3);//THEMROLES
		NodeList nl=themRolesNode.getChildNodes();//THEMROLE list
		for (int i = 0; i < nl.getLength(); i+=2) {
			org.w3c.dom.Element element = (Element) dom.adoptNode(nl.item(i));
			tempRoles.add(element.getAttribute("type"));
		}
		
		
		Node membersNode=node.getChildNodes().item(1);//MEMBERS
		 nl=membersNode.getChildNodes();//MEMBER list
		for (int i = 1; i < nl.getLength(); i+=2) {
			Element element = (Element) dom.adoptNode(nl.item(i));
			if(verbRolesMap.containsKey(element.getAttribute("name")))
			{
				verbRolesMap.get(element.getAttribute("name")).addAll(tempRoles);
			}
			else
			{
				verbRolesMap.put(element.getAttribute("name"), new HashSet<String>(tempRoles));
			}
			
		}
		System.out.println(verbRolesMap);
		
	}
	private String getTextValue(Element doc, String tag) {
	    String value ="";
	    NodeList nl;
	    nl = doc.getElementsByTagName(tag);
	    System.out.println(tag+ " "+nl.getLength());
	    if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
	    	
	        value = nl.item(0).getFirstChild().getNodeValue();
	    }
	    System.out.println(tag+ " "+ value);
	    return value;
	}

}
