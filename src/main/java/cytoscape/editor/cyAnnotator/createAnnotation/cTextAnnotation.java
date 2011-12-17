package cytoscape.editor.cyAnnotator.createAnnotation;

import java.awt.Font;

import javax.swing.JFrame;

import cytoscape.editor.cyAnnotator.Annotations.*;
import cytoscape.Cytoscape;
import cytoscape.ding.DingNetworkView;
import ding.view.DGraphView;

public class cTextAnnotation extends javax.swing.JFrame {
	
	public cTextAnnotation(){
		    			
        initComponents();		        
	}	
    
    private void initComponents() {
    	
        textAnnotation1 = new cTextAnnotationPanel();

        applyButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setTitle("Create Text Annotation");
        setAlwaysOnTop(true);
        setResizable(false);
        getContentPane().setLayout(null);

        getContentPane().add(textAnnotation1);
        textAnnotation1.setBounds(0, 0, 475, 428);

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

        TextAnnotation newOne=new TextAnnotation(getX(), getY(), textAnnotation1.getText() ,((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponentCount(), ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getZoom());
        
        newOne.setFont(textAnnotation1.getNewFont());
        newOne.setTextColor(textAnnotation1.getTextColor());

        ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).add(newOne);

        Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();    		
    	
    	dispose();
           
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        //Cancel

    	dispose();
    }
    
    private javax.swing.JButton applyButton;
    private javax.swing.JButton cancelButton;
    private cTextAnnotationPanel textAnnotation1;  
    
}


