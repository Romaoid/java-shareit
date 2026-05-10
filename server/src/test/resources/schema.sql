-- Простой синтаксис для H2
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    email VARCHAR(50) NOT NULL,
    CONSTRAINT unique_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(30) NOT NULL,
    description VARCHAR(500) NOT NULL,
    available BOOLEAN NOT NULL,
    CONSTRAINT fk_items_to_users FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS booking (
    id INT AUTO_INCREMENT PRIMARY KEY,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    booker_id INT NOT NULL,
    item_id INT NOT NULL,
    status VARCHAR(10) NOT NULL,
    CONSTRAINT fk_booking_to_users FOREIGN KEY(booker_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_to_items FOREIGN KEY(item_id) REFERENCES items(id) ON DELETE CASCADE,
    CONSTRAINT chk_booking_dates CHECK (end_date > start_date)
);

CREATE TABLE IF NOT EXISTS comments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    text VARCHAR NOT NULL,
    item_id INT NOT NULL,
    author_id INT NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_comments_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(500) NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    author_id INT NOT NULL,
    CONSTRAINT fk_requests_to_users FOREIGN KEY(author_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS answers_requests (
    item_id INT NOT NULL,
    request_id INT NOT NULL,
    PRIMARY KEY (item_id, request_id),
    CONSTRAINT fk_answers_to_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    CONSTRAINT fk_answers_to_requests FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE CASCADE
);