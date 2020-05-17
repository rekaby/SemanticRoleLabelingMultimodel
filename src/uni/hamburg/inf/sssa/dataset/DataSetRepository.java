package uni.hamburg.inf.sssa.dataset;

import java.util.ArrayList;
import java.util.List;

import uni.hamburg.inf.sssa.syntactic.DependencyInstanceConll0809;

public class DataSetRepository {

	protected List<DependencyInstanceConll0809> allDataSet=new ArrayList<DependencyInstanceConll0809>();
	protected List<DependencyInstanceConll0809> labeledSet=new ArrayList<DependencyInstanceConll0809>();
	protected List<DependencyInstanceConll0809> unlabeledSet=new ArrayList<DependencyInstanceConll0809>();
	protected List<DependencyInstanceConll0809> tempLabeledSet=new ArrayList<DependencyInstanceConll0809>();
	protected List<DependencyInstanceConll0809> probLabeledSet=new ArrayList<DependencyInstanceConll0809>();
	
	public void addElementToAllDataset(DependencyInstanceConll0809 instance)
	{
		allDataSet.add(instance);
	}
	public void addLabeledElement(DependencyInstanceConll0809 instance)
	{
		labeledSet.add(instance);
	}
	
	public void addLabeledElements(List<DependencyInstanceConll0809> instance)
	{
		labeledSet.addAll(instance);
	}

	public void addUnLabeledElement(DependencyInstanceConll0809 instance)
	{
		unlabeledSet.add(instance);
	}
	public void addUnLabeledElements(List<DependencyInstanceConll0809> instance)
	{
		unlabeledSet.addAll(instance);
	}
	public void removeUnLabeledElement(DependencyInstanceConll0809 instance)
	{
		unlabeledSet.remove(instance);
	}
	public void addTempLabeledElement(DependencyInstanceConll0809 instance)
	{
		tempLabeledSet.add(instance);
	}
	public void addTempLabeledElements(List<DependencyInstanceConll0809> instance)
	{
		tempLabeledSet.addAll(instance);
	}
	public void removeTempLabeledElement(DependencyInstanceConll0809 instance)
	{
		tempLabeledSet.remove(instance);
	}

	public void addProbLabeledElement(DependencyInstanceConll0809 instance)
	{
		probLabeledSet.add(instance);
	}
	public void addProbLabeledElements(List<DependencyInstanceConll0809> instance)
	{
		probLabeledSet.addAll(instance);
	}
	public List<DependencyInstanceConll0809> getLabeledSet() {
		return labeledSet;
	}
	public List<DependencyInstanceConll0809> getUnlabeledSet() {
		return unlabeledSet;
	}
	public List<DependencyInstanceConll0809> getTempLabeledSet() {
		return tempLabeledSet;
	}
	public List<DependencyInstanceConll0809> getProbLabeledSet() {
		return probLabeledSet;
	}
	
	public List<DependencyInstanceConll0809> getAllDataSet() {
		return allDataSet;
	}
	public void mergeIntoList(DependencyInstanceConll0809 element, List<DependencyInstanceConll0809> list)
	{
		if(!list.contains(element))
		{
			list.add(element);
		}
		else
		{
			DependencyInstanceConll0809 currentElement=list.get(list.indexOf(element));
			list.remove(currentElement);//no need to care alot about merge
			list.add(element);//just remove the old element and add the ew version of it, the semantic relations merge has already been done
			
			/*if (!currentElement.hasSemanticRelations()) {
				currentElement.setContextInstance(element.getContextInstance());
			}
			else
			{//here we need to merge the semantic roles;
				currentElement.getContextInstance().mergeSemanticRelations(element.getContextInstance().getSemanticRelations());
			}*/
		}
	}

}
