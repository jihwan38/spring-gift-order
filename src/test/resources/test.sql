DELETE FROM option;
DELETE FROM wishlist;
DELETE FROM member;
DELETE FROM product;

ALTER TABLE product ALTER COLUMN id RESTART WITH 1;
ALTER TABLE member ALTER COLUMN id RESTART WITH 1;
ALTER TABLE wishlist ALTER COLUMN id RESTART WITH 1;
ALTER TABLE option ALTER COLUMN id RESTART WITH 1;

INSERT INTO product (name, price, image_url, is_kakao_approved_by_md)
VALUES
    ('예제상품1', 10000, 'http://image1.url', false),
    ('예제상품2', 20000, 'http://image2.url', false),
    ('예제상품3', 30000, 'http://image3.url', true),
    ('예제상품4', 40000, 'http://image4.url', false);

INSERT INTO member (email, salt, password, role) VALUES ('admin@daum.net', '3dNddJreO8FFohd3PMqS6w==', 'KmmwafNvA+/YYmnXi33Vf4Xa26uyr9dNajVhugCrkp0=', 'ADMIN');

