package ca.mcgill.ecse420.a3bis;

public class Vector {
  int dim;
  double[] data;
  int colDisplace;

  public Vector(double[] data, int colDisplace, int dim) {
    this.dim = dim;
    this.data = data;
    this.colDisplace = colDisplace;
  }

  public Vector(double[] data) {
    this.dim = data.length;
    this.data = data;
    this.colDisplace = 0;
  }

  public double get(int column) {
   return data[column+colDisplace];
  }

  public void set(int column, double value) {
    data[colDisplace + column] = value;
  }

  public Vector[] split2() {
    Vector[] splitV = new Vector[2];
    if (dim%2 == 0) {
      int newDim = dim/2;
      splitV[0] = new Vector(data, colDisplace, newDim);
      splitV[1] = new Vector(data, colDisplace+newDim, newDim);
    }
    else {
      int newDim = dim/2;
      splitV[0] = new Vector(data, colDisplace, newDim);
      splitV[1] = new Vector(data, colDisplace+newDim, newDim+1);
   }
    return splitV;
  }

  public int getDim() {
    return dim;
  }
}
