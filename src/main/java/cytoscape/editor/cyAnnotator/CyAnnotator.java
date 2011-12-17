package cytoscape.editor.cyAnnotator;

//Also contains viewport, mouse, mouseMotion and some other listeners

import cytoscape.editor.cyAnnotator.Annotations.*;
import cytoscape.editor.cyAnnotator.createAnnotation.*;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import cytoscape.Cytoscape;
import cytoscape.ding.DingNetworkView;
import ding.view.*;
import java.util.ArrayList;


public class CyAnnotator{

      private MyViewportChangeListener myViewportChangeListener=null;

      private static boolean USE_FONT_RESIZE=true, DRAG_VAL=false, annotationEnlarge=false, drawShape=false;

      private ArrayList selectedAnnotations=new ArrayList();
      private double prevZoom=1;
      private ShapeAnnotation newShape=null, createShape=null;

      public CyAnnotator() {
          
    	  initListeners();    	  
      }
      
      public void initListeners(){
    	  
          ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).addMouseListener(new ForegroundMouseListener());
          ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).addMouseMotionListener(new ForegroundMouseMotionListener());

          ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).addKeyListener(new ForegroundKeyListener());

          ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).setFocusable(true);

          //Set up the foregroundCanvas as a dropTarget, so that we can drag and drop JPanels, created Annotations onto it.
          //We also set it up as a DragSource, so that we can drag created Annotations
         
          addDropTarget((ArbitraryGraphicsCanvas)(((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)));
          addDragSource((ArbitraryGraphicsCanvas)(((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)));

          if(USE_FONT_RESIZE){

              //The created annotations resize (Their font changes), if we zoom in and out

              ((ArbitraryGraphicsCanvas)(((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS))).addMouseWheelListener(new MyMouseWheelListener());

              //We also setup this class as a ViewportChangeListener to the current networkView

              myViewportChangeListener=new MyViewportChangeListener();
              ((DingNetworkView)Cytoscape.getCurrentNetworkView()).addViewportChangeListener(myViewportChangeListener);

          }    	  
      }

      class MyViewportChangeListener implements ViewportChangeListener{

          public void viewportChanged(int x, int y, double width, double height, double newZoom) {

              //We adjust the font size of all the created annotations if the  if there are changes in viewport

              Component[] annotations=((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponents();

              for(int i=0;i<annotations.length;i++){
            	  
            	  if(annotations[i] instanceof TextAnnotation)
            		  ((TextAnnotation)annotations[i]).adjustFont(newZoom);
              }

              Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
          }
      }      

      public void startDrawShape(ShapeAnnotation createShape, int x, int y){

          drawShape=true;
          this.createShape=createShape;

          //createShape will have all the properties associated with the shape to be drawn
          //Create a shapeAnnotattion based on these properties and add it to foregroundCanvas

          newShape= new ShapeAnnotation(x, y, createShape.getShapeType(), createShape.getFillColor(), createShape.getEdgeColor(), createShape.getEdgeThickness());

          ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).add(newShape);
      }

      public void addDragSource(ArbitraryGraphicsCanvas foregroundCanvas){

      DragSourceComponent source=new DragSourceComponent(foregroundCanvas);
  }

      class DragSourceComponent extends DragSourceAdapter implements DragGestureListener{

          //Add the foregroundCanvas as DraggableComponent

          DragSource dragSource;

          DragSourceComponent(ArbitraryGraphicsCanvas foregroundCanvas){

              dragSource = new DragSource();
              dragSource.createDefaultDragGestureRecognizer( foregroundCanvas, DnDConstants.ACTION_COPY_OR_MOVE, this);

          }

          public void dragGestureRecognized(DragGestureEvent dge) {

              Component annotation=((ArbitraryGraphicsCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)).getComponentAt((int)(dge.getDragOrigin().getX()), (int)(dge.getDragOrigin().getY()));

              //Add the component number of the annotation being dragged in the form of string to transfer information
              
              if(annotation!=null  && (annotation instanceof TextAnnotation)){

                  Transferable t = new StringSelection(new Integer(((TextAnnotation)annotation).getComponentNumber()).toString());
                  dragSource.startDrag (dge, DragSource.DefaultCopyDrop, t, this);
              }
              
          }    

  }

      public void addDropTarget(ArbitraryGraphicsCanvas foregroundCanvas){

      DropTargetComponent target=new DropTargetComponent(foregroundCanvas);
  }

      public class DropTargetComponent implements DropTargetListener
  {
      //Add the foregroundCanvas as a drop Target

      public DropTargetComponent(ArbitraryGraphicsCanvas foregroundCanvas)
      {
          new DropTarget(foregroundCanvas, this);
      }

      public void dragEnter(DropTargetDragEvent evt){}

      public void dragOver(DropTargetDragEvent evt){
    	  
          try
          {
              Transferable t = evt.getTransferable();

              if (t.isDataFlavorSupported(DataFlavor.stringFlavor))
              {
                  String s = (String)t.getTransferData(DataFlavor.stringFlavor);
                 
                  //Get hold of the transfer information and complete the drop

                  //Based on that information popup appropriate JFrames to create those Annotatons
                  
                  Component annotation=((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponent(Integer.parseInt(s));

                  if(annotation instanceof TextAnnotation)
                  {
                	  
	                  if(!((TextAnnotation)annotation).getDrawArrow()){
	
	                      //The drop has been done to move an annotation to a new location
	
	                      annotation.setLocation((int)evt.getLocation().getX(),(int)evt.getLocation().getY());
	
	                      //This will modify the initial location of this annotation stored in an array in foregroundCanvas
	                      //Very important. Without it you won't be able to handle change in viewports
	                          
	                      ((ArbitraryGraphicsCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)).modifyComponentLocation(annotation.getX(), annotation.getY(), ((TextAnnotation)annotation).getComponentNumber());
	                  }
                  }

                  //Repaint the whole network

                  Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
              }
              
          }
          catch (Exception e)
          {
              e.printStackTrace();
          }    	  
    	  
      }

      public void dragExit(DropTargetEvent evt){}

      public void dropActionChanged(DropTargetDragEvent evt){}

      public void drop(DropTargetDropEvent evt)
      {

          try
          {
              Transferable t = evt.getTransferable();

              if (t.isDataFlavorSupported(DataFlavor.stringFlavor))
              {
                  evt.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                  String s = (String)t.getTransferData(DataFlavor.stringFlavor);

                  evt.getDropTargetContext().dropComplete(true);                  

                  //Get hold of the transfer information and complete the drop

                  //Based on that information popup appropriate JFrames to create those Annotatons
                  
                  Component annotation=((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponent(Integer.parseInt(s));

                  if(annotation instanceof TextAnnotation)
                  {
                  
	                  if(((TextAnnotation)annotation).getDrawArrow()){
	
	                      //The drop has been done to create a new Arrow from an Annotation
	                	  
	                	  ((TextAnnotation)annotation).setDrawArrow(false);                	  
	
	                	  ((TextAnnotation)annotation).setArrowPoints((int)evt.getLocation().getX(),(int)evt.getLocation().getY());
	                      
	                	  ((TextAnnotation)annotation).setArrowDrawn(true);                      
	                  }
	
	                  else{
	
	                      //The drop has been done to move an annotation to a new location
	
	                      annotation.setLocation((int)evt.getLocation().getX(),(int)evt.getLocation().getY());
	
	                      //This will modify the initial location of this annotation stored in an array in foregroundCanvas
	                      //Very important. Without it you won't be able to handle change in viewports
	                          
	                      ((ArbitraryGraphicsCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)).modifyComponentLocation(annotation.getX(), annotation.getY(), ((TextAnnotation)annotation).getComponentNumber());
	                  }
	                  
                  }

                  //Repaint the whole network

                  Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
              }
              
              else{            	  
            	  ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.NETWORK_CANVAS)).getDropTarget().drop(evt);            	                   
              }
          }
          catch (Exception e)
          {
              e.printStackTrace();
              evt.rejectDrop();
          }

      }

  }

      class MyMouseWheelListener implements MouseWheelListener{

      //To handle zooming in and out
      
      public void mouseWheelMoved(MouseWheelEvent e) {

          int notches = e.getWheelRotation();
          double factor = 1.0;

          // scroll up, zoom in
          if (notches < 0)
                  factor = 1.1;
          else
                  factor = 0.9;

          if(annotationEnlarge){

              //If some annotations are selected

              for(int i=0;i<selectedAnnotations.size();i++)
                  ((TextAnnotation)selectedAnnotations.get(i)).adjustSpecificFont( prevZoom * factor  );

              //In that case only increase the size (Change font in some cases) for those specific annotations
              
              prevZoom*=factor;                            
          }
          else{
  	        	  
        	  ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).mouseWheelMoved(e);
          }
                           
          Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
      }

  }

      //Returns a boolean value, whether this is a Mac Platform or not

      private boolean isMacPlatform() {

          String MAC_OS_ID = "mac";
          String os = System.getProperty("os.name");

          return os.regionMatches(true, 0, MAC_OS_ID, 0, MAC_OS_ID.length());
      }

      class ForegroundMouseListener implements MouseListener{

      public ForegroundMouseListener() {
      }

      
      public void mousePressed(MouseEvent e) {

          Component newOne=((ArbitraryGraphicsCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)).getComponentAt(e.getX(), e.getY());

          if(newOne!=null && newOne instanceof TextAnnotation){

              //We might drag this annotation
              DRAG_VAL=true;

              //We have right clicked on the Annotation, show a popup
              if( (e.getButton() == MouseEvent.BUTTON3) || ( isMacPlatform()  && e.isControlDown()) )
            	  ((TextAnnotation)newOne).showChangePopup(e);
          }
          else{

              //Let the InnerCanvas handle this event
              ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).mousePressed(e);
          }
          
      }


      public void mouseReleased(MouseEvent e) {

          //We might have finished dragging this Annotation
          DRAG_VAL=false;

          //Let the InnerCanvas handle this event
          ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).mouseReleased(e);
      }

      public void mouseClicked(MouseEvent e) {

          Component newOne=((ArbitraryGraphicsCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)).getComponentAt(e.getX(), e.getY());

          if(newOne instanceof TextAnnotation)
          {
        	          	            
	          if(e.getClickCount()==2 && newOne!=null && !((TextAnnotation)newOne).pointOnArrow){
	
	              //We have doubled clicked on an Annotation
	
	              annotationEnlarge=true;
	
	              //Add this Annotation to the list of selected Annotations
	
	              selectedAnnotations.add(newOne);
	
	              //This preVZoom value will help in resizing the selected Annotations
	
	              prevZoom=((InnerCanvas)(((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.NETWORK_CANVAS))).getScaleFactor();
	
	              ((TextAnnotation)newOne).setTempZoom(prevZoom);
	              ((TextAnnotation)newOne).setSelected(true);
	
	              //We request focus in this window, so that we can move these selected Annotations around using arrow keys
	
	              ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).requestFocusInWindow();
	
	              //Repaint the whole network. The selected annotations will have a yellow outline now
	
	              Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
	          }
	          
          }
          
          else if(drawShape){

              drawShape=false;

              //We have finished drawing a shapeAnnotation
              //We set the otherCorner of that Annotation
              newShape.setOtherCorner(e.getX(), e.getY());

              newShape.adjustCorners();

              Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
          }          
          
          else if(newOne==null)
          {
        	  //Handle the case where we have clicked on a node
        	  
              //We have clicked somewhere else on the network, de-select all the selected Annotations

              annotationEnlarge=false;

              if(!selectedAnnotations.isEmpty()){

                  for(int i=0;i<selectedAnnotations.size();i++)
                      ((TextAnnotation)selectedAnnotations.get(i)).setSelected(false);

                  selectedAnnotations.clear();
              }

              Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
          }
          
          else{
        	  
              //Let the InnerCanvas handle this event

              ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).mouseClicked(e);
          }

      }

      public void mouseEntered(MouseEvent e) {

          ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).mouseEntered(e);
      }

      public void mouseExited(MouseEvent e) {

          ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).mouseExited(e);
      }

  }

      class ForegroundMouseMotionListener implements MouseMotionListener{

      public ForegroundMouseMotionListener() {
      }

      public void mouseDragged(MouseEvent e) {

          //If we are not dragging an Annotation then let the InnerCanvas handle this event

          if(!DRAG_VAL)
              ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).mouseDragged(e);
      }

      public void mouseMoved(MouseEvent e) {

          if(drawShape){

              //We are drawing a shape

              newShape.setOtherCorner(e.getX(), e.getY());

              Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();                
          }
          else
              ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).mouseMoved(e);
      }
      
  }

      class ForegroundKeyListener implements KeyListener{

      public void keyPressed(KeyEvent e) {

          int code = e.getKeyCode();

          if(annotationEnlarge && ( (code == KeyEvent.VK_UP) || (code == KeyEvent.VK_DOWN) || (code == KeyEvent.VK_LEFT)|| (code == KeyEvent.VK_RIGHT) ) )
          {
              //Some annotations have been double clicked and selected

              int move=2;

              for(int i=0;i<selectedAnnotations.size();i++){

                  TextAnnotation temp=((TextAnnotation)selectedAnnotations.get(i));

                  int x=temp.getX(), y=temp.getY();

                  if (code == KeyEvent.VK_UP)
                      y-=move;

                  else if (code == KeyEvent.VK_DOWN)
                      y+=move;

                  else if (code == KeyEvent.VK_LEFT)
                      x-=move;

                  else if (code == KeyEvent.VK_RIGHT)
                      x+=move;

                  //Adjust the locations of the selected annotations

                  temp.setLocation(x,y);

                  ((ArbitraryGraphicsCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)).modifyComponentLocation(temp.getX(), temp.getY(), temp.getComponentNumber());

              }

              Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
          }

          ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).keyPressed(e);
      }

      public void keyReleased(KeyEvent e) {

          ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).keyReleased(e);
      }

      public void keyTyped(KeyEvent e) {

          ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).keyTyped(e);
      }
      
  }
      
}

