package cytoscape.editor.cyAnnotator.Annotations;

import java.awt.*;
import java.awt.event.*;

import cytoscape.editor.cyAnnotator.modifyAnnotations.mArrow;
import cytoscape.editor.cyAnnotator.modifyAnnotations.mTextAnnotation;
import cytoscape.Cytoscape;
import cytoscape.ding.DingNetworkView;
import ding.view.*;
import javax.swing.*;

import java.util.*;

/**
 *
 * @author Avinash Thummala
 */

//A BasicTextAnnotation Class

public class TextAnnotation extends Component{

    private String text;
    private int componentNumber=0;
    public double zoom, tempZoom, arrowLength=7.0;;
    
    private int initialFontSize=12, arrowIndex=0;
    public Font font=new Font("Arial", Font.PLAIN,initialFontSize);

    private Color color=Color.BLACK, arrowColor=Color.BLACK;       

    private boolean drawArrow=false, arrowDrawn=false, selected=false;
    public boolean pointOnArrow=false;
    
    private ArrayList arrowEndPoints;
    
    private BasicStroke arrowStroke=new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    
    public boolean usedForPreviews=false;

    public TextAnnotation(){
        
    }

    public TextAnnotation(int x, int y, String text, int compCount, double zoom){
        
        this.text=text;
        this.componentNumber=compCount;
        this.zoom=zoom;
        this.setLocation(x, y);
    }

    //Verification methods

    public boolean isImageAnnotation(){

        return false;
    }

    public boolean isShapeAnnotation(){

        return false;
    }

    public boolean isTextAnnotation(){
        return true;
    }
    
    public boolean isBoundedAnnotation(){
    	return false;
    }

    public boolean isSelected(){
        return selected;
    }
    
    public boolean isPointOnLine(int x, int y, int x1, int y1, int x2, int y2)
    {    	
    	if( Math.abs( Math.abs( (y-y1)*(x2-x1) )-Math.abs( (y2-y1)*(x-x1) ) )<120 )
    		return true;
    	else
    		return false;
    }
    
    public boolean isPointOnThickLine(int x, int y, int x1, int y1, int x2, int y2, int xCounter, int yCounter)
    {
    	int numRounds=3;
    	    	
    	for(int i=numRounds;i>=0;i--){
    		
    		if(isPointOnLine(x, y, x1+i*xCounter, y1+i*yCounter, x2+i*xCounter, y2+i*yCounter))
    			return true;
    	}
    	
    	xCounter*=-1;
    	yCounter*=-1;
    	
    	for(int i=numRounds;i>=1;i--){
    		
    		if(isPointOnLine(x, y, x1+i*xCounter, y1+i*yCounter, x2+i*xCounter, y2+i*yCounter))
    			return true;
    	}    	
    	
    	return false;
    }    
    
    public boolean isPointOnArrow(int x, int y, int x1, int y1, int x2, int y2){
    	
    	if(x2>=x1)
    	{
    		if(y2>=y1)
    		{    			
    			if( isPointOnThickLine(x, y, x1, y1, x2, y2, -1, 1) )
    				return true;
    		}
    		else
    		{    		    			
    			if( isPointOnThickLine(x, y, x1, y1, x2, y2, -1, -1))
    				return true;    			
    		}
    	}
    	else
    	{
    		if(y2>=y1)
    		{    			
    			if( isPointOnThickLine(x, y, x2, y2, x1, y1, -1, -1))
    				return true;
    		}
    		else
    		{    			
    			if( isPointOnThickLine(x, y, x2, y2, x1, y1, -1, 1))
    				return true;    			
    		}
    	}
    	
		return false;
    }
    
    public int isPointOnArrow(int x, int y){
    	
    	if(!arrowDrawn)
    		return -1;    
    	
        for(int i=0;i<arrowEndPoints.size();i++){

            Point p=getArrowStartPoint((TextAnnotation)arrowEndPoints.get(i));

            if( isPointOnArrow(x, y, p.x, p.y, ((TextAnnotation)arrowEndPoints.get(i)).getX(), ((TextAnnotation)arrowEndPoints.get(i)).getY() ) )
            	return i;

        }

        return -1;
    }
    
    public boolean isPointInComponentOnly(int pX, int pY){

        int x=getX(), y=getY();

        if(pX>=x && pX<=(x+getAnnotationWidth()) && pY>=y && pY<=(y+getAnnotationHeight()) )
            return true;
        
        else 
        	return false;        	
    }    

    public boolean isPointInComponent(int pX, int pY){

    	if( isPointInComponentOnly(pX,pY) )
    		return true;
    	
        else
        {
        	int temp=isPointOnArrow(pX, pY);
        	
        	arrowIndex=temp;
        	
        	if(arrowIndex==-1)
        		pointOnArrow=false;
        	else
        		pointOnArrow=true;

        	return pointOnArrow;
        }
    }

    //Get Methods    
    
    public Color getArrowColor(){
    	
    	return arrowColor;
    }
    
    public Color getTextColor(){
    	
    	return color;
    }

    public int getZone(int x, int y){

        if(isPointInComponentOnly(x, y))
            return 0;

        int midX=getTopX()+getAnnotationWidth()/2, midY=getTopY();

        if(x<=midX){

            if(y<=midY)
                return 3;

            else if(y<=midY+getAnnotationHeight())
                return 4;

            else
                return 5;
        }
        else{

            if(y<=midY)
                return 2;

            else if(y<=midY+getAnnotationHeight())
                return 1;

            else
                return 6;
        }

    }

    public int getQuadrant(Point p1, Point p2){

        if(p2.x >= p1.x){

            if(p2.y<=p1.y)
                return 1;
            else
                return 4;
        }
        else{

            if(p2.y<=p1.y)
                return 2;
            else
                return 3;
        }

    }

    public Point getArrowStartPoint(TextAnnotation temp){

        int x=0, y=0, zone=getZone(temp.getX(), temp.getY());

        if(zone==1){
            x=getTopX()+getAnnotationWidth();
            y=getTopY()+getAnnotationHeight()/2;
        }

        else if(zone==2 || zone==3){
            x=getTopX()+getAnnotationWidth()/2;
            y=getTopY();
        }

        else if(zone==4){
            x=getTopX();
            y=getTopY()+getAnnotationHeight()/2;
        }

        else{
            x=getTopX()+getAnnotationWidth()/2;
            y=getTopY()+getAnnotationHeight();
        }

        return new Point(x,y);
    }

    public int getTopX(){
        return getX();
    }

    public int getTopY(){
        return getY();
    }
    
    public BasicStroke getArrowStroke(){
    	
    	return arrowStroke;
    }

    public int getAnnotationWidth(){
        return getTextWidth();
    }

    public int getAnnotationHeight(){
        return getTextHeight();
    }

    public boolean getArrowDrawn(){
        return arrowDrawn;
    }

    public double getZoom(){
        return zoom;
    }

    public boolean getDrawArrow(){
        return drawArrow;
    }
    
    public int getComponentNumber(){
        return componentNumber;
    }    
    
    @Override
    public Font getFont(){
        return font;
    }

    public String getText(){
        return text;
    }    
    
    public double getTempZoom(){
        return tempZoom;
    }
    
    public int getTextWidth(){

        FontMetrics fontMetrics=this.getGraphics().getFontMetrics(font);
        return fontMetrics.stringWidth(text);
    }

    public int getTextHeight(){

        FontMetrics fontMetrics=this.getGraphics().getFontMetrics(font);
        return fontMetrics.getHeight();
    }
    
    public int getTextWidth(Graphics2D g2){

        FontMetrics fontMetrics=g2.getFontMetrics(font);
        return fontMetrics.stringWidth(text);
    }

    public int getTextHeight(Graphics2D g2){

        FontMetrics fontMetrics=g2.getFontMetrics(font);
        return fontMetrics.getHeight();
    }    
    
    @Override
    public Component getComponentAt(int x, int y) {

        if(isPointInComponent(x,y))
            return this;
        else
            return null;

    }

    //Set methods

    public void setArrowPoints(int pX, int pY){

        int zone=getZone(pX,pY);

        if(zone==0){
            arrowDrawn=false;
            return;
        }

        else{

            if(arrowEndPoints==null)
                arrowEndPoints=new ArrayList();

            //The ArrowEndPoints are also set up as TextAnnotations of null size.
            //They have been implemented this way, so as to handle the change in viewports

            TextAnnotation arrowEndPoint=new TextAnnotation(pX, pY,"",((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponentCount(), ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getZoom());
            arrowEndPoint.setSize(0, 0);            

            ((ArbitraryGraphicsCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)).add(arrowEndPoint);

            arrowEndPoints.add(arrowEndPoint);
        }
    }
    
    public void setArrowStroke(BasicStroke newStroke){
    	
    	arrowStroke=newStroke;
    }

    public void setDrawArrow(boolean val){
        drawArrow=val;
    }

    public void setArrowDrawn(boolean val){
        arrowDrawn=val;
    }

    public void setZoom(double zoom){
        this.zoom=zoom;
    }

    public void setSelected(boolean val){
        this.selected=val;
    }

    public void setComponentNumber(int val){
        componentNumber=val;
    }

    @Override
    public void setFont(Font newFont){
        font=newFont;
    }
    
    public void setTextColor(Color newColor){
        this.color=newColor;
    }    
    
    public void setEdgeColor(Color newColor){
    }    
    
    public void setFillColor(Color newColor){
    }    
    
    public void setArrowColor(Color newColor){
    	
    	arrowColor=newColor;
    }

    public void setText(String newText){
        this.text=newText;
    }

    public void setTempZoom(double zoom){
        this.tempZoom=zoom;
    }

    public void print(Graphics g){
    	
    	paint(g);
    }
    
    @Override
    public void paint(Graphics g) {

        Graphics2D g2=(Graphics2D)g;

        //Setting up Anti-aliasing for high quality rendering

        g2.setComposite(AlphaComposite.Src);

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        if(arrowDrawn){

            //For any annotation that points to some locations

            for(int i=0;i<arrowEndPoints.size();i++){

                Point p=getArrowStartPoint((TextAnnotation)arrowEndPoints.get(i));
                
                g2.setColor( ((TextAnnotation)arrowEndPoints.get(i)).getArrowColor() );

                g2.setStroke( ((TextAnnotation)arrowEndPoints.get(i)).getArrowStroke() );
                
                g2.drawLine(p.x, p.y, ((TextAnnotation)arrowEndPoints.get(i)).getX(), ((TextAnnotation)arrowEndPoints.get(i)).getY());

                drawArrow(g2, p, new Point(((TextAnnotation)arrowEndPoints.get(i)).getX(), ((TextAnnotation)arrowEndPoints.get(i)).getY()) );
            }
            
        }
        
        if(isBoundedAnnotation())
        	return;

        else if(isTextAnnotation()){

            g2.setColor(color);
            g2.setFont(font);
            
            if(usedForPreviews)
            {        	
            	g2.drawChars(getText().toCharArray(), 0, getText().length(), getX()+(int)(getWidth()-getTextWidth(g2))/2, getY()+(int)(getHeight()+getTextHeight(g2))/2 );
            	return;
            }

            g2.drawChars(getText().toCharArray(), 0, getText().length(), getX(), getY()+getTextHeight());            	
        }

        if(isSelected() && ( isTextAnnotation() || isImageAnnotation() ) ){

            //Selected Annotations will have a yellow border

            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(2.0f));

            if(isImageAnnotation())
                g2.drawRect(getTopX(), getTopY(), getAnnotationWidth(), getAnnotationHeight());
            else
                g2.drawRect(getTopX(), getTopY(), getAnnotationWidth(), (int)(getAnnotationHeight()*1.5));
        }
        
    }  

    public void drawArrow(Graphics2D g, Point p1, Point p2){

        double angle=Math.atan(((double)(p1.y-p2.y))/((double)(p2.x-p1.x)));
        int quad=getQuadrant(p1, p2);
        
        if(angle >=0 ){

            if(angle<=Math.PI/4){

                double m1=Math.tan(angle + 3*Math.PI/4);
                double m2=Math.tan(angle + Math.PI/4);

                if(quad==1){

                    double x2=p2.x-arrowLength/(Math.sqrt(1+m1*m1));
                    double y2=p2.y-Math.abs(arrowLength*m1/(Math.sqrt(1+m1*m1)));

                    g.drawLine(p2.x, p2.y, (int)Math.round(x2), (int)Math.round(y2) );

                    x2=p2.x-arrowLength/(Math.sqrt(1+m2*m2));
                    y2=p2.y+Math.abs(arrowLength*m2/(Math.sqrt(1+m2*m2)));

                    g.drawLine(p2.x, p2.y, (int)Math.round(x2), (int)Math.round(y2) );
                }
                else if(quad==3){

                    double x2=p2.x+arrowLength/(Math.sqrt(1+m1*m1));
                    double y2=p2.y+Math.abs(arrowLength*m1/(Math.sqrt(1+m1*m1)));

                    g.drawLine(p2.x, p2.y, (int)Math.round(x2), (int)Math.round(y2) );

                    x2=p2.x+arrowLength/(Math.sqrt(1+m2*m2));
                    y2=p2.y-Math.abs(arrowLength*m2/(Math.sqrt(1+m2*m2)));

                    g.drawLine(p2.x, p2.y, (int)Math.round(x2), (int)Math.round(y2) );

                }
            }

            else if(angle<=Math.PI/2){

                double m1=Math.tan(angle - Math.PI/4);
                double m2=Math.tan(angle + Math.PI/4);

                if(quad==1){

                    double x2=p2.x-arrowLength/(Math.sqrt(1+m1*m1));
                    double y2=p2.y+Math.abs(arrowLength*m1/(Math.sqrt(1+m1*m1)));

                    g.drawLine(p2.x, p2.y, (int)Math.round(x2), (int)Math.round(y2) );

                    x2=p2.x+arrowLength/(Math.sqrt(1+m2*m2));
                    y2=p2.y+Math.abs(arrowLength*m2/(Math.sqrt(1+m2*m2)));

                    g.drawLine(p2.x, p2.y, (int)Math.round(x2), (int)Math.round(y2) );

                }
                else if(quad==3){

                    double x2=p2.x+arrowLength/(Math.sqrt(1+m1*m1));
                    double y2=p2.y-Math.abs(arrowLength*m1/(Math.sqrt(1+m1*m1)));

                    g.drawLine(p2.x, p2.y, (int)Math.round(x2), (int)Math.round(y2) );

                    x2=p2.x-arrowLength/(Math.sqrt(1+m2*m2));
                    y2=p2.y-Math.abs(arrowLength*m2/(Math.sqrt(1+m2*m2)));

                    g.drawLine(p2.x, p2.y, (int)Math.round(x2), (int)Math.round(y2) );
                }
            }

        }
        else{

            if(Math.abs(angle)<=Math.PI/4){
            	
                double m1=Math.tan(3*Math.PI/4 + angle);
                double m2=Math.tan(Math.PI/4 + angle);
                                
                if(quad==4){

                    double x2=p2.x-arrowLength/(Math.sqrt(1+m1*m1));
                    double y2=p2.y-Math.abs(arrowLength*m1/(Math.sqrt(1+m1*m1)));

                    g.drawLine(p2.x, p2.y, (int)Math.round(x2), (int)Math.round(y2) );                                    

                    x2=p2.x-arrowLength/(Math.sqrt(1+m2*m2));
                    y2=p2.y+Math.abs(arrowLength*m2/(Math.sqrt(1+m2*m2)));

                    g.drawLine(p2.x, p2.y, (int)Math.round(x2), (int)Math.round(y2) );
                                        
                }

                else if(quad==2){

                    double x2=p2.x+arrowLength/(Math.sqrt(1+m1*m1));
                    double y2=p2.y+Math.abs(arrowLength*m1/(Math.sqrt(1+m1*m1)));

                    g.drawLine(p2.x, p2.y, (int)Math.round(x2), (int)Math.round(y2) );
                                        
                    x2=p2.x+arrowLength/(Math.sqrt(1+m2*m2));
                    y2=p2.y-Math.abs(arrowLength*m2/(Math.sqrt(1+m2*m2)));

                    g.drawLine(p2.x, p2.y, (int)Math.round(x2), (int)Math.round(y2) );                                       
                }
            }

            else{
            	            	
                double m1=Math.tan(3*Math.PI/4 + angle);
                double m2=Math.tan(5*Math.PI/4 + angle);
                                               
                if(quad==4){

                    double x2=p2.x+arrowLength/(Math.sqrt(1+m1*m1));
                    double y2=p2.y-Math.abs(arrowLength*m1/(Math.sqrt(1+m1*m1)));

                    g.drawLine(p2.x, p2.y, (int)Math.round(x2), (int)Math.round(y2) );
                                       
                    x2=p2.x-arrowLength/(Math.sqrt(1+m2*m2));
                    y2=p2.y-Math.abs(arrowLength*m2/(Math.sqrt(1+m2*m2)));

                    g.drawLine(p2.x, p2.y, (int)Math.round(x2), (int)Math.round(y2) );
                                        
                }
                
                else if(quad==2){

                    double x2=p2.x-arrowLength/(Math.sqrt(1+m1*m1));
                    double y2=p2.y+Math.abs(arrowLength*m1/(Math.sqrt(1+m1*m1)));

                    g.drawLine(p2.x, p2.y, (int)Math.round(x2), (int)Math.round(y2) );
                                       
                    x2=p2.x+arrowLength/(Math.sqrt(1+m2*m2));
                    y2=p2.y+Math.abs(arrowLength*m2/(Math.sqrt(1+m2*m2)));

                    g.drawLine(p2.x, p2.y, (int)Math.round(x2), (int)Math.round(y2) );                                        
                }
                
            }

        }

    }

    public void adjustSpecificFont(double newZoom){

        font=font.deriveFont(((float)(newZoom/tempZoom))*font.getSize2D());
        tempZoom=newZoom;

        setBounds(getX(), getY(), getAnnotationWidth(), getAnnotationHeight());
    }

    public void adjustFont(double newZoom){
        
        font=font.deriveFont(((float)(newZoom/zoom))*font.getSize2D());

        setBounds(getX(), getY(), getAnnotationWidth(), getAnnotationHeight());
        
        adjustArrowThickness(newZoom);
        
        zoom=newZoom;
    }
    
    public void adjustArrowThickness(double newZoom){
    	
    	if(!arrowDrawn)
    		return;
    	
        float factor=(float)(newZoom/zoom);
        
        arrowLength*=factor;

        for(int i=0;i<arrowEndPoints.size();i++)
        {
        	BasicStroke stroke=((TextAnnotation)arrowEndPoints.get(i)).getArrowStroke();
        	
        	((TextAnnotation)arrowEndPoints.get(i)).setArrowStroke( new BasicStroke(factor*stroke.getLineWidth(), stroke.getEndCap(), stroke.getLineJoin()) );
        }
    }
    
    public void addModifyMenuItem(JPopupMenu popup){
    	
        JMenuItem modify=new JMenuItem("Modify Properties");
        modify.addActionListener(new modifyTextAnnotationListener());
        
        popup.add(modify);
    }

    public JPopupMenu createPopUp(){

        JPopupMenu popup=new JPopupMenu();
        
        if(pointOnArrow)
        {
            JMenuItem modArrow=new JMenuItem("Modify Properties");
            modArrow.addActionListener(new modifyArrowListener());
            
            popup.add(modArrow);
            
            popup.add(new JSeparator());
        	
            JMenuItem remArrow=new JMenuItem("Remove Arrow");
            remArrow.addActionListener(new removeArrowListener());
            
            popup.add(remArrow);        	
        	
        	pointOnArrow=false;
        }
        
        else
        {
	        
	        if(!isImageAnnotation())
	        {
		        addModifyMenuItem(popup);
	        	popup.add(new JSeparator());
	        }
	
	        JMenuItem removeAnnotation=new JMenuItem("Remove Annotation");
	        removeAnnotation.addActionListener(new removeAnnotationListener());
	
	        JMenuItem addArrow=new JMenuItem("Add Arrow");
	
	        addArrow.addActionListener( new ActionListener(){
	
	            public void actionPerformed(ActionEvent e) {
	                TextAnnotation.this.setDrawArrow(true);
	            }
	
	        });
	
	        popup.add(removeAnnotation);
	        popup.add(addArrow);
	        
        }

        return popup;
    }

    public void showChangePopup(MouseEvent e){

        createPopUp().show(e.getComponent(), e.getX(), e.getY());
    }
    
    class modifyTextAnnotationListener implements ActionListener{

        public void actionPerformed(ActionEvent e){

            mTextAnnotation mTAnnotation=new mTextAnnotation(TextAnnotation.this);

            mTAnnotation.setVisible(true);
            mTAnnotation.setSize(474, 504);		
            mTAnnotation.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            mTAnnotation.setResizable(false);
            
            mTAnnotation.setLocation(TextAnnotation.this.getX(), TextAnnotation.this.getY());            
        }

    }  
    
    class removeArrowListener implements ActionListener{

        public void actionPerformed(ActionEvent e){

            int remPos=((TextAnnotation)TextAnnotation.this.arrowEndPoints.get(arrowIndex)).getComponentNumber();
            int num=((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponentCount();

            for(int i=remPos+1;i<num;i++)
                ((TextAnnotation)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponent(i)).setComponentNumber(i-1);

            ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).remove((TextAnnotation)TextAnnotation.this.arrowEndPoints.get(arrowIndex));

            TextAnnotation.this.arrowEndPoints.remove(arrowIndex);
            
            if(TextAnnotation.this.arrowEndPoints.isEmpty())
            	TextAnnotation.this.arrowDrawn=false;

            Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
        }

    }
    
    class modifyArrowListener implements ActionListener{

        public void actionPerformed(ActionEvent e){

            mArrow modArrow=new mArrow((TextAnnotation)TextAnnotation.this.arrowEndPoints.get(arrowIndex));

            modArrow.setSize(375, 220);
            modArrow.setVisible(true);
        }

    }    
    
    class removeAnnotationListener implements ActionListener{

        public void actionPerformed(ActionEvent e){

            //When an Annotation is removed we have to adjust the componentNumbers of the anotations added
            //after this Annotation

            int remPos=TextAnnotation.this.getComponentNumber();
            int num=((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponentCount();

            for(int i=remPos+1;i<num;i++)
                ((TextAnnotation)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponent(i)).setComponentNumber(i-1);

            ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).remove(TextAnnotation.this);

            if(TextAnnotation.this.getArrowDrawn()){

                for(int temp=0; temp<TextAnnotation.this.arrowEndPoints.size(); temp++){

                    remPos=((TextAnnotation)TextAnnotation.this.arrowEndPoints.get(temp)).getComponentNumber()-1;
                    num--;

                    for(int i=remPos+1;i<num;i++)
                        ((TextAnnotation)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponent(i)).setComponentNumber(i-1);

                    ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).remove((TextAnnotation)TextAnnotation.this.arrowEndPoints.get(temp));
                }

            }

            Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
        }

    }

}
