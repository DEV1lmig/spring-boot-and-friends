import os
import psycopg2
import csv

def normalize_row(row):
    normalized_row = list(row)
    while len(normalized_row) < 11:
        normalized_row.append('')
    return normalized_row[:11]

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
            normalized_row = normalize_row(row)
            cur.execute('''
            INSERT INTO public.cne (nacionalidad, cedula, primer_apellido, segundo_apellido,
            primer_nombre, segundo_nombre, centro, nombre_completo, sexo, foto, huellas)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            ''', normalized_row)

    # Confirmar los cambios
    conn.commit()

    # Verificar los datos insertados
    print("\n=== Datos en la base de datos ===")
    cur.execute("SELECT * FROM public.cne LIMIT 5")  # Mostrar primeros 5 registros
    rows = cur.fetchall()

    # Obtener nombres de columnas
    cur.execute("SELECT column_name FROM information_schema.columns WHERE table_name='cne' ORDER BY ordinal_position")
    columns = [col[0] for col in cur.fetchall()]

    # Imprimir encabezados
    print("\nColumnas:", ", ".join(columns))

    # Imprimir registros
    print("\nRegistros:")
    for row in rows:
        print(row)

    # Imprimir cantidad total de registros
    cur.execute("SELECT COUNT(*) FROM public.cne")
    total = cur.fetchone()[0]
    print(f"\nTotal de registros en la base de datos: {total}")

except Exception as e:
    print(f"Error: {str(e)}")
    conn.rollback()
finally:
    if cur:
        cur.close()
    if conn:
        conn.close()
