# --- !Ups
CREATE TABLE items (
    id bigserial PRIMARY KEY,
    name varchar(255) NOT NULL,
    price double precision NOT NULL
);

# --- !Downs
DROP TABLE items;
