package cytoscape.editor.cyAnnotator.createAnnotation;

import java.awt.Font;

import javax.swing.JFrame;

import cytoscape.editor.cyAnnotator.CyAnnotator;
import cytoscape.editor.cyAnnotator.Annotations.*;
import cytoscape.Cytoscape;
import cytoscape.ding.DingNetworkView;
import ding.view.DGraphView;

public class cAnnotation extends javax.swing.JFrame {

	private int x,y;
	
	public cAnnotation(){
		
    	this.cyAnnotator=new CyAnnotator();
    	
        initComponents();		        
	}
	
    public cAnnotation(int x, int y) {
    	this.x=x;
    	this.y=y;
    	
    	this.cyAnnotator=new CyAnnotator();
    	
        initComponents();
    }
    
    public void initListeners(){
    	
    	cyAnnotator.initListeners();
    }

    private void initComponents() {
    	
        jTabbedPane1 = new javax.swing.JTabbedPane();
        textAnnotation1 = new cTextAnnotationPanel();
        shapeAnnotation1 = new cShapeAnnotationPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        boundedAnnotation1 = new cBoundedAnnotationPanel();
        imageAnnotation1 = new cImageAnnotationPanel();
        applyButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Add Annotation");
        setAlwaysOnTop(true);
        setResizable(false);
        getContentPane().setLayout(null);

        jTabbedPane1.addTab("Text Annotation", textAnnotation1);
        jTabbedPane1.addTab("Shape Annotation", shapeAnnotation1);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setViewportView(boundedAnnotation1);

        jTabbedPane1.addTab("Bounded Annotation", jScrollPane1);
        jTabbedPane1.addTab("Image Annotation", imageAnnotation1);

        getContentPane().add(jTabbedPane1);
        jTabbedPane1.setBounds(0, 0, 475, 428);

        applyButton.setText("OK");
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });
        getContentPane().add(applyButton);
        applyButton.setBounds(290, 440, applyButton.getPreferredSize().width, 23);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        getContentPane().add(cancelButton);
        cancelButton.setBounds(370, 440, cancelButton.getPreferredSize().width, 23);

        pack();
    }
       
    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {
        //Apply

        //Check which tab was selected
        //Add the appropriate annotation to the foreground canvas
    	
    	//Text Annotation
    	if(jTabbedPane1.getSelectedIndex()==0)
    	{
            //Create a BasicTextAnnotation using the attributes : x, y, Text, componentNumber, scaleFactor

            TextAnnotation newOne=new TextAnnotation(getX(), getY(), textAnnotation1.getText() ,((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponentCount(), ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getZoom());
            
            newOne.setFont(textAnnotation1.getNewFont());
            newOne.setTextColor(textAnnotation1.getTextColor());

            ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).add(newOne);

            Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();    		
    	}
    	
    	//Shape Annotation
    	else if(jTabbedPane1.getSelectedIndex()==1){
    		
    		cyAnnotator.startDrawShape(shapeAnnotation1.getPreview(), getX(), getY());    		
    	}        	
    	    	
    	//Bounded Annotation
    	else if(jTabbedPane1.getSelectedIndex()==2){
    		
            //The attributes are x, y, Text, componentNumber, scaleFactor, shapeColor, edgeColor

            BoundedAnnotation newOne=new BoundedAnnotation(getX(), getY(), boundedAnnotation1.getText(), ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponentCount(), ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getZoom(), boundedAnnotation1.getFillColor(), boundedAnnotation1.getEdgeColor(), boundedAnnotation1.getShapeType(), boundedAnnotation1.getEdgeThickness());
            
            newOne.setFont(boundedAnnotation1.getNewFont());
            newOne.setTextColor(boundedAnnotation1.getTextColor());

            ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).add(newOne);

            Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();    		
    	}

    	setVisible(false);
           
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        //Cancel

    	setVisible(false);
    }
    
    private javax.swing.JButton applyButton;
    private cBoundedAnnotationPanel boundedAnnotation1;
    private javax.swing.JButton cancelButton;
    private cImageAnnotationPanel imageAnnotation1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private cShapeAnnotationPanel shapeAnnotation1;
    private cTextAnnotationPanel textAnnotation1;
    
    private CyAnnotator cyAnnotator;

}
