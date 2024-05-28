import csv

def filter_columns(input_file, output_file, columns_to_keep):
    with open(input_file, 'r') as infile, open(output_file, 'w', newline='') as outfile:
        reader = csv.reader(infile)
        writer = csv.writer(outfile)

        for row in reader:
            # Select only the columns we want to keep
            filtered_row = [row[i] for i in columns_to_keep]
            writer.writerow(filtered_row)

if __name__ == "__main__":
    input_file = '../artd-31.csv'
    output_file = 'expected.csv'
    columns_to_keep = [0, 3]  # Indices of the columns to keep (1st and 4th columns)

    filter_columns(input_file, output_file, columns_to_keep)
