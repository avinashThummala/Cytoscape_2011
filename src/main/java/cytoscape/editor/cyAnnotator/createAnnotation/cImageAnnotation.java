package cytoscape.editor.cyAnnotator.createAnnotation;


import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import cytoscape.Cytoscape;
import cytoscape.ding.DingNetworkView;
import ding.view.DGraphView;
import javax.swing.filechooser.*;
import cytoscape.editor.cyAnnotator.Annotations.*;


//Provides a way to create ImageAnnotations

public class cImageAnnotation extends javax.swing.JFrame {


    public cImageAnnotation() {
        
        initComponents(this.getContentPane());
    }

    private void initComponents(Container pane) {

        setTitle("Select an Image");
        setAlwaysOnTop(true);
        setResizable(false);

        setMinimumSize(new java.awt.Dimension(615, 440));

        pane.setLayout(null);

        jFileChooser1 = new javax.swing.JFileChooser();
        jFileChooser1.setControlButtonsAreShown(false);
        jFileChooser1.setCurrentDirectory(null);
        jFileChooser1.setDialogTitle("");

        jFileChooser1.setAcceptAllFileFilterUsed(false);
        jFileChooser1.addChoosableFileFilter( new ImageFilter() );

        pane.add(jFileChooser1);
        jFileChooser1.setBounds(0, 0, 540, 400);
        
        jButton1 = new javax.swing.JButton();

        jButton1.setText("Open");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        
        pane.add(jButton1);
        jButton1.setBounds(530, 340, 70, (int)jButton1.getPreferredSize().getHeight());

        jButton2 = new javax.swing.JButton();

        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        pane.add(jButton2);
        jButton2.setBounds(530, 370, 70, (int)jButton2.getPreferredSize().getHeight());               
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {

        try{

            //Read the selected Image, create an Image Annotation, repaint the whole network and then dispose off this Frame

            BufferedImage image = ImageIO.read(jFileChooser1.getSelectedFile());

            //The Attributes are x, y, Image, componentNumber, scaleFactor

            ImageAnnotation newOne=new ImageAnnotation(getX(), getY(), image, ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponentCount(), ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getZoom());

            ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).add(newOne);

            Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();

            this.dispose();
        }
        catch(Exception ex){

            System.out.println("Unable to load the selected image");
        }

    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {

        dispose();
    }

    public String getExtension(File f) {

        String ext = null;
        String s = f.getName();

        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        
        return ext;
    }


    //This class provides a FileFilter for the JFileChooser

    public class ImageFilter extends FileFilter{

        //Accept all directories and all gif, jpg, tiff, or png files.
        public boolean accept(File f) {

            if (f.isDirectory()) {
                return true;
            }

            String extension = getExtension(f);
            
            if (extension != null) {
                
                if (extension.equals("tiff") ||
                    extension.equals("tif") ||
                    extension.equals("gif") ||
                    extension.equals("jpeg") ||
                    extension.equals("jpg") ||
                    extension.equals("png"))
                        return true;

                else
                    return false;
            }

            return false;
        }

        //The description of this filter
        public String getDescription() {
            return "Just Images";
        }

    }

    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JFileChooser jFileChooser1;

}