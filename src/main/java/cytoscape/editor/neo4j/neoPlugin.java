package cytoscape.editor.neo4j;

import cytoscape.CyNode;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.plugin.*;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CyNodeView;

import giny.model.Edge;
import giny.view.EdgeView;

import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.Iterator;
import org.neo4j.graphdb.*;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import ding.view.DNodeView;


public class neoPlugin extends CytoscapePlugin implements ActionListener{
	
    GraphDatabaseService graphDb = new EmbeddedGraphDatabase("neo4j/DBase");;
    CyNetworkView newView=null;
		
	public neoPlugin(){
		initializePlugin();
	}
	
	public class SaveActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent event) {
			
			saveGraphToDatabase();
						
		}
		
	}
	
	public class CreateActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent event) {
							
	    	createGraphOneTime();					
			
		}
		
	}
	
	public class ShutDownActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent event) {
					
			graphDb.shutdown();			
			
		}
		
	}		
	
	public void initializePlugin(){
		
		JMenuItem myLoadPluginItem=new JMenuItem("Load from GraphDB");
		myLoadPluginItem.addActionListener(this);
		
		JMenuItem mySavePluginItem=new JMenuItem("Save to GraphDB");
		mySavePluginItem.addActionListener(new SaveActionListener());		
		
		JMenuItem createPluginItem=new JMenuItem("Create GraphDb");
		createPluginItem.addActionListener(new CreateActionListener());
		
		JMenuItem shutDownPluginItem=new JMenuItem("ShutDown GraphDb");
		shutDownPluginItem.addActionListener(new ShutDownActionListener());				
		
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins").addSeparator();
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins").add(createPluginItem);
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins").add(myLoadPluginItem);		
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins").add(mySavePluginItem);
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins").add(shutDownPluginItem);
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins").addSeparator();		
	}
	
	public void createEmptyNetwork(){
		
		CyNetwork newNet = Cytoscape.createNetwork(new int[] {  }, new int[] {  },
				   CytoscapeEditorManager.createUniqueNetworkName(),
				   null,
				   false);		 
		 
		newView = Cytoscape.createNetworkView(newNet,
						    newNet.getTitle(),
						    null,
						    Cytoscape.getVisualMappingManager().getVisualStyle());
	
		CytoscapeEditorManager.setEditorForView(newView, CytoscapeEditorManager.getCurrentEditor());		 
		 
		CytoscapeEditorManager.setupNewNetworkView(newView);		
	}
	
    public enum MyRelationshipTypes implements RelationshipType {
        ArrowTo
    }	
    
	public void createGraphOneTime(){
		
	    Transaction tx = graphDb.beginTx();
	
	    try {
	    	
	        Node firstNode = graphDb.createNode();
	        Node secondNode = graphDb.createNode();
	        Node thirdNode = graphDb.createNode();
	        Node fourthNode = graphDb.createNode();
	
	        firstNode.setProperty("Type", "Node");
	        firstNode.setProperty("Label","Ca");
	
	        secondNode.setProperty("Type", "Node");
	        secondNode.setProperty("Label","Mg");
	
	        thirdNode.setProperty("Type", "Node");
	        thirdNode.setProperty("Label","Al");
	
	        fourthNode.setProperty("Type", "Node");
	        fourthNode.setProperty("Label","Mn");
	
	        firstNode.createRelationshipTo(secondNode, MyRelationshipTypes.ArrowTo);
	        firstNode.createRelationshipTo(thirdNode, MyRelationshipTypes.ArrowTo);
	        fourthNode.createRelationshipTo(firstNode, MyRelationshipTypes.ArrowTo);
	        fourthNode.createRelationshipTo(thirdNode, MyRelationshipTypes.ArrowTo);            
	
	        tx.success();
	    }
	    finally
	    {
	        tx.finish();        
	    }    	
		    	            	
	}
		
	public void readEntireGraph(){
	
	Transaction tx = graphDb.beginTx();
	
	try {
		
	    Iterator<Node> itr=graphDb.getAllNodes().iterator();
	    
	    itr.next();
	    //Ignore the first node
	    //It is a reference node
	
	    while(itr.hasNext()){
	        Node diffNode=itr.next();
	        
	        //Have to add this node to the graph
	        
	        if(((String)diffNode.getProperty("Type")).equals("Node"))
	        {
	        	if(diffNode.hasProperty("xPos") && diffNode.hasProperty("yPos"))
	        	{                		
	        		Point2D location=new Point2D.Double( (Double)diffNode.getProperty("xPos") , (Double)diffNode.getProperty("yPos") ) ;
	        		CytoscapeEditorManager.getEditorForView(newView).addNode((String)diffNode.getProperty("Label"), "Identifier", String.valueOf(diffNode.getId()), location);
	        	}
	        	else	
	        		CytoscapeEditorManager.getEditorForView(newView).addNode((String)diffNode.getProperty("Label"), "Identifier", String.valueOf(diffNode.getId()) );
	        }
	        
	        
	
	    }            
	    
	    //Now, we have to add edges.
	    //All the nodes are already available
	    
	    itr=graphDb.getAllNodes().iterator();
	    itr.next();
	    
	    while(itr.hasNext())
	    {
	        Node diffNode=itr.next();
	                        
	        if(((String)diffNode.getProperty("Type")).equals("Node"))
	        {
	        	Iterator<Relationship> relItr=diffNode.getRelationships(Direction.OUTGOING).iterator();
	        	
	        	while(relItr.hasNext())
	        	{
	        		Relationship rel=relItr.next();
	        		
	        		//diffNode--->end node                		
	        		//This string is used to get hold of the corresponding nodes in graphView
	        		                		
	        		String string1=((String)diffNode.getProperty("Label"));
	        		String string2=((String)rel.getEndNode().getProperty("Label"));                		
	        		
	        		Iterator<CyNode> cyNodeIterator=newView.getNetwork().nodesIterator();
	        		int counter=0;
	        		giny.model.Node node_1=null,node_2=null;
	        		
	        		while(counter!=2 && cyNodeIterator.hasNext())
	        		{
	        			CyNode tempNode=cyNodeIterator.next();
	        			                			                			
	        			if(tempNode.getIdentifier().equals(string1))
	        			{
	        				node_1=tempNode;
	        				counter++;
	        			}
	        			else if(tempNode.getIdentifier().equals(string2))
	        			{
	        				node_2=tempNode;
	        				counter++;                				
	        			}
	        		}                		
	        		
	        		CytoscapeEditorManager.getEditorForView(newView).addEdge(node_1, node_2, CytoscapeEditorManager.EDGE_TYPE , rel.getType().name(), true, "Directed");
	            		
	            	}
	            	
	            }
	
	        }            
	
	        tx.success();
	    }
	    finally
	    {
	        tx.finish();
	    }				            
		
	}
	
	public void traverseGraphFromFirstNode(){
		
        Transaction tx = graphDb.beginTx();

        try {
        	
    		Iterator<Node> itr=graphDb.getAllNodes().iterator(); 
    		itr.next();
    	
    		Traverser arrowTraverser=itr.next().traverse( Traverser.Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL, MyRelationshipTypes.ArrowTo, Direction.BOTH);

        	for(Node node : arrowTraverser)
        		System.out.println(node.getId()+" "+node.getProperty("Label")+" "+node.getProperty("type"));            

            tx.success();
        }
        finally
        {
            tx.finish();
        }
				  	
	}
		
public void saveGraphToDatabase(){
	

    Transaction tx = graphDb.beginTx();

    try {
    	
    	        	
    	Iterator<DNodeView> nodeViewIterator=newView.getNodeViewsIterator();
    	
    	while(nodeViewIterator.hasNext()){
    		
    		DNodeView nodeView=nodeViewIterator.next();
    		
    		if ( Cytoscape.getNodeAttributes().hasAttribute(nodeView.getNode().getIdentifier(), "Identifier") )        		
    		{
    			//Node in database        			
    			
    			long identifier=Long.parseLong( ((String)Cytoscape.getNodeAttributes().getAttribute(nodeView.getNode().getIdentifier(), "Identifier")) );        			
    			
    			//Update the properties of x, y and Label.
    			
    			Node dbNode=graphDb.getNodeById(identifier);        			        		
    			
    			dbNode.setProperty("xPos", nodeView.getXPosition() );
    			dbNode.setProperty("yPos", nodeView.getYPosition() );
    			dbNode.setProperty("Label", nodeView.getLabel().getText() );
    			
    			dbNode.setProperty("Present", "true");
    		}
    		else
    		{
    			//Node is not in database
    			//Add it
    			
                Node addNode = graphDb.createNode();
                
                addNode.setProperty("Type", "Node");
                
    			addNode.setProperty("xPos", nodeView.getXPosition() );
    			addNode.setProperty("yPos", nodeView.getYPosition() );
    			addNode.setProperty("Label", nodeView.getLabel().getText() );        			
    			
    			addNode.setProperty("Present", "true");
    			
    			//Update the appropriate NodeAttributes
    			
    			Cytoscape.getNodeAttributes().setAttribute(nodeView.getNode().getIdentifier(), "Identifier", String.valueOf(addNode.getId()) );        			
    		}
    	}        	
    	
        Iterator<Node> itr=graphDb.getAllNodes().iterator();
        itr.next();
        
        while(itr.hasNext())
        {
            Node testNode=itr.next();
            
            
            if(!testNode.hasProperty("Present"))
            {
            	
            	// Have to delete this node along with all its relationships (Both incoming and outgoing).
            	
            	Iterator<Relationship> relItr=testNode.getRelationships(Direction.BOTH).iterator();
            	
            	while(relItr.hasNext())
            		relItr.next().delete();
            	
            	//Now delete the node itself
            	
            	testNode.delete();
            }
            else
            	testNode.removeProperty("Present");
            
        }
    	
    	//Lets add the edges	    	
    	
    	Iterator<EdgeView> cyEdgeViewIterator=newView.getEdgeViewsIterator();
    	
    	while(cyEdgeViewIterator.hasNext())
    	{
    	
    		EdgeView cyEdgeView=cyEdgeViewIterator.next();
    		
    		Edge edge=cyEdgeView.getEdge();
    		
    		Long sourceIdentifier= Long.decode( (String)Cytoscape.getNodeAttributes().getAttribute( edge.getSource().getIdentifier() , "Identifier") );
    		Long targetIdentifier= Long.decode( (String)Cytoscape.getNodeAttributes().getAttribute( edge.getTarget().getIdentifier() , "Identifier") );
    		
    		//Have to check before creating a new one
    		        		
    		Iterator<Relationship> relItr=graphDb.getNodeById(sourceIdentifier).getRelationships(MyRelationshipTypes.ArrowTo, Direction.BOTH).iterator();
    		boolean edgeExists=false;

        	while(relItr.hasNext())
        	{
        		Relationship rel=relItr.next();
        		
        		if(rel.getStartNode().getId()==targetIdentifier || rel.getEndNode().getId()==targetIdentifier)
        		{
        			edgeExists=true;
        			rel.setProperty("Present", "True");
        		}
        	}
        	
        	//If the relationship does not exist, then create it
    		
        	if(!edgeExists)
        		graphDb.getNodeById(sourceIdentifier).createRelationshipTo( graphDb.getNodeById(targetIdentifier), MyRelationshipTypes.ArrowTo).setProperty("Present", "true");
        	
    	}
    		
		//Delete the relationships not in the graph anymore
		
		Iterator<Node> nodeIterator=graphDb.getAllNodes().iterator();
			nodeIterator.next();
		
		while(nodeIterator.hasNext()){
		
			Iterator<Relationship> newRelItr=nodeIterator.next().getRelationships(MyRelationshipTypes.ArrowTo, Direction.OUTGOING).iterator();
			
			while(newRelItr.hasNext())
			{
				Relationship newRel=newRelItr.next();
				
				if(!newRel.hasProperty("Present"))
					newRel.delete();
				else
					newRel.removeProperty("Present");
			}
			
		}
	
		tx.success();        	
	
    }
    finally
    {
        tx.finish();
    }    	
}

	@Override
	public void actionPerformed(ActionEvent event) {
				
		createEmptyNetwork();		
		
		readEntireGraph();
										
	}


}

