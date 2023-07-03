DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS member_shop;
DROP TABLE IF EXISTS kiosk;
DROP TABLE IF EXISTS shop;
DROP TABLE IF EXISTS member;

create table member
(
    member_id     bigint auto_increment
        primary key,
    email         varchar(30)                               not null,
    password      varchar(100)                              null,
    nickname      varchar(15)                               not null,
    image_url     varchar(255)                              null,
    phone_number  char(11)                                  null,
    role          enum ('GUEST', 'USER', 'SELLER', 'ADMIN') not null,
    social_type   enum ('KAKAO', 'NAVER', 'GOOGLE')         null,
    social_id     varchar(50)                               null,
    refresh_token varchar(255)                              null,
    constraint member_email_uk
        unique (email),
    constraint member_nickname_uk
        unique (nickname)
);

create table shop
(
    shop_id   bigint auto_increment
        primary key,
    shop_code varchar(36)   not null,
    name      varchar(50)   not null,
    latitude  double        not null,
    longitude double        not null,
    rating    decimal(2, 1) null,
    constraint shop_shop_code_uk
        unique (shop_code)
);

create table kiosk
(
    kiosk_id              bigint auto_increment
        primary key,
    kiosk_code            varchar(36)     not null,
    installation_location varchar(50)     null,
    installation_year     date            null,
    installation_status   enum ('Y', 'N') not null,
    shop_id               bigint          null,
    constraint kiosk_kiosk_code_uk
        unique (kiosk_code),
    constraint kiosk_shop_id_fk
        foreign key (shop_id) references shop (shop_id)
);

create table member_shop
(
    member_id bigint not null,
    shop_id   bigint not null,
    primary key (member_id, shop_id),
    constraint member_shop_member_member_id_fk
        foreign key (member_id) references member (member_id),
    constraint member_shop_shop_shop_id_fk
        foreign key (shop_id) references shop (shop_id)
);

create table reservation
(
    reservation_id   bigint auto_increment
        primary key,
    reservation_code varchar(36)     not null,
    start_date_time  datetime        not null,
    end_date_time    datetime        not null,
    arrival_status   enum ('Y', 'N') not null,
    member_id        bigint          not null,
    shop_id          bigint          not null,
    constraint reservation_reservation_code_uk
        unique (reservation_code),
    constraint reservation_member_id_fk
        foreign key (member_id) references member (member_id),
    constraint reservation_shop_id_fk
        foreign key (shop_id) references shop (shop_id)
);

create table review
(
    review_id      bigint auto_increment
        primary key,
    review_code    varchar(36)  not null,
    rating         int          not null,
    content        text         not null,
    image_url      varchar(255) null,
    member_id      bigint       not null,
    shop_id        bigint       not null,
    reservation_id bigint       not null,
    constraint review_review_code_uk
        unique (review_code),
    constraint review_member_id_fk
        foreign key (member_id) references member (member_id),
    constraint review_reservation_id_fk
        foreign key (reservation_id) references reservation (reservation_id),
    constraint review_shop_id_fk
        foreign key (shop_id) references shop (shop_id)
);