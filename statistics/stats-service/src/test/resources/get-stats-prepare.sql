INSERT INTO apps (id, name)
VALUES (1, 'service1'),
       (2, 'service2');

INSERT INTO stats (app_id, uri, ip, hit_timestamp)
VALUES (1, 'uri1', '192.168.0.1', TIMESTAMP '2010-01-30 00:00:00'),
       (1, 'uri1', '192.168.0.1', TIMESTAMP '2010-01-31 00:00:00'),
       (1, 'uri1', '192.168.0.2', TIMESTAMP '2020-01-30 00:00:00'),
       (1, 'uri1', '192.168.0.2', TIMESTAMP '2020-01-31 00:00:00'),

       (2, 'uri1', '192.168.0.1', TIMESTAMP '2010-01-30 00:00:00'),
       (2, 'uri1', '192.168.0.1', TIMESTAMP '2010-01-31 00:00:00'),
       (2, 'uri1', '192.168.0.2', TIMESTAMP '2020-01-30 00:00:00'),
       (2, 'uri1', '192.168.0.2', TIMESTAMP '2020-01-31 00:00:00'),

       (1, 'uri2', '192.168.0.1', TIMESTAMP '2010-01-30 00:00:00'),
       (1, 'uri2', '192.168.0.1', TIMESTAMP '2010-01-31 00:00:00'),
       (1, 'uri2', '192.168.0.2', TIMESTAMP '2020-01-30 00:00:00'),
       (1, 'uri2', '192.168.0.2', TIMESTAMP '2020-01-31 00:00:00'),

       (2, 'uri2', '192.168.0.1', TIMESTAMP '2010-01-30 00:00:00'),
       (2, 'uri2', '192.168.0.1', TIMESTAMP '2010-01-31 00:00:00'),
       (2, 'uri2', '192.168.0.2', TIMESTAMP '2020-01-30 00:00:00'),
       (2, 'uri2', '192.168.0.2', TIMESTAMP '2020-01-31 00:00:00'),

       (1, 'uri3', '192.168.0.1', TIMESTAMP '2010-01-30 00:00:00'),
       (1, 'uri3', '192.168.0.1', TIMESTAMP '2010-01-31 00:00:00'),
       (1, 'uri3', '192.168.0.3', TIMESTAMP '2010-01-29 00:00:00'),
       (1, 'uri3', '192.168.0.2', TIMESTAMP '2020-01-30 00:00:00'),
       (1, 'uri3', '192.168.0.2', TIMESTAMP '2020-01-31 00:00:00'),
       (1, 'uri3', '192.168.0.3', TIMESTAMP '2020-01-29 00:00:00'),

       (2, 'uri3', '192.168.0.1', TIMESTAMP '2010-01-30 00:00:00'),
       (2, 'uri3', '192.168.0.1', TIMESTAMP '2010-01-31 00:00:00'),
       (2, 'uri3', '192.168.0.2', TIMESTAMP '2020-01-30 00:00:00'),
       (2, 'uri3', '192.168.0.2', TIMESTAMP '2020-01-31 00:00:00');