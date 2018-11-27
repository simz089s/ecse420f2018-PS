package ca.mcgill.ecse420.a3;

import ca.mcgill.ecse420.a1.MatrixMultiplication;

import java.util.concurrent.*;

public class MatrixVectorMultiplication {
  private static final int MATRIX_SIZE = 4;

  // To make it easier to use inside the nested ParallelMatrixMultiplication class
  protected static double[][] matrix;
  protected static double[] vector;
  protected static ExecutorService executor = Executors.newCachedThreadPool();

  public static void main(String[] args) {

    // Generate two random matrices, same size
    MatrixVectorMultiplication.matrix = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
    MatrixVectorMultiplication.vector = generateRandomVector(MATRIX_SIZE);
    // measureSequentialTime();
    // measureParallelTime();
    printMatrix(matrix);
    System.out.println();
    printVector(vector);
    System.out.println();
    printVector(sequentialMultiplyMatrix(matrix, vector));
    System.out.println();
    Future<double[]> v =
        executor.submit(new sumVector.multiplyMatrixVector(0, vector.length, 0, vector.length));
    try {
      printVector(v.get());
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
  }

  public static void measureSequentialTime() {
    long startTime = System.currentTimeMillis();
    sequentialMultiplyMatrix(matrix, vector);
    long endTime = System.currentTimeMillis();
    long runTime = endTime - startTime;
    System.out.println("Runtime (sequential) : " + runTime);
  }

  public static void measureParallelTime() {
    long startTime = System.currentTimeMillis();
    parallelMultiplyMatrix(matrix, vector);
    long endTime = System.currentTimeMillis();
    long runTime = endTime - startTime;
    System.out.println("Runtime (parallel) : " + runTime);
  }

  /**
   * Returns the result of a sequential matrix multiplication The two matrices are randomly
   * generated
   *
   * @param matrix is the matrix
   * @param vector is the vector
   * @return the result of the multiplication
   */
  public static double[] sequentialMultiplyMatrix(double[][] matrix, double[] vector) {
    double[] result = new double[MATRIX_SIZE];
    for (int i = 0; i < MATRIX_SIZE; i++) {
      for (int j = 0; j < MATRIX_SIZE; j++) {
        result[i] += matrix[i][j] * vector[j];
      }
    }
    return result;
  }

  /**
   * Returns the result of a concurrent matrix multiplication The two matrices are randomly
   * generated
   *
   * @param matrix is the matrix
   * @param vector is the vector
   * @return the result of the multiplication
   */
  public static double[] parallelMultiplyMatrix(double[][] matrix, double[] vector) {
    return null;
  }

  /**
   * Populates a matrix of given size with randomly generated integers between 0-10.
   *
   * @param numRows number of rows
   * @param numCols number of cols
   * @return matrix
   */
  private static double[][] generateRandomMatrix(int numRows, int numCols) {
    double matrix[][] = new double[numRows][numCols];
    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < numCols; col++) {
        matrix[row][col] = (double) ((int) (Math.random() * 10.0));
      }
    }
    return matrix;
  }

  private static double[] generateRandomVector(int num) {
    double vector[] = new double[num];
    for (int row = 0; row < num; row++) {
      vector[row] = (double) ((int) (Math.random() * 10.0));
    }
    return vector;
  }

  public static void printMatrix(double[][] matrix) {
    for (double[] r : matrix) {
      for (int i = 0; i < r.length; i++) {
        System.out.print(r[i] + "\t");
      }
      System.out.println();
    }
  }

  public static void printVector(double[] vector) {
    for (int i = 0; i < vector.length; i++) {
      System.out.print(vector[i] + "\t");
    }
    System.out.println();
  }

  public static class sumVector extends MatrixMultiplication implements Runnable {
    private int start;
    private int end;
    private double[] firstVector;
    private double[] secondVector;
    protected double[] answerVector;

    public sumVector(
        double[] firstVector, double[] secondVector, int start, int end, double[] answerVector) {
      this.start = start;
      this.end = end;
      this.firstVector = firstVector;
      this.secondVector = secondVector;
      this.answerVector = answerVector;
    }

    @Override
    public void run() {
      if (start == end - 1) {
        answerVector[start] = firstVector[start] + secondVector[start];
      }
      int newStart = end / 2;
      Future<?> top =
          executor.submit(new sumVector(firstVector, secondVector, start, newStart, answerVector));
      Future<?> bottom =
          executor.submit(new sumVector(firstVector, secondVector, newStart, end, answerVector));
      try {
        top.get();
        bottom.get();
      } catch (Exception e) {
        System.out.println("Failed to add properly");
      }
    }

    public static class multiplyMatrixVector implements Callable<double[]> {
      private int rowStart;
      private int rowEnd;
      private int columnStart;
      private int columnEnd;
      protected double[] answerVector;

      public multiplyMatrixVector(int rowStart, int rowEnd, int columnStart, int columnEnd) {
        this.rowStart = rowStart;
        this.rowEnd = rowEnd;
        this.columnStart = columnStart;
        this.columnEnd = columnEnd;
        this.answerVector = new double[columnEnd-columnStart];
      }

      @Override
      public double[] call() throws Exception {
        if (answerVector.length == 1) {
          answerVector[0] = matrix[rowStart][columnStart] * vector[columnStart];
          return answerVector;
        }
        int newRow = (rowEnd-rowStart) / 2 + rowStart;
        int newColumn = (columnEnd-columnStart) / 2 + columnStart;
        Future<double[]> topLeft =
            executor.submit(new multiplyMatrixVector(rowStart, newRow, columnStart, newColumn));
        Future<double[]> topRight =
            executor.submit(new multiplyMatrixVector(rowStart, newRow, newColumn, columnEnd));
        Future<double[]> bottomLeft =
            executor.submit(new multiplyMatrixVector(newRow, rowEnd, columnStart, newColumn));
        Future<double[]> bottomRight =
            executor.submit(new multiplyMatrixVector(newRow, rowEnd, newColumn, columnEnd));

        //        Future<?> topVector =
        //            executor.submit(
        //                new sumVector(topLeft.get(), topRight.get(), rowStart, newRow,
        // answerVector));
        //        Future<?> bottomVector =
        //            executor.submit(
        //                new sumVector(
        //                    bottomLeft.get(), bottomRight.get(), newRow + 1, rowEnd,
        // answerVector));
        //        topVector.get();
        //        bottomVector.get();
        double[] topLeftAr = topLeft.get();
        double[] topRightAr = topRight.get();
        double[] bottomLeftAr = bottomLeft.get();
        double[] bottomRightAr = bottomRight.get();
        for (int i = 0; i < topLeftAr.length; i++) {
          answerVector[i] = topLeftAr[i] + topRightAr[i];
          answerVector[i+topLeftAr.length] = bottomLeftAr[i] + bottomRightAr[i];
        }
        return answerVector;
      }
    }
  }
}
