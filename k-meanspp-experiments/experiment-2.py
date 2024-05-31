import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import sys

column_names = ['point', 'x', 'y', 'cluster']
data = pd.read_csv('artset1.csv', names=column_names)

grouped_data = data.groupby('cluster')

cluster_data_list = []

for _, group_data in grouped_data:
    cluster_data_list.append(group_data[['x', 'y']].values)

cluster_data_array = cluster_data_list #3np.array(cluster_data_list)

coordinates = data[['x', 'y']].values


def plot(ax, centroids, title):
    num_clusters = len(cluster_data_array)
    colors = plt.cm.rainbow(np.linspace(0, 1, num_clusters))
    
    for i in range(num_clusters):
        cluster_data = cluster_data_array[i]
        ax.scatter(cluster_data[:, 0], cluster_data[:, 1], marker='.', color=colors[i], label=f'Cluster {i}')
    
    ax.scatter(centroids[:, 0], centroids[:, 1], color='black', marker='x', label='Centroids')
    ax.set_title(title)
    ax.legend(bbox_to_anchor=(1.05, 1), loc='upper left')

    all_points = np.concatenate(cluster_data_array, axis=0)
    padding = 10000
    ax.set_xlim(all_points[:, 0].min() - padding, all_points[:, 0].max() + padding)
    ax.set_ylim(all_points[:, 1].min() - padding, all_points[:, 1].max() + padding)


def initialize(data, k, ax):
    centroids = []
    centroids.append(data[np.random.randint(data.shape[0]), :])

    for c_id in range(k - 1):
        dist = []
        for i in range(data.shape[0]):
            point = data[i, :]
            d = sys.maxsize
            for j in range(len(centroids)):
                temp_dist = distance(point, centroids[j])
                d = min(d, temp_dist)
            dist.append(d)

        dist = np.array(dist)
        next_centroid = data[np.argmax(dist), :]
        centroids.append(next_centroid)
        dist = []

    plot(ax, np.array(centroids), 'k-means++ Initialization')
    return centroids

def initialize_random(data, k, ax):
    centroids = data[np.random.choice(data.shape[0], k, replace=False)]
    plot(ax, np.array(centroids),'k-means Initialization')
    return centroids

def distance(p1, p2):
    return np.sqrt(np.sum((p1 - p2)**2))

fig, axs = plt.subplots(2, 1, figsize=(8, 12))
centroids_random = initialize_random(coordinates, k=15, ax=axs[0])
centroids = initialize(coordinates, k=15, ax=axs[1])
plt.tight_layout()
plt.show()
