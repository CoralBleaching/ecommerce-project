import argparse
import base64
import json
import markovify
import numpy as np
import os
import re
import sqlite3
from datetime import datetime, timedelta
from unidecode import unidecode

# user
PROPORTION_OF_ADMINS = 0.1

# price and products
AVERAGE_PRICE = 6 
AVERAGE_SOLD_ITEMS = 3
EVALUATION_FREQUENCY = 0.7
NUMBER_OF_PRODUCTS_WITH_MULTIPLE_PRICES = 10
PROPORTION_SOLD_OUT = 0.1
ROUND_TO = 2
SENTENCES_PER_REVIEW = 7
STANDARD_DEVIATION_PRICE = 0.8
TEXT_EVALUATION_FREQUENCY = 0.6

# paths and defaults
CONFIG_FILE_PATH = 'src/main/resources/config.properties'
DATA_PATH = 'database/mock_data/'
DB_PATH = 'database/ieeecommerce-db.db'
RNG_SEED = 2023
SQL_SCRIPT_PATH = 'database/scripts/create-database.sql'

# date and time
DATE_GAP_IN_DAYS = 150
END_TIME = '23:59:59'
START_TIME = '00:00:00'
TIME_FORMAT = '%H:%M:%S'

def convert_to_camel_case(string: str):
    cleaned_string = unidecode(string)
    cleaned_string = re.sub(r'[^a-zA-Z0-9\s]+', ' ', cleaned_string)
    words = cleaned_string.split()
    words = [words[0].lower()] + [word.title() for word in words[1:]]
    return ''.join(words)


def generate_random_time(
        rng: np.random.Generator | None,
        start_time_str: str = START_TIME, 
        end_time_str: str = END_TIME,
        time_format: str = TIME_FORMAT,
        date_gap: int = DATE_GAP_IN_DAYS):    
    if rng is None:
        rng = np.random.default_rng()

    start_time = datetime.strptime(start_time_str, time_format)
    end_time = datetime.strptime(end_time_str, time_format)

    now = datetime.now().toordinal()
    timestamp = rng.integers(now - date_gap, now)
    generated_date = datetime.fromordinal(timestamp)
    time_delta = end_time - start_time
    generated_time = start_time + timedelta(
        seconds=float(rng.random()*time_delta.total_seconds())
    )
    return datetime(
        generated_date.year, generated_date.month, generated_date.day,
        generated_time.hour, generated_time.minute, generated_time.second
    )


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
                    proportion_of_admins: float = PROPORTION_OF_ADMINS,
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
            countries = np.array(data[country]) # prevents a type checking failure
            address_data: dict[str, str] = rng.choice(countries)
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
        proportion_sold_out: float = PROPORTION_SOLD_OUT,
        start_time: str = START_TIME,
        end_time: str = END_TIME,
        time_format: str = TIME_FORMAT,
        date_gap: int = DATE_GAP_IN_DAYS
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
        (id_subcategory, id_picture, name, description, stock, hotness, timestamp) 
        values (?, ?, ?, ?, ?, ?, ?)'''

        for category_name, category in categories.items():
            cur.execute(category_stmt, (category_name, category['description']))
            id_category = cur.lastrowid

            assert isinstance(category['subcategories'], dict), \
                'Error in categories json.\n'
            for subcat_name, subcat_descr in category['subcategories'].items():
                cur.execute(subcategory_stmt, (id_category, subcat_name, subcat_descr))
                id_subcategory = cur.lastrowid

                for product in products[category_name][subcat_name]:
                    stock = int(rng.integers(1,30)) \
                        if rng.random() < proportion_sold_out else 0
                    cur.execute('''
                        select id_picture from Picture where
                        Picture.name = ?
                    ''', (convert_to_camel_case(product['name']),))
                    id_picture = cur.fetchone()[0]
                    cur.execute(
                        product_stmt,
                        (
                            id_subcategory,
                            id_picture,
                            product['name'],
                            product['description'],
                            stock,
                            int(rng.integers(1, 5, endpoint=True)),
                            str(generate_random_time(
                            rng, start_time, end_time, time_format, date_gap)
                            )
                        )
                    )


def fill_price_table(
        cur: sqlite3.Cursor,
        rng: np.random.Generator | None,
        number_of_products_with_multiple_prices: int = \
             NUMBER_OF_PRODUCTS_WITH_MULTIPLE_PRICES,
        average_price: float = AVERAGE_PRICE,
        standard_deviation_price: float = STANDARD_DEVIATION_PRICE,
        round_to: int = ROUND_TO,
        start_time: str = START_TIME, 
        end_time: str = END_TIME,
        time_format: str = TIME_FORMAT,
        date_gap: int = DATE_GAP_IN_DAYS):

    if rng is None:
        rng = np.random.default_rng()
        
    cur.execute('select id_product from Product')
    id_products: list[int] = list(next(zip(*cur.fetchall())))
    rng.shuffle(id_products)

    stmt = 'insert into Price (id_product, timestamp, value) values (?, ?, ?)'

    for id_product in id_products + \
        id_products[:number_of_products_with_multiple_prices]:
        cur.execute(stmt, (
            id_product,
            str(generate_random_time(rng, start_time, end_time, time_format, date_gap)),
            round(rng.lognormal(average_price, standard_deviation_price), round_to)
        ))

def fill_sale_sold_and_evaluation_tables(
        cur: sqlite3.Cursor,
        rng: np.random.Generator,
        data_path: str = DATA_PATH,
        average_sold_items: int = AVERAGE_SOLD_ITEMS,
        evaluation_frequency: float = EVALUATION_FREQUENCY,
        text_evaluation_frequency: float = TEXT_EVALUATION_FREQUENCY,
        sentences_per_review: int = SENTENCES_PER_REVIEW,
        start_time: str = START_TIME, 
        end_time: str = END_TIME,
        time_format: str = TIME_FORMAT,
        date_gap: int = DATE_GAP_IN_DAYS):
    
    if rng is None:
        rng = np.random.default_rng() 

    cur.execute('select id_user from User')
    user_ids = next(zip(*cur.fetchall()))

    cur.execute('select id_price, id_product from Price')
    price_ids, product_ids = list(zip(*cur.fetchall()))

    for user_id in user_ids:
        timestamp = generate_random_time(
        rng, start_time, end_time, time_format, date_gap)
        cur.execute('insert into Sale (id_user, timestamp) values (?, ?)',
                    (user_id, timestamp))
        sale_id = cur.lastrowid

        with open(data_path + 'review.txt', 'r', encoding='utf-8') as review_file:
            text_model = markovify.Text(review_file.read()).compile()

            number_of_sold_items = rng.binomial(average_sold_items * 2, 0.5)
            price_ids_sold = rng.choice(list(enumerate(price_ids)),number_of_sold_items)
            for index, price_id_sold in price_ids_sold:
                price_id_sold = int(price_id_sold)
                cur.execute('''insert into Sold (id_sale, id_price, quantity)
                values (?, ?, ?)''', (sale_id, price_id_sold, 1))

                if rng.random() < evaluation_frequency:
                    review = ''
                    review_timestamp: datetime | None = None
                    while (review_timestamp is None or review_timestamp <= timestamp):
                        review_timestamp = generate_random_time(
                            rng, start_time, end_time, time_format, date_gap)
                    score = int(rng.integers(3, 5, endpoint=True))
                    if rng.random() < text_evaluation_frequency:
                        for _ in range(sentences_per_review):
                            new_sentence = text_model.make_sentence()
                            if new_sentence is not None:
                                review += new_sentence
                        cur.execute('''insert into Evaluation (id_product, id_sale,
                        timestamp, score, review) values (?, ?, ?, ?, ?)''',
                        (product_ids[index], sale_id, review_timestamp, score, review))
                    else:
                        cur.execute('''insert into Evaluation (id_product, id_sale,
                        timestamp, score) values (?, ?, ?, ?)''',
                        (product_ids[index], sale_id, review_timestamp, score))
            

        
    
def main(dbpath: str, 
         data_path: str, 
         seed: int, 
         proportion_of_admins: float,
         proportion_sold_out: float,
         average_price: float,
         average_sold_items: int,
         evaluation_frequency: float,
         number_of_products_with_multiple_prices: int,
         round_to: int,
         standard_deviation_price: float,
         text_evaluation_frequency: float,
         sentences_per_review: int,
         config_file_path: str,
         sql_script_path: str,
         date_gap_in_days: int,
         end_time: str,
         start_time: str,
         time_format: str):
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
            cur.execute('drop table if exists Sale')
            cur.execute('drop table if exists Sold')
            cur.execute('drop table if exists Evaluation')
            cur.execute('drop table if exists Subcategory')
            cur.execute('drop table if exists Category')
            cur.execute('drop table if exists Product')
            create_tables(cur, sql_script_path)
            user_data = fill_user_table(cur, data_path, proportion_of_admins, rng)
            fill_credit_card_table(cur, user_data, rng)
            fill_country_state_and_city_tables(cur, data_path, rng)
            fill_address_table(cur, data_path, user_data, rng)
            fill_picture_table(cur, data_path, rng)
            fill_category_product_and_has_picture_tables(
                cur, data_path, rng, proportion_sold_out)
            fill_price_table(
                cur, rng, number_of_products_with_multiple_prices,
                average_price, standard_deviation_price, round_to,
                start_time, end_time, time_format, date_gap_in_days)
            fill_sale_sold_and_evaluation_tables(
                cur, rng, data_path, average_sold_items, evaluation_frequency,
                text_evaluation_frequency, sentences_per_review, start_time,
                end_time, time_format, date_gap_in_days
            )

            cur.close()

            with open(config_file_path, 'w', encoding='utf-8') as config_file:
                full_db_path = f'database.path={os.getcwd()}/{dbpath}'\
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
                        default=RNG_SEED, 
                        help='Seed for the RNG.')
    parser.add_argument('--proportion_of_admins',
                        type=float, 
                        default=PROPORTION_OF_ADMINS, 
                        help="Proportion of users that will be administrators.")
    parser.add_argument('--proportion_sold_out',
                        type=float, 
                        default=PROPORTION_SOLD_OUT, 
                        help="Proportion of out of stock products.")
    parser.add_argument('--average_price',
                        type=float,
                        default=AVERAGE_PRICE,
                        help="Average price of the products.")
    parser.add_argument('--average_sold_items',
                        type=int,
                        default=AVERAGE_SOLD_ITEMS,
                        help="Average number of products sold per sale.")
    parser.add_argument('--evaluation_frequency',
                        type=int,
                        default=EVALUATION_FREQUENCY,
                        help="Fraction of product sales where the user makes an " +
                        "evaluation.")
    parser.add_argument('--number_of_products_with_multiple_prices',
                        type=int,
                        default=NUMBER_OF_PRODUCTS_WITH_MULTIPLE_PRICES,
                        help="Number of products with multiple prices.")
    parser.add_argument('--round_to',
                        type=int,
                        default=ROUND_TO,
                        help="Number of decimal places to round to.")
    parser.add_argument('--standard_deviation_price',
                        type=float,
                        default=STANDARD_DEVIATION_PRICE,
                        help="Standard deviation of product prices.")
    parser.add_argument('--text_evaluation_frequency',
                        type=int,
                        default=TEXT_EVALUATION_FREQUENCY,
                        help="Fraction of evaluations where the user makes a review.")
    parser.add_argument('--sentences_per_review',
                        type=int,
                        default=SENTENCES_PER_REVIEW,
                        help="Number of sentences generated for each product review.")
    parser.add_argument('--config_file_path',
                        type=str,
                        default=CONFIG_FILE_PATH,
                        help='The path to the config file.')
    parser.add_argument('--sql_script_path',
                        type=str,
                        default=SQL_SCRIPT_PATH,
                        help='The path to the SQL script file.')
    parser.add_argument('--date_gap_in_days',
                        type=int,
                        default=DATE_GAP_IN_DAYS,
                        help='The gap in days for date manipulation.')
    parser.add_argument('--end_time',
                        type=str,
                        default=END_TIME,
                        help='The end time of the day.')
    parser.add_argument('--start_time',
                        type=str,
                        default=START_TIME,
                        help='The start time of the day.')
    parser.add_argument('--time_format',
                        type=str,
                        default=TIME_FORMAT,
                        help='The format of the time.')

    args = parser.parse_args()
    main(dbpath=args.dbpath, 
        data_path=args.data_path, 
        seed=args.seed, 
        proportion_of_admins=args.proportion_of_admins,
        proportion_sold_out=args.proportion_sold_out,
        average_price=args.average_price,
        average_sold_items=args.average_sold_items,
        evaluation_frequency=args.evaluation_frequency,
        number_of_products_with_multiple_prices=args.number_of_products_with_multiple_prices,
        round_to=args.round_to,
        standard_deviation_price=args.standard_deviation_price,
        text_evaluation_frequency=args.text_evaluation_frequency,
        sentences_per_review=args.sentences_per_review,
        config_file_path=args.config_file_path,
        sql_script_path=args.sql_script_path,
        date_gap_in_days=args.date_gap_in_days,
        end_time=args.end_time,
        start_time=args.start_time,
        time_format=args.time_format)