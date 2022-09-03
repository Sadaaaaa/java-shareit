drop table IF EXISTS items;
drop table IF EXISTS users;
drop table IF EXISTS bookings;
drop table IF EXISTS comments;
drop table IF EXISTS requests;

create table IF NOT EXISTS Items
(
    item_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    item_name
    VARCHAR
(
    50
) NOT NULL,
    item_description VARCHAR
(
    255
),
    is_available BOOLEAN DEFAULT FALSE,
    owner_id INTEGER,
    request_id INTEGER,
    constraint Items_PK
    primary key
(
    item_id
)
    );

CREATE TABLE IF NOT EXISTS Users
(
    user_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    user_name
    varchar
(
    50
),
    user_email varchar
(
    50
) not null,
    UNIQUE
(
    user_email
),
    constraint USERS_PK
    primary key
(
    user_id
)
    );

CREATE TABLE IF NOT EXISTS Bookings
(
    bookings_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    bookings_start_date
    TIMESTAMP
    WITHOUT
    TIME
    ZONE,
    bookings_end_date
    TIMESTAMP
    WITHOUT
    TIME
    ZONE,
    bookings_item_id
    INTEGER,
    booker_id
    INTEGER,
    bookings_status
    varchar
(
    50
),
    constraint Bookings_PK
    primary key
(
    bookings_id
)
    );

CREATE TABLE IF NOT EXISTS Requests
(
    request_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    description
    varchar
(
    255
),
    requestor_id INTEGER,
    created TIMESTAMP WITHOUT TIME ZONE,
    constraint Requests_PK
    primary key
(
    request_id
)
    );

CREATE TABLE IF NOT EXISTS Comments
(
    comments_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    comments_text
    varchar
(
    255
),
    item_id INTEGER,
    author_id INTEGER,
    created TIMESTAMP WITHOUT TIME ZONE,
    constraint comments_PK
    primary key
(
    comments_id
)
    );