package app;

import org.paukov.combinatorics3.Generator;

import java.util.*;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {

        int rows = 20;
        int cols = 50;
        //double p = ThreadLocalRandom.current().nextDouble(0, 1);
        double p = 0.5;
        System.out.println("p = " + p +"\n");

        int[][] matrix = generateMatrix(rows, cols, p);


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }

        int t = 3;
        System.out.println("\nt = " + t);

        Integer[] x = new Integer[cols];
        for (int i = 0; i < x.length; i++) {
            if (i <= t)
                x[i] = 1;
            else x[i] = 0;
        }
        Collections.shuffle(Arrays.asList(x));
        for (Integer elem : x) System.out.print(elem + " ");

        System.out.println("\n\ns = ");
        List<Integer> s = matrixMultiplication(matrix,x);
        for (Integer integer : s) System.out.println(integer);
        System.out.println();

        int[][] inMemoryMatrix = {{0, 0, 0, 1, 1, 1, 0, 0, 1, 1},
                                {1, 0, 1, 0, 0, 0, 1, 1, 0, 0},
                                {0, 1, 0, 0, 1, 1, 1, 1, 0, 1}};
        List<Integer> inMemoryList = List.of(1, 3, 2);
        solution(inMemoryMatrix,inMemoryList,4);
    }

    public static void solution(int [][] matrix, List<Integer> syndromeList, int t) {
        sortMatrix(matrix, syndromeList);
        List<Integer> syndrome = syndromeList.stream().sorted().toList();

        List<VectorList> listOfVectors = new ArrayList<>();
        int[] visited = new int[matrix[0].length];

        for (int i = 0; i < matrix.length; i++) {
            List<Integer> listOfOnes = new ArrayList<>();
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == 1 && visited[j] == 0) {
                    visited[j] = 1;
                    listOfOnes.add(j);
                }
            }
            findAllCombinations(matrix,listOfOnes,listOfVectors,syndrome,i,t);
            listOfOnes.clear();
            System.out.println("-----------------------------------------");
        }
    }

    private static void findAllCombinations(int[][] matrix, List<Integer> listOfOnes, List<VectorList> listOfVectors,List<Integer> syndrome, int line, int t) {
        List<List<Integer>> cols;
        if (listOfVectors.isEmpty()) {
            cols = Generator.combination(listOfOnes)
                    .simple(syndrome.get(line))
                    .stream()
                    .toList();

            for (List<Integer> list : cols) {
                VectorList vectorList = new VectorList();
                vectorList.setColumns(list);
                if (calculateAndCheckSyndromeCols(matrix,vectorList,syndrome,listOfOnes,t,line))
                    listOfVectors.add(vectorList);
            }
        } else {
            List<VectorList> auxListOfVectors = new ArrayList<>(listOfVectors);
            listOfVectors.clear();
            for (VectorList vectorList : auxListOfVectors) {
                List<Integer> auxList = Stream.concat(vectorList.getColumns().stream(), listOfOnes.stream()).toList();
                cols = doCombinations(auxList,vectorList,line);

                for (List<Integer> list : cols) {
                    VectorList vectorAuxList = new VectorList();
                    vectorAuxList.setColumns(list);
                    if (calculateAndCheckSyndromeCols(matrix,vectorAuxList,vectorList.getSyndromeCols(),listOfOnes,t,line))
                        listOfVectors.add(vectorAuxList);
                }
            }
        }
    }

    public static List<List<Integer>> doCombinations(List<Integer> auxList, VectorList vectorList, int line) {
        List<List<Integer>> cols = Generator.combination(auxList)
                .simple(vectorList.getColumns().size() + vectorList.getSyndromeCols().get(line))
                .stream()
                .toList();

        List<List<Integer>> result = new ArrayList<>();
        for (List<Integer> list : cols)
            if (list.containsAll(vectorList.getColumns()))
                result.add(list);
        return result;
    }

    private static boolean calculateAndCheckSyndromeCols(int[][] matrix, VectorList vectorList, List<Integer> syndrome, List<Integer> listOfOnes, int t, int line) {
        List<Integer> auxSyndromeCols = new ArrayList<>(syndrome);
        Integer[] array = new Integer[syndrome.size()];
        for (Integer element : vectorList.getColumns()) {
            if (listOfOnes.contains(element)) {
                for (int i = 0; i < matrix.length; i++)
                    if (line != 0) {
                        array[i] = auxSyndromeCols.get(i) - matrix[i][element];
                        auxSyndromeCols.set(i,array[i]);
                    }
                    else array[i]=auxSyndromeCols.get(i)-matrix[i][element];
            }
        }
        vectorList.setSyndromeCols(Arrays.asList(array));
        System.out.println(vectorList);

        if (line == matrix.length-1)
            return true;
        return vectorList.getColumns().size() + auxSyndromeCols.get(line+1) <= t;
    }

    private static void sortMatrix(int[][] matrix, List<Integer> syndrome) {
        int[] array = new int [syndrome.size()];
        for (int i = 0; i < array.length; i++)
            array[i] = syndrome.get(i)-1;

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < array.length; i++)
            list.add(i);
        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(array[o1],array[o2]);
            }
        };
        Collections.sort(list,comparator);


        for (int i = 0; i < array.length; i++) {
            int currentRow = array[i];
            int nextRow = i < array.length - 1 ? array[i + 1] : -1;


            if (nextRow != -1 && nextRow < currentRow) {
                int[] tempRow = matrix[currentRow];
                matrix[currentRow] = matrix[nextRow];
                matrix[nextRow] = tempRow;

                array[i] = nextRow;
                array[i + 1] = currentRow;

                i = -1;
            }
        }
    }

    public static int[][] generateMatrix(int rows, int cols, double p) {
        int[][] matrix = new int[rows][cols];
        int numZeros = (int) (rows * cols * p);
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (numZeros > 0) {
                    matrix[i][j] = 0;
                    numZeros--;
                } else matrix[i][j] = 1;
                list.add(matrix[i][j]);
            }
        }

        Collections.shuffle(list);

        int k = 0;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                matrix[i][j] = list.get(k++);

        return matrix;
    }

    public static List<Integer> matrixMultiplication(int[][] matrix, Integer[] array) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            int sum = 0;
            for (int j = 0; j < cols; j++) {
                sum += matrix[i][j] * array[j];
            }
            list.add(sum);
        }

        return list;
    }
}