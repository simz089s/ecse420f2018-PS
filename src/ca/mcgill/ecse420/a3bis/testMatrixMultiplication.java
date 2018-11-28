package ca.mcgill.ecse420.a3bis;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class testMatrixMultiplication {
  private static int MATRIX_SIZE = 110;
  public static ExecutorService exec = Executors.newCachedThreadPool();

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    double[] v1 = generateRandomVector(MATRIX_SIZE);
    double[] v2 = generateRandomVector(MATRIX_SIZE);
    double[][] m = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
    // System.out.println(Arrays.toString(vectorAddSeq(v1, v2)));
    // System.out.println(Arrays.toString(vectorAddPar(v1, v2)));

    System.out.println(
        Arrays.toString(sequentialMultiplyMatrix(m, v1))
            .replace("], [", "],\n[")
            .replace("[[", "[\n[")
            .replace("]]", "]\n]"));
    System.out.println(
        Arrays.toString(parallelMultiplyMatrix(m, v1))
            .replace("], [", "],\n[")
            .replace("[[", "[\n[")
            .replace("]]", "]\n]"));
  }

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

  public static double[] vectorAddSeq(double[] v1, double[] v2) {
    double[] result = new double[MATRIX_SIZE];
    for (int i = 0; i < MATRIX_SIZE; i++) {
      result[i] += v1[i] + v2[i];
    }
    return result;
  }

  public static double[] vectorAddPar(double[] v1, double[] v2)
      throws ExecutionException, InterruptedException {
    double[] result = new double[MATRIX_SIZE];
    Vector ans = new Vector(result);
    Vector v1V = new Vector(v1);
    Vector v2V = new Vector(v2);
    exec.submit(new AddVectorTask(v1V, v2V, ans)).get();
    exec.shutdown();
    return result;
  }

  public static double[] sequentialMultiplyMatrix(double[][] matrix, double[] vector) {
    double[] result = new double[MATRIX_SIZE];
    for (int i = 0; i < MATRIX_SIZE; i++) {
      for (int j = 0; j < MATRIX_SIZE; j++) {
        result[i] += matrix[i][j] * vector[j];
      }
    }
    return result;
  }

  public static double[] parallelMultiplyMatrix(double[][] matrix, double[] vector)
      throws ExecutionException, InterruptedException {
    Matrix m = new Matrix(matrix);
    Vector v = new Vector(vector);
    double[] ans = new double[v.getDim()];
    Vector vAns = new Vector(ans);
    exec.submit(new multiplyMatrixVectorClass(m, v, vAns)).get();
    exec.shutdown();
    return ans;
  }

  static class AddVectorTask implements Runnable {
    private Vector left;
    private Vector right;
    private Vector ans;

    public AddVectorTask(Vector left, Vector right, Vector ans) {
      this.left = left;
      this.right = right;
      this.ans = ans;
    }

    @Override
    public void run() {
      try {
        if (left.getDim() == 1) {
          ans.set(0, left.get(0) + right.get(0));
          return;
        } else {
          Vector[] leftSplit = left.split2();
          Vector[] rightSplit = right.split2();
          Vector[] ansSplit = ans.split2();
          Future<?> top = exec.submit(new AddVectorTask(leftSplit[0], rightSplit[0], ansSplit[0]));
          Future<?> bottom =
              exec.submit(new AddVectorTask(leftSplit[1], rightSplit[1], ansSplit[1]));
          top.get();
          bottom.get();
        }
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }
  }

  static class multiplyMatrixVectorClass implements Runnable {
    private Matrix m;
    private Vector v;
    private Vector ans;
    private Vector left;
    private Vector right;

    public multiplyMatrixVectorClass(Matrix m, Vector v, Vector ans) {
      this.m = m;
      this.v = v;
      this.ans = ans;
      this.left = new Vector(new double[ans.getDim()]);
      this.right = new Vector(new double[ans.getDim()]);
    }

    @Override
    public void run() {
      try {
        if (m.getColumnDim() == 1 || m.getRowDim() == 1) {
          for (int i = 0; i < m.getRowDim(); i++) {
            for (int j = 0; j < m.getColumnDim(); j++) {
              ans.add(i, m.get(i,j)*v.get(j));
            }
          }
          return;
        }
        Matrix[][] mSplited = m.split4();
        Vector[] vSplited = v.split2();
        Vector[] leftSplit = left.split2();
        Vector[] rightSplit = right.split2();
        Future<?> topLeft =
            exec.submit(new multiplyMatrixVectorClass(mSplited[0][0], vSplited[0], leftSplit[0]));
        Future<?> topRight =
            exec.submit(new multiplyMatrixVectorClass(mSplited[0][1], vSplited[1], rightSplit[0]));
        Future<?> bottomLeft =
            exec.submit(new multiplyMatrixVectorClass(mSplited[1][0], vSplited[0], leftSplit[1]));
        Future<?> bottomRight =
            exec.submit(new multiplyMatrixVectorClass(mSplited[1][1], vSplited[1], rightSplit[1]));
        topLeft.get();
        topRight.get();
        bottomLeft.get();
        bottomRight.get();
        Future<?> add = exec.submit(new AddVectorTask(left, right, ans));
        add.get();
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }
  }
}
