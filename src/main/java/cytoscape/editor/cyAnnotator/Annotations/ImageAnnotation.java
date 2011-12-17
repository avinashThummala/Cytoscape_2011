package cytoscape.editor.cyAnnotator.Annotations;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageAnnotation extends TextAnnotation{

    private BufferedImage image;
    
    private double imageWidth=0, imageHeight=0;
    private BufferedImage resizedImage;

    public ImageAnnotation(int x, int y, BufferedImage image, int compCount, double zoom) {

        super(x, y, "", compCount, zoom);

        this.image=image;

        imageWidth=image.getWidth();
        imageHeight=image.getHeight();
        
        resizedImage=resize(image, (int)imageWidth, (int)imageHeight);
    }

    //Returns a resized high quality BufferedImage

    private static BufferedImage resize(BufferedImage image, int width, int height)
    {
        int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();

        if(height==0)
        	height++;
        
        if(width==0)
        	width++;
        
        BufferedImage resizedImage = new BufferedImage(width, height, type);

        Graphics2D g = resizedImage.createGraphics();
        
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(image, 0, 0, width, height, null);
        
        g.dispose();

        return resizedImage;
    }

    @Override
    public void paint(Graphics g) {                
        
        Graphics2D g2=(Graphics2D)g;

        g2.setComposite(AlphaComposite.Src);

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(resizedImage, getX(), getY(), null);
        
        super.paint(g);
    }


    @Override
    public void adjustSpecificFont(double newZoom) {

        double factor=newZoom/getTempZoom();
        
        imageWidth=imageWidth*factor;
        imageHeight=imageHeight*factor;

        resizedImage=resize(image, (int)Math.round(imageWidth), (int)Math.round(imageHeight));

        setBounds(getX(), getY(), getAnnotationWidth(), getAnnotationHeight());
       
        setTempZoom(newZoom);        
    }


    @Override
    public void adjustFont(double newZoom) {

        double factor=newZoom/getZoom();
        
        adjustArrowThickness(newZoom);

        imageWidth=imageWidth*factor;
        imageHeight=imageHeight*factor;

        resizedImage=resize(image, (int)Math.round(imageWidth), (int)Math.round(imageHeight));

        setBounds(getX(), getY(), getAnnotationWidth(), getAnnotationHeight());
                
        setZoom(newZoom);        
    }


    @Override
    public int getAnnotationWidth() {
        return (int)Math.round(imageWidth);
    }

    @Override
    public int getAnnotationHeight() {
        return (int)Math.round(imageHeight);
    }

    @Override
    public boolean isImageAnnotation() {
        return true;
    }

    @Override
    public boolean isTextAnnotation() {
        return false;
    }

}
