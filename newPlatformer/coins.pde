//Coins don't do anything at the moment besides increase your score, but they're pretty.

class Coin {
  float X;
  float Y;
  float Xmap;
  float sizeX;
  float sizeY;
  float baseSizeX;
  color prism;
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
  
  void drawCoin(){
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
  
  void collisionDetection(){ //Same collisoin detection  as baddies and blocks. I plan to write a single function that works for all of them.
    if (abs(boxman.X - X) < (boxman.boxSize + sizeX)/2 && abs(boxman.Y - Y) < (boxman.boxSize + sizeY)/2){
      score ++;
      collected = true;
    }
  }
}

void coinLoop(){
  for (int i = 0; i < coins.length; i ++){
    coins[i].drawCoin();
  }
}

