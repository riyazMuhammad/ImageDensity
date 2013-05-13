import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.PixelGrabber;
import java.awt.image.ShortLookupTable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class ImageDensity extends JFrame implements ActionListener{
	
	Image img= null,img1= null,img2 = null;
	MediaTracker mt= new MediaTracker(this);
	Graphics2D imggra;	
	BufferedImage imgs,imgd;
	
	int marginTop=50,marginLeft=10;
	int imgw=0,imgh=0;
	float imageRatio=0;
	
	Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	int frameWidth = 900;
	int frameHeight = 600;
	JPanel ImageDisplayPanel,ButtonsPanel;
	JLabel status;
	JButton calculate;
	MenuBar mybar;
	Menu file,about;
	MenuItem openFile,exit,author,howToUse;
	
	public ImageDensity(){
		super("Image Density Algorithm");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(frameWidth, frameHeight);
		setLocation((d.width-frameWidth)/2,(d.height-frameHeight)/2);
		
		//lets first setup the menu bar
		
		mybar = new MenuBar();
		setMenuBar(mybar);   //setting our bar as default menu bar
		
		//setting up menu items
		
		//file menu
		file= new Menu("File");		
		openFile = new MenuItem("Open a Image");		
		openFile.addActionListener(this);		
		file.add(openFile);	
		exit = new MenuItem("Exit");
		exit.addActionListener(this);
		file.add(exit);	
		
		//about menu
		about = new Menu("About");
		author = new MenuItem("Author");
		author.addActionListener(this);
		about.add(author);
		howToUse = new MenuItem("How to Use this app");
		howToUse.addActionListener(this);
		about.add(howToUse);
		
		mybar.add(file);
		mybar.add(about);
		
		ButtonsPanel = new JPanel();
		ButtonsPanel.setBorder(BorderFactory.createEtchedBorder());
		status = new JLabel();
		status.setText("Actual Image Width : "+imgw+"   | Image Height : "+imgh);
		ButtonsPanel.add(status);		
		calculate=new JButton();
		//lets add a listener to this button
		calculate.setActionCommand("calculate");
		calculate.addActionListener(this);
		
		calculate.setText("Calculate Density");
		ButtonsPanel.add(calculate);
		
		add(ButtonsPanel,BorderLayout.SOUTH);
		
	}
	
	public void draw_this_image(Image image){
		img=image;
		mt.addImage(img, 0);
		try {
			mt.waitForID(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		imgw=img.getWidth(this);
		imgh=img.getHeight(this);
		status.setText("Actual Image Width : "+imgw+"   | Image Height : "+imgh);
		resize_image();
	}
	
	public void resize_image(){
		//lets resize image with ratio
		if(imgw > frameWidth){
			imageRatio = (float)frameWidth / imgw;
			imgw = frameWidth-20;  
			imgh = (int) (imgh * imageRatio);			
		}
		
		if(imgh > frameHeight){
			imageRatio = (float)frameHeight / imgh;
			imgh = frameHeight-100; 
			imgw = (int) (imgw * imageRatio);	
		}
		repaint();
	}
	
	@Override
	public void paint(Graphics g) {
			super.paint(g);
			g.drawImage(img, marginLeft, marginTop,imgw,imgh,this);
	}
	
	

	public static void main(String[] args) {
		ImageDensity ID = new ImageDensity();
		ID.setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("calculate")){
			//lets calculate the density of this image
			
			//Step0: lets copy the opened image
			img1 = img;
			
			//Step1: First let's grayscale the image
			imgs= new BufferedImage(img1.getWidth(this), img1.getHeight(this), BufferedImage.TYPE_BYTE_GRAY);
			imggra=imgs.createGraphics();
			imggra.drawImage(img1,0, 0,this);
			img2=imgs;
			
			//step2: do edge detect
			imgs= new BufferedImage(img2.getWidth(this), img2.getHeight(this), BufferedImage.TYPE_INT_RGB);
			imggra=imgs.createGraphics();
			imggra.drawImage(img2, 0, 0, this);
			imgd= new BufferedImage(img2.getWidth(this), img2.getHeight(this), BufferedImage.TYPE_INT_RGB);
			float[] data={0,-1,0,
						 -1,4,-1,
						  0,-1,0};
			BufferedImageOp edge= new ConvolveOp(new Kernel(3, 3, data));
			edge.filter(imgs, imgd);
			img2=imgd;
			
			//step3: now invert the edge detected image
			imgs=new BufferedImage(img2.getWidth(this), img2.getHeight(this), BufferedImage.TYPE_INT_RGB);
			imggra= imgs.createGraphics();
			imggra.drawImage(img2, 0, 0, this);
			imgd= new BufferedImage(img2.getWidth(this), img2.getHeight(this), BufferedImage.TYPE_INT_RGB);
			short[] data1 = new short[256];
			for(int i=0;i<256;i++){
				data1[i]=(short)(255-i);
			}
			BufferedImageOp invert= new LookupOp(new ShortLookupTable(0, data1), null);
			invert.filter(imgs, imgd);
			img2=imgd;
			repaint();
			
			//step4: grab pixels and calculate the density
			int imgsize=img2.getWidth(this)*img2.getHeight(this);
			int[] pixel= new int[imgsize];
			PixelGrabber pg=new PixelGrabber(img2, 0, 0, img2.getWidth(this), img2.getHeight(this), pixel, 0, img2.getWidth(this));
			try {
				pg.grabPixels();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			double density=0;
			for(int i=0;i<imgsize;i++){
				density+=pixel[i];
			}
			double finaldensity=-(density/1000000000);			
			
			JOptionPane.showMessageDialog(this, "Calculated Density is: " + finaldensity);
		}
		if(e.getActionCommand().equals("Open a Image")){

			
			FileDialog jf = new FileDialog(this);
			jf.setVisible(true);
			String full_path=jf.getDirectory()+jf.getFile().toString();			
			if(full_path.endsWith("jpg")||full_path.endsWith("png")||full_path.endsWith("gif")||full_path.endsWith("JPG")||full_path.endsWith("PNG")||full_path.endsWith("GIF")){
			img=Toolkit.getDefaultToolkit().getImage(full_path);
			draw_this_image(img);
			}else{
				JOptionPane.showMessageDialog(this, "We support only (jpg,png,gif) formats! Please load another image.");
			}
		}
		if(e.getActionCommand().equals("Author")){
			JOptionPane.showMessageDialog(this, "Hey there,\n The Author of this Algorithm and App is Riyaz(riyaz@riyazm.com).\n Who writes and develops stuffs in C, Android, JAVA and PHP!");
		}
		if(e.getActionCommand().equals("How to Use this app")){
			JOptionPane.showMessageDialog(this, "Just open a image from File->Open a Image, then press Calculate Density!. As simple as that!");
		}
		if(e.getActionCommand().equals("Exit")){
			System.exit(0);
		}
		
	}
}


