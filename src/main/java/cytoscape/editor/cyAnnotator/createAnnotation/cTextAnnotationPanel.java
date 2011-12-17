package cytoscape.editor.cyAnnotator.createAnnotation;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import cytoscape.editor.cyAnnotator.Annotations.TextAnnotation;
import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

public class cTextAnnotationPanel extends javax.swing.JPanel {

	    public cTextAnnotationPanel() {
	        initComponents();
	    }

	    private void initComponents() {
	    	    	
	        jLabel1 = new javax.swing.JLabel();
	        fTField = new javax.swing.JTextField();
	        jLabel2 = new javax.swing.JLabel();
	        annotationText = new javax.swing.JTextField();
	        selectTextColorButton = new javax.swing.JButton();
	        fSizeField = new javax.swing.JTextField();
	        jLabel4 = new javax.swing.JLabel();
	        jLabel3 = new javax.swing.JLabel();
	        fSField = new javax.swing.JTextField();
	        jScrollPane2 = new javax.swing.JScrollPane();
	        fontStyleList = new javax.swing.JList();
	        jScrollPane1 = new javax.swing.JScrollPane();
	        fontTypeList = new javax.swing.JList();
	        jScrollPane3 = new javax.swing.JScrollPane();
	        fontSizeList = new javax.swing.JList();
	        jPanel1 = new javax.swing.JPanel();	 
	        
	        setMaximumSize(new java.awt.Dimension(470, 400));
	        setMinimumSize(new java.awt.Dimension(470, 400));
	        setLayout(null);
	        
	        setBorder(BorderFactory.createLoweredBevelBorder());

	        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12));
	        jLabel1.setText("Enter Text:");
	        add(jLabel1);
	        jLabel1.setBounds(19, 22, jLabel1.getPreferredSize().width, 15);

	        fTField.setEditable(false);
	        fTField.setText("Arial");
	        add(fTField);
	        fTField.setBounds(19, 98, 128, 20);

	        jLabel2.setText("Font Type:");
	        add(jLabel2);
	        jLabel2.setBounds(19, 73, jLabel2.getPreferredSize().width, 14);

	        annotationText.setText("TextAnnotation");
	        add(annotationText);
	        annotationText.setBounds(113, 20, 145, 20);

	        selectTextColorButton.setText("Select Text Color");

	        add(selectTextColorButton);
	        selectTextColorButton.setBounds(306, 19, selectTextColorButton.getPreferredSize().width, 23);

	        fSizeField.setEditable(false);
	        fSizeField.setText("12");
	        add(fSizeField);
	        fSizeField.setBounds(358, 98, 90, 20);

	        jLabel4.setText("Size:");
	        add(jLabel4);
	        jLabel4.setBounds(358, 73, jLabel4.getPreferredSize().width, 14);

	        jLabel3.setText("Style:");
	        add(jLabel3);
	        jLabel3.setBounds(193, 73, jLabel3.getPreferredSize().width, 14);

	        fSField.setEditable(false);
	        fSField.setText("Plain");
	        add(fSField);
	        fSField.setBounds(193, 98, 110, 20);

	        fontStyleList.setModel(new javax.swing.AbstractListModel() {
	            String[] strings = { "Plain", "Bold", "Italic", "Bold and Italic" };
	            public int getSize() { return strings.length; }
	            public Object getElementAt(int i) { return strings[i]; }
	        });

	        fontStyleList.setSelectedIndex(0);
	        jScrollPane2.setViewportView(fontStyleList);

	        add(jScrollPane2);
	        jScrollPane2.setBounds(193, 136, 110, 130);

	        fontTypeList.setModel(new javax.swing.AbstractListModel() {
	            String[] strings = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	            public int getSize() { return strings.length; }
	            public Object getElementAt(int i) { return strings[i]; }
	        });        

	        fontTypeList.setSelectedIndex(1);
	        jScrollPane1.setViewportView(fontTypeList);

	        add(jScrollPane1);
	        jScrollPane1.setBounds(19, 136, 128, 130);

	        fontSizeList.setModel(new javax.swing.AbstractListModel() {
	            String[] strings = { "10", "12", "14", "16", "18", "20", "22", "24", "26", "28", "30", "32", "34", "36" };
	            public int getSize() { return strings.length; }
	            public Object getElementAt(int i) { return strings[i]; }
	        });
	        fontSizeList.setSelectedIndex(1);

	        jScrollPane3.setViewportView(fontSizeList);

	        add(jScrollPane3);
	        jScrollPane3.setBounds(358, 136, 90, 130);
	        
	        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview"));
	        jPanel1.setLayout(null);
	        
	        add(jPanel1);
	        jPanel1.setBounds(19, 277, 430, 102);
	        	
	        preview=new TextAnnotation();
	        
	        jPanel1.add(preview);
	        preview.setBounds(1, 1, jPanel1.getWidth(), jPanel1.getHeight());
	        
	        preview.usedForPreviews=true;
	        
	        modifyTAPreview();  
	        	        
	        fontStyleList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
	            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
	                fontStyleListValueChanged(evt);
	            }
	        });
	        
	        fontTypeList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
	            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
	                fontTypeListValueChanged(evt);
	            }
	        });	        
	        
	        fontSizeList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
	            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
	                fontSizeListValueChanged(evt);
	            }
	        });
	        
	        selectTextColorButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                selectTextColorButtonActionPerformed(evt);
	            }
	        });	    
	        
	        annotationText.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                annotationTextActionPerformed(evt);
	            }
	        });	        
	    }
	    
	    public String getText(){
	    	
	    	return preview.getText();
	    }
	    
	    public Color getTextColor(){
	    	
	    	return preview.getTextColor();
	    }

	    public Font getNewFont(){

	        int fontStyle=0;
	      
	        if(fSField.getText().equals("Plain"))
	            fontStyle=Font.PLAIN;

	        else if(fSField.getText().equals("Bold"))
	            fontStyle=Font.BOLD;

	        else if(fSField.getText().equals("Italic"))
	            fontStyle=Font.ITALIC;

	        else if(fSField.getText().equals("Bold and Italic"))
	            fontStyle=Font.ITALIC+Font.BOLD;

	        return new Font(fTField.getText(), fontStyle, Integer.parseInt(fSizeField.getText()) );
	    }

	    public void modifyTAPreview(){
	    	
	        preview.setFont(getNewFont());
	        preview.setText(annotationText.getText());	        
	        
	        jPanel1.repaint();
	    }	    
	    
	    private void annotationTextActionPerformed(java.awt.event.ActionEvent evt) {
	    
	        modifyTAPreview();
	    }

	    private void selectTextColorButtonActionPerformed(java.awt.event.ActionEvent evt) {
	        //Select Text Color

	        SelectColor tASelectColor=new SelectColor(preview, 0, this.jPanel1);
	        
            tASelectColor.setVisible(true);
            tASelectColor.setSize(435, 420);	        
	    }

	    private void fontStyleListValueChanged(javax.swing.event.ListSelectionEvent evt) {
	        //Plain, Bold, Italic.......

	        fSField.setText((String)fontStyleList.getModel().getElementAt(fontStyleList.getSelectedIndex()) );
	        modifyTAPreview();
	    }

	    private void fontTypeListValueChanged(javax.swing.event.ListSelectionEvent evt) {	        
	        //Font type

	        fTField.setText((String)fontTypeList.getModel().getElementAt(fontTypeList.getSelectedIndex()));
	        modifyTAPreview();
	    }

	    private void fontSizeListValueChanged(javax.swing.event.ListSelectionEvent evt) {
	        //Font Size

	        fSizeField.setText((String)fontSizeList.getModel().getElementAt(fontSizeList.getSelectedIndex()));
	        modifyTAPreview();
	    }

	    private javax.swing.JTextField annotationText;
	    private javax.swing.JTextField fSField;
	    private javax.swing.JTextField fSizeField;
	    private javax.swing.JTextField fTField;
	    private javax.swing.JList fontSizeList;
	    private javax.swing.JList fontStyleList;
	    private javax.swing.JList fontTypeList;
	    private javax.swing.JLabel jLabel1;
	    private javax.swing.JLabel jLabel2;
	    private javax.swing.JLabel jLabel3;
	    private javax.swing.JLabel jLabel4;
	    private javax.swing.JPanel jPanel1;
	    private javax.swing.JScrollPane jScrollPane1;
	    private javax.swing.JScrollPane jScrollPane2;
	    private javax.swing.JScrollPane jScrollPane3;
	    private javax.swing.JButton selectTextColorButton;
	    
	    private TextAnnotation preview;
}
