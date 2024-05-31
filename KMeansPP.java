import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class KMeansPP {

    public static Map<String, double[]> readCSV(String csvFile) {
        Map<String, double[]> dataMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            br.readLine(); // Skip the header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String name = values[0];
                double x = Double.parseDouble(values[1]);
                double y = Double.parseDouble(values[2]);
                dataMap.put(name, new double[] { x, y });
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return dataMap;
    }

    public static void main(String[] args) {

        if (args.length == 0 || args.length > 2) {
            System.out.println("Usage: java KMeansPP <file path> [<number of clusters (k)>]");
            return;
        }
        String filePath;
        filePath = args[0];

        Map<String, double[]> dataMap = readCSV(filePath);
        if (dataMap == null) {
            System.out.println("Failed to read data from the CSV file.");
            return;
        }

        List<String> names = new ArrayList<>(dataMap.keySet());
        double[][] data = new double[names.size()][2];
        for (int i = 0; i < names.size(); i++) {
            data[i] = dataMap.get(names.get(i));
        }

        int k;
        int[] assignments; 
        if (args.length == 1) {
            k = findOptimalK(data);
            double[][] centroids = getKMeansPPCentroids(data, k);
            assignments = kMeansFit(data, centroids, k);
        } else {
            k = Integer.parseInt(args[1]);
            double[][] centroids = getKMeansPPCentroids(data, k);
            assignments = kMeansFit(data, centroids, k);
            double silhouetteScore = calculateSilhouetteScore(data, assignments, k);
            System.out.println("Accuracy: " + silhouetteScore); 

        }
        printClusters(names, assignments, k);
    }

    public static int findOptimalK(double[][] data) {
        int maxClusters = 100;
        double bestSilhouetteScore = Double.NEGATIVE_INFINITY;
        int bestK = 2;

        for (int k = 2; k <= maxClusters; k++) {
            double[][] centroids = getKMeansPPCentroids(data, k);
            int[] assignments = kMeansFit(data, centroids, k);
            double silhouetteScore = calculateSilhouetteScore(data, assignments, k);

            if (silhouetteScore > bestSilhouetteScore) {
                bestSilhouetteScore = silhouetteScore;
                bestK = k;
            }
        }

        System.out.println("Estimated k: " + bestK);
        System.out.println("Accuracy: " + bestSilhouetteScore);
        return bestK;
    }

    public static double calculateSilhouetteScore(double[][] data, int[] assignments, int k) {
        double totalSilhouetteScore = 0.0;
        int numInstances = data.length;

        for (int i = 0; i < numInstances; i++) {
            double[] instance = data[i];
            int clusterIndex = assignments[i];

            double a = calculateAverageDistance(instance, data, assignments, clusterIndex);
            double b = Double.POSITIVE_INFINITY;

            for (int j = 0; j < k; j++) {
                if (j == clusterIndex)
                    continue;
                double dist = calculateAverageDistance(instance, data, assignments, j);
                if (dist < b)
                    b = dist;
            }

            totalSilhouetteScore += (b - a) / Math.max(a, b);
        }

        return totalSilhouetteScore / numInstances;
    }

    public static double calculateAverageDistance(double[] instance, double[][] data, int[] assignments,
            int clusterIndex) {
        double totalDistance = 0.0;
        int count = 0;

        for (int i = 0; i < data.length; i++) {
            if (assignments[i] == clusterIndex) {
                totalDistance += euclideanDistance(instance, data[i]);
                count++;
            }
        }

        return totalDistance / count;
    }

    // K-Means++ centroids initialization
    public static double[][] getKMeansPPCentroids(double[][] data, int k) {
        List<double[]> centroids = new ArrayList<>();
        Random random = new Random();

        // Step 1: Randomly select the first centroid
        int firstIndex = random.nextInt(data.length);
        centroids.add(data[firstIndex]);

        // Step 2: Select the remaining k-1 centroids
        while (centroids.size() < k) {
            double[] distances = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                double minDist = Double.MAX_VALUE;
                for (double[] centroid : centroids) {
                    double dist = euclideanDistance(data[i], centroid);
                    if (dist < minDist) {
                        minDist = dist;
                    }
                }
                distances[i] = minDist;
            }

            int maxDistIndex = 0;
            for (int i = 1; i < distances.length; i++) {
                if (distances[i] > distances[maxDistIndex]) {
                    maxDistIndex = i;
                }
            }
            centroids.add(data[maxDistIndex]);
        }

        return centroids.toArray(new double[0][]);
    }

    public static int[] kMeansFit(double[][] data, double[][] centroids, int k) {
        boolean converged = false;
        int[] assignments = new int[data.length];
        double[][] newCentroids = new double[k][data[0].length];

        while (!converged) {
            // Assignment step: Assign each point to the nearest centroid
            for (int i = 0; i < data.length; i++) {
                double minDist = Double.MAX_VALUE;
                int closestCentroid = -1;
                for (int j = 0; j < centroids.length; j++) {
                    double dist = euclideanDistance(data[i], centroids[j]);
                    if (dist < minDist) {
                        minDist = dist;
                        closestCentroid = j;
                    }
                }
                assignments[i] = closestCentroid;
            }

            // Update step: Recalculate centroids
            double[][] sum = new double[k][data[0].length];
            int[] count = new int[k];

            for (int i = 0; i < data.length; i++) {
                int cluster = assignments[i];
                for (int j = 0; j < data[0].length; j++) {
                    sum[cluster][j] += data[i][j];
                }
                count[cluster]++;
            }

            for (int i = 0; i < k; i++) {
                if (count[i] != 0) {
                    for (int j = 0; j < data[0].length; j++) {
                        newCentroids[i][j] = sum[i][j] / count[i];
                    }
                }
            }

            // Check for convergence
            converged = true;
            for (int i = 0; i < k; i++) {
                if (!Arrays.equals(centroids[i], newCentroids[i])) {
                    converged = false;
                    break;
                }
            }

            // Update centroids
            for (int i = 0; i < k; i++) {
                centroids[i] = Arrays.copyOf(newCentroids[i], newCentroids[i].length);
            }
        }

        return assignments;
    }

    public static double euclideanDistance(double[] point1, double[] point2) {
        double sum = 0.0;
        for (int i = 0; i < point1.length; i++) {
            sum += Math.pow(point1[i] - point2[i], 2);
        }
        return Math.sqrt(sum);
    }

    public static void printClusters(List<String> names, int[] assignments, int k) {
        List<List<String>> clusters = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            clusters.add(new ArrayList<>());
        }

        for (int i = 0; i < assignments.length; i++) {
            clusters.get(assignments[i]).add(names.get(i));
        }

        for (int i = 0; i < k; i++) {
            List<String> cluster = clusters.get(i);
            System.out.println("Cluster #" + (i + 1) + " => " + String.join(" ", cluster));
        }
    }
}
