create table goods
(
  id          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  qrcode      varchar(100) NOT NULL UNIQUE,
  name        varchar(100),
  cover       varchar(100),
  price       decimal(8, 2)
)
