package cytoscape.editor.cyAnnotator.createAnnotation;

import java.awt.Font;

import javax.swing.JFrame;

import cytoscape.editor.cyAnnotator.CyAnnotator;
import cytoscape.editor.cyAnnotator.Annotations.*;
import cytoscape.Cytoscape;
import cytoscape.ding.DingNetworkView;
import ding.view.DGraphView;

public class cBoundedAnnotation extends javax.swing.JFrame {

	
    public cBoundedAnnotation() {
    	
        initComponents();
    }
    

    private void initComponents() {
    	
        jScrollPane1 = new javax.swing.JScrollPane();
        boundedAnnotation1 = new cBoundedAnnotationPanel();
        applyButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setTitle("Create Bounded Annotation");
        setAlwaysOnTop(true);
        setResizable(false);
        getContentPane().setLayout(null);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setViewportView(boundedAnnotation1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(0, 0, 475, 428);

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

        //The attributes are x, y, Text, componentNumber, scaleFactor, shapeColor, edgeColor

        BoundedAnnotation newOne=new BoundedAnnotation(getX(), getY(), boundedAnnotation1.getText(), ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponentCount(), ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getZoom(), boundedAnnotation1.getFillColor(), boundedAnnotation1.getEdgeColor(), boundedAnnotation1.getShapeType(), boundedAnnotation1.getEdgeThickness());
        
        newOne.setFont(boundedAnnotation1.getNewFont());
        newOne.setTextColor(boundedAnnotation1.getTextColor());

        ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).add(newOne);

        Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();    	
    	
    	dispose();           
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        //Cancel

    	dispose();
    }
    
    private javax.swing.JButton applyButton;
    private cBoundedAnnotationPanel boundedAnnotation1;
    private javax.swing.JButton cancelButton;
    private javax.swing.JScrollPane jScrollPane1;
   
}

