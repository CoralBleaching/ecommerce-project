import argparse
import base64
import json
import numpy as np
import os
import re
import sqlite3
from datetime import datetime, timedelta
from typing import cast
from unidecode import unidecode

CONFIG_FILE_PATH = 'src/main/resources/config.properties'
DATA_PATH = 'database/mock_data/'
DB_PATH = 'database/ieeecommerce-db.db'
SQL_SCRIPT_PATH = 'database/scripts/create-database.sql'

def convert_to_camel_case(string: str):
    cleaned_string = unidecode(string)
    cleaned_string = re.sub(r'[^a-zA-Z0-9\s]+', ' ', cleaned_string)
    words = cleaned_string.split()
    words = [words[0].lower()] + [word.title() for word in words[1:]]
    return ''.join(words)

def replace_special_characters_with_codes(path: str):
    return ''.join(
        '\\u{:04x}'.format(ord(char)) if ord(char) >= 128 else char
        for char in path
    )

def create_tables(
        cur: sqlite3.Cursor,
        sql_file: str = SQL_SCRIPT_PATH):
    with open(sql_file) as sql_script:
        cur.executescript(sql_script.read())


def fill_user_table(cur: sqlite3.Cursor,
                    data_path: str,
                    proportion_of_admins: float,
                    rng: np.random.Generator | None = None,):
    if rng is None:
        rng = np.random.default_rng()

    with open(data_path + "emails.txt") as emails_file, \
        open(data_path + "names.txt") as names_file, \
        open(data_path + "usernames.txt") as usernames_file:

        data: list[dict[str, str]] = []
        chars = list(filter(str.isalnum, (map(chr, range(ord('0'), ord('z') + 1)))))
        while True:
            data.append({
                'name': names_file.readline().rstrip(),
                'username': usernames_file.readline().rstrip(),
                'email': emails_file.readline().rstrip()
            })
            if data[-1]['name'] == '':
                data.pop()
                break
            data[-1].update({
                'password': 'a1' + ''.join(rng.choice(chars, rng.integers(9, 15))),
                'is_admin': str(proportion_of_admins > rng.random())
            })
    
            stmt = '''
            insert into User (name, username, email, password, is_admin)
            values (:name , :username, :email, :password, :is_admin)
            '''

            cur.execute(stmt, data[-1])
            data[-1].update({'id_user': str(cur.lastrowid)})

        return data
        

def fill_credit_card_table(cur: sqlite3.Cursor,
                          users: list[dict[str, str]],
                          rng: np.random.Generator | None = None):
    if rng is None:
        rng = np.random.default_rng()

    for user in users:
        n = int(rng.integers(3))
        for _ in range(n):
            data = ({'id_user': user['id_user'],
                        'name_on_card': user['name']})
            data.update({'number': ''.join(map(str, rng.integers(1,9+1,16))),
                         'ccv': ''.join(map(str, rng.integers(1,9+1,3))),
                         'expiry_date': str(rng.integers(1,13)).zfill(2) \
                            + str(rng.integers(28, 48))
            })
            stmt = '''
            insert into CreditCard (id_user, number, name_on_card, ccv, expiry_date)
            values (:id_user, :number, :name_on_card, :ccv, :expiry_date)
            '''

            cur.execute(stmt, data)

def fill_country_state_and_city_tables(cur: sqlite3.Cursor,
                                       data_path: str,
                                       rng: np.random.Generator | None = None):
    if rng is None:
        rng = np.random.default_rng()

    countries = ('Brazil', 'Argentina', 'Mexico')

    for country in countries:
        stmt = 'insert into Country (name) values (?)'
        cur.execute(stmt, (country,))
        id_country = cur.lastrowid
        assert id_country, 'Insertion error'

        with open(data_path + f'{country}.json', encoding='utf-8') as raw_data:
            data: dict[str, list[str]] = json.loads(raw_data.read())
            for state in data:
                stmt = '''
                insert into State (id_country, name) values
                (?, ?)
                '''

                cur.execute(stmt, (id_country, state))
                id_state = cur.lastrowid
                assert id_state, 'Insertion error'

                for city in data[state]:
                    stmt = '''
                    insert into City (id_state, name) values
                    (?, ?)
                    '''
                    cur.execute(stmt, (id_state, city))


def fill_address_table(cur: sqlite3.Cursor,
                       data_path: str,
                       user_data: list[dict[str, str]],
                       rng: np.random.Generator | None = None):
    if rng is None:
        rng = np.random.default_rng()
    
    with open(data_path + 'streets.json', encoding='utf-8') as raw_data:
        data: dict[str, list[dict[str, str]]] = json.loads(raw_data.read())
        id_cities: dict[str, tuple[int, ...]] = {
            'Brazil': tuple(), 'Argentina': tuple(), 'Mexico': tuple()
            }
        for country in id_cities:
            cur.execute('''
                select id_city from city, state, country where
                country.name = ? and
                State.id_country = country.id_country and
                city.id_state = state.id_state
            ''', (country,))
            id_cities[country] = next(zip(*cur.fetchall()))
        
        for user in user_data:
            stmt = '''
            insert into Address (
                id_user, id_city, street, number, zipcode, district
            ) values (?, ?, ?, ?, ?, ?)
            '''
            country = rng.choice(list(id_cities.keys()))
            address_data = cast(dict[str, str], rng.choice(data[country]))
            cur.execute(stmt, (
                user['id_user'], 
                str(rng.choice(id_cities[country])),
                address_data['street'], 
                address_data['number'], 
                ''.join(map(str, rng.integers(1,9+1,8))), 
                address_data['district']
                )
            )


def fill_picture_table(
        cur: sqlite3.Cursor,
        data_path: str, 
        rng: np.random.Generator | None
        ):
    if rng is None:
        rng = np.random.default_rng()

    stmt = 'insert into Picture (name, data) values (?, ?)'
    
    file_list = os.listdir(data_path + 'pictures/')

    for file_name in file_list:
        with open(f'{data_path}pictures/{file_name}', 'rb') as img_bytes_file:
            encoded_img_bytes = base64.b64encode(img_bytes_file.read()).decode('utf-8')
            picture_name = os.path.splitext(file_name)[0]
            cur.execute(stmt, (picture_name, encoded_img_bytes))


def fill_category_product_and_has_picture_tables(
        cur: sqlite3.Cursor,
        data_path: str,
        rng: np.random.Generator | None,
        proportion_sold_out: float = 0.1
    ):
    if rng is None:
        rng = np.random.default_rng()
    
    with open(data_path + 'categories.json', encoding='utf-8') as raw_category_data, \
        open(data_path + 'products.json', encoding='utf-8') as raw_product_data:
        categories: dict[str, dict[str, str | dict[str, str]]] = \
            json.loads(raw_category_data.read())
        products: dict[str, dict[str, list[dict[str,str]]]] = \
            json.loads(raw_product_data.read())
        
        category_stmt = 'insert into Category (name, description) values (?, ?)'
        subcategory_stmt = '''insert into Subcategory 
            (id_category, name, description) values (?, ?, ?)'''
        product_stmt = '''insert into Product 
        (id_subcategory, id_picture, name, description, stock) values (?, ?, ?, ?, ?)'''

        for category_name, category in categories.items():
            cur.execute(category_stmt, (category_name, category['description']))
            id_category = cur.lastrowid

            assert isinstance(category['subcategories'], dict), \
                'Error in categories json.\n'
            for subcat_name, subcat_descr in category['subcategories'].items():
                cur.execute(subcategory_stmt, (id_category, subcat_name, subcat_descr))
                id_subcategory = cur.lastrowid

                for product in products[category_name][subcat_name]:
                    stock = rng.integers(1,30) if rng.random() < proportion_sold_out \
                        else 0                    
                    cur.execute('''
                        select id_picture from Picture where
                        Picture.name = ?
                    ''', (convert_to_camel_case(product['name']),))
                    id_picture = cur.fetchone()[0]
                    cur.execute(product_stmt,(id_subcategory,
                                              id_picture,
                                              product['name'],
                                              product['description'],
                                              str(stock)))


def fill_price_table(cur: sqlite3.Cursor,
                     rng: np.random.Generator | None):
    FIVE_MONTHS = 150
    NUMBER_OF_PRODUCTS_WITH_MULTIPLE_PRICES = 10
    AVERAGE_PRICE = 6
    STANDARD_DEVIATION_PRICE = 0.8
    ROUND_TO = 2

    if rng is None:
        rng = np.random.default_rng()
        
    cur.execute('select id_product from Product')
    id_products: list[int] = list(next(zip(*cur.fetchall())))
    rng.shuffle(id_products)

    stmt = 'insert into Price (id_product, timestamp, value) values (?, ?, ?)'
    now = datetime.now().toordinal()
    start_time = datetime.strptime('00:00:00', '%H:%M:%S')
    end_time = datetime.strptime('23:59:59', '%H:%M:%S')

    for id_product in id_products + \
        id_products[:NUMBER_OF_PRODUCTS_WITH_MULTIPLE_PRICES]:
        timestamp = rng.integers(now - FIVE_MONTHS, now)
        date = datetime.fromordinal(timestamp)
        time_delta = end_time - start_time
        random_time = start_time + timedelta (
            seconds=float(rng.random()*time_delta.total_seconds())
        )
        datetime_with_time = datetime (
            date.year, date.month, date.day, random_time.hour, 
            random_time.minute, random_time.second
        )
    
        cur.execute(stmt, (
            id_product,
            str(datetime_with_time),
            round(rng.lognormal(AVERAGE_PRICE, STANDARD_DEVIATION_PRICE), ROUND_TO)
        ))


def main(dbpath: str, 
         data_path: str, 
         seed: int, 
         proportion_of_admins: float,
         proportion_sold_out: float):
    try:
        rng = np.random.default_rng(seed)
        with sqlite3.connect(dbpath) as conn:
            cur = conn.cursor()
            cur.execute('drop table if exists CreditCard')
            cur.execute('drop table if exists Address')
            cur.execute('drop table if exists User')
            cur.execute('drop table if exists City')
            cur.execute('drop table if exists State')
            cur.execute('drop table if exists Country')
            cur.execute('drop table if exists Picture')
            cur.execute('drop table if exists Price')
            cur.execute('drop table if exists Subcategory')
            cur.execute('drop table if exists Category')
            cur.execute('drop table if exists Product')
            create_tables(cur)
            user_data = fill_user_table(cur, data_path, proportion_of_admins, rng)
            fill_credit_card_table(cur, user_data, rng)
            fill_country_state_and_city_tables(cur, data_path, rng)
            fill_address_table(cur, data_path, user_data, rng)
            fill_picture_table(cur, data_path, rng)
            fill_category_product_and_has_picture_tables(
                cur, data_path, rng, proportion_sold_out)
            fill_price_table(cur, rng)

            with open(CONFIG_FILE_PATH, 'w', encoding='utf-8') as config_file:
                full_db_path = f'database.path={os.getcwd()}/{DB_PATH}'\
                    .replace('\\', '/')
                config_file.write(replace_special_characters_with_codes(full_db_path))
    except sqlite3.Error as exc:
        print(exc)
    except IOError as exc:
        print(exc)
        print(data_path)

if __name__ == '__main__':
    cwd = os.getcwd()
    parser = argparse.ArgumentParser()
    parser.add_argument('--dbpath', 
                        type=str, 
                        default=DB_PATH,
                        help='The path to the SQLite3 database file.')
    parser.add_argument('--data_path', 
                        type=str, 
                        default=cwd + '/' + DATA_PATH,
                        help='Path to the mock data directory.')
    parser.add_argument('--seed', 
                        type=int, 
                        default=2023, 
                        help='Seed for the RNG.')
    parser.add_argument('--proportion_of_admins',
                        type=float, 
                        default=0.1, 
                        help="Proportion of users that will be administrators.")
    parser.add_argument('--proportion_sold_out',
                        type=float, 
                        default=0.1, 
                        help="Proportion of out of stock products.")

    args = parser.parse_args()
    main(args.dbpath, 
         args.data_path, 
         args.seed, 
         args.proportion_of_admins,
         args.proportion_sold_out)