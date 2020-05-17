package uni.hamburg.inf.sssa.properties;

import java.io.InputStream;
import java.lang.invoke.ConstantCallSite;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import uni.hamburg.inf.sssa.io.ConllReaderConll08;

public class SssaProperties {

	private final String defaultPropertiesFilePath="sssa.properties"; 
	private Properties properties=new Properties();
	
	private static SssaProperties instance = null;
	public static SssaProperties getInstance() {
	      if(instance == null) {
	         instance = new SssaProperties();
	      }
	      return instance;
	   }
	public void loadProperties(String optionsFilePath) {
		InputStream in = getClass().getResourceAsStream(getPropertiesFilePath(optionsFilePath));
		try {
			properties.load(in);
			in.close();
		} catch (Exception e) {
			System.out.println("Loading property file is failed");
			e.printStackTrace();
		}
		
	}
	private String getPropertiesFilePath(String optionsFilePath) 
	{
		if(optionsFilePath==null || optionsFilePath.equals(""))
		{
			return defaultPropertiesFilePath;
		}
		else
		{
			return optionsFilePath;
		}
	}
	public String getStringProperty(String property)
	{
		if(properties.containsKey(property))
			return (String)properties.get(property);
		return "";
	}
	public void setStringProperty(String property,String value)
	{
		if(properties.containsKey(property))
			properties.setProperty(property, value);
		
	}
	
	public float getfloatProperty(String property)
	{
		if(properties.containsKey(property))
			return Float.valueOf(properties.get(property).toString());
		return 1.0f;
	}
	public int getIntProperty(String property)
	{
		if(properties.containsKey(property))
			return Integer.valueOf(properties.get(property).toString());
		return 1;
	}
	public boolean getBooleanProperty(String property)
	{
		if(properties.containsKey(property))
			return  Boolean.valueOf((String)properties.get(property));
		return false;
	}
	public Properties getProperties() {
		return properties;
	}
	
	
}
