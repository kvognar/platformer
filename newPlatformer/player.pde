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
  
  void playerDraw(){
    
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
  
  void playerMove(){
      movement();  
    if (playerControl){
      if (moveLeft){
        VX = VX - .2;
        gear = gear - .05; //gear rotates as player moves
      }
      if (moveRight){
        VX = VX + .2;
        gear = gear + .05;
      }
      if (!moveRight && !moveLeft){
        VX = VX*.9; //Player comes to a stop gradually when keys are let go
        gear = gear*.9; //Gear winds back to its starting position when player stops
      }
      
      VX = constrain(VX,-2,2); //Player velocity must be constrained for collision detection to work properly.
      
       cameraX = cameraX + VX;
       
      
    }
    
    if (dead){
      playerControl = false;
      boxSize = 20;
      //Y = Y + VY;
      if (Y > height*1.5){
        reset();
      }
    }
    
     if(winStage){
         winSequence();
       }
  }
  
  void deathCheck(){ //If player is touching a ceiling and a floor simultaneously, player is crushed
    if (boxman.ceiling && boxman.grounded){
    boxman.dead = true;
  }
  else {
    boxman.ceiling = false;
    boxman.grounded = false;
  }
  }
  
  void winSequence(){ //Moves player into the castle after flag drops
    if(X < castles[0].castleX + castles[0].castleSizeX/4){
      X = X + 2;
      gear = gear +.05;
    }
    if(X >= castles[0].castleX + castles[0].castleSizeX/4){
      gameEnd = true;
    }
  }
}

    void keyPressed(){ //Movement booleans allow multiple simultaneous inputs
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
  
  void keyReleased(){
    if (key == 'a'){
      boxman.moveLeft = false;
    }
    if (key == 'd'){
      boxman.moveRight = false;
    }
    
  }
