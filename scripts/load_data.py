import os
import psycopg2
import csv

try:
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
        centro character varying(100),
        nombre_completo character varying(200),
        sexo character varying(1),
        foto text,
        huellas text
    )
    ''')

    # Leer y cargar datos del CSV
    with open('/docker-entrypoint-initdb.d/nacional.csv', 'r', encoding='utf-8') as f:
        csv_reader = csv.reader(f, delimiter=',')
        next(csv_reader)  # Saltar la cabecera si existe

        for row in csv_reader:
            if len(row) >= 11:  # Verificar que la fila tenga todos los campos necesarios
                cur.execute('''
                INSERT INTO public.cne (nacionalidad, cedula, primer_apellido, segundo_apellido,
                primer_nombre, segundo_nombre, centro, nombre_completo, sexo, foto, huellas)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                ''', row[:11])
            else:
                print(f"Fila ignorada por falta de datos: {row}")

    # Confirmar los cambios
    conn.commit()

except Exception as e:
    print(f"Error: {str(e)}")
    conn.rollback()
finally:
    if cur:
        cur.close()
    if conn:
        conn.close()
