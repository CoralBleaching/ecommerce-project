create table if not exists User (
    id_user integer primary key,
    name varchar(255) not null,
    username varchar(50) not null constraint username check (
        username not glob '*[^a-zA-Z0-9 ]*'
    ), -- Alphanumeric or space, localized
    password varchar(50) not null constraint password check (
        length(password) >= 8 
        and password glob '*[a-zA-Z]*' -- At least 1 letter
        and password glob '*[0-9]*' -- At least 1 number
    ),
    email varchar(50) not null constraint email check (
        email like '%@%.%'
    ),
    is_admin boolean not null default false
);

create table if not exists CreditCard (
    id_user integer not null,
    number varchar(16) not null,
    name_on_card varchar(50) not null,
    ccv varchar(3) not null,
    expiry_date varchar(4) not null,
    foreign key (id_user) references User (id_user)
);

create table if not exists Address (
    id_user integer not null,
    id_city integer not null,
    street varchar(50) not null,
    number varchar(50),
    zipcode varchar(50) not null,
    district varchar(50),
    foreign key (id_user) references User (id_user),
    foreign key (id_city) references City (id_city)
);

create table if not exists City (
    id_city integer primary key,
    id_state integer not null,
    name varchar(50) not null,
    foreign key (id_state) references State (id_state)
);

create table if not exists State (
    id_state integer primary key,
    id_country integer not null,
    name varchar(50) not null,
    foreign key (id_country) references Country (id_country)
);

create table if not exists Country (
    id_country integer primary key,
    name varchar(50) unique not null
);

create table if not exists Sale (
    id_sale integer primary key,
    id_user integer not null,
    timestamp datetime not null,
    foreign key (id_user) references User (id_user)
);

create table if not exists Sold (
    id_sale integer not null,
    id_price integer not null,
    quantity integer not null,
    foreign key (id_sale) references Sale (id_sale),
    foreign key (id_price) references Price (id_price)
);

create table if not exists Product (
    id_product integer primary key,
    id_subcategory integer not null,
    id_picture integer not null,
    name varchar(255) not null,
    description text not null,
    stock integer not null,
    foreign key (id_subcategory) references Subcategory (id_subcategory)
);

create table if not exists Subcategory (
    id_subcategory integer primary key,
    id_category integer not null,
    name varchar(50) not null,
    description varchar(255),
    foreign key (id_category) references Category (id_category)
);

create table if not exists Category (
    id_category integer primary key,
    name varchar(50) not null,
    description varchar(255)
);

create table if not exists Price (
    id_price integer primary key,
    id_product integer not null,
    timestamp datetime not null default current_timestamp,
    value float not null,
    foreign key (id_product) references Product (id_product)
);

create table if not exists Picture (
    id_picture integer primary key,
    name varchar(255) not null unique,
    data blob not null
);