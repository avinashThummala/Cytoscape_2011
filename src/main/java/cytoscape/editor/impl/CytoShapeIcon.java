/* -*-Java-*-
********************************************************************************
*
* File:         CytoShapeIcon.java
* RCS:          $Header: $
* Description:
* Author:       Allan Kuchinksy
* Created:      Mon Sept 06 11:43:57 2005
* Modified:     Thu May 10 15:05:50 2007 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
*
* Revisions:
*
* Wed May 09 17:14:25 2007 (Michael L. Creech) creech@w235krbza760
*  Updated to Cytoscape 2.5-the byte _shape --> NodeShape enum,
*  and changes to use of Arrows.
* Mon Dec 04 11:44:33 2006 (Michael L. Creech) creech@w235krbza760
*  Added constructor for handling size information. Changed
*  getIconWidth/Height() to return real width and height of
*  icon. Added DEFAULT_WIDTH and DEFAULT_HEIGHT and deprecated
*  WIDTH & HEIGHT.
********************************************************************************
*/
package cytoscape.editor.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Image;
import java.awt.Font;

import javax.swing.Icon;

//import com.lowagie.text.Font;

import cytoscape.visual.Arrow;
import cytoscape.visual.ArrowShape;
import cytoscape.visual.NodeShape;


/**
 * Specialized Icon for Cytoscape editor palette entry.  Renders icon
 * based upon input shape, size, color.
 *
 * @author Allan Kuchinsky
 * @version 2.0
 */
public class CytoShapeIcon implements Icon {
	public static final int DEFAULT_WIDTH  = 32;
	public static final int DEFAULT_HEIGHT = 32;
	public static final int YOFFSET        = 7;

	private Dimension _size = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	private Color _color;
	private Image _image = null;
	private NodeShape _shape;
	private Arrow _arrowType = null;
	
	private int annotationType=0;
	
	private int initialFontSize=35;
	private Font font=new Font(Font.SERIF, Font.BOLD,initialFontSize);	
 
	public CytoShapeIcon(NodeShape shape, Color color, Dimension size) {
		this(shape, color);
		_size = size;
	}

	public CytoShapeIcon(NodeShape shape, Color color) {
		_color = color;
		_shape = shape;
		_image = null;
		_arrowType = null;
	}

	/**
	 * Creates a new CytoShapeIcon object.
	 *
	 * @param img  DOCUMENT ME!
	 */
	public CytoShapeIcon(Image img) {
		_image = img;
		_arrowType = null;
	}
	
	public CytoShapeIcon(int type) {
		
		annotationType=type;		
	} 
	
	public boolean isTextAnnotation(){
		
		if(annotationType==1)
			return true;
		
		return false;
	}
	
	public boolean isShapeAnnotation(){
		
		if(annotationType==2)
			return true;
		
		return false;
	}	
	
	public boolean isBoundedAnnotation(){
		
		if(annotationType==3)
			return true;
		
		return false;
	}	
		
	
	/**
	 * Creates a new CytoShapeIcon object.
	 *
	 * @param arrowType  DOCUMENT ME!
	 */
	public CytoShapeIcon(Arrow arrowType) {
		_arrowType = arrowType;
	}

	/* (non-Javadoc)
	 * @see javax.swing.Icon#getIconHeight()
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getIconHeight() {
		return _size.height + 2 * YOFFSET;
	}

	/* (non-Javadoc)
	 * @see javax.swing.Icon#getIconWidth()
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getIconWidth() {
		return _size.width;
	}

	/**
	 * Implements specialized coordinate line drawing for palette shapes
	 *
	 * (non-Javadoc)
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		
		
		if(isTextAnnotation()){
			
			y+=YOFFSET;
			
			g.setColor(Color.BLACK);
			g.setFont(font);
						
			g.drawString("A", x+(getIconWidth()-g.getFontMetrics().charWidth('A'))/2, y+g.getFontMetrics(font).getHeight()-2*YOFFSET);
			
			return;
		}
		
		else if(isShapeAnnotation()){
			
			y+=YOFFSET;
			
			g.setColor(Color.ORANGE);
			
			g.fillRoundRect(x, y, getIconWidth(), getIconHeight()-2*YOFFSET, 5, 5);
			
			Graphics2D g2=(Graphics2D)g;
			g2.setColor(Color.BLACK);			
			g2.setStroke(new BasicStroke(2.0f));
			
			g2.drawRoundRect(x, y, getIconWidth(), getIconHeight()-2*YOFFSET, 5, 5);			
			
			return;
		}	
		
		else if(isBoundedAnnotation()){
			
			y+=YOFFSET/3;
			
			g.setColor(Color.PINK);			
			g.fillRoundRect(x, y, getIconWidth(), getIconHeight()-YOFFSET, 5, 5);
						
			Graphics2D g2=(Graphics2D)g;		
			g2.setStroke(new BasicStroke(2.0f));
			g2.setColor(Color.DARK_GRAY);
			g2.drawRoundRect(x, y, getIconWidth(), getIconHeight()-YOFFSET, 5, 5);
			
			g2.setFont(font);
			g2.setColor(Color.BLACK);
			
			g2.drawString("B", x+(getIconWidth()-g.getFontMetrics().charWidth('A'))/2, y+g.getFontMetrics(font).getHeight()-2*YOFFSET);
			
			return;
		}		
				
		y += YOFFSET;

		if (_image != null) {
			g.drawImage(_image, x, y, c);
			return;
		}

		int width = getIconWidth();
		int height = getIconHeight() - 2 * YOFFSET;

		if (_arrowType != null) {
			g.setColor(_arrowType.getColor());

			if (_arrowType.getShape() == ArrowShape.DELTA) {
				g.fillPolygon(new int[] {
						x, x + ((3 * width) / 4), x + ((3 * width) / 4), x + width,
						x + ((3 * width) / 4), x + ((3 * width) / 4), x
					},
					new int[] {
						y + ((7 * height) / 16), y + ((7 * height) / 16),
						y + ((5 * height) / 16), y + (height / 2),
						y + ((11 * height) / 16), y + ((9 * height) / 16),
						y + ((9 * height) / 16)
					}, 7);
			} else if (_arrowType.getShape() == ArrowShape.CIRCLE) {
				g.fillRect(x, y + ((7 * height) / 16), (13 * (width / 16)), height / 8);
				g.fillOval(x + ((5 * width) / 8), y + ((5 * height) / 16), (6 * width) / 16,
				           (6 * height) / 16);
			} else if (_arrowType.getShape() == ArrowShape.T) {
				g.fillRect(x, y + ((7 * height) / 16), (15 * (width / 16)), height / 8);
				g.fillRect(x + (15 * (width / 16)), y + ((5 * height) / 16), width / 16,
				           (height * 6) / 16);
			} else if (_arrowType.getShape() == ArrowShape.NONE) {
				g.fillRect(x, y + ((7 * height) / 16), (15 * (width / 16)), height / 8);
			}

			return;
		}

		g.setColor(_color);

		if (_shape == NodeShape.TRIANGLE) {
			g.fillPolygon(new int[] { x, x + (width / 2), x + width },
			              new int[] { y + height, y, y + height }, 3);
			g.setColor(Color.BLACK);
			g.drawPolygon(new int[] { x, x + (width / 2), x + width },
			              new int[] { y + height, y, y + height }, 3);
		} else if (_shape == NodeShape.ROUND_RECT) {
			g.fillRoundRect(x, y, width, height, width / 2, height / 2);
			g.setColor(Color.BLACK);
			g.drawRoundRect(x, y, width, height, width / 2, height / 2);
		} else if (_shape == NodeShape.DIAMOND) {
			g.fillPolygon(new int[] { x, x + (width / 2), x + width, x + (width / 2) },
			              new int[] { y + (height / 2), y, y + (height / 2), y + height }, 4);
			g.setColor(Color.BLACK);
			g.drawPolygon(new int[] { x, x + (width / 2), x + width, x + (width / 2) },
			              new int[] { y + (height / 2), y, y + (height / 2), y + height }, 4);
		} else if (_shape == NodeShape.ELLIPSE) {
			g.fillOval(x, y, width, height);
			g.setColor(Color.BLACK);
			g.drawOval(x, y, width, height);
		} else if (_shape == NodeShape.HEXAGON) {
			g.fillPolygon(new int[] {
					x, x + (width / 4), x + ((3 * width) / 4), x + width,
					x + ((3 * width) / 4), x + (width / 4)
				},
				new int[] { y + (height / 2), y, y, y + (height / 2), y + height, y
					    + height }, 6);
			g.setColor(Color.BLACK);
			g.drawPolygon(new int[] {
					x, x + (width / 4), x + ((3 * width) / 4), x + width,
					x + ((3 * width) / 4), x + (width / 4)
				},
				new int[] { y + (height / 2), y, y, y + (height / 2), y + height, y
					    + height }, 6);
		} else if (_shape == NodeShape.OCTAGON) {
			g.fillPolygon(new int[] {
					x, x + (width / 4), x + ((3 * width) / 4), x + width, x + width,
					x + ((3 * width) / 4), x + (width / 4), x
				},
				new int[] {
					y + (height / 4), y, y, y + (height / 4), y + (3 * (height / 4)),
					y + height, y + height, y + (3 * (height / 4))
				}, 8);
			g.setColor(Color.BLACK);
			g.drawPolygon(new int[] {
					x, x + (width / 4), x + ((3 * width) / 4), x + width, x + width,
					x + ((3 * width) / 4), x + (width / 4), x
				},
				new int[] {
					y + (height / 4), y, y, y + (height / 4), y + (3 * (height / 4)),
					y + height, y + height, y + (3 * (height / 4))
				}, 8);
			// MLC 05/09/07:
			// } else if (_shape == ShapeNodeRealizer.PARALLELOGRAM) {
			// MLC 05/09/07:
		} else if (_shape == NodeShape.PARALLELOGRAM) {
			g.fillPolygon(new int[] { x, x + ((3 * width) / 4), x + width, x + (width / 4) },
			              new int[] { y, y, y + height, y + height }, 4);
			g.setColor(Color.BLACK);
			g.drawPolygon(new int[] { x, x + ((3 * width) / 4), x + width, x + (width / 4) },
			              new int[] { y, y, y + height, y + height }, 4);
			// MLC 05/09/07:
			// } else if (_shape == ShapeNodeRealizer.RECT) {
			// MLC 05/09/07:
		} else if (_shape == NodeShape.RECT) {
			g.fillRect(x, y, width, height);
			g.setColor(Color.BLACK);
			g.drawRect(x, y, width, height);
		}
	}
}
