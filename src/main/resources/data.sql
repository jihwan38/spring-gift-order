INSERT INTO product (name, price, image_url, is_kakao_approved_by_md)
VALUES
    ('한우300g', 110000, 'https://gwchild745.firstmall.kr/data/goods/1/2020/12/39585_tmp_94fdc57a45d832cde487fb6b217e1b810344large.jpg', false),
    ('텀블러', 50000, 'https://i.namu.wiki/i/ErhzrRljjqaG0FLsqz0i0S6jAgBLDeV-_az6Dj5MFR_lLFXu8-eRdEFLuuyfDVQm-yLXBb0odSOycvjqwqIiauUYL3XPSy-AG-1u9m6vgXj7qHMsil3ofNME7qJpjns_Vmkxofk7WvWBTwCc-zhpvQ.webp', false),
    ('립스틱', 50000, 'https://i.namu.wiki/i/0GQWD4DtVb4MCjTzYV3lvvFjQE2J05KIoU2-5TPX1Zvq3TXum5D1vYcBkVJndm0YrUntKAwNLRw-qgJ2gtD_Tic8zHCnbTEX5-OEUoiVQ_p_-hS9uD0S8zDak7jcwNtvPvqqjhjRT_NbvPMGt-6HaA.webp', true), -- 카카오 상품 예시
    ('향수', 35000, 'https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/10/0000/0020/A00000020498534ko.jpg?qt=80', false);

INSERT INTO member (email, salt, password, role) VALUES ('admin@daum.net', '3dNddJreO8FFohd3PMqS6w==', 'KmmwafNvA+/YYmnXi33Vf4Xa26uyr9dNajVhugCrkp0=', 'ADMIN');
