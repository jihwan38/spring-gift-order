INSERT INTO product (name, price, image_url, is_kakao_approved_by_md)
VALUES
    ('한우300g', 110000, 'https://i.namu.wiki/i/AkDeSVy9vgABeg6qttN9zAEgQpUGPR4gjkA8ScRTmxySNMwY-SsoJUqTeBdpj52nvwyBng1zPo2k2BPUAoo3KMwNZpr3daNB_ip1XEWwyfxZDko-VYVnCXvbX717Bh_gQ_L4mUMZID-JA86YKo7WUw.webp', false),
    ('텀블러', 50000, 'https://i.namu.wiki/i/7mqMgl46Xw3lS_-cZJEyTIQe6HIZKE6Ewr_Xu1VRe-NMeubfqV6wd0GSKQuO6TIvkPyvaDnvisYuiE0NrubKgZg8PHJhB6SpvBb4LbiAGNvce2osT2s1Bs1Cm1mO7AFNbOZrhJ-PX_JVfAwypH4EIw.webp', false),
    ('립스틱', 50000, 'https://i.namu.wiki/i/iBDBIhjjk0QbffTYOU3y-YDOQUbrBgtllM-PTOHHytzMM98RPvjR86g24f-hINWio2ml4sfCCv7GXhedD67OMhzdonxShT2zvviiTtwy1XdO35evNCffD1mTnZFdflNbLeIaIlMYlb4j8PR8Y-1HJg.webp', true),
    ('향수', 35000, 'https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/10/0000/0020/A00000020498534ko.jpg?qt=80', false);

INSERT INTO option (name, quantity, product_id)
VALUES
    ('옵션: 구이용', 100, 1),
    ('옵션: 국거리용', 100, 1),
    ('색상: 블랙', 50, 2),
    ('색상: 화이트', 50, 2),
    ('색상: 실버', 30, 2),
    ('색상: 21호', 200, 3),
    ('용량: 50ml', 80, 4);

INSERT INTO member (email, salt, password, role, provider, provider_id) VALUES ('admin@daum.net', '3dNddJreO8FFohd3PMqS6w==', 'KmmwafNvA+/YYmnXi33Vf4Xa26uyr9dNajVhugCrkp0=', 'ADMIN', 'LOCAL', 'admin@daum.net');
