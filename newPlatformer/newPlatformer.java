import processing.core.*; 
import processing.xml.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class newPlatformer extends PApplet {

/*Program 6: Array-based platformer
* Written by Kevin Vognar
* This is a short platforming game that uses arrays of objects. 
* Move left and right with a and d, jump with w, and press r to reset.
* Collect prisms and stomp baddies to get to the end!
*
* See tab level_map for heaviest array use.
*/


float cameraX = 0; //universal float to determine X position of all non-player objects
PFont font; 
float xStart;
float yStart;
float xSize = 0;
float ySize = 0;
final float gridSize = 30; //global variables for level layout


Player boxman;
Castle[] castles = new Castle[0];
Baddie[] baddies = new Baddie[0];
Coin[] coins = new Coin[0];
Block[] platforms = new Block[0];
Flag[] flags = new Flag[0];

float gravity = .1f;
float time = 0;
int score = 0;
boolean winStage = false;
boolean gameEnd = false;


public void setup(){
    size(800,600);
    font = loadFont("SynchroLET-30.vlw");
  boxman = new Player();
  levelSetup(levelLayout);
  smooth();
}

public void draw(){
  
      boxman.playerMove();       
      boxman.deathCheck();
      backdrop();
      boxman.playerDraw();
      for (int i = 0; i < castles.length; i++){
        castles[i].castleWall();
      }
      coinLoop();
  for (int i = 0; i < baddies.length; i++){
    
     baddies[i].drawBaddie();
    baddies[i].collisionDetection();
    baddies[i].baddieDeath();
  }
  water();
  hud();
    time ++;
}

public void reset(){ //Returns changing variables to starting values. Does not yet affect moving platforms position or baddie X-position.
  boxman.dead = false;
  boxman.grounded = false;
  boxman.ceiling = false;
  boxman.playerControl = true;
  gameEnd = false;
  winStage = false;
  cameraX = 0;
  boxman.X = 200;
  boxman.Y = 200;
  boxman.VX = 0;
  boxman.VY = 0;
  boxman.boxSize = 40;
  score = 0;
  for(int i = 0; i < baddies.length; i++){
    baddies[i].dead = false;
      baddies[i].XMap = baddies[i].XStart;
      baddies[i].Y = baddies[i].YStart;
      baddies[i].VY = 0;
  }
  for (int i = 0; i < coins.length; i++){
    coins[i].collected = false;
  }
  for (int i = 0; i < flags.length; i++){
    flags[i].flagDrop = false;
    flags[i].flagY = flags[i].y - 190;
  }
  
}
class Creature extends Thing { 
  float boxSize; 
  float VX;
  float VY;
  float orientation; //direction player is facing; 1 is right and -1 is left
  
  
boolean moveLeft;
boolean moveRight;
boolean jumping;
boolean grounded;
boolean floating;
boolean ceiling; 
boolean playerControl;
boolean dead;
  
  Creature(){
    X = 200;
    Y = 200;
    boxSize = 40;
    VX = 0;
    VY = 0;
    orientation = 1;
    dead = false;
    moveLeft = false;
    moveRight = false;
    jumping = false;
    grounded = false;
    floating = false;
    ceiling = false;
  }
  
  public void movement(){
      Y = Y + VY;
      VY = VY + gravity;
      
      if (dead){
        grounded = false;
       
    }
  }
    
  
  public void deathCheck(){ //If player is touching a ceiling and a floor simultaneously, player is crushed
    if (ceiling && grounded){
    dead = true;
  }
  else {
    ceiling = false;
    grounded = false;
  }
  }
  
  
}
public void hud(){
 textFont(font, 30);
 text(score, 10, 25); 
 
 if (gameEnd){
   textFont(font, 40);
   fill(255);
   String congrats = "Congratulations! Press R to reset.";
   text(congrats, 40, 200, 800,300);
 }
}
class Thing {
  float X;
  float Y;
  float sizeX; 
  float sizeY;
  float VX;
  float VY;
  
  public boolean collisionRight(Thing other){
    if (this.rightBound() > other.leftBound() && 
        this.topBound() < other.bottomBound() && 
        this.bottomBound() > other.topBound() && 
        this.leftBound() < other.rightBound() && 
        this.rightBound() - other.leftBound() <=2){
      return true;
    }
    else return false;
  }
  
  public boolean collisionLeft(Thing other){
    if (this.leftBound() < other.rightBound() &&
        this.topBound() < other.bottomBound() &&
        this.bottomBound() > other.topBound() &&
        this.rightBound() > other.leftBound() &&
        this.leftBound() - other.rightBound() >=-2){
          return true;
        }
        else return false;
  }
  
  public boolean collisionTop(Thing other){
    if (this.leftBound() < other.rightBound() &&
        this.rightBound() > other.leftBound() &&
        this.topBound() < other.bottomBound() &&
        this.bottomBound() > other.bottomBound()){
          return true;
        }
          else return false;
  }
  
  public boolean collisionBottom(Thing other){
    if (this.leftBound() < other.rightBound() &&
        this.rightBound() > other.leftBound() &&
        this.bottomBound() > other.topBound() &&
        this.topBound() < other.topBound()){
          return true;
        }
        else return false;
  }
        
  public float leftBound(){
    return(X - sizeX/2);
  }
  
  public float rightBound(){
    return(X + sizeX/2);
  }
  
  public float topBound(){
    return(Y - sizeY/2);
  }
  
  public float bottomBound(){
    return(Y + sizeY/2);
  }
}
public void backdrop(){ //Draws background environment and calls platform collision functions
  background(150,200,255);
  stroke(1);
//  line(0,ground,width,ground);
  for (int i = 0; i <platforms.length; i++){
    platforms[i].drawBlock();
    if(!boxman.dead){
    platforms[i].collisionDetection();
//    if (boxman.Y + boxman.boxSize/2 >= ground){ //Ground collision detection, for absolute floor
//      boxman.jumping = false;
//      boxman.grounded = true;
//      boxman.VY = 0;
//      boxman.Y = ground - boxman.boxSize/2;
//      }
    }
  }
  for (int i = 0; i < castles.length; i++){
    castles[i].castle();
  }
  for (int i = 0; i < flags.length; i++){
    flags[i].endFlag();
  }
}
//Baddies are of limited intelligence presently.
//The plan is to make baddies and the player siblings of a gravity-affected super object,
//so they can walk on platforms and turn around when they hit a wall or ledge.
//Right now they just move back and forth in a defined range without regard for gravity.

class Baddie extends Creature{
  float baddieRange1;
  float baddieRange2;
  float YStart;
  float XMap; //This is the value used for placing the baddie in its correct position on the map.
  float XStart;
  
  boolean brave;
  int currentGround;
  
  Baddie(float X_, float Y_, float baddieRange1_, float baddieRange2_){
    X = X_;
    XMap = X_;
    XStart = X_;
    Y = Y_;
    YStart = Y_;
    baddieRange1 = baddieRange1_; //Upper and lower x-values for baddie movement. 
    baddieRange2 = baddieRange2_;
    sizeX = 30;
    sizeY = 30;
    VX = 1;
    VY = 0;
    brave = false;
    
    dead = false;
  }
  
  
  public void drawBaddie(){
  rectMode(CENTER);
  strokeWeight(1);
  stroke(0);
  fill(50,255,150);
  if (!dead){
    moveBaddie();
    rect(X, Y-2, sizeX, sizeX-5);
    fill(0);
    rect(X - sizeX/2, Y + 10 + 2*sin(time/10), 10, 6);
    rect(X + sizeX/2, Y + 10 - 2*sin(time/10), 10, 6);
    
    }
  }
  
  public void moveBaddie(){
    X = XMap - cameraX;
    XMap = XMap+ VX;
//    if (XMap < baddieRange1 || XMap > baddieRange2) {
//      VX = VX*-1;
//    }
    Y = Y + VY;
    VY = VY + gravity;
    platformCollision();
    edgeDetection();
  }
  
  public void platformCollision(){
    if (!dead){
      for (int i = 0; i < platforms.length; i++){
      if (collisionBottom(platforms[i])){
        Y = platforms[i].topBound() - sizeY/2;
        VY = 0;
        currentGround = i;
      }
      if (collisionRight(platforms[i])){
        VX = -1;
      }
      if (collisionLeft(platforms[i])){
        VX = 1;
      }
    }
    }
  }
  
 public void edgeDetection(){
   if (!brave){
      if (leftBound() < platforms[currentGround].leftBound()){
       VX = 1;
      } 
      if (rightBound() > platforms[currentGround].rightBound()){
        VX = -1;
      }
   }
 }
   
  
  public void collisionDetection(){ //Compares baddie's position and player's position. 
    if (!boxman.dead && !dead){
    if (boxman.bottomBound() > topBound() && boxman.rightBound() > leftBound() && boxman.leftBound() < rightBound() && boxman.bottomBound() < Y){
      boxman.VY = -abs(boxman.VY*1.1f); //Player gets a slight boost when stomping a baddie. This can grow to pretty large jumps.
      dead = true;
      //If the player is falling on the baddie, baddie is stomped.
    }
    else if(boxman.X + boxman.boxSize/2 > X - sizeX/2 && boxman.Y + boxman.boxSize/2 > Y - sizeX/2 && boxman.Y - boxman.boxSize/2 < Y + sizeX/2 && boxman.X - boxman.boxSize/2 < X + sizeX/2){
      boxman.dead = true;
      //If they meet under other circumstances, the player dies.
    }
    
    }
  }
  
  public void baddieDeath(){ //Knocks the baddie off screen if it's stomped.
    if (dead){
      X = XMap - cameraX;
       fill(50,255,150);       
       rect(X, Y-2, sizeX, sizeX-5);
    fill(0);
    rect(X - sizeX/2, Y - 14, 10, 6);
    rect(X + sizeX/2, Y - 14, 10, 6);
    Y = Y + VY;
    VY = VY + gravity;
    }
  }
    
  
}
    
//Blocks are any platform that the player can walk on or be crushed by.

class Block extends Thing{
  float XStart; //initial X position for level layout
  float YStart; //initial Y position for level layout
  float blockMoveY; //block Y velocity (currently only vertically-moving blocks are supported)
  
  Block(float X_, float Y_, float sizeX_, float sizeY_, float blockMoveY_){ //initialize block
    X = X_;
    XStart = X_;
    Y = Y_;
    YStart = Y_;
    sizeX = sizeX_;
    sizeY = sizeY_;
    blockMoveY = blockMoveY_;
  }
  
  public void drawBlock(){
    blockMovement();
    
    rectMode(CENTER);
    stroke(1);
    fill(150,100,50); //dirt
    rect(X,Y,sizeX,sizeY);
    rectMode(CORNER);
    fill(10,150,0); //grass
    rect(X-sizeX/2,Y-sizeY/2,sizeX,10);
    
  }
  
  public void collisionDetection(){ //Checks player position and changes player position and velocity appropriately
      if (collisionLeft(boxman)){
      boxman.X = X - sizeX/2 - boxman.boxSize/2;
      if (boxman.VX > 0){
      boxman.VX = 0; //Stops player if player is moving against block
      }
    } //Left side detection
    
        else if (collisionRight(boxman)){
            boxman.X = X + sizeX/2 + boxman.boxSize/2;
            if (boxman.VX < 0){ //stops player if player is moving against block
            boxman.VX = 0;
            }
    } //Right side detection
    
      else if (collisionTop(boxman)){
      boxman.jumping = false;
      boxman.grounded = true;
      boxman.VY = 0;
      boxman.Y = Y - sizeY/2 - boxman.boxSize/2 + blockMoveY+gravity;
    } //Top detection
    
    else if (collisionBottom(boxman)){
      if (blockMoveY > 0){
      boxman.VY = blockMoveY;
      }
      else {
          boxman.VY = 0;
        }
      boxman.Y = Y + sizeY/2 + boxman.boxSize/2 + blockMoveY;
      boxman.ceiling = true;
    } //Bottom detection
          
  }
  
public void blockMovement(){ //move blocks and wrap them if they leave the screen vertically
    Y = Y +blockMoveY;
    if (Y > height + 100){
      Y = -100;
    }
    if (Y < -100){
      Y = height + 100;
    } 
    
    X = XStart - cameraX;
}

  
}

//Castle graphic for level's end. Nothing much to see here.

class Castle{

float castleX;
float castleY;
float castleSizeX;
float castleSizeY;
float x;
float y;



Castle(float X_, float Y_){
  x = X_;
  y = Y_;
  castleX = x;
  castleY = y;
  castleSizeX = 400;
  castleSizeY = 280;
}

public void castle(){
  castleX = x - cameraX;
  rectMode(CENTER);
    fill(50);
  rect(castleX + castleSizeX/3, castleY - castleSizeY/2, 40, 100);
  rect(castleX - castleSizeX/3, castleY - castleSizeY/2, 40, 100);
  
  fill(255*abs(sin(radians(frameCount))), 0, 0);
  ellipse(castleX - castleSizeX/3, castleY - 2*castleSizeY/3, 80, 80);
  
  fill(0,0,255*abs(cos(radians(frameCount))));
  ellipse(castleX + castleSizeX/3, castleY - 2*castleSizeY/3, 80, 80);
  
  fill(100,100,150);
  rect(castleX, castleY, castleSizeX, castleSizeY);
  
  fill(255,255,255*abs(sin(frameCount/frameRate)));
  rect(castleX - castleSizeX/4, castleY - castleSizeY/4, 60,40);
  rect(castleX + castleSizeX/4, castleY - castleSizeY/4, 60,40);
  
  fill(0);
  rect(castleX, castleY + castleSizeY/3, 100, 93);
  
}

public void castleWall(){ //Castle wall must be a separate function for the player to walk behind it
  fill(100,100,150);
  noStroke();
  rect(castleX + 100, castleY + castleSizeY/3, 100, 94);
}

}
//Coins don't do anything at the moment besides increase your score, but they're pretty.

class Coin {
  float X;
  float Y;
  float Xmap;
  float sizeX;
  float sizeY;
  float baseSizeX;
  int prism;
  boolean collected;
  
  Coin(float X_, float Y_){
   X = X_;
   Xmap = X_;
   Y = Y_;
   sizeX = 10;
   baseSizeX = 10;
   sizeY = 16;
   prism = color (255,200,0); 
   collected = false;
  }
  
  public void drawCoin(){
    noFill();
    stroke(prism);
    strokeWeight(3);
    X = Xmap - cameraX;
    sizeX = baseSizeX*sin(radians(time*5));
    prism = color(255*abs(sin(radians(time))), 0, 255*abs(cos(radians(time))));
    
    if(!collected){
    rect(X, Y, sizeX, sizeY);  
   collisionDetection();
    }
   
  }
  
  public void collisionDetection(){ //Same collisoin detection  as baddies and blocks. I plan to write a single function that works for all of them.
    if (abs(boxman.X - X) < (boxman.boxSize + sizeX)/2 && abs(boxman.Y - Y) < (boxman.boxSize + sizeY)/2){
      score ++;
      collected = true;
    }
  }
}

public void coinLoop(){
  for (int i = 0; i < coins.length; i ++){
    coins[i].drawCoin();
  }
}

//Draws a flag and starts the endgame sequence when the player reaches it

class Flag{
float x;
float y;
float poleLength;
float flagY;
float xStart;
boolean flagDrop;

Flag(float x_, float y_){
  x = x_;
  y = y_;
  xStart = x_;
  poleLength = 200;
  flagY = y - 190;
  flagDrop = false;
}



public void endFlag(){
  x = xStart - cameraX;
  drawFlag();
  
  flagCollision();
  
  if(flagDrop){
    boxman.playerControl = false; //Player loses control when the flagpole is touched
    if(flagY < y - 30){
      flagY = flagY + 2;
    }
    if(flagY >= y - 30){ 
      winStage = true; //Once the flag is down, player enters the castle
    }
  }
}

public void drawFlag(){
 stroke(0,200,0);
strokeWeight(3);
line(x, y, x, y - 200);
strokeWeight(1);
stroke(0);
fill(0,150,0);
ellipse(x, y-poleLength, 10, 10);
fill(255);
triangle(x, flagY, x, flagY + 30, x - 30, flagY);
}

public void flagCollision(){
 if (boxman.X + boxman.boxSize/2 > x){
   flagDrop = true;
 }
}
  
}
/*This tab manages the level layout.
* The game is mapped out in a two-dimensional array so that the level can be designed by essentially drawing it in ASCII.
* The key is as follows:
* P: Stationary platform
* Q: Platform with vertical velocity of -1
* R: Platform with vertical velocity of 1
* S: Platform with vertical velocity of .75
* B: Baddie (enemy object)
* C: Coin (pickup object)
* F: Finish flag
* Z: Castlebot at level's end
*
* The method levelSetup() scans through the array levelLayout and appends new objects to the corresponding object array.
* If the object is a platform, the method calls PlatformCheckX and Y to search for adjacent platforms. Adjacent platforms
* will increase the size of the first platform instead of producing a new object. Only the top and left sides of the 
* platform need to be mapped.
*
* The idea of mapping a level via an ASCII array was found by skimming the page:
* http://www.flashgametuts.com/tutorials/as3/how-to-create-a-platform-game-in-as3-part-3/
* The details and implementation of this method were independently devised.
*/

char[][] levelLayout ={ 
      {'P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','S','S',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ','R',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P',' ',' ',' ',' ',' ',' ',' ',' ',' ','Q','Q',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','C',' ','C',' ',' ',' ',' ','P',' ',' ',' ','P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '}, 
      {'P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','B','C',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P','P','P','P','P',' ',' ',' ','P',' ',' ',' ',' ','P','P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P','P','P','P','P','P',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','S','S',' ',' ',' ',' ',' ',' ',' ',' ',' ','P','P',' ','C',' ','P',' ',' ','P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ','P',' ',' ','P',' ','P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ','B',' ',' ','P',' ',' ',' ',' ',' ',' ',' ','C',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ','P','P',' ',' ','P',' ','P',' ','P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P','P','P','P',' ',' ','P',' ',' ',' ',' ',' ',' ','C',' ','C',' ',' ',' ',' ',' ',' ',' ',' ','S','S',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ','R',' ','P',' ',' ','P',' ','P',' ','P',' ','P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P',' ',' ',' ',' ',' ',' ',' ',' ',' ','Q','Q',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P','P',' ',' ',' ','P',' ',' ','P',' ','P',' ','P',' ','P',' ','P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','C',' ',' ','P',' ',' ',' ','B',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ','P',' ',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','Z',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ','C','P','P','P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','S','S',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ','P','P',' ',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ','P',' ',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ','B',' ',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P','P',' ',' ',' ','P',' ',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P','P','P','P',' ',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','S','S',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ','R',' ',' ','P',' ',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P','P',' ',' ',' ',' ',' ',' ',' ',' ','F',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P','P','P','P','P','P','P','P','P',' ','Q','Q',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','B',' ','B',' ','B',' ','B',' ','B',' ','B',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ','P','P',' ',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P','P','P','P','P','P','P','P','P','P','P','P','P','P','P','P','P','P','P','P','P','P','P','P','P','P','P','P','P','P'},
      {'P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ',' ',' ',' ','P','P','P','P','P','P','P','P','P','P','P','P','P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ','P',' ',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ','P','P','P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','S','S',' ',' ',' ',' ',' ',' ',' ',' ',' ','P','P',' ',' ',' ','P',' ',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P','P',' ',' ',' ','P',' ',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ','P','P',' ',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','S','S',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','R','P','P',' ',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P','P',' ',' ',' ',' ',' ',' ',' ',' ','Q','Q',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','C',' ',' ',' ','P','P',' ',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P','P',' ',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
      {'P','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','S','S',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P','P',' ',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
};

//char[][] levelLayout = {
//  {' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','B',' ',' ',' ',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P','P',' ',' ','P',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','P','P',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ','P','P',' ',' ',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ','B',' ',' ','P',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ','P','P','P',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' ','P',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ','P','P','P','P','P','P','P',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
//  {' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '}};


public void levelSetup(char[][] levelLayout){
  for (int i = 0; i < levelLayout.length; i++){
    for(int j = 0; j < levelLayout[i].length; j++){
      if (levelLayout[i][j] == 'P'){
         xStart = j*gridSize;
         yStart = i*gridSize;
         xSize += gridSize;
         ySize += gridSize;
         platformCheckX(j, i, levelLayout[i][j]);
         platformCheckY(j, i, levelLayout[i][j]);

        createPlatform(xStart + xSize/2, yStart+ySize/2, xSize, ySize, 0);
        xSize = 0;
        ySize = 0;
      }
      
      if (levelLayout[i][j] == 'Q'){
                 xStart = j*gridSize;
         yStart = i*gridSize;
         xSize += gridSize;
         ySize += gridSize;
         platformCheckX(j, i, levelLayout[i][j]);
         platformCheckY(j, i, levelLayout[i][j]);

        createPlatform(xStart + xSize/2, yStart+ySize/2, xSize, ySize, -1);
        xSize = 0;
        ySize = 0;
      }
      
       if (levelLayout[i][j] == 'R'){
               xStart = j*gridSize;
       yStart = i*gridSize;
       xSize += gridSize;
       ySize += gridSize;
       platformCheckX(j, i, levelLayout[i][j]);
       platformCheckY(j, i, levelLayout[i][j]);

      createPlatform(xStart + xSize/2, yStart+ySize/2, xSize, ySize, 1);
      xSize = 0;
      ySize = 0;
    }
    
    if (levelLayout[i][j] == 'S'){
      xStart = j*gridSize;
       yStart = i*gridSize;
       xSize += gridSize;
       ySize += gridSize;
       platformCheckX(j, i, levelLayout[i][j]);
       platformCheckY(j, i, levelLayout[i][j]);

      createPlatform(xStart + xSize/2, yStart+ySize/2, xSize, ySize, .75f);
      xSize = 0;
      ySize = 0;
    }
      
      if (levelLayout[i][j] == 'B'){
        createBaddie(j*gridSize, i*gridSize+16);
      }
      
      if (levelLayout[i][j] == 'C'){
        createCoin(j*gridSize, i*gridSize);
      }
        
      if (levelLayout[i][j] == 'Z'){
        createCastle(j*gridSize, i*gridSize);
      }
      if (levelLayout[i][j] == 'F'){
        createFlag(j*gridSize, i*gridSize);
      }
  }
   
  }
}

public void createBaddie(float x, float y){
  Baddie a = new Baddie(x, y, x - 30, x + 30);
  baddies = (Baddie[]) append(baddies,a); //Create a new object and append it to the object's array
}

public void createPlatform(float x, float y, float xSize, float ySize, float speed){
  Block b = new Block(x, y, xSize, ySize, speed);
  platforms = (Block[]) append(platforms,b);
}

public void createCoin(float x, float y){
  Coin c = new Coin(x, y);
  coins = (Coin[]) append(coins,c);
}

public void createCastle(float x, float y){
  Castle z = new Castle(x, y+10);
  castles = (Castle[]) append(castles, z);
}

public void createFlag(float x, float y){
  Flag f = new Flag(x, y+gridSize);
  flags = (Flag[]) append(flags, f);
}
  
public void platformCheckY(int j, int i, char test){ // "test" allows the function to check for adjacent platforms of the same type
  
  if(i + 1 < levelLayout.length){
    if(levelLayout[i + 1][j] == test){
      ySize += gridSize;
      platformCheckY(j, i+1, test);
      
      levelLayout[i + 1][j] = 'r'; //Overwriting the character keeps the slot from being read again and creating another platform.
    }                              //This is a lazy workaround, though, and must be changed in order to produce multiple levels.
  }
}

public void platformCheckX(int j, int i, char test){
  if (j + 1 < levelLayout[i].length){
    if (levelLayout[i][j + 1] == test){
      xSize += gridSize;
        platformCheckX(j + 1, i, test);
        levelLayout[i][j + 1] = 'r';
    }
  }
}


class Player extends Creature {

  float gear; //angle for player's decorative squares
  
  
boolean moveLeft;
boolean moveRight;
boolean jumping;
boolean grounded;
boolean floating;
boolean ceiling; 
boolean playerControl;
boolean dead;
  
  Player(){
    X = 200;
    Y = 200;
    boxSize = 40;
    sizeX = 40;
    sizeY = 40;
    VX = 0;
    VY = 0;
    gear = 0;
    orientation = 1;
    dead = false;
    moveLeft = false;
    moveRight = false;
    jumping = false;
    grounded = false;
    floating = false;
    ceiling = false;
    playerControl = true;
  }
  
  public void playerDraw(){
    
//    if (VX < 0){
//      orientation = -1;
//    }
//    else if (VX > 0){
//      orientation = 1;
//    } //Changes player direction. Not important with symmetrical player image
    
//  PShape leo;
//  leo = loadShape("leo2.svg");  
//  shapeMode(CENTER);
//  shape(leo,X,Y,(boxSize+5)*orientation,boxSize+10);
//  This is for use with a vector graphic player sprite. It slows the program considerably, so is not used at the moment.

imageMode(CENTER);
//noSmooth();
//PImage boxdude;
//boxdude = loadImage("boxman.gif");
//image(boxdude,X,Y,-boxSize,boxSize);
//smooth();

    rectMode(CENTER);
    stroke(1);
    fill(255);
    rect(X,Y,boxSize,boxSize);
    
    pushMatrix();
    noStroke();
    fill(255,0,0);
    translate(X,Y);
    rotate(gear);
    rect(0,0,boxSize/2,boxSize/2);
    pushMatrix();
    rotate(gear*2);
    fill(255,200,0);
    rect(0,0,boxSize/4,boxSize/4);
    popMatrix();
    popMatrix();
  }
  
  public void playerMove(){
      movement();  
    if (playerControl){
      if (moveLeft){
        VX = VX - .2f;
        gear = gear - .05f; //gear rotates as player moves
      }
      if (moveRight){
        VX = VX + .2f;
        gear = gear + .05f;
      }
      if (!moveRight && !moveLeft){
        VX = VX*.9f; //Player comes to a stop gradually when keys are let go
        gear = gear*.9f; //Gear winds back to its starting position when player stops
      }
      
      VX = constrain(VX,-2,2); //Player velocity must be constrained for collision detection to work properly.
      
       cameraX = cameraX + VX;
       
      
    }
    
    if (dead){
      playerControl = false;
      boxSize = 20;
      //Y = Y + VY;
      if (Y > height*1.5f){
        reset();
      }
    }
    
     if(winStage){
         winSequence();
       }
  }
  
  public void deathCheck(){ //If player is touching a ceiling and a floor simultaneously, player is crushed
    if (boxman.ceiling && boxman.grounded){
    boxman.dead = true;
  }
  else {
    boxman.ceiling = false;
    boxman.grounded = false;
  }
  }
  
  public void winSequence(){ //Moves player into the castle after flag drops
    if(X < castles[0].castleX + castles[0].castleSizeX/4){
      X = X + 2;
      gear = gear +.05f;
    }
    if(X >= castles[0].castleX + castles[0].castleSizeX/4){
      gameEnd = true;
    }
  }
}

    public void keyPressed(){ //Movement booleans allow multiple simultaneous inputs
    if (key == 'a'){
      boxman.moveLeft = true;
    }
    if (key == 'd'){
      boxman.moveRight = true;
    }
    if (key == 'w' && (boxman.grounded || boxman.floating) && boxman.playerControl){
      boxman.jumping = true;
      boxman.grounded = false;
      boxman.VY = -4;
      
    }
    if(key == 'r'){
      reset();
    }
    
  }
  
  public void keyReleased(){
    if (key == 'a'){
      boxman.moveLeft = false;
    }
    if (key == 'd'){
      boxman.moveRight = false;
    }
    
  }
//Draws foreground water and exerts buoyant force on player.

public void water(){
  float waterLevel = 450;
  float buoyancy = .2f;

  fill(0,0,255,100);
  noStroke();
  rectMode(CORNERS);
  rect(0, waterLevel, width, height);
  if (!boxman.dead){
    if (boxman.Y > waterLevel){
      boxman.VY = boxman.VY - buoyancy;
      boxman.VY = constrain(boxman.VY, -2, 5);
    }
    if (abs(boxman.Y - waterLevel) < 1 && boxman.VY < 0){
      boxman.VY = constrain(boxman.VY, -.5f, .5f);        
    }
    if (abs(boxman.Y - waterLevel) < 3){
            boxman.floating = true;
    }
    
    if (abs(boxman.Y - waterLevel) > 3){
      boxman.floating = false;
    }
  }

}
  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#FFFFFF", "newPlatformer" });
  }
}
