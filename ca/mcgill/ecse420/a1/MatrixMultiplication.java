package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatrixMultiplication {

  private static final int NUMBER_THREADS = 2;
  private static final int MATRIX_SIZE = 500;

  // Result arrays
  protected static double[][] resultParallel = new double[MATRIX_SIZE][MATRIX_SIZE];
  protected static double[][] resultSequencial = new double[MATRIX_SIZE][MATRIX_SIZE];

  public static void main(String[] args) {

    // Generate two random matrices, same size
    double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
    double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);

    // Time methods
    long startTime = 0L;
    long endTime = 0L;
    long runTime = 0L;

    startTime = System.currentTimeMillis();
    sequentialMultiplyMatrix(a, b);
    endTime = System.currentTimeMillis();
    runTime = endTime - startTime;
    System.out.println("Runtime (sequential) : " + runTime);

    startTime = System.currentTimeMillis();
    parallelMultiplyMatrix(a, b);
    endTime = System.currentTimeMillis();
    runTime = endTime - startTime;
    System.out.println("Runtime (parallel) : " + runTime);

    // Print matrices
    // printMatrix(a,b);
  }

  public static void printMatrix(double[][] a, double[][] b) {
    for (int i = 0; i < MATRIX_SIZE; i++) {
      for (int j = 0; j < MATRIX_SIZE; j++) {
        System.out.print(a[i][j] + "|");
      }
      System.out.print("\n");
    }
    System.out.print("\n");
    for (int i = 0; i < MATRIX_SIZE; i++) {
      for (int j = 0; j < MATRIX_SIZE; j++) {
        System.out.print(b[i][j] + "|");
      }
      System.out.print("\n");
    }
    System.out.print("\n");
    for (int i = 0; i < MATRIX_SIZE; i++) {
      for (int j = 0; j < MATRIX_SIZE; j++) {
        System.out.print(resultSequencial[i][j] + "|");
      }
      System.out.print("\n");
    }
    System.out.print("\n");
    for (int i = 0; i < MATRIX_SIZE; i++) {
      for (int j = 0; j < MATRIX_SIZE; j++) {
        System.out.print(resultParallel[i][j] + "|");
      }
      System.out.print("\n");
    }
  }
  /**
   * Returns the result of a sequential matrix multiplication The two matrices are randomly
   * generated
   *
   * @param a is the first matrix
   * @param b is the second matrix
   * @return the result of the multiplication
   */
  public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b) {
    for (int i = 0; i < MATRIX_SIZE; i++) {
      for (int j = 0; j < MATRIX_SIZE; j++) {
        for (int k = 0; k < MATRIX_SIZE; k++) {
          resultSequencial[i][j] += a[i][k] * b[k][j];
        }
      }
    }
    return resultSequencial;
  }

  /**
   * Returns the result of a concurrent matrix multiplication The two matrices are randomly
   * generated
   *
   * @param a is the first matrix
   * @param b is the second matrix
   * @return the result of the multiplication
   */
  public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {
    ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);
    int cutsize = MATRIX_SIZE / NUMBER_THREADS;
    int cursor = 0;
    while (cursor < MATRIX_SIZE) {
      executor.execute(new MatrixMultiplication.ParallelMatrixCalculation(cursor, cutsize, a, b));
      cursor = cutsize;
      int cutsize2 = cutsize + cutsize;
      cutsize = cutsize2 > MATRIX_SIZE ? MATRIX_SIZE : cutsize2;
    }
    executor.shutdown();
    return resultParallel;
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

  public static class ParallelMatrixCalculation extends MatrixMultiplication implements Runnable {
    private int start;
    private int end;
    private double[][] firstMatrix;
    private double[][] secondMatrix;

    public ParallelMatrixCalculation(int start, int end, double[][] a, double[][] b) {
      this.start = start;
      this.end = end;
      this.firstMatrix = a;
      this.secondMatrix = b;
    }

    @Override
    public void run() {
      for (int i = start; i < end; i++) {
        for (int j = 0; j < MATRIX_SIZE; j++) {
          for (int k = 0; k < MATRIX_SIZE; k++) {
            resultParallel[i][j] += firstMatrix[i][k] * secondMatrix[k][j];
          }
        }
      }
    }
  }
}
