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

float gravity = .1;
float time = 0;
int score = 0;
boolean winStage = false;
boolean gameEnd = false;


void setup(){
    size(800,600);
    font = loadFont("SynchroLET-30.vlw");
  boxman = new Player();
  levelSetup(levelLayout);
  smooth();
}

void draw(){
  
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

void reset(){ //Returns changing variables to starting values. Does not yet affect moving platforms position or baddie X-position.
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
