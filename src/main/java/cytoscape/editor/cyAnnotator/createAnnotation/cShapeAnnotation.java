package cytoscape.editor.cyAnnotator.createAnnotation;

import javax.swing.JFrame;

import cytoscape.Cytoscape;
import cytoscape.editor.cyAnnotator.CyAnnotator;

public class cShapeAnnotation extends javax.swing.JFrame {
		
	public cShapeAnnotation(CyAnnotator cyAnt){
		    			
		this.cyAnnotator=cyAnt;
		
        initComponents();		        
	}	
    
    private void initComponents() {
    	
    	shapeAnnotation1 = new cShapeAnnotationPanel();

        applyButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setTitle("Create Shape Annotation");
        setAlwaysOnTop(true);
        setResizable(false);
        getContentPane().setLayout(null);

        getContentPane().add(shapeAnnotation1);
        shapeAnnotation1.setBounds(0, 0, 475, 428);

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

		cyAnnotator.startDrawShape(shapeAnnotation1.getPreview(), getX(), getY());
		
    	dispose();           
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        //Cancel

    	dispose();
    }
    
    private javax.swing.JButton applyButton;
    private javax.swing.JButton cancelButton;
    
    private cShapeAnnotationPanel shapeAnnotation1;  
    
    private CyAnnotator cyAnnotator;    
    
}

