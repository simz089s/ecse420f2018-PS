package ca.mcgill.ecse420.a3;

public class Matrix {
  int rowDim;
  int columnDim;
  double[][] data;
  int rowDisplace;
  int columnDisplace;

  public Matrix(int rowDim, int columnDim, int rowDisplace, int columnDisplace, double[][] data) {
    this.rowDim = rowDim;
    this.columnDim = columnDim;
    this.rowDisplace = rowDisplace;
    this.columnDisplace = columnDisplace;
    this.data = data;
  }

  public Matrix(double[][] data) {
    this.rowDim = data.length;
    this.columnDim = data[0].length;
    this.data = data;
    this.rowDisplace = 0;
    this.columnDisplace = 0;
  }

  public double get(int row, int column) {
    return data[row + rowDisplace][column + columnDisplace];
  }

  public void set(int row, int column, double value) {
    data[row + rowDisplace][column + columnDisplace] = value;
  }

  public Matrix[][] split4() {
    Matrix[][] newMatrix = new Matrix[2][2];
    int newRowDim = rowDim / 2;
    int newColDim = columnDim / 2;

    // Case where the matrix has an even number of rows
    if (rowDim % 2 == 0) {
      // Case where the matrix has an even number of columns
      if (columnDim % 2 == 0) {
        newMatrix[0][0] = new Matrix(newRowDim, newColDim, rowDisplace, columnDisplace, data);
        newMatrix[0][1] =
            new Matrix(newRowDim, newColDim, rowDisplace, columnDisplace + newColDim, data);
        newMatrix[1][0] =
            new Matrix(newRowDim, newColDim, rowDisplace + newRowDim, columnDisplace, data);
        newMatrix[1][1] =
            new Matrix(
                newRowDim, newColDim, rowDisplace + newRowDim, columnDisplace + newColDim, data);
      }
      /* Case where the matrix has an odd number of columns the
       * the top right and bottom right matrices will then have an extra column
       */
      else {
        newMatrix[0][0] = new Matrix(newRowDim, newColDim, rowDisplace, columnDisplace, data);
        newMatrix[0][1] =
            new Matrix(newRowDim, newColDim + 1, rowDisplace, columnDisplace + newColDim, data);
        newMatrix[1][0] =
            new Matrix(newRowDim, newColDim, rowDisplace + newRowDim, columnDisplace, data);
        newMatrix[1][1] =
            new Matrix(
                newRowDim,
                newColDim + 1,
                rowDisplace + newRowDim,
                columnDisplace + newColDim,
                data);
      }
    }
    /* Case where the matrix has an odd number of rows the
     * the bottom left and bottom right matrices will then have an extra row
     */
    else {
      if (columnDim % 2 == 0) {
        newMatrix[0][0] = new Matrix(newRowDim, newColDim, rowDisplace, columnDisplace, data);
        newMatrix[0][1] =
            new Matrix(newRowDim, newColDim, rowDisplace, columnDisplace + newColDim, data);
        newMatrix[1][0] =
            new Matrix(newRowDim + 1, newColDim, rowDisplace + newRowDim, columnDisplace, data);
        newMatrix[1][1] =
            new Matrix(
                newRowDim + 1,
                newColDim,
                rowDisplace + newRowDim,
                columnDisplace + newColDim,
                data);
      }
      /* Case where the matrix has an odd number of columns the
       * the top right and bottom right matrices will then have an extra column
       */
      else {
        newMatrix[0][0] = new Matrix(newRowDim, newColDim, rowDisplace, columnDisplace, data);
        newMatrix[0][1] =
            new Matrix(newRowDim, newColDim + 1, rowDisplace, columnDisplace + newColDim, data);
        newMatrix[1][0] =
            new Matrix(newRowDim + 1, newColDim, rowDisplace + newRowDim, columnDisplace, data);
        newMatrix[1][1] =
            new Matrix(
                newRowDim + 1,
                newColDim + 1,
                rowDisplace + newRowDim,
                columnDisplace + newColDim,
                data);
      }
    }
    return newMatrix;
  }

  public int getRowDim() {
    return rowDim;
  }

  public int getColumnDim() {
    return columnDim;
  }
}
