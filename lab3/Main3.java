
import main.java.lab2.CramersRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main3 {

    private static int x1min = -25;
    private static int x1max = 75;
    private static int x2min = 5;
    private static int x2max = 40;
    private static int x3min = 15;
    private static int x3max = 25;
    private static double ymin = 200. + (x1max + x2max + x3max)/3.;
    private static double ymax = 200. + (x1min + x2min + x3min)/3.;
    private static int m = 3;
    private static  int N = 4;
    private static Random r = new Random();
    private static Data data = new Data();

    public static void main(String[] args) {
        while (!make()) {
            m++;
        }
    }

    public static boolean make() {

        if(N > 4){
            System.out.println(
                    "Поміняйте табличні значення в коді!!!"
            );
        }

        int[][] matrix = new int[][]{
                {1, -1, -1, -1},
                {1, -1, 1, 1},
                {1, 1, -1, 1},
                {1, 1, 1, -1}
        };

        double[][] natural_matrix = new double[4][3];

        List<List<Double>> y = new ArrayList<>() {
        };

        for (int j = 0; j < N; j++) {
            natural_matrix[j][0] = (x1min + (x1max - x1min) * r.nextDouble());
            natural_matrix[j][1] = (x1min + (x1max - x1min) * r.nextDouble());
            natural_matrix[j][2] = (x1min + (x1max - x1min) * r.nextDouble());

            List<Double> random_y = new ArrayList<>();
            for (int i = 0; i < m; i++) {
                random_y.add((ymin + (ymax - ymin) * r.nextDouble()));
            }
            y.add(random_y);
        }
        // Знайдемо середні значення функції відгуку за рядками:
        double middle_y[] = new double[N];

        double sum_row;
        for (int i = 0; i < N; i++) {
            sum_row = 0;
            for (int j = 0; j < m; j++) {
                sum_row += y.get(i).get(j);
            }
            middle_y[i] = sum_row / m;
        }

        double my = (middle_y[0] + middle_y[1] + middle_y[2] + middle_y[3]) / 4;

        double[] mx = new double[3];
        double[] a123 = new double[3];

        for (int i = 0; i < 3; i++) {
            mx[i] = (natural_matrix[0][i] + natural_matrix[1][i] + natural_matrix[2][i] + natural_matrix[3][i]) / 4;
            a123[i] = (natural_matrix[0][i] * middle_y[0] + natural_matrix[1][i]* middle_y[1] + natural_matrix[2][i] * middle_y[2] + natural_matrix[3][i] * middle_y[3])/4;
        }

        // a[]= [a11, a22, a33] , a12, a21, a13, a31, a23, a32;
        double[] a = new double[3];
        double a12 =0 , a13 =0 , a23 =0;

        for (int i = 0; i < 3; i++) {
            a[i] = (Math.pow(natural_matrix[0][i], 2) + Math.pow(natural_matrix[1][i], 2) + Math.pow(natural_matrix[2][i], 2) + Math.pow(natural_matrix[3][i], 2)) / 4;
            //a123[i] = natural_matrix[i][0] * middle_y[0] + natural_matrix[i][1] * middle_y[1] + natural_matrix[i][2] * middle_y[2] + natural_matrix[i][3] * middle_y[3];
        }

        for( int i=0; i < 4; i++){
            a12 += natural_matrix[i][0] *natural_matrix[i][1];
            a13 += natural_matrix[i][0] *natural_matrix[i][2];
            a23 += natural_matrix[i][1] *natural_matrix[i][2];
        }

        a12 = a12 / 4;
        a13 = a13 / 4;
        a23 = a23 / 4;
        double a31 = a13;
        double a32 = a23;
        double a21 = a12;

        CramersRule.Matrix mat = new CramersRule.Matrix(
                Arrays.asList(1., mx[0], mx[1], mx[2]),
                Arrays.asList(mx[0], a[0], a12, a13),
                Arrays.asList(mx[1], a12, a[1], a32),
                Arrays.asList(mx[2], a13, a23, a[2]));
        List<Double> c = Arrays.asList(my, a123[0], a123[1], a123[2]);
        List<Double> b = CramersRule.cramersRule(mat, c);

        System.out.println("b : "+b.get(0) +" "+b.get(1) +" "+b.get(2) +" "+b.get(3));

        // Перевірка однорідності дисперсії за критерієм Кохрена:
        //дисперсія по рядкам  q^2
        double dispersion_y[] = new double[4];
        double max_dispersion = 0;
        double sum_dispersion = 0;

        double sum_deviation;
        double deviation;
        for(int i=0; i < 4; i++) {
            sum_deviation = 0;
            for (int j = 0; j < m; j++) {
                deviation = y.get(i).get(j) - middle_y[i];
                sum_deviation += Math.pow(deviation, 2);
            }
            dispersion_y[i] = sum_deviation/m;
            sum_dispersion += dispersion_y[i];

            if(dispersion_y[i] > max_dispersion){
                max_dispersion = dispersion_y[i];
            }
        }

        double Gp = max_dispersion / sum_dispersion;

        if (Gp * 1000 > data.cohrenCriterium[m][N]){
            System.out.println("Дисперсія неоднорідна!");
            return false;
        }
        System.out.println("Дисперсія однорідна!");

        // оцінимо значимість коефіцієнтів регресії згідно критерію Стьюдента

        double dispersion_Sb = sum_dispersion / N;

        double dispersion_Sbs = dispersion_Sb / (N*m);

        double dispersion_S = Math.sqrt(dispersion_Sbs);

        // Визначення оцінок коефіцієнтів
        double[] all_b = new double[4];

        for (int i =0; i < N; i++){
            all_b[0] += middle_y[i]*matrix[i][0] /4;
            all_b[1] += middle_y[i]*matrix[i][1] /4;
            all_b[2] += middle_y[i]*matrix[i][2] /4;
            all_b[3] += middle_y[i]*matrix[i][3] /4;
        }
        double all_t[] = new double[4];
        all_t[0] = Math.abs(all_b[0])/ dispersion_S;
        all_t[1] = Math.abs(all_b[1])/ dispersion_S;
        all_t[2] = Math.abs(all_b[2])/ dispersion_S;
        all_t[3] = Math.abs(all_b[3])/ dispersion_S;

        int f1 = m -1;
        int f2 = N;

        int f3 = f1*f2;

        //  від табл значень
        double t = 2.306;

        for(int i=0; i< 4; i++){
            if(all_t[i] < t){
                b.set(i, 0.);
            }
        }

        double[] yi = new double[4];
        for(int i=0; i < 4; i++){
            yi[i] = b.get(0) + b.get(1)*natural_matrix[i][0] + b.get(2)*natural_matrix[i][1] + b.get(3)*natural_matrix[i][2];
            System.out.println(" y"+i+" = "+ yi[i]);
        }

        //d- кількість значимих коефіцієнтів d=2
        int d = 2;
        int f4 = N - d;

        double Sad =0;
        for(int i=0; i < 4; i++) {
            Sad += Math.pow(yi[i] - middle_y[i], 2);
        }

        Sad = Sad *m / f4;

        double Fp = Sad / dispersion_Sb;

        // зробити з таблиці Ft
        double Ft = 4.5;

        if(Fp > Ft){
            System.out.println("Отже, рівняння регресії неадекватно оригіналу при рівні значимості 0.05");
        }
        return true;
    }

}
