import psycopg2
import csv
from werkzeug.security import generate_password_hash

def load_data():
    conn = psycopg2.connect(
        dbname="basedatos",
        user="postgres",
        password="123456",
        host="postgres"
    )
    cur = conn.cursor()

    # Crear tabla 'cne' si no existe
    cur.execute('''
        CREATE TABLE IF NOT EXISTS cne (
            id BIGSERIAL PRIMARY KEY,
            nacionalidad VARCHAR(15) NOT NULL,
            cedula VARCHAR(15) NOT NULL,
            primer_apellido VARCHAR(50),
            segundo_apellido VARCHAR(50),
            primer_nombre VARCHAR(50),
            segundo_nombre VARCHAR(50),
            centro VARCHAR(50),
            nombre_completo VARCHAR(125),
            sexo VARCHAR(1),
            foto TEXT,
            huellas TEXT
        );
    ''')

    # Crear tabla 'usuarios' si no existe
    cur.execute('''
        CREATE TABLE IF NOT EXISTS usuarios (
            id SERIAL PRIMARY KEY,
            username VARCHAR(80) UNIQUE NOT NULL,
            password_hash VARCHAR(128) NOT NULL,
            role VARCHAR(80) NOT NULL
        );
    ''')

    # Insertar usuario 'admin' si no existe
    cur.execute("SELECT * FROM usuarios WHERE username='admin';")
    if not cur.fetchone():
        password_hash = generate_password_hash('admin123')
        cur.execute(
            "INSERT INTO usuarios (username, password_hash, role) VALUES (%s, %s, %s);",
            ('admin', password_hash, 'admin')
        )
        print("Usuario 'admin' creado exitosamente.")

    # Cargar datos desde CSV en la tabla 'cne'
    with open('/docker-entrypoint-initdb.d/nacional.csv', encoding='utf-8') as f:
        reader = csv.reader(f)
        next(reader)  # Saltar encabezado
        for row in reader:
            cur.execute(
                "INSERT INTO cne (nacionalidad, cedula, primer_apellido, segundo_apellido, primer_nombre, segundo_nombre, centro, nombre_completo, sexo, foto, huellas) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);",
                row
            )

    conn.commit()
    cur.close()
    conn.close()

if __name__ == '__main__':
    load_data()
