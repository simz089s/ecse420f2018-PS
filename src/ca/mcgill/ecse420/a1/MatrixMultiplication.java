package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatrixMultiplication {

  private static final int NUMBER_THREADS = 2;
  private static final int MATRIX_SIZE = 2000;

  // To make it easier to use inside the nested ParallelMatrixMultiplication class
  protected static double[][] a;
  protected static double[][] b;

  public static void main(String[] args) {

    // Generate two random matrices, same size
    MatrixMultiplication.a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
    MatrixMultiplication.b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
    measureSequentialTime();
    measureParallelTime();
  }

  public static void measureSequentialTime() {
    long startTime = System.currentTimeMillis();
    sequentialMultiplyMatrix(a, b);
    long endTime = System.currentTimeMillis();
    long runTime = endTime - startTime;
    System.out.println("Runtime (sequential) : " + runTime);
  }

  public static void measureParallelTime() {
    long startTime = System.currentTimeMillis();
    parallelMultiplyMatrix(a, b);
    long endTime = System.currentTimeMillis();
    long runTime = endTime - startTime;
    System.out.println("Runtime (parallel) : " + runTime);
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
    double[][] result = new double[MATRIX_SIZE][MATRIX_SIZE];
    for (int i = 0; i < MATRIX_SIZE; i++) {
      for (int j = 0; j < MATRIX_SIZE; j++) {
        for (int k = 0; k < MATRIX_SIZE; k++) {
          result[i][j] += a[i][k] * b[k][j];
        }
      }
    }
    return result;
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
    double[][] result = new double[MATRIX_SIZE][MATRIX_SIZE];
    ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);
    // number of rows to be calculated per threads
    int numRow = MATRIX_SIZE / NUMBER_THREADS;
    for (int i = 0; i < NUMBER_THREADS; i++) {
      if (MATRIX_SIZE % NUMBER_THREADS != 0 && i == NUMBER_THREADS - 1) {
        executor.execute(new ParallelMatrixMultiplication(result, i * numRow, MATRIX_SIZE));
      } else {
        executor.execute(new ParallelMatrixMultiplication(result, i * numRow, (i + 1) * numRow));
      }
    }
    executor.shutdown();
    while (!executor.isTerminated()) ;

    return result;
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

  public static class ParallelMatrixMultiplication extends MatrixMultiplication
      implements Runnable {
    private double[][] result;
    private int start;
    private int end;

    public ParallelMatrixMultiplication(double[][] result, int start, int end) {
      this.start = start;
      this.end = end;
      this.result = result;
    }

    @Override
    public void run() {
      for (int i = start; i < end; i++) {
        for (int j = 0; j < MATRIX_SIZE; j++) {
          for (int k = 0; k < MATRIX_SIZE; k++) {
            result[i][j] += a[i][k] + b[k][j];
          }
        }
      }
    }
  }
}
