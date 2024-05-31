import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class A2_G1_t2 {

    public static class Point {
        String id;
        double x, y;
        int cluster;

        Point(String id, double x, double y) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.cluster = -1; // -1 indicates the point is not yet assigned to any cluster
        }
    }

    public static List<Point> readCSV(String filePath) throws IOException {
        List<Point> points = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String id = values[0];
                double x = Double.parseDouble(values[1]);
                double y = Double.parseDouble(values[2]);
                points.add(new Point(id, x, y));
            }
        }
        return points;
    }

    public static void main(String[] args) {
        if (args.length < 2 || args.length > 3) {
            System.err.println("Usage: java A2_G1_t2 <file_path> <mu> [eps]");
            System.exit(1);
        }

        String filePath = args[0];
        Integer minPts = null;
        Double eps = null;

        try {
            if (args.length == 3) {
                minPts = Integer.parseInt(args[1]);
                eps = Double.parseDouble(args[2]);
            } else {
                if (args[1].contains(".")) {
                    eps = Double.parseDouble(args[1]);
                } else {
                    minPts = Integer.parseInt(args[1]);
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Error: mu should be an integer and eps should be a floating-point number.");
            System.exit(1);
        }

        try {
            List<Point> points = readCSV(filePath);

            if (minPts == null) {
                minPts = estimateMinPts();
            }
            if (eps == null) {
                eps = estimateEps(points, minPts);
            }

            DBSCAN dbscan = new DBSCAN(points, minPts, eps);
            dbscan.run();
            dbscan.printResults();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int estimateMinPts() {
        // A common heuristic for minPts is to set it to 2 times the dimensionality of the data
        return 2 * 2; // Assuming 2D data
    }

    private static double estimateEps(List<Point> points, int minPts) {
        List<Double> distances = new ArrayList<>();
        for (Point point : points) {
            List<Double> neighborDistances = new ArrayList<>();
            for (Point other : points) {
                if (!point.equals(other)) {
                    neighborDistances.add(distance(point, other));
                }
            }
            Collections.sort(neighborDistances);
            distances.add(neighborDistances.get(minPts - 1));
        }
        Collections.sort(distances);
        return distances.get((int) (distances.size() * 0.95)); // Using the 95th percentile distance
    }

    private static double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    public static class DBSCAN {
        private List<Point> points;
        private int minPts;
        private double eps;
        private int clusterId = 0;

        public DBSCAN(List<Point> points, int minPts, double eps) {
            this.points = points;
            this.minPts = minPts;
            this.eps = eps;
        }

        public void run() {
            for (Point point : points) {
                if (point.cluster == -1) { // not yet visited
                    List<Point> neighbors = getNeighbors(point);
                    if (neighbors.size() >= minPts) {
                        clusterId++;
                        expandCluster(point, neighbors);
                    } else {
                        point.cluster = 0; // mark as noise
                    }
                }
            }
        }

        private void expandCluster(Point point, List<Point> neighbors) {
            point.cluster = clusterId;
            List<Point> seeds = new ArrayList<>(neighbors);

            for (int i = 0; i < seeds.size(); i++) {
                Point current = seeds.get(i);
                if (current.cluster == 0) {
                    current.cluster = clusterId; // change noise to border point
                }
                if (current.cluster == -1) {
                    current.cluster = clusterId;
                    List<Point> currentNeighbors = getNeighbors(current);
                    if (currentNeighbors.size() >= minPts) {
                        seeds.addAll(currentNeighbors);
                    }
                }
            }
        }

        private List<Point> getNeighbors(Point point) {
            List<Point> neighbors = new ArrayList<>();
            for (Point p : points) {
                if (distance(point, p) <= eps) {
                    neighbors.add(p);
                }
            }
            return neighbors;
        }

        private double distance(Point p1, Point p2) {
            return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
        }

        public void printResults() {
            int noiseCount = 0;
            for (Point point : points) {
                if (point.cluster == 0) {
                    noiseCount++;
                }
            }
            System.out.println("Number of clusters : " + clusterId);
            System.out.println("Number of noise : " + noiseCount);

            for (int i = 1; i <= clusterId; i++) {
                System.out.print("Cluster #" + i + " => ");
                for (Point point : points) {
                    if (point.cluster == i) {
                        System.out.print(point.id + " ");
                    }
                }
                System.out.println();
            }
        }
    }
}
