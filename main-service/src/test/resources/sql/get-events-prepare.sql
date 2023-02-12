INSERT INTO users (id, name, email)
VALUES (1, 'user name 1', 'mail1@mail.com'),
       (2, 'user name 2', 'mail2@mail.com'),
       (3, 'user name 3', 'mail3@mail.com'),
       (4, 'user name 4', 'mail4@mail.com');

INSERT INTO categories (id, name)
VALUES (1, 'cat name 1'),
       (2, 'cat name 2'),
       (3, 'cat name 3');

INSERT INTO events (
                    id,
                    annotation,
                    category_id,
                    description,
                    event_date,
                    location_latitude,
                    location_longitude,
                    paid,
                    participant_limit,
                    request_moderation,
                    title,
                    created_on,
                    initiator_id,
                    published_on,
                    state)
VALUES ( 1,
        'annotation 1',
        2,
        'description 1',
        TIMESTAMP '2030-01-30 00:00:00',
        55.754167,
        37.62,
        true,
        10,
        false,
        'title 1',
        TIMESTAMP '2020-01-29 00:00:00',
        3,
        TIMESTAMP '2020-01-30 00:00:00',
        'PUBLISHED' ),
       ( 2,
         'annotation 2',
         3,
         'description 2',
         TIMESTAMP '2030-01-30 00:00:02',
         25.754167,
         27.62,
         false,
         7,
         true,
         'title 2',
         TIMESTAMP '2020-01-29 00:00:02',
         4,
         TIMESTAMP '2020-01-30 00:00:02',
         'PUBLISHED' );

INSERT INTO requests (id, created, event_id, requester_id, status)
VALUES ( 1,  TIMESTAMP '2021-01-30 00:00:00', 1, 3, 'CONFIRMED' ),
       ( 2,  TIMESTAMP '2021-01-30 00:00:01', 1, 4, 'PENDING' ),
       ( 3,  TIMESTAMP '2021-01-30 00:00:02', 2, 1, 'REJECTED' ),
       ( 4,  TIMESTAMP '2021-01-30 00:00:03', 2, 2, 'CONFIRMED' );