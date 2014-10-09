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



void endFlag(){
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

void drawFlag(){
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

void flagCollision(){
 if (boxman.X + boxman.boxSize/2 > x){
   flagDrop = true;
 }
}
  
}
