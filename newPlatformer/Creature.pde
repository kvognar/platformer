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
  
  void movement(){
      Y = Y + VY;
      VY = VY + gravity;
      
      if (dead){
        grounded = false;
       
    }
  }
    
  
  void deathCheck(){ //If player is touching a ceiling and a floor simultaneously, player is crushed
    if (ceiling && grounded){
    dead = true;
  }
  else {
    ceiling = false;
    grounded = false;
  }
  }
  
  
}
