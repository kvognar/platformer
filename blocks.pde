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
  
  void drawBlock(){
    blockMovement();
    
    rectMode(CENTER);
    stroke(1);
    fill(150,100,50); //dirt
    rect(X,Y,sizeX,sizeY);
    rectMode(CORNER);
    fill(10,150,0); //grass
    rect(X-sizeX/2,Y-sizeY/2,sizeX,10);
    
  }
  
  void collisionDetection(){ //Checks player position and changes player position and velocity appropriately
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
  
void blockMovement(){ //move blocks and wrap them if they leave the screen vertically
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

