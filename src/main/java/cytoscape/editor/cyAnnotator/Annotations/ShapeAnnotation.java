package cytoscape.editor.cyAnnotator.Annotations;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cytoscape.Cytoscape;
import cytoscape.ding.DingNetworkView;
import cytoscape.editor.cyAnnotator.Annotations.TextAnnotation.modifyTextAnnotationListener;
import cytoscape.editor.cyAnnotator.modifyAnnotations.mShapeAnnotation;
import ding.view.*;


public class ShapeAnnotation extends TextAnnotation{

    private int otherCornerX=0, otherCornerY=0, shapeType=1;
    private Color fillColor=null, edgeColor=Color.BLACK;
    private float edgeThickness=2.0f;
    public boolean cornersAdjusted=false;
    private int rVal=5;
    private double shapeWidth=0, shapeHeight=0;

    
    //For previews in Panel
    
    public ShapeAnnotation(){
    	
    }
    
    public ShapeAnnotation(int width, int height){
    	
    	shapeWidth=width/2;
    	shapeHeight=height/3;
    	rVal=10;
    }
    
    public ShapeAnnotation(int x, int y, int shapeType, Color fillColor, Color edgeColor, float edgeThickness) {

        super(x, y, "", ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponentCount(), ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getZoom());

        this.shapeType=shapeType;
        this.fillColor=fillColor;
        this.edgeColor=edgeColor;
        this.edgeThickness=edgeThickness;
    }


    @Override
    public void paint(Graphics g) {

        Graphics2D g2=(Graphics2D)g;
        
        if(getArrowDrawn())
        	super.paint(g);        

        g2.setComposite(AlphaComposite.Src);

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        Point p1;
        int width, height;

        if(!cornersAdjusted){

            //We haven't finalized the shape yet
            //Comes into play when we created a ShapeAnnotation and move the mouse

            p1=getFirstCorner();//To obtain topLeftCorner
            Point p2=getSecondCorner();//To obtain the bottomRightCorner

            width=Math.abs(p2.x-p1.x);
            height=Math.abs(p2.y-p1.y);
        }

        else{
        	
        	if(usedForPreviews)
        		p1=new Point(getX()+(int)shapeWidth/2, getY()+(int)shapeHeight);
        	else
        		p1=new Point(getX(), getY());

            width=(int)shapeWidth;
            height=(int)shapeHeight;            
        }
            
        if(shapeType==0){//Rectangle
            
            if(fillColor!=null){

                g2.setColor(fillColor);
                g2.fillRect( p1.x, p1.y, width, height);
            }

            if(isSelected())
                g2.setColor(Color.YELLOW);
            else
                g2.setColor(edgeColor);

            g2.setStroke(new BasicStroke(edgeThickness));
            g2.drawRect(p1.x, p1.y, width, height);
                
        }

        else if(shapeType==1){//Rounded Rectangle

            if(fillColor!=null){

                g2.setColor(fillColor);
                g2.fillRoundRect( p1.x, p1.y, width, height, rVal, rVal);
            }

            if(isSelected())
                g2.setColor(Color.YELLOW);
            else
                g2.setColor(edgeColor);
            
            g2.setStroke(new BasicStroke(edgeThickness));
            g2.drawRoundRect(p1.x, p1.y, width, height, rVal, rVal);

        }

        else if(shapeType==2){//Oval

            if(fillColor!=null){

                g2.setColor(fillColor);
                g2.fillOval( p1.x, p1.y, width, height);
            }

            if(isSelected())
                g2.setColor(Color.YELLOW);
            else
                g2.setColor(edgeColor);
            
            g2.setStroke(new BasicStroke(edgeThickness));
            g2.drawOval(p1.x, p1.y, width, height);
        }

        //Now draw the arrows associated with this annotation

    }


    public boolean isShapeAnnotation(){

        return true;
    }

    public boolean isTextAnnotation(){
        return false;
    }    
    
    public boolean isPointInComponentOnly(int pX, int pY) {

        int x=getX(), y=getY();

        if(pX>=x && pX<=(x+(int)shapeWidth) && pY>=y && pY<=(y+(int)shapeHeight))
            return true;
        else
            return false;
    }    

    @Override
    public int getAnnotationWidth() {

        return (int)shapeWidth;
    }


    @Override
    public int getAnnotationHeight() {

        return (int)shapeHeight;
    }

    @Override
    public void adjustFont(double newZoom) {

        float factor=(float)(newZoom/getZoom());
        
        edgeThickness*=factor;
        
        adjustArrowThickness(newZoom);
        
        setZoom(newZoom);
                                        
        shapeWidth*=factor;
        shapeHeight*=factor;
        
        setSize((int)shapeWidth, (int)shapeHeight);
    }

    @Override
    public void adjustSpecificFont(double newZoom) {

        float factor=(float)(newZoom/getTempZoom());
        
        setTempZoom(newZoom);        
                
        shapeWidth*=factor;
        shapeHeight*=factor;
        
        setSize((int)shapeWidth, (int)shapeHeight);
    }
    
    public void addModifyMenuItem(JPopupMenu popup){
    	
        JMenuItem modify=new JMenuItem("Modify Properties");
        modify.addActionListener(new modifyShapeAnnotationListener());
        
        popup.add(modify);
    }
    
    class modifyShapeAnnotationListener implements ActionListener{

        public void actionPerformed(ActionEvent e){

            mShapeAnnotation mSAnnotation=new mShapeAnnotation(ShapeAnnotation.this);

            mSAnnotation.setVisible(true);
            mSAnnotation.setSize(474, 504);		
            mSAnnotation.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            mSAnnotation.setResizable(false);
            
            mSAnnotation.setLocation(ShapeAnnotation.this.getX(), ShapeAnnotation.this.getY());            
        }

    }       
    
    public void adjustCorners(){

        //Comes into play when the shape has been created completely
        //We finalize the topLeft and bottomRight corners of the shape

        Point p1=getFirstCorner(), p2=getSecondCorner();

        this.setLocation(p1);

        ((ArbitraryGraphicsCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)).modifyComponentLocation(this.getX(), this.getY(), this.getComponentNumber());

        shapeWidth=Math.abs(p2.x-p1.x);
        shapeHeight=Math.abs(p2.y-p1.y);

        cornersAdjusted=true;
    }

    public Point getFirstCorner(){

        int x=getX(), y=getY();

        if(x<=otherCornerX && y<=otherCornerY)
            return new Point(x,y);

        else if(x>=otherCornerX && y<=otherCornerY)
            return new Point(otherCornerX, y);

        else if(x<=otherCornerX && y>=otherCornerY)
            return new Point(x, otherCornerY);

        else
            return new Point(otherCornerX, otherCornerY);
    }

    public Point getSecondCorner(){

        int x=getX(), y=getY();

        if(x<=otherCornerX && y<=otherCornerY)
            return new Point(otherCornerX,otherCornerY);

        else if(x>=otherCornerX && y<=otherCornerY)
            return new Point(x, otherCornerY);

        else if(x<=otherCornerX && y>=otherCornerY)
            return new Point(otherCornerX, y);

        else
            return new Point(x, y);

    }
    
    public float getEdgeThickness(){
    	
    	return this.edgeThickness;
    }
    
    public Color getFillColor(){
    	
    	return fillColor;
    }
    
    public Color getEdgeColor(){
    	
    	return edgeColor;
    }
    
    public int getShapeType(){
    	
    	return this.shapeType;
    }
        
    public void setFillColor(Color val){
    	fillColor=val;
    }
    
    public void setEdgeColor(Color val){
    	edgeColor=val;
    }

    public void setOtherCorner(int x, int y){

        otherCornerX=x;
        otherCornerY=y;
    }
    
    public void setEdgeThickness(int val){
    	edgeThickness=val;
    }

    public void setShapeType(int val){
    	shapeType=val;
    }
        
}

