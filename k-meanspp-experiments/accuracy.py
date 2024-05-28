import csv
from collections import defaultdict

def read_clusters(file_path):
    clusters = defaultdict(list)
    with open(file_path, 'r') as file:
        reader = csv.reader(file)
        next(reader)  # Skip header
        for row in reader:
            data_point = row[0]
            cluster = int(row[1])
            clusters[cluster].append(data_point)
    return clusters

def match_clusters(ground_truth, results):
    matches = {}
    used = set()

    for gt_cluster, gt_points in ground_truth.items():
        best_match = None
        max_common = -1

        for result_cluster, result_points in results.items():
            if result_cluster in used:
                continue
            common_points = set(gt_points).intersection(result_points)
            if len(common_points) > max_common:
                max_common = len(common_points)
                best_match = result_cluster

        matches[gt_cluster] = best_match
        used.add(best_match)

    return matches

def calculate_accuracy(ground_truth, results, matches):
    common_points = 0
    total_points = sum(len(points) for points in ground_truth.values())

    for gt_cluster, gt_points in ground_truth.items():
        result_cluster = matches[gt_cluster]
        common_points += len(set(gt_points).intersection(results[result_cluster]))

    return common_points / total_points

def main():
    expected_file = 'expected.csv'
    result_file = 'result.csv'

    ground_truth_clusters = read_clusters(expected_file)
    result_clusters = read_clusters(result_file)

    matches = match_clusters(ground_truth_clusters, result_clusters)
    accuracy = calculate_accuracy(ground_truth_clusters, result_clusters, matches)

    print(f'Accuracy: {accuracy*100:.2f}')

if __name__ == "__main__":
    main()
