void backdrop(){ //Draws background environment and calls platform collision functions
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
