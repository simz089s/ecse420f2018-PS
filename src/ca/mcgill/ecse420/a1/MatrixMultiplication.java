package ca.mcgill.ecse420.a1;

import com.sun.tools.javadoc.Start;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatrixMultiplication {

  private static final int NUMBER_THREADS = 1;
  private static final int MATRIX_SIZE = 2000;
  protected static double[][] resultParallel = new double[MATRIX_SIZE][MATRIX_SIZE];

  public static void main(String[] args) {

    // Generate two random matrices, same size
    double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
    double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
    sequentialMultiplyMatrix(a, b);
    parallelMultiplyMatrix(a, b);
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
    // TODO
    return null;
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
	  ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);\
	  for (int i = 0; i<NUMBER_THREADS-1; i++) {
		  executor.execute(new ParallelMatrixCalculation(i*NUMBER_THREADS, 0,
				  a,b));
	  }
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
        int column = 0;
        int origin = start;
    	while(start < MATRIX_SIZE)
        {
            for(int i = 0; i<MATRIX_SIZE; i++) {
                for (int j = 0; j<MATRIX_SIZE; j++) {
                    resultParallel[start][column] += firstMatrix[i][j] + secondMatrix[j][i];
                }
            }
            start++;
            if(origin-start > MATRIX_SIZE/NUMBER_THREADS) {
                start = 
            }
        }
	}
  }
}
