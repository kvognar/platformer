//Draws foreground water and exerts buoyant force on player.

void water(){
  float waterLevel = 450;
  float buoyancy = .2;

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
      boxman.VY = constrain(boxman.VY, -.5, .5);        
    }
    if (abs(boxman.Y - waterLevel) < 3){
            boxman.floating = true;
    }
    
    if (abs(boxman.Y - waterLevel) > 3){
      boxman.floating = false;
    }
  }

}
