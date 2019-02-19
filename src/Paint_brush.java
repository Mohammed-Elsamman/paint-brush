/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.awt.Checkbox;
import java.awt.Image;
import java.awt.event.MouseMotionListener;
import java.awt.image.RenderedImage;
import java.io.File;
import javax.imageio.ImageIO;
/**
 * @author samman
 */
//public class PhotoShop extends Applet {

public class Paint_brush extends Applet {
    
//for loading and saving img
    Image screenImage;
    Image loadedImage;
    Graphics screenGraphics;
    //global diminsion
    int first_x;
    int first_y;
    int second_x;
    int second_y;
    Boolean filled = false;
    String ShapType = null;
    //array for sorting shaps and useing undo & redo
    ArrayList<Shap> ShapsArrObj = new ArrayList<>();
    ArrayList<Shap> RedoShapsArrObj = new ArrayList<>();
    //array of string to draw the buttons
    String[] shabArr = {"filled","line","rect","oval","free","undo","redo"
                        ,"erase","+","-","erase all","save","load"};
    String[] colorArr = {" "," "," "," "," "};
    Button[] btnColor = new Button[5];
    Button[] btnShap = new Button[13];
    //var's to check on color and shape selected and the number of eraser and filled and the width of eraser 
    Checkbox check = new Checkbox("filled");
    Graphics g;
    int shapSelected;
    int checked=0;
    Color newColor = Color.BLACK ;
    Color currentColor;
    int eraserNum=0;
    int eraserSize=6;
    //object to creat file to saving img in it
    File savingFile = new File("./build/img.jpg");

    public void init() {
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent me) {
                painting(me, g);
                repaint();
            }
            @Override
            public void mouseMoved(MouseEvent me) {
            }
        });
        this.addMouseListener(ll);
        
        setSize(1970, 980);
        setBackground(Color.WHITE);
        screenImage = createImage(getWidth(),getHeight());
        screenGraphics = screenImage.getGraphics();
        //==============================================================//
        //==================draw the buttons of color====================
        for(int x = 0; x < colorArr.length ;x++){
            btnColor[x]  = new Button(colorArr[x]);
            btnColor[x].setPreferredSize(new Dimension(30, 40));
            add(btnColor[x]);
        }
        btnColor[0].setBackground(Color.red);
        btnColor[1].setBackground(Color.green);
        btnColor[2].setBackground(Color.BLUE);
        btnColor[3].setBackground(Color.CYAN);
        btnColor[4].setBackground(Color.PINK);
        //================================================================//
        //this 3 buttons to choes the color of draw
        btnColor[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {                
                newColor=Color.red;
            }
        });
        btnColor[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                newColor=Color.green;
            }
        });
        btnColor[2].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               newColor = Color.blue;
            }
        });
        btnColor[3].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                newColor=Color.CYAN;
            }
        });
        btnColor[4].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               newColor = Color.pink;
            }
        });
        //=======================================================//
        //===========draw the buttons of shap====================//
        for (int x = 0; x < shabArr.length; x++) {
            add(btnShap[x]  = new Button(shabArr[x]));
            btnShap[x].setPreferredSize(new Dimension(60, 40));
            btnShap[x].setBackground(Color.BLACK);
            btnShap[x].setForeground(Color.WHITE);
        }
        btnShap[0].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               if(++checked == 1){
                   btnShap[0].setLabel("unfilled");
                   checked=1;
               }else{
                   checked=0;
                   btnShap[0].setLabel("filled");
               }
            }
        });
        //==========================================================//
        //========================= draw the line===================//
        btnShap[1].addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            shapSelected=0;
            ShapType = "line";
            }
        });
        //========================= draw the rect===================//
        btnShap[2].addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            shapSelected=1;
            ShapType = "rect";
            }
        });
        //========================= draw the oval===================
        btnShap[3].addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            shapSelected=2;
            ShapType = "oval";
            }
        });
        //================free hand draw============//
        btnShap[4].addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            eraserSize = 6;
            shapSelected=4;
            ShapType = "ereaser"; //it's take the same type of eraser
            System.out.println(eraserNum);
            }
        });
        //==============function of undo=============//
        btnShap[5].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(ShapsArrObj.size() > 0){
                       int lastIndex = ShapsArrObj.size()-1;
                       int lastCheckNum = ShapsArrObj.get(lastIndex).numCheck;
                    if (ShapsArrObj.get(lastIndex).ShapType != "ereaser"){
                        RedoShapsArrObj.add(ShapsArrObj.get(lastIndex));
                        ShapsArrObj.remove(ShapsArrObj.get(lastIndex));
                    }
                    else{
                        do{
                            RedoShapsArrObj.add(ShapsArrObj.get(lastIndex));
                            ShapsArrObj.remove(ShapsArrObj.get(lastIndex));
                            lastIndex--;
                        }while(lastIndex >= 0 &&
                        ShapsArrObj.get(lastIndex).numCheck == lastCheckNum
                        && ShapsArrObj.get(lastIndex).ShapType=="ereaser");
                    }
                    eraserNum--;
                    repaint();
                }
            }
        });
        //===========================function of redo=====================//
        btnShap[6].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(RedoShapsArrObj.size()>0 ){
                    int lastIndex =RedoShapsArrObj.size()-1;
                    int lastCheckNum = RedoShapsArrObj.get(lastIndex).numCheck;
                    
                    if (!"ereaser".equals(RedoShapsArrObj.get(lastIndex).ShapType)){                    
                        ShapsArrObj.add(RedoShapsArrObj.get(lastIndex));
                        RedoShapsArrObj.remove(RedoShapsArrObj.get(lastIndex));
                    }
                    else{
                        do{
                            ShapsArrObj.add(RedoShapsArrObj.get(lastIndex));
                            RedoShapsArrObj.remove(lastIndex);
                            lastIndex--;
                        }while(lastIndex >= 0 &&
                            RedoShapsArrObj.get(lastIndex).numCheck == lastCheckNum &&
                            "ereaser".equals(RedoShapsArrObj.get(lastIndex).ShapType));
                    }
                    eraserNum--;
                    repaint();
                }
            }
        });
        //========================ereaser=======================//
        btnShap[7].addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            eraserSize = 6;
            shapSelected=3;
            ShapType = "ereaser";
            }
        });
        //============== ereaser & and free hand Size+++============//
        btnShap[8].addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (eraserSize <= 40) {
                eraserSize+=2;
            } else {
                eraserSize=20;
            }
        }
        });
        //===========ereaser & and free hand Size--=================//
        btnShap[9].addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (eraserSize >= 6) {
                eraserSize-=2;
            } else {
                eraserSize=6;
            }
        }
        });
        //====================== erease all ==========================//
        btnShap[10].addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            if(ShapsArrObj.get(ShapsArrObj.size()-1).width < 1999){
                ShapsArrObj.add(new DrawRect(1,1,2000,2000, getBackground(),true,"rect"));
                repaint();
            }
        }
        });
        //=======================save=================================//
        btnShap[11].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    ImageIO.write((RenderedImage)screenImage, "jpg", savingFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //========================load=======================//
        btnShap[12].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ShapsArrObj.clear();
                loadedImage = getImage(getDocumentBase(),"img.jpg");
                repaint();
            }
        });
    }
    Shap currentShap;
    mousePressedAdapter ll = new mousePressedAdapter();
    class mousePressedAdapter implements MouseListener{
        /*on mouse pressed check on the diminsion and the
        attributs of the shapes like color and fill and type
        and after pressed the array of redo will be cleared*/
        @Override
        public void mousePressed (MouseEvent e)
        {
            first_x = e.getX ();
            first_y = e.getY ();
            switch(checked){
                case 1:
                    filled = true;
                    break;
                case 0:
                    filled = false;
                    break;
            }
            switch(shapSelected){
                case 0:
                    currentShap = new DrawLine(first_x,first_y, 0,0, newColor,filled,ShapType); 
                    ShapsArrObj.add(currentShap);
                    break;
                case 1:
                    currentShap = new DrawRect(first_x,first_y, 0 , 0 , newColor,filled,ShapType);
                    ShapsArrObj.add(currentShap);
                    break;
                case 2:
                    currentShap = new DrawOval(first_x , first_y, 0 , 0 , newColor,filled,ShapType);
                    ShapsArrObj.add(currentShap);
                    break;
                case 3:
                    ++eraserNum;
                    ShapsArrObj.add(new DrawOval(e.getX(),e.getY(), eraserSize, 
                    eraserSize, Color.WHITE,true,eraserNum,ShapType));
                    break;
                case 4:
                    ++eraserNum;
                    ShapsArrObj.add(new DrawOval(e.getX(),e.getY(), eraserSize, 
                    eraserSize, newColor,true,eraserNum,ShapType));
                    break;
            }
            RedoShapsArrObj.clear();
        }
        //====================================================================//
        @Override
        public void mouseReleased(MouseEvent e) {
        //using function painting to ceck on the type and width and height//
            painting(e , g);
            repaint();
        }
        @Override
        public void mouseClicked(MouseEvent e) {System.out.print("");}
        @Override
        public void mouseEntered(MouseEvent me) {System.out.print("");}
        @Override
        public void mouseExited(MouseEvent me) {System.out.print("");}
    }
        //====================================================================//
        //class shap using to creat the shapes by extend
    public class Shap{
        int starting_X;
        int starting_Y;
        int ending_X;
        int ending_Y;
        int width;
        int heigt;
        Color c;
        boolean Filled = false;
        String ShapType;
        int numCheck;
     }
    public class DrawLine extends Shap{
        DrawLine(int x, int y,int x2,int y2,Color d,boolean fill,String b){
            starting_X=x;
            starting_Y=y;
            ending_X=x2;
            ending_Y=y2;
            c=d;
            Filled=fill;
            ShapType = b;
        };
    }
    public class DrawRect extends Shap{
        DrawRect(int x, int y,int x2,int y2,Color d,boolean fill,String b){
            starting_X=x;
            starting_Y=y;
            width=x2;
            heigt=y2;
            c=d;
            Filled = fill;
            ShapType = b;
        };
    }
    public class DrawOval extends Shap{
        DrawOval(int x, int y,int x2,int y2,Color d,boolean fill,String b){
            starting_X=x;
            starting_Y=y;
            width=x2;
            heigt=y2;
            c=d;
            Filled = fill;
            ShapType = b;
        };
        DrawOval(int x, int y,int x2,int y2,Color d,boolean fill,int num,String b){
            starting_X=x;
            starting_Y=y;
            width=x2;
            heigt=y2;
            c=d;
            Filled = fill;
            ShapType = b;
            numCheck = num;  
        };
    }
    //=====================================//
    
    //=====================================//
    /*in this function there is one array to loop on it by knowing 
    the type of the shap and if it filled or no with if conditions*/
    int currentShape;
    @Override
    public void paint(Graphics g){
        screenGraphics.clearRect(0, 0, getWidth(), getHeight());
        screenGraphics.drawImage(loadedImage, 0, 0, this);
        Shap c_d_Shap ;
        for(currentShape = 0 ; currentShape < ShapsArrObj.size();currentShape++){
            c_d_Shap = ShapsArrObj.get(currentShape);
            currentColor = c_d_Shap.c;
            screenGraphics.setColor(currentColor);
            if(null != c_d_Shap.ShapType)
            switch (c_d_Shap.ShapType) {
                case "line":
                    screenGraphics.drawLine(c_d_Shap.starting_X
                    ,c_d_Shap.starting_Y,c_d_Shap.ending_X,c_d_Shap.ending_Y);
                    break;
                case "rect":
                    if(c_d_Shap.Filled == true){
                        screenGraphics.fillRect(c_d_Shap.starting_X
                        ,c_d_Shap.starting_Y,c_d_Shap.width,c_d_Shap.heigt);
                    }else{
                        screenGraphics.drawRect(c_d_Shap.starting_X,c_d_Shap.starting_Y,
                                c_d_Shap.width,c_d_Shap.heigt);
                    }   break;
                case "oval":
                    if(c_d_Shap.Filled == true){
                        screenGraphics.fillOval(c_d_Shap.starting_X,c_d_Shap.starting_Y,
                                c_d_Shap.width,c_d_Shap.heigt);
                    }else{
                        screenGraphics.drawOval(c_d_Shap.starting_X,c_d_Shap.starting_Y,
                                c_d_Shap.width,c_d_Shap.heigt);
                    }   break;
                case "ereaser":
                    screenGraphics.fillOval(c_d_Shap.starting_X,c_d_Shap.starting_Y,
                            c_d_Shap.width,c_d_Shap.heigt);
                    break;
                case "free":
                    screenGraphics.fillOval(c_d_Shap.starting_X,c_d_Shap.starting_Y,
                c_d_Shap.width,c_d_Shap.heigt);
                    break;
            }
        }
        g.drawImage(screenImage, 0, 0, this);
    }
    //=========================================//
    
    //when call repaint this function called in it by defult//
    @Override
    public void update(Graphics g){
        paint(g);
    }
    //======================================================================//
    //to chek on shape selected and the width and height by function checkmode
    void painting (MouseEvent e ,Graphics g ){
            second_x = e.getX ();
            second_y = e.getY ();
            switch(shapSelected){
                case 0:
                    currentShap.ending_X=second_x;
                    currentShap.ending_Y=second_y;
                    currentShap.ShapType = "line";
                    break;
                case 1:
                    currentShap.ShapType = "rect";
                    checkMode(currentShap);
                    break;
                case 2:
                    checkMode(currentShap);
                    currentShap.ShapType = "oval";
                    break;
                case 3:    
                    ShapsArrObj.add(new DrawOval(e.getX(),e.getY(), eraserSize,
                    eraserSize, Color.WHITE,true,eraserNum,ShapType));
                    break;
                case 4:    
                    ShapsArrObj.add(new DrawOval(e.getX(),e.getY(), eraserSize, 
                    eraserSize,newColor,true,eraserNum,ShapType));
                    break;
            }
    }
    //here by checked the type of shape we should drawing rect and oval by editing on the second x & y with equations
    void checkMode (Shap currentShap){
        if(second_x >= first_x){
            currentShap.width = second_x - first_x;
            if(second_y >= first_y){
                currentShap.heigt=second_y-first_y;
            }else{
                currentShap.starting_Y =  second_y;
                currentShap.heigt = first_y-second_y;
            }
            }else{
                if(second_y > first_y){
                    currentShap.starting_X = second_x;
                    currentShap.heigt = second_y-first_y;
                    currentShap.width = first_x-second_x;
                }else{
                    currentShap.heigt=first_y-second_y;
                    currentShap.width=first_x-second_x;
                    currentShap.starting_X = second_x;
                    currentShap.starting_Y = second_y;
            }
        }                    
    }
}