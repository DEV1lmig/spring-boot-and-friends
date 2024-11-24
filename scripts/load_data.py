import os
import psycopg2
import csv
from io import StringIO

def process_in_batches(filename, batch_size=10000):
    try:
        # Intentar diferentes codificaciones
        encodings = ['utf-8', 'latin-1', 'iso-8859-1']

        for encoding in encodings:
            try:
                conn = psycopg2.connect(
                    host="postgres",
                    database="basedatos",
                    user="postgres",
                    password="123456"
                )
                cur = conn.cursor()

                # Crear tabla temporal
                cur.execute('''
                CREATE TABLE IF NOT EXISTS public.cne_temp
                (
                    nacionalidad character varying(15) NOT NULL,
                    cedula character varying(15) NOT NULL,
                    primer_apellido character varying(50),
                    segundo_apellido character varying(50),
                    primer_nombre character varying(50),
                    segundo_nombre character varying(50),
                    centro character varying(100),
                    nombre_completo character varying(200),
                    foto text,
                    huellas text
                )
                ''')

                with open(filename, 'r', encoding=encoding) as f:
                    # Saltar cabecera
                    next(f)

                    buffer = StringIO()
                    count = 0
                    total_rows = 0

                    for line in f:
                        buffer.write(line)
                        count += 1

                        if count >= batch_size:
                            buffer.seek(0)
                            cur.copy_from(buffer, 'cne_temp', sep=',', null='')
                            total_rows += count
                            print(f"Procesados {total_rows} registros...")
                            buffer.truncate(0)
                            buffer.seek(0)
                            count = 0

                    # Procesar registros restantes
                    if count > 0:
                        buffer.seek(0)
                        cur.copy_from(buffer, 'cne_temp', sep=',', null='')
                        total_rows += count

                # Mover datos a la tabla final
                cur.execute('''
                INSERT INTO public.cne
                SELECT * FROM public.cne_temp
                ON CONFLICT (nacionalidad, cedula) DO NOTHING
                ''')

                # Limpiar tabla temporal
                cur.execute('DROP TABLE public.cne_temp')

                conn.commit()
                print(f"\nTotal de registros procesados: {total_rows}")

                # Verificar cantidad final
                cur.execute("SELECT COUNT(*) FROM public.cne")
                final_count = cur.fetchone()[0]
                print(f"Total de registros en la base de datos: {final_count}")

                return True

            except UnicodeDecodeError:
                print(f"Fallo con codificación {encoding}, probando siguiente...")
                continue

            finally:
                if 'cur' in locals():
                    cur.close()
                if 'conn' in locals():
                    conn.close()

        raise Exception("No se pudo procesar el archivo con ninguna codificación")

    except Exception as e:
        print(f"Error: {str(e)}")
        if 'conn' in locals():
            conn.rollback()
        return False

# Ejecutar el proceso
process_in_batches('/docker-entrypoint-initdb.d/nacional.csv')
