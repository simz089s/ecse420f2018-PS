package ca.mcgill.ecse420.a3;

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
    return data[column + colDisplace];
  }

  public void set(int column, double value) {
    data[colDisplace + column] = value;
  }

  public void add(int column, double value) {
    data[colDisplace + column] += value;
  }

  public Vector[] split2() {
    Vector[] splitV = new Vector[2];
    // case where the split creats 2 even part
    if (dim % 2 == 0) {
      int newDim = dim / 2;
      splitV[0] = new Vector(data, colDisplace, newDim);
      splitV[1] = new Vector(data, colDisplace + newDim, newDim);
    }
    /* case where the split is not even:
     * The bottom part of the split in the vector will have 1 extra entry than the top part
     */
    else {
      int newDim = dim / 2;
      splitV[0] = new Vector(data, colDisplace, newDim);
      splitV[1] = new Vector(data, colDisplace + newDim, newDim + 1);
    }
    return splitV;
  }

  public int getDim() {
    return dim;
  }
}
