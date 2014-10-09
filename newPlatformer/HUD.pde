void hud(){
 textFont(font, 30);
 text(score, 10, 25); 
 
 if (gameEnd){
   textFont(font, 40);
   fill(255);
   String congrats = "Congratulations! Press R to reset.";
   text(congrats, 40, 200, 800,300);
 }
}
