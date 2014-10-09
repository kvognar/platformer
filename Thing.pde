class Thing {
  float X;
  float Y;
  float sizeX; 
  float sizeY;
  float VX;
  float VY;
  
  boolean collisionRight(Thing other){
    if (this.rightBound() > other.leftBound() && 
        this.topBound() < other.bottomBound() && 
        this.bottomBound() > other.topBound() && 
        this.leftBound() < other.rightBound() && 
        this.rightBound() - other.leftBound() <=2){
      return true;
    }
    else return false;
  }
  
  boolean collisionLeft(Thing other){
    if (this.leftBound() < other.rightBound() &&
        this.topBound() < other.bottomBound() &&
        this.bottomBound() > other.topBound() &&
        this.rightBound() > other.leftBound() &&
        this.leftBound() - other.rightBound() >=-2){
          return true;
        }
        else return false;
  }
  
  boolean collisionTop(Thing other){
    if (this.leftBound() < other.rightBound() &&
        this.rightBound() > other.leftBound() &&
        this.topBound() < other.bottomBound() &&
        this.bottomBound() > other.bottomBound()){
          return true;
        }
          else return false;
  }
  
  boolean collisionBottom(Thing other){
    if (this.leftBound() < other.rightBound() &&
        this.rightBound() > other.leftBound() &&
        this.bottomBound() > other.topBound() &&
        this.topBound() < other.topBound()){
          return true;
        }
        else return false;
  }
        
  float leftBound(){
    return(X - sizeX/2);
  }
  
  float rightBound(){
    return(X + sizeX/2);
  }
  
  float topBound(){
    return(Y - sizeY/2);
  }
  
  float bottomBound(){
    return(Y + sizeY/2);
  }
}
