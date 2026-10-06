import sqlite3
import os

db_path = "Attractions.db"
conn = sqlite3.connect(db_path)
cursor = conn.cursor()

# 1. Define the correct MySQL schema
schema_sql = """
DROP TABLE IF EXISTS attraction_tags;
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS attraction;

CREATE TABLE attraction (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    longerdescription TEXT,
    ticket_price DOUBLE
);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    hash VARCHAR(255) NOT NULL
);

CREATE TABLE favorites (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    attraction_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (attraction_id) REFERENCES attraction(id) ON DELETE CASCADE,
    UNIQUE(user_id, attraction_id)
);

CREATE TABLE tags (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tag_name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE attraction_tags (
    attraction_id INT,
    tag_id INT,
    PRIMARY KEY (attraction_id, tag_id),
    FOREIGN KEY (attraction_id) REFERENCES attraction(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);
"""

with open("src/main/resources/schema.sql", "w", encoding="utf-8") as f:
    f.write(schema_sql)

# 2. Extract data to data.sql
data_sql = ""
tables = ["attraction", "users", "tags", "favorites", "attraction_tags"]

for table in tables:
    cursor.execute(f"SELECT * FROM {table}")
    rows = cursor.fetchall()
    
    # Get column names
    cursor.execute(f"PRAGMA table_info({table})")
    cols = [info[1] for info in cursor.fetchall()]
    col_str = ", ".join(cols)
    
    for row in rows:
        values = []
        for val in row:
            if val is None:
                values.append("NULL")
            elif isinstance(val, str):
                escaped = val.replace("'", "''")
                values.append(f"'{escaped}'")
            else:
                values.append(str(val))
        val_str = ", ".join(values)
        data_sql += f"INSERT INTO {table} ({col_str}) VALUES ({val_str});\n"

with open("src/main/resources/data.sql", "w", encoding="utf-8") as f:
    f.write(data_sql)

print("Migration scripts created successfully!")
