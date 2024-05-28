import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import sys

# Load data from CSV
column_names = ['point', 'x', 'y', 'cluster']
data = pd.read_csv('artset1.csv', names=column_names)
# data = pd.read_csv('artd-31.csv', names=column_names)
# data = pd.read_csv('test.csv', names=column_names)


# Extract coordinates
coordinates = data[['x', 'y']].values

def plot(data, centroids, title):
    plt.scatter(data[:, 0], data[:, 1], marker='.', color='gray', label='data points')
    plt.scatter(centroids[:, 0], centroids[:, 1], color='red', label='selected centroids')
    # plt.scatter(centroids[-1, 0], centroids[-1, 1], color='red', label='next centroid')
    plt.title(title)
    
    plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left')

    padding = 10000
    plt.xlim(min(data[:, 0]) - padding, max(data[:, 0]) + padding)
    plt.ylim(min(data[:, 1]) - padding, max(data[:, 1]) + padding)
   
    # print(min(data[:, 0]), max(data[:, 0]))
    # print(min(data[:, 1]), max(data[:, 1]))

    plt.show()

def distance(p1, p2):
    return np.sqrt(np.sum((p1 - p2)**2))

# Init for k-means++ 
def initialize(data, k):
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
    # print(np.array(centroids))
    plot(data, np.array(centroids), 'k-means++ Initialization')
    return centroids


#Init for k-means 
def initialize_random(data, k):
    centroids = data[np.random.choice(data.shape[0], k, replace=False)]
    # print(np.array(centroids))
    plot(data, np.array(centroids),'k-means Initialization')
    return centroids

#Call init functions for k-means and k-means++ 
centroids_random = initialize_random(coordinates, k=15)
centroids = initialize(coordinates, k=15)
