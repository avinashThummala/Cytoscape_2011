package cytoscape.editor.annotation;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author Avinash Thummala
 */

//A BasicTextAnnotation Class

public class Annotation extends Component{

    private String text;
    
    private int initialFontSize=14;
    private Font font=new Font(Font.SERIF, Font.PLAIN,initialFontSize);

    private Color color=Color.BLACK;

    private boolean selected=false;

    public Annotation(){
        
    }

    public Annotation(int x, int y, String text){
        
        this.text=text;
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

    public boolean isSelected(){
        return selected;
    }

    public boolean isPointInComponent(int pX, int pY){

        int x=getX(), y=getY();

        if(pX>=x && pX<=(x+getTextWidth()) && pY>=y && pY<=(y+getTextHeight()) )
            return true;
        else
            return false;

    }

    //Get Methods

    public int getTopX(){
        return getX();
    }

    public int getTopY(){
        return getY();
    }

    public int getAnnotationWidth(){
        return getTextWidth();
    }

    public int getAnnotationHeight(){
        return getTextHeight();
    } 
    
    @Override
    public Font getFont(){
        return font;
    }

    public String getText(){
        return text;
    }       
    
    public int getTextWidth(){

        FontMetrics fontMetrics=this.getGraphics().getFontMetrics(font);
        return fontMetrics.stringWidth(text);
    }

    public int getTextHeight(){

        FontMetrics fontMetrics=this.getGraphics().getFontMetrics(font);
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

    public void setSelected(boolean val){
        this.selected=val;
    }
    
    public void setText(String newText){
        this.text=newText;
    }


    @Override
    public void paint(Graphics g) {

        Graphics2D g2=(Graphics2D)g;

        //Setting up Anti-aliasing for high quality rendering

        g2.setComposite(AlphaComposite.Src);

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(color);
        g2.setFont(font);

        g2.drawChars(getText().toCharArray(), 0, getText().length(), getX(), getY()+getTextHeight());

        if(isSelected()){

            //Selected Annotations will have a yellow border

            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(2.0f));

            g2.drawRect(getTopX(), getTopY(), getAnnotationWidth(), (int)(getAnnotationHeight()*1.5));
        }
        
    }  

}
