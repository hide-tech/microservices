create table orders(
    id bigserial primary key,
    user_id bigint not null,
    payment boolean default false
);

create table products(
    id bigserial primary key,
    name varchar(50),
    model varchar(50),
    description varchar(1500),
    photo_uri varchar(255),
    price decimal(10,2),
    qty bigint
);

create table order_product(
    order_id bigint not null,
    product_id bigint not null,
    constraint order_product_fk1 foreign key (order_id) references orders(id),
    constraint order_product_fk2 foreign key (product_id) references products(id)
);

create index user_index on orders(user_id);