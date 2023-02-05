INSERT INTO stats (app, uri, ip, hit_timestamp)
VALUES ('service1', 'uri1', '192.168.0.1', TIMESTAMP '2010-01-30 00:00:00'),
       ('service1', 'uri1', '192.168.0.1', TIMESTAMP '2010-01-31 00:00:00'),
       ('service1', 'uri1', '192.168.0.2', TIMESTAMP '2020-01-30 00:00:00'),
       ('service1', 'uri1', '192.168.0.2', TIMESTAMP '2020-01-31 00:00:00'),

       ('service2', 'uri1', '192.168.0.1', TIMESTAMP '2010-01-30 00:00:00'),
       ('service2', 'uri1', '192.168.0.1', TIMESTAMP '2010-01-31 00:00:00'),
       ('service2', 'uri1', '192.168.0.2', TIMESTAMP '2020-01-30 00:00:00'),
       ('service2', 'uri1', '192.168.0.2', TIMESTAMP '2020-01-31 00:00:00'),

       ('service1', 'uri2', '192.168.0.1', TIMESTAMP '2010-01-30 00:00:00'),
       ('service1', 'uri2', '192.168.0.1', TIMESTAMP '2010-01-31 00:00:00'),
       ('service1', 'uri2', '192.168.0.2', TIMESTAMP '2020-01-30 00:00:00'),
       ('service1', 'uri2', '192.168.0.2', TIMESTAMP '2020-01-31 00:00:00'),

       ('service2', 'uri2', '192.168.0.1', TIMESTAMP '2010-01-30 00:00:00'),
       ('service2', 'uri2', '192.168.0.1', TIMESTAMP '2010-01-31 00:00:00'),
       ('service2', 'uri2', '192.168.0.2', TIMESTAMP '2020-01-30 00:00:00'),
       ('service2', 'uri2', '192.168.0.2', TIMESTAMP '2020-01-31 00:00:00'),

       ('service1', 'uri3', '192.168.0.1', TIMESTAMP '2010-01-30 00:00:00'),
       ('service1', 'uri3', '192.168.0.1', TIMESTAMP '2010-01-31 00:00:00'),
       ('service1', 'uri3', '192.168.0.3', TIMESTAMP '2010-01-29 00:00:00'),
       ('service1', 'uri3', '192.168.0.2', TIMESTAMP '2020-01-30 00:00:00'),
       ('service1', 'uri3', '192.168.0.2', TIMESTAMP '2020-01-31 00:00:00'),
       ('service1', 'uri3', '192.168.0.3', TIMESTAMP '2020-01-29 00:00:00'),

       ('service2', 'uri3', '192.168.0.1', TIMESTAMP '2010-01-30 00:00:00'),
       ('service2', 'uri3', '192.168.0.1', TIMESTAMP '2010-01-31 00:00:00'),
       ('service2', 'uri3', '192.168.0.2', TIMESTAMP '2020-01-30 00:00:00'),
       ('service2', 'uri3', '192.168.0.2', TIMESTAMP '2020-01-31 00:00:00');