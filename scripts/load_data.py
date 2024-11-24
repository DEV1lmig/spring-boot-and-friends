import os
import psycopg2
import csv
# Conexión a la base de datos
conn = psycopg2.connect(
    host="postgres",
    database="basedatos",
    user="postgres",
    password="123456"
)
cur = conn.cursor()
# Crear tabla si no existe
cur.execute('''
CREATE TABLE IF NOT EXISTS public.cne
(
    nacionalidad character varying(15) NOT NULL,
    cedula character varying(15) NOT NULL,
    primer_apellido character varying(50),
    segundo_apellido character varying(50),
    primer_nombre character varying(50),
    segundo_nombre character varying(50),
    centro character varying(50),
    nombre_completo character varying(125),
    sexo character varying(1),
    foto character varying(65536),
    huellas character varying(65536),
    id BIGSERIAL PRIMARY KEY
);
''')
# Cargar datos desde CSV
with open('/docker-entrypoint-initdb.d/nacional.csv', 'r') as f:
    reader = csv.reader(f)
    next(reader)  # Omitir encabezado
    for row in reader:
        cur.execute(
            "INSERT INTO cne (nacionalidad, cedula, primer_apellido, segundo_apellido, primer_nombre, segundo_nombre, centro, nombre_completo, sexo, foto, huellas) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
            row
        )

conn.commit()
cur.close()
conn.close()
