FROM postgres:13

# Instalar Python y dependencias
RUN apt-get update && apt-get install -y python3 python3-psycopg2 python3-psutil

# Copiar archivos
COPY nacional.csv /docker-entrypoint-initdb.d/nacional.csv
COPY scripts/load_data.py /docker-entrypoint-initdb.d/load_data.py

# Crear script de inicio
RUN echo '#!/bin/bash\n\
docker-entrypoint.sh postgres & \n\
until pg_isready; do\n\
  sleep 1\n\
done\n\
sleep 5\n\
python3 /docker-entrypoint-initdb.d/load_data.py\n\
wait' > /start.sh

# Dar permisos necesarios
RUN chmod +x /start.sh
RUN chmod 755 /docker-entrypoint-initdb.d/load_data.py

# Usar el script de inicio
CMD ["/start.sh"]
