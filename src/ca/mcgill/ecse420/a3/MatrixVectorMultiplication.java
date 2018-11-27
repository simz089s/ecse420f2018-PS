package ca.mcgill.ecse420.a3;

import ca.mcgill.ecse420.a1.MatrixMultiplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatrixVectorMultiplication {
  private static final int NUMBER_THREADS = 2;
  private static final int MATRIX_SIZE = 20;

  // To make it easier to use inside the nested ParallelMatrixMultiplication class
  protected static double[][] matrix;
  protected static double[] vector;

  public static void main(String[] args) {

    // Generate two random matrices, same size
    MatrixVectorMultiplication.matrix = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
    MatrixVectorMultiplication.vector = generateRandomVector(MATRIX_SIZE);
    measureSequentialTime();
    measureParallelTime();
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
}
