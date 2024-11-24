import os
import psycopg2
import csv
import logging
from psycopg2.extras import execute_batch
from contextlib import contextmanager
import psutil

# Configurar logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Tamaño del batch para inserciones
BATCH_SIZE = 1000

@contextmanager
def get_db_connection():
    conn = psycopg2.connect(
        host="postgres",
        database="basedatos",
        user="postgres",
        password="123456"
    )
    try:
        yield conn
    finally:
        conn.close()

def normalize_row(row):
    normalized_row = list(row)
    while len(normalized_row) < 11:
        normalized_row.append('')
    return normalized_row[:11]

def check_memory_usage():
    process = psutil.Process(os.getpid())
    memory_usage = process.memory_info().rss / 1024 / 1024  # MB
    logger.info(f"Uso de memoria actual: {memory_usage:.2f} MB")

def process_csv_in_batches(filename):
    insert_query = '''
    INSERT INTO public.cne (
        nacionalidad, cedula, primer_apellido, segundo_apellido,
        primer_nombre, segundo_nombre, centro, nombre_completo,
        sexo, foto, huellas
    ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    '''

    with get_db_connection() as conn:
        with conn.cursor() as cur:
            # Crear tabla
            cur.execute('''
            CREATE TABLE IF NOT EXISTS public.cne (
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

            batch_data = []
            total_rows = 0

            with open(filename, 'r', encoding='utf-8') as f:
                reader = csv.reader(f)
                next(reader)  # Skip header

                for row in reader:
                    normalized_row = normalize_row(row)
                    batch_data.append(normalized_row)

                    if len(batch_data) >= BATCH_SIZE:
                        execute_batch(cur, insert_query, batch_data)
                        total_rows += len(batch_data)
                        batch_data = []
                        conn.commit()
                        logger.info(f"Procesadas {total_rows} filas")
                        check_memory_usage()

                # Procesar últimas filas
                if batch_data:
                    execute_batch(cur, insert_query, batch_data)
                    total_rows += len(batch_data)
                    conn.commit()

            logger.info(f"Total de filas procesadas: {total_rows}")

            # Verificar datos
            cur.execute("SELECT COUNT(*) FROM public.cne")
            count = cur.fetchone()[0]
            logger.info(f"Total registros en base de datos: {count}")

if __name__ == "__main__":
    try:
        process_csv_in_batches('/docker-entrypoint-initdb.d/nacional.csv')
    except Exception as e:
        logger.error(f"Error en el proceso: {str(e)}")
