import base64
import json
import os
from datetime import datetime

import psycopg


class DateTimeEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, datetime):
            return obj.isoformat()
        return json.JSONEncoder.default(self, obj)


def fetch_data(query, cursor):
    cursor.execute(query)
    columns = [desc[0] for desc in cursor.description]
    data = cursor.fetchall()
    result = [dict(zip(columns, row)) for row in data]
    return result


def save_to_json(data, file_path):
    with open(file_path, 'w') as f:
        json.dump(data, f, indent=4, cls=DateTimeEncoder)


def main():
    # Database connection parameters
    db_params = {
        'dbname': 'ecommerce-db',
        'user': 'admin',
        'password': 'admin',
        'host': 'localhost',
        'port': 5432
    }

    # Queries to fetch data
    queries = {
        'Product': 'SELECT * FROM Product',
        'Category': 'SELECT * FROM Category',
        'Subcategory': 'SELECT * FROM Subcategory',
        'Price': 'SELECT * FROM Price',
        'LatestPrice': '''
            SELECT id_product, MAX(timestamp) as max_timestamp
            FROM Price
            GROUP BY id_product
        ''',
        'City': 'SELECT * FROM City',
        'State': 'SELECT * FROM State',
        'Country': 'SELECT * FROM Country',
        'Sale': 'SELECT * FROM Sale',
        'Sold': 'SELECT * FROM Sold',
        'Evaluation': 'SELECT * FROM Evaluation',
    }

    # Directory to save JSON files
    output_dir = 'database/json'

    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    try:
        # Connect to the database
        conn = psycopg.connect(**db_params)
        cur = conn.cursor()

        for table_name, query in queries.items():
            data = fetch_data(query, cur)
            file_path = os.path.join(output_dir, f'{table_name}.json')
            save_to_json(data, file_path)
            print(f'Saved {table_name} data to {file_path}')

        cur.execute("SELECT id_picture, data FROM Picture")
        pictures = cur.fetchall()

        picture_list = []
        for picture in pictures:
            id_picture = picture[0]
            data = base64.b64encode(picture[1]).decode('utf-8')
            picture_list.append({
                "id_picture": id_picture,
                "data": data
            })
        save_to_json(picture_list, os.path.join(output_dir, 'Picture.json'))

    except Exception as e:
        print(f'Error: {e}')

    finally:
        cur.close()
        conn.close()


if __name__ == '__main__':
    main()
