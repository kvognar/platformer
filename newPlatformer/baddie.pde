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
  
  
  void drawBaddie(){
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
  
  void moveBaddie(){
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
  
  void platformCollision(){
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
  
 void edgeDetection(){
   if (!brave){
      if (leftBound() < platforms[currentGround].leftBound()){
       VX = 1;
      } 
      if (rightBound() > platforms[currentGround].rightBound()){
        VX = -1;
      }
   }
 }
   
  
  void collisionDetection(){ //Compares baddie's position and player's position. 
    if (!boxman.dead && !dead){
    if (boxman.bottomBound() > topBound() && boxman.rightBound() > leftBound() && boxman.leftBound() < rightBound() && boxman.bottomBound() < Y){
      boxman.VY = -abs(boxman.VY*1.1); //Player gets a slight boost when stomping a baddie. This can grow to pretty large jumps.
      dead = true;
      //If the player is falling on the baddie, baddie is stomped.
    }
    else if(boxman.X + boxman.boxSize/2 > X - sizeX/2 && boxman.Y + boxman.boxSize/2 > Y - sizeX/2 && boxman.Y - boxman.boxSize/2 < Y + sizeX/2 && boxman.X - boxman.boxSize/2 < X + sizeX/2){
      boxman.dead = true;
      //If they meet under other circumstances, the player dies.
    }
    
    }
  }
  
  void baddieDeath(){ //Knocks the baddie off screen if it's stomped.
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
    
