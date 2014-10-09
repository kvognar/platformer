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

void castle(){
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

void castleWall(){ //Castle wall must be a separate function for the player to walk behind it
  fill(100,100,150);
  noStroke();
  rect(castleX + 100, castleY + castleSizeY/3, 100, 94);
}

}
