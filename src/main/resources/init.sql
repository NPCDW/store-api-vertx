create table goods
(
  id          INTEGER PRIMARY KEY NOT NULL,
  create_time TIMESTAMP default CURRENT_TIMESTAMP NOT NULL,
  update_time TIMESTAMP default CURRENT_TIMESTAMP NOT NULL on update CURRENT_TIMESTAMP,
  qrcode      varchar(100) NOT NULL UNIQUE,
  name        varchar(100),
  cover       varchar(100),
  price       decimal(8, 2)
);
